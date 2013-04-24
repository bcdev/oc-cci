package org.esa.beam.occci.qaa.binning;


import org.esa.beam.binning.PostProcessorConfig;
import org.esa.beam.binning.PostProcessorDescriptor;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class QaaDescriptorTest {

    private QaaDescriptor descriptor;

    @Before
    public void setUp() {
        descriptor = new QaaDescriptor();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testInterfaceImplemented() {
        assertTrue(descriptor instanceof PostProcessorDescriptor);
    }

    @Test
    public void testGetName() {
        assertEquals("QAA", descriptor.getName());
    }

    @Test
    public void testCreatePostProcessorConfig() {
        final PostProcessorConfig config = descriptor.createPostProcessorConfig();
        assertNotNull(config);
        assertNotNull(config);
        assertTrue(config instanceof QaaConfig);
        assertEquals("QAA", config.getPostProcessorName());
    }


    @Test
    public void testCreateOutputFeatureNames_MERIS_no_bb_spm() {
        final QaaConfig config = new QaaConfig();
        config.setSensorName("MERIS");
        config.setA_pig_out_indices(new int[]{0, 1});
        config.setA_total_out_indices(new int[]{2, 3});
        config.setA_ys_out_indices(new int[]{4, 5});
        config.setBb_spm_out_indices(new int[0]);

        final String[] featureNames = QaaDescriptor.createOutputFeatureNames(config);
        assertNotNull(featureNames);

        final String[] expected = {"a_pig_413", "a_pig_443", "a_total_490", "a_total_510", "a_ys_560", "a_ys_665"};
        assertArrayEquals(expected, featureNames);
    }

    @Test
    public void testCreateOutputFeatureNames_MODIS_no_a_pig() {
        final QaaConfig config = new QaaConfig();
        config.setSensorName("MODIS");
        config.setA_pig_out_indices(new int[0]);
        config.setA_total_out_indices(new int[]{2, 3});
        config.setA_ys_out_indices(new int[]{4});
        config.setBb_spm_out_indices(new int[]{3, 4, 5});

        final String[] featureNames = QaaDescriptor.createOutputFeatureNames(config);
        assertNotNull(featureNames);

        final String[] expected = {"a_total_488", "a_total_531", "a_ys_547", "bb_spm_531", "bb_spm_547", "bb_spm_667"};
        assertArrayEquals(expected, featureNames);
    }

    @Test
    public void testCreateOutputFeatureNames_SEAWIFS_no_a_total() {
        final QaaConfig config = new QaaConfig();
        config.setSensorName("SEAWIFS");
        config.setA_pig_out_indices(new int[]{0, 1, 5});
        config.setA_total_out_indices(new int[0]);
        config.setA_ys_out_indices(new int[]{4});
        config.setBb_spm_out_indices(new int[]{3, 4, 5});

        final String[] featureNames = QaaDescriptor.createOutputFeatureNames(config);
        assertNotNull(featureNames);

        final String[] expected = {"a_pig_412", "a_pig_443", "a_pig_667", "a_ys_555", "bb_spm_510", "bb_spm_555", "bb_spm_667"};
        assertArrayEquals(expected, featureNames);
    }

    @Test
    public void testGetWavelengthInt() {
        double[] wavelengths = new double[]{12.9, 13.5, 14.3};

        assertEquals(13, QaaDescriptor.getWavelengthInt(wavelengths, 0));
        assertEquals(14, QaaDescriptor.getWavelengthInt(wavelengths, 1));
        assertEquals(14, QaaDescriptor.getWavelengthInt(wavelengths, 2));
    }

    @Test
    public void testGetWavelengthInt_errorCases() {
        double[] wavelengths = new double[]{13.9, 14.5, 15.3};

        try {
            QaaDescriptor.getWavelengthInt(wavelengths, -1);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }

        try {
            QaaDescriptor.getWavelengthInt(wavelengths, 3);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }
    }
}
