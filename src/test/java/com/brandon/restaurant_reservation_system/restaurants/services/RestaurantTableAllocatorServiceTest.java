package com.brandon.restaurant_reservation_system.restaurants.services;

import com.brandon.restaurant_reservation_system.GlobalVariables;
import com.brandon.restaurant_reservation_system.bookings.CreateBookingsForTest;
import com.brandon.restaurant_reservation_system.bookings.data.BookingRepository;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
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
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {BookingRepository.class})
class RestaurantTableAllocatorServiceTest {
	@MockBean
	private BookingRepository bookingRepository;

	@MockBean
	private BookingHandlerService bookingService;

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
		PopulateRestaurantService.populateRestaurant(restaurant);
		PopulateRestaurantService.populateRestaurantTables(restaurant);
		restaurantRestaurantTableList = restaurant.getTableList();

		// create a list of bookings
		Booking booking = createBooking.createBookingForTwoAt19();
		RestaurantTable table = restaurant.getTableList().get(2);
		// FIXME: fix this line with new bookinghandler
//		bookingService.setTable(Collections.singletonList(table));
		bookingList = new ArrayList<>();
		bookingList.add(booking);
		Mockito.when(bookingRepository.findAll()).thenReturn(bookingList);
		Mockito.when(bookingRepository
				.getBookingsDuringTime(
						any(LocalDateTime.class), any(LocalDateTime.class)))
				.thenReturn(bookingList);


		tableAllocator = new TableAllocatorService(restaurant,
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
	public void getAvailableTable() {
		List<RestaurantTable> results =
				tableAllocator.getAvailableTable(booking.getStartTime(),
						booking.getPartySize(), true);

		assertFalse(results.isEmpty());
	}
}   