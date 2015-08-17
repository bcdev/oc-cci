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

import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BruteForceMatcher implements EoProductMatcher {

    protected final ProductDB productDB;

    public BruteForceMatcher(ProductDB productDB) {
        this.productDB = productDB;
    }

    @Override
    public Set<EoProduct> matchInsitu(List<SimpleRecord> insituRecords, long maxTimeDifference) {
        Set<EoProduct> matches = new HashSet<EoProduct>();
        List<EoProduct> eoProducts = productDB.list();
        for (SimpleRecord reference : insituRecords) {
            for (EoProduct eoProduct : eoProducts) {
                long referenceTime = reference.getTime();
                if (referenceTime >= eoProduct.getStartTime() - maxTimeDifference &&
                        referenceTime <= eoProduct.getEndTime() + maxTimeDifference) {
                    Point2D.Float location = reference.getLocation();
                    if (location == null) {
                        throw new IllegalArgumentException("record has no geo-location: " + reference);
                    }
                    if (eoProduct.contains(location.getX(), location.getY())) {
                        matches.add(eoProduct);
                        break;
                    }
                }
            }
        }
        return matches;
    }

    @Override
    public Set<EoProduct> matchProduct() {
        Set<EoProduct> matches = new HashSet<EoProduct>();
        for (EoProduct eoProduct : productDB.list()) {
            if (eoProduct.overlaps()) {
                matches.add(eoProduct);
            }
        }
        return matches;
    }
}
