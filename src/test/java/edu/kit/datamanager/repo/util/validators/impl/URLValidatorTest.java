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

import org.datacite.schema.kernel_4.RelatedIdentifierType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

class URLValidatorTest {

    ValidatorInterface validator = new URLValidator();

    @Test
    void valid() {
        try {
            assertTrue(validator.isValid("http://hdl.handle.net/api/handles/10.1038/nphys1170"));
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void invalidURL() {
        try {
            assertFalse(validator.isValid("hdl.handle/10.1038/nphys1170"));
        } catch (ValidationWarning e) {
            fail(e);
        } catch (ValidationError ignored) {
        }
    }

    @Test
    void invalidType() {
        try {
            assertFalse(validator.isValid("hdl.handle/10.1038/nphys1170", RelatedIdentifierType.ARK));
        } catch (ValidationWarning ignored) {
        } catch (ValidationError validationError) {
            fail(validationError);
        }
    }

    @Test
    void serverNotReachable() {
        try {
            assertFalse(validator.isValid("https://hdl.test.example/10.1038/nphys1170"));
        } catch (ValidationWarning ignored) {
        } catch (ValidationError validationError) {
            fail(validationError);
        }
    }

    @Test
    void invalidPrefixInURL() {
        try {
            assertFalse(validator.isValid("http://hdl.handle.net/api/handles/10.10385/nphys1170"));
        } catch (ValidationError ignored) {
        } catch (ValidationWarning validationWarning) {
            fail(validationWarning);
        }
    }


    @Test
    void invalidCharacters() {
        try {
            assertFalse(validator.isValid("http://google.com/®¡“¢∂‚/®¡“¢∂‚"));
        } catch (ValidationWarning e) {
            fail(e);
        } catch (ValidationError ignored) {
        }
    }
}