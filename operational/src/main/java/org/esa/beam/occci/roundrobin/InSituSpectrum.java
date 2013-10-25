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

    boolean isCompleteSeaWiFS() {
        return isCompleteSet(seaWiFSMeasurements);
    }

    double[] getQaaWavelengths() {
        if (!isCompleteQaa()) {
            throw new IllegalStateException("Incomplete spectrum");
        }

        return getWavelengthsArray(qaaMeasurements);
    }

    double[] getMerisWavelengths() {
        if (!isCompleteMeris()) {
            throw new IllegalStateException("Incomplete spectrum");
        }

        return getWavelengthsArray(merisMeasurements);
    }

    double[] getModisWavelengths() {
        if (!isCompleteModis()) {
            throw new IllegalStateException("Incomplete spectrum");
        }

        return getWavelengthsArray(modisMeasurements);
    }

    public double[] getQaaMeasurements() {
        if (!isCompleteQaa()) {
            throw new IllegalStateException("Incomplete spectrum");
        }

        return getMeasurementsArray(qaaMeasurements);
    }

    public double[] getMerisMeasurements() {
        if (!isCompleteMeris()) {
            throw new IllegalStateException("Incomplete spectrum");
        }
        return getMeasurementsArray(merisMeasurements);
    }


    float[] getMeasurementsFloat() {
        final float[] measurementsFloat = new float[NUM_QAA_VALUES];
        final double[] measurements = getQaaMeasurements();

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

    private double[] getWavelengthsArray(SpectralMeasurement[] spectralMeasurements) {
        final double[] wavelengths = new double[spectralMeasurements.length];
        for (int i = 0; i < spectralMeasurements.length; i++) {
            wavelengths[i] = spectralMeasurements[i].getWavelength();
        }
        return wavelengths;
    }

    private double[] getMeasurementsArray(SpectralMeasurement[] spectralMeasurements) {
        final double[] measurements = new double[spectralMeasurements.length];
        for (int i = 0; i < spectralMeasurements.length; i++) {
            measurements[i] = spectralMeasurements[i].getMeasurement();
        }
        return measurements;
    }
}
