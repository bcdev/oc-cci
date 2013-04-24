package org.esa.beam.occci.bandshift.binning;

import org.esa.beam.binning.PostProcessor;
import org.esa.beam.binning.VariableContext;
import org.esa.beam.binning.Vector;
import org.esa.beam.binning.WritableVector;
import org.esa.beam.occci.bandshift.BandShiftCorrection;
import org.esa.beam.occci.bandshift.CorrectionContext;
import org.esa.beam.occci.bandshift.Sensor;

import java.io.IOException;

public class BandShiftPostProcessor extends PostProcessor {

    private static final int QAA_OFFSET = 6;

    private final BandShiftCorrection bandShiftCorrection;
    private final Sensor sensor;

    private final int[] bandIndices;
    private final double[] rrs;
    private final double[] qaa;

    protected BandShiftPostProcessor(String[] outputFeatureNames, BandShiftConfig config, VariableContext varCtx) throws IOException {
        super(outputFeatureNames);

        sensor = Sensor.valueOf(config.getSensorName());
        final CorrectionContext correctionContext = new CorrectionContext(sensor);
        bandShiftCorrection = new BandShiftCorrection(correctionContext);
        rrs = new double[6];
        qaa = new double[3];

        final String[] bandNames = config.getBandNames();
        bandIndices = new int[bandNames.length];
        for (int i = 0; i < bandNames.length; i++) {
            bandIndices[i] = varCtx.getVariableIndex(bandNames[i]);
            if (bandIndices[i] < 0) {
                throw new IllegalArgumentException("Configured input band is not available: " + bandNames[i]);
            }
        }
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
        final double[] rrs_corrected = bandShiftCorrection.correctBandshift(rrs, sensor.getLambaInterface(), qaa, 0.0, 5.0);
        if (rrs_corrected.length == 0) {
            // no correction
            for (int i = 0; i < postVector.size(); i++) {
                postVector.set(i, Float.NaN);
            }
        } else {
            final double[] rrs_shifted = bandShiftCorrection.weightedAverageEqualCorrectionProducts(rrs_corrected);

            for (int i = 0; i < rrs_shifted.length; i++) {
                postVector.set(i, (float) rrs_shifted[i]);
            }
        }
    }
}
