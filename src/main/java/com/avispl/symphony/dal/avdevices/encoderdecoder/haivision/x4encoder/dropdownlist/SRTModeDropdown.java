/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist;

import java.util.HashMap;
import java.util.Map;

/**
 * SRTMode
 *
 * @author Ivan / Symphony Dev Team<br>
 * Created on 3/11/2022
 * @since 1.0.0
 */
public enum SRTModeDropdown {

	CALLER("Caller", 0),
	LISTENER("Listener", 1),
	RENDEZVOUS("Rendezvous", 2);

	private final String name;
	private final int value;

	/**
	 * SRTModeDropdown instantiation
	 *
	 * @param name {@code {@link #name}}
	 * @param value {@code {@link #value}}
	 */
	SRTModeDropdown(String name, int value) {
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
	 * Retrieves name to value map of SRTModeDropdown
	 *
	 * @return Map<Integer, String> are map value and name
	 */
	public static Map<Integer, String> getNameToValueMap() {
		Map<Integer, String> nameToValue = new HashMap<>();
		for (SRTModeDropdown srtModeDropdown : SRTModeDropdown.values()) {
			nameToValue.put(srtModeDropdown.getValue(), srtModeDropdown.getName());
		}
		return nameToValue;
	}

	/**
	 * Retrieves name to value map of SRTModeDropdown
	 *
	 * @return Map<String,Integer> are map name and value
	 */
	public static Map<String, Integer> getValueToNameMap() {
		Map<String, Integer> valueToName = new HashMap<>();
		for (SRTModeDropdown srtModeDropdown : SRTModeDropdown.values()) {
			valueToName.put(srtModeDropdown.getName(), srtModeDropdown.getValue());
		}
		return valueToName;
	}
}