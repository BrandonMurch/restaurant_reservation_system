package com.brandon.restaurant_reservation_system.bookings.services;

import com.brandon.restaurant_reservation_system.bookings.exceptions.BookingRequestFormatException;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.errors.ApiError;
import com.brandon.restaurant_reservation_system.errors.ApiSubError;
import com.brandon.restaurant_reservation_system.errors.ValidationError;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;

public class BookingValidationService {

  private static void setDateUsingStartTime(Booking booking) {
    booking.setDate(booking.getStartTime().toLocalDate());
  }

  private static String getExampleDateTime() {
    return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
  }

  private static Optional<ValidationError> validateStartTime(LocalDateTime time) {
    if (time == null) {
      return Optional.of(
          new ValidationError("Booking", "Start time", null, "Time must be " +
              "formatted " + getExampleDateTime()));
    } else if (time.isBefore(LocalDateTime.now())) {
      return Optional.of(
          new ValidationError("Booking", "Start time", time.toString(), "Time must be" +
              " " +
              "in the future"));
    }

    return Optional.empty();
  }

  private static Optional<ValidationError> validateEndTime(LocalDateTime start,
      LocalDateTime end) {
    if (start.isAfter(end) || start.isEqual(end)) {
      return Optional.of(
          new ValidationError("Booking", "End Time", end, "End time must be after " +
              "start " +
              "time;"));
    }

    return Optional.empty();
  }

  private static Optional<ValidationError> validatePartySize(Integer partySize) {
    if (partySize == null || partySize <= 0) {
      return Optional.of(
          new ValidationError("Booking", "Party size", partySize, "Party size must be" +
              " greater than zero"));
    }
    return Optional.empty();

  }

  private static ApiError buildApiError(List<ApiSubError> errors) {
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
    apiError.setSubErrors(errors);
    return apiError;
  }

  private static List<ApiSubError> validateFields(Booking booking) {
    List<ApiSubError> subErrors = new ArrayList<>();

    validateStartTime(booking.getStartTime()).ifPresent(subErrors::add);

    if (booking.getEndTime() != null && booking.getStartTime() != null) {
      validateEndTime(booking.getStartTime(), booking.getEndTime()).ifPresent(subErrors::add);

    }

    validatePartySize(booking.getPartySize()).ifPresent(subErrors::add);

    return subErrors;
  }

  public static void validateBooking(Booking booking) {

    List<ApiSubError> subErrors = validateFields(booking);

    if (subErrors.size() > 0) {
      throw new BookingRequestFormatException(buildApiError(subErrors));
    }

    setDateUsingStartTime(booking);
  }
}
