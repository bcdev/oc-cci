package org.esa.beam.occci.roundrobin;

public class TestHelper {

    static InSituSpectrum createMerisSpectrum(double[] wavelengths, double[] rrs_in) {
        final InSituSpectrum spectrum = new InSituSpectrum();
        for (int i = 0; i < wavelengths.length; i++) {
            final SpectralMeasurement measurement = createSpectralMeasurement(wavelengths[i], rrs_in[i]);
            spectrum.setMerisSpectralValue(measurement, i);
        }
        return spectrum;
    }

    static InSituSpectrum createModisSpectrum(double[] wavelengths, double[] rrs_in) {
        final InSituSpectrum spectrum = new InSituSpectrum();
        for (int i = 0; i < wavelengths.length; i++) {
            final SpectralMeasurement measurement = createSpectralMeasurement(wavelengths[i], rrs_in[i]);
            spectrum.setModisSpectralValue(measurement, i);
        }
        return spectrum;
    }

    static InSituSpectrum createSeaWifsSpectrum(double[] wavelengths, double[] rrs_in) {
        final InSituSpectrum spectrum = new InSituSpectrum();
        for (int i = 0; i < wavelengths.length; i++) {
            final SpectralMeasurement measurement = createSpectralMeasurement(wavelengths[i], rrs_in[i]);
            spectrum.setSeaWiFSSpectralValue(measurement, i);
        }
        return spectrum;
    }

    static InSituSpectrum createQaaSpectrum(double[] wavelengths, double[] rrs_in) {
        final InSituSpectrum spectrum = new InSituSpectrum();
        for (int i = 0; i < wavelengths.length; i++) {
            final SpectralMeasurement measurement = createSpectralMeasurement(wavelengths[i], rrs_in[i]);
            spectrum.setQaaSpectralValue(measurement, i);
        }
        return spectrum;
    }

    private static SpectralMeasurement createSpectralMeasurement(double wavelength, double measurement) {
        final SpectralMeasurement spectralMeasurement = new SpectralMeasurement();
        spectralMeasurement.setWavelength(wavelength);
        spectralMeasurement.setMeasurement(measurement);
        return spectralMeasurement;
    }
}
