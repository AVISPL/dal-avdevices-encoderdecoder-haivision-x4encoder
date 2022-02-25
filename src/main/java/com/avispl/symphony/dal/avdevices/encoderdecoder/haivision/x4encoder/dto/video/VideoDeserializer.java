/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.video;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.HaivisionConstant;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.VideoResponse;

/**
 * Custom Deserializer class for VideoResponse
 *
 * @author Ivan
 * @version 1.0.0
 * @since 1.0.0
 */
public class VideoDeserializer extends StdDeserializer<VideoResponse> {
	/**
	 * VideoDeserializer no arg constructor
	 */
	public VideoDeserializer() {
		this(null);
	}

	/**
	 * VideoDeserializer with arg constructor
	 *
	 * @param vc Class name
	 */
	protected VideoDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public VideoResponse deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
		JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);
		VideoResponse videoResponse = new VideoResponse();
		JsonNode infoNode = jsonNode.get("info");

		if (infoNode != null) {
			videoResponse.setId(checkNoneInformation(infoNode, "id"));
			videoResponse.setName(checkNoneInformation(infoNode, "name"));
			videoResponse.setBitrate(checkNoneInformation(infoNode, "bitrate"));
			videoResponse.setMaxBitrate(checkNoneInformation(infoNode, "maxBitrate"));
			videoResponse.setGopSize(checkNoneInformation(infoNode, "gopSize"));
			videoResponse.setClosedCaption(checkNoneInformation(infoNode, "closedCaption"));
			videoResponse.setCodecAlgorithm(checkNoneInformation(infoNode, "codecAlgorithm"));
			videoResponse.setInputInterface(checkNoneInformation(infoNode, "interface"));
			videoResponse.setEncodingProfile(checkNoneInformation(infoNode, "encodingProfile"));
			videoResponse.setChromaSubSampling(checkNoneInformation(infoNode, "chromaSubSampling"));
			videoResponse.setRateControl(checkNoneInformation(infoNode, "ratecontrol"));
			videoResponse.setTimeCode(checkNoneInformation(infoNode, "timeCode"));
			videoResponse.setAspectRatio(checkNoneInformation(infoNode, "aspectRatio"));
			videoResponse.setGopStructure(checkNoneInformation(infoNode, "gopStructure"));
			videoResponse.setResolution(checkNoneInformation(infoNode, "resolutionText"));
			videoResponse.setCropping(checkNoneInformation(infoNode, "cropping"));
			videoResponse.setFrameRate(checkNoneInformation(infoNode, "pictureRate"));
		}

		JsonNode stateNode = jsonNode.get("stats");
		if (stateNode != null) {
			videoResponse.setState(checkNoneInformation(stateNode, "state"));
			videoResponse.setInputFormat(checkNoneInformation(stateNode, "inputFormat"));

			VideoStatistic videoStatistic = new VideoStatistic();
			videoStatistic.setExtractedCSDBytes(checkNoneInformation(stateNode, "extractedCSDBytes"));
			videoStatistic.setcCErrors(checkNoneInformation(stateNode, "CCErrors"));
			videoStatistic.setClosedCaptioning(checkNoneInformation(stateNode, "closedCaptioning"));
			videoStatistic.setEncoderLoad(checkNoneInformation(stateNode, "encoderLoad"));
			videoStatistic.setEncodedBitRate(checkNoneInformation(stateNode, "encodedBitRate"));
			videoStatistic.setEncoderResets(checkNoneInformation(stateNode, "encoderResets"));
			videoStatistic.setEncodedFrameRate(checkNoneInformation(stateNode, "encodedFrameRate"));
			videoStatistic.setEncodedBytes(checkNoneInformation(stateNode, "encodedBytes"));
			videoStatistic.setEncodedFrames(checkNoneInformation(stateNode, "encodedFrames"));
			videoStatistic.setDroppedFrames(checkNoneInformation(stateNode, "droppedFrames"));
			videoStatistic.setAspectRatio(checkNoneInformation(stateNode, "aspectRatio"));
			videoStatistic.setResolutionIsProgressive(checkNoneInformation(stateNode, "resolutionIsProgressive"));
			videoStatistic.setResolutionIsInterlaced(checkNoneInformation(stateNode, "resolutionIsInterlaced"));
			videoStatistic.setResolutionFrameRate(checkNoneInformation(stateNode, "resolutionFrameRate"));
			videoStatistic.setResolutionHeight(checkNoneInformation(stateNode, "resolutionHeight"));
			videoStatistic.setResolutionWidth(checkNoneInformation(stateNode, "resolutionWidth"));
			videoStatistic.setResolutionIsDetailed(checkNoneInformation(stateNode, "resolutionIsDetailed"));
			videoStatistic.setResolution(checkNoneInformation(stateNode, "resolution"));
			videoStatistic.setInputFormatIsProgressive(checkNoneInformation(stateNode, "inputFormatIsProgressive"));
			videoStatistic.setInputFormatIsInterlaced(checkNoneInformation(stateNode, "inputFormatIsInterlaced"));
			videoStatistic.setInputColorPrimaries(checkNoneInformation(stateNode, "inputColorPrimaries"));
			videoStatistic.setInputColourPrimaries(checkNoneInformation(stateNode, "inputColourPrimaries"));
			videoStatistic.setInputFormatFrameRate(checkNoneInformation(stateNode, "inputFormatFrameRate"));
			videoStatistic.setInputFormatHeight(checkNoneInformation(stateNode, "inputFormatHeight"));
			videoStatistic.setInputFormatWidth(checkNoneInformation(stateNode, "inputFormatWidth"));
			videoStatistic.setInputFormatIsDetailed(checkNoneInformation(stateNode, "inputFormatIsDetailed"));
			videoStatistic.setInputFormatWithoutFramerateU64(checkNoneInformation(stateNode, "inputFormatWithoutFramerateU64"));
			videoStatistic.setInputFormatU64(checkNoneInformation(stateNode, "inputFormatU64"));
			videoStatistic.setInputFormatShort(checkNoneInformation(stateNode, "inputFormatShort"));
			videoStatistic.setInputFormat(checkNoneInformation(stateNode, "inputFormat"));
			videoStatistic.setInputPresent(checkNoneInformation(stateNode, "inputPresent"));
			videoStatistic.setUptime(checkNoneInformation(stateNode, "uptime"));
			videoStatistic.setInputTransferCharacteristics(checkNoneInformation(stateNode, "inputTransferCharacteristics"));
			videoStatistic.setInputMatrixCoefficients(checkNoneInformation(stateNode, "inputMatrixCoefficients"));
			videoStatistic.setOccurred(checkNoneInformation(stateNode, "occurred"));
			videoResponse.setVideoStatistic(videoStatistic);
		}

		return videoResponse;
	}

	/**
	 * Check None information of video encoder
	 *
	 * @param stateNode JsonNode object
	 * @param name the name is field name of video encoder
	 * @return String is None or value
	 */
	private String checkNoneInformation(JsonNode stateNode, String name) {
		return stateNode.get(name) == null ? HaivisionConstant.NONE : stateNode.get(name).asText();
	}
}
