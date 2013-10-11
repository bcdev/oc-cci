/*
 * Copyright (C) 2013 Brockmann Consult GmbH (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.beam.occci.merging;


import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class SensorMergingTest {

    @Test
    public void testCreation() throws Exception {

        String[] spatial = {"rrs_2", "rrs_4", "sensor"};
        String[] temporal = {
                "rrs_2_meris", "rrs_2_modis", "rrs_2_seawifs",
                "rrs_2_meris_biasmap", "rrs_2_modis_biasmap", "rrs_2_seawifs_biasmap",

                "rrs_4_meris", "rrs_4_modis", "rrs_4_seawifs",
                "rrs_4_meris_biasmap", "rrs_4_modis_biasmap", "rrs_4_seawifs_biasmap"
        };
        String[] output0 = {"rrs_2", "rrs_4", "sensor_0", "sensor_1", "sensor_2"};
        String[] output2 = {
                "rrs_2_meris",
                "rrs_2_modis",
                "rrs_2_seawifs",
                "rrs_4_meris",
                "rrs_4_modis",
                "rrs_4_seawifs"
        };

        SensorMerging sensorMerging = new SensorMerging(null, 0, "rrs_2", "rrs_4");
        assertArrayEquals(spatial, sensorMerging.getSpatialFeatureNames());
        assertArrayEquals(temporal, sensorMerging.getTemporalFeatureNames());
        assertArrayEquals(output0, sensorMerging.getOutputFeatureNames());

        sensorMerging = new SensorMerging(null, 1, "rrs_2", "rrs_4");
        assertArrayEquals(spatial, sensorMerging.getSpatialFeatureNames());
        assertArrayEquals(temporal, sensorMerging.getTemporalFeatureNames());
        assertArrayEquals(temporal, sensorMerging.getOutputFeatureNames());

        sensorMerging = new SensorMerging(null, 2, "rrs_2", "rrs_4");
        assertArrayEquals(spatial, sensorMerging.getSpatialFeatureNames());
        assertArrayEquals(temporal, sensorMerging.getTemporalFeatureNames());
        assertArrayEquals(output2, sensorMerging.getOutputFeatureNames());

    }
}
