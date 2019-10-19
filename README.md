# API Library
Library to aid API parameter checking. Facilitates rapidly creating a structured API where each parameter will be checked 
to ensure that it meets certain conditions. The list of successfully checked parameters will be returned so the user 
knows which parameters are valid. This library assumes that the parameters are stored in an object of type 
`Map<String, Object>`. It is probably most helpful to think of `Map` and `JsonObject` as synonymous - Java does not 
have a native `JsonObject` type, but in most every library that implements a `JsonObject` they are backed by 
`Map<String, Object>`. 

## Quick Start
An example will make things more digestible since there are a few parts to the library.
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

/*
This is a wrapper around an ob
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
    
    Set<String> successfullyCheckedParams = result.providedParamNames(); // contains: username, password, billing_address, and shipping_addresses
    ApiMapParamCheckResult providedBillingParams = result.getMapParamCheck(CreateCustomer.ParamNames.BILLING_ADDRESS);
    List<ApiMapParamCheckResult> providedShippingAddressParams = result.getCustomParamCheck(
  }

}

```

## Errors ([ApiParamError](./src/main/java/io/github/bhowell2/apilib/ApiParamError.java))
Any error that occurs when checking a parameter (of any type, and at any depty) will be returned with `ApiParamError`, 
which contains the [ApiErrorType](./src/main/java/io/github/bhowell2/apilib/ApiErrorType.java), the key name of the 
parameter that failed and the display name (will be same as key name if not specified when creating parameter). If the 
error occurred because an exception was thrown the exception will be returned - this will come with an error type of 
`cast` or `exceptional`.

### Failure Messages


#### Single Parameter Failure (non-nested)
This is the most simple failure and occurs when a parameter fails to check in the top-level/root Map. A message may 
There are a few different cases of failures that cause failure messages to be a bit difficult to handle with edge cases, 
so the most simple solution has been taken, which will 

### [ApiErrorType](./src/main/java/io/github/bhowell2/apilib/ApiErrorType.java)
Returns an informative message on why the parameter check failed.

## [ApiParamBase](./)
All parameters extend this, which requires a `keyName` and `displayName` be provided. The `keyName` is the value used 
to retrieve the parameter from `Map<String, Object>` and the `displayName` can be used to return an informative error 
message to the user (see [ApiParamError](#apiparamerror)) (or the key name can be used for this purpose too).

## [ApiCheckResultBase](./src/main/java/io/github/bhowell2/apilib/ApiCheckResultBase.java)
Each parameter 

## [ApiSingleParam](./src/main/java/io/github/bhowell2/apilib/ApiSingleParam.java)
Used to check the value of a single parameter. This has a `keyName` 

### Check
Used to check the value of a 

## [ApiParamError]()

## Testing
The testing 
