# API Parameter Library
A library to facilitate the creation and checking of parameters for an API.  This library takes some setup to use in your application, but once this has been taken care of your API can be very quickly built.  An extension to the library has been created for `Map<String, Object> requestParameters` -- this further reduces some boilerplate for the user and/or can be used as an example for the user to create a framework specific extension.

## Return Objects
"Tuple" classes (or data classes) have been created to return objects from each layer of the library. These, generally, return the successfully checked parameter name,
or an error tuple (which contains the parameter name and the error type). This was done to avoid having to throw errors since it is quite possible
that an API checking library could throw a lot of errors (and thus have a non-trivial impact on performance).

## Errors
To keep the return objects as simple as possible, an error will only return what the [error type](./src/main/java/io/bhowell2/ApiLib/ErrorType.java)
is, the parameter that failed (for nested parameters, the parameter name will be of the form 'ParameterName of (nested) NestedParameterName'), and possibly an error message with
 the [ErrorTuple](./src/main/java/io/bhowell2/ApiLib/ErrorTuple.java).

## Api Version
An API Version needs to be created for each version available (this could be done by implementing [ApiVersion]
(./src/main/java/io/bhowell2/ApiLib/ApiVersion.java) in an Enum (preferable) or creating a class for Api Versions and
implementing the interface. If the latter interface approach is used, then it is advisable to create a singleton that holds all of the possible API
Versions and retrieve them as necessary to keep from recreating them all the time.

## [CheckFunction](./src/main/java/io/bhowell2/ApiLib/CheckFunction.java)
Used to check whether a parameter meets certain conditions. Some common check functionality has already been provided for Strings, Doubles, and Integers in [utils](./src/main/java/io/bhowell2/ApiLib/utils). This returns [CheckFunctionTuple](./src/main/java/io/bhowell2/ApiLib/CheckFunctionTuple.java), which returns whether the parameter passed the check or if it failed (as well as a reason for failure).

## [ParameterRetrievalFunction](./src/main/java/io/bhowell2/ApiLib/ParameterRetrievalFunction.java)
Used to retrieve the parameter to check from the object (e.g., Map<String, Object>) that holds the parameters. This is generic so that the user can provide their own retrieval function specific to their needs (it's recommended that the user write some constants and reuse them throughout, or extend the library like it has been extended for `Map<String, Object>`).

## [ApiParameter](./src/main/java/io/bhowell2/ApiLib/ApiParameter.java)
This is really the base of the library; everything is built on top of ApiParameters. A [builder](./src/main/java/io/bhowell2/ApiLib/ApiParameterBuilder.java) is provided to more cleanly and descriptively create a
parameter. The `ApiParameter` requires a name (string), `CheckFunction`s, and a `ParameterRetrievalFunction`. The name will be sent to the `ParameterRetrievalFunction` so that it can be used to grab the parameter if necessary.

### [ApiParameterCheckTuple](./src/main/java/io/bhowell2/ApiLib/ApiParameterCheckTuple.java)
The return value of `ApiParameter.check()`. Returns the name of the parameter that was successfully checked or an `ErrorTuple`.

## [ApiCustomParameters](./src/main/java/io/bhowell2/ApiLib/ApiCustomParameters.java)
These have been provided so that the user can implement their own checks for some parameters.

### [ApiCustomParamsCheckTuple](./src/main/java/io/bhowell2/ApiLib/ApiCustomParamsCheckTuple.java)
Returns a list of the successfully checked and provided parameters or an `ErrorTuple`.

## [ApiNestedParameter](./src/main/java/io/bhowell2/ApiLib/ApiNestedParameter.java)
A nested parameter is a parameter that contains other parameters (e.g., a `JsonObject` within a `JsonObject`). The library has been created so that an arbitrary depth of nested parameters could be created if so desired; however, if you have a depth greater than 2 or 3 nested parameters, there might be something wrong with your design. Sooner or later, an `ApiNestedParameter` will be made up exclusively of `ApiParameter`s.  The nested parameter can have REQUIRED parameters and OPTIONAL parameters. If a required parameter is not provided, or fails to check, an error is returned. If an optional parameter is provided, it will be checked and fail by default (this can be changed by setting `continueOnOptionalFailure` to true) or return the name of the optional parameter if it passes. **Note: If the optional parameter fails to check and `continueOnOptionalFailure=true` the name of the failed parameter will NOT be returned -- this is to make it more likely the user will not use the bad parameter. And generally it is recommended to keep `continueOnOptionalFailure` to false.**  The nested parameter will check all "sub-parameters" (required and optional) and if any sub-parameter fails, the whole nested parameter will fail to check (withstanding what was just stated about optional parameters), otherwise the list of provided parameters is returned. A [builder](./src/main/java/io/bhowell2/ApiLib/ApiNestedParameterBuilder.java) has been provided to facilitate the creation of a nested parameter.

```javascript
{
  param1: value1,
  param2: value2,
  nestedParam: {
    nested1: nestedval1,
    nested2: nestedval2
  }
}
```

### [ApiNestedParamCheckTuple](./src/main/java/io/bhowell2/ApiLib/ApiNestedParamCheckTuple.java)
The nested check tuple is a bit different than the other check tuples. It needs to specify the name of the nested parameter (`nestedParameterName`), all of the provided parameter names (`providedParameterNames`), and whether or not that nested parameter has inner nested parameters (`innerNestedParameters`). If there is an error, the aforementioned values will be null and the `ErrorTuple` will contain the name of the failed parameter in the form of "ParameterName of NestedParameterName of NestedParameterName ... ad infinitum".

## [ApiPathRequestParameters](./src/main/java/io/bhowell2/ApiLib/ApiPathRequestParameters.java)
Contains all of the the parameters (required and optional) for a given version for a given path. A builder ([ApiPathRequestParametersBuilder](
./src/main/java/io/bhowell2/ApiLib/ApiPathRequestParametersBuilder.java) is provided to more cleanly and descriptively create this object. This allows the
user to define which parameters MUST be provided (required) and which parameters are not necessarily required (optional). The ApiPathParameters will fail by default if an optional parameter is provided and fails, unless specified otherwise (this default failure is to ensure that the optional parameter is not accidentally used later on in the application with bad values -- see above in the ApiNestedParameter section). This class is VERY similar to the ApiNestedParameter class.

### [ApiPathRequestParamsCheckTuple](./src/main/java/io/bhowell2/ApiLib/ApiPathRequestParamsCheckTuple.java)
Returns the names of all parameters that were provided and passed their checks. Also including a list of the nested parameters (which themselves may contain a list of nested parameters.. and so on..). Or an `ErrorTuple` is returned.

## [ApiPath](./src/main/java/io/bhowell2/ApiLib/ApiPath.java)
Contains all (all versions) of the ApiPathRequestParameters for a given path. This class will make sure that the correct ApiPathRequestParameters is chosen for the correct API version. Optionally, a path name and a path matcher may be provided to the ApiPath. This whole versioning scheme has not been well tested yet, but the general assumption right now is that the request version will be checked against the `ApiPathRequestParameters` for a version less than or equal to itself (they are sorted in ascending order, so it will check against the newest version first).
