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
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.HaivisionConstant;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.HaivisionMonitoringMetric;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.HaivisionStatisticsUtil;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.HaivisionURL;

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
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.AUDIO_ENCODER)).thenReturn("/audio-error");
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.VIDEO_ENCODER)).thenReturn("/video-error");
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.OUTPUT_ENCODER)).thenReturn("/output-error");
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION);
		assertThrows(ResourceNotReachableException.class, () -> haivisionX4EncoderCommunicator.getMultipleStatistics(), "Expect failed because retrieve monitoring data failed");
	}

	/**
	 * Test login failed with unauthorized
	 *
	 * Expect login failed and throws exception
	 */
	@Test
	void testUnauthorized() {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.AUTHENTICATION)).thenReturn("/authorized");
		assertThrows(ResourceNotReachableException.class, () -> haivisionX4EncoderCommunicator.getMultipleStatistics(), "Login to the device failed,user unauthorized");
	}

	/**
	 * Test retrieve audio encoder statistics success
	 *
	 * Expect retrieve successfully with audio encoder statistics data
	 */
	@Test
	void testRetrieveAudioEncoderStatisticsSuccess() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO);
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO);
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION);
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUTS);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("Working", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.STATE));
		Assert.assertEquals("0x1a0348d5c", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.ENCODER_PTS));
		Assert.assertEquals("24,146,984,454", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.ENCODED_BYTES));
		Assert.assertEquals("None", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.STC_SOURCE_INTERFACE));
		Assert.assertEquals("0", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.ENCODER_ERRORS));
		Assert.assertEquals("128", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.ENCODED_BITRATE));
		Assert.assertEquals("9693", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.MAX_SAMPLE_VALUE));
		Assert.assertEquals("29", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.MAX_SAMPLE_VALUE_PERCENTAGE));
		Assert.assertEquals("Working", stats.get("Audio Encoder 1" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.STATE));
		Assert.assertEquals("0x000000000", stats.get("Audio Encoder 1" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.ENCODER_PTS));
		Assert.assertEquals("0", stats.get("Audio Encoder 1" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.ENCODED_BYTES));
		Assert.assertEquals("None", stats.get("Audio Encoder 1" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.STC_SOURCE_INTERFACE));
		Assert.assertEquals("0", stats.get("Audio Encoder 1" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.ENCODER_ERRORS));
		Assert.assertEquals("0", stats.get("Audio Encoder 1" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.ENCODED_BITRATE));
		Assert.assertEquals("0", stats.get("Audio Encoder 1" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.MAX_SAMPLE_VALUE));
		Assert.assertEquals("0", stats.get("Audio Encoder 1" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.MAX_SAMPLE_VALUE_PERCENTAGE));
	}

	/**
	 * Test retrieve audio encoder statistics failed
	 *
	 * Expect retrieve audio encoder statistics failed with empty data
	 */
	@Test
	void testRetrieveAudioEncoderStatisticFailed() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.AUDIO_ENCODER)).thenReturn("/audio");
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO);
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION);
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUTS);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertNull(stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.STATE));
		Assert.assertNull(stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.ENCODER_PTS));
		Assert.assertNull(stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.ENCODED_BYTES));
		Assert.assertNull(stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.STC_SOURCE_INTERFACE));
		Assert.assertNull(stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.ENCODER_ERRORS));
		Assert.assertNull(stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.ENCODED_BITRATE));
		Assert.assertNull(stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.MAX_SAMPLE_VALUE));
		Assert.assertNull(stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.MAX_SAMPLE_VALUE_PERCENTAGE));
	}

	/**
	 * Test retrieve audio encoder statistics success with None data
	 *
	 * Expect retrieve successfully with audio encoder statistics None data
	 */
	@Test
	void testRetrieveAudioEncoderStatisticsNoneData() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.AUDIO_ENCODER)).thenReturn("/audio-none-data");
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO);
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION);
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUTS);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("None", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.STATE));
		Assert.assertEquals("None", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.ENCODER_PTS));
		Assert.assertEquals("None", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.ENCODED_BYTES));
		Assert.assertEquals("None", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.STC_SOURCE_INTERFACE));
		Assert.assertEquals("None", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.ENCODER_ERRORS));
		Assert.assertEquals("None", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.ENCODED_BITRATE));
		Assert.assertEquals("None", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.MAX_SAMPLE_VALUE));
		Assert.assertEquals("None", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.MAX_SAMPLE_VALUE_PERCENTAGE));
	}

	/**
	 * Test retrieve audio encoder statistics success
	 *
	 * Expect retrieve successfully with audio encoder statistics data
	 */
	@Test
	void testRetrieveVideoEncoderStatisticsSuccess() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO);
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO);
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION);
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUTS);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("Working",
				stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.STATE));
		Assert.assertEquals("17 day(s) 13 hour(s) 4 minute(s) 5 second(s)",
				stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.UPTIME));
		Assert.assertEquals("1", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_PRESENT));
		Assert.assertEquals("3840x2160p60", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_FORMAT));
		Assert.assertEquals("3840x2160p60", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_FORMAT_SHORT));
		Assert.assertEquals("9223372052962009208", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_FORMAT_U64));
		Assert.assertEquals("9223372052962009088",
				stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_FORMAT_WITHOUT_FRAMERATE_U64));
		Assert.assertEquals("true", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_FORMAT_IS_DETAILED));
		Assert.assertEquals("3840", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_FORMAT_WIDTH));
		Assert.assertEquals("2160", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_FORMAT_HEIGHT));
		Assert.assertEquals("60", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_FORMAT_FRAMERATE));
		Assert.assertEquals("false", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_FORMAT_IS_INTERLACED));
		Assert.assertEquals("true", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_FORMAT_IS_PROGRESSIVE));
		Assert.assertEquals("1920x1080p", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.RESOLUTION));
		Assert.assertEquals("1920", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.RESOLUTION_WIDTH));
		Assert.assertEquals("1080", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.RESOLUTION_HEIGHT));
		Assert.assertEquals("0", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.RESOLUTION_FRAMERATE));
		Assert.assertEquals("false", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.RESOLUTION_IS_INTERLACED));
		Assert.assertEquals("true", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.RESOLUTION_IS_PROGRESSIVE));
		Assert.assertEquals("16:9", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.ASPECT_RATIO));
		Assert.assertEquals("26,406,030", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.ENCODED_FRAMES));
		Assert.assertEquals("0", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.DROPPED_FRAMERATE));
		Assert.assertEquals("166,222,146,368", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.ENCODED_BYTES_VIDEO));
		Assert.assertEquals("60", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.ENCODED_FRAMERATE));
		Assert.assertEquals("0", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.ENCODER_RESETS));
		Assert.assertEquals("2,954", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.ENCODED_BITRATE_VIDEO));
		Assert.assertEquals("25", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.ENCODER_LOAD));
		Assert.assertEquals("0", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.CLOSED_CAPTIONING));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.CC_ERRORS));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.EXTRACTED_CSD_BYTES));
		Assert.assertEquals("BT.709", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_COLOUR_PRIMARIES));
		Assert.assertEquals("BT.709", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_TRANSFER_CHARACTERISTICS));
		Assert.assertEquals("BT.709", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_MATRIX_COEFFICIENTS));
	}

	/**
	 * Test retrieve audio encoder statistics failed
	 *
	 * Expect retrieve audio encoder statistics failed with empty data
	 */
	@Test
	void testRetrieveVideoEncoderStatisticsFailed() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO);
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.VIDEO_ENCODER)).thenReturn("/video");
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION);
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUTS);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.STATE));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.UPTIME));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_PRESENT));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_FORMAT));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_FORMAT_SHORT));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_FORMAT_U64));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_FORMAT_WITHOUT_FRAMERATE_U64));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_FORMAT_IS_DETAILED));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_FORMAT_WIDTH));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_FORMAT_HEIGHT));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_FORMAT_FRAMERATE));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_FORMAT_IS_INTERLACED));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_FORMAT_IS_PROGRESSIVE));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.RESOLUTION));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.RESOLUTION_WIDTH));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.RESOLUTION_HEIGHT));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.RESOLUTION_FRAMERATE));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.RESOLUTION_IS_INTERLACED));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.RESOLUTION_IS_PROGRESSIVE));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.ASPECT_RATIO));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.ENCODED_FRAMES));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.DROPPED_FRAMERATE));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.ENCODED_BYTES_VIDEO));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.ENCODED_FRAMERATE));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.ENCODER_RESETS));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.ENCODED_BITRATE_VIDEO));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.ENCODER_LOAD));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.CLOSED_CAPTIONING));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.CC_ERRORS));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.EXTRACTED_CSD_BYTES));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_COLOUR_PRIMARIES));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_TRANSFER_CHARACTERISTICS));
		Assert.assertNull(stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_MATRIX_COEFFICIENTS));
	}

	/**
	 * Test retrieve audio encoder statistics success with None data
	 *
	 * Expect retrieve successfully with audio encoder statistics with None data
	 */
	@Test
	void testRetrieveVideoEncoderStatisticsNoneData() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO);
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.VIDEO_ENCODER)).thenReturn("/video-none-data");
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION);
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUTS);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.STATE));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.UPTIME));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_PRESENT));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_FORMAT));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_FORMAT_SHORT));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_FORMAT_U64));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_FORMAT_WITHOUT_FRAMERATE_U64));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_FORMAT_IS_DETAILED));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_FORMAT_WIDTH));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_FORMAT_HEIGHT));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_FORMAT_FRAMERATE));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_FORMAT_IS_INTERLACED));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_FORMAT_IS_PROGRESSIVE));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.RESOLUTION));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.RESOLUTION_WIDTH));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.RESOLUTION_HEIGHT));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.RESOLUTION_FRAMERATE));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.RESOLUTION_IS_INTERLACED));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.RESOLUTION_IS_PROGRESSIVE));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.ASPECT_RATIO));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.ENCODED_FRAMES));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.DROPPED_FRAMERATE));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.ENCODED_BYTES_VIDEO));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.ENCODED_FRAMERATE));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.ENCODER_RESETS));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.ENCODED_BITRATE_VIDEO));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.ENCODER_LOAD));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.CLOSED_CAPTIONING));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.CC_ERRORS));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.EXTRACTED_CSD_BYTES));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_COLOUR_PRIMARIES));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_TRANSFER_CHARACTERISTICS));
		Assert.assertEquals("None", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.INPUT_MATRIX_COEFFICIENTS));
	}

	/**
	 * Test retrieve output stream statistics success
	 *
	 * Expect retrieve successfully with output stream statistics data
	 */
	@Test
	void testRetrieveOutStreamStatisticsSuccess() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO);
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO);
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION);
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUTS);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("Connecting", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.STATE));
		Assert.assertEquals("17 day(s) 19 hour(s) 6 minute(s) 17 second(s)",
				stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.UPTIME));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.SOURCE_PORT));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.SENT_PACKETS));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.SENT_BYTES));
		Assert.assertEquals("0 kbps", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.BITRATE));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.RECONNECTIONS));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.RESENT_PACKETS));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.RESENT_BYTES));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.DROPPED_PACKETS));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.DROPPED_BYTES));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.MSS));
		Assert.assertEquals("0 kbps", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.MAX_BANDWIDTH));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.REMOTE_PORT));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.SOURCE_ADDRESS));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.REMOTE_ADDRESS));
		Assert.assertEquals("0 kbps", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.PATH_MAX_BANDWIDTH));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.LOST_PACKETS));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.RECV_ACK));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.RECV_NAK));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.RTT));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.BUFFER));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.LATENCY));
		Assert.assertEquals("2 day(s) 23 hour(s) 31 minute(s) 32 second(s)",
				stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.OCCURRED));
	}

	/**
	 * Test retrieve output stream statistics failed
	 *
	 * Expect retrieve failed with output stream statistics with Empty data
	 */
	@Test
	void testRetrieveOutStreamStatisticsFailed() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO);
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO);
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION);
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.OUTPUT_ENCODER)).thenReturn("/output");
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.STATE));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.UPTIME));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.SOURCE_PORT));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.SENT_PACKETS));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.SENT_BYTES));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.BITRATE));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.RECONNECTIONS));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.RESENT_PACKETS));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.RESENT_BYTES));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.DROPPED_PACKETS));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.DROPPED_BYTES));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.MSS));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.MAX_BANDWIDTH));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.REMOTE_PORT));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.SOURCE_ADDRESS));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.REMOTE_ADDRESS));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.PATH_MAX_BANDWIDTH));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.LOST_PACKETS));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.RECV_ACK));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.RECV_NAK));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.RTT));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.BUFFER));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.LATENCY));
		Assert.assertNull(stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.OCCURRED));
	}

	/**
	 * Test retrieve output stream statistics successfully
	 *
	 * Expect retrieve successfully with output stream statistics with None data
	 */
	@Test
	void testRetrieveOutStreamStatisticsNoneData() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO);
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO);
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION);
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.OUTPUT_ENCODER)).thenReturn("/output-none-data");
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.STATE));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.UPTIME));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.SOURCE_PORT));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.SENT_PACKETS));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.SENT_BYTES));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.BITRATE));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.RECONNECTIONS));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.RESENT_PACKETS));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.RESENT_BYTES));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.DROPPED_PACKETS));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.DROPPED_BYTES));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.MSS));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.MAX_BANDWIDTH));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.REMOTE_PORT));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.SOURCE_ADDRESS));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.REMOTE_ADDRESS));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.PATH_MAX_BANDWIDTH));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.LOST_PACKETS));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.RECV_ACK));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.RECV_NAK));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.RTT));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.BUFFER));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.LATENCY));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS + "#" + HaivisionMonitoringMetric.OCCURRED));
	}
}
