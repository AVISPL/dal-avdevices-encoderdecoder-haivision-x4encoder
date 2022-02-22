/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.video;

/**
 * Video DTO class
 *
 * @author Ivan
 * @version 1.0.0
 * @since 1.0.0
 */
public class Video {
	private String id;
	private String name;
	private String pid;
	private String autoAssigned;

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
	 * Retrieves {@code {@link #pid}}
	 *
	 * @return value of {@link #pid}
	 */
	public String getPid() {
		return pid;
	}

	/**
	 * Sets {@code pid}
	 *
	 * @param pid the {@code java.lang.String} field
	 */
	public void setPid(String pid) {
		this.pid = pid;
	}

	/**
	 * Retrieves {@code {@link #autoAssigned}}
	 *
	 * @return value of {@link #autoAssigned}
	 */
	public String getAutoAssigned() {
		return autoAssigned;
	}

	/**
	 * Sets {@code autoAssigned}
	 *
	 * @param autoAssigned the {@code java.lang.String} field
	 */
	public void setAutoAssigned(String autoAssigned) {
		this.autoAssigned = autoAssigned;
	}
}
