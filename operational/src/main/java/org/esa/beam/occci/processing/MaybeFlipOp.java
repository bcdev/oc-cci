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
import org.esa.beam.framework.datamodel.TiePointGeoCoding;
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
    private GeoCoding sourceGC;
    private GeoCoding referenceGC;


    @Override
    public void initialize() throws OperatorException {
        getLogger().info("testing for flip");
        getLogger().info("source = " + sourceProduct.getName());
        getLogger().info("reference = " + referenceProduct.getName());
        getLogger().info("geographicError = " + geographicError);
        sourceGC = sourceProduct.getGeoCoding();
        referenceGC = referenceProduct.getGeoCoding();
        int width = sourceProduct.getSceneRasterWidth();
        int height = sourceProduct.getSceneRasterHeight();

        if (sourceGC == null) {
            throw new OperatorException("source product has no geo-coding");
        }
        if (referenceGC == null) {
            throw new OperatorException("reference product has no geo-coding");
        }
        if (width != referenceProduct.getSceneRasterWidth()) {
            throw new OperatorException("product with differs");
        }
        if (height != referenceProduct.getSceneRasterHeight()) {
            throw new OperatorException("product height differs");
        }

        boolean onlyBadValues = true;
        if (sourceGC instanceof TiePointGeoCoding) {
            TiePointGeoCoding tiePointGeoCoding = (TiePointGeoCoding) sourceGC;

            ProductData latData = tiePointGeoCoding.getLatGrid().getData();
            for (int i = 0; i < latData.getNumElems(); i++) {
                if (!MathUtils.equalValues(latData.getElemFloatAt(i), -999.0f, 10E-6f)) {
                    onlyBadValues = false;
                    break;
                }
            }
            if (onlyBadValues) {
                ProductData lonData = tiePointGeoCoding.getLonGrid().getData();
                for (int i = 0; i < lonData.getNumElems(); i++) {
                    if (!MathUtils.equalValues(lonData.getElemFloatAt(i), -999.0f, 10E-6f)) {
                        onlyBadValues = false;
                        break;
                    }
                }
            }
            if (onlyBadValues) {
                getLogger().warning("CORRUPTED PRODUCT: latitude and longitude of source is completey filled with '-999'");
                throw new OperatorException("latitude and longitude of source is completey filled with '-999'");
            }
        }

        final PixelPos topLeftPP = new PixelPos(0.5f, 0.5f);
        final PixelPos bottomRightPP = new PixelPos(width - 1 + 0.5f, height - 1 + 0.5f);

        // testing one corner would be enoug, but sometimes the are anomalies and the test fails.
        // so we test a second corner
        if (equalsLatLon(topLeftPP, topLeftPP, "tl,tl") ||
                equalsLatLon(bottomRightPP, bottomRightPP, "br,br")) {
            getLogger().info("flipping not required");
            productReader = sourceProduct.getProductReader();
            setTargetProduct(sourceProduct);
            return;
        } else if (equalsLatLon(topLeftPP, bottomRightPP, "tl,br") ||
                equalsLatLon(bottomRightPP, topLeftPP, "br,tl")) {
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

    private boolean equalsLatLon(final PixelPos sourcePP, final PixelPos referencePP, String corner) {
        GeoPos sourceGP = sourceGC.getGeoPos(sourcePP, null);
        GeoPos referenceGP = referenceGC.getGeoPos(referencePP, null);
        return equalsLatLon(sourceGP, referenceGP, corner);
    }

    private boolean equalsLatLon(final GeoPos source, final GeoPos reference, String corner) {
        StringBuilder sb = new StringBuilder(corner).append(" :");
        sb.append(" source = ").append(getGeoPosString(source));
        sb.append(" reference = ").append(getGeoPosString(reference));

        double distanceInDegree = distanceInDegree(source, reference);
        sb.append(" distanceInDegree = ").append(distanceInDegree);
        boolean result = false;
        if (distanceInDegree <= geographicError) {
            result = true;
        }
        sb.append(" equalsLatLon = ").append(result);
        getLogger().info(sb.toString());
        return result;
    }

    private static String getGeoPosString(GeoPos geoPos) {
        return "[" + geoPos.getLatString() + "," + geoPos.getLonString() + "]";
    }

    private static double distanceInDegree(GeoPos p1, GeoPos p2) {
        double lon1_rad = Math.toRadians(p1.getLon());
        double lon2_rad = Math.toRadians(p2.getLon());
        double lat1_rad = Math.toRadians(p1.getLat());
        double lat2_rad = Math.toRadians(p2.getLat());

        double deltaLon = lon1_rad - lon2_rad;
        double cosDeltaLon = Math.cos(deltaLon);
        double sinLat = Math.sin(lat1_rad) * Math.sin(lat2_rad);
        double cosLat = Math.cos(lat1_rad) * Math.cos(lat2_rad);
        double distanceRad = Math.acos(sinLat + cosLat * cosDeltaLon);
        return Math.toDegrees(distanceRad);
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(MaybeFlipOp.class);
        }
    }
}
