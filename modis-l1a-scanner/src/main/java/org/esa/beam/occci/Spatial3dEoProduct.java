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
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;
import org.apache.lucene.geo3d.Bounds;
import org.apache.lucene.geo3d.GeoArea;
import org.apache.lucene.geo3d.GeoAreaFactory;
import org.apache.lucene.geo3d.GeoMembershipShape;
import org.apache.lucene.geo3d.GeoPoint;
import org.apache.lucene.geo3d.GeoPolygonFactory;
import org.apache.lucene.geo3d.PlanetModel;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by marcoz on 17.08.15.
 */
public class Spatial3dEoProduct extends AbstractEoProduct {

    private static final WKTReader wktReader = new WKTReader();
    private static final PlanetModel planetModel = PlanetModel.WGS84;
    private static final GeoArea THE_PRODUCT;

    static {
        try {
            GeoMembershipShape geoMembershipShape = createGeoMembershipShape(EoProduct.OVERLAP_WKT);
            Bounds bounds = geoMembershipShape.getBounds(null);
            THE_PRODUCT = GeoAreaFactory.makeGeoArea(planetModel, bounds.getMaxLatitude(), bounds.getMinLatitude(), bounds.getLeftLongitude(), bounds.getRightLongitude());
        } catch (com.vividsolutions.jts.io.ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private final String wkt;
    private GeoMembershipShape geoMembershipShape;

    public Spatial3dEoProduct(String name, long startTime, long endTime, String wkt) {
        super(name, startTime, endTime);
        this.wkt = wkt;
    }

    @Override
    public boolean contains(double lon, double lat) {
        GeoMembershipShape geoMembershipShape = getGeometry();
        if (geoMembershipShape != null) {
            GeoPoint geoPoint = new GeoPoint(PlanetModel.WGS84, Math.toRadians(lat), Math.toRadians(lon));
            return geoMembershipShape.isWithin(geoPoint);
        }
        return false;
    }

    @Override
    public boolean overlaps() {
        GeoMembershipShape geoMembershipShape = getGeometry();
        if (geoMembershipShape != null) {
            return GeoArea.DISJOINT != THE_PRODUCT.getRelationship(geoMembershipShape);
        }
        return false;
    }

    @Override
    public void reset() {
        geoMembershipShape = null;
    }

    private GeoMembershipShape getGeometry() {
        if (geoMembershipShape == null) {
            try {
                geoMembershipShape = createGeoMembershipShape(wkt);
            } catch (com.vividsolutions.jts.io.ParseException e) {
                e.printStackTrace();
                return null;
            }
        }
        return geoMembershipShape;
    }

    public static GeoMembershipShape createGeoMembershipShape(String wkt) throws com.vividsolutions.jts.io.ParseException {
        Geometry geo = wktReader.read(wkt);
        List<GeoPoint> points = new ArrayList<GeoPoint>();
        Polygon polygon = (Polygon) geo;
        Coordinate[] coordinates = polygon.getCoordinates();
        for (Coordinate coordinate : coordinates) {
            double x = coordinate.getOrdinate(0);
            double y = coordinate.getOrdinate(1);
            points.add(new GeoPoint(planetModel, Math.toRadians(y), Math.toRadians(x)));
        }
        points.remove(points.size() - 1);
        return GeoPolygonFactory.makeGeoPolygon(planetModel, points, 0);
    }

    public static EoProduct parse(String line) throws ParseException {
        String[] splits = line.split("\t");
        String name = new File(splits[0]).getName();
        long startTime = AbstractEoProduct.DATE_FORMAT.parse(DateUtils.getNoFractionString(splits[1])).getTime();
        long endTime = AbstractEoProduct.DATE_FORMAT.parse(DateUtils.getNoFractionString(splits[2])).getTime();
        String wkt = splits[3];
        return new Spatial3dEoProduct(name, startTime, endTime, wkt);
    }
}
