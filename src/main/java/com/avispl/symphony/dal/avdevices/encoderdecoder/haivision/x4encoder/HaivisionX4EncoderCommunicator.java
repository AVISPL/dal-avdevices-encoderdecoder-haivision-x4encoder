/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.databind.JsonNode;
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
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.AudioControllingMetric;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.AudioMonitoringMetric;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.HaivisionConstant;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.HaivisionStatisticsUtil;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.HaivisionURL;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.OutputMonitoringMetric;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.SystemMonitoringMetric;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.VideoMonitoringMetric;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.AudioStateDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.BitRateDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.ChannelModeDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.CodecAlgorithm;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.InputDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.LanguageDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.OutputStateDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.SampleRateDropdown;
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
 * @author Ivan
 * @since 1.0.0
 */
public class HaivisionX4EncoderCommunicator extends RestCommunicator implements Monitorable, Controller {

	private String sessionID;
	private String roleBased;
	private boolean isAdapterFilter;
	private Integer countMonitoringNumber = null;
	private ExtendedStatistics localExtendedStatistics;
	private final Map<String, String> failedMonitor = new HashMap<>();
	private boolean emergencyDelivery;

	private final String uuidDay = UUID.randomUUID().toString().replace(HaivisionConstant.DASH, "");

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
			if (!StringUtils.isNullOrEmpty(getPassword()) && !StringUtils.isNullOrEmpty(getLogin())) {
				sessionID = retrieveSessionID();
				roleBased = retrieveUserRole();
			} else {
				sessionID = HaivisionConstant.AUTHORIZED;
			}
		}

		if (localExtendedStatistics == null) {
			localExtendedStatistics = new ExtendedStatistics();
		}

		isAdapterFiltering();
		populateInformationFromDevice(stats, advancedControllableProperties);

		extendedStatistics.setStatistics(stats);

		if (HaivisionConstant.OPERATOR.equals(roleBased) || HaivisionConstant.ADMIN.equals(roleBased)) {
			extendedStatistics.setControllableProperties(advancedControllableProperties);
		}
		extendedStatistics.setStatistics(stats);

		if (!emergencyDelivery) {
			localExtendedStatistics = extendedStatistics;
		}
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
		emergencyDelivery = true;
		//TODO
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
			headers.set(HaivisionConstant.COOKIE, sessionID);
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
		request.put(HaivisionConstant.PASSWORD, getPassword());

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
		stringBuilder.append(HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUTHENTICATION));

		return stringBuilder.toString();
	}

	/**
	 * Retrieve data and add to stats
	 */
	private void populateInformationFromDevice(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
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
			throw new ResourceNotReachableException("Get monitoring data failed: " + stringBuilder);
		}
		getFilteredForEncoderStatistics();
		for (HaivisionURL haivisionURL : HaivisionURL.values()) {
			if (HaivisionURL.AUDIO_ENCODER.equals(haivisionURL)) {
				populateAudioData(stats, advancedControllableProperties);
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
	private void populateAudioData(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		if (!audioStatisticsList.isEmpty()) {
			for (AudioResponse audioResponses : audioStatisticsList) {
				addAudioDataStatisticsToStatisticsProperty(stats, audioResponses);
			}
		} else if (!audioResponseList.isEmpty() && !isAdapterFilter) {
			for (AudioResponse audioResponses : audioResponseList) {
				addAudioDataStatisticsToStatisticsProperty(stats, audioResponses);
			}
		}
		if (HaivisionConstant.OPERATOR.equals(roleBased) || HaivisionConstant.ADMIN.equals(roleBased)) {
			for (AudioResponse audioResponses : audioResponseList) {
				addAudioDataControlToProperty(stats, audioResponses, advancedControllableProperties);
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
			stats.put(audioName + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + HaivisionConstant.HASH + audioMetric.getName(), audioStatistic.getValueByMetric(audioMetric));
		}
	}

	/**
	 * Add audio data to statistics property
	 *
	 * @param stats list statistics property
	 * @param audioResponseList list of audio response
	 */
	private void addAudioDataControlToProperty(Map<String, String> stats, AudioResponse audioResponseList, List<AdvancedControllableProperty> advancedControllableProperties) {
		String audioName = audioResponseList.getName();
		Map<Integer, String> audioMap = AudioStateDropdown.getNameToValueMap();
		Map<Integer, String> channelModeMap = ChannelModeDropdown.getNameToValueMap();
		Map<Integer, String> channel = ChannelModeDropdown.getNameToValueMap();
		Map<Integer, String> sampleRateMap = SampleRateDropdown.getNameToValueMap();
		Map<Integer, String> codecAlgorithmDropdown = CodecAlgorithm.getNameToValueMap();
		Map<String, String> languageDropdown = LanguageDropdown.getNameToValueMap();
		Map<Integer, String> inputMap = InputDropdown.getNameToValueMap();
		String[] dropdownInput = InputDropdown.names();
		String[] dropdownMode = ChannelModeDropdown.names();
		String[] dropdownAlgorithm = CodecAlgorithm.names();
		String[] dropdownSampleRate = SampleRateDropdown.names();
		String[] dropdownLanguage = LanguageDropdown.names();
		String[] dropdownAction = HaivisionConstant.START_AUDIO_VIDEO;
		String[] dropdownBitRate;
		String value;
		
		for (AudioControllingMetric audioMetric : AudioControllingMetric.values()) {
			switch (audioMetric) {
				case STATE:
					String stateAudio = audioResponseList.getState();
					value = compareNoneValueAndValueDeserializer(stateAudio, HaivisionConstant.NONE.equals(stateAudio), audioMap.get(Integer.parseInt(stateAudio)));
					stats.put(audioName + HaivisionConstant.HASH + audioMetric.getName(), value);
					break;
				case INPUT:
					String input = audioResponseList.getInterfaceName();
					value = compareNoneValueAndValueDeserializer(input, HaivisionConstant.NONE.equals(input), inputMap.get(Integer.parseInt(input)));
					AdvancedControllableProperty inputDropdownControlProperty = controlDropdown(stats, dropdownInput, audioName + HaivisionConstant.HASH + audioMetric.getName(), value);
					addAdvanceControlProperties(advancedControllableProperties, inputDropdownControlProperty);
					break;
				case CHANGE_MODE:
					String changeMode = audioResponseList.getMode();
					value = compareNoneValueAndValueDeserializer(changeMode, HaivisionConstant.NONE.equals(changeMode), channelModeMap.get(Integer.parseInt(changeMode)));
					AdvancedControllableProperty channelModeControlProperty = controlDropdown(stats, dropdownMode, audioName + HaivisionConstant.HASH + audioMetric.getName(), value);
					addAdvanceControlProperties(advancedControllableProperties, channelModeControlProperty);
					break;
				case BITRATE:
					String bitRate = audioResponseList.getInterfaceName();
					value = compareNoneValueAndValueDeserializer(bitRate, HaivisionConstant.NONE.equals(bitRate), channel.get(Integer.parseInt(bitRate)));
					String mode = audioResponseList.getMode();
					mode = HaivisionConstant.NONE.equals(audioResponseList.getMode()) ? mode : channel.get(Integer.parseInt(audioResponseList.getMode()));
					if (mode.equals(ChannelModeDropdown.STEREO.getName())) {
						dropdownBitRate = BitRateDropdown.namesIsStereo();
					} else {
						dropdownBitRate = BitRateDropdown.namesIsMono();
					}
					AdvancedControllableProperty bitRateControlProperty = controlDropdown(stats, dropdownBitRate, audioName + HaivisionConstant.HASH + audioMetric.getName(), value);
					addAdvanceControlProperties(advancedControllableProperties, bitRateControlProperty);
					break;
				case SAMPLE_RATE:
					String sampleRate = audioResponseList.getSampleRate();
					value = compareNoneValueAndValueDeserializer(sampleRate, HaivisionConstant.NONE.equals(sampleRate), sampleRateMap.get(Integer.parseInt(sampleRate)));
					AdvancedControllableProperty samPleRateControlProperty = controlDropdown(stats, dropdownSampleRate, audioName + HaivisionConstant.HASH + audioMetric.getName(), value);
					addAdvanceControlProperties(advancedControllableProperties, samPleRateControlProperty);
					break;
				case ALGORITHM:
					String codecAlgorithm = audioResponseList.getAlgorithm();
					value = compareNoneValueAndValueDeserializer(codecAlgorithm, HaivisionConstant.NONE.equals(codecAlgorithm), codecAlgorithmDropdown.get(Integer.parseInt(codecAlgorithm)));
					AdvancedControllableProperty codecAlgorithmControlProperty = controlDropdown(stats, dropdownAlgorithm, audioName + HaivisionConstant.HASH + audioMetric.getName(), value);
					addAdvanceControlProperties(advancedControllableProperties, codecAlgorithmControlProperty);
					break;
				case LANGUAGE:
					String language = audioResponseList.getLang();
					value = compareNoneValueAndValueDeserializer(HaivisionConstant.NONE, StringUtils.isNullOrEmpty(language), languageDropdown.get(audioResponseList.getLang()));
					AdvancedControllableProperty languageControlProperty = controlDropdownLanguage(stats, dropdownLanguage, audioName + HaivisionConstant.HASH + audioMetric.getName(), value);
					addAdvanceControlProperties(advancedControllableProperties, languageControlProperty);
					break;
				case ACTION:
					stateAudio = audioResponseList.getState();
					value = compareNoneValueAndValueDeserializer(stateAudio, HaivisionConstant.NONE.equals(stateAudio), audioMap.get(Integer.parseInt(stateAudio)));
					stats.put(audioName + HaivisionConstant.HASH + audioMetric.getName(), value);
					if (!value.equals(HaivisionConstant.STOP_AUDIO_VIDEO[0])) {
						dropdownAction = HaivisionConstant.START_AUDIO_VIDEO;
					}
					AdvancedControllableProperty actionDropdownControlProperty = controlDropdown(stats, dropdownAction, audioName + HaivisionConstant.HASH + audioMetric.getName(), value);
					addAdvanceControlProperties(advancedControllableProperties, actionDropdownControlProperty);
					break;
				case APPLY_CHANGE:
					stats.put(audioName + HaivisionConstant.HASH + audioMetric.getName(), "");
					advancedControllableProperties.add(createButton(audioName + HaivisionConstant.HASH + audioMetric.getName(), HaivisionConstant.APPLY, "Applying", 0));
					break;
				default:
					break;
			}
		}

	}

	/**
	 * Add advancedControllableProperties if  advancedControllableProperties different empty
	 *
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 * @param property the property is item advancedControllableProperties
	 */
	private void addAdvanceControlProperties(List<AdvancedControllableProperty> advancedControllableProperties, AdvancedControllableProperty property) {
		if (property != null) {
			advancedControllableProperties.add(property);
		}
	}

	/**
	 * Compare valueDeserializer is equal with noneValue
	 *
	 * @return String is the value
	 */
	private String compareNoneValueAndValueDeserializer(String noneValue, boolean compareNoneValueAndValueDeserializer, String valueDeserializer) {
		return compareNoneValueAndValueDeserializer ? noneValue : valueDeserializer;
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
		for (OutputMonitoringMetric outputStreamMetric : OutputMonitoringMetric.values()) {
			if (OutputMonitoringMetric.UPTIME.equals(outputStreamMetric) || OutputMonitoringMetric.OCCURRED.equals(outputStreamMetric)) {
				stats.put(name + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + HaivisionConstant.HASH + outputStreamMetric.getName(),
						formatTimeData(outputStatistic.getValueByMetric(outputStreamMetric)));
				continue;
			}
			if (OutputMonitoringMetric.STATE.equals(outputStreamMetric)) {
				if (HaivisionConstant.NONE.equals(state)) {
					stats.put(String.format(HaivisionConstant.FORMAT, name + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS, outputStreamMetric.getName()), state);
				} else {
					stats.put(String.format(HaivisionConstant.FORMAT, name + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS, outputStreamMetric.getName()),
							String.valueOf(outputMap.get(Integer.valueOf(state))));
				}
			} else {
				stats.put(name + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + HaivisionConstant.HASH + outputStreamMetric.getName(), outputStatistic.getValueByMetric(outputStreamMetric));
			}
		}
	}

	/**
	 * Retrieve audio encoder
	 */
	private void retrieveAudioEncoder() {
		try {
			AudioResponseWrapper audioResponse = doGet(HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER), AudioResponseWrapper.class);
			audioResponseList.addAll(audioResponse.getData());
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
			videoResponseList.addAll(videoResponse.getData());
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
			outputResponseList.addAll(outputResponse.getData());
		} catch (Exception e) {
			failedMonitor.put(HaivisionURL.OUTPUT_ENCODER.getName(), e.getMessage());
		}
	}

	/**
	 * Retrieve system information status encoder
	 */
	private void retrieveSystemInfoStatus(Map<String, String> stats) {
		try {
			SystemInfoResponse responseData = doGet(HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.SYSTEM_INFO_STATUS), SystemInfoResponse.class);
			if (responseData != null) {
				String name = HaivisionConstant.SYSTEM_INFO_STATUS;
				for (SystemMonitoringMetric systemInfoMetric : SystemMonitoringMetric.values()) {
					stats.put(name + HaivisionConstant.HASH + systemInfoMetric.getName(), checkForNullData(responseData.getValueByMetric(systemInfoMetric)));
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
			stats.put(HaivisionConstant.SYSTEM_INFO_STATUS + HaivisionConstant.HASH + systemInfoMetric.getName(), HaivisionConstant.NONE);
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
						if (logger.isDebugEnabled()) {
							logger.debug("The port range not correct format" + portNumberItem);
						}
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
		for (HaivisionURL metric : HaivisionURL.values()) {
			if (metric.isMonitor()) {
				countMonitoringMetric++;
			}
		}
		return countMonitoringMetric;
	}

	/**
	 * This method is used to retrieve User Role by send GET request to http://{IP_Address}/apis/accounts/{username}
	 *
	 * @throws ResourceNotReachableException When there is no valid User Role data or having an Exception
	 */
	private String retrieveUserRole() {
		try {
			JsonNode response = doGet(HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.ROLE_BASED) + getLogin(), JsonNode.class);
			String role = null;
			if (response != null) {
				JsonNode roleBase = response.get(HaivisionConstant.INFO);
				if (roleBase != null && roleBase.get(HaivisionConstant.ROLE) != null) {
					role = roleBase.get(HaivisionConstant.ROLE).asText();
				}
			}
			if (StringUtils.isNullOrEmpty(role)) {
				throw new ResourceNotReachableException("Role based is empty");
			}
			return role;
		} catch (Exception e) {
			throw new ResourceNotReachableException("Retrieve role based error: " + e.getMessage());
		}
	}

	/***
	 * Create dropdown advanced controllable property
	 *
	 * @param name the name of the control
	 * @param initialValue initial value of the control
	 * @return AdvancedControllableProperty dropdown instance
	 */
	private AdvancedControllableProperty createDropdown(String name, String[] values, String initialValue) {
		AdvancedControllableProperty.DropDown dropDown = new AdvancedControllableProperty.DropDown();
		dropDown.setOptions(values);
		dropDown.setLabels(values);

		return new AdvancedControllableProperty(name, new Date(), dropDown, initialValue);
	}

	/**
	 * Add dropdown is control property for metric
	 *
	 * @param stats list statistic
	 * @param options list select
	 * @param name String name of metric
	 * @return AdvancedControllableProperty dropdown instance if add dropdown success else will is null
	 */
	private AdvancedControllableProperty controlDropdown(Map<String, String> stats, String[] options, String name, String value) {
		stats.put(name, value);
		if (!HaivisionConstant.NONE.equals(value)) {
			return createDropdown(name, options, value);
		}
		return null;
	}

	/**
	 * Add dropdown is control property for metric
	 *
	 * @param stats list statistic
	 * @param options list select
	 * @param name String name of metric
	 * @return AdvancedControllableProperty dropdown instance if add dropdown success else will is null
	 */
	private AdvancedControllableProperty controlDropdownLanguage(Map<String, String> stats, String[] options, String name, String value) {
		stats.put(name, value);
		return createDropdown(name, options, value);
	}

	/**
	 * Create a button.
	 *
	 * @param name name of the button
	 * @param label label of the button
	 * @param labelPressed label of the button after pressing it
	 * @param gracePeriod grace period of button
	 * @return This returns the instance of {@link AdvancedControllableProperty} type Button.
	 */
	private AdvancedControllableProperty createButton(String name, String label, String labelPressed, long gracePeriod) {
		AdvancedControllableProperty.Button button = new AdvancedControllableProperty.Button();
		button.setLabel(label);
		button.setLabelPressed(labelPressed);
		button.setGracePeriod(gracePeriod);
		return new AdvancedControllableProperty(name, new Date(), button, "");
	}
}