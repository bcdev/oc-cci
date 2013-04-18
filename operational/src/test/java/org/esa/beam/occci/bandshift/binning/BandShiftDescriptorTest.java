package org.esa.beam.occci.bandshift.binning;

import org.esa.beam.binning.PostProcessorDescriptor;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
}
