package com.brandon.restaurant_reservation_system.helpers.data_sorting;

import static com.brandon.restaurant_reservation_system.helpers.date_time.services.DateTimeHandler.parseTime;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


class DateTimeSortingTest {

	@Test
	void findClosestIndexTest() {
		List<LocalTime> list = new ArrayList<>();

		for (int i = 16; i < 24; i++) {
			LocalTime dateTime = parseTime("2020-10-11T" + i + ":00:00.00",
					DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			list.add(dateTime);
		}

		LocalTime target = parseTime("2020-10-11T19:30:00.00",
				DateTimeFormatter.ISO_LOCAL_DATE_TIME);

		int index = DateTimeSorting.findClosestIndex(list,
				target);

		assertEquals(3, index);

	}
}