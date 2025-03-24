/*
 *  * Copyright (c) 2022-2025 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.statistics.DynamicStatisticsDefinitions;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.CollectionUtils;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import com.avispl.symphony.api.dal.control.Controller;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;
import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.api.dal.monitor.Monitorable;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.AudioMonitoringMetric;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.HaivisionConstant;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.HaivisionStatisticsUtil;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.HaivisionURL;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.OutputMonitoringMetric;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.SystemMonitoringMetric;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.VideoMonitoringMetric;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.AudioStateDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.LanguageDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.OutputStateDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.ProtocolDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.VideoStateDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.AudioResponse;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.AudioResponseWrapper;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.OutputResponse;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.OutputResponseWrapper;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.SystemInfoResponse;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.VideoResponse;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.VideoResponseWrapper;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.audio.Audio;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.audio.AudioStatistic;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.output.OutputStatistic;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.video.Video;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.video.VideoStatistic;
import com.avispl.symphony.dal.communicator.RestCommunicator;
import com.avispl.symphony.dal.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * An implementation of RestCommunicator to provide communication and interaction with Haivision Makito X4 Encoders
 * Supported features are:
 * <p>
 * Monitoring:
 * <li>Info System</li>
 * <li>Audio encoder statistics</li>
 * <li>Video encoder statistics</li>
 * <li>Output stream encoder statistics</li>
 * <p>
 * Controlling:
 * <li>Start/Stop /Edit audio encoder config</li>
 * <li>Start/Stop /Edit video encoder config</li>
 * <li>Create/ Edit/ Delete / Start/Stop /Edit output stream </li>
 *
 * @author Ivan / Symphony Dev Team<br>
 * Created on 3/8/2022
 * @since 1.0.0
 */

public class HaivisionX4EncoderCommunicator extends RestCommunicator implements Monitorable, Controller {
	/**
	 * API header interceptor instance
	 * @since 1.1.1
	 * */
	private ClientHttpRequestInterceptor haivisionInterceptor = new HaivisionX4EncoderInterceptor();

	/**
	 * HttpRequest interceptor to intercept cookie header and further use it for authentication
	 *
	 * @author Maksym.Rossiitsev/Symphony Team
	 * @since 1.1.1
	 * */
	class HaivisionX4EncoderInterceptor implements ClientHttpRequestInterceptor {
		@Override
		public ClientHttpResponse intercept(org.springframework.http.HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

			ClientHttpResponse response = execution.execute(request, body);

			if (request.getURI().getPath().contains(HaivisionURL.AUTHENTICATION.getUrl())) {
				HttpHeaders headers = response.getHeaders();
				String sessionId = headers.getFirst(HaivisionConstant.SET_COOKIE);
				if (StringUtils.isNotNullOrEmpty(sessionId)) {
					sessionID = sessionId;
				} else {
					sessionID = HaivisionConstant.AUTHORIZED;
					throw new ResourceNotReachableException(HaivisionConstant.ERR_RETRIEVE_SESSION);
				}
			}
			return response;
		}
	}

	private String sessionID;
	private boolean isAdapterFilter;
	private Integer countMonitoringNumber = null;
	private ExtendedStatistics localExtendedStatistics;
	private Map<String, String> failedMonitor = new HashMap<>();

	private final String uuidDay = UUID.randomUUID().toString().replace(HaivisionConstant.DASH, "");

	//The properties adapter
	private String streamNameFilter;
	private String portNumberFilter;
	private String streamStatusFilter;
	private String audioFilter;
	private String videoFilter;

	private List<String> streamNameList = new ArrayList<>();
	private final List<Integer> portNumberList = new ArrayList<>();
	private final List<String> portNumberRangeList = new ArrayList<>();
	private List<String> streamStatusList = new ArrayList<>();

	private Map<String, AudioResponse> audioNameToAudioResponse = new HashMap<>();
	private Map<String, VideoResponse> videoNameToVideoResponse = new HashMap<>();
	private Map<String, String> audioIdToName = new HashMap<>();
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
	private Set<OutputResponse> outputStatisticsList = new HashSet<>();

	/**
	 * List of output statistics for the adapter filter
	 */
	private Set<OutputResponse> outputForPortAndStatusList = new HashSet<>();

	/**
	 * List of audio Response
	 */
	private List<AudioResponse> audioResponseList = new ArrayList<>();

	/**
	 * List of audio Response
	 */
	private List<VideoResponse> videoResponseList = new ArrayList<>();

	/**
	 * List of audio Response
	 */
	private List<OutputResponse> outputResponseList = new ArrayList<>();

	/**
	 * Configurable property for historical properties, comma separated values kept as set
	 * */
	private Set<String> historicalProperties = new HashSet<>();

	/**
	 * Retrieves {@link #historicalProperties}
	 *
	 * @return value of {@link #historicalProperties}
	 */
	public String getHistoricalProperties() {
		return String.join(",", this.historicalProperties);
	}

	/**
	 * Sets {@link #historicalProperties} value
	 *
	 * @param historicalProperties new value of {@link #historicalProperties}
	 */
	public void setHistoricalProperties(String historicalProperties) {
		this.historicalProperties.clear();
		Arrays.asList(historicalProperties.split(",")).forEach(propertyName -> {
			this.historicalProperties.add(propertyName.trim());
		});
	}

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
	 * Retrieves {@code {@link #audioFilter}}
	 *
	 * @return value of {@link #audioFilter}
	 */
	public String getAudioFilter() {
		return audioFilter;
	}

	/**
	 * Sets {@code audioFilter}
	 *
	 * @param audioFilter the {@code java.lang.String} field
	 */
	public void setAudioFilter(String audioFilter) {
		this.audioFilter = audioFilter;
	}

	/**
	 * Retrieves {@code {@link #videoFilter}}
	 *
	 * @return value of {@link #videoFilter}
	 */
	public String getVideoFilter() {
		return videoFilter;
	}

	/**
	 * Sets {@code videoFilter}
	 *
	 * @param videoFilter the {@code java.lang.String} field
	 */
	public void setVideoFilter(String videoFilter) {
		this.videoFilter = videoFilter;
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

		if (sessionID == null) {
			if (!StringUtils.isNullOrEmpty(getPassword()) && !StringUtils.isNullOrEmpty(getLogin())) {
				authenticate();
			} else {
				sessionID = HaivisionConstant.AUTHORIZED;
			}
		}
		localExtendedStatistics = new ExtendedStatistics();
		isAdapterFiltering();
		populateInformationFromDevice(stats);
		provisionTypedStatistics(stats, extendedStatistics);
		localExtendedStatistics = extendedStatistics;

		return Collections.singletonList(localExtendedStatistics);
	}

	@Override
	protected RestTemplate obtainRestTemplate() throws Exception {
		RestTemplate restTemplate = super.obtainRestTemplate();
		List<ClientHttpRequestInterceptor> restTemplateInterceptors = restTemplate.getInterceptors();

		if (!restTemplateInterceptors.contains(haivisionInterceptor))
			restTemplateInterceptors.add(haivisionInterceptor);

		return restTemplate;
	}

	@Override
	public void controlProperty(ControllableProperty controllableProperty) throws Exception {
		String property = controllableProperty.getProperty();
		String value = String.valueOf(controllableProperty.getValue());
		if (logger.isDebugEnabled()) {
			logger.debug("controlProperty property" + property);
			logger.debug("controlProperty value" + value);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void controlProperties(List<ControllableProperty> list) throws Exception {
		if (CollectionUtils.isEmpty(list)) {
			throw new IllegalArgumentException("Controllable properties cannot be null or empty");
		}
		for (ControllableProperty controllableProperty : list) {
			controlProperty(controllableProperty);
		}
	}


	@Override
	protected void authenticate() throws Exception {
		Map<String, String> request = new HashMap<>();
		request.put(HaivisionConstant.USER_NAME, getLogin());
		request.put(HaivisionConstant.PASSWORD, getPassword());
		doPost(this.buildPathToLogin(), request);
	}

	/**
	 * @return HttpHeaders contain cookie for authorization
	 */
	@Override
	protected HttpHeaders putExtraRequestHeaders(HttpMethod httpMethod, String uri, HttpHeaders headers) throws Exception {
		headers.set("Content-Type", "text/xml");
		headers.set("Content-Type", "application/json");

		if (sessionID != null && !sessionID.equals(HaivisionConstant.AUTHORIZED)) {
			headers.set(HaivisionConstant.COOKIE, sessionID);
		}
		return super.putExtraRequestHeaders(httpMethod, uri, headers);
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
		stringBuilder.append(HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION));

		return stringBuilder.toString();
	}

	/**
	 * Retrieve data and add to stats
	 */
	private void populateInformationFromDevice(Map<String, String> stats) {
		for (HaivisionURL haivisionURL : HaivisionURL.values()) {
			if (HaivisionURL.AUTHENTICATION.equals(haivisionURL)) {
				continue;
			}
			retrieveDataByURL(stats, haivisionURL);
		}
		if (countMonitoringNumber == null) {
			countMonitoringNumber = getNumberMonitoringMetric();
		}
		if (failedMonitor.size() == countMonitoringNumber) {
			StringBuilder stringBuilder = new StringBuilder();
			for (Map.Entry<String, String> messageFailed : failedMonitor.entrySet()) {
				stringBuilder.append(messageFailed.getValue());
			}
			sessionID = null;
			failedMonitor.clear();
			throw new ResourceNotReachableException("Get monitoring data failed: " + stringBuilder);
		}
		getFilteredForEncoderStatistics();
		for (HaivisionURL haivisionURL : HaivisionURL.values()) {
			if (HaivisionURL.AUDIO_ENCODER.equals(haivisionURL)) {
				populateAudioData(stats);
			}
			if (HaivisionURL.VIDEO_ENCODER.equals(haivisionURL)) {
				populateVideoData(stats);
			}
			if (HaivisionURL.OUTPUT_ENCODER.equals(haivisionURL)) {
			populateOutputData(stats);
			}
		}
	}

	/**
	 * Retrieve data from the device
	 *
	 * @param url list URL of device
	 */
	private void retrieveDataByURL(Map<String, String> stats, HaivisionURL url) {
		Objects.requireNonNull(url);
		switch (url) {
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
			case ROLE_BASED:
			case STREAM:
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
		int indexName = audioName.indexOf(HaivisionConstant.SPACE, HaivisionConstant.AUDIO_ENCODER.length());
		if (indexName != -1) {
			audioName = audioName.substring(0, indexName);
		}
		AudioStatistic audioStatistic = audioResponseList.getAudioStatistic();
		Map<Integer, String> audioMap = AudioStateDropdown.getNameToValueMap();
		String state = audioResponseList.getState();
		for (AudioMonitoringMetric audioMetric : AudioMonitoringMetric.values()) {
			if (audioMetric.equals(AudioMonitoringMetric.STATE)) {
				if (HaivisionConstant.NONE.equals(state)) {
					stats.put(String.format(HaivisionConstant.FORMAT, audioName + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS, audioMetric.getName()), state);
				} else {
					stats.put(String.format(HaivisionConstant.FORMAT, audioName + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS, audioMetric.getName()),
							String.valueOf(audioMap.get(Integer.valueOf(state))));
				}
				continue;
			}
			if (audioMetric.equals(AudioMonitoringMetric.ENCODED_BYTES) || audioMetric.equals(AudioMonitoringMetric.ENCODED_FRAMES)) {
				stats.put(audioName + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + HaivisionConstant.HASH + audioMetric.getName(),
						replaceCommaByEmptyString(audioStatistic.getValueByMetric(audioMetric)));
			} else {
				stats.put(audioName + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + HaivisionConstant.HASH + audioMetric.getName(), audioStatistic.getValueByMetric(audioMetric));
			}
		}
	}

	/**
	 * Replace comma by empty string
	 *
	 * @param value the value is string
	 * @return value is replace the comma
	 */
	private String replaceCommaByEmptyString(String value) {
		if (StringUtils.isNullOrEmpty(value)) {
			return HaivisionConstant.NONE;
		}
		return value.replace(HaivisionConstant.COMMA, HaivisionConstant.EMPTY_STRING);
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
		for (VideoMonitoringMetric videoMetric : VideoMonitoringMetric.values()) {
			if (VideoMonitoringMetric.UPTIME.equals(videoMetric) || VideoMonitoringMetric.OCCURRED.equals(videoMetric)) {
				stats.put(videoName + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + HaivisionConstant.HASH + videoMetric.getName(),
						formatTimeData(videoStatistic.getValueByMetric(videoMetric)));
				continue;
			}
			if (VideoMonitoringMetric.STATE.equals(videoMetric)) {
				if (HaivisionConstant.NONE.equals(state)) {
					stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS, videoMetric.getName()), state);
				} else {
					stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS, videoMetric.getName()),
							String.valueOf(videoMap.get(Integer.valueOf(state))));
				}
			} else if (VideoMonitoringMetric.ENCODED_BYTES_VIDEO.equals(videoMetric) || VideoMonitoringMetric.DROPPED_FRAMERATE.equals(videoMetric) || VideoMonitoringMetric.ENCODED_BITRATE_VIDEO.equals(
					videoMetric) || VideoMonitoringMetric.ENCODED_FRAMES_VIDEO.equals(videoMetric)) {
				stats.put(videoName + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + HaivisionConstant.HASH + videoMetric.getName(),
						replaceCommaByEmptyString(videoStatistic.getValueByMetric(videoMetric)));
			} else {
				stats.put(videoName + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + HaivisionConstant.HASH + videoMetric.getName(), videoStatistic.getValueByMetric(videoMetric));
			}
		}
	}

	/**
	 * Add output data to statistics property
	 *
	 * @param stats list statistics property
	 */
	private void populateOutputData(Map<String, String> stats) {
		List<OutputResponse> outputResponseControlList = new ArrayList<>();
		if (isAdapterFilter) {
			for (OutputResponse outputResponses : outputStatisticsList) {
				addOutputStreamDataStatisticsToStatisticsProperty(stats, outputResponses);
			}
			outputResponseControlList.addAll(outputStatisticsList);
		} else {
			for (OutputResponse outputResponses : outputResponseList) {
				addOutputStreamDataStatisticsToStatisticsProperty(stats, outputResponses);
			}
			outputResponseControlList.addAll(outputResponseList);
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
		String streamStatisticsName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + name + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + HaivisionConstant.HASH;
		for (OutputMonitoringMetric outputStreamMetric : OutputMonitoringMetric.values()) {
			if (OutputMonitoringMetric.UPTIME.equals(outputStreamMetric) || OutputMonitoringMetric.OCCURRED.equals(outputStreamMetric)) {
				stats.put(streamStatisticsName + outputStreamMetric.getName(),
						formatTimeData(outputStatistic.getValueByMetric(outputStreamMetric)));
				continue;
			}

			if (outputStreamMetric.isReplaceComma()) {
				stats.put(streamStatisticsName + outputStreamMetric.getName(),
						replaceCommaByEmptyString(outputStatistic.getValueByMetric(outputStreamMetric)));
			} else if (outputStreamMetric.isNormalize()) {
				stats.put(streamStatisticsName + outputStreamMetric.getName(),
						extractBitRate(outputStatistic.getValueByMetric(outputStreamMetric)));
			} else if (OutputMonitoringMetric.STATE.equals(outputStreamMetric)) {
				if (HaivisionConstant.NONE.equals(state)) {
					stats.put(String.format(HaivisionConstant.FORMAT,  HaivisionConstant.STREAM + HaivisionConstant.SPACE + name + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS, outputStreamMetric.getName()), state);
				} else {
					stats.put(String.format(HaivisionConstant.FORMAT,  HaivisionConstant.STREAM + HaivisionConstant.SPACE + name + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS, outputStreamMetric.getName()),
							String.valueOf(outputMap.get(Integer.valueOf(state))));
				}
			} else {
				stats.put(streamStatisticsName + outputStreamMetric.getName(), outputStatistic.getValueByMetric(outputStreamMetric));
			}
		}
	}

	/**
	 * Convert String bitRate to Kbps
	 *
	 * @param bitRate the bitRate is string value
	 * @return bitRate extracted
	 */
	private String extractBitRate(String bitRate) {
		try {
			return bitRate.substring(0, bitRate.indexOf(HaivisionConstant.SPACE));
		} catch (Exception e) {
			return bitRate;
		}
	}

	/**
	 * Retrieve audio encoder
	 */
	private void retrieveAudioEncoder() {
		try {
			AudioResponseWrapper audioResponse = doGet(HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER), AudioResponseWrapper.class);
			audioResponseList.clear();
			audioStatisticsList.clear();
			audioNameToAudioResponse.clear();
			audioIdToName.clear();
			Map<String, String> languageName = LanguageDropdown.getNameToValueMap();
			for (AudioResponse audioItem : audioResponse.getData()) {
				String audioName = audioItem.getName();
				if (!StringUtils.isNullOrEmpty(audioItem.getLang()) && !HaivisionConstant.NONE.equals(audioItem.getLang())) {
					String name = languageName.get(audioItem.getLang());
					audioName = audioName + HaivisionConstant.SPACE + "(" + name.substring(0, name.indexOf(HaivisionConstant.SPACE)) + ")";
				}
				audioItem.setName(audioName);
				audioNameToAudioResponse.put(audioName, audioItem);
				audioResponseList.add(audioItem);
				audioIdToName.put(audioItem.getId(), audioItem.getName());
			}
		} catch (Exception e) {
			failedMonitor.put(HaivisionURL.AUDIO_ENCODER.getName(), e.getMessage());
		}
	}

	/**
	 * Retrieve video encoder
	 */
	private void retrieveVideoEncoder() {
		try {
			VideoResponseWrapper videoResponse = doGet(HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER), VideoResponseWrapper.class);
			videoResponseList.clear();
			videoStatisticsList.clear();
			videoNameToVideoResponse.clear();
			for (VideoResponse videoItem : videoResponse.getData()) {
				videoResponseList.add(videoItem);
				videoNameToVideoResponse.put(videoItem.getName(), videoItem);
			}
		} catch (Exception e) {
			failedMonitor.put(HaivisionURL.VIDEO_ENCODER.getName(), e.getMessage());
		}
	}

	/**
	 * Retrieve output stream encoder
	 */
	private void retrieveOutputEncoder() {
		try {
			OutputResponseWrapper outputResponse = doGet(HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.OUTPUT_ENCODER), OutputResponseWrapper.class);
			outputResponseList.clear();
			outputForPortAndStatusList.clear();
			outputStatisticsList.clear();
			handleStreamNameIsEmpty(outputResponse.getData());
			outputResponseList.addAll(outputResponse.getData());
		} catch (Exception e) {
			failedMonitor.put(HaivisionURL.OUTPUT_ENCODER.getName(), e.getMessage());
		}
	}

	/**
	 * Parsing stream name empty to default name ( {protocol}://{address}:{(port)} )
	 *
	 * @param outputStreamList list OutputResponse instance
	 */
	private void handleStreamNameIsEmpty(List<OutputResponse> outputStreamList) {
		Map<Integer, String> protocolMap = ProtocolDropdown.getNameToValueMap();
		for (OutputResponse output : outputStreamList) {
			String streamName = convertStreamNameUnescapeHtml3(output.getName());
			if (StringUtils.isNullOrEmpty(streamName)) {
				String protocol = output.getEncapsulation();
				String address = output.getAddress();
				String port = output.getPort();
				String streamNameConvert = protocolMap.get(Integer.parseInt(protocol)) + "://" + address + "(" + port + ")";
				output.setName(streamNameConvert);
			}
		}
	}

	/**
	 * Retrieve system information status encoder
	 */
	private void retrieveSystemInfoStatus(Map<String, String> stats) {
		try {
			SystemInfoResponse responseData = doGet(HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.SYSTEM_INFO_STATUS), SystemInfoResponse.class);
			if (responseData != null) {
				for (SystemMonitoringMetric systemInfoMetric : SystemMonitoringMetric.values()) {
					stats.put(systemInfoMetric.getName(), checkForNullData(responseData.getValueByMetric(systemInfoMetric)));
				}
			} else {
				contributeNoneValueForSystemInfo(stats);
			}
		} catch (Exception e) {
			contributeNoneValueForSystemInfo(stats);
			failedMonitor.put(HaivisionConstant.SYSTEM_INFO_STATUS, e.getMessage());
		}
	}

	/**
	 * Value of list statistics property of system info is none
	 *
	 * @param stats list statistics
	 */
	private void contributeNoneValueForSystemInfo(Map<String, String> stats) {
		for (SystemMonitoringMetric systemInfoMetric : SystemMonitoringMetric.values()) {
			stats.put(systemInfoMetric.getName(), HaivisionConstant.NONE);
		}
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
		return time.replace("d", uuidDay).replace("s", HaivisionConstant.SECOND).replace(uuidDay, HaivisionConstant.DAY)
				.replace("h", HaivisionConstant.HOUR).replace("m", HaivisionConstant.MINUTE);
	}

	/**
	 * Filter the list of aggregated devices based on filter option in Adapter Properties
	 */
	private void getFilteredForEncoderStatistics() {
		filterAudio();
		filterVideo();
		filterStreamName();
		filterPortNumber();
		filterStreamStatus();
		if (isAdapterFilter) {
			filterAudioAndVideoStatisticsList();
		}
	}

	/**
	 * Filter by audio id
	 */
	private void filterAudio() {
		List<String> audioNameList = extractListNameFilter(this.audioFilter);
		if(!StringUtils.isNullOrEmpty(audioFilter) && !audioNameList.isEmpty()){
			List<AudioResponse> newAudioResponseList = new ArrayList<>();
			for (AudioResponse audioResponse : audioResponseList) {
				if (audioNameList.contains(audioResponse.getId())) {
					newAudioResponseList.add(audioResponse);
				}
			}
			audioResponseList = newAudioResponseList;
		}
	}

	/**
	 * Filter by video id
	 */
	private void filterVideo() {
		List<String> videoNameList = extractListNameFilter(this.videoFilter);
		if(!StringUtils.isNullOrEmpty(videoFilter) && !videoNameList.isEmpty()){
			List<VideoResponse> newVideoResponseList = new ArrayList<>();
			for (VideoResponse videoResponse : videoResponseList) {
				if (videoNameList.contains(videoResponse.getId())) {
					newVideoResponseList.add(videoResponse);
				}
			}
			videoResponseList = newVideoResponseList;
		}
	}

	/**
	 * Filter the name of Stream
	 */
	private void filterStreamName() {
		streamNameList.clear();
		streamNameList = extractListNameFilter(this.streamNameFilter);
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
			List<OutputResponse> portFilterSet = new ArrayList<>();
			for (int portNumber : portNumberList) {
				for (OutputResponse outputResponse : outputResponseList) {
					if (portNumber == Integer.parseInt(outputResponse.getPort())) {
						portFilterSet.add(outputResponse);
					}
				}
				if (!portFilterSet.isEmpty()) {
					outputForPortAndStatusList.addAll(portFilterSet);
				}
			}
		}
		if (!portNumberRangeList.isEmpty()) {
			filterPortNumberRange();
		}
	}

	/**
	 * Filter the port number range of Stream
	 */
	private void filterPortNumberRange() {
		for (String portNumberRange : portNumberRangeList) {
			String[] rangeList = portNumberRange.split(HaivisionConstant.DASH);
			int mixPort = Integer.parseInt(rangeList[0].trim());
			int maxPort = Integer.parseInt(rangeList[1].trim());
			List<OutputResponse> portRangeFilterOutputSet = new ArrayList<>();
			for (OutputResponse outputResponse : outputResponseList) {
				int port = Integer.parseInt(outputResponse.getPort());
				if (mixPort <= port && port <= maxPort) {
					portRangeFilterOutputSet.add(outputResponse);
				}
			}
			if (!portRangeFilterOutputSet.isEmpty()) {
				outputForPortAndStatusList.addAll(portRangeFilterOutputSet);
			}
		}
	}

	/**
	 * Filter the status of Stream
	 */
	private void filterStreamStatus() {
		Set<OutputResponse> outputStreamStatusFilterList = new HashSet<>();
		streamStatusList.clear();
		streamStatusList = extractListNameFilter(this.streamStatusFilter);
		if (!streamStatusList.isEmpty()) {
			Map<Integer, String> stateMap = OutputStateDropdown.getNameToValueMap();
			for (String streamStatus : streamStatusList) {
				if (StringUtils.isNullOrEmpty(portNumberFilter)) {
					for (OutputResponse outputResponse : outputResponseList) {
						String stateOutput = stateMap.get(Integer.parseInt(outputResponse.getState()));
						if (streamStatus.equals(stateOutput)) {
							outputStreamStatusFilterList.add(outputResponse);
						}
					}
				} else {
					//And port number or port range with stream status
					for (OutputResponse outputResponse : outputForPortAndStatusList) {
						String stateOutput = stateMap.get(Integer.parseInt(outputResponse.getState()));
						if (streamStatus.equals(stateOutput)) {
							outputStreamStatusFilterList.add(outputResponse);
						}
					}
				}
			}
			if (!outputStreamStatusFilterList.isEmpty()) {
				outputStatisticsList.addAll(outputStreamStatusFilterList);
			}
		}
		if (streamStatusList.isEmpty() && !outputForPortAndStatusList.isEmpty()) {
			outputStatisticsList.addAll(outputForPortAndStatusList);
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
						Integer.parseInt(portNumberItem.substring(0, index).trim());
						Integer.parseInt(portNumberItem.substring(index + 1).trim());
						portNumberRangeList.add(portNumberItem);
					} catch (Exception ex) {
						if (logger.isDebugEnabled()) {
							logger.debug("The port range not correct format" + portNumberItem);
						}
					}
				}
			}
		}
	}

	/**
	 * Get list name by adapter filter
	 *
	 * @param filterName the name is name of filter
	 * @return List<String> is split list of String
	 */
	private List<String> extractListNameFilter(String filterName) {
		List<String> listName = new ArrayList<>();
		if (!StringUtils.isNullOrEmpty(filterName)) {
			String[] nameStringFilter = filterName.split(HaivisionConstant.COMMA);
			for (String listNameItem : nameStringFilter) {
				listName.add(listNameItem.trim());
			}
		}
		return listName;
	}

	/**
	 * Filter list Audio statistics by output stream response
	 */
	private void filterAudioAndVideoStatisticsList() {
		for (OutputResponse outputResponse : outputStatisticsList) {
			filterAudioAndVideo(outputResponse);
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
		for (HaivisionURL metric : HaivisionURL.values()) {
			if (metric.isMonitor()) {
				countMonitoringMetric++;
			}
		}
		return countMonitoringMetric;
	}

	/**
	 * Add a property as a regular statistics property, or as dynamic one, based on the {@link #historicalProperties} configuration
	 * and DynamicStatisticsDefinitions static definitions.
	 *
	 * @param statistics map of all device properties
	 * @param extendedStatistics device statistics object
	 * */
	private void provisionTypedStatistics(Map<String, String> statistics, ExtendedStatistics extendedStatistics) {
		Map<String, String> dynamicStatistics = new HashMap<>();
		Map<String, String> staticStatistics = new HashMap<>();
		statistics.forEach((propertyName, propertyValue) -> {
			// To ignore the group properties are in, we need to split it
			// whenever there's a hash involved and take the 2nd part
			boolean propertyListed = false;
			if (!historicalProperties.isEmpty()) {
				if (propertyName.contains(HaivisionConstant.HASH)) {
					propertyListed = historicalProperties.contains(propertyName.split(HaivisionConstant.HASH)[1]);
				} else {
					propertyListed = historicalProperties.contains(propertyName);
				}
			}
			if (propertyListed && DynamicStatisticsDefinitions.checkIfExists(propertyName)) {
				dynamicStatistics.put(propertyName, propertyValue);
			} else {
				staticStatistics.put(propertyName, propertyValue);
			}
		});
		extendedStatistics.setDynamicStatistics(dynamicStatistics);
		extendedStatistics.setStatistics(staticStatistics);
	}
}