#!/usr/bin/python -u
# -u for unbuffered

import os
import datetime
import calendar
import sys
from pmonitor import PMonitor
from daemon import Daemon

################################################################################
#years  = [ '2002', '2003', '2004', '2005', '2006', '2007', '2008', '2009', '2010', '2011', '2012' ]
#monthsAll = [ '01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12' ]

years  = [  '2004' ]
monthsAll = [ '01' ]
months2002 = [ '06', '07', '08', '09', '10', '11', '12' ]
months2012 = [ '01', '02', '03', '04' ]

inputs = ['SEAWIFS_L3_daily']
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
        pm = PMonitor(inputs, request='oc-seawifs', logdir='log', hosts=hosts, types=types)

        for year in years:
            months = monthsAll
            if year == '2002':
                months = months2002
            elif year == '2012':
                months = months2012

            for month in months:
                (minDate, maxDate) = getMinMaxDate(year, month)

                # for now because we have not more test-data
                minDate = datetime.date(int(year), int(month), 1)
                maxDate = datetime.date(int(year), int(month), 1)

                for singleDay in dateRange(minDate, maxDate):
                    seawifsDailyBSName = 'seawifs-daily-bs-' + str(singleDay)
                    seawifsDailyBSParams = ['seawifs-daily-bs-\${date}.xml', \
                               'date', str(singleDay), \
                               'year', year, \
                               'month', month ,\
                               'doy', '%03d' % (singleDay.timetuple().tm_yday)  ]
                    pm.execute('template-step.py', ['SEAWIFS_L3_daily'], [seawifsDailyBSName], parameters=seawifsDailyBSParams, logprefix=seawifsDailyBSName)

        #======================================================
        pm.wait_for_completion()
#======================================================
daemon = OcMeris.setup(sys.argv)        
