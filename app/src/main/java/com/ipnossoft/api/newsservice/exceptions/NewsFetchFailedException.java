package com.ipnossoft.api.newsservice.exceptions;

public class NewsFetchFailedException extends Exception {
  public NewsFetchFailedException(String var1) {
    super(var1);
  }

  public NewsFetchFailedException(String var1, Throwable var2) {
    super(var1, var2);
  }
}
