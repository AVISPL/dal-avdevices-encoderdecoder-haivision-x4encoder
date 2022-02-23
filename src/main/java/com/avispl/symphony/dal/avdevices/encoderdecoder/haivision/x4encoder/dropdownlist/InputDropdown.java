/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * InputDropdown class defined the enum for monitoring and controlling process
 *
 * @author Ivan
 * @version 1.0.0
 * @since 1.0.0
 */
public enum InputDropdown {

	ANALOG("Analog", 0),
	SDI_1_1_2("SDI 1 (1-2)", 1),
	SDI_1_3_4("SDI 1 (3-4)", 2),
	SDI_1_5_6("SDI 1 (5-6)", 3),
	SDI_1_7_8("SDI 1 (7-8)", 4),
	SDI_1_9_10("SDI 1 (9-10)", 5),
	SDI_1_11_12("SDI 1 (11-12) ", 6),
	SDI_1_13_14("SDI 1 (13-14)", 7),
	SDI_1_15_16("SDI 1 (15-16)", 8),
	// if "videoInputs" >= 2:
	SDI_2_1_2("SDI 2 (1-2)", 9),
	SDI_2_3_4("SDI 2 (3-4)", 10),
	SDI_2_5_6("SDI 2 (5-6)", 11),
	SDI_2_7_8("SDI 2 (7-8)", 12),
	SDI_2_9_10("SDI 2 (9-10)", 13),
	SDI_2_11_12("SDI 2 (11-12) ", 14),
	SDI_2_13_14("SDI 2 (13-14)", 15),
	SDI_2_15_16("SDI 2 (15-16)", 16),
	// if "videoInputs" >= 3
	SDI_3_1_2("SDI 3 (1-2)", 17),
	SDI_3_3_4("SDI 3 (3-4)", 18),
	SDI_3_5_6("SDI 3 (5-6)", 19),
	SDI_3_7_8("SDI 3 (7-8)", 20),
	SDI_3_9_10("SDI 3 (9-10)", 21),
	SDI_3_11_12("SDI 3 (11-12) ", 22),
	SDI_3_13_14("SDI 3 (13-14)", 23),
	SDI_3_15_16("SDI 3 (15-16)", 24),
	// if "videoInputs" >= 4
	SDI_4_1_2("SDI 4 (1-2)", 25),
	SDI_4_3_4("SDI 4 (3-4)", 26),
	SDI_4_5_6("SDI 4 (5-6)", 27),
	SDI_4_7_8("SDI 4 (7-8)", 28),
	SDI_4_9_10("SDI 4 (9-10)", 29),
	SDI_4_11_12("SDI 4 (11-12) ", 30),
	SDI_4_13_14("SDI 4 (13-14)", 31),
	SDI_4_15_16("SDI 4 (15-16)", 32);

	private final String name;

	private final int value;

	/**
	 * InputDropdown instantiation
	 *
	 * @param name {@code {@link #name}}
	 * @param value {@code {@link #value}}
	 */
	InputDropdown(String name, int value) {
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
	 * Retrieves all name of InputDropdown
	 *
	 * @return list name of Input Interface
	 */
	public static String[] names() {
		List<String> list = new LinkedList<>();
		for (InputDropdown inputDropdown : InputDropdown.values()) {
			list.add(inputDropdown.getName());
		}
		return list.toArray(new String[list.size()]);
	}

	/**
	 * Retrieves name to value map of InputDropdown
	 *
	 * @return Map<String, String> are name and value
	 */
	public static Map<Integer, String> getNameToValueMap() {
		Map<Integer, String> nameToValue = new HashMap<>();
		for (InputDropdown inputDropdown : InputDropdown.values()) {
			nameToValue.put(inputDropdown.getValue(), inputDropdown.getName());
		}
		return nameToValue;
	}
}
