/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist;

/**
 * TimingAndShaping class defined the enum for monitoring and controlling process
 *
 * @author Ivan / Symphony Dev Team<br>
 * Created on 3/9/2022
 * @since 1.0.0
 */
public enum TimingAndShaping {

	VBR("VBR"),
	CBR("CBR"),
	CVBR("CVBR");

	private final String name;

	/**
	 * TimingAndShaping instantiation
	 *
	 * @param name {@code {@link #name}}
	 */
	TimingAndShaping(String name) {
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