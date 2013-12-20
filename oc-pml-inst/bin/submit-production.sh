#!/bin/bash
set -e

# submit-production.sh request.xml 
request=$1

echo "java -jar $OC_PRODUCTION_JAR -e --beam $OC_BEAM_VERSION --calvalus $OC_CALVALUS_VERSION $request"

echo "start $(date --rfc-3339=seconds)"
java -jar $OC_PRODUCTION_JAR -e --beam $OC_BEAM_VERSION --calvalus $OC_CALVALUS_VERSION $request
echo "end   $(date --rfc-3339=seconds)"

