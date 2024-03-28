/*
 * Copyright 2017 Karlsruhe Institute of Technology.
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
package edu.kit.datamanager.repo.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import edu.kit.datamanager.annotations.Searchable;
import edu.kit.datamanager.annotations.SecureUpdate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 *
 * @author jejkal
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "A resource's funding information.")
@Data
public class FundingReference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, accessMode = Schema.AccessMode.READ_ONLY)
    @SecureUpdate({"FORBIDDEN"})
    @Searchable
    private Long id;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @Field(type = FieldType.Text, name = "funderName")
    private String funderName;
    //use identifier?
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Field(type = FieldType.Nested, includeInParent = true)
    private FunderIdentifier funderIdentifier;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Field(type = FieldType.Nested, includeInParent = true)
    private Scheme awardNumber;
    @Field(type = FieldType.Keyword, name = "awardUri")
    private String awardUri;
    @Field(type = FieldType.Text, name = "awardTitle")
    private String awardTitle;

    public static FundingReference factoryFundingReference(String funderName, FunderIdentifier funderIdentifier, Scheme awardNumber, String awardUri, String awardTitle) {
        FundingReference result = new FundingReference();
        result.funderName = funderName;
        result.funderIdentifier = funderIdentifier;
        result.awardNumber = awardNumber;
        result.awardUri = awardUri;
        result.awardTitle = awardTitle;
        return result;
    }

}
