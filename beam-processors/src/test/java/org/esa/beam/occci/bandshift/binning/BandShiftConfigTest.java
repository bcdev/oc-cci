package org.esa.beam.occci.bandshift.binning;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BandShiftConfigTest {

    @Test
    public void testConstruction() {
        final BandShiftConfig bandShiftConfig = new BandShiftConfig("whatever");

        String[] bandNames = bandShiftConfig.getRrsBandNames();
        assertNotNull(bandNames);
        assertEquals(0, bandNames.length);
        bandNames = bandShiftConfig.getIopBandNames();
        assertNotNull(bandNames);
        assertEquals(0, bandNames.length);

        final int[] outputCenterWavelengths = bandShiftConfig.getOutputCenterWavelengths();
        assertNotNull(outputCenterWavelengths);
        assertEquals(0, outputCenterWavelengths.length);
    }
}
