package io.bhowell2.ApiLib.utils;

import io.bhowell2.ApiLib.CheckFunction;
import io.bhowell2.ApiLib.FunctionCheckTuple;

/**
 * @author Blake Howell
 */
public class ParameterDoubleChecks {

    public static CheckFunction<Double> valueGreaterThan(Double d) {
        return dub -> {
            if (dub > d) {
                return FunctionCheckTuple.success();
            } else {
                return FunctionCheckTuple.failure("Value not greater than " + d);
            }
        };
    }

    public static CheckFunction<Double> valueGreaterThanOrEqualTo(Double d) {
        return dub -> {
            if (dub >= d) {
                return FunctionCheckTuple.success();
            } else {
                return FunctionCheckTuple.failure("Value not greater than or equal to " + d);
            }
        };
    }

    public static CheckFunction<Double> valueLessThan(Double d) {
        return dub -> {
            if (dub < d) {
                return FunctionCheckTuple.success();
            } else {
                return FunctionCheckTuple.failure("Value not less than " + d);
            }
        };
    }

    public static CheckFunction<Double> valueLessThanOrEqualTo(Double d) {
        return dub -> {
            if (dub <= d) {
                return FunctionCheckTuple.success();
            } else {
                return FunctionCheckTuple.failure("Value not less than or equal to " + d);
            }
        };
    }

    public static CheckFunction<Double> valueEqualTo(Double d) {
        return dub -> {
            // have to use .equals so not to compare two objects rather than double value
            if (dub.equals(d)) {
                return FunctionCheckTuple.success();
            } else {
                return FunctionCheckTuple.failure("Value not less than or equal to " + d);
            }
        };
    }

}