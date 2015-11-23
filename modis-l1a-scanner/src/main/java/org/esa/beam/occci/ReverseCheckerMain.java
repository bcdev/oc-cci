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
import org.esa.beam.occci.util.StopWatch;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by marcoz on 17.08.15.
 */
public class ReverseCheckerMain {

    private static final long HOURS_IN_MILLIS = 1000 * 60 * 60; // Note: time in ms (NOT h)


    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            printUsage();
        }
        File productIndexListFile1 = new File(args[0]);
        if (!productIndexListFile1.exists()) {
            System.err.printf("productList file '%s' does not exits%n", args[0]);
            printUsage();
        }

        File insituCSVtFile = new File(args[1]);
        if (!insituCSVtFile.exists()) {
            System.err.printf("insituList file '%s' does not exits%n", args[1]);
            printUsage();
        }
        int hours = 0;
        try {
            hours = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.err.printf("cannot parse hours '%s' %n", args[2]);
            printUsage();
        }

        try (StopWatch sw = new StopWatch("TTT ++++TOTAL TIME+++")) {
            List<SimpleRecord> insituRecords;
            try (StopWatch swi = new StopWatch("TTT read insitu")) {
                insituRecords = readInsituRecords(insituCSVtFile);
                System.out.println("num insituRecords = " + insituRecords.size());
            }
            System.out.println();

            ReverseProductDB reverseProductDB;
            try (StopWatch swp = new StopWatch("TTT read product index")) {
                reverseProductDB = ReverseProductDB.readProductIndex(productIndexListFile1);
                System.out.println("reverseProductDB.size() = " + reverseProductDB.size());
            }
            try (StopWatch swm = new StopWatch("TTT matching")) {
                ReverseMatcher reverseMatcher = new ReverseMatcher(reverseProductDB, new File(args[0] + ".polygon"));
                Set<Integer> eoProducts = performInsitu("s2i", reverseMatcher, insituRecords, HOURS_IN_MILLIS * hours);
//                printURLs(eoProducts, new File(productIndexListFile1.getAbsolutePath() + ".url"));
            }
        }
    }

    private static Set<Integer> performInsitu(String format, ReverseMatcher matcher, List<SimpleRecord> insituRecords, long maxTimeDifference) {
        System.out.println("==================== insitu " + format + "-" + matcher.getClass().getSimpleName() + " ====================================");

        long t1 = System.currentTimeMillis();
        Set<Integer> eoProducts = matcher.matchInsitu(insituRecords, maxTimeDifference);
        long t2 = System.currentTimeMillis();

        System.out.println("num matches =  " + eoProducts.size());
//        if (format.equals("s2i")) {
//            System.out.println("cellCounter    =  " + S2IEoProduct.cellCounter);
//            System.out.println("poylgonCounter =  " + S2IEoProduct.poylgonCounter);
//            S2IEoProduct.cellCounter = 0;
//            S2IEoProduct.poylgonCounter = 0;
//        }
        System.out.println("delta time  = " + ((t2 - t1) / 1000f));

        return eoProducts;
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

    static List<SimpleRecord> readInsituRecords(File file) throws Exception {
        try (Reader reader = new LineNumberReader(new FileReader(file), 100 * 1024)) {
            CsvRecordSource recordSource = new CsvRecordSource(reader, SimpleRecord.INSITU_DATE_FORMAT);
            boolean hasTime = recordSource.getHeader().hasTime();
            List<SimpleRecord> records = new ArrayList<>();
            for (Record record : recordSource.getRecords()) {
                if (hasTime) {
                    records.add(new SimpleRecord(record.getTime().getTime(), record.getLocation()));
                } else {
                    records.add(new SimpleRecord(-1, record.getLocation()));
                }
            }
            return records;
        }
    }

    static void printURLs(Collection<EoProduct> eoProducts, File urlFile) throws IOException {
        List<S2IEoProduct> uniqueProductList = new ArrayList<>(eoProducts.size());
        for (EoProduct eoProduct : eoProducts) {
            uniqueProductList.add((S2IEoProduct) eoProduct);
        }
        Collections.sort(uniqueProductList, (o1, o2) -> Integer.compare(o1.productID, o2.productID));

        try (FileWriter urlWriter = new FileWriter("urls_matchin_insitu.txt")) {
            try (StopWatch sw = new StopWatch("  >>load urls")) {
                try (
                        DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(urlFile)))
                ) {
                    int streamPID = 0;
                    for (S2IEoProduct eoProduct : uniqueProductList) {
                        int productID = eoProduct.productID;
                        while (streamPID < productID) {
                            int utflen = dis.readUnsignedShort();
                            dis.skipBytes(utflen);
                            streamPID++;
                        }
                        String url = dis.readUTF();
                        streamPID++;

                        urlWriter.write(url);
                        urlWriter.write('\n');
                    }
                }
            }
        }
    }
}
