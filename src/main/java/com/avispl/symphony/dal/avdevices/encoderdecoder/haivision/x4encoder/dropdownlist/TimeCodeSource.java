/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * TimeCodeSource class defined the enum for monitoring and controlling process
 *
 * @author Ivan
 * @version 1.0.0
 * @since 1.0.0
 */
public enum TimeCodeSource {

	None("None", 0),
	VIDEO("Video", 1),
	SYSTEM("System", 2);

	private final String name;
	private final int value;

	/**
	 * TimeCodeSource instantiation
	 *
	 * @param name {@code {@link #name}}
	 * @param value {@code {@link #value}}
	 */
	TimeCodeSource(String name, int value) {
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
	 * Retrieves name to value map of TimeCodeSource
	 *
	 * @return Map<Integer, String> are name and value
	 */
	public static Map<Integer, String> getNameToValueMap() {
		Map<Integer, String> nameToValue = new HashMap<>();
		for (TimeCodeSource timeCodeSource : TimeCodeSource.values()) {
			nameToValue.put(timeCodeSource.getValue(), timeCodeSource.getName());
		}
		return nameToValue;
	}

	/**
	 * Retrieves all name of TimeCodeSource
	 *
	 * @return list name of TimeCodeSource
	 */
	public static String[] names() {
		List<String> list = new LinkedList<>();
		for (TimeCodeSource timeCodeSource : TimeCodeSource.values()) {
			list.add(timeCodeSource.getName());
		}
		return list.toArray(new String[list.size()]);
	}
}
