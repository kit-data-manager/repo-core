package edu.kit.datamanager.repo.util;

import edu.kit.datamanager.exceptions.UnsupportedMediaTypeException;
import edu.kit.datamanager.repo.util.validators.IIdentifierValidator;
import org.datacite.schema.kernel_4.RelatedIdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class ValidatorUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidatorUtil.class);
    private static final ValidatorUtil soleInstance = new ValidatorUtil();

    private static final Map<RelatedIdentifierType, IIdentifierValidator> validators;

    static {
        Map<RelatedIdentifierType, IIdentifierValidator> validators1 = new HashMap<>();
        Set<Class> classes = findAllClassesUsingClassLoader("edu.kit.datamanager.repo.util.validators.impl");
        for (Class i: classes) {
            try {
                IIdentifierValidator j = (IIdentifierValidator) i.getDeclaredConstructor().newInstance();
                validators1.put(j.supportedType(), j);
                LOGGER.debug(j.supportedType().toString());
            } catch (InstantiationException | IllegalAccessException | ClassCastException | InvocationTargetException | NoSuchMethodException e) {
                LOGGER.error(e.toString());
            }
        }
        validators = validators1;
    }

    private ValidatorUtil() {
        // enforces singularity
    }

    public static ValidatorUtil getSingleton() {
        return soleInstance;
    }

    public List<RelatedIdentifierType> getAllAvailableValidatorTypes() {
        System.out.println("getAllAvailableValidatorTypes");
        List<RelatedIdentifierType> result = new ArrayList<>();
        validators.forEach((key, value) -> result.add(key));
        return result;
    }

    public boolean isValid(String input, RelatedIdentifierType type){
        System.out.println("isValid - string type");
        if (validators.containsKey(type)) {
            if (validators.get(type).isValid(input, type)) LOGGER.info("Valid input and valid input type!");
            return true;
        } else {
            LOGGER.warn("No matching validator found. Please check your input and plugins.");
            throw new UnsupportedMediaTypeException("No matching validator found. Please check your input and plugins.");
        }
    }

    public boolean isValid(String input, String type) {
        System.out.println("isValid - string string");
        for (Map.Entry<RelatedIdentifierType, IIdentifierValidator> entry : validators.entrySet()) {
            if (entry.getKey().toString().equals(type)) {
                if (entry.getValue().isValid(input)) return true;
            }
        }
        throw new UnsupportedMediaTypeException("Invalid Type!");
    }

    private static Set<Class> findAllClassesUsingClassLoader(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        System.out.println(new BufferedReader(new InputStreamReader(IIdentifierValidator.class.getClassLoader().getResourceAsStream(packageName.replaceAll("[.]", "/")))).lines().toList().toString());
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, packageName))
                .collect(Collectors.toSet());
    }

    private static Class getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "." + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
        }
        return null;
    }
}
