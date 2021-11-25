/*
 * Copyright 2021 Karlsruhe Institute of Technology.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.kit.datamanager.repo.util;

import edu.kit.datamanager.exceptions.UnsupportedMediaTypeException;
import edu.kit.datamanager.repo.util.validators.IIdentifierValidator;
import edu.kit.datamanager.repo.util.validators.impl.HandleNetValidator;
import edu.kit.datamanager.repo.util.validators.impl.URLValidator;
import org.datacite.schema.kernel_4.RelatedIdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author maximilianiKIT
 */
public class ValidatorUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidatorUtil.class);
    private static final ValidatorUtil soleInstance = new ValidatorUtil();

    private static final Set<IIdentifierValidator> validators;

    static {
        Set<IIdentifierValidator> validators1 = new HashSet<>();

        validators1.add(new HandleNetValidator());
        validators1.add(new HandleNetValidator());
        validators1.add(new URLValidator());

        validators = validators1;
    }

    /**
     * This private constructor enforces singularity.
     */
    private ValidatorUtil() {
    }

    /**
     * This method returns the singleton.
     *
     * @return singleton instance of this class
     */
    public static ValidatorUtil getSingleton() {
        return soleInstance;
    }

    /**
     * This method returns a list of the types of all implemented validators.
     *
     * @return a list of RelatedIdentifierType.
     */
    public List<RelatedIdentifierType> getAllAvailableValidatorTypes() {
        LOGGER.debug("getAllAvailableValidatorTypes");
        List<RelatedIdentifierType> result = new ArrayList<>();
        for (IIdentifierValidator i : validators) result.add(i.getSupportedType());
        LOGGER.info("All available validator types: {}", result.toString());
        return result;
    }

    /**
     * This method checks if the type passed in the parameter is valid and then uses the corresponding validator to check the input.
     *
     * @param input The input which gets validated.
     * @param type  The type of the validator.
     * @return true if the input and type are valid.
     * May throw an exception if the input or the type is invalid or other errors occur.
     */
    public boolean isValid(String input, RelatedIdentifierType type) {
        LOGGER.debug("isValid - string type");
        for (IIdentifierValidator i : validators) {
            if (i.getSupportedType().equals(type)) {
                if (i.isValid(input, type)) {
                    LOGGER.info("Valid input and valid input type!");
                    return true;
                }
            }
        }
        LOGGER.warn("No matching validator found for type {}. Please check the available types.", type);
        throw new UnsupportedMediaTypeException("No matching validator found. Please check the available types.");
    }

    /**
     * This method checks if the type passed in the parameter is valid and then uses the corresponding validator to check the input.
     *
     * @param input The input which gets validated.
     * @param type  The type of the validator as string.
     * @return true if the input and type are valid.
     * May throw an exception if the input or the type is invalid or other errors occur.
     */
    public boolean isValid(String input, String type) {
        LOGGER.debug("isValid - string string");
        try {
            RelatedIdentifierType rType = RelatedIdentifierType.valueOf(type);
            return isValid(input, rType);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("No matching validator found for type {}. Please check the available types.", type);
            throw new UnsupportedMediaTypeException("No matching validator found. Please check the available types.");
        }
    }
}
