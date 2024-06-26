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

import edu.kit.datamanager.entities.Identifier;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import edu.kit.datamanager.annotations.Searchable;
import edu.kit.datamanager.annotations.SecureUpdate;
import edu.kit.datamanager.entities.BaseEnum;
import edu.kit.datamanager.entities.EtagSupport;
import edu.kit.datamanager.repo.domain.acl.AclEntry;
import edu.kit.datamanager.util.EnumUtils;
import edu.kit.datamanager.util.json.CustomInstantDeserializer;
import edu.kit.datamanager.util.json.CustomInstantSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 *
 * @author jejkal
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Data resource element")
@Data
@Table(indexes = {
    @Index(name = "lastUpdate", columnList = "lastUpdate DESC")
})
public class DataResource implements EtagSupport, Serializable {

    @Autowired
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Transient
    @JsonIgnore
    private Logger LOGGER;

    public enum State implements BaseEnum {
        VOLATILE,
        FIXED,
        REVOKED,
        GONE;

        @Override
        public String getValue() {
            return toString();
        }
    }
    //The internal resource identifier assigned once during creation
    @Id
    @org.springframework.data.annotation.Id
    @Field(type = FieldType.Text)
    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, accessMode = Schema.AccessMode.READ_ONLY)
    @SecureUpdate({"FORBIDDEN"})
    @Searchable
    private String id = null;

    //mandatory
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @OneToOne(cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @Field(type = FieldType.Nested, includeInParent = true)
    private PrimaryIdentifier identifier;

    //vocab
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @OneToMany(cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "resource_id")
    @Field(type = FieldType.Nested, includeInParent = true)
    private Set<Agent> creators = new HashSet<>();

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @OneToMany(cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "resource_id")
    @Field(type = FieldType.Nested, includeInParent = true)
    private Set<Title> titles = new HashSet<>();

    @Schema(description = "Publisher, e.g. institution", example = "Karlsruhe Institute of Technology", requiredMode = Schema.RequiredMode.REQUIRED)
    @Searchable
    @Field(type = FieldType.Search_As_You_Type, name = "publisher")
    private String publisher;

    //format: YYYY
    @Schema(description = "Publication year (could be aquisition year, if publication year is not feasible)", example = "2017",requiredMode = Schema.RequiredMode.REQUIRED)
    @Searchable
    @Field(type = FieldType.Date, name = "publicationYear", format = DateFormat.year)
    private String publicationYear;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @OneToOne(cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "resource_id")
    @Field(type = FieldType.Nested, includeInParent = true)
    private ResourceType resourceType;

    //recommended
    @Schema(description = "One or more subjects describing the resource (recommended).", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @OneToMany(cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "resource_id")
    @Field(type = FieldType.Nested, includeInParent = true)
    private Set<Subject> subjects = new HashSet<>();

    @Schema(description = "One or more contributors that have contributed to the resource (recommended).", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @OneToMany(cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "resource_id")
    @Field(type = FieldType.Nested, includeInParent = true)
    private Set<Contributor> contributors = new HashSet<>();

    @Schema(description = "One or more dates related to the resource, e.g. creation or publication date (recommended).", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @OneToMany(cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "resource_id")
    @Field(type = FieldType.Nested, includeInParent = true)
    private Set<Date> dates = new HashSet<>();

    @Schema(description = "One or more related identifiers the can be used to identify related resources, e.g. metadata, parts or derived resources (recommended).", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @OneToMany(cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "resource_id")
    @Field(type = FieldType.Nested, includeInParent = true)
    private Set<RelatedIdentifier> relatedIdentifiers = new HashSet<>();

    @Schema(description = "One or more description entries providing additional information, e.g. abstract or technical information (recommended).", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @OneToMany(cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "resource_id")
    @Field(type = FieldType.Nested, includeInParent = true)
    private Set<Description> descriptions = new HashSet<>();

    @Schema(description = "One or more geolocation entries providing information about the location of the resource, e.g. storage or aquisition location (recommended).", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @OneToMany(cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "resource_id")
    @Field(type = FieldType.Nested, includeInParent = true)
    private Set<GeoLocation> geoLocations = new HashSet<>();

    //optional
    @Schema(description = "The primary language of the resource. Possible codes are IETF BCP 47 or ISO 639-1.", example = "en, de, fr", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Searchable
    @Field(type = FieldType.Keyword, name = "language")
    private String language;

    @Schema(description = "One or more alternate identifiers the can be used to identify the resources in addition to the primary identifier.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @OneToMany(cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @Field(type = FieldType.Nested, includeInParent = true)
    private Set<Identifier> alternateIdentifiers = new HashSet<>();

    @Schema(description = "Unstructured size information about the resource or its contents.", example = "15 files, 10 page, 100 bytes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @ElementCollection(fetch = FetchType.EAGER)
    @Field(type = FieldType.Text)
    private Set<String> sizes = new HashSet<>();

    @Schema(description = "Format information about the resource or its contents. Preferably, mime types or file extensions are used.", example = "text/plain, xml, application/pdf", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @ElementCollection(fetch = FetchType.EAGER)
    @Field(type = FieldType.Text)
    private Set<String> formats = new HashSet<>();

    //e.g. major.minor
    @Schema(description = "Version of the resource, e.g. major.minor.", example = "1.0", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Searchable
    @Field(type = FieldType.Keyword, name = "version")
    private String version;

    //e.g. CC-0
    @Schema(description = "Intellectual property information.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @OneToMany(cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "resource_id")
    @Field(type = FieldType.Nested, includeInParent = true)
    private Set<Scheme> rights = new HashSet<>();

    @Schema(description = "Funding information, e.g. funder, award number and title.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @OneToMany(cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "resource_id")
    @Field(type = FieldType.Nested, includeInParent = true)
    private Set<FundingReference> fundingReferences = new HashSet<>();

    //internal properties
    @Schema(description = "Date at which the last update occured.", example = "2017-05-10T10:41:00Z", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    @JsonDeserialize(using = CustomInstantDeserializer.class)
    @JsonSerialize(using = CustomInstantSerializer.class)
    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    Instant lastUpdate;
    //state of the resource (VOLATILE by default)
    @Schema(description = "State information of the resource. After creation each resource is classified as VOLATILE", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Enumerated(EnumType.STRING)
    @Searchable
    @Field(type = FieldType.Keyword, name = "state")
    private State state;
    //embargo date that should receive a value 'resourceCreationTime + DefaultEmbargoSpan' on resource creation time
    //embargo date should only be used for policy triggering, not for actual access decisions. This should be done on the basis of ACLs. 
    //As soon as the embargo ends, all access restrictions should be removed in order to allow public access.
    @Schema(description = "Date at which the embargo ends, e.g. after which the resource is published.", example = "2017-05-10T10:41:00Z", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    @JsonDeserialize(using = CustomInstantDeserializer.class)
    @JsonSerialize(using = CustomInstantSerializer.class)
    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    Instant embargoDate;

    @OneToMany(cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @SecureUpdate({"ROLE_ADMINISTRATOR", "PERMISSION_ADMINISTRATE"})
    @JoinColumn(name = "resource_id")
    @Field(type = FieldType.Nested, includeInParent = true)
    private Set<AclEntry> acls = new HashSet<>();

    public static DataResource factoryNewDataResource() {
        DataResource result = new DataResource();
        result.setIdentifier(PrimaryIdentifier.factoryPrimaryIdentifier());
        Identifier internal = Identifier.factoryInternalIdentifier();
        result.getAlternateIdentifiers().add(internal);
        result.id = internal.getValue();
        return result;
    }

    public static DataResource factoryDataResourceWithDoi(@NonNull String doi) {
        DataResource result = new DataResource();
        result.setIdentifier(PrimaryIdentifier.factoryPrimaryIdentifier(doi));
        Identifier internal = Identifier.factoryInternalIdentifier(doi);
        result.getAlternateIdentifiers().add(internal);
        result.id = internal.getValue();
        return result;
    }

    public static DataResource factoryNewDataResource(@NonNull String internalIdentifier) {
        DataResource result = new DataResource();
        result.setIdentifier(PrimaryIdentifier.factoryPrimaryIdentifier());
        result.getAlternateIdentifiers().add(Identifier.factoryInternalIdentifier(internalIdentifier));
        result.id = internalIdentifier;
        return result;
    }

    public void setEmbargoDate(Instant embargoDate) {
        if (embargoDate == null) {
            return;
        }
        this.embargoDate = Objects.requireNonNull(embargoDate).truncatedTo(ChronoUnit.SECONDS);
    }

    @Override
    @JsonIgnore
    public String getEtag() {
        return Integer.toString(hashCode());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.id);
        hash = 41 * hash + Objects.hashCode(this.identifier);
        hash = 41 * hash + Objects.hashCode(this.creators);
        hash = 41 * hash + Objects.hashCode(this.titles);
        hash = 41 * hash + Objects.hashCode(this.publisher);
        hash = 41 * hash + Objects.hashCode(this.publicationYear);
        hash = 41 * hash + Objects.hashCode(this.resourceType);
        hash = 41 * hash + Objects.hashCode(this.subjects);
        hash = 41 * hash + Objects.hashCode(this.contributors);
        hash = 41 * hash + Objects.hashCode(this.dates);
        hash = 41 * hash + Objects.hashCode(this.relatedIdentifiers);
        hash = 41 * hash + Objects.hashCode(this.descriptions);
        hash = 41 * hash + Objects.hashCode(this.geoLocations);
        hash = 41 * hash + Objects.hashCode(this.language);
        hash = 41 * hash + Objects.hashCode(this.alternateIdentifiers);
        hash = 41 * hash + Objects.hashCode(this.sizes);
        hash = 41 * hash + Objects.hashCode(this.formats);
        hash = 41 * hash + Objects.hashCode(this.version);
        hash = 41 * hash + Objects.hashCode(this.rights);
        hash = 41 * hash + Objects.hashCode(this.fundingReferences);
        hash = 41 * hash + EnumUtils.hashCode(this.state);
        hash = 41 * hash + Objects.hashCode(this.embargoDate);
        hash = 41 * hash + Objects.hashCode(this.lastUpdate);
        hash = 41 * hash + Objects.hashCode(this.acls);
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
        final DataResource other = (DataResource) obj;

        if (!Objects.equals(this.publisher, other.publisher)) {
            return false;
        }
        if (!Objects.equals(this.publicationYear, other.publicationYear)) {
            return false;
        }
        if (!Objects.equals(this.language, other.language)) {
            return false;
        }
        if (!Objects.equals(this.version, other.version)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.identifier, other.identifier)) {
            return false;
        }
        if (!Objects.equals(this.creators, other.creators)) {
            return false;
        }
        if (!Objects.equals(this.titles, other.titles)) {
            return false;
        }
        if (!Objects.equals(this.resourceType, other.resourceType)) {
            return false;
        }
        if (!Objects.equals(this.subjects, other.subjects)) {
            return false;
        }
        if (!Objects.equals(this.contributors, other.contributors)) {
            return false;
        }
        if (!Objects.equals(this.dates, other.dates)) {
            return false;
        }
        if (!Objects.equals(this.relatedIdentifiers, other.relatedIdentifiers)) {
            return false;
        }
        if (!Objects.equals(this.descriptions, other.descriptions)) {
            return false;
        }
        if (!Objects.equals(this.geoLocations, other.geoLocations)) {
            return false;
        }
        if (!Objects.equals(this.alternateIdentifiers, other.alternateIdentifiers)) {
            return false;
        }
        if (!Objects.equals(this.sizes, other.sizes)) {
            return false;
        }
        if (!Objects.equals(this.formats, other.formats)) {
            return false;
        }
        if (!Objects.equals(this.rights, other.rights)) {
            return false;
        }
        if (!Objects.equals(this.fundingReferences, other.fundingReferences)) {
            return false;
        }
        if (!EnumUtils.equals(this.state, other.state)) {
            return false;
        }
        if (!Objects.equals(this.lastUpdate, other.lastUpdate)) {
            return false;
        }
        if (!Objects.equals(this.embargoDate, other.embargoDate)) {
            return false;
        }
        boolean result = Objects.equals(this.acls, other.acls);
        return result;
    }
}
