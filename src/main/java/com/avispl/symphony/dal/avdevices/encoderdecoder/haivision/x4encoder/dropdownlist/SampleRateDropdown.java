/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist;

import java.util.HashMap;
import java.util.Map;

/**
 * SampleRateDropdown class defined the enum for monitoring and controlling process
 *
 * @author Ivan / Symphony Dev Team<br>
 * Created on 3/8/2022
 * @since 1.0.0
 */
public enum SampleRateDropdown {

	SAMPLE_RATE_48Hz("48kHz", 48);

	private final String name;

	private final int value;

	/**
	 * SimpleRateDropdown instantiation
	 *
	 * @param name {@code {@link #name}}
	 * @param value {@code {@link #value}}
	 */
	SampleRateDropdown(String name, int value) {
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
	 * Retrieves name to value map of SimpleRateDropdown
	 *
	 * @return Map<Integer, String> are map value and name
	 */
	public static Map<Integer, String> getNameToValueMap() {
		Map<Integer, String> nameToValue = new HashMap<>();
		for (SampleRateDropdown sampleRateDropdown : SampleRateDropdown.values()) {
			nameToValue.put(sampleRateDropdown.getValue(), sampleRateDropdown.getName());
		}
		return nameToValue;
	}

	/**
	 * Retrieves name to value map of sampleRateDropdown
	 *
	 * @return Map<String, Integer> are map name and value
	 */
	public static Map<String, Integer> getValueToNameMap() {
		Map<String, Integer> valueToName = new HashMap<>();
		for (SampleRateDropdown sampleRateDropdown : SampleRateDropdown.values()) {
			valueToName.put(sampleRateDropdown.getName(), sampleRateDropdown.getValue());
		}
		return valueToName;
	}
}
