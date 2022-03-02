package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
     * Retrieves all name of VideoInputDropdown
     *
     * @return list name of Input Interface
     */
    public static String[] names() {
        List<String> list = new LinkedList<>();
        for (VideoInputDropdown videoInputDropdown : VideoInputDropdown.values()) {
            list.add(videoInputDropdown.getName());
        }
        return list.toArray(new String[list.size()]);
    }

    /**
     * Retrieves name to value map of VideoInputDropdown
     *
     * @return Map<String, String> are name and value
     */
    public static Map<Integer, String> getNameToValueMap() {
        Map<Integer, String> nameToValue = new HashMap<>();
        for (VideoInputDropdown videoInputDropdown : VideoInputDropdown.values()) {
            nameToValue.put(videoInputDropdown.getValue(), videoInputDropdown.getName());
        }
        return nameToValue;
    }
}
