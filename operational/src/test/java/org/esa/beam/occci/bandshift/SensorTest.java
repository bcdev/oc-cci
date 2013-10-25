package org.esa.beam.occci.bandshift;

import org.junit.Test;

import static org.junit.Assert.*;

public class SensorTest {

    @Test
    public void testLambdaInterface_MERIS() {
        final double[] expected = {413., 443., 490., 510., 560., 665.};
        assertArrayEquals(expected, Sensor.MERIS.getLambdaInterface(), 1e-8);
    }

    @Test
    public void testLambdaInterface_MODIS() {
        final double[] expected = {412., 443., 488., 531., 547., 667.};
        assertArrayEquals(expected, Sensor.MODISA.getLambdaInterface(), 1e-8);
    }

    @Test
    public void testLambdaInterface_SEAWIFS() {
        final double[] expected = {412., 443., 490., 510., 555., 667.};
        assertArrayEquals(expected, Sensor.SEAWIFS.getLambdaInterface(), 1e-8);
    }

    @Test
    public void testByName() {
        final Sensor meris = Sensor.byName("MERIS");
        assertNotNull(meris);
        assertEquals(413.0, meris.getLambdaI()[0], 1e-8);
        assertEquals(490.0, meris.getLambdaI()[1], 1e-8);

        final Sensor modisa = Sensor.byName("MODISA");
        assertNotNull(modisa);
        assertEquals(412.0, modisa.getLambdaI()[0], 1e-8);
        assertEquals(488.0, modisa.getLambdaI()[1], 1e-8);

        final Sensor seawifs = Sensor.byName("SEAWIFS");
        assertNotNull(seawifs);
        assertEquals(412.0, seawifs.getLambdaI()[0], 1e-8);
        assertEquals(490.0, seawifs.getLambdaI()[1], 1e-8);
    }

    @Test
    public void testByName_invalidName() {
        try {
            Sensor.byName("Firlefanz");
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }
    }
}
