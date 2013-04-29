package org.esa.beam.occci.bandshift.binning;

import org.esa.beam.binning.PostProcessor;
import org.esa.beam.binning.VariableContext;
import org.esa.beam.binning.Vector;
import org.esa.beam.binning.WritableVector;
import org.esa.beam.occci.bandshift.BandShiftCorrection;
import org.esa.beam.occci.bandshift.CorrectionContext;
import org.esa.beam.occci.bandshift.Sensor;
import org.esa.beam.occci.util.binning.BinningUtils;

import java.io.IOException;

public class BandShiftPostProcessor extends PostProcessor {

    private static final int QAA_OFFSET = 6;

    private final BandShiftCorrection bandShiftCorrection;
    private final Sensor sensor;

    private final int[] bandIndices;
    private final double[] rrs;
    private final double[] qaa;
    private final ResultMapper resultMapper;

    protected BandShiftPostProcessor(String[] outputFeatureNames, BandShiftConfig config, VariableContext varCtx) throws IOException {
        super(outputFeatureNames);

        sensor = Sensor.valueOf(config.getSensorName());
        final CorrectionContext correctionContext = new CorrectionContext(sensor);
        bandShiftCorrection = new BandShiftCorrection(correctionContext);
        rrs = new double[6];
        qaa = new double[3];

        final String[] bandNames = config.getBandNames();
        bandIndices = BinningUtils.getBandIndices(varCtx, bandNames);
        resultMapper = new ResultMapper(config);
    }

    @Override
    public void compute(Vector outputVector, WritableVector postVector) {
        for (int i = 0; i < rrs.length; i++) {
            rrs[i] = outputVector.get(bandIndices[i]);
        }

        for (int i = 0; i < qaa.length; i++) {
            qaa[i] = outputVector.get(bandIndices[i + QAA_OFFSET]);
        }

        // @todo 3 tb/tb check if we need to make the thresholds configurable tb 2013-04-22
        final double[] rrs_corrected = bandShiftCorrection.correctBandshift(rrs, sensor.getLambdaInterface(), qaa, 0.0, 5.0);
        if (isCorrected(rrs_corrected)) {
            final double[] rrs_shifted = bandShiftCorrection.weightedAverageEqualCorrectionProducts(rrs_corrected);

            resultMapper.assign(rrs, rrs_shifted, postVector);
        } else {
            BinningUtils.setToInvalid(postVector);
        }
    }

    static boolean isCorrected(double[] rrs_corrected) {
        return rrs_corrected.length != 0;
    }

}
