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

package edu.kit.datamanager.repo.util.validators;

import org.datacite.schema.kernel_4.RelatedIdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This interface provides a method which is necessary to validate things.
 */
public interface IIdentifierValidator {

    Logger LOG = LoggerFactory.getLogger(IIdentifierValidator.class);

    /**
     * This method returns the type of the validator implementation.
     *
     * @return element of the enum defined in the Datacite schema.
     */
    RelatedIdentifierType supportedType();

    /**
     * This method is a shortcut for isValid.
     * It doesn't require the type parameter and sets the type of the implementation instead.
     *
     * @param input to validate
     * @return true if input is valid for the special type of implementation
     */
    default boolean isValid(String input) {
        return isValid(input, supportedType());
    }

    /**
     * This method must be implemented by any implementation.
     * It validates an input and either returns true or throws an exception.
     *
     * @param input to validate
     * @param type  of the input
     * @return true if input is valid for the special type of implementation.
     */
    boolean isValid(String input, RelatedIdentifierType type);

}
