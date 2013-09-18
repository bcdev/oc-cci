package org.esa.beam.occci.biascorrect;

import org.esa.beam.binning.*;
import org.esa.beam.framework.gpf.annotations.Parameter;

public class AggregatorBiasCorrect extends AbstractAggregator {

    public static final String NAME = "OC-CCI-BIAS";
    private final DateIndex dateIndex;
    private int dateIdx;
    private int numReflecs;

    public AggregatorBiasCorrect(Config config) {
        super(NAME, createFeatureNames(config));

        dateIndex = createFrom(config);
    }

    @Override
    public void initSpatial(BinContext ctx, WritableVector vector) {
        final int size = vector.size();
        numReflecs = size - 1;
        for (int i = 0; i < numReflecs; i++) {
            vector.set(i, Float.NaN);
        }
        vector.set(numReflecs, DateIndex.INVALID);

        dateIdx = DateIndex.INVALID;
    }

    @Override
    public void aggregateSpatial(BinContext ctx, Observation observationVector, WritableVector spatialVector) {
        if (dateIdx == DateIndex.INVALID) {
            final int currentIndex = dateIndex.get(observationVector.getMJD());
            if (currentIndex != DateIndex.INVALID) {
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
        //System.out.println("initTemporal");
    }

    @Override
    public void aggregateTemporal(BinContext ctx, Vector spatialVector, int numSpatialObs, WritableVector temporalVector) {
        //System.out.println("aggregateTemporal");
    }

    @Override
    public void completeTemporal(BinContext ctx, int numTemporalObs, WritableVector temporalVector) {
        //System.out.println("completeTemporal");
    }

    @Override
    public void computeOutput(Vector temporalVector, WritableVector outputVector) {
//        System.out.println("computeOutput");
    }

    static String[] createFeatureNames(Config config) {
        final String[] varNames = config.getVarNames();

        if (varNames.length == 0) {
            return new String[0];
        }

        final String[] featureNames = new String[varNames.length + 1];
        System.arraycopy(varNames, 0, featureNames, 0, varNames.length);
        featureNames[varNames.length] = "dateIndex";

        return featureNames;
    }

    static DateIndex createFrom(Config config) {
        final int startYear = config.getStartYear();
        final int endYear = config.getEndYear();
        if (endYear < startYear) {
            throw new IllegalArgumentException("End year < Start Year");
        }

        return new DateIndex(startYear, endYear);
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

