/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist;


import java.util.LinkedList;
import java.util.List;

import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.HaivisionConstant;

/**
 * ResolutionDropdown class defined the enum for monitoring and controlling process
 *
 * @author Ivan
 * @version 1.0.0
 * @since 1.0.0
 */
public enum ResolutionDropdown {

	RESOLUTION_AUTOMATIC("Automatic", false),
	RESOLUTION_3840_2160P("3840_2160p", false),
	RESOLUTION_1920_1080P("1920_1080p", true),
	RESOLUTION_1920_1080I("1920_1080i", true),
	RESOLUTION_1440_1080P("1440_1080p", true),
	RESOLUTION_1440_1080I("1440_1080i", true),
	RESOLUTION_960_1080P("960_1080p", true),
	RESOLUTION_960_1080I("960_1080i", true),
	RESOLUTION_1280_720("1280_720", true),
	RESOLUTION_960_720("960_720", true),
	RESOLUTION_640_720("640_720", true),
	RESOLUTION_720_480P("720_480p", true),
	RESOLUTION_720_480I("720_480i", true),
	RESOLUTION_720_576P("720_576p", true),
	RESOLUTION_720_576I("720_576i", true),
	RESOLUTION_540_480P("540_480p", true),
	RESOLUTION_540_480("540_480", true),
	RESOLUTION_704_576P("704_576p", true),
	RESOLUTION_704_576I("704_576i", true),
	RESOLUTION_540_576P("540_576p", true),
	RESOLUTION_540_576I("540_576i", true),
	RESOLUTION_352_480P("352_480p", true),
	RESOLUTION_352_480I("352_480i", true),
	RESOLUTION_352_576P("352_576p", true),
	RESOLUTION_352_576I("352_576i", true),
	RESOLUTION_352_288P("352_288p", true),
	RESOLUTION_352_288I("352_288i", true);

	private final String name;
	private final boolean isCropping;

	/**
	 * ResolutionDropdown instantiation
	 *
	 * @param name {@code {@link #name}}
	 * @param isCropping {@code {@link #isCropping}}
	 */
	ResolutionDropdown(String name, boolean isCropping) {
		this.name = name;
		this.isCropping = isCropping;
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
	 * Retrieves all name of ResolutionDropdown
	 *
	 * @return list name of ResolutionDropdown
	 */
	public static String[] names() {
		List<String> list = new LinkedList<>();
		for (ResolutionDropdown resolutionDropdown : ResolutionDropdown.values()) {
			list.add(resolutionDropdown.getName());
		}
		return list.toArray(new String[list.size()]);
	}

	/**
	 * Retrieves all name of ResolutionDropdown
	 *
	 * @return list name of ResolutionDropdown
	 */
	public static List<String> getDropdownListNotCropping() {
		List<String> list = new LinkedList<>();
		for (ResolutionDropdown resolutionDropdown : ResolutionDropdown.values()) {
			if(!resolutionDropdown.isCropping){
				list.add(resolutionDropdown.getName());
			}
		}
		list.add(HaivisionConstant.NONE);
		return list;
	}
}
