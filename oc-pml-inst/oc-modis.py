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

inputs = ['MODIS_L3_daily']
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

class OcModis(Daemon):
    def run(self):
        pm = PMonitor(inputs, request='oc-modis', logdir='log', hosts=hosts, types=types)

        modisBiasInputs = []
        for year in years:
            months = monthsAll
            if year == '2002':
                months = months2002
            elif year == '2012':
                months = months2012

            for month in months:
                modisFormatInputs = []
                (minDate, maxDate) = getMinMaxDate(year, month)

                # for now because we have not more test-data
                minDate = datetime.date(int(year), int(month), 1)
                maxDate = datetime.date(int(year), int(month), 9)

                for singleDay in dateRange(minDate, maxDate):
                    modisDailyBSName = 'modis-daily-bs-' + str(singleDay)
                    params = ['modis-daily-bs-\${date}.xml', \
                               'date', str(singleDay), \
                               'year', year, \
                               'month', month ,\
                               'doy', '%03d' % (singleDay.timetuple().tm_yday)  ]
                    pm.execute('template-step.py', ['MODIS_L3_daily'], [modisDailyBSName], parameters=params, logprefix=modisDailyBSName)

                    modisFormatInputs.append(modisDailyBSName)
                    if year >= '2003' and year <= '2007':
                        modisBiasInputs.append(modisDailyBSName)

                modisFormatBSName = 'modis-daily-bs-format-' + month + '-' + year
                params = ['l3format-\${prefix}-\${date}.xml', \
                               'date', month + '-' + year, \
                               'inputPath', 'modis/daily-bs/' + year + '/' + month + '/????-??-??/part-*', \
                               'outputPath', 'modis/daily-bs/' + year + '/' + month + '/netcdf-mapped', \
                               'prefix', 'OC-modis-daily-bs' ]
                #pm.execute('template-step.py', modisFormatInputs, [modisFormatBSName], parameters=params, logprefix=modisFormatBSName)

        modisBiasMapName = 'modis-bias-map'
        params = ['bias-map-\${sensor}.xml', \
                               'sensor', 'modis', \
                               'sensorMarker', '11' ]
        pm.execute('template-step.py', modisBiasInputs, [modisBiasMapName], parameters=params, logprefix=modisBiasMapName)

        modisBiasFormatName = 'modis-bias-map-format'
        params = ['l3format-\${prefix}-\${date}.xml', \
                       'date', '5years', \
                       'inputPath', 'modis/bias-map-parts/part-*', \
                       'outputPath', 'modis/bias-map-netcdf-mapped', \
                       'prefix', 'OC-modis-bias' ]
        #pm.execute('template-step.py', [modisBiasMapName], [modisBiasFormatName], parameters=params, logprefix=modisBiasFormatName)

        #======================================================
        pm.wait_for_completion()
#======================================================
daemon = OcModis.setup(sys.argv)
