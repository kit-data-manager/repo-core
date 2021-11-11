package edu.kit.datamanager.repo.util;

import edu.kit.datamanager.repo.util.validators.IValidator;
import org.datacite.schema.kernel_4.RelatedIdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class ValidatorUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidatorUtil.class);
    private static final ValidatorUtil soleInstance = new ValidatorUtil();

    private static final Map<RelatedIdentifierType, IValidator> validators;

    static {
        Map<RelatedIdentifierType, IValidator> validators1 = new HashMap<>();
        Set<Class> classes = findAllClassesUsingClassLoader("edu.kit.datamanager.repo.validators.impl");
        for (Class i: classes) {
            try {
                IValidator j = (IValidator) i.newInstance();
                validators1.put(j.supportedType(), j);
                LOGGER.debug(j.supportedType().toString());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        validators = validators1;
    }

    private ValidatorUtil() {
        // enforces singularity
    }

    public static ValidatorUtil soleInstance() {
        return soleInstance;
    }

    public List<RelatedIdentifierType> getAllAvailableValidatorTypes() {
        Map<RelatedIdentifierType, IValidator> map = validators;
        List<RelatedIdentifierType> result = new ArrayList<>();
        for (Map.Entry entry : map.entrySet()) {
            result.add((RelatedIdentifierType) entry.getKey());
        }
        return result;
    }

    private static Set<Class> findAllClassesUsingClassLoader(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, packageName))
                .collect(Collectors.toSet());
    }

    private static Class getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
        }
        return null;
    }
}
