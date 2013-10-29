package org.esa.beam.occci.bandshift.binning;

import org.esa.beam.binning.CellProcessor;
import org.esa.beam.binning.VariableContext;
import org.esa.beam.binning.Vector;
import org.esa.beam.binning.WritableVector;
import org.esa.beam.occci.bandshift.BandShiftCorrection;
import org.esa.beam.occci.bandshift.CorrectionContext;
import org.esa.beam.occci.bandshift.Sensor;
import org.esa.beam.occci.util.binning.BinningUtils;

import java.io.IOException;

public class BandShiftPostProcessor extends CellProcessor {

    private static final int NUM_RRS = 6;
    private static final int NUM_QAA = 3;

    private final BandShiftCorrection bandShiftCorrection;
    private final Sensor sensor;

    private final int[] bandIndices;
    private final double[] rrs;
    private final double[] qaa;
    private final ResultMapper resultMapper;

    public BandShiftPostProcessor(VariableContext varCtx, String sensorName, String[] bandNames, int[] outputCenterWavelengths) throws IOException {
        super(createOutputFeatureNames(outputCenterWavelengths));

        sensor = Sensor.byName(sensorName);
         final CorrectionContext correctionContext = new CorrectionContext(sensor);
        bandShiftCorrection = new BandShiftCorrection(correctionContext);
        rrs = new double[NUM_RRS];
        qaa = new double[NUM_QAA];

        bandIndices = BinningUtils.getBandIndices(varCtx, bandNames);
        resultMapper = new ResultMapper(sensorName, outputCenterWavelengths);
    }

    // package access for testing only tb 2013-04-19
    static String[] createOutputFeatureNames(int[] outputCenterWavelengths) {
        final String[] outputFeatureNames = new String[outputCenterWavelengths.length];
        for (int i = 0; i < outputCenterWavelengths.length; i++) {
            outputFeatureNames[i] = "Rrs_" + outputCenterWavelengths[i];
        }
        return outputFeatureNames;
    }

    @Override
    public void compute(Vector input, WritableVector output) {
        for (int i = 0; i < rrs.length; i++) {
            rrs[i] = input.get(bandIndices[i]);
        }

        for (int i = 0; i < qaa.length; i++) {
            qaa[i] = input.get(bandIndices[i + NUM_RRS]);
        }

        final double[] rrs_corrected = bandShiftCorrection.correctBandshift(rrs, sensor.getLambdaInterface(), qaa);
        if (isCorrected(rrs_corrected)) {
            final double[] rrs_shifted = bandShiftCorrection.weightedAverageEqualCorrectionProducts(rrs_corrected);

            resultMapper.assign(rrs, rrs_shifted, output);
        } else {
            BinningUtils.setToInvalid(output);
        }
    }

    static boolean isCorrected(double[] rrs_corrected) {
        return rrs_corrected.length != 0;
    }

}
