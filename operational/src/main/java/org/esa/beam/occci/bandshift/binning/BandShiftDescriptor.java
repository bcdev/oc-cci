package org.esa.beam.occci.bandshift.binning;

import org.esa.beam.binning.PostProcessor;
import org.esa.beam.binning.PostProcessorConfig;
import org.esa.beam.binning.PostProcessorDescriptor;
import org.esa.beam.binning.VariableContext;

public class BandShiftDescriptor implements PostProcessorDescriptor {

    public String getName() {
        return "BandShifting";
    }

    @Override
    public PostProcessorConfig createPostProcessorConfig() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PostProcessor createPostProcessor(VariableContext varCtx, PostProcessorConfig postProcessorConfig) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
