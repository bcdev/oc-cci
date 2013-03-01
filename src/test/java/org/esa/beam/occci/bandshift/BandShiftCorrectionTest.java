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
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class BandShiftCorrectionTest {

    private static final double[] RRS_WAVELENGTHS = new double[]{412, 443, 488, 531, 547, 667};
    public static final double QAA_MIN = 0.0;
    public static final double QAA_MAX = 5.0;

    private BandShiftCorrection bandShiftCorrection;


    @Before
    public void setUp() throws IOException {
        final CorrectionContext correctionContext = new CorrectionContext(Sensor.MODISA);
        bandShiftCorrection = new BandShiftCorrection(correctionContext);
    }

    @Test
    public void testIntegration() throws Exception {
        double[] rrs = new double[]{0.00709421, 0.00560526, 0.00464842, 0.00256442, 0.00196990, 0.000251790};
        double[] qaa = new double[]{0.0189454, 0.00553217, 0.0133541};

        double[] rrs_corrected = bandShiftCorrection.correctBandshift(rrs, RRS_WAVELENGTHS, qaa, QAA_MIN, QAA_MAX);

        assertEquals(8, rrs_corrected.length);
        double[] rrs_corrected_expected = new double[]{
                0.007049453761773617, 0.003118112751606265, 0.004575966940385904, 0.003276320419328844,
                0.00165174742957039, 0.001738623419459412, 0.00025705602023366406, 0.00024558282089105044};
        assertArrayEquals(rrs_corrected_expected, rrs_corrected, 1e-6);

        double[] rrs_averaged = bandShiftCorrection.weightedAverageEqualCorrectionProducts(rrs_corrected);
        double[] rrs_averaged_expected = new double[]{
                0.007049453761773617, 0.0031990562095108406, 0.004575966940385904,
                0.00165174742957039, 0.001738623419459412, 0.00025705602023366406, 0.00024558282089105044};
        assertEquals(7, rrs_averaged.length);
        assertArrayEquals(rrs_averaged_expected, rrs_averaged, 1e-6);

        System.out.println("rrs_corrected = " + Arrays.toString(rrs_corrected));
        System.out.println("rrs_averaged = " + Arrays.toString(rrs_averaged));

        rrs = new double[]{0.00711314, 0.00559714, 0.00459386, 0.00249029, 0.00189400, 0.000241144};
        qaa = new double[]{0.0192148, 0.00571175, 0.0138207};
        rrs_corrected = bandShiftCorrection.correctBandshift(rrs, RRS_WAVELENGTHS, qaa, QAA_MIN, QAA_MAX);
        assertEquals(8, rrs_corrected.length);
        rrs_corrected_expected = new double[]{
                0.007068748264636092, 0.003096325914079895, 0.004523327671806644, 0.0031757862933153645,
                0.001588695583326859, 0.0016721109782915423, 0.0002462035957436175, 0.0002351777737802928};
        assertArrayEquals(rrs_corrected_expected, rrs_corrected, 1e-6);

        System.out.println("rrs_corrected = " + Arrays.toString(rrs_corrected));
    }

    @Test
    public void testIntegration_IDL_MODIS_Dataset_1() throws IOException {
        final double[] rrs_in = {0.00709421, 0.00560526, 0.00464842, 0.00256442, 0.00196990, 0.000251790};
        final double[] qaa = {0.0189454, 0.00553217, 0.0133541};

        final double[] rrs_corrected = bandShiftCorrection.correctBandshift(rrs_in, RRS_WAVELENGTHS, qaa, QAA_MIN, QAA_MAX);
        assertEquals(8, rrs_corrected.length);
        // @todo 1 tb/tb continue here
//        assertEquals(0.00709421, rrs_corrected[0], 1e-8);
    }

    @Test
    public void testIntegration_IDL_MODIS_Dataset_2() throws IOException {
        final double[] rrs_in = {0.00711314, 0.00559714, 0.00459386, 0.00249029, 0.00189400, 0.000241144};
        final double[] qaa = {0.0192148, 0.00571175, 0.0138207};

        final double[] rrs_corrected = bandShiftCorrection.correctBandshift(rrs_in, RRS_WAVELENGTHS, qaa, QAA_MIN, QAA_MAX);
        assertEquals(8, rrs_corrected.length);
        // @todo 1 tb/tb continue here
//        assertEquals(0.00711314, rrs_corrected[0], 1e-8);
    }

}
