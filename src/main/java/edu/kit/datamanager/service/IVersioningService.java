/*
 * Copyright 2020 Karlsruhe Institute of Technology.
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

import edu.kit.datamanager.entities.VersionInfo;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public interface IVersioningService{

  void configure();

  /**
   * adds the file of an object to the default OCFL repository.
   *
   * @param resourceId identifier of the object
   * @param callerId name of the user
   * @param path path of the file
   * @param data the file
   * @param options contains three keys: finalize, token number and parent.
   */
  void write(String resourceId, String callerId, String path, InputStream data, Map<String, String> options);

  /**
   * returns files of an object's version.
   *
   * @param resourceId
   * @param callerId
   * @param path
   * @param versionId
   * @param destination
   * @param options
   */
  void read(String resourceId, String callerId, String path, String versionId, OutputStream destination, Map<String, String> options);

  /**
   * returns information for a specific resource
   *
   * @param resourceId
   * @param path
   * @param versionId
   * @param options
   * @return
   */
  VersionInfo info(String resourceId, String path, String versionId, Map<String, String> options);

  /**
   * Returns the name of this versioning service. The name should be unique.
   * Otherwise, a random implementation with the provided name will be used.
   *
   * @return The service name.
   */
  String getServiceName();
}
