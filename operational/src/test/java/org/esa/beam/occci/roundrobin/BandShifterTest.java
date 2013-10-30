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
        final InSituSpectrum modisLike = TestHelper.createModisSpectrum(wavelengths, rrs_in);

        final double[] qaaAt443 = new double[]{0.009201783686876297, 0.007969709113240242, 0.004412563983350992};
        final double[] rrs_expected = {0.012088970090059437, 0.0089211, 0.006138016198978773, 0.0032432243095825396, 0.0012443304160077934, 1.7500117089236389E-4, 1.0127766945234463E-4};

        final double[] rrs_out = BandShifter.toMeris(modisLike, qaaAt443);
        assertArrayEquals(rrs_expected, rrs_out, 1e-8);
    }

    @Test
    public void testShiftModisLikeToModis() throws IOException {
        final double[] wavelengths = {412.0, 443.0, 488.0, 531.0, 547.0, 667.0, 678.0};
        final double[] rrs_in = {0.0086757, 0.0064082, 0.0042756, 0.0013042, 9.1488E-4, 7.18E-5, 8.693E-5};
        final InSituSpectrum modisLike = TestHelper.createModisSpectrum(wavelengths, rrs_in);

        final double[] qaaAt443 = new double[]{0.007476214785128832, 0.009706169366836548, 0.003199426457285881};
        final double[] rrs_expected = {0.0086757, 0.0064082, 0.0042756, 0.0013042, 9.1488E-4, 7.18E-5, 8.693E-5};

        final double[] rrs_out = BandShifter.toModis(modisLike, qaaAt443);
        assertArrayEquals(rrs_expected, rrs_out, 1e-8);
    }

    @Test
    public void testShiftModisLikeToSeaWifs() throws IOException {
        final double[] wavelengths = {412.0, 443.0, 488.0, 531.0, 547.0, 667.0, 678.0};
        final double[] rrs_in = {0.014407, 0.010421, 0.007321, 0.0025148, 0.001784, 1.0777E-4, 1.1223E-4};
        final InSituSpectrum modisLike = TestHelper.createModisSpectrum(wavelengths, rrs_in);

        final double[] qaaAt443 = new double[]{0.009684919379651546, 0.00665450980886817, 0.0049596792086958885};
        final double[] rrs_expected = {0.014407, 0.010421, 0.007228377483014507, 0.0033374722164972843, 0.0015677037176513903, 1.0986529321888687E-4};

        final double[] rrs_out = BandShifter.toSeaWifs(modisLike, qaaAt443);
        assertArrayEquals(rrs_expected, rrs_out, 1e-8);
    }

    @Test
    public void testShiftSeaWifsLikeToMeris() throws IOException {
        final double[] wavelengths = {412.0, 443.0, 490.0, 510.0, 555.0, 678.0};
        final double[] rrs_in = {0.012131, 0.0091007, 0.0063586, 0.0031315, 0.0013309, 1.0452E-4};
        final InSituSpectrum seaWifsLike = TestHelper.createSeaWifsSpectrum(wavelengths, rrs_in);

        final double[] qaaAt443 = new double[]{0.00815083272755146, 0.008263680152595043, 0.004358689766377211};
        final double[] rrs_expected = {0.012131, 0.0091007, 0.0063586, 0.0031315, 0.0012670876381625582, 2.207558239066342E-4, 1.1585511006367807E-4};

        final double[] rrs_out = BandShifter.toMeris(seaWifsLike, qaaAt443);
        assertArrayEquals(rrs_expected, rrs_out, 1e-8);
    }

    @Test
    public void testShiftSeaWifsLikeToModis() throws IOException {
        final double[] wavelengths = {412.0, 443.0, 490.0, 510.0, 555.0, 670.0};
        final double[] rrs_in = {0.0031088, 0.0033552, 0.0042644, 0.0041139, 0.0035073, 9.21E-4};
        final InSituSpectrum seaWifsLike = TestHelper.createSeaWifsSpectrum(wavelengths, rrs_in);

        final double[] qaaAt443 = new double[]{0.06231730431318283,0.08289932459592819,0.010701252147555351};
        final double[] rrs_expected = {0.0031088, 0.0033552, 0.004230027480178097, 0.0041606818645627056, 0.003740453363936862, 9.409676557825591E-4, 8.648647512429436E-4};

        final double[] rrs_out = BandShifter.toModis(seaWifsLike, qaaAt443);
        assertArrayEquals(rrs_expected, rrs_out, 1e-8);
    }

    @Test
    public void testShiftSeaWifsLikeToSeaWifs() throws IOException {
        final double[] wavelengths = {412.0, 443.0, 490.0, 510.0, 555.0, 670.0};
        final double[] rrs_in = {0.0046052, 0.00438, 0.0040955, 0.0028885, 0.0016933, 1.9945E-4};
        final InSituSpectrum seaWifsLike = TestHelper.createSeaWifsSpectrum(wavelengths, rrs_in);

        final double[] qaaAt443 = new double[]{0.01727110706269741,0.033406905829906464,0.005261986516416073};
        final double[] rrs_expected = {0.0046052, 0.00438, 0.0040955, 0.0028885, 0.0016933, 1.9945E-4};

        final double[] rrs_out = BandShifter.toSeaWifs(seaWifsLike, qaaAt443);
        assertArrayEquals(rrs_expected, rrs_out, 1e-8);
    }
}
