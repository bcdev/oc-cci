package org.esa.beam.occci.util.binning;

import org.esa.beam.binning.VariableContext;
import org.esa.beam.binning.Vector;
import org.esa.beam.binning.WritableVector;
import org.esa.beam.binning.cellprocessor.FeatureSelection;
import static org.junit.Assert.*;

import org.esa.beam.binning.support.VectorImpl;
import org.junit.Test;

public class CellProcessorSequenceTest {

    @Test
    public void testOne() throws Exception {
        VariableContext ctx = BinningUtils.createVariableContext("a", "b");
        FeatureSelection featureSelection = new FeatureSelection(ctx, "b");

        CellProcessorSequence cellProcessorSequence = new CellProcessorSequence(featureSelection);

        String[] outputFeatureNames = cellProcessorSequence.getOutputFeatureNames();
        assertArrayEquals(new String[]{"b"}, outputFeatureNames);

        Vector in = new VectorImpl(new float[]{2f, 4f});
        WritableVector out = new VectorImpl(new float[1]);
        cellProcessorSequence.compute(in, out);
        assertEquals(4f, out.get(0), 1e-6);
    }

    @Test
    public void testTwo() throws Exception {
        VariableContext ctx1 = BinningUtils.createVariableContext("a", "b", "c", "d");
        FeatureSelection selection1 = new FeatureSelection(ctx1, "b", "d");

        VariableContext ctx2 = BinningUtils.createVariableContext(selection1.getOutputFeatureNames());
        FeatureSelection selection2 = new FeatureSelection(ctx2, "d");

        CellProcessorSequence cellProcessorSequence = new CellProcessorSequence(selection1, selection2);

        String[] outputFeatureNames = cellProcessorSequence.getOutputFeatureNames();
        assertArrayEquals(new String[]{"d"}, outputFeatureNames);

        Vector in = new VectorImpl(new float[]{2f, 4f, 6f, 8f});
        WritableVector out = new VectorImpl(new float[1]);
        cellProcessorSequence.compute(in, out);
        assertEquals(8f, out.get(0), 1e-6);
    }

}
