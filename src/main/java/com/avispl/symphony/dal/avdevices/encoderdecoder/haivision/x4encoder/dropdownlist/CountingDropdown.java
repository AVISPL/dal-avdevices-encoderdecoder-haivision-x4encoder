/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * countingDropdown class defined the enum for monitoring and controlling process
 *
 * @author Ivan / Symphony Dev Team<br>
 * Created on 3/8/2022
 * @since 1.0.0
 */
public enum CountingDropdown {

	UTC_CONVERSION("UTC conversion", 0),
	SMPTE_12M_1("SMPTE 12M-1", 1);

	private final String name;
	private final int value;

	/**
	 * countingDropdown instantiation
	 *
	 * @param name {@code {@link #name}}
	 * @param value {@code {@link #value}}
	 */
	CountingDropdown(String name, int value) {
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
	 * Retrieves name to value map of countingDropdown
	 *
	 * @return Map<Integer, String> are name and value
	 */
	public static Map<Integer, String> getNameToValueMap() {
		Map<Integer, String> nameToValue = new HashMap<>();
		for (CountingDropdown countingDropdown : CountingDropdown.values()) {
			nameToValue.put(countingDropdown.getValue(), countingDropdown.getName());
		}
		return nameToValue;
	}

	/**
	 * Retrieves name to value map of CountingDropdown
	 *
	 * @return Map<Integer, String> are name and value
	 */
	public static Map<String, Integer> getValueToNameMap() {
		Map<String, Integer> valueToName = new HashMap<>();
		for (CountingDropdown countingDropdown : CountingDropdown.values()) {
			valueToName.put(countingDropdown.getName(), countingDropdown.getValue());
		}
		return valueToName;
	}

	/**
	 * Retrieves all name of countingDropdown
	 *
	 * @return list name of countingDropdown
	 */
	public static String[] names() {
		List<String> list = new LinkedList<>();
		for (CountingDropdown countingDropdown : CountingDropdown.values()) {
			list.add(countingDropdown.getName());
		}
		return list.toArray(new String[list.size()]);
	}
}
