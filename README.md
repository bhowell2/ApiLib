# API Parameter Library
Provides some default functionality for parameter checking.

## 10,000 ft. view
The library is made up of multiple parts to reach the final product. An API Version needs to be created for each version available (this could be
done by implementing [ApiVersion](src/main/java/io/bhowell2/ApiLib/ApiVersion.java) in an Enum (preferable) or creating a class for Api Versions and 
implementing the 
interface. If the latter interface is used, then it is advisable to create a class to hold each of the different API Versions and return the correct one when it is needed).

## How it works
This will give a bottom up approach, but could just as easily be read backwards to understand the way the library was designed to be used. (See
[Naming Conventions](#naming-conventions-(suggestions)) for more suggested naming to keep your API readable when extending/creating the following
classes.)

0. Create a parameter (by extending ApiParameter).
    a. Optionally, a conditional parameter can be created with multiple ApiParameters.
1. Create a list of the parameters required for a given path for a given version (e.g., 

## Naming Conventions (suggestions)
These are just suggestions and the user can, obviously, choose their own naming conventions if they so wish.
1. 