/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * ResyncHourDropdown class defined the enum for monitoring and controlling process
 *
 * @author Ivan / Symphony Dev Team<br>
 * Created on 3/8/2022
 * @since 1.0.0
 */
public enum ReSyncHourDropdown {

	HOUR_0("00:00 (Midnight)", 0),
	HOUR_1("01:00 (1 AM)", 1),
	HOUR_2("02:00 (2 AM)", 2),
	HOUR_3("03:00 (3 AM)", 3),
	HOUR_4("04:00 (4 AM)", 4),
	HOUR_5("05:00 (5 AM)", 5),
	HOUR_6("06:00 (6 AM)", 6),
	HOUR_7("07:00 (7 AM)", 7),
	HOUR_8("08:00 (8 AM)", 8),
	HOUR_9("09:00 (9 AM)", 9),
	HOUR_10("10:00 (10 AM)", 10),
	HOUR_11("11:00 (11 AM)", 11),
	HOUR_12("12:00 (Noon)", 12),
	HOUR_13("13:00 (13 PM)", 13),
	HOUR_14("14:00 (14 PM)", 14),
	HOUR_15("15:00 (15 PM)", 15),
	HOUR_16("16:00 (16 PM)", 16),
	HOUR_17("17:00 (17 PM)", 17),
	HOUR_18("18:00 (18 PM)", 18),
	HOUR_19("19:00 (19 PM)", 19),
	HOUR_20("20:00 (20 PM)", 20),
	HOUR_21("21:00 (21 PM)", 21),
	HOUR_22("22:00 (22 PM)", 22),
	HOUR_23("23:00 (23 PM)", 23);

	private final String name;
	private final int value;

	/**
	 * ResyncHourDropdown instantiation
	 *
	 * @param name {@code {@link #name}}
	 */
	ReSyncHourDropdown(String name, int value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * Retrieves {@code {@link #name}}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves {@code {@link #value}}
	 *
	 * @return value of {@link #value}
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Retrieves all name of resyncHourDropdown
	 *
	 * @return list name of SimpleRate
	 */
	public static String[] names() {
		List<String> list = new LinkedList<>();
		for (ReSyncHourDropdown resyncHourDropdown : ReSyncHourDropdown.values()) {
			list.add(resyncHourDropdown.getName());
		}
		return list.toArray(new String[list.size()]);
	}

	/**
	 * Retrieves name to value map of ResyncHourDropdown
	 *
	 * @return Map<Integer, String> are name and value
	 */
	public static Map<Integer, String> getNameToValueMap() {
		Map<Integer, String> nameToValue = new HashMap<>();
		for (ReSyncHourDropdown resyncHourDropdown : ReSyncHourDropdown.values()) {
			nameToValue.put(resyncHourDropdown.getValue(), resyncHourDropdown.getName());
		}
		return nameToValue;
	}

	/**
	 * Retrieves name to value map of resyncHourDropdown
	 *
	 * @return Map<Integer, String> are name and value
	 */
	public static Map<String, Integer> getValueToNameMap() {
		Map<String, Integer> valueToName = new HashMap<>();
		for (ReSyncHourDropdown resyncHourDropdown : ReSyncHourDropdown.values()) {
			valueToName.put(resyncHourDropdown.getName(), resyncHourDropdown.getValue());
		}
		return valueToName;
	}
}
