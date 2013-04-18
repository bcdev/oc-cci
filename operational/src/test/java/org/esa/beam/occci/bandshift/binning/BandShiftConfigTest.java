package org.esa.beam.occci.bandshift.binning;

import org.esa.beam.binning.PostProcessorConfig;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BandShiftConfigTest {

    private static final String PROCESSOR_NAME = "stupid name";

    private BandShiftConfig config;

    @Before
    public void setUp() {
        config = new BandShiftConfig(PROCESSOR_NAME);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testAbstractClassImplemented() {
        assertTrue(config instanceof PostProcessorConfig);
    }

    @Test
    public void testParameterConstruction() {
        assertEquals(PROCESSOR_NAME, config.getPostProcessorName());
    }

    @Test
    public void testSetGetSensorName() {
        final String sensorName_1 = "thermometer";
        final String sensorName_2 = "thermostat";

        config.setSensorName(sensorName_1);
        assertEquals(sensorName_1, config.getSensorName());

        config.setSensorName(sensorName_2);
        assertEquals(sensorName_2, config.getSensorName());
    }
}
