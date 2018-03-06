package org.esa.beam.occci.bandshift.binning;

import org.esa.beam.binning.CellProcessorConfig;
import org.esa.beam.framework.gpf.annotations.Parameter;

public class BandShiftConfig extends CellProcessorConfig {

    @Parameter(notNull = true,
            notEmpty = true,
            valueSet = {"MERIS", "MODISA", "SEAWIFS"})
    private String sensorName;

    @Parameter(notNull = true,
            notEmpty = true,
            description = "A comma-separated list of rrs input band-names. The list of bands\n" +
                    "must consist of six reflectance bands. \n" +
                    "{rrs_0, rrs_1, rrs_2, rrs_3, rrs_4, rrs_5}\n" +
                    "Where:\n" +
                    "Reflectance center wavelengths (rrs_0, rrs_1, rrs_2, rrs_3, rrs_4, rrs_5) for \n" +
                    "                         MODIS (412. , 443. , 488. , 531. , 547. , 667.)\n" +
                    "                         MERIS (413. , 443. , 490. , 510. , 560. , 665.)\n" +
                    "                       SEAWIFS (412. , 443. , 490. , 510. , 555. , 670.)\n")
    private String[] rrsBandNames;

    @Parameter(notNull = true,
            notEmpty = true,
            description = "A comma-separated list of iop input band-names. The list of bands\n" +
                    "must consist of three IOP bands. \n" +
                    "{aph, acdm, bbp}\n" +
                    "Where:\n" +
                    "aph:  phytoplankton absorption\n" +
                    "acdm: detritus-gelbstoff absorption\n" +
                    "bbp:  particle backscattering")
    private String[] iopBandNames;

    @Parameter(notNull = true,
            notEmpty = true,
            description = "Defines the output bands of the processor. Must be a comma-separated list\n" +
                    "of desired center-wavelengths for the bands. Available wavelengths are:\n" +
                    "{412, 413, 443, 488, 490, 510, 531, 547, 555, 560, 665, 667, 670} nm",
            defaultValue = "412,443,490,510,555,670")
    private int[] outputCenterWavelengths;

    public BandShiftConfig(String name) {
        super(name);
        rrsBandNames = new String[0];
        iopBandNames = new String[0];
        outputCenterWavelengths = new int[0];
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public String getSensorName() {
        return sensorName;
    }

    public String[] getRrsBandNames() {
        return rrsBandNames;
    }

    public void setRrsBandNames(String...rrsBandNames) {
        this.rrsBandNames = rrsBandNames;
    }

    public String[] getIopBandNames() {
        return iopBandNames;
    }

    public void setIopBandNames(String...iopBandNames) {
        this.iopBandNames = iopBandNames;
    }

    public int[] getOutputCenterWavelengths() {
        return outputCenterWavelengths;
    }

    public void setOutputCenterWavelengths(int[] outputCenterWavelengths) {
        this.outputCenterWavelengths = outputCenterWavelengths;
    }
}
