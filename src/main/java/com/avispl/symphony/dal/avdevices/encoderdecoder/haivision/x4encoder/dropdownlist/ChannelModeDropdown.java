/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * ChannelModeDropdown class defined the enum for monitoring and controlling process
 *
 * @author Ivan
 * @version 1.0.0
 * @since 1.0.0
 */
public enum ChannelModeDropdown {

	STEREO("Stereo", 0),
	MONO_LEFT("Mono Left", 1),
	MONO_RIGHT("Mono Right", 2);

	private final String name;

	private final int value;

	/**
	 * ChannelModeDropdown instantiation
	 *
	 * @param name {@code {@link #name}}
	 * @param value {@code {@link #value}}
	 */
	ChannelModeDropdown(String name, int value) {
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
	 * Retrieves name to value map of ChannelModeDropdown
	 *
	 * @return Map<Integer, String> are name and value
	 */
	public static Map<Integer, String> getNameToValueMap() {
		Map<Integer, String> nameToValue = new HashMap<>();
		for (ChannelModeDropdown channelModeDropdown : ChannelModeDropdown.values()) {
			nameToValue.put(channelModeDropdown.getValue(), channelModeDropdown.getName());
		}
		return nameToValue;
	}

	/**
	 * Retrieves all name of ChannelModeDropdown
	 *
	 * @return list name of ChannelMode
	 */
	public static String[] names() {
		List<String> list = new LinkedList<>();
		for (ChannelModeDropdown channelModeDropdown : ChannelModeDropdown.values()) {
			list.add(channelModeDropdown.getName());
		}
		return list.toArray(new String[list.size()]);
	}
}
