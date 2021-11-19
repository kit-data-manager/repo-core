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

package edu.kit.datamanager.repo.util.validators.impl;

import edu.kit.datamanager.exceptions.BadArgumentException;
import edu.kit.datamanager.exceptions.UnsupportedMediaTypeException;
import edu.kit.datamanager.repo.util.validators.IIdentifierValidator;
import org.datacite.schema.kernel_4.RelatedIdentifierType;

public class ISBNValidator implements IIdentifierValidator {
    /**
     * This method returns the type of the validator implementation.
     *
     * @return element of the enum defined in the Datacite schema.
     */
    @Override
    public RelatedIdentifierType supportedType() {
        return RelatedIdentifierType.ISBN;
    }

    /**
     * This method must be implemented by any implementation.
     * It validates an input and either returns true or throws an exception.
     *
     * @param input to validate
     * @param type  of the input
     * @return true if input is valid for the special type of implementation.
     */
    @Override
    public boolean isValid(String input, RelatedIdentifierType type) {
        if (type != supportedType()) {
            LOG.warn("Illegal type of validator");
            throw new UnsupportedMediaTypeException("Illegal type of Validator.");
        }
        if (input.length() == 10) return isValidISBN10(input);
        else if (input.length() == 13) return isValidISBN13(input);
        LOG.error("Invalid input length. Please use a valid ISBN10 or ISBN13.");
        throw new BadArgumentException("Invalid input length. Please use a valid ISBN10 or ISBN13.");
    }

    private boolean isValidISBN13(String input) {
        int sum = 0;
        boolean isOne = true;
        for (int i = 0; i < 13; i++) {
            int number = input.charAt(i);
            if (!isOne) number *= 3;
            sum += number;
            isOne = !isOne;
        }
        return (sum % 10) == 0;
    }

    private boolean isValidISBN10(String input) {
        int sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += input.charAt(i) * (10 - i);
        }
        return (sum % 11) == 0;
    }

}
