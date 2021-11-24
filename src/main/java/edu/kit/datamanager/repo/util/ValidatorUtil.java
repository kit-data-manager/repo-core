package edu.kit.datamanager.repo.util;

import edu.kit.datamanager.exceptions.UnsupportedMediaTypeException;
import edu.kit.datamanager.repo.util.validators.IIdentifierValidator;
import edu.kit.datamanager.repo.util.validators.impl.HandleNetValidator;
import edu.kit.datamanager.repo.util.validators.impl.URLValidator;
import org.datacite.schema.kernel_4.RelatedIdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

public class ValidatorUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidatorUtil.class);
    private static final ValidatorUtil soleInstance = new ValidatorUtil();

    private static final Map<RelatedIdentifierType, IIdentifierValidator> validators;

    static {
        Map<RelatedIdentifierType, IIdentifierValidator> validators1 = new HashMap<>();

        validators1.put(RelatedIdentifierType.DOI, new HandleNetValidator());
        validators1.put(RelatedIdentifierType.HANDLE, new HandleNetValidator());
        validators1.put(RelatedIdentifierType.URL, new URLValidator());

        validators = validators1;
    }

    /**
     * This private constructor enforces singularity.
     */
    private ValidatorUtil() {
    }

    /**
     * This method returns the singleton.
     * @return singleton instance of this class
     */
    public static ValidatorUtil getSingleton() {
        return soleInstance;
    }

    /**
     * This method returns a list of the types of all implemented validators.
     * @return a list of RelatedIdentifierType.
     */
    public List<RelatedIdentifierType> getAllAvailableValidatorTypes() {
        LOGGER.debug("getAllAvailableValidatorTypes");
        List<RelatedIdentifierType> result = new ArrayList<>();
        validators.forEach((key, value) -> result.add(key));
        return result;
    }

    /**
     * This method checks if the type passed in the parameter is valid and then uses the corresponding validator to check the input.
     * @param input The input which gets validated.
     * @param type The type of the validator.
     * @return true if the input and type are valid.
     * May throw an exception if the input or the type is invalid or other errors occur.
     */
    public boolean isValid(String input, RelatedIdentifierType type){
        LOGGER.debug("isValid - string type");
        if (validators.containsKey(type)) {
            if (validators.get(type).isValid(input, type)) LOGGER.info("Valid input and valid input type!");
            return true;
        } else {
            LOGGER.warn("No matching validator found. Please check your input and plugins.");
            throw new UnsupportedMediaTypeException("No matching validator found. Please check your input and plugins.");
        }
    }

    /**
     * This method checks if the type passed in the parameter is valid and then uses the corresponding validator to check the input.
     * @param input The input which gets validated.
     * @param type The type of the validator as string.
     * @return true if the input and type are valid.
     * May throw an exception if the input or the type is invalid or other errors occur.
     */
    public boolean isValid(String input, String type) {
        LOGGER.debug("isValid - string string");
        try {
            RelatedIdentifierType rType = RelatedIdentifierType.valueOf(type);
            return validators.get(rType).isValid(input);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedMediaTypeException("Invalid Type!");
        }
    }
}
