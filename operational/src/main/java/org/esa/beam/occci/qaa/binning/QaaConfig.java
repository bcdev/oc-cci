package org.esa.beam.occci.qaa.binning;


import org.esa.beam.binning.CellProcessorConfig;
import org.esa.beam.framework.gpf.annotations.Parameter;
import org.esa.beam.occci.qaa.QaaConstants;

public class QaaConfig extends CellProcessorConfig {

    @Parameter(notNull = true,
            notEmpty = true,
            valueSet = {QaaConstants.MERIS, QaaConstants.MODIS, QaaConstants.SEAWIFS})
    private String sensorName;

    @Parameter(notNull = true,
            notEmpty = true,
            description = "A comma-separated list of input band-names. The list of bands\n" +
                    "must consist of six reflectance bands wWhere:\n" +
                    "Reflectance center wavelengths (rrs_0, rrs_1, rrs_2, rrs_3, rrs_4, rrs_5) for \n" +
                    "                         MODIS (412. , 443. , 488. , 531. , 547. , 667.)\n" +
                    "                         MERIS (413. , 443. , 490. , 510. , 560. , 665.)\n" +
                    "                       SEAWIFS (412. , 443. , 490. , 510. , 555. , 667.)")
    private String[] bandNames;

    @Parameter(description = "A comma separated list of band indices defining the output total absorption" +
            "coefficient bands. Indices correspond to the input reflectance band wavelengths. Maximum index = 5.",
            defaultValue = "0,1,2,3,4")
    private int[] aTotalOutIndices;

    @Parameter(description = "A comma separated list of band indices defining the output suspended matter" +
            "backscattering bands. Indices correspond to the input reflectance band wavelengths. Maximum index = 5.",
            defaultValue = "0,1,2,3,4")
    private int[] bbSpmOutIndices;

    @Parameter(description = "A comma separated list of band indices defining the output pigment absorption" +
            "coefficient bands. Indices correspond to the input reflectance band wavelengths. Maximum index = 5.",
            defaultValue = "0,1,2")
    private int[] aPigOutIndices;

    @Parameter(description = "A comma separated list of band indices defining the output yellow substance absorption" +
            "coefficient bands. Indices correspond to the input reflectance band wavelengths. Maximum index = 5.",
            defaultValue = "0,1,2")
    private int[] aYsOutIndices;

    @Parameter(description = "If 'true' the input rrs are included in the output",
               defaultValue = "false")
    private boolean rrsOut;

    public QaaConfig() {
        super("QAA");

        aTotalOutIndices = new int[0];
        aPigOutIndices = new int[0];
        aYsOutIndices = new int[0];
        bbSpmOutIndices = new int[0];
        bandNames = new String[0];
    }

    public String[] getBandNames() {
        return bandNames;
    }

    public void setBandNames(String[] bandNames) {
        this.bandNames = bandNames;
    }

    public int[] getATotalOutIndices() {
        return aTotalOutIndices;
    }

    public void setATotalOutIndices(int[] aTotalOutIndices) {
        this.aTotalOutIndices = aTotalOutIndices;
    }

    public int[] getBbSpmOutIndices() {
        return bbSpmOutIndices;
    }

    public void setBbSpmOutIndices(int[] bbSpmOutIndices) {
        this.bbSpmOutIndices = bbSpmOutIndices;
    }

    public int[] getAPigOutIndices() {
        return aPigOutIndices;
    }

    public void setAPigOutIndices(int[] aPigOutIndices) {
        this.aPigOutIndices = aPigOutIndices;
    }

    public int[] getAYsOutIndices() {
        return aYsOutIndices;
    }

    public void setAYsOutIndices(int[] aYsOutIndices) {
        this.aYsOutIndices = aYsOutIndices;
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public boolean isRrsOut() {
        return rrsOut;
    }

    public void setRrsOut(boolean rrsOut) {
        this.rrsOut = rrsOut;
    }
}