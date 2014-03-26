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
        final int[] aphOutIndices = config.getAphOutIndices();
        for (int i : aphOutIndices) {
            featureNameList.add("aph_" + QaaDescriptor.getWavelengthInt(wavelengths, i));
        }

        final int[] atotOutIndices = config.getAtotOutIndices();
        for (int i : atotOutIndices) {
            featureNameList.add("atot_" + QaaDescriptor.getWavelengthInt(wavelengths, i));
        }

        final int[] adgOutIndices = config.getAdgOutIndices();
        for (int i : adgOutIndices) {
            featureNameList.add("adg_" + QaaDescriptor.getWavelengthInt(wavelengths, i));
        }

        final int[] bbpOutIndices = config.getBbpOutIndices();
        for (int i : bbpOutIndices) {
            featureNameList.add("bbp_" + QaaDescriptor.getWavelengthInt(wavelengths, i));
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
