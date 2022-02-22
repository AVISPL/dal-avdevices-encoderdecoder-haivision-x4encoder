/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common;

/**
 * OutputStreamMonitoringMetric class defined the enum for the monitoring process
 *
 * @author Ivan
 * @version 1.0
 * @since 1.0
 */
public enum OutputMonitoringMetric {

	STATE("State"),
	UPTIME("Uptime"),
	SOURCE_PORT("SourcePort"),
	SENT_PACKETS("SentPackets"),
	SENT_BYTES("SentBytes"),
	BITRATE("Bitrate"),
	RECONNECTIONS("Reconnections"),
	RESENT_PACKETS("ResentPackets"),
	RESENT_BYTES("ResentBytes"),
	DROPPED_PACKETS("DroppedPackets"),
	DROPPED_BYTES("DroppedBytes"),
	MSS("MSS"),
	MAX_BANDWIDTH("MaxBandwidth"),
	REMOTE_PORT("RemotePort"),
	SOURCE_ADDRESS("SourceAddress"),
	REMOTE_ADDRESS("RemoteAddress"),
	PATH_MAX_BANDWIDTH("PathMaxBandwidth"),
	LOST_PACKETS("LostPackets"),
	RECV_ACK("RecvACK"),
	RECV_NAK("RecvNAK"),
	RTT("RTT"),
	BUFFER("Buffer"),
	LATENCY("Latency"),
	OCCURRED("Occurred");

	private final String name;

	/**
	 * OutputMonitoringMetric instantiation
	 *
	 * @param name {@code {@link #name}}
	 */
	OutputMonitoringMetric(String name) {
		this.name = name;
	}

	/**
	 * Retrieves {@code {@link #name}}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}
}