/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist;

import java.util.HashMap;
import java.util.Map;

/**
 * FramingDropdown class defined the enum for monitoring and controlling process
 *
 * @author Ivan / Symphony Dev Team<br>
 * Created on 3/8/2022
 * @since 1.0.0
 */
public enum FramingDropdown {

	I("I", 1),
	IP("IP", 0),
	IBP("IBP", 2),
	IBBP("IBBP", 3),
	IBBBP("IBBBP", 4),
	IBBBBP("IBBBBP", 5);

	private final String name;
	private final int value;

	/**
	 * FramingDropdown instantiation
	 *
	 * @param name {@code {@link #name}}
	 * @param value {@code {@link #value}}
	 */
	FramingDropdown(String name, int value) {
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
		for (FramingDropdown framingDropdown : FramingDropdown.values()) {
			nameToValue.put(framingDropdown.getValue(), framingDropdown.getName());
		}
		return nameToValue;
	}


	/**
	 * Retrieves name to value map of framingDropdown
	 *
	 * @return Map<String, Integer> are map name and value
	 */
	public static Map<String, Integer> getValueToNameMap() {
		Map<String, Integer> valueToName = new HashMap<>();
		for (FramingDropdown framingDropdown : FramingDropdown.values()) {
			valueToName.put(framingDropdown.getName(), framingDropdown.getValue());
		}
		return valueToName;
	}
}
