/*
 * Copyright (C) 2018 Brockmann Consult GmbH (info@brockmann-consult.de) 
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

package org.esa.snap.occci.l3;

import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.util.ProductUtils;

/**
 * Created by olafd on 26.02.24.
 */
@OperatorMetadata(alias = "Snap.Occci.Meris.Merge.L2Idepix",
        description = "Specific operator which merges MERIS L2 water (i.e. Polymer) and Idepix NetCDF products." +
                "Missing latitude/longitude pixels in L2 product (such as Polymer) originating from L1b " +
                "are filled with corresponding pixels from the tie point grids " +
                "which were interpolated and exported as 'lat' and 'lon' into the Idepix Netcdf-BEAM product.",
        authors = "Olaf Danne (Brockmann Consult)",
        copyright = "(C) 2024 by Brockmann Consult",
        version = "0.2")
public class MerisMergeL2IdepixOp extends Operator {

    @SourceProduct()
    private Product sourceProduct;

    @SourceProduct()
    private Product referenceProduct;

    @TargetProduct
    private Product targetProduct;


    @Override
    public void initialize() throws OperatorException {
        getLogger().info("Testing MERIS lat/lon grid for missing pixels. Repair with TPG from reference product...");
        getLogger().info("source = " + sourceProduct.getName());
        getLogger().info("reference = " + referenceProduct.getName());

        validateInputProducts();

        targetProduct = sourceProduct;
        targetProduct.setSceneGeoCoding(referenceProduct.getSceneGeoCoding());
        targetProduct.getBand("latitude").setSourceImage(referenceProduct.getBand("lat").getSourceImage());
        targetProduct.getBand("longitude").setSourceImage(referenceProduct.getBand("lon").getSourceImage());
        ProductUtils.copyFlagBands(referenceProduct, targetProduct, true);
    }

    private void validateInputProducts() {
        GeoCoding sourceGC = sourceProduct.getSceneGeoCoding();
        GeoCoding referenceGC = referenceProduct.getSceneGeoCoding();

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

        if (referenceProduct.getBand("lat") == null) {
            throw new OperatorException("Band 'lat' is missing in NetCDF reference product.");
        }

        if (referenceProduct.getBand("lon") == null) {
            throw new OperatorException("Band 'lon' is missing in NetCDF reference product.");
        }
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(MerisMergeL2IdepixOp.class);
        }
    }
}
