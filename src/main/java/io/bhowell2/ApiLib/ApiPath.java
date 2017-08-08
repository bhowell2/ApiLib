package io.bhowell2.ApiLib;

import io.bhowell2.ApiLib.exceptions.UnsupportedApiVersionForPath;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Represents a path that can be checked.
 * @author Blake Howell
 */
public class ApiPath {

  private final String path;
  private final Pattern pathMatchPattern;
  private final ApiPathParameters[] pathParameters;

  /**
   * If the user does not want/need a path name string, this should be used.
   * @param pathParameters parameters for each version
   */
  public ApiPath(ApiPathParameters... pathParameters) {
    this(null, null, pathParameters);
  }

  /**
   * If the user wants a path name string, it may be set here.
   * @param pathName
   */
  public ApiPath(String pathName, ApiPathParameters... pathParameters) {
    this(pathName, null, pathParameters);
  }

  public ApiPath(String pathName, Pattern pathMatchPattern, ApiPathParameters... pathParameters) {
    this.path = pathName;
    this.pathMatchPattern = pathMatchPattern;
    this.pathParameters = pathParameters;
    // arrange the path parameters in descending order (i.e., the newest version first. e.g., V3, V2, V1, V0)
    // v1.compareTo(v2) puts in ascending order (e.g., V0, V1, V2). so, reverse it
    Arrays.sort(this.pathParameters, (v1, v2) -> v2.getApiVersion().compareVersions(v1.getApiVersion()));
  }

  /**
   * @return an optional that contains the path name if it was set, otherwise an empty optional
   */
  public Optional<String> getPathName() {
    return Optional.ofNullable(path);
  }

  public boolean matchesPath(String path) {
    if (pathMatchPattern != null) {
      return this.pathMatchPattern.matcher(path).matches();
    }
    throw new RuntimeException("Api path does not have a registered path match pattern. Check constructors for ApiPath. Path name: " + path);
  }

  /**
   * Obtains the correct {@link ApiPathParameters} to check against the supplied request version. If the request version is GREATER THAN or EQUAL
   * TO any of the ApiVersions of the ApiPathParameters available, then it will check against the latest version of them. If the request version is
   * LESS THAN any of the ApiVersions of the ApiPathParameters available, then an error of
   * {@link io.bhowell2.ApiLib.exceptions.UnsupportedApiVersionForPath} will be returned.
   *
   * @param requestVersion    the API version of the request
   * @param requestParameters parameters of the request to be checked (will ignore anything that is not specified in the ApiPath to check)
   * @return the list of successfully checked parameters, or an error that was caught in the process of checking the parameters
   */
  public PathParamsTuple check(ApiVersion requestVersion, Map<String, Object> requestParameters) {
    // TODO: Add converters
    for (ApiPathParameters p : pathParameters) {
      if (requestVersion.compareVersions(p.getApiVersion()) >= 0) {
        return p.check(requestParameters);
      }
    }
    return PathParamsTuple.failed(new ErrorTuple(ErrorType.UNSUPORTED_API_VERSION, "API Version " + requestVersion.getVersionString() +
                                                 " is not valid for the path."));
  }

}
