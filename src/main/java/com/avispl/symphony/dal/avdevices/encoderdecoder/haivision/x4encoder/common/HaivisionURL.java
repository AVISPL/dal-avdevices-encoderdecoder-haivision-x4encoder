/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common;

/**
 * HaivisionURL class defined the URL of the Makito X4 Encoder device
 *
 * @author Ivan / Symphony Dev Team<br>
 * Created on 3/8/2022
 * @since 1.0.0
 */
public enum HaivisionURL {

	AUDIO_ENCODER("Audio Encoder", true, "apis/audenc"),
	VIDEO_ENCODER("Video Encoder", true, "apis/videnc"),
	OUTPUT_ENCODER("Output Encoder", true, "apis/streams"),
	SYSTEM_INFO_STATUS("System Info Status", true, "apis/status"),
	AUTHENTICATION("Authentication", false, "/apis/authentication"),
	STREAM("STREAM", false, "apis/streams"),
	ROLE_BASED("Role Based", false, "apis/accounts/");

	private final String name;
	private boolean isMonitor;
	private String url;

	/**
	 * MakitoMonitoringMetric instantiation
	 *
	 * @param name {@code {@link #name}}
	 */
	HaivisionURL(String name, boolean isMonitor,String url) {
		this.name = name;
		this.isMonitor = isMonitor;
		this.url = url;
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
	 * Retrieves {@code {@link #isMonitor}}
	 *
	 * @return value of {@link #isMonitor}
	 */
	public boolean isMonitor() {
		return isMonitor;
	}

	/**
	 * Retrieves {@code {@link #url}}
	 *
	 * @return value of {@link #url}
	 */
	public String getUrl() {
		return url;
	}
}