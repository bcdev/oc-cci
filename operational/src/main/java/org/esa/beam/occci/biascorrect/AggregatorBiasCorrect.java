package org.esa.beam.occci.biascorrect;

import org.esa.beam.binning.*;
import org.esa.beam.framework.gpf.annotations.Parameter;

public class AggregatorBiasCorrect extends AbstractAggregator {

    public static final String NAME = "OC-CCI-BIAS";
    private final DateIndex dateIndex;

    public AggregatorBiasCorrect(VariableContext ctx, Config config) {
        super(NAME, createFeatureNames(ctx));
        // @todo 1 tb/tb get start and stop year as input parameter
        dateIndex = new DateIndex(2007, 2012);
    }

    @Override
    public void initSpatial(BinContext ctx, WritableVector vector) {
        //System.out.println("initSpatial");
    }

    @Override
    public void aggregateSpatial(BinContext ctx, Observation observationVector, WritableVector spatialVector) {
        //System.out.println(observationVector.getMJD());
    }

    @Override
    public void completeSpatial(BinContext ctx, int numSpatialObs, WritableVector spatialVector) {
        //System.out.println("completeSpatial");
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

    static String[] createFeatureNames(VariableContext ctx) {
        // @todo 1 tb/tb
        return new String[0];
    }


    public static class Descriptor implements AggregatorDescriptor {

        @Override
        public Aggregator createAggregator(VariableContext varCtx, AggregatorConfig aggregatorConfig) {
            if (aggregatorConfig instanceof Config) {
                return new AggregatorBiasCorrect(varCtx, (Config) aggregatorConfig);
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

        public Config() {
            super(NAME);
            varNames = new String[0];
        }

        @Override
        public String[] getVarNames() {
            return varNames;
        }
    }
}

