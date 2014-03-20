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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class UncertaintyCellProcessor extends CellProcessor {

    static final String[] WVLS = new String[]{"412", "443", "490", "510", "555", "670"};
    static final String BIAS_SUFFIX = "bias_uncertainty";
    static final String RMS_SUFFIX = "rms_uncertainty";

    // number of optical water types
    private static final int NUM_OWTS = 9;
    // number of bands used in uncertainty calculations
    // (coccolith isn't used for this, so it is NOWTS - 1 at the moment)
    private static final int UNCERTAINTY_CLASSES = 8;

    // the minimum classification threshold to calculate uncertainties */
    private static final double MIN_CLASSIFICATION_TOTAL = 0.01;

    private final int[] waterIndices;
    private final float[] waterValues;
    private final UncertaintyAuxdata[] uncertaintyAuxdatas;

    public UncertaintyCellProcessor(VariableContext varCtx, UncertaintyAuxdata[] auxdata) {
        super(createOutputFeatureNames(auxdata));
        uncertaintyAuxdatas = auxdata;

        waterIndices = BinningUtils.getBandIndices(varCtx, OWTCellProcessor.createWaterClassFeatureNames());
        waterValues = new float[UNCERTAINTY_CLASSES];
    }

    @Override
    public void compute(Vector inputVector, WritableVector outputVector) {
        for (int i = 0; i < waterValues.length; i++) {
            waterValues[i] = inputVector.get(waterIndices[i]);
        }

        final double classificationSum = computeClassificationSum(waterValues);
        for (int i = 0; i < uncertaintyAuxdatas.length; i++) {
            double[] uncertaintyTable = uncertaintyAuxdatas[i].uncertaintyTable;
            double uncertainty = computeUncertainty(uncertaintyTable, classificationSum, waterValues);
            outputVector.set(i, (float) uncertainty);
        }
    }

    /**
     * the classification sum is used to normalise the classifications
     */
    static double computeClassificationSum(float[] waterValues) {
        double classificationSum = 0;
        boolean foundFill = false;
        for (int classIndex = 0; classIndex < UNCERTAINTY_CLASSES; ++classIndex) {
            if (Float.isNaN(waterValues[classIndex])) {
                foundFill = true;
                break;
            }
            classificationSum += waterValues[classIndex];
        }
        if (classificationSum < MIN_CLASSIFICATION_TOTAL && !foundFill) {
            foundFill = true;
        }
        if (foundFill) {
            // we have no classification data for this pixel so no uncertainties can  be calculated
            return Float.NaN;
        } else {
            return classificationSum;
        }
    }

    /**
     * calculate our uncertainty by performing a weighted sum of the
     * normalised classification values multiplied by the corresponding
     * uncertainty value
     */
    static double computeUncertainty(double[] uncertaintyTable, double classificationSum, float[] waterValues) {
        double uncertainty = 0;
        // move down through individual classes for this band (in both the input matrix and uncertainty table)
        for (int classIndex = 0; classIndex < UNCERTAINTY_CLASSES; ++classIndex) {
            uncertainty += (waterValues[classIndex] / classificationSum) * uncertaintyTable[classIndex];
        }
        return uncertainty;
    }

    static String[] createOutputFeatureNames(UncertaintyAuxdata[] auxdata) {
        String[] featureNames = new String[auxdata.length];
        int count = 0;
        for (UncertaintyAuxdata ucAux : auxdata) {
            featureNames[count++] = ucAux.varName + "_" + ucAux.uncertaintySuffix;
        }
        return featureNames;
    }

    static UncertaintyAuxdata[] loadAuxdata() {
        List<UncertaintyAuxdata> list = new ArrayList<UncertaintyAuxdata>();
        try {
            list.addAll(loadAuxdata("cci_chla_bias.dat", BIAS_SUFFIX, "chlor_a"));
            list.addAll(loadAuxdata("cci_chla_rms.dat", RMS_SUFFIX, "chlor_a"));
            list.addAll(loadAuxdata("cci_Rrs_bias.dat", BIAS_SUFFIX, "Rrs", WVLS));
            list.addAll(loadAuxdata("cci_Rrs_rms.dat", RMS_SUFFIX, "Rrs", WVLS));

            list.addAll(loadAuxdata("cci_iop_adg_bias.dat", BIAS_SUFFIX, "adg", WVLS));
            list.addAll(loadAuxdata("cci_iop_adg_rms.dat", RMS_SUFFIX, "adg", WVLS));




//        UncertaintyCellProcessor.class.getResourceAsStream("rrs/cci_Rrs_rms.dat");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load Uncertainty Auxdata", e);
        }
        return list.toArray(new UncertaintyAuxdata[list.size()]);
    }

    static List<UncertaintyAuxdata> loadAuxdata(String resource, String uncertaintySuffix, String varNamePrefix, String[] wvls) throws IOException {
        String[] varNames = new String[wvls.length];
        for (int i = 0; i < varNames.length; i++) {
            varNames[i] = varNamePrefix + "_" + wvls[i];
        }
        return loadAuxdata(resource, uncertaintySuffix, varNames);
    }

    static List<UncertaintyAuxdata> loadAuxdata(String resource, String uncertaintySuffix, String...varName) throws IOException {
        InputStream stream = UncertaintyCellProcessor.class.getResourceAsStream(resource);
        try {
            return loadAuxdataTransposed(new InputStreamReader(stream), uncertaintySuffix, varName).subList(0, 1);
        } finally {
            stream.close();
        }
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
    static List<UncertaintyAuxdata> loadAuxdata(Reader reader, String uncertaintySuffix, String... varNames) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(reader);
        List<UncertaintyAuxdata> auxdata = new ArrayList<UncertaintyAuxdata>(varNames.length);
        for (String varName : varNames) {
            String line = bufferedReader.readLine();
            String[] words = line.split("\\s+");
            double[] values = new double[words.length];
            for (int i = 0; i < words.length; i++) {
                values[i] = Double.parseDouble(words[i]);
            }
            auxdata.add(new UncertaintyAuxdata(values, varName, uncertaintySuffix));
        }
        return auxdata;
    }

    static List<UncertaintyAuxdata> loadAuxdataTransposed(Reader reader, String uncertaintySuffix, String... varNames) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(reader);
        double[] dataRaw = new double[varNames.length * UNCERTAINTY_CLASSES];
        double[][] dataTransposed = new double[varNames.length][UNCERTAINTY_CLASSES];
        String line;
        int count = 0;
        while((line = bufferedReader.readLine()) != null) {
            String[] words = line.split("\\s+");
            for (String word : words) {
                dataRaw[count++] = Double.parseDouble(word);
            }
        }
        count = 0;
        for (int unClass = 0; unClass < UNCERTAINTY_CLASSES; unClass++) {
           for (int varIdx = 0; varIdx < varNames.length; ++varIdx) {
               dataTransposed[varIdx][unClass] = dataRaw[count++];
           }
        }
        List<UncertaintyAuxdata> auxdata = new ArrayList<UncertaintyAuxdata>(varNames.length);
        for (int varIdx = 0; varIdx < varNames.length; varIdx++) {
            auxdata.add(new UncertaintyAuxdata(dataTransposed[varIdx], varNames[varIdx], uncertaintySuffix));
        }
        return auxdata;
    }

    static class UncertaintyAuxdata {
        private final double[] uncertaintyTable;
        private final String varName;
        private final String uncertaintySuffix;

        UncertaintyAuxdata(double[] uncertaintyTable, String varName, String uncertaintySuffix) {
            this.uncertaintyTable = uncertaintyTable;
            this.varName = varName;
            this.uncertaintySuffix = uncertaintySuffix;
//            System.out.println("varName = " + varName);
//            System.out.println("uncertaintySuffix = " + uncertaintySuffix);
//            System.out.println("uncertaintyTable = " + Arrays.toString(uncertaintyTable));
        }
    }
}
