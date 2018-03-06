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

import org.esa.beam.framework.datamodel.GeoCoding;
import org.esa.beam.framework.datamodel.GeoPos;
import org.esa.beam.framework.datamodel.PixelPos;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.framework.gpf.OperatorException;
import org.esa.beam.framework.gpf.OperatorSpi;
import org.esa.beam.framework.gpf.annotations.OperatorMetadata;
import org.esa.beam.framework.gpf.annotations.Parameter;
import org.esa.beam.framework.gpf.annotations.SourceProduct;
import org.esa.beam.framework.gpf.annotations.TargetProduct;
import org.esa.beam.framework.gpf.pointop.ProductConfigurer;
import org.esa.beam.framework.gpf.pointop.Sample;
import org.esa.beam.framework.gpf.pointop.SampleConfigurer;
import org.esa.beam.framework.gpf.pointop.SampleOperator;
import org.esa.beam.framework.gpf.pointop.WritableSample;
import org.esa.beam.util.math.MathUtils;
import org.geotools.referencing.CRS;
import org.geotools.referencing.datum.DefaultEllipsoid;
import org.opengis.referencing.datum.Ellipsoid;

import java.awt.geom.Point2D;

/**
 * Created by marcoz
 */
@OperatorMetadata(alias = "OC-CCI.distanceOnEarth",
        description = "Calculates distance on earth for supersampling",
        authors = "Marco Zuehlke (Brockmann Consult)",
        copyright = "(C) 2016 by Brockmann Consult",
        version = "0.1")
public class DistanceOnEarthOp extends SampleOperator {

    private static final String DISTANCE_ON_EARTH = "distanceOnEarth";
    @SourceProduct()
    private Product sourceProduct;

    @TargetProduct
    private Product targetProduct;

    @Parameter(
            description = "The square of the number of pixels used for super-sampling an input pixel into multiple sub-pixels",
            defaultValue = "3")
    private Integer superSampling;

    private double earthRadius;
    private Point2D.Float[] samplingPoints;
    private GeoCoding gc;

    @Override
    protected void prepareInputs() throws OperatorException {
        Ellipsoid ellipsoid = CRS.getEllipsoid(sourceProduct.getGeoCoding().getMapCRS());
        earthRadius = ellipsoid != null ? ellipsoid.getSemiMajorAxis() : DefaultEllipsoid.WGS84.getSemiMajorAxis();
        samplingPoints = createSamplingPoints(getSuperSamplingSteps(superSampling));
        gc = sourceProduct.getGeoCoding();
    }

    @Override
    protected void configureTargetProduct(ProductConfigurer productConfigurer) {
        super.configureTargetProduct(productConfigurer);
        productConfigurer.addBand(DISTANCE_ON_EARTH, ProductData.TYPE_FLOAT32, Double.NaN);
        productConfigurer.copyBands("Rrs_555");
    }

    @Override
    protected void configureSourceSamples(SampleConfigurer sampleConfigurer) throws OperatorException {
        // no thing to do hier
    }

    @Override
    protected void configureTargetSamples(SampleConfigurer sampleConfigurer) throws OperatorException {
        sampleConfigurer.defineSample(0, DISTANCE_ON_EARTH);

    }

    @Override
    protected void computeSample(int x, int y, Sample[] sourceSamples, WritableSample targetSample) {
        PixelPos sourceCenter = new PixelPos(x + 0.5f, y + 0.5f);
        GeoPos scGP = gc.getGeoPos(sourceCenter, null);
        double maxDistance = 0;
        for (Point2D.Float samplingPoint : samplingPoints) {
            GeoPos sGP = gc.getGeoPos(new PixelPos(x + samplingPoint.x, y + samplingPoint.y), null);
            double distance = MathUtils.sphereDistanceDeg(earthRadius, scGP.getLon(), scGP.getLat(), sGP.getLon(), sGP.getLat());
            maxDistance = Math.max(distance, maxDistance);
        }
        targetSample.set(maxDistance);
    }

    private static Point2D.Float[] createSamplingPoints(float[] samplingSteps) {
        int numSuperSamplingSteps = samplingSteps.length * samplingSteps.length;
        Point2D.Float[] superSamplingPoints = new Point2D.Float[numSuperSamplingSteps];
        int index = 0;
        for (float dy : samplingSteps) {
            for (float dx : samplingSteps) {
                superSamplingPoints[index++] = new Point2D.Float(dx, dy);
            }
        }
        return superSamplingPoints;
    }


    private static float[] getSuperSamplingSteps(Integer superSampling) {
        if (superSampling == null || superSampling <= 1) {
            return new float[]{0.5f};
        } else {
            float[] samplingStep = new float[superSampling];
            for (int i = 0; i < samplingStep.length; i++) {
                samplingStep[i] = (i * 2.0F + 1.0F) / (2.0F * superSampling);
            }
            return samplingStep;
        }
    }


    public static class Spi extends OperatorSpi {
        public Spi() {
            super(DistanceOnEarthOp.class);
        }
    }
}
