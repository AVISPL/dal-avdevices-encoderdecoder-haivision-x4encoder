/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Cropping class defined the enum for monitoring and controlling process
 *
 * @author Ivan
 * @version 1.0.0
 * @since 1.0.0
 */
public enum Cropping {

	H_264("", 0),
	H_265("HEVC/H.265", 1);

	private final String name;
	private final int value;

	/**
	 * Cropping instantiation
	 *
	 * @param name {@code {@link #name}}
	 * @param value {@code {@link #value}}
	 */
	Cropping(String name, int value) {
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
	 * Retrieves name to value map of CodecAlgorithmDropdown
	 *
	 * @return Map<Integer, String> are name and value
	 */
	public static Map<Integer, String> getNameToValueMap() {
		Map<Integer, String> nameToValue = new HashMap<>();
		for (Cropping cropping : Cropping.values()) {
			nameToValue.put(cropping.getValue(), cropping.getName());
		}
		return nameToValue;
	}

	/**
	 * Retrieves all name of Cropping
	 *
	 * @return list name of Cropping
	 */
	public static String[] names() {
		List<String> list = new LinkedList<>();
		for (Cropping cropping : Cropping.values()) {
			list.add(cropping.getName());
		}
		return list.toArray(new String[list.size()]);
	}
}