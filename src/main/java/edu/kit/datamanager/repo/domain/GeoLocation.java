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

import edu.kit.datamanager.annotations.Searchable;
import edu.kit.datamanager.annotations.SecureUpdate;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Geo location information for a resource.")
@Data
public class GeoLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, accessMode = Schema.AccessMode.READ_ONLY)
    @SecureUpdate({"FORBIDDEN"})
    @Searchable
    private Long id;
    @OneToOne(cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @Field(type = FieldType.Nested, includeInParent = true)
    private Point point;
    @OneToOne(cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @Field(type = FieldType.Nested, includeInParent = true)
    private Box box;
    @OneToOne(cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @Field(type = FieldType.Nested, includeInParent = true)
    private Polygon polygon;
    @Field(type = FieldType.Text, name = "place")
    private String place;

    /**
     * Basic factory method.
     *
     * @param point A point location
     *
     * @return A new instance of GeoLocation
     */
    public static GeoLocation factoryGeoLocation(Point point) {
        GeoLocation result = new GeoLocation();
        result.setPoint(point);
        return result;
    }

    /**
     * Basic factory method.
     *
     * @param box A box location
     *
     * @return A new instance of GeoLocation
     */
    public static GeoLocation factoryGeoLocation(Box box) {
        GeoLocation result = new GeoLocation();
        result.setBox(box);
        return result;
    }

    /**
     * Basic factory method.
     *
     * @param polygon A polygon location
     *
     * @return A new instance of GeoLocation
     */
    public static GeoLocation factoryGeoLocation(Polygon polygon) {
        GeoLocation result = new GeoLocation();
        result.setPolygon(polygon);
        return result;
    }

    /**
     * Basic factory method.
     *
     * @param place A place location
     *
     * @return A new instance of GeoLocation
     */
    public static GeoLocation factoryGeoLocation(String place) {
        GeoLocation result = new GeoLocation();
        result.setPlace(place);
        return result;
    }
}
