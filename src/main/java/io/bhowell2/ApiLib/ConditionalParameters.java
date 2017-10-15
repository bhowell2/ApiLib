package io.bhowell2.ApiLib;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Base class for all conditional parameters.
 * @author Blake Howell
 */
public abstract class ConditionalParameters {

  final ConditionalParameters[] conditionalParameters;
  final ApiParameter<?>[] apiParameters;
  final List<String> allParameterNames;

  public ConditionalParameters(ApiParameter<?>[] apiParameters, ConditionalParameters[] conditionalParameters) {
    if (apiParameters == null) {
      this.apiParameters = new ApiParameter[0];
    } else {
      this.apiParameters = apiParameters;
    }
    if (conditionalParameters == null) {
      this.conditionalParameters = new ConditionalParameters[0];
    } else {
      this.conditionalParameters = conditionalParameters;
    }
    this.allParameterNames = generateAllParameterNames();
  }

  public abstract ConditionalCheckTuple check(Map<String, Object> parameters);

  /**
   * Used to determine how many parameters are used to makeup the underlying conditional parameter.
   * @return size of all possible checks (so when checking against
   */
  protected int getParameterSize() {
    int size = apiParameters.length;
    for (ConditionalParameters conditionalParams : conditionalParameters) {
      size += conditionalParams.getParameterSize();
    }
    return size;
  }

  private List<String> generateAllParameterNames() {
    List<String> allParameterNames = new ArrayList<>(apiParameters.length + conditionalParameters.length * 2);
    for (ApiParameter<?> param : apiParameters) {
      allParameterNames.add(param.getParameterName());
    }
    for (ConditionalParameters params : conditionalParameters) {
      allParameterNames.addAll(params.allParameterNames);
    }
    return allParameterNames;
  }

}
