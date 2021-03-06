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
        config.setRrsBandNames("r1","r2","r3","r4","r5","r6");
        config.setIopBandNames("i1","i2","i3");

        VariableContext variableContext = BinningUtils.createVariableContext("r1","r2","r3","r4","r5","r6","i1","i2","i3");
        final CellProcessor postProcessor = descriptor.createCellProcessor(variableContext, config);
        assertNotNull(postProcessor);
        assertTrue(postProcessor instanceof BandShiftCellProcessor);
    }
}
