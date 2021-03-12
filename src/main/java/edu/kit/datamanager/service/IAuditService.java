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

/**
 *
 * @author jejkal
 * @param <C> The generic type.
 */
public interface IAuditService<C>{

  /**
   * Capture audit information for the provided resource and persist it
   * associated to the provided principal who changed the resource.
   *
   * @param C resource The changed resource, which must not be null.
   * @param principal The principal who changed the resource, which must not be
   * null.
   *
   */
  void captureAuditInformation(@NotNull C resource, @NotNull String principal);

  /**
   * Get all known audit information for a specific resource in a certain range.
   * The result contains the JSON representation of the audit information or
   * nothing if no audit information are available.
   *
   * @param resourceId The identifier of the resource to obtain audit
   * information for.
   * @param page The number of the page to return.
   * @param resultsPerPage The number of results per page.
   *
   * @return An optional containing the requested audit information as JSON or
   * nothing.
   */
  Optional<String> getAuditInformationAsJson(@NotNull String resourceId, int page, int resultsPerPage);

  /**
   * Get a specific version of a particular resource. If no resource in the
   * provided version could be found, the result should be empty and the caller
   * should take care of handling the situation.
   *
   * @param resourceId The identifier of the resource to obtain the version for.
   * @param version The resource version.
   *
   * @return The resource in the provided version.
   */
  Optional<C> getResourceByVersion(@NotNull String resourceId, long version);

  /**
   * Get the current version of the resource with the provided resource id.
   *
   * @param resourceId The identifier of the resource to obtain the current
   * version for.
   *
   * @return The current version of the resource.
   */
  long getCurrentVersion(@NotNull String resourceId);

  /**
   * Delete all audit information for a specific resource. This should typically
   * happen, if the resource is deleted.
   *
   * @param resourceId The identifier of the resource to delete all audit
   * information for.
   * @param resource The resource in their final state before deletion.
   */
  void deleteAuditInformation(@NotNull String resourceId, @NotNull C resource);

}
