#!/bin/bash
set -e

# ingest-point-data.sh data/MERIS.csv /calvalus/projects/oc-rr2/point-data/MERIS.csv
input=$1
output=$2

if hadoop fs -ls $output > /dev/null 2>&1; then
    echo "File already exits, removing"
    hadoop fs -rm $output
else
    parentDir=$(dirname $output)
    if hadoop fs -ls $parentDir > /dev/null 2>&1; then
        echo "Parent dir already exits"
    else
        echo "creating parent directory: $parentDir"
        hadoop fs -mkdir $(dirname $output)
    fi
fi
echo "copying point-data $input --> $output"
hadoop fs -put $input $output
