package org.esa.beam.occci.roundrobin;

class SpectralMeasurement {

    private double wavelength;
    private double measurement;

    public void setMeasurement(double measurement) {
        this.measurement = measurement;
    }

    public void setWavelength(double wavelength) {
        this.wavelength = wavelength;
    }

    public double getMeasurement() {
        return measurement;
    }

    public double getWavelength() {
        return wavelength;
    }
}
