/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * RateControlDropdown class defined the enum for monitoring and controlling process
 *
 * @author Ivan
 * @version 1.0.0
 * @since 1.0.0
 */
public enum RateControlDropdown {

	CBR("CBR", 0),
	CVBR("CVBR", 1);

	private final String name;

	private final int value;

	/**
	 * RateControlDropdown instantiation
	 *
	 * @param name {@code {@link #name}}
	 * @param value {@code {@link #value}}
	 */
	RateControlDropdown(String name, int value) {
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
	 * Retrieves name to value map of RateControlDropdown
	 *
	 * @return Map<Integer, String> are name and value
	 */
	public static Map<Integer, String> getNameToValueMap() {
		Map<Integer, String> nameToValue = new HashMap<>();
		for (RateControlDropdown rateControlDropdown : RateControlDropdown.values()) {
			nameToValue.put(rateControlDropdown.getValue(), rateControlDropdown.getName());
		}
		return nameToValue;
	}

	/**
	 * Retrieves name to value map of rateControlDropdown
	 *
	 * @return Map<Integer, String> are name and value
	 */
	public static Map<String, Integer> getValueToNameMap() {
		Map<String, Integer> valueToName = new HashMap<>();
		for (RateControlDropdown rateControlDropdown : RateControlDropdown.values()) {
			valueToName.put(rateControlDropdown.getName(), rateControlDropdown.getValue());
		}
		return valueToName;
	}

	/**
	 * Retrieves all name of rateControlDropdown
	 *
	 * @return list name of rateControlDropdown
	 */
	public static String[] names() {
		List<String> list = new LinkedList<>();
		for (RateControlDropdown rateControlDropdown : RateControlDropdown.values()) {
			list.add(rateControlDropdown.getName());
		}
		return list.toArray(new String[list.size()]);
	}
}
