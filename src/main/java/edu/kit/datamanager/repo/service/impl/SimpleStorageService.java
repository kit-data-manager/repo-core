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

import edu.kit.datamanager.repo.configuration.RepoBaseConfiguration;
import edu.kit.datamanager.repo.configuration.StorageServiceProperties;
import edu.kit.datamanager.repo.domain.DataResource;
import edu.kit.datamanager.repo.service.IRepoStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 */
@Component
public class SimpleStorageService implements IRepoStorageService {

  private static final Logger logger = LoggerFactory.getLogger(SimpleStorageService.class);

  @Override
  public void configure(StorageServiceProperties applicationProperties) {
  }

  @Override
  public String getServiceName() {
    return "simple";
  }

  @Override
  public String createPath(DataResource resource) {
    return "repo";
  }
}
