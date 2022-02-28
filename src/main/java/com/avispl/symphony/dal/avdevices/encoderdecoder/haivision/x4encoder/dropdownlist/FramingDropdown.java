/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * FramingDropdown class defined the enum for monitoring and controlling process
 *
 * @author Ivan
 * @version 1.0.0
 * @since 1.0.0
 */
public enum FramingDropdown {

	I("I", 0),
	IP("IP", 1),
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
	 * @return Map<Integer, String> are name and value
	 */
	public static Map<Integer, String> getNameToValueMap() {
		Map<Integer, String> nameToValue = new HashMap<>();
		for (FramingDropdown framingDropdown : FramingDropdown.values()) {
			nameToValue.put(framingDropdown.getValue(), framingDropdown.getName());
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
		for (FramingDropdown framingDropdown : FramingDropdown.values()) {
			list.add(framingDropdown.getName());
		}
		return list.toArray(new String[list.size()]);
	}
}
