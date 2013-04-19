package org.esa.beam.occci.bandshift.binning;

import org.esa.beam.binning.PostProcessorConfig;
import org.esa.beam.framework.gpf.annotations.Parameter;

public class BandShiftConfig extends PostProcessorConfig {

    @Parameter(notNull = true,
            notEmpty = true,
            valueSet = {"MERIS", "MODISA", "SEAWIFS"})
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
