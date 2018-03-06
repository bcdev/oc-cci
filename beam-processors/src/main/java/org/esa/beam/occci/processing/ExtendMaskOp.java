/*
 * Copyright (C) 2016 Brockmann Consult GmbH (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.beam.occci.processing;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.framework.gpf.Operator;
import org.esa.beam.framework.gpf.OperatorException;
import org.esa.beam.framework.gpf.OperatorSpi;
import org.esa.beam.framework.gpf.Tile;
import org.esa.beam.framework.gpf.annotations.OperatorMetadata;
import org.esa.beam.framework.gpf.annotations.Parameter;
import org.esa.beam.framework.gpf.annotations.SourceProduct;
import org.esa.beam.framework.gpf.annotations.TargetProduct;
import org.esa.beam.gpf.operators.standard.BandMathsOp;

import javax.media.jai.BorderExtender;
import javax.media.jai.BorderExtenderConstant;
import java.awt.*;

/**
 * Created by marcoz
 */
@OperatorMetadata(alias = "OC-CCI.extendMask",
        description = "Extends a mask by a given amount",
        authors = "Marco Zuehlke (Brockmann Consult)",
        copyright = "(C) 2015 by Brockmann Consult",
        version = "0.1")
public class ExtendMaskOp extends Operator {

    @SourceProduct()
    private Product sourceProduct;

    @TargetProduct
    private Product targetProduct;

    @Parameter(notEmpty = true, notNull = true)
    private String maskExpression;

    @Parameter(notEmpty = true, notNull = true)
    private String extendedMaskName;

    @Parameter(defaultValue = "1")
    private int extension;

    private Band maskBand;
    private Rectangle productRect;
    private BorderExtender borderExtender;

    @Override
    public void initialize() throws OperatorException {
        targetProduct = sourceProduct;
        targetProduct.addBand(extendedMaskName, ProductData.TYPE_INT8);

        BandMathsOp.BandDescriptor bandDescriptor = new BandMathsOp.BandDescriptor();
        bandDescriptor.name = "maskBand";
        bandDescriptor.expression = maskExpression;
        bandDescriptor.type = ProductData.TYPESTRING_INT8;

        BandMathsOp bandMathsOp = new BandMathsOp();
        bandMathsOp.setParameterDefaultValues();
        bandMathsOp.setSourceProduct(sourceProduct);
        bandMathsOp.setTargetBandDescriptors(bandDescriptor);
        maskBand = bandMathsOp.getTargetProduct().getBand("maskBand");

        productRect = new Rectangle(sourceProduct.getSceneRasterWidth(), sourceProduct.getSceneRasterHeight());
        borderExtender = new BorderExtenderConstant(new double[]{0});
    }

    @Override
    public void computeTile(Band band, Tile targetTile, ProgressMonitor pm) throws OperatorException {

        Rectangle sourceRect = new Rectangle(targetTile.getRectangle());
        sourceRect.grow(extension, extension);

        Tile sourceTile = getSourceTile(maskBand, sourceRect, borderExtender);
        byte[] sourceBuffer = sourceTile.getDataBufferByte();
        byte[] targetBuffer = targetTile.getDataBufferByte();

        for (int y = targetTile.getMinY(); y <= targetTile.getMaxY(); y++) {
            for (int x = targetTile.getMinX(); x <= targetTile.getMaxX(); x++) {
                int targetIndex = targetTile.getDataBufferIndex(x, y);
                boolean maskSetInWindow = isMaskSetInWindow(sourceTile, sourceBuffer, x, y);
                targetBuffer[targetIndex] = (byte) (maskSetInWindow ? 1 : 0);
            }
        }
    }

    private boolean isMaskSetInWindow(Tile sourceTile, byte[] sourceBuffer, int x, int y) {
        for (int sy = y - extension; sy < y + extension + 1; sy++) {
            for (int sx = x - extension; sx < x + extension +1; sx++) {
                if (sourceBuffer[sourceTile.getDataBufferIndex(sx, sy)] != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public static class Spi extends OperatorSpi {
        public Spi() {
            super(ExtendMaskOp.class);
        }
    }
}
