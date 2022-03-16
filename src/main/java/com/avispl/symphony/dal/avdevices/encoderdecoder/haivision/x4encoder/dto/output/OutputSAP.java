/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.output;

/**
 * OutputSAP DTO class
 *
 * @author Ivan / Symphony Dev Team<br>
 * Created on 3/10/2022
 * @since 1.0.0
 */
public class OutputSAP {

	//Transmit SAP
	private String advertise;
	private String name;
	private String desc;
	private String keywords;
	private String author;
	private String copyright;
	private String address;
	private String port;

	/**
	 * Retrieves {@code {@link #advertise}}
	 *
	 * @return value of {@link #advertise}
	 */
	public String getAdvertise() {
		return advertise;
	}

	/**
	 * Sets {@code advertise}
	 *
	 * @param advertise the {@code java.lang.String} field
	 */
	public void setAdvertise(String advertise) {
		this.advertise = advertise;
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
	 * Retrieves {@code {@link #desc}}
	 *
	 * @return value of {@link #desc}
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * Sets {@code desc}
	 *
	 * @param desc the {@code java.lang.String} field
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 * Retrieves {@code {@link #keywords}}
	 *
	 * @return value of {@link #keywords}
	 */
	public String getKeywords() {
		return keywords;
	}

	/**
	 * Sets {@code keywords}
	 *
	 * @param keywords the {@code java.lang.String} field
	 */
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	/**
	 * Retrieves {@code {@link #author}}
	 *
	 * @return value of {@link #author}
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * Sets {@code author}
	 *
	 * @param author the {@code java.lang.String} field
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * Retrieves {@code {@link #copyright}}
	 *
	 * @return value of {@link #copyright}
	 */
	public String getCopyright() {
		return copyright;
	}

	/**
	 * Sets {@code copyright}
	 *
	 * @param copyright the {@code java.lang.String} field
	 */
	public void setCopyright(String copyright) {
		this.copyright = copyright;
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
	 * Convert OutputSAP
	 *
	 * @return payLoad the payload is String by OutputSAP
	 */
	public String payLoad() {
		return "{" +
				"\"advertise\":\"" + advertise +  "\"" +
				",\"name\":\"" + name +  "\"" +
				",\"desc\":\"" + desc +  "\"" +
				",\"keywords\":\"" + keywords +  "\"" +
				",\"author\":\"" + author +  "\"" +
				",\"copyright\":\"" + copyright +  "\"" +
				",\"address\":\"" + address +  "\"" +
				",\"port\":\"" + port +  "\"" +
				'}';
	}
}