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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class IopSpectralModelTest {

    @Test
    public void testGetABBricaud() throws Exception {
        double[] abBricaud = IopSpectralModel.getABBricaud(443.0);
        assertNotNull(abBricaud);
        assertEquals(2, abBricaud.length);
        assertEquals(0.0394, abBricaud[0], 1e-8);
        assertEquals(0.3435, abBricaud[1], 1e-8);

        abBricaud = IopSpectralModel.getABBricaud(547.0);
        assertNotNull(abBricaud);
        assertEquals(2, abBricaud.length);
        assertEquals(0.00845, abBricaud[0], 1e-8);
        assertEquals(0.0625, abBricaud[1], 1e-8);

        abBricaud = IopSpectralModel.getABBricaud(620.0);
        assertNotNull(abBricaud);
        assertEquals(2, abBricaud.length);
        assertEquals(0.0065, abBricaud[0], 1e-8);
        assertEquals(0.064, abBricaud[1], 1e-8);
    }

    @Test
    public void testIopSpectralModel_SeaWiFS() {
        final double lambda = 490.0;
        final double bricaud_a = 0.0274;
        final double bricaud_b = 0.361;
        final double aph_in = 0.014;            // values from qaa configuration for SeaWiFS tb 2013-03-04
        final double adg_in = 0.016;
        final double bbp_in = 0.00276835;
        final double rrs_blue_in = 0.00186919071018569;
        final double rrs_green_in = 0.00406382197640373;
        final double lambda_out = 488.0;
        final double bricaud_a_out = 0.0279;
        final double bricaud_b_out = 0.369;

        final double[] result = IopSpectralModel.iopSpectralModel(lambda, bricaud_a, bricaud_b, aph_in, adg_in, bbp_in, rrs_blue_in, rrs_green_in, lambda_out, bricaud_a_out, bricaud_b_out);
        assertEquals(3, result.length);
        assertEquals(0.01437582119376783, result[0], 1e-8);
        assertEquals(0.01654960862977674, result[1], 1e-8);
        assertEquals(0.002773036260618248, result[2], 1e-8);
    }

    @Test
    public void testIopSpectralModel_MODIS() {
        final double lambda = 488.0;
        final double bricaud_a = 0.0279;
        final double bricaud_b = 0.369;
        final double aph_in = 0.0145167;            // values from qaa configuration for MODIS tb 2013-03-04
        final double adg_in = 0.0145167;
        final double bbp_in = 0.00281659;
        final double rrs_blue_in = 0.0029860009;
        final double rrs_green_in = 0.0029520008;
        final double lambda_out = 490.0;
        final double bricaud_a_out = 0.0274;
        final double bricaud_b_out = 0.361;

        final double[] result = IopSpectralModel.iopSpectralModel(lambda, bricaud_a, bricaud_b, aph_in, adg_in, bbp_in, rrs_blue_in, rrs_green_in, lambda_out, bricaud_a_out, bricaud_b_out);
        assertEquals(3, result.length);
        assertEquals(0.014138943881420582, result[0], 1e-8);
        assertEquals(0.0140527425854202, result[1], 1e-8);
        assertEquals(0.0028047002794939677, result[2], 1e-8);
    }
}
