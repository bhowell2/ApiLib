package io.bhowell2.ApiLib.exceptions;

import io.bhowell2.ApiLib.ApiVersion;

/**
 * @author Blake Howell
 */
public class UnsupportedApiVersionForPath extends IllegalArgumentException {

  public UnsupportedApiVersionForPath(ApiVersion version) {
    super("The API Version specified (" + version + ") is not valid for the path.");
  }

}
