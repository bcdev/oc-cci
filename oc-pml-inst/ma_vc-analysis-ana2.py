#!/usr/bin/python -u
# -u for unbuffered

import sys
from pmonitor import PMonitor
from daemon import Daemon
################################################################################
sensors = ['SEAWIFS', 'MODIS']

gains = {
    'MODIS':
        {
            'NOVIC' : 'CALIB        1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0',
            'VIC1' : 'CALIB        0.997 1.000 1. 0.999 1.001 0.999 1. 1. 0.993 1. 1. 1. 1. 1.',
            'VIC2' : 'CALIB        1. 0.998 1. 0.997 0.999 0.998 1. 1. 0.994 1. 1. 1. 1. 1.',
        },
    'SEAWIFS':
        {
            'NOVIC' : 'CALIB        1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0',
            'bandset_1' : 'CALIB 1. 1.004 0.999 1.000 1.011 1. 1. 1.',
            'bandset_2' : 'CALIB 1. 1.004 0.998 0.999 1.009 0.980 1. 1.',
            'bandset_3' : 'CALIB 1.043 1.028 1.012 1.010 1. 1. 1. 1.',
            'bandset_4' : 'CALIB 0.994 0.991 0.991 0.996 1.011 0.993 1. 1.',
          }
}

pointData = {
    'MODIS': 'oc_cci_v2.2_bs_MODIS_allsites.csv',
    'SEAWIFS':  'oc_cci_v2.2_bs_SeaWiFS_allsites.csv',
}
################################################################################
calvalusPointDataRoot = '/calvalus/projects/vc-analysis/point-data'
localPointDataRoot = 'vc-ana-point-data'

hosts = [('localhost', 2)]
types = [('ingest-point-data.sh', 1), ('template-step.py', 2)]
################################################################################
class MyDeamon(Daemon):
    def run(self):
        allLocalPointData = []
        for sensor in sensors:
            allLocalPointData.append(localPointDataRoot + "/" + pointData[sensor])

        pm = PMonitor(allLocalPointData, request='ma_vc-analysis-ana', logdir='log', hosts=hosts, types=types)

        for sensor in sensors:
            localPointData = localPointDataRoot + "/" + pointData[sensor]
            calvalusPointData = calvalusPointDataRoot + '/' + pointData[sensor]
            handle = "ingest-point-data-" + sensor
            pm.execute('ingest-point-data.sh', [localPointData], [calvalusPointData], logprefix=handle)
            #======================================================
            for vc in gains[sensor].keys():
                params = ['ma_vc-analysis-ana-' + sensor + '-\${station}-\${vc}.xml',
                          'station', 'global',
                          'sensor', sensor,
                          'vc', vc,
                          'calib', '"'+gains[sensor][vc]+'"',
                          'calvalusPointData', calvalusPointData,
                          'output', '/calvalus/projects/vc-analysis2/' + sensor + "-" + vc
                ]
                handle = 'vc-analysis-ana-' + sensor + "-" + vc
                pm.execute('template-step.py', [calvalusPointData], [handle], parameters=params, logprefix=handle)
        #======================================================
        pm.wait_for_completion()
        #======================================================
daemon = MyDeamon.setup(sys.argv)
