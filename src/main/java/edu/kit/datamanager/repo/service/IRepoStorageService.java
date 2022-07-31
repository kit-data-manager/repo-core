package edu.kit.datamanager.repo.service;

import edu.kit.datamanager.repo.configuration.StorageServiceProperties;
import edu.kit.datamanager.repo.domain.DataResource;

public interface IRepoStorageService{
  /** 
   * Configure the service.
   * @param applicationProperties Properties of repo.
   */
  void configure(StorageServiceProperties applicationProperties);

  /**
   * Create path from given resource
   * finalize, token
   * @param resource Data resource.
   * 
   * @return Path of given resource.
   */
  String createPath(DataResource resource);

  /**
   * Returns the name of this versioning service. The name should be unique.
   * Otherwise, a random implementation with the provided name will be used.
   *
   * @return The service name.
   */
  String getServiceName();
}
