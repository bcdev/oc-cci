package org.esa.beam.occci.qaa;

/**
 * @author Marco Peters
 */
public final class QaaConstants {

    // @todo tb/tb 4 make configurable?? 2013-02-22
    public static final float NO_DATA_VALUE = Float.NaN;

    public static final String MERIS = "MERIS";
    public static final String MODIS = "MODIS";
    public static final String SEAWIFS = "SEAWIFS";

    static final int NUM_A_TOTAL_BANDS = 5;
    static final int NUM_BB_SPM_BANDS = 5;
    static final int NUM_A_PIG_BANDS = 3;
    static final int NUM_A_YS_BANDS = 3;

    static final int FLAG_MASK_VALID = 0x0001;
    static final int FLAG_MASK_IMAGINARY = 0x0002;
    static final int FLAG_MASK_NEGATIVE_AYS = 0x0004;
    static final int FLAG_MASK_INVALID = 0x0008;
    static final int FLAG_MASK_A_TOTAL_OOB = 0x0010;
    static final int FLAG_MASK_BB_SPM_OOB = 0x0020;
    static final int FLAG_MASK_A_PIG_OOB = 0x0040;
    static final int FLAG_MASK_A_YS_OOB = 0x0080;

    private QaaConstants() {
    }
}
