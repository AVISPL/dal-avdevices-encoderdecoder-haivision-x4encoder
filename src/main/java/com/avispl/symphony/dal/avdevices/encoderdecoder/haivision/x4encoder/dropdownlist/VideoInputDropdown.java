package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist;

import java.util.HashMap;
import java.util.Map;

/**
 * VideoInputDropdown class defined the enum for monitoring and controlling process
 *
 * @author Ivan / Symphony Dev Team<br>
 * Created on 3/8/2022
 * @since 1.0.0
 */
public enum VideoInputDropdown {

    BNC_1("BNC_1", 0),
    BNC_2("BNC_2", 1),
    BNC_3("BNC_3", 2),
    BNC_4("BNC_4", 3);

    private final String name;
    private final int value;

    /**
     * VideoInputDropdown instantiation
     *
     * @param name {@code {@link #name}}
     * @param value {@code {@link #value}}
     */
    VideoInputDropdown(String name, int value) {
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
    public int getValue() {
        return value;
    }

    /**
     * Retrieves name to value map of VideoInputDropdown
     *
     * @return Map<String, String> are map value and name
     */
    public static Map<Integer, String> getNameToValueMap() {
        Map<Integer, String> nameToValue = new HashMap<>();
        for (VideoInputDropdown videoInputDropdown : VideoInputDropdown.values()) {
            nameToValue.put(videoInputDropdown.getValue(), videoInputDropdown.getName());
        }
        return nameToValue;
    }

    /**
     * Retrieves name to value map of videoInputDropdown
     *
     * @return Map<String, Integer> are map name and value
     */
    public static Map<String, Integer> getValueToNameMap() {
        Map<String, Integer> valueToName = new HashMap<>();
        for (VideoInputDropdown videoInputDropdown : VideoInputDropdown.values()) {
            valueToName.put(videoInputDropdown.getName(), videoInputDropdown.getValue());
        }
        return valueToName;
    }
}
