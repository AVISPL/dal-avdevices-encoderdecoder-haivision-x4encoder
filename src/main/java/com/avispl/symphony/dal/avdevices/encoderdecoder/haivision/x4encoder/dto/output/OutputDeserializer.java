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
 * @author Ivan / Symphony Dev Team<br>
 * Created on 3/8/2022
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
			outputResponse.setShaping(checkNoneInformation(infoNode, "shaping"));
			outputResponse.setBandwidthOverhead(checkNoneInformation(infoNode, "shapiovereadPercentageng"));

			JsonNode sap = infoNode.get("sap");
			if(sap != null){
					OutputSAP outputSAP = new OutputSAP();
					outputSAP.setAdvertise(checkNoneInformation(sap,"advertise"));
					outputSAP.setName(checkNoneInformation(sap,"name"));
					outputSAP.setDesc(checkNoneInformation(sap,"desc"));
					outputSAP.setKeywords(checkNoneInformation(sap,"keywords"));
					outputSAP.setAuthor(checkNoneInformation(sap,"author"));
					outputSAP.setCopyright(checkNoneInformation(sap,"copyright"));
					outputSAP.setAddress(checkNoneInformation(sap,"address"));
					outputSAP.setPort(checkNoneInformation(sap,"port"));

					outputResponse.setOutputSAP(outputSAP);
			}

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
			outputStatistic.setOccurred(checkNoneInformation(stateNode, "occurred"));
			JsonNode srt = stateNode.get("srt");
			if (srt != null) {
				outputStatistic.setReconnections(checkNoneInformation(srt, "reconnections"));
				outputStatistic.setResentPackets(checkNoneInformation(srt, "resentPackets"));
				outputStatistic.setResentBytes(checkNoneInformation(srt, "resentBytes"));
				outputStatistic.setDroppedPackets(checkNoneInformation(srt, "droppedPackets"));
				outputStatistic.setDroppedBytes(checkNoneInformation(srt, "droppedBytes"));
				outputStatistic.setMss(checkNoneInformation(srt, "mss"));
				outputStatistic.setMaxBandwidth(checkNoneInformation(srt, "maxBandwidth"));
				outputStatistic.setRemotePort(checkNoneInformation(srt, "remotePort"));
				outputStatistic.setSourceAddress(checkNoneInformation(srt, "sourceAddress"));
				outputStatistic.setRemoteAddress(checkNoneInformation(srt, "remoteAddress"));
				outputStatistic.setPathMaxBandwidth(checkNoneInformation(srt, "pathMaxBandwidth"));
				outputStatistic.setLostPackets(checkNoneInformation(srt, "lostPackets"));
				outputStatistic.setRecvACK(checkNoneInformation(srt, "recvACK"));
				outputStatistic.setRecvNAK(checkNoneInformation(srt, "recvNAK"));
				outputStatistic.setRtt(checkNoneInformation(srt, "rtt"));
				outputStatistic.setLatency(checkNoneInformation(srt, "latency"));
				outputStatistic.setBuffer(checkNoneInformation(srt, "buffer"));
			} else {
				outputStatistic.setReconnections(HaivisionConstant.NONE);
				outputStatistic.setResentPackets(HaivisionConstant.NONE);
				outputStatistic.setResentBytes(HaivisionConstant.NONE);
				outputStatistic.setDroppedPackets(HaivisionConstant.NONE);
				outputStatistic.setDroppedBytes(HaivisionConstant.NONE);
				outputStatistic.setMss(HaivisionConstant.NONE);
				outputStatistic.setMaxBandwidth(HaivisionConstant.NONE);
				outputStatistic.setRemotePort(HaivisionConstant.NONE);
				outputStatistic.setSourceAddress(HaivisionConstant.NONE);
				outputStatistic.setRemoteAddress(HaivisionConstant.NONE);
				outputStatistic.setPathMaxBandwidth(HaivisionConstant.NONE);
				outputStatistic.setLostPackets(HaivisionConstant.NONE);
				outputStatistic.setRecvACK(HaivisionConstant.NONE);
				outputStatistic.setRecvNAK(HaivisionConstant.NONE);
				outputStatistic.setRtt(HaivisionConstant.NONE);
				outputStatistic.setLatency(HaivisionConstant.NONE);
				outputStatistic.setBuffer(HaivisionConstant.NONE);
			}
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
