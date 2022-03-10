/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * ChromaSubSamplingDropdown class defined the enum for monitoring and controlling process
 *
 * @author Ivan / Symphony Dev Team<br>
 * Created on 3/8/2022
 * @since 1.0.0
 */
public enum ChromaSubSampling {

	BIT_420_8("4:2:0 8-bit", 1, true, true),
	BIT_420_10("4:2:0 10-bit", 2, false, true),
	BIT_422_8("4:2:2 8-bit", 3, false, false),
	BIT_422_10("4:2:2 10-bit", 4, false, false);

	private final String name;
	private final int value;
	private boolean isBaselineOrMainOrHigh;
	private boolean isMain10OrHigh10;


	/**
	 * ChromaSubSamplingDropdown instantiation
	 *
	 * @param name {@code {@link #name}}
	 * @param value {@code {@link #value}}
	 */
	ChromaSubSampling(String name, int value, boolean isBaselineOrMainOrHigh, boolean isMain) {
		this.name = name;
		this.value = value;
		this.isBaselineOrMainOrHigh = isBaselineOrMainOrHigh;
		this.isMain10OrHigh10 = isMain;
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
	 * Retrieves {@code {@link #isBaselineOrMainOrHigh}}
	 *
	 * @return value of {@link #isBaselineOrMainOrHigh}
	 */
	public boolean isBaselineOrMainOrHigh() {
		return isBaselineOrMainOrHigh;
	}

	/**
	 * Retrieves {@code {@link #isMain10OrHigh10 }}
	 *
	 * @return value of {@link #isMain10OrHigh10}
	 */
	public boolean isMain10OrHigh10() {
		return isMain10OrHigh10;
	}

	/**
	 * Retrieves name to value map of ChromaSubSamplingDropdown
	 *
	 * @return Map<Integer, String> are map value and name
	 */
	public static Map<Integer, String> getNameToValueMap() {
		Map<Integer, String> nameToValue = new HashMap<>();
		for (ChromaSubSampling chromaSubSamplingDropdown : ChromaSubSampling.values()) {
			nameToValue.put(chromaSubSamplingDropdown.getValue(), chromaSubSamplingDropdown.getName());
		}
		return nameToValue;
	}

	/**
	 * Retrieves name to value map of chromaSubSamplingDropdown
	 *
	 * @return Map<String, Integer> are map name and value
	 */
	public static Map<String, Integer> getValueToNameMap() {
		Map<String, Integer> valueToName = new HashMap<>();
		for (ChromaSubSampling chromaSubSamplingDropdown : ChromaSubSampling.values()) {
			valueToName.put(chromaSubSamplingDropdown.getName(), chromaSubSamplingDropdown.getValue());
		}
		return valueToName;
	}

	/**
	 * Retrieves all name of ChromaSubSampling with mode EncodingProfile is Main 10 or High 10
	 *
	 * @return list name of ChromaSubSampling
	 */
	public static String[] namesIsMain10OrHigh10() {
		List<String> list = new LinkedList<>();
		for (ChromaSubSampling chromaSubSampling : ChromaSubSampling.values()) {
			if (chromaSubSampling.isMain10OrHigh10()) {
				list.add(chromaSubSampling.getName());
			}
		}
		return list.toArray(new String[list.size()]);
	}

	/**
	 * Retrieves all name of ChromaSubSampling with mode EncodingProfile is Baseline or Main or High
	 *
	 * @return list name of ChromaSubSampling
	 */
	public static String[] namesIsBaselineOrMainOrHigh() {
		List<String> list = new LinkedList<>();
		for (ChromaSubSampling chromaSubSampling : ChromaSubSampling.values()) {
			if (chromaSubSampling.isBaselineOrMainOrHigh()) {
				list.add(chromaSubSampling.getName());
			}
		}
		return list.toArray(new String[list.size()]);
	}
}
