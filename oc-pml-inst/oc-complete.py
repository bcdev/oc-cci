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

inputs = ['MERIS_L1B', 'MODIS_L3_daily', 'SEAWIFS_L3_daily']
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

class OcComplete(Daemon):
    def run(self):
        pm = PMonitor(inputs, request='oc-complete', logdir='log', hosts=hosts, types=types)

#########################################################################################
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
                params = ['meris-polymer-\${year}-\${month}.xml', \
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
                               'outputPath', 'meris/daily/' + year + '/' + month + '/netcdf-geo', \
                               'prefix', 'OC-meris-daily' ]
                #pm.execute('template-step.py', merisDailyFormatInputs, [merisFormatName], parameters=params, logprefix=merisFormatName)

                merisFormatBSName = 'meris-daily-bs-format-' + month + '-' + year
                params = ['l3format-\${prefix}-\${date}.xml', \
                               'date', month + '-' + year, \
                               'inputPath', 'meris/daily-bs/' + year + '/' + month + '/????-??-??/part-*', \
                               'outputPath', 'meris/daily-bs/' + year + '/' + month + '/netcdf-geo', \
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
                       'outputPath', 'meris/bias-map-netcdf-geo', \
                       'prefix', 'OC-meris-bias' ]
        #pm.execute('template-step.py', [merisBiasMapName], [merisBiasFormatName], parameters=params, logprefix=merisBiasFormatName)

######################################################################

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
                               'outputPath', 'modis/daily-bs/' + year + '/' + month + '/netcdf-geo', \
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
                       'outputPath', 'modis/bias-map-netcdf-geo', \
                       'prefix', 'OC-modis-bias' ]
        #pm.execute('template-step.py', [modisBiasMapName], [modisBiasFormatName], parameters=params, logprefix=modisBiasFormatName)

######################################################################

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
                               'outputPath', 'seawifs/daily-bs/' + year + '/' + month + '/netcdf-geo', \
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
                       'outputPath', 'seawifs/bias-map-netcdf-geo', \
                       'prefix', 'OC-seawifs-bias' ]
        #pm.execute('template-step.py', [seawifsBiasMapName], [seawifsBiasFormatName], parameters=params, logprefix=seawifsBiasFormatName)


######################################################################

        mergedInput = [merisBiasMapName, modisBiasMapName, seawifsBiasMapName]
        for year in years:
            months = monthsAll
            if year == '2002':
                months = months2002
            elif year == '2012':
                months = months2012

            for month in months:
                formatMergedInputs = []
                (minDate, maxDate) = getMinMaxDate(year, month)

                # for now because we have not more test-data
                minDate = datetime.date(int(year), int(month), 1)
                maxDate = datetime.date(int(year), int(month), 9)

                for singleDay in dateRange(minDate, maxDate):
                    mergedName = 'merged-daily-' + str(singleDay)
                    params = ['sensor-merging-\${date}.xml', \
                               'date', str(singleDay), \
                               'year', year, \
                               'month', month  ]

                    pm.execute('template-step.py', mergedInput, [mergedName], parameters=params, logprefix=mergedName)
                    formatMergedInputs.append(mergedName)

                mergedFormatName = 'merged-daily-format-' + month + '-' + year
                params = ['l3format-\${prefix}-\${date}.xml', \
                               'date', month + '-' + year, \
                               'inputPath', 'merged-daily/' + year + '/' + month + '/????-??-??-parts/part-*', \
                               'outputPath', 'merged-daily/' + year + '/' + month + '/netcdf-geo', \
                               'prefix', 'OC-merged-daily' ]
                pm.execute('template-step.py', formatMergedInputs, [mergedFormatName], parameters=params, logprefix=mergedFormatName)


        #======================================================
        pm.wait_for_completion()
#======================================================
daemon = OcComplete.setup(sys.argv)
