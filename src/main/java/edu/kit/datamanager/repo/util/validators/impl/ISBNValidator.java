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
import edu.kit.datamanager.repo.util.validators.IIdentifierValidator;
import org.datacite.schema.kernel_4.RelatedIdentifierType;

/**
 * This class validates ISBN10 and ISBN13.
 * @author maximilianiKIT
 */
public class ISBNValidator implements IIdentifierValidator {

    @Override
    public RelatedIdentifierType getSupportedType() {
        return RelatedIdentifierType.ISBN;
    }

    @Override
    public boolean isValid(String input) {
        boolean result;
        if (input.length() == 10) result = isValidISBN10(input);
        else if (input.length() == 13) result = isValidISBN13(input);
        else {
            LOGGER.error("Invalid input length. Please use a valid ISBN10 or ISBN13.");
            throw new BadArgumentException("Invalid input length. Please use a valid ISBN10 or ISBN13.");
        }
        return result;
    }

    /**
     * This method validates ISBN-13
     * @param input ISBN13 to validate
     * @return true if valid
     * Throws a BadArgumentException if the input is invalid.
     */
    private boolean isValidISBN13(String input) {
        int sum = 0;
        boolean isOddIndex = true;
        for (int i = 0; i < 13; i++) {
            int number = input.charAt(i);
            if (!isOddIndex) number *= 3;
            sum += number;
            isOddIndex = !isOddIndex;
        }
        if ((sum % 10) != 0){
            LOGGER.error("Invalid input: {}", input);
            throw new BadArgumentException("Invalid input");
        }
        LOGGER.debug("The ISBN13 {} is valid!", input);
        return true;
    }

    /**
     * This method validates ISBN-10
     * @param input ISBN10 to validate
     * @return true if valid
     * Throws a BadArgumentException if the input is invalid.
     */
    private boolean isValidISBN10(String input) {
        int sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += input.charAt(i) * (10 - i);
        }
        if ((sum % 11) == 0){
            LOGGER.debug("The ISBN10 {} is valid", input);
            return true;
        } else{
            LOGGER.error("Invalid input: {}", input);
            throw new BadArgumentException("Invalid input");
        }
    }

}
