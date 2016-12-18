package io.bhowell2.ApiLib;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Facilitates the creation of {@link ApiPathParameters}.
 *
 * @author Blake Howell
 */
public class ApiPathParametersBuilder {

  /**
   * Static creation method. May be used instead of new ApiPathParametersBuilder()
   * @param version the version for which these parameters are valid
   * @return
   */
  public static ApiPathParametersBuilder builder(ApiVersion version) {
    return new ApiPathParametersBuilder(version);
  }

  ApiVersion apiVersion;
  List<ApiParameter> requiredParameters;
  List<ConditionalParameters> conditionalRequiredParameters;
  List<ApiParameter> optionalParameters;
  List<ConditionalParameters> conditionalOptionalParameters;

  public ApiPathParametersBuilder(ApiVersion version) {
    this.apiVersion = version;
    this.requiredParameters = new ArrayList<>();
    this.conditionalRequiredParameters = new ArrayList<>();
    this.optionalParameters = new ArrayList<>();
    this.conditionalOptionalParameters = new ArrayList<>();
  }

  public ApiPathParameters build() {
    ApiParameter<?>[] reqParams = new ApiParameter[requiredParameters.size()];
    ConditionalParameters[] conditReqParams = new ConditionalParameters[conditionalRequiredParameters.size()];
    ApiParameter<?>[] optParams = new ApiParameter[optionalParameters.size()];
    ConditionalParameters[] conditOptParams = new ConditionalParameters[conditionalOptionalParameters.size()];
    // TODO: conditional params need to be added
    return new ApiPathParameters(this.apiVersion, this.requiredParameters.toArray(reqParams), this.optionalParameters.toArray(optParams));
  }

  public ApiPathParametersBuilder addRequiredParameter(ApiParameter<?> parameter) {
    ensureUniqueParameterName(parameter.getParameterName());
    this.requiredParameters.add(parameter);
    return this;
  }

  public ApiPathParametersBuilder addConditionalRequiredParameters(ConditionalParameters parameters) {
    this.conditionalRequiredParameters.add(parameters);
    return this;
  }

  public ApiPathParametersBuilder addOptionalParameter(ApiParameter<?> parameter) {
    ensureUniqueParameterName(parameter.getParameterName());
    this.optionalParameters.add(parameter);
    return this;
  }

  public ApiPathParametersBuilder addConditionalOptionalParameters(ConditionalParameters parameters) {
    this.conditionalOptionalParameters.add(parameters);
    return this;
  }

  // use to check for duplicates when added
  private Set<String> parameterNameSet = new HashSet<>();

  /**
   * Checks that the name added is unique.
   *
   * @param newNameAdded
   */
  private void ensureUniqueParameterName(String newNameAdded) {
    if (!parameterNameSet.add(newNameAdded)) {
      throw new DuplicateParameterAddedToPathParametersException(newNameAdded);
    }
  }

  private class DuplicateParameterAddedToPathParametersException extends IllegalArgumentException {
    public DuplicateParameterAddedToPathParametersException(String paramName) {
      super("The parameter '" + paramName + "' has been added twice. This is disallowed, because one implementation may override the other and may " +
                "be undesired behavior.");
    }
  }

}
