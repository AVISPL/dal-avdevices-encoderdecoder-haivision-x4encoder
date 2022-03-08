package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * LanguageDropdown class defined the enum for monitoring and controlling process
 *
 * @author Ivan / Symphony Dev Team<br>
 * Created on 3/8/2022
 * @since 1.0.0
 */
public enum LanguageDropdown {

	ALBANIAN("Albanian (sqi)", "sqi"),
	ARABIC("Arabic (ara)", "ara"),
	ARMENIAN("Armenian (hye)", "hye"),
	ARMENIAN1("Armenian (arm)", "arm"),
	BULGARIAN("Bulgarian (bul)", "bul"),
	CHINESE("Chinese (chi)", "chi"),
	CHINESE1("Chinese (zho)", "zho"),
	CROATIAN("Croatian (hrv)", "hrv"),
	CZECH("Czech (cze)", "cze"),
	CZECH1("Czech (ces)", "ces"),
	DANISH("Danish (dan)", "dan"),
	DUTCH("Dutch (dut)", "dut"),
	DUTCH1("Dutch (nld)", "nld"),
	ENGLISH("English (eng)", "eng"),
	ESTONIAN("Estonian (est)", "est"),
	FINNISH("Finnish (fin)", "fin"),
	FRENCH("French (fre)", "fre"),
	FRENCH1("French (fra)", "fra"),
	GERMAN("German (ger)", "ger"),
	GERMAN1("German (deu)", "deu"),
	GREEK("Greek (gre)", "gre"),
	GREEK1("Greek (ell)", "ell"),
	HEBREW("Hebrew (heb)", "heb"),
	HINDI("Hindi (hin)", "hin"),
	HUNGARIAN("Hungarian (hun)", "hun"),
	INDONESIAN("Indonesian (ind)", "ind"),
	IRISH("Irish (gle)", "gle"),
	ICELANDIC("Icelandic (ice)", "ice"),
	ICELANDIC1("Icelandic (isl)", "isl"),
	ITALIAN("Italian (ita)", "ita"),
	JAPANESE("Japanese (jpn)", "jpn"),
	KHMER("Khmer (khm)", "khm"),
	KOREAN("Korean (kor)", "kor"),
	LATVIAN("Latvian (lav)", "lav"),
	LITHUANIAN("Lithuanian (lit)", "lit"),
	MALAY("Malay (may)", "may"),
	MALAY1("Malay (msa)", "msa"),
	MALTESE("Maltese (mlt)", "mlt"),
	MONGOLIAN("Mongolian (mon)", "mon"),
	NORWEGIAN("Norwegian (nor)", "nor"),
	PUNJABI("Punjabi (pan)", "pan"),
	PERSIAN("Persian (per)", "per"),
	PERSIAN1("Persian (fas)", "fas"),
	POLISH("Polish (pol)", "pol"),
	PORTUGUESE("Portuguese (por)", "por"),
	ROMANIAN("Romanian (rum)", "rum"),
	ROMANIAN1("Romanian (ron)", "ron"),
	RUSSIAN("Russian (rus)", "rus"),
	SLOVAK("Slovak (slo)", "slo"),
	SLOVAK1("Slovak (slk)", "slk"),
	SLOVENIAN("Slovenian (slv)", "slv"),
	SPANISH("Spanish (spa)", "spa"),
	SWAHILI("Swahili (swa)", "swa"),
	SWEDISH("Swedish (swe)", "swe"),
	TURKISH("Turkish (tur)", "tur"),
	UKRAINIAN("Ukrainian (ukr)", "ukr"),
	VIETNAMESE("Vietnamese (vie)", "vie"),
	NONE("None","None");

	private final String name;
	private final String value;

	/**
	 * LanguageDropdown instantiation
	 *
	 * @param name {@code {@link #name}}
	 * @param value {@code {@link #value}}
	 */
	LanguageDropdown(String name, String value) {
		this.name = name;
		this.value = value;
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
	 * Retrieves all name of LanguageDropdown
	 *
	 * @return list name of Language
	 */
	public static String[] names() {
		List<String> list = new LinkedList<>();
		for (LanguageDropdown languageDropdown : LanguageDropdown.values()) {
			list.add(languageDropdown.getName());
		}
		return list.toArray(new String[list.size()]);
	}

	/**
	 * Retrieves name to value map of LanguageDropdown
	 *
	 * @return Map<String, String> are name and value
	 */
	public static Map<String, String> getNameToValueMap() {
		Map<String, String> nameToValue = new HashMap<>();
		for (LanguageDropdown languageDropdown : LanguageDropdown.values()) {
			nameToValue.put(languageDropdown.getValue(), languageDropdown.getName());
		}
		return nameToValue;
	}

	/**
	 * Retrieves name to value map of languageDropdown
	 *
	 * @return Map<Integer, String> are name and value
	 */
	public static Map<String, String> getValueToNameMap() {
		Map<String, String> valueToName = new HashMap<>();
		for (LanguageDropdown languageDropdown : LanguageDropdown.values()) {
			valueToName.put(languageDropdown.getName(), languageDropdown.getValue());
		}
		return valueToName;
	}
}
