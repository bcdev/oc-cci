package org.esa.beam.occci.biascorrect;

import org.esa.beam.binning.Aggregator;
import org.esa.beam.binning.BinContext;
import org.esa.beam.binning.WritableVector;
import org.esa.beam.binning.support.ObservationImpl;
import org.esa.beam.binning.support.VariableContextImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static junit.framework.TestCase.assertNotNull;

public class AggregatorBiasCorrectTest {

    private AggregatorBiasCorrect.Config config;
    private BinContext binContext;
    private VariableContextImpl ctx = new VariableContextImpl();

    @Before
    public void setUp() {
        config = new AggregatorBiasCorrect.Config();
        binContext = createBinContext();
        ctx.defineVariable("rrs_0");
        ctx.defineVariable("rrs_1");
        ctx.defineVariable("rrs_2");
        ctx.defineVariable("rrs_3");
    }

    @Test
    public void testCreateDateIndexFromConfig() {
        config.startYear = 2011;
        config.endYear = 2014;

        final DateIndexCalculator dateIndexCalculator = AggregatorBiasCorrect.Descriptor.createDateIndexCalculator(config);
        assertNotNull(dateIndexCalculator);
        assertEquals(2011, dateIndexCalculator.getStartYear());
        assertEquals(2014, dateIndexCalculator.getStopYear());
    }

    @Test
    public void testCreateDateIndexFromConfig_invalidDateCombination() {
        config.startYear = 2014;
        config.endYear = 2011;

        try {
            AggregatorBiasCorrect.Descriptor.createDateIndexCalculator(config);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testCreateSpatialFeatureNames_empty() {
        final String[] featureNames = AggregatorBiasCorrect.Descriptor.createSpatialFeatureNames(config);
        assertNotNull(featureNames);
        assertEquals(0, featureNames.length);
    }

    @Test
    public void testCreateSpatialFeatureNames() {
        config.varNames = new String[]{"rrs_0", "rrs_1", "rrs_2"};

        final String[] featureNames = AggregatorBiasCorrect.Descriptor.createSpatialFeatureNames(config);
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
        final String[] featureNames = AggregatorBiasCorrect.Descriptor.createTemporalFeatureNames(config, dateIndexCalculator);
        assertNotNull(featureNames);
        assertEquals(0, featureNames.length);
    }

    @Test
    public void testCreateTemporalFeatureNames() {
        config.varNames = new String[]{"rrs_0"};
        final DateIndexCalculator dateIndexCalculator = new DateIndexCalculator(2008, 2008);

        final String[] featureNames = AggregatorBiasCorrect.Descriptor.createTemporalFeatureNames(config, dateIndexCalculator);
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
        final String[] featureNames = AggregatorBiasCorrect.Descriptor.createOutputFeatureNames(config);
        assertNotNull(featureNames);
        assertEquals(0, featureNames.length);
    }

    @Test
    public void testCreateOutputFeatureNames() {
        config.varNames = new String[]{"rrs_0", "rrs_1", "rrs_2"};

        final String[] featureNames = AggregatorBiasCorrect.Descriptor.createOutputFeatureNames(config);
        assertNotNull(featureNames);
        assertEquals(3, featureNames.length);
        assertEquals("rrs_0", featureNames[0]);
        assertEquals("rrs_1", featureNames[1]);
        assertEquals("rrs_2", featureNames[2]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAggregator_invalid() {
        config.varNames = new String[]{"rrs_0", "rrs_1", "rrs_5"};
        new AggregatorBiasCorrect.Descriptor().createAggregator(ctx, config);
        // "rrs_5" is not in the context
    }

    @Test
    public void testInitSpatial() {
        config.varNames = new String[]{"rrs_0", "rrs_1", "rrs_2"};
        final TestVector testVector = new TestVector(4);
        Aggregator aggregatorBiasCorrect = new AggregatorBiasCorrect.Descriptor().createAggregator(ctx, config);

        aggregatorBiasCorrect.initSpatial(binContext, testVector);

        assertEquals(0f, testVector.get(0), 1e-6);
        assertEquals(0f, testVector.get(1), 1e-6);
        assertEquals(0f, testVector.get(2), 1e-6);
        assertEquals(0f, testVector.get(3), 1e-6);
    }

    @Test
    public void testAggregateSpatial() {
        config.varNames = new String[]{"rrs_0", "rrs_2"};
        config.startYear = 2005;
        config.endYear = 2010;
        final TestVector spatialVector = new TestVector(3);

        Aggregator aggregatorBiasCorrect = new AggregatorBiasCorrect.Descriptor().createAggregator(ctx, config);

        aggregatorBiasCorrect.initSpatial(binContext, spatialVector);

        double mjd = 3416.987;
        float[] obsMeasurements = {1.8f, 2.8f, 3.8f, 4.8f};
        ObservationImpl observation = new ObservationImpl(-1, -1, mjd, obsMeasurements);
        aggregatorBiasCorrect.aggregateSpatial(binContext, observation, spatialVector);

        assertEquals(1.8f, spatialVector.get(0), 1e-6);
        assertEquals(3.8f, spatialVector.get(1), 1e-6);
        assertEquals(52, spatialVector.get(2), 1e-6);
    }

    @Test
    public void testAggregateSpatial_mjdOutOfRange() {
        config.varNames = new String[]{"rrs_0", "rrs_1", "rrs_2"};
        final TestVector spatialVector = new TestVector(4);

        Aggregator aggregatorBiasCorrect = new AggregatorBiasCorrect.Descriptor().createAggregator(ctx, config);

        aggregatorBiasCorrect.initSpatial(binContext, spatialVector);

        aggregatorBiasCorrect.aggregateSpatial(binContext, new ObservationImpl(-1, -1, 834.007, 2.6f, 3.5f, 2.4f), spatialVector);

        assertEquals(2.6f, spatialVector.get(0), 1e-6);
        assertEquals(3.5f, spatialVector.get(1), 1e-6);
        assertEquals(2.4f, spatialVector.get(2), 1e-6);
        assertEquals(DateIndexCalculator.INVALID, spatialVector.get(3), 1e-6);
    }

    @Test
    public void testAggregateTemporal_noObservations() {
        config.varNames = new String[]{"rrs_1"};
        config.startYear = 2005;
        config.endYear = 2006;
        final TestVector spatialVector = new TestVector(2);
        final TestVector temporalVector = new TestVector(48);

        Aggregator aggregatorBiasCorrect = new AggregatorBiasCorrect.Descriptor().createAggregator(ctx, config);

        aggregatorBiasCorrect.aggregateTemporal(binContext, spatialVector, 0, temporalVector);

        for (int i = 0; i < 48; i++) {
            assertEquals(0.f, temporalVector.get(i), 1e-6);
        }
    }

    @Test
    public void testAggregateTemporal_invalidObservations() {
        config.varNames = new String[]{"rrs_1"};
        config.startYear = 2005;
        config.endYear = 2006;
        final TestVector spatialVector = new TestVector(2);
        spatialVector.set(0, Float.NaN);
        spatialVector.set(1, 14);       // march 2006
        final TestVector temporalVector = new TestVector(48);

        Aggregator aggregatorBiasCorrect = new AggregatorBiasCorrect.Descriptor().createAggregator(ctx, config);

        aggregatorBiasCorrect.aggregateTemporal(binContext, spatialVector, 1, temporalVector);

        for (int i = 0; i < 48; i++) {
            assertEquals(0.f, temporalVector.get(i), 1e-6);
        }
    }

    @Test
    public void testAggregateTemporal_oneBand_twoYears() {
        config.varNames = new String[]{"rrs_1"};
        config.startYear = 2005;
        config.endYear = 2006;
        final TestVector spatialVector = new TestVector(2);
        spatialVector.set(0, 14.786f);
        spatialVector.set(1, 14);       // march 2006
        final TestVector temporalVector = new TestVector(48);

        Aggregator aggregatorBiasCorrect = new AggregatorBiasCorrect.Descriptor().createAggregator(ctx, config);

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
    public void testAggregateTemporal_twoBands_oneYear() {
        config.varNames = new String[]{"rrs_1", "rrs_2"};
        config.startYear = 2006;
        config.endYear = 2006;
        final TestVector spatialVector = new TestVector(3);
        spatialVector.set(0, 16.786f);
        spatialVector.set(1, 19.013f);
        spatialVector.set(2, 7);       // august 2006
        final TestVector temporalVector = new TestVector(48);

        Aggregator aggregatorBiasCorrect = new AggregatorBiasCorrect.Descriptor().createAggregator(ctx, config);

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
    public void testAggregateTemporal_oneBand_oneYear_twoMeasures() {
        config.varNames = new String[]{"rrs_1"};
        config.startYear = 2006;
        config.endYear = 2006;
        final TestVector spatialVector_1 = new TestVector(2);
        spatialVector_1.set(0, 8.f);
        spatialVector_1.set(1, 4);       // may 2006
        final TestVector spatialVector_2 = new TestVector(2);
        spatialVector_2.set(0, 10.f);
        spatialVector_2.set(1, 4);       // may 2006
        final TestVector temporalVector = new TestVector(48);

        Aggregator aggregatorBiasCorrect = new AggregatorBiasCorrect.Descriptor().createAggregator(ctx, config);

        aggregatorBiasCorrect.aggregateTemporal(binContext, spatialVector_1, 1, temporalVector);
        aggregatorBiasCorrect.aggregateTemporal(binContext, spatialVector_2, 1, temporalVector);

        assertEquals(8.f + 10.f, temporalVector.get(8), 1e-6);
        assertEquals(2.f, temporalVector.get(9), 1e-6);

        for (int i = 0; i < 48; i++) {
            if (i == 8 || i == 9) {
                continue;
            }
            assertEquals(0.f, temporalVector.get(i), 1e-6);
        }
    }

    @Test
    public void testInitTemporal() {
        final TestVector spatialVector = new TestVector(6);
        Aggregator aggregatorBiasCorrect = new AggregatorBiasCorrect.Descriptor().createAggregator(ctx, config);

        aggregatorBiasCorrect.initTemporal(binContext, spatialVector);

        final int size = spatialVector.size();
        for (int i = 0; i < size; i++) {
            assertEquals(0.f, spatialVector.get(i), 1e-6);
        }
    }

    @Test
    public void testAggregateMonths_oneBand_oneYear() {
        final TestVector temporalVector = new TestVector(24);   // 1 year
        temporalVector.set(0, 10);  // two measurements in Jan
        temporalVector.set(1, 2);
        temporalVector.set(4, 18);  // three measurements in March
        temporalVector.set(5, 3);
        temporalVector.set(22, 16);  // five measurements in Dec
        temporalVector.set(23, 5);

        final float[] monthlyMeans = AggregatorBiasCorrect.aggregateMonths(temporalVector, 1, 0);
        assertNotNull(monthlyMeans);
        assertEquals(12, monthlyMeans.length);

        assertEquals(5.f, monthlyMeans[0], 1e-6);
        assertEquals(Float.NaN, monthlyMeans[1], 1e-6);
        assertEquals(6.f, monthlyMeans[2], 1e-6);
        assertEquals(Float.NaN, monthlyMeans[3], 1e-6);
        assertEquals(Float.NaN, monthlyMeans[4], 1e-6);
        assertEquals(Float.NaN, monthlyMeans[5], 1e-6);
        assertEquals(Float.NaN, monthlyMeans[6], 1e-6);
        assertEquals(Float.NaN, monthlyMeans[7], 1e-6);
        assertEquals(Float.NaN, monthlyMeans[8], 1e-6);
        assertEquals(Float.NaN, monthlyMeans[9], 1e-6);
        assertEquals(Float.NaN, monthlyMeans[10], 1e-6);
        assertEquals(3.2f, monthlyMeans[11], 1e-6);
    }

    @Test
    public void testAggregateMonths_twoBands_oneYear() {
        final TestVector temporalVector = new TestVector(48);   // 1 year, two bands
        temporalVector.set(0, 11);  // two measurements in Jan, band 1
        temporalVector.set(1, 2);
        temporalVector.set(24, 12);  // two measurements in Jan, band 2
        temporalVector.set(25, 2);
        temporalVector.set(4, 14);  // three measurements in March, band 1
        temporalVector.set(5, 3);
        temporalVector.set(28, 15);  // three measurements in March, band 2
        temporalVector.set(29, 3);
        temporalVector.set(46, 17);  // five measurements in Dec, band 2
        temporalVector.set(47, 5);

        // first band
        float[] monthlyMeans = AggregatorBiasCorrect.aggregateMonths(temporalVector, 1, 0);
        assertEquals(5.5f, monthlyMeans[0], 1e-6);
        assertEquals(Float.NaN, monthlyMeans[1], 1e-6);
        assertEquals(4.6666667f, monthlyMeans[2], 1e-6);
        assertEquals(Float.NaN, monthlyMeans[3], 1e-6);
        assertEquals(Float.NaN, monthlyMeans[4], 1e-6);
        assertEquals(Float.NaN, monthlyMeans[5], 1e-6);
        assertEquals(Float.NaN, monthlyMeans[6], 1e-6);
        assertEquals(Float.NaN, monthlyMeans[7], 1e-6);
        assertEquals(Float.NaN, monthlyMeans[8], 1e-6);
        assertEquals(Float.NaN, monthlyMeans[9], 1e-6);
        assertEquals(Float.NaN, monthlyMeans[10], 1e-6);
        assertEquals(Float.NaN, monthlyMeans[11], 1e-6);
        // second band
        monthlyMeans = AggregatorBiasCorrect.aggregateMonths(temporalVector, 1, 1);
        assertEquals(6.f, monthlyMeans[0], 1e-6);
        assertEquals(Float.NaN, monthlyMeans[1], 1e-6);
        assertEquals(5.f, monthlyMeans[2], 1e-6);
        assertEquals(Float.NaN, monthlyMeans[3], 1e-6);
        assertEquals(Float.NaN, monthlyMeans[4], 1e-6);
        assertEquals(Float.NaN, monthlyMeans[5], 1e-6);
        assertEquals(Float.NaN, monthlyMeans[6], 1e-6);
        assertEquals(Float.NaN, monthlyMeans[7], 1e-6);
        assertEquals(Float.NaN, monthlyMeans[8], 1e-6);
        assertEquals(Float.NaN, monthlyMeans[9], 1e-6);
        assertEquals(Float.NaN, monthlyMeans[10], 1e-6);
        assertEquals(3.4f, monthlyMeans[11], 1e-6);
    }

    @Test
    public void testAggregateMonths_oneBand_threeYears() {
        final TestVector temporalVector = new TestVector(72);   // 3 years
        temporalVector.set(0, 10);  // two measurements in Jan year 1
        temporalVector.set(1, 2);
        temporalVector.set(24, 12);  // two measurements in Jan year 2
        temporalVector.set(25, 2);
        temporalVector.set(28, 18);  // three measurements in March year 2
        temporalVector.set(29, 3);
        temporalVector.set(52, 19);  // three measurements in March year 3
        temporalVector.set(53, 3);
        temporalVector.set(70, 18);  // five measurements in Dec year 3
        temporalVector.set(71, 5);

        final float[] monthlyMeans = AggregatorBiasCorrect.aggregateMonths(temporalVector, 3, 0);
        assertNotNull(monthlyMeans);
        assertEquals(12, monthlyMeans.length);

        assertEquals(5.5f, monthlyMeans[0], 1e-6);
        assertEquals(Float.NaN, monthlyMeans[1], 1e-6);
        assertEquals(6.1666667f, monthlyMeans[2], 1e-6);
        assertEquals(Float.NaN, monthlyMeans[3], 1e-6);
        assertEquals(Float.NaN, monthlyMeans[4], 1e-6);
        assertEquals(Float.NaN, monthlyMeans[5], 1e-6);
        assertEquals(Float.NaN, monthlyMeans[6], 1e-6);
        assertEquals(Float.NaN, monthlyMeans[7], 1e-6);
        assertEquals(Float.NaN, monthlyMeans[8], 1e-6);
        assertEquals(Float.NaN, monthlyMeans[9], 1e-6);
        assertEquals(Float.NaN, monthlyMeans[10], 1e-6);
        assertEquals(3.6f, monthlyMeans[11], 1e-6);
    }

    @Test
    public void testAggregateYear() {
        final float[] monthlyMeans = new float[12];
        monthlyMeans[0] = 10.f;
        monthlyMeans[1] = 11.f;
        monthlyMeans[2] = 12.f;
        monthlyMeans[3] = 13.f;
        monthlyMeans[4] = 14.f;
        monthlyMeans[5] = 15.f;
        monthlyMeans[6] = 16.f;
        monthlyMeans[7] = 17.f;
        monthlyMeans[8] = 18.f;
        monthlyMeans[9] = 19.f;
        monthlyMeans[10] = 20.f;
        monthlyMeans[11] = 21.f;

        assertEquals(15.5f, AggregatorBiasCorrect.aggregateYear(monthlyMeans, 0.3f), 1e-6);
    }

    @Test
    public void testAggregateYear_missingMonths() {
        final float[] monthlyMeans = new float[12];
        monthlyMeans[0] = 10.f;
        monthlyMeans[1] = Float.NaN;
        monthlyMeans[2] = 12.f;
        monthlyMeans[3] = Float.NaN;
        monthlyMeans[4] = 14.f;
        monthlyMeans[5] = Float.NaN;
        monthlyMeans[6] = 16.f;
        monthlyMeans[7] = Float.NaN;
        monthlyMeans[8] = 18.f;
        monthlyMeans[9] = Float.NaN;
        monthlyMeans[10] = 20.f;
        monthlyMeans[11] = Float.NaN;

        assertEquals(15.f, AggregatorBiasCorrect.aggregateYear(monthlyMeans, 90.f), 1e-6);
    }

    @Test
    public void testAggregateYear_noValidInput() {
        final float[] monthlyMeans = new float[12];
        for (int i = 0; i < monthlyMeans.length; i++) {
            monthlyMeans[i] = Float.NaN;
        }

        final float noDataValue = -23.f;
        assertEquals(noDataValue, AggregatorBiasCorrect.aggregateYear(monthlyMeans, noDataValue), 1e-6);
    }

    @Test
    public void testComputeOutput() {
        config.varNames = new String[]{"rrs_1"};
        config.startYear = 2003;
        config.endYear = 2004;
        final TestVector outputVector = new TestVector(1);
        final TestVector temporalVector = new TestVector(48);   // one variable, two years

        temporalVector.set(24, 12); // three times 4 in Jan 2004
        temporalVector.set(25, 3);


        Aggregator aggregatorBiasCorrect = new AggregatorBiasCorrect.Descriptor().createAggregator(ctx, config);

        aggregatorBiasCorrect.computeOutput(temporalVector, outputVector);
        assertEquals(4.f, outputVector.get(0), 1e-6);
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
