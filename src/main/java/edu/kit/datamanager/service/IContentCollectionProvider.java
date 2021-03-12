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

import edu.kit.datamanager.entities.ContentElement;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.springframework.http.MediaType;

/**
 *
 * @author jejkal
 */
public interface IContentCollectionProvider{

  /**
   * Provide the content collection provided as URI list argument.
   *
   * @param collection A list of collection elements.
   * @param mediaType The mediaType, e.g. obtained from an Accept header, that
   * should be returned.
   * @param response The response entity the data is written to.
   */
  void provide(List<ContentElement> collection, MediaType mediaType, HttpServletResponse response);

  /**
   * Check if this provider implementation is capable of providing content
   * accessible via protocal 'schema', e.g. http(s) or file.
   *
   * @param schema The schema of a collection element's URI.
   *
   * @return TRUE if the content can be provided, FALSE otherwise.
   */
  boolean canProvide(String schema);

  /**
   * Check if the provided MediaType is supported for a response. The MediaType
   * is e.g. received via Accept header from the client and should address
   * exactly one collection provider. Otherwise, the first provider capable of
   * delivering a certain type is called and might return an unexpected result.
   * Therefore, while implementing a collection provider you should make use of
   * custom, vendor specific media types like e.g.
   * 'application/vnd.datamanager.content-information+json'.
   *
   * @param mediaType The media type to check.
   *
   * @return TRUE if media type is supported, FALSE otherwise.
   */
  boolean supportsMediaType(MediaType mediaType);

  /**
   * Returns a list of supported media types.
   *
   * @return A list of supported media types, which should never be empty.
   */
  @NotNull
  @NotEmpty
  MediaType[] getSupportedMediaTypes();
}
