/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto;

import java.util.List;

/**
 * Output Response Wrapper DTO class
 *
 * @author Ivan
 * @version 1.0.0
 * @since 1.0.0
 */
public class OutputResponseWrapper {

	private List<OutputResponse> data;

	/**
	 * Retrieves {@code {@link #data}}
	 *
	 * @return value of {@link #data}
	 */
	public List<OutputResponse> getData() {
		return data;
	}

	/**
	 * Sets {@code data}
	 *
	 * @param data the {@code java.util.List<com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dto.OutputResponse>} field
	 */
	public void setData(List<OutputResponse> data) {
		this.data = data;
	}
}
