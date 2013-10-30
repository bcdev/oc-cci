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

public class BandShiftCellProcessor extends CellProcessor {

    private static final int NUM_RRS = 6;
    private static final int NUM_IOP = 3;

    private final BandShiftCorrection bandShiftCorrection;
    private final Sensor sensor;

    private final int[] rrsBandIndices;
    private final int[] iopBandIndices;
    private final double[] rrs;
    private final double[] iop;
    private final ResultMapper resultMapper;

    public BandShiftCellProcessor(VariableContext varCtx, String sensorName, String[] rrsBandNames, String[] iopBandNames, int[] outputCenterWavelengths) {
        super(createOutputFeatureNames(outputCenterWavelengths));

        sensor = Sensor.byName(sensorName);
        final CorrectionContext correctionContext;
        try {
            correctionContext = new CorrectionContext(sensor);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to init BandShiftCellProcessor.", e);
        }
        bandShiftCorrection = new BandShiftCorrection(correctionContext);

        if (rrsBandNames.length != NUM_RRS) {
            throw new IllegalArgumentException("Expecting " + NUM_RRS + " rrs band names got " + rrsBandNames.length);
        }
        rrsBandIndices = BinningUtils.getBandIndices(varCtx, rrsBandNames);
        rrs = new double[NUM_RRS];
        if (iopBandNames.length != NUM_IOP) {
            throw new IllegalArgumentException("Expecting " + NUM_IOP + " iop band names got " + iopBandNames.length);
        }
        iopBandIndices = BinningUtils.getBandIndices(varCtx, iopBandNames);
        iop = new double[NUM_IOP];
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
            rrs[i] = input.get(rrsBandIndices[i]);
        }

        for (int i = 0; i < iop.length; i++) {
            iop[i] = input.get(iopBandIndices[i]);
        }

        final double[] rrs_corrected = bandShiftCorrection.correctBandshift(rrs, sensor.getLambdaInterface(), iop);
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
