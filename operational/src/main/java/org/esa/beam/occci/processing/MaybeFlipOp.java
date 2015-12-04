/*
 * Copyright (C) 2015 Brockmann Consult GmbH (info@brockmann-consult.de)
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
import org.esa.beam.framework.dataio.ProductFlipper;
import org.esa.beam.framework.dataio.ProductReader;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.GeoCoding;
import org.esa.beam.framework.datamodel.GeoPos;
import org.esa.beam.framework.datamodel.PixelPos;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.framework.gpf.Operator;
import org.esa.beam.framework.gpf.OperatorException;
import org.esa.beam.framework.gpf.OperatorSpi;
import org.esa.beam.framework.gpf.Tile;
import org.esa.beam.framework.gpf.annotations.OperatorMetadata;
import org.esa.beam.framework.gpf.annotations.Parameter;
import org.esa.beam.framework.gpf.annotations.SourceProduct;
import org.esa.beam.util.math.MathUtils;

import java.awt.*;
import java.io.IOException;

/**
 * Created by marcoz on 26.11.15.
 */
@OperatorMetadata(alias = "OC-CCI.maybeflip",
        description = "Flips the soucre product horizontal and vertical " +
                "if they are afterwards aligned",
        authors = "Marco Zuehlke (Brockmann Consult)",
        copyright = "(C) 2015 by Brockmann Consult",
        version = "0.1")
public class MaybeFlipOp extends Operator {

    @SourceProduct()
    private Product sourceProduct;

    @SourceProduct()
    private Product referenceProduct;

    @Parameter(defaultValue = "1.0E-5f",
            description = "Defines the maximum lat/lon error in degree between the products.")
    private float geographicError;
    private ProductReader productReader;


    @Override
    public void initialize() throws OperatorException {
        getLogger().info("testing for flip");
        getLogger().info("sourceProduct = " + sourceProduct);
        getLogger().info("referenceProduct = " + referenceProduct);
        getLogger().info("geographicError = " + geographicError);
        GeoCoding sgc = sourceProduct.getGeoCoding();
        GeoCoding rgc = referenceProduct.getGeoCoding();
        int width = sourceProduct.getSceneRasterWidth();
        int height = sourceProduct.getSceneRasterHeight();

        if (sgc == null) {
            throw new OperatorException("source product has no geo-coding");
        }
        if (rgc == null) {
            throw new OperatorException("reference product has no geo-coding");
        }
        if (width != referenceProduct.getSceneRasterWidth()) {
            throw new OperatorException("product with differs");
        }
        if (height != referenceProduct.getSceneRasterHeight()) {
            throw new OperatorException("product height differs");
        }

        final PixelPos pixelPos = new PixelPos();
        final GeoPos gpSource = new GeoPos();
        final GeoPos gpReference = new GeoPos();

        pixelPos.x = 0.5f;
        pixelPos.y = 0.5f;
        sgc.getGeoPos(pixelPos, gpSource);
        rgc.getGeoPos(pixelPos, gpReference);
        if (equalsLatLon(gpSource, gpReference, geographicError)) {
            pixelPos.x = width - 1 + 0.5f;
            pixelPos.y = height - 1 + 0.5f;
            sgc.getGeoPos(pixelPos, gpSource);
            rgc.getGeoPos(pixelPos, gpReference);
            if (equalsLatLon(gpSource, gpReference, geographicError)) {
                getLogger().info("flipping not required");
                productReader = sourceProduct.getProductReader();
                setTargetProduct(sourceProduct);
                return;
            }
        } else {
            final PixelPos pixelPos2 = new PixelPos();
            pixelPos2.x = width - 1 + 0.5f;
            pixelPos2.y = height - 1 + 0.5f;
            rgc.getGeoPos(pixelPos2, gpReference);
            if (equalsLatLon(gpSource, gpReference, geographicError)) {
                sgc.getGeoPos(pixelPos2, gpSource);
                rgc.getGeoPos(pixelPos, gpReference);
                if (equalsLatLon(gpSource, gpReference, geographicError)) {
                    getLogger().info("flipping horizontal and vertical");
                    final int flipType = ProductFlipper.FLIP_BOTH;
                    try {
                        productReader = new ProductFlipper(flipType, false);
                        Product targetProduct = productReader.readProductNodes(sourceProduct, null);
                        setTargetProduct(targetProduct);
                        return;
                    } catch (IOException e) {
                        throw new OperatorException("product flipp failed", e);
                    }
                }
            }
        }
        throw new OperatorException("Unsupported product geometry. Neither fliped nor aligned.");
    }

    @Override
    public void computeTile(Band band, Tile targetTile, ProgressMonitor pm) throws OperatorException {
        ProductData destBuffer = targetTile.getRawSamples();
        Rectangle rectangle = targetTile.getRectangle();
        try {
            productReader.readBandRasterData(band,
                                              rectangle.x,
                                              rectangle.y,
                                              rectangle.width,
                                              rectangle.height,
                                              destBuffer, pm);
            targetTile.setRawSamples(destBuffer);
        } catch (IOException e) {
            throw new OperatorException(e);
        }
    }

    private static boolean equalsLatLon(final GeoPos pos1, final GeoPos pos2, final float eps) {
        return MathUtils.equalValues(pos1.lat, pos2.lat, eps) && MathUtils.equalValues(pos1.lon, pos2.lon, eps);
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(MaybeFlipOp.class);
        }
    }
}
