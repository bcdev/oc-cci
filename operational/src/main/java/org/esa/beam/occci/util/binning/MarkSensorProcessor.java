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

package org.esa.beam.occci.util.binning;


import org.esa.beam.binning.CellProcessor;
import org.esa.beam.binning.CellProcessorConfig;
import org.esa.beam.binning.CellProcessorDescriptor;
import org.esa.beam.binning.VariableContext;
import org.esa.beam.binning.Vector;
import org.esa.beam.binning.WritableVector;
import org.esa.beam.framework.gpf.annotations.Parameter;

public class MarkSensorProcessor extends CellProcessor {

    private final int sensor;

    public MarkSensorProcessor(VariableContext varCtx, int sensor) {
        super(createOutputFeatureNames(varCtx));
        this.sensor = sensor;
    }

    @Override
    public void compute(Vector outputVector, WritableVector postVector) {
        for (int i = 0; i < outputVector.size(); i++) {
            postVector.set(i, outputVector.get(i));
        }
        postVector.set(outputVector.size(), sensor);
    }

    public static class Config extends CellProcessorConfig {
        @Parameter(description = "Adds an indication indication for the used sensor", notNull = true)
        private int sensor;

        public Config() {
            super(Descriptor.NAME);
        }

        public void setSensor(int sensor) {
            this.sensor = sensor;
        }
    }

    public static class Descriptor implements CellProcessorDescriptor {

        public static final String NAME = "MarkSensor";

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public CellProcessor createCellProcessor(VariableContext varCtx, CellProcessorConfig cellProcessorConfig) {
            Config config = (Config) cellProcessorConfig;
            return new MarkSensorProcessor(varCtx, config.sensor);
        }

        @Override
        public CellProcessorConfig createConfig() {
            return new Config();
        }
    }

    private static String[] createOutputFeatureNames(VariableContext varCtx) {
        int variableCount = varCtx.getVariableCount();

        String[] features = new String[variableCount + 1];
        for (int i = 0; i < variableCount; i++) {
            features[i] = varCtx.getVariableName(i);
        }
        features[variableCount] = "sensor";
        return features;
    }
}
