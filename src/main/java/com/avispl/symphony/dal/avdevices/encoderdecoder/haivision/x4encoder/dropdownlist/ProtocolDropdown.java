/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist;

import java.util.HashMap;
import java.util.Map;

/**
 * ProtocolDropdown class defined the enum for monitoring and controlling process
 *
 * @author Ivan / Symphony Dev Team<br>
 * Created on 3/8/2022
 * @since 1.0.0
 */
public enum ProtocolDropdown {

    TS_OVER_UDP("TS over UDP", 2),
    TS_OVER_RTP("TS over RTP", 3),
    TS_OVER_SRT("TS over SRT", 34);

    private String name;
    private int value;

    ProtocolDropdown(String name, int value) {
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
     * Sets {@code name}
     *
     * @param name the {@code java.lang.String} field
     */
    public void setName(String name) {
        this.name = name;
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
     * Sets {@code value}
     *
     * @param value the {@code int} field
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * Retrieves name to value map of ProtocolDropdown
     *
     * @return Map<Integer, String> are map value and name
     */
    public static Map<Integer, String> getNameToValueMap() {
        Map<Integer, String> nameToValue = new HashMap<>();
        for (ProtocolDropdown protocolDropdown : values()) {
            nameToValue.put(protocolDropdown.getValue(), protocolDropdown.getName());
        }
        return nameToValue;
    }

    /**
     * Retrieves name to value map of protocolDropdown
     *
     * @return Map<String,Integer> are map name and value
     */
    public static Map<String, Integer> getValueToNameMap() {
        Map<String, Integer> valueToName = new HashMap<>();
        for (ProtocolDropdown protocolDropdown : values()) {
            valueToName.put(protocolDropdown.getName(), protocolDropdown.getValue());
        }
        return valueToName;
    }
}