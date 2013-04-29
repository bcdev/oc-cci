package org.esa.beam.occci.bandshift.binning;

import org.apache.commons.lang.ArrayUtils;
import org.esa.beam.binning.WritableVector;
import org.esa.beam.occci.bandshift.Sensor;

import java.util.ArrayList;

class ResultMapper {

    private final int[] lambdaInSrc;
    private final int[] lambdaInDest;
    private final int[] lambdaShifteSrc;
    private final int[] lambdaShiftedDest;

    public ResultMapper(BandShiftConfig config) {
        final Sensor sensor = Sensor.valueOf(config.getSensorName());

        final ArrayList<Integer> lambdaInSrcList = new ArrayList<Integer>();
        final ArrayList<Integer> lambdaInDestList = new ArrayList<Integer>();
        final ArrayList<Integer> lambdaShiftedSrcList = new ArrayList<Integer>();
        final ArrayList<Integer> lambdaShiftedDestList = new ArrayList<Integer>();

        final int[] outputCenterWavelengths = config.getOutputCenterWavelengths();
        for (int i = 0; i < outputCenterWavelengths.length; i++) {
            boolean mapped = false;
            int outputCenterWavelength = outputCenterWavelengths[i];

            final double[] lambdaI = sensor.getLambdaInterface();
            for (int inIdx = 0; inIdx < lambdaI.length; inIdx++) {
                final long lambdaIn = Math.round(lambdaI[inIdx]);
                if (lambdaIn == outputCenterWavelength) {
                    lambdaInSrcList.add(inIdx);
                    lambdaInDestList.add(i);
                    mapped = true;
                    break;
                }
            }

            if (mapped) {
                continue;
            }

            final double[] lambdaOAvg = sensor.getLambdaOAvg();
            for (int shiftIdx = 0; shiftIdx < lambdaOAvg.length; shiftIdx++) {
                final long lambdaShifted = Math.round(lambdaOAvg[shiftIdx]);
                if (lambdaShifted == outputCenterWavelength) {
                    lambdaShiftedSrcList.add(shiftIdx);
                    lambdaShiftedDestList.add(i);
                    mapped = true;
                    break;
                }
            }

            if (!mapped) {
                throw new IllegalArgumentException("Invalid output wavelength: " + outputCenterWavelength);
            }
        }

        lambdaInSrc = ArrayUtils.toPrimitive(lambdaInSrcList.toArray(new Integer[lambdaInSrcList.size()]));
        lambdaInDest = ArrayUtils.toPrimitive(lambdaInDestList.toArray(new Integer[lambdaInDestList.size()]));
        lambdaShifteSrc = ArrayUtils.toPrimitive(lambdaShiftedSrcList.toArray(new Integer[lambdaShiftedSrcList.size()]));
        lambdaShiftedDest = ArrayUtils.toPrimitive(lambdaShiftedDestList.toArray(new Integer[lambdaShiftedDestList.size()]));
    }

    public void assign(double[] rrs_in, double[] rrs_shifted, WritableVector postVector) {
        for (int i = 0; i < lambdaInSrc.length; i++) {
            final double value = rrs_in[lambdaInSrc[i]];
            postVector.set(lambdaInDest[i], (float) value);
        }

        for (int i = 0; i < lambdaShifteSrc.length; i++) {
            final double value = rrs_shifted[lambdaShifteSrc[i]];
            postVector.set(lambdaShiftedDest[i], (float) value);
        }
    }
}
