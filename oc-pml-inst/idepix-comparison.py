#!/usr/bin/python -u
# -u for unbuffered

import os
import datetime
import calendar
import sys
from pmonitor import PMonitor
from daemon import Daemon

################################################################################
TIME_RANGES = [
  (1, 1, 1), (1, 15, 15),
  (2, 1, 1), (2, 15, 15),
  (3, 1, 1), (3, 15, 15),
  (4, 1, 1), (4, 15, 15),
  (5, 1, 1), (5, 15, 15),
  (6, 1, 30),
  (7, 1, 1), (7, 15, 15),
  (8, 1, 1), (8, 15, 15),
  (9, 1, 1), (9, 15, 15),
  (10, 1, 1), (10, 15, 15),
  (11, 1, 1), (11, 15, 15),
  (12, 1, 31)
]
year = 2003

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
        pm = PMonitor(inputs, request='idepix-comparison', logdir='log', hosts=hosts, types=types)

        for (month, firstDay, lastDay) in TIME_RANGES:
                minDate = datetime.date(year, month, firstDay)
                maxDate = datetime.date(year, month, lastDay)
                
                polymerTag = 'polymer-' + str(minDate)
                params = ['polymer-\${year}-\${month}.xml', \
                              'minDate', str(minDate), \
                              'maxDate', str(maxDate), \
                              'year', str(year), \
                              'month', str(month) ]
                pm.execute('template-step.py', ['MERIS_L1B'], [polymerTag], parameters=params, logprefix=polymerTag)

                idepixOldTag = 'idepix-old-' + str(minDate)
                params = ['idepix-\${variant}-\${year}-\${month}.xml', \
                              'minDate', str(minDate), \
                              'maxDate', str(maxDate), \
                              'variant', 'old', \
                              'processorBundleName', 'beam-idepix-cc', \
                              'year', str(year), \
                              'month', str(month) ]
                pm.execute('template-step.py', ['MERIS_L1B'], [idepixOldTag], parameters=params, logprefix=idepixOldTag)

                idepixNewTag = 'idepix-new-' + str(minDate)
                params = ['idepix-\${variant}-\${year}-\${month}.xml', \
                              'minDate', str(minDate), \
                              'maxDate', str(maxDate), \
                              'variant', 'new', \
                              'processorBundleName', 'beam-idepix-oc', \
                              'year', str(year), \
                              'month', str(month) ]
                pm.execute('template-step.py', ['MERIS_L1B'], [idepixNewTag], parameters=params, logprefix=idepixNewTag)

                #idepixNewQlTag = 'idepix-new-ql-' + str(minDate)
                #params = ['idepix-ql-\${variant}-\${year}-\${month}.xml', \
                #              'minDate', str(minDate), \
                #              'maxDate', str(maxDate), \
                #              'variant', 'new', \
                #              'year', str(year), \
                #              'month', str(month) ]
                #pm.execute('template-step.py', [idepixNewTag, polymerTag], [idepixNewQlTag], parameters=params, logprefix=idepixNewQlTag)


                mergedL2Tag = 'merged-l2-' + str(minDate)
                params = ['merge-polymer-and-two-idepix-\${year}-\${month}.xml', \
                              'minDate', str(minDate), \
                              'maxDate', str(maxDate), \
                              'variant', 'new', \
                              'year', str(year), \
                              'month', str(month) ]
                pm.execute('template-step.py', [idepixNewTag, idepixOldTag, polymerTag], [mergedL2Tag], parameters=params, logprefix=mergedL2Tag)

                mergedL2FormatTag = 'merge-l2-format-' + str(minDate)
                params = ['l2-format-\${year}-\${month}.xml', \
                              'minDate', str(minDate), \
                              'maxDate', str(maxDate), \
                              'year', str(year), \
                              'month', str(month) ]
                pm.execute('template-step.py', [mergedL2Tag], [mergedL2FormatTag], parameters=params, logprefix=mergedL2FormatTag)

        (# quicklooks L2 polymer + old idepix ?)
        # DONE # quicklooks L2 polymer + new idepix
        # DONE # merge L2 polymer + old + new idepix --> Format to DIMAP
        # daily L3 polymer + new idepix + quicklooks
        (# daily L3 polymer + old idepix + quicklooks)
        #======================================================
        pm.wait_for_completion()
#======================================================
daemon = OcMeris.setup(sys.argv)        
