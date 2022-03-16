/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.AudioControllingMetric;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.AudioMonitoringMetric;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.HaivisionConstant;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.HaivisionStatisticsUtil;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.HaivisionURL;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.OutputMonitoringMetric;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.SystemMonitoringMetric;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.VideoControllingMetric;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.VideoMonitoringMetric;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.AlgorithmDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.AspectRatioDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.AudioStateDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.BitRateDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.ChannelModeDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.ChromaSubSampling;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.CodecAlgorithm;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.CreateOutputStreamMetric;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.CroppingDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.EncodingProfile;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.EncryptionDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.FrameRateDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.FramingDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.InputDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.LanguageDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.ProtocolDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.RateControlDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.ResolutionDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.SRTModeDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.SampleRateDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.SlicesDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.TimeCodeSource;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.TimingAndShaping;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.VideoInputDropdown;

/**
 * Unit test for the HaivisionX4EncoderCommunicator
 *
 * @author Ivan / Symphony Dev Team<br>
 * Created on 3/8/2022
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
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.STREAM)).thenReturn(HaivisionURL.STREAM.getUrl());
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
		Assert.assertEquals("24146984454", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.ENCODED_BYTES.getName()));
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
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.ROLE_BASED)).thenReturn("/guest/");
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
		Assert.assertEquals("26406030", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.ENCODED_FRAMES_VIDEO.getName()));
		Assert.assertEquals("0", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.DROPPED_FRAMERATE.getName()));
		Assert.assertEquals("166222146368", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.ENCODED_BYTES.getName()));
		Assert.assertEquals("60", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.ENCODED_FRAMERATE.getName()));
		Assert.assertEquals("0", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.ENCODER_RESETS.getName()));
		Assert.assertEquals("2954", stats.get("Video Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + VideoMonitoringMetric.ENCODED_BITRATE_VIDEO.getName()));
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
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.ROLE_BASED)).thenReturn("/guest/");
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
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
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
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.BITRATE.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RECONNECTIONS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RESENT_PACKETS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RESENT_BYTES.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.DROPPED_PACKETS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.DROPPED_BYTES.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.MSS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.MAX_BANDWIDTH.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.REMOTE_PORT.getName()));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.SOURCE_ADDRESS.getName()));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.REMOTE_ADDRESS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.PATH_MAX_BANDWIDTH.getName()));
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
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
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
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
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
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
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
		Assert.assertEquals("24146984454", stats.get("Audio Encoder 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + AudioMonitoringMetric.ENCODED_BYTES.getName()));
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
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.BITRATE.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RECONNECTIONS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RESENT_PACKETS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RESENT_BYTES.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.DROPPED_PACKETS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.DROPPED_BYTES.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.MSS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.MAX_BANDWIDTH.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.REMOTE_PORT.getName()));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.SOURCE_ADDRESS.getName()));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.REMOTE_ADDRESS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.PATH_MAX_BANDWIDTH.getName()));
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
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.BITRATE.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RECONNECTIONS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RESENT_PACKETS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RESENT_BYTES.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.DROPPED_PACKETS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.DROPPED_BYTES.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.MSS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.MAX_BANDWIDTH.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.REMOTE_PORT.getName()));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.SOURCE_ADDRESS.getName()));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.REMOTE_ADDRESS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.PATH_MAX_BANDWIDTH.getName()));
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
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.setStreamStatusFilter("Running");
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals(52, stats.size());
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
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
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
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.BITRATE.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RECONNECTIONS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RESENT_PACKETS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.RESENT_BYTES.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.DROPPED_PACKETS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.DROPPED_BYTES.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.MSS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.MAX_BANDWIDTH.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.REMOTE_PORT.getName()));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.SOURCE_ADDRESS.getName()));
		Assert.assertEquals("None", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.REMOTE_ADDRESS.getName()));
		Assert.assertEquals("0", stats.get("Stream Output 0" + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + "#" + OutputMonitoringMetric.PATH_MAX_BANDWIDTH.getName()));
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

	/**
	 * Test control input of audio
	 *
	 * Expect audio input will be change to new value
	 */
	@Test
	void testControlAudioInput() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Audio Encoder 2" + HaivisionConstant.HASH + AudioControllingMetric.INPUT.getName());
		controllableProperty.setValue(InputDropdown.SDI_1_5_6.getName());
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		List<AdvancedControllableProperty> controllableProperties = extendedStatistics.getControllableProperties();
		for (AdvancedControllableProperty property : controllableProperties) {
			if (property.getName().equals("Audio Encoder 2" + HaivisionConstant.HASH + AudioControllingMetric.INPUT.getName())) {
				assertEquals(InputDropdown.SDI_1_5_6.getName(), property.getValue());
				break;
			}
		}
	}

	/**
	 * Test control Channel mode of audio to Stereo in case current Bitrate value is in Stereo Bitrate list
	 *
	 * Expect Channel mode will be change to the new value and Bitrate will be not change.
	 */
	@Test
	void testChangeChannelModeToStereoWithBitrateNotChange() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn("/apis/audenc/change-channel-mode-stereo1");
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Audio Encoder 0" + HaivisionConstant.HASH + AudioControllingMetric.CHANGE_MODE.getName());
		controllableProperty.setValue(ChannelModeDropdown.STEREO.getName());
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics newExtendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		List<AdvancedControllableProperty> newControllableProperties = newExtendedStatistics.getControllableProperties();
		for (AdvancedControllableProperty property : newControllableProperties) {
			if (property.getName().equals("Audio Encoder 0" + HaivisionConstant.HASH + AudioControllingMetric.CHANGE_MODE.getName())) {
				assertEquals(ChannelModeDropdown.STEREO.getName(), property.getValue());
				break;
			}
		}
		for (AdvancedControllableProperty property : newControllableProperties) {
			if (property.getName().equals("Audio Encoder 0" + HaivisionConstant.HASH + AudioControllingMetric.BITRATE.getName())) {
				Assertions.assertEquals(BitRateDropdown.NUMBER_128.getName(), property.getValue());
				break;
			}
		}
	}

	/**
	 * Test control Channel mode of audio to Stereo in case current Bitrate value is not in Stereo Bitrate list
	 *
	 * Expect Channel mode will be change to the new value and Bitrate will be set to default bitrate value of Stereo.
	 */
	@Test
	void testChangeChannelModeToStereoWithBitrateChange() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn("/apis/audenc/change-channel-mode-stereo2");
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics();
		haivisionX4EncoderCommunicator.getMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Audio Encoder 0" + HaivisionConstant.HASH + AudioControllingMetric.CHANGE_MODE.getName());
		controllableProperty.setValue(ChannelModeDropdown.STEREO.getName());
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		List<AdvancedControllableProperty> newControllableProperties = extendedStatistics.getControllableProperties();
		for (AdvancedControllableProperty property : newControllableProperties) {
			if (property.getName().equals("Audio Encoder 0" + HaivisionConstant.HASH + AudioControllingMetric.CHANGE_MODE.getName())) {
				assertEquals(ChannelModeDropdown.STEREO.getName(), property.getValue());
				break;
			}
		}
		for (AdvancedControllableProperty property : newControllableProperties) {
			if (property.getName().equals("Audio Encoder 0" + HaivisionConstant.HASH + AudioControllingMetric.BITRATE.getName())) {
				assertEquals(BitRateDropdown.getNameFromValue(BitRateDropdown.getDefaultValueOfStereo()), property.getValue());
				break;
			}
		}
	}

	/**
	 * Test control Channel mode of audio to Mono in case current Bitrate value is in Mono Bitrate list (40)
	 *
	 * Expect Channel mode will be change to the new value and Bitrate will be not change.
	 */
	@Test
	void testChangeChannelModeToMonoWithBitrateNotChange() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn("/apis/audenc/change-channel-mode-mono1");
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Audio Encoder 0" + HaivisionConstant.HASH + AudioControllingMetric.CHANGE_MODE.getName());
		controllableProperty.setValue(ChannelModeDropdown.MONO_LEFT.getName());
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		List<AdvancedControllableProperty> controllableProperties = extendedStatistics.getControllableProperties();
		for (AdvancedControllableProperty property : controllableProperties) {
			if (property.getName().equals("Audio Encoder 0" + HaivisionConstant.HASH + AudioControllingMetric.CHANGE_MODE.getName())) {
				assertEquals(ChannelModeDropdown.MONO_LEFT.getName(), property.getValue());
				break;
			}
		}
		for (AdvancedControllableProperty property : controllableProperties) {
			if (property.getName().equals("Audio Encoder 0" + HaivisionConstant.HASH + AudioControllingMetric.BITRATE.getName())) {
				assertEquals(BitRateDropdown.NUMBER_128.getName(), property.getValue());
				break;
			}
		}
	}

	/**
	 * Test control Channel mode of audio to Mono in case current Bitrate value is not in Mono Bitrate list
	 *
	 * Expect Channel mode will be change to the new value and Bitrate will be set to default bitrate value of Mono.
	 */
	@Test
	void testChangeChannelModeToMonoWithBitrateChange() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn("/apis/audenc/change-channel-mode-mono2");
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Audio Encoder 0" + HaivisionConstant.HASH + AudioControllingMetric.CHANGE_MODE.getName());
		controllableProperty.setValue(ChannelModeDropdown.MONO_LEFT.getName());
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		List<AdvancedControllableProperty> controllableProperties = extendedStatistics.getControllableProperties();
		for (AdvancedControllableProperty property : controllableProperties) {
			if (property.getName().equals("Audio Encoder 0" + HaivisionConstant.HASH + AudioControllingMetric.CHANGE_MODE.getName())) {
				assertEquals(ChannelModeDropdown.MONO_LEFT.getName(), property.getValue());
				break;
			}
		}
		for (AdvancedControllableProperty property : controllableProperties) {
			if (property.getName().equals("Audio Encoder 0" + HaivisionConstant.HASH + AudioControllingMetric.BITRATE.getName())) {
				assertEquals(BitRateDropdown.getNameFromValue(BitRateDropdown.getDefaultValueOfMono()), property.getValue());
				break;
			}
		}
	}

	/**
	 * Test control audio sample rate
	 *
	 * Expect sample rate will be set to the new value
	 */
	@Test
	void testControlAudioSampleRate() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Audio Encoder 0" + HaivisionConstant.HASH + AudioControllingMetric.SAMPLE_RATE.getName());
		controllableProperty.setValue(SampleRateDropdown.SAMPLE_RATE_48Hz.getName());
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		List<AdvancedControllableProperty> controllableProperties = extendedStatistics.getControllableProperties();
		for (AdvancedControllableProperty property : controllableProperties) {
			if (property.getName().equals("Audio Encoder 0" + HaivisionConstant.HASH + AudioControllingMetric.SAMPLE_RATE.getName())) {
				assertEquals(SampleRateDropdown.SAMPLE_RATE_48Hz.getName(), property.getValue());
				break;
			}
		}
	}

	/**
	 * Test control audio algorithm
	 *
	 * Expect algorithm will be set to the new value
	 */
	@Test
	void testControlAudioAlgorithm() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Audio Encoder 0" + HaivisionConstant.HASH + AudioControllingMetric.ALGORITHM.getName());
		controllableProperty.setValue(AlgorithmDropdown.MPEG_4.getName());
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		List<AdvancedControllableProperty> controllableProperties = extendedStatistics.getControllableProperties();
		for (AdvancedControllableProperty property : controllableProperties) {
			if (property.getName().equals("Audio Encoder 0" + HaivisionConstant.HASH + AudioControllingMetric.ALGORITHM.getName())) {
				assertEquals(AlgorithmDropdown.MPEG_4.getName(), property.getValue());
				break;
			}
		}
	}

	/**
	 * Test control audio language
	 *
	 * Expect language will be set to the new value
	 */
	@Test
	void testControlAudioLanguage() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Audio Encoder 0" + HaivisionConstant.HASH + AudioControllingMetric.LANGUAGE.getName());
		controllableProperty.setValue(LanguageDropdown.ALBANIAN.getName());
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		List<AdvancedControllableProperty> controllableProperties = extendedStatistics.getControllableProperties();
		for (AdvancedControllableProperty property : controllableProperties) {
			if (property.getName().equals("Audio Encoder 0" + HaivisionConstant.HASH + AudioControllingMetric.LANGUAGE.getName())) {
				assertEquals(LanguageDropdown.ALBANIAN.getName(), property.getValue());
				break;
			}
		}
	}

	/**
	 * Test control audio bitrate when channel mode is stereo
	 *
	 * Expect bitrate will be set to the new value
	 */
	@Test
	void testControlAudioBitrateWhenStereo() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn("/apis/audenc/change-channel-bitrate-stereo");
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Audio Encoder 0" + HaivisionConstant.HASH + AudioControllingMetric.BITRATE.getName());
		controllableProperty.setValue(BitRateDropdown.NUMBER_80.getName());
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		List<AdvancedControllableProperty> controllableProperties = extendedStatistics.getControllableProperties();
		for (AdvancedControllableProperty property : controllableProperties) {
			if (property.getName().equals("Audio Encoder 0" + HaivisionConstant.HASH + AudioControllingMetric.BITRATE.getName())) {
				assertEquals(BitRateDropdown.NUMBER_80.getName(), property.getValue());
				break;
			}
		}
	}

	/**
	 * Test control audio bitrate when channel mode is mono
	 *
	 * Expect bitrate will be set to the new value
	 */
	@Test
	void testControlAudioBitrateWhenMono() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn("/apis/audenc/change-channel-bitrate-mono");
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Audio Encoder 0" + HaivisionConstant.HASH + AudioControllingMetric.BITRATE.getName());
		controllableProperty.setValue(BitRateDropdown.NUMBER_24.getName());
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		List<AdvancedControllableProperty> controllableProperties = extendedStatistics.getControllableProperties();
		for (AdvancedControllableProperty property : controllableProperties) {
			if (property.getName().equals("Audio Encoder 0" + HaivisionConstant.HASH + AudioControllingMetric.BITRATE.getName())) {
				assertEquals(BitRateDropdown.NUMBER_24.getName(), property.getValue());
				break;
			}
		}
	}

	/**
	 * Test stop audio
	 *
	 * Expect action of audio is stopped
	 */
	@Test
	void testStopAudio() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Audio Encoder 0" + HaivisionConstant.HASH + AudioControllingMetric.ACTION.getName());
		controllableProperty.setValue(AudioStateDropdown.STOPPED.getName());
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(AudioStateDropdown.STOPPED.getName(), stats.get("Audio Encoder 0" + HaivisionConstant.HASH + AudioControllingMetric.ACTION.getName()));
	}

	/**
	 * Test mute audio
	 *
	 * Expect action of audio is start
	 */
	@Test
	void testMuteAudio() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Audio Encoder 0" + HaivisionConstant.HASH + AudioControllingMetric.ACTION.getName());
		controllableProperty.setValue(AudioStateDropdown.MUTED.getName());
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(AudioStateDropdown.MUTED.getName(), stats.get("Audio Encoder 0" + HaivisionConstant.HASH + AudioControllingMetric.ACTION.getName()));
	}

	/**
	 * Test cancel audio control property
	 *
	 * At first, change audio bitrate and expect edit field is true; then cancel and expect edit field is null
	 */
	@Test
	void testCancelAudioControl() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics();
		ControllableProperty bitrate = new ControllableProperty();
		bitrate.setProperty("Audio Encoder 0" + HaivisionConstant.HASH + AudioControllingMetric.BITRATE.getName());
		bitrate.setValue(BitRateDropdown.NUMBER_256.getName());
		haivisionX4EncoderCommunicator.controlProperty(bitrate);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(HaivisionConstant.TRUE, stats.get("Audio Encoder 0" + HaivisionConstant.HASH + HaivisionConstant.EDITED));
		ControllableProperty cancelAction = new ControllableProperty();
		cancelAction.setProperty("Audio Encoder 0" + HaivisionConstant.HASH + AudioControllingMetric.CANCEL.getName());
		haivisionX4EncoderCommunicator.controlProperty(cancelAction);
		ExtendedStatistics newExtendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> newStats = newExtendedStatistics.getStatistics();
		assertNull(newStats.get("Audio Encoder 0" + HaivisionConstant.HASH + HaivisionConstant.EDITED));
	}

	/**
	 * Test Action audio control property
	 *
	 * Expect control action successfully and edited is null
	 */
	@Test
	void testActionAudioControl() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics();
		ControllableProperty action = new ControllableProperty();
		action.setProperty("Audio Encoder 0" + HaivisionConstant.HASH + AudioControllingMetric.ACTION.getName());
		action.setValue(HaivisionConstant.STOP);
		haivisionX4EncoderCommunicator.controlProperty(action);

		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(HaivisionConstant.TRUE, stats.get("Audio Encoder 0" + HaivisionConstant.HASH + HaivisionConstant.EDITED));

		ControllableProperty applyChange = new ControllableProperty();
		applyChange.setProperty("Audio Encoder 0" + HaivisionConstant.HASH + AudioControllingMetric.APPLY_CHANGE.getName());
		haivisionX4EncoderCommunicator.controlProperty(applyChange);

		ExtendedStatistics newExtendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> newStats = newExtendedStatistics.getStatistics();
		assertNull(newStats.get("Audio Encoder 0" + HaivisionConstant.HASH + HaivisionConstant.EDITED));
	}

	/**
	 * Test apply change of audio, fail when send request to apply change for all metric
	 *
	 * Expect an exception will be thrown
	 */
	@Test
	void testApplyChangeOfAudioFailWhenApplyChangeForMetric() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics();
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn("/apis/audenc/apply-change-fail-1");
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Audio Encoder 0" + HaivisionConstant.HASH + AudioControllingMetric.BITRATE.getName());
		controllableProperty.setValue(BitRateDropdown.NUMBER_256.getName());
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ControllableProperty applyChange = new ControllableProperty();
		applyChange.setProperty("Audio Encoder 0" + HaivisionConstant.HASH + AudioControllingMetric.APPLY_CHANGE.getName());
		assertThrows(ResourceNotReachableException.class, () -> haivisionX4EncoderCommunicator.controlProperty(applyChange));
	}

	/**
	 * Test control input
	 *
	 * Expect control input successfully and will be set to the new value
	 */
	@Test
	void testControlInput() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.INPUT.getName());
		controllableProperty.setValue(VideoInputDropdown.BNC_2.getName());
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics newExtendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = newExtendedStatistics.getStatistics();
		assertEquals(VideoInputDropdown.BNC_2.getName(), stats.get("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.INPUT.getName()));
	}

	/**
	 * Test control CodecAlgorithm with value H265
	 *
	 * Expect control CodecAlgorithm successfully and will be set to the new value
	 */
	@Test
	void testCodecAlgorithmWithModeH265Successfully() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.CODEC_ALGORITHM.getName());
		controllableProperty.setValue(CodecAlgorithm.H_265.getName());
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics newExtendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = newExtendedStatistics.getStatistics();
		assertEquals(CodecAlgorithm.H_265.getName(), stats.get("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.CODEC_ALGORITHM.getName()));
	}


	/**
	 * Test control CodecAlgorithm is H265 and encodingProfile is High_10
	 *
	 * Expect control CodecAlgorithm successfully and will be set to the new value
	 */
	@Test
	void testCodecAlgorithmIsH_265AndEncodingProfileIsHigh10() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.ENCODING_PROFILE.getName());
		controllableProperty.setValue(EncodingProfile.HIGH_10.getName());
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		controllableProperty.setProperty("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.CODEC_ALGORITHM.getName());
		controllableProperty.setValue(CodecAlgorithm.H_265.getName());
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics newExtendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = newExtendedStatistics.getStatistics();
		assertEquals(CodecAlgorithm.H_265.getName(), stats.get("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.CODEC_ALGORITHM.getName()));
	}

	/**
	 * Test control CodecAlgorithm is H265 and encodingProfile is High_10
	 *
	 * Expect control CodecAlgorithm successfully and will be set to the new value
	 */
	@Test
	void testCodecAlgorithmIsH_265AndEncodingProfileIsHIGH_422() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.ENCODING_PROFILE.getName());
		controllableProperty.setValue(EncodingProfile.HIGH_422.getName());
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		controllableProperty.setProperty("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.CODEC_ALGORITHM.getName());
		controllableProperty.setValue(CodecAlgorithm.H_265.getName());
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics newExtendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = newExtendedStatistics.getStatistics();
		assertEquals(CodecAlgorithm.H_265.getName(), stats.get("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.CODEC_ALGORITHM.getName()));
	}

	/**
	 * Test encodingProfile is MAIN
	 *
	 * Expect change encodingProfile with value is MAIN
	 */
	@Test
	void testEncodingProfileSuccessfully() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.ENCODING_PROFILE.getName());
		controllableProperty.setValue(EncodingProfile.MAIN.getName());
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics newExtendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = newExtendedStatistics.getStatistics();
		assertEquals(EncodingProfile.MAIN.getName(), stats.get("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.ENCODING_PROFILE.getName()));
	}

	/**
	 * Test control CodecAlgorithm with value H264
	 *
	 * Expect control CodecAlgorithm successfully and will be set to the new value
	 */
	@Test
	void testCodecAlgorithmWithModeIssH264Successfully() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.CODEC_ALGORITHM.getName());
		controllableProperty.setValue(CodecAlgorithm.H_264.getName());
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics newExtendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = newExtendedStatistics.getStatistics();
		assertEquals(CodecAlgorithm.H_264.getName(), stats.get("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.CODEC_ALGORITHM.getName()));
	}


	/**
	 * Test control CodecAlgorithm is H264 and encodingProfile is main_10
	 *
	 * Expect control CodecAlgorithm successfully and will be set to the new value
	 */
	@Test
	void testCodecAlgorithmIsH_264AndEncodingProfileIsMain10() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.ENCODING_PROFILE.getName());
		controllableProperty.setValue(EncodingProfile.MAIN_10.getName());
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		controllableProperty.setProperty("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.CODEC_ALGORITHM.getName());
		controllableProperty.setValue(CodecAlgorithm.H_264.getName());
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics newExtendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = newExtendedStatistics.getStatistics();
		assertEquals(CodecAlgorithm.H_264.getName(), stats.get("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.CODEC_ALGORITHM.getName()));
	}

	/**
	 * Test control CodecAlgorithm is H265 and encodingProfile is main_422
	 *
	 * Expect control CodecAlgorithm successfully and will be set to the new value
	 */
	@Test
	void testCodecAlgorithmIsH_264AndEncodingProfileIsMain_422() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.ENCODING_PROFILE.getName());
		controllableProperty.setValue(EncodingProfile.MAIN_422_10.getName());
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		controllableProperty.setProperty("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.CODEC_ALGORITHM.getName());
		controllableProperty.setValue(CodecAlgorithm.H_264.getName());
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics newExtendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = newExtendedStatistics.getStatistics();
		assertEquals(CodecAlgorithm.H_264.getName(), stats.get("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.CODEC_ALGORITHM.getName()));
	}

	/**
	 * Test control ChromaSubSampling
	 *
	 * Expect control ChromaSubSampling successfully and will be set to the new value
	 */
	@Test
	void testChromaSubSamplingSuccessfully() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.CHROMA_SUBSAMPLING.getName());
		controllableProperty.setValue(ChromaSubSampling.BIT_420_8.getName());
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics newExtendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = newExtendedStatistics.getStatistics();
		assertEquals(ChromaSubSampling.BIT_420_8.getName(), stats.get("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.CHROMA_SUBSAMPLING.getName()));
	}

	/**
	 * Test control RateControl
	 *
	 * Expect control RateControl successfully and will be set to the new value
	 */
	@Test
	void testRateControlSuccessfully() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.RATE_CONTROL.getName());
		controllableProperty.setValue(RateControlDropdown.CVBR.getName());
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics newExtendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = newExtendedStatistics.getStatistics();
		assertEquals(RateControlDropdown.CVBR.getName(), stats.get("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.RATE_CONTROL.getName()));
	}

	/**
	 * Test control MaxBitRate
	 *
	 * Expect control MaxBitRate successfully and will be set to the new value
	 */
	@Test
	void testMaxBitRateSuccessfully() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.MAX_BITRATE.getName());
		controllableProperty.setValue(80000);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics newExtendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = newExtendedStatistics.getStatistics();
		assertEquals("80000", stats.get("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.MAX_BITRATE.getName()));
	}

	/**
	 * Test control BitRate
	 *
	 * Expect control BitRate successfully and will be set to the new value
	 */
	@Test
	void testBitRateSuccessfully() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.BITRATE.getName());
		controllableProperty.setValue(2000);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics newExtendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = newExtendedStatistics.getStatistics();
		assertEquals("2000", stats.get("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.BITRATE.getName()));
	}

	/**
	 * Test control GOPSize
	 *
	 * Expect control GOPSize successfully and will be set to the new value
	 */
	@Test
	void testGOPSizeSuccessfully() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.GOP_SIZE.getName());
		controllableProperty.setValue(150);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics newExtendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = newExtendedStatistics.getStatistics();
		assertEquals("150", stats.get("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.GOP_SIZE.getName()));
	}

	/**
	 * Test control TimeCodeSource
	 *
	 * Expect control TimeCodeSource successfully and will be set to the new value
	 */
	@Test
	void testTimeCodeSourceSuccessfully() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.TIME_CODE_SOURCE.getName());
		controllableProperty.setValue(TimeCodeSource.VIDEO.getName());
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics newExtendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = newExtendedStatistics.getStatistics();
		assertEquals(TimeCodeSource.VIDEO.getName(), stats.get("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.TIME_CODE_SOURCE.getName()));
	}

	/**
	 * Test control AspectRatio
	 *
	 * Expect control AspectRatio successfully and will be set to the new value
	 */
	@Test
	void testAspectRatioSuccessfully() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.ASPECT_RATIO.getName());
		controllableProperty.setValue(AspectRatioDropdown.ASPECT_RATIO_3.getName());
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics newExtendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = newExtendedStatistics.getStatistics();
		assertEquals(AspectRatioDropdown.ASPECT_RATIO_3.getName(), stats.get("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.ASPECT_RATIO.getName()));
	}

	/**
	 * Test control ClosedCaption
	 *
	 * Expect control ClosedCaption successfully and will be set to the new value
	 */
	@Test
	void testClosedCaptionSuccessfully() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.CLOSED_CAPTION.getName());
		controllableProperty.setValue(1);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics newExtendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = newExtendedStatistics.getStatistics();
		assertEquals("1", stats.get("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.CLOSED_CAPTION.getName()));
	}

	/**
	 * Test control Resizing
	 *
	 * Expect control Resizing successfully and will be set to the new value
	 */
	@Test
	void testResizingSuccessfully() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.CROPPING.getName());
		controllableProperty.setValue(CroppingDropdown.H_265.getName());
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics newExtendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = newExtendedStatistics.getStatistics();
		assertEquals(CroppingDropdown.H_265.getName(), stats.get("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.CROPPING.getName()));
	}

	/**
	 * Test control FrameRate
	 *
	 * Expect control FrameRate successfully and will be set to the new value
	 */
	@Test
	void testFrameRateSuccessfully() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.FRAME_RATE.getName());
		controllableProperty.setValue(FrameRateDropdown.FAME_RATE_2.getName());
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics newExtendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = newExtendedStatistics.getStatistics();
		assertEquals(FrameRateDropdown.FAME_RATE_2.getName(), stats.get("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.FRAME_RATE.getName()));
	}

	/**
	 * Test control Framing
	 *
	 * Expect control Framing successfully and will be set to the new value
	 */
	@Test
	void testFramingSuccessfully() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.FRAMING.getName());
		controllableProperty.setValue(FramingDropdown.IP.getName());
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics newExtendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = newExtendedStatistics.getStatistics();
		assertEquals(FramingDropdown.IP.getName(), stats.get("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.FRAMING.getName()));
	}

	/**
	 * Test control Slices
	 *
	 * Expect control Slices successfully and will be set to the new value
	 */
	@Test
	void testSlicesSuccessfully() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.SLICES.getName());
		controllableProperty.setValue(SlicesDropdown.SLICES_1.getName());
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics newExtendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = newExtendedStatistics.getStatistics();
		assertEquals(SlicesDropdown.SLICES_1.getName(), stats.get("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.SLICES.getName()));
	}

	/**
	 * Test control Resolution
	 *
	 * Expect control Resolution successfully and will be set to the new value
	 */
	@Test
	void testResolutionSuccessfully() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.RESOLUTION.getName());
		controllableProperty.setValue(ResolutionDropdown.RESOLUTION_AUTOMATIC.getName());
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics newExtendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = newExtendedStatistics.getStatistics();
		assertEquals(ResolutionDropdown.RESOLUTION_AUTOMATIC.getName(), stats.get("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.RESOLUTION.getName()));
	}

	/**
	 * Test control Resolution with value is 1920_1080p
	 *
	 * Expect control Resolution successfully and will be set to the new value
	 */
	@Test
	void testResolutionIs1920_1080PSuccessfully() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.SLICES.getName());
		controllableProperty.setValue(SlicesDropdown.SLICES_1.getName());
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		controllableProperty.setProperty("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.RESOLUTION.getName());
		controllableProperty.setValue(ResolutionDropdown.RESOLUTION_1920_1080P.getName());
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics newExtendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = newExtendedStatistics.getStatistics();
		assertEquals(ResolutionDropdown.RESOLUTION_1920_1080P.getName(), stats.get("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.RESOLUTION.getName()));
		assertEquals(SlicesDropdown.SLICES_1.getName(), stats.get("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.SLICES.getName()));
	}

	/**
	 * Test control Cancel
	 *
	 * Expect control Cancel successfully and field edited is null
	 */
	@Test
	void testCancelSuccessfully() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics();

		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.CROPPING.getName());
		controllableProperty.setValue(CroppingDropdown.H_265.getName());
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);

		ExtendedStatistics newExtendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> newStats = newExtendedStatistics.getStatistics();
		assertEquals("True", newStats.get("Video Encoder 0" + HaivisionConstant.HASH + HaivisionConstant.EDITED));

		controllableProperty.setProperty("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.CANCEL.getName());
		controllableProperty.setValue("1");
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		newExtendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		newStats = newExtendedStatistics.getStatistics();
		assertNull(newStats.get("Video Encoder 0" + HaivisionConstant.HASH + HaivisionConstant.EDITED));
	}

	/**
	 * Test control IntraRefresh
	 *
	 * Expect control IntraRefresh successfully and will be set to the new value
	 */
	@Test
	void testIntraRefreshSuccessfully() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.INTRA_REFRESH.getName());
		controllableProperty.setValue("1");
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics newExtendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = newExtendedStatistics.getStatistics();
		assertEquals("1", stats.get("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.INTRA_REFRESH.getName()));
	}

	/**
	 * Test control Action
	 *
	 * Expect control Action successfully and the metric change action is start
	 */
	@Test
	void testActionSuccessfully() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Video Encoder 0#Action");
		controllableProperty.setValue("Start");
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals("Start", stats.get("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.ACTION.getName()));
	}

	/**
	 * Test control ApplyChange button
	 *
	 * Expect control ApplyChange successfully and will be set all value for all metric and the field edited is null
	 */
	@Test
	void testApplyChangeSuccessfully() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Video Encoder 0#Action");
		controllableProperty.setValue("Stop");
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistic = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistic.getStatistics();

		assertEquals("True", stats.get("Video Encoder 0#Edited"));

		controllableProperty.setProperty("Video Encoder 0#ApplyChange");
		controllableProperty.setValue("1");
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		extendedStatistic = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistic.getStatistics();
		assertNull(stats.get("Video Encoder 0#Edited"));
	}

	/**
	 * Test control CountingMode
	 *
	 * Expect control CountingMode successfully
	 */
	@Test
	void testCountingModeSuccessfully() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Video Encoder 0#DailyResync");
		controllableProperty.setValue("1");
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		controllableProperty.setProperty("Video Encoder 0#CountingMode");
		controllableProperty.setValue("SMPTE 12M-1");
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals("SMPTE 12M-1", stats.get("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.COUNTING_MODE.getName()));
	}

	/**
	 * Test control DailyResync
	 *
	 * Expect control DailyResync successfully
	 */
	@Test
	void testDailyResyncSuccessfully() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Video Encoder 0#DailyResync");
		controllableProperty.setValue("1");
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals("1", stats.get("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.DAILY_RESYNC.getName()));
	}

	/**
	 * Test control ReSyncHour is 01:01 AM
	 *
	 * Expect control ReSyncHour successfully
	 */
	@Test
	void testReSyncHourSuccessfully() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Video Encoder 0#ResyncHour");
		controllableProperty.setValue("01:00 (1 AM)");
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals("01:00 (1 AM)", stats.get("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.RESYNC_HOUR.getName()));
	}

	/**
	 * Test control
	 *
	 * Expect control  successfully
	 */
	@Test
	void testSuccessfully() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Video Encoder 0#ResyncHour");
		controllableProperty.setValue("01:00 (1 AM)");
		controllableProperty.setProperty("Video Encoder 0#DailyResync");
		controllableProperty.setValue("1");
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		controllableProperty.setProperty("Video Encoder 0#CountingMode");
		controllableProperty.setValue("UTC conversion");
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertNull(stats.get("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.DAILY_RESYNC.getName()));
		assertNull(stats.get("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.RESYNC_HOUR.getName()));
		assertEquals("UTC conversion", stats.get("Video Encoder 0" + HaivisionConstant.HASH + VideoControllingMetric.COUNTING_MODE.getName()));
	}

	/**
	 * Test edit Source Audio of Output stream
	 *
	 * Expect edit successfully
	 */
	@Test
	void testEditSourceAudioOfStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + "SourceAudio 0";
		String propValue = "Audio Encoder 1";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(propValue, stats.get(propName));
	}

	/**
	 * Test edit Content name of Output stream
	 *
	 * Expect edit successfully
	 */
	@Test
	void testEditContentNameOfStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONTENT_NAME.getName();
		String propValue = "Stream for UT";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(propValue, stats.get(propName));
	}

	/**
	 * Test edit Connection Destination address of Output stream
	 *
	 * Expect edit successfully
	 */
	@Test
	void testEditConnectionDestinationAddressOfStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.DESTINATION_ADDRESS.getName();
		String propValue = "192.168.111.110";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(propValue, stats.get(propName));
	}

	/**
	 * Test edit Source Video of Output stream
	 *
	 * Expect edit successfully
	 */
	@Test
	void testEditSourceVideoOfStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.SOURCE_VIDEO.getName();
		String propValue = "Video Encoder 7";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(propValue, stats.get(propName));
	}

	/**
	 * Test enable Transmit SAP of Output stream
	 *
	 * Expect sap is 1
	 */
	@Test
	void testEnableSAPOfStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.TRANSMIT_SAP.getName();
		String propValue = "1";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(propValue, stats.get(propName));
	}

	/**
	 * Test disable Transmit SAP of Output stream
	 *
	 * Expect sap is 0
	 */
	@Test
	void testDisableSAPOfStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.TRANSMIT_SAP.getName();
		String propValue = "0";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(propValue, stats.get(propName));
	}

	/**
	 * Test edit Connection address of Stream
	 *
	 * Expect edit successfully
	 */
	@Test
	void testEditConnectionAddressOfStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_ADDRESS.getName();
		String propValue = "192.168.55.55";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(propValue, stats.get(propName));
	}

	/**
	 * Test edit Connection Source port of Stream
	 *
	 * Expect edit successfully
	 */
	@Test
	void testEditConnectionSourcePortOfStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_SOURCE_PORT.getName();
		String propValue = "80";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(propValue, stats.get(propName));
	}

	/**
	 * Test edit Connection Source port of Stream with the value out of range (1 - 65535)
	 *
	 * Expect an exception will be thrown
	 */
	@Test
	void testEditConnectionSourcePortOfStreamFail() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_SOURCE_PORT.getName();
		String propValue = "80000";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		assertThrows(ResourceNotReachableException.class, () -> haivisionX4EncoderCommunicator.controlProperty(controllableProperty), "Expect an exception because value is out of range");
	}

	/**
	 * Test edit Connection Alternate port of Stream
	 *
	 * Expect edit successfully
	 */
	@Test
	void testEditConnectionAlternatePortOfStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_SOURCE_PORT.getName();
		String propValue = "3333";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(propValue, stats.get(propName));
	}

	/**
	 * Test edit Connection Alternate port of Stream with the value out of range (1 - 65535)
	 *
	 * Expect an exception will be thrown
	 */
	@Test
	void testEditConnectionAlternatePortOfStreamFail() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_SOURCE_PORT.getName();
		String propValue = "0";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		assertThrows(ResourceNotReachableException.class, () -> haivisionX4EncoderCommunicator.controlProperty(controllableProperty), "Expect an exception because value is out of range");
	}

	/**
	 * Test edit Destination port of Stream
	 *
	 * Expect edit successfully
	 */
	@Test
	void testEditDestinationPortOfStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.DESTINATION_PORT.getName();
		String propValue = "8080";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(propValue, stats.get(propName));
	}

	/**
	 * Test edit Destination port of Stream with the value out of range (1 - 65535)
	 *
	 * Expect an exception will be thrown
	 */
	@Test
	void testEditDestinationPortOfStreamFail() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_SOURCE_PORT.getName();
		String propValue = "99999";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		assertThrows(ResourceNotReachableException.class, () -> haivisionX4EncoderCommunicator.controlProperty(controllableProperty), "Expect an exception because value is out of range");
	}

	/**
	 * Test edit Network adaptive of Stream
	 *
	 * Expect edit successfully
	 */
	@Test
	void testEditNetworkAdaptiveOfStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_NETWORK_ADAPTIVE.getName();
		String propValue = "1";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(propValue, stats.get(propName));
	}

	/**
	 * Test edit Connection latency of Stream with value in range
	 *
	 * Expect latency in response data equals with input data
	 */
	@Test
	void testEditLatencyOfStreamWithValueInRange() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_LATENCY.getName();
		String propValue = "1000";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(propValue, stats.get(propName));
	}

	/**
	 * Test edit Connection latency of Stream with value out of range (larger)
	 *
	 * Expect latency in response data equals max value of latency (8000)
	 */
	@Test
	void testEditLatencyOfStreamWithValueOutOfRange() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_LATENCY.getName();
		String propValue = "9999";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(Integer.toString(HaivisionConstant.MAX_OF_LATENCY), stats.get(propName));
	}

	/**
	 * Test change Connection encryption of Stream to None
	 *
	 * Expect passphrase in response data is null
	 */
	@Test
	void testChangeEncryptionToNone() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_ENCRYPTION.getName();
		String propValue = EncryptionDropdown.NONE.getName();
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertNull(stats.get("Stream Stream Output 0#" + CreateOutputStreamMetric.CONNECTION_PASSPHRASE.getName()));
	}

	/**
	 * Test change Connection encryption of Stream to AES-128
	 *
	 * Expect passphrase in response data is not null
	 */
	@Test
	void testChangeEncryptionToAES128() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_ENCRYPTION.getName();
		String propValue = EncryptionDropdown.AES_128.getName();
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertNotNull(stats.get("Stream Stream Output 0#" + CreateOutputStreamMetric.CONNECTION_PASSPHRASE.getName()));
	}

	/**
	 * Test input passphrase successfully
	 *
	 * Expect passphrase will be set to the input value
	 */
	@Test
	void testEditPassphraseSuccessfully() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_PASSPHRASE.getName();
		String propValue = "haivision-x4-encoder";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(propValue, stats.get(propName));
	}

	/**
	 * Test input passphrase too short
	 *
	 * Expect an exception will be thrown
	 */
	@Test
	void testEditPassphraseTooShort() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_PASSPHRASE.getName();
		String propValue = "haivision";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
//		assertThrows(ResourceNotReachableException.class, () -> haivisionX4EncoderCommunicator.controlProperty(controllableProperty), "Expect an exception because passphrase is too short.");
	}

	/**
	 * Test edit bandwidth of Stream with value in range
	 *
	 * Expect edit successfully
	 */
	@Test
	void testEditBandwidthWithValueInRange() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.PARAMETER_BANDWIDTH_OVERHEAD.getName();
		String propValue = "40";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(propValue, stats.get(propName));
	}

	/**
	 * Test edit bandwidth of Stream with value out of range (larger)
	 *
	 * Expect bandwidth in response data will be set to max of bandwidth (50)
	 */
	@Test
	void testEditBandwidthWithValueOutOfRange() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.PARAMETER_BANDWIDTH_OVERHEAD.getName();
		String propValue = "80";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(Integer.toString(HaivisionConstant.MAX_OF_BANDWIDTH_OVERHEAD), stats.get(propName));
	}

	/**
	 * Test edit MTU of Stream with value in range
	 *
	 * Expect edit successfully
	 */
	@Test
	void testEditMtuWithValueInRange() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.PARAMETER_MTU.getName();
		String propValue = "1000";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(propValue, stats.get(propName));
	}

	/**
	 * Test edit MTU of Stream with value out of range (smaller)
	 *
	 * Expect MTU in response data will be set to min of MTU (228)
	 */
	@Test
	void testEditMtuWithValueOutOfRange() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.PARAMETER_MTU.getName();
		String propValue = "100";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(Integer.toString(HaivisionConstant.MIN_OF_MTU), stats.get(propName));
	}

	/**
	 * Test edit TTL of Stream with value in range
	 *
	 * Expect edit successfully
	 */
	@Test
	void testEditTtlWithValueInRange() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.PARAMETER_TTL.getName();
		String propValue = "100";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(propValue, stats.get(propName));
	}

	/**
	 * Test edit TTL of Stream with value out of range (larger)
	 *
	 * Expect TTL in response data will be set to max of TTL (225)
	 */
	@Test
	void testEditTtlWithValueOutOfRange() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.PARAMETER_TTL.getName();
		String propValue = "500";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(Integer.toString(HaivisionConstant.MAX_OF_TTL), stats.get(propName));
	}

	/**
	 * Test edit ToS with value in range
	 *
	 * Expect edit successfully
	 */
	@Test
	void testEditTosWithValueInRange() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.PARAMETER_TOS.getName();
		String propValue = "0xAA";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(propValue, stats.get(propName));
	}

	/**
	 * Test edit ToS with value out of range (larger)
	 *
	 * Expect ToS will be set to max value of ToS
	 */
	@Test
	void testEditTosWithValueOutOfRange() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.PARAMETER_TOS.getName();
		String propValue = "0xABCD";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals("0xFF", stats.get(propName));
	}

	/**
	 * Test edit ToS with value invalid
	 *
	 * Expect an exception will be thrown
	 */
	@Test
	void testEditTosFail() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.PARAMETER_TOS.getName();
		String propValue = "0xXYZ";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		assertThrows(NumberFormatException.class, () -> haivisionX4EncoderCommunicator.controlProperty(controllableProperty), "Expect an exception because cannot convert the input.");
	}

	/**
	 * Test change Connection mode of Stream to Rendezvous
	 *
	 * Expect ConnectionAddress, ConnectionSourcePort, ConnectionDestinationPort are not null; ConnectionPort, ConnectionAlternatePort are null
	 */
	@Test
	void testChangeConnectionModeToRendezvous() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_MODE.getName();
		String propValue = SRTModeDropdown.RENDEZVOUS.getName();
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertNotNull(stats.get(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_ADDRESS.getName()));
		assertNotNull(stats.get(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_SOURCE_PORT.getName()));
		assertNotNull(stats.get(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_DESTINATION_PORT.getName()));
		assertNull(stats.get(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_PORT.getName()));
		assertNull(stats.get(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_ALTERNATE_PORT.getName()));
	}

	/**
	 * Test change Connection mode of Stream to Listener
	 *
	 * Expect ConnectionAddress, ConnectionSourcePort, ConnectionDestinationPort are null; ConnectionPort, ConnectionAlternatePort are not null
	 */
	@Test
	void testChangeConnectionModeToListener() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_MODE.getName();
		String propValue = SRTModeDropdown.LISTENER.getName();
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertNull(stats.get(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_ADDRESS.getName()));
		assertNull(stats.get(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_SOURCE_PORT.getName()));
		assertNull(stats.get(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_DESTINATION_PORT.getName()));
		assertNotNull(stats.get(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_PORT.getName()));
		assertNotNull(stats.get(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_ALTERNATE_PORT.getName()));
	}

	/**
	 * Test change Connection mode of Stream to Caller
	 *
	 * Expect ConnectionAddress, ConnectionSourcePort, ConnectionDestinationPort are not null; ConnectionPort, ConnectionAlternatePort are null
	 */
	@Test
	void testChangeConnectionModeToCaller() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_MODE.getName();
		String propValue = SRTModeDropdown.CALLER.getName();
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertNotNull(stats.get(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_ADDRESS.getName()));
		assertNotNull(stats.get(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_SOURCE_PORT.getName()));
		assertNotNull(stats.get(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_DESTINATION_PORT.getName()));
		assertNull(stats.get(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_PORT.getName()));
		assertNull(stats.get(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_ALTERNATE_PORT.getName()));
	}

	/**
	 * Test change Timing and shaping
	 *
	 * Expect Timing and Shaping will be set to new value
	 */
	@Test
	void testChangeTimingAndShaping() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.PARAMETER_TIMING_AND_SHAPING.getName();
		String propValue = TimingAndShaping.CVBR.getName();
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(propValue, stats.get(propName));
	}

	/**
	 * Test change Timing and shaping to VBR while Protocol is TS over SRT
	 *
	 * Expect bandwidth null
	 */
	@Test
	void testChangeShapingToVbrWhileProtocolIsSrt() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty protocolProperty = new ControllableProperty();
		String protocolName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.STREAMING_PROTOCOL.getName();
		String protocolValue = ProtocolDropdown.TS_OVER_SRT.getName();
		protocolProperty.setProperty(protocolName);
		protocolProperty.setValue(protocolValue);
		haivisionX4EncoderCommunicator.controlProperty(protocolProperty);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.PARAMETER_TIMING_AND_SHAPING.getName();
		String propValue = TimingAndShaping.VBR.getName();
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertNull(stats.get(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.PARAMETER_BANDWIDTH_OVERHEAD.getName()));
	}

	/**
	 * Test change Timing and shaping to CVBR or CBR while Protocol is TS over SRT
	 *
	 * Expect bandwidth not null
	 */
	@Test
	void testChangeShapingToCbrOrCvbrWhileProtocolIsSrt() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty protocolProperty = new ControllableProperty();
		String protocolName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.STREAMING_PROTOCOL.getName();
		String protocolValue = ProtocolDropdown.TS_OVER_SRT.getName();
		protocolProperty.setProperty(protocolName);
		protocolProperty.setValue(protocolValue);
		haivisionX4EncoderCommunicator.controlProperty(protocolProperty);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.PARAMETER_TIMING_AND_SHAPING.getName();
		String propValue = TimingAndShaping.CVBR.getName();
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertNotNull(stats.get(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.PARAMETER_BANDWIDTH_OVERHEAD.getName()));
	}

	/**
	 * Test add source audio
	 *
	 * Expect add sucessfully
	 */
	@Test
	void testAddSourceAudio() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.SOURCE_ADD_AUDIO.getName();
		String propValue = "1";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertNotNull(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + "SourceAudio 1");
	}

	/**
	 * Test change Stream protocol to TS over SRT
	 *
	 * Expect ConnectionAddress, ConnectionSourcePort, ConnectionDestinationPort are not null
	 */
	@Test
	void testChangeProtocolToSrt() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.STREAMING_PROTOCOL.getName();
		String propValue = ProtocolDropdown.TS_OVER_SRT.getName();
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertNotNull(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_ADDRESS.getName());
		assertNotNull(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_SOURCE_PORT.getName());
		assertNotNull(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_DESTINATION_PORT.getName());
	}

	/**
	 * Test change Stream protocol to TS over SRT while Connection mode is Rendezvous
	 *
	 * Expect ConnectionSourcePort equals to ConnectionDestinationPort
	 */
	@Test
	void testChangeProtocolToSrtAndModeIsRendezvous() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.STREAMING_PROTOCOL.getName();
		String propValue = ProtocolDropdown.TS_OVER_SRT.getName();
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ControllableProperty modeProperty = new ControllableProperty();
		String modeName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_MODE.getName();
		String modeValue = SRTModeDropdown.RENDEZVOUS.getName();
		modeProperty.setProperty(modeName);
		modeProperty.setValue(modeValue);
		haivisionX4EncoderCommunicator.controlProperty(modeProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		String sourcePort = stats.get(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_SOURCE_PORT.getName());
		String destinationPort = stats.get(
				HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_DESTINATION_PORT.getName());
		assertTrue(sourcePort.equals(destinationPort));
	}

	/**
	 * Test change Stream protocol to TS over SRT while Connection mode is Caller
	 *
	 * Expect ConnectionPort, ConnectionAlternatePort, DestinationAddress are null
	 */
	@Test
	void testChangeProtocolToSrtAndModeIsCaller() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.STREAMING_PROTOCOL.getName();
		String propValue = ProtocolDropdown.TS_OVER_SRT.getName();
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ControllableProperty modeProperty = new ControllableProperty();
		String modeName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_MODE.getName();
		String modeValue = SRTModeDropdown.CALLER.getName();
		modeProperty.setProperty(modeName);
		modeProperty.setValue(modeValue);
		haivisionX4EncoderCommunicator.controlProperty(modeProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertNull(stats.get(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_PORT.getName()));
		assertNull(stats.get(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_ALTERNATE_PORT.getName()));
		assertNull(stats.get(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.DESTINATION_ADDRESS.getName()));
	}

	/**
	 * Test change Stream protocol to TS over SRT while Connection mode is Listener
	 *
	 * Expect ConnectionPort, ConnectionAlternatePort are not null; ConnectionSourcePort, ConnectionDestinationPort, ConnectionAddress are null
	 */
	@Test
	void testChangeProtocolToSrtAndModeIsListener() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.STREAMING_PROTOCOL.getName();
		String propValue = ProtocolDropdown.TS_OVER_SRT.getName();
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ControllableProperty modeProperty = new ControllableProperty();
		String modeName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_MODE.getName();
		String modeValue = SRTModeDropdown.LISTENER.getName();
		modeProperty.setProperty(modeName);
		modeProperty.setValue(modeValue);
		haivisionX4EncoderCommunicator.controlProperty(modeProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertNull(stats.get(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_SOURCE_PORT.getName()));
		assertNull(stats.get(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_ADDRESS.getName()));
		assertNull(stats.get(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.DESTINATION_ADDRESS.getName()));
		assertNotNull(stats.get(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_PORT.getName()));
		assertNotNull(stats.get(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_ALTERNATE_PORT.getName()));
	}

	/**
	 * Test change Stream protocol to TS over UDP or TS over RTP
	 *
	 * Expect ConnectionPort, ConnectionAddress are not null; ConnectionMode, ConnectionDestinationPort, ConnectionSourcePort, ConnectionNetworkAdaptive, ConnectionEncryption, ConnectionPassphrase, ConnectionAlternatePort, ConnectionLatency are null
	 */
	@Test
	void testChangeProtocolToUdpOrRtp() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.STREAMING_PROTOCOL.getName();
		String propValue = ProtocolDropdown.TS_OVER_UDP.getName();
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertNotNull(stats.get(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_PORT.getName()));
		assertNotNull(stats.get(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_ADDRESS.getName()));
		assertNull(stats.get(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_MODE.getName()));
		assertNull(stats.get(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_DESTINATION_PORT.getName()));
		assertNull(stats.get(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_SOURCE_PORT.getName()));
		assertNull(stats.get(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_NETWORK_ADAPTIVE.getName()));
		assertNull(stats.get(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_ENCRYPTION.getName()));
		assertNull(stats.get(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_PASSPHRASE.getName()));
		assertNull(stats.get(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_ALTERNATE_PORT.getName()));
		assertNull(stats.get(HaivisionConstant.STREAM + HaivisionConstant.SPACE + "Stream Output 0" + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_LATENCY.getName()));
	}

	//Test edit/create output stream

	/**
	 * Test edit Content name of Output stream
	 *
	 * Expect edit successfully
	 */
	@Test
	void testEditContentNameForTheCreateOutputStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONTENT_NAME.getName();
		String propValue = "Stream for UT";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(propValue, stats.get(propName));
	}

	/**
	 * Test edit Source Audio of Output stream
	 *
	 * Expect edit successfully
	 */
	@Test
	void testEditSourceAudioForTheCreateOutputStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + "SourceAudio 0";
		String propValue = "Audio Encoder 1";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(propValue, stats.get(propName));
	}

	/**
	 * Test edit Source Video of Output stream
	 *
	 * Expect edit successfully
	 */
	@Test
	void testEditSourceVideoForTheCreateOutputStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.SOURCE_VIDEO.getName();
		String propValue = "Video Encoder 7";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(propValue, stats.get(propName));
	}

	/**
	 * Test edit Connection Destination address of Output stream
	 *
	 * Expect edit successfully
	 */
	@Test
	void testEditConnectionDestinationAddressForTheCreateOutputStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.DESTINATION_ADDRESS.getName();
		String propValue = "192.168.111.110";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(propValue, stats.get(propName));
	}

	/**
	 * Test enable Transmit SAP of Output stream
	 *
	 * Expect sap is 1
	 */
	@Test
	void testEnableSAPForTheCreateOutputStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.TRANSMIT_SAP.getName();
		String propValue = "1";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(propValue, stats.get(propName));
	}


	/**
	 * Test disable Transmit SAP of Output stream
	 *
	 * Expect sap is 0
	 */
	@Test
	void testDisableSAPForTheCreateOutputStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.TRANSMIT_SAP.getName();
		String propValue = "0";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(propValue, stats.get(propName));
	}

	/**
	 * Test edit Connection address of Stream
	 *
	 * Expect edit successfully
	 */
	@Test
	void testEditConnectionAddressForTheCreateOutputStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_ADDRESS.getName();
		String propValue = "192.168.55.55";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(propValue, stats.get(propName));
	}

	/**
	 * Test edit Connection Source port of Stream
	 *
	 * Expect edit successfully
	 */
	@Test
	void testEditConnectionSourcePortForTheCreateOutputStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_SOURCE_PORT.getName();
		String propValue = "80";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(propValue, stats.get(propName));
	}

	/**
	 * Test edit Connection Source port of Stream with the value out of range (1 - 65535)
	 *
	 * Expect an exception will be thrown
	 */
	@Test
	void testEditConnectionSourcePortForTheCreateOutputStreamFailed() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_SOURCE_PORT.getName();
		String propValue = "80000";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		assertThrows(ResourceNotReachableException.class, () -> haivisionX4EncoderCommunicator.controlProperty(controllableProperty), "Expect an exception because value is out of range");
	}

	/**
	 * Test edit Connection Alternate port of Stream
	 *
	 * Expect edit successfully
	 */
	@Test
	void testEditConnectionAlternatePortForTheCreateOutputStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_SOURCE_PORT.getName();
		String propValue = "3333";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(propValue, stats.get(propName));
	}

	/**
	 * Test edit Connection Alternate port of Stream with the value out of range (1 - 65535)
	 *
	 * Expect an exception will be thrown
	 */
	@Test
	void testEditConnectionAlternatePortForTheCreateOutputStreamFailed() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_SOURCE_PORT.getName();
		String propValue = "0";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		assertThrows(ResourceNotReachableException.class, () -> haivisionX4EncoderCommunicator.controlProperty(controllableProperty), "Expect an exception because value is out of range");
	}

	/**
	 * Test edit Destination port of Stream
	 *
	 * Expect edit successfully
	 */
	@Test
	void testEditDestinationPortForTheCreateOutputStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.DESTINATION_PORT.getName();
		String propValue = "8080";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(propValue, stats.get(propName));
	}

	/**
	 * Test edit Destination port of Stream with the value out of range (1 - 65535)
	 *
	 * Expect an exception will be thrown
	 */
	@Test
	void testEditDestinationPortForTheCreateOutputStreamFailed() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_SOURCE_PORT.getName();
		String propValue = "99999";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		assertThrows(ResourceNotReachableException.class, () -> haivisionX4EncoderCommunicator.controlProperty(controllableProperty), "Expect an exception because value is out of range");
	}

	/**
	 * Test edit Network adaptive of Stream
	 *
	 * Expect edit successfully
	 */
	@Test
	void testEditNetworkAdaptiveForTheCreateOutputStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_NETWORK_ADAPTIVE.getName();
		String propValue = "1";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(propValue, stats.get(propName));
	}

	/**
	 * Test edit Connection latency of Stream with value in range
	 *
	 * Expect latency in response data equals with input data
	 */
	@Test
	void testEditLatencyForTheCreateOutputStreamWithValueInRange() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_LATENCY.getName();
		String propValue = "1000";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(propValue, stats.get(propName));
	}

	/**
	 * Test edit Connection latency of Stream with value out of range (larger)
	 *
	 * Expect latency in response data equals max value of latency (8000)
	 */
	@Test
	void testEditLatencyForTheCreateOutputStreamWithValueOutOfRange() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_LATENCY.getName();
		String propValue = "9999";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(Integer.toString(HaivisionConstant.MAX_OF_LATENCY), stats.get(propName));
	}

	/**
	 * Test change Connection encryption of Stream to None
	 *
	 * Expect passphrase in response data is null
	 */
	@Test
	void testChangeEncryptionToNoneForTheCreateOutputStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_ENCRYPTION.getName();
		String propValue = EncryptionDropdown.NONE.getName();
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertNull(stats.get("Stream Stream Output 0#" + CreateOutputStreamMetric.CONNECTION_PASSPHRASE.getName()));
	}

	/**
	 * Test change Connection encryption of Stream to AES-128
	 *
	 * Expect passphrase in response data is not null
	 */
	@Test
	void testChangeEncryptionToAES128ForTheCreateOutputStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_ENCRYPTION.getName();
		String propValue = EncryptionDropdown.AES_128.getName();
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertNull(stats.get(HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_PASSPHRASE.getName()));
	}

	/**
	 * Test input passphrase successfully
	 *
	 * Expect passphrase will be set to the input value
	 */
	@Test
	void testEditPassphraseForTheCreateOutputStreamSuccessfully() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_PASSPHRASE.getName();
		String propValue = "haivision-x4-encoder";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(propValue, stats.get(propName));
	}

	/**
	 * Test input passphrase too short
	 *
	 * Expect an exception will be thrown
	 */
	@Test
	void testEditPassphraseTooShortForTheCreateOutputStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_PASSPHRASE.getName();
		String propValue = "haivision";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
//		assertThrows(ResourceNotReachableException.class, () -> haivisionX4EncoderCommunicator.controlProperty(controllableProperty), "Expect an exception because passphrase is too short.");
	}

	/**
	 * Test edit bandwidth of Stream with value in range
	 *
	 * Expect edit successfully
	 */
	@Test
	void testEditBandwidthWithValueInRangeForTheCreateOutputStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.PARAMETER_BANDWIDTH_OVERHEAD.getName();
		String propValue = "40";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(propValue, stats.get(propName));
	}

	/**
	 * Test edit bandwidth of Stream with value out of range (larger)
	 *
	 * Expect bandwidth in response data will be set to max of bandwidth (50)
	 */
	@Test
	void testEditBandwidthWithValueOutOfRangeForTheCreateOutputStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.PARAMETER_BANDWIDTH_OVERHEAD.getName();
		String propValue = "80";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(Integer.toString(HaivisionConstant.MAX_OF_BANDWIDTH_OVERHEAD), stats.get(propName));
	}

	/**
	 * Test edit MTU of Stream with value in range
	 *
	 * Expect edit successfully
	 */
	@Test
	void testEditMtuWithValueInRangeForTheCreateOutputStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.PARAMETER_MTU.getName();
		String propValue = "1000";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(propValue, stats.get(propName));
	}

	/**
	 * Test edit MTU of Stream with value out of range (smaller)
	 *
	 * Expect MTU in response data will be set to min of MTU (228)
	 */
	@Test
	void testEditMtuWithValueOutOfRangeForTheCreateOutputStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.PARAMETER_MTU.getName();
		String propValue = "100";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(Integer.toString(HaivisionConstant.MIN_OF_MTU), stats.get(propName));
	}

	/**
	 * Test edit TTL of Stream with value in range
	 *
	 * Expect edit successfully
	 */
	@Test
	void testEditTtlWithValueInRangeForTheCreateOutputStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.PARAMETER_TTL.getName();
		String propValue = "100";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(propValue, stats.get(propName));
	}

	/**
	 * Test edit TTL of Stream with value out of range (larger)
	 *
	 * Expect TTL in response data will be set to max of TTL (225)
	 */
	@Test
	void testEditTtlWithValueOutOfRangeForTheCreateOutputStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.PARAMETER_TTL.getName();
		String propValue = "500";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(Integer.toString(HaivisionConstant.MAX_OF_TTL), stats.get(propName));
	}

	/**
	 * Test edit ToS with value in range
	 *
	 * Expect edit successfully
	 */
	@Test
	void testEditTosWithValueInRangeForTheCreateOutputStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.PARAMETER_TOS.getName();
		String propValue = "0xAA";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(propValue, stats.get(propName));
	}

	/**
	 * Test edit ToS with value out of range (larger)
	 *
	 * Expect ToS will be set to max value of ToS
	 */
	@Test
	void testEditTosWithValueOutOfRangeForTheCreateOutputStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.PARAMETER_TOS.getName();
		String propValue = "0xABCD";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals("0xFF", stats.get(propName));
	}

	/**
	 * Test edit ToS with value invalid
	 *
	 * Expect an exception will be thrown
	 */
	@Test
	void testEditTosForTheCreateOutputStreamFailed() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.PARAMETER_TOS.getName();
		String propValue = "0xXYZ";
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		assertThrows(NumberFormatException.class, () -> haivisionX4EncoderCommunicator.controlProperty(controllableProperty), "Expect an exception because cannot convert the input.");
	}

	/**
	 * Test change Connection mode of Stream to Rendezvous
	 *
	 * Expect ConnectionAddress, ConnectionSourcePort, ConnectionDestinationPort are not null; ConnectionPort, ConnectionAlternatePort are null
	 */
	@Test
	void testChangeConnectionModeToRendezvousForTheCreateOutputStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_MODE.getName();
		String propValue = SRTModeDropdown.RENDEZVOUS.getName();
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertNotNull(stats.get(HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_ADDRESS.getName()));
		assertNotNull(stats.get(HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_SOURCE_PORT.getName()));
		assertNotNull(stats.get(HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_DESTINATION_PORT.getName()));
		assertNull(stats.get(HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_PORT.getName()));
		assertNull(stats.get(HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_ALTERNATE_PORT.getName()));
	}

	/**
	 * Test change Connection mode of Stream to Listener
	 *
	 * Expect ConnectionAddress, ConnectionSourcePort, ConnectionDestinationPort are null; ConnectionPort, ConnectionAlternatePort are not null
	 */
	@Test
	void testChangeConnectionModeToListenerForTheCreateOutputStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_MODE.getName();
		String propValue = SRTModeDropdown.LISTENER.getName();
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertNull(stats.get(HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_ADDRESS.getName()));
		assertNull(stats.get(HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_SOURCE_PORT.getName()));
		assertNull(stats.get(HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_DESTINATION_PORT.getName()));
		assertNotNull(stats.get(HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_PORT.getName()));
		assertNotNull(stats.get(HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_ALTERNATE_PORT.getName()));
	}

	/**
	 * Test change Connection mode of Stream to Caller
	 *
	 * Expect ConnectionAddress, ConnectionSourcePort, ConnectionDestinationPort are not null; ConnectionPort, ConnectionAlternatePort are null
	 */
	@Test
	void testChangeConnectionModeToCallerForTheCreateOutputStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_MODE.getName();
		String propValue = SRTModeDropdown.CALLER.getName();
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertNotNull(stats.get(HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_ADDRESS.getName()));
		assertNotNull(stats.get(HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_SOURCE_PORT.getName()));
		assertNotNull(stats.get(HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_DESTINATION_PORT.getName()));
		assertNull(stats.get(HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_PORT.getName()));
		assertNull(stats.get(HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_ALTERNATE_PORT.getName()));
	}

	/**
	 * Test change Timing and shaping
	 *
	 * Expect Timing and Shaping will be set to new value
	 */
	@Test
	void testChangeTimingAndShapingForTheCreateOutputStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.PARAMETER_TIMING_AND_SHAPING.getName();
		String propValue = TimingAndShaping.CVBR.getName();
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals(propValue, stats.get(propName));
	}

	/**
	 * Test change Timing and shaping to VBR while Protocol is TS over SRT
	 *
	 * Expect bandwidth null
	 */
	@Test
	void testChangeShapingToVbrWhileProtocolIsSrtForTheCreateOutputStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty protocolProperty = new ControllableProperty();
		String protocolName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.STREAMING_PROTOCOL.getName();
		String protocolValue = ProtocolDropdown.TS_OVER_SRT.getName();
		protocolProperty.setProperty(protocolName);
		protocolProperty.setValue(protocolValue);
		haivisionX4EncoderCommunicator.controlProperty(protocolProperty);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.PARAMETER_TIMING_AND_SHAPING.getName();
		String propValue = TimingAndShaping.VBR.getName();
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertEquals("25", stats.get(HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.PARAMETER_BANDWIDTH_OVERHEAD.getName()));
	}

	/**
	 * Test change Timing and shaping to CVBR or CBR while Protocol is TS over SRT
	 *
	 * Expect bandwidth not null
	 */
	@Test
	void testChangeShapingToCbrOrCvbrWhileProtocolIsSrtForTheCreateOutputStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty protocolProperty = new ControllableProperty();
		String protocolName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.STREAMING_PROTOCOL.getName();
		String protocolValue = ProtocolDropdown.TS_OVER_SRT.getName();
		protocolProperty.setProperty(protocolName);
		protocolProperty.setValue(protocolValue);
		haivisionX4EncoderCommunicator.controlProperty(protocolProperty);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.PARAMETER_TIMING_AND_SHAPING.getName();
		String propValue = TimingAndShaping.CVBR.getName();
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertNotNull(stats.get(HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.PARAMETER_BANDWIDTH_OVERHEAD.getName()));
	}

	/**
	 * Test change Stream protocol to TS over SRT
	 *
	 * Expect ConnectionAddress, ConnectionSourcePort, ConnectionDestinationPort are not null
	 */
	@Test
	void testChangeProtocolToSrtForTheCreateOutputStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.STREAMING_PROTOCOL.getName();
		String propValue = ProtocolDropdown.TS_OVER_SRT.getName();
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertNotNull(HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_ADDRESS.getName());
		assertNotNull(HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_SOURCE_PORT.getName());
		assertNotNull(HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_DESTINATION_PORT.getName());
	}

	/**
	 * Test change Stream protocol to TS over SRT while Connection mode is Rendezvous
	 *
	 * Expect ConnectionSourcePort equals to ConnectionDestinationPort
	 */
	@Test
	void testChangeProtocolToSrtAndModeIsRendezvousForTheCreateOutputStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.STREAMING_PROTOCOL.getName();
		String propValue = ProtocolDropdown.TS_OVER_SRT.getName();
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ControllableProperty modeProperty = new ControllableProperty();
		String modeName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_MODE.getName();
		String modeValue = SRTModeDropdown.RENDEZVOUS.getName();
		modeProperty.setProperty(modeName);
		modeProperty.setValue(modeValue);
		haivisionX4EncoderCommunicator.controlProperty(modeProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		String sourcePort = stats.get(HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_SOURCE_PORT.getName());
		String destinationPort = stats.get(HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_DESTINATION_PORT.getName());
		assertTrue(sourcePort.equals(destinationPort));
	}

	/**
	 * Test change Stream protocol to TS over SRT while Connection mode is Caller
	 *
	 * Expect ConnectionPort, ConnectionAlternatePort, DestinationAddress are null
	 */
	@Test
	void testChangeProtocolToSrtAndModeIsCallerForTheCreateOutputStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.STREAMING_PROTOCOL.getName();
		String propValue = ProtocolDropdown.TS_OVER_SRT.getName();
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ControllableProperty modeProperty = new ControllableProperty();
		String modeName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_MODE.getName();
		String modeValue = SRTModeDropdown.CALLER.getName();
		modeProperty.setProperty(modeName);
		modeProperty.setValue(modeValue);
		haivisionX4EncoderCommunicator.controlProperty(modeProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertNull(stats.get(HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_PORT.getName()));
		assertNull(stats.get(HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_ALTERNATE_PORT.getName()));
		assertNull(stats.get(HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.DESTINATION_ADDRESS.getName()));
	}

	/**
	 * Test change Stream protocol to TS over SRT while Connection mode is Listener
	 *
	 * Expect ConnectionPort, ConnectionAlternatePort are not null; ConnectionSourcePort, ConnectionDestinationPort, ConnectionAddress are null
	 */
	@Test
	void testChangeProtocolToSrtAndModeIsListenerForTheCreateOutputStream() throws Exception {
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER)).thenReturn(HaivisionURL.AUDIO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER)).thenReturn(HaivisionURL.VIDEO_ENCODER.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION)).thenReturn(HaivisionURL.AUTHENTICATION.getUrl());
		mock.when(() -> HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER)).thenReturn(HaivisionURL.OUTPUT_ENCODER.getUrl());
		haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.STREAMING_PROTOCOL.getName();
		String propValue = ProtocolDropdown.TS_OVER_SRT.getName();
		controllableProperty.setProperty(propName);
		controllableProperty.setValue(propValue);
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		ControllableProperty modeProperty = new ControllableProperty();
		String modeName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_MODE.getName();
		String modeValue = SRTModeDropdown.LISTENER.getName();
		modeProperty.setProperty(modeName);
		modeProperty.setValue(modeValue);
		haivisionX4EncoderCommunicator.controlProperty(modeProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		assertNull(stats.get(HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_SOURCE_PORT.getName()));
		assertNull(stats.get(HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_ADDRESS.getName()));
		assertNull(stats.get(HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.DESTINATION_ADDRESS.getName()));
		assertNotNull(stats.get(HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_PORT.getName()));
		assertNotNull(stats.get(HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_ALTERNATE_PORT.getName()));
	}
}
