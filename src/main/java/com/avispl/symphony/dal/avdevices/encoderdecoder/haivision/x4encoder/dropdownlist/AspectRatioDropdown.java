/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist;

import java.util.HashMap;
import java.util.Map;

/**
 * AspectRatioDropdown class defined the enum for monitoring and controlling process
 *
 * @author Ivan / Symphony Dev Team<br>
 * Created on 3/8/2022
 * @since 1.0.0
 */
public enum AspectRatioDropdown {

	ASPECT_RATIO_0("Automatic", 0),
	ASPECT_RATIO_1("WSS/AFD", 1),
	ASPECT_RATIO_13("3:2", 13),
	ASPECT_RATIO_2("4:3", 2),
	ASPECT_RATIO_3("5:3", 3),
	ASPECT_RATIO_4("5:4", 4),
	ASPECT_RATIO_5("16:9", 5),
	ASPECT_RATIO_14("16:10", 14),
	ASPECT_RATIO_15("17:9", 15);

	private final String name;
	private final int value;

	/**
	 * AspectRatioDropdown instantiation
	 *
	 * @param name {@code {@link #name}}
	 * @param value {@code {@link #value}}
	 */
	AspectRatioDropdown(String name, int value) {
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
	 * Retrieves name to value map of FramingDropdown
	 *
	 * @return Map<Integer, String> are map value and name
	 */
	public static Map<Integer, String> getNameToValueMap() {
		Map<Integer, String> nameToValue = new HashMap<>();
		for (AspectRatioDropdown aspectRatioDropdown : values()) {
			nameToValue.put(aspectRatioDropdown.getValue(), aspectRatioDropdown.getName());
		}
		return nameToValue;
	}

	/**
	 * Retrieves name to value map of aspectRatioDropdown
	 *
	 * @return Map<String, Integer> are map name and value
	 */
	public static Map<String, Integer> getValueToNameMap() {
		Map<String, Integer> valueToName = new HashMap<>();
		for (AspectRatioDropdown aspectRatioDropdown : values()) {
			valueToName.put(aspectRatioDropdown.getName(), aspectRatioDropdown.getValue());
		}
		return valueToName;
	}

	@Override
	public String toString() {
		return name;
	}
}
