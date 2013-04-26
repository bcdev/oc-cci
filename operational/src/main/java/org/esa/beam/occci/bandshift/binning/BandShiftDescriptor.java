package org.esa.beam.occci.bandshift.binning;

import org.esa.beam.binning.PostProcessor;
import org.esa.beam.binning.PostProcessorConfig;
import org.esa.beam.binning.PostProcessorDescriptor;
import org.esa.beam.binning.VariableContext;

import java.io.IOException;

public class BandShiftDescriptor implements PostProcessorDescriptor {

    public static final String BAND_SHIFTING = "BandShifting";

    public String getName() {
        return BAND_SHIFTING;
    }

    @Override
    public PostProcessorConfig createPostProcessorConfig() {
        return new BandShiftConfig(BAND_SHIFTING);
    }

    @Override
    public PostProcessor createPostProcessor(VariableContext varCtx, PostProcessorConfig config) {
        final BandShiftConfig bandShiftConfig = (BandShiftConfig) config;
        final String[] outputFeatureNames = createOutputFeatureNames(bandShiftConfig);
        final BandShiftPostProcessor bandShiftPostProcessor;
        try {
            bandShiftPostProcessor = new BandShiftPostProcessor(outputFeatureNames, bandShiftConfig, varCtx);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
        return bandShiftPostProcessor;
    }

    // package access for testing only tb 2013-04-19
    static String[] createOutputFeatureNames(BandShiftConfig config) {
        final int[] outputCenterWavelengths = config.getOutputCenterWavelengths();
        final String[] outputFeatureNames = new String[outputCenterWavelengths.length];

        for (int i = 0; i < outputCenterWavelengths.length; i++) {
            outputFeatureNames[i] = "Rrs_" + outputCenterWavelengths[i];
        }

        return outputFeatureNames;
    }
}
