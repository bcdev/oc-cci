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

        final DateIndexCalculator dateIndexCalculator = AggregatorBiasCorrect.createFrom(config);
        assertNotNull(dateIndexCalculator);
        assertEquals(2011, dateIndexCalculator.getStartYear());
        assertEquals(2014, dateIndexCalculator.getStopYear());
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
    public void testCreateSpatialFeatureNames_empty() {
        final String[] featureNames = AggregatorBiasCorrect.createSpatialFeatureNames(config);
        assertNotNull(featureNames);
        assertEquals(0, featureNames.length);
    }

    @Test
    public void testCreateSpatialFeatureNames() {
        config.varNames = new String[]{"rrs_0", "rrs_1", "rrs_2"};

        final String[] featureNames = AggregatorBiasCorrect.createSpatialFeatureNames(config);
        assertNotNull(featureNames);
        assertEquals(4, featureNames.length);
        assertEquals("rrs_0", featureNames[0]);
        assertEquals("rrs_1", featureNames[1]);
        assertEquals("rrs_2", featureNames[2]);
        assertEquals("dateIndex", featureNames[3]);
    }

    @Test
    public void testCreateTemporalFeatureNames_empty() {
        final DateIndexCalculator dateIndexCalculator = new DateIndexCalculator(2008, 2011);
        final String[] featureNames = AggregatorBiasCorrect.createTemporalFeatureNames(config, dateIndexCalculator);
        assertNotNull(featureNames);
        assertEquals(0, featureNames.length);
    }

    @Test
    public void testCreateTemporalFeatureNames() {
        config.varNames = new String[]{"rrs_0"};
        final DateIndexCalculator dateIndexCalculator = new DateIndexCalculator(2008, 2008);

        final String[] featureNames = AggregatorBiasCorrect.createTemporalFeatureNames(config, dateIndexCalculator);
        assertNotNull(featureNames);
        assertEquals(24, featureNames.length);

        assertEquals("rrs_0_000_sum", featureNames[0]);
        assertEquals("rrs_0_000_count", featureNames[1]);

        assertEquals("rrs_0_002_sum", featureNames[4]);
        assertEquals("rrs_0_002_count", featureNames[5]);

        assertEquals("rrs_0_011_sum", featureNames[22]);
        assertEquals("rrs_0_011_count", featureNames[23]);
    }

    @Test
    public void testCreateOutputFeatureNames_empty() {
        final String[] featureNames = AggregatorBiasCorrect.createOutputFeatureNames(config);
        assertNotNull(featureNames);
        assertEquals(0, featureNames.length);
    }

    @Test
    public void testCreateOutputFeatureNames() {
        config.varNames = new String[]{"rrs_0", "rrs_1", "rrs_2"};

        final String[] featureNames = AggregatorBiasCorrect.createOutputFeatureNames(config);
        assertNotNull(featureNames);
        assertEquals(3, featureNames.length);
        assertEquals("rrs_0_mean", featureNames[0]);
        assertEquals("rrs_1_mean", featureNames[1]);
        assertEquals("rrs_2_mean", featureNames[2]);
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
        assertEquals(DateIndexCalculator.INVALID, testVector.get(3), 1e-6);
    }

    @Test
    public void testAggregateSpatial() {
        config.varNames = new String[]{"rrs_0", "rrs_1", "rrs_2"};
        final TestVector spatialVector = new TestVector(4);

        final AggregatorBiasCorrect aggregatorBiasCorrect = new AggregatorBiasCorrect(config);

        aggregatorBiasCorrect.initSpatial(binContext, spatialVector);

        aggregatorBiasCorrect.aggregateSpatial(binContext, new ObservationImpl(-1, -1, 3416.987, 1.8f, 2.8f, 3.8f), spatialVector);

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

        assertEquals(2.6f, spatialVector.get(0), 1e-6);
        assertEquals(3.5f, spatialVector.get(1), 1e-6);
        assertEquals(2.4f, spatialVector.get(2), 1e-6);
        assertEquals(DateIndexCalculator.INVALID, spatialVector.get(3), 1e-6);
    }

    @Test
    public void testAggregateTemporal_noObservations() {
        config.varNames = new String[]{"rrs_0"};
        config.startYear = 2005;
        config.endYear = 2006;
        final TestVector spatialVector = new TestVector(2);
        final TestVector temporalVector = new TestVector(48);

        final AggregatorBiasCorrect aggregatorBiasCorrect = new AggregatorBiasCorrect(config);

        aggregatorBiasCorrect.aggregateTemporal(binContext, spatialVector, 0, temporalVector);

        for (int i = 0; i < 48; i++) {
            assertEquals(0.f, temporalVector.get(i), 1e-6);
        }
    }

    @Test
    public void testAggregateTemporal_invalidObservations() {
        config.varNames = new String[]{"rrs_0"};
        config.startYear = 2005;
        config.endYear = 2006;
        final TestVector spatialVector = new TestVector(2);
        spatialVector.set(0, Float.NaN);
        spatialVector.set(1, 14);       // march 2006
        final TestVector temporalVector = new TestVector(48);

        final AggregatorBiasCorrect aggregatorBiasCorrect = new AggregatorBiasCorrect(config);

        aggregatorBiasCorrect.aggregateTemporal(binContext, spatialVector, 1, temporalVector);

        for (int i = 0; i < 48; i++) {
            assertEquals(0.f, temporalVector.get(i), 1e-6);
        }
    }

    @Test
    public void testAggregateTemporal_oneBand_twoYears() {
        config.varNames = new String[]{"rrs_0"};
        config.startYear = 2005;
        config.endYear = 2006;
        final TestVector spatialVector = new TestVector(2);
        spatialVector.set(0, 14.786f);
        spatialVector.set(1, 14);       // march 2006
        final TestVector temporalVector = new TestVector(48);

        final AggregatorBiasCorrect aggregatorBiasCorrect = new AggregatorBiasCorrect(config);

        aggregatorBiasCorrect.aggregateTemporal(binContext, spatialVector, 1, temporalVector);

        assertEquals(14.786f, temporalVector.get(28), 1e-6);
        assertEquals(1.f, temporalVector.get(29), 1e-6);

        for (int i = 0; i < 48; i++) {
            if (i == 28 || i == 29) {
                continue;
            }
            assertEquals(0.f, temporalVector.get(i), 1e-6);
        }
    }

    @Test
    public void testAggregateTemporal_twoBands_oneYears() {
        config.varNames = new String[]{"rrs_0", "rrs_1"};
        config.startYear = 2006;
        config.endYear = 2006;
        final TestVector spatialVector = new TestVector(3);
        spatialVector.set(0, 16.786f);
        spatialVector.set(1, 19.013f);
        spatialVector.set(2, 7);       // august 2006
        final TestVector temporalVector = new TestVector(48);

        final AggregatorBiasCorrect aggregatorBiasCorrect = new AggregatorBiasCorrect(config);

        aggregatorBiasCorrect.aggregateTemporal(binContext, spatialVector, 1, temporalVector);

        assertEquals(16.786f, temporalVector.get(14), 1e-6);
        assertEquals(1.f, temporalVector.get(15), 1e-6);

        assertEquals(19.013f, temporalVector.get(38), 1e-6);
        assertEquals(1.f, temporalVector.get(39), 1e-6);

        for (int i = 0; i < 48; i++) {
            if (i == 14 || i == 15 || i == 38 || i == 39) {
                continue;
            }
            assertEquals(0.f, temporalVector.get(i), 1e-6);
        }
    }

    @Test
    public void testInitTemporal() {
        final TestVector spatialVector = new TestVector(6);

        final AggregatorBiasCorrect aggregatorBiasCorrect = new AggregatorBiasCorrect(config);

        aggregatorBiasCorrect.initTemporal(binContext, spatialVector);

        final int size = spatialVector.size();
        for (int i = 0; i < size; i++) {
            assertEquals(0.f, spatialVector.get(i), 1e-6);
        }
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

            @SuppressWarnings("unchecked")
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
