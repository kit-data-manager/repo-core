/*
 * Copyright 2018 Karlsruhe Institute of Technology.
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
package edu.kit.datamanager.repo.dao.spec.contentinformation;

import edu.kit.datamanager.repo.domain.ContentInformation;
import java.util.Arrays;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author jejkal
 */
public class ContentInformationTagSpecification{

  /**
   * Hidden constructor.
   */
  private ContentInformationTagSpecification(){
  }

  public static Specification<ContentInformation> toSpecification(final String... tags){
    Specification<ContentInformation> newSpec = Specification.where(null);
    if(tags == null || tags.length == 0){
      return newSpec;
    }

    return (Root<ContentInformation> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
      query.distinct(true);

      return builder.and(root.join("tags").in(Arrays.asList(tags)));
    };
  }
}
