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
import edu.kit.datamanager.exceptions.ServiceUnavailableException;
import edu.kit.datamanager.repo.util.validators.IIdentifierValidator;
import org.datacite.schema.kernel_4.RelatedIdentifierType;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class validates Handles and Handle-like systems (e.g. DOIs) with help of handle.net.
 *
 * @author maximilianiKIT
 * @see <a href="https://handle.net">https://handle.net</a>
 */
public class HandleValidator implements IIdentifierValidator {
    private String serverURL;
    private String humanReadableServer;
    private String apiPath;
    private RelatedIdentifierType supportedType;
    private String regex;
    private String standaloneRegex;

    /**
     * This constructor provides default values for validating Handles from handle.net.
     */
    public HandleValidator() {
        serverURL = "https://hdl.handle.net/";
        humanReadableServer = "handle.net";
        apiPath = "api/handles/";
        supportedType = RelatedIdentifierType.HANDLE;
        regex = "^(http://|https://|hdl:)/?(.+)?(10\\.[A-Za-z0-9.]+)/([A-Za-z0-9.]+)$";
        standaloneRegex = "^([A-Za-z0-9.]+)/([A-Za-z0-9.]+)$";
    }

    /**
     * This constructor is used by other validators to validate Handle-like validators.
     *
     * @param serverURL           the URL of the service
     * @param humanReadableServer the human readable server name that is used for logging and exceptions.
     * @param apiPath             the path to the api
     * @param supportedType       the identifier type
     * @param regex               the regex for identifier urls
     * @param standaloneRegex     the regex for the pure identifier (prefix/suffix)
     */
    public HandleValidator(String serverURL,
                           String humanReadableServer,
                           String apiPath,
                           RelatedIdentifierType supportedType,
                           String regex,
                           String standaloneRegex) {
        this.serverURL = serverURL;
        this.humanReadableServer = humanReadableServer;
        this.apiPath = apiPath;
        this.supportedType = supportedType;
        this.regex = regex;
        this.standaloneRegex = standaloneRegex;
    }

    @Override
    public RelatedIdentifierType getSupportedType() {
        return supportedType;
    }

    @Override
    public boolean isValid(String input) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        Pattern pattern2 = Pattern.compile(standaloneRegex);
        Matcher matcher2 = pattern2.matcher(input);
        boolean result;

        if (matcher.find()) {
            if (matcher.group(3).contains("/n") || matcher.group(4).contains("/n")) {
                LOGGER.error("Illegal line breaks in input {}", input);
                throw new BadArgumentException("Illegal line breaks in input: " + input);
            }
            LOGGER.debug("Found {} with regex", matcher.group(0));
            if (matcher.group(2) != null && (!matcher.group(2).equals(humanReadableServer + "/") || !matcher.group(2).equals(humanReadableServer + apiPath)) && (matcher.group(1).equals("https://") || matcher.group(1).equals("http://"))) {
                LOGGER.debug("Server address: {}", matcher.group(2));
                result = isDownloadable((matcher.group(1) + matcher.group(2)), matcher.group(3), matcher.group(4));
            } else result = isDownloadable(serverURL + apiPath, matcher.group(3), matcher.group(4));
        } else if (matcher2.find()) {
            result = isDownloadable(serverURL + apiPath, matcher2.group(1), matcher2.group(2));
        } else throw new BadArgumentException("Invalid input: " + input);

        return result;
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
    private boolean isDownloadable(String serverAddress, String prefix, String suffix) {
        IIdentifierValidator urlValidator = new URLValidator();
        boolean serverValid = false;
        boolean allValid = false;

        LOGGER.debug("Server address: {}", serverAddress);
        LOGGER.debug("Prefix: {}", prefix);
        LOGGER.debug("Suffix: {}", suffix);

        // Check if the serverAddress is valid or at least available
        try {
            serverValid = urlValidator.isValid(serverAddress);
            LOGGER.debug("The server address '" + serverAddress + "' is valid!");
        } catch (ServiceUnavailableException e) {
            LOGGER.error("The server address '{}' is not valid!", serverAddress, e);
        } catch (ResponseStatusException e) {
            serverValid = true;
            LOGGER.debug("The server address '{}' is valid, but throws status '{}'!", serverAddress, e.getStatus());
        } catch (Exception e) {
            LOGGER.error("The server address '{}' is not valid!", serverAddress, e);
        }

        // Check if the prefix is valid
        try {
            urlValidator.isValid(serverURL + apiPath + "0.NA/" + prefix);
            if (serverValid) LOGGER.debug("The prefix '" + prefix + "' is valid!");
            else {
                // If the prefix is valid, but the serverAddress is not, the input can't be accepted as fully valid.
                LOGGER.error("A connection to the server '" + serverAddress + "' is not possible, but the prefix '" + prefix + "' has been confirmed as valid by '" + humanReadableServer + "'.");
                throw new ServiceUnavailableException("A connection to the server '" + serverAddress + "' is not possible, but the prefix '" + prefix + "' has been confirmed as valid by '" + humanReadableServer + "'.");
            }
        } catch (ServiceUnavailableException ignored) {
        } catch (Exception e) {
            LOGGER.error("The prefix '" + prefix + "' is not provable on '" + humanReadableServer + "'.");
            throw new BadArgumentException("The prefix '" + prefix + "' is not provable on '" + humanReadableServer + "'.");
        }

        // Check if the URL built from serverAddress, prefix and suffix is valid and solvable.
        // This means that the URL returns a 2xx or 3xx HTTP response code.
        try {
            allValid = urlValidator.isValid(serverAddress + prefix + "/" + suffix);
        } catch (ResponseStatusException e) {
            HttpStatus status = e.getStatus();
            if (status.is2xxSuccessful()) {
                LOGGER.debug("The '{}' '{}' is valid!", getSupportedType(), prefix + "/" + suffix);
                allValid = true;
            } else if (status.is3xxRedirection()) {
                LOGGER.debug("The '{}' '{}' is valid!", getSupportedType(), prefix + "/" + suffix);
                allValid = true;
            }
        } catch (Exception e) {
            LOGGER.error("The prefix '" + prefix + "' is valid, but the suffix '" + suffix + "' is not.");
            throw new BadArgumentException("The prefix '" + prefix + "' is valid, but the suffix '" + suffix + "' is not.");
        }

        return allValid;
    }
}
