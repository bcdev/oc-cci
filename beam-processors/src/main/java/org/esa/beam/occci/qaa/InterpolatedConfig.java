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

import org.esa.beam.occci.bandshift.PureWater;

import java.io.IOException;

public class InterpolatedConfig implements SensorConfig {

    private static final double[] awCoefficients = {-1.146, -1.366, -0.469};
    private static final double[] wavelengths = {412.0, 443.0, 490.0, 510.0, 555.0, 670.0};
    private static final double referenceWavelength = 555.0;
    private final double[] specificAbsorptions;
    private final double[] specificBackscatters;

    /*
    from PureWater we get
    aw  = [0.004640989999999999, 0.007098098181818183, 0.015267136363636365, 0.0325987, 0.05964877272727272, 0.4401876363636364]
    bbw = [0.0028979950000000004, 0.0021241986363636366, 0.0013848222727272728, 0.0011698504545454545, 8.202877272727272E-4, 3.7432945454545457E-4]
     */


    public InterpolatedConfig() {
        try {
            PureWater pureWater = PureWater.read();
            specificAbsorptions = new double[wavelengths.length];
            specificBackscatters = new double[wavelengths.length];
            for (int i = 0; i < wavelengths.length; i++) {
                double[] awbw = pureWater.getSpectralDataPureWater(wavelengths[i], 10);
                specificAbsorptions[i] = awbw[0];
                specificBackscatters[i] = awbw[1] / 2.0;
            }
        } catch (IOException e) {
            throw new RuntimeException("failed to read water spectras", e);
        }
    }

    @Override
    public double[] getAwCoefficients() {
        return awCoefficients;
    }

    @Override
    public double getReferenceWavelength() {
        return referenceWavelength;
    }

    @Override
    public double[] getWavelengths() {
        return wavelengths;
    }

    @Override
    public double[] getSpecificAbsorptions() {
        return specificAbsorptions;
    }

    @Override
    public double[] getSpecficBackscatters() {
        return specificBackscatters;
    }
}
