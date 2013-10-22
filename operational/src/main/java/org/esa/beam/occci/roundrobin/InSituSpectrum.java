package org.esa.beam.occci.roundrobin;

class InSituSpectrum {

    private static final int NUM_SPECTRAL_VALUES = 6;

    private SpectralMeasurement[] spectralMeasurements;
    private String dateTime;
    private String lat;
    private String lon;

    InSituSpectrum() {
        spectralMeasurements = new SpectralMeasurement[NUM_SPECTRAL_VALUES];
    }

    SpectralMeasurement getSpectralValue(int index) {
        if (index < 0 || index >= NUM_SPECTRAL_VALUES) {
            throw new IllegalArgumentException();
        }
        return spectralMeasurements[index];
    }

    void setSpectralValue(SpectralMeasurement spectralMeasurement, int index) {
        if (index < 0 || index >= NUM_SPECTRAL_VALUES) {
            throw new IllegalArgumentException();
        }
        spectralMeasurements[index] = spectralMeasurement;
    }

    boolean isComplete() {
        for (SpectralMeasurement spectralMeasurement : spectralMeasurements) {
            if (spectralMeasurement == null) {
                return false;
            }
        }

        return true;
    }

    double[] getWavelengths() {
        if (!isComplete()) {
            throw new IllegalStateException("Incomplete spectrum");
        }

        final double[] wavelengths = new double[NUM_SPECTRAL_VALUES];
        for (int i = 0; i < NUM_SPECTRAL_VALUES; i++) {
            wavelengths[i] = spectralMeasurements[i].getWavelength();
        }
        return wavelengths;
    }

    public double[] getMeasurements() {
        if (!isComplete()) {
            throw new IllegalStateException("Incomplete spectrum");
        }

        final double[] measurements = new double[NUM_SPECTRAL_VALUES];
        for (int i = 0; i < NUM_SPECTRAL_VALUES; i++) {
            measurements[i] = spectralMeasurements[i].getMeasurement();
        }
        return measurements;
    }

    float[] getMeasurementsFloat() {
        final float[] measurementsFloat = new float[NUM_SPECTRAL_VALUES];
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

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }
}
