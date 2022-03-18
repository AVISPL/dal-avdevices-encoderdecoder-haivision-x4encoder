/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common;

/**
 * HaivisionConstant class provides the constant during the monitoring and controlling process
 *
 * @author Ivan / Symphony Dev Team<br>
 * Created on 3/8/2022
 * @since 1.0.0
 */
public class HaivisionConstant {

	public static final String STATISTICS = "Statistics";
	public static final String USER_NAME = "username";
	public static final String PASSWORD = "password";
	public static final String SET_COOKIE = "Set-Cookie";
	public static final String COLON_SLASH = "://";
	public static final String COLON = ":";
	public static final String FORMAT = "%s#%s";
	public static final String SPACE = " ";
	public static final String NONE = "None";
	public static final String AUTHORIZED = "Authorized";
	public static final String DAY = " day(s) ";
	public static final String HOUR = " hour(s) ";
	public static final String MINUTE = " minute(s) ";
	public static final String SECOND = " second(s)";
	public static final String DASH = "-";
	public static final String COMMA = ",";
	public static final String HASH = "#";
	public static final String COOKIE = "Cookie";
	public static final String SYSTEM_INFO_STATUS = "System Info Status";
	public static final String OPERATOR = "Operator";
	public static final String ADMIN = "Administrator";
	public static final String INFO = "info";
	public static final String ROLE = "role";
	public static final String APPLY = "Apply";
	public static final String[] WORKING_AUDIO_VIDEO = { "Stop", "None" };
	public static final String[] NOT_WORKING_AUDIO_VIDEO = { "Start", "None" };
	public static final String[] WORKING_STREAM = { "None", "Stop", "Delete" };
	public static final String[] NOT_WORKING_STREAM = { "None", "Start", "Delete" };
	public static final String INPUT_AUTO = "Input/Auto";
	public static final String START = "Start";
	public static final String STOP = "Stop";
	public static final String DELETE = "Delete";
	public static final String SLASH = "/";
	public static final String NO_SET_ERROR_AUDIO = "Can't set configure audio";
	public static final String NO_SET_ERROR_VIDEO = "Can't set configure video";
	public static final String NO_SET_ERROR_STREAM = "Can't set configure output stream";
	public static final String NO_SET_ERROR_CREATE_STREAM = "Can't create output stream";
	public static final String ERR_SET_CONTROL = "Error when controlling: ";
	public static final String NO_CHANGE_ACTION_AUDIO_ERROR = "Can't set state for the audio metric";
	public static final String NO_CHANGE_ACTION_VIDEO_ERROR = "Can't set state for the video metric";
	public static final String NO_CHANGE_ACTION_STREAM_ERROR = "Can't set state for the output stream metric";
	public static final String AUDIO = "Audio";
	public static final String VIDEO = "Video";
	public static final String STREAM = "Stream";
	public static final String CANCEL = "Cancel";
	public static final String EDITED = "Edited";
	public static final String TRUE = "True";
	public static final String FALSE = "False";
	public static final int MIN_OF_VIDEO_BITRATE = 32;
	public static final int MAX_OF_VIDEO_BITRATE = 120000;
	public static final int MIN_OF_VIDEO_GOP_SIZE = 1;
	public static final int MAX_OF_VIDEO_GOP_SIZE = 1000;
	public static final String DISABLE = "Disable";
	public static final String ENABLE = "Enable";
	public static final String AUTOMATIC = "Automatic";
	public static final String NO_INPUT = "No Input";
	public static final String ZERO = "0";
	public static final String EMPTY_STRING = "";
	public static final String NUMBER_ONE = "1";
	public static final String STREAM_CREATE_OUTPUT = "Stream Create Output";
	public static final String CREATE = "Create";
	public static final String PLUS = "+";
	public static final String DEFAULT_MTU = "1496";
	public static final String DEFAULT_TTL = "64";
	public static final String DEFAULT_TOS = "0x80";
	public static final String ONE = "1";
	public static final int MAX_SOURCE_AUDIO_DROPDOWN = 8;
	public static final String DEFAULT_LATENCY = "250";
	public static final String DEFAULT_BANDWIDTH_SRT = "25";
	public static final String DEFAULT_BANDWIDTH_UDP_RTP = "15";
	public static final int MAX_OF_LATENCY = 8000;
	public static final int MIN_OF_LATENCY = 20;
	public static final int MAX_OF_BANDWIDTH_OVERHEAD = 50;
	public static final int MIN_OF_BANDWIDTH_OVERHEAD = 5;
	public static final int MAX_OF_MTU = 1500;
	public static final int MIN_OF_MTU = 228;
	public static final int MAX_OF_TTL = 255;
	public static final int MIN_OF_TTL = 1;
	public static final int MAX_OF_PASSPHRASE_LENGTH = 79;
	public static final int MIN_OF_PASSPHRASE_LENGTH = 10;
	public static final String SOURCE_AUDIO_0 = "SourceAudio 0";
	public static final int SOURCE_PORT_MIN = 1025;
	public static final int SOURCE_PORT_MAX = 65535;
	public static final int DEFAULT_PORT_SAP = 9875;
	public static final String MAX_OF_TOS = "FF";
	public static final String MIN_OF_TOS = "00";
	public static final String AUDIO_ENCODER = "Audio Encoder ";
	public static final int MIN_ADD_SOURCE_AUDIO = 7;

}
