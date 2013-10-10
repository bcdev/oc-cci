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

public class IdentityCellProcessor extends CellProcessor {

    protected IdentityCellProcessor(VariableContext varCtx) {
        super(createOutputFeatureNames(varCtx));
    }

    @Override
    public void compute(Vector outputVector, WritableVector postVector) {
        for (int i = 0; i < outputVector.size(); i++) {
            postVector.set(i, outputVector.get(i));
        }
    }

    public static class Config extends CellProcessorConfig {
        public Config() {
            super(Descriptor.NAME);
        }
    }

    public static class Descriptor implements CellProcessorDescriptor {

        public static final String NAME = "Identity";

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public CellProcessor createCellProcessor(VariableContext varCtx, CellProcessorConfig cellProcessorConfig) {
            return new IdentityCellProcessor(varCtx);
        }

        @Override
        public CellProcessorConfig createConfig() {
            return new Config();
        }
    }

    private static String[] createOutputFeatureNames(VariableContext varCtx) {
        String[] features = new String[varCtx.getVariableCount()];
        for (int i = 0; i < features.length; i++) {
            features[i] = varCtx.getVariableName(i);
        }
        return features;
    }
}
