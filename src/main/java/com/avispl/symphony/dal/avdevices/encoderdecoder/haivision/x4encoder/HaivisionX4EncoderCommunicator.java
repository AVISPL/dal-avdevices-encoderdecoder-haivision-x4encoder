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
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.VideoControllingMetric;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.VideoMonitoringMetric;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.AlgorithmDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.AspectRatioDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.AudioStateDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.BitRateDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.ChannelModeDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.ChromaSubSampling;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.CodecAlgorithm;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.CroppingDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.EncodingProfile;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.FrameRateDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.FramingDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.InputDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.LanguageDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.OutputStateDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.RateControlDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.ResolutionDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.SampleRateDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.TimeCodeSource;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.VideoInputDropdown;
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
	private boolean isEmergencyDelivery;

	private final String uuidDay = UUID.randomUUID().toString().replace(HaivisionConstant.DASH, "");

	//The properties adapter
	private String streamNameFilter;
	private String portNumberFilter;
	private String streamStatusFilter;

	private final List<String> streamNameList = new ArrayList<>();
	private final List<Integer> portNumberList = new ArrayList<>();
	private final List<String> portNumberRangeList = new ArrayList<>();
	private final List<String> streamStatusList = new ArrayList<>();

	private Map<String, String> audioNameToId = new HashMap<>();
	private Map<String, String> videoNameToId = new HashMap<>();
	private Map<String, String> streamNameToId = new HashMap<>();

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

		if (!isEmergencyDelivery) {
			isAdapterFiltering();
			populateInformationFromDevice(stats, advancedControllableProperties);
			extendedStatistics.setStatistics(stats);
			if (HaivisionConstant.OPERATOR.equals(roleBased) || HaivisionConstant.ADMIN.equals(roleBased)) {
				extendedStatistics.setControllableProperties(advancedControllableProperties);
			}
			extendedStatistics.setStatistics(stats);
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
		Map<String, String> extendedStatistics = localExtendedStatistics.getStatistics();
		List<AdvancedControllableProperty> advancedControllableProperties = localExtendedStatistics.getControllableProperties();

		String propertiesAudioAndVideo = property.substring(0, HaivisionConstant.AUDIO.length());
		if (HaivisionConstant.AUDIO.equals(propertiesAudioAndVideo)) {
			controlAudioProperty(property, value, extendedStatistics, advancedControllableProperties);
		}
		if (HaivisionConstant.VIDEO.equals(propertiesAudioAndVideo)) {
			controlVideoProperty(property, value, extendedStatistics, advancedControllableProperties);
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
			headers.set(HaivisionConstant.COOKIE, sessionID);
		}
		return super.putExtraRequestHeaders(httpMethod, uri, headers);
	}

	/**
	 * Control Audio encoder
	 *
	 * @param property the property is the filed name of controlling metric
	 * @param value the value is value of metric
	 * @param extendedStatistics list extendedStatistics
	 * @param advancedControllableProperties the advancedControllableProperties is advancedControllableProperties instance
	 */
	private void controlAudioProperty(String property, String value, Map<String, String> extendedStatistics, List<AdvancedControllableProperty> advancedControllableProperties) {
		String propertyName = property.split(HaivisionConstant.HASH)[1];
		AudioControllingMetric audioControllingMetric = AudioControllingMetric.getByName(propertyName);
		isEmergencyDelivery = true;
		switch (audioControllingMetric) {
			case INPUT:
				String[] inputDropdown = InputDropdown.names();
				AdvancedControllableProperty inputDropdownControlProperty = controlDropdown(extendedStatistics, inputDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, inputDropdownControlProperty);
				break;
			case CHANGE_MODE:
				String[] channelModeDropdown = ChannelModeDropdown.names();
				String currentBitRateValue = extendedStatistics.get(property.split(HaivisionConstant.HASH)[0] + HaivisionConstant.HASH + AudioControllingMetric.BITRATE.getName());
				String newBitRateValue = "";
				Map<String, Integer> bitRateNameModeMap = BitRateDropdown.getValueToNameMap();
				Map<Integer, String> bitRateValueModeMap = BitRateDropdown.getNameToValueMap();
				if (ChannelModeDropdown.STEREO.getName().equals(value)) {
					//If channel mode is Stereo and Bitrate is in Stereo bitrate list, the bitrate value will be not changed
					if (BitRateDropdown.checkIsStereoByValue(bitRateNameModeMap.get(currentBitRateValue))) {
						newBitRateValue = currentBitRateValue;
					}
					//If channel mode is Stereo and Bitrate is not in Stereo bitrate list, the bitrate value will be set to default bitrate value of Stereo
					else {
						newBitRateValue = bitRateValueModeMap.get(BitRateDropdown.getDefaultValueOfStereo());
					}
				} else {
					//If channel mode is Mono and Bitrate is in Mono bitrate list, the bitrate value will be not changed
					if (BitRateDropdown.checkIsMonoByValue(bitRateNameModeMap.get(currentBitRateValue))) {
						newBitRateValue = currentBitRateValue;
					}
					//If channel mode is Mono and Bitrate is in Mono bitrate list, the bitrate value will be set to default bitrate value of Mono
					else {
						newBitRateValue = bitRateValueModeMap.get(BitRateDropdown.getDefaultValueOfMono());
					}
				}
				String[] newBitRateDropdown = BitRateDropdown.namesIsMono();
				if (ChannelModeDropdown.STEREO.getName().equals(value)) {
					newBitRateDropdown = BitRateDropdown.namesIsStereo();
				}
				AdvancedControllableProperty bitRateDropdownControlProperty = controlDropdown(extendedStatistics, newBitRateDropdown,
						property.split(HaivisionConstant.HASH)[0] + HaivisionConstant.HASH + AudioControllingMetric.BITRATE.getName(), newBitRateValue);
				AdvancedControllableProperty channelModeDropdownControlProperty = controlDropdown(extendedStatistics, channelModeDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, bitRateDropdownControlProperty);
				addAdvanceControlProperties(advancedControllableProperties, channelModeDropdownControlProperty);
				break;
			case SAMPLE_RATE:
				String[] sampleRateDropdown = SampleRateDropdown.names();
				AdvancedControllableProperty sampleRateDropdownControlProperty = controlDropdown(extendedStatistics, sampleRateDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, sampleRateDropdownControlProperty);
				break;
			case ALGORITHM:
				String[] algorithmsDropdown = AlgorithmDropdown.names();
				AdvancedControllableProperty algorithmsControlProperty = controlDropdown(extendedStatistics, algorithmsDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, algorithmsControlProperty);
				break;
			case LANGUAGE:
				String[] languageDropdown = LanguageDropdown.names();
				AdvancedControllableProperty languageDropdownControlProperty = controlDropdown(extendedStatistics, languageDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, languageDropdownControlProperty);
				break;
			case BITRATE:
				String valueInput = extendedStatistics.get(property.split(HaivisionConstant.HASH)[0] + HaivisionConstant.HASH + AudioControllingMetric.CHANGE_MODE.getName());
				String[] bitRateDropdown = BitRateDropdown.namesIsMono();
				if (ChannelModeDropdown.STEREO.getName().equalsIgnoreCase(valueInput)) {
					bitRateDropdown = BitRateDropdown.namesIsStereo();
				}
				AdvancedControllableProperty bitRateControlProperty = controlDropdown(extendedStatistics, bitRateDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, bitRateControlProperty);
				break;
			case ACTION:
				String[] actionDropdown = HaivisionConstant.START_AUDIO_VIDEO;
				if (AudioStateDropdown.STOPPED.getName().equals(value)) {
					actionDropdown = HaivisionConstant.STOP_AUDIO_VIDEO;
				}
				AdvancedControllableProperty actionDropdownControlProperty = controlDropdownAcceptNoneValue(extendedStatistics, actionDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, actionDropdownControlProperty);
				break;
			case APPLY_CHANGE:
				String audioName = property.split(HaivisionConstant.HASH)[0];
				AudioResponse audioResponse = convertAudioByValue(extendedStatistics, audioName);

				// sent request to apply all change for all metric
				setAudioApplyChange(audioResponse.payLoad(), audioNameToId.get(audioName));

				//sent request to action for the metric
				setActionAudioControl(audioName, audioResponse);
				isEmergencyDelivery = false;
				break;
			case CANCEL:
				isEmergencyDelivery = false;
				break;
			default:
				break;
		}
		//Editing
		if (isEmergencyDelivery) {
			propertyName = property.split(HaivisionConstant.HASH)[0];
			extendedStatistics.put(propertyName + HaivisionConstant.HASH + AudioControllingMetric.APPLY_CHANGE.getName(), "");
			advancedControllableProperties.add(createButton(propertyName + HaivisionConstant.HASH + AudioControllingMetric.APPLY_CHANGE.getName(), HaivisionConstant.APPLY, "Applying", 0));

			extendedStatistics.put(propertyName + HaivisionConstant.HASH + HaivisionConstant.EDITED, HaivisionConstant.TRUE);
			extendedStatistics.put(propertyName + HaivisionConstant.HASH + HaivisionConstant.CANCEL, "");
			advancedControllableProperties.add(createButton(propertyName + HaivisionConstant.HASH + HaivisionConstant.CANCEL, HaivisionConstant.CANCEL, HaivisionConstant.CANCEL, 0));
		}
		localExtendedStatistics.setStatistics(extendedStatistics);
		localExtendedStatistics.setControllableProperties(advancedControllableProperties);
	}

	/**
	 * Sent request to action start/stop for the audio
	 *
	 * @param audioName the audioName is name of audio
	 * @param audioResponse is instance AudioResponse DTO
	 */
	private void setActionAudioControl(String audioName, AudioResponse audioResponse) {
		Map<String, String> extendedStatistics = localExtendedStatistics.getStatistics();
		String action = extendedStatistics.get(audioName + HaivisionConstant.HASH + AudioControllingMetric.ACTION.getName());
		String stateValue = extendedStatistics.get(audioName + HaivisionConstant.HASH + AudioControllingMetric.STATE.getName());
		String actionCompare = AudioStateDropdown.STOPPED.getName().equals(stateValue) ? HaivisionConstant.STOP : HaivisionConstant.START;
		if (!action.equals(actionCompare)) {
			changeAudioAction(action.toLowerCase(), audioNameToId.get(audioName), audioResponse.payLoad());
		}
	}

	/**
	 * Change AudioResponse by value
	 *
	 * @param extendedStatistics list
	 * @param audioName the audio name is name of audio
	 * @return AudioResponse
	 */
	private AudioResponse convertAudioByValue(Map<String, String> extendedStatistics, String audioName) {
		AudioResponse audioResponse = new AudioResponse();
		Map<String, Integer> channelModeMap = ChannelModeDropdown.getValueToNameMap();
		Map<String, Integer> bitRateMap = BitRateDropdown.getValueToNameMap();
		Map<String, Integer> sampleRateMap = SampleRateDropdown.getValueToNameMap();
		Map<String, Integer> algorithmMap = AlgorithmDropdown.getValueToNameMap();
		Map<String, String> languageMap = LanguageDropdown.getValueToNameMap();
		Map<String, Integer> inputMap = InputDropdown.getValueToNameMap();
		Map<String, Integer> audioStateMap = AudioStateDropdown.getValueToNameMap();
		audioResponse.setInterfaceName(String.valueOf(inputMap.get(extendedStatistics.get(audioName + HaivisionConstant.HASH + AudioControllingMetric.INPUT.getName()))));
		audioResponse.setBitRate(String.valueOf(bitRateMap.get(extendedStatistics.get(audioName + HaivisionConstant.HASH + AudioControllingMetric.BITRATE.getName()))));
		audioResponse.setSampleRate(String.valueOf(sampleRateMap.get(extendedStatistics.get(audioName + HaivisionConstant.HASH + AudioControllingMetric.SAMPLE_RATE.getName()))));
		audioResponse.setMode(String.valueOf(channelModeMap.get(extendedStatistics.get(audioName + HaivisionConstant.HASH + AudioControllingMetric.CHANGE_MODE.getName()))));
		audioResponse.setState(String.valueOf(audioStateMap.get(extendedStatistics.get(audioName + HaivisionConstant.HASH + AudioControllingMetric.STATE.getName()))));
		audioResponse.setAlgorithm(String.valueOf(algorithmMap.get(extendedStatistics.get(audioName + HaivisionConstant.HASH + AudioControllingMetric.ALGORITHM.getName()))));
		String language = languageMap.get(extendedStatistics.get(audioName + HaivisionConstant.HASH + AudioControllingMetric.LANGUAGE.getName()));
		if (HaivisionConstant.NONE.equals(language) || StringUtils.isNullOrEmpty(language)) {
			language = "";
		}
		audioResponse.setLang(language);
		audioResponse.setName(audioName);
		audioResponse.setId(audioNameToId.get(audioName));
		return audioResponse;
	}

	/**
	 * Control Video encoder
	 *
	 * @param property the property is the filed name of controlling metric
	 * @param value the value is value of metric
	 * @param extendedStatistics list extendedStatistics
	 * @param advancedControllableProperties the advancedControllableProperties is advancedControllableProperties instance
	 */
	private void controlVideoProperty(String property, String value, Map<String, String> extendedStatistics, List<AdvancedControllableProperty> advancedControllableProperties) {
		String propertyName = property.split(HaivisionConstant.HASH)[1];
		VideoControllingMetric videoControllingMetric = VideoControllingMetric.getByName(propertyName);
		isEmergencyDelivery = true;
		switch (videoControllingMetric) {
			case INPUT:
				String[] videoInputDropdown = VideoInputDropdown.names();
				AdvancedControllableProperty videoInputDropdownControlProperty = controlDropdown(extendedStatistics, videoInputDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, videoInputDropdownControlProperty);
				break;
			case CODEC_ALGORITHM:
				String[] codecAlgorithmDropdown = CodecAlgorithm.names();
				String[] newEncodingProfileDropdown = EncodingProfile.namesIsHEVC();
				String currentEncodingProfile = extendedStatistics.get(property.split(HaivisionConstant.HASH)[0] + HaivisionConstant.HASH + VideoControllingMetric.ENCODING_PROFILE.getName());
				String newEncodingProfile = "";

				//Change Codec Algorithm to HEVC/H.265
				if (CodecAlgorithm.H_265.getName().equals(value)) {

					//If Encoding profile is Baseline, Main or High; then it will be set to Main
					if (EncodingProfile.BASELINE.getName().equals(currentEncodingProfile) || EncodingProfile.MAIN.getName().equals(currentEncodingProfile) || EncodingProfile.HIGH.getName()
							.equals(currentEncodingProfile)) {
						newEncodingProfile = EncodingProfile.MAIN.getName();
					} else {
						//If Encoding profile is High 10, it will be set to Main 10
						if (EncodingProfile.HIGH_10.getName().equals(currentEncodingProfile)) {
							newEncodingProfile = EncodingProfile.MAIN_10.getName();
						}
						//If Encoding profile is High 4:2:2, it will be set to Main 4:2:2 10
						if (EncodingProfile.HIGH_422.getName().equals(currentEncodingProfile)) {
							newEncodingProfile = EncodingProfile.MAIN_422_10.getName();
						}
					}
				}
				//Change Codec Algorithm to AVC/H.264, change encoding profile dropdown to the list of AVC
				if (CodecAlgorithm.H_264.getName().equals(value)) {
					newEncodingProfileDropdown = EncodingProfile.namesIsAVG();
					//If Encoding profile is Main, it will be not change
					if (EncodingProfile.MAIN.getName().equals(currentEncodingProfile)) {
						newEncodingProfile = currentEncodingProfile;
					}
					//If Encoding profile is Main 10, it will be set to High 10
					else if (EncodingProfile.MAIN_10.getName().equals(currentEncodingProfile)) {
						newEncodingProfile = EncodingProfile.HIGH_10.getName();
					}
					//If Encoding profile is Main 4:2:2 10, it will be set to High 4:2:2
					else {
						newEncodingProfile = EncodingProfile.HIGH_422.getName();
					}
				}

				AdvancedControllableProperty codecAlgorithmDropdownControlProperty = controlDropdown(extendedStatistics, codecAlgorithmDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, codecAlgorithmDropdownControlProperty);
				AdvancedControllableProperty encodingProfileDropdownControlProperty = controlDropdown(extendedStatistics, newEncodingProfileDropdown,
						property.split(HaivisionConstant.HASH)[0] + HaivisionConstant.HASH + VideoControllingMetric.ENCODING_PROFILE.getName(), newEncodingProfile);
				addAdvanceControlProperties(advancedControllableProperties, encodingProfileDropdownControlProperty);
				break;
			case ENCODING_PROFILE:
				String codecAlgorithmInput = extendedStatistics.get(property.split(HaivisionConstant.HASH)[0] + HaivisionConstant.HASH + VideoControllingMetric.CODEC_ALGORITHM.getName());
				String[] encodingProfileDropdown = EncodingProfile.namesIsAVG();
				if (CodecAlgorithm.H_265.getName().equals(codecAlgorithmInput)) {
					encodingProfileDropdown = EncodingProfile.namesIsHEVC();
				}
				AdvancedControllableProperty encodingProfileControlProperty = controlDropdown(extendedStatistics, encodingProfileDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, encodingProfileControlProperty);
				break;
			case CHROMA_SUBSAMPLING:
				String encodingProfileInput = extendedStatistics.get(property.split(HaivisionConstant.HASH)[0] + HaivisionConstant.HASH + VideoControllingMetric.ENCODING_PROFILE.getName());
				String[] chromaSubsampling;

				//If Encoding profile is Baseline, Main or High; then the Chroma subsampling list includes 4:2:0 8-bit
				if (EncodingProfile.BASELINE.getName().equals(encodingProfileInput) || EncodingProfile.MAIN.getName().equals(encodingProfileInput) || EncodingProfile.HIGH.getName().equals(encodingProfileInput)) {
					chromaSubsampling = ChromaSubSampling.namesIsBaselineOrMainOrHigh();
				}
				//If Encoding profile is Main 10 or High 10, the Chroma subsampling list includes {4:2:0 8-bit; 4:2:0 10-bit}
				else if (EncodingProfile.MAIN_10.getName().equals(encodingProfileInput) || EncodingProfile.HIGH_10.getName().equals(encodingProfileInput)) {
					chromaSubsampling = ChromaSubSampling.namesIsMain10OrHigh10();
				}
				//If Encoding profile is Main 4:2:2 10 or High 4:2:2, the Chroma subsampling list includes {4:2:0 8-bit; 4:2:0 10-bit, 4:2:2 8-bit, 4:2:2 10-bit}
				else {
					chromaSubsampling = ChromaSubSampling.names();
				}
				AdvancedControllableProperty chromaSubsamplingControlProperty = controlDropdown(extendedStatistics, chromaSubsampling, property, value);
				addAdvanceControlProperties(advancedControllableProperties, chromaSubsamplingControlProperty);
				break;
			case RATE_CONTROL:
				String[] rateControlDropdown = RateControlDropdown.names();
				AdvancedControllableProperty rateControlDropdownControlProperty = controlDropdown(extendedStatistics, rateControlDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, rateControlDropdownControlProperty);
				break;
			case BITRATE:
				if (Integer.parseInt(value) < HaivisionConstant.MIN_OF_VIDEO_BITRATE) {
					value = Integer.toString(HaivisionConstant.MIN_OF_VIDEO_BITRATE);
				}
				if (Integer.parseInt(value) > HaivisionConstant.MAX_OF_VIDEO_BITRATE) {
					value = Integer.toString(HaivisionConstant.MAX_OF_VIDEO_BITRATE);
				}
				AdvancedControllableProperty bitrateControlProperty = controlNumeric(extendedStatistics, property,value);
				addAdvanceControlProperties(advancedControllableProperties, bitrateControlProperty);
				break;
			case RESOLUTION:
				String[] resolutionDropdown = ResolutionDropdown.names();
				AdvancedControllableProperty resolutionDropdownControlProperty = controlDropdown(extendedStatistics, resolutionDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, resolutionDropdownControlProperty);

				//If resolution is cropping, then display Cropping dropdown
				if (ResolutionDropdown.checkIsCropping(value)) {
					String[] croppingDropdown = CroppingDropdown.names();
					AdvancedControllableProperty croppingDropdownControlProperty = controlDropdown(extendedStatistics, croppingDropdown, property, value);
					addAdvanceControlProperties(advancedControllableProperties, croppingDropdownControlProperty);
				}
				break;
			case CROPPING:
				String[] croppingDropdown = CroppingDropdown.names();
				AdvancedControllableProperty croppingDropdownControlProperty = controlDropdown(extendedStatistics, croppingDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, croppingDropdownControlProperty);
				break;
			case FRAME_RATE:
				String[] frameRateDropdown = FrameRateDropdown.names();
				AdvancedControllableProperty frameRateDropdownControlProperty = controlDropdown(extendedStatistics, frameRateDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, frameRateDropdownControlProperty);
				break;
			case FRAMING:
				// Please adjust this scope, if framing is I or IB then display Slices dropdown.
				String[] framingDropdown = FramingDropdown.names();
				AdvancedControllableProperty framingDropdownControlProperty = controlDropdown(extendedStatistics, framingDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, framingDropdownControlProperty);
				break;
			case GOP_SIZE:
				if (Integer.parseInt(value) < HaivisionConstant.MIN_OF_VIDEO_GOP_SIZE) {
					value = Integer.toString(HaivisionConstant.MIN_OF_VIDEO_GOP_SIZE);
				}
				if (Integer.parseInt(value) > HaivisionConstant.MAX_OF_VIDEO_GOP_SIZE) {
					value = Integer.toString(HaivisionConstant.MAX_OF_VIDEO_GOP_SIZE);
				}
				AdvancedControllableProperty gopSizeControlProperty = controlNumeric(extendedStatistics, property,value);
				addAdvanceControlProperties(advancedControllableProperties, gopSizeControlProperty);
				break;
			case CLOSED_CAPTION:
				AdvancedControllableProperty closeCaptionControlProperty = controlSwitch(extendedStatistics, property, value, HaivisionConstant.DISABLE, HaivisionConstant.ENABLE);
				addAdvanceControlProperties(advancedControllableProperties, closeCaptionControlProperty);
				break;
			case TIME_CODE_SOURCE:
				//Please adjust this scope, if timeCode Source is System, then display some dropdown.
				String[] timeCodeSourceDropdown = TimeCodeSource.names();
				AdvancedControllableProperty timeCodeSourceDropdownControlProperty = controlDropdown(extendedStatistics, timeCodeSourceDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, timeCodeSourceDropdownControlProperty);
				isEmergencyDelivery = true;
				break;
			case ASPECT_RATIO:
				String[] aspectRatioDropdown = AspectRatioDropdown.names();
				AdvancedControllableProperty aspectRatioDropdownControlProperty = controlDropdown(extendedStatistics, aspectRatioDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, aspectRatioDropdownControlProperty);
				break;
			case ACTION:
				String[] actionDropdown = HaivisionConstant.START_AUDIO_VIDEO;
				if (HaivisionConstant.START.equalsIgnoreCase(value)) {
					actionDropdown = HaivisionConstant.STOP_AUDIO_VIDEO;
				}
				AdvancedControllableProperty actionDropdownControlProperty = controlDropdownAcceptNoneValue(extendedStatistics, actionDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, actionDropdownControlProperty);
				break;
			case APPLY_CHANGE:
				String videoName = property.split(HaivisionConstant.HASH)[0];
				VideoResponse videoResponse = new VideoResponse();
				//Please continue set the fields are missing
				videoResponse.setAspectRatio(extendedStatistics.get(videoName + HaivisionConstant.HASH + VideoControllingMetric.ASPECT_RATIO.getName()));
				videoResponse.setBitrate(extendedStatistics.get(videoName + HaivisionConstant.HASH + VideoControllingMetric.BITRATE.getName()));
				videoResponse.setChromaSubSampling(extendedStatistics.get(videoName + HaivisionConstant.HASH + VideoControllingMetric.CHROMA_SUBSAMPLING.getName()));
				videoResponse.setClosedCaption(extendedStatistics.get(videoName + HaivisionConstant.HASH + VideoControllingMetric.CLOSED_CAPTION.getName()));
				videoResponse.setCodecAlgorithm(extendedStatistics.get(videoName + HaivisionConstant.HASH + VideoControllingMetric.CODEC_ALGORITHM.getName()));
				videoResponse.setCropping(extendedStatistics.get(videoName + HaivisionConstant.HASH + VideoControllingMetric.CROPPING.getName()));
				videoResponse.setEncodingProfile(extendedStatistics.get(videoName + HaivisionConstant.HASH + VideoControllingMetric.ENCODING_PROFILE.getName()));
				videoResponse.setGopSize(extendedStatistics.get(videoName + HaivisionConstant.HASH + VideoControllingMetric.GOP_SIZE.getName()));
				videoResponse.setInputInterface(extendedStatistics.get(videoName + HaivisionConstant.HASH + VideoControllingMetric.INPUT.getName()));
				videoResponse.setResolution(extendedStatistics.get(videoName + HaivisionConstant.HASH + VideoControllingMetric.RESOLUTION.getName()));
				videoResponse.setTimeCode(extendedStatistics.get(videoName + HaivisionConstant.HASH + VideoControllingMetric.TIME_CODE_SOURCE.getName()));
				videoResponse.setId(videoNameToId.get(videoName));

				// Send request to apply all change for all metric
				setVideoApplyChange(videoResponse.toString(), audioNameToId.get(videoName));

				// Send request to action for the metric
				if (AudioStateDropdown.STOPPED.getName().equalsIgnoreCase(value)) {
					changeVideoAction(HaivisionConstant.STOP_ACTION, videoNameToId.get(videoName), videoResponse.toString());
				} else {
					changeVideoAction(HaivisionConstant.START_ACTION, videoNameToId.get(videoName), videoResponse.toString());
				}
				isEmergencyDelivery = false;
				break;
			case CANCEL:
				isEmergencyDelivery = false;
				break;
			default:
				break;
		}
		//Editing
		if (isEmergencyDelivery) {
			propertyName = property.split(HaivisionConstant.HASH)[0];
			extendedStatistics.put(propertyName + HaivisionConstant.HASH + VideoControllingMetric.APPLY_CHANGE.getName(), "");
			advancedControllableProperties.add(createButton(propertyName + HaivisionConstant.HASH + VideoControllingMetric.APPLY_CHANGE.getName(), HaivisionConstant.APPLY, "Applying", 0));

			extendedStatistics.put(propertyName + HaivisionConstant.HASH + HaivisionConstant.EDITED, HaivisionConstant.TRUE);
			extendedStatistics.put(propertyName + HaivisionConstant.HASH + HaivisionConstant.CANCEL, "");
			advancedControllableProperties.add(createButton(propertyName + HaivisionConstant.HASH + HaivisionConstant.CANCEL, HaivisionConstant.CANCEL, HaivisionConstant.CANCEL, 0));
		}
		localExtendedStatistics.setStatistics(extendedStatistics);
		localExtendedStatistics.setControllableProperties(advancedControllableProperties);
	}

	/**
	 * Save video apply change
	 *
	 * @param data the data is request body
	 * @param urlId the urlId is id of video encoder
	 */
	private void setVideoApplyChange(String data, String urlId) {
		try {
			String responseData = doPut(HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER) + HaivisionConstant.SLASH + urlId, data);
			if (StringUtils.isNullOrEmpty(responseData)) {
				throw new ResourceNotReachableException(HaivisionConstant.NO_SET_ERROR_VIDEO);
			}
		} catch (Exception e) {
			throw new ResourceNotReachableException(HaivisionConstant.ERR_SET_CONTROL + e.getMessage());
		}
	}

	/**
	 * Change action for video encoder
	 *
	 * @param action the action is state of video encoder
	 * @param urlId the urlId is id of video encoder
	 * @param data the data is request body
	 */
	private void changeVideoAction(String action, String urlId, String data) {
		try {
			String responseData = doPut(HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER) + HaivisionConstant.SLASH + urlId + HaivisionConstant.SLASH + action, data);
			if (StringUtils.isNullOrEmpty(responseData)) {
				throw new ResourceNotReachableException(HaivisionConstant.NO_CHANGE_ACTION_ERROR);
			}
		} catch (Exception e) {
			throw new ResourceNotReachableException(HaivisionConstant.ERR_SET_CONTROL + e.getMessage());
		}
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
			sessionID = null;
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
			for (VideoResponse videoResponse : videoResponseList) {
				addVideoDataControlToProperty(stats, videoResponse, advancedControllableProperties);
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
		Map<Integer, String> bitRateMap = BitRateDropdown.getNameToValueMap();
		Map<Integer, String> sampleRateMap = SampleRateDropdown.getNameToValueMap();
		Map<Integer, String> algorithmDropdown = AlgorithmDropdown.getNameToValueMap();
		Map<String, String> languageDropdown = LanguageDropdown.getNameToValueMap();
		Map<Integer, String> inputMap = InputDropdown.getNameToValueMap();
		String[] dropdownInput = InputDropdown.names();
		String[] dropdownMode = ChannelModeDropdown.names();
		String[] dropdownAlgorithm = AlgorithmDropdown.names();
		String[] dropdownSampleRate = SampleRateDropdown.names();
		String[] dropdownLanguage = LanguageDropdown.names();
		String[] dropdownAction = HaivisionConstant.START_AUDIO_VIDEO;
		String[] dropdownBitRate = BitRateDropdown.namesIsMono();
		String value;

		for (AudioControllingMetric audioMetric : AudioControllingMetric.values()) {
			switch (audioMetric) {
				case STATE:
					String stateAudio = audioResponseList.getState();
					value = getNameByValue(stateAudio, audioMap);
					stats.put(audioName + HaivisionConstant.HASH + audioMetric.getName(), value);
					break;
				case INPUT:
					String input = audioResponseList.getInterfaceName();
					value = getNameByValue(input, inputMap);
					AdvancedControllableProperty inputDropdownControlProperty = controlDropdown(stats, dropdownInput, audioName + HaivisionConstant.HASH + audioMetric.getName(), value);
					addAdvanceControlProperties(advancedControllableProperties, inputDropdownControlProperty);
					break;
				case CHANGE_MODE:
					String changeMode = audioResponseList.getMode();
					value = getNameByValue(changeMode, channelModeMap);
					AdvancedControllableProperty channelModeControlProperty = controlDropdown(stats, dropdownMode, audioName + HaivisionConstant.HASH + audioMetric.getName(), value);
					addAdvanceControlProperties(advancedControllableProperties, channelModeControlProperty);
					break;
				case BITRATE:
					String bitRate = audioResponseList.getBitRate();
					value = getNameByValue(bitRate, bitRateMap);
					String mode = audioResponseList.getMode();
					mode = HaivisionConstant.NONE.equals(audioResponseList.getMode()) ? mode : channelModeMap.get(Integer.parseInt(audioResponseList.getMode()));
					if (mode.equals(ChannelModeDropdown.STEREO.getName())) {
						dropdownBitRate = BitRateDropdown.namesIsStereo();
					}
					AdvancedControllableProperty bitRateControlProperty = controlDropdown(stats, dropdownBitRate, audioName + HaivisionConstant.HASH + audioMetric.getName(), value);
					addAdvanceControlProperties(advancedControllableProperties, bitRateControlProperty);
					break;
				case SAMPLE_RATE:
					String sampleRate = audioResponseList.getSampleRate();
					value = getNameByValue(sampleRate, sampleRateMap);
					AdvancedControllableProperty samPleRateControlProperty = controlDropdown(stats, dropdownSampleRate, audioName + HaivisionConstant.HASH + audioMetric.getName(), value);
					addAdvanceControlProperties(advancedControllableProperties, samPleRateControlProperty);
					break;
				case ALGORITHM:
					String algorithm = audioResponseList.getAlgorithm();
					value = getNameByValue(algorithm, algorithmDropdown);
					AdvancedControllableProperty algorithmControlProperty = controlDropdown(stats, dropdownAlgorithm, audioName + HaivisionConstant.HASH + audioMetric.getName(), value);
					addAdvanceControlProperties(advancedControllableProperties, algorithmControlProperty);
					break;
				case LANGUAGE:
					String language = audioResponseList.getLang();
					value = languageDropdown.get(language);
					AdvancedControllableProperty languageControlProperty = controlDropdownAcceptNoneValue(stats, dropdownLanguage, audioName + HaivisionConstant.HASH + audioMetric.getName(), value);
					addAdvanceControlProperties(advancedControllableProperties, languageControlProperty);
					break;
				case ACTION:
					stateAudio = audioResponseList.getState();
					value = getNameByValue(stateAudio, audioMap);
					//define action = Start
					String action = HaivisionConstant.START;
					if (AudioStateDropdown.STOPPED.getName().equals(value)) {
						dropdownAction = HaivisionConstant.STOP_AUDIO_VIDEO;
						//action = Stop
						action = HaivisionConstant.STOP;
					}
					AdvancedControllableProperty actionDropdownControlProperty = controlDropdownAcceptNoneValue(stats, dropdownAction, audioName + HaivisionConstant.HASH + audioMetric.getName(), action);
					addAdvanceControlProperties(advancedControllableProperties, actionDropdownControlProperty);
					break;
				default:
					break;
			}
		}
	}

	/**
	 * Add Output Stream data to  property
	 *
	 * @param stats list statistics property
	 * @param videoResponseList list of video response
	 * @param advancedControllableProperties the advancedControllableProperties is advancedControllableProperties instance
	 */
	private void addVideoDataControlToProperty(Map<String, String> stats, VideoResponse videoResponseList, List<AdvancedControllableProperty> advancedControllableProperties) {
		String videoName = videoResponseList.getName();
		Map<Integer, String> videoStateMap = VideoStateDropdown.getNameToValueMap();
		Map<Integer, String> codecAlgorithmMap = CodecAlgorithm.getNameToValueMap();
		Map<Integer, String> croppingMap = CroppingDropdown.getNameToValueMap();
		Map<Integer, String> framingMap = FramingDropdown.getNameToValueMap();
		Map<Integer, String> timeCodeSourceMap = TimeCodeSource.getNameToValueMap();
		Map<Integer, String> aspectRatioMap = AspectRatioDropdown.getNameToValueMap();
		Map<Integer, String> inputMap = InputDropdown.getNameToValueMap();
		Map<Integer, String> encodingProfileMap = EncodingProfile.getNameToValueMap();
		Map<Integer, String> chromaSubSamplingMap = ChromaSubSampling.getNameToValueMap();
		Map<Integer, String> rateControlMap = RateControlDropdown.getNameToValueMap();
		String[] dropdownCodecAlgorithm = CodecAlgorithm.names();
		String[] dropdownEncodingProfile = EncodingProfile.namesIsAVG();
		String[] dropdownChromaSubSampling = ChromaSubSampling.namesIsBaselineOrMainOrHigh();
		String[] dropdownRateControl = RateControlDropdown.names();
		String[] dropdownResolution = ResolutionDropdown.names();
		String[] dropdownCropping = CroppingDropdown.names();
		String[] dropdownFraming = FramingDropdown.names();
		String[] dropdownTimeCodeSource = TimeCodeSource.names();
		String[] dropdownAspectRatio = AspectRatioDropdown.names();
		String[] inputDropdown = InputDropdown.names();
		String[] frameRateDropdown = FrameRateDropdown.names();
		List<String> resolutionNotCropping = ResolutionDropdown.getDropdownListNotCropping();
		String[] dropdownAction = HaivisionConstant.START_AUDIO_VIDEO;
		String value;
		for (VideoControllingMetric videoMetric : VideoControllingMetric.values()) {
			switch (videoMetric) {
				case STATE:
					String stateVideo = videoResponseList.getState();
					value = getNameByValue(stateVideo, videoStateMap);
					stats.put(videoName + HaivisionConstant.HASH + videoMetric.getName(), value);
					break;
				case INPUT_FORMAT:
					value = videoResponseList.getInputFormat();
					if (HaivisionConstant.NONE.equals(value)) {
						value = videoResponseList.getResolution();
					}
					stats.put(videoName + HaivisionConstant.HASH + videoMetric.getName(), value);
					break;
				case CODEC_ALGORITHM:
					String codecAlgorithm = videoResponseList.getCodecAlgorithm();
					value = getNameByValue(codecAlgorithm, codecAlgorithmMap);
					AdvancedControllableProperty languageControlProperty = controlDropdown(stats, dropdownCodecAlgorithm, videoName + HaivisionConstant.HASH + videoMetric.getName(), value);
					addAdvanceControlProperties(advancedControllableProperties, languageControlProperty);
					break;
				case ENCODING_PROFILE:
					String codecAlgorithmMode = videoResponseList.getCodecAlgorithm();
					String valueAlgorithm = getNameByValue(codecAlgorithmMode, codecAlgorithmMap);
					String encodingProfile = videoResponseList.getEncodingProfile();
					value = getNameByValue(encodingProfile, encodingProfileMap);
					if (CodecAlgorithm.H_265.getName().equals(valueAlgorithm)) {
						dropdownEncodingProfile = EncodingProfile.namesIsHEVC();
					}
					AdvancedControllableProperty encodingProfileControlProperty = controlDropdown(stats, dropdownEncodingProfile, videoName + HaivisionConstant.HASH + videoMetric.getName(), value);
					addAdvanceControlProperties(advancedControllableProperties, encodingProfileControlProperty);
					break;
				case CHROMA_SUBSAMPLING:
					String encodingProfileMode = videoResponseList.getEncodingProfile();
					String valueEncodingProfileMode = getNameByValue(encodingProfileMode, encodingProfileMap);
					if (EncodingProfile.MAIN_10.getName().equals(valueEncodingProfileMode) || EncodingProfile.HIGH_10.getName().equals(valueEncodingProfileMode)) {
						dropdownChromaSubSampling = ChromaSubSampling.namesIsMain10OrHigh10();
					}
					if (EncodingProfile.MAIN_422_10.getName().equals(valueEncodingProfileMode) || EncodingProfile.HIGH_422.getName().equals(valueEncodingProfileMode)) {
						dropdownChromaSubSampling = ChromaSubSampling.names();
					}
					String chromaSubSampling = videoResponseList.getChromaSubSampling();
					value = getNameByValue(chromaSubSampling, chromaSubSamplingMap);
					AdvancedControllableProperty chromaSubSamplingProfileProperty = controlDropdown(stats, dropdownChromaSubSampling, videoName + HaivisionConstant.HASH + videoMetric.getName(), value);
					addAdvanceControlProperties(advancedControllableProperties, chromaSubSamplingProfileProperty);
					break;
				case RATE_CONTROL:
					String rateControl = videoResponseList.getRateControl();
					value = getNameByValue(rateControl, rateControlMap);
					AdvancedControllableProperty rateControlProperty = controlDropdown(stats, dropdownRateControl, videoName + HaivisionConstant.HASH + videoMetric.getName(), value);
					addAdvanceControlProperties(advancedControllableProperties, rateControlProperty);
					break;
				case MAX_BITRATE:
					String rateControlMode = videoResponseList.getRateControl();
					String valueRateControl = getNameByValue(rateControlMode, rateControlMap);
					if (RateControlDropdown.CVBR.getName().equals(valueRateControl)) {
						String matBitRate = videoResponseList.getMaxBitrate();
						AdvancedControllableProperty maxBitRateProperty = controlNumeric(stats, videoName + HaivisionConstant.HASH + videoMetric.getName(), matBitRate);
						addAdvanceControlProperties(advancedControllableProperties, maxBitRateProperty);
					}
					break;
				case BITRATE:
					String bitRate = videoResponseList.getBitrate();
					AdvancedControllableProperty bitRateProperty = controlNumeric(stats, videoName + HaivisionConstant.HASH + videoMetric.getName(), bitRate);
					addAdvanceControlProperties(advancedControllableProperties, bitRateProperty);
					break;
				case RESOLUTION:
					String resolution = videoResponseList.getResolution();
					if (HaivisionConstant.INPUT_AUTO.equals(resolution)) {
						resolution = videoResponseList.getVideoStatistic().getInputFormat();
					}
					AdvancedControllableProperty resolutionControlProperty = controlDropdown(stats, dropdownResolution, videoName + HaivisionConstant.HASH + videoMetric.getName(), resolution);
					addAdvanceControlProperties(advancedControllableProperties, resolutionControlProperty);
					break;
				case CROPPING:
					String resolutionMode = videoResponseList.getResolution();
					if (HaivisionConstant.INPUT_AUTO.equals(resolutionMode)) {
						resolutionMode = videoResponseList.getVideoStatistic().getInputFormat();
					}
					if (!resolutionNotCropping.contains(resolutionMode)) {
						String cropping = videoResponseList.getCropping();
						value = getNameByValue(cropping, croppingMap);
						AdvancedControllableProperty croppingControlProperty = controlDropdown(stats, dropdownCropping, videoName + HaivisionConstant.HASH + videoMetric.getName(), value);
						addAdvanceControlProperties(advancedControllableProperties, croppingControlProperty);
					}
					break;
				case FRAME_RATE:
					String frameRate = videoResponseList.getFrameRate();
					if (frameRate.equals("0")) {
						frameRate = FrameRateDropdown.FAME_RATE_0.getValue();
					}
					AdvancedControllableProperty croppingControlProperty = controlDropdown(stats, frameRateDropdown, videoName + HaivisionConstant.HASH + videoMetric.getName(), frameRate);
					addAdvanceControlProperties(advancedControllableProperties, croppingControlProperty);
					break;
				case FRAMING:
					String framing = videoResponseList.getGopStructure();
					value = getNameByValue(framing, framingMap);
					AdvancedControllableProperty framingProperty = controlDropdown(stats, dropdownFraming, videoName + HaivisionConstant.HASH + videoMetric.getName(), value);
					addAdvanceControlProperties(advancedControllableProperties, framingProperty);
					break;
				case GOP_SIZE:
					String gopSize = videoResponseList.getGopSize();
					AdvancedControllableProperty gopSizeProperty = controlNumeric(stats, videoName + HaivisionConstant.HASH + videoMetric.getName(), gopSize);
					addAdvanceControlProperties(advancedControllableProperties, gopSizeProperty);
					break;
				case CLOSED_CAPTION:
					String closedCaption = videoResponseList.getClosedCaption();
					AdvancedControllableProperty closedCaptionProperty = controlSwitch(stats, videoName + HaivisionConstant.HASH + videoMetric.getName(), closedCaption, "Disable", "Enable");
					addAdvanceControlProperties(advancedControllableProperties, closedCaptionProperty);
					break;
				case TIME_CODE_SOURCE:
					String timeCodeSource = videoResponseList.getTimeCode();
					value = getNameByValue(timeCodeSource, timeCodeSourceMap);
					AdvancedControllableProperty timeCodeSourceProperty = controlDropdownAcceptNoneValue(stats, dropdownTimeCodeSource, videoName + HaivisionConstant.HASH + videoMetric.getName(), value);
					addAdvanceControlProperties(advancedControllableProperties, timeCodeSourceProperty);
					break;
				case ASPECT_RATIO:
					String aspectRatio = videoResponseList.getAspectRatio();
					value = getNameByValue(aspectRatio, aspectRatioMap);
					AdvancedControllableProperty aspectRatioProperty = controlDropdown(stats, dropdownAspectRatio, videoName + HaivisionConstant.HASH + videoMetric.getName(), value);
					addAdvanceControlProperties(advancedControllableProperties, aspectRatioProperty);
					break;
				case ACTION:
					stateVideo = videoResponseList.getState();
					value = getNameByValue(stateVideo, videoStateMap);
					String action = HaivisionConstant.START;
					if (VideoStateDropdown.STOPPED.getName().equals(value)) {
						dropdownAction = HaivisionConstant.STOP_AUDIO_VIDEO;
						action = HaivisionConstant.STOP;
					}
					AdvancedControllableProperty actionDropdownControlProperty = controlDropdownAcceptNoneValue(stats, dropdownAction, videoName + HaivisionConstant.HASH + videoMetric.getName(), action);
					addAdvanceControlProperties(advancedControllableProperties, actionDropdownControlProperty);
					break;
				case INPUT:
					String input = videoResponseList.getInputInterface();
					value = getNameByValue(input, inputMap);
					AdvancedControllableProperty inputDropdownControlProperty = controlDropdown(stats, inputDropdown, videoName + HaivisionConstant.HASH + videoMetric.getName(), value);
					addAdvanceControlProperties(advancedControllableProperties, inputDropdownControlProperty);
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
			for (AdvancedControllableProperty controllableProperty : advancedControllableProperties) {
				if (controllableProperty.getName().equals(property.getName())) {
					advancedControllableProperties.remove(controllableProperty);
					break;
				}
			}
			advancedControllableProperties.add(property);
		}
	}

	/**
	 * Get name by value of Map<Integer, String>
	 *
	 * @return String is the value
	 */
	private String getNameByValue(String value, Map<Integer, String> nameMap) {
		try {
			return nameMap.get(Integer.parseInt(value));
		} catch (Exception e) {
			return HaivisionConstant.NONE;
		}
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
			for (AudioResponse audioItem : audioResponse.getData()) {
				audioResponseList.add(audioItem);
				audioNameToId.put(audioItem.getName(), audioItem.getId());
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

			for (VideoResponse videoItem : videoResponse.getData()) {
				videoResponseList.add(videoItem);
				videoNameToId.put(videoItem.getName(), videoItem.getId());
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
			for (OutputResponse outputItem : outputResponse.getData()) {
				outputResponseList.add(outputItem);
				streamNameToId.put(outputItem.getName(), outputItem.getId());
			}
		} catch (Exception e) {
			failedMonitor.put(HaivisionURL.OUTPUT_ENCODER.getName(), e.getMessage());
		}
	}

	/**
	 * Retrieve system information status encoder
	 */
	private void retrieveSystemInfoStatus(Map<String, String> stats) {
		try {
			// /apis/status
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
	private AdvancedControllableProperty controlDropdownAcceptNoneValue(Map<String, String> stats, String[] options, String name, String value) {
		stats.put(name, value);
		return createDropdown(name, options, HaivisionConstant.NONE);
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

	/**
	 * Create Numeric is control property for metric
	 *
	 * @param name the name of the property
	 * @param initialValue the initialValue is number
	 * @return AdvancedControllableProperty Numeric instance
	 */
	private AdvancedControllableProperty createNumeric(String name, String initialValue) {
		AdvancedControllableProperty.Numeric numeric = new AdvancedControllableProperty.Numeric();

		return new AdvancedControllableProperty(name, new Date(), numeric, initialValue);
	}

	/**
	 * Add Numeric is control property for metric
	 *
	 * @param stats list statistic
	 * @param name String name of metric
	 * @return AdvancedControllableProperty Numeric instance
	 */
	private AdvancedControllableProperty controlNumeric(Map<String, String> stats, String name, String value) {
		stats.put(name, value);
		return createNumeric(name, value);
	}

	/**
	 * Create switch is control property for metric
	 *
	 * @param name the name of property
	 * @param status initial status (0|1)
	 * @return AdvancedControllableProperty switch instance
	 */
	private AdvancedControllableProperty createSwitch(String name, int status, String labelOff, String labelOn) {
		AdvancedControllableProperty.Switch toggle = new AdvancedControllableProperty.Switch();
		toggle.setLabelOff(labelOff);
		toggle.setLabelOn(labelOn);

		AdvancedControllableProperty advancedControllableProperty = new AdvancedControllableProperty();
		advancedControllableProperty.setName(name);
		advancedControllableProperty.setValue(status);
		advancedControllableProperty.setType(toggle);
		advancedControllableProperty.setTimestamp(new Date());

		return advancedControllableProperty;
	}

	/**
	 * Add switch is control property for metric
	 *
	 * @param stats list statistic
	 * @param name String name of metric
	 * @return AdvancedControllableProperty switch instance
	 */
	private AdvancedControllableProperty controlSwitch(Map<String, String> stats, String name, String value, String labelOff, String labelOn) {
		stats.put(name, value);
		if (!HaivisionConstant.NONE.equals(value)) {
			return createSwitch(name, Integer.parseInt(value), labelOff, labelOn);
		}
		return null;
	}

	/**
	 * Save audio apply change
	 *
	 * @param data the data is request body
	 * @param urlId the urlId is id of audio encoder
	 */
	private void setAudioApplyChange(String data, String urlId) {
		try {
			JsonNode responseData = doPut(HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER) + HaivisionConstant.SLASH + urlId, data, JsonNode.class);
			if (responseData.get("info") == null) {
				throw new ResourceNotReachableException(HaivisionConstant.NO_SET_ERROR_AUDIO);
			}
		} catch (Exception e) {
			throw new ResourceNotReachableException(HaivisionConstant.ERR_SET_CONTROL + e.getMessage());
		}
	}

	/**
	 * Change action for audio encoder
	 *
	 * @param action the action is state of audio encoder
	 * @param urlId the urlId is id of audio encoder
	 * @param data the data is request body
	 */
	private void changeAudioAction(String action, String urlId, String data) {
		try {
			JsonNode responseData = doPut(HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.AUDIO_ENCODER) + HaivisionConstant.SLASH + urlId + HaivisionConstant.SLASH + action, data, JsonNode.class);
			if (responseData.get("info") == null) {
				throw new ResourceNotReachableException(HaivisionConstant.NO_CHANGE_ACTION_ERROR);
			}
		} catch (Exception e) {
			throw new ResourceNotReachableException(HaivisionConstant.ERR_SET_CONTROL + e.getMessage());
		}
	}
}