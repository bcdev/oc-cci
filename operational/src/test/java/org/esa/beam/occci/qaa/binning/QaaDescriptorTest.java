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

        final String[] featureNames = descriptor.createOutputFeatureNames(config);
        assertNotNull(featureNames);

        final String[] expected = {"a_pig_413", "a_pig_443", "a_total_490", "a_total_510", "a_ys_560", "a_ys_665"};
        // @todo 1 tb/tb continue here
        //assertArrayEquals(expected, featureNames);
    }
}
