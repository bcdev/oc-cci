package org.esa.beam.occci.bandshift.binning;

import org.esa.beam.binning.VariableContext;
import org.esa.beam.binning.support.VariableContextImpl;
import org.esa.beam.binning.support.VectorImpl;
import org.geotools.filter.function.FilterFunction_strEqualsIgnoreCase;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class BandShiftPostProcessorTest {

    @Test
    public void testGetOutputFeatureNames() throws IOException {
        final String[] outputFeatureNames = {"out", "feature", "names"};
        final BandShiftConfig config = createConfig("MERIS");
        final VariableContext variableContext = createVariableContext();

        final BandShiftPostProcessor postProcessor = new BandShiftPostProcessor(outputFeatureNames, config, variableContext);

        final String[] featureNamesFromProcessor = postProcessor.getOutputFeatureNames();
        assertArrayEquals(outputFeatureNames, featureNamesFromProcessor);
    }

    @Test
    public void testThrowsExceptionOnMissingBands() throws IOException {
        final String[] outputFeatureNames = {"out", "feature", "names"};
        final BandShiftConfig config = createConfig("MERIS");
        final VariableContextImpl variableContext =  createVariableContext();

        // add band to config that is not in the context
        final String[] bandNames = config.getBandNames();
        final String[] additionalBandNames = new String[bandNames.length + 1];
        System.arraycopy(bandNames, 0, additionalBandNames, 0, bandNames.length);
        additionalBandNames[bandNames.length] = "does_not_exist";
        config.setBandNames(additionalBandNames);

        try {
            new BandShiftPostProcessor(outputFeatureNames, config, variableContext);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testCompute_MODIS() throws IOException {
        // inputData consists of six reflectances followed by the three qaa values
        final float[] inputData = new float[]{0.00711314f, 0.00559714f, 0.00459386f, 0.00249029f, 0.00189400f, 0.000241144f, 0.0192148f, 0.00571175f, 0.0138207f};
        final String[] outputFeatureNames = {"Rrs_413", "Rrs_510", "Rrs_490", "Rrs_560", "Rrs_555", "Rrs_665", "Rrs_670"};

        final VariableContext variableContext = createVariableContext();
        final BandShiftConfig config = createConfig("MODISA");
        final BandShiftPostProcessor postProcessor = new BandShiftPostProcessor(outputFeatureNames, config, variableContext);

        final VectorImpl postVector = new VectorImpl(new float[outputFeatureNames.length]);
        final VectorImpl outVector = new VectorImpl(inputData);
        postProcessor.compute(outVector, postVector);

        assertEquals(0.007067796774, postVector.get(0), 1e-6);
        assertEquals(0.003140728688, postVector.get(1), 1e-6);
        assertEquals(0.004552158527, postVector.get(2), 1e-6);
        assertEquals(0.001587149804, postVector.get(3), 1e-6);
        assertEquals(0.001667401055, postVector.get(4), 1e-6);
        assertEquals(0.000246264943, postVector.get(5), 1e-6);
        assertEquals(0.000236338106, postVector.get(6), 1e-6);
    }

    private VariableContextImpl createVariableContext() {
        final VariableContextImpl variableContext = new VariableContextImpl();
        variableContext.defineVariable("band_1");
        variableContext.defineVariable("band_2");
        variableContext.defineVariable("band_3");
        variableContext.defineVariable("band_4");
        variableContext.defineVariable("band_5");
        variableContext.defineVariable("band_6");
        variableContext.defineVariable("band_7");
        variableContext.defineVariable("band_8");
        variableContext.defineVariable("band_9");
        return variableContext;
    }

    private BandShiftConfig createConfig(String meris) {
        final BandShiftConfig config = new BandShiftConfig("bla");
        config.setSensorName(meris);
        final String[] bandNames = {"band_1", "band_2", "band_3", "band_4", "band_5", "band_6", "band_7", "band_8", "band_9"};
        config.setBandNames(bandNames);
        return config;
    }
}
