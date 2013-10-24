package org.esa.beam.occci.roundrobin;

class InSituSpectrum {

    private static final int NUM_QAA_VALUES = 6;
    private static final int NUM_MERIS_VALUES = 7;
    private static final int NUM_MODIS_VALUES = 7;
    private static final int NUM_SEAWIFS_VALUES = 6;

    private SpectralMeasurement[] qaaMeasurements;
    private SpectralMeasurement[] merisMeasurements;
    private SpectralMeasurement[] modisMeasurements;
    private SpectralMeasurement[] seaWiFSMeasurements;
    private String dateTime;
    private String lat;
    private String lon;

    InSituSpectrum() {
        qaaMeasurements = new SpectralMeasurement[NUM_QAA_VALUES];
        merisMeasurements = new SpectralMeasurement[NUM_MERIS_VALUES];
        modisMeasurements = new SpectralMeasurement[NUM_MODIS_VALUES];
        seaWiFSMeasurements = new SpectralMeasurement[NUM_SEAWIFS_VALUES];
    }

    SpectralMeasurement getQaaSpectralValue(int index) {
        checkValidQaaIndex(index);
        return qaaMeasurements[index];
    }

    void setQaaSpectralValue(SpectralMeasurement spectralMeasurement, int index) {
        checkValidQaaIndex(index);
        qaaMeasurements[index] = spectralMeasurement;
    }

    void setMerisSpectralValue(SpectralMeasurement spectralMeasurement, int index) {
        checkValidMerisIndex(index);
        merisMeasurements[index] = spectralMeasurement;
    }

    SpectralMeasurement getMerisSpectralValue(int index) {
        checkValidMerisIndex(index);
        return merisMeasurements[index];
    }

    void setModisSpectralValue(SpectralMeasurement spectralMeasurement, int index) {
        checkValidModisIndex(index);
        modisMeasurements[index] = spectralMeasurement;
    }

    SpectralMeasurement getModisSpectralValue(int index) {
        checkValidModisIndex(index);
        return modisMeasurements[index];
    }

    void setSeaWiFSSpectralValue(SpectralMeasurement spectralMeasurement, int index) {
        checkValidSeaWiFSIndex(index);
        seaWiFSMeasurements[index] = spectralMeasurement;
    }

    SpectralMeasurement getSeaWiFSSpectralValue(int index) {
        checkValidSeaWiFSIndex(index);
        return seaWiFSMeasurements[index];
    }

    boolean isCompleteQaa() {
        return isCompleteSet(qaaMeasurements);
    }

    boolean isCompleteMeris() {
        return isCompleteSet(merisMeasurements);
    }

    boolean isCompleteModis() {
        return isCompleteSet(modisMeasurements);
    }

    double[] getWavelengths() {
        if (!isCompleteQaa()) {
            throw new IllegalStateException("Incomplete spectrum");
        }

        final double[] wavelengths = new double[NUM_QAA_VALUES];
        for (int i = 0; i < NUM_QAA_VALUES; i++) {
            wavelengths[i] = qaaMeasurements[i].getWavelength();
        }
        return wavelengths;
    }

    public double[] getMeasurements() {
        if (!isCompleteQaa()) {
            throw new IllegalStateException("Incomplete spectrum");
        }

        final double[] measurements = new double[NUM_QAA_VALUES];
        for (int i = 0; i < NUM_QAA_VALUES; i++) {
            measurements[i] = qaaMeasurements[i].getMeasurement();
        }
        return measurements;
    }

    float[] getMeasurementsFloat() {
        final float[] measurementsFloat = new float[NUM_QAA_VALUES];
        final double[] measurements = getMeasurements();

        for (int i = 0; i < measurements.length; i++) {
            measurementsFloat[i] = (float) measurements[i];
        }
        return measurementsFloat;
    }

    String getDateTime() {
        return dateTime;
    }

    void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    String getLat() {
        return lat;
    }

    void setLat(String lat) {
        this.lat = lat;
    }

    String getLon() {
        return lon;
    }

    void setLon(String lon) {
        this.lon = lon;
    }

    private void checkValidQaaIndex(int index) {
        if (index < 0 || index >= NUM_QAA_VALUES) {
            throw new IllegalArgumentException();
        }
    }

    private void checkValidMerisIndex(int index) {
        if (index < 0 || index >= NUM_MERIS_VALUES) {
            throw new IllegalArgumentException();
        }
    }

    private void checkValidModisIndex(int index) {
        if (index < 0 || index >= NUM_MODIS_VALUES) {
            throw new IllegalArgumentException();
        }
    }

    private void checkValidSeaWiFSIndex(int index) {
        if (index < 0 || index >= NUM_SEAWIFS_VALUES) {
            throw new IllegalArgumentException();
        }
    }

    private boolean isCompleteSet(SpectralMeasurement[] spectralMeasurements) {
        for (SpectralMeasurement spectralMeasurement : spectralMeasurements) {
            if (spectralMeasurement == null) {
                return false;
            }
        }

        return true;
    }
}
