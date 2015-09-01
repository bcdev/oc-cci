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

import com.spatial4j.core.distance.DistanceUtils;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.spatial.prefix.NumberRangePrefixTreeStrategy;
import org.apache.lucene.spatial.prefix.PrefixTreeStrategy;
import org.apache.lucene.spatial.prefix.tree.DateRangePrefixTree;
import org.apache.lucene.spatial.prefix.tree.NumberRangePrefixTree;
import org.apache.lucene.spatial.query.SpatialArgs;
import org.apache.lucene.spatial.query.SpatialOperation;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

/**
 * Created by marcoz on 31.08.15.
 */
public class LuceneQueryIndexMain {

    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    private static final Locale ENGLISH = Locale.ENGLISH;
    private static final long HOURS_IN_MILLIS = 1000 * 60 * 60; // Note: time in ms (NOT h)


    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            printUsage();
        }
        File indexfile = new File(args[0]);
        File insituCSVtFile = new File(args[1]);
        if (!insituCSVtFile.exists()) {
            System.err.printf("insituList file '%s' does not exits%n", args[2]);
            printUsage();
        }
        int hours = 0;
        try {
            hours = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.err.printf("cannot parse hours '%s' %n", args[3]);
            printUsage();
        }
        long maxTimeDifference = HOURS_IN_MILLIS * hours;

        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", ENGLISH);
        dateFormat.setCalendar(GregorianCalendar.getInstance(UTC, Locale.ENGLISH));

        List<SimpleRecord> insituRecords = ProductDBCheckerMain.readInsituRecords(insituCSVtFile);
        System.out.println("num insituRecords = " + insituRecords.size());

        Directory indexDirectory = FSDirectory.open(indexfile.toPath());
        IndexReader indexReader = DirectoryReader.open(indexDirectory);

        int numProductsInIndex = indexReader.getDocCount("name");
        System.out.println("numProductsInIndex = " + numProductsInIndex);

        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        DateRangePrefixTree dateRangePrefixTree = DateRangePrefixTree.INSTANCE;
        PrefixTreeStrategy strategy = new NumberRangePrefixTreeStrategy(dateRangePrefixTree, "productDateRange");

        SpatialOperation operation = SpatialOperation.Intersects;
        int hits = 0;
        long t1 = System.currentTimeMillis();
        Set<Integer> matches = new HashSet<>();

        Calendar calendar = dateRangePrefixTree.newCal();
        for (SimpleRecord insituRecord : insituRecords) {
            final long referenceTime = insituRecord.getTime();
            final long windowStartTime = referenceTime - maxTimeDifference;
            final long windowEndTime = referenceTime + maxTimeDifference;

            calendar.setTimeInMillis(windowStartTime);
            NumberRangePrefixTree.UnitNRShape leftShape = dateRangePrefixTree.toShape(calendar);
            calendar.setTimeInMillis(windowEndTime);
            NumberRangePrefixTree.UnitNRShape rightShape = dateRangePrefixTree.toShape(calendar);

            NumberRangePrefixTree.NRShape nrShape = dateRangePrefixTree.toRangeShape(leftShape, rightShape);
            SpatialArgs sargs = new SpatialArgs(operation, nrShape);
            Query query = strategy.makeQuery(sargs);

            TopDocs topDocs = indexSearcher.search(query, 1000);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            for (ScoreDoc scoreDoc : scoreDocs) {
                matches.add(scoreDoc.doc);
            }
//                Document doc = indexSearcher.doc(docID);
//                String productName = doc.get("name");
//                matches.add(productName);
//            }
//            System.out.println("topDocs.totalHits = " + topDocs.totalHits);
//            hits += topDocs.totalHits;
        }
        long t2 = System.currentTimeMillis();
        System.out.println("delta time test insitu = " + ((t2 - t1) / 1000f));

        System.out.println("hits = " + hits);
        System.out.println("matches = " + matches.size());

    }

    private static void printUsage() {
        System.err.println("Usage: LuceneQueryIndexMain <indexfile> <insituList> <hours>");
        System.exit(1);
    }
}
