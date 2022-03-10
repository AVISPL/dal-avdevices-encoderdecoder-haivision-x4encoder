/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist;

import java.util.HashMap;
import java.util.Map;

/**
 * AlgorithmDropdown class defined the enum for monitoring and controlling process
 *
 * @author Ivan / Symphony Dev Team<br>
 * Created on 3/8/2022
 * @since 1.0.0
 */
public enum AlgorithmDropdown {

	MPEG_2("MPEG-2 ADTS", 10),
	MPEG_4("MPEG-4 LOAS/LATM", 21);

	private final String name;

	private final int value;

	/**
	 * AlgorithmDropdown instantiation
	 *
	 * @param name {@code {@link #name}}
	 * @param value {@code {@link #value}}
	 */
	AlgorithmDropdown(String name, int value) {
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
	 * Retrieves name to value map of AlgorithmDropdown
	 *
	 * @return Map<Integer, String> are map value and name
	 */
	public static Map<Integer, String> getNameToValueMap() {
		Map<Integer, String> nameToValue = new HashMap<>();
		for (AlgorithmDropdown algorithmDropdown : AlgorithmDropdown.values()) {
			nameToValue.put(algorithmDropdown.getValue(), algorithmDropdown.getName());
		}
		return nameToValue;
	}

	/**
	 * Retrieves name to value map of algorithmDropdown
	 *
	 * @return Map<String, Integer> are map name and value
	 */
	public static Map<String, Integer> getValueToNameMap() {
		Map<String, Integer> valueToName = new HashMap<>();
		for (AlgorithmDropdown algorithmDropdown : AlgorithmDropdown.values()) {
			valueToName.put(algorithmDropdown.getName(), algorithmDropdown.getValue());
		}
		return valueToName;
	}

	@Override
	public String toString() {
		return name;
	}
}
