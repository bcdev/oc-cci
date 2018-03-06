package org.esa.beam.occci.qaa.binning;


import org.junit.Test;

import static org.junit.Assert.*;

public class QaaConfigTest {

    @Test
    public void testConstruction() {
        final QaaConfig config = new QaaConfig();

        final int[] atotOutIndices = config.getAtotOutIndices();
        assertNotNull(atotOutIndices);
        assertEquals(0, atotOutIndices.length);

        final int[] aphOutIndices = config.getAphOutIndices();
        assertNotNull(aphOutIndices);
        assertEquals(0, aphOutIndices.length);

        final int[] adgOutIndices = config.getAdgOutIndices();
        assertNotNull(adgOutIndices);
        assertEquals(0, adgOutIndices.length);

        final int[] bbpOutIndices = config.getBbpOutIndices();
        assertNotNull(bbpOutIndices);
        assertEquals(0, bbpOutIndices.length);

        final String[] bandNames = config.getBandNames();
        assertNotNull(bandNames);
        assertEquals(0, bandNames.length);
    }
}
