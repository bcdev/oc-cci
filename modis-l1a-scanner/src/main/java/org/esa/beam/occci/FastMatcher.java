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

import com.google.common.geometry.S2CellId;

import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FastMatcher extends BruteForceMatcher {

    public FastMatcher(ProductDB productDB) {
        super(productDB);
    }

    @Override
    public Set<EoProduct> matchInsitu(List<SimpleRecord> insituRecords, long maxTimeDifference) {
        Set<EoProduct> matches = new HashSet<EoProduct>();
        for (SimpleRecord insituRecord : insituRecords) {
            final long referenceTime = insituRecord.getTime();
            final long windowStartTime = referenceTime - maxTimeDifference;
            final long windowEndTime = referenceTime + maxTimeDifference;

            int productIndex = productDB.getIndexForTime(windowStartTime);
            if (productIndex == -1) {
                continue;
            }

            boolean finishedWithInsitu = false;
            while (!finishedWithInsitu) {
                EoProduct eoProduct = productDB.getRecord(productIndex);
                productIndex++;

                if (eoProduct == null) {
                    finishedWithInsitu = true;
                } else if (eoProduct.getEndTime() > windowEndTime) {
                    finishedWithInsitu = true;
                } else if (eoProduct.getStartTime() < windowStartTime) {
                    //test next product;
                } else {
                    Point2D.Float location = insituRecord.getLocation();
                    if (location == null) {
                        throw new IllegalArgumentException("record has no geo-location: " + insituRecord);
                    }
                    if (eoProduct.contains(location.getX(), location.getY())) {
                        matches.add(eoProduct);
                    }
                }
            }
        }
        return matches;
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
