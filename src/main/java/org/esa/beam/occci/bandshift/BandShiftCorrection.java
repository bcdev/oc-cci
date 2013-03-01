/*
 * Copyright (C) 2013 Brockmann Consult GmbH (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.beam.occci.bandshift;

import com.bc.ceres.core.Assert;
import org.apache.commons.lang.ArrayUtils;

import java.util.Arrays;

/**
 * A group of procedure and functions to apply a given set of bandshift corrections to a remote sensing reflectances,
 * calculating IOPs at the desired wavelengths using a spectral model that starts from -preferably QAA(v5)derived- IOPs at
 * reference wavelengths, and feeding the obtained IOPs into QAA(v5) in forward mode to calculate corrections factors.
 */
public class BandShiftCorrection {

    private static final int APH_INDEX = 0;
    private static final int ACDM_INDEX = 1;
    private static final int BBP_INDEX = 2;

    //constants used in the calculation of the below-water-reflectance
    private static final double g0 = 0.089;
    private static final double g1 = 0.125;

    private final CorrectionContext context;

    public BandShiftCorrection(CorrectionContext context) {
        this.context = context;
    }

    double[] correctBandshift(double[] rrs, double[] rrs_wavelengths, double[] qaa, double qaa_min, double qaa_max) {
        int number_correction = context.getLambdaI().length;
        Assert.argument(qaa.length == 3, "qaa must have dimension equal to 3");

        // Conversion routine needs RRS at blue and green wavelengths. The blue wavelength
        // is always 443, however the green wavelength differs according to the sensor.
        int blue_index = ArrayUtils.indexOf(rrs_wavelengths, 443.0);
        if (blue_index == ArrayUtils.INDEX_NOT_FOUND) {
            throw new IllegalArgumentException("rrs_wavelengths does not contain blue band at 443");
        }
        double greenWavelength = context.getSensor().getGreenWavelength();
        int green_index = ArrayUtils.indexOf(rrs_wavelengths, greenWavelength);
        if (green_index == ArrayUtils.INDEX_NOT_FOUND) {
            throw new IllegalArgumentException("rrs_wavelengths does not contain green band at " + greenWavelength);
        }

        // @todo  tb/tb continue here 2013-03-01
        // Determine the indexes of the correction input products and create the correction output product names
        int[] input_wavelength_indexes = new int[number_correction];
        for (int i = 0; i < number_correction; i++) {
            double wavelength = context.getLambdaI()[i];
            int rrs_prod_position = ArrayUtils.indexOf(rrs_wavelengths, wavelength);
            if (rrs_prod_position == ArrayUtils.INDEX_NOT_FOUND) {
                throw new IllegalArgumentException("rrs_wavelengths does not contain band at " + wavelength);
            }
            input_wavelength_indexes[i] = rrs_prod_position;
        }
        // Determine which intersection bins have valid IOP values (GT MIN and LT MAX)
        boolean invalid_aph = qaa[APH_INDEX] <= qaa_min || qaa[APH_INDEX] >= qaa_max;
        boolean invalid_acdm = qaa[ACDM_INDEX] <= qaa_min || qaa[ACDM_INDEX] >= qaa_max;
        boolean invalid_bbp = qaa[BBP_INDEX] <= qaa_min || qaa[BBP_INDEX] >= qaa_max;

        if (invalid_aph || invalid_acdm || invalid_bbp) {
            // no correction
            return new double[0];
        }
        // Only continue if there are intersection bins with a valid value for all of the used IOPs (aph, acdm, bbp)
        double[] rrsI = new double[number_correction];
        for (int i = 0; i < rrsI.length; i++) {
            rrsI[i] = rrs[input_wavelength_indexes[i]];
        }
        // Below-water reflectance for blue and green wavelengths
        double rrs_blue = rrs[blue_index] / 0.52 + 1.7 * rrs[blue_index];
        double rrs_green = rrs[green_index] / 0.52 + 1.7 * rrs[green_index];

        double[] rrs_corrected = new double[number_correction];
        for (int i = 0; i < number_correction; i++) {

            // Derive the aph, adg and bbp for the correction input wavelengths starting from the blue band

            double[] spec_model_start = new double[number_correction];
            Arrays.fill(spec_model_start, context.getSpec_model_start());
            double[] iopSM_i = IopSpectralModel.iopSpectralModel(context.getSpec_model_start(),
                                                                 context.getSmsA(),
                                                                 context.getSmsB(),
                                                                 qaa[APH_INDEX],
                                                                 qaa[ACDM_INDEX],
                                                                 qaa[BBP_INDEX],
                                                                 rrs_blue,
                                                                 rrs_green,
                                                                 context.getLambdaI()[i],
                                                                 context.getA_i()[i],
                                                                 context.getB_i()[i]);
            // Derive the aph, adg and bbp for the correction output wavelengths starting from the blue band
            double[] iopSM_o = IopSpectralModel.iopSpectralModel(context.getSpec_model_start(),
                                                                 context.getSmsA(),
                                                                 context.getSmsB(),
                                                                 qaa[APH_INDEX],
                                                                 qaa[ACDM_INDEX],
                                                                 qaa[BBP_INDEX],
                                                                 rrs_blue,
                                                                 rrs_green,
                                                                 context.getLambdaO()[i],
                                                                 context.getA_o()[i],
                                                                 context.getB_o()[i]);
            // Calculate the total absorption and backscattering at correction output wavelengths
            double a_tot_out = iopSM_o[APH_INDEX] + iopSM_o[ACDM_INDEX] + context.getAw_o()[i];
            double bb_tot_out = iopSM_o[BBP_INDEX] + context.getBbw_o()[i];
            // Calculate the total absorption and backscattering at correction input wavelengths
            double a_tot_in = iopSM_i[APH_INDEX] + iopSM_i[ACDM_INDEX] + context.getAw_i()[i];
            double bb_tot_in = iopSM_i[BBP_INDEX] + context.getBbw_i()[i];
            // Using the forward QAA mode, calculate the above water RRS for the correction output wavelengths
            double QAA_u_out = bb_tot_out / (a_tot_out + bb_tot_out);
            double QAA_rrs_bw_out = (g0 + g1 * QAA_u_out) * QAA_u_out;
            double QAA_rrs_aw_out = (-1. * 0.52 * QAA_rrs_bw_out) / ((1.7 * QAA_rrs_bw_out) - 1);
            // Using the forward QAA model, calculate the above wrater RRS for the correction input wavelengths
            double QAA_u_in = bb_tot_in / (a_tot_in + bb_tot_in);
            double QAA_rrs_bw_in = (g0 + g1 * QAA_u_in) * QAA_u_in;
            double QAA_rrs_aw_in = (-1. * 0.52 * QAA_rrs_bw_in) / ((1.7 * QAA_rrs_bw_in) - 1);
            // Correction factors that, when multiplied with the RRS at correction input wavelengths give RRS at correction output wavelengths
            double correction_factor = QAA_rrs_aw_out / QAA_rrs_aw_in;
            // Predict RRS at output wavelengths, multiplying with correction factors
            rrs_corrected[i] = correction_factor * rrsI[i];
        }
        return rrs_corrected;
    }

    double[] weightedAverageEqualCorrectionProducts(double[] rrs_corrected) {
        int[] averageIndices = context.getSensor().getAverageIndices();
        double[] rrs_Averaged = new double[rrs_corrected.length - 1];
        int destIndex = 0;
        for (int srcIndex = 0; srcIndex < rrs_corrected.length; srcIndex++) {
            if (averageIndices[0] == srcIndex) {
                rrs_Averaged[destIndex++] = weightedAverage(averageIndices, rrs_corrected);
            } else if (averageIndices[1] == srcIndex) {
                // skip
            } else {
                rrs_Averaged[destIndex++] = rrs_corrected[srcIndex];
            }
        }
        return rrs_Averaged;
    }

    private double weightedAverage(int[] averageIndices, double[] rrs_corrected) {
        double[] lambdaI = context.getLambdaI();
        double[] lambdaO = context.getLambdaO();
        double double_wl = lambdaO[averageIndices[0]];

        double[] wlDiff = new double[2];
        wlDiff[0] = Math.abs(lambdaI[averageIndices[0]] - double_wl);
        wlDiff[1] = Math.abs(lambdaI[averageIndices[1]] - double_wl);
        double wlDiffSum = wlDiff[0] + wlDiff[1];

        double[] rel_weigth = new double[2];
        final int n_wl = 2;
        rel_weigth[0] = (1 - (wlDiff[0] / wlDiffSum)) / (n_wl - 1);
        rel_weigth[1] = (1 - (wlDiff[1] / wlDiffSum)) / (n_wl - 1);

        return rrs_corrected[averageIndices[0]] * rel_weigth[0] + rrs_corrected[averageIndices[1]] * rel_weigth[1];
    }

}
