/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.kit.datamanager.repo.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 *
 * @author jejkal
 */
@Component
@Data
@Validated
public class StorageServiceProperties {

    /**
     *Id-based storage properties
     */
    @Value("${repo.plugin.storage.id.charPerDirectory:4}")
    private int charPerDirectory;

    @Value("${repo.plugin.storage.id.maxDepth:7}")
    private int maxDepth;

    /**
     *Date-based storage properties
     */
    @Value("${repo.plugin.storage.date.pathPattern:'@{year}/@{month}'}")
    private String pathPattern;
}
