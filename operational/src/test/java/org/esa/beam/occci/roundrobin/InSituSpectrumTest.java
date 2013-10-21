package org.esa.beam.occci.roundrobin;


import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
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
}
