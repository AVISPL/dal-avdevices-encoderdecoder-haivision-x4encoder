/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto;

import java.util.List;

/**
 * Audio Response Wrapper DTO class
 *
 * @author Ivan / Symphony Dev Team<br>
 * Created on 3/8/2022
 * @since 1.0.0
 */
public class AudioResponseWrapper {

	private List<AudioResponse> data;

	/**
	 * Retrieves {@code {@link #data}}
	 *
	 * @return value of {@link #data}
	 */
	public List<AudioResponse> getData() {
		return data;
	}

	/**
	 * Sets {@code data}
	 *
	 * @param data the {@code java.util.List<com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.AudioResponse>} field
	 */
	public void setData(List<AudioResponse> data) {
		this.data = data;
	}
}
