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
import org.esa.beam.binning.support.VectorImpl;
import org.esa.beam.occci.util.binning.BinningUtils;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

import java.io.IOException;
import java.util.Arrays;

import static org.esa.beam.occci.util.binning.BinningUtils.concat;

public class ProcessPixelMain {
    private static final String[] RRS_NAMES = {"Rrs_412", "Rrs_443", "Rrs_490", "Rrs_510", "Rrs_555", "Rrs_670"};

    private static final float[] INPUT = new float[]{
            0.006186951f, 0.0054932158f, 0.004319099f,
            0.0028702824f, 0.0014102075f, 0.00029511406f,
            2f, 4f, 6f};

    public static void main(String[] args) throws IOException, InvalidRangeException {
        Vector inputVector = new VectorImpl(INPUT);
        String[] bands = concat(RRS_NAMES, "sensor_0", "sensor_1", "sensor_2");
        VariableContext varCtx = BinningUtils.createVariableContext(bands);
        CellProcessor processor = PostMergingDescriptor.create(varCtx);
        String[] featureNames = processor.getOutputFeatureNames();
        System.out.println("featureNames = " + Arrays.toString(featureNames));
        float[] outputValues = new float[featureNames.length];
        VectorImpl outputVector = new VectorImpl(outputValues);

        processor.compute(inputVector, outputVector);
        String[] sortedNames = Arrays.copyOf(featureNames, featureNames.length);
        Arrays.sort(sortedNames);
        for (String name : sortedNames) {
            System.out.printf("%-30s = %15.10f%n", name, outputValues[idxFor(name, featureNames)]);
        }

    }

    private static int idxFor(String bandName, String[] bandNames) {
        for (int i = 0; i < bandNames.length; i++) {
            if (bandName.equals(bandNames[i])) {
                return i;
            }
        }
        return -1;
    }
}
