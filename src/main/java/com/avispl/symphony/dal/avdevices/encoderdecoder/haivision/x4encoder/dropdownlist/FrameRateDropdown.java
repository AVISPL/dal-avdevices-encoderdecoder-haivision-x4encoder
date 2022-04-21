/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist;

/**
 * FrameRateDropdown class defined the enum for monitoring and controlling process
 *
 * @author Ivan / Symphony Dev Team<br>
 * Created on 3/8/2022
 * @since 1.0.0
 */
public enum FrameRateDropdown {

	FAME_RATE_0("Automatic"),
	FAME_RATE_1("1"),
	FAME_RATE_2("2"),
	FAME_RATE_3("3"),
	FAME_RATE_4("4"),
	FAME_RATE_5("5"),
	FAME_RATE_6("6"),
	FAME_RATE_7("7"),
	FAME_RATE_8("8"),
	FAME_RATE_9("9"),
	FAME_RATE_10("10"),
	FAME_RATE_11("11"),
	FAME_RATE_12("12"),
	FAME_RATE_13("13"),
	FAME_RATE_14("14"),
	FAME_RATE_15("15"),
	FAME_RATE_16("16"),
	FAME_RATE_17("17"),
	FAME_RATE_18("18"),
	FAME_RATE_19("19"),
	FAME_RATE_20("20"),
	FAME_RATE_21("21"),
	FAME_RATE_22("22"),
	FAME_RATE_23("23"),
	FAME_RATE_24("24"),
	FAME_RATE_25("25"),
	FAME_RATE_26("26"),
	FAME_RATE_27("27"),
	FAME_RATE_28("28"),
	FAME_RATE_29("29"),
	FAME_RATE_30("30"),
	FAME_RATE_31("31"),
	FAME_RATE_32("32"),
	FAME_RATE_33("33"),
	FAME_RATE_34("34"),
	FAME_RATE_35("35"),
	FAME_RATE_36("36"),
	FAME_RATE_37("37"),
	FAME_RATE_38("38"),
	FAME_RATE_39("39"),
	FAME_RATE_40("40"),
	FAME_RATE_41("41"),
	FAME_RATE_42("42"),
	FAME_RATE_43("43"),
	FAME_RATE_44("44"),
	FAME_RATE_45("45"),
	FAME_RATE_46("46"),
	FAME_RATE_47("47"),
	FAME_RATE_48("48"),
	FAME_RATE_49("49"),
	FAME_RATE_50("50"),
	FAME_RATE_51("51"),
	FAME_RATE_52("52"),
	FAME_RATE_53("53"),
	FAME_RATE_54("54"),
	FAME_RATE_55("55"),
	FAME_RATE_56("56"),
	FAME_RATE_57("57"),
	FAME_RATE_58("58"),
	FAME_RATE_59("59"),
	FAME_RATE_60("60");

	private final String name;

	/**
	 * FrameRateDropdown instantiation
	 *
	 * @param name {@code {@link #name}}
	 */
	FrameRateDropdown(String name) {
		this.name = name;
	}

	/**
	 * Retrieves {@code {@link #name}}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}
}
