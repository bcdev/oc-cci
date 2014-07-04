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

import java.awt.geom.Point2D;
import java.util.Date;
import java.util.List;

public class ProductInsituMatcher {
    private final long maxTimeDifference;
    private final List<Product> products;
    private final List<Record> insituRecords;
    private final boolean geoTest;
    private final GeometryFactory geometryFactory;

    public ProductInsituMatcher(List<Product> products, List<Record> insituRecords, long maxTimeDifference, boolean geoTest) {
        this.products = products;
        this.insituRecords = insituRecords;
        this.maxTimeDifference = maxTimeDifference;
        this.geoTest = geoTest;
        if (geoTest) {
            geometryFactory = new GeometryFactory();
        } else {
            geometryFactory = null;
        }
    }

    private long getMinReferenceTime(Record referenceRecord) {
        Date time = referenceRecord.getTime();
        if (time == null) {
            throw new IllegalArgumentException("Point record has no time information.");
        }
        return time.getTime() - maxTimeDifference;
    }

    private long getMaxReferenceTime(Record referenceRecord) {
        Date time = referenceRecord.getTime();
        if (time == null) {
            throw new IllegalArgumentException("Point record has no time information.");
        }
        return time.getTime() + maxTimeDifference;
    }

    public void match() {
        int counter = 0;
        for (Product product : products) {
            for (Record referenceRecord : insituRecords) {
                long minReferenceTime = getMinReferenceTime(referenceRecord);
                if (minReferenceTime > product.getEndTime()) {
                    continue;
                }

                long maxReferenceTime = getMaxReferenceTime(referenceRecord);
                if (maxReferenceTime < product.getStartTime()) {
                    continue;
                }
                if (geoTest) {
                    Point2D.Float location = referenceRecord.getLocation();
                    if (location == null) {
                        throw new IllegalArgumentException("record has no geo-location: " + referenceRecord);
                    }
                    Geometry productGeometry = product.getGeometry();
                    if (productGeometry == null) {
                        throw new IllegalArgumentException("product is missing geometry: " + product);
                    }
                    Coordinate coordinate = new Coordinate(location.getX(), location.getY());
                    if (!productGeometry.contains(geometryFactory.createPoint(coordinate))) {
                        continue;
                    }
                }
                System.out.println(product.getName());
                counter++;
                break;
            }
        }
        System.out.println();
        System.out.println("counter = " + counter);
    }
}
