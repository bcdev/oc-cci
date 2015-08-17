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

package org.esa.beam.occci;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.TopologyException;
import com.vividsolutions.jts.geom.prep.PreparedGeometry;
import com.vividsolutions.jts.geom.prep.PreparedGeometryFactory;
import com.vividsolutions.jts.io.WKTReader;

import java.io.File;
import java.text.ParseException;

/**
 * Created by marcoz on 17.08.15.
 */
public class JtsEoProduct extends AbstractEoProduct {

    private static final WKTReader wktReader = new WKTReader();
    private static final GeometryFactory geometryFactory = new GeometryFactory();
    private static final Geometry THE_PRODUCT;
    private static final PreparedGeometry THE_PRODUCT_PREPARED;

    static {
        try {
            THE_PRODUCT = wktReader.read(EoProduct.OVERLAP_WKT);
            THE_PRODUCT_PREPARED = PreparedGeometryFactory.prepare(THE_PRODUCT);
        } catch (com.vividsolutions.jts.io.ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static int topExceptions = 0;

    private final String wkt;
    private Geometry geometry;

    public JtsEoProduct(String name, long startTime, long endTime, String wkt) {
        super(name, startTime, endTime);
        this.wkt = wkt;
    }

    @Override
    public boolean contains(double lon, double lat) {
        Geometry geometry = getGeometry();
        if (geometry != null) {
            return geometry.contains(geometryFactory.createPoint(new Coordinate(lon, lat)));
        }
        return false;
    }

    @Override
    public boolean overlaps() {
        Geometry geometry = getGeometry();
        if (geometry != null) {
            try {
                return !THE_PRODUCT_PREPARED.disjoint(geometry);
            } catch (TopologyException e) {
                topExceptions++;
                return false;
            }
        }
        return false;
    }

    @Override
    public void reset() {
        geometry = null;
        topExceptions = 0;
    }

    private Geometry getGeometry() {
        if (geometry == null) {
            try {
                geometry = wktReader.read(wkt);
            } catch (com.vividsolutions.jts.io.ParseException e) {
                e.printStackTrace();
                return null;
            }
            try {
                int unwrapDateline = DateLineOps.unwrapDateline(geometry, -180, 180);
                if (unwrapDateline > 0) {
                    geometry = DateLineOps.pageGeom(geometry, -180, 180);
                }
            } catch (TopologyException e) {
                topExceptions++;
            }
        }
        return geometry;
    }

    public static EoProduct parse(String line) throws ParseException {
        String[] splits = line.split("\t");
        String name = new File(splits[0]).getName();
        long startTime = AbstractEoProduct.DATE_FORMAT.parse(DateUtils.getNoFractionString(splits[1])).getTime();
        long endTime = AbstractEoProduct.DATE_FORMAT.parse(DateUtils.getNoFractionString(splits[2])).getTime();
        String wkt = splits[3];
        return new JtsEoProduct(name, startTime, endTime, wkt);
    }
}
