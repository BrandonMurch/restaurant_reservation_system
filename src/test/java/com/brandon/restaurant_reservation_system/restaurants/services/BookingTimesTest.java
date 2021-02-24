///USE THIS TO UPDATE HoursOfOperation tests
//
//package com.brandon.restaurant_reservation_system.restaurants.services;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//import com.brandon.restaurant_reservation_system.restaurants.data.HoursOfOperation;
//import com.brandon.restaurant_reservation_system.restaurants.model.TimePair;
//import java.time.DayOfWeek;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//@ExtendWith(MockitoExtension.class)
//class BookingTimesTest {
//
//  TimePair openClosePair;
//  @Mock
//  private HoursOfOperation hoursOfOperation;
//  @InjectMocks
//  private BookingTimes bookingTimes;
//
//  @BeforeEach
//  void setUp() {
//    MockitoAnnotations.initMocks(this);
//    openClosePair = new TimePair(
//        LocalTime.of(15, 0, 0),
//        LocalTime.of(20, 0, 0)
//    );
//  }
//
//  @Test
//  void settingUpFixedTimes() {
//    Mockito
//        .when(hoursOfOperation.isOpen(Mockito.any(LocalDate.class)))
//        .thenReturn(true);
//    List<LocalTime> list = new ArrayList<>();
//    list.add(LocalTime.now());
//    list.add(LocalTime.now().plusHours(1));
//    list.add(LocalTime.now().plusHours(1));
//
//    bookingTimes.setBookingTimes(list);
//
//    List<LocalTime> result = bookingTimes.getAll(LocalDate.now());
//
//    assertEquals(list, result);
//  }
//
//  @Test
//  void settingUpTimeIntervals() {
//    Mockito
//        .when(hoursOfOperation.isOpen(Mockito.any(DayOfWeek.class)))
//        .thenReturn(true);
//    Mockito
//        .when(hoursOfOperation.isOpen(Mockito.any(LocalDate.class)))
//        .thenReturn(true);
//    Mockito
//        .when(hoursOfOperation.getOpenClosePairs(Mockito.any(DayOfWeek.class)))
//        .thenReturn(Collections.singletonList(openClosePair));
//
//    bookingTimes.setBookingSlotInterval(30);
//
//    List<LocalTime> result = bookingTimes.getAll(LocalDate.now());
//
//    assertEquals(createMockLocalTimeList(), result);
//  }
//
//  private List<LocalTime> createMockLocalTimeList() {
//    List<LocalTime> timeList = new ArrayList<>();
//    for (int i = 15; i < 20; i++) {
//      for (int j = 0; j <= 59; j += 30) {
//        timeList.add(LocalTime.of(i, j));
//      }
//    }
//    return timeList;
//  }
//
//  @Test
//  void isBookingTime() {
//    Mockito
//        .when(hoursOfOperation.isOpen(Mockito.any(LocalDate.class)))
//        .thenReturn(true);
//    Mockito
//        .when(hoursOfOperation.isOpen(Mockito.any(DayOfWeek.class)))
//        .thenReturn(true);
//    Mockito
//        .when(hoursOfOperation.getOpenClosePairs(Mockito.any(DayOfWeek.class)))
//        .thenReturn(Collections.singletonList(openClosePair));
//
//    LocalDate today = LocalDate.now();
//    LocalDateTime bookingTime = today.atTime(15, 30);
//    LocalDateTime bookingTime1 = today.atTime(15, 0);
//    LocalDateTime notBookingTime = today.atTime(7, 30);
//    LocalDateTime notBookingTime2 = today.atTime(20, 0);
//
//    bookingTimes.setBookingSlotInterval(30);
//
//    assertTrue(bookingTimes.isBookingTime(bookingTime));
//    assertTrue(bookingTimes.isBookingTime(bookingTime1));
//    assertFalse(bookingTimes.isBookingTime(notBookingTime));
//    assertFalse(bookingTimes.isBookingTime(notBookingTime2));
//  }
//}