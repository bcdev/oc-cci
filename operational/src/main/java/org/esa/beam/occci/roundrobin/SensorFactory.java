package org.esa.beam.occci.roundrobin;

import org.esa.beam.occci.bandshift.Sensor;

class SensorFactory {

    private static final double[] MERIS_WAVELENGTHS =  new double[] {413.0, 443.0, 490.0, 510.0, 560.0, 620.0, 665.0};
    private static final double[] MODIS_WAVELENGTHS = new double[]{412.0, 443.0, 488.0, 531.0, 547.0, 667.0, 678.0};

    static Sensor createToMeris(InSituSpectrum inSituSpectrum) {
        if (inSituSpectrum.isCompleteMeris()) {
            return createMerisToMerisConfig(inSituSpectrum);
        } else if (inSituSpectrum.isCompleteModis()) {
            return createModisToMerisConfig(inSituSpectrum);
        } else if (inSituSpectrum.isCompleteSeaWiFS()) {
            return createSeaWifsToMerisConfig(inSituSpectrum);
        }
        return null;
    }

    static Sensor createToModis(InSituSpectrum inSituSpectrum) {
        if (inSituSpectrum.isCompleteMeris()) {
            return createMerisToModisConfig(inSituSpectrum);
        } else if (inSituSpectrum.isCompleteModis()) {
            return createModisToModisConfig(inSituSpectrum);
        } else if (inSituSpectrum.isCompleteSeaWiFS()) {
            return createSeaWifsToModisConfig(inSituSpectrum);
        }
        return null;
    }

    static Sensor createToSeaWifs(InSituSpectrum inSituSpectrum) {
        if (inSituSpectrum.isCompleteMeris()) {
            return createMerisToSeaWifsConfig(inSituSpectrum);
        } if (inSituSpectrum.isCompleteModis()){
            return createModisToSeaWifsConfig(inSituSpectrum);
        }
        return null;
    }

    private static Sensor createMerisToMerisConfig(InSituSpectrum inSituSpectrum) {
        final double[] inSituWavelengths = inSituSpectrum.getMerisWavelengths();
        final double[] lambdaI = new double[8];

        System.arraycopy(inSituWavelengths, 0, lambdaI, 0, inSituWavelengths.length);
        lambdaI[7] = inSituWavelengths[6];

        final double[] lambdaO = new double[]{413.0, 443.0, 490.0, 510.0, 560.0, 620.0, 665.0, 665.0};
        final double[] lambdaInterface = MERIS_WAVELENGTHS;
        final double[] lambdaOAvg = MERIS_WAVELENGTHS;

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

        final double[] lambdaO = new double[]{412.0, 443.0, 488.0, 531.0, 531.0, 547.0, 667.0, 678.0};
        final double[] lambdaInterface = MODIS_WAVELENGTHS;
        final double[] lambdaOAvg = MODIS_WAVELENGTHS;

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

        final double[] lambdaO = new double[]{412.0, 443.0, 490.0, 510.0, 555.0, 670.0, 670.0, 670.0};
        final double[] lambdaInterface = new double[]{412.0, 443.0, 490.0, 510.0, 555.0, 670.0, 670.0};
        final double[] lambdaOAvg = new double[]{412.0, 443.0, 490.0, 510.0, 555.0, 670.0, 670.0};

        return new Sensor(lambdaI, lambdaO, lambdaInterface, lambdaOAvg, inSituWavelengths[1], new int[]{5, 6});
    }

    private static Sensor createModisToMerisConfig(InSituSpectrum inSituSpectrum) {
        final double[] inSituWavelengths = inSituSpectrum.getModisWavelengths();
        final double[] lambdaI = new double[8];

        lambdaI[0] = inSituWavelengths[0];
        lambdaI[1] = inSituWavelengths[1];
        lambdaI[2] = inSituWavelengths[2];
        lambdaI[3] = inSituWavelengths[2];
        lambdaI[4] = inSituWavelengths[3];
        lambdaI[5] = inSituWavelengths[4];
        lambdaI[6] = inSituWavelengths[5];
        lambdaI[7] = inSituWavelengths[5];

        final double[] lambdaO = new double[]{413.0, 443.0, 490.0, 510.0, 510.0, 560.0, 620.0, 665.0};
        final double[] lambdaInterface = MERIS_WAVELENGTHS;
        final double[] lambdaOAvg = MERIS_WAVELENGTHS;

        return new Sensor(lambdaI, lambdaO, lambdaInterface, lambdaOAvg, inSituWavelengths[1], new int[]{3, 4});
    }

    private static Sensor createModisToModisConfig(InSituSpectrum inSituSpectrum) {
        final double[] inSituWavelengths = inSituSpectrum.getModisWavelengths();
        final double[] lambdaI = new double[8];

        System.arraycopy(inSituWavelengths, 0, lambdaI, 0, inSituWavelengths.length);
        lambdaI[7] = inSituWavelengths[6];

        final double[] lambdaO = new double[]{412.0, 443.0, 488.0, 531.0, 547.0, 667.0, 678.0, 678.0};
        final double[] lambdaInterface = MODIS_WAVELENGTHS;
        final double[] lambdaOAvg = MODIS_WAVELENGTHS;

        return new Sensor(lambdaI, lambdaO, lambdaInterface, lambdaOAvg, inSituWavelengths[1], new int[]{5, 6});
    }

    private static Sensor createModisToSeaWifsConfig(InSituSpectrum inSituSpectrum) {
        final double[] inSituWavelengths = inSituSpectrum.getModisWavelengths();
        final double[] lambdaI = new double[8];

        lambdaI[0] = inSituWavelengths[0];
        lambdaI[1] = inSituWavelengths[1];
        lambdaI[2] = inSituWavelengths[2];
        lambdaI[3] = inSituWavelengths[3];
        lambdaI[4] = inSituWavelengths[4];
        lambdaI[5] = inSituWavelengths[5];
        lambdaI[6] = inSituWavelengths[6];
        lambdaI[7] = inSituWavelengths[6];

        final double[] lambdaO = new double[]{412.0, 443.0, 490.0, 510.0, 555.0, 670.0, 670.0, 670.0};
        final double[] lambdaInterface = new double[]{412.0, 443.0, 490.0, 510.0, 555.0, 670.0, 670.0};
        final double[] lambdaOAvg = new double[]{412.0, 443.0, 490.0, 510.0, 555.0, 670.0, 670.0};

        return new Sensor(lambdaI, lambdaO, lambdaInterface, lambdaOAvg, inSituWavelengths[1], new int[]{5, 6});
    }

    private static Sensor createSeaWifsToMerisConfig(InSituSpectrum inSituSpectrum) {
        final double[] inSituWavelengths = inSituSpectrum.getSeaWifsWavelengths();
        final double[] lambdaI = new double[8];

        lambdaI[0] = inSituWavelengths[0];
        lambdaI[1] = inSituWavelengths[1];
        lambdaI[2] = inSituWavelengths[2];
        lambdaI[3] = inSituWavelengths[3];
        lambdaI[4] = inSituWavelengths[4];
        lambdaI[5] = inSituWavelengths[4];
        lambdaI[6] = inSituWavelengths[5];
        lambdaI[7] = inSituWavelengths[5];

        final double[] lambdaO = new double[]{412.0, 443.0, 490.0, 510.0, 560.0, 620.0, 620.0, 665.0};
        final double[] lambdaInterface = new double[]{412.0, 443.0, 490.0, 510.0, 555.0, 620.0, 665.0};
        final double[] lambdaOAvg = new double[]{412.0, 443.0, 490.0, 510.0, 555.0, 620.0, 665.0};

        return new Sensor(lambdaI, lambdaO, lambdaInterface, lambdaOAvg, inSituWavelengths[1], new int[]{5, 6});
    }

    private static Sensor createSeaWifsToModisConfig(InSituSpectrum inSituSpectrum) {
        final double[] inSituWavelengths = inSituSpectrum.getSeaWifsWavelengths();
        final double[] lambdaI = new double[8];

        lambdaI[0] = inSituWavelengths[0];
        lambdaI[1] = inSituWavelengths[1];
        lambdaI[2] = inSituWavelengths[2];
        lambdaI[3] = inSituWavelengths[3];
        lambdaI[4] = inSituWavelengths[4];
        lambdaI[5] = inSituWavelengths[4];
        lambdaI[6] = inSituWavelengths[5];
        lambdaI[7] = inSituWavelengths[5];

        final double[] lambdaO = new double[]{412.0, 443.0, 488.0, 531.0, 531.0, 547.0, 667.0, 678.0};
        final double[] lambdaInterface = new double[]{412.0, 443.0, 488.0, 531.0, 547.0, 667.0, 678.0};
        final double[] lambdaOAvg = new double[]{412.0, 443.0, 488.0, 531.0, 547.0, 667.0, 678.0};

        return new Sensor(lambdaI, lambdaO, lambdaInterface, lambdaOAvg, inSituWavelengths[1], new int[]{4, 5});
    }
}
