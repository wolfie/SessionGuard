package com.github.wolfie.sessionguard.exception;

public class NonPositiveTimeSpanException extends RuntimeException {
  private static final long serialVersionUID = -2838328792672181311L;
  
  public NonPositiveTimeSpanException(final String message) {
    super(message);
  }
}
