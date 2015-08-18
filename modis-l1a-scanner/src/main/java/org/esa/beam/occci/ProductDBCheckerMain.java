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

import org.esa.beam.occci.insitu.CsvRecordSource;
import org.esa.beam.occci.insitu.Record;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by marcoz on 17.08.15.
 */
public class ProductDBCheckerMain {

    private static final long HOURS_IN_MILLIS = 1000 * 60 * 60; // Note: time in ms (NOT h)


    public static void main(String[] args) throws Exception {
        if (args.length != 4) {
            printUsage();
        }
        File productIndexListFile1 = new File(args[0]);
        if (!productIndexListFile1.exists()) {
            System.err.printf("productList file '%s' does not exits%n", args[0]);
            printUsage();
        }
        File productListFile2 = new File(args[1]);
        if (!productListFile2.exists()) {
            System.err.printf("productList file '%s' does not exits%n", args[1]);
            printUsage();
        }

        File insituCSVtFile = new File(args[2]);
        if (!insituCSVtFile.exists()) {
            System.err.printf("insituList file '%s' does not exits%n", args[2]);
            printUsage();
        }
        int hours = 0;
        try {
            hours = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.err.printf("cannot parse hours '%s' %n", args[3]);
            printUsage();
        }

        List<SimpleRecord> insituRecords = readInsituRecords(insituCSVtFile);
        System.out.println("num insituRecords = " + insituRecords.size());
//        ProductDB jtsProductDB = ProductDB.create(ProductDB.readProducts("jts", productListFile));
//        ProductDB spatial3dProductDB = ProductDB.create(ProductDB.readProducts("spatial3d", productListFile));
//        System.out.println("num jts products =  " + jtsProductDB.size());
//        System.out.println("num spatial3d products =  " + spatial3dProductDB.size());
//        System.out.println("num s2 products =  " + s2ProductDB.size());
//
//        System.out.println();
//        performInsitu("spatial3d", new FastMatcher(spatial3dProductDB), insituRecords, HOURS_IN_MILLIS * hours);
//        resetProductDB(spatial3dProductDB);
//        performOverlap("spatial3d", new FastMatcher(spatial3dProductDB));
//
//        System.out.println();
//        performInsitu("jts", new FastMatcher(jtsProductDB), insituRecords, HOURS_IN_MILLIS * hours);
//        resetProductDB(jtsProductDB);
//        performOverlap("jts", new FastMatcher(jtsProductDB));

        System.out.println();
        ProductDB s2iProductDB = ProductDB.create(ProductDB.readProducts("s2i", productIndexListFile1));
        System.out.println("s2iProductDB.size() = " + s2iProductDB.size());
        performInsitu("s2i", new FastMatcher(s2iProductDB), insituRecords, HOURS_IN_MILLIS * hours);
        performOverlap("s2i", new FastMatcher(s2iProductDB));
        s2iProductDB = null;

        System.out.println();
        ProductDB s2ProductDB = ProductDB.create(ProductDB.readProducts("s2", productListFile2));
        System.out.println("s2ProductDB.size() = " + s2ProductDB.size());
        performInsitu("s2", new FastMatcher(s2ProductDB), insituRecords, HOURS_IN_MILLIS * hours);
        performOverlap("s2", new FastMatcher(s2ProductDB));
        s2ProductDB = null;

        System.out.println();
        System.out.println();
        System.out.println();
        s2iProductDB = ProductDB.create(ProductDB.readProducts("s2i", productIndexListFile1));
        System.out.println("s2iProductDB.size() = " + s2iProductDB.size());
        performInsitu("s2i", new FastMatcher(s2iProductDB), insituRecords, HOURS_IN_MILLIS * hours);
        performOverlap("s2i", new FastMatcher(s2iProductDB));
        s2iProductDB = null;

        System.out.println();
        s2ProductDB = ProductDB.create(ProductDB.readProducts("s2", productListFile2));
        System.out.println("s2ProductDB.size() = " + s2ProductDB.size());
        performInsitu("s2", new FastMatcher(s2ProductDB), insituRecords, HOURS_IN_MILLIS * hours);
        performOverlap("s2", new FastMatcher(s2ProductDB));
        s2ProductDB = null;

    }

    private static void resetProductDB(ProductDB productDB) {
        for (EoProduct eoProduct : productDB.list()) {
            eoProduct.reset();
        }
    }

    private static void createGeometries(ProductDB productDB) {
        long t1 = System.currentTimeMillis();
        for (EoProduct eoProduct : productDB.list()) {
            eoProduct.createGeo();
        }
        long t2 = System.currentTimeMillis();
        System.out.println("delta time createGeometries = " + ((t2 - t1) / 1000f));

    }

    private static void performInsitu(String format, EoProductMatcher matcher, List<SimpleRecord> insituRecords, long maxTimeDifference) {
        System.out.println("==================== insitu " + format + "-" + matcher.getClass().getSimpleName() + " ====================================");

        long t1 = System.currentTimeMillis();
        Set<EoProduct> eoProducts = matcher.matchInsitu(insituRecords, maxTimeDifference);
        long t2 = System.currentTimeMillis();

        System.out.println("num matches =  " + eoProducts.size());
        if (format.equals("s2i")) {
            System.out.println("cellCounter    =  " + S2IEoProduct.cellCounter);
            System.out.println("poylgonCounter =  " + S2IEoProduct.poylgonCounter);
            S2IEoProduct.cellCounter = 0;
            S2IEoProduct.poylgonCounter = 0;
        }
        System.out.println("delta time  = " + ((t2 - t1) / 1000f));
    }

    private static void performOverlap(String format, EoProductMatcher matcher) {
        System.out.println("==================== overlap " + format + "-" + matcher.getClass().getSimpleName() + " ====================================");

        long t1 = System.currentTimeMillis();
        Set<EoProduct> eoProducts = matcher.matchProduct();
        long t2 = System.currentTimeMillis();

        System.out.println("num matches =  " + eoProducts.size());
        if (format.equals("jts")) {
            System.out.println("num TopologyExceptions =  " + JtsEoProduct.topExceptions);
        } else if (format.equals("s2i")) {
            System.out.println("cellCounter    =  " + S2IEoProduct.cellCounter);
            System.out.println("poylgonCounter =  " + S2IEoProduct.poylgonCounter);
            S2IEoProduct.cellCounter = 0;
            S2IEoProduct.poylgonCounter = 0;
        }
        System.out.println("delta time  = " + ((t2 - t1) / 1000f));
    }


    private static void printUsage() {
        System.err.println("Usage: ProductDBCheckerMain <productIndexList> <productList> <insituList> <hours>");
        System.exit(1);
    }

    private static List<SimpleRecord> readInsituRecords(File file) throws Exception {
        FileReader fileReader = new FileReader(file);
        CsvRecordSource recordSource = new CsvRecordSource(fileReader, SimpleRecord.INSITU_DATE_FORMAT);
        List<SimpleRecord> records = new ArrayList<SimpleRecord>();
        for (Record record : recordSource.getRecords()) {
            records.add(new SimpleRecord(record.getTime().getTime(), record.getLocation()));
        }
        fileReader.close();
        return records;
    }

}
