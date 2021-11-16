package edu.kit.datamanager.repo.util;

import edu.kit.datamanager.exceptions.UnsupportedMediaTypeException;
import edu.kit.datamanager.repo.util.validators.IValidator;
import lombok.extern.java.Log;
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
        Set<Class> classes = findAllClassesUsingClassLoader("edu.kit.datamanager.repo.util.validators.impl");
        for (Class i: classes) {
            try {
                IValidator j = (IValidator) i.newInstance();
                validators1.put(j.supportedType(), j);
                LOGGER.debug(j.supportedType().toString());
            } catch (InstantiationException | IllegalAccessException | ClassCastException e) {
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

    public boolean isValid(String input, RelatedIdentifierType type){
        if (validators.containsKey(type)) {
            if (validators.get(type).isValid(input, type)) LOGGER.info("Valid input and valid input type!");
            return true;
        } else {
            LOGGER.warn("No matching validator found. Please check your input and plugins.");
            throw new UnsupportedMediaTypeException("No matching validator found. Please check your input and plugins.");
        }
    }

    public boolean isValid(String input, String type) {
        for (Map.Entry<RelatedIdentifierType, IValidator> entry : validators.entrySet()) {
            if (entry.getKey().toString().equals(type)) {
                if (entry.getValue().isValid(input)) return true;
            }
        }
        throw new UnsupportedMediaTypeException("Invalid Type!");
    }

    private static Set<Class> findAllClassesUsingClassLoader(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        System.out.println(stream);
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
