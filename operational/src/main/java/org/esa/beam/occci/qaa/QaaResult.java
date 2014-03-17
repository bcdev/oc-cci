package org.esa.beam.occci.qaa;

public class QaaResult {

    private float[] atot;
    private float[] bbp;
    private float[] aph;
    private float[] adg;
    private int flags;

    public QaaResult() {
        atot = new float[QaaConstants.NUM_IOP_BANDS];
        bbp = new float[QaaConstants.NUM_IOP_BANDS];
        aph = new float[QaaConstants.NUM_IOP_BANDS];
        adg = new float[QaaConstants.NUM_IOP_BANDS];

        setValid(true);
    }

    public void setAtot(float atot, int bandIndex) {
        this.atot[bandIndex] = atot;
    }

    public float[] getAtot() {
        return atot;
    }

    public void setBbp(float bbp, int bandIndex) {
        this.bbp[bandIndex] = bbp;
    }

    public float[] getBbp() {
        return bbp;
    }

    public void setAph(float aph, int bandIndex) {
        this.aph[bandIndex] = aph;
    }

    public float[] getAph() {
        return aph;
    }

    public void setAdg(float adg, int bandIndex) {
        this.adg[bandIndex] = adg;
    }

    public float[] getAdg() {
        return adg;
    }

    @SuppressWarnings("PointlessBitwiseExpression")
    public void setValid(boolean valid) {
        if (valid) {
            flags |= QaaConstants.FLAG_MASK_VALID;
        } else {
            flags &= ~QaaConstants.FLAG_MASK_VALID;
        }
    }

    public void setATotalOutOfBounds(boolean outOfBounds) {
        if (outOfBounds) {
            flags |= QaaConstants.FLAG_MASK_A_TOTAL_OOB;
        } else {
            flags &= ~QaaConstants.FLAG_MASK_A_TOTAL_OOB;
        }
    }

    public void setBbSpmOutOfBounds(boolean outOfBounds) {
        if (outOfBounds) {
            flags |= QaaConstants.FLAG_MASK_BB_SPM_OOB;
        } else {
            flags &= ~QaaConstants.FLAG_MASK_BB_SPM_OOB;
        }
    }

    public void setAPigOutOfBounds(boolean outOfBounds) {
        if (outOfBounds) {
            flags |= QaaConstants.FLAG_MASK_A_PIG_OOB;
        } else {
            flags &= ~QaaConstants.FLAG_MASK_A_PIG_OOB;
        }
    }

    public int getFlags() {
        return flags;
    }

    public void setAYsOutOfBounds(boolean outOfBounds) {
        if (outOfBounds) {
            flags |= QaaConstants.FLAG_MASK_A_YS_OOB;
        } else {
            flags &= ~QaaConstants.FLAG_MASK_A_YS_OOB;
        }
    }

    public void setAYsNegative(boolean isNegative) {
        if (isNegative) {
            flags |= QaaConstants.FLAG_MASK_NEGATIVE_AYS;
        } else {
            flags &= ~QaaConstants.FLAG_MASK_NEGATIVE_AYS;
        }
    }

    public void setInvalid(boolean invalid) {
        if (invalid) {
            flags |= QaaConstants.FLAG_MASK_INVALID;
        } else {
            flags &= ~QaaConstants.FLAG_MASK_INVALID;
        }
    }

    public void setImaginary(boolean imaginary) {
        if (imaginary) {
            flags |= QaaConstants.FLAG_MASK_IMAGINARY;
        } else {
            flags &= ~QaaConstants.FLAG_MASK_IMAGINARY;
        }
    }

    public void invalidate() {
        setMeasurementsTo(QaaConstants.NO_DATA_VALUE);
        clearFlags();
        setInvalid(true);
    }

    public void invalidateImaginary() {
        setMeasurementsTo(QaaConstants.NO_DATA_VALUE);
        clearFlags();
        setImaginary(true);
    }

    public void reset() {
        clearFlags();
        setValid(true);
        setMeasurementsTo(0.f);
    }

    private void clearFlags() {
        flags = 0;
    }

    private void setMeasurementsTo(float value) {
        for (int i = 0; i < aph.length; i++) {
            aph[i] = value;
        }

        for (int i = 0; i < atot.length; i++) {
            atot[i] = value;
        }

        for (int i = 0; i < adg.length; i++) {
            adg[i] = value;
        }

        for (int i = 0; i < bbp.length; i++) {
            bbp[i] = value;
        }
    }

    public void infinityAsNaN() {
        infinityAsNaN(aph);
        infinityAsNaN(atot);
        infinityAsNaN(adg);
        infinityAsNaN(bbp);
    }

    private static void infinityAsNaN(float[] floats) {
        for (int i = 0; i < floats.length; i++) {
            if (Float.isInfinite(floats[i])) {
                floats[i] = Float.NaN;
            }
        }
    }
}
