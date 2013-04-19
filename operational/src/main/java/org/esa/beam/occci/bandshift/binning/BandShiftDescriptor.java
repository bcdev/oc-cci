package org.esa.beam.occci.bandshift.binning;

import org.esa.beam.binning.PostProcessor;
import org.esa.beam.binning.PostProcessorConfig;
import org.esa.beam.binning.PostProcessorDescriptor;
import org.esa.beam.binning.VariableContext;
import org.esa.beam.occci.bandshift.Sensor;

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
        final String sensorName = ((BandShiftConfig) config).getSensorName();
        return new BandShiftPostProcessor(new String[0]);
    }

    // package access for testing only tb 2013-04-19
    static String[] createOutputFeatureNames(String sensorName) {
        final String[] outputFeatureNames = new String[8];
        final double[] lambdaO = Sensor.MERIS.getLambdaO();
        for (int i = 0; i < lambdaO.length; i++) {
            final int lambdaInt = (int) Math.floor(lambdaO[i]);
            outputFeatureNames[i] = "Rrs_" + lambdaInt;
        }

        return outputFeatureNames;
    }
}
