package org.esa.beam.occci.processing;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.core.SubProgressMonitor;
import org.esa.beam.framework.dataio.ProductFlipper;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.util.Debug;
import org.esa.beam.util.Guardian;

import java.io.IOException;

/**
 * todo: add comment
 * To change this template use File | Settings | File Templates.
 * Date: 04.07.2018
 * Time: 09:51
 *
 * @author olafd
 */
public class OccciProductFlipper extends ProductFlipper {

    private int flipType;

    public OccciProductFlipper(int flipType, boolean sourceProductOwner) {
        super(flipType, sourceProductOwner);
        this.flipType = flipType;
    }


    @Override
    public void readBandRasterData(Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, ProgressMonitor pm) throws IOException {
        Band sourceBand = (Band) bandMap.get(destBand);
        Debug.assertNotNull(sourceBand);

        Guardian.assertNotNull("destBand", destBand);
        Guardian.assertNotNull("destBuffer", destBuffer);

        if (destBuffer.getNumElems() < destWidth * destHeight) {
            throw new IllegalArgumentException("destination buffer too small");
        }
        if (destBuffer.getNumElems() > destWidth * destHeight) {
            throw new IllegalArgumentException("destination buffer too big");
        }


        final int sourceW = sourceProduct.getSceneRasterWidth();
        final int sourceH = sourceProduct.getSceneRasterHeight();

        // change to standard BEAM product flipper:
        // use doubles here for sufficient precision if high bits are set (applies e.g. for VIIRS)
        double[] line = new double[sourceW];

        pm.beginTask("Flipping raster data...", destHeight);
        try {
            int sourceX;
            int sourceY;
            if (flipType == FLIP_HORIZONTAL) {
                for (int j = 0; j < destHeight; j++) {
                    if (pm.isCanceled()) {
                        break;
                    }
                    sourceY = destOffsetY + j;
                    sourceBand.readPixels(0, sourceY, sourceW, 1, line, SubProgressMonitor.create(pm, 1));
                    for (int i = 0; i < destWidth; i++) {
                        sourceX = sourceW - (destOffsetX + i + 1);
                        destBuffer.setElemDoubleAt(j * destWidth + i, line[sourceX]);
                    }
                }
            } else if (flipType == FLIP_VERTICAL) {
                for (int j = 0; j < destHeight; j++) {
                    if (pm.isCanceled()) {
                        break;
                    }
                    sourceY = sourceH - (destOffsetY + j + 1);
                    sourceBand.readPixels(0, sourceY, sourceW, 1, line, SubProgressMonitor.create(pm, 1));
                    for (int i = 0; i < destWidth; i++) {
                        sourceX = destOffsetX + i;
                        destBuffer.setElemDoubleAt(j * destWidth + i, line[sourceX]);
                    }
                }
            } else {
                for (int j = 0; j < destHeight; j++) {
                    if (pm.isCanceled()) {
                        break;
                    }
                    sourceY = sourceH - (destOffsetY + j + 1);
                    sourceBand.readPixels(0, sourceY, sourceW, 1, line, SubProgressMonitor.create(pm, 1));
                    for (int i = 0; i < destWidth; i++) {
                        sourceX = sourceW - (destOffsetX + i + 1);
                        destBuffer.setElemDoubleAt(j * destWidth + i, line[sourceX]);
                    }
                }
            }
        } finally {
            pm.done();
        }
    }
}
