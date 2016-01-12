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
import me.lemire.integercompression.IntWrapper;
import me.lemire.integercompression.differential.IntegratedBinaryPacking;
import me.lemire.integercompression.differential.IntegratedComposition;
import me.lemire.integercompression.differential.IntegratedIntegerCODEC;
import me.lemire.integercompression.differential.IntegratedVariableByte;
import org.esa.beam.occci.util.StopWatch;

import java.awt.geom.Point2D;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReverseProductDB {

    private static final long MILLIS_PER_DAY = 1000 * 60 * 60 * 24L;
    private static final Comparator<EoProduct> EO_PRODUCT_COMPARATOR = new Comparator<EoProduct>() {
        @Override
        public int compare(EoProduct eo1, EoProduct eo2) {
            final long t1 = eo1.getStartTime();
            final long t2 = eo2.getStartTime();
            return (t1 < t2 ? -1 : (t1 == t2 ? 0 : 1));
        }
    };
    private final int[][] reverseIndex;
    private final int[] cellIds;
    private final long[] startTimes;
    private final long[] endTimes;

    public ReverseProductDB(long[] startTimes, long[] endTimes, int[][] reverseIndex, int[] cellIds) {

        this.startTimes = startTimes;
        this.endTimes = endTimes;
        this.reverseIndex = reverseIndex;
        this.cellIds = cellIds;
    }

    static ReverseProductDB readProductIndex(File file) throws IOException, ParseException {
        int[][] reverseIndex;
        int[] cellIds;

        File coverFile = new File(file.getAbsolutePath() + ".coverages");
        try (
                StopWatch sw = new StopWatch("R " + coverFile.getName());
                DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(coverFile)))
        ) {

            IntegratedIntegerCODEC codec =  new IntegratedComposition(
                                new IntegratedBinaryPacking(),
                                new IntegratedVariableByte()
                        );

            int numCellIds = dis.readInt();
            reverseIndex = new int[numCellIds][0];
            cellIds = new int[numCellIds];
            for (int i = 0; i < reverseIndex.length; i++) {
                cellIds[i] = dis.readInt();
                int numProducts = dis.readInt();
                int lengthCompressed = dis.readInt();
                int[] compressed = new int[lengthCompressed];
                for (int j = 0; j < compressed.length; j++) {
                    compressed[j] = dis.readInt();
                }
                int[] data = new int[numProducts];
                IntWrapper recoffset = new IntWrapper(0);
                codec.uncompress(compressed, new IntWrapper(0), compressed.length, data, recoffset);
                reverseIndex[i] = data;
            }
        }
        try (
                StopWatch sw = new StopWatch("R " + file.getName());
                DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))
        ) {
            int numProducts = dis.readInt();
            long[] startTimes = new long[numProducts];
            long[] endTimes = new long[numProducts];

            for (int i = 0; i < numProducts; i++) {
                startTimes[i] = dis.readLong();
                endTimes[i] = dis.readLong();
            }
            return new ReverseProductDB(startTimes, endTimes, reverseIndex, cellIds);
        }
    }

    private int indexedBinarySearch(int[] productIndices, long startTime) {
        int low = 0;
        int high = productIndices.length - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            int productIndice = productIndices[mid];
            final long midVal = startTimes[productIndice];
            if (midVal < startTime) {
                low = mid + 1;
            } else if (midVal == startTime) {
                return mid; // key found
            } else {
                high = mid - 1;
            }
        }
        return low;  // key not found
    }

    public List<Integer> findInsitu(int cellInt, long windowStartTime, long windowEndTime) {
        int cellIdIndex = Arrays.binarySearch(cellIds, cellInt);
        if (cellIdIndex < 0) {
            return Collections.EMPTY_LIST;
        }
        int[] products = reverseIndex[cellIdIndex];
        int productIndex = indexedBinarySearch(products, windowStartTime);
        if (productIndex < 0) {
            return Collections.EMPTY_LIST;
        }

        List<Integer> list = new ArrayList<>();
        while (productIndex < products.length) {
            int productID = products[productIndex];
            if (startTimes[productID] > windowEndTime) {
                break;
            } else {
                list.add(productID);
            }
            productIndex++;
        }
        return list;
    }

    public int size() {
        return startTimes.length;
    }

    public static class S2ReverseProduct extends AbstractEoProduct {

        final int productID;

        public S2ReverseProduct(int productID, String name, long startTime, long endTime) {
            super(name, startTime, endTime);
            this.productID = productID;
        }

        @Override
        public boolean contains(double lon, double lat) {
            return false;
        }

        @Override
        public boolean overlaps() {
            return false;
        }

        @Override
        public void reset() {

        }

        @Override
        public void createGeo() {

        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof S2IEoProduct)) return false;

            S2IEoProduct that = (S2IEoProduct) o;
            return productID == that.productID;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(productID);
        }
    }
}
