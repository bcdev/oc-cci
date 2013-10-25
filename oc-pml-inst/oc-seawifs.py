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

class OcSeawifs(Daemon):
    def run(self):
        pm = PMonitor(inputs, request='oc-seawifs', logdir='log', hosts=hosts, types=types)

        seawifsBiasInputs = []
        for year in years:
            months = monthsAll
            if year == '2002':
                months = months2002
            elif year == '2012':
                months = months2012

            for month in months:
                seawifsFormatInputs = []
                (minDate, maxDate) = getMinMaxDate(year, month)

                # for now because we have not more test-data
                minDate = datetime.date(int(year), int(month), 1)
                maxDate = datetime.date(int(year), int(month), 9)

                for singleDay in dateRange(minDate, maxDate):
                    seawifsDailyBSName = 'seawifs-daily-bs-' + str(singleDay)
                    params = ['seawifs-daily-bs-\${date}.xml', \
                               'date', str(singleDay), \
                               'year', year, \
                               'month', month ,\
                               'doy', '%03d' % (singleDay.timetuple().tm_yday)  ]
                    pm.execute('template-step.py', ['SEAWIFS_L3_daily'], [seawifsDailyBSName], parameters=params, logprefix=seawifsDailyBSName)

                    seawifsFormatInputs.append(seawifsDailyBSName)
                    if year >= '2003' and year <= '2007':
                        seawifsBiasInputs.append(seawifsDailyBSName)

                seawifsFormatBSName = 'seawifs-daily-bs-format-' + month + '-' + year
                params = ['l3format-\${prefix}-\${date}.xml', \
                               'date', month + '-' + year, \
                               'inputPath', 'seawifs/daily-bs/' + year + '/' + month + '/????-??-??/part-*', \
                               'outputPath', 'seawifs/daily-bs/' + year + '/' + month + '/netcdf-mapped', \
                               'prefix', 'OC-seawifs-daily-bs' ]
                #pm.execute('template-step.py', seawifsFormatInputs, [seawifsFormatBSName], parameters=params, logprefix=seawifsFormatBSName)

        seawifsBiasMapName = 'seawifs-bias-map'
        params = ['bias-map-\${sensor}.xml', \
                               'sensor', 'seawifs', \
                               'sensorMarker', '12' ]
        pm.execute('template-step.py', seawifsBiasInputs, [seawifsBiasMapName], parameters=params, logprefix=seawifsBiasMapName)

        seawifsBiasFormatName = 'seawifs-bias-map-format'
        params = ['l3format-\${prefix}-\${date}.xml', \
                       'date', '5years', \
                       'inputPath', 'seawifs/bias-map-parts/part-*', \
                       'outputPath', 'seawifs/bias-map-netcdf-mapped', \
                       'prefix', 'OC-seawifs-bias' ]
        #pm.execute('template-step.py', [seawifsBiasMapName], [seawifsBiasFormatName], parameters=params, logprefix=seawifsBiasFormatName)

        #======================================================
        pm.wait_for_completion()
#======================================================
daemon = OcSeawifs.setup(sys.argv)
