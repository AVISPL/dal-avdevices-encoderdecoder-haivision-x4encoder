/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.audio;

import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.HaivisionConstant;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.AudioMonitoringMetric;

/**
 * Audio Statistic DTO class
 *
 * @author Ivan
 * @version 1.0.0
 * @since 1.0.0
 */
public class AudioStatistic {

	private String state;
	private String encoderPTS;
	private String encodedBytes;
	private String encodedFrames;
	private String sTCSourceInterface;
	private String encoderErrors;
	private String encodedBitrate;
	private String maxSampleValue;
	private String maxSampleValuePercentage;

	/**
	 * Retrieves {@code {@link #state}}
	 *
	 * @return value of {@link #state}
	 */
	public String getState() {
		return state;
	}

	/**
	 * Sets {@code state}
	 *
	 * @param state the {@code java.lang.String} field
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * Retrieves {@code {@link #encoderPTS}}
	 *
	 * @return value of {@link #encoderPTS}
	 */
	public String getEncoderPTS() {
		return encoderPTS;
	}

	/**
	 * Sets {@code encoderPTS}
	 *
	 * @param encoderPTS the {@code java.lang.String} field
	 */
	public void setEncoderPTS(String encoderPTS) {
		this.encoderPTS = encoderPTS;
	}

	/**
	 * Retrieves {@code {@link #encodedBytes}}
	 *
	 * @return value of {@link #encodedBytes}
	 */
	public String getEncodedBytes() {
		return encodedBytes;
	}

	/**
	 * Sets {@code encodedBytes}
	 *
	 * @param encodedBytes the {@code java.lang.String} field
	 */
	public void setEncodedBytes(String encodedBytes) {
		this.encodedBytes = encodedBytes;
	}

	/**
	 * Retrieves {@code {@link #encodedFrames}}
	 *
	 * @return value of {@link #encodedFrames}
	 */
	public String getEncodedFrames() {
		return encodedFrames;
	}

	/**
	 * Sets {@code encodedFrames}
	 *
	 * @param encodedFrames the {@code java.lang.String} field
	 */
	public void setEncodedFrames(String encodedFrames) {
		this.encodedFrames = encodedFrames;
	}

	/**
	 * Retrieves {@code {@link #sTCSourceInterface}}
	 *
	 * @return value of {@link #sTCSourceInterface}
	 */
	public String getsTCSourceInterface() {
		return sTCSourceInterface;
	}

	/**
	 * Sets {@code sTCSourceInterface}
	 *
	 * @param sTCSourceInterface the {@code java.lang.String} field
	 */
	public void setsTCSourceInterface(String sTCSourceInterface) {
		this.sTCSourceInterface = sTCSourceInterface;
	}

	/**
	 * Retrieves {@code {@link #encoderErrors}}
	 *
	 * @return value of {@link #encoderErrors}
	 */
	public String getEncoderErrors() {
		return encoderErrors;
	}

	/**
	 * Sets {@code encoderErrors}
	 *
	 * @param encoderErrors the {@code java.lang.String} field
	 */
	public void setEncoderErrors(String encoderErrors) {
		this.encoderErrors = encoderErrors;
	}

	/**
	 * Retrieves {@code {@link #encodedBitrate}}
	 *
	 * @return value of {@link #encodedBitrate}
	 */
	public String getEncodedBitrate() {
		return encodedBitrate;
	}

	/**
	 * Sets {@code encodedBitrate}
	 *
	 * @param encodedBitrate the {@code java.lang.String} field
	 */
	public void setEncodedBitrate(String encodedBitrate) {
		this.encodedBitrate = encodedBitrate;
	}

	/**
	 * Retrieves {@code {@link #maxSampleValue}}
	 *
	 * @return value of {@link #maxSampleValue}
	 */
	public String getMaxSampleValue() {
		return maxSampleValue;
	}

	/**
	 * Sets {@code maxSampleValue}
	 *
	 * @param maxSampleValue the {@code java.lang.String} field
	 */
	public void setMaxSampleValue(String maxSampleValue) {
		this.maxSampleValue = maxSampleValue;
	}

	/**
	 * Retrieves {@code {@link #maxSampleValuePercentage}}
	 *
	 * @return value of {@link #maxSampleValuePercentage}
	 */
	public String getMaxSampleValuePercentage() {
		return maxSampleValuePercentage;
	}

	/**
	 * Sets {@code maxSampleValuePercentage}
	 *
	 * @param maxSampleValuePercentage the {@code java.lang.String} field
	 */
	public void setMaxSampleValuePercentage(String maxSampleValuePercentage) {
		this.maxSampleValuePercentage = maxSampleValuePercentage;
	}

	/**
	 * Get the value by the metric monitoring
	 *
	 * @param metric the metric is metric monitoring
	 * @return String value of encoder monitoring properties by metric
	 */
	public String getValueByMetric(AudioMonitoringMetric metric) {
		switch (metric) {
			case STATE:
				return getState();
			case ENCODER_PTS:
				return getEncoderPTS();
			case ENCODED_BYTES:
				return getEncodedBytes();
			case ENCODED_FRAMES:
				return getEncodedFrames();
			case STC_SOURCE_INTERFACE:
				return getsTCSourceInterface();
			case ENCODER_ERRORS:
				return getEncoderErrors();
			case ENCODED_BITRATE:
				return getEncodedBitrate();
			case MAX_SAMPLE_VALUE:
				return getMaxSampleValue();
			case MAX_SAMPLE_VALUE_PERCENTAGE:
				return getMaxSampleValuePercentage();
			default:
				return HaivisionConstant.NONE;
		}
	}
}
