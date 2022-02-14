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
	 * @param makitoMonitoringMetric is instance of makitoMonitoringMetric
	 * @return URL is instance of AxisURL
	 * @throws Exception if the name is not supported
	 */
	public static String getMonitorURL(HaivisionMonitoringMetric makitoMonitoringMetric) {
		Objects.requireNonNull(makitoMonitoringMetric);
		switch (makitoMonitoringMetric) {
			case AUDIO_ENCODER:
				return HaivisionURL.AUDIO;
			case VIDEO_ENCODER:
				return HaivisionURL.VIDEO;
			case OUTPUT_ENCODER:
				return HaivisionURL.OUTPUTS;
			default:
				throw new IllegalArgumentException("Do not support axisStatisticsMetric: " + makitoMonitoringMetric.name());
		}
	}
}