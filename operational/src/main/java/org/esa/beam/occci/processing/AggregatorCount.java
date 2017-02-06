/*
 * Copyright (C) 2017 Brockmann Consult GmbH (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.beam.occci.processing;

import org.esa.beam.binning.AbstractAggregator;
import org.esa.beam.binning.Aggregator;
import org.esa.beam.binning.AggregatorConfig;
import org.esa.beam.binning.AggregatorDescriptor;
import org.esa.beam.binning.BinContext;
import org.esa.beam.binning.Observation;
import org.esa.beam.binning.VariableContext;
import org.esa.beam.binning.Vector;
import org.esa.beam.binning.WritableVector;
import org.esa.beam.framework.gpf.annotations.Parameter;
import org.esa.beam.util.StringUtils;

/**
 * An aggregator that counts the number of observations with a value greater than ZERO (TRUE)
 *
 * @author marcoz
 */
public class AggregatorCount extends AbstractAggregator {

    private final int varIndex;

    public AggregatorCount(VariableContext varCtx, String varName, String targetName) {
        super(AggregatorCount.Descriptor.NAME,
              createFeatureNames(varName, "count"),
              createFeatureNames(varName, "count"),
              createFeatureNames(targetName, "count"));

        if (varCtx == null) {
            throw new NullPointerException("varCtx");
        }
        if (varName == null) {
            throw new NullPointerException("varName");
        }
        this.varIndex = varCtx.getVariableIndex(varName);
        if (this.varIndex == -1) {
            throw new IllegalArgumentException("unknown feature name: " + varName);
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void initSpatial(BinContext ctx, WritableVector vector) {
        vector.set(0, 0);
    }

    @Override
    public void aggregateSpatial(BinContext ctx, Observation observationVector, WritableVector spatialVector) {
        final float value = observationVector.get(varIndex);
        if (value > 0) {
            spatialVector.set(0, spatialVector.get(0) + 1);
        }
    }

    @Override
    public void completeSpatial(BinContext ctx, int numSpatialObs, WritableVector spatialVector) {
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void initTemporal(BinContext ctx, WritableVector vector) {
        vector.set(0, 0);
    }

    @Override
    public void aggregateTemporal(BinContext ctx, Vector spatialVector, int numSpatialObs, WritableVector temporalVector) {
        temporalVector.set(0, temporalVector.get(0) + spatialVector.get(0));
    }

    @Override
    public void completeTemporal(BinContext ctx, int numTemporalObs, WritableVector temporalVector) {
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void computeOutput(Vector temporalVector, WritableVector outputVector) {
        outputVector.set(0, temporalVector.get(0));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public String toString() {
        return "AggregatorCount{" +
                "varIndex=" + varIndex +
                "} " + super.toString();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    public static class Config extends AggregatorConfig {

        @Parameter(label = "Source band name", notEmpty = true, notNull = true, description = "The source band used for aggregation.")
        String varName;
        @Parameter(label = "Target band name prefix (optional)", description = "The name prefix for the resulting bands. If empty, the source band name is used.")
        String targetName;

        public Config() {
            this(null, null);
        }

        public Config(String targetName, String varName) {
            super(AggregatorCount.Descriptor.NAME);
            this.targetName = targetName;
            this.varName = varName;
        }
    }

    public static class Descriptor implements AggregatorDescriptor {

        public static final String NAME = "COUNT";

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public Aggregator createAggregator(VariableContext varCtx, AggregatorConfig aggregatorConfig) {
            AggregatorCount.Config config = (AggregatorCount.Config) aggregatorConfig;
            String targetName = config.targetName != null ? config.targetName : config.varName;
            return new AggregatorCount(varCtx, config.varName, targetName);
        }

        @Override
        public AggregatorConfig createConfig() {
            return new AggregatorCount.Config();
        }

        @Override
        public String[] getSourceVarNames(AggregatorConfig aggregatorConfig) {
            AggregatorCount.Config config = (AggregatorCount.Config) aggregatorConfig;
            return new String[]{config.varName};
        }

        @Override
        public String[] getTargetVarNames(AggregatorConfig aggregatorConfig) {
            AggregatorCount.Config config = (AggregatorCount.Config) aggregatorConfig;
            String targetName = StringUtils.isNotNullAndNotEmpty(config.targetName) ? config.targetName : config.varName;
            return createFeatureNames(targetName, "count");
        }

    }
}
