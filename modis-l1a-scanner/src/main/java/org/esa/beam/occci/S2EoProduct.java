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

import com.google.common.geometry.S2CellId;
import com.google.common.geometry.S2CellUnion;
import com.google.common.geometry.S2LatLng;
import com.google.common.geometry.S2Loop;
import com.google.common.geometry.S2Point;
import com.google.common.geometry.S2Polygon;
import com.google.common.geometry.S2RegionCoverer;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by marcoz on 17.08.15.
 */
public class S2EoProduct extends AbstractEoProduct {

    private static final WKTReader wktReader = new WKTReader();
    private static final S2Polygon THE_PRODUCT;
//    private static final S2CellUnion THE_PRODUCT_UNION;

    static {
        THE_PRODUCT = createPolygon(EoProduct.OVERLAP_WKT);
//        S2RegionCoverer coverer = new S2RegionCoverer();
//        coverer.setMinLevel(0);
//        coverer.setMaxLevel(3);
//        coverer.setMaxCells(500);
//        THE_PRODUCT_UNION = coverer.getCovering(THE_PRODUCT);
    }

    private final String wkt;
    private S2Polygon poygon;
    private S2CellUnion cellUnion;

    public S2EoProduct(String name, long startTime, long endTime, String wkt) {
        super(name, startTime, endTime);
        this.wkt = wkt;
    }

    @Override
    public boolean contains(double lon, double lat) {
        S2Polygon poly = getPolygon();
        if (poly != null) {
            S2Point s2Point = S2LatLng.fromDegrees(lat, lon).toPoint();
            return poly.contains(s2Point);
        }
        return false;
    }

    @Override
    public boolean overlaps() {
        S2Polygon poly = getPolygon();
        if (poly != null) {
            return poly.intersects(THE_PRODUCT);
        }
        return false;
    }

    @Override
    public void reset() {
        poygon = null;
    }

    @Override
    public void createGeo() {
        getPolygon();
        S2RegionCoverer coverer = new S2RegionCoverer();
        coverer.setMinLevel(0);
        coverer.setMaxLevel(3);
        coverer.setMaxCells(500);
        cellUnion = coverer.getCovering(poygon);
    }


    private S2Polygon getPolygon() {
        if (poygon == null) {
            poygon = createPolygon(wkt);
        }
        return poygon;
    }

    public static S2Polygon createPolygon(String wkt) {
        Polygon polygon;
        try {
            polygon = (Polygon) wktReader.read(wkt);
        } catch (com.vividsolutions.jts.io.ParseException e) {
            e.printStackTrace();
            return null;
        }

        List<S2Point> vertices = new ArrayList<S2Point>();
        for (Coordinate coordinate : polygon.getCoordinates()) {
            double x = coordinate.getOrdinate(0);
            double y = coordinate.getOrdinate(1);
            vertices.add(S2LatLng.fromDegrees(y, x).toPoint());
        }
        vertices.remove(vertices.size() - 1);
        S2Loop loop = new S2Loop(vertices);
        loop.normalize();
        return new S2Polygon(loop);
    }


    public static EoProduct parse(String line) throws ParseException {
        String[] splits = line.split("\t");
        String name = new File(splits[0]).getName();
        long startTime = AbstractEoProduct.DATE_FORMAT.parse(DateUtils.getNoFractionString(splits[1])).getTime();
        long endTime = AbstractEoProduct.DATE_FORMAT.parse(DateUtils.getNoFractionString(splits[2])).getTime();
        String wkt = splits[3];
        return new S2EoProduct(name, startTime, endTime, wkt);
    }

    public String getIndexString() {
        String points = singleLoopToString(poygon);
        String cells = cellUnionToString(cellUnion);
        return getName() + "\t" + getStartTime() + "\t" + getEndTime() + "\t" + points + "\t" + cells + "\n";

    }

    private static String cellUnionToString(S2CellUnion cellUnion) {
        ArrayList<S2CellId> s2CellIds = cellUnion.cellIds();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s2CellIds.size(); i++) {
            S2CellId s2CellId = s2CellIds.get(i);
            sb.append(s2CellId.toToken());
            if (i < s2CellIds.size() - 1) {
                sb.append(";");
            }
        }
        return sb.toString();
    }

    private static String singleLoopToString(S2Polygon s2polygon) {
        S2Loop loop = s2polygon.loop(0);
        int numVertices = loop.numVertices();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numVertices; i++) {
            sb.append(loop.vertex(i).toString());
            if (i < numVertices - 1) {
                sb.append(";");
            }
        }
        return sb.toString();
    }
}
