/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common;

/**
 * OutputStreamMonitoringMetric class defined the enum for the monitoring process
 *
 * @author Ivan / Symphony Dev Team<br>
 * Created on 3/8/2022
 * @since 1.0.0
 */
public enum OutputMonitoringMetric {

	STATE("State",false,false),
	UPTIME("Uptime",false,false),
	SOURCE_PORT("SourcePort",false,false),
	SENT_PACKETS("SentPackets",false,true),
	SENT_BYTES("SentBytes",false,true),
	BITRATE("BitRate (kbps)",true,false),
	RECONNECTIONS("Reconnections",false,false),
	RESENT_PACKETS("ResentPackets",false,false),
	RESENT_BYTES("ResentBytes",false,false),
	DROPPED_PACKETS("DroppedPackets",false,false),
	DROPPED_BYTES("DroppedBytes",false,false),
	MSS("MSS",false,false),
	MAX_BANDWIDTH("MaxBandwidth (kbps)",true,false),
	REMOTE_PORT("RemotePort",false,false),
	SOURCE_ADDRESS("SourceAddress",false,false),
	REMOTE_ADDRESS("RemoteAddress",false,false),
	PATH_MAX_BANDWIDTH("PathMaxBandwidth (kbps)",true,false),
	LOST_PACKETS("LostPackets",false,false),
	RECV_ACK("RecvACK",false,true),
	RECV_NAK("RecvNAK",false,false),
	RTT("RTT",false,false),
	BUFFER("Buffer",false,false),
	LATENCY("Latency",false,false),
	OCCURRED("Occurred",false,false	);

	private final String name;
	private boolean isNormalize;
	private boolean isReplaceComma;

	/**
	 * OutputMonitoringMetric instantiation
	 *
	 * @param name {@code {@link #name}}
	 */
	OutputMonitoringMetric(String name, boolean isNormalize, boolean isReplaceComma) {
		this.name = name;
		this.isNormalize = isNormalize;
		this.isReplaceComma = isReplaceComma;
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
	 * Retrieves {@code {@link #isNormalize}}
	 *
	 * @return value of {@link #isNormalize}
	 */
	public boolean isNormalize() {
		return isNormalize;
	}

	/**
	 * Retrieves {@code {@link #isReplaceComma}}
	 *
	 * @return value of {@link #isReplaceComma}
	 */
	public boolean isReplaceComma() {
		return isReplaceComma;
	}
}