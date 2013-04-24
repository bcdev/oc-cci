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
}
