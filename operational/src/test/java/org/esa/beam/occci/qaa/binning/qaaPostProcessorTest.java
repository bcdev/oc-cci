package org.esa.beam.occci.qaa.binning;


import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class QaaPostProcessorTest {

    @Test
    public void testGetOutputFeatureNames() {
        final String[] outputFeatureNames = {"out", "feature", "names"};

        final QaaPostProcessor qaaPostProcessor = new QaaPostProcessor(outputFeatureNames);
        assertArrayEquals(outputFeatureNames, qaaPostProcessor.getOutputFeatureNames());
    }
}
