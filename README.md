# API Parameter Library
A library to facilitate the creation and checking of parameters for an API.  This library takes some setup to use in your application, but once this
has been taken care of your API can be very quickly built. An extension to the library has been created for `Map<String, Object> requestParameters`
([ApiMapParam](./src/main/java/io/bhowell2/ApiLib/extensions/map/ApiMapParam.java) -- this further reduces some boilerplate for the user and can be
 used as an example for the user to create an extension specific to their needs/framework. 

## Return Objects
Data classes have been created to return objects from each layer of the library. These, generally, return the successfully
checked parameter name, or a [ParamError](./src/main/java/io/bhowell2/ApiLib/ParamError.java) (which contains the parameter name and the error type). 
Whether or not the parameter was successfully checked can be determined by checking `ApiParamCheck.successful()` (et al.).
This was done to avoid having to throw errors since it is quite possible that an API checking library could throw a lot of errors (and thus have a 
non-trivial impact on performance). These are named `ApiParamCheck`, `ApiObjParamCheck`, etc.

## Errors
To keep the return objects as simple as possible, an error will only return what the [error type](./src/main/java/io/bhowell2/ApiLib/ErrorType.java)
is, the parameter that failed (for object parameters, the parameter name will be of the form 'ParameterName of (object) ObjectParameterName'), and
an error message with the [ParamError](./src/main/java/io/bhowell2/ApiLib/ParamError.java), which can be conditionally tested for.

## [ApiParam](./src/main/java/io/bhowell2/ApiLib/ApiParam.java)
The base of the library. Everything is built on top of ApiParams. A [builder](./src/main/java/io/bhowell2/ApiLib/ApiParamBuilder.java)
is provided to more cleanly and descriptively create a parameter. The `ApiParam` requires a name (string), `CheckFunction`s, and a
`ParameterRetrievalFunction`. The name will be sent to the `ParameterRetrievalFunction` so that it can be used to retrieve the parameter from the
object that stores the parameter (e.g., Map).

### [CheckFunc](./src/main/java/io/bhowell2/ApiLib/CheckFunc.java)
Used to check whether a parameter meets certain conditions. ApiParam may use one or multiple of these to check the parameter.
Some common check functionality has already been provided for Strings, Doubles, and Integers in [utils](./src/main/java/io/bhowell2/ApiLib/utils).
This returns [CheckFunctionResult](./src/main/java/io/bhowell2/ApiLib/CheckFunctionResult.java), which returns whether the parameter passed the check
or if it failed and the reason for failure.

### [ParamRetrievalFunc](./src/main/java/io/bhowell2/ApiLib/ParamRetrievalFunc.java)
Used to retrieve the parameter to check from the object that holds the parameters (e.g., Map<String, Object>). ApiParam uses this to retrieve the
parameter to check from the request parameters. This is generic so that the user can provide their own retrieval function specific to their needs
(it's recommended that the user write some constants and reuse them throughout, or extend the library like it has been extended for `Map<String,
Object>`). As just noted, the library provides an extension for this for parameters that are stored in Map<String, Object> type.

### [ApiParamCheck](./src/main/java/io/bhowell2/ApiLib/ApiParamCheck.java)
The return value of `ApiParam.check()`. Returns the name of the parameter that was successfully checked or an [ParamError](./src/main/java/io/bhowell2/ApiLib/ParamError.java).

#### Example
```java
  // Using Java 8 lambdas
  // Parameter Type: String
  // The object to retrieve the parameter from is of type Map<String, Object>
  ApiParam<String, Map<String, Object>> ApiParam =
      ApiParamBuilder.builder("ParameterName1", (paramName, map) -> {
        // ParamRetrievalFunc
        return (String)map.get(paramName);
      })
    .addCheckFunction(StringParamChecks.lengthGreaterThan(5))
    .addCheckFunction(s -> s.length < 1000) // create your own check function (this one is already provided, but just as a lambda example)
    .build();

  // Using map extension
  // The parameter type to be checked is String
  // The object to retrieve the parameter from is of type Map<String, Object>
  ApiMapParam<String> ApiMapParam = ApiMapParamBuilder.builder("ParameterName1", String.class)
    .addCheckFunction(StringParamChecks.lengthGreaterThan(5))
    .build();

  Map<String, Object> paramsToCheck = new HashMap();
  paramsToCheck.put("ParameterName1", "long enough");

  ApiParamCheck paramCheck = ApiMapParam.check(paramsToCheck);
  paramCheck.successful();  // true

  Map<String, Object> badParamsToCheck = new HashMap();
  badParamsToCheck.put("ParameterName1", "short");

  // If you wanted to check the parameter individually yourself, could do this,
  // but the ApiObjParam will do all of this for you and you can get the
  // final result from all of the parameters being checked.

  ApiParamCheck failedParamCheck = ApiMapParam.check(badParamsToCheck);
  failedParamCheck.successful();      // false
  faiedParamCheck.getParamError();    // gives name of parameter and why it is invalid
```

## [ApiObjParam](./src/main/java/io/bhowell2/ApiLib/ApiObjParam.java)
This is the interface to check all parameters provided to the API. The ApiObjParam is made up of ApiParams, ApiCustomParams, and nested ApiObjParams.
The top level ApiObjParam does not have a name as it is not actually a parameter in itself, but an object that holds all of the requests parameters.
Some parameters are required and some parameters are optional (the optionality is specified when creating the ApiParam). [ApiObjParamBuilder](
./src/main/java/io/bhowell2/ApiLib/ApiObjParamBuilder.java) has been provided to facilitate the creation of these parameters. The ApiObjParam allows
the specification of whether or not to fail on optional parameter check failure. The default is false - which is to help prevent the user from
accidentally using a provided, but invalid, optional parameter in their application code. If all required parameters are supplied and pass their
checks then the ApiObjParam will return as successfully checked if the optional parameters that are provided have passed their checks or
`continueOnFailure` is set to true.

### [ApiObjParamCheck](./src/main/java/io/bhowell2/ApiLib/ApiObjParamCheck.java)
The return object from checking the ApiObjParam. Provides the list of successfully checked parameter names, the ApiCustomParamCheck for successfully
checked custom parameters, and the ApiObjParamCheck for successfully checked inner objects. If the check fails, a ParamError is returned (all
other properties are null) that gives the ErrorType (missing, invalid, cast), a failure message, and the parameter name.

**In your application code it is recommended that you only use the returned list of providedParameterNames.
This is to ensure that you are only using parameters that have passed the API checks.**

#### Example
```java

  ApiMapParam<String> ApiMapParam1 = ApiMapParamBuilder.builder("ParameterName1", String.class)
      .addCheckFunction(StringParamChecks.lengthGreaterThan(5))
      .build();

  ApiMapParam<String> ApiMapParam2 = ApiMapParamBuilder.builder("ParameterName2", String.class)
      .addCheckFunction(StringParamChecks.lengthGreaterThan(5))
      .build();

  ApiMapParam<String> ApiMapParam3 = ApiMapParamBuilder.builder("ParameterName3", String.class)
      .addCheckFunction(StringParamChecks.lengthGreaterThan(5))
      .build();

  ApiMapObjParam requestApi = ApiMapObjParamBuilder.builder()
  .addRequiredParameter(ApiMapParam1)
  .addRequiredParameter(ApiMapParam2)
  .addOptionalParameter(ApiMapParam3)
  .build();

  Map<String, Object> requestParameters = new HashMap<>();
  requestParameters.add("ParameterName1", "some value") // value will pass
                   .add("ParameterName2", "long enough"); // value will pass

  ApiObjParamCheck objParamCheck = requestParameters.check(requestParameters);

  objParamCheck.successful(); // true
  objParamCheck.providedParamNames;  // Set containing ParameterName1, ParameterName2

  ApiObjParamCheck failedObjParamCheck = requestParameters.check(new HashMap());
  failedObjParamCheck.successful();    // false
  failedObjParamCheck.ParamError       // ErrorType == MISSING_PARAMETER, parameterName = ParameterName1 (always returns name of first failure)

```

## [ApiCustomParam](./src/main/java/io/bhowell2/ApiLib/ApiCustomParam.java)
Allows the user to create their own parameter check(s). This can be used for uncommon conditional checks (e.g., `(P1 &&
P2) || P3)`) or to check the indices of an array and just about any other use you can think of, but it really should only be used in extenuating circumstances or for an array. If you need to use this outside of checking an array, your API is likely getting too complicated. A name is required for the custom parameter to differentiate it from other potential custom parameters in the final returning object.

### [ApiCustomParamCheck](./src/main/java/io/bhowell2/ApiLib/ApiCustomParamCheck.java)
Provides the name of the custom parameter check (the reason

#### Example
```java
  // Should have both ParameterName1 && ParameterName2 || ParameterName3

  ApiMapParam<String> ApiMapParam1 = ApiMapParamBuilder.builder("ParameterName1", String.class)
      .addCheckFunction(StringParamChecks.lengthGreaterThan(5))
      .build();

  ApiMapParam<String> ApiMapParam2 = ApiMapParamBuilder.builder("ParameterName2", String.class)
      .addCheckFunction(StringParamChecks.lengthGreaterThan(5))
      .build();

  ApiMapParam<String> ApiMapParam3 = ApiMapParamBuilder.builder("ParameterName3", String.class)
      .addCheckFunction(StringParamChecks.lengthGreaterThan(5))
      .build();


  ApiCustomParam<Map<String, Object>> conditionalCheck = (map) -> {

    ApiParamCheck paramCheck1 = ApiMapParam1.check(map);
    ApiParamCheck paramCheck2 = ApiMapParam2.check(map);

    if (paramCheck1.successful() && paramCheck2.successful()) {
      Set<String> providedParamNames = new HashSet<>(Arrays.asList(paramCheck1.parameterName, paramCheck2.parameterName));
      return ApiCustomParamCheck.success("ConditionalCheck", providedParamNames, null, null);
    }
    ApiParamCheck paramCheck3 = ApiMapParam3.check(map);
    if (paramCheck3.successful()) {
      Set<String> providedParamNames = new HashSet<>(Arrays.asList(paramCheck3.parameterName));
      return ApiCustomParamCheck.success("ConditionalCheck", providedParamNames, null, null);
    }

    // check to see if it just wasnt provided or if they just failed to pass the parameter checks
    if (paramCheck1.ParamError.errorType == ErrorType.MISSING_PARAMETER && paramCheck2.ParamError.errorType == ErrorType.MISSING_PARAMETER
        && paramCheck3.ParamError.errorType == ErrorType.MISSING_PARAMETER) {
      return ApiCustomParamCheck.failure(new ParamError(ErrorType.MISSING_PARAMETER, "ParameterName1 AND ParameterName2 OR ParameterName3 were missing."));
    }
    return ApiCustomParamCheck.failure(new ParamError(ErrorType.INVALID_PARAMETER, "ParameterName1 AND ParameterName2 OR ParameterName3 failed to meet API requirements."));
  };

```

**See tests for more examples.**
