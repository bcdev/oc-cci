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

public class IdentityCellProcessor extends CellProcessor {

    public static final String NAME = "Identity";

    private final int[] bandIndices;

    public IdentityCellProcessor(VariableContext varCtx, String... bandNames) {
        super(createOutputFeatureNames(varCtx, bandNames));
        if (bandNames == null || bandNames.length == 0) {
            bandIndices = new int[varCtx.getVariableCount()];
            for (int i = 0; i < bandIndices.length; i++) {
                bandIndices[i] = varCtx.getVariableIndex(varCtx.getVariableName(i));
            }
        } else {
            bandIndices = BinningUtils.getBandIndices(varCtx, bandNames);
        }
    }

    @Override
    public void compute(Vector outputVector, WritableVector postVector) {
        for (int i = 0; i < bandIndices.length; i++) {
            postVector.set(i, outputVector.get(bandIndices[i]));
        }
    }

    public static class Config extends CellProcessorConfig {

        @Parameter()
        private String[] bandNames;

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
            IdentityCellProcessor.Config config = (Config) cellProcessorConfig;
            return new IdentityCellProcessor(varCtx, config.bandNames);
        }

        @Override
        public CellProcessorConfig createConfig() {
            return new Config();
        }
    }

    private static String[] createOutputFeatureNames(VariableContext varCtx, String[] bandNames) {
        if (bandNames == null || bandNames.length == 0) {
            String[] features = new String[varCtx.getVariableCount()];
            for (int i = 0; i < features.length; i++) {
                features[i] = varCtx.getVariableName(i);
            }
            return features;
        } else {
            return bandNames;
        }
    }
}
