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
        final float[] a_total = result.getAtot();
        assertEquals(1.376624, a_total[0], 1e-6);
        assertEquals(1.114580, a_total[1], 1e-6);
        assertEquals(0.566514, a_total[2], 1e-6);
        assertEquals(0.448758, a_total[3], 1e-6);
        assertEquals(0.270145, a_total[4], 1e-6);
        assertEquals(0.567642, a_total[5], 1e-6);

        final float[] bb_spm = result.getBbp();
        assertEquals(0.055704, bb_spm[0], 1e-6);
        assertEquals(0.053813, bb_spm[1], 1e-6);
        assertEquals(0.051861, bb_spm[2], 1e-6);
        assertEquals(0.051244, bb_spm[3], 1e-6);
        assertEquals(0.050152, bb_spm[4], 1e-6);
        assertEquals(0.048410, bb_spm[5], 1e-6);

        final float[] a_pig = result.getAph();
        assertEquals(0.621739, a_pig[0], 1e-6);
        assertEquals(0.669095, a_pig[1], 1e-6);
        assertEquals(0.357394, a_pig[2], 1e-6);
        assertEquals(0.279009, a_pig[3], 1e-6);
        assertEquals(0.147630, a_pig[4], 1e-6);
        assertEquals(0.124183, a_pig[5], 1e-6);

        final float[] a_ys = result.getAdg();
        assertEquals(0.750334, a_ys[0], 1e-6);
        assertEquals(0.438416, a_ys[1], 1e-6);
        assertEquals(0.194120, a_ys[2], 1e-6);
        assertEquals(0.137249, a_ys[3], 1e-6);
        assertEquals(0.062914, a_ys[4], 1e-6);
        assertEquals(0.008571, a_ys[5], 1e-6);
    }
}
