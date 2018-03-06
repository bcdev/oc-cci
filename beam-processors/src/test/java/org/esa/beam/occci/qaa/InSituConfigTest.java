package org.esa.beam.occci.qaa;


import org.junit.Test;

import static org.junit.Assert.*;

public class InSituConfigTest {

    @Test
    public void testGetAwCoefficients() {
        final InSituConfig config = new InSituConfig(new double[0]);

        final double[] aw_coeffs = config.getAwCoefficients();
        assertEquals(3, aw_coeffs.length);
        assertEquals(-1.146, aw_coeffs[0], 1e-8);
        assertEquals(-1.366, aw_coeffs[1], 1e-8);
        assertEquals(-0.469, aw_coeffs[2], 1e-8);
    }

    @Test
    public void testGetWavelengths() {
        final double[] wavelengths = {101, 102, 103, 104, 105, 106};
        final InSituConfig inSituConfig = new InSituConfig(wavelengths);

        assertArrayEquals(wavelengths, inSituConfig.getWavelengths(), 1e-8);
    }

    @Test
    public void testGetReferenceWavelength() {
        final double[] wavelengths = {201, 202, 203, 204, 205, 206};
        final InSituConfig inSituConfig = new InSituConfig(wavelengths);

        assertEquals(205.0, inSituConfig.getReferenceWavelength(), 1e-8);
    }

    @Test
    public void testGetSpecificAbsorptions() {
        final double[] expectedAbsorptions = new double[]{0.0045136, 0.006838, 0.015432, 0.044128, 0.057732, 0.43672};
        final double[] wavelengths = {412.7, 442.0, 490.9, 531.4, 551.1, 668.1};
        final InSituConfig inSituConfig = new InSituConfig(wavelengths);

        final double[] specificAbsorptions = inSituConfig.getSpecificAbsorptions();

        assertArrayEquals(expectedAbsorptions, specificAbsorptions, 1e-8);
    }

    @Test
    public void testGetAwAtWavelength() {
        assertEquals(0.00473, InSituConfig.getAwAtWavelength(410.0), 1e-8);  // lower bound

        assertEquals(0.0619, InSituConfig.getAwAtWavelength(560.0), 1e-8);
        assertEquals(0.06274, InSituConfig.getAwAtWavelength(561.0), 1e-8);
        assertEquals(0.06358, InSituConfig.getAwAtWavelength(562.0), 1e-8);
        assertEquals(0.064, InSituConfig.getAwAtWavelength(562.5), 1e-8);

        assertEquals(0.516, InSituConfig.getAwAtWavelength(690.0), 1e-8);    // upper bound

        // and to be sure, check against the SeaWIFS config supplied by PML
        assertEquals(0.004562, InSituConfig.getAwAtWavelength(412.0), 1e-8);
        assertEquals(0.00707, InSituConfig.getAwAtWavelength(443.0), 1e-8);
        assertEquals(0.015, InSituConfig.getAwAtWavelength(490.0), 1e-8);
        assertEquals(0.0325, InSituConfig.getAwAtWavelength(510.0), 1e-8);
        assertEquals(0.0596, InSituConfig.getAwAtWavelength(555.0), 1e-8);
        assertEquals(0.0596, InSituConfig.getAwAtWavelength(555.0), 1e-8);
        assertEquals(0.4346, InSituConfig.getAwAtWavelength(667.0), 1e-8);
    }

    @Test
    public void testGetAwAtWavelength_outOfBounds() {
        try {
            InSituConfig.getAwAtWavelength(409.0);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }

        try {
            InSituConfig.getAwAtWavelength(691.0);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testGetBbwAtWavelength() {
        assertEquals(0.0059145, InSituConfig.getBbwAtWavelength(410.0), 1e-8);  // lower bound

        assertEquals(0.015, InSituConfig.getAwAtWavelength(490.0), 1e-8);
        assertEquals(0.01548, InSituConfig.getAwAtWavelength(491.0), 1e-8);
        assertEquals(0.01596, InSituConfig.getAwAtWavelength(492.0), 1e-8);
        assertEquals(0.0162, InSituConfig.getAwAtWavelength(492.5), 1e-8);

        assertEquals(0.0006626, InSituConfig.getBbwAtWavelength(690.0), 1e-8);    // upper bound

        // and to be sure, check against the MERIS config supplied by PML
        assertEquals(0.00573196, InSituConfig.getBbwAtWavelength(413.0), 1e-6);
        assertEquals(0.00424592, InSituConfig.getBbwAtWavelength(443.0), 1e-6);
        assertEquals(0.00276835, InSituConfig.getBbwAtWavelength(490.0), 1e-7);
        assertEquals(0.00233870, InSituConfig.getBbwAtWavelength(510.0), 1e-7);
        assertEquals(0.00157958, InSituConfig.getBbwAtWavelength(560.0), 1e-7);
        assertEquals(0.000772104, InSituConfig.getBbwAtWavelength(665.0), 1e-8);
    }

    @Test
    public void testGetBbwAtWavelength_outOfBounds() {
        try {
            InSituConfig.getBbwAtWavelength(407.0);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }

        try {
            InSituConfig.getBbwAtWavelength(692.0);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testGetSpecificBackscatters() {
        final double[] expectedBackscatters = new double[]{0.005750084, 0.00428726, 0.002747196, 0.001967716, 0.00168922, 0.000757392};
        final double[] wavelengths = {412.7, 442.0, 490.9, 531.4, 551.1, 668.1};
        final InSituConfig inSituConfig = new InSituConfig(wavelengths);

        final double[] specficBackscatters = inSituConfig.getSpecficBackscatters();

        assertArrayEquals(expectedBackscatters, specficBackscatters, 1e-8);
    }
}
