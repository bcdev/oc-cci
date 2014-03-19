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

package org.esa.beam.occci.merging;

import org.esa.beam.binning.CellProcessor;
import org.esa.beam.binning.VariableContext;
import org.esa.beam.binning.Vector;
import org.esa.beam.binning.WritableVector;
import org.esa.beam.occci.util.binning.BinningUtils;

public class UncertaintyCellProcessor extends CellProcessor {

    // number of optical water types
    private static final int NUM_OWTS = 9;
    // number of bands used in uncertainty calculations
    // (coccolith isn't used for this, so it is NOWTS - 1 at the moment)
    private static final int UNCERTAINTY_CLASSES = 8;

    // the minimum classification threshold to calculate uncertainties */
    private static final double MIN_CLASSIFICATION_TOTAL = 0.01;

    private static final String BIAS_SUFFIX = "bias_uncertainty";
    private static final String RMS_SUFFIX = "rms_uncertainty";

    private final int[] waterIndices;
    private final int[] inputIndices;
    private final float[] waterValues;
    private final UncertaintyAlgorithm rmsAlgo;
    private final UncertaintyAlgorithm biasAlgo;

    public UncertaintyCellProcessor(VariableContext varCtx, String biasFile, String rmsFile, String[] varNames) {
        super(createOutputFeatureNames(varNames));

        waterIndices = BinningUtils.getBandIndices(varCtx, OWTCellProcessor.createWaterClassFeatureNames());
        waterValues = new float[waterIndices.length];

        inputIndices = BinningUtils.getBandIndices(varCtx, varNames);

//        rmsAlgo = new UncertaintyAlgorithm(rmsFile, UNCERTAINTY_CLASSES, varNames.length);
//        biasAlgo = new UncertaintyAlgorithm(biasFile, UNCERTAINTY_CLASSES, varNames.length);

        double[][] rmsTable = new double[][]{
                {0.205403}, {0.235605}, {0.267787}, {0.315107}, {0.305332}, {0.405685}, {0.326042}, {0.334740}
        };
        double[][] biasTable = new double[][]{
                {0.051788}, {-0.037328}, {-0.037751}, {-0.014557}, {0.007397}, {-0.097694}, {0.010787}, {-0.122734}
        };

        rmsAlgo = new UncertaintyAlgorithm(rmsTable);
        biasAlgo = new UncertaintyAlgorithm(biasTable);

    }

    @Override
    public void compute(Vector inputVector, WritableVector outputVector) {
        for (int i = 0; i < waterIndices.length; i++) {
            waterValues[i] = inputVector.get(waterIndices[i]);
        }

        int count = 0;
        for (int i = 0; i < inputIndices.length; i++) {
            float inValue = inputVector.get(inputIndices[i]);
            outputVector.set(count++, biasAlgo.compute(i, inValue, waterValues));
            outputVector.set(count++, rmsAlgo.compute(i, inValue, waterValues));
        }
    }

    static String[] createOutputFeatureNames(String[] varNames) {
        String[] featureNames = new String[varNames.length * 2];
        int count = 0;
        for (String varName : varNames) {
            featureNames[count++] = varName + "_" + BIAS_SUFFIX;
            featureNames[count++] = varName + "_" + RMS_SUFFIX;
        }
        return featureNames;
    }

    private static class UncertaintyAlgorithm {

        private final double[][] uncertaintyTable;

        public UncertaintyAlgorithm(double[][] uncertaintyTable) {
            this.uncertaintyTable = uncertaintyTable;
        }

        /**
         * read in the uncertainty table, which is structured with rows for each variable
         * and columns for each class, e.g.
         * rrs412_class1 rrs412_class2 rrs412_class3 rrs412_class4
         * rrs443_class1 rrs443_class2 rrs443_class3 rrs443_class4
         * rrs510_class1 rrs510_class2 rrs510_class3 rrs510_class4
         * <p/>
         * the output variable names *must* be in the same order as the table above, or you'll get garbage
         */
        static double[][] read(String file, int numClasses, int numVars) {
            return null;
        }

        public float compute(int varIndex, float inValue, float[] waterValues) {
            double classificationSum = 0;
            boolean found_fill = false;
            for (int classIndex = 0; classIndex < UNCERTAINTY_CLASSES; ++classIndex) {
                if (Float.isNaN(waterValues[classIndex])) {
                    found_fill = true;
                    break;
                }
                        /* the classification sum is used to normalise the classifications */
                classificationSum += waterValues[classIndex];
            }
            if (classificationSum < MIN_CLASSIFICATION_TOTAL && !found_fill) {
                found_fill = true;
            }
            if (found_fill) {
                /* we have no classification data for this pixel so no uncertainties can  be calculated */
                return Float.NaN;
            } else {
                /* calculate our uncertainty by performing a weighted sum of the
                   normalised classification values multiplied by the corresponding
                   uncertainty value */
                double uncertainty = 0;
                for (int classIndex = 0; classIndex < UNCERTAINTY_CLASSES; ++classIndex) {
                    /* move down through individual classes for this band (in both
                       the input matrix and uncertainty table) */
                    uncertainty +=
                            (waterValues[classIndex] / classificationSum) *
                            uncertaintyTable[classIndex][varIndex];
                }
                return (float) uncertainty;
            }
        }
    }
}
