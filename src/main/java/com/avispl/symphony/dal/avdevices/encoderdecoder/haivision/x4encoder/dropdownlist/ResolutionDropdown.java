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
	RESOLUTION_3840_2160P("3840x2160p", false),
	RESOLUTION_1920_1080P("1920x1080p", true),
	RESOLUTION_1920_1080I("1920x1080i", true),
	RESOLUTION_1440_1080P("1440x1080p", true),
	RESOLUTION_1440_1080I("1440x1080i", true),
	RESOLUTION_960_1080P("960x1080p", true),
	RESOLUTION_960_1080I("960x1080i", true),
	RESOLUTION_1280_720("1280x720", true),
	RESOLUTION_960_720("960x720", true),
	RESOLUTION_640_720("640x720", true),
	RESOLUTION_720_480P("720x480p", true),
	RESOLUTION_720_480I("720x480i", true),
	RESOLUTION_720_576P("720x576p", true),
	RESOLUTION_720_576I("720x576i", true),
	RESOLUTION_540_480P("540x480p", true),
	RESOLUTION_540_480("540x480", true),
	RESOLUTION_704_576P("704x576p", true),
	RESOLUTION_704_576I("704x576i", true),
	RESOLUTION_540_576P("540x576p", true),
	RESOLUTION_540_576I("540x576i", true),
	RESOLUTION_352_480P("352x480p", true),
	RESOLUTION_352_480I("352x480i", true),
	RESOLUTION_352_576P("352x576p", true),
	RESOLUTION_352_576I("352x576i", true),
	RESOLUTION_352_288P("352x288p", true),
	RESOLUTION_352_288I("352x288i", true);

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
