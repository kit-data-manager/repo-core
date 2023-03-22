/*
 * Copyright 2019 Karlsruhe Institute of Technology.
 *
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
package edu.kit.datamanager.repo.service.impl;

import edu.kit.datamanager.repo.configuration.StorageServiceProperties;
import edu.kit.datamanager.repo.domain.DataResource;
import edu.kit.datamanager.repo.service.IRepoStorageService;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 */
@Component
public class DateBasedStorageService implements IRepoStorageService {

    private static final Logger logger = LoggerFactory.getLogger(DateBasedStorageService.class);

    private StorageServiceProperties applicationProperties;

    @Override
    public void configure(StorageServiceProperties applicationProperties) {
         this.applicationProperties = applicationProperties;
    }

    @Override
    public String getServiceName() {
        return "dateBased";
    }

    @Override
    public String createPath(DataResource resource) {
        Map<String, String> data = new HashMap<>();
        // ToDo: Get create date of data resource.
        data.put("year", String.format("%d", Calendar.getInstance().get(Calendar.YEAR)));
        data.put("month", String.format("%02d", Calendar.getInstance().get(Calendar.MONTH)));
        data.put("day", String.format("%02d", Calendar.getInstance().get(Calendar.DAY_OF_MONTH)));
        data.put("hour", String.format("%02d", Calendar.getInstance().get(Calendar.HOUR_OF_DAY)));
        data.put("minute", String.format("%02d", Calendar.getInstance().get(Calendar.MINUTE)));

        String pattern = applicationProperties.getPathPattern().replaceAll("\\@", "\\$");
        if (pattern.startsWith("/")) {
            pattern = pattern.substring(1);
        }
        if (pattern.endsWith("/")) {
            pattern = pattern.substring(0, pattern.length() - 1);
        }
        return StringSubstitutor.replace(pattern, data);
    }
}
