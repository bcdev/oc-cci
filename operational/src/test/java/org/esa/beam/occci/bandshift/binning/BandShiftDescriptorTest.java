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
    public void testCreateOutputFeaturesNames_MERIS() {
        final String[] merisFeatures = BandShiftDescriptor.createOutputFeatureNames("MERIS");
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
    public void testCreateOutputFeaturesNames_MODIS() {
        final String[] modisFeatures = BandShiftDescriptor.createOutputFeatureNames("MODISA");
        assertEquals(7, modisFeatures.length);

        assertEquals("Rrs_413", modisFeatures[0]);
        assertEquals("Rrs_510", modisFeatures[1]);
        assertEquals("Rrs_490", modisFeatures[2]);
        assertEquals("Rrs_560", modisFeatures[3]);
        assertEquals("Rrs_555", modisFeatures[4]);
        assertEquals("Rrs_665", modisFeatures[5]);
        assertEquals("Rrs_670", modisFeatures[6]);
    }

    @Test
    public void testCreateOutputFeaturesNames_SEAWIFS() {
        final String[] seawifsFeatures = BandShiftDescriptor.createOutputFeatureNames("SEAWIFS");
        assertEquals(7, seawifsFeatures.length);

        assertEquals("Rrs_413", seawifsFeatures[0]);
        assertEquals("Rrs_488", seawifsFeatures[1]);
        assertEquals("Rrs_531", seawifsFeatures[2]);
        assertEquals("Rrs_547", seawifsFeatures[3]);
        assertEquals("Rrs_560", seawifsFeatures[4]);
        assertEquals("Rrs_665", seawifsFeatures[5]);
        assertEquals("Rrs_670", seawifsFeatures[6]);
    }

    @Test
    public void testCreateOutputFeaturesNames_invalidSensor() {
        try {
            BandShiftDescriptor.createOutputFeatureNames("strange_sensor");
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }
    }
}
