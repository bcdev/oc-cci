package org.esa.beam.occci.merging;

import org.esa.beam.binning.CellProcessor;
import org.esa.beam.binning.VariableContext;
import org.esa.beam.binning.Vector;
import org.esa.beam.binning.support.VectorImpl;
import org.esa.beam.occci.util.binning.BinningUtils;
import ucar.ma2.Array;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

import java.io.IOException;
import java.util.Arrays;

import static org.esa.beam.occci.util.binning.BinningUtils.concat;

public class ProductValidationMain {
    private static final String[] RRS_NAMES = {"Rrs_412", "Rrs_443", "Rrs_490", "Rrs_510", "Rrs_555", "Rrs_670"};

    public static void main(String[] args) throws IOException, InvalidRangeException {
        NetcdfFile netcdfFile = NetcdfFile.open(args[0]);
        Variable[] rrsVars = new Variable[RRS_NAMES.length];
        float[] fillValues = new float[RRS_NAMES.length];
        for (int i = 0; i < RRS_NAMES.length; i++) {
            rrsVars[i] = netcdfFile.findVariable(RRS_NAMES[i]);
            fillValues[i] = rrsVars[i].findAttribute("_FillValue").getNumericValue().floatValue();
            System.out.println(fillValues[i]);
        }
        Variable comp = netcdfFile.findVariable("bbp_412");

        int chunkSize = rrsVars[0].findAttribute("_ChunkSize").getNumericValue().intValue();
        float[][] rrsData = new float[RRS_NAMES.length][];
        float[] compareData;
        int numElems = rrsVars[0].getDimension(0).getLength();

        System.out.println("numElems = " + numElems);
        System.out.println("chunkSize = " + chunkSize);

        final float[] inputValues = new float[6+3];
        Vector inputVector = new VectorImpl(inputValues);
        String[] bands = concat(RRS_NAMES, "sensor_0", "sensor_1", "sensor_2");
        VariableContext varCtx = BinningUtils.createVariableContext(bands);
        CellProcessor processor = PostMergingDescriptor.create(varCtx);
        String[] featureNames = processor.getOutputFeatureNames();
        System.out.println("featureNames = " + Arrays.toString(featureNames));
        float[] outputValues = new float[featureNames.length];
        VectorImpl outputVector = new VectorImpl(outputValues);

        VariableContext variableContext = BinningUtils.createVariableContext(featureNames);
        int compareIndex = BinningUtils.getBandIndices(variableContext, comp.getFullName())[0];
        System.out.println("compareIndex = " + compareIndex);

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

            for (int rrsIndex = 0; rrsIndex < rrsVars.length; rrsIndex++) {
                rrsData[rrsIndex] = (float[]) rrsVars[rrsIndex].read(origin, shape).get1DJavaArray(float.class);
            }
            compareData = (float[]) comp.read(origin, shape).get1DJavaArray(float.class);

            for (int i = 0; i < actualChunkSize; i++) {
                boolean hasData = true;
                for (int rrsIndex = 0; rrsIndex < rrsVars.length; rrsIndex++) {
                    float v = rrsData[rrsIndex][i];
                    if (v == fillValues[rrsIndex]) {
                        hasData = false;
                        break;
                    }
                    inputValues[rrsIndex] = v;
                }
                if (hasData) {
                    processor.compute(inputVector, outputVector);
                    float diff = Math.abs(compareData[i] - outputValues[compareIndex]);
                    if (diff > 1e-4) {
                        System.out.println("reference = " + compareData[i]);
                        System.out.println("computed  = " + outputValues[compareIndex]);
                        System.out.println("diff      = " + diff);
                        System.out.println("inputValues = " + Arrays.toString(inputValues));
                        System.out.println("outputValues = " + Arrays.toString(outputValues));
                        System.out.println();
                    }
                    //System.out.println("offset = " + (offset + i));
                    //System.out.println("inputValues = " + Arrays.toString(inputValues));
                    //System.out.println("outputValues = " + Arrays.toString(outputValues));
                }
            }
        }
        long t1 = System.currentTimeMillis();
        long dt = t1 - t0;
        System.out.println("dt = " + dt);
    }
}
