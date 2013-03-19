#!/usr/bin/python
# template-step.py template [param value] [param2 value2] input output

import sys
import os
import os.path
from string import Template
import subprocess

BASE_DIR = os.environ['OC_INST']

def generateRequest(templateFileName, requestFileName, keywords):
    with open(templateFileName, 'r') as templateFile:
        template_data = templateFile.read()
    templateFile.closed

    content = Template(template_data).safe_substitute(keywords)

    with open(requestFileName, 'w') as requestFile:
        requestFile.write(content + '\n')
    requestFile.closed

def main(templateName, parameters):
    parameterDict = {}
    for key, value in zip(parameters[0::2], parameters[1::2]):
      parameterDict[key] = value
    requestName = Template(templateName).safe_substitute(parameterDict)
    templateFileName = BASE_DIR + '/etc/' + templateName
    requestFileName = BASE_DIR + '/requests/' + requestName
    generateRequest(templateFileName, requestFileName, parameterDict)
    return subprocess.call(['submit-production.sh', requestFileName])

if __name__ == "__main__":
    if len(sys.argv) < 3:
      print 'Usage: template-step.py template [param value] [param2 value2] input output'
      sys.exit(1)
    sys.exit(main(sys.argv[1], sys.argv[2:len(sys.argv)-2]))