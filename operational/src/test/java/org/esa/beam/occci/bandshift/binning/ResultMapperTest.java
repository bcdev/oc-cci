package org.esa.beam.occci.bandshift.binning;

import org.esa.beam.binning.support.VectorImpl;
import org.junit.Test;

public class ResultMapperTest {

    @Test
    public void testAssign_MODIS_noBands() {
        final BandShiftConfig config = new BandShiftConfig("who");
        config.setSensorName("MODISA");
        config.setOutputCenterWavelengths(new int[]{});

        final ResultMapper mapper = new ResultMapper(config);

        final VectorImpl outVector = new VectorImpl(new float[]{-1, -1});

        // @todo 1 tb/tb continue here
    }
}
