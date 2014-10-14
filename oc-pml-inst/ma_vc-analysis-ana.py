#!/usr/bin/python -u
# -u for unbuffered

import sys
from pmonitor import PMonitor
from daemon import Daemon
################################################################################
sensors = ['SEAWIFS', 'MODIS']
sensorStations = {
    'MODIS': ['MOBY'],
    'SEAWIFS': ['MOBY', 'BOUSSOL', 'AAOT'],
}
gainsWithoutVC = {
    'MODIS': 'CALIB        1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 ',
    'SEAWIFS': 'CALIB      1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0',
}
gainsWithVC = {
    'MODIS':
        { 'default' : 'CALIB   0.995 1.001 1. 1.000 1.001 1.000 1. 1. 0.992 1. 1. 1. 1. 1. '},
    'SEAWIFS':
        {
            'bandset_1' : 'CALIB 1. 1.007 1.000 1.002 1.015 1. 1. 1. ',
            'bandset_2' : 'CALIB 1. 1.006 0.998 1.000 1.011 0.982 1. 1. ',
            'bandset_3' : 'CALIB 1.048 1.033 1.014 1.013 1. 1. 1. 1. ',
            'bandset_4' : 'CALIB 0.997 0.992 0.990 0.994 1.010 0.989 1. 1. ',
          }
}

pointData = {
    'MODIS': {
        'MOBY': 'oc_cci_MOBY_MODIS.csv',
    },
    'SEAWIFS': {
        'MOBY': 'oc_cci_MOBY_SeaWiFS.csv',
        'BOUSSOL': 'insitudb_v2.1_satbands6_SEAWIFS_bs_DM_QC_boussole.txt',
        'AAOT': 'insitudb_v2.1_satbands6_SEAWIFS_bs_DM_QC_aaot.txt',
    }
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
            for station in sensorStations[sensor]:
                allLocalPointData.append(localPointDataRoot + "/" + pointData[sensor][station])
        print allLocalPointData
        pm = PMonitor(allLocalPointData, request='ma_vc-analysis-ana', logdir='log', hosts=hosts, types=types)

        for sensor in sensors:
            for station in sensorStations[sensor]:
                localPointData = localPointDataRoot + "/" + pointData[sensor][station]
                calvalusPointData = calvalusPointDataRoot + '/' + pointData[sensor][station]
                handle = "ingest-point-data-" + sensor + "-" + station
                pm.execute('ingest-point-data.sh', [localPointData], [calvalusPointData], logprefix=handle)
                #======================================================
                for vc in gainsWithVC[sensor].keys():
                    params = ['ma_vc-analysis-ana-' + sensor + '-\${station}-\${vc}.xml',
                              'station', station,
                              'sensor', sensor,
                              'vc', vc,
                              'calib', '"'+gainsWithVC[sensor][vc]+'"',
                              'calvalusPointData', calvalusPointData,
                              'output', '/calvalus/projects/vc-analysis/' + sensor + "-" + station + "-" + vc
                    ]
                    handle = 'vc-analysis-ana-' + sensor + "-" + station + "-" + vc
                    pm.execute('template-step.py', [calvalusPointData], [handle], parameters=params, logprefix=handle)
                #======================================================
                vc = 'withoutvc'
                params = ['ma_vc-analysis-ana-' + sensor + '-\${station}-\${vc}.xml',
                          'station', station,
                          'sensor', sensor,
                          'vc', vc,
                          'calib', '"'+gainsWithoutVC[sensor]+'"',
                          'calvalusPointData', calvalusPointData,
                          'output', '/calvalus/projects/vc-analysis/' + sensor + "-" + station + "-" + vc
                ]
                handle = 'vc-analysis-ana-' + sensor + "-" + station + "-" + vc
                pm.execute('template-step.py', [calvalusPointData], [handle], parameters=params, logprefix=handle)
        #======================================================
        pm.wait_for_completion()
        #======================================================
daemon = MyDeamon.setup(sys.argv)
