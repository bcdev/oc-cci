package org.esa.beam.occci.qaa;


class InSituConfig implements SensorConfig {

    private static final double[] awCoefficients = {-1.146, -1.366, -0.469};
    // absorption coefficients of pure water (Poe & Fry 1997) start at 410 nm step 2.5 nm
    private static final double[] awPopeFry = {
            0.0000473, 0.0000452, 0.0000444, 0.0000442, 0.0000454, 0.0000474, 0.0000478, 0.0000482, 0.0000495, 0.0000504,
            0.000053, 0.000058, 0.0000635, 0.0000696, 0.0000751, 0.000083, 0.0000922, 0.0000969, 0.0000962, 0.0000957,
            0.0000979, 0.0001005, 0.0001011, 0.000102, 0.000106, 0.000109, 0.000114, 0.000121, 0.000127, 0.000131, 0.000136,
            0.000144, 0.00015, 0.000162, 0.000173, 0.000191, 0.000204, 0.000228, 0.000256, 0.00028, 0.000325, 0.000372,
            0.000396, 0.000399, 0.000409, 0.000416, 0.000417, 0.000428, 0.000434, 0.000447, 0.000452, 0.000466, 0.000474,
            0.000489, 0.000511, 0.000537, 0.000565, 0.000593, 0.000596, 0.000606, 0.000619, 0.00064, 0.000642, 0.000672,
            0.000695, 0.000733, 0.000772, 0.000836, 0.000896, 0.000989, 0.0011, 0.00122, 0.001351, 0.001516, 0.001672,
            0.001925, 0.002224, 0.00247, 0.002577, 0.002629, 0.002644, 0.002665, 0.002678, 0.002707, 0.002755, 0.00281,
            0.002834, 0.002904, 0.002916, 0.002995, 0.003012, 0.003077, 0.003108, 0.00322, 0.00325, 0.00335, 0.0034, 0.00358,
            0.00371, 0.00393, 0.0041, 0.00424, 0.00429, 0.00436, 0.00439, 0.00448, 0.00448, 0.00461, 0.00465, 0.00478,
            0.00486, 0.00502, 0.00516, 0.00538};
    private static final double lambda0 = 410.0;
    private static final double lambdaHigh = 690.0;

    private final double[] wavelengths;

    public InSituConfig(double[] wavelengths) {
        this.wavelengths = wavelengths;
    }

    @Override
    public double[] getAwCoefficients() {
        return awCoefficients;
    }

    @Override
    public double getReferenceWavelength() {
        return wavelengths[4];  // @todo 2 tb/tb check if this is always true tb 2013-10-21

    }

    @Override
    public double[] getWavelengths() {
        return wavelengths;
    }

    @Override
    public double[] getSpecificAbsorptions() {
        final double[] specificAbsorptions = new double[6];

        for (int i = 0; i < wavelengths.length; i++) {
            specificAbsorptions[i] = getAwAtWavelength(wavelengths[i]);

        }
        return specificAbsorptions;
    }

    @Override
    public double[] getSpecficBackscatters() {
        return new double[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    // package access for testing only tb 2013-10-21
    static double getAwAtWavelength(double lambda) {
        if (lambda < lambda0 || lambda > lambdaHigh) {
            throw new IllegalArgumentException("wavelength out of valid range: " + lambda);
        }

        final int lowIndex = (int) Math.floor((lambda - lambda0) / 2.5);
        final int highIndex = lowIndex + 1;
        final double wlAtLowIndex = lowIndex * 2.5 + lambda0;
        final double k = (lambda - wlAtLowIndex) / 2.5;
        final double awPerCm = awPopeFry[lowIndex] * (1.0 - k) + awPopeFry[highIndex] * k;
        return awPerCm * 100.0;
    }
}
