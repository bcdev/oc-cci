package org.esa.beam.occci.roundrobin;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class SpectrumBuilderTest {

    @Test
    public void testParseBand_1_MERIS() {
        final String[] csvRecord = createEmptyStringArray(5);
        csvRecord[3] = "0.4567";
        csvRecord[4] = "412.1";

        final InSituSpectrum spectrum = SpectrumBuilder.create(csvRecord);
        final SpectralMeasurement spectralValue = spectrum.getSpectralValue(0);
        assertNotNull(spectralValue);
        assertEquals(0.4567, spectralValue.getMeasurement(), 1e-8);
        assertEquals(412.1, spectralValue.getWavelength(), 1e-8);
    }

    @Test
    public void testParseBand_2_MERIS() {
        final String[] csvRecord = createEmptyStringArray(7);
        csvRecord[5] = "0.8662";
        csvRecord[6] = "443.1";

        final InSituSpectrum spectrum = SpectrumBuilder.create(csvRecord);
        final SpectralMeasurement spectralValue = spectrum.getSpectralValue(1);
        assertNotNull(spectralValue);
        assertEquals(0.8662, spectralValue.getMeasurement(), 1e-8);
        assertEquals(443.1, spectralValue.getWavelength(), 1e-8);
    }

    @Test
    public void testParseBand_1_MODIS() {
        final String[] csvRecord = createEmptyStringArray(23);
        csvRecord[21] = "0.5678";
        csvRecord[22] = "412.2";

        final InSituSpectrum spectrum = SpectrumBuilder.create(csvRecord);
        final SpectralMeasurement spectralValue = spectrum.getSpectralValue(0);
        assertNotNull(spectralValue);
        assertEquals(0.5678, spectralValue.getMeasurement(), 1e-8);
        assertEquals(412.2, spectralValue.getWavelength(), 1e-8);
    }

    @Test
    public void testParseBand_2_MODIS() {
        final String[] csvRecord = createEmptyStringArray(25);
        csvRecord[23] = "0.6647";
        csvRecord[24] = "443.2";

        final InSituSpectrum spectrum = SpectrumBuilder.create(csvRecord);
        final SpectralMeasurement spectralValue = spectrum.getSpectralValue(1);
        assertNotNull(spectralValue);
        assertEquals(0.6647, spectralValue.getMeasurement(), 1e-8);
        assertEquals(443.2, spectralValue.getWavelength(), 1e-8);
    }

    @Test
    public void testParseBand_1_SeaWiFS() {
        final String[] csvRecord = createEmptyStringArray(37);
        csvRecord[35] = "0.6789";
        csvRecord[36] = "412.3";

        final InSituSpectrum spectrum = SpectrumBuilder.create(csvRecord);
        final SpectralMeasurement spectralValue = spectrum.getSpectralValue(0);
        assertNotNull(spectralValue);
        assertEquals(0.6789, spectralValue.getMeasurement(), 1e-8);
        assertEquals(412.3, spectralValue.getWavelength(), 1e-8);
    }

    @Test
    public void testParseBand_2_SeaWiFS() {
        final String[] csvRecord = createEmptyStringArray(39);
        csvRecord[37] = "0.2256";
        csvRecord[38] = "443.5";

        final InSituSpectrum spectrum = SpectrumBuilder.create(csvRecord);
        final SpectralMeasurement spectralValue = spectrum.getSpectralValue(1);
        assertNotNull(spectralValue);
        assertEquals(0.2256, spectralValue.getMeasurement(), 1e-8);
        assertEquals(443.5, spectralValue.getWavelength(), 1e-8);
    }

    private String[] createEmptyStringArray(int size) {
        final String[] strings = new String[size];
        for (int i = 0; i < strings.length; i++) {
            strings[i] = "";
        }
        return strings;
    }
}
