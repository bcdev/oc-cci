package org.esa.beam.occci.qaa.binning;

import org.esa.beam.binning.support.VariableContextImpl;
import org.esa.beam.binning.support.VectorImpl;
import org.esa.beam.occci.qaa.QaaConstants;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class QaaPostProcessorTest {

    @Test
    public void testGetOutputFeatureNames() {
        final String[] outputFeatureNames = {"out", "feature", "names"};

        final QaaConfig config = new QaaConfig();
        config.setSensorName(QaaConstants.SEAWIFS);

        final QaaPostProcessor qaaPostProcessor = new QaaPostProcessor(new VariableContextImpl(), config, outputFeatureNames);
        assertArrayEquals(outputFeatureNames, qaaPostProcessor.getOutputFeatureNames());
    }

    @Test
    public void testProcess_MODIS() {
        final float[] rrs_in = {0.0029080009f, 0.0019860009f, 0.0039160008f, 0.0020800009f, 0.0039520008f, 0.0001980009f};
        final VectorImpl outVector = new VectorImpl(rrs_in);

        final QaaConfig config = createConfig();
        final VariableContextImpl varCtx = createVariableContext();

        final String[] outputFeatureNames = QaaDescriptor.createOutputFeatureNames(config);
        final VectorImpl postVector = new VectorImpl(new float[outputFeatureNames.length]);

        final QaaPostProcessor postProcessor = new QaaPostProcessor(varCtx, config, outputFeatureNames);
        postProcessor.compute(outVector, postVector);

        // [a_pig_412,a_pig_443,a_pig_488,a_total_488,a_total_531,a_total_547]

       // assertEquals(0.2496127486228943, postVector.get(0), 1e-8);

        // a_total: [0.2070285528898239,0.2585449516773224,0.11189476400613785,0.18294332921504974,0.0938049703836441]
        // bb_spm:  [0.01265037152916193,0.010870732367038727,0.009142275899648666,0.008049498312175274,0.007732917554676533]
        // a_pig:   [0.2496127486228943,0.2794683873653412,0.11051654815673828]
        // a_ys:    [-0.047134749591350555,-0.027992580085992813,-0.013138488866388798]
    }

    private VariableContextImpl createVariableContext() {
        final VariableContextImpl context = new VariableContextImpl();
        context.defineVariable("ref_1");
        context.defineVariable("ref_2");
        context.defineVariable("ref_4");
        context.defineVariable("ref_3");
        context.defineVariable("ref_6");
        context.defineVariable("ref_5");
        return context;
    }

    private QaaConfig createConfig() {
        final QaaConfig config = new QaaConfig();
        config.setSensorName(QaaConstants.MODIS);
        config.setBandNames(new String[]{"ref_1", "ref_2", "ref_3", "ref_4", "ref_5", "ref_6",});
        config.setA_pig_out_indices(new int[]{0, 1, 2});
        config.setA_total_out_indices(new int[]{2, 3, 4});
        return config;
    }
}
