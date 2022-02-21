/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import com.avispl.symphony.api.dal.control.Controller;
import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;
import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.api.dal.monitor.Monitorable;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.HaivisionConstant;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.HaivisionMonitoringMetric;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.HaivisionStatisticsUtil;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.AudioStateDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.OutputStateDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.VideoStateDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.AudioResponse;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.OutputResponse;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.SystemInfoResponse;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.VideoResponse;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.audio.Audio;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.audio.AudioStatistic;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.output.OutputStatistic;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.video.Video;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.video.VideoStatistic;
import com.avispl.symphony.dal.communicator.RestCommunicator;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * An implementation of RestCommunicator to provide communication and interaction with Haivision Makito X4 Encoders
 * Supported features are:
 * <p>
 * Monitoring:
 * <p>
 * Controlling:
 *
 * @author Ivan
 * @since 1.0.0
 */
public class HaivisionX4EncoderCommunicator extends RestCommunicator implements Monitorable, Controller {

	private String sessionID;
	private boolean isAdapterFilter;
	private Integer countMonitoringNumber = null;
	private ExtendedStatistics localExtendedStatistics;
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final Map<String, String> failedMonitor = new HashMap<>();
	private final Map<String, String> errorFilter = new HashMap<>();

	private final String uuidDay = UUID.randomUUID().toString().replace(HaivisionConstant.DASH, "");
	private final String uuidSeconds = UUID.randomUUID().toString().replace(HaivisionConstant.DASH, "");

	//The properties adapter
	private String streamNameFilter;
	private String portNumberFilter;
	private String streamStatusFilter;

	private final List<String> streamNameList = new ArrayList<>();
	private final List<Integer> portNumberList = new ArrayList<>();
	private final List<String> portNumberRangeList = new ArrayList<>();
	private final List<String> streamStatusList = new ArrayList<>();

	/**
	 * List of audio statistics filter
	 */
	private final List<AudioResponse> audioStatisticsList = new ArrayList<>();

	/**
	 * List of video statistics filter
	 */
	private final List<VideoResponse> videoStatisticsList = new ArrayList<>();

	/**
	 * List of output statistics if the adapter not filter
	 */
	private List<OutputResponse> outputStatisticsList = new ArrayList<>();

	/**
	 * List of output statistics for the adapter filter
	 */
	private List<OutputResponse> outputForPortAndStatusList = new ArrayList<>();

	/**
	 * List of audio Response
	 */
	private List<AudioResponse> audioResponseList = Collections.synchronizedList(new ArrayList<>());

	/**
	 * List of audio Response
	 */
	private List<VideoResponse> videoResponseList = Collections.synchronizedList(new ArrayList<>());

	/**
	 * List of audio Response
	 */
	private List<OutputResponse> outputResponseList = Collections.synchronizedList(new ArrayList<>());

	/**
	 * Retrieves {@code {@link #streamNameFilter}}
	 *
	 * @return value of {@link #streamNameFilter}
	 */
	public String getStreamNameFilter() {
		return streamNameFilter;
	}

	/**
	 * Sets {@code streamNameFilter}
	 *
	 * @param streamNameFilter the {@code java.lang.String} field
	 */
	public void setStreamNameFilter(String streamNameFilter) {
		this.streamNameFilter = streamNameFilter;
	}

	/**
	 * Retrieves {@code {@link #portNumberFilter}}
	 *
	 * @return value of {@link #portNumberFilter}
	 */
	public String getPortNumberFilter() {
		return portNumberFilter;
	}

	/**
	 * Sets {@code portNumberFilter}
	 *
	 * @param portNumberFilter the {@code java.lang.String} field
	 */
	public void setPortNumberFilter(String portNumberFilter) {
		this.portNumberFilter = portNumberFilter;
	}

	/**
	 * Retrieves {@code {@link #streamStatusFilter}}
	 *
	 * @return value of {@link #streamStatusFilter}
	 */
	public String getStreamStatusFilter() {
		return streamStatusFilter;
	}

	/**
	 * Sets {@code streamStatusFilter}
	 *
	 * @param streamStatusFilter the {@code java.lang.String} field
	 */
	public void setStreamStatusFilter(String streamStatusFilter) {
		this.streamStatusFilter = streamStatusFilter;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This method is called by Symphony to get the list of statistics to be displayed
	 *
	 * @return List<Statistics> This returns the list of statistics
	 */
	@Override
	public List<Statistics> getMultipleStatistics() throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Getting statistics from the device X4 at host %s with port %s", this.host, this.getPort()));
		}

		ExtendedStatistics extendedStatistics = new ExtendedStatistics();
		Map<String, String> stats = new HashMap<>();
		List<AdvancedControllableProperty> advancedControllableProperties = new ArrayList<>();

		if (sessionID == null) {
			sessionID = retrieveSessionID();
		}

		if (localExtendedStatistics == null) {
			localExtendedStatistics = new ExtendedStatistics();
		}

		isAdapterFiltering();
		populateInformationFromDevice(stats);

		if (!errorFilter.isEmpty()) {
			for (Map.Entry<String, String> errorMessage : errorFilter.entrySet()) {
				stats.put(errorMessage.getKey(), errorMessage.getValue());
			}
		}
		extendedStatistics.setStatistics(stats);
		extendedStatistics.setControllableProperties(advancedControllableProperties);

		localExtendedStatistics = extendedStatistics;
		return Collections.singletonList(localExtendedStatistics);
	}

	@Override
	public void controlProperty(ControllableProperty controllableProperty) throws Exception {
		String property = controllableProperty.getProperty();
		String value = String.valueOf(controllableProperty.getValue());
		if (logger.isDebugEnabled()) {
			logger.debug("controlProperty value" + value);
			logger.debug("controlProperty value" + property);
		}
	}

	@Override
	public void controlProperties(List<ControllableProperty> list) throws Exception {
		//TODO
	}

	@Override
	protected void authenticate() throws Exception {
		//TODO
	}

	/**
	 * @return HttpHeaders contain cookie for authorization
	 */
	@Override
	protected HttpHeaders putExtraRequestHeaders(HttpMethod httpMethod, String uri, HttpHeaders headers) throws Exception {
		headers.set("Content-Type", "text/xml");
		headers.set("Content-Type", "application/json");

		if (sessionID != null && !sessionID.equals(HaivisionConstant.AUTHORIZED)) {
			headers.set("Cookie", sessionID);
		}
		return super.putExtraRequestHeaders(httpMethod, uri, headers);
	}

	/**
	 * Retrieve SessionID from the Device
	 *
	 * @return sessionId of the device
	 * @throws Exception if get sessionID failed
	 */
	private String retrieveSessionID() throws Exception {
		HttpClient client = this.obtainHttpClient(true);

		StringBuilder stringBuilder = new StringBuilder();
		HttpPost httpPost = new HttpPost(this.buildPathToLogin());

		ObjectNode request = JsonNodeFactory.instance.objectNode();
		request.put(HaivisionConstant.USER_NAME, getLogin());
		request.put(HaivisionConstant.PASS_WORD, getPassword());

		StringEntity entity = new StringEntity(request.toString());
		httpPost.setEntity(entity);
		HttpResponse response = null;
		try {
			response = client.execute(httpPost);
			Header headers = response.getFirstHeader(HaivisionConstant.SET_COOKIE);
			if (headers == null) {
				throw new ResourceNotReachableException("Login to the device failed,user unauthorized");
			}
			stringBuilder.append(headers.getValue());
			return stringBuilder.toString();
		} catch (Exception e) {
			throw new ResourceNotReachableException(e.getMessage());
		}
	}

	/**
	 * Build full path to login for the device
	 *
	 * @return String full path of the device
	 */
	private String buildPathToLogin() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(this.getProtocol()).append(HaivisionConstant.COLON_SLASH);
		stringBuilder.append(getHost());
		stringBuilder.append(HaivisionConstant.COLON);
		stringBuilder.append(getPort());
		stringBuilder.append(HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.AUTHENTICATION));

		return stringBuilder.toString();
	}

	/**
	 * Retrieve data and add to stats
	 */
	private void populateInformationFromDevice(Map<String, String> stats) {
		for (HaivisionMonitoringMetric makitoMonitoringMetric : HaivisionMonitoringMetric.values()) {
			if (HaivisionMonitoringMetric.AUTHENTICATION.equals(makitoMonitoringMetric)) {
				continue;
			}
			retrieveDataByMetric(stats, makitoMonitoringMetric);
		}
		if (countMonitoringNumber == null) {
			countMonitoringNumber = getNumberMonitoringMetric();
		}
		if (failedMonitor.size() == countMonitoringNumber) {
			StringBuilder stringBuilder = new StringBuilder();
			for (Map.Entry<String, String> messageFailed : failedMonitor.entrySet()) {
				stringBuilder.append(messageFailed.getValue());
			}
			throw new ResourceNotReachableException("Get monitoring data failed: " + stringBuilder);
		}
		getFilteredForEncoderStatistics();
		for (HaivisionMonitoringMetric makitoMonitoringMetric : HaivisionMonitoringMetric.values()) {
			if (HaivisionMonitoringMetric.AUDIO_ENCODER.equals(makitoMonitoringMetric)) {
				populateAudioData(stats);
			}
			if (HaivisionMonitoringMetric.VIDEO_ENCODER.equals(makitoMonitoringMetric)) {
				populateVideoData(stats);
			}
			if (HaivisionMonitoringMetric.OUTPUT_ENCODER.equals(makitoMonitoringMetric)) {
				populateOutputData(stats);
			}
		}
	}

	/**
	 * Retrieve data from the device
	 *
	 * @param metric list metric of device
	 */
	private void retrieveDataByMetric(Map<String, String> stats, HaivisionMonitoringMetric metric) {
		Objects.requireNonNull(metric);
		switch (metric) {
			case AUDIO_ENCODER:
				retrieveAudioEncoder();
				break;
			case VIDEO_ENCODER:
				retrieveVideoEncoder();
				break;
			case OUTPUT_ENCODER:
				retrieveOutputEncoder();
				break;
			case SYSTEM_INFO_STATUS:
				retrieveSystemInfoStatus(stats);
				break;
			default:
				throw new IllegalStateException("Error the metric monitoring");
		}
	}

	/**
	 * Add audio data to statistics property
	 *
	 * @param stats list statistics property
	 */
	private void populateAudioData(Map<String, String> stats) {
		if (!audioStatisticsList.isEmpty()) {
			for (AudioResponse audioResponses : audioStatisticsList) {
				addAudioDataStatisticsToStatisticsProperty(stats, audioResponses);
			}
		} else if (!audioResponseList.isEmpty() && !isAdapterFilter) {
			for (AudioResponse audioResponses : audioResponseList) {
				addAudioDataStatisticsToStatisticsProperty(stats, audioResponses);
			}
		}
	}

	/**
	 * Add audio data statistics to statistics property
	 *
	 * @param stats list statistics property
	 * @param audioResponseList list of audio response
	 */
	private void addAudioDataStatisticsToStatisticsProperty(Map<String, String> stats, AudioResponse audioResponseList) {
		String audioName = audioResponseList.getName();
		AudioStatistic audioStatistic = audioResponseList.getAudioStatistic();

		Map<Integer, String> audioMap = AudioStateDropdown.getNameToValueMap();
		String state = audioResponseList.getState();
		if (HaivisionConstant.NONE.equals(state)) {
			stats.put(String.format(HaivisionConstant.FORMAT, audioName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.STATE), state);
		} else {
			stats.put(String.format(HaivisionConstant.FORMAT, audioName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.STATE),
					String.valueOf(audioMap.get(Integer.valueOf(state))));
		}
		stats.put(String.format(HaivisionConstant.FORMAT, audioName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.ENCODER_PTS),
				String.valueOf(audioStatistic.getEncoderPTS()));
		stats.put(String.format(HaivisionConstant.FORMAT, audioName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.ENCODED_BYTES),
				String.valueOf(audioStatistic.getEncodedBytes()));
		stats.put(String.format(HaivisionConstant.FORMAT, audioName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.ENCODER_ERRORS),
				String.valueOf(audioStatistic.getEncoderErrors()));
		stats.put(String.format(HaivisionConstant.FORMAT, audioName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.ENCODED_FRAMES),
				String.valueOf(audioStatistic.getEncodedFrames()));
		stats.put(String.format(HaivisionConstant.FORMAT, audioName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.STC_SOURCE_INTERFACE),
				String.valueOf(audioStatistic.getsTCSourceInterface()));
		stats.put(String.format(HaivisionConstant.FORMAT, audioName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.ENCODED_BITRATE),
				String.valueOf(audioStatistic.getEncodedBitrate()));
		stats.put(String.format(HaivisionConstant.FORMAT, audioName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.MAX_SAMPLE_VALUE),
				String.valueOf(audioStatistic.getMaxSampleValue()));
		stats.put(String.format(HaivisionConstant.FORMAT, audioName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.MAX_SAMPLE_VALUE_PERCENTAGE),
				String.valueOf(audioStatistic.getMaxSampleValuePercentage()));
	}

	/**
	 * Add video data to statistics property
	 *
	 * @param stats list statistics property
	 */
	private void populateVideoData(Map<String, String> stats) {
		if (!videoStatisticsList.isEmpty()) {
			for (VideoResponse videoResponses : videoStatisticsList) {
				addVideoDataStatisticsToStatisticsProperty(stats, videoResponses);
			}
		} else if (!videoResponseList.isEmpty() && !isAdapterFilter) {
			for (VideoResponse videoResponses : videoResponseList) {
				addVideoDataStatisticsToStatisticsProperty(stats, videoResponses);
			}
		}
	}

	/**
	 * Add video data control to statistics property
	 *
	 * @param stats list statistics property
	 * @param videoResponseList list of video response
	 */
	private void addVideoDataStatisticsToStatisticsProperty(Map<String, String> stats, VideoResponse videoResponseList) {
		String videoName = videoResponseList.getName();
		VideoStatistic videoStatistic = videoResponseList.getVideoStatistic();
		Map<Integer, String> videoMap = VideoStateDropdown.getNameToValueMap();
		String state = videoResponseList.getState();
		if (HaivisionConstant.NONE.equals(state)) {
			stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.STATE), HaivisionConstant.NONE);
		} else {
			stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.STATE),
					String.valueOf(videoMap.get(Integer.valueOf(state))));
		}
		stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.UPTIME),
				String.valueOf(formatTimeData(videoStatistic.getUptime())));
		stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.INPUT_PRESENT),
				String.valueOf(videoStatistic.getInputPresent()));
		stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.INPUT_FORMAT),
				String.valueOf(videoStatistic.getInputFormat()));
		stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.INPUT_FORMAT_SHORT),
				String.valueOf(videoStatistic.getInputFormatShort()));
		stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.INPUT_FORMAT_U64),
				String.valueOf(videoStatistic.getInputFormatU64()));
		stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.INPUT_FORMAT_WITHOUT_FRAMERATE_U64),
				String.valueOf(videoStatistic.getInputFormatWithoutFramerateU64()));
		stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.INPUT_FORMAT_IS_DETAILED),
				String.valueOf(videoStatistic.getInputFormatIsDetailed()));
		stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.INPUT_COLOUR_PRIMARIES),
				String.valueOf(videoStatistic.getInputColorPrimaries()));
		stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.INPUT_FORMAT_WIDTH),
				String.valueOf(videoStatistic.getInputFormatWidth()));
		stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.INPUT_FORMAT_HEIGHT),
				String.valueOf(videoStatistic.getInputFormatHeight()));
		stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.INPUT_FORMAT_FRAMERATE),
				String.valueOf(videoStatistic.getInputFormatFrameRate()));
		stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.INPUT_FORMAT_IS_INTERLACED),
				String.valueOf(videoStatistic.getInputFormatIsInterlaced()));
		stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.INPUT_FORMAT_IS_PROGRESSIVE),
				String.valueOf(videoStatistic.getInputFormatIsProgressive()));
		stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.RESOLUTION),
				String.valueOf(videoStatistic.getResolution()));
		stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.RESOLUTION_IS_DETAILED),
				String.valueOf(videoStatistic.getResolutionIsDetailed()));
		stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.RESOLUTION_WIDTH),
				String.valueOf(videoStatistic.getResolutionWidth()));
		stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.RESOLUTION_HEIGHT),
				String.valueOf(videoStatistic.getResolutionHeight()));
		stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.RESOLUTION_FRAMERATE),
				String.valueOf(videoStatistic.getResolutionFrameRate()));
		stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.RESOLUTION_IS_INTERLACED),
				String.valueOf(videoStatistic.getResolutionIsInterlaced()));
		stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.RESOLUTION_IS_PROGRESSIVE),
				String.valueOf(videoStatistic.getResolutionIsProgressive()));
		stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.ASPECT_RATIO),
				String.valueOf(videoStatistic.getAspectRatio()));
		stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.ENCODED_FRAMES_VIDEO),
				String.valueOf(videoStatistic.getEncodedFrames()));
		stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.ENCODED_BYTES_VIDEO),
				String.valueOf(videoStatistic.getEncodedBytes()));
		stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.ENCODED_FRAMERATE),
				String.valueOf(videoStatistic.getEncodedFrameRate()));
		stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.DROPPED_FRAMERATE),
				String.valueOf(videoStatistic.getDroppedFrames()));
		stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.ENCODER_RESETS),
				String.valueOf(videoStatistic.getEncoderResets()));
		stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.ENCODED_BITRATE_VIDEO),
				String.valueOf(videoStatistic.getEncodedBitRate()));
		stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.ENCODER_LOAD),
				String.valueOf(videoStatistic.getEncoderLoad()));
		stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.CLOSED_CAPTIONING),
				String.valueOf(videoStatistic.getClosedCaptioning()));
		stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.CC_ERRORS),
				String.valueOf(videoStatistic.getCCErrors()));
		stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.EXTRACTED_CSD_BYTES),
				String.valueOf(videoStatistic.getExtractedCSDBytes()));
		stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.INPUT_TRANSFER_CHARACTERISTICS),
				String.valueOf(videoStatistic.getInputTransferCharacteristics()));
		stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.INPUT_MATRIX_COEFFICIENTS),
				String.valueOf(videoStatistic.getInputMatrixCoefficients()));
		stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.OCCURRED),
				String.valueOf(formatTimeData(videoStatistic.getOccurred())));
	}

	/**
	 * Add output data to statistics property
	 *
	 * @param stats list statistics property
	 */
	private void populateOutputData(Map<String, String> stats) {
		if (isAdapterFilter) {
			if (!outputForPortAndStatusList.isEmpty()) {
				for (OutputResponse outputResponses : outputForPortAndStatusList) {
					addOutputStreamDataStatisticsToStatisticsProperty(stats, outputResponses);
				}
			} else {
				for (OutputResponse outputResponses : outputStatisticsList) {
					addOutputStreamDataStatisticsToStatisticsProperty(stats, outputResponses);
				}
			}
		} else {
			for (OutputResponse outputResponses : outputResponseList) {
				addOutputStreamDataStatisticsToStatisticsProperty(stats, outputResponses);
			}
		}
	}

	/**
	 * Add output stream data to statistics property
	 *
	 * @param stats list statistics property
	 * @param outputResponseList list of output response
	 */
	private void addOutputStreamDataStatisticsToStatisticsProperty(Map<String, String> stats, OutputResponse outputResponseList) {
		String name = convertStreamNameUnescapeHtml3(outputResponseList.getName());
		OutputStatistic outputStatistic = outputResponseList.getOutputStatistic();
		Map<Integer, String> outputMap = OutputStateDropdown.getNameToValueMap();
		String state = outputResponseList.getState();
		if (HaivisionConstant.NONE.equals(state)) {
			stats.put(String.format(HaivisionConstant.FORMAT, name + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.STATE), state);
		} else {
			stats.put(String.format(HaivisionConstant.FORMAT, name + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.STATE),
					String.valueOf(outputMap.get(Integer.valueOf(state))));
		}
		stats.put(String.format(HaivisionConstant.FORMAT, name + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.UPTIME),
				String.valueOf(formatTimeData(outputStatistic.getUptime())));
		stats.put(String.format(HaivisionConstant.FORMAT, name + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.SOURCE_PORT),
				String.valueOf(outputStatistic.getSourcePort()));
		stats.put(String.format(HaivisionConstant.FORMAT, name + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.SENT_PACKETS),
				String.valueOf(outputStatistic.getSentPackets()));
		stats.put(String.format(HaivisionConstant.FORMAT, name + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.SENT_BYTES),
				String.valueOf(outputStatistic.getSentBytes()));
		stats.put(String.format(HaivisionConstant.FORMAT, name + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.BITRATE),
				String.valueOf(outputStatistic.getBitrate()));
		stats.put(String.format(HaivisionConstant.FORMAT, name + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.RECONNECTIONS),
				String.valueOf(outputStatistic.getReconnections()));
		stats.put(String.format(HaivisionConstant.FORMAT, name + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.RESENT_PACKETS),
				String.valueOf(outputStatistic.getResentPackets()));
		stats.put(String.format(HaivisionConstant.FORMAT, name + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.RESENT_BYTES),
				String.valueOf(outputStatistic.getResentBytes()));
		stats.put(String.format(HaivisionConstant.FORMAT, name + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.DROPPED_PACKETS),
				String.valueOf(outputStatistic.getDroppedPackets()));
		stats.put(String.format(HaivisionConstant.FORMAT, name + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.DROPPED_BYTES),
				String.valueOf(outputStatistic.getDroppedBytes()));
		stats.put(String.format(HaivisionConstant.FORMAT, name + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.MSS), String.valueOf(outputStatistic.getMss()));
		stats.put(String.format(HaivisionConstant.FORMAT, name + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.MAX_BANDWIDTH),
				String.valueOf(outputStatistic.getMaxBandwidth()));
		stats.put(String.format(HaivisionConstant.FORMAT, name + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.REMOTE_PORT),
				String.valueOf(outputStatistic.getRemotePort()));
		stats.put(String.format(HaivisionConstant.FORMAT, name + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.SOURCE_ADDRESS),
				String.valueOf(outputStatistic.getSourceAddress()));
		stats.put(String.format(HaivisionConstant.FORMAT, name + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.REMOTE_ADDRESS),
				String.valueOf(outputStatistic.getRemoteAddress()));
		stats.put(String.format(HaivisionConstant.FORMAT, name + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.PATH_MAX_BANDWIDTH),
				String.valueOf(outputStatistic.getPathMaxBandwidth()));
		stats.put(String.format(HaivisionConstant.FORMAT, name + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.LOST_PACKETS),
				String.valueOf(outputStatistic.getLostPackets()));
		stats.put(String.format(HaivisionConstant.FORMAT, name + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.RECV_ACK),
				String.valueOf(outputStatistic.getRecvACK()));
		stats.put(String.format(HaivisionConstant.FORMAT, name + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.RECV_NAK),
				String.valueOf(outputStatistic.getRecvNAK()));
		stats.put(String.format(HaivisionConstant.FORMAT, name + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.RTT),
				String.valueOf(outputStatistic.getRtt()));
		stats.put(String.format(HaivisionConstant.FORMAT, name + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.BUFFER),
				String.valueOf(outputStatistic.getBuffer()));
		stats.put(String.format(HaivisionConstant.FORMAT, name + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.LATENCY),
				String.valueOf(outputStatistic.getLatency()));
		stats.put(String.format(HaivisionConstant.FORMAT, name + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.OCCURRED),
				String.valueOf(formatTimeData(outputStatistic.getOccurred())));
	}

	/**
	 * Retrieve audio encoder
	 */
	private void retrieveAudioEncoder() {
		try {
			String responseData = doGet(HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.AUDIO_ENCODER));
			JsonNode audio = objectMapper.readTree(responseData).get(HaivisionConstant.DATA);
			for (int i = 0; i < audio.size(); i++) {
				AudioResponse audioItem = objectMapper.treeToValue(audio.get(i), AudioResponse.class);
				audioResponseList.add(audioItem);
			}
		} catch (Exception e) {
			failedMonitor.put(HaivisionMonitoringMetric.AUDIO_ENCODER.getName(), e.getMessage());
		}
	}

	/**
	 * Retrieve video encoder
	 */
	private void retrieveVideoEncoder() {
		try {
			String responseData = doGet(HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.VIDEO_ENCODER));
			JsonNode video = objectMapper.readTree(responseData).get(HaivisionConstant.DATA);
			for (int i = 0; i < video.size(); i++) {
				VideoResponse videoItem = objectMapper.treeToValue(video.get(i), VideoResponse.class);
				videoResponseList.add(videoItem);
			}
		} catch (Exception e) {
			failedMonitor.put(HaivisionMonitoringMetric.VIDEO_ENCODER.getName(), e.getMessage());
		}
	}

	/**
	 * Retrieve output stream encoder
	 */
	private void retrieveOutputEncoder() {
		try {
			String responseData = doGet(HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.OUTPUT_ENCODER));
			JsonNode output = objectMapper.readTree(responseData).get(HaivisionConstant.DATA);
			for (int i = 0; i < output.size(); i++) {
				OutputResponse outputItem = objectMapper.treeToValue(output.get(i), OutputResponse.class);
				outputResponseList.add(outputItem);
			}
		} catch (Exception e) {
			failedMonitor.put(HaivisionMonitoringMetric.OUTPUT_ENCODER.getName(), e.getMessage());
		}
	}

	/**
	 * Retrieve system information status encoder
	 */
	private void retrieveSystemInfoStatus(Map<String, String> stats) {
		try {
			SystemInfoResponse responseData = doGet(HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.SYSTEM_INFO_STATUS), SystemInfoResponse.class);
			if (responseData != null) {
				stats.put(HaivisionMonitoringMetric.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + HaivisionMonitoringMetric.CARD_STATUS, checkForNullData(responseData.getCardStatus()));
				stats.put(HaivisionMonitoringMetric.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + HaivisionMonitoringMetric.SERIAL_NUMBER, checkForNullData(responseData.getSerialNumber()));
				stats.put(HaivisionMonitoringMetric.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + HaivisionMonitoringMetric.HARDWARE_COMPATIBILITY,
						checkForNullData(responseData.getHardwareCompatibility()));
				stats.put(HaivisionMonitoringMetric.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + HaivisionMonitoringMetric.MEZZANINE_PRESENT, checkForNullData(responseData.getMezzaninePresent()));
				stats.put(HaivisionMonitoringMetric.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + HaivisionMonitoringMetric.HARDWARE_REVISION, checkForNullData(responseData.getHardwareRevision()));
				stats.put(HaivisionMonitoringMetric.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + HaivisionMonitoringMetric.CPL_REVISION, checkForNullData(responseData.getCpldRevision()));
				stats.put(HaivisionMonitoringMetric.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + HaivisionMonitoringMetric.BOOT_VERSION, checkForNullData(responseData.getBootVersion()));
				stats.put(HaivisionMonitoringMetric.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + HaivisionMonitoringMetric.CARD_TYPE, checkForNullData(responseData.getCardType()));
				stats.put(HaivisionMonitoringMetric.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + HaivisionMonitoringMetric.PART_NUMBER, checkForNullData(responseData.getPartNumber()));
				stats.put(HaivisionMonitoringMetric.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + HaivisionMonitoringMetric.FIRMWARE_DATE, checkForNullData(responseData.getFirmwareDate()));
				stats.put(HaivisionMonitoringMetric.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + HaivisionMonitoringMetric.FIRMWARE_VERSION, checkForNullData(responseData.getFirmwareVersion()));
				stats.put(HaivisionMonitoringMetric.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + HaivisionMonitoringMetric.FIRMWARE_OPTIONS, checkForNullData(responseData.getFirmwareOptions()));
				stats.put(HaivisionMonitoringMetric.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + HaivisionMonitoringMetric.UPTIME, checkForNullData(responseData.getUptime()));
				stats.put(HaivisionMonitoringMetric.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + HaivisionMonitoringMetric.CHIPSET_LOAD, checkForNullData(responseData.getChipsetLoad()));
				stats.put(HaivisionMonitoringMetric.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + HaivisionMonitoringMetric.TEMPERATURE, checkForNullData(responseData.getTemperature()));
			} else {
				contributeNoneValueForSystemInfo(stats);
			}
		} catch (Exception e) {
			contributeNoneValueForSystemInfo(stats);
			failedMonitor.put(HaivisionMonitoringMetric.SYSTEM_INFO_STATUS.getName(), e.getMessage());
		}
	}

	/**
	 * Value of list statistics property of system info is none
	 *
	 * @param stats list statistics
	 */
	private void contributeNoneValueForSystemInfo(Map<String, String> stats) {
		stats.put(HaivisionMonitoringMetric.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + HaivisionMonitoringMetric.CARD_STATUS, HaivisionConstant.NONE);
		stats.put(HaivisionMonitoringMetric.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + HaivisionMonitoringMetric.SERIAL_NUMBER, HaivisionConstant.NONE);
		stats.put(HaivisionMonitoringMetric.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + HaivisionMonitoringMetric.HARDWARE_COMPATIBILITY, HaivisionConstant.NONE);
		stats.put(HaivisionMonitoringMetric.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + HaivisionMonitoringMetric.MEZZANINE_PRESENT, HaivisionConstant.NONE);
		stats.put(HaivisionMonitoringMetric.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + HaivisionMonitoringMetric.HARDWARE_REVISION, HaivisionConstant.NONE);
		stats.put(HaivisionMonitoringMetric.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + HaivisionMonitoringMetric.CPL_REVISION, HaivisionConstant.NONE);
		stats.put(HaivisionMonitoringMetric.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + HaivisionMonitoringMetric.BOOT_VERSION, HaivisionConstant.NONE);
		stats.put(HaivisionMonitoringMetric.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + HaivisionMonitoringMetric.CARD_TYPE, HaivisionConstant.NONE);
		stats.put(HaivisionMonitoringMetric.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + HaivisionMonitoringMetric.PART_NUMBER, HaivisionConstant.NONE);
		stats.put(HaivisionMonitoringMetric.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + HaivisionMonitoringMetric.FIRMWARE_DATE, HaivisionConstant.NONE);
		stats.put(HaivisionMonitoringMetric.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + HaivisionMonitoringMetric.FIRMWARE_VERSION, HaivisionConstant.NONE);
		stats.put(HaivisionMonitoringMetric.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + HaivisionMonitoringMetric.FIRMWARE_OPTIONS, HaivisionConstant.NONE);
		stats.put(HaivisionMonitoringMetric.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + HaivisionMonitoringMetric.UPTIME, HaivisionConstant.NONE);
		stats.put(HaivisionMonitoringMetric.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + HaivisionMonitoringMetric.CHIPSET_LOAD, HaivisionConstant.NONE);
		stats.put(HaivisionMonitoringMetric.SYSTEM_INFO_STATUS.getName() + HaivisionConstant.HASH + HaivisionMonitoringMetric.TEMPERATURE, HaivisionConstant.NONE);
	}

	/**
	 * check for null data
	 *
	 * @param value the value of monitoring properties
	 * @return String (none/value)
	 */
	private String checkForNullData(String value) {
		if (StringUtils.isNullOrEmpty(value)) {
			return HaivisionConstant.NONE;
		}
		return value;
	}

	/**
	 * Convert Name use UnescapeHtml3
	 *
	 * @param name the name is String
	 * @return the name to be converted
	 */
	private String convertStreamNameUnescapeHtml3(String name) {
		try {
			HTMLDocument doc = new HTMLDocument();
			new HTMLEditorKit().read(new StringReader("<html><body>" + name), doc, 0);
			return doc.getText(1, doc.getLength() - 1);
		} catch (Exception ex) {
			return name;
		}
	}

	/**
	 * Format time data
	 *
	 * @param time the time is String
	 * @return String
	 */
	private String formatTimeData(String time) {
		if (HaivisionConstant.NONE.equals(time)) {
			return HaivisionConstant.NONE;
		}
		int index = time.indexOf(HaivisionConstant.SPACE);
		if (index > -1) {
			time = time.substring(0, index);
		}
		return time.replace("d", uuidDay).replace("s", uuidSeconds).replace(uuidDay, HaivisionConstant.DAY)
				.replace("h", HaivisionConstant.HOUR).replace("m", HaivisionConstant.MINUTE).replace(uuidSeconds, HaivisionConstant.SECOND);
	}

	/**
	 * Filter the list of aggregated devices based on filter option in Adapter Properties
	 */
	private void getFilteredForEncoderStatistics() {
		filterStreamName();
		filterPortNumber();
		filterStreamStatus();
		if (isAdapterFilter) {
			filterAudioAndVideoStatisticsList();
		}
	}

	/**
	 * Filter the name of Stream
	 */
	private void filterStreamName() {
		extractStreamNameList(this.streamNameFilter);
		if (!streamNameList.isEmpty()) {
			for (String streamName : streamNameList) {
				OutputResponse outputResponseFilter = null;
				for (OutputResponse outputResponse : outputResponseList) {
					if (streamName.equals(convertStreamNameUnescapeHtml3(outputResponse.getName()))) {
						outputResponseFilter = outputResponse;
						break;
					}
				}
				if (outputResponseFilter != null) {
					outputStatisticsList.add(outputResponseFilter);
				}
			}
		}
	}

	/**
	 * Filter the port of Stream
	 */
	private void filterPortNumber() {
		extractPortNumberList(this.portNumberFilter);
		if (!portNumberList.isEmpty()) {
			OutputResponse outputResponseFilter = null;
			for (int portNumber : portNumberList) {
				for (OutputResponse outputResponse : outputResponseList) {
					if (portNumber == Integer.parseInt(outputResponse.getPort())) {
						outputResponseFilter = outputResponse;
						break;
					}
				}
				if (outputResponseFilter != null) {
					outputForPortAndStatusList.add(outputResponseFilter);
				}
			}
			filterPortNumberRange();
		}
	}

	/**
	 * Filter the port number range of Stream
	 */
	private void filterPortNumberRange() {
		for (String portNumberRange : portNumberRangeList) {
			String[] rangeList = portNumberRange.split(HaivisionConstant.DASH);
			int mixPort = Integer.parseInt(rangeList[0]);
			int maxPort = Integer.parseInt(rangeList[1]);
			OutputResponse outputResponseRangeFilter = null;
			for (OutputResponse outputResponse : outputResponseList) {
				int port = Integer.parseInt(outputResponse.getPort());
				if (mixPort <= port && port <= maxPort) {
					outputResponseRangeFilter = outputResponse;
					break;
				}
			}
			if (outputResponseRangeFilter != null) {
				outputForPortAndStatusList.add(outputResponseRangeFilter);
			}
		}
		if (!outputForPortAndStatusList.isEmpty()) {
			outputStatisticsList.addAll(outputForPortAndStatusList);
		}
	}

	/**
	 * Filter the status of Stream
	 */
	private void filterStreamStatus() {
		extractStreamStatus(this.streamStatusFilter);
		if (!streamStatusList.isEmpty()) {
			Map<Integer, String> stateMap = OutputStateDropdown.getNameToValueMap();
			for (String streamStatus : streamStatusList) {
				OutputResponse outputStreamStatusFilter = null;

				if (outputForPortAndStatusList.isEmpty()) {
					for (OutputResponse outputResponse : outputResponseList) {
						String stateOutput = stateMap.get(Integer.parseInt(outputResponse.getState()));
						if (streamStatus.equals(stateOutput)) {
							outputStreamStatusFilter = outputResponse;
							break;
						}
					}
				} else {
					//And port number or port range with stream status
					for (OutputResponse outputResponse : outputForPortAndStatusList) {
						String stateOutput = stateMap.get(Integer.parseInt(outputResponse.getState()));
						if (streamStatus.equals(stateOutput)) {
							outputStreamStatusFilter = outputResponse;
							break;
						}
					}
				}
				if (outputStreamStatusFilter != null) {
					outputStatisticsList.add(outputStreamStatusFilter);
				}
			}
		}
	}

	/**
	 * Get streamName list from the streamNameFilter string
	 *
	 * @param streamName the portNumber is the name of stream
	 */
	private void extractStreamNameList(String streamName) {
		if (!StringUtils.isNullOrEmpty(streamName)) {
			String[] streamNameListString = streamName.split(HaivisionConstant.COMMA);
			for (String streamNameItem : streamNameListString) {
				streamNameList.add(streamNameItem.trim());
			}
		}
	}

	/**
	 * Get portNumber list from the portNumber string
	 *
	 * @param portNumber the portNumber is the port of stream
	 */
	private void extractPortNumberList(String portNumber) {
		if (!StringUtils.isNullOrEmpty(portNumber)) {
			String[] portNumberListString = portNumberFilter.split(HaivisionConstant.COMMA);
			for (String portNumberItem : portNumberListString) {
				try {
					portNumberList.add(Integer.valueOf(portNumberItem.trim()));
				} catch (Exception e) {
					try {
						int index = portNumberItem.trim().indexOf(HaivisionConstant.DASH);
						Integer.parseInt(portNumberItem.substring(0, index));
						Integer.parseInt(portNumberItem.substring(index + 1));
						portNumberRangeList.add(portNumberItem);
					} catch (Exception ex) {
						throw new ResourceNotReachableException("The port range not correct format" + portNumberItem);
					}
				}
			}
		}
	}

	/**
	 * Get streamStatus list from the streamStatusFilter string
	 *
	 * @param streamStatus the streamStatus is the state of stream
	 */
	private void extractStreamStatus(String streamStatus) {
		if (!StringUtils.isNullOrEmpty(streamStatus)) {
			String[] streamNameFilterString = streamStatusFilter.split(HaivisionConstant.COMMA);
			for (String portNumberItem : streamNameFilterString) {
				streamStatusList.add(portNumberItem.trim());
			}
		}
	}

	/**
	 * Filter list Audio statistics by output stream response
	 */
	private void filterAudioAndVideoStatisticsList() {
		if (!outputForPortAndStatusList.isEmpty()) {
			for (OutputResponse outputResponse : outputForPortAndStatusList) {
				filterAudioAndVideo(outputResponse);
			}
		} else {
			for (OutputResponse outputResponse : outputStatisticsList) {
				filterAudioAndVideo(outputResponse);
			}
		}
	}

	/**
	 * Filter audio and video with output outputResponse
	 *
	 * @param outputResponse the outputResponse is DTO
	 */
	private void filterAudioAndVideo(OutputResponse outputResponse) {
		List<Audio> audioList = outputResponse.getAudio();
		if (audioList != null) {
			for (Audio audio : audioList) {
				filterTheAudioResponseByAudioStatistics(audio);
			}
		}
		List<Video> videoList = outputResponse.getVideo();
		if (videoList != null) {
			for (Video video : videoList) {
				filterVideoResponseByVideoStatistics(video);
			}
		}
	}

	/**
	 * Filter audio response by audio statistics
	 *
	 * @param audio the audio is audio DTO
	 */
	private void filterTheAudioResponseByAudioStatistics(Audio audio) {
		for (AudioResponse audioResponse : audioResponseList) {
			if (audioResponse.getId().equals(audio.getId())) {
				audioStatisticsList.add(audioResponse);
				break;
			}
		}
	}

	/**
	 * Filter video response by video statistics
	 *
	 * @param video the video is video DTO
	 */
	private void filterVideoResponseByVideoStatistics(Video video) {
		for (VideoResponse videoResponse : videoResponseList) {
			if (videoResponse.getId().equals(video.getId())) {
				videoStatisticsList.add(videoResponse);
				break;
			}
		}
	}

	/**
	 * Check the adapter filter
	 */
	private void isAdapterFiltering() {
		isAdapterFilter = false;
		if (!StringUtils.isNullOrEmpty(streamStatusFilter) || !StringUtils.isNullOrEmpty(portNumberFilter) || !StringUtils.isNullOrEmpty(streamNameFilter)) {
			isAdapterFilter = true;
		}
	}

	/**
	 * Count metric monitoring in the metrics
	 *
	 * @return number monitoring in the metric
	 */
	private int getNumberMonitoringMetric() {
		int countMonitoringMetric = 0;
		for (HaivisionMonitoringMetric metric : HaivisionMonitoringMetric.values()) {
			if (metric.isMonitor()) {
				countMonitoringMetric++;
			}
		}
		return countMonitoringMetric;
	}
}