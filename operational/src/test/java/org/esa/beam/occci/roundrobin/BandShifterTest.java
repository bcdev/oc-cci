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
        final double[] rrs_expected = {0.013781359096269702, 0.010255, 0.0065454, 0.0034769, 0.001357, 1.3942E-4, 4.9656E-5};

        final double[] rrs_out = BandShifter.toMeris(merisLike, qaaAt443);
        assertArrayEquals(rrs_expected, rrs_out, 1e-8);
    }

    @Test
    public void testShiftMerisLikeToModis() throws IOException {
        final double[] wavelengths = {412.0, 443.0, 490.0, 510.0, 560.0, 620.0, 665.0};
        final double[] rrs_in = {0.01241, 0.010061, 0.006738, 0.0036552, 0.0014477, 1.3783E-4, 4.7428E-5};
        final InSituSpectrum merisLike = TestHelper.createMerisSpectrum(wavelengths, rrs_in);

        final double[] qaaAt443 = new double[]{0.006321068853139877, 0.010088754817843437, 0.004806386306881905};
        final double[] rrs_expected = {0.01241, 0.010061, 0.006850902394240231, 0.002475536609868352, 0.001765366076261974, 4.6442624365916906E-5, 4.20358898002875E-5};

        final double[] rrs_out = BandShifter.toModis(merisLike, qaaAt443);
        assertArrayEquals(rrs_expected, rrs_out, 1e-8);
    }

    @Test
    public void testShiftMerisLikeToSeaWifs() throws IOException {
        final double[] wavelengths = {412.0, 443.0, 490.0, 510.0, 560.0, 620.0, 665.0};
        final double[] rrs_in = {0.012419, 0.0094287, 0.0059378, 0.0029889, 0.0011236, 8.467E-5, 2.9285E-5};
        final InSituSpectrum merisLike = TestHelper.createMerisSpectrum(wavelengths, rrs_in);

        final double[] qaaAt443 = new double[]{0.006127410102635622, 0.008217282593250275, 0.004114268347620964};
        final double[] rrs_expected = {0.012419, 0.0094287, 0.0059378, 0.0029889, 0.0011902769618527184, 2.808981822131411E-5};

        final double[] rrs_out = BandShifter.toSeaWifs(merisLike, qaaAt443);
        assertArrayEquals(rrs_expected, rrs_out, 1e-8);
    }

    @Test
    public void testShiftModisLikeToMeris() throws IOException {
        final double[] wavelengths = {412.0, 443.0, 488.0, 531.0, 547.0, 667.0, 678.0};
        final double[] rrs_in = {0.012084, 0.0089211, 0.0062153, 0.0021173, 0.0014871, 9.943E-5, 1.0206E-4};
        final InSituSpectrum modisLike = TestHelper.createModisSpectrum(wavelengths, rrs_in);

        final double[] qaaAt443 = new double[]{0.009201783686876297, 0.007969709113240242, 0.004412563983350992};
        final double[] rrs_expected = {0.012064532071565038, 0.0089211, 0.006118134625748988, 0.003231064436954976, 0.0012200792228360658, 1.8732370890246213E-4, 1.0156656212109172E-4};

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
        final double[] rrs_expected = {0.014407, 0.010421, 0.007204518178319516, 0.003450295886468677, 0.0015485618757527825, 1.0990886071176424E-4};

        final double[] rrs_out = BandShifter.toSeaWifs(modisLike, qaaAt443);
        assertArrayEquals(rrs_expected, rrs_out, 1e-8);
    }

    @Test
    public void testShiftSeaWifsLikeToMeris() throws IOException {
        final double[] wavelengths = {412.0, 443.0, 490.0, 510.0, 555.0, 678.0};
        final double[] rrs_in = {0.012131, 0.0091007, 0.0063586, 0.0031315, 0.0013309, 1.0452E-4};
        final InSituSpectrum seaWifsLike = TestHelper.createSeaWifsSpectrum(wavelengths, rrs_in);

        final double[] qaaAt443 = new double[]{0.00815083272755146, 0.008263680152595043, 0.004358689766377211};
        final double[] rrs_expected = {0.012131, 0.0091007, 0.0063586, 0.0031315, 0.0012574156782648143, 2.1963012595660692E-4, 1.1802303007965027E-4};

        final double[] rrs_out = BandShifter.toMeris(seaWifsLike, qaaAt443);
        assertArrayEquals(rrs_expected, rrs_out, 1e-8);
    }

    @Test
    public void testShiftSeaWifsLikeToModis() throws IOException {
        final double[] wavelengths = {412.0, 443.0, 490.0, 510.0, 555.0, 670.0};
        final double[] rrs_in = {0.0031088, 0.0033552, 0.0042644, 0.0041139, 0.0035073, 9.21E-4};
        final InSituSpectrum seaWifsLike = TestHelper.createSeaWifsSpectrum(wavelengths, rrs_in);

        final double[] qaaAt443 = new double[]{0.06231730431318283, 0.08289932459592819, 0.010701252147555351};
        final double[] rrs_expected = {0.0031088, 0.0033552, 0.0042293292572847824, 0.004167003119984167, 0.0037365155702517435, 9.408043343679408E-4, 8.652618574031671E-4};

        final double[] rrs_out = BandShifter.toModis(seaWifsLike, qaaAt443);
        assertArrayEquals(rrs_expected, rrs_out, 1e-8);
    }

    @Test
    public void testShiftSeaWifsLikeToSeaWifs() throws IOException {
        final double[] wavelengths = {412.0, 443.0, 490.0, 510.0, 555.0, 670.0};
        final double[] rrs_in = {0.0046052, 0.00438, 0.0040955, 0.0028885, 0.0016933, 1.9945E-4};
        final InSituSpectrum seaWifsLike = TestHelper.createSeaWifsSpectrum(wavelengths, rrs_in);

        final double[] qaaAt443 = new double[]{0.01727110706269741, 0.033406905829906464, 0.005261986516416073};
        final double[] rrs_expected = {0.0046052, 0.00438, 0.0040955, 0.0028885, 0.0016933, 1.9945E-4};

        final double[] rrs_out = BandShifter.toSeaWifs(seaWifsLike, qaaAt443);
        assertArrayEquals(rrs_expected, rrs_out, 1e-8);
    }

    @Test
    public void testShiftQaaToMeris() throws IOException {
        final double[] wavelengths = {412.5, 442.0, 490.5, 530.4, 551.1, 667.7};
        final double[] rrs_in = {0.0053467, 0.0058061, 0.0069733, 0.007243, 0.0064503, 0.001346};
        final InSituSpectrum qaaSpectrum = TestHelper.createQaaSpectrum(wavelengths, rrs_in);

        final double[] qaaAt443 = new double[]{0.06498473137617111, 0.07231264561414719, 0.017275825142860413};
        final double[] rrs_expected = {0.0053420657599614315, 0.005864469735430617, 0.0069586740477587065, 0.007154225877223405, 0.003988695714859882, 0.002349575526862405, 0.0013777214319676155};

        final double[] rrs_out = BandShifter.toMeris(qaaSpectrum, qaaAt443);
        assertArrayEquals(rrs_expected, rrs_out, 1e-8);
    }

    @Test
    public void testShiftQaaToModis() throws IOException {
        final double[] wavelengths = {412.5, 442.0, 490.5, 530.4, 551.1, 667.7};
        final double[] rrs_in = {0.004824, 0.0052236, 0.0079236, 0.0061406, 0.0063258, 0.0010136};
        final InSituSpectrum qaaSpectrum = TestHelper.createQaaSpectrum(wavelengths, rrs_in);

        final double[] qaaAt443 = new double[]{0.06653127074241638, 0.07217124104499817, 0.015740450471639633};
        final double[] rrs_expected = {0.004801636118639405, 0.005277140523302186, 0.007845325922832982, 0.006127979097420359, 0.00658289489176213, 0.001014448004988037, 9.351359654863559E-4};

        final double[] rrs_out = BandShifter.toModis(qaaSpectrum, qaaAt443);
        assertArrayEquals(rrs_expected, rrs_out, 1e-8);
    }

    @Test
    public void testShiftQaaToSeaWifs() throws IOException {
        final double[] wavelengths = {412.5, 442.0, 490.5, 530.4, 551.1, 667.7};
        final double[] rrs_in = {0.0042168, 0.0055431, 0.0073847, 0.00684, 0.0058682, 8.1508E-4};
        final InSituSpectrum qaaSpectrum = TestHelper.createQaaSpectrum(wavelengths, rrs_in);

        final double[] qaaAt443 = new double[]{0.010684970766305923, 0.1092723086476326, 0.014527889899909496};
        final double[] rrs_expected = {0.004187365725482156, 0.0055977183180414255, 0.00734815935139117, 0.0070130688125838525, 0.005782819873706516, 8.040323633408551E-4};

        final double[] rrs_out = BandShifter.toSeaWifs(qaaSpectrum, qaaAt443);
        assertArrayEquals(rrs_expected, rrs_out, 1e-8);
    }
}
