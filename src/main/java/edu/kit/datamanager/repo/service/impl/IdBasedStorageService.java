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

import edu.kit.datamanager.repo.configuration.IdBasedStorageProperties;
import edu.kit.datamanager.repo.configuration.RepoBaseConfiguration;
import edu.kit.datamanager.repo.domain.DataResource;
import edu.kit.datamanager.repo.service.IRepoStorageService;
import java.nio.file.Paths;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 */
@Component
public class IdBasedStorageService implements IRepoStorageService {
  
  public static final String SERVICE_NAME = "idBased";

  @Autowired
  private Logger logger;

  @Autowired
  private IdBasedStorageProperties applicationProperties;

  @Override
  public void configure(RepoBaseConfiguration applicationProperties) {
  }
  
  public void configure(IdBasedStorageProperties applicationProperties) {
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

    String[] createPathToRecord = resource.getId().replace("-", "").split("(?<=\\G.{" + charPerDirectory + "})");
    int depth = maxDepth < createPathToRecord.length ? maxDepth : createPathToRecord.length;
    String[] pathElements = new String[depth];
    for (int index = 0; index < depth; index++) {
      pathElements[index] = createPathToRecord[index];
    }
    String pattern = Paths.get("/", pathElements).toString();

    if (pattern.startsWith("/")) {
      pattern = pattern.substring(1);
    }

    return pattern;
  }
}
