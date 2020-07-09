package com.brandon.restaurant_reservation_system.restaurants.services;

import com.brandon.restaurant_reservation_system.GlobalVariables;
import com.brandon.restaurant_reservation_system.bookings.CreateBookingsForTest;
import com.brandon.restaurant_reservation_system.bookings.data.BookingRepository;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.restaurants.CreateRestaurantForTest;
import com.brandon.restaurant_reservation_system.restaurants.model.Restaurant;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {BookingRepository.class})
class RestaurantTableAllocatorServiceTest {
	@MockBean
	private BookingRepository bookingRepository;

	private TableAllocatorService tableAllocator;
	private Booking booking;
	private final DateTimeFormatter dateTimeFormat = GlobalVariables.getDateTimeFormat();
	private Restaurant restaurant;
	private List<RestaurantTable> restaurantRestaurantTableList;
	private final CreateBookingsForTest createBooking = new CreateBookingsForTest();
	private List<Booking> bookingList;


	@BeforeEach
	public void setUp() {

		bookingRepository = Mockito.mock(BookingRepository.class);

		booking = createBooking.createBookingForFourAt20();
		restaurant = CreateRestaurantForTest.create();
		restaurantRestaurantTableList = restaurant.getTableList();

		// create a list of bookings
		Booking booking = createBooking.createBookingForTwoAt19();
		RestaurantTable table = restaurant.getTableList().get(2);
		booking.setTable(Collections.singletonList(table));
		bookingList = new ArrayList<>();
		bookingList.add(booking);
		Mockito.when(bookingRepository.findAll()).thenReturn(bookingList);
		Mockito.when(bookingRepository
				.getBookingsDuringTime(
						any(LocalDateTime.class), any(LocalDateTime.class)))
				.thenReturn(bookingList);


		tableAllocator = new TableAllocatorService(restaurant, this.booking,
				bookingRepository);
	}

	@BeforeEach
	public void setUpBeforeEach() {
		Mockito.reset(bookingRepository);
	}


	@Test
	public void getBookings() {

		List<Booking> bookings =
				tableAllocator.getBookings(
						booking.getStartTime(),
						booking.getEndTime());
		assertEquals(bookingList, bookings);
	}

	@Test
	public void getOccupiedTables() {
		Map<RestaurantTable, Booking> occupiedTables =
				tableAllocator.getOccupiedTables(bookingList);

		assertEquals(1, occupiedTables.size());
	}

	@Test
	public void bookTable() {
		assertTrue(tableAllocator.bookTable(booking));
	}

	@Test
	public void getAvailableTable() {
		List<RestaurantTable> results =
				tableAllocator.getAvailableTable(booking.getStartTime(),
						booking.getPartySize(), true);

		assertFalse(results.isEmpty());
	}
}
//	@Test
//	void getAFreeTable() {
//		List<RestaurantTable> freeTables = Arrays.asList(restaurantRestaurantTableList.get(0),
//				restaurantRestaurantTableList.get(1));
//
//		assertEquals(freeTables, tableAllocator.getAFreeTable());
//	}
//
//	@Test
//	void findAvailableTables() {
//		Map<Integer, List<RestaurantTable>> availableTables = new HashMap<>();
//
//		// populate map of tables, skipping 2 which should be occupied
//		for (int i = 0; i < restaurantRestaurantTableList.size() && i != 2; i++) {
//			RestaurantTable table = restaurantRestaurantTableList.get(0);
//			availableTables.computeIfAbsent(table.getSeats(),
//					k -> new ArrayList<>()).add(table);
//		}
//
//		assertEquals(availableTables, tableAllocator.getAvailableTablesBySize());
//	}
//
//	@Test
//	void findTablesEqualToPartySize() {
//		this.booking = createBooking.createBookingForTwoAt19();
//
//		assertEquals(1, tableAllocator.findTables(booking.getPartySize()).size());
//		assertEquals(restaurant.getTableList().get(0),
//				tableAllocator.findTables(booking.getPartySize()).get(0));
//	}
//
//	@Test
//	void findCombinationsEqualToPartySize() {
//		List<RestaurantTable> tables = Arrays.asList(restaurant.getTableList().get(0),
//		restaurant.getTableList().get(1));
//		assertEquals(tables, tableAllocator.findCombinations(booking.getPartySize()));
//	}
//
//	@Test
//	void findTablesAndCombinationsGreaterSize() {
//		// BLOCK REMAINING TABLES, FORCING THE 2 TO A TABLE OF 8
//		List<Booking> bookingList = new ArrayList<>();
//
//		Booking booking = createBooking.createBookingForTwoAt19();
//		booking.setTable(restaurant.getTableList().get(0));
//		bookingList.add(booking);
//
//		booking = createBooking.createBookingForFourAt19();
//		List<RestaurantTable> tables =
//				Arrays.asList(restaurant.getTableList().get(1),
//						restaurant.getTableList().get(2));
//		booking.setTable(tables);
//		bookingList.add(booking);
//
//		booking = createBooking.createBookingForTwoAt19();
//		booking.setTable(restaurant.getTableList().get(4));
//		bookingList.add(booking);
//
//		Mockito.when(bookingRepository.findAll()).thenReturn(bookingList);
//		tableAllocator.testRefresh();
//
//		tables.clear();
//		tables.add(restaurant.getTableList().get(3));
//		assertEquals(tables,
//				tableAllocator.findTablesAndCombinationsGreaterSize());
//	}
//
//	@Test
//	void isCombinationFullyBooked() {
//		// add bookings to fully book a combination of tables.
//		List<RestaurantTable> tables = Arrays.asList(restaurant.getTableList().get(1),
//				restaurant.getTableList().get(2));
//		booking = createBooking.createBookingForFourAt19();
//		booking.setTable(tables);
//		List<Booking> bookingList = Collections.singletonList(booking);
//		Mockito.when(bookingRepository.findAll()).thenReturn(bookingList);
//		tableAllocator.testRefresh();
//
//		CombinationOfTables combination =
//				restaurant.getCombinationsOfTables().get(2);
//		assertTrue(tableAllocator.isCombinationFullyBooked(combination));
//	}
//
//	@Test
//	void rearrangeCombination() {
//		Booking booking = createBooking.createBookingForTwoAt19();
//		booking.setTable(restaurantRestaurantTableList.get(1));
//		List<Booking> bookingList = Collections.singletonList(booking);
//
//		Mockito.when(bookingRepository.findAll()).thenReturn(bookingList);
//		tableAllocator.testRefresh();
//
//		List<RestaurantTable> expectedTables = Arrays.asList(restaurantRestaurantTableList.get(0),
//				restaurantRestaurantTableList.get(1));
//
//		assertEquals(expectedTables,
//				tableAllocator.findTableSwapsInAllCombinations(this.booking.getPartySize()));
//	}
//
//	@Test
//	void swapTables() {
//		Booking booking = createBooking.createBookingForTwoAt19();
//		booking.setTable(restaurantRestaurantTableList.get(1));
//
//		List<Booking> bookingList = Collections.singletonList(booking);
//		Mockito.when(bookingRepository.findAll()).thenReturn(bookingList);
//
//		tableAllocator.testRefresh();
//
//		assertTrue(tableAllocator
//				.findAllTableSwaps(restaurant.getCombinationsOfTables().get(1)));
//	}
//
//	@Test
//	void swapTablesInBookings() {
//		Booking booking = createBooking.createBookingForTwoAt19();
//		booking.setTable(restaurantRestaurantTableList.get(1));
//
//		List<Booking> bookingList = Collections.singletonList(booking);
//		Mockito.when(bookingRepository
//				.findAll()).thenReturn(bookingList);
//		tableAllocator.testRefresh();
//
//		Map<List<RestaurantTable>, List<RestaurantTable>> swapsToPerform = new HashMap<>();
//		swapsToPerform.put(Collections.singletonList(restaurantRestaurantTableList.get(1)),
//				Collections.singletonList(restaurantRestaurantTableList.get(2)));
//		tableAllocator.swapTablesInBookings(swapsToPerform);
//
//
//
//		assertEquals(Collections.singletonList(restaurantRestaurantTableList.get(2)),
//					 booking.getTable());
//	}
//
////	@Test
////	void checkOtherTimes() {
////	todo implement this test
////	}
//}