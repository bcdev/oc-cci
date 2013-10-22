package org.esa.beam.occci.qaa;


class InSituConfig implements SensorConfig {

    private static final double[] AW_COEFFICIENTS = {-1.146, -1.366, -0.469};

    // absorption coefficients of pure water (Poe & Fry 1997) start at 410 nm step 2.5 nm
    private static final double[] AW_POPE_FRY = {
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

    // backscatter coefficients of pure water (Zhand & Hu, 2009) start at 410 nm step 2.5 nm
    private static final double[] BBW_ZHANG_HU = {0.0059145, 0.0057619, 0.0056142, 0.0054713, 0.0053329, 0.0051989, 0.0050692,
            0.0049435, 0.0048217, 0.0047037, 0.0045892, 0.0044783, 0.0043707, 0.0042664, 0.0041652, 0.0040670, 0.0039717,
            0.0038791, 0.0037893, 0.0037021, 0.0036174, 0.0035351, 0.0034552, 0.0033775, 0.0033020, 0.0032286, 0.0031573,
            0.0030879, 0.0030204, 0.0029548, 0.0028909, 0.0028288, 0.0027684, 0.0027095, 0.0026522, 0.0025965, 0.0025422,
            0.0024893, 0.0024378, 0.0023876, 0.0023387, 0.0022911, 0.0022446, 0.0021994, 0.0021553, 0.0021123, 0.0020703,
            0.0020294, 0.0019895, 0.0019506, 0.0019127, 0.0018756, 0.0018395, 0.0018042, 0.0017698, 0.0017361, 0.0017033,
            0.0016713, 0.0016400, 0.0016094, 0.0015796, 0.0015504, 0.0015219, 0.0014941, 0.0014669, 0.0014403, 0.0014143,
            0.0013889, 0.0013641, 0.0013398, 0.0013161, 0.0012929, 0.0012702, 0.0012480, 0.0012262, 0.0012050, 0.0011842,
            0.0011639, 0.0011439, 0.0011245, 0.0011054, 0.0010867, 0.0010684, 0.0010506, 0.0010330, 0.0010159, 0.0009991,
            0.0009826, 0.0009665, 0.0009507, 0.0009353, 0.0009201, 0.0009053, 0.0008907, 0.0008765, 0.0008625, 0.0008488,
            0.0008354, 0.0008222, 0.0008093, 0.0007967, 0.0007843, 0.0007721, 0.0007602, 0.0007485, 0.0007370, 0.0007258,
            0.0007147, 0.0007039, 0.0006933, 0.0006828, 0.0006726, 0.0006626, 0.0006527};

    private static final double LAMBDA_LOW = 410.0;
    private static final double LAMBDA_HIGH = 690.0;
    private static final double SPECTRAL_STEP = 2.5;

    private final double[] wavelengths;

    public InSituConfig(double[] wavelengths) {
        this.wavelengths = wavelengths;
    }

    @Override
    public double[] getAwCoefficients() {
        return AW_COEFFICIENTS;
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
        final double[] specificBackscatter = new double[6];

        for (int i = 0; i < wavelengths.length; i++) {
            specificBackscatter[i] = getBbwAtWavelength(wavelengths[i]);

        }
        return specificBackscatter;
    }

    // package access for testing only tb 2013-10-21
    static double getAwAtWavelength(double lambda) {
        final double awPerCm = interpolateAtWavelength(lambda, AW_POPE_FRY);
        return awPerCm * 100.0;
    }

    static double getBbwAtWavelength(double lambda) {
        return interpolateAtWavelength(lambda, BBW_ZHANG_HU);
    }

    private static double interpolateAtWavelength(double lambda, double[] dataArray) {
        if (lambda < LAMBDA_LOW || lambda > LAMBDA_HIGH) {
            throw new IllegalArgumentException("wavelength out of valid range: " + lambda);
        }

        final int lowIndex = (int) Math.floor((lambda - LAMBDA_LOW) / SPECTRAL_STEP);
        final int highIndex = lowIndex + 1;
        final double wlAtLowIndex = lowIndex * SPECTRAL_STEP + LAMBDA_LOW;
        final double k = (lambda - wlAtLowIndex) / SPECTRAL_STEP;
        return dataArray[lowIndex] * (1.0 - k) + dataArray[highIndex] * k;
    }
}
