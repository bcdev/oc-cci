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
    private final WritableVector[] temporaryVectors;

    public CellProcessorSequence(CellProcessor... cellProcessors) {
        super(cellProcessors[cellProcessors.length - 1].getOutputFeatureNames());
        this.cellProcessors = cellProcessors;
        temporaryVectors = new WritableVector[cellProcessors.length - 1];
        for (int i = 0; i < temporaryVectors.length; i++) {
            CellProcessor cellProcessor = cellProcessors[i];
            temporaryVectors[i] = new VectorImpl(new float[cellProcessor.getOutputFeatureNames().length]);
        }
    }

    @Override
    public void compute(Vector inputVector, WritableVector outputVector) {
        for (int i = 0; i < cellProcessors.length; i++) {

            final WritableVector output;
            if (i == cellProcessors.length - 1) {
                // last processor write to outputVector
                output = outputVector;
            } else {
                output = temporaryVectors[i];
            }

            final Vector input;
            if (i == 0) {
                // first processor read from inputVector
                input = inputVector;
            } else {
                input = temporaryVectors[i - 1];
            }
            cellProcessors[i].compute(input, output);
        }
    }
}
