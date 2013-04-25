package org.esa.beam.occci.qaa.binning;


import org.esa.beam.binning.PostProcessor;
import org.esa.beam.binning.VariableContext;
import org.esa.beam.binning.Vector;
import org.esa.beam.binning.WritableVector;
import org.esa.beam.occci.qaa.*;
import org.esa.beam.occci.util.binning.BinningUtils;

public class QaaPostProcessor extends PostProcessor {

    private final QaaAlgorithm qaaAlgorithm;
    private final int[] bandIndices;
    private final float[] rrs;
    QaaResult qaaResult;
    private final ResultMapper resultMapper;

    public QaaPostProcessor(VariableContext varCtx, QaaConfig config, String[] outputFeatureNames) {
        super(outputFeatureNames);

        final SensorConfig sensorConfig = SensorConfigFactory.get(config.getSensorName());
        qaaAlgorithm = new QaaAlgorithm(sensorConfig);

        final String[] bandNames = config.getBandNames();
        bandIndices = BinningUtils.getBandIndices(varCtx, bandNames);

        rrs = new float[bandNames.length];
        qaaResult = new QaaResult();
        resultMapper = new ResultMapper(config);
    }

    @Override
    public void compute(Vector outputVector, WritableVector postVector) {
        for (int i = 0; i < rrs.length; i++) {
            rrs[i] = outputVector.get(bandIndices[i]);
        }

        try {
            qaaResult = qaaAlgorithm.process(rrs, qaaResult);
        } catch (ImaginaryNumberException e) {
            BinningUtils.setToInvalid(postVector);
            return;
        }

        resultMapper.assign(qaaResult, postVector);
    }
}
