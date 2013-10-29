package org.esa.beam.occci.bandshift.binning;


import org.esa.beam.binning.CellProcessor;
import org.esa.beam.binning.VariableContext;
import org.esa.beam.binning.Vector;
import org.esa.beam.binning.WritableVector;
import org.esa.beam.binning.support.VectorImpl;
import org.esa.beam.occci.qaa.QaaConstants;
import org.esa.beam.occci.util.binning.BinningUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class BandShiftChainDescriptorTest {

    @Test
    public void testMeris() throws Exception {
        BandShiftChainDescriptor.Config config = new BandShiftChainDescriptor.Config();
        config.setSensorName(QaaConstants.MERIS);
        BandShiftChainDescriptor descriptor = new BandShiftChainDescriptor();
        String[] merisBinnedFeatures = {
                "Rrs412_mean", "Rrs443_mean", "Rrs490_mean", "Rrs510_mean", "Rrs560_mean", "Rrs665_mean",
                "a_pig_443_mean", "a_ys_443_mean", "bb_spm_443_mean"};
        VariableContext varCtx = BinningUtils.createVariableContext(merisBinnedFeatures);
        CellProcessor processor = descriptor.createCellProcessor(varCtx, config);

        String[] merisBandshiftedFeatures = {"Rrs_412", "Rrs_443", "Rrs_490", "Rrs_510", "Rrs_555", "Rrs_670", "sensor"};
        assertArrayEquals(merisBandshiftedFeatures, processor.getOutputFeatureNames());


        Vector input = new VectorImpl(new float[]{0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.1f, 0.2f, 0.3f});
        WritableVector output = new VectorImpl(new float[merisBandshiftedFeatures.length]);
        processor.compute(input, output);

        assertEquals(0.0991176962852478, output.get(0), 1e-6f);
        assertEquals(0.20000000298023224, output.get(1), 1e-6f);
        assertEquals(0.30000001192092896, output.get(2), 1e-6f);
        assertEquals(0.4000000059604645, output.get(3), 1e-6f);
        assertEquals(0.49425825476646423, output.get(4), 1e-6f);
        assertEquals(0.5753246545791626, output.get(5), 1e-6f);
        assertEquals(0.0f, output.get(6), 1e-6f);
    }

    @Test
    public void testModis() throws Exception {
        // TODO
        assertTrue(true);

    }

    @Test
    public void testSeawifs() throws Exception {
        // TODO
        assertTrue(true);
    }
}
