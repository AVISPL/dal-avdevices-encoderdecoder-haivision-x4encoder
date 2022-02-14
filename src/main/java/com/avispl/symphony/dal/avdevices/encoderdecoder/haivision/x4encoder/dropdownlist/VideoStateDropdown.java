/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist;

import java.util.HashMap;
import java.util.Map;

/**
 * StateDropdown class defined the enum for monitoring and controlling process
 *
 * @author Ivan
 * @version 1.0.0
 * @since 1.0.0
 */
public enum VideoStateDropdown {

	STOPPED("Stopped", 0),
	AWAIT_FRAMING("Await Framing", 3),
	NOT_ENCODING("Not Encoding", 5),
	WORKING("Working", 7),
	RESETTING("Resetting", 8),
	FAILED("Failed", 128);

	private final String name;
	private final int value;

	/**
	 * VideoDropdown instantiation
	 *
	 * @param name {@code {@link #name}}
	 * @param value {@code {@link #value}}
	 */
	VideoStateDropdown(String name, int value) {
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
	 * Retrieves name to value map of StateOutputDropdown
	 *
	 * @return Map<Integer, String> are name and value
	 */
	public static Map<Integer, String> getNameToValueMap() {
		Map<Integer, String> nameToValue = new HashMap<>();
		for (VideoStateDropdown stateOutputDropdown : VideoStateDropdown.values()) {
			nameToValue.put(stateOutputDropdown.getValue(), stateOutputDropdown.getName());
		}
		return nameToValue;
	}
}
