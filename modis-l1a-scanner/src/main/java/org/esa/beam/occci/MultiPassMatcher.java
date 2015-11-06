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
import com.google.common.geometry.S2LatLng;
import com.google.common.geometry.S2Point;
import com.google.common.geometry.S2Polygon;
import org.esa.beam.occci.util.StopWatch;

import java.awt.geom.Point2D;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MultiPassMatcher extends BruteForceMatcher {

    private final File polygonFile;

    public MultiPassMatcher(ProductDB productDB, File polygonFile) {
        super(productDB);
        this.polygonFile = polygonFile;
    }

    @Override
    public Set<EoProduct> matchInsitu(List<SimpleRecord> insituRecords, long maxTimeDifference) {
        Map<S2IEoProduct, List<S2Point>> candidatesMap = new HashMap<>();

        long globalStartTime;
        long globalEndTime;
        try {
            globalStartTime = AbstractEoProduct.DATE_FORMAT.parse(DateUtils.getNoFractionString("2014-01-01T00:00:00")).getTime();
            globalEndTime = AbstractEoProduct.DATE_FORMAT.parse(DateUtils.getNoFractionString("2015-01-01T00:00:00")).getTime();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        try (StopWatch sw = new StopWatch("  >>test for time and cell")) {
            for (SimpleRecord insituRecord : insituRecords) {
                S2CellId s2CellId = null;
                S2Point s2Point = null;
                int level1Mask = 0;
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

                int productIndex = productDB.getIndexForTime(windowStartTime);
                if (productIndex == -1) {
                    continue;
                }

                boolean finishedWithInsitu = false;
                while (!finishedWithInsitu) {
                    S2IEoProduct eoProduct = (S2IEoProduct) productDB.getRecord(productIndex);
                    productIndex++;

                    if (eoProduct == null) {
                        finishedWithInsitu = true;
                    } else if (eoProduct.getStartTime() > windowEndTime) {
                        finishedWithInsitu = true;
                    } else if (eoProduct.getEndTime() < windowStartTime) {
                        //test next product;
                    } else {
                        // time match
                        if (s2CellId == null) {
                            Point2D.Float location = insituRecord.getLocation();
                            double lon = location.getX();
                            double lat = location.getY();
                            S2LatLng s2LatLng = S2LatLng.fromDegrees(lat, lon);
                            s2Point = s2LatLng.toPoint();
                            s2CellId = S2CellId.fromPoint(s2Point);
                            level1Mask = (1 << (int) (s2CellId.id() >>> S2IndexCreatorMain.MASK_SHIFT));
                        }
                        if ((eoProduct.level1Mask & level1Mask) != 0) {
                            if (S2CellIdInteger.containsPoint(eoProduct.cellIds, s2CellId)) {
                                List<S2Point> candidateProducts = candidatesMap.get(eoProduct);
                                if (candidateProducts == null) {
                                    candidateProducts = new ArrayList<>();
                                    candidatesMap.put(eoProduct, candidateProducts);
                                }
                                candidateProducts.add(s2Point);
                            }
                        }
                    }
                }
            }
            System.out.println("candidatesMap = " + candidatesMap.size());
        }

        List<S2IEoProduct> uniqueProductList = new ArrayList<>(candidatesMap.size());
        uniqueProductList.addAll(candidatesMap.keySet());
        Set<EoProduct> matches = new HashSet<>();
        try (StopWatch sw = new StopWatch("  >>load and test polygons")) {
            Collections.sort(uniqueProductList, (o1, o2) -> Integer.compare(o1.productID, o2.productID));
            try (
                    DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(polygonFile)))
            ) {
                int streamPID = 0;
                for (S2IEoProduct eoProduct : uniqueProductList) {
                    final int productID = eoProduct.productID;
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
                    List<S2Point> s2Points = candidatesMap.get(eoProduct);
                    for (S2Point s2Point : s2Points) {
                        if (s2Polygon.contains(s2Point)) {
                            matches.add(eoProduct);
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

    private static strictfp boolean containsPoint(final S2CellId[] cellIds, final S2CellId id) {
        int pos = Arrays.binarySearch(cellIds, id);
        if (pos < 0) {
            pos = -pos - 1;
        }

        return pos < cellIds.length && cellIds[pos].rangeMin().lessOrEquals(id) ? true : pos != 0 && cellIds[pos - 1].rangeMax().greaterOrEquals(id);
    }

    @Override
    public Set<EoProduct> matchProduct() {
        Map<S2CellId, List<EoProduct>> productCellMap = productDB.getProductCellMap();
        if (productCellMap == null) {
            return super.matchProduct();
        } else {
            Set<EoProduct> candidates = new HashSet<EoProduct>();
            for (Map.Entry<S2CellId, List<EoProduct>> entry : productCellMap.entrySet()) {
                S2CellId s2CellId = entry.getKey();
                if (S2IEoProduct.THE_PRODUCT_UNION.intersects(s2CellId)) {
                    candidates.addAll(entry.getValue());
                }
            }
            Set<EoProduct> matches = new HashSet<EoProduct>();
            for (EoProduct candidate : candidates) {
                if (candidate.overlaps()) {
                    matches.add(candidate);
                }
            }
            return matches;
        }
    }
}
