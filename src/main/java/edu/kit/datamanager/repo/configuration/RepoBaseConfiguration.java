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
 * Holds all properties needed to manage repositories and its data resources.
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
   * Limits the number of referenced entity shadows. 
   * @see https://github.com/javers/javers/blob/master/javers-core/src/main/java/org/javers/repository/jql/QueryBuilder.java
   */
  private int maxJaversScope = 20;
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
   * Returns status of versioning.
   *
   * @return true or false.
   */
  public boolean isAuditEnabled() {
    return auditEnabled;
  }

  /**
   * Returns the local base path of the repository.
   * @return the basePath
   */
  public URL getBasepath() {
    return basepath;
  }

  /**
   * Set the base base path of the repository.
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
   * Returns status of the repository. writeable <-> readonly
   * @return the readOnly
   */
  public boolean isReadOnly() {
    return readOnly;
  }

  /**
   * Set the readonly status of repository.
   * @param readOnly the readOnly to set
   */
  public void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
  }
  /**
   * Returns the service used for auditing. 
   * @return the auditService
   */
  public IAuditService<DataResource> getAuditService() {
    return auditService;
  }

  /**
   * Set audit service.
   * @param auditService the auditService to set
   */
  public void setAuditService(IAuditService<DataResource> auditService) {
    this.auditService = auditService;
  }

  /**
   * Returns the content information service. 
   * @return the contentInformationService
   */
  public ContentInformationAuditService getContentInformationAuditService() {
    return contentInformationAuditService;
  }

  /**
   * Set the content information service.
   * @param contentInformationAuditService the contentInformationService to set
   */
  public void setContentInformationAuditService(ContentInformationAuditService contentInformationAuditService) {
    this.contentInformationAuditService = contentInformationAuditService;
  }

  /**
   * Returns the versioning service.
   * @return the versioningService
   */
  public IRepoVersioningService getVersioningService() {
    return versioningService;
  }

  /**
   * Set the versioning service.
   * @param versioningService the versioningService to set
   */
  public void setVersioningService(IRepoVersioningService versioningService) {
    this.versioningService = versioningService;
    auditEnabled = !new NoneDataVersioningService().getServiceName().equals(versioningService.getServiceName());
 }

  /**
   * Returns the storage service.
   * @return the storageService
   */
  public IRepoStorageService getStorageService() {
    return storageService;
  }

  /**
   * Set the storage service. 
   * @param storageService the storageService to set
   */
  public void setStorageService(IRepoStorageService storageService) {
    this.storageService = storageService;
  }

  /**
   * Returns the dataresource service.
   * @return the dataResourceService
   */
  public IDataResourceService getDataResourceService() {
    return dataResourceService;
  }

  /**
   * Set the dataresource service.
   * @param dataResourceService the dataResourceService to set
   */
  public void setDataResourceService(IDataResourceService dataResourceService) {
    this.dataResourceService = dataResourceService;
  }

  /**
   * Returns the event publisher.
   * @return the eventPublisher
   */
  public ApplicationEventPublisher getEventPublisher() {
    return eventPublisher;
  }

  /**
   * Set the event publisher.
   * @param eventPublisher the eventPublisher to set
   */
  public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }

  /**
   * Returns the contentinformation service.
   * @return the contentInformationService
   */
  public IContentInformationService getContentInformationService() {
    return contentInformationService;
  }

  /**
   * Set the content information service.
   * @param contentInformationService the contentInformationService to set
   */
  public void setContentInformationService(IContentInformationService contentInformationService) {
    this.contentInformationService = contentInformationService;
  }

  /**
   * Returns the JWT secret.
   * @return the jwtSecret
   */
  public String getJwtSecret() {
    return jwtSecret;
  }

  /**
   * Set the JWT secret.
   * @param jwtSecret the jwtSecret to set
   */
  public void setJwtSecret(String jwtSecret) {
    this.jwtSecret = jwtSecret;
  }

  /**
   * Returns the status of authorization.
   * @return the authEnabled
   */
  public boolean isAuthEnabled() {
    return authEnabled;
  }

  /**
   * Set the authorization status.
   * @param authEnabled the authEnabled to set
   */
  public void setAuthEnabled(boolean authEnabled) {
    this.authEnabled = authEnabled;
  }

  /**
   * Returns the maximum scope of deep shaddow queries.
   * @return the maxJaversScope
   */
  public int getMaxJaversScope() {
    return maxJaversScope;
  }

  /**
   * Set the maximum scope of deep shaddow queries.
   * @param maxJaversScope the maxJaversScope to set
   */
  public void setMaxJaversScope(int maxJaversScope) {
    this.maxJaversScope = maxJaversScope;
  }

}
