/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * AspectRatioDropdown class defined the enum for monitoring and controlling process
 *
 * @author Ivan
 * @version 1.0.0
 * @since 1.0.0
 */
public enum AspectRatioDropdown {

	ASPECT_RATIO_0("Automatic", 0),
	ASPECT_RATIO_1("WSS/AFD", 1),
	ASPECT_RATIO_2("4:3", 2),
	ASPECT_RATIO_3("5:3", 3),
	ASPECT_RATIO_4("5:4", 4),
	ASPECT_RATIO_5("16:9", 5),
	ASPECT_RATIO_13("3:2", 13),
	ASPECT_RATIO_14("16:10",14),
	ASPECT_RATIO_15("17:9",15);

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
	 * @return Map<Integer, String> are name and value
	 */
	public static Map<Integer, String> getNameToValueMap() {
		Map<Integer, String> nameToValue = new HashMap<>();
		for (AspectRatioDropdown aspectRatioDropdown : AspectRatioDropdown.values()) {
			nameToValue.put(aspectRatioDropdown.getValue(), aspectRatioDropdown.getName());
		}
		return nameToValue;
	}

	/**
	 * Retrieves all name of framingDropdown
	 *
	 * @return list name of framingDropdown
	 */
	public static String[] names() {
		List<String> list = new LinkedList<>();
		for (AspectRatioDropdown aspectRatioDropdown : AspectRatioDropdown.values()) {
			list.add(aspectRatioDropdown.getName());
		}
		return list.toArray(new String[list.size()]);
	}
}
