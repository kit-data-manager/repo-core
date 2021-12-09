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
import edu.kit.datamanager.exceptions.CustomInternalServerError;
import edu.kit.datamanager.exceptions.ServiceUnavailableException;
import edu.kit.datamanager.repo.util.ValidatorUtil;
import edu.kit.datamanager.repo.util.validators.EValidatorMode;
import edu.kit.datamanager.repo.util.validators.IIdentifierValidator;
import org.apache.http.HttpStatus;
import org.datacite.schema.kernel_4.RelatedIdentifierType;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class validates URLs via a HTTP get request.
 *
 * @author maximilianiKIT
 */
public class URLValidator implements IIdentifierValidator {

    @Override
    public RelatedIdentifierType getSupportedType() {
        return RelatedIdentifierType.URL;
    }

    @Override
    public boolean isValid(String input) {
        String regex = "^(http|https):\\/\\/[-A-Za-z0-9+&@#\\/%?=~_|!:,.;]+[-A-Za-z0-9+&@#\\/%=~_|]$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        URL urlHandler;
        HttpURLConnection con;
        LOGGER.debug("URL: {}", input);
        int status;
        boolean validRegex = matcher.find();
        boolean result = false;

        if (ValidatorUtil.getSingleton().getMode() == EValidatorMode.OFF) result = true;
        else if (ValidatorUtil.getSingleton().getMode() == EValidatorMode.SIMPLE) result = validRegex;
        else if (ValidatorUtil.getSingleton().getMode() == EValidatorMode.FULL && validRegex) {
            try {
                urlHandler = new URL(input);
                con = (HttpURLConnection) urlHandler.openConnection();
                con.setRequestMethod("GET");
                status = con.getResponseCode();
                LOGGER.debug("HTTP status: {}", status);
                if (status != HttpStatus.SC_OK) {
                    LOGGER.error("Connection to URL '{}' fails with status '{}'", input, status);
                    throw new ResponseStatusException(org.springframework.http.HttpStatus.valueOf(status));
                }
            } catch (IOException e) {
                LOGGER.warn("No connection to the server '{}' possible. Do you have an internet connection?", input);
                throw new ServiceUnavailableException("No connection to the server '" + input + "' possible. Do you have an internet connection?");
            }
            LOGGER.debug("The URL '{}' is valid!", input);
            result = true;
        } else if (!validRegex) {
            LOGGER.error("The URL '{}' does not match the pattern or contains illegal characters.", input);
            throw new BadArgumentException("The URL '" + input + "' does not match the pattern or contains illegal characters.");
        }
        return result;
    }
}
