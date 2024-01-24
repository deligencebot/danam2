package com.delbot.danam.global.util;

public class CommonUtils {
  //
  private static final String FILE_EXTENSION_SEPARATOR = ".";

  public static String buildFileName(String originalFileName) {
    int fileExtensionIndex = originalFileName.lastIndexOf(FILE_EXTENSION_SEPARATOR);
    String fileName = originalFileName.substring(0, fileExtensionIndex);
    String fileExtension = originalFileName.substring(fileExtensionIndex);
    String now = String.valueOf(System.currentTimeMillis());

    return fileName + "_" + now + fileExtension;
  }
}
