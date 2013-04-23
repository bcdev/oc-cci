package org.esa.beam.occci.qaa.binning;


import org.esa.beam.binning.PostProcessorConfig;
import org.esa.beam.framework.gpf.annotations.Parameter;

public class QaaConfig extends PostProcessorConfig {

    @Parameter(notNull = true,
            notEmpty = true,
            valueSet = {"MERIS", "MODIS", "SEAWIFS"})
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
            "coefficient bands. Indices correspond to the input reflectance band wavelengths.",
            defaultValue = "0,1,2,3,4,5")
    private int[] a_total_out_indices;

    @Parameter(description = "A comma separated list of band indices defining the output suspended matter" +
            "backscattering bands. Indices correspond to the input reflectance band wavelengths.",
            defaultValue = "0,1,2,3,4,5")
    private int[] bb_spm_out_indices;

    @Parameter(description = "A comma separated list of band indices defining the output pigment absorption" +
            "coefficient bands. Indices correspond to the input reflectance band wavelengths.",
            defaultValue = "0,1,2,3,4,5")
    private int[] a_pig_out_indices;

    @Parameter(description = "A comma separated list of band indices defining the output yellow substance absorption" +
            "coefficient bands. Indices correspond to the input reflectance band wavelengths.",
            defaultValue = "0,1,2,3,4,5")
    private int[] a_ys_out_indices;

    public QaaConfig() {
        super("QAA");

        a_total_out_indices = new int[0];
        a_pig_out_indices = new int[0];
        a_ys_out_indices = new int[0];
        bb_spm_out_indices = new int[0];
    }

    public String[] getBandNames() {
        return bandNames;
    }

    public void setBandNames(String[] bandNames) {
        this.bandNames = bandNames;
    }

    public int[] getA_total_out_indices() {
        return a_total_out_indices;
    }

    public void setA_total_out_indices(int[] a_total_out_indices) {
        this.a_total_out_indices = a_total_out_indices;
    }

    public int[] getBb_spm_out_indices() {
        return bb_spm_out_indices;
    }

    public void setBb_spm_out_indices(int[] bb_spm_out_indices) {
        this.bb_spm_out_indices = bb_spm_out_indices;
    }

    public int[] getA_pig_out_indices() {
        return a_pig_out_indices;
    }

    public void setA_pig_out_indices(int[] a_pig_out_indices) {
        this.a_pig_out_indices = a_pig_out_indices;
    }

    public int[] getA_ys_out_indices() {
        return a_ys_out_indices;
    }

    public void setA_ys_out_indices(int[] a_ys_out_indices) {
        this.a_ys_out_indices = a_ys_out_indices;
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }
}