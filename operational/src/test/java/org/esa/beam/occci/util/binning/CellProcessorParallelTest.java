package org.esa.beam.occci.util.binning;

import org.esa.beam.binning.CellProcessor;
import org.esa.beam.binning.VariableContext;
import org.esa.beam.binning.Vector;
import org.esa.beam.binning.WritableVector;
import org.esa.beam.binning.cellprocessor.FeatureSelection;
import org.esa.beam.binning.support.VectorImpl;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class CellProcessorParallelTest {

    @Test
    public void testOne() throws Exception {
        VariableContext ctx = BinningUtils.createVariableContext("a", "b");
        FeatureSelection featureSelection = new FeatureSelection(ctx, "b");

        CellProcessor processor = new CellProcessorParallel(featureSelection);

        String[] outputFeatureNames = processor.getOutputFeatureNames();
        assertArrayEquals(new String[]{"b"}, outputFeatureNames);

        Vector in = new VectorImpl(new float[]{2f, 4f});
        WritableVector out = new VectorImpl(new float[1]);
        processor.compute(in, out);
        assertEquals(4f, out.get(0), 1e-6);
    }

    @Test
    public void testTwo() throws Exception {
        VariableContext ctx = BinningUtils.createVariableContext("a", "b", "c", "d");
        FeatureSelection selection1 = new FeatureSelection(ctx, "b", "d");
        FeatureSelection selection2 = new FeatureSelection(ctx, "c");

        CellProcessor processor = new CellProcessorParallel(selection1, selection2);

        String[] outputFeatureNames = processor.getOutputFeatureNames();
        assertArrayEquals(new String[]{"b", "d", "c"}, outputFeatureNames);

        Vector in = new VectorImpl(new float[]{2f, 4f, 6f, 8f});
        WritableVector out = new VectorImpl(new float[3]);
        processor.compute(in, out);
        assertEquals(4f, out.get(0), 1e-6);
        assertEquals(8f, out.get(1), 1e-6);
        assertEquals(6f, out.get(2), 1e-6);
    }

}
