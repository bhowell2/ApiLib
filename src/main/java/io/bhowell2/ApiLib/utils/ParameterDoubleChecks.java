package io.bhowell2.ApiLib.utils;

import io.bhowell2.ApiLib.CheckFunction;
import io.bhowell2.ApiLib.CheckFunctionTuple;

/**
 * @author Blake Howell
 */
public class ParameterDoubleChecks {

    public static final CheckFunction<Double> valueGreaterThan(Double d) {
        return dub -> {
            if (dub > d) {
                return CheckFunctionTuple.success();
            } else {
                return CheckFunctionTuple.failure("Value not greater than " + d);
            }
        };
    }

    public static final CheckFunction<Double> valueGreaterThanOrEqualTo(Double d) {
        return dub -> {
            if (dub >= d) {
                return CheckFunctionTuple.success();
            } else {
                return CheckFunctionTuple.failure("Value not greater than or equal to " + d);
            }
        };
    }

    public static final CheckFunction<Double> valueLessThan(Double d) {
        return dub -> {
            if (dub < d) {
                return CheckFunctionTuple.success();
            } else {
                return CheckFunctionTuple.failure("Value not less than " + d);
            }
        };
    }

    public static final CheckFunction<Double> valueLessThanOrEqualTo(Double d) {
        return dub -> {
            if (dub <= d) {
                return CheckFunctionTuple.success();
            } else {
                return CheckFunctionTuple.failure("Value not less than or equal to " + d);
            }
        };
    }

    public static final CheckFunction<Double> valueEqualTo(Double d) {
        return dub -> {
            // have to use .equals so not to compare two objects rather than double value
            if (dub.equals(d)) {
                return CheckFunctionTuple.success();
            } else {
                return CheckFunctionTuple.failure("Value not less than or equal to " + d);
            }
        };
    }

}
