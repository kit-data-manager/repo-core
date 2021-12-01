/*
 * Copyright 2021 Karlsruhe Institute of TechnoLOGy.
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

package edu.kit.datamanager.repo.util.validators.impl;

import edu.kit.datamanager.repo.util.validators.IIdentifierValidator;
import org.datacite.schema.kernel_4.RelatedIdentifierType;

/**
 * This class validates DOIs with help of doi.org.
 *
 * @author maximilianiKIT
 * @see <a href="https://doi.org">https://doi.org</a>
 */
public class DOIValidator implements IIdentifierValidator {
    @Override
    public RelatedIdentifierType getSupportedType() {
        return RelatedIdentifierType.DOI;
    }

    @Override
    public boolean isValid(String input) {
        return new HandleValidator("https://doi.org/",
                "doi.org",
                "api/handles/",
                RelatedIdentifierType.DOI,
                "^(http://|https://|doi:)/?(.+)?(10\\.[A-Za-z0-9.]+)/([A-Za-z0-9.]+)$",
                "^(10\\.[A-Za-z0-9.]+)/([A-Za-z0-9.]+)$")
                .isValid(input);
    }
}
