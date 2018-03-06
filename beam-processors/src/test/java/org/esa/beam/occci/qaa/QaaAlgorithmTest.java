package org.esa.beam.occci.qaa;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class QaaAlgorithmTest {

    @Test
    public void testProcess_Meris()  {
        final float[] rrs_in = {0.030262154f, 0.031086152f, 0.022717977f, 0.013177891f, 0.0072450927f, 0.0028870495f, 0.0024475828f};

        final QaaAlgorithm algorithm = new QaaAlgorithm(new MerisConfig());

        final QaaResult result = algorithm.process(rrs_in, null);
        final float[] atot = result.getAtot();
        assertEquals(0.0326309, atot[0], 1e-6);
        assertEquals(0.0264908, atot[1], 1e-6);
        assertEquals(0.0289172, atot[2], 1e-6);
        assertEquals(0.0455736, atot[3], 1e-6);
        assertEquals(0.0657829, atot[4], 1e-6);

        final float[] bbp = result.getBbp();
        assertEquals(0.014772, bbp[0], 1e-6);
        assertEquals(0.0128989, bbp[1], 1e-6);
        assertEquals(0.0106138, bbp[2], 1e-6);
        assertEquals(0.0098237, bbp[3], 1e-6);
        assertEquals(0.00819854, bbp[4], 1e-6);

        final float[] aph = result.getAph();
        assertEquals(0.00263956, aph[0], 1e-6);
        assertEquals(0.00337634, aph[1], 1e-6);
        assertEquals(0.00614989, aph[2], 1e-6);

        final float[] adg = result.getAdg();
        assertEquals(0.0254953, adg[0], 1e-6);
        assertEquals(0.0160453, adg[1], 1e-6);
        assertEquals(0.0077673, adg[2], 1e-6);
    }

    @Test
    public void testProcess_Modis() {
        final float[] rrs_in = {0.0019080009f, 0.0029860009f, 0.0029160008f, 0.0030800009f, 0.0029520008f, 0.0011980009f};

        final QaaAlgorithm algorithm = new QaaAlgorithm(new ModisConfig());

        final QaaResult result = algorithm.process(rrs_in, null);
        final float[] atot = result.getAtot();
        assertEquals(0.326133, atot[0], 1e-6);
        assertEquals(0.177058, atot[1], 1e-6);
        assertEquals(0.147228, atot[2], 1e-6);
        assertEquals(0.118495, atot[3], 1e-6);
        assertEquals(0.117016, atot[4], 1e-6);

        final float[] bbp = result.getBbp();
        assertEquals(0.007390, bbp[0], 1e-6);
        assertEquals(0.0068567605, bbp[1], 1e-6);
        assertEquals(0.0062039173, bbp[2], 1e-6);
        assertEquals(0.005685087, bbp[3], 1e-6);
        assertEquals(0.0055131954, bbp[4], 1e-6);

        final float[] aph = result.getAph();
        assertEquals(-0.0426686, aph[0], 1e-6);
        assertEquals(-0.0501741, aph[1], 1e-6);
        assertEquals(0.0267035, aph[2], 1e-6);

        final float[] adg = result.getAdg();
        assertEquals(0.364251, adg[0], 1e-6);
        assertEquals(0.220163, adg[1], 1e-6);
        assertEquals(0.106008, adg[2], 1e-6);
    }

    @Test
    public void testProcess_SeaWiFS() {
        final float[] rrs_in = {0.00167972470255084f, 0.00186919071018569f, 0.0027188008445359f, 0.00309262196610828f, 0.00406382197640373f, 0.00120514585009823f};

        final QaaAlgorithm algorithm = new QaaAlgorithm(new SeaWifsConfig());

        final QaaResult result = algorithm.process(rrs_in, null);
        final float[] atot = result.getAtot();
        assertEquals(0.585938, atot[0], 1e-6);
        assertEquals(0.477027, atot[1], 1e-6);
        assertEquals(0.293892, atot[2], 1e-6);
        assertEquals(0.24889, atot[3], 1e-6);
        assertEquals(0.176759, atot[4], 1e-6);

        final float[] bbp = result.getBbp();
        assertEquals(0.0151021, bbp[0], 1e-6);
        assertEquals(0.0146508, bbp[1], 1e-6);
        assertEquals(0.0140458, bbp[2], 1e-6);
        assertEquals(0.0138128, bbp[3], 1e-6);
        assertEquals(0.0133328, bbp[4], 1e-6);

        final float[] aph = result.getAph();
        assertEquals(0.240965, aph[0], 1e-6);
        assertEquals(0.268238, aph[1], 1e-6);
        assertEquals(0.187655, aph[2], 1e-6);

        final float[] adg = result.getAdg();
        assertEquals(0.340423, adg[0], 1e-6);
        assertEquals(0.201719, adg[1], 1e-6);
        assertEquals(0.0912376, adg[2], 1e-6);
    }

    @Test
    public void testProcess_Meris_oldCoeffs() {
        // this testcase implements a comparison between the old implementation used in te operator and the
        // algorithm supplied by PML - the assertions of the old testcase are used to test the new algorithm with the
        // configuration used by the old algorithm tb 2013-03-01
        final float[] rrs_in = {0.030262154f, 0.031086152f, 0.022717977f, 0.013177891f, 0.0072450927f, 0.0024475828f};

        // old input vector was not normalized to PI - do it now tb 2013-03-01
        for (int i = 0; i < rrs_in.length; i++) {
            rrs_in[i] /= Math.PI;
        }
        //                       413               440          490          510            560            620              665
        //final float[] rrs = {0.030262154f, 0.031086152f, 0.022717977f, 0.013177891f, 0.0072450927f, 0.0028870495f, 0.0024475828f};

        final QaaAlgorithm algorithm = new QaaAlgorithm(new MerisConfigOldCoeffs());

        final QaaResult result = algorithm.process(rrs_in, null);
        final float[] atot = result.getAtot();
        assertEquals(0.03845500573515892f, atot[0], 1e-4);
        assertEquals(0.030030209571123123f, atot[1], 1e-4);
        assertEquals(0.030713409185409546f, atot[2], 1e-4);
        assertEquals(0.046738818287849426f, atot[3], 1e-4);
        assertEquals(0.06614950299263f, atot[4], 1e-4);

        final float[] bbp = result.getBbp();
        assertEquals(0.004216643515974283, bbp[0], 1e-4);
        assertEquals(0.003661837661638856, bbp[1], 1e-4);
        assertEquals(0.0030098173301666975, bbp[2], 1e-4);
        assertEquals(0.0027845455333590508, bbp[3], 1e-4);
        assertEquals(0.0023214993998408318, bbp[4], 1e-4);

        final float[] aph = result.getAph();
        assertEquals(0.0028468116652220488f, aph[0], 1e-4);
        assertEquals(0.0036492901854217052f, aph[1], 1e-4);
        assertEquals(0.006425064522773027f, aph[2], 1e-4);

        final float[] adg = result.getAdg();
        assertEquals(0.030918193981051445f, adg[0], 1e-4);
        assertEquals(0.019170919433236122f, adg[1], 1e-4);
        assertEquals(0.009288343600928783f, adg[2], 1e-4);
    }
}