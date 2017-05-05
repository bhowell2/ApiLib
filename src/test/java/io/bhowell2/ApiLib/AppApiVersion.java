package io.bhowell2.ApiLib;

import io.bhowell2.ApiLib.exceptions.ApiVersionFormatException;

/**
 * @author Blake Howell
 */
public enum AppApiVersion implements ApiVersion {

  V0("V0"),
  V1("V1"),
  V2("V2");

  private String version;

  // to avoid calling .values() every time a value is requested
  // THIS ARRAY CANNOT BE RETURNED AS IT IS MUTABLE. ONLY ITS VALUES (APIVERSION) MAY BE RETURNED, AS THEY ARE CONSTANTS/SINGLETONS
  private static AppApiVersion[] versions = AppApiVersion.values();

  // ensure that all versions are in order (precautionary check)
  // each implementation must write a different version of this to compare (assuming versions are not of the format V0, V1, ... V10, V11, ... VN)
  static {
    if (versions.length == 0)
      throw new RuntimeException("No API Versions specified.");
    AppApiVersion tmp = versions[0];
    for (int i = 1; i < versions.length; i++) {
      Integer tmpVersion = Integer.parseInt(tmp.version.substring(1));
      Integer nextVersion = Integer.parseInt(versions[i].version.substring(1));
      if (tmpVersion > nextVersion || tmpVersion.equals(nextVersion))
        throw new RuntimeException("API versions are not in ascending order. " + versions[i] + " (enum ordinal = " +versions[i].ordinal() + ")" +
                                       " is smaller than " + tmp + " (enum ordinal = " + tmp.ordinal() + ").");
      tmp = versions[i]; // test has passed, check the next one
    }
  }

  AppApiVersion(String versionAsString) {
    version = versionAsString;
  }

  @Override
  public String getVersionString() {
    return version;
  }

  /// WARNING: IT IS ONLY OKAY TO USE THE ENUM'S COMPARE TO IF THE VALUES ARE CREATED IN ASCENDING ORDER. I.E., THE SMALLEST VERSION (OLDEST) TO
  /// THE LARGEST VERSION (NEWEST)
  @Override
  public int compareVersions(ApiVersion version) {
    return this.compareTo((AppApiVersion)version);
  }

  public static ApiVersion getPreviousVersion(String apiVersion) throws ArrayIndexOutOfBoundsException, ApiVersionFormatException {
    // get the version from the string, get the version before, by subtracting 1 and mod by the length
    return versions[fromString(apiVersion).ordinal() - 1];
  }

  public static ApiVersion getNextVersion(String apiVersion) throws ArrayIndexOutOfBoundsException, ApiVersionFormatException {
    return versions[fromString(apiVersion).ordinal() + 1];
  }

  public static ApiVersion getLatestVersion() {return versions[versions.length - 1];}

  public static String getLatestVersionAsString() {
    return versions[versions.length - 1].version;
  }

  /**
   * This does the same thing as {@link Enum#valueOf(Class, String)}, but returns a more specific error.
   * @param version
   * @return
   * @throws ApiVersionFormatException
   */
  public static AppApiVersion fromString(String version) throws ApiVersionFormatException {
    if (version != null) {
      for (AppApiVersion v : AppApiVersion.versions) {
        if (version.equals(v.version))
          return v;
      }
      throw new ApiVersionFormatException("The version requested (" + version + ") does not match any version.");
    }
    throw new IllegalArgumentException("Version cannot be null");
  }


}
