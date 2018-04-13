# API Parameter Library
A library to facilitate the creation and checking of parameters for an API. ApiLib was built in a semi-opinionated way, but was built heavily on
generics so that it could be applied to many different scenarios. The semi-opinionated nature is as follows: all of the parameters of a request will
be received in one object (e.g., Map<String, Object>) and some of those parameters MUST be provided (required) and some parameters may or may not be
provided (optional). Depending on your requirements, this library takes some setup to use in your application, but once this has been taken care of
your API can be very quickly built. An extension to the library has been created for  `Map<String, Object>` type parameters ([ApiMapParam](
./src/main/java/io/bhowell2/ApiLib/extensions/map/ApiMapParam.java) -- this further reduces some boilerplate for the user and can be used as an
example for the user to create an extension specific to their needs/framework if this the Map extension does not suffice.

## Return Objects
Data classes have been created to return objects from each layer of the library. These, generally, return the successfully
checked parameter name, or a [ParamError](./src/main/java/io/bhowell2/ApiLib/ParamError.java).
Whether or not the parameter was successfully checked can be determined by checking `ApiParamCheck.successful()` (or `ApiParamCheck.failed()` could be
 used).
This was done to avoid having to throw errors since it is quite possible that an API checking library could throw a lot of errors (and thus have a 
non-trivial impact on performance). These are named `ApiParamCheck`, `ApiObjParamCheck`, `ApiCustomParamCheck`.

**A word of warning that will be reiterated again below. Only trust the ApiObjParamCheck.providedParamNames and not all of the parameters in the
request if the request passes your ApiObjParam check. This is because you may have set the ApiObjParam to continue on optional failure and the
optional parameter was invalid (and thus not supplied to `ApiObjParamCheck.providedParamNames`, but is still in the request parameters.**

**The examples below are pretty long, but this is to display the full functionality of the library. Using the map extension (or writing your own)
helps reduce this a good bit and you really should only be defining parameters and whether or not they are required in the ApiObjParam**.

## Errors
To keep the return objects as simple as possible, an error will only return what the [error type](./src/main/java/io/bhowell2/ApiLib/ErrorType.java)
is, the parameter that failed (for object parameters, the parameter name will be of the form 'ParameterName of ObjectParameterName'), and
an error message with the [ParamError](./src/main/java/io/bhowell2/ApiLib/ParamError.java), which can be conditionally tested for. If an error occurs
the check short-circuits and returns immediately, propagating all the way to the `ApiObjParam` check.

### ErrorType
*Invalid* - does not meet check requirements
*Missing* - was not provided
*Cast* - was not of correct type

## [ApiParam](./src/main/java/io/bhowell2/ApiLib/ApiParam.java)
This is the base of the library. Everything is built on top of ApiParams. A [builder](./src/main/java/io/bhowell2/ApiLib/ApiParamBuilder.java)
is provided to more cleanly and descriptively create a parameter. The `ApiParam` requires a name (string), `CheckFunction`s, and a
`ParameterRetrievalFunction`. The parameter name will be sent to the `ParameterRetrievalFunction` so that it can be used to retrieve the parameter
from the object that stores the parameter (e.g., Map).

### [CheckFunc](./src/main/java/io/bhowell2/ApiLib/CheckFunc.java)
Used to check whether a parameter meets certain conditions. ApiParam may use one or multiple of these to check the parameter. At least one is required
. Some common check functionality has already been provided for Strings, Doubles, and Integers in [utils](./src/main/java/io/bhowell2/ApiLib/utils).
This returns [CheckFuncResult](./src/main/java/io/bhowell2/ApiLib/CheckFuncResult.java), which returns whether the parameter passed the check
or if it failed and the reason for failure.

### [ParamRetrievalFunc](./src/main/java/io/bhowell2/ApiLib/ParamRetrievalFunc.java)
Used to retrieve the parameter for ApiParam to check from the object that holds the parameters (e.g., Map<String, Object>). This is generic so that
the user can provide their own retrieval function specific to their needs (it's recommended that the user write some constants and reuse them
throughout, or extend the library like it has been extended for `Map<String, Object>`). As just noted, the library provides an extension for this for
 parameters that are stored in Map<String, Object> type.

### [ApiParamCheck](./src/main/java/io/bhowell2/ApiLib/ApiParamCheck.java)
The return value of `ApiParam.check()`. Returns the name of the parameter that was successfully checked or a [ParamError](./src/main/java/io/bhowell2/ApiLib/ParamError.java).

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

  // Using map extension and provided param checks
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
  faiedParamCheck.getParamError();    // gives name of parameter, error type, and reason for failure
```

## [ApiObjParam](./src/main/java/io/bhowell2/ApiLib/ApiObjParam.java)
This is the interface to check all parameters provided to the API. The ApiObjParam is made up of `ApiParam`s, `ApiCustomParam`s, and nested `ApiObjParam`s.
The top level ApiObjParam does not have a name as it is not actually a parameter in itself, but an object that holds all of the request's parameters.
Some parameters are required and some parameters are optional - this optionality is specified by the builder or the parameters provided to the
constructor. [ApiObjParamBuilder](./src/main/java/io/bhowell2/ApiLib/ApiObjParamBuilder.java) has been provided to facilitate the creation of these
parameters. The ApiObjParam allows the specification of whether or not to fail on optional parameter check failure. The default is false - which is to
 help prevent the user from accidentally using a provided, but invalid, optional parameter in their application code. If all required parameters are
 supplied and pass their checks then the ApiObjParam will return as successfully checked if the optional parameters that are provided have passed
 their checks or `continueOnOptionalFailure` is set to true.

### [ApiObjParamCheck](./src/main/java/io/bhowell2/ApiLib/ApiObjParamCheck.java)
The return object from checking the ApiObjParam. Provides the list of successfully checked parameter names, the ApiCustomParamCheck for successfully
checked custom parameters, and the ApiObjParamCheck for successfully checked inner objects. If the check fails, a ParamError is returned (all
other properties are null) that gives the ErrorType (missing, invalid, cast), a failure message, and the parameter name.

**In your application code it is recommended that you only use the returned list of providedParameterNames.
This is to ensure that you are only using parameters that have passed the API checks.**

#### Example
```java
    // this was taken from one of the tests
    ParamRetrievalFunc<String, Map<String, Object>> stringRetrievalFunc = (String name, Map<String, Object> map) -> (String)map.get(name);

    ApiParam<String, Map<String, Object>> param1 = ApiParamBuilder.builder("param1", stringRetrievalFunc)
                                                                  .addCheckFunction(StringParamChecks.lengthGreaterThanOrEqual(2))
                                                                  .build();
    ApiParam<String, Map<String, Object>> param2 = ApiParamBuilder.builder("param2", stringRetrievalFunc)
                                                                  .addCheckFunction(StringParamChecks.lengthGreaterThanOrEqual(5))
                                                                  .build();
    ApiParam<String, Map<String, Object>> param3 = ApiParamBuilder.builder("param3", stringRetrievalFunc)
                                                                  .addCheckFunction(ParamChecks.alwaysPass()) // doesnt matter what it is,
                                                                  .build();

    ApiParam<String, Map<String, Object>> innerParam1 = ApiParamBuilder.builder("innerParam1", stringRetrievalFunc)
                                                                  .addCheckFunction(ParamChecks.alwaysPass()) // doesnt matter what it is,
                                                                  .build();

    ApiObjParam<Map<String, Object>, Map<String, Object>> innerObjParam =
        ApiObjParamBuilder.builder("innerobj", (String name, Map<String, Object> map) -> (Map<String, Object>) map.get(name))
                          .addRequiredParam(innerParam1)
                          .build();

    ApiCustomParam<Map<String, Object>> customParam = (map) -> {
        // don't really need to worry about cast exception (if used in ApiObjParam as it will catch it)
        List<String> stringListParam = (List<String>) map.get("customparam");
        if (stringListParam == null) {
            return ApiCustomParamCheck.failure(ErrorType.MISSING_PARAMETER, "customparam");
        } else {
            // exists, does it meet conditions?
            // let's say that every string in the list must have a length greater than 5
            for (String s : stringListParam) {
                if (s.length() <= 5) {
                    return ApiCustomParamCheck.failure(ErrorType.INVALID_PARAMETER, "customparam");
                }
            }
            return ApiCustomParamCheck.success("SomeSpecialCustomParamName", new HashSet<>(Arrays.asList("customparam")));
        }
    };

    ApiObjParam<Map<String, Object>, Map<String, Object>> rootObjParam =
        ApiObjParamBuilder.rootObjBuilder((String name, Map<String, Object> map) -> map)    // just return the map since it is the root
                          .addRequiredParams(param1, param2)
                          .addOptionalParam(param3)
                          .addOptionalObjParam(innerObjParam)
                          .addRequiredCustomParam(customParam)
                          .build();

    Map<String, Object> innerObjOfRequest = new HashMap<>(1);
    innerObjOfRequest.put("innerParam1", "whatever");
    innerObjOfRequest.put("anything", "won't be returned in ApiObjParamCheck, because it is not a parameter that is checked by innerObjParam");

    Map<String, Object> requestParams = new HashMap<>(4);
    requestParams.put("param1", "long enough");
    requestParams.put("param2", "long enough too");
    requestParams.put("param3", "doesn't matter");
    requestParams.put("customparam", Arrays.asList("long enough", "still long enough"));
    requestParams.put("innerobj", innerObjOfRequest);

    ApiObjParamCheck check = rootObjParam.check(requestParams);
    assertTrue(check.successful());
    assertEquals(5, check.providedParamNames.size());
    assertEquals(1, check.providedCustomParams.size());
    assertEquals(1, check.providedObjParams.get("innerobj").providedParamNames.size());
    assertTrue(check.providedObjParams.get("innerobj").providedParamNames.contains("innerParam1"));
```

## [ApiCustomParam](./src/main/java/io/bhowell2/ApiLib/ApiCustomParam.java)
Allows the user to create their own parameter check(s). This can be used for uncommon conditional checks (e.g., `(P1 &&
P2) || P3)`) or to check the indices of an array and just about any other use you can think of, but it really should only be used in extenuating
circumstances or for an array. If you need to use this outside of checking an array and maybe some small conditionals, your API is likely getting too
complicated. A name is required for the custom parameter to differentiate it from other potential custom parameters in the final returning object. See
 below about what should be returned from the custom parameter check.

### [ApiCustomParamCheck](./src/main/java/io/bhowell2/ApiLib/ApiCustomParamCheck.java)
Must specify a name for the custom parameter

#### Custom Param Conditional Example
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

#### Custom Param Array of Objects Example
```java
  // taken from a test

    String customParamName = "AnArrayWithObjectsAsValues";
    String arrayParamName = "objArray";
    String param1Name = "param1";
    String param2Name = "param2";
    String param3Name = "param3";

    ApiParam<String, Map<String, Object>> param1 = ApiParamBuilder.builder(param1Name,
                                                                           (String name, Map<String, Object> map) -> (String) map.get(name))
                                                                  .addCheckFunction(ParamChecks.alwaysPass())
                                                                  .build();

    ApiParam<String, Map<String, Object>> param2 = ApiParamBuilder.builder(param2Name,
                                                                           (String name, Map<String, Object> map) -> (String) map.get(name))
                                                                  .addCheckFunction(StringParamChecks.lengthGreaterThanOrEqual(2))
                                                                  .build();

    ApiParam<Integer, Map<String, Object>> param3 = ApiParamBuilder.builder(param3Name, ApiMapParamRetrievalFuncs.getIntegerFromMap())
                                                                   .addCheckFunction(IntegerParamChecks.valueGreaterThan(3))
                                                                   .build();

    ParamRetrievalFunc<Map<String, Object>, Map<String, Object>> mapParamRetrievalFunc = (name, map) -> map;

    ApiObjParam<Map<String, Object>, Map<String, Object>> expectedInnerObject =
        ApiObjParamBuilder.rootObjBuilder(mapParamRetrievalFunc)
                          .addRequiredParams(param1, param2, param3)
                          .build();

    ApiCustomParam<Map<String, Object>> mapApiCustomParam = (requestParameters -> {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> arrayOfObjects = (List<Map<String, Object>>) requestParameters.get(arrayParamName);
        if (arrayOfObjects == null) {
            return ApiCustomParamCheck.failure(ErrorType.MISSING_PARAMETER, arrayParamName);
        }
        List<ApiObjParamCheck> checkedObjectsOfArray = new ArrayList<>();
        for (Map<String, Object> map : arrayOfObjects) {
            ApiObjParamCheck check = expectedInnerObject.check(map);
            if (check.failed()) {
                return ApiCustomParamCheck.failure(check.paramError);
            }
            checkedObjectsOfArray.add(check);
        }
        return ApiCustomParamCheck.success(customParamName, checkedObjectsOfArray);
    });

    Map<String, Object> innerPassingReqObj = new HashMap<>(3);
    innerPassingReqObj.put(param1Name, "always pass");
    innerPassingReqObj.put(param2Name, "pass");
    innerPassingReqObj.put(param3Name, 4);

    Map<String, Object> innerPassingReqObj2 = new HashMap<>(3);
    innerPassingReqObj2.put(param1Name, "always pass");
    innerPassingReqObj2.put(param2Name, "pass");
    innerPassingReqObj2.put(param3Name, 4);

    List<Map<String, Object>> array = Arrays.asList(innerPassingReqObj, innerPassingReqObj2);
    Map<String, Object> passingReqParams = new HashMap<>(1);
    passingReqParams.put(arrayParamName, array);

    ApiCustomParamCheck check = mapApiCustomParam.check(passingReqParams);
    assertTrue(check.successful());
    assertEquals(2, check.arrayObjParams.size(), "Array size should be 2. One for each object in array position.");
```


**See tests for more examples.**
