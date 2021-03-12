package edu.kit.datamanager.repo.service;

import edu.kit.datamanager.repo.configuration.RepoBaseConfiguration;
import edu.kit.datamanager.repo.domain.DataResource;

public interface IRepoStorageService{
  /** 
   * Configure the service.
   */
  void configure(RepoBaseConfiguration applicationProperties);

  /**
   * Create path from given resource
   * finalize, token
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
