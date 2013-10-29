package org.esa.beam.occci.qaa.binning;


import org.esa.beam.binning.CellProcessor;
import org.esa.beam.binning.VariableContext;
import org.esa.beam.binning.Vector;
import org.esa.beam.binning.WritableVector;
import org.esa.beam.occci.qaa.*;
import org.esa.beam.occci.util.binning.BinningUtils;

import java.util.ArrayList;
import java.util.Collections;

public class QaaCellProcessor extends CellProcessor {

    private final QaaAlgorithm qaaAlgorithm;
    private final int[] bandIndices;
    private final float[] rrs;
    QaaResult qaaResult;
    private final ResultMapper resultMapper;

    public QaaCellProcessor(VariableContext varCtx, QaaConfig config) {
        super(createOutputFeatureNames(config));

        final SensorConfig sensorConfig = SensorConfigFactory.get(config.getSensorName());
        qaaAlgorithm = new QaaAlgorithm(sensorConfig);

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

        if (config.isRrsOut()) {
            Collections.addAll(featureNameList, config.getBandNames());
        }

        return featureNameList.toArray(new String[featureNameList.size()]);
    }

    @Override
    public void compute(Vector inputVector, WritableVector outputVector) {
        for (int i = 0; i < rrs.length; i++) {
            rrs[i] = inputVector.get(bandIndices[i]);
        }

        try {
            qaaResult = qaaAlgorithm.process(rrs, qaaResult);
        } catch (ImaginaryNumberException e) {
            BinningUtils.setToInvalid(outputVector);
            return;
        }
        if (containsInfinite(qaaResult.getA_PIG()) ||
                containsInfinite(qaaResult.getA_Total()) ||
                containsInfinite(qaaResult.getA_YS()) ||
                containsInfinite(qaaResult.getBB_SPM())) {
            BinningUtils.setToInvalid(outputVector);
        } else {
            resultMapper.assign(qaaResult, rrs, outputVector);
        }
    }

    private static boolean containsInfinite(float[] values) {
        for (float value : values) {
            if (Float.isInfinite(value)) {
                return true;
            }
        }
        return false;
    }
}