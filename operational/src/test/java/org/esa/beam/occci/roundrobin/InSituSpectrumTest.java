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
    public void testSetGetQaaSpectralValue() {
        final double wavelength = 517.4;
        final double measurementValue = 1.8;
        addSpectralMeasurement(wavelength, measurementValue, 2);

        final SpectralMeasurement value = inSituSpectrum.getQaaSpectralValue(2);
        assertNotNull(value);
        assertEquals(measurementValue, value.getMeasurement(), 1e-8);
        assertEquals(wavelength, value.getWavelength(), 1e-8);
    }

    @Test
    public void testSetQaaSpectralValue_invalidIndex() {
        final SpectralMeasurement spectralMeasurement = new SpectralMeasurement();

        try {
            inSituSpectrum.setQaaSpectralValue(spectralMeasurement, -1);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }

        try {
            inSituSpectrum.setQaaSpectralValue(spectralMeasurement, 6);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testGetQaaSpectralValue_invalidIndex() {
        try {
            inSituSpectrum.getQaaSpectralValue(-1);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }

        try {
            inSituSpectrum.getQaaSpectralValue(6);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testSetGetMerisSpectralValue() {
        final double wavelength = 560.2;
        final double measurementValue = 0.8;
        addMerisMeasurement(wavelength, measurementValue, 3);

        final SpectralMeasurement value = inSituSpectrum.getMerisSpectralValue(3);
        assertNotNull(value);
        assertEquals(measurementValue, value.getMeasurement(), 1e-8);
        assertEquals(wavelength, value.getWavelength(), 1e-8);
    }

    @Test
    public void testSetMerisSpectralValue_invalidIndex() {
        final SpectralMeasurement spectralMeasurement = new SpectralMeasurement();

        try {
            inSituSpectrum.setMerisSpectralValue(spectralMeasurement, -1);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }

        try {
            inSituSpectrum.setMerisSpectralValue(spectralMeasurement, 7);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testGetMerisSpectralValue_invalidIndex() {
        try {
            inSituSpectrum.getMerisSpectralValue(-1);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }

        try {
            inSituSpectrum.getMerisSpectralValue(7);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testIsCompleteQaa() {
        assertFalse(inSituSpectrum.isCompleteQaa());

        addSpectralMeasurement(1.0, 0.0, 0);
        assertFalse(inSituSpectrum.isCompleteQaa());

        for (int i = 1; i < 6; i++) {
            addSpectralMeasurement(1.0, 0.0, i);
        }
        assertTrue(inSituSpectrum.isCompleteQaa());
    }

    @Test
    public void testIsCompleteMeris() {
        assertFalse(inSituSpectrum.isCompleteMeris());

        addMerisMeasurement(1.0, 0.0, 3);
        assertFalse(inSituSpectrum.isCompleteMeris());

        for (int i = 0; i < 7; i++) {
            addMerisMeasurement(1.0, i, i);
        }
        assertTrue(inSituSpectrum.isCompleteMeris());
    }

    @Test
    public void testGetWavelengths() {
        for (int i = 0; i < 6; i++) {
            addSpectralMeasurement(10.0 + i, 0, i);
        }

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
        final SpectralMeasurement spectralMeasurement = createSpectralMeasurement(wavelength, value);
        inSituSpectrum.setQaaSpectralValue(spectralMeasurement, index);
    }

    private void addMerisMeasurement(double wavelength, double value, int index) {
        final SpectralMeasurement spectralMeasurement = createSpectralMeasurement(wavelength, value);
        inSituSpectrum.setMerisSpectralValue(spectralMeasurement, index);
    }

    private SpectralMeasurement createSpectralMeasurement(double wavelength, double value) {
        final SpectralMeasurement spectralMeasurement = new SpectralMeasurement();
        spectralMeasurement.setWavelength(wavelength);
        spectralMeasurement.setMeasurement(value);
        return spectralMeasurement;
    }
}
