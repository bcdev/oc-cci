package org.esa.beam.occci.bandshift.binning;

import org.esa.beam.binning.PostProcessorConfig;
import org.esa.beam.framework.gpf.annotations.Parameter;

public class BandShiftConfig extends PostProcessorConfig {

    @Parameter(notNull = true,
            notEmpty = true,
            valueSet = {"MERIS", "MODISA", "SEAWIFS"})
    private String sensorName;

    @Parameter(notNull = true,
            notEmpty = true,
            description = "A comma-separated list of input band-names. The list of bands\n" +
                    "must consist of six reflectance bands followed by three IOP bands. \n" +
                    "{rrs_0, rrs_1, rrs_2, rrs_3, rrs_4, rrs_5, aph, acdm, bbp}\n" +
                    "Where:\n" +
                    "Reflectance center wavelengths (rrs_0, rrs_1, rrs_2, rrs_3, rrs_4, rrs_5) for \n" +
                    "                         MODIS (412. , 443. , 488. , 531. , 547. , 667.)\n" +
                    "                         MERIS (413. , 443. , 490. , 510. , 560. , 665.)\n" +
                    "                       SEAWIFS (412. , 443. , 490. , 510. , 555. , 667.)\n" +
                    "aph:  phytoplankton absorption\n" +
                    "acdm: detritus-gelbstoff absorption\n" +
                    "bbp:  particle backscattering")
    private String[] bandNames;

    @Parameter(notNull = true,
            notEmpty = true,
            description = "Defines the output bands of the processor. Must be a comma-separated list\n" +
                    "of desired center-wavelengths for the bands. Available wavelengths are:\n" +
                    "{412, 413, 443, 488, 490, 510, 531, 547, 555, 560, 665, 667, 670} nm",
            defaultValue = "412,443,490,510,555,667")
    private int[] outputCenterWavelengths;

    public BandShiftConfig(String name) {
        super(name);
        bandNames = new String[0];
        outputCenterWavelengths = new int[0];
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setBandNames(String[] bandNames) {
        this.bandNames = bandNames;
    }

    public String[] getBandNames() {
        return bandNames;
    }

    public int[] getOutputCenterWavelengths() {
        return outputCenterWavelengths;
    }

    public void setOutputCenterWavelengths(int[] outputCenterWavelengths) {
        this.outputCenterWavelengths = outputCenterWavelengths;
    }
}
