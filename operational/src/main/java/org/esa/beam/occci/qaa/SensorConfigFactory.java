package org.esa.beam.occci.qaa;

import org.esa.beam.framework.gpf.OperatorException;

public class SensorConfigFactory {

    public static SensorConfig get(String sensorName) {
        if (QaaConstants.MERIS.equals(sensorName)) {
            return new MerisConfig();
        } else if (QaaConstants.MODIS.equals(sensorName)) {
            return new ModisConfig();
        } else if (QaaConstants.SEAWIFS.equals(sensorName)) {
            return new SeaWifsConfig();
        }

        throw new OperatorException("Unsupported sensor name: " + sensorName);
    }

    public static SensorConfig get(double[] wavelengths) {
        if (wavelengths.length != 6) {
            throw new OperatorException("Invalid number of wavelengths" + wavelengths.length);
        }
        return new InSituConfig(wavelengths);
    }
}
