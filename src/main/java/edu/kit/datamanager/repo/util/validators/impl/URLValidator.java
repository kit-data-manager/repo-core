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
import edu.kit.datamanager.exceptions.UnsupportedMediaTypeException;
import edu.kit.datamanager.repo.util.validators.IValidator;
import org.datacite.schema.kernel_4.RelatedIdentifierType;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class URLValidator implements IValidator {
    @Override
    public RelatedIdentifierType supportedType() {
        return RelatedIdentifierType.URL;
    }

    /**
     * This method must be implemented by any implementation.
     * It validates an input and either returns true or throws an exception.
     *
     * @param input to validate
     * @param type  of the input
     * @return true if input is valid for the special type of implementation.
     */
    @Override
    public boolean isValid(String input, RelatedIdentifierType type){
        if (type != supportedType()) {
            LOG.warn("Illegal type of validator");
            throw new UnsupportedMediaTypeException("Illegal type of Validator.");
        }

        URL urlHandler = null;
        HttpURLConnection con = null;
        LOG.debug("URL: {}", input);
        int status;
        try {
            urlHandler = new URL(input);
            con = (HttpURLConnection) urlHandler.openConnection();
            con.setRequestMethod("GET");
            status = con.getResponseCode();
            LOG.debug("HTTP status: {}", status);
            if (status != 200) {
                LOG.error("Invalid URL");
                throw new BadArgumentException("Invalid URL!");
            }
            return true;
        } catch (ProtocolException e) {
            LOG.warn("Error while setting request method");
            throw new CustomInternalServerError("Error setting request method");
        } catch (MalformedURLException e) {
            LOG.warn("Invalid URL");
            throw new BadArgumentException("Invalid URL");
        } catch (IOException e) {
            LOG.warn("No connection to the server possible. Do you have an internet connection?");
            throw new ServiceUnavailableException("No connection to the server possible. Do you have an internet connection?");
        }
    }
}
