/*
 * Copyright 2021 Karlsruhe Institute of Technology.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.kit.datamanager.repo.util;

import edu.kit.datamanager.exceptions.ServiceUnavailableException;
import edu.kit.datamanager.exceptions.UnsupportedMediaTypeException;
import edu.kit.datamanager.repo.util.validators.EValidatorMode;
import org.datacite.schema.kernel_4.RelatedIdentifierType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author maximilianiKIT
 */
public class ValidatorUtilTest {

    @BeforeEach
    public void setValidatorMode() {
        ValidatorUtil.getSingleton().setMode(EValidatorMode.FULL);
    }

    @AfterEach
    public void resetValidatorMode() {
        ValidatorUtil.getSingleton().setMode(EValidatorMode.FULL);
    }

    @Test
    public void validNumberOfValidators() {
        Assert.assertEquals(4, ValidatorUtil.getSingleton().getAllAvailableValidatorTypes().size());
    }

    @Test
    public void valid() {
        assertTrue(ValidatorUtil.getSingleton().isValid("https://kit.edu", RelatedIdentifierType.URL));
    }

    @Test
    public void validTypeString() {
        assertTrue(ValidatorUtil.getSingleton().isValid("https://kit.edu", "URL"));
    }

    @Test
    public void invalidInputString() {
        try {
            assertFalse(ValidatorUtil.getSingleton().isValid("https://kit.example", RelatedIdentifierType.URL));
        } catch (ServiceUnavailableException ignored) {
        }
    }

    @Test
    public void invalidTypeString() {
        try {
            assertTrue(ValidatorUtil.getSingleton().isValid("https://kit.edu", "INVALID"));
        } catch (UnsupportedMediaTypeException ignored) {
        }
    }

    @Test
    public void unimplementedType() {
        try {
            assertFalse(ValidatorUtil.getSingleton().isValid("https://kit.edu", RelatedIdentifierType.ARK));
        } catch (UnsupportedMediaTypeException ignored) {
        }
    }

    @Test
    public void getMode() {
        assertEquals(ValidatorUtil.getSingleton().getMode(), EValidatorMode.FULL);
    }

    @Test
    public void setMode() {
        ValidatorUtil.getSingleton().setMode(EValidatorMode.OFF);
        assertEquals(ValidatorUtil.getSingleton().getMode(), EValidatorMode.OFF);
        ValidatorUtil.getSingleton().setMode(EValidatorMode.FULL);
    }
}