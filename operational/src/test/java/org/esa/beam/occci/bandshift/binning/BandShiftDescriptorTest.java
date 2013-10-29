package org.esa.beam.occci.bandshift.binning;

import org.esa.beam.binning.CellProcessor;
import org.esa.beam.binning.CellProcessorConfig;
import org.esa.beam.binning.CellProcessorDescriptor;
import org.esa.beam.binning.VariableContext;
import org.esa.beam.occci.util.binning.BinningUtils;
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
        assertTrue(descriptor instanceof CellProcessorDescriptor);
    }

    @Test
    public void testGetName() {
        assertEquals("BandShifting", descriptor.getName());
    }

    @Test
    public void testCreateConfig() {
        final CellProcessorConfig config = descriptor.createConfig();
        assertNotNull(config);
        assertTrue(config instanceof BandShiftConfig);
        assertEquals("BandShifting", config.getName());
    }

    @Test
    public void testCreateCellProcessor() {
        final BandShiftConfig config = new BandShiftConfig("BandShifting");
        config.setSensorName("MODISA");

        VariableContext variableContext = BinningUtils.createVariableContext();
        final CellProcessor postProcessor = descriptor.createCellProcessor(variableContext, config);
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
