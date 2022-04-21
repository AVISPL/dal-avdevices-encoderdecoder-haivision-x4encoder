/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist;

import java.util.HashMap;
import java.util.Map;

/**
 * SRTMode
 *
 * @author Ivan / Symphony Dev Team<br>
 * Created on 3/11/2022
 * @since 1.0.0
 */
public enum EncryptionDropdown {

	NONE("None", 0),
	AES_128("AES-128", 16),
	AES_256("AES-256", 32);

	private final String name;
	private final int value;

	/**
	 * SRTModeDropdown instantiation
	 *
	 * @param name {@code {@link #name}}
	 * @param value {@code {@link #value}}
	 */
	EncryptionDropdown(String name, int value) {
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
	 * Retrieves name to value map of EncryptionDropdown
	 *
	 * @return Map<Integer, String> are map value and name
	 */
	public static Map<Integer, String> getNameToValueMap() {
		Map<Integer, String> nameToValue = new HashMap<>();
		for (EncryptionDropdown encryptionDropdown : EncryptionDropdown.values()) {
			nameToValue.put(encryptionDropdown.getValue(), encryptionDropdown.getName());
		}
		return nameToValue;
	}


	/**
	 * Retrieves name to value map of EncryptionDropdown
	 *
	 * @return Map<String,Integer> are map name and value
	 */
	public static Map<String, Integer> getValueToNameMap() {
		Map<String, Integer> valueToName = new HashMap<>();
		for (EncryptionDropdown encryptionDropdown : EncryptionDropdown.values()) {
			valueToName.put(encryptionDropdown.getName(), encryptionDropdown.getValue());
		}
		return valueToName;
	}
}