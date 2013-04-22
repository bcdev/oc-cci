package org.esa.beam.occci.bandshift.binning;

import org.esa.beam.binning.PostProcessorConfig;
import org.esa.beam.framework.gpf.annotations.Parameter;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

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
    public void testSensorNameAnnotated() throws NoSuchFieldException {
        final Field sensorNameField = BandShiftConfig.class.getDeclaredField("sensorName");
        final Parameter sensorNameParameter = sensorNameField.getAnnotation(Parameter.class);
        assertTrue(sensorNameParameter.notEmpty());
        assertTrue(sensorNameParameter.notNull());

        final String[] valueSet = sensorNameParameter.valueSet();
        assertEquals(3, valueSet.length);
        assertEquals("MERIS", valueSet[0]);
        assertEquals("MODISA", valueSet[1]);
        assertEquals("SEAWIFS", valueSet[2]);
    }

    @Test
    public void testSetGetSensorName() {
        final String sensorName = "MODISA";

        config.setSensorName(sensorName);
        assertEquals(sensorName, config.getSensorName());
    }

//    @Test
//    public void testBandNamesAnnotated() throws NoSuchFieldException {
//        final Field bandNamesField = BandShiftConfig.class.getDeclaredField("bandNames");
//    }
}
