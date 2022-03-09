/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.audio;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.AudioResponse;

/**
 * Custom Deserializer class for AudioResponse
 *
 * @author Ivan / Symphony Dev Team<br>
 * Created on 3/8/2022
 * @since 1.0.0
 */
public class AudioDeserializer extends StdDeserializer<AudioResponse> {
	/**
	 * AudioDeserializer no arg constructor
	 */
	public AudioDeserializer() {
		this(null);
	}

	/**
	 * AudioDeserializer with arg constructor
	 *
	 * @param vc Class name
	 */
	protected AudioDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public AudioResponse deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
		JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);
		AudioResponse audioResponse = new AudioResponse();
		JsonNode infoNode = jsonNode.get("info");
		if (infoNode != null) {
			audioResponse.setId(checkNoneInformation(infoNode, "id"));
			audioResponse.setBitRate(checkNoneInformation(infoNode, "bitRate"));
			audioResponse.setName(checkNoneInformation(infoNode, "name"));
			audioResponse.setLang(checkNoneInformation(infoNode, "lang"));
			audioResponse.setMode(checkNoneInformation(infoNode, "mode"));
			audioResponse.setInterfaceName(checkNoneInformation(infoNode, "interface"));
			audioResponse.setSampleRate(checkNoneInformation(infoNode, "sampleRate"));
			audioResponse.setAlgorithm(checkNoneInformation(infoNode, "algorithm"));
		}

		JsonNode stateNode = jsonNode.get("stats");
		if (stateNode != null) {
			audioResponse.setState(checkNoneInformation(stateNode, "state"));
			AudioStatistic audioStatistic = new AudioStatistic();
			audioStatistic.setState(checkNoneInformation(stateNode, "state"));
			audioStatistic.setEncoderErrors(checkNoneInformation(stateNode, "encoderErrors"));
			audioStatistic.setEncodedBitrate(checkNoneInformation(stateNode, "encodedBitrate"));
			audioStatistic.setMaxSampleValue(checkNoneInformation(stateNode, "maxSampleValue"));
			audioStatistic.setEncoderPTS(checkNoneInformation(stateNode, "encoderPTS"));
			audioStatistic.setEncodedBytes(checkNoneInformation(stateNode, "encodedBytes"));
			audioStatistic.setEncodedFrames(checkNoneInformation(stateNode, "encodedFrames"));
			audioStatistic.setsTCSourceInterface(checkNoneInformation(stateNode, "STCSourceInterface"));
			audioStatistic.setMaxSampleValuePercentage(checkNoneInformation(stateNode, "maxSampleValuePercentage"));
			audioResponse.setAudioStatistic(audioStatistic);
		}

		return audioResponse;
	}

	/**
	 * Check None information of audio encoder
	 *
	 * @param stateNode JsonNode object
	 * @param name the name is field name of audio encoder
	 * @return String is None or value
	 */
	private String checkNoneInformation(JsonNode stateNode, String name) {
		return stateNode.get(name) == null ? "None" : stateNode.get(name).asText();
	}
}
