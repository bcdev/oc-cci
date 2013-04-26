package org.esa.beam.occci.bandshift.binning;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class BandShiftConfigTest {

    @Test
    public void testConstruction() {
        final BandShiftConfig bandShiftConfig = new BandShiftConfig("whatever");

        final String[] bandNames = bandShiftConfig.getBandNames();
        assertNotNull(bandNames);
        assertEquals(0, bandNames.length);

        final int[] outputCenterWavelengths = bandShiftConfig.getOutputCenterWavelengths();
        assertNotNull(outputCenterWavelengths);
        assertEquals(0, outputCenterWavelengths.length);
    }
}
