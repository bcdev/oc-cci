package org.esa.beam.occci.qaa.binning;


import org.esa.beam.binning.CellProcessor;
import org.esa.beam.binning.VariableContext;
import org.esa.beam.binning.Vector;
import org.esa.beam.binning.WritableVector;
import org.esa.beam.occci.qaa.*;
import org.esa.beam.occci.util.binning.BinningUtils;

import java.util.ArrayList;

public class QaaCellProcessor extends CellProcessor {

    private final QaaAlgo qaaAlgorithm;
    private final int[] bandIndices;
    private final float[] rrs;
    QaaResult qaaResult;
    private final ResultMapper resultMapper;

    public QaaCellProcessor(VariableContext varCtx, QaaConfig config) {
        this(new QaaAlgorithm(SensorConfigFactory.get(config.getSensorName())), varCtx, config);
    }

    public QaaCellProcessor(QaaAlgo qaaAlgorithm, VariableContext varCtx, QaaConfig config) {
        super(createOutputFeatureNames(config));
        this.qaaAlgorithm = qaaAlgorithm;

        final String[] bandNames = config.getBandNames();
        bandIndices = BinningUtils.getBandIndices(varCtx, bandNames);

        rrs = new float[bandNames.length];
        qaaResult = new QaaResult();
        resultMapper = new ResultMapper(config);
    }

    // package access for testing only tb 2013-04-23
    public static String[] createOutputFeatureNames(QaaConfig config) {
        final SensorConfig sensorConfig = SensorConfigFactory.get(config.getSensorName());
        final ArrayList<String> featureNameList = new ArrayList<String>();

        final double[] wavelengths = sensorConfig.getWavelengths();
        final int[] a_pig_out_indices = config.getAPigOutIndices();
        for (int a_pig_out_index : a_pig_out_indices) {
            featureNameList.add("a_pig_" + QaaDescriptor.getWavelengthInt(wavelengths, a_pig_out_index));
        }

        final int[] a_total_out_indices = config.getATotalOutIndices();
        for (int a_total_out_index : a_total_out_indices) {
            featureNameList.add("a_total_" + QaaDescriptor.getWavelengthInt(wavelengths, a_total_out_index));
        }

        final int[] a_ys_out_indices = config.getAYsOutIndices();
        for (int a_ys_out_index : a_ys_out_indices) {
            featureNameList.add("a_ys_" + QaaDescriptor.getWavelengthInt(wavelengths, a_ys_out_index));
        }

        final int[] bb_spm_out_indices = config.getBbSpmOutIndices();
        for (int bb_spm_out_index : bb_spm_out_indices) {
            featureNameList.add("bb_spm_" + QaaDescriptor.getWavelengthInt(wavelengths, bb_spm_out_index));
        }
        return featureNameList.toArray(new String[featureNameList.size()]);
    }

    @Override
    public void compute(Vector inputVector, WritableVector outputVector) {
        for (int i = 0; i < rrs.length; i++) {
            rrs[i] = inputVector.get(bandIndices[i]);
        }

        qaaResult = qaaAlgorithm.process(rrs, qaaResult);
        qaaResult.infinityAsNaN();
        resultMapper.assign(qaaResult, outputVector);

    }
}
