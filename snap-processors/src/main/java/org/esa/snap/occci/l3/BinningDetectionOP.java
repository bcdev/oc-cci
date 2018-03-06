/*
 * Copyright (C) 2018 Brockmann Consult GmbH (info@brockmann-consult.de) 
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

package org.esa.snap.occci.l3;

import org.esa.snap.binning.DataPeriod;
import org.esa.snap.binning.operator.BinningConfig;
import org.esa.snap.binning.operator.BinningOp;
import org.esa.snap.binning.support.SpatialDataPeriod;
import org.esa.snap.core.dataio.ProductIO;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.PixelPos;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.datamodel.ProductFilter;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.util.ProductUtils;
import org.esa.snap.core.util.SystemUtils;
import org.esa.snap.core.util.io.WildcardMatcher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.esa.snap.binning.operator.BinningOp.DATETIME_INPUT_PATTERN;
import static org.esa.snap.binning.operator.BinningOp.DATE_INPUT_PATTERN;

/**
 * Tests which products are needed for binnning by applying the filtering.
 * Especially the spatial data day filter.
 */
@OperatorMetadata(alias = "OC-CCI.binning-detection",
        description = "Tests which products are needed for binnning",
        authors = "Marco Zuehlke (Brockmann Consult)",
        copyright = "(C) 2018 by Brockmann Consult",
        version = "0.2")
public class BinningDetectionOP extends Operator {

    @Parameter(description = "A comma-separated list of file paths specifying the source products.\n" +
            "Each path may contain the wildcards '**' (matches recursively any directory),\n" +
            "'*' (matches any character sequence in path names) and\n" +
            "'?' (matches any single character).",
            notNull = true, notEmpty = true)
    private String[] sourceProductPaths;

    @Parameter(description = "The common product format of all source products. This parameter is optional and may be used in conjunction " +
            "with parameter 'sourceProductPaths' and only to speed up source product opening." +
            "Try \"NetCDF-CF\", \"GeoTIFF\", \"BEAM-DIMAP\", or \"ENVISAT\", etc.",
            defaultValue = "")
    private String sourceProductFormat;

    @Parameter(pattern = "\\d{4}-\\d{2}-\\d{2}(\\s\\d{2}:\\d{2}:\\d{2})?",
            description = "The UTC start date of the binning period. " +
                    "The format is either 'yyyy-MM-dd HH:mm:ss' or 'yyyy-MM-dd'. If only the date part is given, the time 00:00:00 is assumed.",
            notNull = true, notEmpty = true)
    private String startDateTime;

    @Parameter(description = "Duration of the binning period in days.", notNull = true, notEmpty = true)
    private Double periodDuration;

    @Parameter(description = "The method that is used to decide which source pixels are used with respect to their observation time. " +
            "'NONE': ignore pixel observation time, use all source pixels. " +
            "'TIME_RANGE': use all pixels that have been acquired in the given binning period. " +
            "'SPATIOTEMPORAL_DATA_DAY': use a sensor-dependent, spatial \"data-day\" definition with the goal " +
            "to minimise the time between the first and last observation contributing to the same bin in the given binning period. " +
            "The decision, whether a source pixel contributes to a bin or not, is a function of the pixel's observation longitude and time. " +
            "Requires the parameter 'minDataHour'.",
            defaultValue = "NONE")
    private BinningOp.TimeFilterMethod timeFilterMethod;

    @Parameter(interval = "[0,24]",
            description = "A sensor-dependent constant given in hours of a day (0 to 24) at which a sensor has a minimum number of " +
                    "observations at the date line (the 180 degree meridian). Only used if parameter 'timeFilterMethod' is set to 'SPATIOTEMPORAL_DATADAY'.",
            notNull = true, notEmpty = true)
    private Double minDataHour;

    @Parameter
    private String fileWithDetectedProducts;

    @Override
    public void initialize() throws OperatorException {
        validateInput();

        ProductData.UTC startUTC = parseStartDateUtc(startDateTime);
        DataPeriod dataPeriod = BinningConfig.createDataPeriod(startUTC, periodDuration, minDataHour);
        BinningProductFilter productFilter = createSourceProductFilter(dataPeriod);

        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileWithDetectedProducts)))) {
            handleSources(out, productFilter);
        } catch (IOException ioe) {
            throw new OperatorException(ioe);
        }
        setTargetProduct(new Product("dummy", "dummy", 0, 0));
    }

    private void handleSources(PrintWriter out, BinningProductFilter productFilter) throws IOException {
        if (sourceProductPaths != null) {
            getLogger().info("expanding sourceProductPaths wildcards.");
            SortedSet<File> fileSet = new TreeSet<>();
            for (String filePattern : sourceProductPaths) {
                WildcardMatcher.glob(filePattern, fileSet);
            }
            if (fileSet.isEmpty()) {
                getLogger().warning("The given source file patterns did not match any files");
            } else {
                getLogger().info("found " + fileSet.size() + " files.");
                for (File file : fileSet) {
                    getLogger().info(file.getCanonicalPath());
                }
            }
            for (File file : fileSet) {
                Product sourceProduct = null;
                try {
                    if (sourceProductFormat != null) {
                        sourceProduct = ProductIO.readProduct(file, sourceProductFormat);
                    } else {
                        sourceProduct = ProductIO.readProduct(file);
                    }
                } catch (Exception e) {
                    String msgPattern = "Failed to read file '%s'. %s: %s";
                    getLogger().severe(String.format(msgPattern, file, e.getClass().getSimpleName(), e.getMessage()));
                }
                if (sourceProduct != null) {
                    try {
                        if (productFilter.accept(sourceProduct)) {
                            out.println(file.getName());
                            getLogger().info("Accepting product '" + sourceProduct.getFileLocation() + "'");
                        } else {
                            getLogger().info("Filtered out product '" + sourceProduct.getFileLocation() + "'");
                            getLogger().info("              reason: " + productFilter.getReason());
                        }
                    } finally {
                        sourceProduct.dispose();
                    }
                } else {
                    String msgPattern = "Failed to read file '%s' (not a data product or reader missing)";
                    getLogger().severe(String.format(msgPattern, file));
                }
            }
        }
    }

    static ProductData.UTC parseStartDateUtc(String date) {
        try {
            if (date.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
                return ProductData.UTC.parse(date, DATETIME_INPUT_PATTERN);
            } else {
                return ProductData.UTC.parse(date, DATE_INPUT_PATTERN);
            }
        } catch (ParseException e) {
            throw new OperatorException(String.format("Error while parsing start date parameter '%s': %s", date, e.getMessage()));
        }
    }


    static BinningProductFilter createSourceProductFilter(DataPeriod dataPeriod) {
        if (dataPeriod instanceof SpatialDataPeriod) {
            return new SpatialDataDaySourceProductFilter(null, dataPeriod);
        }
        return null;
    }

    private void validateInput() {
        if (timeFilterMethod == null) {
            timeFilterMethod = BinningOp.TimeFilterMethod.NONE;
        }
        if (timeFilterMethod != BinningOp.TimeFilterMethod.NONE && (startDateTime == null || periodDuration == null)) {
            throw new OperatorException("Using a time filer requires the parameters 'startDateTime' and 'periodDuration'");
        }
        if (periodDuration != null && periodDuration < 0.0) {
            throw new OperatorException("The parameter 'periodDuration' must be a positive value");
        }
        if (timeFilterMethod == BinningOp.TimeFilterMethod.SPATIOTEMPORAL_DATA_DAY && minDataHour == null) {
            throw new OperatorException("If SPATIOTEMPORAL_DATADAY filtering is used the parameters 'minDataHour' must be given");
        }
    }

    static abstract class BinningProductFilter implements ProductFilter {

        private String reason;
        private BinningProductFilter parent;

        String getReason() {
            if (parent != null && parent.getReason() != null) {
                return parent.getReason();
            } else {
                return reason;
            }
        }

        @Override
        public boolean accept(Product product) {
            setReason(null);
            if (parent != null && !parent.accept(product)) {
                return false;
            }
            return acceptForBinning(product);
        }

        protected abstract boolean acceptForBinning(Product product);

        public void setReason(String reason) {
            this.reason = reason;
        }

        public void setParent(BinningProductFilter parent) {
            this.parent = parent;
        }

        public BinningProductFilter getParent() {
            return parent;
        }
    }

    /**
     * Filters out all products that do not overlap with the given data day.
     *
     * @author Thomas Storm
     */
    static class SpatialDataDaySourceProductFilter extends BinningProductFilter {

        private final DataPeriod dataPeriod;

        SpatialDataDaySourceProductFilter(BinningProductFilter parent, DataPeriod dataPeriod) {
            setParent(parent);
            this.dataPeriod = dataPeriod;
        }

        @Override
        protected boolean acceptForBinning(Product product) {
            GeoCoding geoCoding = product.getSceneGeoCoding();
            ProductData.UTC firstScanLineTime = ProductUtils.getScanLineTime(product, 0);
            ProductData.UTC lastScanLineTime = ProductUtils.getScanLineTime(product, product.getSceneRasterHeight() - 1);
            if (firstScanLineTime == null || lastScanLineTime == null) {
                String message = String.format("not accepting product '%s': missing time coding", product.getName());
                setReason(message);
                return false;
            }
            double firstLon = geoCoding.getGeoPos(new PixelPos(0, 0), null).lon;
            DataPeriod.Membership topLeft = dataPeriod.getObservationMembership(firstLon, firstScanLineTime.getMJD());
            double lastLon = geoCoding.getGeoPos(new PixelPos(product.getSceneRasterWidth() - 1, 0), null).lon;
            DataPeriod.Membership topRight = dataPeriod.getObservationMembership(lastLon, firstScanLineTime.getMJD());


            firstLon = geoCoding.getGeoPos(new PixelPos(0, product.getSceneRasterHeight() - 1), null).lon;
            DataPeriod.Membership bottomLeft = dataPeriod.getObservationMembership(firstLon, lastScanLineTime.getMJD());
            lastLon = geoCoding.getGeoPos(new PixelPos(product.getSceneRasterWidth() - 1, product.getSceneRasterHeight() - 1), null).lon;
            DataPeriod.Membership bottomRight = dataPeriod.getObservationMembership(lastLon, lastScanLineTime.getMJD());

            String message = String.format("accepting product '%s': " +
                                                   "topLeftMembership=%s, topRightMembership=%s, " +
                                                   "bottomLeftMembership=%s, bottomRightMembership=%s, " +
                                                   "startTime=%s, endTime=%s",
                                           product.getName(),
                                           topLeft, topRight,
                                           bottomLeft, bottomRight,
                                           product.getStartTime(),
                                           product.getEndTime());

            if (topLeft == topRight &&
                    topRight == bottomLeft &&
                    bottomLeft == bottomRight &&
                    topLeft != DataPeriod.Membership.CURRENT_PERIOD) {
                final String msg = "not " + message;
                SystemUtils.LOG.finer(msg);
                setReason(msg);
                return false;
            }
            SystemUtils.LOG.finer(message);

            return true;
        }
    }

    public static class Spi extends OperatorSpi {
        public Spi() {
            super(BinningDetectionOP.class);
        }
    }
}
