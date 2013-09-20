package org.esa.beam.occci.biascorrect;


import org.esa.beam.framework.datamodel.ProductData;

import java.util.Calendar;
import java.util.TimeZone;

class DateIndexCalculator {

    static final int INVALID = -1;

    private final Calendar calendar;
    private final int startYear;
    private final int stopYear;

    DateIndexCalculator(int startYear, int stopYear) {
        this.startYear = startYear;
        this.stopYear = stopYear;
        calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    }

    int get(double mjd2000) {
        final ProductData.UTC utc = new ProductData.UTC(mjd2000);

        calendar.setTime(utc.getAsDate());
        final int year = calendar.get(Calendar.YEAR);
        if (year < startYear || year > stopYear) {
            return INVALID;
        }
        final int yearOffset = 12 * (year - startYear);
        final int month = calendar.get(Calendar.MONTH);
        return yearOffset + month;
    }

    int getIndexCount() {
        return getNumYears() * 12;
    }

    int getNumYears() {
        return stopYear - startYear + 1;
    }

    // for testing only - tb 2013-09-18
    int getStartYear() {
        return startYear;
    }

    // for testing only - tb 2013-09-18
    int getStopYear() {
        return stopYear;
    }
}
