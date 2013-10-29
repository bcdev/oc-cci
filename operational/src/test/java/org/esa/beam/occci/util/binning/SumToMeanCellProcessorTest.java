package org.esa.beam.occci.util.binning;


import org.esa.beam.binning.VariableContext;
import org.esa.beam.binning.Vector;
import org.esa.beam.binning.WritableVector;
import org.esa.beam.binning.support.VectorImpl;
import org.junit.Test;

import static org.junit.Assert.*;

public class SumToMeanCellProcessorTest {

    @Test
    public void testGetOutputFeatureNames() throws Exception {
        VariableContext ctx = BinningUtils.createVariableContext("a_sum", "b_sum", "weights");
        SumToMeanCellProcessor processor = new SumToMeanCellProcessor(ctx, "weights", "a_sum", "b_sum");

        String[] outputFeatureNames = processor.getOutputFeatureNames();
        String[] expected = new String[]{"a", "b"};
        assertArrayEquals(expected, outputFeatureNames);
    }

    @Test
    public void testCompute() throws Exception {
        VariableContext ctx = BinningUtils.createVariableContext("a_sum", "b_sum", "weights");
        SumToMeanCellProcessor processor = new SumToMeanCellProcessor(ctx, "weights", "a_sum", "b_sum");

        Vector input = new VectorImpl(new float[]{20f, 40f, 2f});
        WritableVector output = new VectorImpl(new float[2]);
        processor.compute(input, output);

        assertEquals(10f, output.get(0), 1e-6);
        assertEquals(20f, output.get(1), 1e-6);
    }
}
