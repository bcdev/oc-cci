package org.esa.beam.occci.roundrobin;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class SpectrumBuilderTest {

    @Test
    public void testParseBand_1_MERIS() {
        final String[] csvRecord = createEmptyStringArray(5);
        csvRecord[3] = "0.4567";
        csvRecord[4] = "412.1";

        final InSituSpectrum spectrum = SpectrumBuilder.create(csvRecord);
        final SpectralMeasurement spectralValue = spectrum.getQaaSpectralValue(0);
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
        final SpectralMeasurement spectralValue = spectrum.getQaaSpectralValue(1);
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
        final SpectralMeasurement spectralValue = spectrum.getQaaSpectralValue(0);
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
        final SpectralMeasurement spectralValue = spectrum.getQaaSpectralValue(1);
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
        final SpectralMeasurement spectralValue = spectrum.getQaaSpectralValue(0);
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
        final SpectralMeasurement spectralValue = spectrum.getQaaSpectralValue(1);
        assertNotNull(spectralValue);
        assertEquals(0.2256, spectralValue.getMeasurement(), 1e-8);
        assertEquals(443.5, spectralValue.getWavelength(), 1e-8);
    }

    @Test
    public void testParseDateTime() {
        final String[] csvRecord = createEmptyStringArray(3);
        csvRecord[0] = "1998-03-08T22:41:01Z";

        final InSituSpectrum spectrum = SpectrumBuilder.create(csvRecord);
        assertEquals(csvRecord[0], spectrum.getDateTime());
    }

    @Test
    public void testParseLat() {
        final String[] csvRecord = createEmptyStringArray(3);
        csvRecord[1] = "-19.087";

        final InSituSpectrum spectrum = SpectrumBuilder.create(csvRecord);
        assertEquals(csvRecord[1], spectrum.getLat());
    }

    @Test
    public void testParseLon() {
        final String[] csvRecord = createEmptyStringArray(3);
        csvRecord[2] = "65.998";

        final InSituSpectrum spectrum = SpectrumBuilder.create(csvRecord);
        assertEquals(csvRecord[2], spectrum.getLon());
    }

    @Test
    public void testParseMerisSpectrum() {
        final String[] csvRecord = createEmptyStringArray(19);
        csvRecord[3] = "0.012656";
        csvRecord[4] = "412";
        csvRecord[5] = "0.0095722";
        csvRecord[6] = "443";
        csvRecord[7] = "0.0060453";
        csvRecord[8] = "490";
        csvRecord[9] = "0.0031277";
        csvRecord[10] = "510";
        csvRecord[11] = "0.0011844";
        csvRecord[12] = "560";
        csvRecord[13] = "0.00011236";
        csvRecord[14] = "620";
        csvRecord[15] = "4.2335e-05";
        csvRecord[16] = "665";
        csvRecord[17] = "3.6606e-05";
        csvRecord[18] = "681";

        final InSituSpectrum spectrum = SpectrumBuilder.create(csvRecord);
        assertNotNull(spectrum);
        SpectralMeasurement value = spectrum.getMerisSpectralValue(0);
        assertEquals(0.012656, value.getMeasurement(), 1e-8);
        assertEquals(412.0, value.getWavelength(), 1e-8);

        value = spectrum.getMerisSpectralValue(1);
        assertEquals(0.0095722, value.getMeasurement(), 1e-8);
        assertEquals(443.0, value.getWavelength(), 1e-8);

        value = spectrum.getMerisSpectralValue(2);
        assertEquals(0.0060453, value.getMeasurement(), 1e-8);
        assertEquals(490.0, value.getWavelength(), 1e-8);

        value = spectrum.getMerisSpectralValue(3);
        assertEquals(0.0031277, value.getMeasurement(), 1e-8);
        assertEquals(510.0, value.getWavelength(), 1e-8);

        value = spectrum.getMerisSpectralValue(4);
        assertEquals(0.0011844, value.getMeasurement(), 1e-8);
        assertEquals(560.0, value.getWavelength(), 1e-8);

        value = spectrum.getMerisSpectralValue(5);
        assertEquals(0.00011236, value.getMeasurement(), 1e-8);
        assertEquals(620.0, value.getWavelength(), 1e-8);

        value = spectrum.getMerisSpectralValue(6);
        assertEquals(4.2335e-05, value.getMeasurement(), 1e-8);
        assertEquals(665.0, value.getWavelength(), 1e-8);
    }

    @Test
    public void testParseModisSpectrum() {
        final String[] csvRecord = createEmptyStringArray(35);
        csvRecord[21] = "0.011445";
        csvRecord[22] = "412";
        csvRecord[23] = "0.008856";
        csvRecord[24] = "443";
        csvRecord[25] = "0.0064771";
        csvRecord[26] = "490";
        csvRecord[27] = "";
        csvRecord[28] = "";
        csvRecord[29] = "";
        csvRecord[30] = "";
        csvRecord[31] = "0.00010887";
        csvRecord[32] = "667";
        csvRecord[33] = "0.00012887";
        csvRecord[34] = "678";

        final InSituSpectrum spectrum = SpectrumBuilder.create(csvRecord);
        assertNotNull(spectrum);

        SpectralMeasurement value = spectrum.getModisSpectralValue(0);
        assertEquals(0.011445, value.getMeasurement(), 1e-8);
        assertEquals(412.0, value.getWavelength(), 1e-8);

        value = spectrum.getModisSpectralValue(1);
        assertEquals(0.008856, value.getMeasurement(), 1e-8);
        assertEquals(443.0, value.getWavelength(), 1e-8);

        value = spectrum.getModisSpectralValue(2);
        assertEquals(0.0064771, value.getMeasurement(), 1e-8);
        assertEquals(490.0, value.getWavelength(), 1e-8);

        value = spectrum.getModisSpectralValue(3);
        assertNull(value);

        value = spectrum.getModisSpectralValue(4);
        assertNull(value);

        value = spectrum.getModisSpectralValue(5);
        assertEquals(0.00010887, value.getMeasurement(), 1e-8);
        assertEquals(667.0, value.getWavelength(), 1e-8);

        value = spectrum.getModisSpectralValue(6);
        assertEquals(0.00012887, value.getMeasurement(), 1e-8);
        assertEquals(678.0, value.getWavelength(), 1e-8);
    }

    private String[] createEmptyStringArray(int size) {
        final String[] strings = new String[size];
        for (int i = 0; i < strings.length; i++) {
            strings[i] = "";
        }
        return strings;
    }
}
