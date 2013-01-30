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


/**
 * @author MarcoZ
 */
public enum Sensor {

    SEAWIFS(new double[]{412., 490., 510., 555., 555., 555., 667., 667.},
            new double[]{413., 488., 531., 531., 547., 560., 665., 670.}, 555.0),
    MERIS(new double[]{413., 490., 510., 560., 560., 560., 665., 665.},
          new double[]{412., 488., 531., 531., 547., 555., 667., 670.}, 560.0),
    MODISA(new double[]{412., 488., 488., 531., 547., 547., 667., 667.},
           new double[]{413., 510., 490., 510., 560., 555., 665., 670.}, 547.0);

    private final double[] lambdaI;
    private final double[] lambdaO;
    private final double greenWavelength;

    private Sensor(double[] lambdaI, double[] lambdaO, double  greenWavelength) {
        this.lambdaI = lambdaI;
        this.lambdaO = lambdaO;
        this.greenWavelength = greenWavelength;
    }

    public double[] getLambdaI() {
        return lambdaI;
    }

    public double[] getLambdaO() {
        return lambdaO;
    }

    public double getGreenWavelength() {
        return greenWavelength;
    }
}
