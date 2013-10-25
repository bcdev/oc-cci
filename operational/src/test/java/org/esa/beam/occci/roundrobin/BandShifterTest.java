package org.esa.beam.occci.roundrobin;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;

public class BandShifterTest {

    @Test
    public void testShiftMerisLikeToMeris() throws IOException {
        final double[] wavelengths = {412.0, 443.0, 490.0, 510.0, 560.0, 620.0, 665.0};
        final double[] rrs_in = {0.013793, 0.010255, 0.0065454, 0.0034769, 0.001357, 1.3942E-4, 4.9656E-5};
        final InSituSpectrum merisLike = createMerisSpectrum(wavelengths, rrs_in);

        final double[] qaaAt443 = new double[]{0.007660664152354002, 0.007377655711025, 0.004610979929566383};
        final double[] rrs_expected = {0.013810073167320214, 0.010255, 0.0065454, 0.0034769, 0.001357, 1.3942E-4, 4.9656E-5};

        final double[] rrs_out = BandShifter.toMeris(merisLike, qaaAt443);
        assertArrayEquals(rrs_expected, rrs_out, 1e-8);
    }

    private InSituSpectrum createMerisSpectrum(double[] wavelengths, double[] rrs_in) {
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
