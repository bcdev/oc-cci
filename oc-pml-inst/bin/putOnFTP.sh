#!/bin/bash
set -e

# putOnFTP.sh  ftp.brockmann-consult.de oc-cci localFile ftpDirectory hanldeIn handleOut

ftphost=$1
ftpuser=$2
localFile=$3
ftpDirectory=$4


echo "ftphost      ${ftphost}"
echo "ftpuser      ${ftpuser}"
echo "localFile    ${localFile}"
echo "ftpDirectory ${ftpDirectory}"

resultDir=${OC_INST}/results


echo lftp -c "open ftp://${ftpuser}@${ftphost};mkdir -p ${ftpDirectory};cd ${ftpDirectory};lcd ${resultDir};put ${localFile}"
echo
lftp -c "open ftp://${ftpuser}@${ftphost};mkdir -p ${ftpDirectory};cd ${ftpDirectory};lcd ${resultDir};put ${localFile}"