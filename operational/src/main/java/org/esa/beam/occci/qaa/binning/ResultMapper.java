package org.esa.beam.occci.qaa.binning;


import org.esa.beam.binning.WritableVector;
import org.esa.beam.occci.qaa.QaaResult;

class ResultMapper {

    private final int[] a_pig_src;
    private final int[] a_pig_dest;

    private final int[] a_total_src;
    private final int[] a_total_dest;

    private final int[] a_ys_src;
    private final int[] a_ys_dest;

    private final int[] bb_spm_src;
    private final int[] bb_spm_dest;

    private final int numIOPs;
    private final boolean copyRrsToOutput;

    ResultMapper(QaaConfig config) {
        int outIndex = 0;

        a_pig_src = config.getAPigOutIndices();
        a_pig_dest = new int[a_pig_src.length];
        for (int i = 0; i < a_pig_dest.length; i++) {
            a_pig_dest[i] = outIndex;
            ++outIndex;
        }

        a_total_src = config.getATotalOutIndices();
        a_total_dest = new int[a_total_src.length];
        for (int i = 0; i < a_total_dest.length; i++) {
            a_total_dest[i] = outIndex;
            ++outIndex;
        }

        a_ys_src = config.getAYsOutIndices();
        a_ys_dest = new int[a_ys_src.length];
        for (int i = 0; i < a_ys_dest.length; i++) {
            a_ys_dest[i] = outIndex;
            ++outIndex;
        }

        bb_spm_src = config.getBbSpmOutIndices();
        bb_spm_dest = new int[bb_spm_src.length];
        for (int i = 0; i < bb_spm_dest.length; i++) {
            bb_spm_dest[i] = outIndex;
            ++outIndex;
        }
        numIOPs = outIndex;
        copyRrsToOutput = config.isRrsOut();
    }

    void assign(QaaResult result, float[] rrs, WritableVector outVector) {
        final float[] a_pig = result.getA_PIG();
        for (int i = 0; i < a_pig_src.length; i++) {
            final float value = a_pig[a_pig_src[i]];
            outVector.set(a_pig_dest[i], value);
        }

        final float[] a_total = result.getA_Total();
        for (int i = 0; i < a_total_src.length; i++) {
            final float value = a_total[a_total_src[i]];
            outVector.set(a_total_dest[i], value);
        }

        final float[] a_ys = result.getA_YS();
        for (int i = 0; i < a_ys_src.length; i++) {
            final float value = a_ys[a_ys_src[i]];
            outVector.set(a_ys_dest[i], value);
        }

        final float[] bb_spm = result.getBB_SPM();
        for (int i = 0; i < bb_spm_src.length; i++) {
            final float value = bb_spm[bb_spm_src[i]];
            outVector.set(bb_spm_dest[i], value);
        }

        if (copyRrsToOutput) {
            for (int i = 0; i < rrs.length; i++) {
                outVector.set(numIOPs + i, rrs[i]);
            }
        }
    }
}
