#!/usr/bin/python -u
# -u for unbuffered

import os
import datetime
import calendar
import sys
from pmonitor import PMonitor
from daemon import Daemon

################################################################################
years  = [  '2008' ]
#years  = [ '2002', '2003', '2004', '2005', '2006', '2007', '2008', '2009', '2010', '2011', '2012' ]
monthsAll = [ '01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12' ]
monthsAll = [ '01' ]
months2002 = [ '06', '07', '08', '09', '10', '11', '12' ]
months2012 = [ '01', '02', '03', '04' ]

inputs = ['MERIS_L1B']
hosts  = [('localhost',12)]
types  = [('template-step.py',12)]
################################################################################

def dateFromIsoString(isoString):
    return datetime.datetime.strptime(isoString, '%Y-%m-%d')

def dateRange(start_date, end_date):
    for n in range(int ((end_date - start_date).days) + 1):
        yield start_date + datetime.timedelta(n)

def getMinMaxDate(year, month):
    monthrange = calendar.monthrange(int(year), int(month))
    minDate = datetime.date(int(year), int(month), 1)
    maxDate = datetime.date(int(year), int(month), monthrange[1])
    return (minDate, maxDate)

################################################################################

class OcMeris(Daemon):
    def run(self):
        pm = PMonitor(inputs, request='oc-meris', logdir='log', hosts=hosts, types=types)

        for year in years:
            months = monthsAll
            if year == '2002':
                months = months2002
            elif year == '2012':
                months = months2012

            for month in months:
                (minDate, maxDate) = getMinMaxDate(year, month)
                polymerName = 'polymer-' + str(minDate)
                polymerParams = ['polymer-\${year}-\${month}.xml', \
                              'minDate', str(minDate), \
                              'maxDate', str(maxDate), \
                              'year', str(year), \
                              'month', str(month) ]
                pm.execute('template-step.py', ['MERIS_L1B'], [polymerName], parameters=polymerParams, logprefix=polymerName)

                dayCounter = 0
                for singleDay in dateRange(minDate, maxDate):
                    merisDailyName = 'meris-daily-' + str(singleDay)
                    merisDailyParams = ['meris-daily-useIdepix-QAA-\${date}.xml', \
                              'date', str(singleDay), \
                              'year', '%4d' % (singleDay.year), \
                              'month', '%02d' % (singleDay.month) ]
                    pm.execute('template-step.py', [polymerName], [merisDailyName], parameters=merisDailyParams, logprefix=merisDailyName)

                    mergedDailyName = 'merged-daily-' + str(singleDay)
                    mergedDailyParams = ['merged-daily-\${date}.xml', \
                              'date', str(singleDay), \
                              'year', '%4d' % (singleDay.year), \
                              'month', '%02d' % (singleDay.month) ]
                    pm.execute('template-step.py', [merisDailyName], [mergedDailyName], parameters=mergedDailyParams, logprefix=mergedDailyName)

                    dayCounter = dayCounter + 1
                    if dayCounter > 3:
                        break

        #======================================================
        pm.wait_for_completion()
#======================================================
daemon = OcMeris.setup(sys.argv)        