package org.esa.beam.occci.qaa.binning;


import org.esa.beam.binning.WritableVector;
import org.esa.beam.occci.qaa.QaaResult;

public class ResultMapper {

    private final int[] aph_src;
    private final int[] aph_dest;

    private final int[] atot_src;
    private final int[] atot_dest;

    private final int[] adg_src;
    private final int[] adg_dest;

    private final int[] bbp_src;
    private final int[] bbp_dest;

    public ResultMapper(QaaConfig config) {
        int outIndex = 0;

        aph_src = config.getAphOutIndices();
        aph_dest = new int[aph_src.length];
        for (int i = 0; i < aph_dest.length; i++) {
            aph_dest[i] = outIndex;
            ++outIndex;
        }

        atot_src = config.getAtotOutIndices();
        atot_dest = new int[atot_src.length];
        for (int i = 0; i < atot_dest.length; i++) {
            atot_dest[i] = outIndex;
            ++outIndex;
        }

        adg_src = config.getAdgOutIndices();
        adg_dest = new int[adg_src.length];
        for (int i = 0; i < adg_dest.length; i++) {
            adg_dest[i] = outIndex;
            ++outIndex;
        }

        bbp_src = config.getBbpOutIndices();
        bbp_dest = new int[bbp_src.length];
        for (int i = 0; i < bbp_dest.length; i++) {
            bbp_dest[i] = outIndex;
            ++outIndex;
        }
    }

    public void assign(QaaResult result, WritableVector outVector) {
        final float[] aph = result.getAph();
        for (int i = 0; i < aph_src.length; i++) {
            outVector.set(aph_dest[i], aph[aph_src[i]]);
        }

        final float[] atot = result.getAtot();
        for (int i = 0; i < atot_src.length; i++) {
            outVector.set(atot_dest[i], atot[atot_src[i]]);
        }

        final float[] adg = result.getAdg();
        for (int i = 0; i < adg_src.length; i++) {
            outVector.set(adg_dest[i], adg[adg_src[i]]);
        }

        final float[] bbp = result.getBbp();
        for (int i = 0; i < bbp_src.length; i++) {
            outVector.set(bbp_dest[i], bbp[bbp_src[i]]);
        }
    }
}
