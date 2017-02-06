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

import org.esa.beam.binning.BinContext;
import org.esa.beam.binning.BinManager;
import org.esa.beam.binning.Observation;
import org.esa.beam.binning.SpatialBin;
import org.esa.beam.binning.TemporalBin;
import org.esa.beam.binning.Vector;
import org.esa.beam.binning.WritableVector;
import org.esa.beam.binning.support.ObservationImpl;
import org.esa.beam.binning.support.VectorImpl;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class AggregatorTestUtils {

    public static VectorImpl vec(float... values) {
        return new VectorImpl(values);
    }

    public static Observation obs(double mjd, float... values) {
        return new ObservationImpl(0.0, 0.0, mjd, values);
    }

    public static Observation obsNT(float... values) {
        return new ObservationImpl(0.0, 0.0, 0.0, values);
    }

    public static BinContext createCtx() {
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

    public static void assertVectorEquals(Vector expected, Vector actual) {
        int size = expected.size();
        assertEquals("vector size", size, actual.size());
        for (int i = 0; i < size; i++) {
            assertEquals("vector elem " + i, expected.get(i), actual.get(i), 1E-6);
        }
    }

    public static Vector aggregate(BinManager binManager, Observation[][] multipleProductObs) {
        TemporalBin temporalBin = binManager.createTemporalBin(42);
        for (Observation[] observations : multipleProductObs) {
            SpatialBin spatialBin = binManager.createSpatialBin(42);
            for (Observation observation : observations) {
                binManager.aggregateSpatialBin(observation, spatialBin);
            }
            binManager.completeSpatialBin(spatialBin);
            binManager.aggregateTemporalBin(spatialBin, temporalBin);
        }
        binManager.completeTemporalBin(temporalBin);
        WritableVector outputVector = binManager.createOutputVector();
        binManager.computeOutput(temporalBin, outputVector);
        return outputVector;
    }
}
