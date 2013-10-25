package org.esa.beam.occci.roundrobin;

import org.esa.beam.occci.bandshift.Sensor;

class SensorFactory {

    static Sensor createToMeris(InSituSpectrum inSituSpectrum) {
        if (inSituSpectrum.isCompleteMeris()) {
            return createMerisToMerisConfig(inSituSpectrum);
        }
        return null;
    }

    static Sensor createToModis(InSituSpectrum inSituSpectrum) {
        if (inSituSpectrum.isCompleteMeris()) {
            return createMerisToModisConfig(inSituSpectrum);
        }
        return null;
    }

    static Sensor createToSeaWifs(InSituSpectrum inSituSpectrum) {
        if (inSituSpectrum.isCompleteMeris()) {
            return createMerisToSeaWifsConfig(inSituSpectrum);
        }
        return null;
    }

    private static Sensor createMerisToMerisConfig(InSituSpectrum inSituSpectrum) {
        final double[] inSituWavelengths = inSituSpectrum.getMerisWavelengths();
        final double[] lambdaI = new double[8];

        System.arraycopy(inSituWavelengths, 0, lambdaI, 0, inSituWavelengths.length);
        lambdaI[7] = inSituWavelengths[6];

        final double[] lambdaO = {413.0, 443.0, 490.0, 510.0, 560.0, 620.0, 665.0, 665.0};
        final double[] lambdaInterface = new double[]{413.0, 443.0, 490.0, 510.0, 560.0, 620.0, 665.0};
        final double[] lambdaOAvg = {413.0, 443.0, 490.0, 510.0, 560.0, 620.0, 665.0};

        return new Sensor(lambdaI, lambdaO, lambdaInterface, lambdaOAvg, inSituWavelengths[1], new int[]{5, 6});
    }

    private static Sensor createMerisToModisConfig(InSituSpectrum inSituSpectrum) {
        final double[] inSituWavelengths = inSituSpectrum.getMerisWavelengths();
        final double[] lambdaI = new double[8];

        lambdaI[0] = inSituWavelengths[0];
        lambdaI[1] = inSituWavelengths[1];
        lambdaI[2] = inSituWavelengths[2];
        lambdaI[3] = inSituWavelengths[3];
        lambdaI[4] = inSituWavelengths[4];
        lambdaI[5] = inSituWavelengths[4];
        lambdaI[6] = inSituWavelengths[6];
        lambdaI[7] = inSituWavelengths[6];

        final double[] lambdaO = {412.0, 443.0, 488.0, 531.0, 531.0, 547.0, 667.0, 678.0};
        final double[] lambdaInterface = new double[]{412.0, 443.0, 488.0, 531.0, 547.0, 667.0, 678.0};
        final double[] lambdaOAvg = {412.0, 443.0, 488.0, 531.0, 547.0, 667.0, 678.0};

        return new Sensor(lambdaI, lambdaO, lambdaInterface, lambdaOAvg, inSituWavelengths[1], new int[]{3, 4});
    }

    private static Sensor createMerisToSeaWifsConfig(InSituSpectrum inSituSpectrum) {
        final double[] inSituWavelengths = inSituSpectrum.getMerisWavelengths();
        final double[] lambdaI = new double[8];

        lambdaI[0] = inSituWavelengths[0];
        lambdaI[1] = inSituWavelengths[1];
        lambdaI[2] = inSituWavelengths[2];
        lambdaI[3] = inSituWavelengths[3];
        lambdaI[4] = inSituWavelengths[4];
        lambdaI[5] = inSituWavelengths[6];
        lambdaI[6] = inSituWavelengths[6];
        lambdaI[7] = inSituWavelengths[6];

        final double[] lambdaO = {412.0, 443.0, 490.0, 510.0, 555.0, 670.0, 670.0, 670.0};
        final double[] lambdaInterface = new double[]{412.0, 443.0, 490.0, 510.0, 555.0, 670.0, 670.0};
        final double[] lambdaOAvg = {412.0, 443.0, 490.0, 510.0, 555.0, 670.0, 670.0};

        return new Sensor(lambdaI, lambdaO, lambdaInterface, lambdaOAvg, inSituWavelengths[1], new int[]{5, 6});
    }
}
