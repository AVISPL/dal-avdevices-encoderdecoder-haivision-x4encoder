/*
 * Copyright (c) 2022-2025 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder;

import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import org.junit.jupiter.api.*;

import java.util.Map;

/**
 * Unit test for HaivisionX4EncoderCommunicator
 *
 * @author Maksym.Rossiitsev / Symphony Dev Team<br>
 * Created on 3/17/2025
 * @version 1.0
 * @since 1.0
 */
class HaivisionX4EncoderCommunicatorTest {
	private final HaivisionX4EncoderCommunicator haivisionX4EncoderCommunicator = new HaivisionX4EncoderCommunicator();

	@BeforeEach()
	public void setUp() throws Exception {
		haivisionX4EncoderCommunicator.setHost("****");
		haivisionX4EncoderCommunicator.setPort(443);
		haivisionX4EncoderCommunicator.setLogin("***");
		haivisionX4EncoderCommunicator.setPassword("***");
		haivisionX4EncoderCommunicator.init();
		haivisionX4EncoderCommunicator.connect();
	}

	@AfterEach()
	public void destroy() throws Exception {
		haivisionX4EncoderCommunicator.disconnect();
	}

	/**
	 * Test HaivisionX4DecoderCommunicator.getMultipleStatistics successful with valid username password
	 * Expected retrieve valid device monitoring data
	 */
	@Tag("RealDevice")
	@Test
	void testHaivisionX4DecoderCommunicatorGetMonitoringDataSuccessful() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) haivisionX4EncoderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assertions.assertNotNull(stats);
		Assertions.assertFalse(stats.isEmpty());
	}

	/**
	 * Test HaivisionX4DecoderCommunicator.internalDestroy Successful
	 * Expected does not throw NPE
	 */
	@Tag("RealDevice")
	@Test
	void testHaivisionX4DecoderInternalDestroySuccessful() {
		Assertions.assertDoesNotThrow( () -> {
			haivisionX4EncoderCommunicator.getMultipleStatistics();
			haivisionX4EncoderCommunicator.destroy();
		});
	}
}
