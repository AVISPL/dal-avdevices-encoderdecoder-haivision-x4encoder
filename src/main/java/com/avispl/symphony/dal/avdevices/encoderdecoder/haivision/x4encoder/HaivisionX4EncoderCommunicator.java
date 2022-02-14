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
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.VideoResponse;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.audio.AudioStatistic;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.output.OutputStatistic;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.video.VideoStatistic;
import com.avispl.symphony.dal.communicator.RestCommunicator;

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

	private final ObjectMapper objectMapper = new ObjectMapper();
	/**
	 * List of audio Response
	 */
	private final List<AudioResponse> audioResponseList = Collections.synchronizedList(new ArrayList<>());

	/**
	 * List of audio Response
	 */
	private final List<VideoResponse> videoResponseList = Collections.synchronizedList(new ArrayList<>());

	/**
	 * List of audio Response
	 */
	private final List<OutputResponse> outputResponseList = Collections.synchronizedList(new ArrayList<>());

	private ExtendedStatistics localExtendedStatistics;
	private final Map<String, String> failedMonitor = new HashMap<>();
	private String sessionID;

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
		populateInformationFromDevice(stats);
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
				throw new ResourceNotReachableException("Login to the device failed,Device unauthorized");
			}
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
		stringBuilder.append(HaivisionConstant.LOGIN);

		return stringBuilder.toString();
	}

	/**
	 * Retrieve data and add to stats
	 *
	 * @param stats list statistics property
	 */
	private void populateInformationFromDevice(Map<String, String> stats) {
		Objects.requireNonNull(stats);

		for (HaivisionMonitoringMetric makitoMonitoringMetric : HaivisionMonitoringMetric.values()) {
			retrieveDataByMetric(stats, makitoMonitoringMetric);
		}
		if (failedMonitor.size() == HaivisionMonitoringMetric.values().length) {
			StringBuilder stringBuilder = new StringBuilder();
			for (Map.Entry<String, String> messageFailed : failedMonitor.entrySet()) {
				stringBuilder.append(messageFailed.getValue());
			}
			throw new ResourceNotReachableException("Get monitoring data failed: " + stringBuilder);
		}
	}

	/**
	 * Retrieve data from the device
	 *
	 * @param stats list statistics property
	 * @param metric list metric of device
	 */
	private void retrieveDataByMetric(Map<String, String> stats, HaivisionMonitoringMetric metric) {
		Objects.requireNonNull(metric);
		switch (metric) {
			case AUDIO_ENCODER:
				retrieveAudioEncoder(stats);
				break;
			case VIDEO_ENCODER:
				retrieveVideoEncoder(stats);
				break;
			case OUTPUT_ENCODER:
				retrieveOutputEncoder(stats);
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
		if (!audioResponseList.isEmpty()) {
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
			stats.put(String.format(HaivisionConstant.FORMAT, audioName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.STATE), HaivisionConstant.NONE);
		} else {
			stats.put(String.format(HaivisionConstant.FORMAT, audioName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.STATE),
					String.valueOf(audioMap.get(Integer.valueOf(state))));
		}
		stats.put(String.format(HaivisionConstant.FORMAT, audioName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.STATE),
				String.valueOf(audioMap.get(Integer.valueOf(audioStatistic.getState()))));
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
		if (!videoResponseList.isEmpty()) {
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
				String.valueOf(videoStatistic.getEncodedFrameRate()));
		stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.ENCODED_BYTES_VIDEO),
				String.valueOf(videoStatistic.getEncodedBytes()));
		stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.ENCODED_FRAMERATE),
				String.valueOf(videoStatistic.getEncodedFrames()));
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
		stats.put(String.format(HaivisionConstant.FORMAT, videoName + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.INPUT_COLOUR_PRIMARIES),
				String.valueOf(videoStatistic.getInputColourPrimaries()));
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
		for (OutputResponse outputResponses : outputResponseList) {
			addOutputStreamDataStatisticsToStatisticsProperty(stats, outputResponses);
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
			stats.put(String.format(HaivisionConstant.FORMAT, name + HaivisionConstant.SPACE + HaivisionMonitoringMetric.STATISTICS, HaivisionMonitoringMetric.STATE), HaivisionConstant.NONE);
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
	 *
	 * @param stats list statistics property
	 */
	private void retrieveAudioEncoder(Map<String, String> stats) {
		try {
			String responseData = doGet(HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.AUDIO_ENCODER));
			JsonNode audio = objectMapper.readTree(responseData).get(HaivisionConstant.DATA);
			for (int i = 0; i < audio.size(); i++) {
				AudioResponse audioItem = objectMapper.treeToValue(audio.get(i), AudioResponse.class);
				audioResponseList.add(audioItem);
			}
			populateAudioData(stats);
		} catch (Exception e) {
			failedMonitor.put(HaivisionMonitoringMetric.AUDIO_ENCODER.getName(), e.getMessage());
		}
	}

	/**
	 * Retrieve video encoder
	 *
	 * @param stats list statistics property
	 */
	private void retrieveVideoEncoder(Map<String, String> stats) {
		try {
			String responseData = doGet(HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.VIDEO_ENCODER));
			JsonNode video = objectMapper.readTree(responseData).get(HaivisionConstant.DATA);
			for (int i = 0; i < video.size(); i++) {
				VideoResponse videoItem = objectMapper.treeToValue(video.get(i), VideoResponse.class);
				videoResponseList.add(videoItem);
			}
			populateVideoData(stats);
		} catch (Exception e) {
			failedMonitor.put(HaivisionMonitoringMetric.VIDEO_ENCODER.getName(), e.getMessage());
		}
	}

	/**
	 * Retrieve output stream encoder
	 *
	 * @param stats list statistics property
	 */
	private void retrieveOutputEncoder(Map<String, String> stats) {
		try {
			String responseData = doGet(HaivisionStatisticsUtil.getMonitorURL(HaivisionMonitoringMetric.OUTPUT_ENCODER));
			JsonNode output = objectMapper.readTree(responseData).get(HaivisionConstant.DATA);
			for (int i = 0; i < output.size(); i++) {
				OutputResponse outputItem = objectMapper.treeToValue(output.get(i), OutputResponse.class);
				outputResponseList.add(outputItem);
			}
			populateOutputData(stats);
		} catch (Exception e) {
			failedMonitor.put(HaivisionMonitoringMetric.OUTPUT_ENCODER.getName(), e.getMessage());
		}
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
		int index = time.indexOf("s");
		if (index > -1) {
			time = time.substring(0, index + 1);
		}
		int indexDay = time.indexOf("d");
		int indexHour = time.indexOf("h");
		int indexMinute = time.indexOf("m");
		StringBuilder stringBuilder = new StringBuilder();
		if (indexDay > -1) {
			stringBuilder.append(time, 0, indexDay);
			stringBuilder.append(HaivisionConstant.DAY);
			stringBuilder.append(time, indexDay + 1, indexHour);
			stringBuilder.append(HaivisionConstant.HOUR);
			stringBuilder.append(time, indexHour + 1, indexMinute);
			stringBuilder.append(HaivisionConstant.MINUTE);
			stringBuilder.append(time, indexMinute + 1, index);
			stringBuilder.append(HaivisionConstant.SECOND);
		} else if (indexHour > -1) {
			stringBuilder.append(time, 0, indexHour);
			stringBuilder.append(HaivisionConstant.HOUR);
			stringBuilder.append(time, indexHour + 1, indexMinute);
			stringBuilder.append(HaivisionConstant.MINUTE);
			stringBuilder.append(time, indexMinute + 1, index);
			stringBuilder.append(HaivisionConstant.SECOND);
		} else if (indexMinute > -1) {
			stringBuilder.append(time, 0, indexMinute);
			stringBuilder.append(HaivisionConstant.MINUTE);
			stringBuilder.append(time, indexMinute + 1, index);
			stringBuilder.append(HaivisionConstant.SECOND);
		} else {
			stringBuilder.append(time, 0, index);
			stringBuilder.append(HaivisionConstant.SECOND);
		}
		return String.valueOf(stringBuilder);
	}
}