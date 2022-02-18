/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common;

import com.fasterxml.jackson.annotation.JsonAlias;

/**
 * HaivisionMonitoringMetric class defined the enum for the monitoring process
 *
 * @author Ivan
 * @version 1.0
 * @since 1.0
 */
public enum HaivisionMonitoringMetric {

	AUDIO_ENCODER("Audio Encoder",true),
	VIDEO_ENCODER("Video Encoder",true),
	OUTPUT_ENCODER("Output Encoder",true),
	SYSTEM_INFO_STATUS("System Info Status",true),
	AUTHENTICATION("Authentication",false);

	public static final String STATISTICS = "Statistics";
	//audio statistics
	public static final String STATE = "State";
	public static final String ENCODER_PTS = "EncoderPTS";
	public static final String ENCODED_BYTES = "EncodedBytes";
	public static final String ENCODED_FRAMES = "EncodedFrames";
	public static final String STC_SOURCE_INTERFACE = "STCSourceInterface";
	public static final String ENCODER_ERRORS = "EncoderErrors";
	public static final String ENCODED_BITRATE = "EncodedBitrate";
	public static final String MAX_SAMPLE_VALUE = "MaxSampleValue";
	public static final String MAX_SAMPLE_VALUE_PERCENTAGE = "MaxSampleValuePercentage";

	//video statistics
	public static final String UPTIME = "Uptime";
	public static final String INPUT_PRESENT = "InputPresent";
	public static final String INPUT_FORMAT = "InputFormat";
	public static final String INPUT_FORMAT_SHORT = "InputFormatShort";
	public static final String INPUT_FORMAT_U64 = "InputFormatU64";
	public static final String INPUT_FORMAT_WITHOUT_FRAMERATE_U64 = "InputFormatWithoutFramerateU64";
	public static final String INPUT_FORMAT_IS_DETAILED = "InputFormatIsDetailed";
	public static final String INPUT_FORMAT_WIDTH = "InputFormatWidth";
	public static final String INPUT_FORMAT_HEIGHT = "InputFormatHeight";
	public static final String INPUT_FORMAT_FRAMERATE = "InputFormatFrameRate";
	public static final String INPUT_FORMAT_IS_INTERLACED = "InputFormatIsInterlaced";
	public static final String INPUT_FORMAT_IS_PROGRESSIVE = "InputFormatIsProgressive";
	public static final String RESOLUTION = "Resolution";
	public static final String RESOLUTION_IS_DETAILED = "ResolutionIsDetailed";
	public static final String RESOLUTION_WIDTH = "ResolutionWidth";
	public static final String RESOLUTION_HEIGHT = "ResolutionHeight";
	public static final String RESOLUTION_FRAMERATE = "ResolutionFrameRate";
	public static final String RESOLUTION_IS_INTERLACED = "ResolutionIsInterlaced";
	public static final String RESOLUTION_IS_PROGRESSIVE = "ResolutionIsProgressive";
	public static final String ASPECT_RATIO = "AspectRatio";
	public static final String ENCODED_FRAMES_VIDEO = "EncodedFrames";
	public static final String ENCODED_BYTES_VIDEO = "EncodedBytes";
	public static final String ENCODED_FRAMERATE = "EncodedFrameRate";
	public static final String DROPPED_FRAMERATE = "DroppedFrames";
	public static final String ENCODER_RESETS = "EncoderResets";
	public static final String ENCODED_BITRATE_VIDEO = "EncodedBitRate";
	public static final String ENCODER_LOAD = "EncoderLoad";
	public static final String CLOSED_CAPTIONING = "ClosedCaptioning";
	public static final String CC_ERRORS = "CCErrors";
	public static final String EXTRACTED_CSD_BYTES = "ExtractedCSDBytes";
	public static final String INPUT_COLOUR_PRIMARIES = "InputColourPrimaries";
	public static final String INPUT_TRANSFER_CHARACTERISTICS = "InputTransferCharacteristics";
	public static final String INPUT_MATRIX_COEFFICIENTS = "InputMatrixCoefficients";

	//output stream statistics
	public static final String SOURCE_PORT = "SourcePort";
	public static final String SENT_PACKETS = "SentPackets";
	public static final String SENT_BYTES = "SentBytes";
	public static final String BITRATE = "Bitrate";
	public static final String RECONNECTIONS = "Reconnections";
	public static final String RESENT_PACKETS = "ResentPackets";
	public static final String RESENT_BYTES = "ResentBytes";
	public static final String DROPPED_PACKETS = "DroppedPackets";
	public static final String DROPPED_BYTES = "DroppedBytes";
	public static final String MSS = "MSS";
	public static final String MAX_BANDWIDTH = "MaxBandwidth";
	public static final String REMOTE_PORT = "RemotePort";
	public static final String SOURCE_ADDRESS = "SourceAddress";
	public static final String REMOTE_ADDRESS = "RemoteAddress";
	public static final String PATH_MAX_BANDWIDTH = "PathMaxBandwidth";
	public static final String LOST_PACKETS = "LostPackets";
	public static final String RECV_ACK = "RecvACK";
	public static final String RECV_NAK = "RecvNAK";
	public static final String RTT = "RTT";
	public static final String BUFFER = "Buffer";
	public static final String LATENCY = "Latency";
	public static final String OCCURRED = "Occurred";

	//Info System
	public static final String CARD_STATUS ="cardStatus";
	public static final String SERIAL_NUMBER ="serialNumber";
	public static final String HARDWARE_COMPATIBILITY ="hardwareCompatibility";
	public static final String MEZZANINE_PRESENT ="mezzaninePresent";
	public static final String HARDWARE_REVISION ="hardwareRevision";
	public static final String CPL_REVISION ="cpldRevision";
	public static final String BOOT_VERSION ="bootVersion";
	public static final String CARD_TYPE ="cardType";
	public static final String PART_NUMBER ="partNumber";
	public static final String FIRMWARE_DATE ="firmwareDate";
	public static final String FIRMWARE_VERSION ="firmwareVersion";
	public static final String FIRMWARE_OPTIONS ="firmwareOptions";
	public static final String CHIPSET_LOAD ="chipsetLoad";
	public static final String TEMPERTURE ="temperature";

	private final String name;
	private boolean isMonitor;

	/**
	 * MakitoMonitoringMetric instantiation
	 *
	 * @param name {@code {@link #name}}
	 */
	HaivisionMonitoringMetric(String name, boolean isMonitor) {
		this.name = name;
		this.isMonitor = isMonitor;
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
	 * Retrieves {@code {@link #isMonitor}}
	 *
	 * @return value of {@link #isMonitor}
	 */
	public boolean isMonitor() {
		return isMonitor;
	}
}