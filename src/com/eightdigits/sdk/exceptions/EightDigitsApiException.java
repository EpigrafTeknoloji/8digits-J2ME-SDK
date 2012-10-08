package com.eightdigits.sdk.exceptions;

public class EightDigitsApiException extends Exception {

  int errorCode;

  public EightDigitsApiException() {
    // TODO Auto-generated constructor stub
  }

  public EightDigitsApiException(int errorCode, String message) {
    super(message);
    this.setErrorCode(errorCode);
  }

  public int getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(int errorCode) {
    this.errorCode = errorCode;
  }

}
