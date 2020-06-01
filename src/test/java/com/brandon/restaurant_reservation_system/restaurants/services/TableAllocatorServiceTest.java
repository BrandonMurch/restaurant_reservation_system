package com.brandon.restaurant_reservation_system.restaurants.services;

import com.brandon.restaurant_reservation_system.GlobalVariables;
import com.brandon.restaurant_reservation_system.bookings.CreateBookingsForTest;
import com.brandon.restaurant_reservation_system.bookings.data.BookingRepository;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.restaurants.CreateRestaurantForTest;
import com.brandon.restaurant_reservation_system.restaurants.model.CombinationOfTables;
import com.brandon.restaurant_reservation_system.restaurants.model.Restaurant;
import com.brandon.restaurant_reservation_system.restaurants.model.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.brandon.restaurant_reservation_system.helpers.date_time.services.DateTimeHandler.parseDateTime;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {BookingRepository.class})
class TableAllocatorServiceTest {

	private final BookingRepository bookingRepository =
		Mockito.mock(BookingRepository.class);


	private TableAllocatorService tableAllocator;
	private Booking booking;
	private final DateTimeFormatter dateTimeFormat = GlobalVariables.getDateTimeFormat();
	private Restaurant restaurant;
	private List<Table> restaurantTableList;
	private final CreateBookingsForTest createBooking = new CreateBookingsForTest();


	// have to use BeforeEach due to SpringBootTest not playing nice
	@BeforeEach
	public void setUp() {

		Mockito.reset(bookingRepository);
		booking = createBooking.createBookingForFourAt20();
		restaurant = CreateRestaurantForTest.create();
		restaurantTableList = restaurant.getTableList();

		// create a list of bookings
		Booking booking = createBooking.createBookingForTwoAt19();
		booking.setTable(restaurant.getTableList().get(2));
		List<Booking> bookingList = new ArrayList<>();
		bookingList.add(booking);
		Mockito.when(bookingRepository.findAll()).thenReturn(bookingList);

		tableAllocator = new TableAllocatorService(restaurant, this.booking,
				bookingRepository);
	}


//	@Test
//	todo
//	void bookTableOtherwiseReturnTime() {
//	}

	@Test
	void isTheRestaurantOpen() {
		LocalDateTime openTime =
				parseDateTime("2020-10-11T19:00:00.00", dateTimeFormat);
		LocalDateTime closedTime =
				parseDateTime("2020-08-11T19:00:00.00", dateTimeFormat);
		assertTrue(tableAllocator.isTheRestaurantOpen(openTime));
		assertFalse(tableAllocator.isTheRestaurantOpen(closedTime));
	}

	@Test
	void getAFreeTable() {
		List<Table> freeTables = Arrays.asList(restaurantTableList.get(0),
				restaurantTableList.get(1));

		assertEquals(freeTables, tableAllocator.getAFreeTable());
	}

	@Test
	void findOccupiedTables() {
		tableAllocator.getBookings();
		Map<Table, Booking> occupiedTables =
				tableAllocator.findOccupiedTables();
		assertEquals(1, occupiedTables.size());
	}

	@Test
	void findAvailableTables() {
		Map<Integer, List<Table>> availableTables = new HashMap<>();

		// populate map of tables, skipping 2 which should be occupied
		for (int i = 0; i < restaurantTableList.size() && i != 2; i++) {
			Table table = restaurantTableList.get(0);
			availableTables.computeIfAbsent(table.getSeats(),
					k -> new ArrayList<>()).add(table);
		}

		assertEquals(availableTables, tableAllocator.findAvailableTables());
	}

	@Test
	void findTablesEqualToPartySize() {
		this.booking = createBooking.createBookingForTwoAt19();

		assertEquals(1, tableAllocator.findTables(booking.getPartySize()).size());
		assertEquals(restaurant.getTableList().get(0),
				tableAllocator.findTables(booking.getPartySize()).get(0));
	}

	@Test
	void findCombinationsEqualToPartySize() {
		List<Table> tables = Arrays.asList(restaurant.getTableList().get(0),
		restaurant.getTableList().get(1));
		assertEquals(tables, tableAllocator.findCombinations(booking.getPartySize()));
	}

	@Test
	void findTablesAndCombinationsGreaterSize() {
		// BLOCK REMAINING TABLES, FORCING THE 2 TO A TABLE OF 8
		List<Booking> bookingList = new ArrayList<>();

		Booking booking = createBooking.createBookingForTwoAt19();
		booking.setTable(restaurant.getTableList().get(0));
		bookingList.add(booking);

		booking = createBooking.createBookingForFourAt19();
		List<Table> tables =
				Arrays.asList(restaurant.getTableList().get(1),
						restaurant.getTableList().get(2));
		booking.setTable(tables);
		bookingList.add(booking);

		booking = createBooking.createBookingForTwoAt19();
		booking.setTable(restaurant.getTableList().get(4));
		bookingList.add(booking);

		Mockito.when(bookingRepository.findAll()).thenReturn(bookingList);
		tableAllocator.testRefresh();

		tables.clear();
		tables.add(restaurant.getTableList().get(3));
		assertEquals(tables,
				tableAllocator.findTablesAndCombinationsGreaterSize());
	}

	@Test
	void isCombinationFullyBooked() {
		// add bookings to fully book a combination of tables.
		List<Table> tables = Arrays.asList(restaurant.getTableList().get(1),
				restaurant.getTableList().get(2));
		booking = createBooking.createBookingForFourAt19();
		booking.setTable(tables);
		List<Booking> bookingList = Collections.singletonList(booking);
		Mockito.when(bookingRepository.findAll()).thenReturn(bookingList);
		tableAllocator.testRefresh();

		CombinationOfTables combination =
				restaurant.getCombinationsOfTables().get(2);
		assertTrue(tableAllocator.isCombinationFullyBooked(combination));
	}

	@Test
	void rearrangeCombination() {
		Booking booking = createBooking.createBookingForTwoAt19();
		booking.setTable(restaurantTableList.get(1));
		List<Booking> bookingList = Collections.singletonList(booking);

		Mockito.when(bookingRepository.findAll()).thenReturn(bookingList);
		tableAllocator.testRefresh();

		List<Table> expectedTables = Arrays.asList(restaurantTableList.get(0),
				restaurantTableList.get(1));

		assertEquals(expectedTables,
				tableAllocator.findTableSwapsInAllCombinations(this.booking.getPartySize()));
	}

	@Test
	void swapTables() {
		Booking booking = createBooking.createBookingForTwoAt19();
		booking.setTable(restaurantTableList.get(1));

		List<Booking> bookingList = Collections.singletonList(booking);
		Mockito.when(bookingRepository.findAll()).thenReturn(bookingList);

		tableAllocator.testRefresh();

		assertTrue(tableAllocator
				.findAllTableSwaps(restaurant.getCombinationsOfTables().get(1)));
	}

	@Test
	void swapTablesInBookings() {
		Booking booking = createBooking.createBookingForTwoAt19();
		booking.setTable(restaurantTableList.get(1));

		List<Booking> bookingList = Collections.singletonList(booking);
		Mockito.when(bookingRepository
				.findAll()).thenReturn(bookingList);
		tableAllocator.testRefresh();

		Map<List<Table>, List<Table>> swapsToPerform = new HashMap<>();
		swapsToPerform.put(Collections.singletonList(restaurantTableList.get(1)),
				Collections.singletonList(restaurantTableList.get(2)));
		tableAllocator.swapTablesInBookings(swapsToPerform);



		assertEquals(Collections.singletonList(restaurantTableList.get(2)),
					 booking.getTable());
	}

//	@Test
//	void checkOtherTimes() {
//	todo implement this test
//	}
}