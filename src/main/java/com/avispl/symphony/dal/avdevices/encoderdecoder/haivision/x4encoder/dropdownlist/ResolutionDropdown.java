/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.common.HaivisionConstant;

/**
 * ResolutionDropdown class defined the enum for monitoring and controlling process
 *
 * @author Ivan / Symphony Dev Team<br>
 * Created on 3/8/2022
 * @since 1.0.0
 */
public enum ResolutionDropdown {

	RESOLUTION_AUTOMATIC("Automatic", "0", false),
	RESOLUTION_3840_2160P("3840x2160p", "9223372052962009088", false),
	RESOLUTION_1920_1080P("1920x1080p", "9223372044908392448", true),
	RESOLUTION_1920_1080I("1920x1080i", "9223372044908392449", true),
	RESOLUTION_1440_1080P("1440x1080p", "9223372042895126528", true),
	RESOLUTION_1440_1080I("1440x1080i", "9223372042895126529", true),
	RESOLUTION_1280_720("1280x720p", "9223372042223853568", true),
	RESOLUTION_960_1080P("960x1080p", "9223372040881860608", true),
	RESOLUTION_960_1080I("960x1080i", "9223372040881860609", true),
	RESOLUTION_960_720("960x720p", "9223372040881676288", true),
	RESOLUTION_720_576P("720x576p", "9223372039874969600", true),
	RESOLUTION_720_576I("720x576i", "9223372039874969601", true),
	RESOLUTION_720_480P("720x480p", "9223372039874920448", true),
	RESOLUTION_720_480I("720x480i", "9223372039874920449", true),
	RESOLUTION_640_720("640x720p", "9223372039539499008", true),
	RESOLUTION_540_576P("540x576p", "9223372039119994880", true),
	RESOLUTION_540_576I("540x576i", "9223372039119994881", true),
	RESOLUTION_540_480I("540x480i", "9223372039119945729", true),
	RESOLUTION_540_480P("540x480p", "9223372039119945728", true),
	RESOLUTION_448_336P("448x336p", "9223372038733996032", true),
	RESOLUTION_352_480P("352x480p", "9223372038331416576", true),
	RESOLUTION_352_480I("352x480i", "9223372038331416577", true),
	RESOLUTION_352_576P("352x576p", "9223372038331465728", true),
	RESOLUTION_352_576I("352x576i", "9223372038331465729", true),
	RESOLUTION_352_288P("352x288p", "9223372038331318272", true),
	RESOLUTION_352_288I("352x288i", "9223372038331318273", true);

	private final String name;
	private final String value;
	private final boolean isCropping;

	/**
	 * ResolutionDropdown instantiation
	 *
	 * @param name {@code {@link #name}}
	 * @param value {@code {@link #value}}
	 * @param isCropping {@code {@link #isCropping}}
	 */
	ResolutionDropdown(String name, String value, boolean isCropping) {
		this.name = name;
		this.value = value;
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
	 * Retrieves {@code {@link #value}}
	 *
	 * @return value of {@link #value}
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Retrieves {@code {@link #isCropping}}
	 *
	 * @return value of {@link #isCropping}
	 */
	public boolean isCropping() {
		return isCropping;
	}

	/**
	 * Retrieves name to value map of ResolutionDropdown
	 *
	 * @return Map<Integer, String> are map value and name
	 */
	public static Map<String, String> getNameToValueMap() {
		Map<String, String> nameToValue = new HashMap<>();
		for (ResolutionDropdown resolutionDropdown : values()) {
			nameToValue.put(resolutionDropdown.getValue(), resolutionDropdown.getName());
		}
		return nameToValue;
	}

	/**
	 * Retrieves name to value map of resolutionDropdown
	 *
	 * @return Map<String, Integer> are map name and value
	 */
	public static Map<String, String> getValueToNameMap() {
		Map<String, String> valueToName = new HashMap<>();
		for (ResolutionDropdown resolutionDropdown : values()) {
			valueToName.put(resolutionDropdown.getName(), resolutionDropdown.getValue());
		}
		return valueToName;
	}

	/**
	 * Retrieves all name of ResolutionDropdown
	 *
	 * @return list name of ResolutionDropdown
	 */
	public static List<String> getDropdownListNotCropping() {
		List<String> list = new LinkedList<>();
		for (ResolutionDropdown resolutionDropdown : values()) {
			if (!resolutionDropdown.isCropping()) {
				list.add(resolutionDropdown.getName());
			}
		}
		list.add(HaivisionConstant.NONE);
		return list;
	}

	/**
	 * Check if enum is cropping or not
	 *
	 * @param name of enum
	 */
	public static boolean checkIsCropping(String name) {
		boolean isCropping = false;
		for (ResolutionDropdown resolution : values()) {
			if (resolution.getName().equals(name)) {
				isCropping = resolution.isCropping();
				break;
			}
		}
		return isCropping;
	}
}
