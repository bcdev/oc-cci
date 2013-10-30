package org.esa.beam.occci.bandshift.binning;

import org.esa.beam.binning.VariableContext;
import org.esa.beam.binning.support.VectorImpl;
import org.esa.beam.occci.util.binning.BinningUtils;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class BandShiftCellProcessorTest {

    private static final String[] RRS_BAND_NAMES = new String[]{"band_1",
            "band_2",
            "band_3",
            "band_4",
            "band_5",
            "band_6"};
    private static final String[] IOP_BAND_NAMES = new String[]{"iop_1", "iop_2", "iop_3"};

    @Test
    public void testThrowsExceptionOnMissingBands() throws IOException {
        String[] bandNames = BinningUtils.combine(RRS_BAND_NAMES, "does_not_exist");
        final VariableContext ctx = BinningUtils.createVariableContext(RRS_BAND_NAMES);
     try {
            new BandShiftCellProcessor(ctx, "MERIS", bandNames, bandNames, new int[0]);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testCompute_MODIS() throws IOException {
        // inputData consists of six reflectances followed by the three qaa values
        final float[] inputData = new float[]{0.00711314f, 0.00559714f, 0.00459386f, 0.00249029f, 0.00189400f, 0.000241144f, 0.0192148f, 0.00571175f, 0.0138207f};
        final String[] outputFeatureNames = {"Rrs_413", "Rrs_510", "Rrs_490", "Rrs_560", "Rrs_555", "Rrs_665", "Rrs_670"};
        final int[] outCenterWaveLengths = new int[]{413, 510, 490, 560, 555, 665, 670};

        String[] allBands = BinningUtils.combine(RRS_BAND_NAMES, IOP_BAND_NAMES);
        final VariableContext ctx = BinningUtils.createVariableContext(allBands);
        final BandShiftCellProcessor postProcessor = new BandShiftCellProcessor(ctx, "MODISA", RRS_BAND_NAMES, IOP_BAND_NAMES, outCenterWaveLengths);

        final VectorImpl postVector = new VectorImpl(new float[outputFeatureNames.length]);
        final VectorImpl outVector = new VectorImpl(inputData);
        postProcessor.compute(outVector, postVector);

        assertEquals(0.007067796774, postVector.get(0), 1e-6);
        assertEquals(0.003140728688, postVector.get(1), 1e-6);
        assertEquals(0.004552158527, postVector.get(2), 1e-6);
        assertEquals(0.001587149804, postVector.get(3), 1e-6);
        assertEquals(0.001667401055, postVector.get(4), 1e-6);
        assertEquals(0.000246264943, postVector.get(5), 1e-6);
        assertEquals(0.000236338106, postVector.get(6), 1e-6);
    }

    @Test
    public void testIsCorrected() {
        assertTrue(BandShiftCellProcessor.isCorrected(new double[2]));
        assertFalse(BandShiftCellProcessor.isCorrected(new double[0]));
    }

    @Test
    public void testCreateOutputFeaturesNames() {
        int[] outputCenterWavelengths = {412, 488, 531, 547, 555, 667, 670};
        final String[] merisFeatures = BandShiftCellProcessor.createOutputFeatureNames(outputCenterWavelengths);
        assertEquals(7, merisFeatures.length);

        assertEquals("Rrs_412", merisFeatures[0]);
        assertEquals("Rrs_488", merisFeatures[1]);
        assertEquals("Rrs_531", merisFeatures[2]);
        assertEquals("Rrs_547", merisFeatures[3]);
        assertEquals("Rrs_555", merisFeatures[4]);
        assertEquals("Rrs_667", merisFeatures[5]);
        assertEquals("Rrs_670", merisFeatures[6]);
    }

    @Test
    public void testCreateOutputFeaturesNames_justTwo() {
        int[] outputCenterWavelengths = {667, 670};
        final String[] merisFeatures = BandShiftCellProcessor.createOutputFeatureNames(outputCenterWavelengths);
        assertEquals(2, merisFeatures.length);

        assertEquals("Rrs_667", merisFeatures[0]);
        assertEquals("Rrs_670", merisFeatures[1]);
    }

    @Test
    public void testCreateOutputFeaturesNames_noBands() {
        int[] outputCenterWavelengths = new int[0];
        final String[] merisFeatures = BandShiftCellProcessor.createOutputFeatureNames(outputCenterWavelengths);
        assertEquals(0, merisFeatures.length);
    }
}
