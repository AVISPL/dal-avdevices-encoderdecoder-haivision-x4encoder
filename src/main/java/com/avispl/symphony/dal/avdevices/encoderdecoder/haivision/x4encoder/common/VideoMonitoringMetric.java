/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common;

/**
 * VideoMonitoringMetric class defined the enum for the monitoring process
 *
 * @author Ivan
 * @version 1.0
 * @since 1.0
 */
public enum VideoMonitoringMetric {

	STATE("State"),
	UPTIME("Uptime"),
	INPUT_PRESENT("InputPresent"),
	INPUT_FORMAT("InputFormat"),
	INPUT_FORMAT_SHORT("InputFormatShort"),
	INPUT_FORMAT_U64("InputFormatU64"),
	INPUT_FORMAT_WITHOUT_FRAMERATE_U64("InputFormatWithoutFramerateU64"),
	INPUT_FORMAT_IS_DETAILED("InputFormatIsDetailed"),
	INPUT_FORMAT_WIDTH("InputFormatWidth"),
	INPUT_FORMAT_HEIGHT("InputFormatHeight"),
	INPUT_FORMAT_FRAMERATE("InputFormatFrameRate"),
	INPUT_FORMAT_IS_INTERLACED("InputFormatIsInterlaced"),
	INPUT_FORMAT_IS_PROGRESSIVE("InputFormatIsProgressive"),
	RESOLUTION("Resolution"),
	RESOLUTION_IS_DETAILED("ResolutionIsDetailed"),
	RESOLUTION_WIDTH("ResolutionWidth"),
	RESOLUTION_HEIGHT("ResolutionHeight"),
	RESOLUTION_FRAMERATE("ResolutionFrameRate"),
	RESOLUTION_IS_INTERLACED("ResolutionIsInterlaced"),
	RESOLUTION_IS_PROGRESSIVE("ResolutionIsProgressive"),
	ASPECT_RATIO("AspectRatio"),
	ENCODED_FRAMES_VIDEO("EncodedFrames"),
	ENCODED_BYTES_VIDEO("EncodedBytes"),
	ENCODED_FRAMERATE("EncodedFrameRate"),
	DROPPED_FRAMERATE("DroppedFrames"),
	ENCODER_RESETS("EncoderResets"),
	ENCODED_BITRATE_VIDEO("EncodedBitRate"),
	ENCODER_LOAD("EncoderLoad"),
	CLOSED_CAPTIONING("ClosedCaptioning"),
	CC_ERRORS("CCErrors"),
	EXTRACTED_CSD_BYTES("ExtractedCSDBytes"),
	INPUT_COLOR_PRIMARIES("InputColorPrimaries"),
	INPUT_COLOUR_PRIMARIES("InputColourPrimaries"),
	INPUT_TRANSFER_CHARACTERISTICS("InputTransferCharacteristics"),
	INPUT_MATRIX_COEFFICIENTS("InputMatrixCoefficients"),
	OCCURRED("Occurred");;

	private final String name;

	/**
	 * VideoMonitoringMetric instantiation
	 *
	 * @param name {@code {@link #name}}
	 */
	VideoMonitoringMetric(String name) {
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