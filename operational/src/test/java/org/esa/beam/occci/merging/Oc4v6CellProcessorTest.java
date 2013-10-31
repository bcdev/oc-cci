package org.esa.beam.occci.merging;

import org.esa.beam.binning.VariableContext;
import org.esa.beam.binning.Vector;
import org.esa.beam.binning.WritableVector;
import org.esa.beam.binning.support.VectorImpl;
import org.esa.beam.occci.util.binning.BinningUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Oc4v6CellProcessorTest {

    @Test
    public void testCompute() throws Exception {
        String[] spectrumBandNames = {"Rrs_412", "Rrs_443", "Rrs_490", "Rrs_510", "Rrs_555", "Rrs_670"};
        VariableContext varCtx = BinningUtils.createVariableContext(spectrumBandNames);
        Oc4v6CellProcessor processor = new Oc4v6CellProcessor(varCtx, Oc4v6CellProcessor.BAND_NAMES);

        Vector in = new VectorImpl(new float[]{0.017414005f, 0.013600607f, 0.007675953f, 0.00390938f, 0.0016726361f, 2.9419627E-4f});
        WritableVector chl = new VectorImpl(new float[1]);
        processor.compute(in, chl);

        assertEquals(0.034884255f, chl.get(0), 1e-6); // value taken from PML processed product
    }
}
