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

public class ProductValidationMain {
    private static final String[] RRS_NAMES = {"Rrs_412", "Rrs_443", "Rrs_490", "Rrs_510", "Rrs_555", "Rrs_670"};


//    private static final String[] COMPARISON_NAMES = {"chlor_a", "chlor_a_bias_uncertainty", "chlor_a_rms_uncertainty"};

//    private static final String[] COMPARISON_NAMES = {"atot_412", "adg_412", "aph_412", "bbp_412"};

//    private static final String[] COMPARISON_NAMES = {
//            "adg_412", "adg_412_bias_uncertainty", "adg_412_rms_uncertainty",
//            "aph_412", "aph_412_bias_uncertainty", "aph_412_rms_uncertainty",
//            "atot_412",
//            "bbp_412"
//    };

    private static final String[] COMPARISON_NAMES = {
                "Rrs_412", "Rrs_412_bias_uncertainty", "Rrs_412_rms_uncertainty",
                "Rrs_555", "Rrs_555_bias_uncertainty", "Rrs_555_rms_uncertainty"
        };


//    private static final String[] COMPARISON_NAMES = {
//            "water_class1", "water_class2", "water_class3", "water_class4",
//            "water_class5", "water_class6", "water_class7", "water_class8", "water_class9"
//    };


    public static void main(String[] args) throws IOException, InvalidRangeException {
        NetcdfFile netcdfFile = NetcdfFile.open(args[0]);

        Variable[] rrsVars = new Variable[RRS_NAMES.length];
        float[] rrsFillValues = new float[RRS_NAMES.length];
        for (int i = 0; i < RRS_NAMES.length; i++) {
            rrsVars[i] = netcdfFile.findVariable(RRS_NAMES[i]);
            rrsFillValues[i] = rrsVars[i].findAttribute("_FillValue").getNumericValue().floatValue();
        }

        Variable[] compVars = new Variable[COMPARISON_NAMES.length];
        float[] compFillValues = new float[COMPARISON_NAMES.length];
        for (int i = 0; i < COMPARISON_NAMES.length; i++) {
            compVars[i] = netcdfFile.findVariable(COMPARISON_NAMES[i]);
            compFillValues[i] = compVars[i].findAttribute("_FillValue").getNumericValue().floatValue();
        }

        int numElems = rrsVars[0].getDimension(0).getLength();
        int chunkSize = rrsVars[0].findAttribute("_ChunkSize").getNumericValue().intValue();
        System.out.println("numElems = " + numElems);
        System.out.println("chunkSize = " + chunkSize);

        float[][] rrsData = new float[RRS_NAMES.length][];
        float[][] compData = new float[COMPARISON_NAMES.length][];

        final float[] inputValues = new float[6 + 3];
        Vector inputVector = new VectorImpl(inputValues);
        String[] bands = concat(RRS_NAMES, "sensor_0", "sensor_1", "sensor_2");
        VariableContext varCtx = BinningUtils.createVariableContext(bands);
        CellProcessor processor = PostMergingDescriptor.create(varCtx);
        String[] featureNames = processor.getOutputFeatureNames();
        System.out.println("featureNames = " + Arrays.toString(featureNames));
        float[] outputValues = new float[featureNames.length];
        VectorImpl outputVector = new VectorImpl(outputValues);

        VariableContext variableContext = BinningUtils.createVariableContext(featureNames);
        int[] compIndices = BinningUtils.getBandIndices(variableContext, COMPARISON_NAMES);
        System.out.println("compareIndex = " + Arrays.toString(compIndices));

        int countPixel = 0;
        int countPixelWithData = 0;
        int[] countPixelSameData = new int[COMPARISON_NAMES.length];

        long t0 = System.currentTimeMillis();
        int lastProgress = -99;
        for (int offset = 0; offset < numElems; offset += chunkSize) {
            int currentProgress = (int) (((double) offset) / numElems * 100);
            if (currentProgress != lastProgress) {
                System.out.println("progress = " + currentProgress);
                lastProgress = currentProgress;
            }
            int actualChunkSize = (numElems - offset) > chunkSize ? chunkSize : (numElems - offset);
            int[] origin = {offset};
            int[] shape = {actualChunkSize};

            for (int i = 0; i < rrsVars.length; i++) {
                rrsData[i] = (float[]) rrsVars[i].read(origin, shape).get1DJavaArray(float.class);
            }
            for (int i = 0; i < compVars.length; i++) {
                compData[i] = (float[]) compVars[i].read(origin, shape).get1DJavaArray(float.class);
            }

            for (int i = 0; i < actualChunkSize; i++) {
                countPixel++;
                boolean hasData = true;
                for (int rrsIndex = 0; rrsIndex < rrsVars.length; rrsIndex++) {
                    float v = rrsData[rrsIndex][i];
                    if (v == rrsFillValues[rrsIndex]) {
                        hasData = false;
                        break;
                    }
                    inputValues[rrsIndex] = v;
                }
                if (hasData) {
                    countPixelWithData++;
                    processor.compute(inputVector, outputVector);

                    for (int j = 0; j < compVars.length; j++) {
                        float expected = compData[j][i];
                        if (expected == compFillValues[j]) {
                            expected = Float.NaN;
                        }
                        final float actual = outputValues[compIndices[j]];
                        if (same(expected, actual)) {
                            countPixelSameData[j]++;
                        } else {
//                            System.out.println("band     = " + COMPARISON_NAMES[j]);
//                            System.out.println("expected = " + expected);
//                            System.out.println("actual   = " + actual);
//                            System.out.println("inputs   = " + Arrays.toString(inputValues));
//                            System.out.println("outputs  = " + Arrays.toString(outputValues));
//                            System.out.println();
                        }
                    }
                }
            }
        }
        long t1 = System.currentTimeMillis();
        long dt = t1 - t0;
        System.out.println("dt = " + dt);
        System.out.println();
        System.out.println("comparing: " + Arrays.toString(COMPARISON_NAMES));
        printNameLabelValue("countPixel", null, countPixel);
        printNameLabelValue("countPixelWithData", null, countPixelWithData);
        for (int i = 0; i < COMPARISON_NAMES.length; i++) {
            printNameLabelValue("countPixelSame", COMPARISON_NAMES[i], countPixelSameData[i]);
            printNameLabelValue("countPixelDiffer", COMPARISON_NAMES[i], (countPixelWithData - countPixelSameData[i]));
        }
    }

    private static boolean same(float v1, float v2) {
        return Float.isNaN(v1) && Float.isNaN(v2) || Math.abs(v1 - v1) <= 1e-4;
    }

    private static void printNameLabelValue(String label, String name, int value) {
        if (name != null) {
            System.out.printf("%-20s[%-30s] = %,10d%n", label, name, value);
        } else {
            System.out.printf("%-20s%-32s = %,10d%n", label, " ", value);
        }
    }
}
