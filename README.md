# API Parameter Library
A library to facilitate the creation and checking of parameters for an API.  This library takes some setup to use in your application, but once this
has been taken care of your API can be very quickly built. An extension to the library has been created for `Map<String, Object> requestParameters`
-- this further reduces some boilerplate for the user and/or can be used as an example for the user to create a framework specific extension.

## Return Objects
"Tuple" classes (or, data classes) have been created to return objects from each layer of the library. These, generally, return the successfully
checked parameter name, or an error tuple (which contains the parameter name and the error type). This was done to avoid having to throw errors since
it is quite possible that an API checking library could throw a lot of errors (and thus have a non-trivial impact on performance).

## Errors
To keep the return objects as simple as possible, an error will only return what the [error type](./src/main/java/io/bhowell2/ApiLib/ErrorType.java)
is, the parameter that failed (for nested parameters, the parameter name will be of the form 'ParameterName of (nested) NestedParameterName'), and
possibly an error message with the [ErrorTuple](./src/main/java/io/bhowell2/ApiLib/ErrorTuple.java), which can be conditionally tested for.

## [CheckFunction](./src/main/java/io/bhowell2/ApiLib/CheckFunction.java)
Used to check whether a parameter meets certain conditions. ApiParameter may use one or multiple of these to check its parameter.
Some common check functionality has already been provided for Strings, Doubles, and Integers in [utils](./src/main/java/io/bhowell2/ApiLib/utils).
This returns [CheckFunctionTuple](./src/main/java/io/bhowell2/ApiLib/CheckFunctionTuple.java), which returns whether the parameter passed the check
or if it failed (as well as a reason for failure).

## [ParameterRetrievalFunction](./src/main/java/io/bhowell2/ApiLib/ParameterRetrievalFunction.java)
Used to retrieve the parameter to check from the object (e.g., Map<String, Object>) that holds the parameters. ApiParameter uses this to retrieve the
parameter to check from the map. This is generic so that the user can provide their own retrieval function specific to their needs (it's recommended
that the user write some constants and reuse them throughout, or extend the library like it has been extended for `Map<String, Object>`). As just
noted, the library provides an extension for this for parameters that are stored in a Map<String, Object>.

## [ApiParameter](./src/main/java/io/bhowell2/ApiLib/ApiParameter.java)
The base of the library. Everything is built on top of ApiParameters. A [builder](./src/main/java/io/bhowell2/ApiLib/ApiParameterBuilder.java)
is provided to more cleanly and descriptively create a parameter. The `ApiParameter` requires a name (string), `CheckFunction`s, and a
`ParameterRetrievalFunction`. The name will be sent to the `ParameterRetrievalFunction` so that it can be used to retrieve the parameter from the
object that stores the parameter (e.g., Map).

### [ApiParamTuple](./src/main/java/io/bhowell2/ApiLib/ApiParamTuple.java)
The return value of `ApiParameter.check()`. Returns the name of the parameter that was successfully checked or an `ErrorTuple`.

#### Example
```java
  // Using Java 8 lambdas
  // The parameter type to be checked is String
  // The object to retrieve the parameter from is of type Map<String, Object>
  ApiParameter<String, Map<String, Object>> apiParameter = ApiParameterBuilder.builder("ParameterName1", (paramName, map) -> {
    return (String)map.get(paramName);
  })
    .addCheckFunction(ParameterStringChecks.lengthGreaterThan(5))
    .addCheckFunction(s -> s.length < 1000) // create your own check function (this one is already provided, but just an example on how to do your own)
    .build();

  // Using map extension
  // The parameter type to be checked is String
  // The object to retrieve the parameter from is of type Map<String, Object>
  ApiMapParameter<String> apiMapParameter = ApiMapParameterBuilder.builder("ParameterName1", String.class)
    .addCheckFunction(ParameterStringChecks.lengthGreaterThan(5))
    .build();

  Map<String, Object> paramsToCheck = new HashMap();
  paramsToCheck.put("ParameterName1", "long enough");

  ApiParamTuple checkTuple = apiMapParameter.check(paramsToCheck);
  checkTuple.successful();  // true

  Map<String, Object> badParamsToCheck = new HashMap();
  badParamsToCheck.put("ParameterName1", "short");

  // If you wanted to check the parameter individually yourself, could do this,
  // but the ApiObjectParameter will do all of this for you and you can get the
  // final result from all of the parameters being checked.

  ApiParamTuple failedCheckTuple = apiMapParameter.check(badParamsToCheck);
  failedCheckTuple.successful();      // false
  faiedCheckTuple.getErrorTuple();    // gives name of parameter and why it is invalid
```

## [ApiCustomParameter](./src/main/java/io/bhowell2/ApiLib/ApiCustomParameter.java)
Allows the user to create their own parameter check(s). This can be used for uncommon conditional checks (e.g., `(P1 &&
P2) || P3)`) or to check the indices of an array and just about any other use you can think of, but it really should only be used in extenuating circumstances or for an array. If you need to use this outside of checking an array, your API is likely getting too complicated. A name is required for the custom parameter to differentiate it from other potential custom parameters in the final returning object.

### [ApiCustomParamCheckTuple](./src/main/java/io/bhowell2/ApiLib/ApiCustomParamCheckTuple.java)
Provides the name of the custom parameter check (the reason

#### Example
```java
  // Should have both ParameterName1 && ParameterName2 || ParameterName3

  ApiMapParameter<String> apiMapParameter1 = ApiMapParameterBuilder.builder("ParameterName1", String.class)
      .addCheckFunction(ParameterStringChecks.lengthGreaterThan(5))
      .build();

  ApiMapParameter<String> apiMapParameter2 = ApiMapParameterBuilder.builder("ParameterName2", String.class)
      .addCheckFunction(ParameterStringChecks.lengthGreaterThan(5))
      .build();

  ApiMapParameter<String> apiMapParameter3 = ApiMapParameterBuilder.builder("ParameterName3", String.class)
      .addCheckFunction(ParameterStringChecks.lengthGreaterThan(5))
      .build();


  ApiCustomParameter<Map<String, Object>> conditionalCheck = (map) -> {

    ApiParamTuple tuple1 = apiMapParameter1.check(map);
    ApiParamTuple tuple2 = apiMapParameter2.check(map);

    if (tuple1.successful() && tuple2.successful()) {
      Set<String> providedParamNames = new HashSet<>(Arrays.asList(tuple1.parameterName, tuple2.parameterName));
      return ApiCustomParamTuple.success("ConditionalCheck", providedParamNames, null, null);
    }
    ApiParamTuple tuple3 = apiMapParameter3.check(map);
    if (tuple3.successful()) {
      Set<String> providedParamNames = new HashSet<>(Arrays.asList(tuple3.parameterName));
      return ApiCustomParamTuple.success("ConditionalCheck", providedParamNames, null, null);
    }

    // check to see if it just wasnt provided or if they just failed to pass the parameter checks
    if (tuple1.errorTuple.errorType == ErrorType.MISSING_PARAMETER && tuple2.errorTuple.errorType == ErrorType.MISSING_PARAMETER
        && tuple3.errorTuple.errorType == ErrorType.MISSING_PARAMETER) {
      return ApiCustomParamTuple.failure(new ErrorTuple(ErrorType.MISSING_PARAMETER, "ParameterName1 AND ParameterName2 OR ParameterName3 were missing."));
    }
    return ApiCustomParamTuple.failure(new ErrorTuple(ErrorType.INVALID_PARAMETER, "ParameterName1 AND ParameterName2 OR ParameterName3 failed to meet API requirements."));
  };

```

## [ApiObjectParameter](./src/main/java/io/bhowell2/ApiLib/ApiObjectParameter.java)
The outermost element of the library. The ApiObjectParameter is made up of ApiParameters, ApiCustomParameters, and other ApiObjectParameters. The top level ApiObjectParameter does not have a name as it is not actually a parameter, but an object that holds all of the requests parameters (i.e, it is the Map<String, Object> that may contain child maps). Some parameters are required and some parameters are optional. [ApiObjectParameterBuilder](./src/main/java/io/bhowell2/ApiLib/ApiObjectParameterBuilder.java) has been provided to facilitate the creation of these parameters. If all required parameters are supplied and pass their checks then the ApiObjectParameter will return as successfully checked. The ApiObjectParameter allows the specification of whether or not to fail on optional parameter check failure. The default is false - this is to help prevent the user from accidentally using a provided, but invalid, parameter in their application code.

### [ApiObjectParamTuple](./src/main/java/io/bhowell2/ApiLib/ApiObjectParamTuple.java)
The return object from checking the ApiObjectParameter. Provides the list of successfully checked parameter names, the ApiCustomParamTuple for successfully checked custom parameters, and the ApiObjectParamTuple for successfully checked inner objects. If the check fails, an ErrorTuple is returned (all other properties are null) that gives the ErrorType, a failure message, and possible the parametername

** NOTE: In your application code, you should only use the returned list of providedParameterNames. This is to ensure that you are only using parameters that have passed the API checks. **

#### Example
```java

  ApiMapParameter<String> apiMapParameter1 = ApiMapParameterBuilder.builder("ParameterName1", String.class)
      .addCheckFunction(ParameterStringChecks.lengthGreaterThan(5))
      .build();

  ApiMapParameter<String> apiMapParameter2 = ApiMapParameterBuilder.builder("ParameterName2", String.class)
      .addCheckFunction(ParameterStringChecks.lengthGreaterThan(5))
      .build();

  ApiMapParameter<String> apiMapParameter3 = ApiMapParameterBuilder.builder("ParameterName3", String.class)
      .addCheckFunction(ParameterStringChecks.lengthGreaterThan(5))
      .build();

  ApiMapObjectParameter requestApi = ApiMapObjectParameterBuilder.builder()
  .addRequiredParameter(apiMapParameter1)
  .addRequiredParameter(apiMapParameter2)
  .addOptionalParameter(apiMapParameter3)
  .build();

  Map<String, Object> requestParameters = new HashMap<>();
  requestParameters.add("ParameterName1", "some value") // value will pass
                   .add("ParameterName2", "long enough"); // value will pass

  ApiObjectParamTuple checkTuple = requestParameters.check(requestParameters);

  checkTuple.successful(); // true
  checkTuple.providedParamNames;  // Set containing ParameterName1, ParameterName2

  ApiObjectParamTuple failedCheckTuple = requestParameters.check(new HashMap());
  failedCheckTuple.successful();    // false
  failedCheckTuple.errorTuple       // ErrorType == MISSING_PARAMETER, parameterName = ParameterName1 (always returns name of first failure)

```

** See tests for more examples. **
