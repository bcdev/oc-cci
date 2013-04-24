package org.esa.beam.occci.qaa.binning;


import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class QaaConfigTest {

    @Test
    public void testConstruction() {
        final QaaConfig config = new QaaConfig();

        final int[] a_total_out_indices = config.getA_total_out_indices();
        assertNotNull(a_total_out_indices);
        assertEquals(0, a_total_out_indices.length);

        final int[] a_pig_out_indices = config.getA_pig_out_indices();
        assertNotNull(a_pig_out_indices);
        assertEquals(0, a_pig_out_indices.length);

        final int[] a_ys_out_indices = config.getA_ys_out_indices();
        assertNotNull(a_ys_out_indices);
        assertEquals(0, a_ys_out_indices.length);

        final int[] bb_spm_out_indices = config.getBb_spm_out_indices();
        assertNotNull(bb_spm_out_indices);
        assertEquals(0, bb_spm_out_indices.length);

        final String[] bandNames = config.getBandNames();
        assertNotNull(bandNames);
        assertEquals(0, bandNames.length);
    }
}
