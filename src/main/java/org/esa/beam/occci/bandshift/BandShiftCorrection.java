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
 * Created with IntelliJ IDEA.
 * User: marcoz
 * Date: 30.01.13
 * Time: 10:44
 * To change this template use File | Settings | File Templates.
 */
public class BandShiftCorrection {
    private static final int aph_index  = 0;
    private static final int acdm_index  = 1;
    private static final int bbp_index = 2;
    //constants used in the calculation of the below-water-reflectance
    private static final double g0 = 0.089;
    private static final double g1 = 0.125;

    private final CorrectionContext context;

    public BandShiftCorrection(CorrectionContext context) {
        this.context = context;
    }

    public double[] correct(double[] rrs, double[] rrs_wavelengths, double[] qaa) {
        return correct(rrs, rrs_wavelengths, qaa, 0, 100);
    }

    public double[] correct(double[] rrs, double[] rrs_wavelengths, double[] qaa, double qaa_min, double qaa_max) {
        return new double[0];  //To change body of created methods use File | Settings | File Templates.
    }

    void core(double[] rrs, double[] rrs_wavelengths, double[] qaa, double qaa_min, double qaa_max) {
//        n_qaa_bins = n_elements(qaa_bins)
//        qaa_matrix_dimension = size(qaa_matrix,/dimension)
//        qaa_matrix_n_dimensions = n_elements(qaa_matrix_dimension)
        int number_correction = context.getLambdaI().length;
        int n_start_bands = 1;
        double a_start = context.getSmsA();
        double b_start = context.getSmsB();
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

        // Determine the indexes of the correction input products and create the correction output product names
        int[] input_wavelength_indexes = new int[number_correction];
        double[] rrs_wavelengths_corrected = new double[number_correction];
        for (int i = 0; i < number_correction; i++) {
            double wavelength = context.getLambdaI()[i];
            int rrs_prod_position = ArrayUtils.indexOf(rrs_wavelengths, wavelength);
            if (rrs_prod_position == ArrayUtils.INDEX_NOT_FOUND) {
                throw new IllegalArgumentException("rrs_wavelengths does not contain band at " + wavelength);
            }
            input_wavelength_indexes[i] = rrs_prod_position;
            rrs_wavelengths_corrected[i] = context.getLambdaO()[i];
        }
        // Determine which intersection bins have valid IOP values (GT MIN and LT MAX)
        boolean invalid_aph = qaa[aph_index] <= qaa_min || qaa[aph_index] >= qaa_max;
        boolean invalid_acdm = qaa[acdm_index] <= qaa_min || qaa[acdm_index] >= qaa_max;
        boolean invalid_bbp = qaa[bbp_index] <= qaa_min || qaa[bbp_index] >= qaa_max;

        if (invalid_aph || invalid_acdm || invalid_bbp) {
            // no correction
            return;
        }
        // Only continue if there are intersection bins with a valid value for all of the used IOPs (aph, acdm, bbp)
        double[] rrsI = new double[number_correction];
        for (int i = 0; i < rrsI.length; i++) {
            rrsI[i] = rrs[input_wavelength_indexes[i]];
        }
        // Below-water reflectance for blue and green wavelengths
        double rrs_blue = rrs[blue_index] / 0.52 + 1.7 * rrs[blue_index];
        double rrs_green = rrs[green_index] / 0.52 + 1.7 * rrs[green_index];

        for (int i = 0; i < number_correction; i++) {

            // Derive the aph, adg and bbp for the correction input wavelengths starting from the blue band

            double[] spec_model_start = new double[number_correction];
            Arrays.fill(spec_model_start, context.getSpec_model_start());
            double[] iopSM_i = IopSpectralModel.iopSpectralModel(context.getSpec_model_start(),
                                                                   context.getSmsA(),
                                                                   context.getSmsB(),
                                                                   qaa[aph_index],
                                                                   qaa[acdm_index],
                                                                   qaa[bbp_index],
                                                                   rrs_blue,
                                                                   rrs_green,
                                                                   context.getLambdaI()[i],
                                                                   context.getA_i()[i],
                                                                   context.getB_i()[i]);
            // Derive the aph, adg and bbp for the correction output wavelengths starting from the blue band
            double[] iopSM_o = IopSpectralModel.iopSpectralModel(context.getSpec_model_start(),
                                                                   context.getSmsA(),
                                                                   context.getSmsB(),
                                                                   qaa[aph_index],
                                                                   qaa[acdm_index],
                                                                   qaa[bbp_index],
                                                                   rrs_blue,
                                                                   rrs_green,
                                                                   context.getLambdaO()[i],
                                                                   context.getA_o()[i],
                                                                   context.getB_o()[i]);
            // Calculate the total absorption and backscattering at correction output wavelengths
            double a_tot_out = iopSM_o[aph_index] + iopSM_o[acdm_index] + context.getAw_o()[i];
            double bb_tot_out = iopSM_o[bbp_index] + context.getBbw_o()[i];
            // Calculate the total absorption and backscattering at correction input wavelengths
            double a_tot_in = iopSM_i[aph_index] + iopSM_i[acdm_index] + context.getAw_i()[i];
            double bb_tot_in = iopSM_i[bbp_index] + context.getBbw_i()[i];
            // Using the forward QAA mode, calculate the above water RRS for the correction output wavelengths
            double QAA_u_out = bb_tot_out/(a_tot_out + bb_tot_out);
            double QAA_rrs_bw_out = (g0 + g1 * QAA_u_out) * QAA_u_out;
            double QAA_rrs_aw_out = (-1.*0.52*QAA_rrs_bw_out)/((1.7*QAA_rrs_bw_out)-1);
            // Using the forward QAA model, calculate the above wrater RRS for the correction input wavelengths
            double QAA_u_in = bb_tot_in/(a_tot_in + bb_tot_in);
            double QAA_rrs_bw_in = (g0 + g1 * QAA_u_in) * QAA_u_in;
            double QAA_rrs_aw_in = (-1.*0.52*QAA_rrs_bw_in)/((1.7*QAA_rrs_bw_in)-1);
            // Correction factors that, when multiplied with the RRS at correction input wavelengths give RRS at correction output wavelengths
            double correction_factors = QAA_rrs_aw_out/QAA_rrs_aw_in;
            // Predict RRS at output wavelengths, multiplying with correction factors
            double rrs_matrix_corrected = correction_factors * rrsI[i];
        }

    }

}
