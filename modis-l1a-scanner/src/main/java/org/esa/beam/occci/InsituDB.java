/*
 * Copyright (C) 2014 Brockmann Consult GmbH (info@brockmann-consult.de)
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 */
public class InsituDB {

    private static final long MILLIS_PER_DAY = 1000 * 60 * 60 * 24L;

    private final List<SimpleRecord> insituRecords;
    private final int[] dayIndex;
    private final int firstDay;

    public InsituDB(List<SimpleRecord> insituRecords, int[] dayIndex, int firstDay) {
        this.insituRecords = insituRecords;
        this.dayIndex = dayIndex;
        this.firstDay = firstDay;
    }

    public static InsituDB create(List<SimpleRecord> insituRecords, long maxTimeDifference) {
        Collections.sort(insituRecords, new Comparator<SimpleRecord>() {
            @Override
            public int compare(SimpleRecord sr1, SimpleRecord sr2) {
                long sr1Time = sr1.getTime();
                long sr2Time = sr2.getTime();
                return (sr1Time < sr2Time ? -1 : (sr1Time == sr2Time ? 0 : 1));
            }
        });

        int firstDay = getDaySinceEpoch(insituRecords.get(0).getTime());
        int lastDay = getDaySinceEpoch(insituRecords.get(insituRecords.size() - 1).getTime());
        int indexLength = lastDay - firstDay + 1;
        int[] dayIndex = new int[indexLength];
        Arrays.fill(dayIndex, -1);
        for (int i = 0; i < insituRecords.size(); i++) {
            SimpleRecord insituRecord = insituRecords.get(i);

            int daysSinceEpoch = getDaySinceEpoch(insituRecord.getTime() - maxTimeDifference);
            int indexID = daysSinceEpoch - firstDay;
            if (dayIndex[indexID] == -1) {
                dayIndex[indexID] = i;
            }
        }

        return new InsituDB(insituRecords, dayIndex, firstDay);
    }

    public static int getDaySinceEpoch(long time) {
        return (int) (time / MILLIS_PER_DAY);
    }

    public int getIndexForTime(long startTime) {
        int daysSinceEpoch = getDaySinceEpoch(startTime);
        int indexID = daysSinceEpoch - firstDay;
        if (indexID < dayIndex.length) {
            return dayIndex[indexID];
        } else {
            return -1;
        }
    }

    public SimpleRecord getRecord(int index) {
        if (index < insituRecords.size()) {
            return insituRecords.get(index);
        } else {
            return null;
        }
    }
}
