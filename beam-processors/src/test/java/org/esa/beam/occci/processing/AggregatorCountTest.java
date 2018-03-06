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

import org.esa.beam.binning.Aggregator;
import org.esa.beam.binning.BinManager;
import org.esa.beam.binning.Observation;
import org.esa.beam.binning.VariableContext;
import org.junit.Before;
import org.junit.Test;

import static java.lang.Float.NaN;
import static org.esa.beam.occci.processing.AggregatorTestUtils.aggregate;
import static org.esa.beam.occci.processing.AggregatorTestUtils.assertVectorEquals;
import static org.esa.beam.occci.processing.AggregatorTestUtils.obsNT;
import static org.esa.beam.occci.processing.AggregatorTestUtils.vec;
import static org.junit.Assert.*;

public class AggregatorCountTest {

    @Test
    public void testMetadata() {
        VariableContext varCtx = new MyVariableContext("b1", "b2", "b3");
        Aggregator agg = new AggregatorCount(varCtx, "b2", "b2");
        assertArrayEquals(new String[]{"b2_count"}, agg.getSpatialFeatureNames());
        assertArrayEquals(new String[]{"b2_count"}, agg.getTemporalFeatureNames());
        assertArrayEquals(new String[]{"b2_count"}, agg.getOutputFeatureNames());

        agg = new AggregatorCount(varCtx, "b2", "d3");
        assertArrayEquals(new String[]{"b2_count"}, agg.getSpatialFeatureNames());
        assertArrayEquals(new String[]{"b2_count"}, agg.getTemporalFeatureNames());
        assertArrayEquals(new String[]{"d3_count"}, agg.getOutputFeatureNames());
    }

    @Test
    public void testAggregate_e2e() throws Exception {
        VariableContext varCtx = new MyVariableContext("b");
        Aggregator agg = new AggregatorCount(varCtx, "b", "b");
        BinManager bm = new BinManager(varCtx, agg);

        // 0 obs
        Observation[][] multipleProductObs = new Observation[][]{{}};
        assertVectorEquals(vec(0), aggregate(bm, multipleProductObs));

        // 1 valid obs
        multipleProductObs = new Observation[][]{
                {obsNT(1)}
        };
        assertVectorEquals(vec(1), aggregate(bm, multipleProductObs));

        // 1 in-valid obs
        multipleProductObs = new Observation[][]{
                {obsNT(0)}
        };
        assertVectorEquals(vec(0), aggregate(bm, multipleProductObs));

        // many obs
        multipleProductObs = new Observation[][]{
                {obsNT(1),obsNT(0),obsNT(1)},
                {obsNT(4),obsNT(3),obsNT(1)},
                {obsNT(0),obsNT(77),obsNT(1)},
        };
        assertVectorEquals(vec(7), aggregate(bm, multipleProductObs));
    }




}