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

import org.esa.beam.binning.AbstractAggregator;
import org.esa.beam.binning.Aggregator;
import org.esa.beam.binning.AggregatorConfig;
import org.esa.beam.binning.AggregatorDescriptor;
import org.esa.beam.binning.BinContext;
import org.esa.beam.binning.Observation;
import org.esa.beam.binning.VariableContext;
import org.esa.beam.binning.Vector;
import org.esa.beam.binning.WritableVector;
import org.esa.beam.framework.gpf.annotations.Parameter;

import java.util.Arrays;

/**
 * Implements BIAS correction using the BIAS maps and sensor merging (in one step).
 */
public class SensorMerging extends AbstractAggregator {

    public static final String NAME = "SensorMerging";
    public static final String[] SENSORS = {"meris", "modis", "seawifs"};

    private final int mode;
    private final int numRrs;

    public SensorMerging(VariableContext varCtx, int mode, String... rrsFeatureNames) {
        super(NAME,
              createSpatialFeatureNames(rrsFeatureNames),
              createTemporalFeatureNames(rrsFeatureNames),
              createOutputFeatureNames(mode, rrsFeatureNames)
        );
        this.mode = mode;
        this.numRrs = rrsFeatureNames.length;
    }

    @Override
    public void initSpatial(BinContext ctx, WritableVector vector) {
        //nothing
    }

    @Override
    public void aggregateSpatial(BinContext ctx, Observation observationVector, WritableVector spatialVector) {
        for (int i = 0; i < observationVector.size(); i++) {
            spatialVector.set(i, observationVector.get(i));
        }
    }

    @Override
    public void completeSpatial(BinContext ctx, int numSpatialObs, WritableVector spatialVector) {
        //nothing
    }

    @Override
    public void initTemporal(BinContext ctx, WritableVector vector) {
        for (int i = 0; i < vector.size(); i++) {
            vector.set(i, Float.NaN);
        }
    }

    @Override
    public void aggregateTemporal(BinContext ctx, Vector spatialVector, int numSpatialObs, WritableVector temporalVector) {
        float sensorRaw = spatialVector.get(spatialVector.size() - 1);
        if (!Float.isNaN(sensorRaw)) {
            int sensor = (int) sensorRaw;
            for (int rrs = 0; rrs < numRrs; rrs++) {
                int temporalIndex = rrs * 6 + (sensor < 10 ? sensor : (sensor - 10 + 3));
                temporalVector.set(temporalIndex, spatialVector.get(rrs));
            }
        }
    }

    @Override
    public void completeTemporal(BinContext ctx, int numTemporalObs, WritableVector temporalVector) {
        switch (mode) {
            case 0:
                // TODO normal
                break;
            case 1:
                // nothing
                break;
            case 2:
                // TODO biasCorrection
                break;
        }
    }

    @Override
    public void computeOutput(Vector temporalVector, WritableVector outputVector) {
        switch (mode) {
            case 0:
                // TODO normal
                break;
            case 1:
                for (int i = 0; i < temporalVector.size(); i++) {
                    outputVector.set(i, temporalVector.get(i));
                }
                break;
            case 2:
                // TODO biasCorrection
                break;
        }
    }

    public static class Config extends AggregatorConfig {

        @Parameter(description = "Name rrs features", notNull = true, notEmpty = true)
        private String[] rrsFeatureNames;

        @Parameter(description = "Processing mode. 0:normal, 1:temporal, 2:biasCorrection",
                   valueSet = {"0,1,2"},
                   defaultValue = "0")
        public int mode;

        public Config() {
            super(NAME);
        }

        @Override
        public String[] getVarNames() {
            String[] varNames = Arrays.copyOf(rrsFeatureNames, rrsFeatureNames.length + 1);
            varNames[rrsFeatureNames.length] = "sensor";
            return varNames;
        }
    }

    public static class Descriptor implements AggregatorDescriptor {

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public AggregatorConfig createConfig() {
            return new Config();
        }

        @Override
        public Aggregator createAggregator(VariableContext varCtx, AggregatorConfig aggregatorConfig) {
            Config config = (Config) aggregatorConfig;
            return new SensorMerging(varCtx, config.mode, config.rrsFeatureNames);
        }
    }

    private static String[] createSpatialFeatureNames(String[] rrsFeatureNames) {
        String[] features = Arrays.copyOf(rrsFeatureNames, rrsFeatureNames.length + 1);
        features[rrsFeatureNames.length] = "sensor";
        return features;
    }

    private static String[] createTemporalFeatureNames(String[] rrsFeatureNames) {
        String[] features = new String[rrsFeatureNames.length * SENSORS.length * 2];
        int index = 0;
        for (String rrsFeatureName : rrsFeatureNames) {
            for (String sensor : SENSORS) {
                features[index++] = rrsFeatureName + "_" + sensor;
            }
            for (String sensor : SENSORS) {
                features[index++] = rrsFeatureName + "_" + sensor + "_biasmap";
            }
        }
        return features;
    }

    private static String[] createOutputFeatureNames(int mode, String[] rrsFeatureNames) {
        switch (mode) {
            case 0: {
                String[] features = Arrays.copyOf(rrsFeatureNames, rrsFeatureNames.length + 3);
                features[rrsFeatureNames.length] = "sensor_0";
                features[rrsFeatureNames.length + 1] = "sensor_1";
                features[rrsFeatureNames.length + 2] = "sensor_2";
                return features;
            }
            case 1:
                return createTemporalFeatureNames(rrsFeatureNames);
            case 2: {
                String[] features = new String[rrsFeatureNames.length * SENSORS.length];
                int index = 0;
                for (String rrsFeatureName : rrsFeatureNames) {
                    for (String sensor : SENSORS) {
                        features[index++] = rrsFeatureName + "_" + sensor;
                    }
                }
                return features;
            }
        }
        throw new IllegalArgumentException("Unsupported mode:" + mode);
    }
}
