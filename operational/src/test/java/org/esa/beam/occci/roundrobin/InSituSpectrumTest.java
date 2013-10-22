package org.esa.beam.occci.roundrobin;


import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.*;

public class InSituSpectrumTest {

    private InSituSpectrum inSituSpectrum;

    @Before
    public void setUp() {
        inSituSpectrum = new InSituSpectrum();
    }

    @Test
    public void testSetGetSpectralValue() {
        final double wavelength = 517.4;
        final double measurementValue = 1.8;
        addSpectralMeasurement(wavelength, measurementValue, 2);

        final SpectralMeasurement value = inSituSpectrum.getSpectralValue(2);
        assertNotNull(value);
        assertEquals(measurementValue, value.getMeasurement(), 1e-8);
        assertEquals(wavelength, value.getWavelength(), 1e-8);
    }

    @Test
    public void testSetSpectralValue_invalidIndex() {
        final SpectralMeasurement spectralMeasurement = new SpectralMeasurement();

        try {
            inSituSpectrum.setSpectralValue(spectralMeasurement, -1);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }

        try {
            inSituSpectrum.setSpectralValue(spectralMeasurement, 6);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testGetSpectralValue_invalidIndex() {
        try {
            inSituSpectrum.getSpectralValue(-1);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }

        try {
            inSituSpectrum.getSpectralValue(6);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testIsComplete() {
        assertFalse(inSituSpectrum.isComplete());

        addSpectralMeasurement(1.0, 0.0, 0);
        assertFalse(inSituSpectrum.isComplete());

        for (int i = 1; i < 6; i++) {
            addSpectralMeasurement(1.0, 0.0, i);
        }
        assertTrue(inSituSpectrum.isComplete());
    }

    @Test
    public void testGetWavelengths() {
        // @todo 4 tb/tb make loop
        addSpectralMeasurement(10.0, 0, 0);
        addSpectralMeasurement(11.0, 0, 1);
        addSpectralMeasurement(12.0, 0, 2);
        addSpectralMeasurement(13.0, 0, 3);
        addSpectralMeasurement(14.0, 0, 4);
        addSpectralMeasurement(15.0, 0, 5);

        final double[] wavelengths = inSituSpectrum.getWavelengths();
        assertNotNull(wavelengths);
        assertEquals(6, wavelengths.length);

        for (int i = 0; i < 6; i++) {
            assertEquals(10.0 + i, wavelengths[i], 1e-8);
        }
    }

    @Test
    public void testGetWavelengths_incompleteSpectrum() {
        try {
            inSituSpectrum.getWavelengths();
            fail("IllegalStateException expected");
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    public void testGetMeasurementsFloat() {
        for (int i = 0; i < 6; i++) {
            addSpectralMeasurement(10.0, 4 + i, i);
        }

        final float[] measurements = inSituSpectrum.getMeasurementsFloat();
        assertNotNull(measurements);
        assertEquals(6, measurements.length);

        for (int i = 0; i < 6; i++) {
            assertEquals(4 + i, measurements[i], 1e-8);
        }
    }

    @Test
    public void testGetMeasurementsFloat_incompleteSpectrum() {
        try {
            inSituSpectrum.getMeasurementsFloat();
            fail("IllegalStateException expected");
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    public void testGetMeasurements() {
        for (int i = 0; i < 6; i++) {
            addSpectralMeasurement(10.0, 6 + i, i);
        }

        final double[] measurements = inSituSpectrum.getMeasurements();
        assertNotNull(measurements);
        assertEquals(6, measurements.length);

        for (int i = 0; i < 6; i++) {
            assertEquals(6 + i, measurements[i], 1e-8);
        }
    }

    @Test
    public void testGetMeasurements_incompleteSpectrum() {
        try {
            inSituSpectrum.getMeasurements();
            fail("IllegalStateException expected");
        } catch (IllegalStateException expected) {
        }
    }

    private void addSpectralMeasurement(double wavelength, double value, int index) {
        final SpectralMeasurement spectralMeasurement = new SpectralMeasurement();
        spectralMeasurement.setWavelength(wavelength);
        spectralMeasurement.setMeasurement(value);
        inSituSpectrum.setSpectralValue(spectralMeasurement, index);
    }
}
