/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.AudioMonitoringMetric;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.HaivisionConstant;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.HaivisionStatisticsUtil;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.HaivisionURL;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.OutputMonitoringMetric;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.SystemMonitoringMetric;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.VideoMonitoringMetric;

/**
 * Unit test for simple App.
 *
 * @author Ivan
 * @since 1.0.0
 */
class EncoderCommunicatorTest {

	static HaivisionX4EncoderCommunicator haivisionX4EncoderCommunicator;
	private static final int HTTP_PORT = 8088;
	private static final int HTTPS_PORT = 8443;
	private static final String HOST_NAME = "127.0.0.1";
	private static final String PROTOCOL = "http";

	@Rule
	WireMockRule wireMockRule = new WireMockRule(options().port(HTTP_PORT).httpsPort(HTTPS_PORT)
			.bindAddress(HOST_NAME));
	MockedStatic<HaivisionStatisticsUtil> mock = Mockito.mockStatic(HaivisionStatisticsUtil.class);

	@BeforeEach
	public void init() throws Exception {
		wireMockRule.start();
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.ROLE_BASED)).thenReturn(HaivisionURL.ROLE_BASED.getUrl());
		haivisionX4EncoderCommunicator = new HaivisionX4EncoderCommunicator();
		haivisionX4EncoderCommunicator.setTrustAllCertificates(false);
		haivisionX4EncoderCommunicator.setProtocol(PROTOCOL);
		haivisionX4EncoderCommunicator.setPort(wireMockRule.port());
		haivisionX4EncoderCommunicator.setHost(HOST_NAME);
		haivisionX4EncoderCommunicator.setContentType("application/json");
		haivisionX4EncoderCommunicator.setLogin("operator");
		haivisionX4EncoderCommunicator.setPassword("supervisor");
		haivisionX4EncoderCommunicator.init();
		haivisionX4EncoderCommunicator.authenticate();
	}

	@AfterEach
	void stopWireMockRule() {
		haivisionX4EncoderCommunicator.destroy();
		wireMockRule.stop();
		mock.close();
	}

	/**
	 * Test get monitoring data failed all
	 *
	 * Expect get monitoring data failed all and throws exception
	 */
	@Test
	void testFailedMonitoring() {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn("/audio-error");
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn("/video-error");
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn("/output-error");
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		assertThrows(ResourceNotReachableException.class, () -> haivisionX4EncoderCommunicator.getMultipleStatistics(), "Expect failed because retrieve monitoring data failed");
	}

	/**
	 * Test login failed with unauthorized
	 *
	 * Expect login failed and throws exception
	 */
	@Test
	void testUnauthorized() {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn("/authorized");
		assertThrows(ResourceNotReachableException.class, () -> haivisionX4EncoderCommunicator.getMultipleStatistics(), "Login to the device failed,user unauthorized");
	}

	/**
	 * Test retrieve audio encoder statistics success
	 *
	 * Expect retrieve successfully with audio encoder statistics data
	 */
	@Test
	void testRetrieveAudioEncoderStatisticsSuccess() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("Working", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.STATE.getName()));
		Assert.assertEquals("0x1a0348d5c", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.ENCODER_PTS.getName()));
		Assert.assertEquals("24,146,984,454", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.ENCODED_BYTES.getName()));
		Assert.assertEquals("None", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.STC_SOURCE_INTERFACE.getName()));
		Assert.assertEquals("0", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.ENCODER_ERRORS.getName()));
		Assert.assertEquals("128", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.ENCODED_BITRATE.getName()));
		Assert.assertEquals("9693", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.MAX_SAMPLE_VALUE.getName()));
		Assert.assertEquals("29", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.MAX_SAMPLE_VALUE_PERCENTAGE.getName()));
		Assert.assertEquals("Working", stats.get("Audio Encoder 1" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.STATE.getName()));
		Assert.assertEquals("0x000000000", stats.get("Audio Encoder 1" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.ENCODER_PTS.getName()));
		Assert.assertEquals("0", stats.get("Audio Encoder 1" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.ENCODED_BYTES.getName()));
		Assert.assertEquals("None", stats.get("Audio Encoder 1" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.STC_SOURCE_INTERFACE.getName()));
		Assert.assertEquals("0", stats.get("Audio Encoder 1" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.ENCODER_ERRORS.getName()));
		Assert.assertEquals("0", stats.get("Audio Encoder 1" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.ENCODED_BITRATE.getName()));
		Assert.assertEquals("0", stats.get("Audio Encoder 1" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.MAX_SAMPLE_VALUE.getName()));
		Assert.assertEquals("0", stats.get("Audio Encoder 1" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.MAX_SAMPLE_VALUE_PERCENTAGE.getName()));
	}

	/**
	 * Test retrieve audio encoder statistics failed
	 *
	 * Expect retrieve audio encoder statistics failed with empty data
	 */
	@Test
	void testRetrieveAudioEncoderStatisticFailed() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn("/audio");
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.ROLE_BASED)).thenReturn(HaivisionURL.ROLE_BASED.getUrl());
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertNull(stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.STATE.getName()));
		Assert.assertNull(stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.ENCODER_PTS.getName()));
		Assert.assertNull(stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.ENCODED_BYTES.getName()));
		Assert.assertNull(stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.STC_SOURCE_INTERFACE.getName()));
		Assert.assertNull(stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.ENCODER_ERRORS.getName()));
		Assert.assertNull(stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.ENCODED_BITRATE.getName()));
		Assert.assertNull(stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.MAX_SAMPLE_VALUE.getName()));
		Assert.assertNull(stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.MAX_SAMPLE_VALUE_PERCENTAGE.getName()));
	}

	/**
	 * Test retrieve audio encoder statistics success with None data
	 *
	 * Expect retrieve successfully with audio encoder statistics None data
	 */
	@Test
	void testRetrieveAudioEncoderStatisticsNoneData() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn("/audio-none-data");
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("None", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.STATE.getName()));
		Assert.assertEquals("None", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.ENCODER_PTS.getName()));
		Assert.assertEquals("None", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.ENCODED_BYTES.getName()));
		Assert.assertEquals("None", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.STC_SOURCE_INTERFACE.getName()));
		Assert.assertEquals("None", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.ENCODER_ERRORS.getName()));
		Assert.assertEquals("None", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.ENCODED_BITRATE.getName()));
		Assert.assertEquals("None", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.MAX_SAMPLE_VALUE.getName()));
		Assert.assertEquals("None", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.MAX_SAMPLE_VALUE_PERCENTAGE.getName()));
	}

	/**
	 * Test retrieve audio encoder statistics success
	 *
	 * Expect retrieve successfully with audio encoder statistics data
	 */
	@Test
	void testRetrieveVideoEncoderStatisticsSuccess() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("Working",
				stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.STATE.getName()));
		Assert.assertEquals("17 day(s) 13 hour(s) 4 minute(s) 5 second(s)",
				stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.UPTIME.getName()));
		Assert.assertEquals("1", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_PRESENT.getName()));
		Assert.assertEquals("3840x2160p60", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_FORMAT.getName()));
		Assert.assertEquals("3840x2160p60", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_FORMAT_SHORT.getName()));
		Assert.assertEquals("9223372052962009208", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_FORMAT_U64.getName()));
		Assert.assertEquals("9223372052962009088",
				stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_FORMAT_WITHOUT_FRAMERATE_U64.getName()));
		Assert.assertEquals("true", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_FORMAT_IS_DETAILED.getName()));
		Assert.assertEquals("3840", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_FORMAT_WIDTH.getName()));
		Assert.assertEquals("2160", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_FORMAT_HEIGHT.getName()));
		Assert.assertEquals("60", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_FORMAT_FRAMERATE.getName()));
		Assert.assertEquals("false", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_FORMAT_IS_INTERLACED.getName()));
		Assert.assertEquals("true", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_FORMAT_IS_PROGRESSIVE.getName()));
		Assert.assertEquals("1920x1080p", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.RESOLUTION.getName()));
		Assert.assertEquals("1920", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.RESOLUTION_WIDTH.getName()));
		Assert.assertEquals("1080", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.RESOLUTION_HEIGHT.getName()));
		Assert.assertEquals("0", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.RESOLUTION_FRAMERATE.getName()));
		Assert.assertEquals("false", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.RESOLUTION_IS_INTERLACED.getName()));
		Assert.assertEquals("true", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.RESOLUTION_IS_PROGRESSIVE.getName()));
		Assert.assertEquals("16:9", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.ASPECT_RATIO.getName()));
		Assert.assertEquals("26,406,030", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.ENCODED_FRAMES_VIDEO.getName()));
		Assert.assertEquals("0", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.DROPPED_FRAMERATE.getName()));
		Assert.assertEquals("166,222,146,368", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.ENCODED_BYTES.getName()));
		Assert.assertEquals("60", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.ENCODED_FRAMERATE.getName()));
		Assert.assertEquals("0", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.ENCODER_RESETS.getName()));
		Assert.assertEquals("2,954", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.ENCODED_BITRATE_VIDEO.getName()));
		Assert.assertEquals("25", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.ENCODER_LOAD.getName()));
		Assert.assertEquals("0", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.CLOSED_CAPTIONING.getName()));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.CC_ERRORS.getName()));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.EXTRACTED_CSD_BYTES.getName()));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_COLOUR_PRIMARIES.getName()));
		Assert.assertEquals("BT.709", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_COLOR_PRIMARIES.getName()));
		Assert.assertEquals("BT.709", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_TRANSFER_CHARACTERISTICS.getName()));
		Assert.assertEquals("BT.709", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_MATRIX_COEFFICIENTS.getName()));
	}

	/**
	 * Test retrieve audio encoder statistics failed
	 *
	 * Expect retrieve audio encoder statistics failed with empty data
	 */
	@Test
	void testRetrieveVideoEncoderStatisticsFailed() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn("/video");
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.STATE.getName()));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.UPTIME.getName()));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_PRESENT.getName()));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_FORMAT.getName()));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_FORMAT_SHORT.getName()));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_FORMAT_U64.getName()));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_FORMAT_WITHOUT_FRAMERATE_U64.getName()));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_FORMAT_IS_DETAILED.getName()));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_FORMAT_WIDTH.getName()));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_FORMAT_HEIGHT.getName()));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_FORMAT_FRAMERATE.getName()));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_FORMAT_IS_INTERLACED.getName()));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_FORMAT_IS_PROGRESSIVE.getName()));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.RESOLUTION.getName()));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.RESOLUTION_WIDTH.getName()));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.RESOLUTION_HEIGHT.getName()));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.RESOLUTION_FRAMERATE.getName()));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.RESOLUTION_IS_INTERLACED.getName()));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.RESOLUTION_IS_PROGRESSIVE.getName()));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.ASPECT_RATIO.getName()));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.ENCODED_FRAMES_VIDEO.getName()));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.DROPPED_FRAMERATE.getName()));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.ENCODED_BYTES_VIDEO.getName()));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.ENCODED_FRAMERATE.getName()));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.ENCODER_RESETS.getName()));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.ENCODED_BITRATE_VIDEO.getName()));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.ENCODER_LOAD.getName()));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.CLOSED_CAPTIONING.getName()));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.CC_ERRORS.getName()));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.EXTRACTED_CSD_BYTES.getName()));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_COLOUR_PRIMARIES.getName()));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_TRANSFER_CHARACTERISTICS.getName()));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_MATRIX_COEFFICIENTS.getName()));
	}

	/**
	 * Test retrieve audio encoder statistics success with None data
	 *
	 * Expect retrieve successfully with audio encoder statistics with None data
	 */
	@Test
	void testRetrieveVideoEncoderStatisticsNoneData() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn("/video-none-data");
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.STATE.getName()));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.UPTIME.getName()));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_PRESENT.getName()));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_FORMAT.getName()));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_FORMAT_SHORT.getName()));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_FORMAT_U64.getName()));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_FORMAT_WITHOUT_FRAMERATE_U64.getName()));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_FORMAT_IS_DETAILED.getName()));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_FORMAT_WIDTH.getName()));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_FORMAT_HEIGHT.getName()));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_FORMAT_FRAMERATE.getName()));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_FORMAT_IS_INTERLACED.getName()));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_FORMAT_IS_PROGRESSIVE.getName()));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.RESOLUTION.getName()));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.RESOLUTION_WIDTH.getName()));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.RESOLUTION_HEIGHT.getName()));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.RESOLUTION_FRAMERATE.getName()));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.RESOLUTION_IS_INTERLACED.getName()));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.RESOLUTION_IS_PROGRESSIVE.getName()));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.ASPECT_RATIO.getName()));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.ENCODED_FRAMES_VIDEO.getName()));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.DROPPED_FRAMERATE.getName()));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.ENCODED_BYTES_VIDEO.getName()));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.ENCODED_FRAMERATE.getName()));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.ENCODER_RESETS.getName()));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.ENCODED_BITRATE_VIDEO.getName()));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.ENCODER_LOAD.getName()));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.CLOSED_CAPTIONING.getName()));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.CC_ERRORS.getName()));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.EXTRACTED_CSD_BYTES.getName()));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_COLOUR_PRIMARIES.getName()));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_TRANSFER_CHARACTERISTICS.getName()));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.INPUT_MATRIX_COEFFICIENTS.getName()));
	}

	/**
	 * Test retrieve output stream statistics success
	 *
	 * Expect retrieve successfully with output stream statistics data
	 */
	@Test
	void testRetrieveOutStreamStatisticsSuccess() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn("/abc");
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("Connecting", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.STATE.getName()));
		Assert.assertEquals("17 day(s) 19 hour(s) 6 minute(s) 17 second(s)",
				stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.UPTIME.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.SOURCE_PORT.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.SENT_PACKETS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.SENT_BYTES.getName()));
		Assert.assertEquals("0 kbps", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.BITRATE.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RECONNECTIONS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RESENT_PACKETS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RESENT_BYTES.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.DROPPED_PACKETS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.DROPPED_BYTES.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.MSS.getName()));
		Assert.assertEquals("0 kbps", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.MAX_BANDWIDTH.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.REMOTE_PORT.getName()));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.SOURCE_ADDRESS.getName()));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.REMOTE_ADDRESS.getName()));
		Assert.assertEquals("0 kbps", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.PATH_MAX_BANDWIDTH.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.LOST_PACKETS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RECV_ACK.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RECV_NAK.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RTT.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.BUFFER.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.LATENCY.getName()));
		Assert.assertEquals("2 day(s) 23 hour(s) 31 minute(s) 32 second(s)",
				stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.OCCURRED.getName()));
	}

	/**
	 * Test retrieve output stream statistics failed
	 *
	 * Expect retrieve failed with output stream statistics with Empty data
	 */
	@Test
	void testRetrieveOutStreamStatisticsFailed() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn("/output");
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.STATE.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.UPTIME.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.SOURCE_PORT.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.SENT_PACKETS.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.SENT_BYTES.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.BITRATE.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RECONNECTIONS.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RESENT_PACKETS.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RESENT_BYTES.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.DROPPED_PACKETS.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.DROPPED_BYTES.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.MSS.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.MAX_BANDWIDTH.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.REMOTE_PORT.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.SOURCE_ADDRESS.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.REMOTE_ADDRESS.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.PATH_MAX_BANDWIDTH.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.LOST_PACKETS.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RECV_ACK.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RECV_NAK.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RTT.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.BUFFER.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.LATENCY.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.OCCURRED.getName()));
	}

	/**
	 * Test retrieve output stream statistics successfully
	 *
	 * Expect retrieve successfully with output stream statistics with None data
	 */
	@Test
	void testRetrieveOutStreamStatisticsNoneData() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn("/output-none-data");
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.STATE.getName()));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.UPTIME.getName()));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.SOURCE_PORT.getName()));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.SENT_PACKETS.getName()));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.SENT_BYTES.getName()));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.BITRATE.getName()));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RECONNECTIONS.getName()));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RESENT_PACKETS.getName()));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RESENT_BYTES.getName()));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.DROPPED_PACKETS.getName()));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.DROPPED_BYTES.getName()));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.MSS.getName()));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.MAX_BANDWIDTH.getName()));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.REMOTE_PORT.getName()));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.SOURCE_ADDRESS.getName()));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.REMOTE_ADDRESS.getName()));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.PATH_MAX_BANDWIDTH.getName()));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.LOST_PACKETS.getName()));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RECV_ACK.getName()));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RECV_NAK.getName()));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RTT.getName()));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.BUFFER.getName()));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.LATENCY.getName()));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.OCCURRED.getName()));
	}

	/**
	 * Test retrieving system info status with not null and not empty response data
	 *
	 * Expect retrieve successfully with response data
	 */
	@Test
	void testRetrieveSystemInfoStatusWithResponseData() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.SYSTEM_INFO_STATUS)).thenReturn(HaivisionURL.SYSTEM_INFO_STATUS.getUrl());
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("OK", stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.CARD_STATUS.getName()));
		Assert.assertEquals("HAI-031743040010", stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.SERIAL_NUMBER.getName()));
		Assert.assertEquals("-001G", stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.HARDWARE_COMPATIBILITY.getName()));
		Assert.assertEquals("0", stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.MEZZANINE_PRESENT.getName()));
		Assert.assertEquals("A", stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.HARDWARE_REVISION.getName()));
		Assert.assertEquals("4 (Official, Internal flash)", stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.CPL_REVISION.getName()));
		Assert.assertEquals("U-Boot 2018.01 (May 24 2019 - 18:24:54 -0400)",
				stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.BOOT_VERSION.getName()));
		Assert.assertEquals("Makito X4 SDI Encoder", stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.CARD_TYPE.getName()));
		Assert.assertEquals("B-MX4E-SDI4", stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.PART_NUMBER.getName()));
		Assert.assertEquals("Aug 24 2020", stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.FIRMWARE_DATE.getName()));
		Assert.assertEquals("1.2.0-14", stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.FIRMWARE_VERSION.getName()));
		Assert.assertEquals("None", stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.FIRMWARE_OPTIONS.getName()));
		Assert.assertEquals("0 days 21:51:28", stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.UPTIME.getName()));
		Assert.assertEquals("4", stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.CHIPSET_LOAD.getName()));
		Assert.assertEquals("54", stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.TEMPERATURE.getName()));
	}

	/**
	 * Test retrieving system info status with the response data is null
	 *
	 * Expect return data with "None" in each field
	 */
	@Test
	void testRetrieveSystemInfoStatusWithNullResponseData() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.SYSTEM_INFO_STATUS)).thenReturn("/apis/status-empty-data");
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals(HaivisionConstant.NONE, stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.CARD_STATUS.getName()));
		Assert.assertEquals(HaivisionConstant.NONE, stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.SERIAL_NUMBER.getName()));
		Assert.assertEquals(HaivisionConstant.NONE, stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.HARDWARE_COMPATIBILITY.getName()));
		Assert.assertEquals(HaivisionConstant.NONE, stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.MEZZANINE_PRESENT.getName()));
		Assert.assertEquals(HaivisionConstant.NONE, stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.HARDWARE_REVISION.getName()));
		Assert.assertEquals(HaivisionConstant.NONE, stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.CPL_REVISION.getName()));
		Assert.assertEquals(HaivisionConstant.NONE, stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.BOOT_VERSION.getName()));
		Assert.assertEquals(HaivisionConstant.NONE, stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.CARD_TYPE.getName()));
		Assert.assertEquals(HaivisionConstant.NONE, stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.PART_NUMBER.getName()));
		Assert.assertEquals(HaivisionConstant.NONE, stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.FIRMWARE_DATE.getName()));
		Assert.assertEquals(HaivisionConstant.NONE, stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.FIRMWARE_VERSION.getName()));
		Assert.assertEquals(HaivisionConstant.NONE, stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.FIRMWARE_OPTIONS.getName()));
		Assert.assertEquals(HaivisionConstant.NONE, stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.UPTIME.getName()));
		Assert.assertEquals(HaivisionConstant.NONE, stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.CHIPSET_LOAD.getName()));
		Assert.assertEquals(HaivisionConstant.NONE, stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.TEMPERATURE.getName()));
	}

	/**
	 * Test retrieving system info status with error, access to unknown url
	 *
	 * Expect return data with "None" in each field
	 */
	@Test
	void testRetrieveSystemInfoStatusWithError() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.SYSTEM_INFO_STATUS)).thenReturn("/apis/status-unknown-url");
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals(HaivisionConstant.NONE, stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.CARD_STATUS.getName()));
		Assert.assertEquals(HaivisionConstant.NONE, stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.SERIAL_NUMBER.getName()));
		Assert.assertEquals(HaivisionConstant.NONE, stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.HARDWARE_COMPATIBILITY.getName()));
		Assert.assertEquals(HaivisionConstant.NONE, stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.MEZZANINE_PRESENT.getName()));
		Assert.assertEquals(HaivisionConstant.NONE, stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.HARDWARE_REVISION.getName()));
		Assert.assertEquals(HaivisionConstant.NONE, stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.CPL_REVISION.getName()));
		Assert.assertEquals(HaivisionConstant.NONE, stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.BOOT_VERSION.getName()));
		Assert.assertEquals(HaivisionConstant.NONE, stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.CARD_TYPE.getName()));
		Assert.assertEquals(HaivisionConstant.NONE, stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.PART_NUMBER.getName()));
		Assert.assertEquals(HaivisionConstant.NONE, stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.FIRMWARE_DATE.getName()));
		Assert.assertEquals(HaivisionConstant.NONE, stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.FIRMWARE_VERSION.getName()));
		Assert.assertEquals(HaivisionConstant.NONE, stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.FIRMWARE_OPTIONS.getName()));
		Assert.assertEquals(HaivisionConstant.NONE, stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.UPTIME.getName()));
		Assert.assertEquals(HaivisionConstant.NONE, stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.CHIPSET_LOAD.getName()));
		Assert.assertEquals(HaivisionConstant.NONE, stats.get(HaivisionURL.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + SystemMonitoringMetric.TEMPERATURE.getName()));
	}

	/**
	 * Test filter exits stream name
	 *
	 * Expect retrieve successfully with audio encoder statistics
	 */
	@Test
	void testFilterStreamNameWithNameExits() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.setStreamNameFilter("Stream Output 0");
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("Working", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.STATE.getName()));
		Assert.assertEquals("0x1a0348d5c", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.ENCODER_PTS.getName()));
		Assert.assertEquals("24,146,984,454", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.ENCODED_BYTES.getName()));
		Assert.assertEquals("None", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.STC_SOURCE_INTERFACE.getName()));
		Assert.assertEquals("0", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.ENCODER_ERRORS.getName()));
		Assert.assertEquals("128", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.ENCODED_BITRATE.getName()));
		Assert.assertEquals("9693", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.MAX_SAMPLE_VALUE.getName()));
		Assert.assertEquals("29", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.MAX_SAMPLE_VALUE_PERCENTAGE.getName()));
	}

	/**
	 * Test filter exits stream name
	 *
	 * Expect retrieve successfully with output stream
	 */
	@Test
	void testFilterStreamNameWithNameNotExits() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.setStreamNameFilter("Stream Output 1");
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.STATE.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.UPTIME.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.SOURCE_PORT.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.SENT_PACKETS.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.SENT_BYTES.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.BITRATE.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RECONNECTIONS.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RESENT_PACKETS.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RESENT_BYTES.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.DROPPED_PACKETS.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.DROPPED_BYTES.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.MSS.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.MAX_BANDWIDTH.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.REMOTE_PORT.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.SOURCE_ADDRESS.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.REMOTE_ADDRESS.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.PATH_MAX_BANDWIDTH.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.LOST_PACKETS.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RECV_ACK.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RECV_NAK.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RTT.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.BUFFER.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.LATENCY.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.OCCURRED.getName()));
	}

	/**
	 * Test filter exits port number
	 * Expect retrieve successfully with output stream
	 */
	@Test
	void testFilterWithPortNumberExit() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.setPortNumberFilter("6064,6054-64065");
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("Connecting", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.STATE.getName()));
		Assert.assertEquals("17 day(s) 19 hour(s) 6 minute(s) 17 second(s)",
				stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.UPTIME.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.SOURCE_PORT.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.SENT_PACKETS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.SENT_BYTES.getName()));
		Assert.assertEquals("0 kbps", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.BITRATE.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RECONNECTIONS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RESENT_PACKETS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RESENT_BYTES.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.DROPPED_PACKETS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.DROPPED_BYTES.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.MSS.getName()));
		Assert.assertEquals("0 kbps", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.MAX_BANDWIDTH.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.REMOTE_PORT.getName()));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.SOURCE_ADDRESS.getName()));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.REMOTE_ADDRESS.getName()));
		Assert.assertEquals("0 kbps", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.PATH_MAX_BANDWIDTH.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.LOST_PACKETS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RECV_ACK.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RECV_NAK.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RTT.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.BUFFER.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.LATENCY.getName()));
		Assert.assertEquals("2 day(s) 23 hour(s) 31 minute(s) 32 second(s)",
				stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.OCCURRED.getName()));
	}

	/**
	 * Test filter exits stream status connecting
	 * Expect retrieve successfully with output stream
	 */
	@Test
	void testFilterWithStreamStatusExits() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.setStreamStatusFilter("Connecting");
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("Connecting", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.STATE.getName()));
		Assert.assertEquals("17 day(s) 19 hour(s) 6 minute(s) 17 second(s)",
				stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.UPTIME.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.SOURCE_PORT.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.SENT_PACKETS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.SENT_BYTES.getName()));
		Assert.assertEquals("0 kbps", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.BITRATE.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RECONNECTIONS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RESENT_PACKETS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RESENT_BYTES.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.DROPPED_PACKETS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.DROPPED_BYTES.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.MSS.getName()));
		Assert.assertEquals("0 kbps", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.MAX_BANDWIDTH.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.REMOTE_PORT.getName()));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.SOURCE_ADDRESS.getName()));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.REMOTE_ADDRESS.getName()));
		Assert.assertEquals("0 kbps", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.PATH_MAX_BANDWIDTH.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.LOST_PACKETS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RECV_ACK.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RECV_NAK.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RTT.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.BUFFER.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.LATENCY.getName()));
		Assert.assertEquals("2 day(s) 23 hour(s) 31 minute(s) 32 second(s)",
				stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.OCCURRED.getName()));
	}

	/**
	 * Test filter exits stream status running
	 * Expect retrieve successfully and have not data
	 */
	@Test
	void testFilterWithStreamStatusNotExits() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.setStreamStatusFilter("Running");
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals(15, stats.size());
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.STATE.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.UPTIME.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.SOURCE_PORT.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.SENT_PACKETS.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.SENT_BYTES.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.BITRATE.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RECONNECTIONS.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RESENT_PACKETS.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RESENT_BYTES.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.DROPPED_PACKETS.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.DROPPED_BYTES.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.MSS.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.MAX_BANDWIDTH.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.REMOTE_PORT.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.SOURCE_ADDRESS.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.REMOTE_ADDRESS.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.PATH_MAX_BANDWIDTH.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.LOST_PACKETS.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RECV_ACK.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RECV_NAK.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RTT.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.BUFFER.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.LATENCY.getName()));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.OCCURRED.getName()));
	}

	/**
	 * Test filter exits stream status connecting and port number
	 * Expect retrieve successfully
	 */
	@Test
	void testFilterWithStreamStatusAndExits() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.setStreamStatusFilter("Connecting");
		haivisionX4EncoderCommunicator.setPortNumberFilter("6064");
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("Connecting", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.STATE.getName()));
		Assert.assertEquals("17 day(s) 19 hour(s) 6 minute(s) 17 second(s)",
				stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.UPTIME.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.SOURCE_PORT.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.SENT_PACKETS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.SENT_BYTES.getName()));
		Assert.assertEquals("0 kbps", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.BITRATE.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RECONNECTIONS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RESENT_PACKETS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RESENT_BYTES.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.DROPPED_PACKETS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.DROPPED_BYTES.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.MSS.getName()));
		Assert.assertEquals("0 kbps", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.MAX_BANDWIDTH.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.REMOTE_PORT.getName()));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.SOURCE_ADDRESS.getName()));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.REMOTE_ADDRESS.getName()));
		Assert.assertEquals("0 kbps", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.PATH_MAX_BANDWIDTH.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.LOST_PACKETS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RECV_ACK.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RECV_NAK.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RTT.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.BUFFER.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.LATENCY.getName()));
		Assert.assertEquals("2 day(s) 23 hour(s) 31 minute(s) 32 second(s)",
				stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.OCCURRED.getName()));
	}

	/**
	 * Test getting user role when it is Administrator
	 *
	 * Expect response data contains controlling part
	 */
	@Test
	void testRetrievingUserRoleWithAdministrator() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.ROLE_BASED)).thenReturn(HaivisionURL.ROLE_BASED.getUrl());
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		System.out.println(extendedStatistics.getControllableProperties());
		Assert.assertNotNull(extendedStatistics.getControllableProperties());
	}

	/**
	 * Test getting user role when it is Operator
	 *
	 * Expect response data contains controlling part
	 */
	@Test
	void testRetrieveUserRoleWithOperator() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.ROLE_BASED)).thenReturn(HaivisionURL.ROLE_BASED.getUrl());
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		extendedStatistics.getControllableProperties();
		Assert.assertNotNull(extendedStatistics.getControllableProperties());
	}

	/**
	 * Test getting user role when it is Guest
	 *
	 * Expect response data does not contain controlling part
	 */
	@Test
	void testRetrieveUserRoleWithGuest() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.ROLE_BASED)).thenReturn("/guest/");
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Assert.assertNull(extendedStatistics.getControllableProperties());
	}

	/**
	 * Test getting user role when role is null or empty
	 *
	 * Expect an exception will be thrown
	 */
	@Test
	void testRetrievingUserRoleWithNullOrEmpty() {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.ROLE_BASED)).thenReturn("/role-empty/");
		assertThrows(ResourceNotReachableException.class, () -> haivisionX4EncoderCommunicator.getMultipleStatistics(), "Expect failed because retrieve roleBased empty");
	}

	/**
	 * Test getting user role fail
	 *
	 * Expect an exception will be thrown
	 */
	@Test
	void testRetrievingUserRoleFailed() {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.ROLE_BASED)).thenReturn("/role-failed/");
		assertThrows(ResourceNotReachableException.class, () -> haivisionX4EncoderCommunicator.getMultipleStatistics(), "Expect failed because retrieve roleBased failed");
	}
}
