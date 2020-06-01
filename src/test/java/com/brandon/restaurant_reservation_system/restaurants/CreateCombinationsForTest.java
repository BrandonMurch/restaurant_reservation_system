package com.brandon.restaurant_reservation_system.restaurants;

import com.brandon.restaurant_reservation_system.restaurants.model.CombinationOfTables;
import com.brandon.restaurant_reservation_system.restaurants.model.Table;

import java.util.ArrayList;
import java.util.List;

public class CreateCombinationsForTest {
	List<Table> tableList;

	public CreateCombinationsForTest(List<Table> tableList) {
		this.tableList = tableList;
	}

	public List<CombinationOfTables> getCombinationList() {
		List<CombinationOfTables> combinationOfTablesList = new ArrayList<>();
		combinationOfTablesList.add(getCombination1());
		combinationOfTablesList.add(getCombination2());
		combinationOfTablesList.add(getCombination3());
		return combinationOfTablesList;
	}

	public CombinationOfTables getCombination1() {
		List<Table> tableList = new ArrayList<>();
		tableList.add(this.tableList.get(0));
		tableList.add(this.tableList.get(1));
		tableList.add(this.tableList.get(2));
		return new CombinationOfTables(tableList);
	}

	public CombinationOfTables getCombination2() {
		List<Table> tableList = new ArrayList<>();
		tableList.add(this.tableList.get(0));
		tableList.add(this.tableList.get(1));
		return new CombinationOfTables(tableList);
	}

	public CombinationOfTables getCombination3() {
		List<Table> tableList = new ArrayList<>();
		tableList.add(this.tableList.get(1));
		tableList.add(this.tableList.get(2));
		return new CombinationOfTables(tableList);
	}





}
