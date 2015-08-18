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

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by marcoz on 17.08.15.
 */
public class S2IEoProduct extends AbstractEoProduct {

    private static final S2Polygon THE_PRODUCT;
    private static final S2CellUnion THE_PRODUCT_UNION;

    static {
        THE_PRODUCT = S2EoProduct.createPolygon(EoProduct.OVERLAP_WKT);
        S2RegionCoverer coverer = new S2RegionCoverer();
        coverer.setMinLevel(0);
        coverer.setMaxLevel(3);
        coverer.setMaxCells(500);
        THE_PRODUCT_UNION = coverer.getCovering(THE_PRODUCT);
    }

    static int cellCounter = 0;
    static int poylgonCounter = 0;

    private final String poygonString;
    private final String cellUnionString;

    private S2Polygon polygon;
    private S2CellUnion cellUnion;

    public S2IEoProduct(String name, long startTime, long endTime, String poygonString, String cellUnionString) {
        super(name, startTime, endTime);
        this.poygonString = poygonString;
        this.cellUnionString = cellUnionString;
    }

    @Override
    public boolean contains(double lon, double lat) {
        S2Point s2Point = S2LatLng.fromDegrees(lat, lon).toPoint();
        cellCounter++;
        if (getCellUnion().contains(s2Point)) {
            poylgonCounter++;
            return getPolygon().contains(s2Point);
        }
        return false;
    }

    @Override
    public boolean overlaps() {
        cellCounter++;
        if (getCellUnion().intersects(THE_PRODUCT_UNION)) {
            poylgonCounter++;
            return getPolygon().intersects(THE_PRODUCT);
        }
        return false;
    }

    @Override
    public void reset() {
        polygon = null;
        cellUnion = null;
    }

    @Override
    public void createGeo() {
        getPolygon();
        getCellUnion();
    }

    private S2Polygon getPolygon() {
        if (polygon == null) {
            polygon = createS2Polygon(poygonString);
        }
        return polygon;
    }

    private S2CellUnion getCellUnion() {
        if (cellUnion == null) {
            cellUnion = createS2CellUnion(cellUnionString);
        }
        return cellUnion;
    }


    public static EoProduct parse(String line) throws ParseException {
        String[] splits = line.split("\t");
        if (splits.length == 5) {
            String name = new File(splits[0]).getName();
            long startTime = Long.parseLong(splits[1]);
            long endTime = Long.parseLong(splits[2]);
            String loopPointString = splits[3];
            String cellIdString = splits[4];
            return new S2IEoProduct(name, startTime, endTime, loopPointString, cellIdString);
        } else {
            System.out.println(line);
        }
        return null;
    }

    private static S2CellUnion createS2CellUnion(String cellIdString) {
        S2CellUnion cellUnion = new S2CellUnion();
        ArrayList<S2CellId> cellIds = new ArrayList<S2CellId>();
        for (String token : cellIdString.split(";")) {
            cellIds.add(S2CellId.fromToken(token));
        }
        cellUnion.initFromCellIds(cellIds);
        return cellUnion;
    }

    private static S2Polygon createS2Polygon(String loopPoints) {
        String[] points = loopPoints.split(";");
        List<S2Point> vertices = new ArrayList<S2Point>();
        for (String point : points) {
            String substring = point.substring(1, point.length() - 1);
            String[] xyz = substring.split(",");
            S2Point s2Point = new S2Point(Double.parseDouble(xyz[0]), Double.parseDouble(xyz[1]), Double.parseDouble(xyz[2]));
            vertices.add(s2Point);
        }
        S2Loop s2Loop = new S2Loop(vertices);
        return new S2Polygon(s2Loop);
    }
}
