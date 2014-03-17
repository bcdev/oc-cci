package org.esa.beam.occci.qaa.binning;

import org.esa.beam.binning.VariableContext;
import org.esa.beam.binning.support.VectorImpl;
import org.esa.beam.occci.qaa.QaaConstants;
import org.esa.beam.occci.util.binning.BinningUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class QaaCellProcessorTest {

    @Test
    public void testProcess_MODIS_a_pig_a_total() {
        final float[] rrs_in = {0.0020080009f, 0.0030860009f, 0.0030160008f, 0.0031800009f, 0.0030520008f, 0.0012980009f};
        final VectorImpl outVector = new VectorImpl(rrs_in);

        final QaaConfig config = createConfig();
        config.setAPigOutIndices(new int[]{0, 1, 2});
        config.setATotalOutIndices(new int[]{2, 3, 4});

        final VariableContext varCtx = createVariableContext();

        final QaaCellProcessor processor = new QaaCellProcessor(varCtx, config);
        final VectorImpl postVector = new VectorImpl(new float[processor.getOutputFeatureNames().length]);
        processor.compute(outVector, postVector);

        // out:     [a_pig_412, a_pig_443, a_pig_488, a_total_488, a_total_531, a_total_547]

        // a_pig:   [-0.035466618835926056, -0.04170411825180054, 0.03294333815574646]
        assertEquals(-0.035466618835926056, postVector.get(0), 1e-8);
        assertEquals(-0.04170411825180054, postVector.get(1), 1e-8);
        assertEquals(0.03294333815574646, postVector.get(2), 1e-8);

        // a_total: [0.3258124589920044, 0.18097913265228271, 0.1512765884399414, 0.12253236770629883, 0.12102054804563522]
        assertEquals(0.1512765884399414, postVector.get(3), 1e-8);
        assertEquals(0.12253236770629883, postVector.get(4), 1e-8);
        assertEquals(0.12102054804563522, postVector.get(5), 1e-8);
    }

    @Test
    public void testProcess_MODIS_a_ys_bb_spm() {
        final float[] rrs_in = {0.0020080009f, 0.0030860009f, 0.0030160008f, 0.0031800009f, 0.0030520008f, 0.0012980009f};
        final VectorImpl outVector = new VectorImpl(rrs_in);

        final QaaConfig config = createConfig();
        config.setAYsOutIndices(new int[]{0, 2});
        config.setBbSpmOutIndices(new int[]{0, 2, 3, 4});

        final VariableContext varCtx = createVariableContext();

        final QaaCellProcessor processor = new QaaCellProcessor(varCtx, config);
        final VectorImpl postVector = new VectorImpl(new float[processor.getOutputFeatureNames().length]);
        processor.compute(outVector, postVector);

        // out:     [[a_ys_412, a_ys_488, bb_spm_412 ,bb_spm_488 ,bb_spm_531 ,bb_spm_547]]
        // a_ys:    [0.35672852396965027, 0.21561411023139954, 0.10381654649972916]
        assertEquals(0.35672852396965027, postVector.get(0), 1e-8);
        assertEquals(0.10381654649972916, postVector.get(1), 1e-8);

        // bb_spm:  [0.01384813990443945, 0.011719915084540844, 0.0095791881904006, 0.008171066641807556, 0.00775269977748394]
        assertEquals(0.01384813990443945, postVector.get(2), 1e-8);
        assertEquals(0.0095791881904006, postVector.get(3), 1e-8);
        assertEquals(0.008171066641807556, postVector.get(4), 1e-8);
        assertEquals(0.00775269977748394, postVector.get(5), 1e-8);
    }

    @Test
    public void testCreateOutputFeatureNames_MERIS_no_bb_spm() {
        final QaaConfig config = new QaaConfig();
        config.setSensorName(QaaConstants.MERIS);
        config.setAPigOutIndices(new int[]{0, 1});
        config.setATotalOutIndices(new int[]{2, 3});
        config.setAYsOutIndices(new int[]{4, 5});
        config.setBbSpmOutIndices(new int[0]);

        final String[] featureNames = QaaCellProcessor.createOutputFeatureNames(config);
        assertNotNull(featureNames);

        final String[] expected = {"a_pig_413", "a_pig_443", "a_total_490", "a_total_510", "a_ys_560", "a_ys_665"};
        assertArrayEquals(expected, featureNames);
    }

    @Test
    public void testCreateOutputFeatureNames_MODIS_no_a_pig() {
        final QaaConfig config = new QaaConfig();
        config.setSensorName(QaaConstants.MODIS);
        config.setAPigOutIndices(new int[0]);
        config.setATotalOutIndices(new int[]{2, 3});
        config.setAYsOutIndices(new int[]{4});
        config.setBbSpmOutIndices(new int[]{3, 4, 5});

        final String[] featureNames = QaaCellProcessor.createOutputFeatureNames(config);
        assertNotNull(featureNames);

        final String[] expected = {"a_total_488", "a_total_531", "a_ys_547", "bb_spm_531", "bb_spm_547", "bb_spm_667"};
        assertArrayEquals(expected, featureNames);
    }

    @Test
    public void testCreateOutputFeatureNames_SEAWIFS_no_a_total() {
        final QaaConfig config = new QaaConfig();
        config.setSensorName(QaaConstants.SEAWIFS);
        config.setAPigOutIndices(new int[]{0, 1, 5});
        config.setATotalOutIndices(new int[0]);
        config.setAYsOutIndices(new int[]{4});
        config.setBbSpmOutIndices(new int[]{3, 4, 5});

        final String[] featureNames = QaaCellProcessor.createOutputFeatureNames(config);
        assertNotNull(featureNames);

        final String[] expected = {"a_pig_412", "a_pig_443", "a_pig_670", "a_ys_555", "bb_spm_510", "bb_spm_555", "bb_spm_670"};
        assertArrayEquals(expected, featureNames);
    }

    private VariableContext createVariableContext() {
        return BinningUtils.createVariableContext("ref_1",
                                                  "ref_2",
                                                  "ref_3",
                                                  "ref_4",
                                                  "ref_5",
                                                  "ref_6");
    }

    private QaaConfig createConfig() {
        final QaaConfig config = new QaaConfig();
        config.setSensorName(QaaConstants.MODIS);
        config.setBandNames(new String[]{"ref_1", "ref_2", "ref_3", "ref_4", "ref_5", "ref_6",});
        return config;
    }
}
