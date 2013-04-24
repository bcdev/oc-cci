package org.esa.beam.occci.qaa;


import org.esa.beam.framework.gpf.OperatorException;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.fail;

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
}
