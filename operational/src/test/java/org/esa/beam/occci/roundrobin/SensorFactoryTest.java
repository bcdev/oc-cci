package org.esa.beam.occci.roundrobin;

import org.esa.beam.occci.bandshift.Sensor;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class SensorFactoryTest {

    public static final double[] MERIS_WAVELENGTHS = new double[]{413.0, 443.0, 490.0, 510.0, 560.0, 620.0, 665.0};

    @Test
    public void testCreateMerisLikeToMeris() {
        final double[] wavelengths = {412.0, 443.0, 490.0, 510.0, 560.0, 620.0, 665.0};
        final double[] rrs_in = {0.013793, 0.010255, 0.0065454, 0.0034769, 0.001357, 1.3942E-4, 4.9656E-5};
        final InSituSpectrum merisSpectrum= TestHelper.createMerisSpectrum(wavelengths, rrs_in);

        final Sensor toMeris = SensorFactory.createToMeris(merisSpectrum);
        assertNotNull(toMeris);
        assertArrayEquals(new double[]{412.0, 443.0, 490.0, 510.0, 560.0, 620.0, 665.0, 665.0}, toMeris.getLambdaI(), 1e-8);
        assertArrayEquals(new double[]{413.0, 443.0, 490.0, 510.0, 560.0, 620.0, 665.0, 665.0}, toMeris.getLambdaO(), 1e-8);
        assertArrayEquals(MERIS_WAVELENGTHS, toMeris.getLambdaInterface(), 1e-8);
        assertArrayEquals(MERIS_WAVELENGTHS, toMeris.getLambdaOAvg(), 1e-8);
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

    @Test
    public void testCreateModisLikeToMeris() {
        final double[] wavelengths = {412.0, 443.0, 488.0, 531.0, 547.0, 667.0, 678.0};
        final double[] rrs_in = {0.012084, 0.0089211, 0.0062153, 0.0021173, 0.0014871, 9.943E-5, 1.0206E-4};
        final InSituSpectrum modisSpectrum= TestHelper.createModisSpectrum(wavelengths, rrs_in);

        final Sensor toMeris = SensorFactory.createToMeris(modisSpectrum);
        assertNotNull(toMeris);
        assertArrayEquals(new double[]{412.0, 443.0, 488.0, 488.0, 531.0, 547.0, 667.0, 667.0}, toMeris.getLambdaI(), 1e-8);
        assertArrayEquals(new double[]{413.0, 443.0, 490.0, 510.0, 510.0, 560.0, 620.0, 665.0}, toMeris.getLambdaO(), 1e-8);
        assertArrayEquals(MERIS_WAVELENGTHS, toMeris.getLambdaInterface(), 1e-8);
        assertArrayEquals(new double[]{413.0, 443.0, 490.0, 510.0, 560.0, 620.0, 665.0}, toMeris.getLambdaOAvg(), 1e-8);
        assertEquals(443.0, toMeris.getGreenWavelength(), 1e-8);
        final int[] averageIndices = toMeris.getAverageIndices();
        assertEquals(2, averageIndices.length);
        assertEquals(3, averageIndices[0]);
        assertEquals(4, averageIndices[1]);
    }

    @Test
    public void testCreateModisLikeToModis() {
        final double[] wavelengths = {412.1, 443.1, 487.9, 531.1, 547.2, 667.2, 678.1};
        final double[] rrs_in = {0.0086757, 0.0064082, 0.0042756, 0.0013042, 9.1488E-4, 7.18E-5, 8.693E-5};
        final InSituSpectrum modisSpectrum= TestHelper.createModisSpectrum(wavelengths, rrs_in);

        final Sensor toModis = SensorFactory.createToModis(modisSpectrum);
        assertNotNull(toModis);
        assertArrayEquals(new double[]{412.1, 443.1, 487.9, 531.1, 547.2, 667.2, 678.1, 678.1}, toModis.getLambdaI(), 1e-8);
        assertArrayEquals(new double[]{412.0, 443.0, 488.0, 531.0, 547.0, 667.0, 678.0, 678.0}, toModis.getLambdaO(), 1e-8);
        assertArrayEquals(new double[]{412.0, 443.0, 488.0, 531.0, 547.0, 667.0, 678.0}, toModis.getLambdaInterface(), 1e-8);
        assertArrayEquals(new double[]{412.0, 443.0, 488.0, 531.0, 547.0, 667.0, 678.0}, toModis.getLambdaOAvg(), 1e-8);
        assertEquals(443.1, toModis.getGreenWavelength(), 1e-8);
        final int[] averageIndices = toModis.getAverageIndices();
        assertEquals(2, averageIndices.length);
        assertEquals(5, averageIndices[0]);
        assertEquals(6, averageIndices[1]);
    }

    @Test
    public void testCreateModisLikeToSeaWifs() {
        final double[] wavelengths = {412.0, 443.0, 488.0, 531.0, 547.0, 667.0, 678.0};
        final double[] rrs_in = {0.0095838, 0.0076441, 0.0056548, 0.0020036, 0.001417, 1.0517E-4, 1.1266E-4};
        final InSituSpectrum modisSpectrum= TestHelper.createModisSpectrum(wavelengths, rrs_in);

        final Sensor toSeaWifs = SensorFactory.createToSeaWifs(modisSpectrum);
        assertNotNull(toSeaWifs);
        assertArrayEquals(new double[]{412.0, 443.0, 488.0, 531.0, 547.0, 667.0, 678.0, 678.0}, toSeaWifs.getLambdaI(), 1e-8);
        assertArrayEquals(new double[]{412.0, 443.0, 490.0, 510.0, 555.0, 670.0, 670.0, 670.0}, toSeaWifs.getLambdaO(), 1e-8);
        assertArrayEquals(new double[]{412.0, 443.0, 490.0, 510.0, 555.0, 670.0, 670.0}, toSeaWifs.getLambdaInterface(), 1e-8);
        assertArrayEquals(new double[]{412.0, 443.0, 490.0, 510.0, 555.0, 670.0, 670.0}, toSeaWifs.getLambdaOAvg(), 1e-8);
        assertEquals(443.0, toSeaWifs.getGreenWavelength(), 1e-8);
        final int[] averageIndices = toSeaWifs.getAverageIndices();
        assertEquals(2, averageIndices.length);
        assertEquals(5, averageIndices[0]);
        assertEquals(6, averageIndices[1]);
    }

    @Test
    public void testCreateSeaWifsLikeToMeris() {
        final double[] wavelengths = {412.0, 443.0, 490.0, 510.0, 555.0, 670.0};
        final double[] rrs_in = {0.01318, 0.0094184, 0.0065419, 0.0032623, 0.0013976, 1.1169E-4};
        final InSituSpectrum seaWifsSpectrum= TestHelper.createSeaWifsSpectrum(wavelengths, rrs_in);

        final Sensor toMeris = SensorFactory.createToMeris(seaWifsSpectrum);
        assertNotNull(toMeris);
        assertArrayEquals(new double[]{412.0, 443.0, 490.0, 510.0, 555.0, 555.0, 670.0, 670.0}, toMeris.getLambdaI(), 1e-8);
        assertArrayEquals(new double[]{412.0, 443.0, 490.0, 510.0, 560.0, 620.0, 620.0, 665.0}, toMeris.getLambdaO(), 1e-8);
        assertArrayEquals(new double[]{412.0, 443.0, 490.0, 510.0, 555.0, 620.0, 665.0}, toMeris.getLambdaInterface(), 1e-8);
        assertArrayEquals(new double[]{412.0, 443.0, 490.0, 510.0, 555.0, 620.0, 665.0}, toMeris.getLambdaOAvg(), 1e-8);
        assertEquals(443.0, toMeris.getGreenWavelength(), 1e-8);
        final int[] averageIndices = toMeris.getAverageIndices();
        assertEquals(2, averageIndices.length);
        assertEquals(5, averageIndices[0]);
        assertEquals(6, averageIndices[1]);
    }

    @Test
    public void testCreateSeaWifsLikeToModis() {
        final double[] wavelengths = {412.0, 443.0, 490.0, 510.0, 555.0, 670.0};
        final double[] rrs_in = {0.0086553, 0.0063214, 0.0047236, 0.0028191, 0.0013089, 1.0021E-4};
        final InSituSpectrum seaWifsSpectrum= TestHelper.createSeaWifsSpectrum(wavelengths, rrs_in);

        final Sensor toModis = SensorFactory.createToModis(seaWifsSpectrum);
        assertNotNull(toModis);
        assertArrayEquals(new double[]{412.0, 443.0, 490.0, 510.0, 555.0, 555.0, 670.0, 670.0}, toModis.getLambdaI(), 1e-8);
        assertArrayEquals(new double[]{412.0, 443.0, 488.0, 531.0, 531.0, 547.0, 667.0, 678.0}, toModis.getLambdaO(), 1e-8);
        assertArrayEquals(new double[]{412.0, 443.0, 488.0, 531.0, 547.0, 667.0, 678.0}, toModis.getLambdaInterface(), 1e-8);
        assertArrayEquals(new double[]{412.0, 443.0, 488.0, 531.0, 547.0, 667.0, 678.0}, toModis.getLambdaOAvg(), 1e-8);
        assertEquals(443.0, toModis.getGreenWavelength(), 1e-8);
        final int[] averageIndices = toModis.getAverageIndices();
        assertEquals(2, averageIndices.length);
        assertEquals(4, averageIndices[0]);
        assertEquals(5, averageIndices[1]);
    }
}
