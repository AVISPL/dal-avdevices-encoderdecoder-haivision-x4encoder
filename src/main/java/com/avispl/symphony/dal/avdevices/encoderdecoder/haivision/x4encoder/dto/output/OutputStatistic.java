/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.output;

/**
 * Output Statistic DTO class
 *
 * @author Ivan
 * @version 1.0.0
 * @since 1.0.0
 */
public class OutputStatistic {
	private String uptime;
	private String sourcePort;
	private String sentPackets;
	private String sentBytes;
	private String bitrate;
	private String reconnections;
	private String resentPackets;
	private String resentBytes;
	private String droppedPackets;
	private String droppedBytes;
	private String mss;
	private String maxBandwidth;
	private String remotePort;
	private String sourceAddress;
	private String remoteAddress;
	private String pathMaxBandwidth;
	private String lostPackets;
	private String recvACK;
	private String recvNAK;
	private String rtt;
	private String buffer;
	private String latency;
	private String state;
	private String occurred;

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
	 * Retrieves {@code {@link #sentPackets}}
	 *
	 * @return value of {@link #sentPackets}
	 */
	public String getSentPackets() {
		return sentPackets;
	}

	/**
	 * Sets {@code sentPackets}
	 *
	 * @param sentPackets the {@code java.lang.String} field
	 */
	public void setSentPackets(String sentPackets) {
		this.sentPackets = sentPackets;
	}

	/**
	 * Retrieves {@code {@link #sentBytes}}
	 *
	 * @return value of {@link #sentBytes}
	 */
	public String getSentBytes() {
		return sentBytes;
	}

	/**
	 * Sets {@code sentBytes}
	 *
	 * @param sentBytes the {@code java.lang.String} field
	 */
	public void setSentBytes(String sentBytes) {
		this.sentBytes = sentBytes;
	}

	/**
	 * Retrieves {@code {@link #bitrate}}
	 *
	 * @return value of {@link #bitrate}
	 */
	public String getBitrate() {
		return bitrate;
	}

	/**
	 * Sets {@code bitrate}
	 *
	 * @param bitrate the {@code java.lang.String} field
	 */
	public void setBitrate(String bitrate) {
		this.bitrate = bitrate;
	}

	/**
	 * Retrieves {@code {@link #reconnections}}
	 *
	 * @return value of {@link #reconnections}
	 */
	public String getReconnections() {
		return reconnections;
	}

	/**
	 * Sets {@code reconnections}
	 *
	 * @param reconnections the {@code java.lang.String} field
	 */
	public void setReconnections(String reconnections) {
		this.reconnections = reconnections;
	}

	/**
	 * Retrieves {@code {@link #resentPackets}}
	 *
	 * @return value of {@link #resentPackets}
	 */
	public String getResentPackets() {
		return resentPackets;
	}

	/**
	 * Sets {@code resentPackets}
	 *
	 * @param resentPackets the {@code java.lang.String} field
	 */
	public void setResentPackets(String resentPackets) {
		this.resentPackets = resentPackets;
	}

	/**
	 * Retrieves {@code {@link #resentBytes}}
	 *
	 * @return value of {@link #resentBytes}
	 */
	public String getResentBytes() {
		return resentBytes;
	}

	/**
	 * Sets {@code resentBytes}
	 *
	 * @param resentBytes the {@code java.lang.String} field
	 */
	public void setResentBytes(String resentBytes) {
		this.resentBytes = resentBytes;
	}

	/**
	 * Retrieves {@code {@link #droppedPackets}}
	 *
	 * @return value of {@link #droppedPackets}
	 */
	public String getDroppedPackets() {
		return droppedPackets;
	}

	/**
	 * Sets {@code droppedPackets}
	 *
	 * @param droppedPackets the {@code java.lang.String} field
	 */
	public void setDroppedPackets(String droppedPackets) {
		this.droppedPackets = droppedPackets;
	}

	/**
	 * Retrieves {@code {@link #droppedBytes}}
	 *
	 * @return value of {@link #droppedBytes}
	 */
	public String getDroppedBytes() {
		return droppedBytes;
	}

	/**
	 * Sets {@code droppedBytes}
	 *
	 * @param droppedBytes the {@code java.lang.String} field
	 */
	public void setDroppedBytes(String droppedBytes) {
		this.droppedBytes = droppedBytes;
	}

	/**
	 * Retrieves {@code {@link #mss}}
	 *
	 * @return value of {@link #mss}
	 */
	public String getMss() {
		return mss;
	}

	/**
	 * Sets {@code mss}
	 *
	 * @param mss the {@code java.lang.String} field
	 */
	public void setMss(String mss) {
		this.mss = mss;
	}

	/**
	 * Retrieves {@code {@link #maxBandwidth}}
	 *
	 * @return value of {@link #maxBandwidth}
	 */
	public String getMaxBandwidth() {
		return maxBandwidth;
	}

	/**
	 * Sets {@code maxBandwidth}
	 *
	 * @param maxBandwidth the {@code java.lang.String} field
	 */
	public void setMaxBandwidth(String maxBandwidth) {
		this.maxBandwidth = maxBandwidth;
	}

	/**
	 * Retrieves {@code {@link #remotePort}}
	 *
	 * @return value of {@link #remotePort}
	 */
	public String getRemotePort() {
		return remotePort;
	}

	/**
	 * Sets {@code remotePort}
	 *
	 * @param remotePort the {@code java.lang.String} field
	 */
	public void setRemotePort(String remotePort) {
		this.remotePort = remotePort;
	}

	/**
	 * Retrieves {@code {@link #sourceAddress}}
	 *
	 * @return value of {@link #sourceAddress}
	 */
	public String getSourceAddress() {
		return sourceAddress;
	}

	/**
	 * Sets {@code sourceAddress}
	 *
	 * @param sourceAddress the {@code java.lang.String} field
	 */
	public void setSourceAddress(String sourceAddress) {
		this.sourceAddress = sourceAddress;
	}

	/**
	 * Retrieves {@code {@link #remoteAddress}}
	 *
	 * @return value of {@link #remoteAddress}
	 */
	public String getRemoteAddress() {
		return remoteAddress;
	}

	/**
	 * Sets {@code remoteAddress}
	 *
	 * @param remoteAddress the {@code java.lang.String} field
	 */
	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	/**
	 * Retrieves {@code {@link #pathMaxBandwidth}}
	 *
	 * @return value of {@link #pathMaxBandwidth}
	 */
	public String getPathMaxBandwidth() {
		return pathMaxBandwidth;
	}

	/**
	 * Sets {@code pathMaxBandwidth}
	 *
	 * @param pathMaxBandwidth the {@code java.lang.String} field
	 */
	public void setPathMaxBandwidth(String pathMaxBandwidth) {
		this.pathMaxBandwidth = pathMaxBandwidth;
	}

	/**
	 * Retrieves {@code {@link #lostPackets}}
	 *
	 * @return value of {@link #lostPackets}
	 */
	public String getLostPackets() {
		return lostPackets;
	}

	/**
	 * Sets {@code lostPackets}
	 *
	 * @param lostPackets the {@code java.lang.String} field
	 */
	public void setLostPackets(String lostPackets) {
		this.lostPackets = lostPackets;
	}

	/**
	 * Retrieves {@code {@link #recvACK}}
	 *
	 * @return value of {@link #recvACK}
	 */
	public String getRecvACK() {
		return recvACK;
	}

	/**
	 * Sets {@code recvACK}
	 *
	 * @param recvACK the {@code java.lang.String} field
	 */
	public void setRecvACK(String recvACK) {
		this.recvACK = recvACK;
	}

	/**
	 * Retrieves {@code {@link #recvNAK}}
	 *
	 * @return value of {@link #recvNAK}
	 */
	public String getRecvNAK() {
		return recvNAK;
	}

	/**
	 * Sets {@code recvNAK}
	 *
	 * @param recvNAK the {@code java.lang.String} field
	 */
	public void setRecvNAK(String recvNAK) {
		this.recvNAK = recvNAK;
	}

	/**
	 * Retrieves {@code {@link #rtt}}
	 *
	 * @return value of {@link #rtt}
	 */
	public String getRtt() {
		return rtt;
	}

	/**
	 * Sets {@code rtt}
	 *
	 * @param rtt the {@code java.lang.String} field
	 */
	public void setRtt(String rtt) {
		this.rtt = rtt;
	}

	/**
	 * Retrieves {@code {@link #buffer}}
	 *
	 * @return value of {@link #buffer}
	 */
	public String getBuffer() {
		return buffer;
	}

	/**
	 * Sets {@code buffer}
	 *
	 * @param buffer the {@code java.lang.String} field
	 */
	public void setBuffer(String buffer) {
		this.buffer = buffer;
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
	 * Retrieves {@code {@link #occurred}}
	 *
	 * @return value of {@link #occurred}
	 */
	public String getOccurred() {
		return occurred;
	}

	/**
	 * Sets {@code occurred}
	 *
	 * @param occurred the {@code java.lang.String} field
	 */
	public void setOccurred(String occurred) {
		this.occurred = occurred;
	}
}
