/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * CodecAlgorithmDropdown class defined the enum for monitoring and controlling process
 *
 * @author Ivan
 * @version 1.0.0
 * @since 1.0.0
 */
public enum CodecAlgorithm {

	H_264("AVC/H.264", 0),
	H_265("HEVC/H.265", 1);

	private final String name;
	private final int value;

	/**
	 * CodecAlgorithmDropdown instantiation
	 *
	 * @param name {@code {@link #name}}
	 * @param value {@code {@link #value}}
	 */
	CodecAlgorithm(String name, int value) {
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
		for (CodecAlgorithm codecAlgorithmDropdown : CodecAlgorithm.values()) {
			nameToValue.put(codecAlgorithmDropdown.getValue(), codecAlgorithmDropdown.getName());
		}
		return nameToValue;
	}

	/**
	 * Retrieves all name of CodecAlgorithmDropdown
	 *
	 * @return list name of CodecAlgorithm
	 */
	public static String[] names() {
		List<String> list = new LinkedList<>();
		for (CodecAlgorithm codecAlgorithm : CodecAlgorithm.values()) {
			list.add(codecAlgorithm.getName());
		}
		return list.toArray(new String[list.size()]);
	}
}
