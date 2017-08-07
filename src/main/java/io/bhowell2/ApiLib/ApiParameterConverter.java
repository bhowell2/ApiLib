package io.bhowell2.ApiLib;

/**
 *
 * @author Blake Howell
 */
public class ApiParameterConverter {

  private final String parameterName;
  private final ApiVersion fromVersion, toVersion;

  public ApiParameterConverter(String parameterName, ApiVersion fromVersion, ApiVersion toVersion) {
    this.parameterName = parameterName;
    this.fromVersion = fromVersion;
    this.toVersion = toVersion;
  }

}
