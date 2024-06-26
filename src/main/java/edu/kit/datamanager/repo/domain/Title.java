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
import edu.kit.datamanager.entities.BaseEnum;
import edu.kit.datamanager.util.EnumUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 *
 * @author jejkal
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Schema(description = "The title of a resource.")
public class Title {

    public enum TYPE implements BaseEnum {
        ALTERNATIVE_TITLE("AlternativeTitle"),
        SUBTITLE("Subtitle"),
        TRANSLATED_TITLE("TranslatedTitle"),
        OTHER("Other");
        private final String value;

        TYPE(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }

    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, accessMode = Schema.AccessMode.READ_ONLY)
    @SecureUpdate({"FORBIDDEN"})
    @Searchable
    private Long id;
    @Schema(example = "My sample resource", requiredMode = Schema.RequiredMode.REQUIRED)
    @Field(type = FieldType.Search_As_You_Type, name = "value")
    private String value;
    //vocab, e.g. Subtitle, AlternativeTitle
    @Schema(example = "SUBTITLE", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Enumerated(EnumType.STRING)
    @Field(type = FieldType.Keyword, name = "titleType")
    private TYPE titleType;
    @Schema(example = "en", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Field(type = FieldType.Keyword, name = "lang")
    private String lang;

    public static Title factoryTitle(String value) {
        Title t = new Title();
        t.value = value;
        t.titleType = TYPE.TRANSLATED_TITLE;
        return t;
    }

    public static Title factoryTitle(String value, TYPE type) {
        Title t = new Title();
        t.titleType = type;
        t.value = value;
        return t;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.id);
        hash = 89 * hash + Objects.hashCode(this.value);
        hash = 89 * hash + EnumUtils.hashCode(this.titleType);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Title other = (Title) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return EnumUtils.equals(this.titleType, other.titleType);
    }
}
