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
import java.io.File;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 */
@Component
public class IdBasedStorageService implements IRepoStorageService {

  public static final String SERVICE_NAME = "idBased";

  private static final Logger logger = LoggerFactory.getLogger(IdBasedStorageService.class);

  private StorageServiceProperties applicationProperties;

  @Override
  public void configure(StorageServiceProperties applicationProperties) {
       this.applicationProperties = applicationProperties;
  }


  @Override
  public String getServiceName() {
    return SERVICE_NAME;
  }

  @Override
  public String createPath(DataResource resource) {
    // Remove all '-' and split resulting string to substrings with 4 characters each.
    int charPerDirectory = applicationProperties.getCharPerDirectory();
    int maxDepth = applicationProperties.getMaxDepth();

    StringBuilder builder = new StringBuilder();
    // remove all possible invalid characters for a path from id. 
    builder.append(resource.getId().replaceAll("[^A-Za-z0-9]", ""));
    // to prevent an empty string.
    if (builder.toString().isEmpty()) {
     builder.append("hash");
     builder.append(Integer.toString(Math.abs(resource.getId().hashCode())));
    }
    // split id in small pieces.
    String[] createPathToRecord = builder.toString().split("(?<=\\G.{" + charPerDirectory + "})");

    int depth = maxDepth < createPathToRecord.length ? maxDepth : createPathToRecord.length;
    String[] pathElements = new String[depth];
    for (int index = 0; index < depth; index++) {
      pathElements[index] = createPathToRecord[index];
    }
    // Add localDir due to mandatory first parameter.
    String localDir = "." + File.separator;
    String pattern = Paths.get(localDir, pathElements).toString();
    // remove localDir from path
    pattern = pattern.substring(localDir.length());
    
    return pattern;
  }
}
