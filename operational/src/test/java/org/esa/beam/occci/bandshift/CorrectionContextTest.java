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

        final double[] b_i = correctionContext.getB_i();
        assertEquals(8, b_i.length);
        assertEquals(0.28775, b_i[0], 1e-8);
        assertEquals(0.361, b_i[1], 1e-8);
        assertEquals(0.2599999904633, b_i[2], 1e-8);
        assertEquals(0.016, b_i[3], 1e-8);
        assertEquals(0.016, b_i[4], 1e-8);
        assertEquals(0.016, b_i[5], 1e-8);
        assertEquals(0.134, b_i[6], 1e-8);
        assertEquals(0.134, b_i[7], 1e-8);

        final double[] a_o = correctionContext.getA_o();
        assertEquals(8, a_o.length);
        assertEquals(0.0322999991477, a_o[0], 1e-8);
        assertEquals(0.0278999991715, a_o[1], 1e-8);
        assertEquals(0.0115000000224, a_o[2], 1e-8);
        assertEquals(0.0115000000224, a_o[3], 1e-8);
        assertEquals(0.0084499996156, a_o[4], 1e-8);
        assertEquals(0.0070000002161, a_o[5], 1e-8);
        assertEquals(0.0168500002474, a_o[6], 1e-8);
        assertEquals(0.0188999995589, a_o[7], 1e-8);

        final double[] b_o = correctionContext.getB_o();
        assertEquals(8, b_o.length);
        assertEquals(0.286, b_o[0], 1e-8);
        assertEquals(0.369, b_o[1], 1e-8);
        assertEquals(0.1340000033379, b_o[2], 1e-8);
        assertEquals(0.1340000033379, b_o[3], 1e-8);
        assertEquals(0.0625, b_o[4], 1e-8);
        assertEquals(0.0315, b_o[5], 1e-8);
        assertEquals(0.14, b_o[6], 1e-8);
        assertEquals(0.149, b_o[7], 1e-8);

        final double[] aw_o = correctionContext.getAw_o();
        assertEquals(8, aw_o.length);
        assertEquals(0.0045505599119, aw_o[0], 1e-8);
        assertEquals(0.0145167000592, aw_o[1], 1e-8);
        assertEquals(0.0439153015614, aw_o[2], 1e-8);
        assertEquals(0.0439153015614, aw_o[3], 1e-8);
        assertEquals(0.0531685985625, aw_o[4], 1e-8);
        assertEquals(0.0595999993384, aw_o[5], 1e-8);
        assertEquals(0.4348880052567, aw_o[6], 1e-8);
        assertEquals(0.439, aw_o[7], 1e-8);

        final double[] aw_i = correctionContext.getAw_i();
        assertEquals(8, aw_i.length);
        assertEquals(0.0044960700907, aw_i[0], 1e-8);
        assertEquals(0.0149999996647, aw_i[1], 1e-8);
        assertEquals(0.0324999988079, aw_i[2], 1e-8);
        assertEquals(0.0619000010192, aw_i[3], 1e-8);
        assertEquals(0.0619000010192, aw_i[4], 1e-8);
        assertEquals(0.0619000010192, aw_i[5], 1e-8);
        assertEquals(0.4289999902248, aw_i[6], 1e-8);
        assertEquals(0.4289999902248, aw_i[7], 1e-8);

        final double[] bbw_o = correctionContext.getBbw_o();
        assertEquals(8, bbw_o.length);
        assertEquals(0.0028960050549, bbw_o[0], 1e-8);
        assertEquals(0.0014082950074, bbw_o[1], 1e-8);
        assertEquals(0.0009869249770, bbw_o[2], 1e-8);
        assertEquals(0.0009869249770, bbw_o[3], 1e-8);
        assertEquals(0.0008713999996, bbw_o[4], 1e-8);
        assertEquals(0.0008199950098, bbw_o[5], 1e-8);
        assertEquals(0.0003812715004, bbw_o[6], 1e-8);
        assertEquals(0.0003742394911, bbw_o[7], 1e-8);

        final double[] bbw_i = correctionContext.getBbw_i();
        assertEquals(8, bbw_i.length);
        assertEquals(0.0028659799136, bbw_i[0], 1e-8);
        assertEquals(0.0013841750333, bbw_i[1], 1e-8);
        assertEquals(0.0011693499982, bbw_i[2], 1e-8);
        assertEquals(0.0007897900068, bbw_i[3], 1e-8);
        assertEquals(0.0007897900068, bbw_i[4], 1e-8);
        assertEquals(0.0007897900068, bbw_i[5], 1e-8);
        assertEquals(0.0003860520083, bbw_i[6], 1e-8);
        assertEquals(0.0003860520083, bbw_i[7], 1e-8);

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
        assertEquals(0.0322999991477, a_i[0], 1e-8);
        assertEquals(0.0278999991715, a_i[1], 1e-8);
        assertEquals(0.0278999991715, a_i[2], 1e-8);
        assertEquals(0.0115000000224, a_i[3], 1e-8);
        assertEquals(0.0084499996156, a_i[4], 1e-8);
        assertEquals(0.0084499996156, a_i[5], 1e-8);
        assertEquals(0.0168500002474, a_i[6], 1e-8);
        assertEquals(0.0168500002474, a_i[7], 1e-8);

        final double[] a_o = correctionContext.getA_o();
        assertEquals(8, a_o.length);
        assertEquals(0.0327749997377, a_o[0], 1e-8);
        assertEquals(0.0179999992251, a_o[1], 1e-8);
        assertEquals(0.0274000000209, a_o[2], 1e-8);
        assertEquals(0.0179999992251, a_o[3], 1e-8);
        assertEquals(0.0062000001781, a_o[4], 1e-8);
        assertEquals(0.0070000002161, a_o[5], 1e-8);
        assertEquals(0.0152000002563, a_o[6], 1e-8);
        assertEquals(0.0188999995589, a_o[7], 1e-8);

        final double[] b_i = correctionContext.getB_i();
        assertEquals(8, b_i.length);
        assertEquals(0.286, b_i[0], 1e-8);
        assertEquals(0.369, b_i[1], 1e-8);
        assertEquals(0.369, b_i[2], 1e-8);
        assertEquals(0.134, b_i[3], 1e-8);
        assertEquals(0.0625, b_i[4], 1e-8);
        assertEquals(0.0625, b_i[5], 1e-8);
        assertEquals(0.14, b_i[6], 1e-8);
        assertEquals(0.14, b_i[7], 1e-8);

        final double[] b_o = correctionContext.getB_o();
        assertEquals(8, b_o.length);
        assertEquals(0.2877500057220, b_o[0], 1e-8);
        assertEquals(0.2599999904633, b_o[1], 1e-8);
        assertEquals(0.3610000014305, b_o[2], 1e-8);
        assertEquals(0.2599999904633, b_o[3], 1e-8);
        assertEquals(0.0160000007600, b_o[4], 1e-8);
        assertEquals(0.0315000005066, b_o[5], 1e-8);
        assertEquals(0.1340000033379, b_o[6], 1e-8);
        assertEquals(0.1490000039339, b_o[7], 1e-8);

        final double[] aw_o = correctionContext.getAw_o();
        assertEquals(8, aw_o.length);
        assertEquals(0.0044960700907, aw_o[0], 1e-8);
        assertEquals(0.0324999988079, aw_o[1], 1e-8);
        assertEquals(0.0149999996647, aw_o[2], 1e-8);
        assertEquals(0.0324999988079, aw_o[3], 1e-8);
        assertEquals(0.0619000010192, aw_o[4], 1e-8);
        assertEquals(0.0595999993384, aw_o[5], 1e-8);
        assertEquals(0.4289999902248, aw_o[6], 1e-8);
        assertEquals(0.439, aw_o[7], 1e-8);

        final double[] aw_i = correctionContext.getAw_i();
        assertEquals(8, aw_i.length);
        assertEquals(0.0045505599119, aw_i[0], 1e-8);
        assertEquals(0.0145167000592, aw_i[1], 1e-8);
        assertEquals(0.0145167000592, aw_i[2], 1e-8);
        assertEquals(0.0439153015614, aw_i[3], 1e-8);
        assertEquals(0.0531685985625, aw_i[4], 1e-8);
        assertEquals(0.0531685985625, aw_i[5], 1e-8);
        assertEquals(0.4348880052567, aw_i[6], 1e-8);
        assertEquals(0.4348880052567, aw_i[7], 1e-8);

        final double[] bbw_o = correctionContext.getBbw_o();
        assertEquals(8, bbw_o.length);
        assertEquals(0.0028659799136, bbw_o[0], 1e-8);
        assertEquals(0.0011693499982, bbw_o[1], 1e-8);
        assertEquals(0.0013841750333, bbw_o[2], 1e-8);
        assertEquals(0.0011693499982, bbw_o[3], 1e-8);
        assertEquals(0.0007897900068, bbw_o[4], 1e-8);
        assertEquals(0.0008199950098, bbw_o[5], 1e-8);
        assertEquals(0.0003860520083, bbw_o[6], 1e-8);
        assertEquals(0.0003742394911, bbw_o[7], 1e-8);

        final double[] bbw_i = correctionContext.getBbw_i();
        assertEquals(8, bbw_i.length);
        assertEquals(0.0028960050549, bbw_i[0], 1e-8);
        assertEquals(0.0014082950074, bbw_i[1], 1e-8);
        assertEquals(0.0014082950074, bbw_i[2], 1e-8);
        assertEquals(0.0009869249770, bbw_i[3], 1e-8);
        assertEquals(0.0008713999996, bbw_i[4], 1e-8);
        assertEquals(0.0008713999996, bbw_i[5], 1e-8);
        assertEquals(0.0003812715004, bbw_i[6], 1e-8);
        assertEquals(0.0003812715004, bbw_i[7], 1e-8);

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

        final double[] b_i = correctionContext.getB_i();
        assertEquals(8, b_i.length);
        assertEquals(0.286, b_i[0], 1e-8);
        assertEquals(0.361, b_i[1], 1e-8);
        assertEquals(0.26, b_i[2], 1e-8);
        assertEquals(0.0315, b_i[3], 1e-8);
        assertEquals(0.0315, b_i[4], 1e-8);
        assertEquals(0.0315, b_i[5], 1e-8);
        assertEquals(0.14, b_i[6], 1e-8);
        assertEquals(0.14, b_i[7], 1e-8);

        final double[] a_o = correctionContext.getA_o();
        assertEquals(8, a_o.length);
        assertEquals(0.0327749997377, a_o[0], 1e-8);
        assertEquals(0.0278999991715, a_o[1], 1e-8);
        assertEquals(0.0115000000224, a_o[2], 1e-8);
        assertEquals(0.0115000000224, a_o[3], 1e-8);
        assertEquals(0.0084499996156, a_o[4], 1e-8);
        assertEquals(0.0062000001781, a_o[5], 1e-8);
        assertEquals(0.0152000002563, a_o[6], 1e-8);
        assertEquals(0.0188999995589, a_o[7], 1e-8);

        final double[] b_o = correctionContext.getB_o();
        assertEquals(8, b_o.length);
        assertEquals(0.2877500057220, b_o[0], 1e-8);
        assertEquals(0.369, b_o[1], 1e-8);
        assertEquals(0.1340000033379, b_o[2], 1e-8);
        assertEquals(0.1340000033379, b_o[3], 1e-8);
        assertEquals(0.0625, b_o[4], 1e-8);
        assertEquals(0.016, b_o[5], 1e-8);
        assertEquals(0.134, b_o[6], 1e-8);
        assertEquals(0.149, b_o[7], 1e-8);

        final double[] aw_o = correctionContext.getAw_o();
        assertEquals(8, aw_o.length);
        assertEquals(0.0044960700907, aw_o[0], 1e-8);
        assertEquals(0.0145167000592, aw_o[1], 1e-8);
        assertEquals(0.0439153015614, aw_o[2], 1e-8);
        assertEquals(0.0439153015614, aw_o[3], 1e-8);
        assertEquals(0.0531685985625, aw_o[4], 1e-8);
        assertEquals(0.0619000010192, aw_o[5], 1e-8);
        assertEquals(0.4289999902248, aw_o[6], 1e-8);
        assertEquals(0.439, aw_o[7], 1e-8);

        final double[] aw_i = correctionContext.getAw_i();
        assertEquals(8, aw_i.length);
        assertEquals(0.0045505599119, aw_i[0], 1e-8);
        assertEquals(0.0149999996647, aw_i[1], 1e-8);
        assertEquals(0.0324999988079, aw_i[2], 1e-8);
        assertEquals(0.0595999993384, aw_i[3], 1e-8);
        assertEquals(0.0595999993384, aw_i[4], 1e-8);
        assertEquals(0.0595999993384, aw_i[5], 1e-8);
        assertEquals(0.4348880052567, aw_i[6], 1e-8);
        assertEquals(0.4348880052567, aw_i[7], 1e-8);

        final double[] bbw_o = correctionContext.getBbw_o();
        assertEquals(8, bbw_o.length);
        assertEquals(0.0028659799136, bbw_o[0], 1e-8);
        assertEquals(0.0014082950074, bbw_o[1], 1e-8);
        assertEquals(0.0009869249770, bbw_o[2], 1e-8);
        assertEquals(0.0009869249770, bbw_o[3], 1e-8);
        assertEquals(0.0008713999996, bbw_o[4], 1e-8);
        assertEquals(0.0007897900068, bbw_o[5], 1e-8);
        assertEquals(0.0003860520083, bbw_o[6], 1e-8);
        assertEquals(0.0003742394911, bbw_o[7], 1e-8);

        final double[] bbw_i = correctionContext.getBbw_i();
        assertEquals(8, bbw_i.length);
        assertEquals(0.0028960050549, bbw_i[0], 1e-8);
        assertEquals(0.0013841750333, bbw_i[1], 1e-8);
        assertEquals(0.0011693499982, bbw_i[2], 1e-8);
        assertEquals(0.0008199950098, bbw_i[3], 1e-8);
        assertEquals(0.0008199950098, bbw_i[4], 1e-8);
        assertEquals(0.0008199950098, bbw_i[5], 1e-8);
        assertEquals(0.0003812715004, bbw_i[6], 1e-8);
        assertEquals(0.0003812715004, bbw_i[7], 1e-8);

        assertEquals(443.0, correctionContext.getSpec_model_start(), 1e-8);
        assertEquals(0.0394, correctionContext.getSmsA(), 1e-8);
        assertEquals(0.3435, correctionContext.getSmsB(), 1e-8);
    }
}
