/*
 * Copyright (C) 2014 Brockmann Consult GmbH (info@brockmann-consult.de)
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

package org.esa.beam.occci;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductInsituMatcherFast {


    private final long maxTimeDifference;
    private final List<SimpleRecord> insituRecords;
    private final boolean geoTest;
    private final InsituDB insituDB;
    private final List<Product> products;
    private Geometry northsea;


    public ProductInsituMatcherFast(List<Product> products, List<SimpleRecord> insituRecords, long maxTimeDifference, boolean geoTest) {
        this.products = products;
        insituDB = InsituDB.create(insituRecords, 0);
        this.insituRecords = insituRecords;
        this.maxTimeDifference = maxTimeDifference;
        this.geoTest = geoTest;

        WKTReader wktReader = new WKTReader();

        String acadiaWKT = "polygon((-71.00 41.00, -52.00 41.00, -52.00 52.00, -71.00 52.00, -71.00 41.00))";
        String northseaWKT = "polygon((-19.94 40.00, 0.00 40.00, 0.00 49.22, 12.99 53.99, 13.06 65.00, 0.00 65.00, 0.0 60.00, -20.00 60.00, -19.94 40.00))";
        try {
            northsea = wktReader.read(acadiaWKT);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public int match() {
        int northseaTest = 0;
        int counter = 0;
        Set<Product> matches = new HashSet<Product>();
        for (Product product : products) {
            long startTime = product.getStartTime() - maxTimeDifference;
            int index = insituDB.getIndexForTime(startTime);
            if (index == -1) {
                long endTime = product.getStartTime() + maxTimeDifference;
                index = insituDB.getIndexForTime(endTime);
                if (index == -1) {
                    continue;
                }
            }
            boolean finishedWithProduct = false;
            while (!finishedWithProduct) {
                SimpleRecord reference = insituDB.getRecord(index);
                if (reference == null) {
                    finishedWithProduct = true;
                    continue;
                }
                long referenceTime = reference.getTime();

                if (referenceTime - maxTimeDifference > product.getEndTime()) {
                    finishedWithProduct = true;
                    continue;
                }
                if (referenceTime + maxTimeDifference < product.getStartTime()) {
                    //test next referenceRecord;
                    index++;
                    continue;
                }
                if (geoTest) {
                    Point point = reference.getPoint();
                    if (point == null) {
                        throw new IllegalArgumentException("record has no geo-location: " + reference);
                    }
                    Geometry productGeometry = product.getGeometry();
                    if (productGeometry == null) {
                        throw new IllegalArgumentException("product is missing geometry: " + product);
                    }
                    if (!productGeometry.contains(point)) {
                        index++;
                        continue;
                    }
//                    if (productGeometry.intersects(northsea)) {
//                        System.out.println(new File(product.getName()).getName());
//                        ProductWithInsituComparer.ModisGeoProduct geop = (ProductWithInsituComparer.ModisGeoProduct) product;
//                        System.out.println(geop.getWkt());
//                        northseaTest++;
//                    }
                }
                //System.out.println(product.getName());
                finishedWithProduct = true;
                matches.add(product);
                counter++;
            }
        }
        System.err.println("matches.size() = " + matches.size());
        System.err.println("counter = " + counter);
        System.err.println("northseaTest = " + northseaTest);
        return counter;
    }
}
