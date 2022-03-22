/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.HaivisionConstant;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.audio.Audio;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.output.OutputDeserializer;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.output.OutputStatistic;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.video.Video;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.output.OutputSAP;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * Output Response DTO class
 *
 * @author Ivan / Symphony Dev Team<br>
 * Created on 3/8/2022
 * @since 1.0.0
 */
@JsonDeserialize(using = OutputDeserializer.class)
public class OutputResponse {
	private String id;
	private String encapsulation;
	private String port;
	private String address;
	private String mtu;
	private String ttl;
	private String tos;
	private String state;
	private String name;
	private List<Video> video;
	private List<Audio> audio;
	private String shaping;
	//name is shapiovereadPercentageng
	private String bandwidthOverhead;
	private OutputSAP outputSAP;
	//Average bandwidth
	private String bandwidthEstimate;
	private String srtMode;
	private String sourcePort;
	private String adaptive;
	private String latency;
	private String encryption;
	private String passphrase;
	private String srtListenerSecondPort;
	private String srtRedundancyMode;
	private OutputStatistic outputStatistic;

	/**
	 * Retrieves {@code {@link #id}}
	 *
	 * @return value of {@link #id}
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets {@code id}
	 *
	 * @param id the {@code java.lang.String} field
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Retrieves {@code {@link #encapsulation}}
	 *
	 * @return value of {@link #encapsulation}
	 */
	public String getEncapsulation() {
		return encapsulation;
	}

	/**
	 * Sets {@code encapsulation}
	 *
	 * @param encapsulation the {@code java.lang.String} field
	 */
	public void setEncapsulation(String encapsulation) {
		this.encapsulation = encapsulation;
	}

	/**
	 * Retrieves {@code {@link #port}}
	 *
	 * @return value of {@link #port}
	 */
	public String getPort() {
		return port;
	}

	/**
	 * Sets {@code port}
	 *
	 * @param port the {@code java.lang.String} field
	 */
	public void setPort(String port) {
		this.port = port;
	}

	/**
	 * Retrieves {@code {@link #address}}
	 *
	 * @return value of {@link #address}
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Sets {@code address}
	 *
	 * @param address the {@code java.lang.String} field
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * Retrieves {@code {@link #mtu}}
	 *
	 * @return value of {@link #mtu}
	 */
	public String getMtu() {
		return mtu;
	}

	/**
	 * Sets {@code mtu}
	 *
	 * @param mtu the {@code java.lang.String} field
	 */
	public void setMtu(String mtu) {
		this.mtu = mtu;
	}

	/**
	 * Retrieves {@code {@link #ttl}}
	 *
	 * @return value of {@link #ttl}
	 */
	public String getTtl() {
		return ttl;
	}

	/**
	 * Sets {@code ttl}
	 *
	 * @param ttl the {@code java.lang.String} field
	 */
	public void setTtl(String ttl) {
		this.ttl = ttl;
	}

	/**
	 * Retrieves {@code {@link #tos}}
	 *
	 * @return value of {@link #tos}
	 */
	public String getTos() {
		return tos;
	}

	/**
	 * Sets {@code tos}
	 *
	 * @param tos the {@code java.lang.String} field
	 */
	public void setTos(String tos) {
		this.tos = tos;
	}

	/**
	 * Retrieves {@code {@link #state}}
	 *
	 * @return value of {@link #state}
	 */
	public String getState() {
		return state;
	}

	/**
	 * Sets {@code state}
	 *
	 * @param state the {@code java.lang.String} field
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * Retrieves {@code {@link #name}}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets {@code name}
	 *
	 * @param name the {@code java.lang.String} field
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Retrieves {@code {@link #video}}
	 *
	 * @return value of {@link #video}
	 */
	public List<Video> getVideo() {
		return video;
	}

	/**
	 * Sets {@code video}
	 *
	 * @param video the {@code java.util.List<com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.video.Video>} field
	 */
	public void setVideo(List<Video> video) {
		this.video = video;
	}

	/**
	 * Retrieves {@code {@link #audio}}
	 *
	 * @return value of {@link #audio}
	 */
	public List<Audio> getAudio() {
		return audio;
	}

	/**
	 * Sets {@code audio}
	 *
	 * @param audio the {@code java.util.List<com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.audio.Audio>} field
	 */
	public void setAudio(List<Audio> audio) {
		this.audio = audio;
	}

	/**
	 * Retrieves {@code {@link #outputStatistic}}
	 *
	 * @return value of {@link #outputStatistic}
	 */
	public OutputStatistic getOutputStatistic() {
		return outputStatistic;
	}

	/**
	 * Sets {@code outputStatistic}
	 *
	 * @param outputStatistic the {@code com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.output.OutputStatistic} field
	 */
	public void setOutputStatistic(OutputStatistic outputStatistic) {
		this.outputStatistic = outputStatistic;
	}

	/**
	 * Retrieves {@code {@link #shaping}}
	 *
	 * @return value of {@link #shaping}
	 */
	public String getShaping() {
		return shaping;
	}

	/**
	 * Sets {@code shaping}
	 *
	 * @param shaping the {@code java.lang.String} field
	 */
	public void setShaping(String shaping) {
		this.shaping = shaping;
	}

	/**
	 * Retrieves {@code {@link #bandwidthOverhead}}
	 *
	 * @return value of {@link #bandwidthOverhead}
	 */
	public String getBandwidthOverhead() {
		return bandwidthOverhead;
	}

	/**
	 * Sets {@code bandwidthOverhead}
	 *
	 * @param bandwidthOverhead the {@code java.lang.String} field
	 */
	public void setBandwidthOverhead(String bandwidthOverhead) {
		this.bandwidthOverhead = bandwidthOverhead;
	}

	/**
	 * Retrieves {@code {@link #outputSAP}}
	 *
	 * @return value of {@link #outputSAP}
	 */
	public OutputSAP getOutputSAP() {
		return outputSAP;
	}

	/**
	 * Sets {@code outputSAP}
	 *
	 * @param outputSAP the {@code com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.output.OutputSAP} field
	 */
	public void setOutputSAP(OutputSAP outputSAP) {
		this.outputSAP = outputSAP;
	}

	/**
	 * Retrieves {@code {@link #bandwidthEstimate }}
	 *
	 * @return value of {@link #bandwidthEstimate}
	 */
	public String getBandwidthEstimate() {
		return bandwidthEstimate;
	}

	/**
	 * Sets {@code bandwithEstimate}
	 *
	 * @param bandwidthEstimate the {@code java.lang.String} field
	 */
	public void setBandwidthEstimate(String bandwidthEstimate) {
		this.bandwidthEstimate = bandwidthEstimate;
	}

	/**
	 * Retrieves {@code {@link #srtMode}}
	 *
	 * @return value of {@link #srtMode}
	 */
	public String getSrtMode() {
		return srtMode;
	}

	/**
	 * Sets {@code srtMode}
	 *
	 * @param srtMode the {@code java.lang.String} field
	 */
	public void setSrtMode(String srtMode) {
		this.srtMode = srtMode;
	}

	/**
	 * Retrieves {@code {@link #sourcePort}}
	 *
	 * @return value of {@link #sourcePort}
	 */
	public String getSourcePort() {
		return sourcePort;
	}

	/**
	 * Sets {@code sourcePort}
	 *
	 * @param sourcePort the {@code java.lang.String} field
	 */
	public void setSourcePort(String sourcePort) {
		this.sourcePort = sourcePort;
	}

	/**
	 * Retrieves {@code {@link #adaptive}}
	 *
	 * @return value of {@link #adaptive}
	 */
	public String getAdaptive() {
		return adaptive;
	}

	/**
	 * Sets {@code adaptive}
	 *
	 * @param adaptive the {@code java.lang.String} field
	 */
	public void setAdaptive(String adaptive) {
		this.adaptive = adaptive;
	}

	/**
	 * Retrieves {@code {@link #latency}}
	 *
	 * @return value of {@link #latency}
	 */
	public String getLatency() {
		return latency;
	}

	/**
	 * Sets {@code latency}
	 *
	 * @param latency the {@code java.lang.String} field
	 */
	public void setLatency(String latency) {
		this.latency = latency;
	}

	/**
	 * Retrieves {@code {@link #encryption}}
	 *
	 * @return value of {@link #encryption}
	 */
	public String getEncryption() {
		return encryption;
	}

	/**
	 * Sets {@code encryption}
	 *
	 * @param encryption the {@code java.lang.String} field
	 */
	public void setEncryption(String encryption) {
		this.encryption = encryption;
	}

	/**
	 * Retrieves {@code {@link #passphrase}}
	 *
	 * @return value of {@link #passphrase}
	 */
	public String getPassphrase() {
		return passphrase;
	}

	/**
	 * Sets {@code passphrase}
	 *
	 * @param passphrase the {@code java.lang.String} field
	 */
	public void setPassphrase(String passphrase) {
		this.passphrase = passphrase;
	}

	/**
	 * Retrieves {@code {@link #srtListenerSecondPort}}
	 *
	 * @return value of {@link #srtListenerSecondPort}
	 */
	public String getSrtListenerSecondPort() {
		return srtListenerSecondPort;
	}

	/**
	 * Sets {@code srtListenerSecondPort}
	 *
	 * @param srtListenerSecondPort the {@code java.lang.String} field
	 */
	public void setSrtListenerSecondPort(String srtListenerSecondPort) {
		this.srtListenerSecondPort = srtListenerSecondPort;
	}

	/**
	 * Retrieves {@code {@link #srtRedundancyMode}}
	 *
	 * @return value of {@link #srtRedundancyMode}
	 */
	public String getSrtRedundancyMode() {
		return srtRedundancyMode;
	}

	/**
	 * Sets {@code srtRedundancyMode}
	 *
	 * @param srtRedundancyMode the {@code java.lang.String} field
	 */
	public void setSrtRedundancyMode(String srtRedundancyMode) {
		this.srtRedundancyMode = srtRedundancyMode;
	}

	/**
	 * Convert OutputResponse
	 *
	 * @return payLoad the payload is String by OutputResponse
	 */
	public String payLoad() {
		StringBuilder audioPayload = new StringBuilder();
		audioPayload.append("[");
		if (audio.size() > 0) {
			for (Audio audioItem : audio) {
				audioPayload.append("{\"id\":\"" + audioItem.getId() + "\"}");
				if (!audioItem.equals(audio.get(audio.size()-1))) {
					audioPayload.append(",");
				}
			}
		}
		audioPayload.append("]");

		StringBuilder videoPayload = new StringBuilder();
		videoPayload.append("[");
		if (video.size() > 0) {
			videoPayload.append("{\"id\":\"" + video.get(0).getId() + "\"}");
		}
		videoPayload.append("]");
		String srtRedundancyModeValue = "";
		if (!StringUtils.isNullOrEmpty(srtRedundancyMode)) {
			srtRedundancyModeValue = ",\"srtRedundancyMode\":\"" + srtRedundancyMode + "\"";
		}
		String passphraseValue = "";
		if(!HaivisionConstant.ZERO.equals(encryption) && !StringUtils.isNullOrEmpty(passphrase)){
			passphraseValue = ",\"passphrase\":\"" + passphrase + "\"";
		}
		return "{" +
				"\"id\":\"" + id + "\"" +
				",\"encapsulation\":\"" + encapsulation + "\"" +
				",\"port\":\"" + port + "\"" +
				",\"address\":\"" + address + "\"" +
				",\"mtu\":\"" + mtu + "\"" +
				",\"ttl\":\"" + ttl + "\"" +
				",\"tos\":\"" + tos + "\"" +
				",\"name\":\"" + name + "\"" +
				",\"video\":" + videoPayload +
				",\"audio\":" + audioPayload +
				",\"shaping\":\"" + shaping + "\"" +
				",\"overheadPercentage\":\"" + bandwidthOverhead + "\"" +
				",\"sap\":" + outputSAP.payLoad() +
				",\"bandwithEstimate\":\"" + bandwidthEstimate + "\"" +
				",\"srtMode\":\"" + srtMode + "\"" +
				",\"sourcePort\":\"" + sourcePort + "\"" +
				",\"adaptive\":\"" + adaptive + "\"" +
				",\"latency\":\"" + latency + "\"" +
				",\"encKeyLength\":\"" + encryption + "\"" +
				passphraseValue +
				",\"srtListenerSecondPort\":\"" + srtListenerSecondPort + "\"" +
				 srtRedundancyModeValue +
				'}';
	}
}
