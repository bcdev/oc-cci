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

        merisBiasInputs = []
        for year in years:
            merisDailyTemplate = 'meris-daily-useIdepix-QAA-\${date}.xml'
            months = monthsAll
            if year == '2002':
                months = months2002
                # in 2002 the current version of idepix is not correct because of sensor re-programming
                merisDailyTemplate = 'meris-daily-useIdepix-QAA-2002-\${date}.xml'
            elif year == '2012':
                months = months2012

            for month in months:
                merisDailyFormatInputs = []
                merisDailyBSFormatInputs = []
                (minDate, maxDate) = getMinMaxDate(year, month)
                polymerName = 'polymer-' + str(minDate)
                params = ['polymer-\${year}-\${month}.xml', \
                              'minDate', str(minDate), \
                              'maxDate', str(maxDate), \
                              'year', year, \
                              'month', month ]
                pm.execute('template-step.py', ['MERIS_L1B'], [polymerName], parameters=params, logprefix=polymerName)

                # for now because we have not more test-data
                minDate = datetime.date(int(year), int(month), 1)
                maxDate = datetime.date(int(year), int(month), 9)

                for singleDay in dateRange(minDate, maxDate):
                    merisDailyName = 'meris-daily-' + str(singleDay)
                    params = [merisDailyTemplate, \
                              'date', str(singleDay), \
                              'year', year, \
                              'month', month ]
                    pm.execute('template-step.py', [polymerName], [merisDailyName], parameters=params, logprefix=merisDailyName)
                    merisDailyFormatInputs.append(merisDailyName)

                    merisDailyBSName = 'meris-daily-bs-' + str(singleDay)
                    params = ['meris-daily-bs-\${date}.xml', \
                               'date', str(singleDay), \
                               'year', year, \
                               'month', month ]
                    pm.execute('template-step.py', [merisDailyName], [merisDailyBSName], parameters=params, logprefix=merisDailyBSName)

                    merisDailyBSFormatInputs.append(merisDailyBSName)
                    if year >= '2003' and year <= '2007':
                        merisBiasInputs.append(merisDailyBSName)

                merisFormatName = 'meris-daily-format-' + month + '-' + year
                params = ['l3format-\${prefix}-\${date}.xml', \
                               'date', month + '-' + year, \
                               'inputPath', 'meris/daily/' + year + '/' + month + '/????-??-??-L3-1/part-*', \
                               'outputPath', 'meris/daily/' + year + '/' + month + '/netcdf-mapped', \
                               'prefix', 'OC-meris-daily' ]
                #pm.execute('template-step.py', merisDailyFormatInputs, [merisFormatName], parameters=params, logprefix=merisFormatName)

                merisFormatBSName = 'meris-daily-bs-format-' + month + '-' + year
                params = ['l3format-\${prefix}-\${date}.xml', \
                               'date', month + '-' + year, \
                               'inputPath', 'meris/daily-bs/' + year + '/' + month + '/????-??-??/part-*', \
                               'outputPath', 'meris/daily-bs/' + year + '/' + month + '/netcdf-mapped', \
                               'prefix', 'OC-meris-daily-bs' ]
                #pm.execute('template-step.py', merisDailyBSFormatInputs, [merisFormatBSName], parameters=params, logprefix=merisFormatBSName)

        merisBiasMapName = 'meris-bias-map'
        params = ['bias-map-\${sensor}.xml', \
                               'sensor', 'meris', \
                               'sensorMarker', '10' ]
        pm.execute('template-step.py', merisBiasInputs, [merisBiasMapName], parameters=params, logprefix=merisBiasMapName)

        merisBiasFormatName = 'meris-bias-map-format'
        params = ['l3format-\${prefix}-\${date}.xml', \
                       'date', '5years', \
                       'inputPath', 'meris/bias-map-parts/part-*', \
                       'outputPath', 'meris/bias-map-netcdf-mapped', \
                       'prefix', 'OC-meris-bias' ]
        #pm.execute('template-step.py', [merisBiasMapName], [merisBiasFormatName], parameters=params, logprefix=merisBiasFormatName)

        #======================================================
        pm.wait_for_completion()
#======================================================
daemon = OcMeris.setup(sys.argv)        
