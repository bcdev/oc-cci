/*
 * Copyright (C) 2013 Brockmann Consult GmbH (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.beam.occci.bandshift;


import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class BandShiftCorrectionTest_MODIS {

    private static final double[] RRS_WAVELENGTHS = new double[]{412, 443, 488, 531, 547, 667};

    private BandShiftCorrection bandShiftCorrection;

    @Before
    public void setUp() throws IOException {
        final CorrectionContext correctionContext = new CorrectionContext(Sensor.MODISA);
        bandShiftCorrection = new BandShiftCorrection(correctionContext);
    }

    @Test
    public void testCorrectBandshift() throws Exception {
        double[] rrs = new double[]{0.00709421, 0.00560526, 0.00464842, 0.00256442, 0.00196990, 0.000251790};
        double[] qaa = new double[]{0.0189454, 0.00553217, 0.0133541};

        double[] rrs_corrected = bandShiftCorrection.correctBandshift(rrs, RRS_WAVELENGTHS, qaa);
        assertEquals(8, rrs_corrected.length);
        assertEquals(0.007048483472, rrs_corrected[0], 1e-6);
        assertEquals(0.003120107576, rrs_corrected[1], 1e-6);
        assertEquals(0.004605459515, rrs_corrected[2], 1e-6);
        assertEquals(0.003281945130, rrs_corrected[3], 1e-6);
        assertEquals(0.001650141436, rrs_corrected[4], 1e-6);
        assertEquals(0.001733719837, rrs_corrected[5], 1e-6);
        assertEquals(0.000257119944, rrs_corrected[6], 1e-6);
        assertEquals(0.000246794894, rrs_corrected[7], 1e-6);

        rrs = new double[]{0.00711314, 0.00559714, 0.00459386, 0.00249029, 0.00189400, 0.000241144};
        qaa = new double[]{0.0192148, 0.00571175, 0.0138207};

        rrs_corrected = bandShiftCorrection.correctBandshift(rrs, RRS_WAVELENGTHS, qaa);
        assertEquals(8, rrs_corrected.length);
        assertEquals(0.007067796774, rrs_corrected[0], 1e-6);
        assertEquals(0.003098302055, rrs_corrected[1], 1e-6);
        assertEquals(0.004552158527, rrs_corrected[2], 1e-6);
        assertEquals(0.003181226784, rrs_corrected[3], 1e-6);
        assertEquals(0.001587149804, rrs_corrected[4], 1e-6);
        assertEquals(0.001667401055, rrs_corrected[5], 1e-6);
        assertEquals(0.000246264943, rrs_corrected[6], 1e-6);
        assertEquals(0.000236338106, rrs_corrected[7], 1e-6);
    }

    @Test
    public void testWeightedAverageEqualCorrectionProducts() {
        double[] rrs_corr = new double[]{0.007067796774, 0.003098302055, 0.004552158527, 0.003181226784,
                0.001587149804, 0.001667401055, 0.000246264943, 0.000236338106};

        final double[] rrs_weight = bandShiftCorrection.weightedAverageEqualCorrectionProducts(rrs_corr);
        assertEquals(7, rrs_weight.length);
        assertEquals(0.007067796774, rrs_weight[0], 1e-6);
        assertEquals(0.003140728688, rrs_weight[1], 1e-6);
        assertEquals(0.004552158527, rrs_weight[2], 1e-6);
        assertEquals(0.001587149804, rrs_weight[3], 1e-6);
        assertEquals(0.001667401055, rrs_weight[4], 1e-6);
        assertEquals(0.000246264943, rrs_weight[5], 1e-6);
        assertEquals(0.000236338106, rrs_weight[6], 1e-6);
    }
}
