package edu.kit.datamanager.repo.service;

import edu.kit.datamanager.entities.VersionInfo;
import edu.kit.datamanager.repo.configuration.RepoBaseConfiguration;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 *  Interface for versioning files.
 */
public interface IRepoVersioningService{
  /** 
   * Configure the service.
   * 
   * @param applicationProperties Properties of repo.
   */
  void configure(RepoBaseConfiguration applicationProperties);

  /**
   * adds the file of an object to the default OCFL repository.
   *
   * @param resourceId identifier of the object
   * @param callerId name of the user
   * @param path path of the file
   * @param data the file
   * @param options contains three keys: finalize, token number and parent.
   */
  void write(String resourceId, String callerId, String path, InputStream data, Map<String, String> options);

  /**
   * Returns files of an object's version.
   *
   * @param resourceId identifier of the object
   * @param callerId name of the user
   * @param path path of the file
   * @param versionId Id of the version.
   * @param destination Outputstream.
   * @param options contains three keys: finalize, token number and parent.
   */
  void read(String resourceId, String callerId, String path, String versionId, OutputStream destination, Map<String, String> options);

  /**
   * Returns information for a specific resource
   *
   * @param resourceId identifier of the object
   * @param path path of the file
   * @param versionId Id of the version.
   * @param options contains three keys: finalize, token number and parent.
   * 
   * @return Instance holding versioning information.
   */
  VersionInfo info(String resourceId, String path, String versionId, Map<String, String> options);

  /**
   * Returns the name of this versioning service. The name should be unique.
   * Otherwise, a random implementation with the provided name will be used.
   *
   * @return The service name.
   */
  String getServiceName();
}
