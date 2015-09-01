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

import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.spatial.prefix.NumberRangePrefixTreeStrategy;
import org.apache.lucene.spatial.prefix.PrefixTreeStrategy;
import org.apache.lucene.spatial.prefix.RecursivePrefixTreeStrategy;
import org.apache.lucene.spatial.prefix.tree.DateRangePrefixTree;
import org.apache.lucene.spatial.prefix.tree.NumberRangePrefixTree;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by marcoz on 31.08.15.
 */
public class LuceneCreateIndexMain {

    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    private static final Locale ENGLISH = Locale.ENGLISH;

    public static void main(String[] args) throws IOException, ParseException {
        if (args.length != 2) {
            printUsage();
        }
        File productListFile = new File(args[0]);
        File indexfile = new File(args[1]);
        if (!productListFile.exists()) {
            System.err.printf("productList file '%s' does not exits%n", args[0]);
            printUsage();
        }
        List<EoProduct> eoProductList = ProductDB.readProducts("s2", productListFile);


        Directory indexDirectory = FSDirectory.open(indexfile.toPath());
        IndexWriterConfig config = new IndexWriterConfig(new SimpleAnalyzer());
        config.setRAMBufferSizeMB(100);

        DateRangePrefixTree dateRangePrefixTree = DateRangePrefixTree.INSTANCE;
        PrefixTreeStrategy strategy = new NumberRangePrefixTreeStrategy(dateRangePrefixTree, "productDateRange");

        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", ENGLISH);
        dateFormat.setCalendar(GregorianCalendar.getInstance(UTC, Locale.ENGLISH));

        int indexCount = 0;
        try (IndexWriter indexWriter = new IndexWriter(indexDirectory, config)) {
            for (EoProduct eoProduct : eoProductList) {
                Document doc = new Document();
                doc.add(new StringField("name", eoProduct.getName(), Field.Store.YES));
                String start = dateFormat.format(new Date(eoProduct.getStartTime()));
                String end = dateFormat.format(new Date(eoProduct.getEndTime()));
                String range = "[" + start + " TO " + end + "]";

                NumberRangePrefixTree.NRShape nrShape = dateRangePrefixTree.parseShape(range);
                for (IndexableField f : strategy.createIndexableFields(nrShape)) {
                    doc.add(f);
                }
                indexWriter.addDocument(doc);

                indexCount++;
                if (indexCount % 10_000 == 0) {
                    System.out.println("indexCount = " + indexCount);
                }
            }

        }

    }

    private static void printUsage() {
        System.err.println("Usage: LuceneCreateIndexMain <productList> <indexfile>");
        System.exit(1);
    }
}
