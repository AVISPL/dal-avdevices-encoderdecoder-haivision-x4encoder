/*
 *  * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.encoderdecoder.haivision.x4encoder;

import com.avispl.symphony.api.dal.control.Controller;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;
import com.avispl.symphony.api.dal.monitor.Monitorable;
import com.avispl.symphony.dal.communicator.RestCommunicator;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An implementation of RestCommunicator to provide communication and interaction with Haivision X4 Encoders
 * Supported features are:
 * <p>
 * Monitoring:
 * <p>
 * Controlling:
 *
 * @author Ivan
 * @since 1.0.0
 */
public class HaivisionX4EncoderCommunicator extends RestCommunicator implements Monitorable, Controller {


    /**
     * This method is called by Symphony to control the properties in the device
     *
     * @param controllableProperty ControllableProperty instance
     * @throws Exception if unexpected the value
     */
    @Override
    public void controlProperty(ControllableProperty controllableProperty) throws Exception {

    }

    /**
     * This method is called by Symphony to control the properties in the device
     *
     * @param list the list ControllableProperty instance
     * @throws Exception if ControllableProperty list is empty
     */
    @Override
    public void controlProperties(List<ControllableProperty> list) throws Exception {

    }

    /**
     * This method is called by Symphony to get the list of statistics to be displayed
     *
     * @return List<Statistics> This return the list of statistics
     */
    @Override
    public List<Statistics> getMultipleStatistics() throws Exception {
        ExtendedStatistics extendedStatistics = new ExtendedStatistics();
        Map<String, String> stats = new HashMap<>();
        extendedStatistics.setStatistics(stats);

        return Collections.singletonList(extendedStatistics);
    }

    @Override
    protected void authenticate() throws Exception {

    }
}