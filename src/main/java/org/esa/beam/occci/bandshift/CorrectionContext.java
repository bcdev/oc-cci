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


import java.io.IOException;

/**
 * @author MarcoZ
 */
public class CorrectionContext {

    private final Sensor sensor;
    private final double smsA;
    private final double smsB;
    private final double[] lambdaI;
    private final double[] lambdaO;
    private final double[] aw_i;
    private final double[] aw_o;
    private final double[] bbw_i;
    private final double[] bbw_o;
    private final double[] a_i;
    private final double[] a_o;
    private final double[] b_i;
    private final double[] b_o;
    private final double spec_model_start;

    public CorrectionContext(Sensor sensor) throws IOException {
        this.sensor = sensor;
        lambdaI = sensor.getLambdaI();
        lambdaO = sensor.getLambdaO();

        double[] subset = new double[lambdaI.length];
        PureWater pureWater = PureWater.read();
        int number_i = lambdaI.length;
        int number_o = lambdaO.length;

        aw_i = new double[number_i];
        bbw_i = new double[number_i];
        a_i = new double[number_i];
        b_i = new double[number_i];

        aw_o = new double[number_o];
        bbw_o = new double[number_o];
        a_o = new double[number_o];
        b_o = new double[number_o];

        for (int i = 0; i < number_i; i++) {
            double[] awbw = pureWater.getSpectralDataPureWater(lambdaI[i]);
            if (awbw != null) {
                aw_i[i] = awbw[0];
                bbw_i[i] = awbw[1]/2.0;
                double[] abBricaud = IopSpectralModel.getABBricaud(lambdaI[i]);
                a_i[i] = abBricaud[0];
                b_i[i] = abBricaud[1];
            } else {
                throw new IllegalArgumentException();
            }
        }

        for (int i = 0; i < number_o; i++) {
            double[] awbw = pureWater.getSpectralDataPureWater(lambdaO[i]);
            if (awbw != null) {
                aw_o[i] = awbw[0];
                bbw_o[i] = awbw[1]/2.0;
                double[] abBricaud = IopSpectralModel.getABBricaud(lambdaO[i]);
                a_o[i] = abBricaud[0];
                b_o[i] = abBricaud[1];
            } else {
                throw new IllegalArgumentException();
            }
        }

        //spectral model is applied from blue wavelength onwards, for each of the corrections.
        spec_model_start = 443.0;
        double[] sms = IopSpectralModel.getABBricaud(spec_model_start);
        smsA =sms[0];
        smsB =sms[1];
    }


}
