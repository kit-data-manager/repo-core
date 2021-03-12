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
package edu.kit.datamanager.service;

import java.util.Optional;
import javax.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author jejkal
 */
public interface IServiceAuditSupport{

  /**
   * Get all known audit information for a specific resource in a certain range.
   * The result contains the JSON representation of the audit information or
   * nothing if no audit information are available.
   *
   * @param resourceId The identifier of the resource to obtain audit
   * information for.
   * @param pgbl Pageable information.
   *
   * @return An optional containing the requested audit information as JSON or
   * nothing.
   */
  Optional<String> getAuditInformationAsJson(@NotNull String resourceId, Pageable pgbl);

}
