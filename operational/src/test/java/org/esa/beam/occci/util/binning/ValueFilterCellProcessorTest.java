/*
 * Copyright (C) 2014 Brockmann Consult GmbH (info@brockmann-consult.de)
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

package org.esa.beam.occci.util.binning;

import org.esa.beam.binning.CellProcessor;
import org.esa.beam.binning.VariableContext;
import org.esa.beam.binning.WritableVector;
import org.esa.beam.binning.support.VectorImpl;
import org.junit.Test;

import static org.junit.Assert.*;

public class ValueFilterCellProcessorTest {

    @Test
    public void testFilter() throws Exception {
        VariableContext ctx = BinningUtils.createVariableContext("a", "b", "c", "d");
        String[] bandNames = new String[]{"b", "d"};
        double[] minValues = new double[]{2f, 4f};
        double[] maxValues = new double[]{8f, 16f};
        CellProcessor processor = new ValueFilterCellProcessor(ctx, bandNames, minValues, maxValues);

        String[] outputFeatureNames = processor.getOutputFeatureNames();
        assertArrayEquals(bandNames, outputFeatureNames);

        // no filtering
        assertFiltered(processor, new float[]{2f, 4f, 6f, 8f}, new float[]{4f, 8f});

        // min filtering
        assertFiltered(processor, new float[]{2f, 0f, 6f, 8f}, new float[]{Float.NaN, 8f});

        // max filtering
        assertFiltered(processor, new float[]{2f, 8.0001f, 6f, 17f}, new float[]{Float.NaN, Float.NaN});
        assertFiltered(processor, new float[]{2f, 8f, 6f, 17f}, new float[]{8f, Float.NaN});

    }

    private void assertFiltered(CellProcessor processor, float[] in, float[] outExpected) {
        float[] outActual = new float[outExpected.length];
        WritableVector outVector = new VectorImpl(outActual);
        processor.compute(new VectorImpl(in), outVector);
        assertArrayEquals(outExpected, outActual, 1e-6f);
    }
}
