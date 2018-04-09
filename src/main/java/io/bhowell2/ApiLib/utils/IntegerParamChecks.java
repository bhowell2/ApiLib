package io.bhowell2.ApiLib.utils;

import io.bhowell2.ApiLib.CheckFunc;
import io.bhowell2.ApiLib.CheckFuncResult;

/**
 * @author Blake Howell
 */
public class IntegerParamChecks {

    public static CheckFunc<Integer> valueGreaterThan(Integer i) {
        return n -> {
            if (n > i) {
                return CheckFuncResult.success();
            } else {
                return CheckFuncResult.failure("must be greater than " + i);
            }
        };
    }

    public static CheckFunc<Integer> valueGreaterThanOrEqualTo(Integer i) {
        return n -> {
            if (n >= i) {
                return CheckFuncResult.success();
            } else {
                return CheckFuncResult.failure("must be greater than or equal to " + i);
            }
        };
    }

    public static CheckFunc<Integer> valueLessThan(Integer i) {
        return n -> {
            if (n < i) {
                return CheckFuncResult.success();
            } else {
                return CheckFuncResult.failure("must be less than " + i);
            }
        };
    }

    public static CheckFunc<Integer> valueLessThanOrEqualTo(Integer i) {
        return n -> {
            if (n <= i) {
                return CheckFuncResult.success();
            } else {
                return CheckFuncResult.failure("must be less than or equal to " + i);
            }
        };
    }

    public static CheckFunc<Integer> valueEqualTo(Integer i) {
        return n -> {
            if (n.equals(i)) {
                return CheckFuncResult.success();
            } else {
                return CheckFuncResult.failure("must be equal to " + i);
            }
        };
    }


}
