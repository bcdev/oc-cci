package org.esa.beam.occci.roundrobin;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;

public class BandShifterTest {

    @Test
    public void testShiftMerisLikeToMeris() throws IOException {
        final double[] wavelengths = {412.0, 443.0, 490.0, 510.0, 560.0, 620.0, 665.0};
        final double[] rrs_in = {0.013793, 0.010255, 0.0065454, 0.0034769, 0.001357, 1.3942E-4, 4.9656E-5};
        final InSituSpectrum merisLike = TestHelper.createMerisSpectrum(wavelengths, rrs_in);

        final double[] qaaAt443 = new double[]{0.007660664152354002, 0.007377655711025, 0.004610979929566383};
        final double[] rrs_expected = {0.013810073167320214, 0.010255, 0.0065454, 0.0034769, 0.001357, 1.3942E-4, 4.9656E-5};

        final double[] rrs_out = BandShifter.toMeris(merisLike, qaaAt443);
        assertArrayEquals(rrs_expected, rrs_out, 1e-8);
    }

    @Test
    public void testShiftMerisLikeToModis() throws IOException {
        final double[] wavelengths = {412.0, 443.0, 490.0, 510.0, 560.0, 620.0, 665.0};
        final double[] rrs_in = {0.01241, 0.010061, 0.006738, 0.0036552, 0.0014477, 1.3783E-4, 4.7428E-5};
        final InSituSpectrum merisLike = TestHelper.createMerisSpectrum(wavelengths, rrs_in);

        final double[] qaaAt443 = new double[]{0.006321068853139877, 0.010088754817843437, 0.004806386306881905};
        final double[] rrs_expected = {0.01241, 0.010061, 0.006828090360701465, 0.0024855016228982046, 0.001730657976463807, 4.657613078459093E-5, 4.282420656035962E-5};

        final double[] rrs_out = BandShifter.toModis(merisLike, qaaAt443);
        assertArrayEquals(rrs_expected, rrs_out, 1e-8);
    }

    @Test
    public void testShiftMerisLikeToSeaWifs() throws IOException {
        final double[] wavelengths = {412.0, 443.0, 490.0, 510.0, 560.0, 620.0, 665.0};
        final double[] rrs_in = {0.012419, 0.0094287, 0.0059378, 0.0029889, 0.0011236, 8.467E-5, 2.9285E-5};
        final InSituSpectrum merisLike = TestHelper.createMerisSpectrum(wavelengths, rrs_in);

        final double[] qaaAt443 = new double[]{0.006127410102635622, 0.008217282593250275, 0.004114268347620964};
        final double[] rrs_expected = {0.012419, 0.0094287, 0.0059378, 0.0029889, 0.0011812400518793071, 2.829220181996939E-5};

        final double[] rrs_out = BandShifter.toSeaWifs(merisLike, qaaAt443);
        assertArrayEquals(rrs_expected, rrs_out, 1e-8);
    }

    @Test
    public void testShiftModisLikeToMeris() throws IOException {
        final double[] wavelengths = {412.0, 443.0, 488.0, 531.0, 547.0, 667.0, 678.0};
        final double[] rrs_in = {0.012084, 0.0089211, 0.0062153, 0.0021173, 0.0014871, 9.943E-5, 1.0206E-4};
        final InSituSpectrum merisLike = TestHelper.createMerisSpectrum(wavelengths, rrs_in);

        final double[] qaaAt443 = new double[]{0.009201783686876297, 0.007969709113240242, 0.004412563983350992};
        final double[] rrs_expected = {0.012088970090059437, 0.0089211, 0.006138016198978773, 0.0028114008634353926, 0.0012443304160077934, 1.4731044498197948E-4, 1.1313891087980301E-4};

        final double[] rrs_out = BandShifter.toMeris(merisLike, qaaAt443);
        assertArrayEquals(rrs_expected, rrs_out, 1e-8);
    }

    @Test
    public void testShiftModisLikeToModis() throws IOException {
        final double[] wavelengths = {412.0, 443.0, 488.0, 531.0, 547.0, 667.0, 678.0};
        final double[] rrs_in = {0.0086757, 0.0064082, 0.0042756, 0.0013042, 9.1488E-4, 7.18E-5, 8.693E-5};
        final InSituSpectrum merisLike = TestHelper.createModisSpectrum(wavelengths, rrs_in);

        final double[] qaaAt443 = new double[]{0.007476214785128832, 0.009706169366836548, 0.003199426457285881};
        final double[] rrs_expected = {0.0086757, 0.0064082, 0.0042756, 0.0013042, 9.1488E-4, 7.18E-5, 8.693E-5};

        final double[] rrs_out = BandShifter.toModis(merisLike, qaaAt443);
        assertArrayEquals(rrs_expected, rrs_out, 1e-8);
    }
}
