/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto;

import java.util.List;

/**
 * Video Response Wrapper DTO class
 *
 * @author Ivan
 * @version 1.0.0
 * @since 1.0.0
 */
public class VideoResponseWrapper {

	private List<VideoResponse> data;

	/**
	 * Retrieves {@code {@link #data}}
	 *
	 * @return value of {@link #data}
	 */
	public List<VideoResponse> getData() {
		return data;
	}

	/**
	 * Sets {@code data}
	 *
	 * @param data the {@code java.util.List<com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.VideoResponse>} field
	 */
	public void setData(List<VideoResponse> data) {
		this.data = data;
	}
}
