/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * SystemInfoResponse Response DTO class
 *
 * @author Ivan
 * @version 1.0.0
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SystemInfoResponse {

	@JsonAlias("cardStatus")
	private String cardStatus;

	@JsonAlias("audioPairMode")
	private String serialNumber;

	@JsonAlias("audioPairMode")
	private String hardwareCompatibility;

	@JsonAlias("audioPairMode")
	private String mezzaninePresent;

	@JsonAlias("audioPairMode")
	private String hardwareRevision;

	@JsonAlias("audioPairMode")
	private String cpldRevision;

	@JsonAlias("audioPairMode")
	private String bootVersion;

	@JsonAlias("audioPairMode")
	private String cardType;

	@JsonAlias("audioPairMode")
	private String partNumber;

	@JsonAlias("audioPairMode")
	private String firmwareDate;

	@JsonAlias("audioPairMode")
	private String firmwareVersion;

	@JsonAlias("audioPairMode")
	private String firmwareOptions;

	@JsonAlias("audioPairMode")
	private String uptime;

	@JsonAlias("audioPairMode")
	private String chipsetLoad;

	@JsonAlias("audioPairMode")
	private String temperature;

	/**
	 * Retrieves {@code {@link #cardStatus}}
	 *
	 * @return value of {@link #cardStatus}
	 */
	public String getCardStatus() {
		return cardStatus;
	}

	/**
	 * Sets {@code cardStatus}
	 *
	 * @param cardStatus the {@code java.lang.String} field
	 */
	public void setCardStatus(String cardStatus) {
		this.cardStatus = cardStatus;
	}

	/**
	 * Retrieves {@code {@link #serialNumber}}
	 *
	 * @return value of {@link #serialNumber}
	 */
	public String getSerialNumber() {
		return serialNumber;
	}

	/**
	 * Sets {@code serialNumber}
	 *
	 * @param serialNumber the {@code java.lang.String} field
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	/**
	 * Retrieves {@code {@link #hardwareCompatibility}}
	 *
	 * @return value of {@link #hardwareCompatibility}
	 */
	public String getHardwareCompatibility() {
		return hardwareCompatibility;
	}

	/**
	 * Sets {@code hardwareCompatibility}
	 *
	 * @param hardwareCompatibility the {@code java.lang.String} field
	 */
	public void setHardwareCompatibility(String hardwareCompatibility) {
		this.hardwareCompatibility = hardwareCompatibility;
	}

	/**
	 * Retrieves {@code {@link #mezzaninePresent}}
	 *
	 * @return value of {@link #mezzaninePresent}
	 */
	public String getMezzaninePresent() {
		return mezzaninePresent;
	}

	/**
	 * Sets {@code mezzaninePresent}
	 *
	 * @param mezzaninePresent the {@code java.lang.String} field
	 */
	public void setMezzaninePresent(String mezzaninePresent) {
		this.mezzaninePresent = mezzaninePresent;
	}

	/**
	 * Retrieves {@code {@link #hardwareRevision}}
	 *
	 * @return value of {@link #hardwareRevision}
	 */
	public String getHardwareRevision() {
		return hardwareRevision;
	}

	/**
	 * Sets {@code hardwareRevision}
	 *
	 * @param hardwareRevision the {@code java.lang.String} field
	 */
	public void setHardwareRevision(String hardwareRevision) {
		this.hardwareRevision = hardwareRevision;
	}

	/**
	 * Retrieves {@code {@link #cpldRevision}}
	 *
	 * @return value of {@link #cpldRevision}
	 */
	public String getCpldRevision() {
		return cpldRevision;
	}

	/**
	 * Sets {@code cpldRevision}
	 *
	 * @param cpldRevision the {@code java.lang.String} field
	 */
	public void setCpldRevision(String cpldRevision) {
		this.cpldRevision = cpldRevision;
	}

	/**
	 * Retrieves {@code {@link #bootVersion}}
	 *
	 * @return value of {@link #bootVersion}
	 */
	public String getBootVersion() {
		return bootVersion;
	}

	/**
	 * Sets {@code bootVersion}
	 *
	 * @param bootVersion the {@code java.lang.String} field
	 */
	public void setBootVersion(String bootVersion) {
		this.bootVersion = bootVersion;
	}

	/**
	 * Retrieves {@code {@link #cardType}}
	 *
	 * @return value of {@link #cardType}
	 */
	public String getCardType() {
		return cardType;
	}

	/**
	 * Sets {@code cardType}
	 *
	 * @param cardType the {@code java.lang.String} field
	 */
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	/**
	 * Retrieves {@code {@link #partNumber}}
	 *
	 * @return value of {@link #partNumber}
	 */
	public String getPartNumber() {
		return partNumber;
	}

	/**
	 * Sets {@code partNumber}
	 *
	 * @param partNumber the {@code java.lang.String} field
	 */
	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	/**
	 * Retrieves {@code {@link #firmwareDate}}
	 *
	 * @return value of {@link #firmwareDate}
	 */
	public String getFirmwareDate() {
		return firmwareDate;
	}

	/**
	 * Sets {@code firmwareDate}
	 *
	 * @param firmwareDate the {@code java.lang.String} field
	 */
	public void setFirmwareDate(String firmwareDate) {
		this.firmwareDate = firmwareDate;
	}

	/**
	 * Retrieves {@code {@link #firmwareVersion}}
	 *
	 * @return value of {@link #firmwareVersion}
	 */
	public String getFirmwareVersion() {
		return firmwareVersion;
	}

	/**
	 * Sets {@code firmwareVersion}
	 *
	 * @param firmwareVersion the {@code java.lang.String} field
	 */
	public void setFirmwareVersion(String firmwareVersion) {
		this.firmwareVersion = firmwareVersion;
	}

	/**
	 * Retrieves {@code {@link #firmwareOptions}}
	 *
	 * @return value of {@link #firmwareOptions}
	 */
	public String getFirmwareOptions() {
		return firmwareOptions;
	}

	/**
	 * Sets {@code firmwareOptions}
	 *
	 * @param firmwareOptions the {@code java.lang.String} field
	 */
	public void setFirmwareOptions(String firmwareOptions) {
		this.firmwareOptions = firmwareOptions;
	}

	/**
	 * Retrieves {@code {@link #uptime}}
	 *
	 * @return value of {@link #uptime}
	 */
	public String getUptime() {
		return uptime;
	}

	/**
	 * Sets {@code uptime}
	 *
	 * @param uptime the {@code java.lang.String} field
	 */
	public void setUptime(String uptime) {
		this.uptime = uptime;
	}

	/**
	 * Retrieves {@code {@link #chipsetLoad}}
	 *
	 * @return value of {@link #chipsetLoad}
	 */
	public String getChipsetLoad() {
		return chipsetLoad;
	}

	/**
	 * Sets {@code chipsetLoad}
	 *
	 * @param chipsetLoad the {@code java.lang.String} field
	 */
	public void setChipsetLoad(String chipsetLoad) {
		this.chipsetLoad = chipsetLoad;
	}

	/**
	 * Retrieves {@code {@link #temperature}}
	 *
	 * @return value of {@link #temperature}
	 */
	public String getTemperature() {
		return temperature;
	}

	/**
	 * Sets {@code temperature}
	 *
	 * @param temperature the {@code java.lang.String} field
	 */
	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}
}