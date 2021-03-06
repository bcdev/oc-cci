package org.esa.beam.occci.roundrobin;


import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class InSituSpectrumTest {

    private InSituSpectrum inSituSpectrum;

    @Before
    public void setUp() {
        inSituSpectrum = new InSituSpectrum();
    }

    @Test
    public void testConstruction() {
         assertNotNull(inSituSpectrum.getSubdatasetRrs_1());
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
    public void testSetGetModisSpectralValue() {
        final double wavelength = 531.5;
        final double measurementValue = 0.092;
        addModisMeasurement(wavelength, measurementValue, 4);

        final SpectralMeasurement value = inSituSpectrum.getModisSpectralValue(4);
        assertNotNull(value);
        assertEquals(measurementValue, value.getMeasurement(), 1e-8);
        assertEquals(wavelength, value.getWavelength(), 1e-8);
    }

    @Test
    public void testSetModisSpectralValue_invalidIndex() {
        final SpectralMeasurement spectralMeasurement = new SpectralMeasurement();

        try {
            inSituSpectrum.setModisSpectralValue(spectralMeasurement, -1);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }

        try {
            inSituSpectrum.setModisSpectralValue(spectralMeasurement, 7);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testGetModisSpectralValue_invalidIndex() {
        try {
            inSituSpectrum.getModisSpectralValue(-1);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }

        try {
            inSituSpectrum.getModisSpectralValue(7);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testSetGetSeawifsSpectralValue() {
        final double wavelength = 555.6;
        final double measurementValue = 0.052;
        addSeawifsMeasurement(wavelength, measurementValue, 5);

        final SpectralMeasurement value = inSituSpectrum.getSeaWiFSSpectralValue(5);
        assertNotNull(value);
        assertEquals(measurementValue, value.getMeasurement(), 1e-8);
        assertEquals(wavelength, value.getWavelength(), 1e-8);
    }

    @Test
    public void testSetSeawifsSpectralValue_invalidIndex() {
        final SpectralMeasurement spectralMeasurement = new SpectralMeasurement();

        try {
            inSituSpectrum.setSeaWiFSSpectralValue(spectralMeasurement, -1);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }

        try {
            inSituSpectrum.setSeaWiFSSpectralValue(spectralMeasurement, 6);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testGetSeawifsSpectralValue_invalidIndex() {
        try {
            inSituSpectrum.getSeaWiFSSpectralValue(-1);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }

        try {
            inSituSpectrum.getSeaWiFSSpectralValue(6);
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
    public void testIsCompleteModis() {
        assertFalse(inSituSpectrum.isCompleteModis());

        addModisMeasurement(2.0, 0.0, 4);
        assertFalse(inSituSpectrum.isCompleteModis());

        for (int i = 0; i < 7; i++) {
            addModisMeasurement(1.0, i, i);
        }
        assertTrue(inSituSpectrum.isCompleteModis());
    }

    @Test
    public void testIsCompleteSeawifs() {
        assertFalse(inSituSpectrum.isCompleteSeaWiFS());

        addSeawifsMeasurement(2.0, 0.0, 4);
        assertFalse(inSituSpectrum.isCompleteSeaWiFS());

        for (int i = 0; i < 6; i++) {
            addSeawifsMeasurement(1.0, i, i);
        }
        assertTrue(inSituSpectrum.isCompleteSeaWiFS());
    }

    @Test
    public void testGetQaaWavelengths() {
        for (int i = 0; i < 6; i++) {
            addSpectralMeasurement(10.0 + i, 0, i);
        }

        final double[] wavelengths = inSituSpectrum.getQaaWavelengths();
        assertNotNull(wavelengths);
        assertEquals(6, wavelengths.length);

        for (int i = 0; i < 6; i++) {
            assertEquals(10.0 + i, wavelengths[i], 1e-8);
        }
    }

    @Test
    public void testGetQaaWavelengths_incompleteSpectrum() {
        try {
            inSituSpectrum.getQaaWavelengths();
            fail("IllegalStateException expected");
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    public void testGetMerisWavelengths() {
        for (int i = 0; i < 7; i++) {
            addMerisMeasurement(11.0 + i, 0, i);
        }

        final double[] wavelengths = inSituSpectrum.getMerisWavelengths();
        assertNotNull(wavelengths);
        assertEquals(7, wavelengths.length);

        for (int i = 0; i < 7; i++) {
            assertEquals(11.0 + i, wavelengths[i], 1e-8);
        }
    }

    @Test
    public void testGetMerisWavelengths_incompleteSpectrum() {
        try {
            inSituSpectrum.getMerisWavelengths();
            fail("IllegalStateException expected");
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    public void testGetModisWavelengths() {
        for (int i = 0; i < 7; i++) {
            addModisMeasurement(12.0 + i, 0, i);
        }

        final double[] wavelengths = inSituSpectrum.getModisWavelengths();
        assertNotNull(wavelengths);
        assertEquals(7, wavelengths.length);

        for (int i = 0; i < 7; i++) {
            assertEquals(12.0 + i, wavelengths[i], 1e-8);
        }
    }

    @Test
    public void testGetModisWavelengths_incompleteSpectrum() {
        try {
            inSituSpectrum.getModisWavelengths();
            fail("IllegalStateException expected");
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    public void testGetSeaWifsWavelengths() {
        for (int i = 0; i < 6; i++) {
            addSeawifsMeasurement(13.0 + i, 0, i);
        }

        final double[] wavelengths = inSituSpectrum.getSeaWifsWavelengths();
        assertNotNull(wavelengths);
        assertEquals(6, wavelengths.length);

        for (int i = 0; i < 6; i++) {
            assertEquals(13.0 + i, wavelengths[i], 1e-8);
        }
    }

    @Test
    public void testGetSeaWifsWavelengths_incompleteSpectrum() {
        try {
            inSituSpectrum.getSeaWifsWavelengths();
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
    public void testGetQaaMeasurements() {
        for (int i = 0; i < 6; i++) {
            addSpectralMeasurement(10.0, 6 + i, i);
        }

        final double[] measurements = inSituSpectrum.getQaaMeasurements();
        assertNotNull(measurements);
        assertEquals(6, measurements.length);

        for (int i = 0; i < 6; i++) {
            assertEquals(6 + i, measurements[i], 1e-8);
        }
    }

    @Test
    public void testGetQaaMeasurements_incompleteSpectrum() {
        try {
            inSituSpectrum.getQaaMeasurements();
            fail("IllegalStateException expected");
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    public void testGetMerisMeasurements() {
        for (int i = 0; i < 7; i++) {
            addMerisMeasurement(11.0, 7 + i, i);
        }

        final double[] measurements = inSituSpectrum.getMerisMeasurements();
        assertNotNull(measurements);
        assertEquals(7, measurements.length);

        for (int i = 0; i < 7; i++) {
            assertEquals(7 + i, measurements[i], 1e-8);
        }
    }

    @Test
    public void testGetMerisMeasurements_incompleteSpectrum() {
        try {
            inSituSpectrum.getMerisMeasurements();
            fail("IllegalStateException expected");
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    public void testGetModisMeasurements() {
        for (int i = 0; i < 7; i++) {
            addModisMeasurement(12.0, 8 + i, i);
        }

        final double[] measurements = inSituSpectrum.getModisMeasurements();
        assertNotNull(measurements);
        assertEquals(7, measurements.length);

        for (int i = 0; i < 7; i++) {
            assertEquals(8 + i, measurements[i], 1e-8);
        }
    }

    @Test
    public void testGetModisMeasurements_incompleteSpectrum() {
        try {
            inSituSpectrum.getModisMeasurements();
            fail("IllegalStateException expected");
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    public void testGetSeaWifsMeasurements() {
        for (int i = 0; i < 6; i++) {
            addSeawifsMeasurement(13.0, 9 + i, i);
        }

        final double[] measurements = inSituSpectrum.getSeaWifsMeasurements();
        assertNotNull(measurements);
        assertEquals(6, measurements.length);

        for (int i = 0; i < 6; i++) {
            assertEquals(9 + i, measurements[i], 1e-8);
        }
    }

    @Test
    public void testSetGetSubdatasetRrs_1() {
         final String subdatasetRrs_1 = "hoppla";
         final String subdatasetRrs_2 = "hier komm ich";

        inSituSpectrum.setSubdatasetRrs_1(subdatasetRrs_1);
        assertEquals(subdatasetRrs_1, inSituSpectrum.getSubdatasetRrs_1());

        inSituSpectrum.setSubdatasetRrs_1(subdatasetRrs_2);
        assertEquals(subdatasetRrs_2, inSituSpectrum.getSubdatasetRrs_1());
    }

    private void addSpectralMeasurement(double wavelength, double value, int index) {
        final SpectralMeasurement spectralMeasurement = createSpectralMeasurement(wavelength, value);
        inSituSpectrum.setQaaSpectralValue(spectralMeasurement, index);
    }

    private void addMerisMeasurement(double wavelength, double value, int index) {
        final SpectralMeasurement spectralMeasurement = createSpectralMeasurement(wavelength, value);
        inSituSpectrum.setMerisSpectralValue(spectralMeasurement, index);
    }

    private void addModisMeasurement(double wavelength, double value, int index) {
        final SpectralMeasurement spectralMeasurement = createSpectralMeasurement(wavelength, value);
        inSituSpectrum.setModisSpectralValue(spectralMeasurement, index);
    }

    private void addSeawifsMeasurement(double wavelength, double value, int index) {
        final SpectralMeasurement spectralMeasurement = createSpectralMeasurement(wavelength, value);
        inSituSpectrum.setSeaWiFSSpectralValue(spectralMeasurement, index);
    }

    private SpectralMeasurement createSpectralMeasurement(double wavelength, double value) {
        final SpectralMeasurement spectralMeasurement = new SpectralMeasurement();
        spectralMeasurement.setWavelength(wavelength);
        spectralMeasurement.setMeasurement(value);
        return spectralMeasurement;
    }
}
