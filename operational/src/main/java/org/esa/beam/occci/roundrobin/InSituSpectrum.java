package org.esa.beam.occci.roundrobin;

class InSituSpectrum {

    private SpectralMeasurement[] spectralMeasurements;

    InSituSpectrum() {
        spectralMeasurements = new SpectralMeasurement[6];
    }

    SpectralMeasurement getSpectralValue(int index) {
        if (index < 0 || index > 5) {
            throw new IllegalArgumentException();
        }
        return spectralMeasurements[index];
    }

    public void setSpectralValue(SpectralMeasurement spectralMeasurement, int index) {
        if (index < 0 || index > 5) {
            throw new IllegalArgumentException();
        }
        spectralMeasurements[index] = spectralMeasurement;
    }

    public boolean isComplete() {
        for (SpectralMeasurement spectralMeasurement : spectralMeasurements) {
            if (spectralMeasurement == null) {
                return false;
            }
        }

        return true;
    }
}
