//package io.bhowell2.ApiLib;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @author Blake Howell
// */
//public class ApiArrayParameterBuilder<ArrayType, ParentParamsObj> {
//
//    public static <ArrayType, ParentParamsObj> ApiArrayParameterBuilder<ArrayType, ParentParamsObj> builder(                                                                                                            ParameterRetrievalFunction<ArrayType, ParentParamsObj> retrievalFunction) {
//        return new ApiArrayParameterBuilder<>(null, retrievalFunction);
//    }
//
//    public static <ArrayType, ParentParamsObj> ApiArrayParameterBuilder<ArrayType, ParentParamsObj> builder(String parameterName,
//                                                                                                            ParameterRetrievalFunction<ArrayType, ParentParamsObj> retrievalFunction) {
//        return new ApiArrayParameterBuilder<>(parameterName, retrievalFunction);
//    }
//
//    String parameterName;
//    boolean continueOnFailedOptional = false;
//    ParameterRetrievalFunction<ArrayType, ParentParamsObj> retrievalFunction;
//    List<ApiCustomParameters<ArrayType>> requiredCustomParams;
//    List<ApiCustomParameters<ArrayType>> optionalCustomParams;
//
//    public ApiArrayParameterBuilder(String parameterName, ParameterRetrievalFunction<ArrayType, ParentParamsObj> retrievalFunction) {
//        this.parameterName = parameterName;
//        this.retrievalFunction = retrievalFunction;
//        this.requiredCustomParams = new ArrayList<>();
//        this.optionalCustomParams = new ArrayList<>();
//    }
//
//
//    public ApiArrayParameterBuilder<ArrayType, ParentParamsObj> addRequiredCustomParameter(ApiCustomParameters<ArrayType> requiredCustomParam) {
//        this.requiredCustomParams.add(requiredCustomParam);
//        return this;
//    }
//
//    public ApiArrayParameterBuilder<ArrayType, ParentParamsObj> addOptionalCustomParameter(ApiCustomParameters<ArrayType> optionalCustomParam) {
//        this.optionalCustomParams.add(optionalCustomParam);
//        return this;
//    }
//
//    public ApiArrayParameter<ArrayType, ParentParamsObj> build() {
//        return new ApiArrayParameter<>(this.parameterName,
//                                       this.continueOnFailedOptional,
//                                       this.retrievalFunction,
//                                       this.requiredCustomParams,
//                                       this.optionalCustomParams);
//    }
//
//}
