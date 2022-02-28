/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.audio.AudioDeserializer;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.audio.AudioStatistic;

/**
 * Audio Response DTO class
 *
 * @author Ivan
 * @version 1.0.0
 * @since 1.0.0
 */
@JsonDeserialize(using = AudioDeserializer.class)
public class AudioResponse {
	private String id;
	private String interfaceName;
	private String bitRate;
	private String sampleRate;
	private String mode;
	private String state;
	private String algorithm;
	private String name;
	private String lang;
	private AudioStatistic audioStatistic;

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
	 * Retrieves {@code {@link #interfaceName}}
	 *
	 * @return value of {@link #interfaceName}
	 */
	public String getInterfaceName() {
		return interfaceName;
	}

	/**
	 * Sets {@code interfaceName}
	 *
	 * @param interfaceName the {@code java.lang.String} field
	 */
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	/**
	 * Retrieves {@code {@link #bitRate}}
	 *
	 * @return value of {@link #bitRate}
	 */
	public String getBitRate() {
		return bitRate;
	}

	/**
	 * Sets {@code bitRate}
	 *
	 * @param bitRate the {@code java.lang.String} field
	 */
	public void setBitRate(String bitRate) {
		this.bitRate = bitRate;
	}

	/**
	 * Retrieves {@code {@link #sampleRate}}
	 *
	 * @return value of {@link #sampleRate}
	 */
	public String getSampleRate() {
		return sampleRate;
	}

	/**
	 * Sets {@code sampleRate}
	 *
	 * @param sampleRate the {@code java.lang.String} field
	 */
	public void setSampleRate(String sampleRate) {
		this.sampleRate = sampleRate;
	}

	/**
	 * Retrieves {@code {@link #mode}}
	 *
	 * @return value of {@link #mode}
	 */
	public String getMode() {
		return mode;
	}

	/**
	 * Sets {@code mode}
	 *
	 * @param mode the {@code java.lang.String} field
	 */
	public void setMode(String mode) {
		this.mode = mode;
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
	 * Retrieves {@code {@link #algorithm}}
	 *
	 * @return value of {@link #algorithm}
	 */
	public String getAlgorithm() {
		return algorithm;
	}

	/**
	 * Sets {@code algorithm}
	 *
	 * @param algorithm the {@code java.lang.String} field
	 */
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
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
	 * Retrieves {@code {@link #lang}}
	 *
	 * @return value of {@link #lang}
	 */
	public String getLang() {
		return lang;
	}

	/**
	 * Sets {@code lang}
	 *
	 * @param lang the {@code java.lang.String} field
	 */
	public void setLang(String lang) {
		this.lang = lang;
	}

	/**
	 * Retrieves {@code {@link #audioStatistic}}
	 *
	 * @return value of {@link #audioStatistic}
	 */
	public AudioStatistic getAudioStatistic() {
		return audioStatistic;
	}

	/**
	 * Sets {@code audioStatistic}
	 *
	 * @param audioStatistic the {@code com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.audio.AudioStatistic} field
	 */
	public void setAudioStatistic(AudioStatistic audioStatistic) {
		this.audioStatistic = audioStatistic;
	}

	/**
	 * Convert AudioResponse
	 *
	 * @return payLoad the payload is String by AudioResponse
	 */
	public String payLoad() {
		return "AudioResponse{" +
				"id='" + id + '\'' +
				", interface='" + interfaceName + '\'' +
				", bitRate='" + bitRate + '\'' +
				", sampleRate='" + sampleRate + '\'' +
				", mode='" + mode + '\'' +
				", state='" + state + '\'' +
				", algorithm='" + algorithm + '\'' +
				", name='" + name + '\'' +
				", lang='" + lang + '\'' +
				", inputLevel='" + "6" + '\'' +
				'}';
	}
}
