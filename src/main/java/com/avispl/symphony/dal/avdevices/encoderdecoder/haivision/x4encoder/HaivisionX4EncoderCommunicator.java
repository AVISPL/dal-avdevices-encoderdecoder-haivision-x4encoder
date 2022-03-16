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
import java.util.stream.Collectors;

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
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.CountingDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.CreateOutputStreamMetric;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.CroppingDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.DropdownList;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.EncodingProfile;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.EncryptionDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.FrameRateDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.FramingDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.InputDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.LanguageDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.OutputStateDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.ProtocolDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.RateControlDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.ReSyncHourDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.ResolutionDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.SRTModeDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.SampleRateDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.SlicesDropdown;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.TimeCodeSource;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.TimingAndShaping;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist.VideoDropdown;
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
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.output.OutputSAP;
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
 * @author Ivan / Symphony Dev Team<br>
 * Created on 3/8/2022
 * @since 1.0.0
 */
public class HaivisionX4EncoderCommunicator extends RestCommunicator implements Monitorable, Controller {

	private String sessionID;
	private String roleBased;
	private boolean isAdapterFilter;
	private Integer countMonitoringNumber = null;
	private ExtendedStatistics localExtendedStatistics;
	private ExtendedStatistics localCreateOutputStream;
	private Map<String, String> failedMonitor = new HashMap<>();
	private boolean isEmergencyDelivery;
	private boolean isCreateStreamCalled;

	private final String uuidDay = UUID.randomUUID().toString().replace(HaivisionConstant.DASH, "");

	//The properties adapter
	private String streamNameFilter;
	private String portNumberFilter;
	private String streamStatusFilter;

	private final List<String> streamNameList = new ArrayList<>();
	private final List<Integer> portNumberList = new ArrayList<>();
	private final List<String> portNumberRangeList = new ArrayList<>();
	private final List<String> streamStatusList = new ArrayList<>();

	private Map<String, AudioResponse> audioNameToAudioResponse = new HashMap<>();
	private Map<String, VideoResponse> videoNameToVideoResponse = new HashMap<>();
	private Map<String, OutputResponse> streamNameToStreamResponse = new HashMap<>();
	private Map<String, Map<String, Audio>> sourceAudioResponse = new HashMap<>();
	private Map<String, Audio> sourceAudio = new HashMap<>();
	private Map<String, String> statsStreamOutput = new HashMap<>();

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
		Map<String, String> statsCreateOutputStream = new HashMap<>();
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

		if (localCreateOutputStream == null) {
			localCreateOutputStream = new ExtendedStatistics();
		}

		if (!isEmergencyDelivery) {
			isAdapterFiltering();
			populateInformationFromDevice(stats, advancedControllableProperties);
			if (HaivisionConstant.OPERATOR.equals(roleBased) || HaivisionConstant.ADMIN.equals(roleBased)) {
				extendedStatistics.setControllableProperties(advancedControllableProperties);
			}
			extendedStatistics.setStatistics(stats);
			localExtendedStatistics = extendedStatistics;
		}

		if (HaivisionConstant.ADMIN.equals(roleBased) || HaivisionConstant.OPERATOR.equals(roleBased) && !isCreateStreamCalled) {
			List<AdvancedControllableProperty> createStreamAdvancedControllable = new ArrayList<>();
			streamCreateOutput(statsCreateOutputStream, createStreamAdvancedControllable);
			localCreateOutputStream.setStatistics(statsCreateOutputStream);
			localCreateOutputStream.setControllableProperties(createStreamAdvancedControllable);
			statsStreamOutput.putAll(statsCreateOutputStream);
			isCreateStreamCalled = true;
		}

		if (isCreateStreamCalled) {
			stats.putAll(localCreateOutputStream.getStatistics());
			Map<String, String> currentStat = localExtendedStatistics.getStatistics();
			Map<String, String> createOutputStat = localCreateOutputStream.getStatistics();
			currentStat.putAll(createOutputStat);
			List<AdvancedControllableProperty> currentProps = localExtendedStatistics.getControllableProperties();

			List<AdvancedControllableProperty> newProps = localCreateOutputStream.getControllableProperties();
			List<String> newPropNames = newProps.stream().map(AdvancedControllableProperty::getName).collect(Collectors.toList());

			currentProps.removeIf(item -> newPropNames.contains(item.getName()));
			currentProps.addAll(new ArrayList<>(newProps));
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
		Map<String, String> updateCreateOutputStream = localCreateOutputStream.getStatistics();
		Map<String, String> extendedStatistics = localExtendedStatistics.getStatistics();
		List<AdvancedControllableProperty> advancedControllableProperties = localExtendedStatistics.getControllableProperties();
		List<AdvancedControllableProperty> advancedControllableCreateOutputProperties = localCreateOutputStream.getControllableProperties();

		if (property.contains(HaivisionConstant.STREAM_CREATE_OUTPUT)) {
			controlStreamCreateOutput(property, value, updateCreateOutputStream, advancedControllableCreateOutputProperties);
			statsStreamOutput.putAll(updateCreateOutputStream);
			return;
		}

		String propertiesAudioAndVideo = property.substring(0, HaivisionConstant.AUDIO.length());
		if (HaivisionConstant.AUDIO.equals(propertiesAudioAndVideo)) {
			controlAudioProperty(property, value, extendedStatistics, advancedControllableProperties);
		}
		if (HaivisionConstant.VIDEO.equals(propertiesAudioAndVideo)) {
			controlVideoProperty(property, value, extendedStatistics, advancedControllableProperties);
		}
		String propertiesStream = property.substring(0, HaivisionConstant.STREAM.length());
		if (HaivisionConstant.STREAM.equals(propertiesStream)) {
			controlStreamProperty(property, value, extendedStatistics, advancedControllableProperties);
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
		String[] audioProperty = property.split(HaivisionConstant.HASH);
		String audioName = audioProperty[0];
		String propertyName = audioProperty[1];
		AudioControllingMetric audioControllingMetric = AudioControllingMetric.getByName(propertyName);
		isEmergencyDelivery = true;
		switch (audioControllingMetric) {
			case INPUT:
				String[] inputDropdown = DropdownList.Names(InputDropdown.class);
				AdvancedControllableProperty inputDropdownControlProperty = controlDropdown(extendedStatistics, inputDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, inputDropdownControlProperty);
				break;
			case CHANGE_MODE:
				String nameIsBitRate = AudioControllingMetric.BITRATE.getName();
				String[] channelModeDropdown = DropdownList.Names(ChannelModeDropdown.class);
				String currentBitRateValue = extendedStatistics.get(audioName + HaivisionConstant.HASH + nameIsBitRate);
				String newBitRateValue = "";
				Map<String, Integer> bitRateNameModeMap = BitRateDropdown.getValueToNameMap();
				Map<Integer, String> bitRateValueModeMap = BitRateDropdown.getNameToValueMap();
				String nameIsStereo = ChannelModeDropdown.STEREO.getName();
				if (nameIsStereo.equals(value)) {
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
				if (nameIsStereo.equals(value)) {
					newBitRateDropdown = BitRateDropdown.namesIsStereo();
				}
				AdvancedControllableProperty bitRateDropdownControlProperty = controlDropdown(extendedStatistics, newBitRateDropdown,
						audioName + HaivisionConstant.HASH + nameIsBitRate, newBitRateValue);
				AdvancedControllableProperty channelModeDropdownControlProperty = controlDropdown(extendedStatistics, channelModeDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, bitRateDropdownControlProperty);
				addAdvanceControlProperties(advancedControllableProperties, channelModeDropdownControlProperty);
				break;
			case SAMPLE_RATE:
				String[] sampleRateDropdown = DropdownList.Names(SampleRateDropdown.class);
				AdvancedControllableProperty sampleRateDropdownControlProperty = controlDropdown(extendedStatistics, sampleRateDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, sampleRateDropdownControlProperty);
				break;
			case ALGORITHM:
				String[] algorithmsDropdown = DropdownList.Names(AlgorithmDropdown.class);
				AdvancedControllableProperty algorithmsControlProperty = controlDropdown(extendedStatistics, algorithmsDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, algorithmsControlProperty);
				break;
			case LANGUAGE:
				String[] languageDropdown = DropdownList.Names(LanguageDropdown.class);
				AdvancedControllableProperty languageDropdownControlProperty = controlDropdownAcceptNoneValue(extendedStatistics, languageDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, languageDropdownControlProperty);
				break;
			case BITRATE:
				String valueInput = extendedStatistics.get(audioName + HaivisionConstant.HASH + AudioControllingMetric.CHANGE_MODE.getName());
				String[] bitRateDropdown = BitRateDropdown.namesIsMono();
				if (ChannelModeDropdown.STEREO.getName().equalsIgnoreCase(valueInput)) {
					bitRateDropdown = BitRateDropdown.namesIsStereo();
				}
				AdvancedControllableProperty bitRateControlProperty = controlDropdown(extendedStatistics, bitRateDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, bitRateControlProperty);
				break;
			case ACTION:
				for (AdvancedControllableProperty advancedControllableProperty : advancedControllableProperties) {
					if (advancedControllableProperty.getName().equals(property)) {
						extendedStatistics.put(property, value);
						advancedControllableProperty.setValue(value);
						break;
					}
				}
				break;
			case APPLY_CHANGE:
				AudioResponse audioResponse = convertAudioByValue(extendedStatistics, audioName);

				// sent request to apply all change for all metric
				setAudioApplyChange(audioResponse.payLoad(), audioResponse.getId());

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
			propertyName = audioName;
			extendedStatistics.put(propertyName + HaivisionConstant.HASH + AudioControllingMetric.APPLY_CHANGE.getName(), "");
			advancedControllableProperties.add(createButton(propertyName + HaivisionConstant.HASH + AudioControllingMetric.APPLY_CHANGE.getName(), HaivisionConstant.APPLY, HaivisionConstant.APPLY, 0));

			extendedStatistics.put(propertyName + HaivisionConstant.HASH + HaivisionConstant.EDITED, HaivisionConstant.TRUE);
			extendedStatistics.put(propertyName + HaivisionConstant.HASH + HaivisionConstant.CANCEL, "");
			advancedControllableProperties.add(createButton(propertyName + HaivisionConstant.HASH + HaivisionConstant.CANCEL, HaivisionConstant.CANCEL, HaivisionConstant.CANCEL, 0));
		}
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

		if (!HaivisionConstant.NONE.equals(action)) {
			changeAudioAction(action.toLowerCase(), audioResponse.getId(), audioResponse.payLoad());
		}
	}

	/**
	 * Change AudioResponse by value
	 *
	 * @param extendedStatistics list extendedStatistics
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
		AudioResponse audio = audioNameToAudioResponse.get(audioName);
		String id = "";
		if (audio != null) {
			id = audio.getId();
		}
		audioResponse.setId(id);

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
		String[] videoProperty = property.split(HaivisionConstant.HASH);
		String videoName = videoProperty[0];
		String propertyName = videoProperty[1];
		VideoControllingMetric videoControllingMetric = VideoControllingMetric.getByName(propertyName);
		isEmergencyDelivery = true;
		switch (videoControllingMetric) {
			case INPUT:
				String[] videoInputDropdown = DropdownList.Names(VideoInputDropdown.class);
				AdvancedControllableProperty videoInputDropdownControlProperty = controlDropdown(extendedStatistics, videoInputDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, videoInputDropdownControlProperty);
				break;
			case CODEC_ALGORITHM:
				String nameIsCodecAlgorithm = VideoControllingMetric.ENCODING_PROFILE.getName();
				String[] codecAlgorithmDropdown = DropdownList.Names(CodecAlgorithm.class);
				String[] newEncodingProfileDropdown = EncodingProfile.namesIsHEVC();
				String currentEncodingProfile = extendedStatistics.get(videoName + HaivisionConstant.HASH + nameIsCodecAlgorithm);
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
						videoName + HaivisionConstant.HASH + nameIsCodecAlgorithm, newEncodingProfile);
				addAdvanceControlProperties(advancedControllableProperties, encodingProfileDropdownControlProperty);
				break;
			case ENCODING_PROFILE:
				String codecAlgorithmInput = extendedStatistics.get(videoName + HaivisionConstant.HASH + VideoControllingMetric.CODEC_ALGORITHM.getName());
				String[] encodingProfileDropdown = EncodingProfile.namesIsAVG();
				if (CodecAlgorithm.H_265.getName().equals(codecAlgorithmInput)) {
					encodingProfileDropdown = EncodingProfile.namesIsHEVC();
				}

				AdvancedControllableProperty encodingProfileControlProperty = controlDropdown(extendedStatistics, encodingProfileDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, encodingProfileControlProperty);

				property = videoName + HaivisionConstant.HASH + VideoControllingMetric.CHROMA_SUBSAMPLING.getName();
				String[] chromaSubsampling;
				String chromaValue = extendedStatistics.get(property);
				if (EncodingProfile.BASELINE.getName().equals(value) || EncodingProfile.MAIN.getName().equals(value) || EncodingProfile.HIGH.getName()
						.equals(value)) {
					chromaSubsampling = ChromaSubSampling.namesIsBaselineOrMainOrHigh();
					chromaValue = ChromaSubSampling.BIT_420_8.getName();
				}
				//If Encoding profile is Main 10 or High 10, the Chroma subsampling list includes {4:2:0 8-bit; 4:2:0 10-bit}
				else if (EncodingProfile.MAIN_10.getName().equals(value) || EncodingProfile.HIGH_10.getName().equals(value)) {
					chromaSubsampling = ChromaSubSampling.namesIsMain10OrHigh10();
					if (ChromaSubSampling.BIT_422_8.getName().equals(chromaValue)) {
						chromaValue = ChromaSubSampling.BIT_420_8.getName();
					}

					if (ChromaSubSampling.BIT_422_10.getName().equals(chromaValue)) {
						chromaValue = ChromaSubSampling.BIT_420_10.getName();
					}
				}
				//If Encoding profile is Main 4:2:2 10 or High 4:2:2, the Chroma subsampling list includes {4:2:0 8-bit; 4:2:0 10-bit, 4:2:2 8-bit, 4:2:2 10-bit}
				else {
					chromaSubsampling = DropdownList.Names(ChromaSubSampling.class);
				}

				AdvancedControllableProperty chromaSubsamplingControl = controlDropdown(extendedStatistics, chromaSubsampling, property, chromaValue);
				addAdvanceControlProperties(advancedControllableProperties, chromaSubsamplingControl);
				break;
			case CHROMA_SUBSAMPLING:
				String encodingProfileInput = extendedStatistics.get(videoName + HaivisionConstant.HASH + VideoControllingMetric.ENCODING_PROFILE.getName());

				//If Encoding profile is Baseline, Main or High; then the Chroma subsampling list includes 4:2:0 8-bit
				if (EncodingProfile.BASELINE.getName().equals(encodingProfileInput) || EncodingProfile.MAIN.getName().equals(encodingProfileInput) || EncodingProfile.HIGH.getName()
						.equals(encodingProfileInput)) {
					chromaSubsampling = ChromaSubSampling.namesIsBaselineOrMainOrHigh();
				}
				//If Encoding profile is Main 10 or High 10, the Chroma subsampling list includes {4:2:0 8-bit; 4:2:0 10-bit}
				else if (EncodingProfile.MAIN_10.getName().equals(encodingProfileInput) || EncodingProfile.HIGH_10.getName().equals(encodingProfileInput)) {
					chromaSubsampling = ChromaSubSampling.namesIsMain10OrHigh10();
				}
				//If Encoding profile is Main 4:2:2 10 or High 4:2:2, the Chroma subsampling list includes {4:2:0 8-bit; 4:2:0 10-bit, 4:2:2 8-bit, 4:2:2 10-bit}
				else {
					chromaSubsampling = DropdownList.Names(ChromaSubSampling.class);
				}

				AdvancedControllableProperty chromaSubsamplingControlProperty = controlDropdown(extendedStatistics, chromaSubsampling, property, value);
				addAdvanceControlProperties(advancedControllableProperties, chromaSubsamplingControlProperty);
				break;
			case RATE_CONTROL:
				String[] rateControlDropdown = DropdownList.Names(RateControlDropdown.class);
				AdvancedControllableProperty rateControlDropdownControlProperty = controlDropdown(extendedStatistics, rateControlDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, rateControlDropdownControlProperty);
				if (RateControlDropdown.CVBR.getName().equals(value)) {
					VideoResponse videoResponseRateControl = videoNameToVideoResponse.get(videoName);
					String matBitRate = videoResponseRateControl.getMaxBitrate();
					if (HaivisionConstant.ZERO.equals(matBitRate)) {
						matBitRate = HaivisionConstant.EMPTY_STRING;
					}
					AdvancedControllableProperty maxBitRateProperty = controlNumeric(extendedStatistics, videoName + HaivisionConstant.HASH + VideoControllingMetric.MAX_BITRATE.getName(), matBitRate);
					addAdvanceControlProperties(advancedControllableProperties, maxBitRateProperty);
				}
				break;
			case MAX_BITRATE:
				VideoResponse videoResponseMaxBitrate = videoNameToVideoResponse.get(videoName);
				if (HaivisionConstant.ZERO.equals(value)) {
					value = HaivisionConstant.EMPTY_STRING;
					videoResponseMaxBitrate.setMaxBitrate(HaivisionConstant.ZERO);
				} else {
					int maxBitRate = Integer.parseInt(value);
					if (maxBitRate < HaivisionConstant.MIN_OF_VIDEO_BITRATE) {
						value = String.valueOf(HaivisionConstant.MIN_OF_VIDEO_BITRATE);
					}
					if (maxBitRate > HaivisionConstant.MAX_OF_VIDEO_BITRATE) {
						value = String.valueOf(HaivisionConstant.MAX_OF_VIDEO_BITRATE);
					}
					videoResponseMaxBitrate.setMaxBitrate(value);
				}

				AdvancedControllableProperty maxBitRateProperty = controlNumeric(extendedStatistics, property, value);
				addAdvanceControlProperties(advancedControllableProperties, maxBitRateProperty);
				break;
			case BITRATE:
				if (Integer.parseInt(value) < HaivisionConstant.MIN_OF_VIDEO_BITRATE) {
					value = Integer.toString(HaivisionConstant.MIN_OF_VIDEO_BITRATE);
				}
				if (Integer.parseInt(value) > HaivisionConstant.MAX_OF_VIDEO_BITRATE) {
					value = Integer.toString(HaivisionConstant.MAX_OF_VIDEO_BITRATE);
				}

				AdvancedControllableProperty bitrateControlProperty = controlNumeric(extendedStatistics, property, value);
				addAdvanceControlProperties(advancedControllableProperties, bitrateControlProperty);
				break;
			case RESOLUTION:
				String[] resolutionDropdown = DropdownList.Names(ResolutionDropdown.class);
				AdvancedControllableProperty resolutionDropdownControlProperty = controlDropdown(extendedStatistics, resolutionDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, resolutionDropdownControlProperty);

				String croppingProperties = videoName + HaivisionConstant.HASH + VideoControllingMetric.CROPPING.getName();
				//If resolution is cropping, then display Cropping dropdown
				if (ResolutionDropdown.checkIsCropping(value)) {
					VideoResponse videoResponseResolution = videoNameToVideoResponse.get(videoName);
					String cropping = videoResponseResolution.getCropping();
					Map<Integer, String> croppingMap = CroppingDropdown.getNameToValueMap();
					String[] croppingDropdown = DropdownList.Names(CroppingDropdown.class);

					AdvancedControllableProperty croppingDropdownControlProperty = controlDropdown(extendedStatistics, croppingDropdown, croppingProperties, croppingMap.get(Integer.parseInt(cropping)));
					addAdvanceControlProperties(advancedControllableProperties, croppingDropdownControlProperty);
				} else {
					String valueCropping = extendedStatistics.get(croppingProperties);
					if (!StringUtils.isNullOrEmpty(valueCropping)) {
						extendedStatistics.remove(croppingProperties);
					}
				}
				break;
			case CROPPING:
				String[] croppingDropdown = DropdownList.Names(CroppingDropdown.class);
				AdvancedControllableProperty croppingDropdownControlProperty = controlDropdown(extendedStatistics, croppingDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, croppingDropdownControlProperty);
				break;
			case FRAME_RATE:
				String[] frameRateDropdown = DropdownList.Names(FrameRateDropdown.class);
				AdvancedControllableProperty frameRateDropdownControlProperty = controlDropdown(extendedStatistics, frameRateDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, frameRateDropdownControlProperty);
				break;
			case FRAMING:
				String[] framingDropdown = DropdownList.Names(FramingDropdown.class);
				AdvancedControllableProperty framingDropdownControlProperty = controlDropdown(extendedStatistics, framingDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, framingDropdownControlProperty);
				String slicesProperties = videoName + HaivisionConstant.HASH + VideoControllingMetric.SLICES.getName();
				if (FramingDropdown.I.getName().equals(value) || FramingDropdown.IP.getName().equals(value)) {
					VideoResponse videoResponseFraming = videoNameToVideoResponse.get(videoName);
					String slices = videoResponseFraming.getSlices();
					String[] dropdownSlices = DropdownList.Names(SlicesDropdown.class);

					AdvancedControllableProperty slicesProperty = controlDropdown(extendedStatistics, dropdownSlices, slicesProperties, slices);
					addAdvanceControlProperties(advancedControllableProperties, slicesProperty);
				} else {
					String slicesValue = extendedStatistics.get(slicesProperties);
					if (!StringUtils.isNullOrEmpty(slicesValue)) {
						extendedStatistics.remove(slicesProperties);
					}
				}
				break;
			case SLICES:
				String[] dropdownSlices = DropdownList.Names(SlicesDropdown.class);
				VideoResponse videoResponseSlices = videoNameToVideoResponse.get(videoName);
				videoResponseSlices.setSlices(value);

				AdvancedControllableProperty slicesProperty = controlDropdown(extendedStatistics, dropdownSlices, property, value);
				addAdvanceControlProperties(advancedControllableProperties, slicesProperty);
				break;
			case INTRA_REFRESH:
				AdvancedControllableProperty intraRefreshProperty = controlSwitch(extendedStatistics, property, value, HaivisionConstant.DISABLE, HaivisionConstant.ENABLE);
				addAdvanceControlProperties(advancedControllableProperties, intraRefreshProperty);

				String gopSizeName = videoName + HaivisionConstant.HASH + VideoControllingMetric.GOP_SIZE.getName();
				if (HaivisionConstant.ZERO.equals(value)) {
					VideoResponse videoResponseItem = videoNameToVideoResponse.get(videoName);
					String gopSizeValue = videoResponseItem.getGopSize();

					AdvancedControllableProperty gopSizeControlProperty = controlNumeric(extendedStatistics, gopSizeName, gopSizeValue);
					addAdvanceControlProperties(advancedControllableProperties, gopSizeControlProperty);
				} else {
					String gopSize = extendedStatistics.get(gopSizeName);
					if (!StringUtils.isNullOrEmpty(gopSize)) {
						extendedStatistics.remove(gopSizeName);
					}
				}
				break;
			case GOP_SIZE:
				if (Integer.parseInt(value) < HaivisionConstant.MIN_OF_VIDEO_GOP_SIZE) {
					value = Integer.toString(HaivisionConstant.MIN_OF_VIDEO_GOP_SIZE);
				}
				if (Integer.parseInt(value) > HaivisionConstant.MAX_OF_VIDEO_GOP_SIZE) {
					value = Integer.toString(HaivisionConstant.MAX_OF_VIDEO_GOP_SIZE);
				}
				VideoResponse videoResponseGopSize = videoNameToVideoResponse.get(videoName);
				videoResponseGopSize.setGopSize(value);

				AdvancedControllableProperty gopSizeControlProperty = controlNumeric(extendedStatistics, property, value);
				addAdvanceControlProperties(advancedControllableProperties, gopSizeControlProperty);
				break;
			case CLOSED_CAPTION:
				AdvancedControllableProperty closeCaptionControlProperty = controlSwitch(extendedStatistics, property, value, HaivisionConstant.DISABLE, HaivisionConstant.ENABLE);
				addAdvanceControlProperties(advancedControllableProperties, closeCaptionControlProperty);
				break;
			case TIME_CODE_SOURCE:
				String[] timeCodeSourceDropdown = DropdownList.Names(TimeCodeSource.class);
				AdvancedControllableProperty timeCodeSourceDropdownControlProperty;
				if (HaivisionConstant.NONE.equals(value)) {
					timeCodeSourceDropdownControlProperty = controlDropdownAcceptNoneValue(extendedStatistics, timeCodeSourceDropdown, property, value);
				} else {
					timeCodeSourceDropdownControlProperty = controlDropdown(extendedStatistics, timeCodeSourceDropdown, property, value);
				}
				addAdvanceControlProperties(advancedControllableProperties, timeCodeSourceDropdownControlProperty);

				String countingName = videoName + HaivisionConstant.HASH + VideoControllingMetric.COUNTING_MODE.getName();
				String reSyncHourName = videoName + HaivisionConstant.HASH + VideoControllingMetric.RESYNC_HOUR.getName();
				String dailyReSyncName = videoName + HaivisionConstant.HASH + VideoControllingMetric.DAILY_RESYNC.getName();
				if (TimeCodeSource.SYSTEM.getName().equals(value)) {
					Map<Integer, String> countingMap = CountingDropdown.getNameToValueMap();
					String[] dropdownCounting = DropdownList.Names(CountingDropdown.class);
					VideoResponse video = videoNameToVideoResponse.get(videoName);

					//If time code source is system => add counting
					String counting = video.getCountingMode();
					value = getNameByValue(counting, countingMap);
					AdvancedControllableProperty countingControlProperty = controlDropdown(extendedStatistics, dropdownCounting, countingName, value);
					addAdvanceControlProperties(advancedControllableProperties, countingControlProperty);

					//if CountingMode is SMPTE_12M_1
					if (CountingDropdown.SMPTE_12M_1.getName().equals(value)) {
						String dailyReSync = video.getDailyReSync();
						String dailyReSyncValue = HaivisionConstant.ZERO;
						if (HaivisionConstant.TRUE.equalsIgnoreCase(dailyReSync) || HaivisionConstant.NUMBER_ONE.equals(dailyReSync)) {
							dailyReSyncValue = HaivisionConstant.NUMBER_ONE;
						}
						AdvancedControllableProperty dailyReSyncProperty = controlSwitch(extendedStatistics, dailyReSyncName, dailyReSyncValue, HaivisionConstant.DISABLE, HaivisionConstant.ENABLE);
						addAdvanceControlProperties(advancedControllableProperties, dailyReSyncProperty);

						//if dailyReSync is 1
						if (HaivisionConstant.NUMBER_ONE.equals(dailyReSyncValue)) {
							Map<Integer, String> reSyncHourMap = ReSyncHourDropdown.getNameToValueMap();
							String[] dropdownReSyncHour = DropdownList.Names(ReSyncHourDropdown.class);
							String reSyncHour = video.getReSyncHour();
							value = getNameByValue(reSyncHour, reSyncHourMap);
							AdvancedControllableProperty reSyncHourControlProperty = controlDropdown(extendedStatistics, dropdownReSyncHour, reSyncHourName, value);
							addAdvanceControlProperties(advancedControllableProperties, reSyncHourControlProperty);
						}
					}
				} else {
					String countingOption = extendedStatistics.get(countingName);
					if (!StringUtils.isNullOrEmpty(countingOption)) {
						extendedStatistics.remove(countingName);
					}
					String dailyReSyncOption = extendedStatistics.get(dailyReSyncName);
					if (!StringUtils.isNullOrEmpty(dailyReSyncOption)) {
						extendedStatistics.remove(dailyReSyncName);
					}
					String reSyncHourOption = extendedStatistics.get(reSyncHourName);
					if (!StringUtils.isNullOrEmpty(reSyncHourOption)) {
						extendedStatistics.remove(reSyncHourName);
					}
				}
				break;
			case ASPECT_RATIO:
				String[] aspectRatioDropdown = DropdownList.Names(AspectRatioDropdown.class);
				AdvancedControllableProperty aspectRatioDropdownControlProperty = controlDropdown(extendedStatistics, aspectRatioDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, aspectRatioDropdownControlProperty);
				break;
			case ACTION:
				for (AdvancedControllableProperty advancedControllableProperty : advancedControllableProperties) {
					if (advancedControllableProperty.getName().equals(property)) {
						extendedStatistics.put(property, value);
						advancedControllableProperty.setValue(value);
						break;
					}
				}
				break;
			case APPLY_CHANGE:
				VideoResponse videoResponse = convertVideoByValue(extendedStatistics, videoName);

				// sent request to apply all change for all metric
				setVideoApplyChange(videoResponse.payLoad(), videoResponse.getId());

				//sent request to action for the metric
				setActionVideoControl(videoName, videoResponse);
				isEmergencyDelivery = false;
				break;
			case CANCEL:
				isEmergencyDelivery = false;
				break;
			case COUNTING_MODE:
				String[] dropdownCounting = DropdownList.Names(CountingDropdown.class);
				AdvancedControllableProperty countingControlProperty = controlDropdown(extendedStatistics, dropdownCounting, property, value);
				addAdvanceControlProperties(advancedControllableProperties, countingControlProperty);

				//if CountingMode is SMPTE_12M_1
				reSyncHourName = videoName + HaivisionConstant.HASH + VideoControllingMetric.RESYNC_HOUR.getName();
				dailyReSyncName = videoName + HaivisionConstant.HASH + VideoControllingMetric.DAILY_RESYNC.getName();
				if (CountingDropdown.SMPTE_12M_1.getName().equals(value)) {
					VideoResponse video = videoNameToVideoResponse.get(videoName);
					String dailyReSync = video.getDailyReSync();
					String dailyReSyncValue = HaivisionConstant.ZERO;
					if (HaivisionConstant.TRUE.equalsIgnoreCase(dailyReSync) || HaivisionConstant.NUMBER_ONE.equals(dailyReSync)) {
						dailyReSyncValue = HaivisionConstant.NUMBER_ONE;
					}
					AdvancedControllableProperty dailyReSyncProperty = controlSwitch(extendedStatistics, dailyReSyncName, dailyReSyncValue, HaivisionConstant.DISABLE, HaivisionConstant.ENABLE);
					addAdvanceControlProperties(advancedControllableProperties, dailyReSyncProperty);

					//if dailyReSync is 1
					if (HaivisionConstant.NUMBER_ONE.equals(dailyReSyncValue)) {
						Map<Integer, String> reSyncHourMap = ReSyncHourDropdown.getNameToValueMap();
						String[] dropdownReSyncHour = DropdownList.Names(ReSyncHourDropdown.class);
						String reSyncHour = video.getReSyncHour();
						value = getNameByValue(reSyncHour, reSyncHourMap);
						AdvancedControllableProperty reSyncHourControlProperty = controlDropdown(extendedStatistics, dropdownReSyncHour, reSyncHourName, value);
						addAdvanceControlProperties(advancedControllableProperties, reSyncHourControlProperty);
					}
				} else {
					String dailyReSyncOption = extendedStatistics.get(dailyReSyncName);
					if (!StringUtils.isNullOrEmpty(dailyReSyncOption)) {
						extendedStatistics.remove(dailyReSyncName);
					}
					String reSyncHourOption = extendedStatistics.get(reSyncHourName);
					if (!StringUtils.isNullOrEmpty(reSyncHourOption)) {
						extendedStatistics.remove(reSyncHourName);
					}
				}
				break;
			case DAILY_RESYNC:
				reSyncHourName = videoName + HaivisionConstant.HASH + VideoControllingMetric.RESYNC_HOUR.getName();
				AdvancedControllableProperty dailyResyncProperty = controlSwitch(extendedStatistics, property, value, HaivisionConstant.DISABLE, HaivisionConstant.ENABLE);
				addAdvanceControlProperties(advancedControllableProperties, dailyResyncProperty);

				VideoResponse videoResponseDailyReSync = videoNameToVideoResponse.get(videoName);
				videoResponseDailyReSync.setDailyReSync(value);

				//if dailyReSync is 1
				if (HaivisionConstant.NUMBER_ONE.equals(value)) {
					VideoResponse video = videoNameToVideoResponse.get(videoName);
					Map<Integer, String> reSyncHourMap = ReSyncHourDropdown.getNameToValueMap();
					String[] dropdownReSyncHour = DropdownList.Names(ReSyncHourDropdown.class);
					String reSyncHour = video.getReSyncHour();
					value = getNameByValue(reSyncHour, reSyncHourMap);

					AdvancedControllableProperty reSyncHourControlProperty = controlDropdown(extendedStatistics, dropdownReSyncHour, reSyncHourName, value);
					addAdvanceControlProperties(advancedControllableProperties, reSyncHourControlProperty);
				} else {
					String reSyncHourOption = extendedStatistics.get(reSyncHourName);
					if (!StringUtils.isNullOrEmpty(reSyncHourOption)) {
						extendedStatistics.remove(reSyncHourName);
					}
				}
				break;
			case RESYNC_HOUR:
				String[] dropdownReSyncHour = DropdownList.Names(ReSyncHourDropdown.class);
				AdvancedControllableProperty reSyncHourControlProperty = controlDropdown(extendedStatistics, dropdownReSyncHour, property, value);
				addAdvanceControlProperties(advancedControllableProperties, reSyncHourControlProperty);

				videoResponseDailyReSync = videoNameToVideoResponse.get(videoName);
				videoResponseDailyReSync.setReSyncHour(value);
				break;
			default:
				break;
		}
		//Editing
		if (isEmergencyDelivery) {
			extendedStatistics.put(videoName + HaivisionConstant.HASH + VideoControllingMetric.APPLY_CHANGE.getName(), "");
			advancedControllableProperties.add(createButton(videoName + HaivisionConstant.HASH + VideoControllingMetric.APPLY_CHANGE.getName(), HaivisionConstant.APPLY, HaivisionConstant.APPLY, 0));

			extendedStatistics.put(videoName + HaivisionConstant.HASH + HaivisionConstant.EDITED, HaivisionConstant.TRUE);
			extendedStatistics.put(videoName + HaivisionConstant.HASH + HaivisionConstant.CANCEL, "");
			advancedControllableProperties.add(createButton(videoName + HaivisionConstant.HASH + HaivisionConstant.CANCEL, HaivisionConstant.CANCEL, HaivisionConstant.CANCEL, 0));
		}
	}

	/**
	 * Control stream encoder
	 *
	 * @param property the property is the filed name of controlling metric
	 * @param value the value is value of metric
	 * @param extendedStatistics list extendedStatistics
	 * @param advancedControllableProperties the advancedControllableProperties is advancedControllableProperties instance
	 */
	private void controlStreamProperty(String property, String value, Map<String, String> extendedStatistics, List<AdvancedControllableProperty> advancedControllableProperties) {
		String[] propertyOption = property.split(HaivisionConstant.HASH);
		String streamName = propertyOption[0];
		String metricName = propertyOption[1];
		String[] encryptionDropdown = DropdownList.Names(EncryptionDropdown.class);
		String[] protocolDropdown = DropdownList.Names(ProtocolDropdown.class);
		String[] audioList = audioNameToAudioResponse.keySet().toArray(new String[audioNameToAudioResponse.size()]);
		String[] shapingDropdown = DropdownList.Names(TimingAndShaping.class);
		String[] videoDropdown = DropdownList.Names(VideoDropdown.class);
		String[] srtModeDropdown = DropdownList.Names(SRTModeDropdown.class);
		Map<Integer, String> srtModeMap = SRTModeDropdown.getNameToValueMap();
		OutputResponse outputResponseItem = streamNameToStreamResponse.get(streamName);
		OutputSAP outputSAP = outputResponseItem.getOutputSAP();
		isEmergencyDelivery = true;

		//Control Source Audio
		if (metricName.contains(CreateOutputStreamMetric.SOURCE_AUDIO.getName())) {
			if (HaivisionConstant.NONE.equals(value) && !(HaivisionConstant.SOURCE_AUDIO_0.equals(metricName))) {
				extendedStatistics.remove(property);
				Map<String, Audio> sourceAudioMap = sourceAudioResponse.get(streamName);
				sourceAudioMap.put(metricName, null);
			} else {
				AdvancedControllableProperty sourceAudioControlProperty = controlDropdownAcceptNoneValue(extendedStatistics, audioList, property, value);
				addAdvanceControlProperties(advancedControllableProperties, sourceAudioControlProperty);
				Audio audio = new Audio();
				audio.setId(audioNameToAudioResponse.get(value).getId());
				Map<String, Audio> sourceAudioMap = sourceAudioResponse.get(streamName);
				sourceAudioMap.put(metricName, audio);
			}
			//Editing
			if (isEmergencyDelivery) {
				extendedStatistics.put(streamName + HaivisionConstant.HASH + CreateOutputStreamMetric.APPLY_CHANGE.getName(), "");
				advancedControllableProperties.add(createButton(streamName + HaivisionConstant.HASH + CreateOutputStreamMetric.APPLY_CHANGE.getName(), HaivisionConstant.APPLY, "Applying", 0));

				extendedStatistics.put(streamName + HaivisionConstant.HASH + HaivisionConstant.EDITED, HaivisionConstant.TRUE);
				extendedStatistics.put(streamName + HaivisionConstant.HASH + HaivisionConstant.CANCEL, "");
				advancedControllableProperties.add(createButton(streamName + HaivisionConstant.HASH + HaivisionConstant.CANCEL, HaivisionConstant.CANCEL, HaivisionConstant.CANCEL, 0));
			}
			return;
		}
		CreateOutputStreamMetric streamMetric = CreateOutputStreamMetric.getByName(metricName);

		switch (streamMetric) {
			case ACTION:
				for (AdvancedControllableProperty advancedControllableProperty : advancedControllableProperties) {
					if (advancedControllableProperty.getName().equals(property)) {
						extendedStatistics.put(property, value);
						advancedControllableProperty.setValue(value);
						break;
					}
				}
				break;
			case CONTENT_NAME:
			case DESTINATION_ADDRESS:
				AdvancedControllableProperty textControlProperty = controlText(extendedStatistics, property, value);
				addAdvanceControlProperties(advancedControllableProperties, textControlProperty);
				break;
			case SOURCE_VIDEO:
				AdvancedControllableProperty sourceVideoControl = controlDropdownAcceptNoneValue(extendedStatistics, videoDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, sourceVideoControl);
				break;
			case TRANSMIT_SAP:
				String keyProperty = streamName + HaivisionConstant.HASH;
				String sapName = keyProperty + CreateOutputStreamMetric.SAP_NAME.getName();
				String keywordsName = keyProperty + CreateOutputStreamMetric.SAP_KEYWORDS.getName();
				String descName = keyProperty + CreateOutputStreamMetric.SAP_DESCRIPTION.getName();
				String authorName = keyProperty + CreateOutputStreamMetric.SAP_AUTHOR.getName();
				String copyrightName = keyProperty + CreateOutputStreamMetric.SAP_COPYRIGHT.getName();
				String addressName = keyProperty + CreateOutputStreamMetric.SAP_ADDRESS.getName();
				String portName = keyProperty + CreateOutputStreamMetric.SAP_PORT.getName();

				AdvancedControllableProperty transmitControl = controlSwitch(extendedStatistics, property, value, HaivisionConstant.DISABLE, HaivisionConstant.ENABLE);
				addAdvanceControlProperties(advancedControllableProperties, transmitControl);
				outputSAP.setAdvertise(value);
				//Update mode SAP
				if (HaivisionConstant.NUMBER_ONE.equals(value)) {
					value = checkNoneStringValue(outputSAP.getName());

					AdvancedControllableProperty sapNameControl = controlText(extendedStatistics, sapName, value);
					addAdvanceControlProperties(advancedControllableProperties, sapNameControl);
					outputSAP.setName(value);

					value = checkNoneStringValue(outputSAP.getKeywords());
					AdvancedControllableProperty keywordsControl = controlText(extendedStatistics, keywordsName, value);
					addAdvanceControlProperties(advancedControllableProperties, keywordsControl);
					outputSAP.setKeywords(value);

					value = checkNoneStringValue(outputSAP.getDesc());
					AdvancedControllableProperty descControl = controlText(extendedStatistics, descName, value);
					addAdvanceControlProperties(advancedControllableProperties, descControl);
					outputSAP.setDesc(value);

					value = checkNoneStringValue(outputSAP.getAuthor());
					AdvancedControllableProperty authorControl = controlText(extendedStatistics, authorName, value);
					addAdvanceControlProperties(advancedControllableProperties, authorControl);
					outputSAP.setAuthor(value);

					value = checkNoneStringValue(outputSAP.getCopyright());
					AdvancedControllableProperty copyrightControl = controlText(extendedStatistics, copyrightName, value);
					addAdvanceControlProperties(advancedControllableProperties, copyrightControl);
					outputSAP.setCopyright(value);

					value = checkNoneStringValue(outputSAP.getAddress());
					AdvancedControllableProperty addressControl = controlText(extendedStatistics, addressName, value);
					addAdvanceControlProperties(advancedControllableProperties, addressControl);
					outputSAP.setAddress(value);

					value = checkNoneStringValue(outputSAP.getPort());
					AdvancedControllableProperty portControl = controlText(extendedStatistics, portName, value);
					addAdvanceControlProperties(advancedControllableProperties, portControl);
					outputSAP.setPort(value);

					outputResponseItem.setOutputSAP(outputSAP);
				} else {
					extendedStatistics.remove(sapName);
					extendedStatistics.remove(keywordsName);
					extendedStatistics.remove(descName);
					extendedStatistics.remove(authorName);
					extendedStatistics.remove(copyrightName);
					extendedStatistics.remove(addressName);
					extendedStatistics.remove(portName);
				}
				break;
			case SAP_NAME:
				AdvancedControllableProperty sapNameControl = controlText(extendedStatistics, streamName + HaivisionConstant.HASH + CreateOutputStreamMetric.SAP_NAME.getName(), value);
				addAdvanceControlProperties(advancedControllableProperties, sapNameControl);
				outputSAP.setName(value);
				break;
			case SAP_KEYWORDS:
				AdvancedControllableProperty keywordsControl = controlText(extendedStatistics, streamName + HaivisionConstant.HASH + CreateOutputStreamMetric.SAP_KEYWORDS.getName(), value);
				addAdvanceControlProperties(advancedControllableProperties, keywordsControl);
				outputSAP.setKeywords(value);
				break;
			case SAP_DESCRIPTION:
				AdvancedControllableProperty descControl = controlText(extendedStatistics, streamName + HaivisionConstant.HASH + CreateOutputStreamMetric.SAP_DESCRIPTION.getName(), value);
				addAdvanceControlProperties(advancedControllableProperties, descControl);
				outputSAP.setDesc(value);
				break;
			case SAP_AUTHOR:
				AdvancedControllableProperty authorControl = controlText(extendedStatistics, streamName + HaivisionConstant.HASH + CreateOutputStreamMetric.SAP_AUTHOR.getName(), value);
				addAdvanceControlProperties(advancedControllableProperties, authorControl);
				outputSAP.setAuthor(value);
				break;
			case SAP_COPYRIGHT:
				AdvancedControllableProperty copyrightControl = controlText(extendedStatistics, streamName + HaivisionConstant.HASH + CreateOutputStreamMetric.SAP_COPYRIGHT.getName(), value);
				addAdvanceControlProperties(advancedControllableProperties, copyrightControl);
				outputSAP.setCopyright(value);
				break;
			case SAP_ADDRESS:
				AdvancedControllableProperty addressControl = controlText(extendedStatistics, streamName + HaivisionConstant.HASH + CreateOutputStreamMetric.SAP_ADDRESS.getName(), value);
				addAdvanceControlProperties(advancedControllableProperties, addressControl);
				outputSAP.setAddress(value);
				break;
			case SAP_PORT:
				AdvancedControllableProperty portControl = controlNumeric(extendedStatistics, streamName + HaivisionConstant.HASH + CreateOutputStreamMetric.SAP_PORT.getName(), value);
				addAdvanceControlProperties(advancedControllableProperties, portControl);
				outputSAP.setPort(value);
				break;
			case STREAMING_PROTOCOL:
				AdvancedControllableProperty protocolControl = controlDropdown(extendedStatistics, protocolDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, protocolControl);

				keyProperty = streamName + HaivisionConstant.HASH;
				String modeName = keyProperty + CreateOutputStreamMetric.CONNECTION_MODE.getName();
				addressName = keyProperty + CreateOutputStreamMetric.CONNECTION_ADDRESS.getName();
				String connectionDestinationPortName = keyProperty + CreateOutputStreamMetric.CONNECTION_DESTINATION_PORT.getName();
				String sourcePortName = keyProperty + CreateOutputStreamMetric.CONNECTION_SOURCE_PORT.getName();
				String networkAdapterName = keyProperty + CreateOutputStreamMetric.CONNECTION_NETWORK_ADAPTIVE.getName();
				String latencyName = keyProperty + CreateOutputStreamMetric.CONNECTION_LATENCY.getName();
				String encryptionName = keyProperty + CreateOutputStreamMetric.CONNECTION_ENCRYPTION.getName();
				String passPhraseName = keyProperty + CreateOutputStreamMetric.CONNECTION_PASSPHRASE.getName();
				portName = keyProperty + CreateOutputStreamMetric.CONNECTION_PORT.getName();
				String alternatePortName = keyProperty + CreateOutputStreamMetric.CONNECTION_ALTERNATE_PORT.getName();
				String destinationAddressName = keyProperty + CreateOutputStreamMetric.DESTINATION_ADDRESS.getName();
				String destinationPortName = keyProperty + CreateOutputStreamMetric.DESTINATION_PORT.getName();

				if (ProtocolDropdown.TS_OVER_SRT.getName().equals(value)) {
					String srtMode = outputResponseItem.getSrtMode();
					String connectionMode = SRTModeDropdown.CALLER.getName();
					if (!HaivisionConstant.NONE.equals(srtMode)) {
						connectionMode = getNameByValue(srtMode, srtModeMap);
					}
					AdvancedControllableProperty modeControl = controlDropdown(extendedStatistics, srtModeDropdown, modeName, connectionMode);
					addAdvanceControlProperties(advancedControllableProperties, modeControl);

					//Add address
					value = checkNoneStringValue(outputResponseItem.getAddress());
					AdvancedControllableProperty connectionAddressControl = controlText(extendedStatistics, addressName, value);
					addAdvanceControlProperties(advancedControllableProperties, connectionAddressControl);

					//Add sourcePort => is SourcePort Response
					value = checkNoneStringValue(outputResponseItem.getSourcePort());
					AdvancedControllableProperty connectionSourcePortControl = controlNumeric(extendedStatistics, sourcePortName, value);
					addAdvanceControlProperties(advancedControllableProperties, connectionSourcePortControl);

					//destinationPort is port Response
					value = checkNoneStringValue(outputResponseItem.getPort());
					AdvancedControllableProperty connectionDestinationPortControl = controlNumeric(extendedStatistics, connectionDestinationPortName, value);
					addAdvanceControlProperties(advancedControllableProperties, connectionDestinationPortControl);

					if (SRTModeDropdown.RENDEZVOUS.getName().equals(connectionMode)) {

						//Update sourcePort = DestinationPort and not accept control
						value = checkNoneStringValue(outputResponseItem.getPort());
						extendedStatistics.put(sourcePortName, value);
						advancedControllableProperties.removeIf(item -> item.getName().equals(sourcePortName));
					}
					if (SRTModeDropdown.CALLER.getName().equals(connectionMode) || SRTModeDropdown.RENDEZVOUS.getName().equals(connectionMode)) {

						//remove port, alternatePort estinationPort and destinationAddress
						extendedStatistics.remove(portName);
						extendedStatistics.remove(alternatePortName);
						extendedStatistics.remove(destinationPortName);
						extendedStatistics.remove(destinationAddressName);
					}
					if (SRTModeDropdown.LISTENER.getName().equals(connectionMode)) {

						//Update Port
						String portValue = checkNoneStringValue(outputResponseItem.getPort());
						AdvancedControllableProperty connectionPortControl = controlNumeric(extendedStatistics, portName, portValue);
						addAdvanceControlProperties(advancedControllableProperties, connectionPortControl);

						//Update alternatePortName
						String alternateValue = checkNoneStringValue(outputResponseItem.getSrtListenerSecondPort());
						AdvancedControllableProperty alternatePortControl = controlNumeric(extendedStatistics, alternatePortName, alternateValue);
						addAdvanceControlProperties(advancedControllableProperties, alternatePortControl);

						//remove sourcePort and destinationPort
						extendedStatistics.remove(sourcePortName);
						extendedStatistics.remove(connectionDestinationPortName);
						extendedStatistics.remove(addressName);
					}

					// NetworkAdpter
					value = outputResponseItem.getAdaptive();
					if (HaivisionConstant.NONE.equals(value)) {
						value = HaivisionConstant.ZERO;
					}
					AdvancedControllableProperty connectionAdaptive = controlSwitch(extendedStatistics, networkAdapterName, value, HaivisionConstant.DISABLE, HaivisionConstant.DISABLE);
					addAdvanceControlProperties(advancedControllableProperties, connectionAdaptive);

					//Latency
					value = checkNoneStringValue(outputResponseItem.getLatency());
					if (StringUtils.isNullOrEmpty(value)) {
						value = HaivisionConstant.DEFAULT_LATENCY;
					}
					AdvancedControllableProperty connectionLatency = controlNumeric(extendedStatistics, latencyName, value);
					addAdvanceControlProperties(advancedControllableProperties, connectionLatency);

					//Encryption
					value = checkNoneStringValue(outputResponseItem.getEncryption());
					AdvancedControllableProperty connectionEncryption = controlDropdownAcceptNoneValue(extendedStatistics, encryptionDropdown, encryptionName, value);
					addAdvanceControlProperties(advancedControllableProperties, connectionEncryption);

					if (!StringUtils.isNullOrEmpty(value)) {
						value = checkNoneStringValue(outputResponseItem.getPassphrase());
						AdvancedControllableProperty connectionPassphrase = controlText(extendedStatistics, passPhraseName, value);
						addAdvanceControlProperties(advancedControllableProperties, connectionPassphrase);
					}
					extendedStatistics.remove(destinationPortName);
					extendedStatistics.remove(destinationAddressName);
					break;
				} else {
					//Add address
					value = checkNoneStringValue(outputResponseItem.getAddress());
					AdvancedControllableProperty connectionAddressControl = controlText(extendedStatistics, addressName, value);
					addAdvanceControlProperties(advancedControllableProperties, connectionAddressControl);

					//Update Port
					value = checkNoneStringValue(outputResponseItem.getPort());
					AdvancedControllableProperty connectionPortControl = controlNumeric(extendedStatistics, portName, value);
					addAdvanceControlProperties(advancedControllableProperties, connectionPortControl);

					extendedStatistics.remove(modeName);
					extendedStatistics.remove(connectionDestinationPortName);
					extendedStatistics.remove(sourcePortName);
					extendedStatistics.remove(networkAdapterName);
					extendedStatistics.remove(networkAdapterName);
					extendedStatistics.remove(encryptionName);
					extendedStatistics.remove(passPhraseName);
					extendedStatistics.remove(alternatePortName);
					extendedStatistics.remove(latencyName);
				}
				break;
			case CONNECTION_MODE:
				srtModeDropdown = DropdownList.Names(SRTModeDropdown.class);
				AdvancedControllableProperty modeControlProperty = controlDropdown(extendedStatistics, srtModeDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, modeControlProperty);
				outputResponseItem.setSrtMode(value);

				keyProperty = streamName + HaivisionConstant.HASH;
				addressName = keyProperty + CreateOutputStreamMetric.CONNECTION_ADDRESS.getName();
				connectionDestinationPortName = keyProperty + CreateOutputStreamMetric.CONNECTION_DESTINATION_PORT.getName();
				sourcePortName = keyProperty + CreateOutputStreamMetric.CONNECTION_SOURCE_PORT.getName();
				portName = keyProperty + CreateOutputStreamMetric.CONNECTION_PORT.getName();
				alternatePortName = keyProperty + CreateOutputStreamMetric.CONNECTION_ALTERNATE_PORT.getName();

				if (SRTModeDropdown.RENDEZVOUS.getName().equals(value)) {

					//Add address
					String addressValue = checkNoneStringValue(outputResponseItem.getAddress());
					AdvancedControllableProperty connectionAddressControl = controlText(extendedStatistics, addressName, addressValue);
					addAdvanceControlProperties(advancedControllableProperties, connectionAddressControl);

					//Add sourcePort => is SourcePort Response
					String sourcePortValue = checkNoneStringValue(outputResponseItem.getSourcePort());
					AdvancedControllableProperty connectionSourcePortControl = controlNumeric(extendedStatistics, sourcePortName, sourcePortValue);
					addAdvanceControlProperties(advancedControllableProperties, connectionSourcePortControl);

					//destinationPort is port Response
					String portValue = checkNoneStringValue(outputResponseItem.getPort());
					AdvancedControllableProperty connectionDestinationPortControl = controlNumeric(extendedStatistics, connectionDestinationPortName, portValue);
					addAdvanceControlProperties(advancedControllableProperties, connectionDestinationPortControl);

					//Update sourcePort = DestinationPort and not accept control
					String sourcePortValueRemove = checkNoneStringValue(outputResponseItem.getPort());
					extendedStatistics.put(sourcePortName, sourcePortValueRemove);
					advancedControllableProperties.removeIf(item -> item.getName().equals(sourcePortName));

					//remove port and alternatePort
					extendedStatistics.remove(portName);
					extendedStatistics.remove(alternatePortName);

				}
				if (SRTModeDropdown.LISTENER.getName().equals(value)) {

					//Update Port
					String portValue = checkNoneStringValue(outputResponseItem.getPort());
					AdvancedControllableProperty connectionPortControl = controlNumeric(extendedStatistics, portName, portValue);
					addAdvanceControlProperties(advancedControllableProperties, connectionPortControl);

					//Update alternatePortName
					String alternateValue = checkNoneStringValue(outputResponseItem.getSrtListenerSecondPort());
					AdvancedControllableProperty alternatePortControl = controlNumeric(extendedStatistics, alternatePortName, alternateValue);
					addAdvanceControlProperties(advancedControllableProperties, alternatePortControl);

					//remove sourcePort and destinationPort
					extendedStatistics.remove(sourcePortName);
					extendedStatistics.remove(connectionDestinationPortName);
					extendedStatistics.remove(addressName);
				}

				if (SRTModeDropdown.CALLER.getName().equals(value)) {

					//Add address
					String addressValue = checkNoneStringValue(outputResponseItem.getAddress());
					AdvancedControllableProperty connectionAddressControl = controlText(extendedStatistics, addressName, addressValue);
					addAdvanceControlProperties(advancedControllableProperties, connectionAddressControl);

					//Add sourcePort => is SourcePort Response
					String sourcePortValue = checkNoneStringValue(outputResponseItem.getSourcePort());
					AdvancedControllableProperty connectionSourcePortControl = controlNumeric(extendedStatistics, sourcePortName, sourcePortValue);
					addAdvanceControlProperties(advancedControllableProperties, connectionSourcePortControl);

					//destinationPort is port Response
					String portValue = checkNoneStringValue(outputResponseItem.getPort());
					AdvancedControllableProperty connectionDestinationPortControl = controlNumeric(extendedStatistics, connectionDestinationPortName, portValue);
					addAdvanceControlProperties(advancedControllableProperties, connectionDestinationPortControl);

					//remove port and alternatePort
					extendedStatistics.remove(portName);
					extendedStatistics.remove(alternatePortName);
				}
				break;
			case CONNECTION_ADDRESS:
				AdvancedControllableProperty connectionAddressControl = controlText(extendedStatistics, property, value);
				addAdvanceControlProperties(advancedControllableProperties, connectionAddressControl);
				outputResponseItem.setAddress(value);
				break;
			case CONNECTION_SOURCE_PORT:
				int sourcePort = Integer.parseInt(value);
				if (sourcePort < HaivisionConstant.SOURCE_PORT_MIN || sourcePort > HaivisionConstant.SOURCE_PORT_MAX) {
					throw new ResourceNotReachableException("Value of source port is invalid. Source port must be between 1 to 65535");
				}
				AdvancedControllableProperty connectionSourcePortControl = controlNumeric(extendedStatistics, property, value);
				addAdvanceControlProperties(advancedControllableProperties, connectionSourcePortControl);
				outputResponseItem.setSourcePort(value);
				break;
			case CONNECTION_ALTERNATE_PORT:
				int alternatePort = Integer.parseInt(value);
				if (alternatePort < HaivisionConstant.SOURCE_PORT_MIN || alternatePort > HaivisionConstant.SOURCE_PORT_MAX) {
					throw new ResourceNotReachableException("Value of alternate port is invalid. Alternate port must be between 1 to 65535");
				}
				AdvancedControllableProperty alternatePortProperty = controlNumeric(extendedStatistics, property, value);
				addAdvanceControlProperties(advancedControllableProperties, alternatePortProperty);
				outputResponseItem.setSourcePort(value);
				break;
			case DESTINATION_PORT:
			case CONNECTION_DESTINATION_PORT:
				int destinationPort = Integer.parseInt(value);
				if (destinationPort < HaivisionConstant.SOURCE_PORT_MIN || destinationPort > HaivisionConstant.SOURCE_PORT_MAX) {
					throw new ResourceNotReachableException("Value of destination port is invalid. Destination port must be between 1 to 65535");
				}
				AdvancedControllableProperty destinationPortProperty = controlNumeric(extendedStatistics, property, value);
				addAdvanceControlProperties(advancedControllableProperties, destinationPortProperty);
				outputResponseItem.setPort(value);
				break;
			case CONNECTION_NETWORK_ADAPTIVE:
				AdvancedControllableProperty networkAdaptiveControlProperty = controlSwitch(extendedStatistics, property, value,
						HaivisionConstant.DISABLE, HaivisionConstant.ENABLE);
				addAdvanceControlProperties(advancedControllableProperties, networkAdaptiveControlProperty);
				outputResponseItem.setAdaptive(value);
				break;
			case CONNECTION_LATENCY:
				if (Integer.parseInt(value) < HaivisionConstant.MIN_OF_LATENCY) {
					value = Integer.toString(HaivisionConstant.MIN_OF_LATENCY);
				}
				if (Integer.parseInt(value) > HaivisionConstant.MAX_OF_LATENCY) {
					value = Integer.toString(HaivisionConstant.MAX_OF_LATENCY);
				}

				AdvancedControllableProperty latencyControlProperty = controlNumeric(extendedStatistics, property, value);
				addAdvanceControlProperties(advancedControllableProperties, latencyControlProperty);
				outputResponseItem.setLatency(value);
				break;
			case CONNECTION_ENCRYPTION:
				String connectionPassphrase = streamName + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_PASSPHRASE.getName();
				AdvancedControllableProperty encryptionControlProperty = controlDropdown(extendedStatistics, encryptionDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, encryptionControlProperty);
				outputResponseItem.setEncryption(value);

				if (EncryptionDropdown.AES_128.getName().equals(value) || EncryptionDropdown.AES_256.getName().equals(value)) {
					String passwordsPhrase = checkNoneStringValue(outputResponseItem.getPassphrase());
					advancedControllableProperties.add(controlText(extendedStatistics, connectionPassphrase, passwordsPhrase));
				} else {
					extendedStatistics.remove(connectionPassphrase);
				}
				break;
			case CONNECTION_PASSPHRASE:
				if (value.length() >= HaivisionConstant.MIN_OF_PASSPHRASE_LENGTH || value.length() <= HaivisionConstant.MAX_OF_PASSPHRASE_LENGTH) {
					AdvancedControllableProperty passControlProperty = controlText(extendedStatistics, property, value);
					addAdvanceControlProperties(advancedControllableProperties, passControlProperty);
					outputResponseItem.setPassphrase(value);
				} else {
					throw new ResourceNotReachableException("Passphrase is too short or to long.");
				}
				break;
			case PARAMETER_TIMING_AND_SHAPING:
				AdvancedControllableProperty shapingControl = controlDropdown(extendedStatistics, shapingDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, shapingControl);

				keyProperty = streamName + HaivisionConstant.HASH;
				String bandwidthOverhead = keyProperty + CreateOutputStreamMetric.PARAMETER_BANDWIDTH_OVERHEAD.getName();
				String protocolMode = outputResponseItem.getShaping().toUpperCase();
				String overhead = outputResponseItem.getBandwidthOverhead();
				if (ProtocolDropdown.TS_OVER_SRT.getName().equals(protocolMode)) {
					AdvancedControllableProperty bandwidthControl = controlNumeric(extendedStatistics, bandwidthOverhead, overhead);
					addAdvanceControlProperties(advancedControllableProperties, bandwidthControl);
				} else {
					if (!TimingAndShaping.VBR.getName().equalsIgnoreCase(value)) {
						AdvancedControllableProperty bandwidthControl = controlNumeric(extendedStatistics, bandwidthOverhead, overhead);
						addAdvanceControlProperties(advancedControllableProperties, bandwidthControl);
					} else {
						extendedStatistics.remove(bandwidthOverhead);
					}
				}
				break;
			case PARAMETER_BANDWIDTH_OVERHEAD:
				if (Integer.parseInt(value) < HaivisionConstant.MIN_OF_BANDWIDTH_OVERHEAD) {
					value = Integer.toString(HaivisionConstant.MIN_OF_BANDWIDTH_OVERHEAD);
				}
				if (Integer.parseInt(value) > HaivisionConstant.MAX_OF_BANDWIDTH_OVERHEAD) {
					value = Integer.toString(HaivisionConstant.MAX_OF_BANDWIDTH_OVERHEAD);
				}

				AdvancedControllableProperty bandwidthControlProperty = controlNumeric(extendedStatistics, property, value);
				addAdvanceControlProperties(advancedControllableProperties, bandwidthControlProperty);
				outputResponseItem.setBandwidthOverhead(value);
				break;
			case PARAMETER_MTU:
				if (Integer.parseInt(value) < HaivisionConstant.MIN_OF_MTU) {
					value = Integer.toString(HaivisionConstant.MIN_OF_MTU);
				}
				if (Integer.parseInt(value) > HaivisionConstant.MAX_OF_MTU) {
					value = Integer.toString(HaivisionConstant.MAX_OF_MTU);
				}

				AdvancedControllableProperty mtuControlProperty = controlNumeric(extendedStatistics, property, value);
				addAdvanceControlProperties(advancedControllableProperties, mtuControlProperty);
				outputResponseItem.setMtu(value);
				break;
			case PARAMETER_TTL:
				if (Integer.parseInt(value) < HaivisionConstant.MIN_OF_TTL) {
					value = Integer.toString(HaivisionConstant.MIN_OF_TTL);
				}
				if (Integer.parseInt(value) > HaivisionConstant.MAX_OF_TTL) {
					value = Integer.toString(HaivisionConstant.MAX_OF_TTL);
				}

				AdvancedControllableProperty ttlControlProperty = controlNumeric(extendedStatistics, property, value);
				addAdvanceControlProperties(advancedControllableProperties, ttlControlProperty);
				outputResponseItem.setTtl(value);
				break;
			case PARAMETER_TOS:
				//Support hex value only
				if (value.startsWith("0x")) {
					String valueCopy = value.replace("0x", "");
					try {
						int decTos = Integer.parseInt(valueCopy, 16);
						int decMaxTos = Integer.parseInt(HaivisionConstant.MAX_OF_TOS, 16);
						int decMinTos = Integer.parseInt(HaivisionConstant.MIN_OF_TOS, 16);
						if (decTos < decMinTos) {
							value = "Ox" + HaivisionConstant.MIN_OF_TOS;
						}
						if (decTos > decMaxTos) {
							value = "0x" + HaivisionConstant.MAX_OF_TOS;
						}

						AdvancedControllableProperty toSControlProperty = controlText(extendedStatistics, property, value);
						addAdvanceControlProperties(advancedControllableProperties, toSControlProperty);
						outputResponseItem.setTos(value);
					} catch (Exception e) {
						throw new NumberFormatException("Value of ParameterToS is invalid. TOS must be between 0 to 255");
					}
				}
				break;
			case SOURCE_ADD_AUDIO:
				Map<String, Audio> addSourceAudio = sourceAudioResponse.get(streamName);
				editSourceAudioCreateOutputStream(streamName, addSourceAudio, extendedStatistics, advancedControllableProperties);
				break;
			case APPLY_CHANGE:
				OutputResponse outputResponse = convertOutputStreamByValue(extendedStatistics, streamName);

				// sent request to apply all change for all metric
				setOutputStreamApplyChange(outputResponse.payLoad(), outputResponse.getId());

				//sent request to action for the metric
				setActionOutputStreamControl(streamName, outputResponse);
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
			extendedStatistics.put(streamName + HaivisionConstant.HASH + CreateOutputStreamMetric.APPLY_CHANGE.getName(), "");
			advancedControllableProperties.add(createButton(streamName + HaivisionConstant.HASH + VideoControllingMetric.APPLY_CHANGE.getName(), HaivisionConstant.APPLY, "Applying", 0));

			extendedStatistics.put(streamName + HaivisionConstant.HASH + HaivisionConstant.EDITED, HaivisionConstant.TRUE);
			extendedStatistics.put(streamName + HaivisionConstant.HASH + HaivisionConstant.CANCEL, "");
			advancedControllableProperties.add(createButton(streamName + HaivisionConstant.HASH + HaivisionConstant.CANCEL, HaivisionConstant.CANCEL, HaivisionConstant.CANCEL, 0));
		}
	}

	/**
	 * Change OutputResponse by value
	 *
	 * @param extendedStatistics list extendedStatistics
	 * @param streamName the audio name is name of output stream
	 * @return OutputResponse
	 */
	private OutputResponse convertCreateOutputStreamByValue(Map<String, String> extendedStatistics, String streamName) {
		String mtuName = streamName + HaivisionConstant.HASH + CreateOutputStreamMetric.PARAMETER_MTU.getName();
		String ttlName = streamName + HaivisionConstant.HASH + CreateOutputStreamMetric.PARAMETER_TTL.getName();
		String tosName = streamName + HaivisionConstant.HASH + CreateOutputStreamMetric.PARAMETER_TOS.getName();
		String nameName = streamName + HaivisionConstant.HASH + CreateOutputStreamMetric.CONTENT_NAME.getName();
		String srtModeName = streamName + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_MODE.getName();
		String latencyName = streamName + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_LATENCY.getName();
		String addressName = streamName + HaivisionConstant.HASH + CreateOutputStreamMetric.DESTINATION_ADDRESS.getName();
		String encapsulationName = streamName + HaivisionConstant.HASH + CreateOutputStreamMetric.STREAMING_PROTOCOL.getName();
		String encryptionName = streamName + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_ENCRYPTION.getName();
		String passphraseName = streamName + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_PASSPHRASE.getName();
		String sourcePortName = streamName + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_SOURCE_PORT.getName();
		String adaptiveName = streamName + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_NETWORK_ADAPTIVE.getName();
		String shapingName = streamName + HaivisionConstant.HASH + CreateOutputStreamMetric.PARAMETER_TIMING_AND_SHAPING.getName();
		String sourceVideoName = streamName + HaivisionConstant.HASH + CreateOutputStreamMetric.SOURCE_VIDEO.getName();
		String sapName = streamName + HaivisionConstant.HASH + CreateOutputStreamMetric.SAP_NAME.getName();
		String sapKeywordsName = streamName + HaivisionConstant.HASH + CreateOutputStreamMetric.SAP_KEYWORDS.getName();
		String sapDescName = streamName + HaivisionConstant.HASH + CreateOutputStreamMetric.SAP_DESCRIPTION.getName();
		String sapAuthorName = streamName + HaivisionConstant.HASH + CreateOutputStreamMetric.SAP_AUTHOR.getName();
		String sapCopyrightName = streamName + HaivisionConstant.HASH + CreateOutputStreamMetric.SAP_COPYRIGHT.getName();
		String sapAddressName = streamName + HaivisionConstant.HASH + CreateOutputStreamMetric.SAP_ADDRESS.getName();
		String sapPortName = streamName + HaivisionConstant.HASH + CreateOutputStreamMetric.SAP_PORT.getName();
		String transmitSAPName = streamName + HaivisionConstant.HASH + CreateOutputStreamMetric.TRANSMIT_SAP.getName();
		String destinationPortName = streamName + HaivisionConstant.HASH + CreateOutputStreamMetric.DESTINATION_PORT.getName();
		String conectionDestinationPortName = streamName + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_DESTINATION_PORT.getName();
		String bandwidthOverHeadName = streamName + HaivisionConstant.HASH + CreateOutputStreamMetric.PARAMETER_BANDWIDTH_OVERHEAD.getName();

		OutputResponse outputItem = new OutputResponse();
		String encapsulation = checkNullValue(extendedStatistics.get(encapsulationName));
		String address = checkNullValue(extendedStatistics.get(addressName));
		String name = checkNullValue(extendedStatistics.get(nameName));
		String srtMode = checkNullValue(extendedStatistics.get(srtModeName));
		String sourcePort = checkNullValue(extendedStatistics.get(sourcePortName));
		String adaptive = checkNullValue(extendedStatistics.get(adaptiveName));
		String latency = checkNullValue(extendedStatistics.get(latencyName));
		String encryption = checkNullValue(extendedStatistics.get(encryptionName));
		String passphrase = checkNullValue(extendedStatistics.get(passphraseName));
		String sapContentName = checkNullValue(extendedStatistics.get(sapName));
		String sapKeywords = checkNullValue(extendedStatistics.get(sapKeywordsName));
		String sapDesc = checkNullValue(extendedStatistics.get(sapDescName));
		String sapAuthor = checkNullValue(extendedStatistics.get(sapAuthorName));
		String sapCopyright = checkNullValue(extendedStatistics.get(sapCopyrightName));
		String sapAddress = checkNullValue(extendedStatistics.get(sapAddressName));
		String sapPort = checkNullValue(extendedStatistics.get(sapPortName));
		String transmitSAP = checkNullValue(extendedStatistics.get(transmitSAPName));
		String destinationPort = checkNullValue(extendedStatistics.get(destinationPortName));
		String connectionDestinationPort = checkNullValue(extendedStatistics.get(conectionDestinationPortName));
		String sourceVideo = checkNullValue(extendedStatistics.get(sourceVideoName));
		String bandwidthOverHead = checkNullValue(extendedStatistics.get(bandwidthOverHeadName));

		String mtu = checkNullValue(extendedStatistics.get(mtuName));
		String ttl = checkNullValue(extendedStatistics.get(ttlName));
		String tos = checkNullValue(extendedStatistics.get(tosName));
		String shaping = checkNullValue(extendedStatistics.get(shapingName));

		List<Audio> audioList = new ArrayList<>();
		for (Audio audioName : sourceAudio.values()) {
			if (audioName != null && audioName.getId() != null) {
				audioList.add(audioName);
			}
		}
		Video video = new Video();
		video.setId(videoNameToVideoResponse.get(sourceVideo).getId());
		List<Video> videoList = new ArrayList<>();
		videoList.add(video);
		Map<String, Integer> protocol = ProtocolDropdown.getValueToNameMap();
		Map<String, Integer> srtModeOption = SRTModeDropdown.getValueToNameMap();
		Map<String, Integer> encryptionOption = EncryptionDropdown.getValueToNameMap();
		if (!StringUtils.isNullOrEmpty(srtMode)) {
			srtMode = String.valueOf(srtModeOption.get(srtMode));
		}
		if (!StringUtils.isNullOrEmpty(encryption)) {
			encryption = String.valueOf(encryptionOption.get(encryption));
		}
		if (StringUtils.isNullOrEmpty(latency)) {
			latency = HaivisionConstant.DEFAULT_LATENCY;
		}
		if (StringUtils.isNullOrEmpty(adaptive)) {
			adaptive = HaivisionConstant.ZERO;
		}
		if (StringUtils.isNullOrEmpty(transmitSAP)) {
			transmitSAP = HaivisionConstant.ZERO;
		}
		if (StringUtils.isNullOrEmpty(destinationPort)) {
			destinationPort = connectionDestinationPort;
		}
		if (StringUtils.isNullOrEmpty(bandwidthOverHead)) {
			if (ProtocolDropdown.TS_OVER_SRT.getName().equals(encapsulation)) {
				bandwidthOverHead = HaivisionConstant.DEFAULT_BANDWIDTH_SRT;
			} else {
				bandwidthOverHead = HaivisionConstant.DEFAULT_BANDWIDTH_UDP_RTP;
			}
		}
		OutputSAP outputSAP = new OutputSAP();
		outputSAP.setAdvertise(transmitSAP);
		outputSAP.setPort(sapPort);
		outputSAP.setCopyright(sapCopyright);
		outputSAP.setDesc(sapDesc);
		outputSAP.setAddress(sapAddress);
		outputSAP.setName(sapContentName);
		outputSAP.setKeywords(sapKeywords);
		outputSAP.setAuthor(sapAuthor);

		outputItem.setAddress(address);
		outputItem.setMtu(mtu);
		outputItem.setTtl(ttl);
		outputItem.setTos(tos);
		outputItem.setName(name);
		outputItem.setVideo(videoList);
		outputItem.setAudio(audioList);
		outputItem.setShaping(shaping.toLowerCase());
		outputItem.setPassphrase(passphrase);
		outputItem.setEncapsulation(String.valueOf(protocol.get(encapsulation)));
		outputItem.setSrtMode(srtMode);
		outputItem.setAdaptive(adaptive);
		outputItem.setLatency(latency);
		outputItem.setEncryption(encryption);
		outputItem.setOutputSAP(outputSAP);
		outputItem.setPort(destinationPort);
		outputItem.setSourcePort(sourcePort);
		outputItem.setBandwidthOverhead(bandwidthOverHead);

		return outputItem;
	}

	/**
	 * Change OutputResponse by value
	 *
	 * @param extendedStatistics list extendedStatistics
	 * @param streamName the audio name is name of output stream
	 * @return OutputResponse
	 */
	private OutputResponse convertOutputStreamByValue(Map<String, String> extendedStatistics, String streamName) {
		String property = streamName + HaivisionConstant.HASH;
		OutputResponse outputResponseItem = streamNameToStreamResponse.get(streamName);
		Map<String, Integer> protocolMap = ProtocolDropdown.getValueToNameMap();
		Map<String, Integer> srtModeMap = SRTModeDropdown.getValueToNameMap();

		String port = checkNoneStringValue(outputResponseItem.getPort());
		String sourcePort = checkNoneStringValue(outputResponseItem.getSourcePort());
		String encryption = checkNoneStringValue(outputResponseItem.getEncryption());
		String shaping = checkNoneStringValue(outputResponseItem.getShaping());
		String latency = checkNoneStringValue(outputResponseItem.getLatency());
		String adaptive = checkNoneStringValue(outputResponseItem.getAdaptive());
		String passphrase = checkNoneStringValue(outputResponseItem.getPassphrase());
		String srtListenerSecondPort = checkNoneStringValue(outputResponseItem.getSrtListenerSecondPort());
		String bandwidthOverhead = checkNoneStringValue(outputResponseItem.getBandwidthOverhead());
		String srtMode = String.valueOf(srtModeMap.get(extendedStatistics.get(property + CreateOutputStreamMetric.CONNECTION_MODE.getName())));
		String protocol = String.valueOf(protocolMap.get(extendedStatistics.get(property + CreateOutputStreamMetric.STREAMING_PROTOCOL.getName())));
		String name = extendedStatistics.get(property + CreateOutputStreamMetric.CONTENT_NAME.getName());

		OutputSAP outputSAP = outputResponseItem.getOutputSAP();
		outputSAP.setName(checkNoneStringValue(outputSAP.getName()));
		outputSAP.setDesc(checkNoneStringValue(outputSAP.getDesc()));
		outputSAP.setKeywords(checkNoneStringValue(outputSAP.getKeywords()));
		outputSAP.setAuthor(checkNoneStringValue(outputSAP.getAuthor()));
		outputSAP.setCopyright(checkNoneStringValue(outputSAP.getCopyright()));
		outputSAP.setAddress(checkNoneStringValue(outputSAP.getAddress()));
		String advertise = outputSAP.getAdvertise();
		if (StringUtils.isNullOrEmpty(advertise) || HaivisionConstant.NONE.equals(advertise)) {
			advertise = HaivisionConstant.ZERO;
		}
		outputSAP.setAdvertise(advertise);

		//handle case transmit SAP enable
		if (HaivisionConstant.ONE.equals(outputSAP.getAdvertise()) && StringUtils.isNullOrEmpty(outputSAP.getName())) {
			throw new ResourceNotReachableException("Can't set transmit SAP, Please enter a SAP name");
		}
		String portSAP = outputSAP.getPort();
		if (HaivisionConstant.NONE.equals(portSAP)) {
			portSAP = HaivisionConstant.EMPTY_STRING;
		}
		outputSAP.setPort(portSAP);
		Map<String, Audio> audioMap = sourceAudioResponse.get(streamName);
		List<Audio> audioList = new ArrayList<>();
		for (Audio audioName : audioMap.values()) {
			if (audioName != null && audioName.getId() != null) {
				audioList.add(audioName);
			}
		}

		outputResponseItem.setPort(port);
		outputResponseItem.setSourcePort(sourcePort);
		outputResponseItem.setEncryption(encryption);
		outputResponseItem.setShaping(shaping);
		outputResponseItem.setBandwidthOverhead(bandwidthOverhead);
		outputResponseItem.setAdaptive(adaptive);
		outputResponseItem.setPassphrase(passphrase);
		outputResponseItem.setSrtListenerSecondPort(srtListenerSecondPort);
		outputResponseItem.setAudio(audioList);
		outputResponseItem.setEncapsulation(protocol);
		outputResponseItem.setName(name);
		outputResponseItem.setSrtMode(srtMode);
		if (StringUtils.isNullOrEmpty(latency)) {
			latency = HaivisionConstant.DEFAULT_LATENCY;
		}
		outputResponseItem.setLatency(latency);

		return outputResponseItem;
	}

	/**
	 * Change VideoResponse by value
	 *
	 * @param extendedStatistics list extendedStatistics
	 * @param videoName the audio name is name of video
	 * @return VideoResponse
	 */
	private VideoResponse convertVideoByValue(Map<String, String> extendedStatistics, String videoName) {
		VideoResponse videoResponseItem = videoNameToVideoResponse.get(videoName);

		Map<String, Integer> codecAlgorithmMap = CodecAlgorithm.getValueToNameMap();
		Map<String, Integer> framingMap = FramingDropdown.getValueToNameMap();
		Map<String, Integer> timeCodeSourceMap = TimeCodeSource.getValueToNameMap();
		Map<String, Integer> aspectRatioMap = AspectRatioDropdown.getValueToNameMap();
		Map<String, Integer> inputMap = VideoInputDropdown.getValueToNameMap();
		Map<String, Integer> encodingProfileMap = EncodingProfile.getValueToNameMap();
		Map<String, Integer> chromaSubSamplingMap = ChromaSubSampling.getValueToNameMap();
		Map<String, Integer> rateControlMap = RateControlDropdown.getValueToNameMap();
		Map<String, Integer> timeCodeMap = TimeCodeSource.getValueToNameMap();
		Map<String, Integer> countingMap = CountingDropdown.getValueToNameMap();
		Map<String, Integer> reSyncHourMap = ReSyncHourDropdown.getValueToNameMap();
		Map<String, String> resolutionMap = ResolutionDropdown.getValueToNameMap();
		String gopSizeValue = getValueIfExits(extendedStatistics, videoName, VideoControllingMetric.GOP_SIZE, videoResponseItem.getGopSize());
		String croppingValue = getValueIfExits(extendedStatistics, videoName, VideoControllingMetric.CROPPING, videoResponseItem.getCropping());
		String slicesValue = getValueIfExits(extendedStatistics, videoName, VideoControllingMetric.SLICES, videoResponseItem.getSlices());
		String maxBitrateValue = getValueIfExits(extendedStatistics, videoName, VideoControllingMetric.MAX_BITRATE, videoResponseItem.getMaxBitrate());
		String intraRefreshValue = getValueIfExits(extendedStatistics, videoName, VideoControllingMetric.INTRA_REFRESH, videoResponseItem.getMaxBitrate());
		VideoResponse videoResponse = new VideoResponse();

		videoResponse.setCodecAlgorithm(String.valueOf(codecAlgorithmMap.get(extendedStatistics.get(videoName + HaivisionConstant.HASH + VideoControllingMetric.CODEC_ALGORITHM.getName()))));
		videoResponse.setGopStructure(String.valueOf(framingMap.get(extendedStatistics.get(videoName + HaivisionConstant.HASH + VideoControllingMetric.FRAMING.getName()))));
		videoResponse.setTimeCode(String.valueOf(timeCodeSourceMap.get(extendedStatistics.get(videoName + HaivisionConstant.HASH + VideoControllingMetric.TIME_CODE_SOURCE.getName()))));
		videoResponse.setAspectRatio(String.valueOf(aspectRatioMap.get(extendedStatistics.get(videoName + HaivisionConstant.HASH + VideoControllingMetric.ASPECT_RATIO.getName()))));
		videoResponse.setInputInterface(String.valueOf(inputMap.get(extendedStatistics.get(videoName + HaivisionConstant.HASH + VideoControllingMetric.INPUT.getName()))));
		videoResponse.setEncodingProfile(String.valueOf(encodingProfileMap.get(extendedStatistics.get(videoName + HaivisionConstant.HASH + VideoControllingMetric.ENCODING_PROFILE.getName()))));
		videoResponse.setChromaSubSampling(String.valueOf(chromaSubSamplingMap.get(extendedStatistics.get(videoName + HaivisionConstant.HASH + VideoControllingMetric.CHROMA_SUBSAMPLING.getName()))));
		videoResponse.setRateControl(String.valueOf(rateControlMap.get(extendedStatistics.get(videoName + HaivisionConstant.HASH + VideoControllingMetric.RATE_CONTROL.getName()))));
		videoResponse.setBitrate(extendedStatistics.get(videoName + HaivisionConstant.HASH + VideoControllingMetric.BITRATE.getName()));
		videoResponse.setClosedCaption(extendedStatistics.get(videoName + HaivisionConstant.HASH + VideoControllingMetric.CLOSED_CAPTION.getName()));
		videoResponse.setResolution(String.valueOf(resolutionMap.get(extendedStatistics.get(videoName + HaivisionConstant.HASH + VideoControllingMetric.RESOLUTION.getName()))));
		videoResponse.setTimeCode(String.valueOf(timeCodeMap.get(extendedStatistics.get(videoName + HaivisionConstant.HASH + VideoControllingMetric.TIME_CODE_SOURCE.getName()))));
		videoResponse.setName(videoName);
		videoResponse.setGopSize(gopSizeValue);
		videoResponse.setCropping(croppingValue);
		videoResponse.setSlices(slicesValue);
		videoResponse.setMaxBitrate(maxBitrateValue);
		videoResponse.setIntraRefresh(intraRefreshValue);
		videoResponse.setCountingMode(String.valueOf(countingMap.get(extendedStatistics.get(videoName + HaivisionConstant.HASH + VideoControllingMetric.COUNTING_MODE.getName()))));
		String frameRate = extendedStatistics.get(videoName + HaivisionConstant.HASH + VideoControllingMetric.FRAME_RATE.getName());
		if (FrameRateDropdown.FAME_RATE_0.getName().equals(frameRate)) {
			frameRate = HaivisionConstant.ZERO;
		}
		videoResponse.setFrameRate(frameRate);
		String id = "";
		String reSyncHourValue = "";
		String dailyReSync = HaivisionConstant.ZERO;
		VideoResponse video = videoNameToVideoResponse.get(videoName);
		if (video != null) {
			id = video.getId();

			//set dailySyncValue if exits
			String dailySyncValue = video.getDailyReSync();
			if (HaivisionConstant.TRUE.equalsIgnoreCase(dailySyncValue) || HaivisionConstant.NUMBER_ONE.equals(dailySyncValue)) {
				dailyReSync = HaivisionConstant.NUMBER_ONE;
			}

			//set ReSyncHour is exists
			reSyncHourValue = video.getReSyncHour();
			if (reSyncHourMap.get(reSyncHourValue) != null) {
				reSyncHourValue = String.valueOf(reSyncHourMap.get(reSyncHourValue));
			}
		}
		videoResponse.setId(id);
		videoResponse.setDailyReSync(dailyReSync);
		videoResponse.setReSyncHour(reSyncHourValue);

		return videoResponse;
	}

	/**
	 * Check the value if exists
	 *
	 * @param extendedStatistics is list extendedStatistics
	 * @param videoName the video name is name of video encoder
	 * @param metric is name of VideoControllingMetric
	 * @param value the value is value of video encoder
	 * @return String is String value
	 */
	private String getValueIfExits(Map<String, String> extendedStatistics, String videoName, VideoControllingMetric metric, String value) {
		String currentValue = extendedStatistics.get(videoName + HaivisionConstant.HASH + metric.getName());
		if (StringUtils.isNullOrEmpty(currentValue)) {
			currentValue = value;
		}
		return currentValue;
	}

	/**
	 * Save video apply change
	 *
	 * @param data the data is request body
	 * @param urlId the urlId is id of video encoder
	 */
	private void setVideoApplyChange(String data, String urlId) {
		try {
			JsonNode responseData = doPut(HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER) + HaivisionConstant.SLASH + urlId, data, JsonNode.class);
			if (responseData.get(HaivisionConstant.INFO) == null) {
				throw new ResourceNotReachableException(HaivisionConstant.NO_SET_ERROR_VIDEO);
			}
		} catch (Exception e) {
			throw new ResourceNotReachableException(HaivisionConstant.ERR_SET_CONTROL + e.getMessage());
		}
	}

	/**
	 * Save output stream encoder
	 *
	 * @param data the data is request body
	 * @param urlId the urlId is id of output stream encoder
	 */
	private void setOutputStreamApplyChange(String data, String urlId) {
		try {
			JsonNode responseData = doPut(HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.STREAM) + HaivisionConstant.SLASH + urlId, data, JsonNode.class);
			if (responseData.get(HaivisionConstant.INFO) == null) {
				throw new ResourceNotReachableException(HaivisionConstant.NO_SET_ERROR_STREAM);
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
			JsonNode responseData = doPut(HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.VIDEO_ENCODER) + HaivisionConstant.SLASH + urlId + HaivisionConstant.SLASH + action, data, JsonNode.class);
			if (responseData.get(HaivisionConstant.INFO) == null) {
				throw new ResourceNotReachableException(HaivisionConstant.NO_CHANGE_ACTION_VIDEO_ERROR);
			}
		} catch (Exception e) {
			throw new ResourceNotReachableException(HaivisionConstant.ERR_SET_CONTROL + e.getMessage());
		}
	}


	/**
	 * Sent request to action start/stop/delete for the output stream
	 *
	 * @param streamName the streamName is name of output stream
	 * @param outputResponse is instance OutputResponse DTO
	 */
	private void setActionOutputStreamControl(String streamName, OutputResponse outputResponse) {
		Map<String, String> extendedStatistics = localExtendedStatistics.getStatistics();
		String action = extendedStatistics.get(streamName + HaivisionConstant.HASH + CreateOutputStreamMetric.ACTION.getName());
		if (!HaivisionConstant.NONE.equals(action)) {
			changeOutputStreamAction(action.toLowerCase(), streamNameToStreamResponse.get(streamName).getId(), outputResponse.payLoad());
		}
	}

	/**
	 * Change action for output stream
	 *
	 * @param action the action is state of output stream
	 * @param urlId the urlId is id of output stream
	 * @param data the data is request body
	 */
	private void changeOutputStreamAction(String action, String urlId, String data) {
		try {
			if (HaivisionConstant.DELETE.equalsIgnoreCase(action)) {
				doDelete(HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.STREAM) + HaivisionConstant.SLASH + urlId);
			} else {
				JsonNode responseData = doPut(HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.STREAM) + HaivisionConstant.SLASH + urlId + HaivisionConstant.SLASH + action, data, JsonNode.class);
				if (responseData.get(HaivisionConstant.INFO) == null) {
					throw new ResourceNotReachableException(HaivisionConstant.NO_CHANGE_ACTION_STREAM_ERROR);
				}
			}
		} catch (Exception e) {
			throw new ResourceNotReachableException(HaivisionConstant.ERR_SET_CONTROL + e.getMessage());
		}
	}

	/**
	 * Sent request to action start/stop for the video
	 *
	 * @param videoName the audioName is name of video
	 * @param videoResponse is instance AudioResponse DTO
	 */
	private void setActionVideoControl(String videoName, VideoResponse videoResponse) {
		Map<String, String> extendedStatistics = localExtendedStatistics.getStatistics();
		String action = extendedStatistics.get(videoName + HaivisionConstant.HASH + VideoControllingMetric.ACTION.getName());
		if (!HaivisionConstant.NONE.equals(action)) {
			changeVideoAction(action.toLowerCase(), videoNameToVideoResponse.get(videoName).getId(), videoResponse.payLoad());
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
			failedMonitor.clear();
			throw new ResourceNotReachableException("Get monitoring data failed: " + stringBuilder);
		}
		getFilteredForEncoderStatistics();
		for (HaivisionURL haivisionURL : HaivisionURL.values()) {
			if (HaivisionURL.AUDIO_ENCODER.equals(haivisionURL)) {
				populateAudioData(stats, advancedControllableProperties);
			}
			if (HaivisionURL.VIDEO_ENCODER.equals(haivisionURL)) {
				populateVideoData(stats, advancedControllableProperties);
			}
			if (HaivisionURL.OUTPUT_ENCODER.equals(haivisionURL)) {
				populateOutputData(stats, advancedControllableProperties);
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
	 * @param advancedControllableProperties the advancedControllableProperties is list AdvancedControllableProperties
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
		String[] dropdownInput = DropdownList.Names(InputDropdown.class);
		String[] dropdownMode = DropdownList.Names(ChannelModeDropdown.class);
		String[] dropdownAlgorithm = DropdownList.Names(AlgorithmDropdown.class);
		String[] dropdownSampleRate = DropdownList.Names(SampleRateDropdown.class);
		String[] dropdownLanguage = DropdownList.Names(LanguageDropdown.class);
		String[] dropdownAction = HaivisionConstant.WORKING_AUDIO_VIDEO;
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
					if (StringUtils.isNullOrEmpty(value)) {
						value = HaivisionConstant.NONE;
					}
					AdvancedControllableProperty languageControlProperty = controlDropdownAcceptNoneValue(stats, dropdownLanguage, audioName + HaivisionConstant.HASH + audioMetric.getName(), value);
					addAdvanceControlProperties(advancedControllableProperties, languageControlProperty);
					break;
				case ACTION:
					stateAudio = audioResponseList.getState();
					value = getNameByValue(stateAudio, audioMap);
					//define action = Start
					if (AudioStateDropdown.STOPPED.getName().equals(value)) {
						dropdownAction = HaivisionConstant.NOT_WORKING_AUDIO_VIDEO;
					}
					AdvancedControllableProperty actionDropdownControlProperty = controlDropdownAcceptNoneValue(stats, dropdownAction, audioName + HaivisionConstant.HASH + audioMetric.getName(), value);
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
		Map<Integer, String> inputMap = VideoInputDropdown.getNameToValueMap();
		Map<Integer, String> encodingProfileMap = EncodingProfile.getNameToValueMap();
		Map<Integer, String> chromaSubSamplingMap = ChromaSubSampling.getNameToValueMap();
		Map<Integer, String> rateControlMap = RateControlDropdown.getNameToValueMap();
		Map<String, String> resolutionMap = ResolutionDropdown.getNameToValueMap();
		Map<Integer, String> countingMap = CountingDropdown.getNameToValueMap();
		Map<Integer, String> reSyncHourMap = ReSyncHourDropdown.getNameToValueMap();
		String[] dropdownCodecAlgorithm = DropdownList.Names(CodecAlgorithm.class);
		String[] dropdownEncodingProfile = EncodingProfile.namesIsAVG();
		String[] dropdownChromaSubSampling = ChromaSubSampling.namesIsBaselineOrMainOrHigh();
		String[] dropdownRateControl = DropdownList.Names(RateControlDropdown.class);
		String[] dropdownResolution = DropdownList.Names(ResolutionDropdown.class);
		String[] dropdownCropping = DropdownList.Names(CroppingDropdown.class);
		String[] dropdownFraming = DropdownList.Names(FramingDropdown.class);
		String[] dropdownTimeCodeSource = DropdownList.Names(TimeCodeSource.class);
		String[] dropdownAspectRatio = DropdownList.Names(AspectRatioDropdown.class);
		String[] inputDropdown = DropdownList.Names(VideoInputDropdown.class);
		String[] frameRateDropdown = DropdownList.Names(FrameRateDropdown.class);
		String[] dropdownSlices = DropdownList.Names(SlicesDropdown.class);
		String[] dropdownCounting = DropdownList.Names(CountingDropdown.class);
		String[] dropdownReSyncHour = DropdownList.Names(ReSyncHourDropdown.class);
		List<String> resolutionNotCropping = ResolutionDropdown.getDropdownListNotCropping();
		String[] dropdownAction = HaivisionConstant.WORKING_AUDIO_VIDEO;
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
					if (HaivisionConstant.INPUT_AUTO.equals(value) || HaivisionConstant.NONE.equals(value)) {
						value = HaivisionConstant.NO_INPUT;
					}
					stats.put(videoName + HaivisionConstant.HASH + videoMetric.getName(), value);
					break;
				case CODEC_ALGORITHM:
					String codecAlgorithm = videoResponseList.getCodecAlgorithm();
					value = getNameByValue(codecAlgorithm, codecAlgorithmMap);
					AdvancedControllableProperty codecAlgorithmControlProperty = controlDropdown(stats, dropdownCodecAlgorithm, videoName + HaivisionConstant.HASH + videoMetric.getName(), value);
					addAdvanceControlProperties(advancedControllableProperties, codecAlgorithmControlProperty);
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
						dropdownChromaSubSampling = DropdownList.Names(ChromaSubSampling.class);
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
						String maxBitRate = videoResponseList.getMaxBitrate();
						if (HaivisionConstant.ZERO.equals(maxBitRate)) {
							maxBitRate = HaivisionConstant.EMPTY_STRING;
						}
						AdvancedControllableProperty maxBitRateProperty = controlNumeric(stats, videoName + HaivisionConstant.HASH + videoMetric.getName(), maxBitRate);
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
					value = resolutionMap.get(resolution);
					AdvancedControllableProperty resolutionControlProperty = controlDropdown(stats, dropdownResolution, videoName + HaivisionConstant.HASH + videoMetric.getName(), value);
					addAdvanceControlProperties(advancedControllableProperties, resolutionControlProperty);
					break;
				case CROPPING:
					String resolutionMode = resolutionMap.get(videoResponseList.getResolution());
					if (!resolutionNotCropping.contains(resolutionMode)) {
						String cropping = videoResponseList.getCropping();
						value = getNameByValue(cropping, croppingMap);
						AdvancedControllableProperty croppingControlProperty = controlDropdown(stats, dropdownCropping, videoName + HaivisionConstant.HASH + videoMetric.getName(), value);
						addAdvanceControlProperties(advancedControllableProperties, croppingControlProperty);
					}
					break;
				case FRAME_RATE:
					String frameRate = videoResponseList.getFrameRate();
					if (frameRate.equals(HaivisionConstant.ZERO)) {
						frameRate = FrameRateDropdown.FAME_RATE_0.getName();
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
				case SLICES:
					String framingMode = videoResponseList.getGopStructure();
					value = getNameByValue(framingMode, framingMap);
					if (FramingDropdown.I.getName().equals(value) || FramingDropdown.IP.getName().equals(value)) {
						String slices = videoResponseList.getSlices();
						AdvancedControllableProperty slicesProperty = controlDropdown(stats, dropdownSlices, videoName + HaivisionConstant.HASH + videoMetric.getName(), slices);
						addAdvanceControlProperties(advancedControllableProperties, slicesProperty);
					}
					break;
				case INTRA_REFRESH:
					String intraRefresh = videoResponseList.getIntraRefresh();
					AdvancedControllableProperty intraRefreshProperty = controlSwitch(stats, videoName + HaivisionConstant.HASH + videoMetric.getName(), intraRefresh, HaivisionConstant.DISABLE,
							HaivisionConstant.ENABLE);
					addAdvanceControlProperties(advancedControllableProperties, intraRefreshProperty);
					break;
				case GOP_SIZE:
					String intraRefreshMode = videoResponseList.getIntraRefresh();
					if (!HaivisionConstant.NONE.equals(intraRefreshMode) && Integer.parseInt(intraRefreshMode) == 0) {
						String gopSize = videoResponseList.getGopSize();
						AdvancedControllableProperty gopSizeProperty = controlNumeric(stats, videoName + HaivisionConstant.HASH + videoMetric.getName(), gopSize);
						addAdvanceControlProperties(advancedControllableProperties, gopSizeProperty);
					}
					break;
				case CLOSED_CAPTION:
					String closedCaption = videoResponseList.getClosedCaption();
					AdvancedControllableProperty closedCaptionProperty = controlSwitch(stats, videoName + HaivisionConstant.HASH + videoMetric.getName(), closedCaption, HaivisionConstant.DISABLE,
							HaivisionConstant.ENABLE);
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
					if (VideoStateDropdown.STOPPED.getName().equals(value)) {
						dropdownAction = HaivisionConstant.NOT_WORKING_AUDIO_VIDEO;
					}
					AdvancedControllableProperty actionDropdownControlProperty = controlDropdownAcceptNoneValue(stats, dropdownAction, videoName + HaivisionConstant.HASH + videoMetric.getName(), value);
					addAdvanceControlProperties(advancedControllableProperties, actionDropdownControlProperty);
					break;
				case INPUT:
					String input = videoResponseList.getInputInterface();
					value = getNameByValue(input, inputMap);
					AdvancedControllableProperty inputDropdownControlProperty = controlDropdown(stats, inputDropdown, videoName + HaivisionConstant.HASH + videoMetric.getName(), value);
					addAdvanceControlProperties(advancedControllableProperties, inputDropdownControlProperty);
					break;
				case COUNTING_MODE:
					timeCodeSource = videoResponseList.getTimeCode();
					value = getNameByValue(timeCodeSource, timeCodeSourceMap);
					if (TimeCodeSource.SYSTEM.getName().equals(value)) {
						String counting = videoResponseList.getCountingMode();
						value = getNameByValue(counting, countingMap);
						AdvancedControllableProperty countingControlProperty = controlDropdown(stats, dropdownCounting, videoName + HaivisionConstant.HASH + videoMetric.getName(), value);
						addAdvanceControlProperties(advancedControllableProperties, countingControlProperty);
					}
					break;
				case DAILY_RESYNC:
					timeCodeSource = videoResponseList.getTimeCode();
					value = getNameByValue(timeCodeSource, timeCodeSourceMap);
					if (TimeCodeSource.SYSTEM.getName().equals(value)) {
						String countingMode = videoResponseList.getCountingMode();
						String countingOption = getNameByValue(countingMode, countingMap);
						if (CountingDropdown.SMPTE_12M_1.getName().equals(countingOption)) {
							String dailyResync = videoResponseList.getDailyReSync();
							String dailyReSyncValue = HaivisionConstant.ZERO;
							if (HaivisionConstant.TRUE.equalsIgnoreCase(dailyResync)) {
								dailyReSyncValue = HaivisionConstant.NUMBER_ONE;
							}
							AdvancedControllableProperty dailyReSyncProperty = controlSwitch(stats, videoName + HaivisionConstant.HASH + videoMetric.getName(), dailyReSyncValue, HaivisionConstant.DISABLE,
									HaivisionConstant.ENABLE);
							addAdvanceControlProperties(advancedControllableProperties, dailyReSyncProperty);
						}
					}
					break;
				case RESYNC_HOUR:
					timeCodeSource = videoResponseList.getTimeCode();
					value = getNameByValue(timeCodeSource, timeCodeSourceMap);
					if (TimeCodeSource.SYSTEM.getName().equals(value)) {
						String countingMode = videoResponseList.getCountingMode();
						String countingOption = getNameByValue(countingMode, countingMap);
						if (CountingDropdown.SMPTE_12M_1.getName().equals(countingOption)) {
							String dailyReSyncMode = videoResponseList.getDailyReSync();
							String dailyReSyncValue = HaivisionConstant.ZERO;
							if (HaivisionConstant.TRUE.equalsIgnoreCase(dailyReSyncMode)) {
								dailyReSyncValue = HaivisionConstant.NUMBER_ONE;
							}
							if (HaivisionConstant.NUMBER_ONE.equals(dailyReSyncValue)) {
								String reSyncHour = videoResponseList.getReSyncHour();
								value = getNameByValue(reSyncHour, reSyncHourMap);
								AdvancedControllableProperty reSyncHourControlProperty = controlDropdown(stats, dropdownReSyncHour, videoName + HaivisionConstant.HASH + videoMetric.getName(), value);
								addAdvanceControlProperties(advancedControllableProperties, reSyncHourControlProperty);
							}
						}
					}
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
			advancedControllableProperties.removeIf(item -> item.getName().equals(property.getName()));
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
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 * @param stats list statistics property
	 */
	private void populateVideoData(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		if (!videoStatisticsList.isEmpty()) {
			for (VideoResponse videoResponses : videoStatisticsList) {
				addVideoDataStatisticsToStatisticsProperty(stats, videoResponses);
			}
		} else if (!videoResponseList.isEmpty() && !isAdapterFilter) {
			for (VideoResponse videoResponses : videoResponseList) {
				addVideoDataStatisticsToStatisticsProperty(stats, videoResponses);
			}
		}
		if (HaivisionConstant.OPERATOR.equals(roleBased) || HaivisionConstant.ADMIN.equals(roleBased)) {
			for (VideoResponse videoResponse : videoResponseList) {
				addVideoDataControlToProperty(stats, videoResponse, advancedControllableProperties);
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
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 * @param stats list statistics property
	 */
	private void populateOutputData(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		List<OutputResponse> outputResponseControlList;
		if (isAdapterFilter) {
			if (!outputForPortAndStatusList.isEmpty()) {
				for (OutputResponse outputResponses : outputForPortAndStatusList) {
					addOutputStreamDataStatisticsToStatisticsProperty(stats, outputResponses);
				}
				outputResponseControlList = outputForPortAndStatusList;
			} else {
				for (OutputResponse outputResponses : outputStatisticsList) {
					addOutputStreamDataStatisticsToStatisticsProperty(stats, outputResponses);
				}
				outputResponseControlList = outputStatisticsList;
			}
		} else {
			for (OutputResponse outputResponses : outputResponseList) {
				addOutputStreamDataStatisticsToStatisticsProperty(stats, outputResponses);
			}
			outputResponseControlList = outputResponseList;
		}
		if (HaivisionConstant.OPERATOR.equals(roleBased) || HaivisionConstant.ADMIN.equals(roleBased)) {
			sourceAudioResponse.clear();
			streamNameToStreamResponse.clear();
			for (OutputResponse outputResponses : outputResponseControlList) {
				retrieveStreamOutputById(outputResponses);
				String name = HaivisionConstant.STREAM + HaivisionConstant.SPACE + convertStreamNameUnescapeHtml3(outputResponses.getName());
				Map<String, Audio> sourceAudioMap = new HashMap<>();
				List<Audio> audio = outputResponses.getAudio();
				int index = 0;
				if (audio != null) {
					index = audio.size();
				}
				for (int i = 0; i < HaivisionConstant.MAX_SOURCE_AUDIO_DROPDOWN; i++) {
					if (i < index) {
						sourceAudioMap.put(CreateOutputStreamMetric.SOURCE_AUDIO.getName() + HaivisionConstant.SPACE + i, audio.get(i));
					} else {
						sourceAudioMap.put(CreateOutputStreamMetric.SOURCE_AUDIO.getName() + HaivisionConstant.SPACE + i, null);
					}
				}
				sourceAudioResponse.put(name, sourceAudioMap);
				addOutputStreamControlToProperty(stats, streamNameToStreamResponse.get(name), advancedControllableProperties);
			}
		}
	}

	/**
	 * Add Output Stream data to property
	 *
	 * @param stats list statistics property
	 * @param outputResponse list of out stream response
	 * @param advancedControllableProperties the advancedControllableProperties is advancedControllableProperties instance
	 */
	private void addOutputStreamControlToProperty(Map<String, String> stats, OutputResponse outputResponse, List<AdvancedControllableProperty> advancedControllableProperties) {
		String value;
		String protocolMode;
		String connectionMode;
		String protocol = outputResponse.getEncapsulation();
		String srtMode = outputResponse.getSrtMode();
		String[] dropdownAction = HaivisionConstant.WORKING_STREAM;
		OutputSAP outputSAP = outputResponse.getOutputSAP();
		String transmitSAP = HaivisionConstant.NONE;
		if (outputSAP != null) {
			transmitSAP = outputSAP.getAdvertise();
		}
		String srtProtocol = outputResponse.getEncapsulation();
		String[] shapingDropdown = DropdownList.Names(TimingAndShaping.class);
		String[] videoDropdown = DropdownList.Names(VideoDropdown.class);
		String[] protocolDropdown = DropdownList.Names(ProtocolDropdown.class);
		String[] srtModeDropdown = DropdownList.Names(SRTModeDropdown.class);
		String[] encryptionDropdown = DropdownList.Names(EncryptionDropdown.class);
		Map<Integer, String> protocolMap = ProtocolDropdown.getNameToValueMap();
		Map<Integer, String> srtModeMap = SRTModeDropdown.getNameToValueMap();
		Map<Integer, String> stateMap = OutputStateDropdown.getNameToValueMap();
		Map<Integer, String> encryptionMap = EncryptionDropdown.getNameToValueMap();
		String protocolOption = null;
		if (!HaivisionConstant.NONE.equals(srtProtocol)) {
			protocolOption = protocolMap.get(Integer.parseInt(srtProtocol));
		}
		if (protocolOption == null) {
			protocolOption = srtProtocol;
		}
		String bandwidthOverHead =
				HaivisionConstant.STREAM + HaivisionConstant.SPACE + convertStreamNameUnescapeHtml3(outputResponse.getName()) + HaivisionConstant.HASH + CreateOutputStreamMetric.AVERAGE_BANDWIDTH.getName();
		for (CreateOutputStreamMetric streamMetric : CreateOutputStreamMetric.values()) {
			String streamName = HaivisionConstant.STREAM + HaivisionConstant.SPACE + convertStreamNameUnescapeHtml3(outputResponse.getName()) + HaivisionConstant.HASH + streamMetric.getName();
			switch (streamMetric) {
				case STATE:
					String stateStream = outputResponse.getState();
					value = getNameByValue(stateStream, stateMap);
					stats.put(streamName, value);
					break;
				case CONTENT_NAME:
					value = checkNoneStringValue(outputResponse.getName());
					AdvancedControllableProperty contentNameControl = controlText(stats, streamName, convertStreamNameUnescapeHtml3(value));
					addAdvanceControlProperties(advancedControllableProperties, contentNameControl);
					break;
				case DESTINATION_ADDRESS:
					protocolMode = getNameByValue(protocol, protocolMap);
					if (!ProtocolDropdown.TS_OVER_SRT.getName().equals(protocolMode)) {
						value = checkNoneStringValue(outputResponse.getAddress());
						AdvancedControllableProperty destinationAddress = controlText(stats, streamName, value);
						addAdvanceControlProperties(advancedControllableProperties, destinationAddress);
					}
					break;
				case DESTINATION_PORT:
					protocolMode = getNameByValue(protocol, protocolMap);
					if (!ProtocolDropdown.TS_OVER_SRT.getName().equals(protocolMode)) {
						value = checkNoneStringValue(outputResponse.getPort());
						AdvancedControllableProperty destinationPort = controlText(stats, streamName, value);
						addAdvanceControlProperties(advancedControllableProperties, destinationPort);
					}
					break;
				case PARAMETER_TIMING_AND_SHAPING:
					value = outputResponse.getShaping().toUpperCase();
					AdvancedControllableProperty shapingControl = controlDropdown(stats, shapingDropdown, streamName, value);
					addAdvanceControlProperties(advancedControllableProperties, shapingControl);
					break;
				case PARAMETER_BANDWIDTH_OVERHEAD:
					protocolMode = getNameByValue(protocol, protocolMap);
					value = outputResponse.getShaping().toUpperCase();
					String overhead = outputResponse.getBandwidthOverhead();
					if (ProtocolDropdown.TS_OVER_SRT.getName().equals(protocolMode)) {

						AdvancedControllableProperty bandwidthControl = controlNumeric(stats, streamName, overhead);
						addAdvanceControlProperties(advancedControllableProperties, bandwidthControl);
					} else {
						if (!TimingAndShaping.VBR.getName().equalsIgnoreCase(value)) {

							AdvancedControllableProperty bandwidthControl = controlNumeric(stats, streamName, overhead);
							addAdvanceControlProperties(advancedControllableProperties, bandwidthControl);
						}
					}
					break;
				case PARAMETER_TOS:
					value = checkNoneStringValue(outputResponse.getTos());
					AdvancedControllableProperty tosControl = controlText(stats, streamName, value);
					addAdvanceControlProperties(advancedControllableProperties, tosControl);
					break;
				case PARAMETER_TTL:
					value = checkNoneStringValue(outputResponse.getTtl());
					AdvancedControllableProperty ttlControl = controlNumeric(stats, streamName, value);
					addAdvanceControlProperties(advancedControllableProperties, ttlControl);
					break;
				case PARAMETER_MTU:
					value = checkNoneStringValue(outputResponse.getMtu());
					AdvancedControllableProperty mtuControl = controlNumeric(stats, streamName, value);
					addAdvanceControlProperties(advancedControllableProperties, mtuControl);
					break;
				case SOURCE_VIDEO:
					String sourceVideo = HaivisionConstant.NONE;
					List<Video> videos = outputResponse.getVideo();
					AdvancedControllableProperty sourceVideoControl;
					if (videos != null) {
						sourceVideo = videos.get(0).getName();
						sourceVideoControl = controlDropdown(stats, videoDropdown, streamName, sourceVideo);
					} else {
						sourceVideoControl = controlDropdownAcceptNoneValue(stats, videoDropdown, streamName, sourceVideo);
					}
					addAdvanceControlProperties(advancedControllableProperties, sourceVideoControl);
					break;
				case STREAMING_PROTOCOL:
					String protocolValue = getNameByValue(protocol, protocolMap);
					AdvancedControllableProperty protocolControl = controlDropdown(stats, protocolDropdown, streamName, protocolValue);
					addAdvanceControlProperties(advancedControllableProperties, protocolControl);
					if (!ProtocolDropdown.TS_OVER_UDP.getName().equals(protocolValue)) {
						stats.put(bandwidthOverHead, outputResponse.getBandwidthEstimate());
					}
					break;
				case TRANSMIT_SAP:
					if (HaivisionConstant.NONE.equals(transmitSAP)) {
						transmitSAP = HaivisionConstant.ZERO;
					}
					AdvancedControllableProperty transmitControl = controlSwitch(stats, streamName, transmitSAP, HaivisionConstant.DISABLE, HaivisionConstant.ENABLE);
					addAdvanceControlProperties(advancedControllableProperties, transmitControl);
					break;
				case SAP_NAME:
					if (HaivisionConstant.NUMBER_ONE.equals(transmitSAP) && !ProtocolDropdown.TS_OVER_SRT.getName().equals(protocolOption)) {
						value = checkNoneStringValue(outputSAP.getName());
						AdvancedControllableProperty sapNameControl = controlText(stats, streamName, value);
						addAdvanceControlProperties(advancedControllableProperties, sapNameControl);
					}
					break;
				case SAP_KEYWORDS:
					if (HaivisionConstant.NUMBER_ONE.equals(transmitSAP) && !ProtocolDropdown.TS_OVER_SRT.getName().equals(protocolOption)) {
						value = checkNoneStringValue(outputSAP.getKeywords());
						AdvancedControllableProperty keywordsControl = controlText(stats, streamName, value);
						addAdvanceControlProperties(advancedControllableProperties, keywordsControl);
					}
					break;
				case SAP_DESCRIPTION:
					if (HaivisionConstant.NUMBER_ONE.equals(transmitSAP) && !ProtocolDropdown.TS_OVER_SRT.getName().equals(protocolOption)) {
						value = checkNoneStringValue(outputSAP.getDesc());
						AdvancedControllableProperty descControl = controlText(stats, streamName, value);
						addAdvanceControlProperties(advancedControllableProperties, descControl);
					}
					break;
				case SAP_AUTHOR:
					if (HaivisionConstant.NUMBER_ONE.equals(transmitSAP) && !ProtocolDropdown.TS_OVER_SRT.getName().equals(protocolOption)) {
						value = checkNoneStringValue(outputSAP.getAuthor());
						AdvancedControllableProperty authorControl = controlText(stats, streamName, value);
						addAdvanceControlProperties(advancedControllableProperties, authorControl);
					}
					break;
				case SAP_COPYRIGHT:
					if (HaivisionConstant.NUMBER_ONE.equals(transmitSAP) && !ProtocolDropdown.TS_OVER_SRT.getName().equals(protocolOption)) {
						value = checkNoneStringValue(outputSAP.getCopyright());
						AdvancedControllableProperty copyrightControl = controlText(stats, streamName, value);
						addAdvanceControlProperties(advancedControllableProperties, copyrightControl);
					}
					break;
				case SAP_ADDRESS:
					if (HaivisionConstant.NUMBER_ONE.equals(transmitSAP) && !ProtocolDropdown.TS_OVER_SRT.getName().equals(protocolOption)) {
						value = checkNoneStringValue(outputSAP.getAddress());
						AdvancedControllableProperty addressControl = controlText(stats, streamName, value);
						addAdvanceControlProperties(advancedControllableProperties, addressControl);
					}
					break;
				case SAP_PORT:
					if (HaivisionConstant.NUMBER_ONE.equals(transmitSAP) && !ProtocolDropdown.TS_OVER_SRT.getName().equals(protocolOption)) {
						value = checkNoneStringValue(outputSAP.getPort());
						AdvancedControllableProperty portControl = controlText(stats, streamName, value);
						addAdvanceControlProperties(advancedControllableProperties, portControl);
					}
					break;
				case CONNECTION_MODE:
					protocolMode = getNameByValue(protocol, protocolMap);
					if (ProtocolDropdown.TS_OVER_SRT.getName().equals(protocolMode)) {
						String mode = outputResponse.getSrtMode();
						value = getNameByValue(mode, srtModeMap);
						AdvancedControllableProperty modeControl = controlDropdown(stats, srtModeDropdown, streamName, value);
						addAdvanceControlProperties(advancedControllableProperties, modeControl);
					}
					break;
				case CONNECTION_ADDRESS:
					connectionMode = getNameByValue(srtMode, srtModeMap);
					protocolMode = getNameByValue(protocol, protocolMap);
					if (ProtocolDropdown.TS_OVER_SRT.getName().equals(protocolMode) && !SRTModeDropdown.LISTENER.getName().equals(connectionMode)) {
						value = checkNoneStringValue(outputResponse.getAddress());
						AdvancedControllableProperty connectionAddressControl = controlText(stats, streamName, value);
						addAdvanceControlProperties(advancedControllableProperties, connectionAddressControl);
					}
					break;
				case CONNECTION_SOURCE_PORT:
					protocolMode = getNameByValue(protocol, protocolMap);
					String mode = outputResponse.getSrtMode();
					String modeOption = getNameByValue(mode, srtModeMap);
					if (ProtocolDropdown.TS_OVER_SRT.getName().equals(protocolMode) && !SRTModeDropdown.LISTENER.getName().equals(modeOption)) {
						String sourcePort = outputResponse.getSourcePort();
						value = checkNoneStringValue(sourcePort);
						if (HaivisionConstant.ZERO.equals(value)) {
							value = HaivisionConstant.EMPTY_STRING;
						}
						AdvancedControllableProperty connectionAddressControl = controlNumeric(stats, streamName, value);
						addAdvanceControlProperties(advancedControllableProperties, connectionAddressControl);
					}
					break;
				case CONNECTION_DESTINATION_PORT:
					protocolMode = getNameByValue(protocol, protocolMap);
					mode = outputResponse.getSrtMode();
					modeOption = getNameByValue(mode, srtModeMap);
					if (ProtocolDropdown.TS_OVER_SRT.getName().equals(protocolMode) && !SRTModeDropdown.LISTENER.getName().equals(modeOption)) {
						value = checkNoneStringValue(outputResponse.getPort());
						AdvancedControllableProperty connectionPort = controlNumeric(stats, streamName, value);
						addAdvanceControlProperties(advancedControllableProperties, connectionPort);
					}
					break;
				case CONNECTION_NETWORK_ADAPTIVE:
					protocolMode = getNameByValue(protocol, protocolMap);
					if (ProtocolDropdown.TS_OVER_SRT.getName().equals(protocolMode)) {
						value = checkNoneStringValue(outputResponse.getAdaptive());
						AdvancedControllableProperty connectionAdaptive = controlSwitch(stats, streamName, value, HaivisionConstant.DISABLE, HaivisionConstant.DISABLE);
						addAdvanceControlProperties(advancedControllableProperties, connectionAdaptive);
					}
					break;
				case CONNECTION_LATENCY:
					protocolMode = getNameByValue(protocol, protocolMap);
					if (ProtocolDropdown.TS_OVER_SRT.getName().equals(protocolMode)) {
						value = checkNoneStringValue(outputResponse.getLatency());
						AdvancedControllableProperty connectionLatency = controlNumeric(stats, streamName, value);
						addAdvanceControlProperties(advancedControllableProperties, connectionLatency);
					}
					break;
				case CONNECTION_ENCRYPTION:
					protocolMode = getNameByValue(protocol, protocolMap);
					if (ProtocolDropdown.TS_OVER_SRT.getName().equals(protocolMode)) {
						value = encryptionMap.get(Integer.parseInt(outputResponse.getEncryption()));
						AdvancedControllableProperty connectionEncryption = controlDropdownAcceptNoneValue(stats, encryptionDropdown, streamName, value);
						addAdvanceControlProperties(advancedControllableProperties, connectionEncryption);
					}
					break;
				case CONNECTION_PASSPHRASE:
					protocolMode = getNameByValue(protocol, protocolMap);
					String encryptionMode = outputResponse.getEncryption();
					if (!HaivisionConstant.NONE.equals(encryptionMode)) {
						encryptionMode = encryptionMap.get(Integer.parseInt(outputResponse.getEncryption()));
					}
					if (ProtocolDropdown.TS_OVER_SRT.getName().equals(protocolMode) && !HaivisionConstant.NONE.equals(encryptionMode)) {
						value = checkNoneStringValue(outputResponse.getPassphrase());
						AdvancedControllableProperty connectionPassphrase = controlText(stats, streamName, value);
						addAdvanceControlProperties(advancedControllableProperties, connectionPassphrase);
					}
					break;
				case CONNECTION_PORT:
					connectionMode = getNameByValue(srtMode, srtModeMap);
					protocolMode = getNameByValue(protocol, protocolMap);
					if (ProtocolDropdown.TS_OVER_SRT.getName().equals(protocolMode) && SRTModeDropdown.LISTENER.getName().equals(connectionMode)) {
						value = checkNoneStringValue(outputResponse.getPort());
						AdvancedControllableProperty connectionPort = controlNumeric(stats, streamName, value);
						addAdvanceControlProperties(advancedControllableProperties, connectionPort);
					}
					break;
				case CONNECTION_ALTERNATE_PORT:
					connectionMode = getNameByValue(srtMode, srtModeMap);
					protocolMode = getNameByValue(protocol, protocolMap);
					if (ProtocolDropdown.TS_OVER_SRT.getName().equals(protocolMode) && SRTModeDropdown.LISTENER.getName().equals(connectionMode)) {
						value = checkNoneStringValue(outputResponse.getSrtListenerSecondPort());
						AdvancedControllableProperty connectionPort = controlNumeric(stats, streamName, value);
						addAdvanceControlProperties(advancedControllableProperties, connectionPort);
					}
					break;
				case ACTION:
					String state = outputResponse.getState();
					value = getNameByValue(state, stateMap);
					if (VideoStateDropdown.STOPPED.getName().equals(value)) {
						dropdownAction = HaivisionConstant.NOT_WORKING_STREAM;
					}
					AdvancedControllableProperty actionDropdownControlProperty = controlDropdownAcceptNoneValue(stats, dropdownAction, streamName, value);
					addAdvanceControlProperties(advancedControllableProperties, actionDropdownControlProperty);
					break;
				case SOURCE_ADD_AUDIO:
					advancedControllableProperties.add(controlButton(stats, streamName, HaivisionConstant.PLUS, HaivisionConstant.PLUS, 0));
					break;
				case SOURCE_AUDIO:
					String name = HaivisionConstant.STREAM + HaivisionConstant.SPACE + convertStreamNameUnescapeHtml3(outputResponse.getName());
					Map<String, Audio> sourceAudioMap = sourceAudioResponse.get(name);
					editSourceAudioControlStream(name, sourceAudioMap, stats, advancedControllableProperties);
					break;
				default:
					break;
			}
		}
	}

	/**
	 * Edit new Source Audio by output stream
	 *
	 * @param streamName the streamName is name of stream output
	 * @param sourceAudio the sourceAudio is Map<String,Audio>
	 * @param stats the stats is list statistics
	 * @param advancedControllablePropertyList list AdvancedControllableProperty instance
	 */
	private void editSourceAudioControlStream(String streamName, Map<String, Audio> sourceAudio, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllablePropertyList) {
		audioNameToAudioResponse.put(HaivisionConstant.NONE, new AudioResponse());
		List<String> audioName = new ArrayList<>(audioNameToAudioResponse.keySet());
		Collections.sort(audioName);
		String[] audioNames = audioName.toArray(new String[audioName.size()]);
		String defaultName = CreateOutputStreamMetric.SOURCE_AUDIO.getName() + HaivisionConstant.SPACE + HaivisionConstant.ZERO;
		for (Map.Entry<String, Audio> sourceAudioMap : sourceAudio.entrySet()) {
			String audioKey = String.valueOf(sourceAudioMap.getKey());
			if (sourceAudio.get(defaultName) == null) {
				advancedControllablePropertyList.add(controlDropdownAcceptNoneValue(stats, audioNames, streamName + HaivisionConstant.HASH + audioKey, HaivisionConstant.NONE));
				break;
			}
			if (sourceAudioMap.getValue() != null) {
				String key = sourceAudio.get(audioKey).getName();
				advancedControllablePropertyList.add(controlDropdown(stats, audioNames, streamName + HaivisionConstant.HASH + audioKey, key));
			}
		}
	}

	/**
	 * Edit new Source Audio Create Output
	 *
	 * @param streamName the streamName is name of stream output
	 * @param sourceAudio the sourceAudio is Map<String,Audio>
	 * @param stats the stats is list statistics
	 * @param advancedControllablePropertyList list AdvancedControllableProperty instance
	 */
	private void editSourceAudioCreateOutputStream(String streamName, Map<String, Audio> sourceAudio, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllablePropertyList) {
		List<String> audioName = new ArrayList<>(audioNameToAudioResponse.keySet());
		Collections.sort(audioName);
		String[] audioNames = audioName.toArray(new String[audioName.size()]);
		String defaultName = CreateOutputStreamMetric.SOURCE_AUDIO.getName() + HaivisionConstant.SPACE + HaivisionConstant.ZERO;
		for (Map.Entry<String, Audio> audioSourceKey : sourceAudio.entrySet()) {
			if (sourceAudio.get(defaultName) == null) {
				advancedControllablePropertyList.add(controlDropdownAcceptNoneValue(stats, audioNames, streamName + HaivisionConstant.HASH + audioSourceKey.getKey(), HaivisionConstant.NONE));
			} else if (audioSourceKey.getValue() == null) {
				advancedControllablePropertyList.add(controlDropdown(stats, audioNames, streamName + HaivisionConstant.HASH + audioSourceKey.getKey(), audioNames[0]));
				Audio audio = new Audio();
				audioSourceKey.setValue(audio);
				break;
			}
		}
	}

	/**
	 * Add new Source Audio for the create output stream
	 *
	 * @param stats iss list statistics
	 * @param advancedControllablePropertyList is list AdvancedControllableProperty instance
	 */
	private void addSourceAudioCreateOutputStream(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllablePropertyList) {
		String prefixName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH;
		List<String> audioName = new ArrayList<>(audioNameToAudioResponse.keySet());
		Collections.sort(audioName);
		String[] audioNames = audioName.toArray(new String[audioName.size()]);
		for (Map.Entry<String, Audio> element : sourceAudio.entrySet()) {
			if (element.getValue() == null) {
				advancedControllablePropertyList.add(controlDropdown(stats, audioNames, prefixName + element.getKey(), audioNames[0]));
				Audio audio = new Audio();
				audio.setId(audioNameToAudioResponse.get(audioNames[0]).getId());
				element.setValue(audio);
				break;
			}
		}
	}

	/**
	 * Check the value is None
	 *
	 * @param value the value is string
	 * @return String is the value
	 */
	private String checkNoneStringValue(String value) {
		if (HaivisionConstant.NONE.equals(value)) {
			return HaivisionConstant.EMPTY_STRING;
		}
		return value;
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

			if (outputStreamMetric.isReplaceComma()) {
				stats.put(name + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + HaivisionConstant.HASH + outputStreamMetric.getName(),
						replaceCommaByEmptyString(outputStatistic.getValueByMetric(outputStreamMetric)));
			} else if (outputStreamMetric.isNormalize()) {
				stats.put(name + HaivisionConstant.SPACE + HaivisionConstant.STATISTICS + HaivisionConstant.HASH + outputStreamMetric.getName(),
						extractBitRate(outputStatistic.getValueByMetric(outputStreamMetric)));
			} else if (OutputMonitoringMetric.STATE.equals(outputStreamMetric)) {
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
			audioNameToAudioResponse.clear();
			for (AudioResponse audioItem : audioResponse.getData()) {
				audioResponseList.add(audioItem);
				audioNameToAudioResponse.put(audioItem.getName(), audioItem);
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
			outputResponseList.addAll(outputResponse.getData());
		} catch (Exception e) {
			failedMonitor.put(HaivisionURL.OUTPUT_ENCODER.getName(), e.getMessage());
		}
	}

	/**
	 * Retrieve output stream encoder by id
	 *
	 * @param outputItem Output DTO instance
	 */
	private void retrieveStreamOutputById(OutputResponse outputItem) {
		try {
			if (outputItem.getId() != null) {
				OutputResponse data = doGet(HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.STREAM) + HaivisionConstant.SLASH + outputItem.getId(), OutputResponse.class);
				streamNameToStreamResponse.put(HaivisionConstant.STREAM + HaivisionConstant.SPACE + convertStreamNameUnescapeHtml3(outputItem.getName()), data);
			}
		} catch (Exception ex) {
			streamNameToStreamResponse.put(HaivisionConstant.STREAM + HaivisionConstant.SPACE + convertStreamNameUnescapeHtml3(outputItem.getName()), outputItem);
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
	private AdvancedControllableProperty controlDropdownAcceptNoneValue(Map<String, String> stats, String[] options, String name, String value) {

		//handle case accept None value
		String nameMetric = name.split(HaivisionConstant.HASH)[1];
		if (nameMetric.equals(AudioControllingMetric.ACTION.getName())) {
			stats.put(name, HaivisionConstant.NONE);
			return createDropdown(name, options, HaivisionConstant.NONE);
		}
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
	 * Add Button is control property for metric
	 *
	 * @param stats list statistic
	 * @param name String name of metric
	 * @param label label of the button
	 * @param labelPressed label of the button after pressing it
	 * @param gracePeriod grace period of button
	 * @return AdvancedControllableProperty Button instance
	 */
	private AdvancedControllableProperty controlButton(Map<String, String> stats, String name, String label, String labelPressed, long gracePeriod) {
		stats.put(name, HaivisionConstant.EMPTY_STRING);
		return createButton(name, label, labelPressed, gracePeriod);
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
	 * Create text is control property for metric
	 *
	 * @param name the name of the property
	 * @param stringValue character string
	 * @return AdvancedControllableProperty Text instance
	 */
	private AdvancedControllableProperty createText(String name, String stringValue) {
		AdvancedControllableProperty.Text text = new AdvancedControllableProperty.Text();

		return new AdvancedControllableProperty(name, new Date(), text, stringValue);
	}

	/**
	 * Add text is control property for metric
	 *
	 * @param stats list statistic
	 * @param name String name of metric
	 * @return AdvancedControllableProperty text instance
	 */
	private AdvancedControllableProperty controlText(Map<String, String> stats, String name, String value) {
		stats.put(name, value);

		return createText(name, value);
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
			if (responseData.get(HaivisionConstant.INFO) == null) {
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
			if (responseData.get(HaivisionConstant.INFO) == null) {
				throw new ResourceNotReachableException(HaivisionConstant.NO_CHANGE_ACTION_AUDIO_ERROR);
			}
		} catch (Exception e) {
			throw new ResourceNotReachableException(HaivisionConstant.ERR_SET_CONTROL + e.getMessage());
		}
	}

	/**
	 * Check value is null
	 *
	 * @param value the value is String
	 * @return value or empty string
	 */
	private String checkNullValue(String value) {
		if (StringUtils.isNullOrEmpty(value)) {
			return HaivisionConstant.EMPTY_STRING;
		}
		return value;
	}

	/**
	 * Create output stream
	 *
	 * @param stats is list statistics
	 * @param advancedControllableProperties is list advancedControllableProperties
	 */
	private void streamCreateOutput(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		String prefixName = HaivisionConstant.STREAM_CREATE_OUTPUT + HaivisionConstant.HASH;
		String[] timingAndShapingValues = DropdownList.Names(TimingAndShaping.class);
		String[] videoNames = DropdownList.Names(VideoDropdown.class);
		String[] protocolList = DropdownList.Names(ProtocolDropdown.class);

		advancedControllableProperties.add(controlButton(stats, prefixName + CreateOutputStreamMetric.ACTION.getName(), HaivisionConstant.CREATE, HaivisionConstant.CREATE, 0));
		advancedControllableProperties.add(controlText(stats, prefixName + CreateOutputStreamMetric.CONTENT_NAME.getName(), HaivisionConstant.EMPTY_STRING));
		advancedControllableProperties.add(controlText(stats, prefixName + CreateOutputStreamMetric.DESTINATION_ADDRESS.getName(), HaivisionConstant.EMPTY_STRING));
		advancedControllableProperties.add(controlNumeric(stats, prefixName + CreateOutputStreamMetric.DESTINATION_PORT.getName(), HaivisionConstant.EMPTY_STRING));
		advancedControllableProperties.add(controlNumeric(stats, prefixName + CreateOutputStreamMetric.PARAMETER_MTU.getName(), HaivisionConstant.DEFAULT_MTU));
		advancedControllableProperties.add(controlDropdown(stats, timingAndShapingValues, prefixName + CreateOutputStreamMetric.PARAMETER_TIMING_AND_SHAPING.getName(), TimingAndShaping.VBR.getName()));
		advancedControllableProperties.add(controlText(stats, prefixName + CreateOutputStreamMetric.PARAMETER_TOS.getName(), HaivisionConstant.DEFAULT_TOS));
		advancedControllableProperties.add(controlNumeric(stats, prefixName + CreateOutputStreamMetric.PARAMETER_TTL.getName(), HaivisionConstant.DEFAULT_TTL));
		advancedControllableProperties.add(controlButton(stats, prefixName + CreateOutputStreamMetric.SOURCE_ADD_AUDIO.getName(), HaivisionConstant.PLUS, HaivisionConstant.PLUS, 0));

		//Initialize source audio list
		for (int index = 0; index < HaivisionConstant.MAX_SOURCE_AUDIO_DROPDOWN; index++) {
			sourceAudio.put(CreateOutputStreamMetric.SOURCE_AUDIO.getName() + HaivisionConstant.SPACE + index, null);
		}
		addSourceAudioCreateOutputStream(stats, advancedControllableProperties);

		advancedControllableProperties.add(controlDropdown(stats, videoNames, prefixName + CreateOutputStreamMetric.SOURCE_VIDEO.getName(), VideoDropdown.VIDEO_0.getName()));
		advancedControllableProperties.add(controlSwitch(stats, prefixName + CreateOutputStreamMetric.TRANSMIT_SAP.getName(), HaivisionConstant.ZERO, HaivisionConstant.DISABLE, HaivisionConstant.ENABLE));
		advancedControllableProperties.add(controlDropdown(stats, protocolList, prefixName + CreateOutputStreamMetric.STREAMING_PROTOCOL.getName(), ProtocolDropdown.TS_OVER_UDP.getName()));
	}

	/**
	 * Control Stream Create Output
	 *
	 * @param property the property is the filed name of controlling metric
	 * @param value the value is value of metric
	 * @param updateExtendedStatistic list updateExtendedStatistic
	 * @param advancedControllableProperties the advancedControllableProperties is advancedControllableProperties instance
	 */
	private void controlStreamCreateOutput(String property, String value, Map<String, String> updateExtendedStatistic, List<AdvancedControllableProperty> advancedControllableProperties) {
		String prefixName = HaivisionConstant.STREAM_CREATE_OUTPUT;
		String propertyName = property.split(HaivisionConstant.HASH)[1];
		String[] encryptionDropdown = DropdownList.Names(EncryptionDropdown.class);
		String[] audioList = audioNameToAudioResponse.keySet().toArray(new String[audioNameToAudioResponse.size()]);
		String[] shapingDropdown = DropdownList.Names(TimingAndShaping.class);
		String[] videoDropdown = DropdownList.Names(VideoDropdown.class);
		isCreateStreamCalled = true;
		//Control Source Audio
		if (propertyName.contains(CreateOutputStreamMetric.SOURCE_AUDIO.getName())) {
			if (HaivisionConstant.NONE.equals(value) && !(HaivisionConstant.SOURCE_AUDIO_0.equals(propertyName))) {
				updateExtendedStatistic.remove(property);
				sourceAudio.remove(propertyName);
				sourceAudio.put(propertyName, null);
			} else {
				AdvancedControllableProperty sourceAudioControlProperty = controlDropdown(updateExtendedStatistic, audioList, property, value);
				addAdvanceControlProperties(advancedControllableProperties, sourceAudioControlProperty);
				Audio audio = new Audio();
				audio.setId(audioNameToAudioResponse.get(value).getId());
				sourceAudio.put(propertyName, null);
			}
			if (isCreateStreamCalled) {

				updateExtendedStatistic.put(prefixName + HaivisionConstant.HASH + HaivisionConstant.EDITED, HaivisionConstant.TRUE);
				updateExtendedStatistic.put(prefixName + HaivisionConstant.HASH + HaivisionConstant.CANCEL, "");
				advancedControllableProperties.add(createButton(prefixName + HaivisionConstant.HASH + HaivisionConstant.CANCEL, HaivisionConstant.CANCEL, HaivisionConstant.CANCEL, 0));
			}
			return;
		}
		CreateOutputStreamMetric createOutputStreamMetric = CreateOutputStreamMetric.getByName(propertyName);
		switch (createOutputStreamMetric) {
			//Control text
			case CONTENT_NAME:
			case SAP_ADDRESS:
			case SAP_AUTHOR:
			case SAP_COPYRIGHT:
			case SAP_DESCRIPTION:
			case SAP_KEYWORDS:
			case SAP_NAME:
			case CONNECTION_ADDRESS:
			case DESTINATION_ADDRESS:
				AdvancedControllableProperty textControlProperty = controlText(updateExtendedStatistic, property, value);
				addAdvanceControlProperties(advancedControllableProperties, textControlProperty);
				break;
			case SAP_PORT:
			case DESTINATION_PORT:
				AdvancedControllableProperty portControl = controlNumeric(updateExtendedStatistic, property, value);
				addAdvanceControlProperties(advancedControllableProperties, portControl);
				break;
			case CONNECTION_DESTINATION_PORT:
				int destinationPort = Integer.parseInt(value);
				if (destinationPort < HaivisionConstant.SOURCE_PORT_MIN || destinationPort > HaivisionConstant.SOURCE_PORT_MAX) {
					throw new ResourceNotReachableException("Value of destination port is invalid. Destination port must be between 1 to 65535");
				}
				AdvancedControllableProperty destinationPortProperty = controlNumeric(updateExtendedStatistic, property, value);
				addAdvanceControlProperties(advancedControllableProperties, destinationPortProperty);
				break;
			case SOURCE_ADD_AUDIO:
				addSourceAudioCreateOutputStream(updateExtendedStatistic, advancedControllableProperties);
				break;
			case SOURCE_VIDEO:
				AdvancedControllableProperty sourceVideoControl = controlDropdownAcceptNoneValue(updateExtendedStatistic, videoDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, sourceVideoControl);
				break;
			case TRANSMIT_SAP:
				AdvancedControllableProperty transmitSapControl = controlSwitch(updateExtendedStatistic, property, value, HaivisionConstant.DISABLE, HaivisionConstant.ENABLE);
				addAdvanceControlProperties(advancedControllableProperties, transmitSapControl);

				String keyProperty = prefixName + HaivisionConstant.HASH;
				String sapName = keyProperty + CreateOutputStreamMetric.SAP_NAME.getName();
				String keywordsName = keyProperty + CreateOutputStreamMetric.SAP_KEYWORDS.getName();
				String descName = keyProperty + CreateOutputStreamMetric.SAP_DESCRIPTION.getName();
				String authorName = keyProperty + CreateOutputStreamMetric.SAP_AUTHOR.getName();
				String copyrightName = keyProperty + CreateOutputStreamMetric.SAP_COPYRIGHT.getName();
				String addressName = keyProperty + CreateOutputStreamMetric.SAP_ADDRESS.getName();
				String portName = keyProperty + CreateOutputStreamMetric.SAP_PORT.getName();

				if (HaivisionConstant.ONE.equals(value)) {
					String authorValue = checkNullValue(updateExtendedStatistic.get(authorName));
					String addressValue = checkNullValue(updateExtendedStatistic.get(addressName));
					String copyrightValue = checkNullValue(updateExtendedStatistic.get(copyrightName));
					String descValue = checkNullValue(updateExtendedStatistic.get(descName));
					String keywordsValue = checkNullValue(updateExtendedStatistic.get(keywordsName));
					String sapValue = checkNullValue(updateExtendedStatistic.get(sapName));
					String portValue = checkNullValue(updateExtendedStatistic.get(portName));

					advancedControllableProperties.add(controlText(updateExtendedStatistic, addressName, addressValue));
					advancedControllableProperties.add(controlText(updateExtendedStatistic, authorName, authorValue));
					advancedControllableProperties.add(controlText(updateExtendedStatistic, copyrightName, copyrightValue));
					advancedControllableProperties.add(controlText(updateExtendedStatistic, descName, descValue));
					advancedControllableProperties.add(controlText(updateExtendedStatistic, keywordsName, keywordsValue));
					advancedControllableProperties.add(controlText(updateExtendedStatistic, sapName, sapValue));
					advancedControllableProperties.add(controlNumeric(updateExtendedStatistic, portName, portValue));
				} else {
					updateExtendedStatistic.remove(sapName);
					updateExtendedStatistic.remove(keywordsName);
					updateExtendedStatistic.remove(descName);
					updateExtendedStatistic.remove(authorName);
					updateExtendedStatistic.remove(copyrightName);
					updateExtendedStatistic.remove(addressName);
					updateExtendedStatistic.remove(portName);
				}
				break;
			//Control numeric
			case CONNECTION_SOURCE_PORT:
				int sourcePort = Integer.parseInt(value);
				if (sourcePort < HaivisionConstant.SOURCE_PORT_MIN || sourcePort > HaivisionConstant.SOURCE_PORT_MAX) {
					throw new ResourceNotReachableException("Value of source port is invalid. Source port must be between 1 to 65535");
				}
				AdvancedControllableProperty connectionSourcePortControl = controlNumeric(updateExtendedStatistic, property, value);
				addAdvanceControlProperties(advancedControllableProperties, connectionSourcePortControl);
				break;
			case CONNECTION_LATENCY:
				if (Integer.parseInt(value) < HaivisionConstant.MIN_OF_LATENCY) {
					value = Integer.toString(HaivisionConstant.MIN_OF_LATENCY);
				}
				if (Integer.parseInt(value) > HaivisionConstant.MAX_OF_LATENCY) {
					value = Integer.toString(HaivisionConstant.MAX_OF_LATENCY);
				}

				AdvancedControllableProperty latencyControlProperty = controlNumeric(updateExtendedStatistic, property, value);
				addAdvanceControlProperties(advancedControllableProperties, latencyControlProperty);
				break;
			case CONNECTION_PASSPHRASE:
				if (value.length() >= HaivisionConstant.MIN_OF_PASSPHRASE_LENGTH || value.length() <= HaivisionConstant.MAX_OF_PASSPHRASE_LENGTH) {
					AdvancedControllableProperty passControlProperty = controlText(updateExtendedStatistic, property, value);
					addAdvanceControlProperties(advancedControllableProperties, passControlProperty);
				} else {
					throw new ResourceNotReachableException("Passphrase is too short or to long.");
				}
				break;
			case PARAMETER_TOS:
				//Support hex value only
				if (value.startsWith("0x")) {
					String valueCopy = value.replace("0x", "");
					try {
						int decTos = Integer.parseInt(valueCopy, 16);
						int decMaxTos = Integer.parseInt(HaivisionConstant.MAX_OF_TOS, 16);
						int decMinTos = Integer.parseInt(HaivisionConstant.MIN_OF_TOS, 16);
						if (decTos < decMinTos) {
							value = "0x" + HaivisionConstant.MIN_OF_TOS;
						}
						if (decTos > decMaxTos) {
							value = "0x" + HaivisionConstant.MAX_OF_TOS;
						}

						AdvancedControllableProperty toSControlProperty = controlText(updateExtendedStatistic, property, value);
						addAdvanceControlProperties(advancedControllableProperties, toSControlProperty);
					} catch (Exception e) {
						throw new NumberFormatException("Value of ParameterToS is invalid. TOS must be between 0 to 255");
					}
				}
				break;
			case PARAMETER_MTU:
				if (Integer.parseInt(value) < HaivisionConstant.MIN_OF_MTU) {
					value = Integer.toString(HaivisionConstant.MIN_OF_MTU);
				}
				if (Integer.parseInt(value) > HaivisionConstant.MAX_OF_MTU) {
					value = Integer.toString(HaivisionConstant.MAX_OF_MTU);
				}

				AdvancedControllableProperty mtuControlProperty = controlNumeric(updateExtendedStatistic, property, value);
				addAdvanceControlProperties(advancedControllableProperties, mtuControlProperty);
				break;
			case PARAMETER_TTL:
				if (Integer.parseInt(value) < HaivisionConstant.MIN_OF_TTL) {
					value = Integer.toString(HaivisionConstant.MIN_OF_TTL);
				}
				if (Integer.parseInt(value) > HaivisionConstant.MAX_OF_TTL) {
					value = Integer.toString(HaivisionConstant.MAX_OF_TTL);
				}

				AdvancedControllableProperty ttlControlProperty = controlNumeric(updateExtendedStatistic, property, value);
				addAdvanceControlProperties(advancedControllableProperties, ttlControlProperty);
				break;
			case PARAMETER_BANDWIDTH_OVERHEAD:
				if (Integer.parseInt(value) < HaivisionConstant.MIN_OF_BANDWIDTH_OVERHEAD) {
					value = Integer.toString(HaivisionConstant.MIN_OF_BANDWIDTH_OVERHEAD);
				}
				if (Integer.parseInt(value) > HaivisionConstant.MAX_OF_BANDWIDTH_OVERHEAD) {
					value = Integer.toString(HaivisionConstant.MAX_OF_BANDWIDTH_OVERHEAD);
				}

				AdvancedControllableProperty bandwidthControlProperty = controlNumeric(updateExtendedStatistic, property, value);
				addAdvanceControlProperties(advancedControllableProperties, bandwidthControlProperty);
				break;
			case CONNECTION_ALTERNATE_PORT:
				int alternatePort = Integer.parseInt(value);
				if (alternatePort < HaivisionConstant.SOURCE_PORT_MIN || alternatePort > HaivisionConstant.SOURCE_PORT_MAX) {
					throw new ResourceNotReachableException("Value of alternation port is invalid. Alternation port must be between 1 to 65535");
				}
				AdvancedControllableProperty alternatePortProperty = controlNumeric(updateExtendedStatistic, property, value);
				addAdvanceControlProperties(advancedControllableProperties, alternatePortProperty);
				break;
			case PARAMETER_TIMING_AND_SHAPING:
				AdvancedControllableProperty shapingControlProperty = controlDropdown(updateExtendedStatistic, shapingDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, shapingControlProperty);

				//Update bandwidth overHead
				String nameProtocol = prefixName + HaivisionConstant.HASH + CreateOutputStreamMetric.STREAMING_PROTOCOL.getName();
				String bandwidthOverhead = prefixName + HaivisionConstant.HASH + CreateOutputStreamMetric.PARAMETER_BANDWIDTH_OVERHEAD.getName();
				String protocolMode = updateExtendedStatistic.get(nameProtocol);
				if (ProtocolDropdown.TS_OVER_SRT.getName().equals(protocolMode)) {
					AdvancedControllableProperty bandwidthControl = controlNumeric(updateExtendedStatistic, bandwidthOverhead, HaivisionConstant.DEFAULT_BANDWIDTH_SRT);
					addAdvanceControlProperties(advancedControllableProperties, bandwidthControl);
				} else {
					if (!TimingAndShaping.VBR.getName().equalsIgnoreCase(value)) {
						AdvancedControllableProperty bandwidthControl = controlNumeric(updateExtendedStatistic, bandwidthOverhead, HaivisionConstant.DEFAULT_BANDWIDTH_UDP_RTP);
						addAdvanceControlProperties(advancedControllableProperties, bandwidthControl);
					} else {
						updateExtendedStatistic.remove(bandwidthOverhead);
					}
				}
				break;
			case STREAMING_PROTOCOL:
				keyProperty = prefixName + HaivisionConstant.HASH;
				addressName = keyProperty + CreateOutputStreamMetric.CONNECTION_ADDRESS.getName();
				portName = keyProperty + CreateOutputStreamMetric.CONNECTION_PORT.getName();
				String desPortName = keyProperty + CreateOutputStreamMetric.DESTINATION_PORT.getName();
				String modeName = keyProperty + CreateOutputStreamMetric.CONNECTION_MODE.getName();
				String latencyName = keyProperty + CreateOutputStreamMetric.CONNECTION_LATENCY.getName();
				String encryptionName = keyProperty + CreateOutputStreamMetric.CONNECTION_ENCRYPTION.getName();
				String passPhraseName = keyProperty + CreateOutputStreamMetric.CONNECTION_PASSPHRASE.getName();
				String alternatePortName = keyProperty + CreateOutputStreamMetric.CONNECTION_ALTERNATE_PORT.getName();
				String sourcePortName = keyProperty + CreateOutputStreamMetric.CONNECTION_SOURCE_PORT.getName();
				String networkAdapterName = keyProperty + CreateOutputStreamMetric.CONNECTION_NETWORK_ADAPTIVE.getName();
				String destinationPortName = keyProperty + CreateOutputStreamMetric.CONNECTION_DESTINATION_PORT.getName();
				String destinationAddressName = keyProperty + CreateOutputStreamMetric.DESTINATION_ADDRESS.getName();
				String[] srtModeDropdown = DropdownList.Names(SRTModeDropdown.class);
				String[] protocolDropdown = DropdownList.Names(ProtocolDropdown.class);

				AdvancedControllableProperty protocolControl = controlDropdown(updateExtendedStatistic, protocolDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, protocolControl);

				if (ProtocolDropdown.TS_OVER_SRT.getName().equals(value)) {
					String connectionMode = updateExtendedStatistic.get(modeName);
					if (StringUtils.isNullOrEmpty(connectionMode)) {
						connectionMode = SRTModeDropdown.CALLER.getName();
					}
					AdvancedControllableProperty modeControl = controlDropdown(updateExtendedStatistic, srtModeDropdown, modeName, connectionMode);
					addAdvanceControlProperties(advancedControllableProperties, modeControl);

					//Add address
					value = checkNullValue(updateExtendedStatistic.get(addressName));
					AdvancedControllableProperty connectionAddressControl = controlText(updateExtendedStatistic, addressName, value);
					addAdvanceControlProperties(advancedControllableProperties, connectionAddressControl);

					//Add sourcePort => is SourcePort Response
					value = checkNullValue(updateExtendedStatistic.get(sourcePortName));
					AdvancedControllableProperty connectionSourcePort = controlNumeric(updateExtendedStatistic, sourcePortName, value);
					addAdvanceControlProperties(advancedControllableProperties, connectionSourcePort);

					//destinationPort is port Response
					value = checkNullValue(updateExtendedStatistic.get(destinationPortName));

					AdvancedControllableProperty connectionDestinationPortControl = controlNumeric(updateExtendedStatistic, destinationPortName, value);
					addAdvanceControlProperties(advancedControllableProperties, connectionDestinationPortControl);

					if (SRTModeDropdown.RENDEZVOUS.getName().equals(connectionMode)) {

						//Update sourcePort = DestinationPort and not accept control
						value = checkNullValue(updateExtendedStatistic.get(portName));
						updateExtendedStatistic.put(sourcePortName, value);
						advancedControllableProperties.removeIf(item -> item.getName().equals(sourcePortName));
					}
					if (SRTModeDropdown.CALLER.getName().equals(connectionMode) || SRTModeDropdown.RENDEZVOUS.getName().equals(connectionMode)) {

						//remove port and alternatePort
						updateExtendedStatistic.remove(portName);
						updateExtendedStatistic.remove(alternatePortName);
						updateExtendedStatistic.remove(desPortName);
						updateExtendedStatistic.remove(destinationAddressName);
					}
					if (SRTModeDropdown.LISTENER.getName().equals(connectionMode)) {

						//Update Port
						value = checkNullValue(updateExtendedStatistic.get(portName));
						AdvancedControllableProperty connectionPortControl = controlNumeric(updateExtendedStatistic, portName, value);
						addAdvanceControlProperties(advancedControllableProperties, connectionPortControl);

						//Update alternatePortName
						String alternateValue = checkNullValue(updateExtendedStatistic.get(alternatePortName));
						AdvancedControllableProperty alternatePortControl = controlNumeric(updateExtendedStatistic, alternatePortName, alternateValue);
						addAdvanceControlProperties(advancedControllableProperties, alternatePortControl);

						//remove sourcePort and destinationPort
						updateExtendedStatistic.remove(sourcePortName);
						updateExtendedStatistic.remove(destinationPortName);
						updateExtendedStatistic.remove(addressName);
					}

					// NetworkAdpter
					value = checkNullValue(updateExtendedStatistic.get(networkAdapterName));
					if (StringUtils.isNullOrEmpty(value)) {
						value = HaivisionConstant.ZERO;
					}
					AdvancedControllableProperty connectionAdaptive = controlSwitch(updateExtendedStatistic, networkAdapterName, value, HaivisionConstant.DISABLE, HaivisionConstant.DISABLE);
					addAdvanceControlProperties(advancedControllableProperties, connectionAdaptive);

					//Latency
					value = checkNullValue(updateExtendedStatistic.get(latencyName));
					if (StringUtils.isNullOrEmpty(value)) {
						value = HaivisionConstant.DEFAULT_LATENCY;
					}
					AdvancedControllableProperty connectionLatency = controlNumeric(updateExtendedStatistic, latencyName, value);
					addAdvanceControlProperties(advancedControllableProperties, connectionLatency);

					//Encryption
					value = checkNullValue(updateExtendedStatistic.get(encryptionName));
					if (StringUtils.isNullOrEmpty(value)) {
						value = HaivisionConstant.NONE;
					}
					AdvancedControllableProperty connectionEncryption = controlDropdownAcceptNoneValue(updateExtendedStatistic, encryptionDropdown, encryptionName, value);
					addAdvanceControlProperties(advancedControllableProperties, connectionEncryption);

					if (!HaivisionConstant.NONE.equals(value)) {
						value = checkNullValue(updateExtendedStatistic.get(passPhraseName));
						AdvancedControllableProperty connectionPassphrase = controlText(updateExtendedStatistic, passPhraseName, value);
						addAdvanceControlProperties(advancedControllableProperties, connectionPassphrase);
					}
					break;
				} else {
					//Add address
					value = checkNullValue(updateExtendedStatistic.get(addressName));
					AdvancedControllableProperty connectionAddressControl = controlText(updateExtendedStatistic, addressName, value);
					addAdvanceControlProperties(advancedControllableProperties, connectionAddressControl);

					//Update Port
					value = checkNullValue(updateExtendedStatistic.get(portName));
					AdvancedControllableProperty connectionPortControl = controlNumeric(updateExtendedStatistic, portName, value);
					addAdvanceControlProperties(advancedControllableProperties, connectionPortControl);

					updateExtendedStatistic.remove(modeName);
					updateExtendedStatistic.remove(destinationPortName);
					updateExtendedStatistic.remove(sourcePortName);
					updateExtendedStatistic.remove(networkAdapterName);
					updateExtendedStatistic.remove(networkAdapterName);
					updateExtendedStatistic.remove(encryptionName);
					updateExtendedStatistic.remove(passPhraseName);
					updateExtendedStatistic.remove(alternatePortName);
					updateExtendedStatistic.remove(latencyName);
				}
				break;
			case CONNECTION_MODE:
				srtModeDropdown = DropdownList.Names(SRTModeDropdown.class);
				AdvancedControllableProperty modeControlProperty = controlDropdown(updateExtendedStatistic, srtModeDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, modeControlProperty);

				keyProperty = prefixName + HaivisionConstant.HASH;
				addressName = keyProperty + CreateOutputStreamMetric.CONNECTION_ADDRESS.getName();
				destinationPortName = keyProperty + CreateOutputStreamMetric.CONNECTION_DESTINATION_PORT.getName();
				sourcePortName = keyProperty + CreateOutputStreamMetric.CONNECTION_SOURCE_PORT.getName();
				portName = keyProperty + CreateOutputStreamMetric.CONNECTION_PORT.getName();
				alternatePortName = keyProperty + CreateOutputStreamMetric.CONNECTION_ALTERNATE_PORT.getName();

				if (SRTModeDropdown.RENDEZVOUS.getName().equals(value)) {

					//Add address
					String addressValue = checkNullValue(updateExtendedStatistic.get(addressName));
					AdvancedControllableProperty connectionAddressControl = controlText(updateExtendedStatistic, addressName, addressValue);
					addAdvanceControlProperties(advancedControllableProperties, connectionAddressControl);

					//Add sourcePort => is SourcePort Response
					String sourcePortValue = checkNullValue(updateExtendedStatistic.get(sourcePortName));
					AdvancedControllableProperty sourcePortControl = controlNumeric(updateExtendedStatistic, sourcePortName, sourcePortValue);
					addAdvanceControlProperties(advancedControllableProperties, sourcePortControl);

					//destinationPort is port Response
					String portValue = checkNullValue(updateExtendedStatistic.get(destinationPortName));
					AdvancedControllableProperty connectionDestinationPortControl = controlNumeric(updateExtendedStatistic, destinationPortName, portValue);
					addAdvanceControlProperties(advancedControllableProperties, connectionDestinationPortControl);

					//Update sourcePort = DestinationPort and not accept control
					String sourcePortValueRemove = checkNullValue(updateExtendedStatistic.get(sourcePortName));
					updateExtendedStatistic.put(sourcePortName, sourcePortValueRemove);
					advancedControllableProperties.removeIf(item -> item.getName().equals(sourcePortName));

					//remove port and alternatePort
					updateExtendedStatistic.remove(portName);
					updateExtendedStatistic.remove(alternatePortName);

				}
				if (SRTModeDropdown.LISTENER.getName().equals(value)) {

					//Update Port
					String portValue = checkNullValue(updateExtendedStatistic.get(portName));
					AdvancedControllableProperty connectionPortControl = controlNumeric(updateExtendedStatistic, portName, portValue);
					addAdvanceControlProperties(advancedControllableProperties, connectionPortControl);

					//Update alternatePortName
					String alternateValue = checkNullValue(updateExtendedStatistic.get(alternatePortName));
					AdvancedControllableProperty alternatePortControl = controlNumeric(updateExtendedStatistic, alternatePortName, alternateValue);
					addAdvanceControlProperties(advancedControllableProperties, alternatePortControl);

					//remove sourcePort and destinationPort
					updateExtendedStatistic.remove(sourcePortName);
					updateExtendedStatistic.remove(destinationPortName);
					updateExtendedStatistic.remove(addressName);
				}

				if (SRTModeDropdown.CALLER.getName().equals(value)) {

					//Add address
					String addressValue = checkNullValue(updateExtendedStatistic.get(addressName));
					AdvancedControllableProperty connectionAddressControl = controlText(updateExtendedStatistic, addressName, addressValue);
					addAdvanceControlProperties(advancedControllableProperties, connectionAddressControl);

					//Add sourcePort => is SourcePort Response
					String sourcePortValue = checkNullValue(updateExtendedStatistic.get(sourcePortName));
					connectionSourcePortControl = controlNumeric(updateExtendedStatistic, sourcePortName, sourcePortValue);
					addAdvanceControlProperties(advancedControllableProperties, connectionSourcePortControl);

					//destinationPort is port Response
					String portValue = checkNullValue(updateExtendedStatistic.get(destinationPortName));
					AdvancedControllableProperty connectionDestinationPortControl = controlNumeric(updateExtendedStatistic, destinationPortName, portValue);
					addAdvanceControlProperties(advancedControllableProperties, connectionDestinationPortControl);

					//remove port and alternatePort
					updateExtendedStatistic.remove(portName);
					updateExtendedStatistic.remove(alternatePortName);
				}
				break;
			case CONNECTION_ENCRYPTION:
				AdvancedControllableProperty encryptionControlProperty = controlDropdown(updateExtendedStatistic, encryptionDropdown, property, value);
				addAdvanceControlProperties(advancedControllableProperties, encryptionControlProperty);
				if (EncryptionDropdown.AES_128.getName().equals(value) || EncryptionDropdown.AES_256.getName().equals(value)) {
					advancedControllableProperties.add(controlText(updateExtendedStatistic, prefixName + CreateOutputStreamMetric.CONNECTION_PASSPHRASE.getName(), HaivisionConstant.EMPTY_STRING));
				} else {
					String connectionPassphrase = prefixName + HaivisionConstant.HASH + CreateOutputStreamMetric.CONNECTION_PASSPHRASE.getName();
					updateExtendedStatistic.remove(connectionPassphrase);
				}
				break;
			case CONNECTION_NETWORK_ADAPTIVE:
				AdvancedControllableProperty networkAdaptiveControlProperty = controlSwitch(updateExtendedStatistic, property, value,
						HaivisionConstant.DISABLE, HaivisionConstant.ENABLE);
				addAdvanceControlProperties(advancedControllableProperties, networkAdaptiveControlProperty);
				break;
			case ACTION:
				OutputResponse outputResponse = convertCreateOutputStreamByValue(updateExtendedStatistic, prefixName);

				// sent request to apply all change for all metric
				setOutputStreamAction(outputResponse.payLoad());
				isCreateStreamCalled = false;
				break;
			case CANCEL:
				isCreateStreamCalled = false;
				break;
			default:
				break;
		}
		//Editing
		if (isCreateStreamCalled) {
			updateExtendedStatistic.put(prefixName + HaivisionConstant.HASH + HaivisionConstant.EDITED, HaivisionConstant.TRUE);
			updateExtendedStatistic.put(prefixName + HaivisionConstant.HASH + HaivisionConstant.CANCEL, "");
			advancedControllableProperties.add(createButton(prefixName + HaivisionConstant.HASH + HaivisionConstant.CANCEL, HaivisionConstant.CANCEL, HaivisionConstant.CANCEL, 0));
		}
		Map<String, String> extendedStats = localExtendedStatistics.getStatistics();
		extendedStats.putAll(updateExtendedStatistic);
		List<AdvancedControllableProperty> listControlProperty = localExtendedStatistics.getControllableProperties();
		//update or add props form advancedControllableProperties to listControlProperty

		List<String> newPropNames = advancedControllableProperties.stream().map(AdvancedControllableProperty::getName).collect(Collectors.toList());
		listControlProperty.removeIf(item -> newPropNames.contains(item.getName()));
		listControlProperty.addAll(new ArrayList<>(advancedControllableProperties));
	}

	/**
	 * Sent request to create output stream encoder
	 *
	 * @param bodyPayLoad the bodyPayLoad is body contain data
	 */
	private void setOutputStreamAction(String bodyPayLoad) {
		try {
			JsonNode responseData = doPost(HaivisionStatisticsUtil.getMonitorURL(HaivisionURL.STREAM), bodyPayLoad, JsonNode.class);
			if (responseData.get(HaivisionConstant.INFO) == null) {
				throw new ResourceNotReachableException(HaivisionConstant.NO_SET_ERROR_CREATE_STREAM);
			}
		} catch (Exception e) {
			throw new ResourceNotReachableException(HaivisionConstant.ERR_SET_CONTROL + e.getMessage());
		}
	}
}