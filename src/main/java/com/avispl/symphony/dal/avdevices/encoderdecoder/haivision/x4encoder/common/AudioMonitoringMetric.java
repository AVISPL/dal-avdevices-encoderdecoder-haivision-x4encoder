/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common;

/**
 * AudioMonitoringMetric class defined the enum for the monitoring process
 *
 * @author Ivan / Symphony Dev Team<br>
 * Created on 3/8/2022
 * @since 1.0.0
 */
public enum AudioMonitoringMetric {

	STATE("State"),
	ENCODER_PTS("EncoderPTS"),
	ENCODED_BYTES("EncodedBytes"),
	ENCODED_FRAMES("EncodedFrames"),
	STC_SOURCE_INTERFACE("STCSourceInterface"),
	ENCODER_ERRORS("EncoderErrors"),
	ENCODED_BITRATE("EncodedBitrate"),
	MAX_SAMPLE_VALUE("MaxSampleValue"),
	MAX_SAMPLE_VALUE_PERCENTAGE("MaxSampleValuePercentage");

	private final String name;

	/**
	 * AudioMonitoringMetric instantiation
	 *
	 * @param name {@code {@link #name}}
	 */
	AudioMonitoringMetric(String name) {
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