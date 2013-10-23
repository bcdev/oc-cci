package org.esa.beam.occci.bandshift;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class BandShiftCorrectionTest_MERIS {

    private static final double[] RRS_WAVELENGTHS = new double[]{413.0, 443.0, 490.0, 510.0, 560.0, 665.0 };

    private BandShiftCorrection bandShiftCorrection;

    @Before
    public void setUp() throws IOException {
        final CorrectionContext correctionContext = new CorrectionContext(Sensor.MERIS);
        bandShiftCorrection = new BandShiftCorrection(correctionContext);
    }

    @Test
    public void testCorrectBandshift() throws Exception {
        double[] rrs = new double[]{0.00709421, 0.00560526, 0.00464842, 0.00256442, 0.00196990, 0.00196990};
        double[] qaa = new double[]{0.0189454, 0.00553217, 0.0133541};

        double[] rrs_corrected = bandShiftCorrection.correctBandshift(rrs, RRS_WAVELENGTHS, qaa);
        assertEquals(8, rrs_corrected.length);
        assertEquals(0.007140232716, rrs_corrected[0], 1e-6);
        assertEquals(0.004691781010, rrs_corrected[1], 1e-6);
        assertEquals(0.002003765898, rrs_corrected[2], 1e-6);
        assertEquals(0.002889874624, rrs_corrected[3], 1e-6);
        assertEquals(0.002351620235, rrs_corrected[4], 1e-6);
        assertEquals(0.002069674199, rrs_corrected[5], 1e-6);
        assertEquals(0.001929065329, rrs_corrected[6], 1e-6);
        assertEquals(0.001890795655, rrs_corrected[7], 1e-6);

        rrs = new double[]{0.00711314, 0.00559714, 0.00459386, 0.00249029, 0.00189400, 0.000241144};
        qaa = new double[]{0.0192148, 0.00571175, 0.0138207};

        rrs_corrected = bandShiftCorrection.correctBandshift(rrs, RRS_WAVELENGTHS, qaa);
        assertEquals(8, rrs_corrected.length);
        assertEquals(0.007158774417, rrs_corrected[0], 1e-6);
        assertEquals(0.004635943566, rrs_corrected[1], 1e-6);
        assertEquals(0.001949419267, rrs_corrected[2], 1e-6);
        assertEquals(0.002775410656, rrs_corrected[3], 1e-6);
        assertEquals(0.002260174835, rrs_corrected[4], 1e-6);
        assertEquals(0.001989766490, rrs_corrected[5], 1e-6);
        assertEquals(0.000236129519, rrs_corrected[6], 1e-6);
        assertEquals(0.000231423590, rrs_corrected[7], 1e-6);
    }

    @Test
    public void testWeightedAverageEqualCorrectionProducts() {
        double[] rrs_corr = new double[]{0.007158774417, 0.004635943566, 0.001949419267, 0.002775410656,
                0.002260174835, 0.001989766490, 0.000236129519, 0.000231423590};

        final double[] rrs_weight = bandShiftCorrection.weightedAverageEqualCorrectionProducts(rrs_corr);
        assertEquals(7, rrs_weight.length);
        assertEquals(0.007158774417, rrs_weight[0], 1e-6);
        assertEquals(0.004635943566, rrs_weight[1], 1e-6);
        assertEquals(0.002296335762, rrs_weight[2], 1e-6);
        assertEquals(0.002260174835, rrs_weight[3], 1e-6);
        assertEquals(0.001989766490, rrs_weight[4], 1e-6);
        assertEquals(0.000236129519, rrs_weight[5], 1e-6);
        assertEquals(0.000231423590, rrs_weight[6], 1e-6);
    }
}
