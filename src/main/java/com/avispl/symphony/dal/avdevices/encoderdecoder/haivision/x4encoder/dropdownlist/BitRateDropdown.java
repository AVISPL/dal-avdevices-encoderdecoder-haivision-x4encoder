/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * BitRateDropdown class defined the enum for monitoring and controlling process
 *
 * @author Ivan / Symphony Dev Team<br>
 * Created on 3/8/2022
 * @since 1.0.0
 */
public enum BitRateDropdown {

	NUMBER_12("12 kbps", 12, false, true),
	NUMBER_14("14 kbps", 14, true, false),
	NUMBER_16("16 kbps", 16, true, true),
	NUMBER_24("24 kbps", 24, false, true),
	NUMBER_32("32 kbps", 32, true, true),
	NUMBER_40("40 kbps", 40, false, true),
	NUMBER_48("48 kbps", 48, true, true),
	NUMBER_56("56 kbps", 56, false, true),
	NUMBER_64("64 kbps", 64, true, true),
	NUMBER_80("80 kbps", 80, true, false),
	NUMBER_96("96 kbps", 96, true, true),
	NUMBER_128("128 kbps", 128, true, true),
	NUMBER_160("160 kbps", 160, false, true),
	NUMBER_192("192 kbps", 192, true, true),
	NUMBER_224("224 kbps", 224, false, true),
	NUMBER_256("256 kbps", 256, true, true),
	NUMBER_288("288 kbps", 288, false, true),
	NUMBER_320("320 kbps", 320, true, false),
	NUMBER_384("384 kbps", 384, true, false),
	NUMBER_448("448 kbps", 448, true, false),
	NUMBER_512("512 kbps", 512, true, false),
	NUMBER_576("576 kbps", 576, true, false);

	private final String name;
	private final int value;
	private final boolean isStereo;
	private final boolean isMono;

	/**
	 * BitRateDropdown instantiation
	 *
	 * @param name {@code {@link #name}}
	 * @param value {@code {@link #value}}
	 * @param isStereo {@code {@link #isStereo}}
	 * @param isMono {@code {@link #isMono}}
	 */
	BitRateDropdown(String name, int value, boolean isStereo, boolean isMono) {
		this.name = name;
		this.value = value;
		this.isStereo = isStereo;
		this.isMono = isMono;
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
	 * Retrieves {@code {@link #isStereo}}
	 *
	 * @return value of {@link #isStereo}
	 */
	public boolean isStereo() {
		return isStereo;
	}

	/**
	 * Retrieves {@code {@link #isMono}}
	 *
	 * @return value of {@link #isMono}
	 */
	public boolean isMono() {
		return isMono;
	}

	/**
	 * Retrieves all name of BitRateDropdown is stereo mode
	 *
	 * @return list name of BitRate
	 */
	public static String[] namesIsStereo() {
		List<String> list = new LinkedList<>();
		for (BitRateDropdown bitRateDropdown : values()) {
			if (bitRateDropdown.isStereo()) {
				list.add(bitRateDropdown.getName());
			}
		}
		return list.toArray(new String[list.size()]);
	}

	/**
	 * Retrieves all name of BitRateDropdown is mono mode
	 *
	 * @return list name of BitRate
	 */
	public static String[] namesIsMono() {
		List<String> list = new LinkedList<>();
		for (BitRateDropdown bitRateDropdown : values()) {
			if (bitRateDropdown.isMono()) {
				list.add(bitRateDropdown.getName());
			}
		}
		return list.toArray(new String[list.size()]);
	}

	/**
	 * Retrieves name to value map of bitRateDropdown
	 *
	 * @return Map<Integer, String> are map value and name
	 */
	public static Map<Integer, String> getNameToValueMap() {
		Map<Integer, String> nameToValue = new HashMap<>();
		for (BitRateDropdown bitRateDropdown : values()) {
			nameToValue.put(bitRateDropdown.getValue(), bitRateDropdown.getName());
		}
		return nameToValue;
	}

	/**
	 * Retrieves name to value map of bitRateDropdown
	 *
	 * @return Map<String, Integer> are map name and value
	 */
	public static Map<String, Integer> getValueToNameMap() {
		Map<String, Integer> valueToName = new HashMap<>();
		for (BitRateDropdown bitRateDropdown : values()) {
			valueToName.put(bitRateDropdown.getName(), bitRateDropdown.getValue());
		}
		return valueToName;
	}

	/**
	 * Check a BitRate is stereo or not
	 *
	 * @param value value of BitRate
	 */
	public static boolean checkIsStereoByValue(int value) {
		for (BitRateDropdown bitRateDropdown : values()) {
			if (bitRateDropdown.getValue() == value && bitRateDropdown.isStereo()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check a BitRate is mono or not
	 *
	 * @param value value of BitRate
	 */
	public static boolean checkIsMonoByValue(int value) {
		for (BitRateDropdown bitRateDropdown : values()) {
			if (bitRateDropdown.getValue() == value && bitRateDropdown.isMono()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get default bitrate value of mono
	 *
	 * @return default bitrate value of mono
	 */
	public static int getDefaultValueOfMono() {
		int defaultValue = 0;
		for (BitRateDropdown bitRateDropdown : values()) {
			if (bitRateDropdown.isMono()) {
				defaultValue = bitRateDropdown.value;
				break;
			}
		}
		return defaultValue;
	}

	/**
	 * Get default bitrate value of stereo
	 *
	 * @return default bitrate value of stereo
	 */
	public static int getDefaultValueOfStereo() {
		int defaultValue = 0;
		for (BitRateDropdown bitRateDropdown : values()) {
			if (bitRateDropdown.isStereo()) {
				defaultValue = bitRateDropdown.value;
				break;
			}
		}
		return defaultValue;
	}

	/**
	 * Get name by value
	 *
	 * @param value the value is value of metric
	 * @return name the name is name of metric
	 */
	public static String getNameFromValue(int value) {
		String name = "";
		for (BitRateDropdown bitrate : values()) {
			if (bitrate.getValue() == value) {
				name = bitrate.getName();
				break;
			}
		}
		return name;
	}
}
