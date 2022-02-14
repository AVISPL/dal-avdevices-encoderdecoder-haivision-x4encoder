/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.output;

import java.io.IOException;
import java.util.Collections;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.HaivisionConstant;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.OutputResponse;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.audio.Audio;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.video.Video;

/**
 * Custom Deserializer class for OutputResponse
 *
 * @author Ivan
 * @version 1.0.0
 * @since 1.0.0
 */
public class OutputDeserializer extends StdDeserializer<OutputResponse> {
	/**
	 * OutputDeserializer no arg constructor
	 */
	public OutputDeserializer() {
		this(null);
	}

	/**
	 * OutputDeserializer with arg constructor
	 *
	 * @param vc Class name
	 */
	protected OutputDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public OutputResponse deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
		JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);
		OutputResponse outputResponse = new OutputResponse();
		JsonNode infoNode = jsonNode.get("info");
		if (infoNode != null) {
			outputResponse.setName(checkNoneInformation(infoNode, "name"));
			outputResponse.setId(checkNoneInformation(infoNode, "id"));
			outputResponse.setEncapsulation(checkNoneInformation(infoNode, "encapsulation"));
			outputResponse.setPort(checkNoneInformation(infoNode, "port"));
			outputResponse.setAddress(checkNoneInformation(infoNode, "address"));
			outputResponse.setMtu(checkNoneInformation(infoNode, "mtu"));
			outputResponse.setTtl(checkNoneInformation(infoNode, "ttl"));
			outputResponse.setTos(checkNoneInformation(infoNode, "tos"));

			Video video = new Video();
			JsonNode videoList = infoNode.get("video");
			if (videoList != null) {
				for (int i = 0; i < videoList.size(); i++) {
					video.setId(checkNoneInformation(videoList.get(i), "id"));
					video.setName(checkNoneInformation(videoList.get(i), "name"));
					video.setPid(checkNoneInformation(videoList.get(i), "pid"));
					video.setAutoAssigned(checkNoneInformation(videoList.get(i), "autoassigned"));
					outputResponse.setVideo(Collections.singletonList(video));
				}
			}
			Audio audio = new Audio();
			JsonNode audioList = infoNode.get("video");
			if (audioList != null) {
				for (int i = 0; i < audioList.size(); i++) {
					audio.setId(checkNoneInformation(audioList.get(i), "id"));
					audio.setName(checkNoneInformation(audioList.get(i), "name"));
					audio.setPid(checkNoneInformation(audioList.get(i), "pid"));
					audio.setAutoAssigned(checkNoneInformation(audioList.get(i), "autoassigned"));
					outputResponse.setAudio(Collections.singletonList(audio));
				}
			}
		}

		JsonNode stateNode = jsonNode.get("stats");
		if (stateNode != null) {
			outputResponse.setState(checkNoneInformation(stateNode, "state"));
			OutputStatistic outputStatistic = new OutputStatistic();
			outputStatistic.setUptime(checkNoneInformation(stateNode, "uptime"));
			outputStatistic.setSourcePort(checkNoneInformation(stateNode, "sourcePort"));
			outputStatistic.setSentPackets(checkNoneInformation(stateNode, "sentPackets"));
			outputStatistic.setSentBytes(checkNoneInformation(stateNode, "sentBytes"));
			outputStatistic.setBitrate(checkNoneInformation(stateNode, "bitrate"));
			outputStatistic.setReconnections(checkNoneInformation(stateNode, "reconnections"));
			outputStatistic.setResentPackets(checkNoneInformation(stateNode, "resentPackets"));
			outputStatistic.setResentBytes(checkNoneInformation(stateNode, "resentBytes"));
			outputStatistic.setDroppedPackets(checkNoneInformation(stateNode, "droppedPackets"));
			outputStatistic.setDroppedBytes(checkNoneInformation(stateNode, "droppedBytes"));
			outputStatistic.setMss(checkNoneInformation(stateNode, "mss"));
			outputStatistic.setMaxBandwidth(checkNoneInformation(stateNode, "maxBandwidth"));
			outputStatistic.setRemotePort(checkNoneInformation(stateNode, "remotePort"));
			outputStatistic.setSourceAddress(checkNoneInformation(stateNode, "sourceAddress"));
			outputStatistic.setRemoteAddress(checkNoneInformation(stateNode, "remoteAddress"));
			outputStatistic.setPathMaxBandwidth(checkNoneInformation(stateNode, "pathMaxBandwidth"));
			outputStatistic.setLostPackets(checkNoneInformation(stateNode, "lostPackets"));
			outputStatistic.setRecvACK(checkNoneInformation(stateNode, "recvACK"));
			outputStatistic.setRecvNAK(checkNoneInformation(stateNode, "recvNAK"));
			outputStatistic.setRtt(checkNoneInformation(stateNode, "rtt"));
			outputStatistic.setLatency(checkNoneInformation(stateNode, "latency"));
			outputStatistic.setBuffer(checkNoneInformation(stateNode, "buffer"));
			outputStatistic.setOccurred(checkNoneInformation(stateNode, "occurred"));

			outputResponse.setOutputStatistic(outputStatistic);
		}

		return outputResponse;
	}

	/**
	 * Check None information of output stream encoder
	 *
	 * @param stateNode JsonNode object
	 * @param name the name is field name of output stream encoder
	 * @return String is None or value
	 */
	private String checkNoneInformation(JsonNode stateNode, String name) {
		return stateNode.get(name) == null ? HaivisionConstant.NONE : stateNode.get(name).asText();
	}
}
