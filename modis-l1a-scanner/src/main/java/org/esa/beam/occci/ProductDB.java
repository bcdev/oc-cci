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
import org.esa.beam.occci.util.StopWatch;

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

public class ProductDB {

    private static final long MILLIS_PER_DAY = 1000 * 60 * 60 * 24L;
    private static final Comparator<EoProduct> EO_PRODUCT_COMPARATOR = new Comparator<EoProduct>() {
        @Override
        public int compare(EoProduct eo1, EoProduct eo2) {
            final long t1 = eo1.getStartTime();
            final long t2 = eo2.getStartTime();
            return (t1 < t2 ? -1 : (t1 == t2 ? 0 : 1));
        }
    };

    private final List<EoProduct> eoProducts;
    private final int[] dayIndex;
    private final int firstDay;
    private final int lastDay;
    private Map<S2CellId, List<EoProduct>> productCellMap;

    public ProductDB(List<EoProduct> eoProducts, int[] dayIndex, int firstDay, int lastDay) {
        this.eoProducts = eoProducts;
        this.dayIndex = dayIndex;
        this.firstDay = firstDay;
        this.lastDay = lastDay;
    }

    static List<EoProduct> readProducts(String format, File file) throws IOException, java.text.ParseException {
        long t1 = System.currentTimeMillis();
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = bufferedReader.readLine();
        List<EoProduct> eoProducts = new ArrayList<>();
        while (line != null) {
            EoProduct eoProduct = createEoProduct(format, line);
            if (eoProduct != null) {
                eoProducts.add(eoProduct);
            }
            line = bufferedReader.readLine();
        }
        long t2 = System.currentTimeMillis();
        System.out.println("read products time (" + format + ") = " + ((t2 - t1) / 1000f));
        return eoProducts;
    }

    static ProductDB readProductIndex(File file) throws IOException, java.text.ParseException {
        List<EoProduct> eoProducts = new ArrayList<>(10000);
        Map<S2CellId, List<EoProduct>> productCellMap = new HashMap<>();

        int[][] allCoverages;
        File coverFile = new File(file.getAbsolutePath() + ".coverages");
        try (
                StopWatch sw = new StopWatch("R " + coverFile.getName());
                DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(coverFile)))
        ) {
            int numCovers = dis.readInt();
            allCoverages = new int[numCovers][0];
            for (int i = 0; i < allCoverages.length; i++) {
                int numCells = dis.readInt();
                allCoverages[i] = new int[numCells];
                for (int j = 0; j < allCoverages[i].length; j++) {
                    allCoverages[i][j] = dis.readInt();
                }
            }
        }
        try (
                StopWatch sw = new StopWatch("R " + file.getName());
                DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))
        ) {
            final String name = null;
            boolean done = false;
            int productID = 0;
            while (!done) {
                try {
                    long startTime = dis.readLong();
                    long endTime = dis.readLong();
                    int allCoveragesIndex = dis.readInt();

                    int[] cellIds = allCoverages[allCoveragesIndex];
                    S2IEoProduct product = new S2IEoProduct(productID++, name, startTime, endTime, cellIds);
                    product.level1Mask = dis.readInt();

                    eoProducts.add(product);
                } catch (EOFException eof) {
                    done = true;
                }
            }
        }
        try (StopWatch sw = new StopWatch("prepare product index (s2 index)")){
            ProductDB productDB = create(eoProducts);
            productDB.setProductCellMap(productCellMap);
            return productDB;
        }
    }

    private static EoProduct createEoProduct(String format, String line) throws ParseException {
        if ("jts".equals(format)) {
            return JtsEoProduct.parse(line);
        } else if ("spatial3d".equals(format)) {
            return Spatial3dEoProduct.parse(line);
        } else if ("s2".equals(format)) {
            return S2EoProduct.parse(line);
        } else if ("s2i".equals(format)) {
            return S2IEoProduct.parse(line);
        }
        throw new IllegalArgumentException("Unknow format " + format);
    }

    static ProductDB create(List<EoProduct> eoProducts) {
        Collections.sort(eoProducts, EO_PRODUCT_COMPARATOR);

//        EoProduct eoFirst = eoProducts.get(0);
//        EoProduct eoLast = eoProducts.get(eoProducts.size() - 1);
//
//        int firstDay = getDaySinceEpoch(eoFirst.getStartTime());
//        int lastDay = getDaySinceEpoch(eoLast.getEndTime());
//        int indexLength = lastDay - firstDay + 1;
//        int[] dayIndex = new int[indexLength];
//        Arrays.fill(dayIndex, -1);
//        for (int i = 0; i < eoProducts.size(); i++) {
//            EoProduct eoProduct = eoProducts.get(i);
//
//            int daysSinceEpoch = getDaySinceEpoch(eoProduct.getStartTime());
//            int indexID = daysSinceEpoch - firstDay;
//            if (dayIndex[indexID] == -1) {
//                dayIndex[indexID] = i;
//            }
//        }
//        for (int i = 0; i < dayIndex.length; i++) {
//            if (dayIndex[i] == -1) {
//                int j = i+1;
//                boolean found = false;
//                while (j < dayIndex.length && ! found) {
//                    if (dayIndex[j] != -1) {
//                        found = true;
//                        dayIndex[i] = dayIndex[j];
//                    }
//                    j++;
//                }
//            }
//        }

//        return new ProductDB(eoProducts, dayIndex, firstDay, lastDay);
        return new ProductDB(eoProducts, null, 0, 0);
    }

    public static int getDaySinceEpoch(long time) {
        return (int) (time / MILLIS_PER_DAY);
    }

    public int getIndexForTime(long startTime) {
//        int daysSinceEpoch = getDaySinceEpoch(startTime);
//        if (daysSinceEpoch < firstDay || daysSinceEpoch > lastDay) {
//            return -1;
//        }
//        int day = daysSinceEpoch - firstDay;
//        int low = dayIndex[day];
//        int high;
//        if (day + 1 >= dayIndex.length) {
//            high = eoProducts.size() -1;
//        } else {
//            high = dayIndex[day + 1];
//        }
//        return indexedBinarySearch(eoProducts, startTime, low, high);
        int low = 0;
        int high = eoProducts.size() -1;
        return indexedBinarySearch(eoProducts, startTime, low, high);
    }

    private static int indexedBinarySearch(List<EoProduct> list, long startTime, int low, int high) {
        while (low <= high) {
            int mid = (low + high) >>> 1;
            final long t1 = list.get(mid).getStartTime();
            if (t1 < startTime) {
                low = mid + 1;
            } else if (t1 == startTime) {
                return mid; // key found
            } else {
                high = mid - 1;
            }
        }
        return low;  // key not found
    }

    public EoProduct getRecord(int index) {
        if (index < eoProducts.size()) {
            return eoProducts.get(index);
        } else {
            return null;
        }
    }

    public int size() {
        return eoProducts.size();
    }

    public List<EoProduct> list() {
        return eoProducts;
    }

    public void setProductCellMap(Map<S2CellId, List<EoProduct>> productCellMap) {
        this.productCellMap = productCellMap;
    }

    public Map<S2CellId, List<EoProduct>> getProductCellMap() {
        return productCellMap;
    }
}
