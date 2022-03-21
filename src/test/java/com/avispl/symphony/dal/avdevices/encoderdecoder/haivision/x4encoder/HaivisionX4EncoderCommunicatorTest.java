/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.CreateOutputStreamMetric;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.EncryptionDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.ProtocolDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.SRTModeDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.audio.Audio;

/**
 * Unit test for simple App.
 *
 * @author Ivan
 * @since 1.0.0
 */
public class HaivisionX4EncoderCommunicatorTest {

	static HaivisionX4EncoderCommunicator haivisionX4EncoderCommunicator;
	private static final int HTTP_PORT = 80;
	private static final int HTTPS_PORT = 8443;
	//	private static final String HOST_NAME = "127.0.0.1";
	private static final String HOST_NAME = "66.254.60.71";
	private static final String PROTOCOL = "https";


	@BeforeEach
	public void init() throws Exception {
		haivisionX4EncoderCommunicator = new HaivisionX4EncoderCommunicator();
		haivisionX4EncoderCommunicator.setTrustAllCertificates(true);
		haivisionX4EncoderCommunicator.setHost(HOST_NAME);
		haivisionX4EncoderCommunicator.setPort(4443);
		haivisionX4EncoderCommunicator.setContentType("application/json");
		haivisionX4EncoderCommunicator.setProtocol("https");
		haivisionX4EncoderCommunicator.setLogin("operatortest");
		haivisionX4EncoderCommunicator.setPassword("111111");
		haivisionX4EncoderCommunicator.init();
		haivisionX4EncoderCommunicator.authenticate();
	}

	@Test
	void testApplyChangeSuccessfully() throws Exception {

//		ExtendedStatistics extendedStatistic = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
//		Map<String, String> stats = extendedStatistic.getStatistics();
//		ControllableProperty controllableProperty =new ControllableProperty();
//		controllableProperty.setProperty("Video Encoder 5#"+ VideoControllingMetric.TIME_CODE_SOURCE.getName());
//		controllableProperty.setValue(TimeCodeSource.VIDEO.getName());
//		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
//		controllableProperty.setProperty("Video Encoder 3#ApplyChange");
//		controllableProperty.setValue("1");
//		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
//
//		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
	}

	@Test
	public void payLoad() {
		Audio audios = new Audio();
		audios.setId("1");
		List<Audio> audio = new ArrayList<>();
		audio.add(audios);
		StringBuilder audioPayload = new StringBuilder();
		if (audio.size() > 0) {
			audioPayload.append("[");
			for (Audio audioItem : audio) {
				audioPayload.append("{\"id\":\"" + audioItem.getId() + "\"}");
				if(!audioItem.equals(audio.get(audio.size()-1))){
					audioPayload.append(",");
				}
			}
			audioPayload.append("]");
//			audioPayload.replace(audioPayload.lastIndexOf(","), audioPayload.lastIndexOf(",") + 1, "");
		}
		System.out.println(audioPayload);
	}

	@Test
	void test() throws Exception {
		haivisionX4EncoderCommunicator.setStreamNameFilter("Ivan test 01");
		ExtendedStatistics extendedStatistic = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistic.getStatistics();

		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Stream Ivan test 01#SourceAudio 0");
		controllableProperty.setValue("None");
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
//		controllableProperty.setProperty("Stream Ivan test 01#"+ CreateOutputStreamMetric.CONNECTION_ADDRESS.getName());
//		controllableProperty.setValue("128.3.3.6");
//		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
//		controllableProperty.setProperty("Stream Ivan test 01#ConnectionPassphrase");
//		controllableProperty.setValue("12346578");
//		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
//		controllableProperty.setProperty("Stream Ivan test 01#");
//		controllableProperty.setValue("Start");
//		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
		controllableProperty.setProperty("Stream Ivan test 01#ApplyChange");
		controllableProperty.setValue("1");
		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
//		haivisionX4EncoderCommunicator.setStreamNameFilter("Test Ivan 04");
//		ExtendedStatistics extendedStatistic = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
//		Map<String, String> stats = extendedStatistic.getStatistics();
//
//		ControllableProperty controllableProperty = new ControllableProperty();
//		controllableProperty.setProperty("Test Ivan 04#Action");
//		controllableProperty.setValue("Start");
//		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
//		controllableProperty.setProperty("Test Ivan 04#ApplyChange");
//		controllableProperty.setValue("1");
//		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
//		controllableProperty.setProperty("Video Encoder 5#Action");
//		controllableProperty.setValue("Start");
//		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
//		controllableProperty.setProperty("Video Encoder 5#Action");
//		controllableProperty.setValue("None");
//		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
//		controllableProperty.setProperty("Video Encoder 5#ApplyChange");
//		controllableProperty.setValue("1");
//		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);

//		controllableProperty.setProperty("Video Encoder 2#ApplyChange");
//		controllableProperty.setValue("1");
//		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
//		controllableProperty.setProperty("Audio Encoder 3#Action");
//		controllableProperty.setValue("Stop");
//		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
//		controllableProperty.setProperty("Audio Encoder 3#ApplyChange");
//		controllableProperty.setValue("1");
//		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
//		controllableProperty.setProperty("Audio Encoder 3#Action");
//		controllableProperty.setValue("Start");
//		haivisionX4EncoderCommunicator.controlProperty(controllableProperty);
//
//		controllableProperty.setProperty("Audio Encoder 3#ApplyChange");
//		controllableProperty.setValue("1");
	}

}