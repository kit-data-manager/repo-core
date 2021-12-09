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
import edu.kit.datamanager.repo.util.validators.EValidatorMode;
import edu.kit.datamanager.repo.util.validators.IIdentifierValidator;
import edu.kit.datamanager.repo.util.validators.impl.DOIValidator;
import edu.kit.datamanager.repo.util.validators.impl.HandleValidator;
import edu.kit.datamanager.repo.util.validators.impl.ISBNValidator;
import edu.kit.datamanager.repo.util.validators.impl.URLValidator;
import org.datacite.schema.kernel_4.RelatedIdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * This class provides an util to validate identifiers.
 *
 * @author maximilianiKIT
 */
public class ValidatorUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidatorUtil.class);
    private static final ValidatorUtil soleInstance = new ValidatorUtil();
    private static final Map<RelatedIdentifierType, IIdentifierValidator> validators;
    private static EValidatorMode mode = EValidatorMode.FULL;

    static {
        Map<RelatedIdentifierType, IIdentifierValidator> validators1 = new HashMap<>();

        validators1.put(RelatedIdentifierType.HANDLE, new HandleValidator());
        validators1.put(RelatedIdentifierType.DOI, new DOIValidator());
        validators1.put(RelatedIdentifierType.URL, new URLValidator());
        validators1.put(RelatedIdentifierType.ISBN, new ISBNValidator());

        if (LOGGER.isInfoEnabled()) {
            for (RelatedIdentifierType type : validators1.keySet()) LOGGER.info("Validator found for '{}'", type);
        }
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
        List<RelatedIdentifierType> result = new ArrayList<>();
        validators.forEach((key, value) -> result.add(key));
        LOGGER.debug("All available validator types: {}", result);
        return result;
    }

    /**
     * This method is a setter for the validator mode.
     * @param mode the new validator mode from the enum.
     */
    public void setMode(EValidatorMode mode) {
        LOGGER.info("Changed validator mode to {}", mode);
        if(mode == EValidatorMode.OFF) LOGGER.warn("You disabled the validation of identifiers. THIS IS NOT RECOMMENDED!");
        ValidatorUtil.mode = mode;
    }

    /**
     * This method is a setter for the validator mode.
     * @return the actual validator mode.
     */
    public EValidatorMode getMode() {
        return mode;
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
        LOGGER.debug("Validate identifier '{}' with type '{}'...", input, type);
        boolean result = false;
        if (validators.containsKey(type)) {
            if (validators.get(type).isValid(input, type)) {
                LOGGER.debug("Valid input {} and valid input type{}!", input, type);
                result = true;
            }
        } else {
            LOGGER.warn("No matching validator found for type {}. Please check the available types.", type);
            throw new UnsupportedMediaTypeException("No matching validator found. Please check the available types.");
        }
        return result;
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
        LOGGER.debug("Validate identifier '{}' with type '{}'...", input, type);
        boolean result = false;
        try {
            RelatedIdentifierType rType = RelatedIdentifierType.valueOf(type);
            result = isValid(input, rType);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("No matching validator found for type {}. Please check the available types.", type);
            throw new UnsupportedMediaTypeException("No matching validator found. Please check the available types.");
        }
        return result;
    }
}
