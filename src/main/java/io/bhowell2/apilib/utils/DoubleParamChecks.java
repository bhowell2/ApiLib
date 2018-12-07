package io.bhowell2.apilib.utils;

import io.bhowell2.apilib.CheckFunc;
import io.bhowell2.apilib.CheckFuncResult;

/**
 * @author Blake Howell
 */
public class DoubleParamChecks {

    public static CheckFunc<Double> valueGreaterThan(Double d) {
        return dub -> {
            if (dub > d) {
                return CheckFuncResult.success();
            } else {
                return CheckFuncResult.failure("must be greater than " + d);
            }
        };
    }

    public static CheckFunc<Double> valueGreaterThanOrEqualTo(Double d) {
        return dub -> {
            if (dub >= d) {
                return CheckFuncResult.success();
            } else {
                return CheckFuncResult.failure("must be greater than or equal to " + d);
            }
        };
    }

    public static CheckFunc<Double> valueLessThan(Double d) {
        return dub -> {
            if (dub < d) {
                return CheckFuncResult.success();
            } else {
                return CheckFuncResult.failure("must be less than " + d);
            }
        };
    }

    public static CheckFunc<Double> valueLessThanOrEqualTo(Double d) {
        return dub -> {
            if (dub <= d) {
                return CheckFuncResult.success();
            } else {
                return CheckFuncResult.failure("must be less than or equal to " + d);
            }
        };
    }

    public static CheckFunc<Double> valueEqualTo(Double d) {
        return dub -> {
            // have to use .equals so not to compare two objects rather than double value
            if (dub.equals(d)) {
                return CheckFuncResult.success();
            } else {
                return CheckFuncResult.failure("must be equal to " + d);
            }
        };
    }



}
