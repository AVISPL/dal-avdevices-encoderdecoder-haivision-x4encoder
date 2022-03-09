/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.audio.Audio;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.output.OutputDeserializer;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.output.OutputStatistic;
import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.video.Video;

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
}
