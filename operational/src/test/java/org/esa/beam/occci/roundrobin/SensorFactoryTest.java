package org.esa.beam.occci.roundrobin;

import org.esa.beam.occci.bandshift.Sensor;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class SensorFactoryTest {

    @Test
    public void testCreateMerisLikeToMeris() {
        final double[] wavelengths = {412.0, 443.0, 490.0, 510.0, 560.0, 620.0, 665.0};
        final double[] rrs_in = {0.013793, 0.010255, 0.0065454, 0.0034769, 0.001357, 1.3942E-4, 4.9656E-5};
        final InSituSpectrum merisSpectrum= TestHelper.createMerisSpectrum(wavelengths, rrs_in);

        final Sensor toMeris = SensorFactory.createToMeris(merisSpectrum);
        assertNotNull(toMeris);
        assertArrayEquals(new double[]{412.0, 443.0, 490.0, 510.0, 560.0, 620.0, 665.0, 665.0}, toMeris.getLambdaI(), 1e-8);
        assertArrayEquals(new double[]{413.0, 443.0, 490.0, 510.0, 560.0, 620.0, 665.0, 665.0}, toMeris.getLambdaO(), 1e-8);
        assertArrayEquals(new double[]{413.0, 443.0, 490.0, 510.0, 560.0, 620.0, 665.0}, toMeris.getLambdaInterface(), 1e-8);
        assertArrayEquals(new double[]{413.0, 443.0, 490.0, 510.0, 560.0, 620.0, 665.0}, toMeris.getLambdaOAvg(), 1e-8);
        assertEquals(443.0, toMeris.getGreenWavelength(), 1e-8);
        final int[] averageIndices = toMeris.getAverageIndices();
        assertEquals(2, averageIndices.length);
        assertEquals(5, averageIndices[0]);
        assertEquals(6, averageIndices[1]);
    }

    @Test
    public void testCreateMerisLikeToModis() {
        final double[] wavelengths = {412.0, 443.0, 490.0, 510.0, 560.0, 620.0, 665.0};
        final double[] rrs_in = {0.006293, 0.0051744, 0.004696, 0.0034769, 0.0010097, 3.2786E-5, 4.173E-4};
        final InSituSpectrum merisSpectrum= TestHelper.createMerisSpectrum(wavelengths, rrs_in);

        final Sensor toModis = SensorFactory.createToModis(merisSpectrum);
        assertNotNull(toModis);
        assertArrayEquals(new double[] {412.0, 443.0, 490.0, 510.0, 560.0, 560.0, 665.0, 665.0}, toModis.getLambdaI(), 1e-8);
        assertArrayEquals(new double[] {412.0, 443.0, 488.0, 531.0, 531.0, 547.0, 667.0, 678.0}, toModis.getLambdaO(), 1e-8);
        assertArrayEquals(new double[]{412.0, 443.0, 488.0, 531.0, 547.0, 667.0, 678.0}, toModis.getLambdaInterface(), 1e-8);
        assertArrayEquals(new double[]{412.0, 443.0, 488.0, 531.0, 547.0, 667.0, 678.0}, toModis.getLambdaOAvg(), 1e-8);
        assertEquals(443.0, toModis.getGreenWavelength(), 1e-8);
        final int[] averageIndices = toModis.getAverageIndices();
        assertEquals(2, averageIndices.length);
        assertEquals(3, averageIndices[0]);
        assertEquals(4, averageIndices[1]);
    }

    @Test
    public void testCreateMerisLikeToSeaWifs() {
        final double[] wavelengths = {412.0, 443.0, 490.0, 510.0, 560.0, 620.0, 665.0};
        final double[] rrs_in = {0.0096578, 0.0079046, 0.0054482, 0.0028782, 0.0011144, 9.5493E-5, 3.8515E-5};
        final InSituSpectrum merisSpectrum= TestHelper.createMerisSpectrum(wavelengths, rrs_in);

        final Sensor toSeaWifs = SensorFactory.createToSeaWifs(merisSpectrum);
        assertNotNull(toSeaWifs);
        assertArrayEquals(new double[] {412.0, 443.0, 490.0, 510.0, 560.0, 665.0, 665.0, 665.0}, toSeaWifs.getLambdaI(), 1e-8);
        assertArrayEquals(new double[] {412.0, 443.0, 490.0, 510.0, 555.0, 670.0, 670.0, 670.0}, toSeaWifs.getLambdaO(), 1e-8);
        assertArrayEquals(new double[] {412.0, 443.0, 490.0, 510.0, 555.0, 670.0, 670.0}, toSeaWifs.getLambdaInterface(), 1e-8);
        assertArrayEquals(new double[] {412.0, 443.0, 490.0, 510.0, 555.0, 670.0, 670.0}, toSeaWifs.getLambdaOAvg(), 1e-8);
        assertEquals(443.0, toSeaWifs.getGreenWavelength(), 1e-8);
        final int[] averageIndices = toSeaWifs.getAverageIndices();
        assertEquals(2, averageIndices.length);
        assertEquals(5, averageIndices[0]);
        assertEquals(6, averageIndices[1]);
    }
}
