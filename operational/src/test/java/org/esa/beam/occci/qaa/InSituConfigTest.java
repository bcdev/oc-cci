package org.esa.beam.occci.qaa;


import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class InSituConfigTest {

    @Test
    public void testGetAwCoefficients() {
        final InSituConfig config= new InSituConfig(new double[0]);

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
    public void testGetAbsorptions() {
        final double[] expectedAbsorptions = new double[] {0.0045136, 0.006838, 0.015432, 0.044128, 0.057732, 0.43672};
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
}
