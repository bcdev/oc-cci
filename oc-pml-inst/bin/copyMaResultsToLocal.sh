#!/bin/bash
set -e

# copyMaResultsToLocal.sh  /calvalus/data/path localDirecoryName hanldeIn handleOut
calvalusPath=$1
localDirecoryName=$2

echo "calvalusPath      ${calvalusPath}"
echo "localDirecoryName ${localDirecoryName}"

resultDir=${OC_INST}/results

mkdir -pv ${resultDir}/${localDirecoryName}
hadoop fs -get ${calvalusPath}/* ${resultDir}/${localDirecoryName}/

rm -rfv ${resultDir}/${localDirecoryName}/_*
rm -rfv ${resultDir}/${localDirecoryName}/part*

tar cvzf ${resultDir}/${localDirecoryName}.tar.gz ${resultDir}/${localDirecoryName}/*
