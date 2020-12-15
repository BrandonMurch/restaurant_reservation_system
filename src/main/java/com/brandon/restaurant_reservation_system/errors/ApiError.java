package com.brandon.restaurant_reservation_system.errors;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import org.springframework.http.HttpStatus;

public class ApiError {

  private HttpStatus status;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@hh:mm" +
      ":ss")
  private LocalDateTime timestamp;
  private String message;
  private List<ApiSubError> subErrors = new LinkedList<>();

  public ApiError() {
    timestamp = LocalDateTime.now();
  }

  public ApiError(HttpStatus status) {
    this();
    this.status = status;
    this.message = "Unexpected Error";
  }

  public ApiError(HttpStatus status,
      Throwable ex) {
    this();
    this.status = status;
    this.message = ex.getLocalizedMessage();
  }

  public ApiError(HttpStatus status, String message) {
    this();
    this.status = status;
    this.message = message;
  }

  public ApiError(HttpStatus status, String message,
      Throwable ex) {
    this();
    this.status = status;
    this.message = message;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public void setStatus(HttpStatus status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public List<ApiSubError> getSubErrors() {
    return subErrors;
  }

  public void setSubErrors(
      List<ApiSubError> subErrors) {
    this.subErrors = subErrors;
  }

  public void addSubError(ApiSubError subError) {
    this.subErrors.add(subError);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ApiError)) {
      return false;
    }
    ApiError apiError = (ApiError) o;
    return getStatus() == apiError.getStatus() &&
        Objects.equals(getTimestamp(), apiError.getTimestamp()) &&
        Objects.equals(getMessage(), apiError.getMessage()) &&
        Objects.equals(getSubErrors(), apiError.getSubErrors());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getStatus(), getTimestamp(), getMessage(),
        getSubErrors());
  }
}
