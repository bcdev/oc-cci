package org.esa.beam.occci.util.binning;

import org.esa.beam.binning.CellProcessor;
import org.esa.beam.binning.Vector;
import org.esa.beam.binning.WritableVector;
import org.esa.beam.binning.support.VectorImpl;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Computes all steps in a parallel (virtually).
 * All processors get the same input and write to parts of the common output.
 */
public class CellProcessorParallel extends CellProcessor {

    private final CellProcessor[] cellProcessors;
    private final int[] offsets;
    private final int[] sizes;
    private final int outputFeatureCount;

    public CellProcessorParallel(CellProcessor... cellProcessors) {
        super(getOutputFeatureNames(cellProcessors));
        this.cellProcessors = cellProcessors;
        offsets = new int[cellProcessors.length];
        sizes = new int[cellProcessors.length];
        for (int i = 0; i < cellProcessors.length; i++) {
            CellProcessor cellProcessor = cellProcessors[i];
            sizes[i] = cellProcessor.getOutputFeatureNames().length;
            if (i == 0) {
                offsets[i] = 0;
            } else {
                offsets[i] = sizes[i - 1];
            }
        }
        outputFeatureCount = getOutputFeatureNames().length;
    }

    static String[] getOutputFeatureNames(CellProcessor... cellProcessors) {
        final ArrayList<String> featureNameList = new ArrayList<String>();
        for (CellProcessor cellProcessor : cellProcessors) {
            Collections.addAll(featureNameList, cellProcessor.getOutputFeatureNames());
        }
        return featureNameList.toArray(new String[featureNameList.size()]);
    }

    @Override
    public void compute(Vector input, WritableVector output) {
        if (output instanceof VectorImpl) {
            VectorImpl outputVector = (VectorImpl) output;
            for (int i = 0; i < cellProcessors.length; i++) {
                int offset = offsets[i];
                int size = sizes[i];
                outputVector.setOffsetAndSize(offset, size);
                cellProcessors[i].compute(input, output);
            }
            outputVector.setOffsetAndSize(0, outputFeatureCount);
        } else {
            throw new IllegalArgumentException("output vector must be of type 'VectorImpl'");
        }
    }
}