package io.bhowell2.ApiLib.utils;

import io.bhowell2.ApiLib.CheckFunction;
import io.bhowell2.ApiLib.CheckFunctionTuple;

/**
 * @author Blake Howell
 */
public class ParameterIntegerChecks {

    public static CheckFunction<Integer> valueGreaterThan(Integer i) {
        return n -> {
            if (n > i) {
                return CheckFunctionTuple.success();
            } else {
                return CheckFunctionTuple.failure("must be greater than " + i);
            }
        };
    }

    public static CheckFunction<Integer> valueGreaterThanOrEqualTo(Integer i) {
        return n -> {
            if (n >= i) {
                return CheckFunctionTuple.success();
            } else {
                return CheckFunctionTuple.failure("must be greater than or equal to " + i);
            }
        };
    }

    public static CheckFunction<Integer> valueLessThan(Integer i) {
        return n -> {
            if (n < i) {
                return CheckFunctionTuple.success();
            } else {
                return CheckFunctionTuple.failure("must be less than " + i);
            }
        };
    }

    public static CheckFunction<Integer> valueLessThanOrEqualTo(Integer i) {
        return n -> {
            if (n <= i) {
                return CheckFunctionTuple.success();
            } else {
                return CheckFunctionTuple.failure("must be less than or equal to " + i);
            }
        };
    }

    public static CheckFunction<Integer> valueEqualTo(Integer i) {
        return n -> {
            if (n.equals(i)) {
                return CheckFunctionTuple.success();
            } else {
                return CheckFunctionTuple.failure("must be equal to " + i);
            }
        };
    }


}
