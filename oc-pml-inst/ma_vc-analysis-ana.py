#!/usr/bin/python -u
# -u for unbuffered

import sys
from pmonitor import PMonitor
from daemon import Daemon
################################################################################
sensors = ['MODIS', 'SEAWIFS']
sensorStations = {
    'MODIS': ['MOBY'],
    'SEAWIFS': ['MOBY', 'BOUSSOL', 'AAOT'],
}
gainsWithoutVC = {
    'MODIS': 'CALIB        1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0',
    'SEAWIFS': 'CALIB        1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0',
}
gainsWithVC = {
    'MODIS': 'CALIB        0.998 1.004 1. 1.002 1.003 1.002 1. 1. 0.993 1. 1. 1. 1. 1.',
    'SEAWIFS': 'CALIB 0.956 0.96 0.968 0.975 0.997 0.993',
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

hosts = [('localhost', 1)]
types = []
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
                vc = 'withvc'
                params = ['ma_vc-analysis-ana-' + sensor + '-\${station}-\${vc}.xml',
                          'station', station,
                          'vc', vc,
                          'calib', '"'+gainsWithVC[sensor]+'"',
                          'calvalusPointData', calvalusPointData,
                          'output', '/calvalus/projects/vc-analysis/' + sensor + "-" + station + "-" + vc
                ]
                handle = 'vc-analysis-ana-' + sensor + "-" + station + "-" + vc
                pm.execute('template-step.py', [calvalusPointData], [handle], parameters=params, logprefix=handle)
                #======================================================
                vc = 'withoutvc'
                params = ['ma_vc-analysis-ana-' + sensor + '-\${station}-\${vc}.xml',
                          'station', station,
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
