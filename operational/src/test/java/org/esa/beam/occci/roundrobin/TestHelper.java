package org.esa.beam.occci.roundrobin;

public class TestHelper {
    static InSituSpectrum createMerisSpectrum(double[] wavelengths, double[] rrs_in) {
        final InSituSpectrum spectrum = new InSituSpectrum();
        for (int i = 0; i < wavelengths.length; i++) {
            final SpectralMeasurement measurement = new SpectralMeasurement();
            measurement.setWavelength(wavelengths[i]);
            measurement.setMeasurement(rrs_in[i]);
            spectrum.setMerisSpectralValue(measurement, i);
        }
        return spectrum;
    }
}
