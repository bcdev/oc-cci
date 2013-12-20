#!/usr/bin/python -u
# -u for unbuffered

import sys
from pmonitor import PMonitor
from daemon import Daemon

################################################################################
year = '2008'
processors = ['l2gen', 'ocnnrd', 'polymer', "megs"]
sensors = ['MERIS', 'MODIS', 'SEAWIFS']
sensorEodata = {
    'MERIS': {'root': 'MER_RR__1P/r03', 'suffix': 'N1'},
    'MODIS': {'root': 'MODIS_L1B/OBPG', 'suffix': 'L1B_LAC'},
    'SEAWIFS': {'root': 'SEAWIFS_L1B/OBPG', 'suffix': 'L1B_LAC'}
}
processorConfig = {'l2gen': {'processorBundleName': 'seadas',
                             'processorBundleVersion': '7.0',
                             'processorName': {
                                 'MERIS': 'l2gen',
                                 'MODIS': 'l2gen',
                                 'SEAWIFS': 'l2gen'
                             },
                             'needsFormatting': False},

                   'ocnnrd': {'processorBundleName': 'ocnnrd',
                              'processorBundleVersion': '1.0',
                              'processorName': {
                                  'MERIS': 'ocnnrd-ncep',
                                  'MODIS': 'ocnnrd-ncep',
                                  'SEAWIFS': 'ocnnrd-ncep'
                              },
                              'needsFormatting': True},

                   'polymer': {'processorBundleName': 'polymer',
                               'processorBundleVersion': '3.0',
                               'processorName': {
                                   'MERIS': 'polymerMeris',
                                   'MODIS': 'polymerModis'
                               },
                               'needsFormatting': False},

                   'megs': {'processorBundleName': 'megs',
                            'processorBundleVersion': '8.1',
                            'processorName': {
                                'MERIS': 'megsNormalised'
                            },
                            'needsFormatting': False}
}

pointDataLocal = {
    'MERIS': 'point-data/fake_data.csv',
    'MODIS': 'point-data/fake_data.csv',
    'SEAWIFS': 'point-data/fake_data.csv',
}

inputs = ['point-data/fake_data.csv']
calvalusPointDataRoot  = '/calvalus/projects/oc-rr2/point-data'

namenode = 'master00:9000'

hosts = [('localhost', 10)]
types = []

def basename(p):
    """Returns the final component of a pathname"""
    i = p.rfind('/') + 1
    return p[i:]
################################################################################

class RoundRobin2(Daemon):
    def run(self):
        pm = PMonitor(inputs, request='round-robin2-matchups', logdir='log', hosts=hosts, types=types)

        for sensor in sensors:
            sensorPointData = pointDataLocal[sensor]
            calvalusPointData = calvalusPointDataRoot + '/' + sensor + '-' + basename(sensorPointData)
            pm.execute('ingest-point-data.sh', [sensorPointData], [calvalusPointData], logprefix="ingest-point-data-" +  sensor)

            eodata = sensorEodata[sensor]
            for processor in processors:
                if sensor in processorConfig[processor]['processorName']:
                    params = ['round-robin-\${processor}-\${sensor}-matchup.xml',
                              'sensor', sensor,
                              'processor', processor,
                              'eodataRoot', eodata['root'],
                              'eodataSuffix', eodata['suffix'],
                              'processorBundleName', processorConfig[processor]['processorBundleName'],
                              'processorBundleVersion', processorConfig[processor]['processorBundleVersion'],
                              'processorName', processorConfig[processor]['processorName'][sensor],
                              'pointData', calvalusPointData,
                              'namenode', namenode
                    ]
                    matchupName = 'round-robin-matchup-' + processor + '-' + sensor
                    pm.execute('template-step.py', [calvalusPointData], [matchupName], parameters=params, logprefix=matchupName)

        #======================================================
        pm.wait_for_completion()

        #======================================================


daemon = RoundRobin2.setup(sys.argv)
