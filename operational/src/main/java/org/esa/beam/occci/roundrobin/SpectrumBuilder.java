package org.esa.beam.occci.roundrobin;

import org.esa.beam.util.StringUtils;

class SpectrumBuilder {

    /*
      insitu DB v1
    private static final int MERIS_BAND_413_IDX = 3;
    private static final int MERIS_BAND_443_IDX = 5;
    private static final int MERIS_BAND_490_IDX = 7;
    private static final int MERIS_BAND_510_IDX = 9;
    private static final int MERIS_BAND_560_IDX = 11;
    private static final int MERIS_BAND_620_IDX = 13;
    private static final int MERIS_BAND_665_IDX = 15;
    // private static final int MERIS_BAND_681_IDX = 17;    not needed for now tb 2013-10-23

    private static final int MODIS_BAND_412_IDX = 21;
    private static final int MODIS_BAND_443_IDX = 23;
    private static final int MODIS_BAND_488_IDX = 25;
    private static final int MODIS_BAND_531_IDX = 27;
    private static final int MODIS_BAND_547_IDX = 29;
    private static final int MODIS_BAND_667_IDX = 31;
    private static final int MODIS_BAND_678_IDX = 33;

    private static final int SEAWIFS_BAND_412_IDX = 35;
    private static final int SEAWIFS_BAND_443_IDX = 37;
    private static final int SEAWIFS_BAND_490_IDX = 39;
    private static final int SEAWIFS_BAND_510_IDX = 41;
    private static final int SEAWIFS_BAND_555_IDX = 43;
    private static final int SEAWIFS_BAND_670_IDX = 45;

    private static final int SUBDATASET_RRS_1_IDX = 142;
    */

    // insitu DB v2
    private static final int MERIS_BAND_413_IDX = 5;
    private static final int MERIS_BAND_443_IDX = 7;
    private static final int MERIS_BAND_490_IDX = 9;
    private static final int MERIS_BAND_510_IDX = 11;
    private static final int MERIS_BAND_560_IDX = 13;
    private static final int MERIS_BAND_620_IDX = 15;
    private static final int MERIS_BAND_665_IDX = 17;

    private static final int MODIS_BAND_412_IDX = 23;
    private static final int MODIS_BAND_443_IDX = 25;
    private static final int MODIS_BAND_488_IDX = 27;
    private static final int MODIS_BAND_531_IDX = 29;
    private static final int MODIS_BAND_547_IDX = 31;
    private static final int MODIS_BAND_667_IDX = 33;
    private static final int MODIS_BAND_678_IDX = 35;

    private static final int SEAWIFS_BAND_412_IDX = 37;
    private static final int SEAWIFS_BAND_443_IDX = 39;
    private static final int SEAWIFS_BAND_490_IDX = 41;
    private static final int SEAWIFS_BAND_510_IDX = 43;
    private static final int SEAWIFS_BAND_555_IDX = 45;
    private static final int SEAWIFS_BAND_670_IDX = 47;

    // insitu DB v3
    /*
    private static final int MERIS_BAND_413_IDX = 3;
    private static final int MERIS_BAND_443_IDX = 4;
    private static final int MERIS_BAND_490_IDX = 5;
    private static final int MERIS_BAND_510_IDX = 6;
    private static final int MERIS_BAND_560_IDX = 7;
    private static final int MERIS_BAND_620_IDX = 8;
    private static final int MERIS_BAND_665_IDX = 9;

    private static final int MODIS_BAND_412_IDX = 11;
    private static final int MODIS_BAND_443_IDX = 12;
    private static final int MODIS_BAND_488_IDX = 13;
    private static final int MODIS_BAND_531_IDX = 14;
    private static final int MODIS_BAND_547_IDX = 15;
    private static final int MODIS_BAND_667_IDX = 16;
    private static final int MODIS_BAND_678_IDX = 17;

    private static final int SEAWIFS_BAND_412_IDX = 18;
    private static final int SEAWIFS_BAND_443_IDX = 19;
    private static final int SEAWIFS_BAND_490_IDX = 20;
    private static final int SEAWIFS_BAND_510_IDX = 21;
    private static final int SEAWIFS_BAND_555_IDX = 22;
    private static final int SEAWIFS_BAND_670_IDX = 23;
    */

    private static final int SUBDATASET_RRS_1_IDX = 234;


    public static InSituSpectrum create(String[] csvRecords) {
        final InSituSpectrum spectrum = new InSituSpectrum();

        parseTimeAndLocation(csvRecords, spectrum);
        parseQaaSpectrum(csvRecords, spectrum);
        parseMerisSpectrum(csvRecords, spectrum);
        parseModisSpectrum(csvRecords, spectrum);
        parseSeaWiFSSpectrum(csvRecords, spectrum);

        if (csvRecords.length > SUBDATASET_RRS_1_IDX) {
            spectrum.setSubdatasetRrs_1(csvRecords[SUBDATASET_RRS_1_IDX]);
        }

        return spectrum;
    }

    private static void parseSeaWiFSSpectrum(String[] csvRecords, InSituSpectrum spectrum) {
        spectrum.setSeaWiFSSpectralValue(getMeasurement(csvRecords, SEAWIFS_BAND_412_IDX), 0);
        spectrum.setSeaWiFSSpectralValue(getMeasurement(csvRecords, SEAWIFS_BAND_443_IDX), 1);
        spectrum.setSeaWiFSSpectralValue(getMeasurement(csvRecords, SEAWIFS_BAND_490_IDX), 2);
        spectrum.setSeaWiFSSpectralValue(getMeasurement(csvRecords, SEAWIFS_BAND_510_IDX), 3);
        spectrum.setSeaWiFSSpectralValue(getMeasurement(csvRecords, SEAWIFS_BAND_555_IDX), 4);
        spectrum.setSeaWiFSSpectralValue(getMeasurement(csvRecords, SEAWIFS_BAND_670_IDX), 5);
    }

    private static void parseModisSpectrum(String[] csvRecords, InSituSpectrum spectrum) {
        spectrum.setModisSpectralValue(getMeasurement(csvRecords, MODIS_BAND_412_IDX), 0);
        spectrum.setModisSpectralValue(getMeasurement(csvRecords, MODIS_BAND_443_IDX), 1);
        spectrum.setModisSpectralValue(getMeasurement(csvRecords, MODIS_BAND_488_IDX), 2);
        spectrum.setModisSpectralValue(getMeasurement(csvRecords, MODIS_BAND_531_IDX), 3);
        spectrum.setModisSpectralValue(getMeasurement(csvRecords, MODIS_BAND_547_IDX), 4);
        spectrum.setModisSpectralValue(getMeasurement(csvRecords, MODIS_BAND_667_IDX), 5);
        spectrum.setModisSpectralValue(getMeasurement(csvRecords, MODIS_BAND_678_IDX), 6);
    }

    private static void parseMerisSpectrum(String[] csvRecords, InSituSpectrum spectrum) {
        spectrum.setMerisSpectralValue(getMeasurement(csvRecords, MERIS_BAND_413_IDX), 0);
        spectrum.setMerisSpectralValue(getMeasurement(csvRecords, MERIS_BAND_443_IDX), 1);
        spectrum.setMerisSpectralValue(getMeasurement(csvRecords, MERIS_BAND_490_IDX), 2);
        spectrum.setMerisSpectralValue(getMeasurement(csvRecords, MERIS_BAND_510_IDX), 3);
        spectrum.setMerisSpectralValue(getMeasurement(csvRecords, MERIS_BAND_560_IDX), 4);
        spectrum.setMerisSpectralValue(getMeasurement(csvRecords, MERIS_BAND_620_IDX), 5);
        spectrum.setMerisSpectralValue(getMeasurement(csvRecords, MERIS_BAND_665_IDX), 6);
    }

    private static SpectralMeasurement getMeasurement(String[] csvRecords, int index) {
        if (isBandPresent(csvRecords, index)) {
            return parseSpectralMeasurement(csvRecords, index);
        }
        return null;
    }

    private static void parseQaaSpectrum(String[] csvRecords, InSituSpectrum spectrum) {
        SpectralMeasurement spectralMeasurement = getSpectralMeasurement_412(csvRecords);
        spectrum.setQaaSpectralValue(spectralMeasurement, 0);

        spectralMeasurement = getSpectralMeasurement_443(csvRecords);
        spectrum.setQaaSpectralValue(spectralMeasurement, 1);

        spectralMeasurement = getSpectralMeasurement_490(csvRecords);
        spectrum.setQaaSpectralValue(spectralMeasurement, 2);

        spectralMeasurement = getSpectralMeasurement_510(csvRecords);
        spectrum.setQaaSpectralValue(spectralMeasurement, 3);

        spectralMeasurement = getSpectralMeasurement_555(csvRecords);
        spectrum.setQaaSpectralValue(spectralMeasurement, 4);

        spectralMeasurement = getSpectralMeasurement_670(csvRecords);
        spectrum.setQaaSpectralValue(spectralMeasurement, 5);
    }

    private static void parseTimeAndLocation(String[] csvRecords, InSituSpectrum spectrum) {
        spectrum.setDateTime(csvRecords[0]);
        spectrum.setLat(csvRecords[1]);
        spectrum.setLon(csvRecords[2]);
    }

    private static SpectralMeasurement getSpectralMeasurement_412(String[] csvRecords) {
        SpectralMeasurement spectralMeasurement = getMeasurement(csvRecords, MERIS_BAND_413_IDX);
        if (spectralMeasurement == null) {
            spectralMeasurement = getMeasurement(csvRecords, MODIS_BAND_412_IDX);
            if (spectralMeasurement == null) {
                spectralMeasurement = getMeasurement(csvRecords, SEAWIFS_BAND_412_IDX);
            }
        }
        return spectralMeasurement;
    }

    private static SpectralMeasurement getSpectralMeasurement_443(String[] csvRecords) {
        SpectralMeasurement spectralMeasurement = getMeasurement(csvRecords, MERIS_BAND_443_IDX);
        if (spectralMeasurement == null) {
            spectralMeasurement = getMeasurement(csvRecords, MODIS_BAND_443_IDX);
            if (spectralMeasurement == null) {
                spectralMeasurement = getMeasurement(csvRecords, SEAWIFS_BAND_443_IDX);
            }
        }
        return spectralMeasurement;
    }

    private static SpectralMeasurement getSpectralMeasurement_490(String[] csvRecords) {
        SpectralMeasurement spectralMeasurement = getMeasurement(csvRecords, MERIS_BAND_490_IDX);
        if (spectralMeasurement == null) {
            spectralMeasurement = getMeasurement(csvRecords, MODIS_BAND_488_IDX);
            if (spectralMeasurement == null) {
                spectralMeasurement = getMeasurement(csvRecords, SEAWIFS_BAND_490_IDX);
            }
        }
        return spectralMeasurement;
    }

    private static SpectralMeasurement getSpectralMeasurement_510(String[] csvRecords) {
        SpectralMeasurement spectralMeasurement = getMeasurement(csvRecords, MERIS_BAND_510_IDX);
        if (spectralMeasurement == null) {
            spectralMeasurement = getMeasurement(csvRecords, MODIS_BAND_531_IDX);
            if (spectralMeasurement == null) {
                spectralMeasurement = getMeasurement(csvRecords, SEAWIFS_BAND_510_IDX);
            }
        }
        return spectralMeasurement;
    }

    private static SpectralMeasurement getSpectralMeasurement_555(String[] csvRecords) {
        SpectralMeasurement spectralMeasurement = null;
        if (isBandPresent(csvRecords, MERIS_BAND_560_IDX)) {
            spectralMeasurement = parseSpectralMeasurement(csvRecords, MERIS_BAND_560_IDX);
        } else if (isBandPresent(csvRecords, MODIS_BAND_547_IDX)) {
            spectralMeasurement = parseSpectralMeasurement(csvRecords, MODIS_BAND_547_IDX);
        } else if (isBandPresent(csvRecords, SEAWIFS_BAND_555_IDX)) {
            spectralMeasurement = parseSpectralMeasurement(csvRecords, SEAWIFS_BAND_555_IDX);
        }
        return spectralMeasurement;
    }

    private static SpectralMeasurement getSpectralMeasurement_670(String[] csvRecords) {
        SpectralMeasurement spectralMeasurement = null;
        if (isBandPresent(csvRecords, MERIS_BAND_665_IDX)) {
            spectralMeasurement = parseSpectralMeasurement(csvRecords, MERIS_BAND_665_IDX);
        } else if (isBandPresent(csvRecords, MODIS_BAND_667_IDX)) {
            spectralMeasurement = parseSpectralMeasurement(csvRecords, MODIS_BAND_667_IDX);
        } else if (isBandPresent(csvRecords, SEAWIFS_BAND_670_IDX)) {
            spectralMeasurement = parseSpectralMeasurement(csvRecords, SEAWIFS_BAND_670_IDX);
        }
        return spectralMeasurement;
    }

    private static SpectralMeasurement parseSpectralMeasurement(String[] csvRecords, int bandIndex) {
        final SpectralMeasurement spectralMeasurement = new SpectralMeasurement();
        spectralMeasurement.setMeasurement(Double.parseDouble(csvRecords[bandIndex]));
        spectralMeasurement.setWavelength(Double.parseDouble(csvRecords[bandIndex + 1]));
        return spectralMeasurement;
    }

    private static boolean isBandPresent(String[] csvRecords, int bandIndex) {
        return csvRecords.length > bandIndex &&
                StringUtils.isNotNullAndNotEmpty(csvRecords[bandIndex]) &&
                StringUtils.isNotNullAndNotEmpty(csvRecords[bandIndex + 1]);
    }
}
