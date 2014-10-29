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

def getMinMaxDate(year, month):
    monthrange = calendar.monthrange(int(year), int(month))
    minDate = datetime.date(int(year), int(month), 1)
    maxDate = datetime.date(int(year), int(month), monthrange[1])
    return (minDate, maxDate)

################################################################################

class OcMeris(Daemon):
    def run(self):
        pm = PMonitor(inputs, request='oc-idepix-meris', logdir='log', hosts=hosts, types=types)

        for year in years:
            months = monthsAll
            if year == '2002':
                months = months2002
                # in 2002 the current version of idepix is not correct because of sensor re-programming
                merisDailyTemplate = 'meris-daily-useIdepix-2002-\${date}.xml'
            elif year == '2012':
                months = months2012

            for month in months:
                (minDate, maxDate) = getMinMaxDate(year, month)

                idepixName = 'meris-idepix-' + year + '-' + month
                params = ['meris-idepix-\${year}-\${month}.xml', \
                          'minDate', str(minDate), \
                          'maxDate', str(maxDate), \
                          'year', year, \
                          'month', month ]
                pm.execute('template-step.py', ['MERIS_L1B'], [idepixName], parameters=params, logprefix=idepixName)

        #======================================================
        pm.wait_for_completion()
#======================================================
daemon = OcMeris.setup(sys.argv)        
