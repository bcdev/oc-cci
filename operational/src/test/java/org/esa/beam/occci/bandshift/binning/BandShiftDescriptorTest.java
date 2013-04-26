package org.esa.beam.occci.bandshift.binning;

import org.esa.beam.binning.PostProcessor;
import org.esa.beam.binning.PostProcessorConfig;
import org.esa.beam.binning.PostProcessorDescriptor;
import org.esa.beam.binning.support.VariableContextImpl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BandShiftDescriptorTest {

    private BandShiftDescriptor descriptor;

    @Before
    public void setUp() {
        descriptor = new BandShiftDescriptor();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testInterfaceImplemented() {
        assertTrue(descriptor instanceof PostProcessorDescriptor);
    }

    @Test
    public void testGetName() {
        assertEquals("BandShifting", descriptor.getName());
    }

    @Test
    public void testCreatePostProcessorConfig() {
        final PostProcessorConfig config = descriptor.createPostProcessorConfig();
        assertNotNull(config);
        assertTrue(config instanceof BandShiftConfig);
        assertEquals("BandShifting", config.getPostProcessorName());
    }

    @Test
    public void testCreatePostProcessor() {
        final BandShiftConfig config = new BandShiftConfig("BandShifting");
        config.setSensorName("MODISA");

        final PostProcessor postProcessor = descriptor.createPostProcessor(new VariableContextImpl(), config);
        assertNotNull(postProcessor);
        assertTrue(postProcessor instanceof BandShiftPostProcessor);
    }

    @Test
    public void testCreateOutputFeaturesNames() {
        final BandShiftConfig config = new BandShiftConfig("bla");
        config.setOutputCenterWavelengths(new int[]{412, 488, 531, 547, 555, 667, 670});
        final String[] merisFeatures = BandShiftDescriptor.createOutputFeatureNames(config);
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
        final BandShiftConfig config = new BandShiftConfig("schwafel");
        config.setOutputCenterWavelengths(new int[]{667, 670});
        final String[] merisFeatures = BandShiftDescriptor.createOutputFeatureNames(config);
        assertEquals(2, merisFeatures.length);

        assertEquals("Rrs_667", merisFeatures[0]);
        assertEquals("Rrs_670", merisFeatures[1]);
    }

    @Test
    public void testCreateOutputFeaturesNames_noBands() {
        final BandShiftConfig config = new BandShiftConfig("schwafel");
        config.setOutputCenterWavelengths(new int[0]);
        final String[] merisFeatures = BandShiftDescriptor.createOutputFeatureNames(config);
        assertEquals(0, merisFeatures.length);
    }
}
