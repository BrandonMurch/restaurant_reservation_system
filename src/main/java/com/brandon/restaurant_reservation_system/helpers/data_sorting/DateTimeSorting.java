package com.brandon.restaurant_reservation_system.helpers.data_sorting;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

public class DateTimeSorting {

	public static int findClosestIndex(List<LocalTime> list,
	                                   LocalTime target) {
		Collections.sort(list);
		int start = 0;
		int end = list.size();

		while (start < end) {
			int check = start + ((end - start) / 2);
			LocalTime obj = list.get(check);

			if (obj.equals(target)) {
				return check;
			} else if ((start == check - 1 || start == check) && (end == check + 1 || end == check)) {
				return check;
			}

			if (target.compareTo(obj) > 0) {
				start = check;
			} else if (target.compareTo(obj) < 0) {
				end = check;
			}

		}
		return end;
	}
}
