package org.esa.beam.occci.qaa;

public interface SensorConfig {
    double[] getAwCoefficients();

    double getReferenceWavelength();

    double[] getWavelengths();

    double[] getSpecificAbsorptions();

    double[] getSpecficBackscatters();
}
