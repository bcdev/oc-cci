package org.esa.beam.occci.util.binning;

import org.esa.beam.binning.VariableContext;
import org.esa.beam.binning.WritableVector;
import org.esa.beam.binning.support.VariableContextImpl;

public class BinningUtils {

    public static int[] getBandIndices(VariableContext context, String...bandNames) {
        final int[] bandIndices = new int[bandNames.length];
        for (int i = 0; i < bandNames.length; i++) {
            bandIndices[i] = context.getVariableIndex(bandNames[i]);
            if (bandIndices[i] < 0) {
                throw new IllegalArgumentException("Configured input band is not available: " + bandNames[i]);
            }
        }
        return bandIndices;
    }

    // package access for testing only tb 2013-04-24
    public static void setToInvalid(WritableVector postVector) {
        for (int i = 0; i < postVector.size(); i++) {
            postVector.set(i, Float.NaN);
        }
    }

    public static VariableContext createVariableContext(String... outputFeatureNames) {
        VariableContextImpl variableContext = new VariableContextImpl();
        for (String outputFeatureName : outputFeatureNames) {
            variableContext.defineVariable(outputFeatureName);
        }
        return variableContext;
    }
}
