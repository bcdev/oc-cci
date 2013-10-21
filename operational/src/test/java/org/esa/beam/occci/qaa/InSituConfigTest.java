package org.esa.beam.occci.qaa;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InSituConfigTest {

    @Test
    public void testGetAwCoefficients() {
        final InSituConfig config= new InSituConfig();
        final double[] aw_coeffs = config.getAwCoefficients();
        assertEquals(3, aw_coeffs.length);
        assertEquals(-1.146, aw_coeffs[0], 1e-8);
        assertEquals(-1.366, aw_coeffs[1], 1e-8);
        assertEquals(-0.469, aw_coeffs[2], 1e-8);
    }

}
