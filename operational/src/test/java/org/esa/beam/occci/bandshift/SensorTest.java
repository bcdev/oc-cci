package org.esa.beam.occci.bandshift;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class SensorTest {

    @Test
    public void testLambdaInterface_MERIS() {
        final double[] expected = {413., 443., 490., 510., 560., 665.};
        assertArrayEquals(expected, Sensor.MERIS.getLambaInterface(), 1e-8);
    }

    @Test
    public void testLambdaInterface_MODIS() {
        final double[] expected = {412., 443., 488., 531., 547., 667.};
        assertArrayEquals(expected, Sensor.MODISA.getLambaInterface(), 1e-8);
    }

    @Test
    public void testLambdaInterface_SEAWIFS() {
        final double[] expected = {412., 443., 490., 510., 555., 667.};
        assertArrayEquals(expected, Sensor.SEAWIFS.getLambaInterface(), 1e-8);
    }
}
