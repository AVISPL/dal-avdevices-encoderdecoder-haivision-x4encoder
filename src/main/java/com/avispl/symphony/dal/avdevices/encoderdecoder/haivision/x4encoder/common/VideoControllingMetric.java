/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common;

/**
 * VideoControllingMetric class defined the enum for the monitoring process
 *
 * @author Ivan
 * @version 1.0
 * @since 1.0
 */
public enum VideoControllingMetric {

	STATE("State"),
	INPUT_FORMAT("InputFormat"),
	CODEC_ALGORITHM("CodecAlgorithm"),
	ENCODING_PROFILE("EncodingProfile"),
	CHROMA_SUBSAMPLING("ChromaSubSampling"),
	RATE_CONTROL("RateControl"),
	MAX_BITRATE("MaxBitRate"),
	BITRATE("BitRate"),
	RESOLUTION("Resolution"),
	GOP_SIZE("GOPSize"),
	TIME_CODE_SOURCE("TimeCodeSource"),
	ASPECT_RATIO("AspectRatio"),
	CLOSED_CAPTION("ClosedCaption"),
	APPLY_CHANGE("ApplyChange"),
	CROPPING("Resizing"),
	FRAME_RATE("FrameRate");

	private final String name;

	/**
	 * VideoControllingMetric instantiation
	 *
	 * @param name {@code {@link #name}}
	 */
	VideoControllingMetric(String name) {
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
