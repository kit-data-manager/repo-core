///*
// * Copyright 2021 Karlsruhe Institute of TechnoLOGGERy.
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *       http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package edu.kit.datamanager.repo.util.validators.impl;
//
//import edu.kit.datamanager.exceptions.BadArgumentException;
//import edu.kit.datamanager.exceptions.MessageValidationException;
//import edu.kit.datamanager.repo.util.validators.IIdentifierValidator;
//import org.datacite.schema.kernel_4.RelatedIdentifierType;
//
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
///**
// * This class validates Handles with help of handle.net.
// *
// * @author maximilianiKIT
// * @see <a href="https://handle.net">https://handle.net</a>
// */
//public class HandleNetValidator implements IIdentifierValidator {
//    private static final String serverURL = "https://hdl.handle.net/";
//    private static final String apiPath = "api/handles/";
//
//    @Override
//    public RelatedIdentifierType getSupportedType() {
//        return RelatedIdentifierType.HANDLE;
//    }
//
//    @Override
//    public boolean isValid(String input) {
//        String regex = "^(hdl://|http://|https://)(.+)";
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(input);
//        boolean result;
//
//        if (matcher.find()) {
//            if (matcher.group(1).equals("https://") || matcher.group(1).equals("http://"))
//                result = isValidHTTP_URL(matcher.group(0));
//            else result = isValidHandle(matcher.group(2));
//        } else result = isValidHandle(input);
//
//        return result;
//    }
//
//    /**
//     * This method checks if the given prefix and suffix is downloadable.
//     * If there is some invalid input, it will validate the prefix with help of the RESTful API by handle.net.
//     *
//     * @param serverAddress from the server which should be uses for the validation.
//     * @param prefix        the handle prefix
//     * @param suffix        the handle suffix
//     * @return true if the record is downloadable
//     */
//    private boolean isDownloadable(String serverAddress, String prefix, String suffix) {
//        IIdentifierValidator urlValidator = new URLValidator();
//        boolean fullValid = false;
//
//        LOGGER.debug("Server address: {}", serverAddress);
//        LOGGER.debug("Prefix: {}", prefix);
//        LOGGER.debug("Suffix: {}", suffix);
//
//        try {
//            fullValid = urlValidator.isValid(serverAddress + "/" + prefix + "/" + suffix);
//        } catch (Exception ignored) {
//        }
//        if (!fullValid) {
//            LOGGER.warn("Either the suffix or the prefix might be invalid. Proving if the prefix is valid...");
//            if (urlValidator.isValid(serverURL + "0.NA/" + prefix)) {
//                LOGGER.info("The prefix {} is valid!", prefix);
//                throw new MessageValidationException("The prefix " + prefix + " is valid, but suffix " + suffix + " is not.");
//            } else {
//                LOGGER.error("The entered prefix {} is invalid!", prefix);
//                throw new BadArgumentException("The prefix " + prefix + " not provable on handle.net");
//            }
//        }
//        LOGGER.debug("The handle {} is valid!", prefix + "/" + suffix);
//        return true;
//    }
//
//    /**
//     * Uses the other isDownloadable method with the handle.net server as serverAddress
//     *
//     * @param prefix the handle prefix
//     * @param suffix the handle suffix
//     * @return true if the record is downloadable
//     */
//    private boolean isDownloadable(String prefix, String suffix) {
//        return isDownloadable(serverURL + apiPath, prefix, suffix);
//    }
//
//    /**
//     * This method uses regex to get the prefix, suffix and the server address out of a http or https URL.
//     * After this it uses the isDownloadable method to check whether the input is downloadable.
//     *
//     * @param url to validate
//     * @return true if the input is valid and downloadable.
//     */
//    private boolean isValidHTTP_URL(String url) {
//        String regex = "(http|https)://(.+)/([A-Za-z0-9.]+)/([A-Za-z0-9.]+)";
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(url);
//        if (matcher.find()) {
//            return isDownloadable((matcher.group(1) + "://" + matcher.group(2)), matcher.group(3), matcher.group(4));
//        } else throw new BadArgumentException("Invalid input");
//    }
//
//    /**
//     * This method uses regex to check whether unallowed characters are used and to extract the prefix and suffix.
//     * After this it uses the isDownloadable method to check whether the input is downloadable.
//     *
//     * @param handle to validate
//     * @return true if the input is valid and downloadable.
//     */
//    private boolean isValidHandle(String handle) {
//        String regex = "([A-Za-z0-9.]+)/([A-Za-z0-9.]+)";
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(handle);
//        if (matcher.find()) {
//            return isDownloadable(matcher.group(1), matcher.group(2));
//        } else throw new BadArgumentException("Invalid input");
//    }
//}
