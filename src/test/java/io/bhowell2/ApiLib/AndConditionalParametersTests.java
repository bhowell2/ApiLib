//package io.bhowell2.ApiLib;
//
//import org.junit.Test;
//
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Map;
//
//import static io.bhowell2.ApiLib.ApiParametersForTests.*;
//import static org.junit.Assert.*;
//
///**
// * Ensure that AND parameters work as expected (i.e., all parameters must be provided and pass)
// * @author Blake Howell
// */
//public class AndConditionalParametersTests {
//
//    public static AndConditionalParameters AND_CONDIT_INTEGER1_STRING1 = new AndConditionalParameters(INTEGER1, STRING1);
//    public static AndConditionalParameters AND_CONDIT_INTEGER2_STRING2 = new AndConditionalParameters(INTEGER2, STRING2);
//
//
//    @Test
//    public void shouldPass() {
//        Map<String, Object> requestParams = new HashMap<>();
//        requestParams.put(INTEGER1.getParameterName(), 99);
//        requestParams.put(ApiParametersForTests.STRING1.getParameterName(), "passthis");
//        ConditionalCheckTuple checkTuple = AND_CONDIT_INTEGER1_STRING1.check(requestParams);
//        assertTrue(checkTuple.successful());
//        assertTrue(checkTuple.parameterNames.contains(INTEGER1.getParameterName()));
//        assertTrue(checkTuple.parameterNames.contains(STRING1.getParameterName()));
//    }
//
//    @Test
//    public void shouldFailFromMissingParameter() {
//        Map<String, Object> requestParams = new HashMap<>();
//        requestParams.put(ApiParametersForTests.STRING1.getParameterName(), "passthis");
//        ConditionalCheckTuple checkTuple = AND_CONDIT_INTEGER1_STRING1.check(requestParams);
//        assertTrue(checkTuple.failed());
//        assertEquals(checkTuple.errorTuple.errorType, ErrorType.MISSING_PARAMETER);
//        assertEquals(checkTuple.errorTuple.parameterName, INTEGER1.getParameterName());
//        //
//        Map<String, Object> emptyRequestParams = new HashMap<>();
//        ConditionalCheckTuple checkTupleEmpty = AND_CONDIT_INTEGER1_STRING1.check(emptyRequestParams);
//        assertTrue(checkTupleEmpty.failed());
//        assertEquals(checkTupleEmpty.errorTuple.errorType, ErrorType.MISSING_PARAMETER);
//        // either Integer1 or String1 will be missing, whichever is checked first
//        boolean oneNameMatches = false;
//        for (String s : AND_CONDIT_INTEGER1_STRING1.allParameterNames) {
//            if (checkTupleEmpty.errorTuple.parameterName.equals(s)) {
//                oneNameMatches = true;
//            }
//        }
//        assertTrue(oneNameMatches);
//    }
//
//    @Test
//    public void shouldFailFromInvalidParameter() {
//        Map<String, Object> requestParams = new HashMap<>();
//        requestParams.put(INTEGER1.getParameterName(), 101);
//        requestParams.put(ApiParametersForTests.STRING1.getParameterName(), "passthis");
//        ConditionalCheckTuple checkTuple = AND_CONDIT_INTEGER1_STRING1.check(requestParams);
//        assertTrue(checkTuple.failed());
//        assertEquals(checkTuple.errorTuple.errorType, ErrorType.INVALID_PARAMETER);
//        assertEquals(checkTuple.errorTuple.parameterName, INTEGER1.getParameterName());
//    }
//
//
//    // Testing nested AND parameters
//
//    // requires that STRING2 AND (STRING1 AND INTEGER1) be provided
//    public static AndConditionalParameters NESTED_AND_CONDIT_PARAMS = new AndConditionalParameters(new ApiParameter<?>[]{STRING2},
//                                                                                                   new ApiConditionalParameters[]{AND_CONDIT_INTEGER1_STRING1});
//
//    @Test
//    public void shouldFailFromNestedMissingParameters() {
//        // empty params
//        Map<String, Object> emptyRequestParams = new HashMap<>();
//        ConditionalCheckTuple checkTupleEmpty = NESTED_AND_CONDIT_PARAMS.check(emptyRequestParams);
//        assertTrue(checkTupleEmpty.failed());
//        assertEquals(checkTupleEmpty.errorTuple.errorType, ErrorType.MISSING_PARAMETER);
//        // either Integer1, String1, or String2 will be missing, whichever is checked first
//        boolean oneNameMatches = false;
//        for (String s : NESTED_AND_CONDIT_PARAMS.allParameterNames) {
//            if (checkTupleEmpty.errorTuple.parameterName.equals(s)) {
//                oneNameMatches = true;
//            }
//        }
//        assertTrue(oneNameMatches);
//
//        // missing nested AND parameters (INTEGER1 or STRING1)
//        Map<String, Object> requestParams = new HashMap<>();
//        requestParams.put(STRING2.getParameterName(), "passing");
//        ConditionalCheckTuple checkTuple = NESTED_AND_CONDIT_PARAMS.check(requestParams);
//        assertTrue(checkTuple.failed());
//        assertEquals(checkTuple.errorTuple.errorType, ErrorType.MISSING_PARAMETER);
//        boolean oneNameMatches2 = false;     // reset
//        for (String s : AND_CONDIT_INTEGER1_STRING1.allParameterNames) {
//            if (checkTuple.errorTuple.parameterName.equals(s)) {
//                oneNameMatches2 = true;
//            }
//        }
//        assertTrue(oneNameMatches2);
//
//        // missing INTEGER1
//        Map<String, Object> requestParams2 = new HashMap<>();
//        requestParams2.put(STRING2.getParameterName(), "passing");
//        requestParams2.put(STRING1.getParameterName(), "passing");
//        ConditionalCheckTuple checkTuple2 = NESTED_AND_CONDIT_PARAMS.check(requestParams2);
//        assertTrue(checkTuple2.failed());
//        assertEquals(checkTuple2.errorTuple.errorType, ErrorType.MISSING_PARAMETER);
//        assertEquals(checkTuple2.errorTuple.parameterName, INTEGER1.getParameterName());
//
//        // missing STRING1
//        Map<String, Object> requestParams3 = new HashMap<>();
//        requestParams3.put(STRING2.getParameterName(), "passing");
//        requestParams3.put(INTEGER1.getParameterName(), 99);
//        ConditionalCheckTuple checkTuple3 = NESTED_AND_CONDIT_PARAMS.check(requestParams3);
//        assertTrue(checkTuple3.failed());
//        assertEquals(checkTuple3.errorTuple.errorType, ErrorType.MISSING_PARAMETER);
//        assertEquals(checkTuple3.errorTuple.parameterName, STRING1.getParameterName());
//
//        // missing STRING2
//        Map<String, Object> requestParams4 = new HashMap<>();
//        requestParams4.put(STRING1.getParameterName(), "passing");
//        requestParams4.put(INTEGER1.getParameterName(), 99);
//        ConditionalCheckTuple checkTuple4 = NESTED_AND_CONDIT_PARAMS.check(requestParams4);
//        assertTrue(checkTuple4.failed());
//        assertEquals(checkTuple4.errorTuple.errorType, ErrorType.MISSING_PARAMETER);
//        assertEquals(checkTuple4.errorTuple.parameterName, STRING2.getParameterName());
//
//    }
//
//    @Test
//    public void shouldFailFromNestedInvalidParameters() {
//        // invalid STRING1
//        Map<String, Object> requestParams = new HashMap<>();
//        requestParams.put(STRING1.getParameterName(), "");
//        requestParams.put(INTEGER1.getParameterName(), 99);
//        requestParams.put(STRING2.getParameterName(), "passing");
//        ConditionalCheckTuple checkTuple = NESTED_AND_CONDIT_PARAMS.check(requestParams);
//        assertTrue(checkTuple.failed());
//        assertEquals(checkTuple.errorTuple.errorType, ErrorType.INVALID_PARAMETER);
//        assertEquals(checkTuple.errorTuple.parameterName, STRING1.getParameterName());
//
//        // invalid STRING2
//        Map<String, Object> requestParams2 = new HashMap<>();
//        requestParams2.put(STRING1.getParameterName(), "passing");
//        requestParams2.put(INTEGER1.getParameterName(), 99);
//        requestParams2.put(STRING2.getParameterName(), "aaaa");
//        ConditionalCheckTuple checkTuple2 = NESTED_AND_CONDIT_PARAMS.check(requestParams2);
//        assertTrue(checkTuple2.failed());
//        assertEquals(checkTuple2.errorTuple.errorType, ErrorType.INVALID_PARAMETER);
//        assertEquals(checkTuple2.errorTuple.parameterName, STRING2.getParameterName());
//
//        // invalid INTEGER1
//        Map<String, Object> requestParams3 = new HashMap<>();
//        requestParams3.put(STRING1.getParameterName(), "passing");
//        requestParams3.put(INTEGER1.getParameterName(), 101);
//        requestParams3.put(STRING2.getParameterName(), "passing");
//        ConditionalCheckTuple checkTuple3 = NESTED_AND_CONDIT_PARAMS.check(requestParams3);
//        assertTrue(checkTuple3.failed());
//        assertEquals(checkTuple3.errorTuple.errorType, ErrorType.INVALID_PARAMETER);
//        assertEquals(checkTuple3.errorTuple.parameterName, INTEGER1.getParameterName());
//    }
//
//    @Test
//    public void shouldPassNestedParameters() {
//        Map<String, Object> requestParams = new HashMap<>();
//        requestParams.put(STRING1.getParameterName(), "passing");
//        requestParams.put(INTEGER1.getParameterName(), 99);
//        requestParams.put(STRING2.getParameterName(), "passing");
//        ConditionalCheckTuple checkTuple = NESTED_AND_CONDIT_PARAMS.check(requestParams);
//        assertTrue(checkTuple.successful());
//        int countParamNames = 0;
//        for (String s : Arrays.asList(STRING1.getParameterName(), STRING2.getParameterName(), INTEGER1.getParameterName())) {
//            if (checkTuple.parameterNames.contains(s)) {
//                countParamNames++;
//            }
//        }
//        assertEquals(3, countParamNames);
//    }
//
//    // 2 AND parameters and an ApiParameter (i.e., STRING3 && (STRING2 && INTEGER2) && (INTEGER1 STRING1))
//    public static AndConditionalParameters DOUBLE_NESTED_AND_ADDITIONAL_CONDIT_PARAMS = new AndConditionalParameters(new ApiParameter[]{STRING3},
//                                                                                                                     new
//                                                                                                                        ApiConditionalParameters[]{AND_CONDIT_INTEGER1_STRING1,
//                                                                                                                                                   AND_CONDIT_INTEGER2_STRING2});
//
//    @Test
//    public void shouldFailFromDoubleNesterdMissingParameters() {
//        // empty params
//        Map<String, Object> emptyRequestParams = new HashMap<>();
//        ConditionalCheckTuple checkTupleEmpty = DOUBLE_NESTED_AND_ADDITIONAL_CONDIT_PARAMS.check(emptyRequestParams);
//        assertTrue(checkTupleEmpty.failed());
//        assertEquals(checkTupleEmpty.errorTuple.errorType, ErrorType.MISSING_PARAMETER);
//        // either Integer1, String1, or String2 will be missing, whichever is checked first
//        boolean oneNameMatches = false;
//        for (String s : DOUBLE_NESTED_AND_ADDITIONAL_CONDIT_PARAMS.allParameterNames) {
//            if (checkTupleEmpty.errorTuple.parameterName.equals(s)) {
//                oneNameMatches = true;
//            }
//        }
//        assertTrue(oneNameMatches);
//
//        // missing nested AND parameters (INTEGER1 or STRING1)
//        Map<String, Object> requestParams1 = new HashMap<>();
//        requestParams1.put(STRING3.getParameterName(), "passing");
//        requestParams1.put(STRING2.getParameterName(), "passing");
//        requestParams1.put(INTEGER1.getParameterName(), 55);
//        ConditionalCheckTuple checkTuple = DOUBLE_NESTED_AND_ADDITIONAL_CONDIT_PARAMS.check(requestParams1);
//        assertTrue(checkTuple.failed());
//        assertEquals(checkTuple.errorTuple.errorType, ErrorType.MISSING_PARAMETER);
//        boolean oneNameMatches1 = false;     // reset
//        // depends what is checked first, but could be missing STRING1 or INTEGER2, so must check both (2nd for loop)
//        for (String s : AND_CONDIT_INTEGER1_STRING1.allParameterNames) {
//            if (checkTuple.errorTuple.parameterName.equals(s)) {
//                oneNameMatches1 = true;
//            }
//        }
//        if (!oneNameMatches1) {
//            for (String s : AND_CONDIT_INTEGER2_STRING2.allParameterNames) {
//                if (checkTuple.errorTuple.parameterName.equals(s)) {
//                    oneNameMatches1 = true;
//                }
//            }
//        }
//        assertTrue(oneNameMatches1);
//
//
//        // missing STRING3 (non nested param)
//        Map<String, Object> requestParams2 = new HashMap<>();
//        requestParams2.put(STRING2.getParameterName(), "passing");
//        requestParams2.put(INTEGER2.getParameterName(), 101);
//        requestParams2.put(STRING1.getParameterName(), "passing");
//        requestParams2.put(INTEGER1.getParameterName(), 55);
//        ConditionalCheckTuple checkTuple2 = DOUBLE_NESTED_AND_ADDITIONAL_CONDIT_PARAMS.check(requestParams2);
//        assertTrue(checkTuple2.failed());
//        assertEquals(checkTuple2.errorTuple.errorType, ErrorType.MISSING_PARAMETER);
//        assertEquals(checkTuple2.errorTuple.parameterName, STRING3.getParameterName());
//
//        // missing STRING1 (one of nested conditionals)
//        Map<String, Object> requestParams3 = new HashMap<>();
//        requestParams3.put(STRING3.getParameterName(), "passing");
//        requestParams3.put(STRING2.getParameterName(), "passing");
//        requestParams3.put(INTEGER1.getParameterName(), 55);
//        requestParams3.put(INTEGER2.getParameterName(), 101);
//        ConditionalCheckTuple checkTuple3 = DOUBLE_NESTED_AND_ADDITIONAL_CONDIT_PARAMS.check(requestParams3);
//        assertTrue(checkTuple3.failed());
//        assertEquals(checkTuple3.errorTuple.errorType, ErrorType.MISSING_PARAMETER);
//        assertEquals(checkTuple3.errorTuple.parameterName, STRING1.getParameterName());
//    }
//
//    @Test
//    public void shouldFailFromDoubleNestedInvalidParameters() {
//        // invalid STRING3
//        Map<String, Object> requestParams = new HashMap<>();
//        requestParams.put(STRING3.getParameterName(), "");
//        requestParams.put(STRING2.getParameterName(), "passing");
//        requestParams.put(INTEGER2.getParameterName(), 101);
//        requestParams.put(STRING1.getParameterName(), "passing");
//        requestParams.put(INTEGER1.getParameterName(), 55);
//        ConditionalCheckTuple checkTuple = DOUBLE_NESTED_AND_ADDITIONAL_CONDIT_PARAMS.check(requestParams);
//        assertTrue(checkTuple.failed());
//        assertEquals(checkTuple.errorTuple.errorType, ErrorType.INVALID_PARAMETER);
//        assertEquals(checkTuple.errorTuple.parameterName, STRING3.getParameterName());
//
//        // invalid STRING2
//        Map<String, Object> requestParams2 = new HashMap<>();
//        requestParams2.put(STRING3.getParameterName(), "passing");
//        requestParams2.put(STRING2.getParameterName(), "");
//        requestParams2.put(INTEGER2.getParameterName(), 101);
//        requestParams2.put(STRING1.getParameterName(), "passing");
//        requestParams2.put(INTEGER1.getParameterName(), 55);
//        ConditionalCheckTuple checkTuple2 = DOUBLE_NESTED_AND_ADDITIONAL_CONDIT_PARAMS.check(requestParams2);
//        assertTrue(checkTuple2.failed());
//        assertEquals(checkTuple2.errorTuple.errorType, ErrorType.INVALID_PARAMETER);
//        assertEquals(checkTuple2.errorTuple.parameterName, STRING2.getParameterName());
//
//        // invalid INTEGER1
//        Map<String, Object> requestParams3 = new HashMap<>();
//        requestParams3.put(STRING3.getParameterName(), "passing");
//        requestParams3.put(STRING2.getParameterName(), "passing");
//        requestParams3.put(INTEGER2.getParameterName(), 101);
//        requestParams3.put(STRING1.getParameterName(), "passing");
//        requestParams3.put(INTEGER1.getParameterName(), 101);
//        ConditionalCheckTuple checkTuple3 = DOUBLE_NESTED_AND_ADDITIONAL_CONDIT_PARAMS.check(requestParams3);
//        assertTrue(checkTuple3.failed());
//        assertEquals(checkTuple3.errorTuple.errorType, ErrorType.INVALID_PARAMETER);
//        assertEquals(checkTuple3.errorTuple.parameterName, INTEGER1.getParameterName());
//    }
//
//    @Test
//    public void shouldPassDoubleNestedParameters() {
//        Map<String, Object> requestParams = new HashMap<>();
//        requestParams.put(STRING3.getParameterName(), "passing");
//        requestParams.put(STRING2.getParameterName(), "passing");
//        requestParams.put(INTEGER2.getParameterName(), 101);
//        requestParams.put(STRING1.getParameterName(), "passing");
//        requestParams.put(INTEGER1.getParameterName(), 55);
//        ConditionalCheckTuple checkTuple = DOUBLE_NESTED_AND_ADDITIONAL_CONDIT_PARAMS.check(requestParams);
//        assertTrue(checkTuple.successful());
//    }
//
//    public static AndConditionalParameters DOUBLE_NESTED_CONDIT_PARAMS = new AndConditionalParameters(null, new ApiConditionalParameters[]{AND_CONDIT_INTEGER1_STRING1,
//                                                                                                                                                          AND_CONDIT_INTEGER2_STRING2});
//
//    @Test
//    public void onlyNestedParamsShouldPass() {
//        Map<String, Object> requestParams = new HashMap<>();
//        requestParams.put(STRING2.getParameterName(), "passing");
//        requestParams.put(INTEGER2.getParameterName(), 101);
//        requestParams.put(STRING1.getParameterName(), "passing");
//        requestParams.put(INTEGER1.getParameterName(), 55);
//        ConditionalCheckTuple checkTuple = DOUBLE_NESTED_CONDIT_PARAMS.check(requestParams);
//        assertTrue(checkTuple.successful());
//    }
//
//    @Test
//    public void shouldFailOnlyNestedMissingParam() {
//        // missing STRING1
//        Map<String, Object> requestParams = new HashMap<>();
//        requestParams.put(STRING2.getParameterName(), "passing");
//        requestParams.put(INTEGER2.getParameterName(), 101);
//        requestParams.put(INTEGER1.getParameterName(), 55);
//        ConditionalCheckTuple checkTuple = DOUBLE_NESTED_CONDIT_PARAMS.check(requestParams);
//        assertTrue(checkTuple.failed());
//        assertEquals(checkTuple.errorTuple.errorType, ErrorType.MISSING_PARAMETER);
//        assertEquals(checkTuple.errorTuple.parameterName, STRING1.getParameterName());
//    }
//
//    @Test
//    public void shouldFailOnlyNestedInvalidParam(){
//        // missing STRING1
//        Map<String, Object> requestParams = new HashMap<>();
//        requestParams.put(STRING1.getParameterName(), "");
//        requestParams.put(STRING2.getParameterName(), "passing");
//        requestParams.put(INTEGER2.getParameterName(), 101);
//        requestParams.put(INTEGER1.getParameterName(), 55);
//        ConditionalCheckTuple checkTuple = DOUBLE_NESTED_CONDIT_PARAMS.check(requestParams);
//        assertTrue(checkTuple.failed());
//        assertEquals(checkTuple.errorTuple.errorType, ErrorType.INVALID_PARAMETER);
//        assertEquals(checkTuple.errorTuple.parameterName, STRING1.getParameterName());
//    }
//
//}
