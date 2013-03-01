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

import static org.junit.Assert.*;

public class CorrectionContextTest {

    @Test
    public void testCreate_MERIS() throws Exception {
        final CorrectionContext correctionContext = new CorrectionContext(Sensor.MERIS);
        assertNotNull(correctionContext);

        final Sensor sensor = correctionContext.getSensor();
        assertEquals(Sensor.MERIS, sensor);

        final double[] lambdaI = correctionContext.getLambdaI();
        assertArrayEquals(sensor.getLambdaI(), lambdaI, 1e-8);

        final double[] a_i = correctionContext.getA_i();
        assertEquals(8, a_i.length);
        assertEquals(0.032775, a_i[0], 1e-8);
        assertEquals(0.0274, a_i[1], 1e-8);
        assertEquals(0.018, a_i[2], 1e-8);
        assertEquals(0.0062, a_i[3], 1e-8);
        assertEquals(0.0062, a_i[4], 1e-8);
        assertEquals(0.0062, a_i[5], 1e-8);
        assertEquals(0.0152, a_i[6], 1e-8);
        assertEquals(0.0152, a_i[7], 1e-8);

        assertEquals(443.0, correctionContext.getSpec_model_start(), 1e-8);
        assertEquals(0.0394, correctionContext.getSmsA(), 1e-8);
        assertEquals(0.3435, correctionContext.getSmsB(), 1e-8);
    }

    @Test
    public void testCreate_MODISA() throws Exception {
        final CorrectionContext correctionContext = new CorrectionContext(Sensor.MODISA);
        assertNotNull(correctionContext);

        final Sensor sensor = correctionContext.getSensor();
        assertEquals(Sensor.MODISA, sensor);

        final double[] lambdaI = correctionContext.getLambdaI();
        assertArrayEquals(sensor.getLambdaI(), lambdaI, 1e-8);

        final double[] a_i = correctionContext.getA_i();
        assertEquals(8, a_i.length);
        assertEquals(0.0323, a_i[0], 1e-8);
        assertEquals(0.0279, a_i[1], 1e-8);
        assertEquals(0.0279, a_i[2], 1e-8);
        assertEquals(0.0115, a_i[3], 1e-8);
        assertEquals(0.00845, a_i[4], 1e-8);
        assertEquals(0.00845, a_i[5], 1e-8);
        assertEquals(0.01685, a_i[6], 1e-8);
        assertEquals(0.01685, a_i[7], 1e-8);

        assertEquals(443.0, correctionContext.getSpec_model_start(), 1e-8);
        assertEquals(0.0394, correctionContext.getSmsA(), 1e-8);
        assertEquals(0.3435, correctionContext.getSmsB(), 1e-8);
    }

    @Test
    public void testCreate_SeaWiFS() throws Exception {
        final CorrectionContext correctionContext = new CorrectionContext(Sensor.SEAWIFS);
        assertNotNull(correctionContext);

        final Sensor sensor = correctionContext.getSensor();
        assertEquals(Sensor.SEAWIFS, sensor);

        final double[] lambdaI = correctionContext.getLambdaI();
        assertArrayEquals(sensor.getLambdaI(), lambdaI, 1e-8);

        final double[] a_i = correctionContext.getA_i();
        assertEquals(8, a_i.length);
        assertEquals(0.0323, a_i[0], 1e-8);
        assertEquals(0.0274, a_i[1], 1e-8);
        assertEquals(0.018, a_i[2], 1e-8);
        assertEquals(0.007, a_i[3], 1e-8);
        assertEquals(0.007, a_i[4], 1e-8);
        assertEquals(0.007, a_i[5], 1e-8);
        assertEquals(0.01685, a_i[6], 1e-8);
        assertEquals(0.01685, a_i[7], 1e-8);

        assertEquals(443.0, correctionContext.getSpec_model_start(), 1e-8);
        assertEquals(0.0394, correctionContext.getSmsA(), 1e-8);
        assertEquals(0.3435, correctionContext.getSmsB(), 1e-8);
    }
}
