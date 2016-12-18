package io.bhowell2.ApiLib;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author Blake Howell
 */
public class GenericsTest<T> {

  List<T> list = new ArrayList<T>();
  List<Function<T, Boolean>> alist = new ArrayList<>();

  public static void main(String[] args) {

//    GenericsTest<?> genericsTest = new GenericsTest<>();
//    genericsTest.addToGenericTestList("Testing");
//    genericsTest.getList().forEach(System.out::println);
//    Function<String, Boolean> stringBooleanFunction = s -> true;
//    genericsTest.addFunctionToList(stringBooleanFunction);
//    genericsTest.addFunctionToList(s -> false);
//    genericsTest.addFunctionToList(s -> true);
//    genericsTest.addFunctionToList(s -> false);
//    genericsTest.addFunctionToList(s -> false);
//    List<Function<String, Boolean>> list = genericsTest.getAlist();
//    list.forEach(f -> System.out.println(f.apply("whattt..")));
    List<String> l = new ArrayList<>(5);
    System.out.println(l.size());
  }

  public void addToGenericTestList(T elem) {
    list.add(elem);
  }

  public void addFunctionToList(Function<T, Boolean> function) {
    alist.add(function);
  }

  public List<T> getList() {
    return list;
  }

  public List<Function<T, Boolean>> getAlist() {
    return alist;
  }
}
