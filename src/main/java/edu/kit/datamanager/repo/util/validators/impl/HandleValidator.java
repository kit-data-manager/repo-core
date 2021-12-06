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
import lombok.SneakyThrows;
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

    /**
     * This constructor provides default values for validating Handles from handle.net.
     */
    public HandleValidator() {
        serverURL = "https://hdl.handle.net/";
        humanReadableServer = "handle.net";
        apiPath = "api/handles/";
        supportedType = RelatedIdentifierType.HANDLE;
        regex = "^(http:\\/\\/|https:\\/\\/|hdl:)?([A-Za-z0-9+&@#\\/%?=~_|!:,.;]+[A-Za-z0-9+&@#\\/%=~_|]\\/)?([A-Za-z0-9.]+)\\/([A-Za-z0-9.]+)$";
    }

    /**
     * This constructor is used by other validators to validate Handle-like validators.
     *
     * @param serverURL           the URL of the service
     * @param humanReadableServer the human readable server name that is used for logging and exceptions.
     * @param apiPath             the path to the api
     * @param supportedType       the identifier type
     * @param regex               the regex for identifier urls
     */
    public HandleValidator(String serverURL,
                           String humanReadableServer,
                           String apiPath,
                           RelatedIdentifierType supportedType,
                           String regex) {
        this.serverURL = serverURL;
        this.humanReadableServer = humanReadableServer;
        this.apiPath = apiPath;
        this.supportedType = supportedType;
        this.regex = regex;
    }

    @Override
    public RelatedIdentifierType getSupportedType() {
        return supportedType;
    }

    @Override
    public boolean isValid(String input) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            if (matcher.group(3).contains("/n") || matcher.group(4).contains("/n")) {
                LOGGER.debug(matcher.group(3));
                LOGGER.debug(matcher.group(4));
                LOGGER.error("Illegal line breaks in input '{}'", input);
                throw new BadArgumentException("Illegal line breaks in input: " + input);
            }

            LOGGER.debug("Found '{}' with regex in input.", matcher.group(0));
            if (matcher.group(1) != null && matcher.group(2) != null && (matcher.group(1).equals("http://") || matcher.group(1).equals("https://"))) {
                LOGGER.debug("Proving {} on '{}' ...", supportedType, matcher.group(2));
                isDownloadable(matcher.group(1) + matcher.group(2), matcher.group(3), matcher.group(4));
            } else if (matcher.group(2) == null) {
                LOGGER.debug("No custom server entered. Proving the {} on '{}' ...", supportedType, humanReadableServer);
                isDownloadable(serverURL + apiPath, matcher.group(3), matcher.group(4));
            } else {
                LOGGER.error("The input '{}' contains an invalid or uncomplete server address and is therefore invalid.", input);
                throw new BadArgumentException("The input '" + input + "' contains an invalid or uncomplete server address and is therefore invalid.");
            }
        } else {
            LOGGER.error("The input '{}' does not meet the minimum requirements of the type {} and is therefore invalid!", input, supportedType);
            throw new BadArgumentException("The input '" + input + "' does not meet the minimum requirements of the type " + supportedType + " and is therefore invalid!");
        }

        LOGGER.debug("The input '{}' is a valid {}!", input, supportedType);
        return true;
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
        Exception buffer = null;
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
                buffer = new ServiceUnavailableException("A connection to the server '" + serverAddress + "' is not possible, but the prefix '" + prefix + "' has been confirmed as valid by '" + humanReadableServer + "'.");
            }
        } catch (Exception e) {
            LOGGER.error("The prefix '" + prefix + "' is not provable on '" + humanReadableServer + "'.");
            throw new BadArgumentException("The prefix '" + prefix + "' is not provable on '" + humanReadableServer + "'.");
        }

        if (buffer != null) throw buffer;

        // Check if the URL built from serverAddress, prefix and suffix is valid and solvable.
        // This means that the URL returns a 2xx or 3xx HTTP response code.
        try {
            allValid = urlValidator.isValid(serverAddress + prefix + "/" + suffix);
        } catch (ResponseStatusException e) {
            int status = e.getStatus().value();
            if (status < 400) {
                LOGGER.debug("The '{}' '{}' is valid!", getSupportedType(), prefix + "/" + suffix);
                allValid = true;
            } else {
                LOGGER.error("The prefix '" + prefix + "' is valid, but the suffix '" + suffix + "' is not.");
                throw new BadArgumentException("The prefix '" + prefix + "' is valid, but the suffix '" + suffix + "' is not.");
            }
        } catch (Exception e) {
            LOGGER.error("The prefix '" + prefix + "' is valid, but the suffix '" + suffix + "' is not.");
            throw new BadArgumentException("The prefix '" + prefix + "' is valid, but the suffix '" + suffix + "' is not.");
        }

        return allValid;
    }
}
