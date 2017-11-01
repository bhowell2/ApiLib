package io.bhowell2.ApiLib.utils;

import io.bhowell2.ApiLib.CheckFunction;
import io.bhowell2.ApiLib.FunctionCheckTuple;

/**
 * @author Blake Howell
 */
public class ParameterIntegerChecks {

    public static CheckFunction<Integer> valueGreaterThan(Integer i) {
        return n -> {
            if (n > i) {
                return FunctionCheckTuple.success();
            } else {
                return FunctionCheckTuple.failure("Value not greater than " + i);
            }
        };
    }

    public static CheckFunction<Integer> valueGreaterThanOrEqualTo(Integer i) {
        return n -> {
            if (n >= i) {
                return FunctionCheckTuple.success();
            } else {
                return FunctionCheckTuple.failure("Value not greater than or equal to " + i);
            }
        };
    }

    public static CheckFunction<Integer> valueLessThan(Integer i) {
        return n -> {
            if (n < i) {
                return FunctionCheckTuple.success();
            } else {
                return FunctionCheckTuple.failure("Value not less than " + i);
            }
        };
    }

    public static CheckFunction<Integer> valueLessThanOrEqualTo(Integer i) {
        return n -> {
            if (n <= i) {
                return FunctionCheckTuple.success();
            } else {
                return FunctionCheckTuple.failure("Value not less than or equal to " + i);
            }
        };
    }

    public static CheckFunction<Integer> valueEqualTo(Integer i) {
        return n -> {
            if (n.equals(i)) {
                return FunctionCheckTuple.success();
            } else {
                return FunctionCheckTuple.failure("Value not equal to " + i);
            }
        };
    }


}
