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
        File productIndexListFile1;
        if (args.length == 2) {
            productIndexListFile1 = new File(args[0]);
            if (!productIndexListFile1.exists()) {
                System.err.printf("productList file '%s' does not exits%n", args[0]);
                printUsage();
            }

            try (StopWatch sw = new StopWatch("TTT ++++TOTAL TIME polygon+++")) {
                ReverseProductDB reverseProductDB;
                try (StopWatch swp = new StopWatch("TTT read product index")) {
                    reverseProductDB = ReverseProductDB.readProductIndex(productIndexListFile1);
                    System.out.println("reverseProductDB.size() = " + reverseProductDB.size());
                }
                try (StopWatch swm = new StopWatch("TTT matching")) {
                    ReverseMatcher reverseMatcher = new ReverseMatcher(reverseProductDB, new File(args[0] + ".polygon"));
                    Set<Integer> eoProducts = performOverlap(reverseMatcher, args[1]);
                    printURLs(eoProducts, new File(productIndexListFile1.getAbsolutePath() + ".url"));
                }
            }
        } else if (args.length == 3) {
            productIndexListFile1 = new File(args[0]);
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

            try (StopWatch sw = new StopWatch("TTT ++++TOTAL TIME insitu+++")) {
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
                    Set<Integer> eoProducts = performInsitu(reverseMatcher, insituRecords, HOURS_IN_MILLIS * hours);
                    printURLs(eoProducts, new File(productIndexListFile1.getAbsolutePath() + ".url"));
                }
            }
        } else {
            printUsage();
        }
    }

    private static Set<Integer> performInsitu(ReverseMatcher matcher, List<SimpleRecord> insituRecords, long maxTimeDifference) {
        System.out.println("==================== insitu " + matcher.getClass().getSimpleName() + " ====================================");
        Set<Integer> eoProducts = matcher.matchInsitu(insituRecords, maxTimeDifference);
        System.out.println("num matches =  " + eoProducts.size());
        return eoProducts;
    }

    private static Set<Integer> performOverlap(ReverseMatcher matcher, String polyWKT) {
        System.out.println("==================== overlap " + matcher.getClass().getSimpleName() + " ====================================");
        Set<Integer> eoProducts = matcher.matchProduct(polyWKT, 0L, Long.MAX_VALUE);
        System.out.println("num matches =  " + eoProducts.size());
        return eoProducts;
    }


    private static void printUsage() {
        System.err.println("Usage: ReverseCheckerMain <productIndexList> <insituList> <hours>");
        System.err.println("or");
        System.err.println("Usage: ReverseCheckerMain <productIndexList> polygon");
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

    static void printURLs(Collection<Integer> eoProducts, File urlFile) throws IOException {
        List<Integer> uniqueProductIndices = new ArrayList<>(eoProducts);
        Collections.sort(uniqueProductIndices);

        try (FileWriter urlWriter = new FileWriter("urls_matching_request.txt")) {
            try (StopWatch sw = new StopWatch("  >>load urls")) {
                try (
                        DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(urlFile)))
                ) {
                    int streamPID = 0;
                    for (int productIndex : uniqueProductIndices) {
                        while (streamPID < productIndex) {
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
