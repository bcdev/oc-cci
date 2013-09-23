package org.esa.beam.occci.biascorrect;

import org.esa.beam.binning.*;
import org.esa.beam.framework.gpf.annotations.Parameter;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class AggregatorBiasCorrect extends AbstractAggregator {

    public static final String NAME = "OC-CCI-BIAS";
    private final DateIndexCalculator dateIndexCalculator;
    private int dateIdx;
    private int numReflecs;
    private final String[] varNames;

    public AggregatorBiasCorrect(Config config) {
        super(NAME, createSpatialFeatureNames(config),
                createTemporalFeatureNames(config, createFrom(config)),
                createOutputFeatureNames(config));

        dateIndexCalculator = createFrom(config);
        varNames = config.getVarNames();
    }

    @Override
    public void initSpatial(BinContext ctx, WritableVector vector) {
        final int size = vector.size();
        numReflecs = size - 1;
        for (int i = 0; i < numReflecs; i++) {
            vector.set(i, Float.NaN);
        }
        vector.set(numReflecs, DateIndexCalculator.INVALID);

        dateIdx = DateIndexCalculator.INVALID;
    }

    @Override
    public void aggregateSpatial(BinContext ctx, Observation observationVector, WritableVector spatialVector) {
        if (dateIdx == DateIndexCalculator.INVALID) {
            final int currentIndex = dateIndexCalculator.get(observationVector.getMJD());
            if (currentIndex != DateIndexCalculator.INVALID) {
                dateIdx = currentIndex;
            }
        }

        for (int i = 0; i < numReflecs; i++) {
            spatialVector.set(i, observationVector.get(i));
        }
        spatialVector.set(numReflecs, dateIdx);
    }

    @Override
    public void completeSpatial(BinContext ctx, int numSpatialObs, WritableVector spatialVector) {
        // nothing to do here tb 2013-09-18
    }

    @Override
    public void initTemporal(BinContext ctx, WritableVector vector) {
        final int size = vector.size();
        for (int i = 0; i < size; i++) {
            vector.set(i, 0.f);
        }
    }

    @Override
    public void aggregateTemporal(BinContext ctx, Vector spatialVector, int numSpatialObs, WritableVector temporalVector) {
        if (numSpatialObs == 0) {
            return;
        }

        final int indexCount = dateIndexCalculator.getIndexCount();
        final int variablesCount = getSpatialFeatureNames().length - 1;
        for (int i = 0; i < variablesCount; i++) {
            final float value = spatialVector.get(i);
            if (Float.isNaN(value)) {
                continue;
            }

            final int offset = i * 2 * indexCount + (int) spatialVector.get(variablesCount) * 2;
            final float sum = temporalVector.get(offset);
            temporalVector.set(offset, sum + value);
            final float count = temporalVector.get(offset + 1);
            temporalVector.set(offset + 1, count + 1);
        }
    }

    @Override
    public void completeTemporal(BinContext ctx, int numTemporalObs, WritableVector temporalVector) {
        // nothing to do here tb 2013-09-20
    }

    @Override
    public void computeOutput(Vector temporalVector, WritableVector outputVector) {
        final int numYears = dateIndexCalculator.getNumYears();

        final int numBands = varNames.length;
        for (int band = 0; band < numBands; band++) {
            final float[] monthlyMeans = aggregateMonths(temporalVector, numYears, band);
            final float mean = aggregateYear(monthlyMeans);
            outputVector.set(band, mean);
        }
    }

    // package access for testing only tb 2013-09-18
    static String[] createSpatialFeatureNames(Config config) {
        final String[] varNames = config.getVarNames();

        if (varNames.length == 0) {
            return new String[0];
        }

        final String[] featureNames = new String[varNames.length + 1];
        System.arraycopy(varNames, 0, featureNames, 0, varNames.length);
        featureNames[varNames.length] = "dateIndex";

        return featureNames;
    }

    static String[] createTemporalFeatureNames(Config config, DateIndexCalculator dateIndexCalculator) {
        final NumberFormat numberFormat = new DecimalFormat("000");
        final int numIndices = dateIndexCalculator.getIndexCount();
        final String[] varNames = config.getVarNames();
        final int numFeatures = varNames.length * numIndices * 2;
        final String[] temporalFeatureNames = new String[numFeatures];

        for (int var = 0; var < varNames.length; var++) {
            for (int idx = 0; idx < numIndices; idx++) {
                final int featureNameOffset = var + 2 * idx;
                final String offset = numberFormat.format(idx);
                temporalFeatureNames[featureNameOffset] = varNames[var] + "_" + offset + "_sum";
                temporalFeatureNames[featureNameOffset + 1] = varNames[var] + "_" + offset + "_count";
            }
        }
        return temporalFeatureNames;
    }

    static String[] createOutputFeatureNames(Config config) {
        final String[] varNames = config.getVarNames();
        final String[] outputFeatureNames = new String[varNames.length];
        for (int i = 0; i < outputFeatureNames.length; i++) {
            outputFeatureNames[i] = varNames[i].concat("_mean");
        }
        return outputFeatureNames;
    }

    static DateIndexCalculator createFrom(Config config) {
        final int startYear = config.getStartYear();
        final int endYear = config.getEndYear();
        if (endYear < startYear) {
            throw new IllegalArgumentException("End year < Start Year");
        }

        return new DateIndexCalculator(startYear, endYear);
    }

    static float[] aggregateMonths(Vector temporalVector, int numYears, int bandNumber) {
        final float[] monthlyMeans = new float[12];
        for (int i = 0; i < monthlyMeans.length; i++) {
            monthlyMeans[i] = Float.NaN;
        }
        final int varOffset = bandNumber * numYears * 12 * 2;

        for (int month = 0; month < 12; month++) {
            double monthSum = 0.f;
            int monthMeasCount = 0;
            for (int year = 0; year < numYears; year++) {
                final int offset = (year * 12 + month) * 2 + varOffset;
                final float monthlySum = temporalVector.get(offset);
                final float monthlyCounts = temporalVector.get(offset + 1);
                if (monthlyCounts > 0.f) {
                    final float monthlyAverage = monthlySum / monthlyCounts;
                    monthSum += monthlyAverage;
                    monthMeasCount++;
                }
            }
            if (monthMeasCount > 0) {
                monthlyMeans[month] = (float) (monthSum / monthMeasCount);
            }
        }

        return monthlyMeans;
    }

    static float aggregateYear(float[] monthlyMeans) {
        double sum = 0.0;
        int count = 0;

        for (float monthlyMean : monthlyMeans) {
            if (!Float.isNaN(monthlyMean)) {
                sum += monthlyMean;
                ++count;
            }
        }
        if (count > 0) {
            return (float) (sum / count);
        } else {
            return Float.NaN;
        }
    }


    public static class Descriptor implements AggregatorDescriptor {

        @Override
        public Aggregator createAggregator(VariableContext varCtx, AggregatorConfig aggregatorConfig) {
            if (aggregatorConfig instanceof Config) {
                return new AggregatorBiasCorrect((Config) aggregatorConfig);
            }
            throw new IllegalArgumentException("Invalid type of configuration: " + aggregatorConfig.getClass());
        }

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public AggregatorConfig createConfig() {
            return new Config();
        }
    }

    public static class Config extends AggregatorConfig {

        @Parameter
        String[] varNames;

        @Parameter(defaultValue = "2005")
        int startYear;
        @Parameter(defaultValue = "2010")
        int endYear;

        public Config() {
            super(NAME);
            varNames = new String[0];
            startYear = 2005;
            endYear = 2010;
        }

        @Override
        public String[] getVarNames() {
            return varNames;
        }

        public int getStartYear() {
            return startYear;
        }

        public int getEndYear() {
            return endYear;
        }
    }
}

