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
public class Sensor {

    public static final String MERIS_NAME = "MERIS";
    public static final String MODISA_NAME = "MODISA";
    public static final String SEAWIFS_NAME = "SEAWIFS";

    public static final Sensor SEAWIFS = SEAWIFS();
    public static final Sensor MODISA = MODISA();
    public static final Sensor MERIS = MERIS();

    private final double[] lambdaI;
    private final double[] lambdaO;
    private final double[] lambdaInterface;
    private final double[] lambdaOAvg;
    private final double greenWavelength;
    private final int[] averageIndices;

    public static Sensor byName(String name) {
        if (SEAWIFS_NAME.equalsIgnoreCase(name)) {
            return SEAWIFS;
        } else if (MERIS_NAME.equalsIgnoreCase(name)) {
            return MERIS;
        } else if (MODISA_NAME.equalsIgnoreCase(name)) {
            return MODISA;
        }
        throw new IllegalArgumentException("Unsupported sensor: " + name);
    }

    public Sensor(double[] lambdaI, double[] lambdaO, double[] lambdaInterface, double[] lamdaOAvg, double greenWavelength, int[] averageIndices) {
        this.lambdaI = lambdaI;
        this.lambdaO = lambdaO;
        this.lambdaInterface = lambdaInterface;
        this.lambdaOAvg = lamdaOAvg;
        this.greenWavelength = greenWavelength;
        this.averageIndices = averageIndices;
    }

    public double[] getLambdaI() {
        return lambdaI;
    }

    public double[] getLambdaInterface() {
        return lambdaInterface;
    }

    public double[] getLambdaO() {
        return lambdaO;
    }

    public double getGreenWavelength() {
        return greenWavelength;
    }

    public double[] getLambdaOAvg() {
        return lambdaOAvg;
    }

    public int[] getAverageIndices() {
        return averageIndices;
    }

    private static Sensor SEAWIFS() {
        return new Sensor(new double[]{412., 490., 510., 555., 555., 555., 667., 667.},
                new double[]{413., 488., 531., 531., 547., 560., 665., 670.},
                new double[]{412., 443., 490., 510., 555., 667.},
                new double[]{413., 488., 531., 547., 560., 665., 670.},
                555.0,
                new int[]{2, 3});
    }

    private static Sensor MERIS() {
        return new Sensor(new double[]{413., 490., 510., 560., 560., 560., 665., 665.},
                new double[]{412., 488., 531., 531., 547., 555., 667., 670.},
                new double[]{413., 443., 490., 510., 560., 665.},
                new double[]{412., 488., 531., 547., 555., 667., 670.},
                560.0,
                new int[]{2, 3});
    }

    private static Sensor MODISA() {
        return new Sensor(new double[]{412., 488., 488., 531., 547., 547., 667., 667.},
                new double[]{413., 510., 490., 510., 560., 555., 665., 670.},
                new double[]{412., 443., 488., 531., 547., 667.},
                new double[]{413., 510., 490., 560., 555., 665., 670.},
                547.0,
                new int[]{1, 3});
    }
}
