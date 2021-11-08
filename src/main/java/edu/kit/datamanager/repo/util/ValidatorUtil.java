package edu.kit.datamanager.repo.util;

import edu.kit.datamanager.repo.util.validators.IValidator;
import org.datacite.schema.kernel_4.RelatedIdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidatorUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidatorUtil.class);
    private static final ValidatorUtil soleInstance = new ValidatorUtil();

    private static final Map<RelatedIdentifierType, IValidator> validators;

    static {
        Map<RelatedIdentifierType, IValidator> validators1 = new HashMap<>();
        Set<IValidator> availableValidators = new HashSet<>();
        availableValidators = findAllValidators("edu.kit.datamanager.repo.util.validators.impl");
        for (IValidator i: availableValidators) {
            validators1.put(i.supportedType(), i);
            LOGGER.debug("Found validator class ({}) from type: {}", i.getClass().toString(), i.supportedType());
        }
        validators = validators1;
    }

    private ValidatorUtil() {
        // enforces singularity
    }

    public static ValidatorUtil soleInstance() {
        return soleInstance;
    }

    private static Set<IValidator> findAllValidators(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, packageName))
                .collect(Collectors.toSet());
    }

    private static IValidator getClass(String className, String packageName) {
        try {
            Class result = Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
            if(result.getInterfaces().equals(IValidator.class)) return (IValidator) result.cast(IValidator.class);
        } catch (ClassNotFoundException e) {
        }
        return null;
    }
}
