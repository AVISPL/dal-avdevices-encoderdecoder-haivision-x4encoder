/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common;

/**
 * OutputControllingMetric class defined the enum for the monitoring process
 *
 * @author Ivan
 * @version 1.0
 * @since 1.0
 */
public enum OutputControllingMetric {

	VIDEO("Video"),
	ADD_VIDEO("AddVideo"),
	AUDIO("Audio"),
	ADD_AUDIO("AddAudio"),
	METADATA("MetaData"),
	PROTOCOL("Protocol"),
	ADDRESS("Address"),
	PORT("Port"),
	TIMING_AND_SHAPING("Timing And Shaping"),
	MTU("MTU [228-1500]"),
	TTL("TTL [1-255]"),
	TOS("TOS"),
	STRANSMIT_SAP("Stransmit SAP"),
	CREATE_STREAM("Create Stream"),
	MODE("Mode"),
	SOURCE_PORT("Source Port"),
	DESTINATION_PORT("Destination Port"),
	NETWORK_ADAPTER("Network Adapter"),
	ENCRYPTION("Encryption"),
	NAME("Name");

	private final String name;

	/**
	 * OutputControllingMetric instantiation
	 *
	 * @param name {@code {@link #name}}
	 */
	OutputControllingMetric(String name) {
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

	/**
	 *
	 * @param name {@code {@link #name}}
	 * @return name of metric
	 * @throws Exception if can not find the enum with name
	 */
	public static OutputControllingMetric getByName(String name) {
		for (OutputControllingMetric metric : OutputControllingMetric.values()) {
			if (metric.getName().equals(name)) {
				return metric;
			}
		}
		throw new IllegalArgumentException("Can not find the enum with name: " + name);
	}
}
