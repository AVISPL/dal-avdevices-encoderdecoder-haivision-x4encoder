/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist;

import java.util.LinkedList;
import java.util.List;

/**
 * BitRateDropdown class defined the enum for monitoring and controlling process
 *
 * @author Ivan
 * @version 1.0.0
 * @since 1.0.0
 */
public enum BitRateDropdown {

	Number_12("12 kbps", 12, false, true),
	Number_14("14 kbps", 14, true, false),
	Number_16("16 kbps", 16, false, true),
	Number_24("24 kbps", 24, false, true),
	Number_32("32 kbps", 32, true, true),
	Number_40("40 kbps", 40, true, true),
	Number_48("48 kbps", 48, true, true),
	Number_56("56 kbps", 56, true, true),
	Number_64("64 kbps", 64, true, true),
	Number_80("80 kbps", 80, true, false),
	Number_96("96 kbps", 96, true, true),
	Number_128("128 kbps", 128, true, true),
	Number_160("160 kbps", 160, true, true),
	Number_192("192 kbps", 192, true, true),
	Number_224("224 kbps", 224, true, true),
	Number_256("256 kbps", 256, true, true),
	Number_288("288 kbps", 288, true, true),
	Number_320("320 kbps", 320, true, false),
	Number_384("384 kbps", 384, true, false),
	Number_448("448 kbps", 448, true, false),
	Number_512("512 kbps", 512, true, false),
	Number_576("576 kbps", 576, true, false);

	private final String name;
	private final int value;
	private final boolean isStereo;
	private final boolean isMono;

	/**
	 * BitRateDropdown instantiation
	 *
	 * @param name {@code {@link #name}}
	 * @param value {@code {@link #value}}
	 * @param isStereo {@code {@link #isStereo}}
	 * @param isMono {@code {@link #isMono}}
	 */
	BitRateDropdown(String name, int value, boolean isStereo, boolean isMono) {
		this.name = name;
		this.value = value;
		this.isStereo = isStereo;
		this.isMono = isMono;
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
	public int getValue() {
		return value;
	}

	/**
	 * Retrieves {@code {@link #isStereo}}
	 *
	 * @return value of {@link #isStereo}
	 */
	public boolean isStereo() {
		return isStereo;
	}

	/**
	 * Retrieves {@code {@link #isMono}}
	 *
	 * @return value of {@link #isMono}
	 */
	public boolean isMono() {
		return isMono;
	}

	/**
	 * Retrieves all name of BitRateDropdown is stereo mode
	 *
	 * @return list name of BitRate
	 */
	public static String[] namesIsStereo() {
		List<String> list = new LinkedList<>();
		for (BitRateDropdown bitRateDropdown : BitRateDropdown.values()) {
			if(bitRateDropdown.isStereo()){
				list.add(bitRateDropdown.getName());
			}
		}
		return list.toArray(new String[list.size()]);
	}

	/**
	 * Retrieves all name of BitRateDropdown is mono mode
	 *
	 * @return list name of BitRate
	 */
	public static String[] namesIsMono() {
		List<String> list = new LinkedList<>();
		for (BitRateDropdown bitRateDropdown : BitRateDropdown.values()) {
			if(bitRateDropdown.isMono()){
				list.add(bitRateDropdown.getName());
			}
		}
		return list.toArray(new String[list.size()]);
	}
}
