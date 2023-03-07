package com.energy.battery.exceptions;

public class InvalidDataException extends RuntimeException {

  private static final long serialVersionUID = -1030433630014239372L;

  private final String message;

  public InvalidDataException(String message) {
    super(message);
    this.message = message;
  }

  @Override
  public String getMessage() {
    return message;
  }

}
