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
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import java.awt.geom.Point2D;

public class SimpleRecord {
    private static final GeometryFactory geometryFactory = new GeometryFactory();

    private final long time;
    private final Point2D.Float location;
    private Point point;

    public SimpleRecord(long time, Point2D.Float location) {
        this.time = time;
        this.location = location;
    }

    long getTime() {
        return time;
    }

    Point getPoint() {
        if (point == null) {
            point = geometryFactory.createPoint(new Coordinate(location.getX(), location.getY()));
        }
        return point;
    }
}
