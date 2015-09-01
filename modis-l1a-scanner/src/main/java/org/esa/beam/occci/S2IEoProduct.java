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
import java.util.StringTokenizer;

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

    private final double[] poygonData;
    private final long[] cellUnionData;

    private S2Polygon polygon;
    private S2CellUnion cellUnion;

    public S2IEoProduct(String name, long startTime, long endTime, double[] poygonData, long[] cellUnionData) {
        super(name, startTime, endTime);
        this.poygonData = poygonData;
        this.cellUnionData = cellUnionData;
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
            polygon = createS2Polygon(poygonData);
        }
        return polygon;
    }

    private S2Polygon createS2Polygon(double[] poygonData) {
        List<S2Point> vertices = new ArrayList<S2Point>(poygonData.length / 3);
        for (int i = 0; i < poygonData.length; ) {
            double x = poygonData[i++];
            double y = poygonData[i++];
            double z = poygonData[i++];
            S2Point s2Point = new S2Point(x, y, z);
            vertices.add(s2Point);
        }
        return new S2Polygon(new S2Loop(vertices));
    }

    private S2CellUnion getCellUnion() {
        if (cellUnion == null) {
            cellUnion = createS2CellUnion(cellUnionData);
        }
        return cellUnion;
    }

    private S2CellUnion createS2CellUnion(long[] cellUnionData) {
        ArrayList<S2CellId> cellIds = new ArrayList<S2CellId>(cellUnionData.length);
        for (long aCellUnionData : cellUnionData) {
            cellIds.add(new S2CellId(aCellUnionData));
        }
        S2CellUnion cellUnion = new S2CellUnion();
        cellUnion.initRawCellIds(cellIds);
        return cellUnion;
    }


    public static EoProduct parse(String line) throws ParseException {
        StringTokenizer st = new StringTokenizer(line, "\t");
        String name = new File(st.nextToken()).getName();
        long startTime = Long.parseLong(st.nextToken());
        long endTime = Long.parseLong(st.nextToken());
        String loopPointString = st.nextToken();
        String cellIdString = st.nextToken();
        return null;//new S2IEoProduct(name, startTime, endTime, loopPointString, cellIdString);
    }

    private static S2CellUnion createS2CellUnion(String cellIdString) {
        StringTokenizer st = new StringTokenizer(cellIdString, ";");
        ArrayList<S2CellId> cellIds = new ArrayList<S2CellId>();
        while (st.hasMoreTokens()) {
            cellIds.add(new S2CellId(Long.parseLong(st.nextToken())));
        }
        S2CellUnion cellUnion = new S2CellUnion();
        cellUnion.initRawCellIds(cellIds);
        return cellUnion;
    }

    private static S2Polygon createS2Polygon(String loopPoints) {
        StringTokenizer stPoints = new StringTokenizer(loopPoints, ";");
        List<S2Point> vertices = new ArrayList<S2Point>();
        while (stPoints.hasMoreTokens()) {
            String point = stPoints.nextToken();
            String substring = point.substring(1, point.length() - 1);
            StringTokenizer stXYZ = new StringTokenizer(substring, ",");
            S2Point s2Point = new S2Point(Double.parseDouble(stXYZ.nextToken()),
                                          Double.parseDouble(stXYZ.nextToken()),
                                          Double.parseDouble(stXYZ.nextToken()));
            vertices.add(s2Point);
        }
        S2Loop s2Loop = new S2Loop(vertices);
        return new S2Polygon(s2Loop);
    }
}
