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
import edu.kit.datamanager.repo.util.validators.impl.URLValidator;
import org.datacite.schema.kernel_4.RelatedIdentifierType;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author maximilianiKIT
 */
public class URLValidatorTest {

    IIdentifierValidator validator = new URLValidator();

    @Test
    public void httpValidInFULLValidMode() {
        ValidatorUtil.getSingleton().setMode(EValidatorMode.FULL);
        assertTrue(validator.isValid("http://hdl.handle.net"));
    }

    @Test
    public void httpsValidInFULLValidMode() {
        ValidatorUtil.getSingleton().setMode(EValidatorMode.FULL);
        assertTrue(validator.isValid("https://kit.edu"));
    }

    @Test
    public void validInSIMPLEValidMode() {
        ValidatorUtil.getSingleton().setMode(EValidatorMode.SIMPLE);
        assertTrue(validator.isValid("http://kit.example"));
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
            assertFalse(validator.isValid("hdl.example/10.1038/nphys1170", RelatedIdentifierType.AR_XIV));
        } catch (UnsupportedMediaTypeException ignored) {
        }
    }


    @Test
    public void invalidInFULLValidMode() {
        ValidatorUtil.getSingleton().setMode(EValidatorMode.FULL);
        try {
            assertFalse(validator.isValid("https://google.example"));
        } catch (BadArgumentException | ServiceUnavailableException ignored) {
        }
    }

    @Test
    public void invalidInSIMPLEValidMode() {
        ValidatorUtil.getSingleton().setMode(EValidatorMode.SIMPLE);
        try {
            assertFalse(validator.isValid("hdl.handle/10.1038/nphys1170"));
        } catch (BadArgumentException ignored) {
        }
        ValidatorUtil.getSingleton().setMode(EValidatorMode.FULL);
    }

    @Test
    public void serverNotReachable() {
        try {
            assertFalse(validator.isValid("https://hdl.test.example/10.1038/nphys1170"));
        } catch (ServiceUnavailableException ignored) {
        }
    }

    @Test
    public void invalidCharacters() {
        try {
            assertFalse(validator.isValid("http://google.com/®¡“¢∂‚/®¡“¢∂‚"));
        } catch (BadArgumentException ignored) {
        }
    }
}