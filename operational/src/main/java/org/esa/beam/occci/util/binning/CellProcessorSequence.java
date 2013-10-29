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
    public void compute(Vector input, WritableVector output) {
        for (int i = 0; i < cellProcessors.length; i++) {

            final WritableVector out;
            if (i == cellProcessors.length - 1) {
                // last processor write to portVector
                out = output;
            } else {
                out = outputVectors[i];
            }

            final Vector in;
            if (i == 0) {
                // first processor read from input
                in = input;
            } else {
                in = outputVectors[i - 1];
            }

            cellProcessors[i].compute(in, out);
        }
    }
}
