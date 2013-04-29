package org.esa.beam.occci.bandshift.binning;

import org.esa.beam.binning.support.VectorImpl;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ResultMapperTest {


    @Test
    public void testAssignInvalidSensorName() {
        final BandShiftConfig config= new BandShiftConfig("macht_bäng");
        config.setSensorName("Nikon");

        try {
            new ResultMapper(config);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testAssignInvalidWavelength() {
        final BandShiftConfig config= new BandShiftConfig("macht_bäng");
        config.setSensorName("SEAWIFS");
        config.setOutputCenterWavelengths(new int[] {876});

        try {
            new ResultMapper(config);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testAssign_MODIS_noBands() {
        final BandShiftConfig config = new BandShiftConfig("who");
        config.setSensorName("MODISA");
        config.setOutputCenterWavelengths(new int[]{});

        final ResultMapper mapper = new ResultMapper(config);

        final VectorImpl postVector = createVector(2);
        final double[] rrs_shifted = new double[] {-1.0, -1.0};
        final double[] rrs_in = new double[0];

        mapper.assign(rrs_in, rrs_shifted, postVector);

        // check nothing happened
        assertEquals(-1.0, postVector.get(0), 1e-7);
        assertEquals(-1.0, postVector.get(1), 1e-7);
    }

    @Test
    public void testAssign_MERIS_twoBands_justInput() {
        final BandShiftConfig config = new BandShiftConfig("the");
        config.setSensorName("MERIS");
        config.setOutputCenterWavelengths(new int[]{413, 443});

        final ResultMapper mapper = new ResultMapper(config);

        final VectorImpl postVector = createVector(2);
        final double[] rrs_shifted = new double[] {-1.0, -1.0};
        final double[] rrs_in = new double[]{0.5, 0.6};

        mapper.assign(rrs_in, rrs_shifted, postVector);

        assertEquals(0.5, postVector.get(0), 1e-7);
        assertEquals(0.6, postVector.get(1), 1e-7);
    }

    @Test
    public void testAssign_SeaWifs_threeBands_justShifted() {
        final BandShiftConfig config = new BandShiftConfig("f**k");
        config.setSensorName("SEAWIFS");
        config.setOutputCenterWavelengths(new int[]{413, 547, 560});

        final ResultMapper mapper = new ResultMapper(config);

        final VectorImpl postVector = createVector(3);
        final double[] rrs_shifted = new double[] {0.6, 0.7, 0.8, 0.9, 1.0, 1.1};
        final double[] rrs_in = new double[]{-1.0, -1.0, -1.0};

        mapper.assign(rrs_in, rrs_shifted, postVector);

        assertEquals(0.6, postVector.get(0), 1e-7);
        assertEquals(0.9, postVector.get(1), 1e-7);
        assertEquals(1.0, postVector.get(2), 1e-7);
    }

    @Test
    public void testAssign_MODIS_fourBands_mixedInAndOut() {
        final BandShiftConfig config = new BandShiftConfig("is");
        config.setSensorName("MODISA");
        config.setOutputCenterWavelengths(new int[]{412, 413, 547, 555});

        final ResultMapper mapper = new ResultMapper(config);

        final VectorImpl postVector = createVector(4);
        final double[] rrs_shifted = new double[] {0.6, 0.7, 0.8, 0.9, 1.0, 1.1};
        final double[] rrs_in = new double[]{1.2, 1.3, 1.4, 1.5, 1.6};

        mapper.assign(rrs_in, rrs_shifted, postVector);

        assertEquals(1.2, postVector.get(0), 1e-7);
        assertEquals(0.6, postVector.get(1), 1e-7);
        assertEquals(1.6, postVector.get(2), 1e-7);
        assertEquals(1.0, postVector.get(3), 1e-7);
    }

    private VectorImpl createVector(int numBands) {
        final float[] data = new float[numBands];
        for (int i = 0; i < data.length; i++) {
             data[i] = -1.f;
        }
        return new VectorImpl(data);
    }
}
