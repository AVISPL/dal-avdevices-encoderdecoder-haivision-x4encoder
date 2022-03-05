/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.video.VideoDeserializer;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.video.VideoStatistic;

/**
 * Video Response DTO class
 *
 * @author Ivan
 * @version 1.0.0
 * @since 1.0.0
 */
@JsonDeserialize(using = VideoDeserializer.class)
public class VideoResponse {
	private String id;
	private String bitrate;
	private String maxBitrate;
	private String gopSize;
	private String closedCaption;
	private String state;
	private String inputInterface;
	private String codecAlgorithm;
	private String encodingProfile;
	private String chromaSubSampling;
	private String rateControl;
	private String timeCode;
	private String aspectRatio;
	private String resolution;
	private String inputFormat;
	private String name;
	private String cropping;
	private String frameRate;
	private String slices;
	private String intraRefresh;
	private VideoStatistic videoStatistic;

	/**
	 * Framing Video
	 */
	private String gopStructure;


	/**
	 * Retrieves {@code {@link #id}}
	 *
	 * @return value of {@link #id}
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets {@code id}
	 *
	 * @param id the {@code java.lang.String} field
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Retrieves {@code {@link #bitrate}}
	 *
	 * @return value of {@link #bitrate}
	 */
	public String getBitrate() {
		return bitrate;
	}

	/**
	 * Sets {@code bitrate}
	 *
	 * @param bitrate the {@code java.lang.String} field
	 */
	public void setBitrate(String bitrate) {
		this.bitrate = bitrate;
	}

	/**
	 * Retrieves {@code {@link #maxBitrate}}
	 *
	 * @return value of {@link #maxBitrate}
	 */
	public String getMaxBitrate() {
		return maxBitrate;
	}

	/**
	 * Sets {@code maxBitrate}
	 *
	 * @param maxBitrate the {@code java.lang.String} field
	 */
	public void setMaxBitrate(String maxBitrate) {
		this.maxBitrate = maxBitrate;
	}

	/**
	 * Retrieves {@code {@link #gopSize}}
	 *
	 * @return value of {@link #gopSize}
	 */
	public String getGopSize() {
		return gopSize;
	}

	/**
	 * Sets {@code gopSize}
	 *
	 * @param gopSize the {@code java.lang.String} field
	 */
	public void setGopSize(String gopSize) {
		this.gopSize = gopSize;
	}

	/**
	 * Retrieves {@code {@link #closedCaption}}
	 *
	 * @return value of {@link #closedCaption}
	 */
	public String getClosedCaption() {
		return closedCaption;
	}

	/**
	 * Sets {@code closedCaption}
	 *
	 * @param closedCaption the {@code java.lang.String} field
	 */
	public void setClosedCaption(String closedCaption) {
		this.closedCaption = closedCaption;
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
	 * Retrieves {@code {@link #inputInterface}}
	 *
	 * @return value of {@link #inputInterface}
	 */
	public String getInputInterface() {
		return inputInterface;
	}

	/**
	 * Sets {@code inputInterface}
	 *
	 * @param inputInterface the {@code java.lang.String} field
	 */
	public void setInputInterface(String inputInterface) {
		this.inputInterface = inputInterface;
	}

	/**
	 * Retrieves {@code {@link #codecAlgorithm}}
	 *
	 * @return value of {@link #codecAlgorithm}
	 */
	public String getCodecAlgorithm() {
		return codecAlgorithm;
	}

	/**
	 * Sets {@code codecAlgorithm}
	 *
	 * @param codecAlgorithm the {@code java.lang.String} field
	 */
	public void setCodecAlgorithm(String codecAlgorithm) {
		this.codecAlgorithm = codecAlgorithm;
	}

	/**
	 * Retrieves {@code {@link #encodingProfile}}
	 *
	 * @return value of {@link #encodingProfile}
	 */
	public String getEncodingProfile() {
		return encodingProfile;
	}

	/**
	 * Sets {@code encodingProfile}
	 *
	 * @param encodingProfile the {@code java.lang.String} field
	 */
	public void setEncodingProfile(String encodingProfile) {
		this.encodingProfile = encodingProfile;
	}

	/**
	 * Retrieves {@code {@link #chromaSubSampling}}
	 *
	 * @return value of {@link #chromaSubSampling}
	 */
	public String getChromaSubSampling() {
		return chromaSubSampling;
	}

	/**
	 * Sets {@code chromaSubSampling}
	 *
	 * @param chromaSubSampling the {@code java.lang.String} field
	 */
	public void setChromaSubSampling(String chromaSubSampling) {
		this.chromaSubSampling = chromaSubSampling;
	}

	/**
	 * Retrieves {@code {@link #rateControl}}
	 *
	 * @return value of {@link #rateControl}
	 */
	public String getRateControl() {
		return rateControl;
	}

	/**
	 * Sets {@code rateControl}
	 *
	 * @param rateControl the {@code java.lang.String} field
	 */
	public void setRateControl(String rateControl) {
		this.rateControl = rateControl;
	}

	/**
	 * Retrieves {@code {@link #timeCode}}
	 *
	 * @return value of {@link #timeCode}
	 */
	public String getTimeCode() {
		return timeCode;
	}

	/**
	 * Sets {@code timeCode}
	 *
	 * @param timeCode the {@code java.lang.String} field
	 */
	public void setTimeCode(String timeCode) {
		this.timeCode = timeCode;
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
	 * Retrieves {@code {@link #gopStructure}}
	 *
	 * @return value of {@link #gopStructure}
	 */
	public String getGopStructure() {
		return gopStructure;
	}

	/**
	 * Sets {@code gopStructure}
	 *
	 * @param gopStructure the {@code java.lang.String} field
	 */
	public void setGopStructure(String gopStructure) {
		this.gopStructure = gopStructure;
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
	 * Retrieves {@code {@link #name}}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets {@code name}
	 *
	 * @param name the {@code java.lang.String} field
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Retrieves {@code {@link #videoStatistic}}
	 *
	 * @return value of {@link #videoStatistic}
	 */
	public VideoStatistic getVideoStatistic() {
		return videoStatistic;
	}

	/**
	 * Sets {@code videoStatistic}
	 *
	 * @param videoStatistic the {@code com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.video.VideoStatistic} field
	 */
	public void setVideoStatistic(VideoStatistic videoStatistic) {
		this.videoStatistic = videoStatistic;
	}

	/**
	 * Retrieves {@code {@link #cropping}}
	 *
	 * @return value of {@link #cropping}
	 */
	public String getCropping() {
		return cropping;
	}

	/**
	 * Sets {@code cropping}
	 *
	 * @param cropping the {@code java.lang.String} field
	 */
	public void setCropping(String cropping) {
		this.cropping = cropping;
	}

	/**
	 * Retrieves {@code {@link #frameRate}}
	 *
	 * @return value of {@link #frameRate}
	 */
	public String getFrameRate() {
		return frameRate;
	}

	/**
	 * Sets {@code frameRate}
	 *
	 * @param frameRate the {@code java.lang.String} field
	 */
	public void setFrameRate(String frameRate) {
		this.frameRate = frameRate;
	}

	/**
	 * Retrieves {@code {@link #slices}}
	 *
	 * @return value of {@link #slices}
	 */
	public String getSlices() {
		return slices;
	}

	/**
	 * Sets {@code slices}
	 *
	 * @param slices the {@code java.lang.String} field
	 */
	public void setSlices(String slices) {
		this.slices = slices;
	}

	/**
	 * Retrieves {@code {@link #intraRefresh}}
	 *
	 * @return value of {@link #intraRefresh}
	 */
	public String getIntraRefresh() {
		return intraRefresh;
	}

	/**
	 * Sets {@code intraRefresh}
	 *
	 * @param intraRefresh the {@code java.lang.String} field
	 */
	public void setIntraRefresh(String intraRefresh) {
		this.intraRefresh = intraRefresh;
	}

	/**
	 * Convert AudioResponse
	 *
	 * @return payLoad the payload is String by AudioResponse
	 */
	public String payLoad() {
		return "VideoResponse{" +
				"\"id\":\"" +  id + "\"" +
				"\"bitrate\":\"" +  bitrate + "\"" +
				"\"maxBitrate\":\"" +  maxBitrate + "\"" +
				"\"gopSize\":\"" +  gopSize + "\"" +
				"\"closedCaption\":\"" +  closedCaption + "\"" +
				"\"state\":\"" +  state + "\"" +
				"\"inputInterface\":\"" +  inputInterface + "\"" +
				"\"codecAlgorithm\":\"" +  codecAlgorithm + "\"" +
				"\"encodingProfile\":\"" +  encodingProfile + "\"" +
				"\"chromaSubSampling\":\"" +  chromaSubSampling + "\"" +
				"\"rateControl\":\"" +  rateControl + "\"" +
				"\"timeCode\":\"" +  timeCode + "\"" +
				"\"aspectRatio\":\"" +  aspectRatio + "\"" +
				"\"resolution\":\"" +  resolution + "\"" +
				"\"inputFormat\":\"" +  inputFormat + "\"" +
				"\"name\":\"" +  name + "\"" +
				"\"cropping\":\"" +  cropping + "\"" +
				"\"frameRate\":\"" +  frameRate + "\"" +
				"\"slices\":\"" +  slices + "\"" +
				"\"intraRefresh\":\"" +  intraRefresh + "\"" +
				"\"videoStatistic=" + videoStatistic +
				"\"gopStructure\":\"" +  gopStructure + "\"" +
				'}';
	}
}
