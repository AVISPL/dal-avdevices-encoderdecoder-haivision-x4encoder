/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common;

/**
 * AudioControllingMetric class defined the enum for the monitoring process
 *
 * @author Ivan
 * @version 1.0
 * @since 1.0
 */
public enum AudioControllingMetric {

	STATE("State"),
	INPUT("Input"),
	CHANGE_MODE("ChangeMode"),
	BITRATE("BitRate"),
	SAMPLE_RATE("SampleRate"),
	ALGORITHM("ACC-LC Algorithm"),
	ACTION("Action"),
	APPLY_CHANGE("ApplyChange"),
	LANGUAGE("Language"),
	CANCEL("Cancel");

	private final String name;

	/**
	 * AudioControllingMetric instantiation
	 *
	 * @param name {@code {@link #name}}
	 */
	AudioControllingMetric(String name) {
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
	public static AudioControllingMetric getByName(String name) {
		for (AudioControllingMetric metric : AudioControllingMetric.values()) {
			if (metric.getName().equals(name)) {
				return metric;
			}
		}
		throw new IllegalArgumentException("Can not find the enum with name: " + name);
	}
}
