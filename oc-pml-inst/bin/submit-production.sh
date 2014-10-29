#!/bin/bash
set -e

# submit-production.sh request.xml 
request=$1

if [[ -v OC_BEAM_VERSION ]]; then
  beam_version="--beam ${OC_BEAM_VERSION}"
fi
if [[ -v OC_CALVALUS_VERSION ]]; then
  calvalus_version="--calvalus ${OC_CALVALUS_VERSION}"
fi
if [[ -v OC_JAVA ]]; then
  java_executable="${OC_JAVA}"
else
  java_executable="java"
fi
command="${java_executable} -jar ${OC_PRODUCTION_JAR} -e ${beam_version} ${calvalus_version} $request"

echo ${command}
echo "start $(date --rfc-3339=seconds)"
${command}
echo "end   $(date --rfc-3339=seconds)"

