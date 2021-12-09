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

import edu.kit.datamanager.exceptions.BadArgumentException;
import edu.kit.datamanager.exceptions.ServiceUnavailableException;
import edu.kit.datamanager.exceptions.UnsupportedMediaTypeException;
import edu.kit.datamanager.repo.util.ValidatorUtil;
import edu.kit.datamanager.repo.util.validators.impl.ISBNValidator;
import org.datacite.schema.kernel_4.RelatedIdentifierType;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author maximilianiKIT
 */
public class ISBNValidatorTest {
    ISBNValidator validator = new ISBNValidator();

    @Test
    public void isbn13ValidInFULLValidMode() {
        ValidatorUtil.getSingleton().setMode(EValidatorMode.FULL);
        assertTrue(validator.isValid("9783104909271"));
    }

    @Test
    public void isb10ValidInFULLValidMode() {
        ValidatorUtil.getSingleton().setMode(EValidatorMode.FULL);
        assertTrue(validator.isValid("1861972717"));
    }

    @Test
    public void validInSIMPLEValidMode() {
        ValidatorUtil.getSingleton().setMode(EValidatorMode.SIMPLE);
        assertTrue(validator.isValid("1861972717"));
        ValidatorUtil.getSingleton().setMode(EValidatorMode.FULL);
    }

    @Test
    public void validInOFFValidMode() {
        ValidatorUtil.getSingleton().setMode(EValidatorMode.OFF);
        assertTrue(validator.isValid("invalid input"));
        ValidatorUtil.getSingleton().setMode(EValidatorMode.FULL);
    }

    @Test
    public void invalidType() {
        try {
            assertFalse(validator.isValid("1861972717", RelatedIdentifierType.AR_XIV));
        } catch (UnsupportedMediaTypeException ignored) {
        }
    }


    @Test
    public void isbn13InvalidInFULLValidMode() {
        ValidatorUtil.getSingleton().setMode(EValidatorMode.FULL);
        try {
            assertFalse(validator.isValid("1234567890123"));
        } catch (BadArgumentException ignored) {
        }
    }

    @Test
    public void isbn10invalidInSIMPLEValidMode() {
        ValidatorUtil.getSingleton().setMode(EValidatorMode.SIMPLE);
        try {
            assertFalse(validator.isValid("1234567890"));
        } catch (BadArgumentException ignored) {
        }
        ValidatorUtil.getSingleton().setMode(EValidatorMode.FULL);
    }

    @Test
    public void invalidLength() {
        try {
            assertFalse(validator.isValid("12345678901"));
        } catch (BadArgumentException ignored) {
        }
    }
}