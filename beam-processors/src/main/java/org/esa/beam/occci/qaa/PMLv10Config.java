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

public class PMLv10Config implements SensorConfig {

    private static final double[] awCoefficients = {-1.146, -1.366, -0.469};
    private static final double[] wavelengths = {412.0, 443.0, 490.0, 510.0, 555.0, 670.0};
    private static final double referenceWavelength = 555.0;
    private static final double[] specificAbsorptions = {0.004883,   0.006377,   0.014271,   0.028390,   0.057716,   0.433916};
    private static final double[] specificBackscatters = {0.002991,   0.002187,   0.001421,   0.001199,   0.000839,   0.000381};

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
