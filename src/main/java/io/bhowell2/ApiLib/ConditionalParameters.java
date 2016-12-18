package io.bhowell2.ApiLib;

/**
 * @author Blake Howell
 */
public abstract class ConditionalParameters {

  private ApiParameter[] parameters;

  public ConditionalParameters(ApiParameter... parameters) {
    this.parameters = parameters;
  }

}
