///*
// * Copyright (c) 2020 Brandon Murch
// */
//
//package com.brandon.restaurant_reservation_system.restaurants.data;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.spy;
//
//import com.brandon.restaurant_reservation_system.restaurants.model.DateRange;
//import com.brandon.restaurant_reservation_system.restaurants.model.Restaurant;
//import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
//import com.brandon.restaurant_reservation_system.tables.service.TableAllocatorService;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Optional;
//import java.util.SortedSet;
//import java.util.TreeSet;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.test.util.ReflectionTestUtils;
//
//@ExtendWith(MockitoExtension.class)
//class RestaurantCacheUnitTest {
//
//  final SortedSet<LocalDate> availableDates = new TreeSet<>();
//  @InjectMocks
//  private final RestaurantCache cache = new RestaurantCache();
//  private final Map<LocalDate, Integer> bookingsPerDate = new HashMap<>();
//  @Mock
//  private TableAllocatorService tableAllocatorService;
//  @Mock
//  private Restaurant restaurant;
//
//  @BeforeEach
//  void setUp() {
//    availableDates.add(LocalDate.now());
//    availableDates.add(LocalDate.now().plusDays(1));
//    bookingsPerDate.put(LocalDate.now(), 2);
//    bookingsPerDate.put(LocalDate.now().plusDays(1), 4);
//  }
//
//  @Test
//  void getAvailableDates() {
//    mockInstanceVariables();
//    assertEquals(availableDates, cache.getAvailableDates());
//  }
//
//  @Test
//  void addAvailableDate() {
//    mockInstanceVariables();
//    RestaurantCache spy = spy(cache);
//    Mockito.doNothing().when(spy).checkAvailableDatesCache();
//    LocalDate twoDays = LocalDate.now().plusDays(2);
//    availableDates.add(twoDays);
//    spy.addAvailableDate(twoDays);
//    assertEquals(availableDates, spy.getAvailableDates());
//  }
//
//  @Test
//  void removeDateIfUnavailable() {
//    mockInstanceVariables();
//    Mockito
//        .when(restaurant.getBookingTimes(any(LocalDate.class)))
//        .thenReturn(Collections.singletonList(LocalTime.now()));
//    Mockito
//        .when(tableAllocatorService.getAvailableTable(any(LocalDateTime.class), eq(2),
//            eq(false)))
//        .thenReturn(Optional.empty());
//    LocalDate oneDay = LocalDate.now().plusDays(1);
//    availableDates.remove(oneDay);
//    cache.removeDateIfUnavailable(oneDay);
//    assertEquals(availableDates, cache.getAvailableDates());
//  }
//
//  @Test
//  void createCache() {
//    Mockito
//        .when(restaurant.getBookingDateRange())
//        .thenReturn(new DateRange(LocalDate.now(), LocalDate.now().plusDays(2)));
//    Mockito
//        .when(restaurant.isOpenOnDate(any(LocalDate.class)))
//        .thenReturn(true);
//    Mockito
//        .when(restaurant.getBookingTimes(any(LocalDate.class)))
//        .thenReturn(Collections.singletonList(LocalTime.now()));
//    Mockito
//        .when(tableAllocatorService.getAvailableTable(any(LocalDateTime.class), eq(2),
//            eq(false)))
//        .thenReturn(Optional.of(new RestaurantTable("", 0, 0)));
//
//    SortedSet<LocalDate> expected = new TreeSet<>();
//    for (int i = 0; i < 3; i++) {
//      expected.add(LocalDate.now().plusDays(i));
//    }
//    assertEquals(expected, cache.getAvailableDates());
//  }
//
//  @Test
//  void getBookingsPerDate() {
//    mockInstanceVariables();
//    assertEquals(bookingsPerDate, cache.getBookingsPerDate());
//    assertTrue(cache.getBookingsPerDate().keySet().size() > 1);
//  }
//
//  @Test
//  void addBookingToDate() {
//    mockInstanceVariables();
//    cache.addBookingToDate(LocalDate.now(), 2);
//    assertEquals(4, cache.getBookingsPerDate().get(LocalDate.now()));
//  }
//
//  @Test
//  void removeBookingFromDate() {
//    mockInstanceVariables();
//    cache.removeBookingFromDate(LocalDate.now(), 2);
//    assertEquals(0, cache.getBookingsPerDate().get(LocalDate.now()));
//  }
//
//  private void mockInstanceVariables() {
//    ReflectionTestUtils.setField(cache, "availableDates", availableDates);
//    ReflectionTestUtils.setField(cache, "datesLastUpdated", LocalDate.now());
//    ReflectionTestUtils.setField(cache, "bookingsPerDate", bookingsPerDate);
//    ReflectionTestUtils.setField(cache, "countsLastUpdated", LocalDate.now());
//  }
//}