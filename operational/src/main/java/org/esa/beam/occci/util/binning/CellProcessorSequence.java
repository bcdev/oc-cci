package org.esa.beam.occci.util.binning;

import org.esa.beam.binning.CellProcessor;
import org.esa.beam.binning.Vector;
import org.esa.beam.binning.WritableVector;
import org.esa.beam.binning.support.VectorImpl;

/**
 * Computes all steps in a sequence. Feeding the output as input into the next one.
 */
public class CellProcessorSequence extends CellProcessor {

    private final CellProcessor[] cellProcessors;
    private final WritableVector[] outputVectors;

    public CellProcessorSequence(CellProcessor... cellProcessors) {
        super(cellProcessors[cellProcessors.length - 1].getOutputFeatureNames());
        this.cellProcessors = cellProcessors;
        outputVectors = new WritableVector[cellProcessors.length - 1];
        for (int i = 0; i < outputVectors.length; i++) {
            CellProcessor cellProcessor = cellProcessors[i];
            outputVectors[i] = new VectorImpl(new float[cellProcessor.getOutputFeatureNames().length]);
        }
    }

    @Override
    public void compute(Vector outputVector, WritableVector postVector) {
        for (int i = 0; i < cellProcessors.length; i++) {

            final WritableVector output;
            if (i == cellProcessors.length - 1) {
                // last processor write to portVector
                output = postVector;
            } else {
                output = outputVectors[i];
            }

            final Vector input;
            if (i == 0) {
                // first processor read from outputVector
                input = outputVector;
            } else {
                input = outputVectors[i - 1];
            }

            cellProcessors[i].compute(input, output);
        }
    }
}
