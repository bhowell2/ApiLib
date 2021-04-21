![](https://github.com/bhowell2/ApiLib/workflows/build/badge.svg)
[![codecov](https://codecov.io/gh/bhowell2/ApiLib/branch/master/graph/badge.svg?token=7qiOJ8Vs7G)](https://codecov.io/gh/bhowell2/ApiLib)
![](https://img.shields.io/maven-central/v/io.github.bhowell2/api-lib)

# API Library
Library to aid API parameter checking. Facilitates rapidly creating a structured API where each parameter will be checked
to ensure that it meets certain conditions. The list of successfully checked parameters will be returned so that the user
may be certain which parameters are valid (have met checks). This library assumes that the parameters are stored in an 
object of type `Map<String, Object>`. It is probably most helpful to think of `Map` and `JsonObject` as synonymous - Java 
does not have a native `JsonObject` type, but in most libraries the `JsonObject` is backed by `Map<String, Object>`.

### Maven
``` 
<dependency>
    <groupId>io.github.bhowell2</groupId>
    <artifactId>api-lib</artifactId>
    <version>0.2.0</version>
</dependency>
```

### Gradle
```
dependencies {
    implementation("io.github.bhowell2:api-lib:0.2.0")
}
```

## The Basics
The entire process is modeled just as you'd create your Map or JsonObject. You just specify the checks for any given
parameter -- even nested Maps/JsonObjects, made up of other parameters or even more nested Maps! This is just a quick
overview of the API and a more in depth example is below and also there are plenty of examples in the
[tests](./src/test/java/io/github/bhowell2/apilib).

### [ApiMapParam](./src/main/java/io/github/bhowell2/apilib/ApiMapParam.java)
The top level `ApiMapParam` contains [ApiSingleParams](#apisingleparam), nested `ApiMapParam`s, and
[ApiCollectionParams](#apicollectionparam). The user can also define "custom parameters", but these act like
the others.

The `ApiMapParam` does not require a `keyName` if it is the root level `ApiMapParam` or if it is used to check the
indices of an [ApiCollectionParam](#apicollectionparam). The parameters of the request (`Map`/`JsonObject`) are can
be set such that they are `required` or `optional`.

```java
ApiMapParam rootParam = ApiMapParam.builder()
	.addRequiredSingleParams(...)
	.addRequiredMapParams(...)
	.addOptionalCollectionParams(...);
```

#### [ApiMapParam.Result](./src/main/java/io/github/bhowell2/apilib/ApiMapParam.java)
This is returned with the successful results or failure information of the `ApiMapParam`. This contains all the
information necessary for the user to find out which parameters were successfully checked (as well as which nested
parameters were successfully checked).

In the case of an error an [ApiParamError](#apiparamerror) is returned which allows the user to obtain where and
(potentially) what caused the failure of the `ApiMapParam` check.

See [full example below](#example) or see [tests](./src/test/java/io/github/bhowell2/apilib) for more examples.

### [ApiSingleParam](./src/main/java/io/github/bhowell2/apilib/ApiSingleParam.java)
This can be any type of parameter. Single parameters have a `keyName`, at least one
[Check](./src/main/java/io/github/bhowell2/apilib/checks/Check.java) (even if that check is
`Check.alwaysPass()`) which helps ensure the user did not miss providing a check, an optional `displayName`, an optional
`invalidErrorMessage` and optional
[Formatter](./src/main/java/io/github/bhowell2/apilib/formatters/Formatter.java)s.

The user does not really need to worry about the `ApiSingleParam.Result` as it is handled for the user, but for
completeness the result either returns `successful` or `failed` where the failure contains the reason of failure.

```java
ApiSingleParam<String> username =
	ApiSingleParam
	.builder<String>("username")
	.addChecks(StringChecks.lengthGreaterThan(5), StringChecks.lengthLessThan(100))
	.build();

public static final ApiSingleParam<String> PASSWORD =
	ApiSingleParam
	.builder(BodyParamNames.PASSWORD, String.class)
	.addChecks(StringChecks.lengthGreaterThan(5),
	           StringChecks.containsCodePointsInRange(1, "0", "9", false),
	           StringChecks.lengthLessThan(100))
	.build();
```

### [ApiCollectionParam](./src/main/java/io/github/bhowell2/apilib/ApiMapParam.java)
A list-type collection parameter (not a `Map`). This has two, current, extensions:  
[ApiListParam](./src/main/java/io/github/bhowell2/apilib/ApiListParam.java) and
[ApiArrayParam](./src/main/java/io/github/bhowell2/apilib/ApiArrayParam.java). Similar to `ApiSingleParam`,
`ApiCollectionParam`s have a `keyName` (unless they are nested ApiCollectionChecks).

### Custom Parameters
There are conditional cases that cannot be handled with the `required` or `optional` parameters of `ApiMapParam`,
necessitating these custom parameters.The custom parameters provide the user with the ability to check parameters
in any way they want and (in the case of [ApiCustomParam](#apicustomparam)) return the names of the successfully
checked parameters.

#### [ApiMapParamConditionalCheck](./src/main/java/io/github/bhowell2/apilib/ApiMapParamConditionalCheck.java)
These are simpler than [ApiCustomParam](#apicustomparam), because they are binary. They can return that they were
successful (without returning any parameter key names) or that they failed (returning a failure message).

An example for needing the custom parameter is is the user signing up for `e_billing` but not providing an `email`.
```java
ApiMapParamConditionalCheck.Result check(Map<String, Object> params, ApiMapParam.Result result){
	// return success if e_billing was NOT provided OR it is
	// provided AND is true AND email was provided
	if(!result.containsParameter("e_billing")
	  || (
	      result.containsParameter("e_billing")
	      &&
	      (Boolean)params.get("e_billing")
      	&&
      	result.containsParameter("email")
       )
	) {
	  return ApiMapParamConditionalCheck.Result.success();
	}
	return ApiMapParamConditionalCheck.Result.failure("Must provide email if e_billing is set to true");
}	
```

#### [ApiCustomParam](./src/main/java/io/github/bhowell2/apilib/ApiCustomMapParam.java)
Similar to `ApiCustomMapParam` this allows to check any values of an `ApiMapParam`, but also allows for returning
everything all the other `Results` return: checked key names and/or checked collection results and/or checked map results.

Example of the previous `ApiCustomMapParam` as an `ApiCustomParam`:
```java
public class CheckEmailWithEbillingParam extends ApiCustomParam { 
  ApiCustomParam.Result check(Map<String, Object> map) {
    // keep in mind this isn't even checking the value of email and it should be 
    // verified that email is actually a valid email (could use a check here)
    if (map.containsKey("e_billing")) {
      if ((Boolean)map.get("e_billing") && !map.containsKey("email")) {
        return ApiCustomParam.Result.failed("Must provide email if e_billing is set to true");
      }
      Check.Result emailCheckResult = StringChecks.MATCHES_BASIC_EMAIL_PATTERN(map.get("email"));
      if (emailCheckResult.failed()) {
        return ApiCustomParam.Result.success("e_billing", "email");
      }
    }
    // e_billing was not provided and don't care about email then (this is just 
    // for demonstration purposes! really, you'd want to still return email if it
    // provided and the check passes - but you'd be better not doing that in a 
    // single param check)
    return ApiCustomParam.Result.success();
  }
}
```

There are more examples (in [ApiCustomParamTests](./src/test/java/io/github/bhowell2/apilib/ApiCustomParamTests.java))
of using custom parameters and returning more complex information (collections and nested maps).

### [Check](./src/main/java/io/github/bhowell2/apilib/checks/Check.java)
Checks are used to check the values for `ApiSingleParams` or `ApiCollectionParams`. Many common use case checks have
already been [provided](./src/main/java/io/github/bhowell2/apilib/checks) (e.g. `StringChecks.lengthLessThan(50)`).

In the case of a failure the check can return a decriptive error message that may be returned to the client.
E.g., `StringChecks.lengthGreaterThan(5).check("hey")` will return the default error message of the check which
is "Length must be greater than 5.".

### [ApiParamError](./src/main/java/io/github/bhowell2/apilib/errors/ApiParamError.java)
This returns the `keyName` of the parameter that caused the check to fail an `errorMessage` that may be returned
to the client if desired (though care should be taken to not divulge too much information -- generally
`ApiErrorType.MISSING_PARAMETER` or `ApiErrorType.INVALID_PARAMETER` will be safe to return the `keyName` and
error message).

The `ApiParamError` also provides information for when the error occurred in a nested `ApiMapParam` via
`ApiParamError.childParamError`, allowing the user to trace the error to the source. (The user can chain the `keyNames`
of each error if desired to return to the user.)

If a thrown exception caused the error it can be retrieved with `ApiParamError.exception`.

## Example
A full example will make things more digestible since there are a few parts to the library.
```java
/*
This is just a possible way to create parameters, but can be done whichever way the user deems 
appropriate. However, it is HIGHLY recommended that parameters are created statically. Otherwise,
there would be a lot of overhead recreating the parameters every time they need to be checked. The 
classes are thread-safe so long as they do not use a Formatter. If a Formatter is supplied, then 
it will write back to the Map containing the parameters being checked - generally this should not 
cause problems because the parameters should not be trusted until they have passed all checks.. 
This can of course be alleviated if the map is synchronized in some way (e.g.,  
Collections.synchronizedMap(..) or ConcurrentHashMap). 
*/

public abstract class ApiRequestBase {
  
  public final ApiMapParam rootParam;
  public final Map<String, Object> requestParams;
  public final ApiMapParamCheckResult checkResult;

  public ApiBase(ApiMapParam rootParam) { 
    this.rootParam = rootParam;
  }

  public boolean successful() {
    return checkResult.successful();
  }

  public boolean failed() {
    return checkResult.failed();
  }

  public void check(Map<String, Object> requestParams) {
    // if you want to access from this class later..
    this.requestParams = requestParams;
    this.checkResult = this.rootParam.check(requestParams);
  }

  // this only works for top-level parameters, not for embedded map params
  public boolean containsParameter(String keyName) {
    return this.checkResult.containsParameter(keyName);
  }

  public ApiMapParamCheckResult getMapParamCheck(String keyName) {
    return this.checkResult.getMapParamCheck(keyName);
  }

}

public class Address {

  public static class ParamNames {
    public static final String NAME = "name";
    public static final String LINE1 = "line1";
    public static final String LINE2 = "line2";
    public static final String CITY = "city";
    public static final String STATE = "state";
    public static final String ZIP = "zip";
  }

  public static final ApiSingleParam<String> LINE1 =
    ApiSingleParamBuilder.builder(BodyParamNames.LINE1, String.class)
                         .addChecks(StringChecks.IS_NOT_EMPTY_OR_ONLY_WHITESPACE, StringChecks.lengthGreaterThan(2))
                         .build();

  public static final ApiSingleParam<String> LINE2 =
    ApiSingleParamBuilder.builder(BodyParamNames.LINE2, String.class)
                         .addChecks(Check.alwaysPass(String.class))
                         .build();

  public static final ApiSingleParam<String> LINE3 =
    ApiSingleParamBuilder.builder(BodyParamNames.LINE3, String.class)
                         .addChecks(Check.alwaysPass(String.class))
                         .build();

  public static final ApiSingleParam<String> CITY =
    ApiSingleParamBuilder.builder(BodyParamNames.CITY, String.class)
                         .addChecks(StringChecks.IS_NOT_EMPTY_OR_ONLY_WHITESPACE)
                         .build();

  // only allowing state abbreviation. e.g., TN
  public static final ApiSingleParam<String> STATE =
    ApiSingleParamBuilder.builder(BodyParamNames.STATE, String.class)
                         .addChecks(StringChecks.lengthEqualTo(2))
                         .build();

  public static final ApiSingleParam<String> ZIP =
    ApiSingleParamBuilder.builder(BodyParamNames.ZIP, String.class)
                         .addChecks(StringChecks.IS_NOT_EMPTY_OR_ONLY_WHITESPACE)
                         .build();

  /*
   * There is not a single parameter named "shipping_address", but they are part of an array which
   * is named "shipping_addresses", so it is a root/unnamed parameter.
   * */
  public static final ApiMapParam ADDRESS =
    ApiMapParamBuilder.rootBuilder()
                      // if line2 or line 3 is supplied, but they fail for whatever reason still continue (just do not use them)
                      .setContinueOnOptionalFailure(true)
                      .addRequiredSingleParams(LINE1, CITY, STATE, ZIP)
                      .addOptionalSingleParams(LINE2, LINE3)
                      .build();

}

/*
  Extends request base from above. Which gives some 
*/
public class CreateCustomer extends ApiRequestBase {

  public static class ParamNames {
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String BILLING_ADDRESS = "billing_address";
    public static final String SHIPPING_ADDRESSES = "shipping_addresses";
  }

  public static final ApiSingleParam USERNAME =
    ApiSingleParamBuilder.builder(ParamNames.USERNAME, String.class)
                         .addCheck(StringChecks.lengthGreaterThan(5)) // ensure that username length > 5
                         .addCheck(StringChecks.lengthLessThan(100)) // ensure that username length < 100
                         // ensures no code point is repeated more than 2 times (e.g., cant have aaauser1)
                         .addCheck(StringChecks.limitConsecutiveCodePoints(2))
                         // ensure that only A-z and 0-9 may be supplied for username. 
                         // does not allow white space (the false)
                         .addCheck(ConditionalChecks.orCheck(StringChecks.limitCodePointsToRange("A", "z", false),
                                                             StringChecks.limitCodePointsToRange("0", "9", false)))
                         .build();


  public static final ApiSingleParam PASSWORD =
    ApiSingleParamBuilder.builder(ParamNames.PASSWORD, String.class)
                         .addCheck(StringChecks.codePointCountGreaterThan(6))  // must contain 7 or more code points
                         // must contain at least 2 digits in range 0-9 (repeated digits count)
                         .addCheck(StringChecks.containsCodePointsInRange(2, "0", "9", false))
                         // must contain at least 1 of the code points in the string
                         .addCheck(StringChecks.containsCodePoints(1, "!@#$%^&*()", true))
                         .build();

  public static final ApiMapParam BILLING_ADDRESS =
    // copies already created map param, but provides different key name and display name (was actually null)
    ApiMapParam.builder(ParamNames.BILLING_ADDRESS, "Billing Address", Address.ADDRESS).build();

  /*
    An ApiCustomParam allows for returning a list of ApiMapParamCheckResult, which correspond to 
    each position in the provided array/list. This wrapper was created specifically for this case.
    It simply goes through each position in the array and makes sure that the ApiMapParam successfully 
    checks each position and returns the ApiMapParamCheckResult for that position so the user can 
    obtain the list of parameters that were successfully checked for each position.
  */
  public static final ApiCustomParam SHIPPING_ADDRESSES =
    // use checkArray* or checkList* based on the data type
    ArrayChecks.checkArrayWithMapParams(ParamNames.SHIPPING_ADDRESSES, "Shipping Addresses", Address.ADDRESS);

  /*
    Creates an ApiMapParam that will ensure valid parameters are supplied for username, password, 
    and billing_address and optionally that some shipping_addresses are supplied. 
  */
  public static final ApiMapParam CREATE_CUSTOMER_MAP_PARAM =
    ApiMapParam.rootBuilder()
               .addRequiredSingleParams(USERNAME, PASSWORD)
               .addRequiredMapParam(BILLING_ADDRESS)
               .addOptionalCustomParam(SHIPPING_ADDRESSES)
               .build();

  public CreateCustomer() {
    super(CREATE_CUSTOMER_MAP_PARAM);
  }

}

// Example running it
public class Main {

  public static void main(String[] args) {

    // this example can more or less be seen in ApiMapParamTests - with more cases
    Map<String, Object> request = new HashMap();
    request.put(CreateCustomer.ParamNames.USERNAME, "OnlyAThroughZAllowedAnd0Through9");
    request.put(CreateCustomer.ParamNames.PASSWORD, "a secure pass @ 23");

    Map<String, Object> billingAddress = new HashMap();
    billingAddress.put(Address.ParamNames.NAME, "A Company LLC");
    billingAddress.put(Address.ParamNames.LINE1, "1234 Main St.");
    billingAddress.put(Address.ParamNames.LINE2, "Suite 5");
    billingAddress.put(Address.ParamNames.CITY, "Memphis");
    billingAddress.put(Address.ParamNames.ZIP, "12345");
    billingAddress.put(Address.ParamNames.STATE, "TN");

    request.put(CreateCustomer.ParamNames.BILLING_ADDRESS, billingAddress);

    Map<String, Object>[] shippingAddresses = new Map[2];

    // first shipping address same as billing
    Map<String, Object> shippingAddress1 = new HashMap();
    shippingAddress1.put(Address.ParamNames.NAME, "A Company LLC");
    shippingAddress1.put(Address.ParamNames.LINE1, "1234 Main St.");
    shippingAddress1.put(Address.ParamNames.LINE2, "Suite 5");
    shippingAddress1.put(Address.ParamNames.CITY, "Memphis");
    shippingAddress1.put(Address.ParamNames.ZIP, "12345");
    shippingAddress1.put(Address.ParamNames.STATE, "TN");

    shippingAddresses[0] = shippingAddress1;

    Map<String, Object> shippingAddress2 = new HashMap();
    shippingAddress2.put(Address.ParamNames.NAME, "A Company LLC");
    shippingAddress2.put(Address.ParamNames.LINE1, "100 First St.");
    shippingAddress2.put(Address.ParamNames.CITY, "Nashville");
    shippingAddress2.put(Address.ParamNames.ZIP, "54321");
    shippingAddress2.put(Address.ParamNames.STATE, "TN");

    shippingAddresses[1] = shippingAddress2;

    request.put(CreateCustomer.ParamNames.SHIPPING_ADDRESSES, shippingAddresses);

    // now check it
    ApiMapParamCheckResult result = CreateCustomer.CREATE_CUSTOMER_MAP_PARAM.check(request);

    boolean status = result.successful();    // true

    Set<String> successfullyCheckedParams = 
        result.providedParamNames(); // contains: username, password, billing_address, and shipping_addresses
		// note billing address is a nested map/jsonobject
    ApiMapParamCheckResult providedBillingParams = 
        result.getMapParamCheck(CreateCustomer.ParamNames.BILLING_ADDRESS);
    // note each index of the shipping addresses array is a map/jsonobject
		// now each index of the result has the correct
    List<ApiMapParamCheckResult> providedShippingAddressParams = 
        result.getCustomParamCheck(CreateCustomer.ParamNames.SHIPPING_ADDRESSES);
    
  }

}

```
