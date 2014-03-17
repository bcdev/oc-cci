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

    private static final String[] BANDSHIFTED_FEATURES = new String[]{"Rrs_412", "Rrs_443", "Rrs_490", "Rrs_510", "Rrs_555", "Rrs_670", "sensor"};

    @Test
    public void testMeris() throws Exception {
        BandShiftChainDescriptor.Config config = new BandShiftChainDescriptor.Config();
        config.setSensorName(QaaConstants.MERIS);
        BandShiftChainDescriptor descriptor = new BandShiftChainDescriptor();
        String[] merisBinnedDailyFeatures = {
                "Rrs412_mean", "Rrs443_mean", "Rrs490_mean", "Rrs510_mean", "Rrs560_mean", "Rrs665_mean",
                "aph_443_mean", "adg_443_mean", "bbp_443_mean"};
        VariableContext varCtx = BinningUtils.createVariableContext(merisBinnedDailyFeatures);
        CellProcessor processor = descriptor.createCellProcessor(varCtx, config);

        assertArrayEquals(BANDSHIFTED_FEATURES, processor.getOutputFeatureNames());


        Vector input = new VectorImpl(new float[]{0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.1f, 0.2f, 0.3f});
        WritableVector output = new VectorImpl(new float[BANDSHIFTED_FEATURES.length]);
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
        BandShiftChainDescriptor.Config config = new BandShiftChainDescriptor.Config();
        config.setSensorName(QaaConstants.MODIS);
        BandShiftChainDescriptor descriptor = new BandShiftChainDescriptor();
        String[] modisL2DailyBinnedFeatures = {"Rrs_412_sum", "Rrs_443_sum", "Rrs_488_sum", "Rrs_531_sum", "Rrs_547_sum", "Rrs_667_sum", "weights"};
        VariableContext varCtx = BinningUtils.createVariableContext(modisL2DailyBinnedFeatures);
        CellProcessor processor = descriptor.createCellProcessor(varCtx, config);

        assertArrayEquals(BANDSHIFTED_FEATURES, processor.getOutputFeatureNames());
        final float[] rrs_sum = {0.012084f*5, 0.0089211f*5, 0.0062153f*5, 0.0021173f*5, 0.0014871f*5, 0.0010871f*5, 5.f};
        Vector input = new VectorImpl(rrs_sum);
        WritableVector output = new VectorImpl(new float[BANDSHIFTED_FEATURES.length]);
        processor.compute(input, output);

        assertEquals(0.012083999812602997, output.get(0), 1e-6f);
        assertEquals(0.008921099826693535, output.get(1), 1e-6f);
        assertEquals(0.006076225079596043, output.get(2), 1e-6f);
        assertEquals(0.003202288644388318, output.get(3), 1e-6f);
        assertEquals(0.001263132900930941, output.get(4), 1e-6f);
        assertEquals(0.0010569796431809664, output.get(5), 1e-6f);
        assertEquals(1.0f, output.get(6), 1e-6f);
    }

    @Test
    public void testSeawifs() throws Exception {
        BandShiftChainDescriptor.Config config = new BandShiftChainDescriptor.Config();
        config.setSensorName(QaaConstants.SEAWIFS);
        BandShiftChainDescriptor descriptor = new BandShiftChainDescriptor();
        String[] seawifsL2DailyBinnedFeatures = {"Rrs_412_sum", "Rrs_443_sum", "Rrs_490_sum", "Rrs_510_sum", "Rrs_555_sum", "Rrs_670_sum", "weights"};
        //String[] seawifsL2DailyBinnedFeatures = {"Rrs_412_sum", "Rrs_443_sum", "Rrs_488_sum", "Rrs_531_sum", "Rrs_547_sum", "Rrs_667_sum", "weights"};
        VariableContext varCtx = BinningUtils.createVariableContext(seawifsL2DailyBinnedFeatures);
        CellProcessor processor = descriptor.createCellProcessor(varCtx, config);

        assertArrayEquals(BANDSHIFTED_FEATURES, processor.getOutputFeatureNames());
        final float[] rrs_sum = {0.012084f*4f, 0.0089211f*4f, 0.0062153f*4f, 0.0021173f*4f, 0.0014871f*4f, 9.943E-5f*4f, 4.f};
        Vector input = new VectorImpl(rrs_sum);
        WritableVector output = new VectorImpl(new float[BANDSHIFTED_FEATURES.length]);
        processor.compute(input, output);

        assertEquals(0.012083999812602997, output.get(0), 1e-6f);
        assertEquals(0.008921099826693535, output.get(1), 1e-6f);
        assertEquals(0.006215299945324659, output.get(2), 1e-6f);
        assertEquals(0.0021172999404370785, output.get(3), 1e-6f);
        assertEquals(0.0014871000312268734, output.get(4), 1e-6f);
        assertEquals(9.942999895429239E-5, output.get(5), 1e-6f);
        assertEquals(2.0f, output.get(6), 1e-6f);
    }
}
