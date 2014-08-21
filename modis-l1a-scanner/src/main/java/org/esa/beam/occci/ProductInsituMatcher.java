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

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import java.util.List;

public class ProductInsituMatcher {
    private final long maxTimeDifference;
    private final List<Product> products;
    private final List<SimpleRecord> insituRecords;
    private final boolean geoTest;


    public ProductInsituMatcher(List<Product> products, List<SimpleRecord> insituRecords, long maxTimeDifference, boolean geoTest) {
        this.products = products;
        this.insituRecords = insituRecords;
        this.maxTimeDifference = maxTimeDifference;
        this.geoTest = geoTest;
    }

    public int match() {
        int counter = 0;
        for (Product product : products) {
            for (SimpleRecord reference : insituRecords) {
                if (reference.getTime() - maxTimeDifference > product.getEndTime()) {
                    continue;
                }

                if (reference.getTime() + maxTimeDifference < product.getStartTime()) {
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
                        continue;
                    }
                }
                System.out.println(product.getName());
                counter++;
                break;
            }
        }
        return counter;
    }
}
