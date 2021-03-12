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
package edu.kit.datamanager.repo.configuration;

import edu.kit.datamanager.repo.dao.IDataResourceDao;
import edu.kit.datamanager.repo.domain.DataResource;
import edu.kit.datamanager.repo.service.IContentInformationService;
import edu.kit.datamanager.repo.service.IDataResourceService;
import edu.kit.datamanager.repo.service.IRepoStorageService;
import edu.kit.datamanager.repo.service.IRepoVersioningService;
import edu.kit.datamanager.repo.service.impl.ContentInformationAuditService;
import edu.kit.datamanager.repo.service.impl.NoneDataVersioningService;
import edu.kit.datamanager.service.IAuditService;
import java.net.URL;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Holds all properties needed to manage data resources.
 */
public class RepoBaseConfiguration {

  /**
   * Base path of repo. (External url is also allowed.)
   */
  private URL basepath;
  /**
   * Repo is read only or not.
   */
  private boolean readOnly;
   
  /**
   * Secret for creating tokens. 
   */
  private String jwtSecret;
  /** 
   * Is authentication enabled or not. 
   */
  private boolean authEnabled;
  /**
   * Versioning is supported or not.
   */
  private boolean auditEnabled;
  /**
   * Versioning service for data resource.
   */
  private IRepoVersioningService versioningService;
  /**
   * Versioning service for data resource.
   */
  private IRepoStorageService storageService;
  /**
   * Service for data resource.
   */
   private IDataResourceService dataResourceService;
   /** 
    * Service for content information.
    */
  private IContentInformationService contentInformationService;
  /**
   * Auditservice for data resource.
   */
  private IAuditService<DataResource> auditService;
 /**
   * Content information service
   */
  private ContentInformationAuditService contentInformationAuditService;
  /**
   * Access to data resource db.
   */
  private IDataResourceDao dao;
  /** 
   * EventPublisher.
   */
   private ApplicationEventPublisher eventPublisher;
 /**
   * If versioning is available or not.
   *
   * @return true or false.
   */
  public boolean isAuditEnabled() {
    return auditEnabled;
  }

  /**
   * @return the basePath
   */
  public URL getBasepath() {
    return basepath;
  }

  /**
   * @param basePath the basePath to set
   */
  public void setBasepath(URL basePath) {
    this.basepath = basePath;
  }

//  /**
//   * @return the hierarchy
//   */
//  public FileHierarchy getHierarchy() {
//    return hierarchy;
//  }
//
//  /**
//   * @param hierarchy the hierarchy to set
//   */
//  public void setHierarchy(FileHierarchy hierarchy) {
//    this.hierarchy = hierarchy;
//  }
  /**
   * @return the readOnly
   */
  public boolean isReadOnly() {
    return readOnly;
  }

  /**
   * @param readOnly the readOnly to set
   */
  public void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
  }
  /**
   * @return the auditService
   */
  public IAuditService<DataResource> getAuditService() {
    return auditService;
  }

  /**
   * @param auditService the auditService to set
   */
  public void setAuditService(IAuditService<DataResource> auditService) {
    this.auditService = auditService;
  }

  /**
   * @return the contentInformationService
   */
  public ContentInformationAuditService getContentInformationAuditService() {
    return contentInformationAuditService;
  }

  /**
   * @param contentInformationAuditService the contentInformationService to set
   */
  public void setContentInformationAuditService(ContentInformationAuditService contentInformationAuditService) {
    this.contentInformationAuditService = contentInformationAuditService;
  }

  /**
   * @return the versioningService
   */
  public IRepoVersioningService getVersioningService() {
    return versioningService;
  }

  /**
   * @param versioningService the versioningService to set
   */
  public void setVersioningService(IRepoVersioningService versioningService) {
    this.versioningService = versioningService;
    auditEnabled = !new NoneDataVersioningService().getServiceName().equals(versioningService.getServiceName());
 }

  /**
   * @return the storageService
   */
  public IRepoStorageService getStorageService() {
    return storageService;
  }

  /**
   * @param storageService the storageService to set
   */
  public void setStorageService(IRepoStorageService storageService) {
    this.storageService = storageService;
  }

  /**
   * @return the dataResourceService
   */
  public IDataResourceService getDataResourceService() {
    return dataResourceService;
  }

  /**
   * @param dataResourceService the dataResourceService to set
   */
  public void setDataResourceService(IDataResourceService dataResourceService) {
    this.dataResourceService = dataResourceService;
  }

  /**
   * @return the eventPublisher
   */
  public ApplicationEventPublisher getEventPublisher() {
    return eventPublisher;
  }

  /**
   * @param eventPublisher the eventPublisher to set
   */
  public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }

  /**
   * @return the contentInformationService
   */
  public IContentInformationService getContentInformationService() {
    return contentInformationService;
  }

  /**
   * @param contentInformationService the contentInformationService to set
   */
  public void setContentInformationService(IContentInformationService contentInformationService) {
    this.contentInformationService = contentInformationService;
  }

  /**
   * @return the jwtSecret
   */
  public String getJwtSecret() {
    return jwtSecret;
  }

  /**
   * @param jwtSecret the jwtSecret to set
   */
  public void setJwtSecret(String jwtSecret) {
    this.jwtSecret = jwtSecret;
  }

  /**
   * @return the authEnabled
   */
  public boolean isAuthEnabled() {
    return authEnabled;
  }

  /**
   * @param authEnabled the authEnabled to set
   */
  public void setAuthEnabled(boolean authEnabled) {
    this.authEnabled = authEnabled;
  }

}
