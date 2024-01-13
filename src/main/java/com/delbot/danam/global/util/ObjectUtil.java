package com.delbot.danam.global.util;

import java.util.ArrayList;
import java.util.List;

public class ObjectUtil {
  //
  public static List<String> convertToListString(Object object) {
    if (object instanceof List<?>) {
      List<?> list = (List<?>) object;
      List<String> stringList = new ArrayList<>();
      for (Object element : list) {
        stringList.add(String.valueOf(element));
      }
      return stringList;
    } else {
        return new ArrayList<>();
    }
  }
}
