package io.bhowell2.ApiLib;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Represents a path that can be checked.
 * @author Blake Howell
 */
public class ApiPath<RequestParameters> {

    private final String pathName;
    private final Pattern pathMatchPattern;
    ApiPathRequestParameters<RequestParameters>[] pathRequestParameters;

    /**
     * If the user does not want/need a path name string, this should be used.
     * @param pathRequestParameters parameters for each version
     */
    public ApiPath(ApiPathRequestParameters<RequestParameters>[] pathRequestParameters) {
        this(null, null, pathRequestParameters);
    }

    /**
     * If the user wants a path name string, it may be set here.
     * @param pathName
     */
    public ApiPath(String pathName, ApiPathRequestParameters<RequestParameters>[] pathRequestParameters) {
        this(pathName, null, pathRequestParameters);
    }

    public ApiPath(String pathName, Pattern pathMatchPattern, ApiPathRequestParameters<RequestParameters>[] pathRequestParameters) {
        this.pathName = pathName;
        this.pathMatchPattern = pathMatchPattern;
        this.pathRequestParameters = pathRequestParameters;
        // arrange the path parameters in descending order (i.e., the newest version first. e.g., V3, V2, V1, V0)
        // v1.compareTo(v2) puts in ascending order (e.g., V0, V1, V2). so, reverse it
        Arrays.sort(this.pathRequestParameters, (v1, v2) -> v2.apiVersion.compareVersions(v1.apiVersion));
    }

    /**
     * @return an optional that contains the path name if it was set, otherwise an empty optional
     */
    public Optional<String> getPathName() {
        return Optional.ofNullable(pathName);
    }

    public boolean matchesPath(String path) {
        if (pathMatchPattern != null) {
            return this.pathMatchPattern.matcher(path).matches();
        }
        throw new RuntimeException("Api path does not have a registered path match pattern. Check your constructor for the Path: " + path);
    }

    /**
     * Obtains the correct {@link ApiPathRequestParameters} to check against the supplied request version. If the request version is GREATER THAN or EQUAL
     * TO any of the ApiVersions of the ApiPathParameters available, then it will check against the latest version of them. If the request version is
     * LESS THAN any of the ApiVersions of the ApiPathParameters available, then {@link ErrorType#UNSUPPORTED_API_VERSION} is returned.
     * {@link io.bhowell2.ApiLib.exceptions.UnsupportedApiVersionForPath} will be returned.
     *
     * @param requestVersion    the API version of the request
     * @param requestParameters parameters of the request to be checked (will ignore anything that is not specified in the ApiPath to check)
     * @return the list of successfully checked parameters, or an error that was caught in the process of checking the parameters
     */
    public ApiObjectParamTuple check(ApiVersion requestVersion, RequestParameters requestParameters) {
        // TODO: Add converters
        for (ApiPathRequestParameters<RequestParameters> p : pathRequestParameters) {
            if (requestVersion.compareVersions(p.apiVersion) >= 0) {
                return p.check(requestParameters);
            }
        }
        return ApiObjectParamTuple.failed(new ErrorTuple(ErrorType.UNSUPPORTED_API_VERSION, "API Version " + requestVersion.getVersionString() +
            " is not valid for the path."));
    }

}
