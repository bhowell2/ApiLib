# API Parameter Library
A library to facilitate the creation and checking of parameters for an API.


## Api Version
An API Version needs to be created for each version available (this could be done by implementing [ApiVersion]
(src/main/java/io/bhowell2/ApiLib/ApiVersion.java) in an Enum (preferable) or creating a class for Api Versions and
implementing the interface. If the latter interface approach is used, then it is advisable to create a singleton that holds all of the possible API
Versions and retrieve them as necessary to keep from recreating them.

## [Api Parameter](./src/main/java/io/bhowell2/ApiLib/ApiParameter.java)
This is really the base of the library; everything is built on top of parameters. A builder is provided to more cleanly and descriptively create a
parameter. The parameter is given a name that will be used to retrieve the value to check. A type converter may be supplied to the ApiParameter that
will take care of converting the parameter to the required type and inserting it back in the Parameters map (this may be useful if the parameter comes
 in as a String, but should be checked as a double.)

## [Conditional Parameters](./src/main/java/io/bhowell2/ApiLib/ConditionalParameters.java)
These are simply made up of more than one ApiParameter. The main types of conditional parameters will be AND and OR conditions (i.e., AND conditions
require that ALL of the parameters are provided. OR conditions require that at least ONE of the parameters checks passes.) **It should be noted here
that an error might NOT be returned if the a Parameter is provided to an OR conditional, but does not meet the checks while another parameter is
also provided but does meet the checks.**

## [ApiPathParameters](./src/main/java/io/bhowell2/ApiLib/ApiPathParameters)
Contains the parameters for a given version for a given path. A builder ([ApiPathParametersBuilder](
./src/main/java/io/bhowell2/ApiLib/ApiPathParametersBuilder.java) is provided to more cleanly and descriptively create this object. This allows the
user to define which parameters MUST be provided (required) and which parameters are not necessarily required (optional).

## [ApiPath](./src/main/java/io/bhowell2/ApiLib/ApiPath.java)
Contains all of the ApiPathParameters for a given path. This class will make sure that the correct ApiPathParameters is chosen for the version
specified. Optionally, a path name and a path matcher may be provided to the ApiPath.

## Return Objects
"Tuple" classes have been created to return objects from each layer of the library. These, generally, return the successfully checked parameter name,
or an error tuple (which contains the parameter name and the error type). This was done to avoid having to throw errors since it is quite possible
that an API checking library could throw a lot of errors (and thus have a non-trivial impact on performance).

### Errors
To keep the return objects as simple as possible, an error will only return what the [error type](./src/main/java/io/bhowell2/ApiLib/ErrorType.java)
is, the parameter check that failed, and an error message that may contain a list of multiple parameters that were missed (in the case of a
conditional parameter multiple parameters may be listed in the error message, denoting that all must be provided for an AND param and so forth).


## TODO
1. Api version parameter conversion. Allow converters to be provided for each parameter for a given path.
2. More functions.
3.

## How it works
This will give a bottom up approach, but could just as easily be read backwards to understand the way the library was designed to be used. (See
[Naming Conventions](#naming-conventions-(suggestions)) for more suggested naming to keep your API readable when extending/creating the following
classes.)

0. Create an API Version.
1.. Create a parameter (by creating an instance of [ApiParameter](./src/main/java/io/bhowell2/ApiLib/ApiParameter), or using [ApiParameterBuilder](
./src/main/java/io/bhowell2/ApiLib/ApiParameterBuilder)).
    a. Optionally, a [ConditionalParameter](./src/main/java/io/bhowell2/ApiLib/ConditionalParameters) can be created with multiple ApiParameters.
2. Create a list of the parameters required for a given path for a given version (this is most easily done by using ApiPathParametersBuilder).
3. (Optional) Create a list of

## Naming Conventions (suggestions)
These are just suggestions and the developer may, obviously, choose their own naming conventions if they wish.
1. 