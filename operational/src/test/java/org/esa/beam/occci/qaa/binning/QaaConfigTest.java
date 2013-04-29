package org.esa.beam.occci.qaa.binning;


import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class QaaConfigTest {

    @Test
    public void testConstruction() {
        final QaaConfig config = new QaaConfig();

        final int[] a_total_out_indices = config.getATotalOutIndices();
        assertNotNull(a_total_out_indices);
        assertEquals(0, a_total_out_indices.length);

        final int[] a_pig_out_indices = config.getAPigOutIndices();
        assertNotNull(a_pig_out_indices);
        assertEquals(0, a_pig_out_indices.length);

        final int[] a_ys_out_indices = config.getAYsOutIndices();
        assertNotNull(a_ys_out_indices);
        assertEquals(0, a_ys_out_indices.length);

        final int[] bb_spm_out_indices = config.getBbSpmOutIndices();
        assertNotNull(bb_spm_out_indices);
        assertEquals(0, bb_spm_out_indices.length);

        final String[] bandNames = config.getBandNames();
        assertNotNull(bandNames);
        assertEquals(0, bandNames.length);
    }
}
