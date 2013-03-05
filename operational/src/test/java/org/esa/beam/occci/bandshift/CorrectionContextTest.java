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
        assertArrayEquals(Sensor.MERIS.getLambdaI(), lambdaI, 1e-8);

        final double[] lambdaO = correctionContext.getLambdaO();
        assertArrayEquals(Sensor.MERIS.getLambdaO(), lambdaO, 1e-8);

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

        final double[] aw_o = correctionContext.getAw_o();
        assertEquals(8, aw_o.length);
        assertEquals(0.004640989999999999, aw_o[0], 1e-8);
        assertEquals(0.01457920909090909, aw_o[1], 1e-8);
        assertEquals(0.043935209090909096, aw_o[2], 1e-8);
        assertEquals(0.043935209090909096, aw_o[3], 1e-8);
        assertEquals(0.05337546363636363, aw_o[4], 1e-8);
        assertEquals(0.05964877272727272, aw_o[5], 1e-8);
        assertEquals(0.43391563636363634, aw_o[6], 1e-8);
        assertEquals(0.4401876363636364, aw_o[7], 1e-8);

        final double[] aw_i = correctionContext.getAw_i();
        assertEquals(8, aw_i.length);
        assertEquals(0.004581862727272728, aw_i[0], 1e-8);
        assertEquals(0.015267136363636365, aw_i[1], 1e-8);
        assertEquals(0.0325987, aw_i[2], 1e-8);
        assertEquals(0.062064, aw_i[3], 1e-8);
        assertEquals(0.062064, aw_i[4], 1e-8);
        assertEquals(0.062064, aw_i[5], 1e-8);
        assertEquals(0.4281564545454545, aw_i[6], 1e-8);
        assertEquals(0.4281564545454545, aw_i[7], 1e-8);

        final double[] bbw_o = correctionContext.getBbw_o();
        assertEquals(8, bbw_o.length);
        assertEquals(0.0028979950000000004, bbw_o[0], 1e-8);
        assertEquals(0.0014089559090909093, bbw_o[1], 1e-8);
        assertEquals(9.87310909090909E-4, bbw_o[2], 1e-8);
        assertEquals(9.87310909090909E-4, bbw_o[3], 1e-8);
        assertEquals(8.717213636363637E-4, bbw_o[4], 1e-8);
        assertEquals(8.202877272727272E-4, bbw_o[5], 1e-8);
        assertEquals(3.81364E-4, bbw_o[6], 1e-8);
        assertEquals(3.7432945454545457E-4, bbw_o[7], 1e-8);

        final double[] bbw_i = correctionContext.getBbw_i();
        assertEquals(8, bbw_i.length);
        assertEquals(0.0028679404545454545, bbw_i[0], 1e-8);
        assertEquals(0.0013848222727272728, bbw_i[1], 1e-8);
        assertEquals(0.0011698504545454545, bbw_i[2], 1e-8);
        assertEquals(7.900659090909093E-4, bbw_i[3], 1e-8);
        assertEquals(7.900659090909093E-4, bbw_i[4], 1e-8);
        assertEquals(7.900659090909093E-4, bbw_i[5], 1e-8);
        assertEquals(3.8614568181818184E-4, bbw_i[6], 1e-8);
        assertEquals(3.8614568181818184E-4, bbw_i[7], 1e-8);

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
        assertArrayEquals(Sensor.MODISA.getLambdaI(), lambdaI, 1e-8);

        final double[] lambdaO = correctionContext.getLambdaO();
        assertArrayEquals(Sensor.MODISA.getLambdaO(), lambdaO, 1e-8);

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

        final double[] aw_o = correctionContext.getAw_o();
        assertEquals(8, aw_o.length);
        assertEquals(0.004581862727272728, aw_o[0], 1e-8);
        assertEquals(0.0325987, aw_o[1], 1e-8);
        assertEquals(0.015267136363636365, aw_o[2], 1e-8);
        assertEquals(0.0325987, aw_o[3], 1e-8);
        assertEquals(0.062064, aw_o[4], 1e-8);
        assertEquals(0.05964877272727272, aw_o[5], 1e-8);
        assertEquals(0.4281564545454545, aw_o[6], 1e-8);
        assertEquals(0.4401876363636364, aw_o[7], 1e-8);

        final double[] aw_i = correctionContext.getAw_i();
        assertEquals(8, aw_i.length);
        assertEquals(0.004640989999999999, aw_i[0], 1e-8);
        assertEquals(0.01457920909090909, aw_i[1], 1e-8);
        assertEquals(0.01457920909090909, aw_i[2], 1e-8);
        assertEquals(0.043935209090909096, aw_i[3], 1e-8);
        assertEquals(0.05337546363636363, aw_i[4], 1e-8);
        assertEquals(0.05337546363636363, aw_i[5], 1e-8);
        assertEquals(0.43391563636363634, aw_i[6], 1e-8);
        assertEquals(0.43391563636363634, aw_i[7], 1e-8);

        final double[] bbw_o = correctionContext.getBbw_o();
        assertEquals(8, bbw_o.length);
        assertEquals(0.0028679404545454545, bbw_o[0], 1e-8);
        assertEquals(0.0011698504545454545, bbw_o[1], 1e-8);
        assertEquals(0.0013848222727272728, bbw_o[2], 1e-8);
        assertEquals(0.0011698504545454545, bbw_o[3], 1e-8);
        assertEquals(7.900659090909093E-4, bbw_o[4], 1e-8);
        assertEquals(8.202877272727272E-4, bbw_o[5], 1e-8);
        assertEquals(3.8614568181818184E-4, bbw_o[6], 1e-8);
        assertEquals(3.7432945454545457E-4, bbw_o[7], 1e-8);

        final double[] bbw_i = correctionContext.getBbw_i();
        assertEquals(8, bbw_i.length);
        assertEquals(0.0028979950000000004, bbw_i[0], 1e-8);
        assertEquals(0.0014089559090909093, bbw_i[1], 1e-8);
        assertEquals(0.0014089559090909093, bbw_i[2], 1e-8);
        assertEquals(9.87310909090909E-4, bbw_i[3], 1e-8);
        assertEquals(8.717213636363637E-4, bbw_i[4], 1e-8);
        assertEquals(8.717213636363637E-4, bbw_i[5], 1e-8);
        assertEquals(3.81364E-4, bbw_i[6], 1e-8);
        assertEquals(3.81364E-4, bbw_i[7], 1e-8);

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
        assertArrayEquals(Sensor.SEAWIFS.getLambdaI(), lambdaI, 1e-8);

        final double[] lambdaO = correctionContext.getLambdaO();
        assertArrayEquals(Sensor.SEAWIFS.getLambdaO(), lambdaO, 1e-8);

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

        final double[] aw_o = correctionContext.getAw_o();
        assertEquals(8, aw_o.length);
        assertEquals(0.004581862727272728, aw_o[0], 1e-8);
        assertEquals(0.01457920909090909, aw_o[1], 1e-8);
        assertEquals(0.043935209090909096, aw_o[2], 1e-8);
        assertEquals(0.043935209090909096, aw_o[3], 1e-8);
        assertEquals(0.05337546363636363, aw_o[4], 1e-8);
        assertEquals(0.062064, aw_o[5], 1e-8);
        assertEquals(0.4281564545454545, aw_o[6], 1e-8);
        assertEquals(0.4401876363636364, aw_o[7], 1e-8);

        final double[] aw_i = correctionContext.getAw_i();
        assertEquals(8, aw_i.length);
        assertEquals(0.004640989999999999, aw_i[0], 1e-8);
        assertEquals(0.015267136363636365, aw_i[1], 1e-8);
        assertEquals(0.0325987, aw_i[2], 1e-8);
        assertEquals(0.05964877272727272, aw_i[3], 1e-8);
        assertEquals(0.05964877272727272, aw_i[4], 1e-8);
        assertEquals(0.05964877272727272, aw_i[5], 1e-8);
        assertEquals(0.43391563636363634, aw_i[6], 1e-8);
        assertEquals(0.43391563636363634, aw_i[7], 1e-8);

        final double[] bbw_o = correctionContext.getBbw_o();
        assertEquals(8, bbw_o.length);
        assertEquals(0.0028679404545454545, bbw_o[0], 1e-8);
        assertEquals(0.0014089559090909093, bbw_o[1], 1e-8);
        assertEquals(9.87310909090909E-4, bbw_o[2], 1e-8);
        assertEquals(9.87310909090909E-4, bbw_o[3], 1e-8);
        assertEquals(8.717213636363637E-4, bbw_o[4], 1e-8);
        assertEquals(7.900659090909093E-4, bbw_o[5], 1e-8);
        assertEquals(3.8614568181818184E-4, bbw_o[6], 1e-8);
        assertEquals(3.7432945454545457E-4, bbw_o[7], 1e-8);

        final double[] bbw_i = correctionContext.getBbw_i();
        assertEquals(8, bbw_i.length);
        assertEquals(0.0028979950000000004, bbw_i[0], 1e-8);
        assertEquals(0.0013848222727272728, bbw_i[1], 1e-8);
        assertEquals(0.0011698504545454545, bbw_i[2], 1e-8);
        assertEquals(8.202877272727272E-4, bbw_i[3], 1e-8);
        assertEquals(8.202877272727272E-4, bbw_i[4], 1e-8);
        assertEquals(8.202877272727272E-4, bbw_i[5], 1e-8);
        assertEquals(3.81364E-4, bbw_i[6], 1e-8);
        assertEquals(3.81364E-4, bbw_i[7], 1e-8);

        assertEquals(443.0, correctionContext.getSpec_model_start(), 1e-8);
        assertEquals(0.0394, correctionContext.getSmsA(), 1e-8);
        assertEquals(0.3435, correctionContext.getSmsB(), 1e-8);
    }
}
