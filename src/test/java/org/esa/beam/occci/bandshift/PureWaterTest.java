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

import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.*;

public class PureWaterTest {

    @Test
    public void testRead() throws Exception {
        PureWater pureWater = PureWater.read();
        assertNotNull(pureWater);

        assertEquals(2250, pureWater.getLambda().length);
        assertEquals(200.0, pureWater.getLambda()[0], 1e-10);
        assertEquals(2449.0, pureWater.getLambda()[2249], 1e-10);

        assertEquals(2250, pureWater.getAw().length);
        assertEquals(3.07, pureWater.getAw()[0], 1e-10);
        assertEquals(7061.60, pureWater.getAw()[2249], 1e-10);

        assertEquals(2250, pureWater.getBw().length);
        assertEquals(0.183171, pureWater.getBw()[0], 1e-10);
        assertEquals(3.74577e-06, pureWater.getBw()[2249], 1e-10);
    }


    @Test
    public void testReadWater() throws Exception {
        InputStream resourceAsStream = getClass().getResourceAsStream("water_spectra.dat");
        InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream);
        PureWater pureWater = PureWater.readWater(inputStreamReader);
        assertNotNull(pureWater);

        assertEquals(2250, pureWater.getLambda().length);
        assertEquals(200.0, pureWater.getLambda()[0], 1e-10);
        assertEquals(2449.0, pureWater.getLambda()[2249], 1e-10);

        assertEquals(2250, pureWater.getAw().length);
        assertEquals(3.07, pureWater.getAw()[0], 1e-10);
        assertEquals(7061.60, pureWater.getAw()[2249], 1e-10);

        assertEquals(2250, pureWater.getBw().length);
        assertEquals(0.151, pureWater.getBw()[0], 1e-10);
        assertEquals(3.00000E-06, pureWater.getBw()[2249], 1e-10);
    }

    @Test
    public void testReadBWater() throws Exception {
        InputStream resourceAsStream = getClass().getResourceAsStream("bw_spectrum_s35t22.dat");
        InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream);
        PureWater pureWater = PureWater.readBWater(inputStreamReader);
        assertNotNull(pureWater);

        assertEquals(2250, pureWater.getLambda().length);
        assertEquals(200.0, pureWater.getLambda()[0], 1e-10);
        assertEquals(2449.0, pureWater.getLambda()[2249], 1e-10);

        assertNull(pureWater.getAw());

        assertEquals(2250, pureWater.getBw().length);
        assertEquals(0.183171, pureWater.getBw()[0], 1e-10);
        assertEquals(3.74577e-06, pureWater.getBw()[2249], 1e-10);
    }

    @Test
    public void testGetSpectralDataPureWater() throws Exception {
        PureWater pureWater = PureWater.read();
        double[] spectralDataPureWater = pureWater.getSpectralDataPureWater(413.0);
        assertNotNull(spectralDataPureWater);
        double[] expected = {0.004581862727272728, 0.005735880909090909};
        assertArrayEquals(expected, spectralDataPureWater, 1e-10);
    }

    @Test
    public void testMean() throws Exception {
        double[] data = {1, 2, 3, 4};
        assertEquals(2.5, PureWater.mean(data, 1, 2), 1e-10);
        assertEquals(3, PureWater.mean(data, 1, 3), 1e-10);
    }
}
