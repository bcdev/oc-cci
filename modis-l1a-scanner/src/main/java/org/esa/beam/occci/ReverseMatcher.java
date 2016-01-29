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
import com.google.common.geometry.S2Point;
import com.google.common.geometry.S2Polygon;
import com.google.common.geometry.S2Region;
import com.google.common.geometry.S2RegionCoverer;
import org.esa.beam.occci.util.StopWatch;

import java.awt.geom.Point2D;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by marco on 22.11.15.
 */
public class ReverseMatcher {

    private static final DateFormat DATE_FORMAT = DateUtils.createDateFormat("yyyy-MM-dd HH:mm:ss");

    private final long globalStartTime;
    private final long globalEndTime;

    private final ReverseProductDB reverseProductDB;
    private final File polygonFile;

    public ReverseMatcher(ReverseProductDB reverseProductDB, File polygonFile) {
        this.reverseProductDB = reverseProductDB;
        this.polygonFile = polygonFile;
        try {
            globalStartTime = DATE_FORMAT.parse("2008-01-01 00:00:00").getTime();
            globalEndTime = DATE_FORMAT.parse("2009-01-01 00:00:00").getTime();
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Set<Integer> matchInsitu(List<SimpleRecord> insituRecords, long maxTimeDifference) {
        Map<Integer, List<S2Point>> candidatesMap = new HashMap<>();

        try (StopWatch sw = new StopWatch("  >>test for time and cell")) {
            for (SimpleRecord insituRecord : insituRecords) {
                final long referenceTime = insituRecord.getTime();
                final long windowStartTime;
                final long windowEndTime;
                if (referenceTime == -1) {
                    windowStartTime = globalStartTime;
                    windowEndTime = globalEndTime;
                } else {
                    windowStartTime = referenceTime - maxTimeDifference;
                    windowEndTime = referenceTime + maxTimeDifference;
                }

                Point2D.Float location = insituRecord.getLocation();
                final double lon = location.getX();
                final double lat = location.getY();
                S2LatLng s2LatLng = S2LatLng.fromDegrees(lat, lon);
                S2Point s2Point = s2LatLng.toPoint();
                S2CellId s2CellId = S2CellId.fromPoint(s2Point);
                final int cellInt = S2CellIdInteger.asInt(s2CellId.parent(3));

                List<Integer> productIndices = reverseProductDB.findInsitu(cellInt, windowStartTime, windowEndTime);
                for (Integer productIndex : productIndices) {
                    List<S2Point> candidateProducts = candidatesMap.get(productIndex);
                    if (candidateProducts == null) {
                        candidateProducts = new ArrayList<>();
                        candidatesMap.put(productIndex, candidateProducts);
                    }
                    candidateProducts.add(s2Point);
                }
            }
            System.out.println("candidatesMap = " + candidatesMap.size());
        }

        List<Integer> uniqueProductList = new ArrayList<>(candidatesMap.size());
        uniqueProductList.addAll(candidatesMap.keySet());

        Set<Integer> matches = new HashSet<>();
        try (StopWatch sw = new StopWatch("  >>load and test polygons")) {
            Collections.sort(uniqueProductList);
            try (
                    DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(polygonFile)))
            ) {
                int streamPID = 0;
                for (Integer productID : uniqueProductList) {
                    while (streamPID < productID) {
                        int numLoopPoints = dis.readInt();
                        dis.skipBytes(numLoopPoints * 3 * 8);
                        streamPID++;
                    }
                    final int numLoopPoints = dis.readInt();
                    final double[] pointData = new double[numLoopPoints * 3];
                    for (int i = 0; i < pointData.length; i++) {
                        pointData[i] = dis.readDouble();
                    }
                    streamPID++;

                    S2Polygon s2Polygon = S2IEoProduct.createS2Polygon(pointData);
                    List<S2Point> s2Points = candidatesMap.get(productID);
                    for (S2Point s2Point : s2Points) {
                        if (s2Polygon.contains(s2Point)) {
                            matches.add(productID);
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return matches;
    }

    public Set<Integer> matchProduct(String testWKT, long windowStartTime, long windowEndTime) {
        S2Region testRegion = new S2WKTReader().read(testWKT);
        S2Polygon testPoly = (S2Polygon) testRegion;
        S2RegionCoverer coverer = new S2RegionCoverer();
        coverer.setMinLevel(3);
        coverer.setMaxLevel(3);
        coverer.setMaxCells(500);
        S2CellUnion covering = coverer.getCovering(testRegion);
        S2ReverseIndexCreatorMain.S2IntCoverage s2IntCoverage = new S2ReverseIndexCreatorMain.S2IntCoverage(covering);

        Set<Integer> candidatesSet = new HashSet<>();
        for (int cellId : s2IntCoverage.intIds) {
            candidatesSet.addAll(reverseProductDB.findInsitu(cellId, windowStartTime, windowEndTime));
        }

        System.out.println("candidatesSet = " + candidatesSet.size());
        List<Integer> uniqueProductList = new ArrayList<>(candidatesSet);

        Set<Integer> matches = new HashSet<>();
        try (StopWatch sw = new StopWatch("  >>load and test polygons")) {
            Collections.sort(uniqueProductList);
            try (
                    DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(polygonFile)))
            ) {
                int streamPID = 0;
                for (Integer productID : uniqueProductList) {
                    while (streamPID < productID) {
                        int numLoopPoints = dis.readInt();
                        dis.skipBytes(numLoopPoints * 3 * 8);
                        streamPID++;
                    }
                    final int numLoopPoints = dis.readInt();
                    final double[] pointData = new double[numLoopPoints * 3];
                    for (int i = 0; i < pointData.length; i++) {
                        pointData[i] = dis.readDouble();
                    }
                    streamPID++;

                    S2Polygon s2Polygon = S2IEoProduct.createS2Polygon(pointData);
                    if (testPoly.intersects(s2Polygon)) {
                        matches.add(productID);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return matches;
    }
}
