/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist;

/**
 * SlicesDropdown class defined the enum for monitoring and controlling process
 *
 * @author Ivan / Symphony Dev Team<br>
 * Created on 3/8/2022
 * @since 1.0.0
 */
public enum SlicesDropdown {

	SLICES_1("1"),
	SLICES_2("2"),
	SLICES_3("3"),
	SLICES_4("4"),
	SLICES_5("5"),
	SLICES_6("6"),
	SLICES_7("7"),
	SLICES_8("8"),
	SLICES_9("9"),
	SLICES_10("10"),
	SLICES_11("11");

	private final String name;

	/**
	 * SlicesDropdown instantiation
	 *
	 * @param name {@code {@link #name}}
	 */
	SlicesDropdown(String name) {
		this.name = name;
	}

	/**
	 * Retrieves {@code {@link #name}}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}
}
