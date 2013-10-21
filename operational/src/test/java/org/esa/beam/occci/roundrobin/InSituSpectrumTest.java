package org.esa.beam.occci.roundrobin;


import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class InSituSpectrumTest {

    private InSituSpectrum inSituSpectrum;

    @Before
    public void setUp() {
        inSituSpectrum = new InSituSpectrum();
    }

    @Test
    public void testSetGetSpectralValue() {
        final SpectralMeasurement spectralMeasurement = new SpectralMeasurement();
        spectralMeasurement.setMeasurement(1.8);
        spectralMeasurement.setWavelength(517.4);

        inSituSpectrum.setSpectralValue(spectralMeasurement, 2);
        final SpectralMeasurement value = inSituSpectrum.getSpectralValue(2);
        assertNotNull(value);
        assertEquals(spectralMeasurement.getMeasurement(), value.getMeasurement(), 1e-8);
        assertEquals(spectralMeasurement.getWavelength(), value.getWavelength(), 1e-8);
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

        final SpectralMeasurement spectralMeasurement = new SpectralMeasurement();
        inSituSpectrum.setSpectralValue(spectralMeasurement, 0);

        assertFalse(inSituSpectrum.isComplete());

        for (int i = 1; i < 6; i++) {
            inSituSpectrum.setSpectralValue(spectralMeasurement, i);
        }
        assertTrue(inSituSpectrum.isComplete());
    }

    @Test
    public void testGetWavelengths() {
        addSpectralMeasurement(10.0, 0);
        addSpectralMeasurement(11.0, 1);
        addSpectralMeasurement(12.0, 2);
        addSpectralMeasurement(13.0, 3);
        addSpectralMeasurement(14.0, 4);
        addSpectralMeasurement(15.0, 5);

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

    private void addSpectralMeasurement(double wavelength, int index) {
        final SpectralMeasurement spectralMeasurement = new SpectralMeasurement();
        spectralMeasurement.setWavelength(wavelength);
        inSituSpectrum.setSpectralValue(spectralMeasurement, index);
    }
}
