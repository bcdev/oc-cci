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

    public static String[] concat(String[] a, String... b){
       int length = a.length + b.length;
        String[] result = new String[length];
       System.arraycopy(a, 0, result, 0, a.length);
       System.arraycopy(b, 0, result, a.length, b.length);
       return result;
   }

}
