/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;

/**
 * Unit test for simple App.
 *
 * @author Ivan
 * @since 1.0.0
 */
public class EncoderCommunicatorTest {

	static HaivisionX4EncoderCommunicator haivisionX4EncoderCommunicator;
	private static final int HTTP_PORT = 80;
	private static final int HTTPS_PORT = 8443;
	private static final String HOST_NAME = "127.0.0.1";
	private static final String PROTOCOL = "http";

	@BeforeEach
	public void init() throws Exception {
		haivisionX4EncoderCommunicator = new HaivisionX4EncoderCommunicator();
		haivisionX4EncoderCommunicator.setTrustAllCertificates(false);
		haivisionX4EncoderCommunicator.setProtocol(PROTOCOL);
		haivisionX4EncoderCommunicator.setHost(HOST_NAME);
		haivisionX4EncoderCommunicator.setContentType("application/json");
		haivisionX4EncoderCommunicator.setPort(443);
		haivisionX4EncoderCommunicator.setLogin("operator");
		haivisionX4EncoderCommunicator.setPassword("supervisor");
		haivisionX4EncoderCommunicator.init();
	}

	@AfterEach
	void stopWireMockRule() {
		haivisionX4EncoderCommunicator.destroy();
	}

	/**
	 * Rigorous Test :-)
	 */
	@Test
	void test() throws Exception {
	//TODO
	}
}
