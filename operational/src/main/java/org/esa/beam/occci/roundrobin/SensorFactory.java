package org.esa.beam.occci.roundrobin;

import org.esa.beam.occci.bandshift.Sensor;

class SensorFactory {

    static Sensor createMerisLikeToMeris(double[] inSituWavelengths) {
        final double[] lambdaI = new double[8];

        System.arraycopy(inSituWavelengths, 0, lambdaI, 0, inSituWavelengths.length);
        lambdaI[7] = inSituWavelengths[6];

        final double[] lambdaO = {413.0, 443.0, 490.0, 510.0, 560.0, 620.0, 665.0, 665.0};
        final double[] lambdaInterface = new double[]{413.0, 443.0, 490.0, 510.0, 560.0, 620.0, 665.0};
        final double[] lambdaOAvg = {413.0, 443.0, 490.0, 510.0, 560.0, 620.0, 665.0};

        return new Sensor(lambdaI, lambdaO, lambdaInterface, lambdaOAvg, inSituWavelengths[1], new int[]{5, 6});
    }
}
