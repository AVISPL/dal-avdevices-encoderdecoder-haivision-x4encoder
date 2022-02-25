/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * AlgorithmDropdown class defined the enum for monitoring and controlling process
 *
 * @author Ivan
 * @version 1.0.0
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
	 * @return Map<Integer, String> are name and value
	 */
	public static Map<Integer, String> getNameToValueMap() {
		Map<Integer, String> nameToValue = new HashMap<>();
		for (AlgorithmDropdown algorithmDropdown : AlgorithmDropdown.values()) {
			nameToValue.put(algorithmDropdown.getValue(), algorithmDropdown.getName());
		}
		return nameToValue;
	}

	/**
	 * Retrieves all name of AlgorithmDropdown
	 *
	 * @return list name of Algorithm
	 */
	public static String[] names() {
		List<String> list = new LinkedList<>();
		for (AlgorithmDropdown algorithmDropdown : AlgorithmDropdown.values()) {
			list.add(algorithmDropdown.getName());
		}
		return list.toArray(new String[list.size()]);
	}
}
