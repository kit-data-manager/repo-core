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

import edu.kit.datamanager.exceptions.BadArgumentException;
import edu.kit.datamanager.exceptions.MessageValidationException;
import edu.kit.datamanager.exceptions.ServiceUnavailableException;
import edu.kit.datamanager.repo.util.validators.IIdentifierValidator;
import lombok.SneakyThrows;
import org.datacite.schema.kernel_4.RelatedIdentifierType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class validates DOIS with help of doi.org.
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
        String regex = "^(http:\\/\\/|https:\\/\\/|doi:)\\/?(.+)?(10\\.[A-Za-z0-9.]+)\\/([A-Za-z0-9.]*)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            LOG.debug(matcher.group(0));
            if (matcher.group(2) != null && (!matcher.group(2).equals("doi.org/") || matcher.group(2).equals("doi.org/api/handles/")) && (matcher.group(1).equals("https://") || matcher.group(1).equals("http://"))) {
                LOG.debug("Server address: {}", matcher.group(2));
                return isDownloadable((matcher.group(1) + matcher.group(2)), matcher.group(3), matcher.group(4));
            }
            return isDownloadable("https://doi.org/api/handles/", matcher.group(3), matcher.group(4));
        } else {
            String regex2 = "([A-Za-z0-9.]+)/([A-Za-z0-9.]+)";
            Pattern pattern2 = Pattern.compile(regex2);
            Matcher matcher2 = pattern2.matcher(input);
            if (matcher2.find())
                return isDownloadable("https://doi.org/api/handles/", matcher2.group(1), matcher2.group(2));
        }
        throw new BadArgumentException("Invalid input!");
    }

    /**
     * This method checks if the given prefix and suffix is downloadable.
     * If there is some invalid input, it will validate the prefix with help of the RESTful API by doi.org.
     *
     * @param serverAddress from the server which should be uses for the validation.
     * @param prefix        the DOI prefix
     * @param suffix        the DOI suffix
     * @return true if the record is downloadable
     */
    @SneakyThrows
    private boolean isDownloadable(String serverAddress, String prefix, String suffix) {
        IIdentifierValidator urlValidator = new URLValidator();
        boolean fullValid = false;
        boolean serverUnavailable = false;

        LOG.debug("Server address: {}", serverAddress);
        LOG.debug("Prefix: {}", prefix);
        LOG.debug("Suffix: {}", suffix);

        try {
            fullValid = urlValidator.isValid(serverAddress + "" + prefix + "/" + suffix);
        } catch (ServiceUnavailableException e) {
            LOG.warn("Server address {} is not reachable", serverAddress + "" + prefix + "/" + suffix);
            serverUnavailable = true;
        } catch (Exception ignored) {
        }

        if (fullValid) {
            LOG.debug("The DOI {} is valid!", prefix + "/" + suffix);
            return true;
        }

        Exception buffer = null;

        LOG.warn("Either the suffix or the prefix might be invalid. Proving if prefix is valid...");
        try {
            if (urlValidator.isValid("https://doi.org/0.NA/" + prefix)) {
                LOG.info("The prefix {} is valid!", prefix);
                if (!serverUnavailable) buffer = new BadArgumentException("The prefix is valid, but suffix is not.");
                else buffer = new ServiceUnavailableException("Connection to the specified server is not possible, but the prefix has been confirmed as valid on doi.org.");
            }
        } catch (BadArgumentException ignored) {
        }

        if(buffer!= null) throw buffer;

        LOG.error("The entered prefix {} is invalid!", prefix);
        if (!serverUnavailable) throw new BadArgumentException("Prefix not provable on doi.org");
        else throw new BadArgumentException("A connection to the specified server is not possible and the prefix has been detected as invalid by doi.org.");
    }
}
