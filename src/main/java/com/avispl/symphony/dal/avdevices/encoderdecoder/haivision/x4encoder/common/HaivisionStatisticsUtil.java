/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common;

import java.util.Objects;

/**
 * HaivisionStatisticsUtil class support getting the URL by metric
 *
 * @author Ivan
 * @version 1.0
 * @since 1.0
 */
public class HaivisionStatisticsUtil {

	/**
	 * Retrieves the URL for monitoring process
	 *
	 * @param haivisionURL is instance of haivisionURL
	 * @return URL is instance of haivisionURL
	 * @throws Exception if the name is not supported
	 */
	public static String getMonitorURL(HaivisionURL haivisionURL) {
		Objects.requireNonNull(haivisionURL);
		switch (haivisionURL) {
			case AUDIO_ENCODER:
				return HaivisionURL.AUDIO_ENCODER.getUrl();
			case VIDEO_ENCODER:
				return HaivisionURL.VIDEO_ENCODER.getUrl();
			case OUTPUT_ENCODER:
				return HaivisionURL.OUTPUT_ENCODER.getUrl();
			case AUTHENTICATION:
				return HaivisionURL.AUTHENTICATION.getUrl();
			case SYSTEM_INFO_STATUS:
				return HaivisionURL.SYSTEM_INFO_STATUS.getUrl();
			default:
				throw new IllegalArgumentException("Do not support HaivisionStatisticsMetric: " + haivisionURL.name());
		}
	}
}