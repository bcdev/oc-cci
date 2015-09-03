
POLYMER=./polymer-3.3

function parameter_defaults() {
    minimumsize=100
    actualsize=$(du -b parameters.txt | cut -f 1)
    if [ $actualsize -lt $minimumsize ]; then
        echo parameters.txt is too small, replacing with defaults
        cp default-parameters.txt parameters.txt
    fi
    # append newline
    echo >> parameters.txt
}

function handle_progress() {
    line=$1
    echo $line
    if [[ ${line} =~ Processing\ block\ ([0-9]+)x([0-9]+)\ /\ ([0-9]+)x([0-9]+) ]]; then
        px=${BASH_REMATCH[1]}
        py=${BASH_REMATCH[2]}
        nx=${BASH_REMATCH[3]}
        ny=${BASH_REMATCH[4]}

        # y is inner loop, x is outer loop
        progress=$(echo "scale=3; (${py} - 1 + (${px} - 1) * ${ny}) / (${ny} * ${nx})" | bc)
        printf "CALVALUS_PROGRESS %.3f\n" $progress
    fi
}

function process() {

    local inFile="${1}"
    local inFileName="$(basename ${inFile})"

    # create links to auxdata
    ln -sf ${POLYMER}/auxdata .
    ln -sf ${POLYMER}/LUTS .

    suffix="hdf"
    if grep -E "OUTPUT_FORMAT\ +NETCDF" parameters.txt; then
        suffix="nc"
    fi

    meteo_type="ERA_INTERIM"
    if grep -E "AUXDATA\ +NCEP" parameters.txt; then
        meteo_type="NCEP"
    fi

    POLY_PYTHON=./pyhdf
    if [ -d ${POLY_PYTHON} ]; then
        export LD_LIBRARY_PATH="${POLY_PYTHON}/pylib"
        export PYTHONPATH="${POLY_PYTHON}/pyshared"
    fi
    if [ ! -d "${HOME}" ]; then
        export HOME=/tmp/
    fi
    ./get_meteo_calvalus.py "${inFileName}" "${meteo_type}" >> parameters.txt

    outFile=${inFileName}.polymer.${suffix}
    # append input,output to parameters.txt
    echo INPUT_FILE "$inFile" >> parameters.txt
    echo OUTPUT_FILE "$outFile" >> parameters.txt

    set -o pipefail
    ${POLYMER}/polymer parameters.txt | while read x ; do handle_progress "$x" ; done
    echo CALVALUS_OUTPUT_PRODUCT "${outFile}"
}

function get_geo() {
    local inPath="${1}"
    local inFileName="${2}"

    # MODIS needs geofile
    echo "getting GEO file"
    ./getGEO.sh "${inPath}"

    # polymer expects a concrete name for the GEO file
    geoFileArchive=${inFileName:0:14}.GEO
    geoFileLocal=${inFileName%L1B_LAC}GEO
    if [ -e "${geoFileArchive}" ];then
        if [ ! -e "${geoFileLocal}" ];then
            ln -vs "${geoFileArchive}" "${geoFileLocal}"
        fi
    fi
}

