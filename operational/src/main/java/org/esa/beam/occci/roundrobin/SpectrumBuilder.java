package org.esa.beam.occci.roundrobin;

import org.esa.beam.util.StringUtils;

class SpectrumBuilder {

    private static final int MERIS_BAND_413_IDX = 3;
    private static final int MERIS_BAND_443_IDX = 5;
    private static final int MERIS_BAND_490_IDX = 7;
    private static final int MERIS_BAND_510_IDX = 9;
    private static final int MERIS_BAND_560_IDX = 11;
    private static final int MERIS_BAND_665_IDX = 15;

    private static final int MODIS_BAND_412_IDX = 21;
    private static final int MODIS_BAND_443_IDX = 23;
    private static final int MODIS_BAND_488_IDX = 25;
    private static final int MODIS_BAND_531_IDX = 27;
    private static final int MODIS_BAND_547_IDX = 29;
    private static final int MODIS_BAND_667_IDX = 31;

    private static final int SEAWIFS_BAND_412_IDX = 35;
    private static final int SEAWIFS_BAND_443_IDX = 37;
    private static final int SEAWIFS_BAND_490_IDX = 39;
    private static final int SEAWIFS_BAND_510_IDX = 41;
    private static final int SEAWIFS_BAND_555_IDX = 43;
    private static final int SEAWIFS_BAND_670_IDX = 45;

    public static InSituSpectrum create(String[] csvRecord) {
        final InSituSpectrum spectrum = new InSituSpectrum();

        SpectralMeasurement spectralMeasurement = getSpectralMeasurement_412(csvRecord);
        spectrum.setSpectralValue(spectralMeasurement, 0);

        spectralMeasurement = getSpectralMeasurement_443(csvRecord);
        spectrum.setSpectralValue(spectralMeasurement, 1);

        spectralMeasurement = getSpectralMeasurement_490(csvRecord);
        spectrum.setSpectralValue(spectralMeasurement, 2);

        spectralMeasurement = getSpectralMeasurement_510(csvRecord);
        spectrum.setSpectralValue(spectralMeasurement, 3);

        spectralMeasurement = getSpectralMeasurement_555(csvRecord);
        spectrum.setSpectralValue(spectralMeasurement, 4);

        spectralMeasurement = getSpectralMeasurement_670(csvRecord);
        spectrum.setSpectralValue(spectralMeasurement, 5);
        return spectrum;
    }

    private static SpectralMeasurement getSpectralMeasurement_412(String[] csvRecord) {
        SpectralMeasurement spectralMeasurement = null;
        if (isBandPresent(csvRecord, MERIS_BAND_413_IDX)) {
            spectralMeasurement = parseSpectralMeasurement(csvRecord, MERIS_BAND_413_IDX);
        } else if (isBandPresent(csvRecord, MODIS_BAND_412_IDX)) {
            spectralMeasurement = parseSpectralMeasurement(csvRecord, MODIS_BAND_412_IDX);
        } else if (isBandPresent(csvRecord, SEAWIFS_BAND_412_IDX)) {
            spectralMeasurement = parseSpectralMeasurement(csvRecord, SEAWIFS_BAND_412_IDX);
        }
        return spectralMeasurement;
    }

    private static SpectralMeasurement getSpectralMeasurement_443(String[] csvRecord) {
        SpectralMeasurement spectralMeasurement = null;
        if (isBandPresent(csvRecord, MERIS_BAND_443_IDX)) {
            spectralMeasurement = parseSpectralMeasurement(csvRecord, MERIS_BAND_443_IDX);
        } else if (isBandPresent(csvRecord, MODIS_BAND_443_IDX)) {
            spectralMeasurement = parseSpectralMeasurement(csvRecord, MODIS_BAND_443_IDX);
        } else if (isBandPresent(csvRecord, SEAWIFS_BAND_443_IDX)) {
            spectralMeasurement = parseSpectralMeasurement(csvRecord, SEAWIFS_BAND_443_IDX);
        }
        return spectralMeasurement;
    }

    private static SpectralMeasurement getSpectralMeasurement_490(String[] csvRecord) {
        SpectralMeasurement spectralMeasurement = null;
        if (isBandPresent(csvRecord, MERIS_BAND_490_IDX)) {
            spectralMeasurement = parseSpectralMeasurement(csvRecord, MERIS_BAND_490_IDX);
        } else if (isBandPresent(csvRecord, MODIS_BAND_488_IDX)) {
            spectralMeasurement = parseSpectralMeasurement(csvRecord, MODIS_BAND_488_IDX);
        } else if (isBandPresent(csvRecord, SEAWIFS_BAND_490_IDX)) {
            spectralMeasurement = parseSpectralMeasurement(csvRecord, SEAWIFS_BAND_490_IDX);
        }
        return spectralMeasurement;
    }

    private static SpectralMeasurement getSpectralMeasurement_510(String[] csvRecord) {
        SpectralMeasurement spectralMeasurement = null;
        if (isBandPresent(csvRecord, MERIS_BAND_510_IDX)) {
            spectralMeasurement = parseSpectralMeasurement(csvRecord, MERIS_BAND_510_IDX);
        } else if (isBandPresent(csvRecord, MODIS_BAND_531_IDX)) {
            spectralMeasurement = parseSpectralMeasurement(csvRecord, MODIS_BAND_531_IDX);
        } else if (isBandPresent(csvRecord, SEAWIFS_BAND_510_IDX)) {
            spectralMeasurement = parseSpectralMeasurement(csvRecord, SEAWIFS_BAND_510_IDX);
        }
        return spectralMeasurement;
    }

    private static SpectralMeasurement getSpectralMeasurement_555(String[] csvRecord) {
        SpectralMeasurement spectralMeasurement = null;
        if (isBandPresent(csvRecord, MERIS_BAND_560_IDX)) {
            spectralMeasurement = parseSpectralMeasurement(csvRecord, MERIS_BAND_560_IDX);
        } else if (isBandPresent(csvRecord, MODIS_BAND_547_IDX)) {
            spectralMeasurement = parseSpectralMeasurement(csvRecord, MODIS_BAND_547_IDX);
        } else if (isBandPresent(csvRecord, SEAWIFS_BAND_555_IDX)) {
            spectralMeasurement = parseSpectralMeasurement(csvRecord, SEAWIFS_BAND_555_IDX);
        }
        return spectralMeasurement;
    }

    private static SpectralMeasurement getSpectralMeasurement_670(String[] csvRecord) {
        SpectralMeasurement spectralMeasurement = null;
        if (isBandPresent(csvRecord, MERIS_BAND_665_IDX)) {
            spectralMeasurement = parseSpectralMeasurement(csvRecord, MERIS_BAND_665_IDX);
        } else if (isBandPresent(csvRecord, MODIS_BAND_667_IDX)) {
            spectralMeasurement = parseSpectralMeasurement(csvRecord, MODIS_BAND_667_IDX);
        } else if (isBandPresent(csvRecord, SEAWIFS_BAND_670_IDX)) {
            spectralMeasurement = parseSpectralMeasurement(csvRecord, SEAWIFS_BAND_670_IDX);
        }
        return spectralMeasurement;
    }

    private static SpectralMeasurement parseSpectralMeasurement(String[] csvRecord, int bandIndex) {
        final SpectralMeasurement spectralMeasurement = new SpectralMeasurement();
        spectralMeasurement.setMeasurement(Double.parseDouble(csvRecord[bandIndex]));
        spectralMeasurement.setWavelength(Double.parseDouble(csvRecord[bandIndex + 1]));
        return spectralMeasurement;
    }

    private static boolean isBandPresent(String[] csvRecord, int bandIndex) {
        return csvRecord.length > bandIndex &&
                StringUtils.isNotNullAndNotEmpty(csvRecord[bandIndex]) &&
                StringUtils.isNotNullAndNotEmpty(csvRecord[bandIndex + 1]);
    }
}
