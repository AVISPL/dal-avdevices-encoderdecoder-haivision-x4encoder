/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common;

/**
 * HavisionControllingMetric class defined the enum for the monitoring process
 *
 * @author Ivan
 * @version 1.0
 * @since 1.0
 */
public enum HavisionControllingMetric {

	AUDIO_ENCODER("Audio Encoder"),
	VIDEO_ENCODER("Video Encoder"),
	OUTPUT_ENCODER("Output Encoder");

	// audio metric
	public static final String STATE = "State";
	public static final String INPUT = "Input";
	public static final String CHANGE_MODE = "Change Mode";
	public static final String BITE_RATE = "BitRate";
	public static final String SAMPLE_RATE = "Sample Rate";
	public static final String ALGORITHM = "ACC-LC Algorithm";
	public static final String LANGUAGE = "Language";

	//video metric
	public static final String INPUT_FORMAT = "Input Format";
	public static final String CODEC_ALGORIHM = "Codec Algorithm";
	public static final String ENCODING_PROFILE = "Encoding Profile";
	public static final String CHROMA_SUBSAMPLING = "Chroma SubSampling";
	public static final String RATE_CONTROL = "Rate Control";
	public static final String MAX_BITRATE = "Max BitRate";
	public static final String RESOLUTION = "Resolution";
	public static final String FRAME_RATE = "Frame Rate";
	public static final String GOP_SIZE = "GOP Size (1-10000)";
	public static final String FRAMING = "Framing";
	public static final String TIME_CODE_SOURCE = "TimeCode Source";
	public static final String ASPECT_RATIO = "Aspect Ratio";
	public static final String CLOSED_CAPTION = "Closed Caption";
	public static final String APPLY_CHANGE = "Apply Change";

	//output stream
	public static final String VIDEO = "Video";
	public static final String ADD_VIDEO = "AddVideo";
	public static final String AUDIO = "Audio";
	public static final String ADD_AUDIO = "AddAudio";
	public static final String METADATA = "MetaData";
	public static final String PROTOCOL = "Protocol";
	public static final String ADDRESS = "Address";
	public static final String PORT = "Port";
	public static final String TIMING_AND_SHAPING = "Timing And Shaping";
	public static final String MTU = "MTU [228-1500]";
	public static final String TTL = "TTL [1-255]";
	public static final String TOS = "TOS";
	public static final String STRANSMIT_SAP = "Stransmit SAP";
	public static final String CREATE_STREAM = "Create Stream";
	public static final String MODE = "Mode";
	public static final String SOURCE_PORT = "Source Port";
	public static final String DESTINATION_PORT = "Destination Port";
	public static final String NETWORK_ADAPTER = "Network Adapter";
	public static final String ENCRYPTION = "Encryption";

	private final String name;

	/**
	 * HavisionControllingMetric instantiation
	 *
	 * @param name {@code {@link #name}}
	 */
	HavisionControllingMetric(String name) {
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
