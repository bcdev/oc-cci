package org.esa.beam.occci.biascorrect;

import org.esa.beam.binning.BinContext;
import org.esa.beam.binning.WritableVector;
import org.esa.beam.binning.support.ObservationImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static junit.framework.TestCase.assertNotNull;

public class AggregatorBiasCorrectTest {

    private AggregatorBiasCorrect.Config config;
    private BinContext binContext;

    @Before
    public void setUp() {
        config = new AggregatorBiasCorrect.Config();
        binContext = createBinContext();
    }

    @Test
    public void testCreateDateIndexFromConfig() {
        config.startYear = 2011;
        config.endYear = 2014;

        final DateIndex dateIndex = AggregatorBiasCorrect.createFrom(config);
        assertNotNull(dateIndex);
        assertEquals(2011, dateIndex.getStartYear());
        assertEquals(2014, dateIndex.getStopYear());
    }

    @Test
    public void testCreateDateIndexFromConfig_invalidDateCombination() {
        config.startYear = 2014;
        config.endYear = 2011;

        try {
            AggregatorBiasCorrect.createFrom(config);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testCreateFeatureNames_empty() {
        final String[] featureNames = AggregatorBiasCorrect.createFeatureNames(config);
        assertNotNull(featureNames);
        assertEquals(0, featureNames.length);
    }

    @Test
    public void testCreateFeatureNames() {
        config.varNames = new String[]{"rrs_0", "rrs_1", "rrs_2"};

        final String[] featureNames = AggregatorBiasCorrect.createFeatureNames(config);
        assertNotNull(featureNames);
        assertEquals(4, featureNames.length);
        assertEquals("rrs_0", featureNames[0]);
        assertEquals("rrs_1", featureNames[1]);
        assertEquals("rrs_2", featureNames[2]);
        assertEquals("dateIndex", featureNames[3]);
    }

    @Test
    public void testInitSpatial() {
        config.varNames = new String[]{"rrs_0", "rrs_1", "rrs_2"};
        final TestVector testVector = new TestVector(4);

        final AggregatorBiasCorrect aggregatorBiasCorrect = new AggregatorBiasCorrect(config);

        aggregatorBiasCorrect.initSpatial(binContext, testVector);

        assertEquals(Float.NaN, testVector.get(0), 1e-6);
        assertEquals(Float.NaN, testVector.get(1), 1e-6);
        assertEquals(Float.NaN, testVector.get(2), 1e-6);
        assertEquals(DateIndex.INVALID, testVector.get(3), 1e-6);
    }

    @Test
    public void testAggregateSpatial() {
        config.varNames = new String[]{"rrs_0", "rrs_1", "rrs_2"};
        final TestVector spatialVector = new TestVector(4);

        final AggregatorBiasCorrect aggregatorBiasCorrect = new AggregatorBiasCorrect(config);

        aggregatorBiasCorrect.initSpatial(binContext, spatialVector);

        aggregatorBiasCorrect.aggregateSpatial(binContext, new ObservationImpl(-1, -1, 3416.987, 1.8f, 2.8f, 3.8f), spatialVector);

        System.out.println(new DateIndex(2005, 2010).get(3416.987));

        assertEquals(1.8f, spatialVector.get(0), 1e-6);
        assertEquals(2.8f, spatialVector.get(1), 1e-6);
        assertEquals(3.8f, spatialVector.get(2), 1e-6);
        assertEquals(52, spatialVector.get(3), 1e-6);
    }

    @Test
    public void testAggregateSpatial_mjdOutOfRange() {
        config.varNames = new String[]{"rrs_0", "rrs_1", "rrs_2"};
        final TestVector spatialVector = new TestVector(4);

        final AggregatorBiasCorrect aggregatorBiasCorrect = new AggregatorBiasCorrect(config);

        aggregatorBiasCorrect.initSpatial(binContext, spatialVector);

        aggregatorBiasCorrect.aggregateSpatial(binContext, new ObservationImpl(-1, -1, 834.007, 2.6f, 3.5f, 2.4f), spatialVector);

        System.out.println(new DateIndex(2005, 2010).get(3416.987));

        assertEquals(2.6f, spatialVector.get(0), 1e-6);
        assertEquals(3.5f, spatialVector.get(1), 1e-6);
        assertEquals(2.4f, spatialVector.get(2), 1e-6);
        assertEquals(DateIndex.INVALID, spatialVector.get(3), 1e-6);
    }

    private class TestVector implements WritableVector {

        private final float[] data;

        private TestVector(int size) {
            data = new float[size];
        }

        @Override
        public void set(int index, float element) {
            data[index] = element;
        }

        @Override
        public int size() {
            return data.length;
        }

        @Override
        public float get(int index) {
            return data[index];
        }
    }

    private static BinContext createBinContext() {
        return new BinContext() {
            private HashMap<String, Object> map = new HashMap<String, Object>();

            @Override
            public long getIndex() {
                return 0;
            }

            @Override
            public <T> T get(String name) {
                return (T) map.get(name);
            }

            @Override
            public void put(String name, Object value) {
                map.put(name, value);
            }
        };
    }
}
