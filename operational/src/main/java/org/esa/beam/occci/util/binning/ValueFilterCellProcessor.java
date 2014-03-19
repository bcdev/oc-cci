/*
 * Copyright (C) 2014 Brockmann Consult GmbH (info@brockmann-consult.de)
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

public class ValueFilterCellProcessor extends CellProcessor {

    public static final String NAME = "ValueFilter";

    private final int[] bandIndices;
    private final double[] minValues;
    private final double[] maxValues;

    public ValueFilterCellProcessor(VariableContext varCtx, String[] bandNames, double[] minValues, double[] maxValues) {
        super(bandNames);
        this.minValues = minValues;
        this.maxValues = maxValues;
        bandIndices = BinningUtils.getBandIndices(varCtx, bandNames);
    }

    @Override
    public void compute(Vector inputVector, WritableVector outputVector) {
        for (int i = 0; i < bandIndices.length; i++) {
            float value = inputVector.get(bandIndices[i]);
            if (value < minValues[i] || value > maxValues[i]) {
                value = Float.NaN;
            }
            outputVector.set(i, value);
        }
    }

    public static class Config extends CellProcessorConfig {

        @Parameter
        private String[] bandNames;
        @Parameter
        private double[] minValues;
        @Parameter
        private double[] maxValues;

        public Config() {
            super(NAME);
        }
    }

    public static class Descriptor implements CellProcessorDescriptor {

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public CellProcessor createCellProcessor(VariableContext varCtx, CellProcessorConfig cellProcessorConfig) {
            ValueFilterCellProcessor.Config config = (Config) cellProcessorConfig;
            return new ValueFilterCellProcessor(varCtx, config.bandNames, config.minValues, config.maxValues);
        }

        @Override
        public CellProcessorConfig createConfig() {
            return new Config();
        }
    }
}
