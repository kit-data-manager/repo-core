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
package edu.kit.datamanager.repo.dao.spec.dataresource;

import edu.kit.datamanager.repo.domain.DataResource;
import java.time.Instant;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author jejkal
 */
public class LastUpdateSpecification{

  /**
   * Hidden constructor.
   */
  private LastUpdateSpecification(){
  }

  public static Specification<DataResource> toSpecification(Instant lastUpdateFrom, Instant lastUpdateUntil){
    Specification<DataResource> newSpec = Specification.where(null);
    if(lastUpdateFrom == null && lastUpdateUntil == null){
      return newSpec;
    }

    return (Root<DataResource> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
      if(lastUpdateFrom != null && lastUpdateUntil != null){
        return builder.and(builder.between(root.get("lastUpdate"), lastUpdateFrom, lastUpdateUntil));
      } else if(lastUpdateFrom == null){
        return builder.and(builder.lessThan(root.get("lastUpdate"), lastUpdateUntil));
      }

      //otherwise, lastUpdateUntil is null
      return builder.and(builder.greaterThan(root.get("lastUpdate"), lastUpdateFrom), root.get("lastUpdate").isNotNull());
    };
  }
}
