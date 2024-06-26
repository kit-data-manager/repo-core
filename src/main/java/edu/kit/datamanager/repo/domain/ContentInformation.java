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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import edu.kit.datamanager.annotations.Searchable;
import edu.kit.datamanager.annotations.SecureUpdate;
import edu.kit.datamanager.entities.EtagSupport;
import edu.kit.datamanager.repo.util.PathUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;

/**
 *
 * @author jejkal
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@EqualsAndHashCode(callSuper = false)
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"parent_resource_id", "relativePath"})})
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Content information element referring to a single file or remote reference in the repository.")
public class ContentInformation implements EtagSupport, Serializable {

    public static final MediaType CONTENT_INFORMATION_MEDIA_TYPE = MediaType.parseMediaType("application/vnd.datamanager.content-information+json");

    @Id
    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, accessMode = Schema.AccessMode.READ_ONLY)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SecureUpdate({"FORBIDDEN"})
    @Searchable
    private Long id;
    @ManyToOne
    @SecureUpdate({"FORBIDDEN"})
    @Schema(description = "The dataResource this element is associated with.")
    private DataResource parentResource;
    @SecureUpdate({"ROLE_ADMINISTRATOR"})//only allow modification by 'real' administrator, not for owner (having ADMINISTRATE permissions)
    @Schema(description = "The relative path of this element under which the file content is accessible. The path is relative the the resource's 'data' url, e.g. http://hostname:port/api/v1/dataresources/resourceId/data/relativePath")
    private String relativePath;
    @SecureUpdate({"FORBIDDEN"})
    @Schema(description = "The version of the metadata of this element. The metadata version may differ from the fileVersion.")
    private Integer version;
    @SecureUpdate({"FORBIDDEN"})
    @Schema(description = "The version of the file, e.g. the bitstream, associated with this element. The file version may differ from the version, which related to the metadata.")
    private String fileVersion;
    @SecureUpdate({"FORBIDDEN"})
    @Schema(description = "Unqiue identifier of the versioning service under which this element is versioned (if configured).")
    private String versioningService;
    @SecureUpdate({"FORBIDDEN"})
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private int depth;
    @SecureUpdate({"ROLE_ADMINISTRATOR"})//only allow modification by 'real' administrator, not for owner (having ADMINISTRATE permissions)
    @Schema(description = "The URI where the content is located, e.g. pointing to the remote resource or a local file.")
    private String contentUri;
    @SecureUpdate({"ROLE_ADMINISTRATOR"})//only allow modification by 'real' administrator, not for owner (having ADMINISTRATE permissions)
    @Schema(description = "The uploader of this element.")
    private String uploader;
    @SecureUpdate({"ROLE_ADMINISTRATOR", "PERMISSION_ADMINISTRATE"})
    @Schema(description = "The mediaType, either provided by the uploader or determined by the repository.")
    private String mediaType;
    @SecureUpdate({"ROLE_ADMINISTRATOR", "PERMISSION_ADMINISTRATE"})
    @Schema(description = "The hash of the associated content, either provided by the user (for remote resources) or computed during upload by the repository.")
    private String hash;
    @SecureUpdate({"ROLE_ADMINISTRATOR", "PERMISSION_ADMINISTRATE"})
    @Schema(description = "The size of the associated bit stream in bytes.")
    private long size;
    @SecureUpdate({"ROLE_ADMINISTRATOR", "PERMISSION_WRITE"})
    @ElementCollection
    @Schema(description = "A key-value map containing additional metadata associated with this element.")
    private Map<String, String> metadata = new HashMap<>();
    @SecureUpdate({"ROLE_ADMINISTRATOR", "PERMISSION_WRITE"})
    @ElementCollection
    @Schema(description = "A list of tags (strings) associated with this element. If a resource has a tag assigned, the tag can be used to access this (and all resources with this tag) directly.")
    private Set<String> tags = new HashSet<>();

    public static ContentInformation createContentInformation(@NonNull String parentId, @NonNull String relativePath, String... tags) {
        ContentInformation result = new ContentInformation();
        result.setRelativePath(relativePath);

        if (tags != null && (tags.length >= 1 && tags[0] != null)) {
            result.getTags().addAll(Arrays.asList(tags));
        }

        DataResource res = new DataResource();
        res.setId(parentId);
        result.setParentResource(res);
        return result;
    }

    public static ContentInformation createContentInformation(String relativePath) {
        ContentInformation info = new ContentInformation();
        info.setRelativePath(relativePath);
        return info;
    }

    public String getFilename() {
////    if(relativePath == null){
////      return "unknown.bin";
////    }
        if (relativePath == null) {
            return null;
        }
        return relativePath.substring(relativePath.lastIndexOf("/") + 1);
    }

    @JsonIgnore
    public void setMediaTypeAsObject(MediaType mediaType) {
        if (mediaType == null) {
            throw new IllegalArgumentException("Argument must not be null.");
        }
        this.mediaType = mediaType.toString();
    }

    @JsonIgnore
    public MediaType getMediaTypeAsObject() {
        try {
            return MediaType.parseMediaType(mediaType);
        } catch (InvalidMediaTypeException ex) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    public void setRelativePath(String path) {
        if (path == null) {
            //    throw new IllegalArgumentException("Argument must not be null.");
            relativePath = null;
            depth = 0;
            return;
        }

        //remove multiple slashes
        relativePath = PathUtils.normalizePath(path);
        depth = PathUtils.getDepth(relativePath);
    }

    @Override
    @JsonIgnore
    public String getEtag() {
        return Integer.toString(hashCode());
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
        final ContentInformation other = (ContentInformation) obj;

        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.parentResource.getId(), other.parentResource.getId())) {
            return false;
        }
        if (!Objects.equals(this.contentUri, other.contentUri)) {
            return false;
        }
        if (!Objects.equals(this.fileVersion, other.fileVersion)) {
            return false;
        }
        if (!Objects.equals(this.hash, other.hash)) {
            return false;
        }
        if (!Objects.equals(this.mediaType, other.mediaType)) {
            return false;
        }
        if (!Objects.equals(this.metadata, other.metadata)) {
            return false;
        }
        if (!Objects.equals(this.tags, other.tags)) {
            return false;
        }
        if (!Objects.equals(this.relativePath, other.relativePath)) {
            return false;
        }
        if (this.size != other.size) {
            return false;
        }
        if (!Objects.equals(this.uploader, other.uploader)) {
            return false;
        }
        if (!Objects.equals(this.version, other.version)) {
            return false;
        }

        return Objects.equals(this.versioningService, other.versioningService);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.id);
        hash = 41 * hash + Objects.hashCode(this.parentResource.getId());
        hash = 41 * hash + Objects.hashCode(this.contentUri);
        hash = 41 * hash + Objects.hashCode(this.fileVersion);
        hash = 41 * hash + Objects.hashCode(this.hash);
        hash = 41 * hash + Objects.hashCode(this.mediaType);
        hash = 41 * hash + Objects.hashCode(this.metadata);
        hash = 41 * hash + Objects.hashCode(this.tags);
        hash = 41 * hash + Objects.hashCode(this.relativePath);
        hash = 41 * hash + Long.hashCode(this.size);
        hash = 41 * hash + Objects.hashCode(this.uploader);
        hash = 41 * hash + Objects.hashCode(this.version);
        hash = 41 * hash + Objects.hashCode(this.versioningService);
        return hash;
    }
}
