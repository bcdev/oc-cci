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
    'MERIS' :   { 'root' : 'MER_RR__1P/r03', 'suffix' : 'N1' },
    'MODIS' :   { 'root' : 'MODIS_L1B/OBPG', 'suffix' : 'L1B_LAC' },
    'SEAWIFS' : { 'root' : 'SEAWIFS_L1B/OBPG', 'suffix' : 'L1B_LAC' }
}
processorConfig = {'l2gen': {'processorBundleName': 'seadas',
                             'processorBundleVersion': '7.0',
                             'processorName': 'l2gen',
                             'needsFormatting': False},

                   'ocnnrd': {'processorBundleName': 'ocnnrd',
                              'processorBundleVersion': '1.0',
                              'processorName': 'ocnnrd-ncep',
                              'needsFormatting': True},

                   'polymer': {'processorBundleName': 'polymer',
                               'processorBundleVersion': '2.7',
                               'processorName': 'polymer',
                               'needsFormatting': False},

                   'megs': {'processorBundleName': 'megs',
                            'processorBundleVersion': '8.1',
                            'processorName': 'megs',
                            'needsFormatting': False}
}

#inputs = ['MER_RR']
hosts = [('localhost', 4)]
types = [('template-step.py', 4)]
################################################################################

class RoundRobin2(Daemon):
    def run(self):
        pm = PMonitor(sensors, request='round-robin2-matchups', logdir='log', hosts=hosts, types=types)

        for sensor in sensors:
            eodata = sensorEodata[sensor]
            for processor in processors:
                nameProcess = 'round-robin-matchup-' + processor + '-' + sensor
                params = ['round-robin-\${processor}-\${sensor}-matchup.xml',
                          'processor', processor,
                          'sensor', sensor,
                          'eodataRoot', eodata['root'],
                          'eodataSuffix', eodata['suffix'],
                          'processorBundleName', processorConfig[processor]['processorBundleName'],
                          'processorBundleVersion', processorConfig[processor]['processorBundleVersion'],
                          'processorName', processorConfig[processor]['processorName']
                ]
                pm.execute('template-step.py', [sensor], [nameProcess], parameters=params, logprefix=nameProcess)

        #======================================================
        pm.wait_for_completion()

    #======================================================
daemon = RoundRobin2.setup(sys.argv)
