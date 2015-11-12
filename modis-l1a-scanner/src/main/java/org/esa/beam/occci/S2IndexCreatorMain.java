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

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by marcoz on 18.08.15.
 */
public class S2IndexCreatorMain {

    static final short MASK_SHIFT = 2 * S2CellId.MAX_LEVEL - 1;

    public static void main(String[] args) throws IOException, ParseException {
        if (args.length != 2) {
            printUsage();
        }
        File productListFile = new File(args[0]);

        if (!productListFile.exists()) {
            System.err.printf("productList file '%s' does not exits%n", args[0]);
            printUsage();
        }
        int counter = 0;
        List<EoProduct> eoProductList = ProductDB.readProducts("s2", productListFile);

        File indexFile = new File(args[1]);
        File urlFile = new File(indexFile + ".url");
        File poylFile = new File(indexFile + ".polygon");
        File coverFile = new File(indexFile + ".coverages");

        List<S2IntCoverage> allCoverages = new ArrayList<>();
        try (
                DataOutputStream dosIndex = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(indexFile)));
                DataOutputStream dosUrl = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(urlFile)));
                DataOutputStream dosPoly = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(poylFile)))
        ) {
            for (EoProduct eoProduct : eoProductList) {
                eoProduct.createGeo();
                S2EoProduct s2EoProduct = (S2EoProduct) eoProduct;

                dosUrl.writeUTF(eoProduct.getName());

                dosIndex.writeLong(eoProduct.getStartTime());
                dosIndex.writeLong(eoProduct.getEndTime());

                S2CellUnion cellUnion = s2EoProduct.cellUnion;
                S2IntCoverage s2IntCoverage = new S2IntCoverage(cellUnion);
                int index = allCoverages.indexOf(s2IntCoverage);
                if (index <= 0) {
                    allCoverages.add(s2IntCoverage);
                    index = allCoverages.size() - 1;
                }
                dosIndex.writeInt(index);

                int level1Mask = 0;
                for (int i = 0; i < cellUnion.cellIds().size(); i++) {
                    S2CellId s2CellId = cellUnion.cellIds().get(i);
                    level1Mask |= (1 << (int) (s2CellId.id() >>> MASK_SHIFT));
                }
                dosIndex.writeInt(level1Mask);

                s2EoProduct.writePolygone(dosPoly);

                s2EoProduct.reset();

                counter++;
                if (counter % 10000 == 0) {
                    System.out.println("counter = " + counter);
                }
            }
        }
        System.out.println("allCoverages.size() = " + allCoverages.size());
        try (
                DataOutputStream dosCover = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(coverFile)))
        ) {
            dosCover.writeInt(allCoverages.size());
            for (S2IntCoverage s2Cover : allCoverages) {
                int[] intIds = s2Cover.intIds;
                dosCover.writeInt(intIds.length);
                for (int intId : intIds) {
                    dosCover.writeInt(intId);
                }
            }
        }
    }

    private static class S2IntCoverage {
        private final int[] intIds;

        public S2IntCoverage(S2CellUnion cellUnion) {
            ArrayList<S2CellId> s2CellIds = cellUnion.cellIds();
            intIds = new int[s2CellIds.size()];
            for (int i = 0; i < intIds.length; i++) {
                intIds[i] = S2CellIdInteger.asInt(s2CellIds.get(i));
            }
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof S2IntCoverage)) return false;
            return Arrays.equals(intIds, ((S2IntCoverage) other).intIds);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(intIds);
        }
    }


    private static void printUsage() {
        System.err.println("Usage: S2IndexCreatorMain <productList> <indexfile>");
        System.exit(1);
    }

}
