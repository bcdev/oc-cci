package org.esa.beam.occci.bandshift.binning;

import org.esa.beam.binning.PostProcessorConfig;

public class BandShiftConfig extends PostProcessorConfig {

    private String sensorName;

    public BandShiftConfig(String name) {
        super(name);
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public String getSensorName() {
        return sensorName;
    }
}
