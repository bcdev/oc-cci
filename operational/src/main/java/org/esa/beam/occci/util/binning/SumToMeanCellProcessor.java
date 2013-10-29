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

public class SumToMeanCellProcessor extends CellProcessor {

    private final int weightIndex;
    private final int[] sumIndices;

    public SumToMeanCellProcessor(VariableContext varCtx, String weightFeatureName, String...sumFeatureNames) {
        super(createOutputFeatureNames(sumFeatureNames));

        weightIndex = BinningUtils.getBandIndices(varCtx, weightFeatureName)[0];
        sumIndices = BinningUtils.getBandIndices(varCtx, sumFeatureNames);
    }

    @Override
    public void compute(Vector input, WritableVector output) {
        float weight = input.get(weightIndex);
        for (int i = 0; i < sumIndices.length; i++) {
            final float sum = input.get(sumIndices[i]);
            final float mean = sum / weight;
            output.set(i, mean);
        }
    }

    public static class Config extends CellProcessorConfig {
        @Parameter(description = "Name of the sum feature", notNull = true, notEmpty = true)
        private String[] sumFeatureNames;

        @Parameter(description = "Name of the weight feature",
                   defaultValue = "weights")
        private String weightFeatureName;

        public Config() {
            super(Descriptor.NAME);
        }
    }

    public static class Descriptor implements CellProcessorDescriptor {

        public static final String NAME = "SumToMean";

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public CellProcessor createCellProcessor(VariableContext varCtx, CellProcessorConfig cellProcessorConfig) {
            Config config = (Config) cellProcessorConfig;
            return new SumToMeanCellProcessor(varCtx, config.weightFeatureName, config.sumFeatureNames);
        }

        @Override
        public CellProcessorConfig createConfig() {
            return new Config();
        }
    }

    private static String[] createOutputFeatureNames(String[] sumFeatureNames) {
        String[] features = new String[sumFeatureNames.length];
        for (int i = 0; i < sumFeatureNames.length; i++) {
            features[i] = sumFeatureNames[i].replace("_sum", "");
        }
        return features;
    }
}
