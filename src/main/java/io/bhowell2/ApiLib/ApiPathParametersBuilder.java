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
     * Static creation method. May be used instead of {@code new ApiPathParametersBuilder()}. Sets {@code continueOnFailedOptionalChecks} to false.
     * @param version the version for which these parameters are valid
     * @return a fluent builder for the API path's paramters
     */
    public static ApiPathParametersBuilder builder(ApiVersion version) {
        return new ApiPathParametersBuilder(version, false);
    }

    /**
     *
     * @param version the version for which these parameters are valid
     * @param continueOnFailedOptionalChecks whether or not to continue checking when an optional parameter fails its checks
     * @return a fluent builder for the API path's paramters
     */
    public static ApiPathParametersBuilder builder(ApiVersion version, boolean continueOnFailedOptionalChecks) {
        return new ApiPathParametersBuilder(version, continueOnFailedOptionalChecks);
    }

    private ApiVersion apiVersion;
    private boolean continueOnFailedOptionalChecks;
    private List<ApiParameter> requiredParameters;
    private List<ApiConditionalParameters> requiredConditionalParameters;
    private List<ApiParameter> optionalParameters;
    private List<ApiConditionalParameters> optionalConditionalParameters;
    private List<ApiCustomParameters> requiredCustomParameters;
    private List<ApiCustomParameters> optionalCustomParameters;

    public ApiPathParametersBuilder(ApiVersion version, boolean continueOnFailedOptionalChecks) {
        this.apiVersion = version;
        this.requiredParameters = new ArrayList<>();
        this.requiredConditionalParameters = new ArrayList<>();
        this.optionalParameters = new ArrayList<>();
        this.optionalConditionalParameters = new ArrayList<>();
        this.requiredCustomParameters = new ArrayList<>();
        this.optionalCustomParameters = new ArrayList<>();
    }

    /**
     * Should be called at the end of the building process to get the Path's Parameters that were just added.
     * @return an object holding the required and optional parameters for a path for a specified version
     */
    public ApiPathParameters build() {
        return new ApiPathParameters(this.apiVersion,
                                     this.continueOnFailedOptionalChecks,
                                     this.requiredParameters.toArray(new ApiParameter[requiredParameters.size()]),
                                     this.optionalParameters.toArray(new ApiParameter[optionalParameters.size()]),
                                     this.requiredConditionalParameters.toArray(new ApiConditionalParameters[requiredConditionalParameters.size()]),
                                     this.optionalConditionalParameters.toArray(new ApiConditionalParameters[optionalConditionalParameters.size()]),
                                     this.requiredCustomParameters.toArray(new ApiCustomParameters[requiredCustomParameters.size()]),
                                     this.optionalCustomParameters.toArray(new ApiCustomParameters[optionalCustomParameters.size()]));
    }

    public ApiPathParametersBuilder addRequiredParameter(ApiParameter<?> parameter) {
        ensureUniqueParameterName(parameter.getParameterName());
        this.requiredParameters.add(parameter);
        return this;
    }

    public ApiPathParametersBuilder addConditionalRequiredParameters(ApiConditionalParameters parameters) {
        this.requiredConditionalParameters.add(parameters);
        return this;
    }

    public ApiPathParametersBuilder addOptionalParameter(ApiParameter<?> parameter) {
        ensureUniqueParameterName(parameter.getParameterName());
        this.optionalParameters.add(parameter);
        return this;
    }

    public ApiPathParametersBuilder addConditionalOptionalParameters(ApiConditionalParameters parameters) {
        this.optionalConditionalParameters.add(parameters);
        return this;
    }

    public ApiPathParametersBuilder addRequiredCustomParameters(ApiCustomParameters customParameters) {
        this.requiredCustomParameters.add(customParameters);
        return this;
    }

    public ApiPathParametersBuilder addOptionalCustomParameters(ApiCustomParameters customParameters) {
        this.optionalCustomParameters.add(customParameters);
        return this;
    }

    // use to check for duplicates when added to required and optional parameters
    private Set<String> parameterNameSet = new HashSet<>();

    /**
     * Checks that the name added is unique.
     *
     * @param newNameAdded
     */
    private void ensureUniqueParameterName(String newNameAdded) {
        if (!this.parameterNameSet.add(newNameAdded)) {
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
