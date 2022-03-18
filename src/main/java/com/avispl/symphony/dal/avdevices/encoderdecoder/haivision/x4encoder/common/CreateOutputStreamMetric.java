/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common;

/**
 * CreateOutputStream
 *
 * @author Ivan / Symphony Dev Team<br>
 * Created on 3/8/2022
 * @since 1.0.0
 */
public enum CreateOutputStreamMetric {

	STATE("State"),
	ACTION("Action"),
	CONTENT_NAME("ContentName"),
	DESTINATION_ADDRESS("DestinationAddress"),
	DESTINATION_PORT("DestinationPort"),
	PARAMETER_MTU("ParameterMTU"),
	PARAMETER_TIMING_AND_SHAPING("ParameterTimingAndShaping"),
	PARAMETER_TOS("ParameterToS"),
	PARAMETER_TTL("ParameterTTL"),
	SOURCE_ADD_AUDIO("SourceAddAudio"),
	SOURCE_AUDIO("SourceAudio"),
	SOURCE_VIDEO("SourceVideo"),
	TRANSMIT_SAP("TransmitSAP"),
	PARAMETER_BANDWIDTH_OVERHEAD("ParameterBandwidthOverhead"),
	SAP_NAME("SAPName"),
	SAP_KEYWORDS("SAPKeywords"),
	SAP_DESCRIPTION("SAPDescription"),
	SAP_AUTHOR("SAPAuthor"),
	SAP_COPYRIGHT("SAPCopyright"),
	SAP_ADDRESS("SAPAddress"),
	SAP_PORT("SAPPort"),
	AVERAGE_BANDWIDTH("AverageBandwidth (Kbps)"),
	CONNECTION_MODE("ConnectionMode"),
	CONNECTION_ADDRESS("ConnectionAddress"),
	CONNECTION_SOURCE_PORT("ConnectionSourcePort"),
	CONNECTION_DESTINATION_PORT("ConnectionDestinationPort"),
	CONNECTION_NETWORK_ADAPTIVE("ConnectionNetworkAdaptive"),
	CONNECTION_LATENCY("ConnectionLatency"),
	CONNECTION_ENCRYPTION("ConnectionEncryption"),
	CONNECTION_PASSPHRASE("ConnectionPassphrase"),
	CONNECTION_PORT("ConnectionPort"),
	CONNECTION_ALTERNATE_PORT("ConnectionAlternatePort"),
	CANCEL("Cancel"),
	APPLY_CHANGE("ApplyChange"),
	STREAMING_PROTOCOL("StreamingProtocol");

	private String name;

	CreateOutputStreamMetric(String name) {
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

	/**
	 * Sets {@code name}
	 *
	 * @param name the {@code java.lang.String} field
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param name {@code {@link #name}}
	 * @return name of metric
	 * @throws Exception if can not find the enum with name
	 */
	public static CreateOutputStreamMetric getByName(String name) {
		for (CreateOutputStreamMetric metric : CreateOutputStreamMetric.values()) {
			if (metric.getName().equals(name)) {
				return metric;
			}
		}
		throw new IllegalArgumentException("Can not find the enum with name: " + name);
	}
}