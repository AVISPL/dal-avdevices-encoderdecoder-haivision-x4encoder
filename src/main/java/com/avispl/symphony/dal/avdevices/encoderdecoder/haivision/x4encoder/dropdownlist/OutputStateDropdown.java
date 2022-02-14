/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist;

import java.util.HashMap;
import java.util.Map;

/**
 * OutputStateDropdown class defined the enum for monitoring and controlling process
 *
 * @author Ivan
 * @version 1.0.0
 * @since 1.0.0
 */
public enum OutputStateDropdown {

	STOPPED("Stopped", 0),
	RUNNING("Running", 1),
	FAILED("Failed", 2),
	CONNECTING("Connecting", 3),
	SECURING("Securing", 4),
	LISTENING("Listening", 5),
	PAUSED("Paused", 6),
	PUBLISHING("Publishing", 7),
	RESOLVING("Resolving", 8);

	private final String name;
	private final int value;

	/**
	 * VideoDropdown instantiation
	 *
	 * @param name {@code {@link #name}}
	 * @param value {@code {@link #value}}
	 */
	OutputStateDropdown(String name, int value) {
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
	 * Retrieves name to value map of StateDropdown
	 *
	 * @return Map<Integer, String> are name and value
	 */
	public static Map<Integer, String> getNameToValueMap() {
		Map<Integer, String> nameToValue = new HashMap<>();
		for (OutputStateDropdown stateDropdown : OutputStateDropdown.values()) {
			nameToValue.put(stateDropdown.getValue(), stateDropdown.getName());
		}
		return nameToValue;
	}
}
