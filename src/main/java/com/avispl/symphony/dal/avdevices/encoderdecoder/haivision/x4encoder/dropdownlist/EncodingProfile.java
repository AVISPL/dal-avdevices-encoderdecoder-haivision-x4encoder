/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * EncodingProfileDropdown class defined the enum for monitoring and controlling process
 *
 * @author Ivan / Symphony Dev Team<br>
 * Created on 3/8/2022
 * @since 1.0.0
 */
public enum EncodingProfile {

	BASELINE("Baseline", 1, true, false),
	MAIN("Main", 2, true, true),
	HIGH("High", 3, true, false),
	HIGH_10("High 10", 4, true, false),
	HIGH_422("High 4:2:2", 5, true, false),
	MAIN_10("Main 10", 6, false, true),
	MAIN_422_10("Main 4:2:2 10", 7, false, true);

	private final String name;
	private final int value;
	//H.264
	private final boolean isAVG;
	//H.265
	private final boolean isHEVC;

	/**
	 * EncodingProfileDropdown instantiation
	 *
	 * @param name {@code {@link #name}}
	 * @param value {@code {@link #value}}
	 */
	EncodingProfile(String name, int value, boolean isAVG, boolean isHEVC) {
		this.name = name;
		this.value = value;
		this.isAVG = isAVG;
		this.isHEVC = isHEVC;
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
	 * Retrieves {@code {@link #isAVG}}
	 *
	 * @return value of {@link #isAVG}
	 */
	public boolean isAVG() {
		return isAVG;
	}

	/**
	 * Retrieves {@code {@link #isHEVC}}
	 *
	 * @return value of {@link #isHEVC}
	 */
	public boolean isHEVC() {
		return isHEVC;
	}

	/**
	 * Retrieves name to value map of EncodingProfileDropdown
	 *
	 * @return Map<Integer, String> are map value and name
	 */
	public static Map<Integer, String> getNameToValueMap() {
		Map<Integer, String> nameToValue = new HashMap<>();
		for (EncodingProfile encodingProfileDropdown : values()) {
			nameToValue.put(encodingProfileDropdown.getValue(), encodingProfileDropdown.getName());
		}
		return nameToValue;
	}

	/**
	 * Retrieves name to value map of encodingProfileDropdown
	 *
	 * @return Map<String, Integer> are map name and value
	 */
	public static Map<String, Integer> getValueToNameMap() {
		Map<String, Integer> valueToName = new HashMap<>();
		for (EncodingProfile encodingProfileDropdown : values()) {
			valueToName.put(encodingProfileDropdown.getName(), encodingProfileDropdown.getValue());
		}
		return valueToName;
	}

	/**
	 * Retrieves all name of encodingProfile
	 *
	 * @return list name of encodingProfile
	 */
	public static String[] namesIsAVG() {
		List<String> list = new LinkedList<>();
		for (EncodingProfile encodingProfile : values()) {
			if (encodingProfile.isAVG()) {
				list.add(encodingProfile.getName());
			}
		}
		return list.toArray(new String[list.size()]);
	}

	/**
	 * Retrieves all name of encodingProfile
	 *
	 * @return list name of encodingProfile
	 */
	public static String[] namesIsHEVC() {
		List<String> list = new LinkedList<>();
		for (EncodingProfile encodingProfile : values()) {
			if (encodingProfile.isHEVC()) {
				list.add(encodingProfile.getName());
			}
		}
		return list.toArray(new String[list.size()]);
	}
}
