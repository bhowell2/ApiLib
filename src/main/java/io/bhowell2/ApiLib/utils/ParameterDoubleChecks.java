package io.bhowell2.ApiLib.utils;

import io.bhowell2.ApiLib.FunctionCheckTuple;

import java.util.function.Function;

/**
 * @author Blake Howell
 */
public class ParameterDoubleChecks {

    public static Function<Double, FunctionCheckTuple> valueGreaterThan(Double d) {
        return dub -> {
            if (dub > d) {
                return FunctionCheckTuple.success();
            } else {
                return FunctionCheckTuple.failure("Value not greater than " + d);
            }
        };
    }

    public static Function<Double, FunctionCheckTuple> valueGreaterThanOrEqualTo(Double d) {
        return dub -> {
            if (dub >= d) {
                return FunctionCheckTuple.success();
            } else {
                return FunctionCheckTuple.failure("Value not greater than or equal to " + d);
            }
        };
    }

    public static Function<Double, FunctionCheckTuple> valueLessThan(Double d) {
        return dub -> {
            if (dub < d) {
                return FunctionCheckTuple.success();
            } else {
                return FunctionCheckTuple.failure("Value not less than " + d);
            }
        };
    }

    public static Function<Double, FunctionCheckTuple> valueLessThanOrEqualTo(Double d) {
        return dub -> {
            if (dub <= d) {
                return FunctionCheckTuple.success();
            } else {
                return FunctionCheckTuple.failure("Value not less than or equal to " + d);
            }
        };
    }

    public static Function<Double, FunctionCheckTuple> valueEqualTo(Double d) {
        return dub -> {
            if (dub == d) {
                return FunctionCheckTuple.success();
            } else {
                return FunctionCheckTuple.failure("Value not less than or equal to " + d);
            }
        };
    }

}
