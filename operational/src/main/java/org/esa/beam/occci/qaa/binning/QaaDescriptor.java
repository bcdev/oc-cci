package org.esa.beam.occci.qaa.binning;


import org.esa.beam.binning.PostProcessor;
import org.esa.beam.binning.PostProcessorConfig;
import org.esa.beam.binning.PostProcessorDescriptor;
import org.esa.beam.binning.VariableContext;
import org.esa.beam.occci.qaa.SensorConfig;
import org.esa.beam.occci.qaa.SensorConfigFactory;

public class QaaDescriptor implements PostProcessorDescriptor {

    @Override
    public String getName() {
        return "QAA";
    }

    @Override
    public PostProcessorConfig createPostProcessorConfig() {
        return new QaaConfig();
    }

    @Override
    public PostProcessor createPostProcessor(VariableContext varCtx, PostProcessorConfig postProcessorConfig) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    // package access for testing only tb 2013-04-23
    String[] createOutputFeatureNames(QaaConfig config) {
        final SensorConfig sensorConfig = SensorConfigFactory.get(config.getSensorName());

        // @todo 1 tb/tb continue here
//        final int[] a_pig_out_indices = config.getA_pig_out_indices();
//        for (int i = 0; i < a_pig_out_indices.length; i++) {
//            int a_pig_out_indice = a_pig_out_indices[i];
//
//        }
        return new String[0];  //To change body of created methods use File | Settings | File Templates.
    }
}
