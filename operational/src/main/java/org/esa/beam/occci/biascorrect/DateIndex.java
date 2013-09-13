package org.esa.beam.occci.biascorrect;


import org.esa.beam.util.DateTimeUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

class DateIndex {

    private final Calendar calendar;
    private final int startYear;
    private final int stopYear;

    public DateIndex(int startYear, int stopYear) {
        this.startYear = startYear;
        this.stopYear = stopYear;
        calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    }

    public int get(double mjd) {
        //double mjd = 56538.75963d;   // 3.09.2013, 18:13 UTC

        final double jd = DateTimeUtils.mjdToJD(mjd);
        final Date utc = DateTimeUtils.jdToUTC(jd);
        calendar.setTime(utc);
        final int year = calendar.get(Calendar.YEAR);
        if (year < startYear || year > stopYear) {
            return -1;
        }
        final int yearOffset = 12 * (year - startYear);
        final int month = calendar.get(Calendar.MONTH);
        return yearOffset + month;
    }
}
