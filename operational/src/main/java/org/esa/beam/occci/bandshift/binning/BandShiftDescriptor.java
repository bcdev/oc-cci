package org.esa.beam.occci.bandshift.binning;

import org.esa.beam.binning.PostProcessor;
import org.esa.beam.binning.PostProcessorConfig;
import org.esa.beam.binning.PostProcessorDescriptor;
import org.esa.beam.binning.VariableContext;
import org.esa.beam.occci.bandshift.Sensor;

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
        final String sensorName = ((BandShiftConfig) config).getSensorName();
        final String[] outputFeatureNames = createOutputFeatureNames(sensorName);
        final BandShiftPostProcessor bandShiftPostProcessor;
        try {
            bandShiftPostProcessor = new BandShiftPostProcessor(outputFeatureNames, sensorName);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
        return bandShiftPostProcessor;
    }

    // package access for testing only tb 2013-04-19
    static String[] createOutputFeatureNames(String sensorName) {
        final Sensor sensor = Sensor.valueOf(sensorName);
        final double[] lambdaO = sensor.getLambdaO();
        final String[] outputFeatureNames = new String[lambdaO.length - 1];

        int outIndex = 0;
        for (int i = 0; i < lambdaO.length; i++) {
            if (sensor.getAverageIndices()[1] == i) {
                continue;
            }
            final int lambdaInt = (int) Math.floor(lambdaO[i]);
            outputFeatureNames[outIndex] = "Rrs_" + lambdaInt;
            ++outIndex;
        }

        return outputFeatureNames;
    }
}
