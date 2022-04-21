/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.video;

import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.HaivisionConstant;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.VideoMonitoringMetric;

/**
 * Video Statistic DTO class
 *
 * @author Ivan / Symphony Dev Team<br>
 * Created on 3/8/2022
 * @since 1.0.0
 */
public class VideoStatistic {
	private String uptime;
	private String inputPresent;
	private String inputFormat;
	private String inputFormatShort;
	private String inputFormatU64;
	private String inputFormatWithoutFramerateU64;
	private String inputFormatIsDetailed;
	private String inputFormatWidth;
	private String inputFormatHeight;
	private String inputFormatFrameRate;
	private String inputFormatIsInterlaced;
	private String inputFormatIsProgressive;
	private String resolution;
	private String resolutionIsDetailed;
	private String resolutionWidth;
	private String resolutionHeight;
	private String resolutionFrameRate;
	private String resolutionIsInterlaced;
	private String resolutionIsProgressive;
	private String aspectRatio;
	private String encodedFrames;
	private String encodedBytes;
	private String encodedFrameRate;
	private String encoderResets;
	private String encodedBitRate;
	private String encoderLoad;
	private String closedCaptioning;
	private String cCErrors;
	private String extractedCSDBytes;
	private String inputColourPrimaries;
	private String inputTransferCharacteristics;
	private String inputMatrixCoefficients;
	private String state;
	private String occurred;
	private String droppedFrames;
	private String inputColorPrimaries;

	/**
	 * Retrieves {@code {@link #uptime}}
	 *
	 * @return value of {@link #uptime}
	 */
	public String getUptime() {
		return uptime;
	}

	/**
	 * Sets {@code uptime}
	 *
	 * @param uptime the {@code java.lang.String} field
	 */
	public void setUptime(String uptime) {
		this.uptime = uptime;
	}

	/**
	 * Retrieves {@code {@link #inputPresent}}
	 *
	 * @return value of {@link #inputPresent}
	 */
	public String getInputPresent() {
		return inputPresent;
	}

	/**
	 * Sets {@code inputPresent}
	 *
	 * @param inputPresent the {@code java.lang.String} field
	 */
	public void setInputPresent(String inputPresent) {
		this.inputPresent = inputPresent;
	}

	/**
	 * Retrieves {@code {@link #inputFormat}}
	 *
	 * @return value of {@link #inputFormat}
	 */
	public String getInputFormat() {
		return inputFormat;
	}

	/**
	 * Sets {@code inputFormat}
	 *
	 * @param inputFormat the {@code java.lang.String} field
	 */
	public void setInputFormat(String inputFormat) {
		this.inputFormat = inputFormat;
	}

	/**
	 * Retrieves {@code {@link #inputFormatShort}}
	 *
	 * @return value of {@link #inputFormatShort}
	 */
	public String getInputFormatShort() {
		return inputFormatShort;
	}

	/**
	 * Sets {@code inputFormatShort}
	 *
	 * @param inputFormatShort the {@code java.lang.String} field
	 */
	public void setInputFormatShort(String inputFormatShort) {
		this.inputFormatShort = inputFormatShort;
	}

	/**
	 * Retrieves {@code {@link #inputFormatU64}}
	 *
	 * @return value of {@link #inputFormatU64}
	 */
	public String getInputFormatU64() {
		return inputFormatU64;
	}

	/**
	 * Sets {@code inputFormatU64}
	 *
	 * @param inputFormatU64 the {@code java.lang.String} field
	 */
	public void setInputFormatU64(String inputFormatU64) {
		this.inputFormatU64 = inputFormatU64;
	}

	/**
	 * Retrieves {@code {@link #inputFormatWithoutFramerateU64}}
	 *
	 * @return value of {@link #inputFormatWithoutFramerateU64}
	 */
	public String getInputFormatWithoutFramerateU64() {
		return inputFormatWithoutFramerateU64;
	}

	/**
	 * Sets {@code inputFormatWithoutFramerateU64}
	 *
	 * @param inputFormatWithoutFramerateU64 the {@code java.lang.String} field
	 */
	public void setInputFormatWithoutFramerateU64(String inputFormatWithoutFramerateU64) {
		this.inputFormatWithoutFramerateU64 = inputFormatWithoutFramerateU64;
	}

	/**
	 * Retrieves {@code {@link #inputFormatIsDetailed}}
	 *
	 * @return value of {@link #inputFormatIsDetailed}
	 */
	public String getInputFormatIsDetailed() {
		return inputFormatIsDetailed;
	}

	/**
	 * Sets {@code inputFormatIsDetailed}
	 *
	 * @param inputFormatIsDetailed the {@code java.lang.String} field
	 */
	public void setInputFormatIsDetailed(String inputFormatIsDetailed) {
		this.inputFormatIsDetailed = inputFormatIsDetailed;
	}

	/**
	 * Retrieves {@code {@link #inputFormatWidth}}
	 *
	 * @return value of {@link #inputFormatWidth}
	 */
	public String getInputFormatWidth() {
		return inputFormatWidth;
	}

	/**
	 * Sets {@code inputFormatWidth}
	 *
	 * @param inputFormatWidth the {@code java.lang.String} field
	 */
	public void setInputFormatWidth(String inputFormatWidth) {
		this.inputFormatWidth = inputFormatWidth;
	}

	/**
	 * Retrieves {@code {@link #inputFormatHeight}}
	 *
	 * @return value of {@link #inputFormatHeight}
	 */
	public String getInputFormatHeight() {
		return inputFormatHeight;
	}

	/**
	 * Sets {@code inputFormatHeight}
	 *
	 * @param inputFormatHeight the {@code java.lang.String} field
	 */
	public void setInputFormatHeight(String inputFormatHeight) {
		this.inputFormatHeight = inputFormatHeight;
	}

	/**
	 * Retrieves {@code {@link #inputFormatFrameRate}}
	 *
	 * @return value of {@link #inputFormatFrameRate}
	 */
	public String getInputFormatFrameRate() {
		return inputFormatFrameRate;
	}

	/**
	 * Sets {@code inputFormatFrameRate}
	 *
	 * @param inputFormatFrameRate the {@code java.lang.String} field
	 */
	public void setInputFormatFrameRate(String inputFormatFrameRate) {
		this.inputFormatFrameRate = inputFormatFrameRate;
	}

	/**
	 * Retrieves {@code {@link #inputFormatIsInterlaced}}
	 *
	 * @return value of {@link #inputFormatIsInterlaced}
	 */
	public String getInputFormatIsInterlaced() {
		return inputFormatIsInterlaced;
	}

	/**
	 * Sets {@code inputFormatIsInterlaced}
	 *
	 * @param inputFormatIsInterlaced the {@code java.lang.String} field
	 */
	public void setInputFormatIsInterlaced(String inputFormatIsInterlaced) {
		this.inputFormatIsInterlaced = inputFormatIsInterlaced;
	}

	/**
	 * Retrieves {@code {@link #inputFormatIsProgressive}}
	 *
	 * @return value of {@link #inputFormatIsProgressive}
	 */
	public String getInputFormatIsProgressive() {
		return inputFormatIsProgressive;
	}

	/**
	 * Sets {@code inputFormatIsProgressive}
	 *
	 * @param inputFormatIsProgressive the {@code java.lang.String} field
	 */
	public void setInputFormatIsProgressive(String inputFormatIsProgressive) {
		this.inputFormatIsProgressive = inputFormatIsProgressive;
	}

	/**
	 * Retrieves {@code {@link #resolution}}
	 *
	 * @return value of {@link #resolution}
	 */
	public String getResolution() {
		return resolution;
	}

	/**
	 * Sets {@code resolution}
	 *
	 * @param resolution the {@code java.lang.String} field
	 */
	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	/**
	 * Retrieves {@code {@link #resolutionIsDetailed}}
	 *
	 * @return value of {@link #resolutionIsDetailed}
	 */
	public String getResolutionIsDetailed() {
		return resolutionIsDetailed;
	}

	/**
	 * Sets {@code resolutionIsDetailed}
	 *
	 * @param resolutionIsDetailed the {@code java.lang.String} field
	 */
	public void setResolutionIsDetailed(String resolutionIsDetailed) {
		this.resolutionIsDetailed = resolutionIsDetailed;
	}

	/**
	 * Retrieves {@code {@link #resolutionWidth}}
	 *
	 * @return value of {@link #resolutionWidth}
	 */
	public String getResolutionWidth() {
		return resolutionWidth;
	}

	/**
	 * Sets {@code resolutionWidth}
	 *
	 * @param resolutionWidth the {@code java.lang.String} field
	 */
	public void setResolutionWidth(String resolutionWidth) {
		this.resolutionWidth = resolutionWidth;
	}

	/**
	 * Retrieves {@code {@link #resolutionHeight}}
	 *
	 * @return value of {@link #resolutionHeight}
	 */
	public String getResolutionHeight() {
		return resolutionHeight;
	}

	/**
	 * Sets {@code resolutionHeight}
	 *
	 * @param resolutionHeight the {@code java.lang.String} field
	 */
	public void setResolutionHeight(String resolutionHeight) {
		this.resolutionHeight = resolutionHeight;
	}

	/**
	 * Retrieves {@code {@link #resolutionFrameRate}}
	 *
	 * @return value of {@link #resolutionFrameRate}
	 */
	public String getResolutionFrameRate() {
		return resolutionFrameRate;
	}

	/**
	 * Sets {@code resolutionFrameRate}
	 *
	 * @param resolutionFrameRate the {@code java.lang.String} field
	 */
	public void setResolutionFrameRate(String resolutionFrameRate) {
		this.resolutionFrameRate = resolutionFrameRate;
	}

	/**
	 * Retrieves {@code {@link #resolutionIsInterlaced}}
	 *
	 * @return value of {@link #resolutionIsInterlaced}
	 */
	public String getResolutionIsInterlaced() {
		return resolutionIsInterlaced;
	}

	/**
	 * Sets {@code resolutionIsInterlaced}
	 *
	 * @param resolutionIsInterlaced the {@code java.lang.String} field
	 */
	public void setResolutionIsInterlaced(String resolutionIsInterlaced) {
		this.resolutionIsInterlaced = resolutionIsInterlaced;
	}

	/**
	 * Retrieves {@code {@link #resolutionIsProgressive}}
	 *
	 * @return value of {@link #resolutionIsProgressive}
	 */
	public String getResolutionIsProgressive() {
		return resolutionIsProgressive;
	}

	/**
	 * Sets {@code resolutionIsProgressive}
	 *
	 * @param resolutionIsProgressive the {@code java.lang.String} field
	 */
	public void setResolutionIsProgressive(String resolutionIsProgressive) {
		this.resolutionIsProgressive = resolutionIsProgressive;
	}

	/**
	 * Retrieves {@code {@link #aspectRatio}}
	 *
	 * @return value of {@link #aspectRatio}
	 */
	public String getAspectRatio() {
		return aspectRatio;
	}

	/**
	 * Sets {@code aspectRatio}
	 *
	 * @param aspectRatio the {@code java.lang.String} field
	 */
	public void setAspectRatio(String aspectRatio) {
		this.aspectRatio = aspectRatio;
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
	 * Retrieves {@code {@link #encodedFrameRate}}
	 *
	 * @return value of {@link #encodedFrameRate}
	 */
	public String getEncodedFrameRate() {
		return encodedFrameRate;
	}

	/**
	 * Sets {@code encodedFrameRate}
	 *
	 * @param encodedFrameRate the {@code java.lang.String} field
	 */
	public void setEncodedFrameRate(String encodedFrameRate) {
		this.encodedFrameRate = encodedFrameRate;
	}

	/**
	 * Retrieves {@code {@link #encoderResets}}
	 *
	 * @return value of {@link #encoderResets}
	 */
	public String getEncoderResets() {
		return encoderResets;
	}

	/**
	 * Sets {@code encoderResets}
	 *
	 * @param encoderResets the {@code java.lang.String} field
	 */
	public void setEncoderResets(String encoderResets) {
		this.encoderResets = encoderResets;
	}

	/**
	 * Retrieves {@code {@link #encodedBitRate}}
	 *
	 * @return value of {@link #encodedBitRate}
	 */
	public String getEncodedBitRate() {
		return encodedBitRate;
	}

	/**
	 * Sets {@code encodedBitRate}
	 *
	 * @param encodedBitRate the {@code java.lang.String} field
	 */
	public void setEncodedBitRate(String encodedBitRate) {
		this.encodedBitRate = encodedBitRate;
	}

	/**
	 * Retrieves {@code {@link #encoderLoad}}
	 *
	 * @return value of {@link #encoderLoad}
	 */
	public String getEncoderLoad() {
		return encoderLoad;
	}

	/**
	 * Sets {@code encoderLoad}
	 *
	 * @param encoderLoad the {@code java.lang.String} field
	 */
	public void setEncoderLoad(String encoderLoad) {
		this.encoderLoad = encoderLoad;
	}

	/**
	 * Retrieves {@code {@link #closedCaptioning}}
	 *
	 * @return value of {@link #closedCaptioning}
	 */
	public String getClosedCaptioning() {
		return closedCaptioning;
	}

	/**
	 * Sets {@code closedCaptioning}
	 *
	 * @param closedCaptioning the {@code java.lang.String} field
	 */
	public void setClosedCaptioning(String closedCaptioning) {
		this.closedCaptioning = closedCaptioning;
	}

	/**
	 * Retrieves {@code {@link #cCErrors }}
	 *
	 * @return value of {@link #cCErrors}
	 */
	public String getcCErrors() {
		return cCErrors;
	}

	/**
	 * Sets {@code CCErrors}
	 *
	 * @param cCErrors the {@code java.lang.String} field
	 */
	public void setcCErrors(String cCErrors) {
		this.cCErrors = cCErrors;
	}

	/**
	 * Retrieves {@code {@link #extractedCSDBytes}}
	 *
	 * @return value of {@link #extractedCSDBytes}
	 */
	public String getExtractedCSDBytes() {
		return extractedCSDBytes;
	}

	/**
	 * Sets {@code extractedCSDBytes}
	 *
	 * @param extractedCSDBytes the {@code java.lang.String} field
	 */
	public void setExtractedCSDBytes(String extractedCSDBytes) {
		this.extractedCSDBytes = extractedCSDBytes;
	}

	/**
	 * Retrieves {@code {@link #inputColourPrimaries}}
	 *
	 * @return value of {@link #inputColourPrimaries}
	 */
	public String getInputColourPrimaries() {
		return inputColourPrimaries;
	}

	/**
	 * Sets {@code inputColourPrimaries}
	 *
	 * @param inputColourPrimaries the {@code java.lang.String} field
	 */
	public void setInputColourPrimaries(String inputColourPrimaries) {
		this.inputColourPrimaries = inputColourPrimaries;
	}

	/**
	 * Retrieves {@code {@link #inputTransferCharacteristics}}
	 *
	 * @return value of {@link #inputTransferCharacteristics}
	 */
	public String getInputTransferCharacteristics() {
		return inputTransferCharacteristics;
	}

	/**
	 * Sets {@code inputTransferCharacteristics}
	 *
	 * @param inputTransferCharacteristics the {@code java.lang.String} field
	 */
	public void setInputTransferCharacteristics(String inputTransferCharacteristics) {
		this.inputTransferCharacteristics = inputTransferCharacteristics;
	}

	/**
	 * Retrieves {@code {@link #inputMatrixCoefficients}}
	 *
	 * @return value of {@link #inputMatrixCoefficients}
	 */
	public String getInputMatrixCoefficients() {
		return inputMatrixCoefficients;
	}

	/**
	 * Sets {@code inputMatrixCoefficients}
	 *
	 * @param inputMatrixCoefficients the {@code java.lang.String} field
	 */
	public void setInputMatrixCoefficients(String inputMatrixCoefficients) {
		this.inputMatrixCoefficients = inputMatrixCoefficients;
	}

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
	 * Retrieves {@code {@link #occurred}}
	 *
	 * @return value of {@link #occurred}
	 */
	public String getOccurred() {
		return occurred;
	}

	/**
	 * Sets {@code occurred}
	 *
	 * @param occurred the {@code java.lang.String} field
	 */
	public void setOccurred(String occurred) {
		this.occurred = occurred;
	}

	/**
	 * Retrieves {@code {@link #droppedFrames}}
	 *
	 * @return value of {@link #droppedFrames}
	 */
	public String getDroppedFrames() {
		return droppedFrames;
	}

	/**
	 * Sets {@code droppedFrames}
	 *
	 * @param droppedFrames the {@code java.lang.String} field
	 */
	public void setDroppedFrames(String droppedFrames) {
		this.droppedFrames = droppedFrames;
	}

	/**
	 * Retrieves {@code {@link #inputColorPrimaries}}
	 *
	 * @return value of {@link #inputColorPrimaries}
	 */
	public String getInputColorPrimaries() {
		return inputColorPrimaries;
	}

	/**
	 * Sets {@code inputColorPrimaries}
	 *
	 * @param inputColorPrimaries the {@code java.lang.String} field
	 */
	public void setInputColorPrimaries(String inputColorPrimaries) {
		this.inputColorPrimaries = inputColorPrimaries;
	}

	/**
	 * Get the value by the metric monitoring
	 *
	 * @param metric the metric is metric monitoring
	 * @return String value of encoder monitoring properties by metric
	 */
	public String getValueByMetric(VideoMonitoringMetric metric) {
		switch (metric) {
			case STATE:
				return getState();
			case UPTIME:
				return getUptime();
			case INPUT_PRESENT:
				return getInputPresent();
			case INPUT_FORMAT:
				return getInputFormat();
			case INPUT_FORMAT_SHORT:
				return getInputFormatShort();
			case INPUT_FORMAT_U64:
				return getInputFormatU64();
			case INPUT_FORMAT_WITHOUT_FRAMERATE_U64:
				return getInputFormatWithoutFramerateU64();
			case INPUT_FORMAT_IS_DETAILED:
				return getInputFormatIsDetailed();
			case INPUT_FORMAT_WIDTH:
				return getInputFormatWidth();
			case INPUT_FORMAT_HEIGHT:
				return getInputFormatHeight();
			case INPUT_FORMAT_FRAMERATE:
				return getInputFormatFrameRate();
			case INPUT_FORMAT_IS_INTERLACED:
				return getInputFormatIsInterlaced();
			case RESOLUTION:
				return getResolution();
			case RESOLUTION_IS_DETAILED:
				return getResolutionIsDetailed();
			case RESOLUTION_WIDTH:
				return getResolutionWidth();
			case RESOLUTION_HEIGHT:
				return getResolutionHeight();
			case RESOLUTION_FRAMERATE:
				return getResolutionFrameRate();
			case RESOLUTION_IS_INTERLACED:
				return getResolutionIsInterlaced();
			case RESOLUTION_IS_PROGRESSIVE:
				return getResolutionIsProgressive();
			case ASPECT_RATIO:
				return getAspectRatio();
			case ENCODED_FRAMES_VIDEO:
				return getEncodedFrames();
			case ENCODED_BYTES_VIDEO:
				return getEncodedBytes();
			case ENCODED_FRAMERATE:
				return getEncodedFrameRate();
			case DROPPED_FRAMERATE:
				return getDroppedFrames();
			case ENCODER_RESETS:
				return getEncoderResets();
			case ENCODED_BITRATE_VIDEO:
				return getEncodedBitRate();
			case ENCODER_LOAD:
				return getEncoderLoad();
			case CLOSED_CAPTIONING:
				return getClosedCaptioning();
			case EXTRACTED_CSD_BYTES:
				return getExtractedCSDBytes();
			case INPUT_COLOUR_PRIMARIES:
				return getInputColourPrimaries();
			case INPUT_COLOR_PRIMARIES:
			return getInputColorPrimaries();
			case CC_ERRORS:
				return getcCErrors();
			case INPUT_TRANSFER_CHARACTERISTICS:
				return getInputTransferCharacteristics();
			case INPUT_MATRIX_COEFFICIENTS:
				return getInputMatrixCoefficients();
			case OCCURRED:
				return getOccurred();
			case INPUT_FORMAT_IS_PROGRESSIVE:
				return getInputFormatIsProgressive();
			default:
				return HaivisionConstant.NONE;
		}
	}
}
