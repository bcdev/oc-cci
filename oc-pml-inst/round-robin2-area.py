#!/usr/bin/python -u
# -u for unbuffered

import sys
from pmonitor import PMonitor
from daemon import Daemon

################################################################################
year = '2008'
processors = ['l2gen', 'ocnnrd', 'polymer', "megs"]
regions = ['SPG', 'NA']
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

inputs = ['MER_RR']
hosts = [('localhost', 4)]
types = [('template-step.py', 4)]
################################################################################

class RoundRobin2(Daemon):
    def run(self):
        pm = PMonitor(inputs, request='round-robin2-area', logdir='log', hosts=hosts, types=types)

        for region in regions:
            for processor in processors:
                nameProcess = 'round-robin-process-' + processor + '-' + region
                params = ['round-robin-\${processor}-\${region}-process.xml',
                          'processor', processor,
                          'region', region,
                          'processorBundleName', processorConfig[processor]['processorBundleName'],
                          'processorBundleVersion', processorConfig[processor]['processorBundleVersion'],
                          'processorName', processorConfig[processor]['processorName']
                ]
                pm.execute('template-step.py', inputs, [nameProcess], parameters=params, logprefix=nameProcess)
                if processorConfig[processor]['needsFormatting']:
                    nameFormat = 'round-robin-format-' + processor + '-' + region
                    params = ['round-robin-\${processor}-\${region}-format.xml',
                              'processor', processor,
                              'region', region
                    ]
                    pm.execute('template-step.py', [nameProcess], [nameFormat], parameters=params, logprefix=nameFormat)

        #======================================================
        pm.wait_for_completion()

    #======================================================
daemon = RoundRobin2.setup(sys.argv)
