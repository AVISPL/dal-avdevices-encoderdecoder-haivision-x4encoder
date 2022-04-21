/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder.dropdownlist;

/**
 * VideoDropdown class defined the enum for monitoring and controlling process
 *
 * @author Ivan / Symphony Dev Team<br>
 * Created on 3/9/2022
 * @since 1.0.0
 */
public enum VideoDropdown {

    NONE("None", "None"),
    VIDEO_0("Video Encoder 0", "0"),
    VIDEO_1("Video Encoder 1", "1"),
    VIDEO_2("Video Encoder 2", "2"),
    VIDEO_3("Video Encoder 3", "3"),
    VIDEO_4("Video Encoder 4", "4"),
    VIDEO_5("Video Encoder 5", "5"),
    VIDEO_6("Video Encoder 6", "6"),
    VIDEO_7("Video Encoder 7", "7");

    private String name;
    private String id;

    VideoDropdown(String name, String id) {
        this.name = name;
        this.id = id;
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
     * Retrieves {@code {@link #id}}
     *
     * @return value of {@link #id}
     */
    public String getId() {
        return id;
    }

    /**
     * Sets {@code id}
     *
     * @param id the {@code int} field
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Retrieve default video
     *
     * @return the first video
     */
    public static String getDefault() {
        String defaultValue = "";
        for (VideoDropdown videoDropdown : VideoDropdown.values()) {
            if (videoDropdown != NONE) {
                defaultValue = videoDropdown.getName();
                break;
            }
        }
        return defaultValue;
    }
}