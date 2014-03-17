/*
 * Copyright (C) 2014 Brockmann Consult GmbH (info@brockmann-consult.de)
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

package org.esa.beam.occci.qaa;


import static java.lang.Math.*;

/**
 * IImplementation of v6 of the QAA algorithm,
 * that is in line with the sedas implementation.
 */
public class QaaAlgorithmv6Seadas implements QaaAlgo {

    private static final int IDX_410 = 0; // 415.5nm
    private static final int IDX_440 = 1; // 442.5nm
    private static final int IDX_490 = 2; // 490nm
    private static final int IDX_510 = 3; // 510nm
    private static final int IDX_560 = 4; // 560nm
    private static final int IDX_670 = 5; // 670nm

    private static final double G0 = 0.08945;
    private static final double G0_SQUARE = G0 * G0;
    private static final double G1 = 0.1247;

    private final double[] a_coeffs;
    private final double[] aw;
    private final double[] bbw;
    private final double[] wavelengths;

    public QaaAlgorithmv6Seadas(SensorConfig sensorConfig) {
        a_coeffs = sensorConfig.getAwCoefficients();
        aw = sensorConfig.getSpecificAbsorptions();
        bbw = sensorConfig.getSpecficBackscatters();
        wavelengths = sensorConfig.getWavelengths();
    }

    @Override
    public QaaResult process(float[] Rrsf, QaaResult recycle) {
        QaaResult result = ensureResult(recycle);
        final double[] Rrs = new double[Rrsf.length];
        for (int i = 0; i < Rrsf.length; i++) {
            Rrs[i] = Rrsf[i];
        }

        /* Test for bad Rrs at idx555 */
        if (Rrs[IDX_560] <= 0.0) {
            Rrs[IDX_560] = 0.001f;
        }

        /* pre-test 670 */
        final double up_667 = 20.0 * pow(Rrs[IDX_560], 1.5);
        final double lw_667 = 0.9 * pow(Rrs[IDX_560], 1.7);
        if (Rrs[IDX_670] > up_667 || Rrs[IDX_670] < lw_667) {
            final double t1 = 1.27 * pow(Rrs[IDX_560], 1.47);
            final double t2 = 0.00018 * pow((Rrs[IDX_490] / Rrs[IDX_560]), -3.19);
            Rrs[IDX_670] = t1 + t2;
        }

        /* Step 0 */
        // Coefficients for converting Rrs to rrs (above to below sea-surface)
        final double[] rrs = new double[Rrs.length];
        for (int i = 0; i < Rrs.length; i++) {
            rrs[i] = Rrs[i] / (0.52 + 1.7 * Rrs[i]);
        }

        /* Step 1 */
        // Coefficients as defined by Gordon et al. (1988) and modified by Lee et al. (2002) to estimate bb/a+bb referred to as U
        final double[] U = new double[Rrs.length];
        for (int i = 0; i < U.length; i++) {
            U[i] = (sqrt(G0_SQUARE + 4.0 * G1 * rrs[i]) - G0) / (2.0 * G1);
        }

        /* Step 2 */
        // Estimation of a at reference wavelength
        final double aRef;
        final int idxRef;
        if (Rrs[IDX_670] >= 0.0015) {
            aRef = aw[IDX_670] + 0.07 * pow(Rrs[IDX_670] / Rrs[IDX_440], 1.1);
            idxRef = IDX_670;
        } else {
            final double numer = Rrs[IDX_440] + Rrs[IDX_490];
            final double denom = Rrs[IDX_560] + 5. * (Rrs[IDX_670] / Rrs[IDX_490]) * Rrs[IDX_670];
            final double X = log10(numer / denom);
            final double rho = a_coeffs[0] + a_coeffs[1] * X + a_coeffs[2] * X * X;
            aRef = aw[IDX_560] + pow(10.0, rho);
            idxRef = IDX_560;
        }

        /* Step 3 */
        // Estimation of bbp at reference wavelength
        final double bbpRef = ((U[idxRef] * aRef) / (1 - U[idxRef])) - bbw[idxRef];

        /* Step 4 */
        // Exponent of bbp
        final double ratio = rrs[IDX_440] / rrs[IDX_560];
        final double Y = 2.0 * (1.0 - 1.2 * exp(-0.9 * ratio));

        /* Step 5 */
        // Estimation of bbp and bb at all wavelengths
        final double[] bbp = new double[wavelengths.length];
        final double[] bb = new double[wavelengths.length];
        for (int i = 0; i < bb.length; i++) {
            bbp[i] = bbpRef * pow(wavelengths[idxRef] / wavelengths[i], Y);
            bb[i] = bbp[i] + bbw[i];

        }

         /* Step 6 */
        // Estimation of a at all wavelengths
        final double[] a = new double[wavelengths.length];
        for (int i = 0; i < a.length; i++) {
            a[i] = ((1.0 - U[i]) * bb[i]) / U[i];
        }

        /* step 7 */
        // Estimate ratio of aph411/aph443
        final double Ratio_aph = 0.74 + 0.2 / (0.8 + ratio);

        /* step 8 */
        // Estimate ratio of adg411/adg443
        final double Slope_adg = 0.015 + 0.002 / (0.6 + ratio);
        final double Ratio_adg = exp(Slope_adg * (wavelengths[IDX_440] - wavelengths[IDX_410]));

        /* step 9 */
        // Estimation of adg and aph at all wavelengths
        double denom = Ratio_adg - Ratio_aph;
        double dif1 = a[IDX_410] - Ratio_aph * a[IDX_440];
        double dif2 = aw[IDX_410] - Ratio_aph * aw[IDX_440];
        final double ag_440 = (dif1 - dif2) / denom;

        final double[] adg = new double[wavelengths.length];
        final double[] aph = new double[wavelengths.length];
        for (int i = 0; i < adg.length; i++) {
            adg[i] = ag_440 * exp(Slope_adg * (wavelengths[IDX_440] - wavelengths[i]));
            aph[i] = a[i] - adg[i] - aw[i];
        }

        /* ZP Lee, 17 August 2007 (get_qaa.c)*/
        for (int i = 0; i < a.length; i++) {
            if (a[i] > 0 && !Double.isNaN(a[i])) {
                a[i] = Math.max(a[i], aw[i] * 1.05);
            }
        }
        for (int i = 0; i < bb.length; i++) {
            if (bb[i] > 0 && !Double.isNaN(bb[i])) {
                bb[i] = Math.max(bb[i], bbw[i] * 1.05);
            }
        }

        for (int i = 0; i < QaaConstants.NUM_IOP_BANDS; i++) {
            result.setAtot((float) a[i], i);
            result.setBbp((float) bbp[i], i);
        }

        for (int i = 0; i < QaaConstants.NUM_IOP_BANDS; i++) {
            result.setAph((float) aph[i], i);
            result.setAdg((float) adg[i], i);
        }

        return result;
    }

    static QaaResult ensureResult(QaaResult recycle) {
        QaaResult result = recycle;
        if (result == null) {
            result = new QaaResult();
        }
        result.reset();

        return result;
    }
}
