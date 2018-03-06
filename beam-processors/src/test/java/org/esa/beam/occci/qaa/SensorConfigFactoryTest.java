package org.esa.beam.occci.qaa;


import org.esa.beam.framework.gpf.OperatorException;
import org.junit.Test;

import static org.junit.Assert.*;

public class SensorConfigFactoryTest {

    @Test
    public void testGet() {
        SensorConfig sensorConfig = SensorConfigFactory.get(QaaConstants.MERIS);
        assertNotNull(sensorConfig);
        assertTrue(sensorConfig instanceof MerisConfig);

        sensorConfig = SensorConfigFactory.get(QaaConstants.MODIS);
        assertNotNull(sensorConfig);
        assertTrue(sensorConfig instanceof ModisConfig);

        sensorConfig = SensorConfigFactory.get(QaaConstants.SEAWIFS);
        assertNotNull(sensorConfig);
        assertTrue(sensorConfig instanceof SeaWifsConfig);
    }

    @Test
    public void testGet_unsupportedSensor() {
        try {
            SensorConfigFactory.get("Bratwurst");
            fail("OperatorException expected");
        } catch (OperatorException expected) {
        }
    }

    @Test
    public void testGetWithWavelengths() {
        final double[] wavelengths = new double[]{12, 13, 14, 15, 15, 17};

        final SensorConfig sensorConfig = SensorConfigFactory.get(wavelengths);
        assertNotNull(sensorConfig);
        assertTrue(sensorConfig instanceof InSituConfig);

        final InSituConfig inSituConfig = (InSituConfig) sensorConfig;
        assertArrayEquals(wavelengths, inSituConfig.getWavelengths(), 1e-8);
    }

    @Test
    public void testGetWithWavelengths_invalidInput() {
        try {
            SensorConfigFactory.get(new double[5]);
            fail("OperatorException expected");
        } catch (OperatorException expected) {
        }

        try {
            SensorConfigFactory.get(new double[7]);
            fail("OperatorException expected");
        } catch (OperatorException expected) {
        }
    }
}
