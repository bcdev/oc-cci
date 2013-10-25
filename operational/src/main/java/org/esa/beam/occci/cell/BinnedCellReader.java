package org.esa.beam.occci.cell;


import org.esa.beam.framework.datamodel.ProductData;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class BinnedCellReader {

    private NetcdfFile netcdfFile;
    private Date startDateTime;
    private Date endDateTime;

    public void open(File file) throws IOException {
        netcdfFile = NetcdfFile.open(file.getAbsolutePath());

        final Dimension bin_index = netcdfFile.findDimension("bin_index");

        final ProductData.UTC startTime = extractStartTime(netcdfFile);
        startDateTime = startTime.getAsDate();

        final ProductData.UTC endTime = extractEndTime(netcdfFile);
        endDateTime = endTime.getAsDate();

        final ArrayList<String> bandNames = getBandNames(netcdfFile);

    }

    private static ArrayList<String> getBandNames(NetcdfFile netcdfFile) throws IOException {
        final ArrayList<String> bandNames = new ArrayList<String>();
        int largestDimensionSize = getLargestDimensionSize(netcdfFile);
        for (Variable variable : netcdfFile.getVariables()) {
            final String bandName = variable.getFullName();
            if (variable.getDimensions().get(0).getLength() == largestDimensionSize) {
                bandNames.add(bandName);
            }
        }
        return bandNames;
    }

    public void close() throws IOException {
        if (netcdfFile != null) {
            netcdfFile.close();
        }
    }

    // @todo 3 tb/tb duplicated - copied from BinnedProductReader 2013-05-02
    private static ProductData.UTC extractTime(NetcdfFile netcdfFile, String attributeName) throws IOException {
        final Attribute timeAttribute = netcdfFile.findGlobalAttribute(attributeName);
        if (timeAttribute == null) {
            return null;
        }
        String timeAsString = timeAttribute.getStringValue();
        timeAsString = timeAsString.substring(0, timeAsString.length() - 1);
        ProductData.UTC parsedDate = null;
        try {
            parsedDate = ProductData.UTC.parse(timeAsString, "yyyy-MM-dd'T'HH:mm:ss");
        } catch (ParseException ignored) {
        }
        if (parsedDate == null) {
            throw new IOException("Unable to retrieve time: " + attributeName);
        }
        return parsedDate;
    }

    private static ProductData.UTC extractStartTime(NetcdfFile netcdfFile) throws IOException {
        return extractTime(netcdfFile, "start_time");
    }

    private static ProductData.UTC extractEndTime(NetcdfFile netcdfFile) throws IOException {
        return extractTime(netcdfFile, "stop_time");
    }

    private static int getLargestDimensionSize(NetcdfFile netcdfFile) {
        int largestDimensionSize = 0;
        for (Dimension dimension : netcdfFile.getDimensions()) {
            if (dimension.getLength() > largestDimensionSize) {
                largestDimensionSize = dimension.getLength();
            }
        }
        return largestDimensionSize;
    }
}
