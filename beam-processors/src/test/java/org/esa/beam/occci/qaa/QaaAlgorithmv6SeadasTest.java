/*
 * Copyright (C) 2014 Brockmann Consult GmbH (info@brockmann-consult.de)
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

package org.esa.beam.occci.qaa;

import org.junit.Test;

import static org.junit.Assert.*;

public class QaaAlgorithmv6SeadasTest {

    @Test
    public void testProcess() throws Exception {
        final float[] rrs_in = {0.001919f, 0.002297f, 0.004420f, 0.005547f, 0.009138f, 0.004110f};

        final QaaAlgo algorithm = new QaaAlgorithmv6Seadas(new SeaWifsConfig());

        final QaaResult result = algorithm.process(rrs_in, null);
        final float[] atot = result.getAtot();
        assertEquals(1.376624, atot[0], 1e-6);
        assertEquals(1.114580, atot[1], 1e-6);
        assertEquals(0.566514, atot[2], 1e-6);
        assertEquals(0.448758, atot[3], 1e-6);
        assertEquals(0.270145, atot[4], 1e-6);
        assertEquals(0.567642, atot[5], 1e-6);

        final float[] bbp = result.getBbp();
        assertEquals(0.049912, bbp[0], 1e-6);
        assertEquals(0.049567, bbp[1], 1e-6);
        assertEquals(0.049092, bbp[2], 1e-6);
        assertEquals(0.048905, bbp[3], 1e-6);
        assertEquals(0.048511, bbp[4], 1e-6);
        assertEquals(0.047647, bbp[5], 1e-6);

        final float[] aph = result.getAph();
        assertEquals(0.621739, aph[0], 1e-6);
        assertEquals(0.669095, aph[1], 1e-6);
        assertEquals(0.357394, aph[2], 1e-6);
        assertEquals(0.279009, aph[3], 1e-6);
        assertEquals(0.147630, aph[4], 1e-6);
        assertEquals(0.124183, aph[5], 1e-6);

        final float[] adg = result.getAdg();
        assertEquals(0.750334, adg[0], 1e-6);
        assertEquals(0.438416, adg[1], 1e-6);
        assertEquals(0.194120, adg[2], 1e-6);
        assertEquals(0.137249, adg[3], 1e-6);
        assertEquals(0.062914, adg[4], 1e-6);
        assertEquals(0.008571, adg[5], 1e-6);
    }
}
