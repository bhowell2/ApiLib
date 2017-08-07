package io.bhowell2.ApiLib;

/**
 * The user should implement ONE of these for each API version, OR, better yet, implement this in an Enum.
 *
 * If an Enum is not used, the user should create a class to hold all of the versions (e.g., ApiVersions) that has an array of some sort and a class
 * for each version (e.g. ApiVersionV0).
 *
 * @author Blake Howell
 */
public interface ApiVersion {

  /**
   * @return the string value of the version
   */
  String getVersionString();

  /**
   * This should work just like {@link Comparable}, but the comparable interface is not used, because the user may want to use an Enum rather than
   * a class. (Enums are preferable.)
   *
   * @return greater than 0 if THIS is newer/greater than the PARAMETER, 0 if THIS and the PARAMETER are the SAME version, less than 0 if THIS is
   * older/smaller than the PARAMETER.
   */
  int compareVersions(ApiVersion version);

}
