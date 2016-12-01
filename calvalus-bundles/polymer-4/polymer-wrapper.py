import sys
print(sys.path)
from os.path import basename, exists
import os
import traceback

from get_meteo_calvalus import interp_meteo_ozone_files
import input_rectangle_parameter

from polymer.polymer import polymer, Level1
from polymer.level2_nc import Level2_NETCDF
from polymer.ancillary import Ancillary_NASA


def autodetect_sensor(filename):
    if (filename.startswith('MER_RR') or filename.startswith('MER_FR')) and filename.endswith('.N1'):
        return 'meris'
    elif filename.startswith('S3A_OL_1') and filename.endswith('.SEN3'):
        return 'olci'
    elif filename.startswith('V') and '.L1C' in filename:
        return 'viirs'
    elif filename.startswith('A') and '.L1C' in filename:
        return 'modis'
    elif filename.startswith('S') and '.L1C' in filename:
        return 'seawifs'
    elif filename.startswith('S2A_OPER_MSI_L1C'):
        return 'msi'
    else:
        raise Exception('Unable to detect sensor for file "{}"'.format(filename))

def get_sline_eline(sensor):
    (ix, iy, iw, ih) = input_rectangle_parameter.get_input_rect()
    if ix is None:
        return (None, None)
    (px, py, pw, ph) = input_rectangle_parameter.get_product_rect()

    sline = None
    eline = None
    if sensor == 'meris' or sensor == 'seawifs':
        sline = iy
        eline = iy + ih
        print("CALVALUS_PRODUCT_TRANSFORMATION  subset 0, %d, %d, %d" % (iy, pw, ih))
    elif sensor == 'viirs' or sensor == 'modis':
        sline = ph - (iy + ih)
        eline = ph - iy
        print("CALVALUS_PRODUCT_TRANSFORMATION  subset 0, %d, %d, %d flipX flipY" % (iy, pw, ih))
    return (sline, eline)



def read_parameters():
    import json
    parameter_file = 'parameters.json'
    if exists(parameter_file):
        try:
            with open(parameter_file) as data_file:
                return json.load(data_file) or {}
        except ValueError:
            return {}
    return {}


def split_parameters(data):
    params = data
    datasets = None
    if 'datasets' in params:
        datasets = params['datasets']
        del params['datasets']
    return params, datasets


if __name__ == "__main__":
    input_path = sys.argv[1]
    output_path = sys.argv[2]
    try:
        input_filename = basename(input_path)
        sensor = autodetect_sensor(input_filename)

        (meteo_file, ozone_file) = interp_meteo_ozone_files(input_filename)
        if ozone_file is None or meteo_file is None:
            raise Exception('Unable retrieve NCEP ancillary files.')
        print('ozone: ' + ozone_file)
        print('meteo: ' + meteo_file)

        (sline, eline) = get_sline_eline(sensor)

        (params, datasets) = split_parameters(read_parameters())
        params['dir_base'] = 'auxdata-polymer'

        l1_params = {}
        if sensor == 'meris':
            l1_params['dir_smile'] = 'auxdata-polymer/auxdata/meris/smile/v2/'
        l1_params['ancillary'] = Ancillary_NASA(ozone=ozone_file, meteo=meteo_file, directory='.')
        l1_params['filename'] = input_path
        l1_params['sensor'] = sensor

        if sline and eline:
            l1_params['sline'] = sline
            l1_params['eline'] = eline

        level1 = Level1(**l1_params)
        if datasets:
            print("datasets " + str(datasets))
            level2 = Level2_NETCDF(filename=output_path, datasets=datasets)
        else:
            level2 = Level2_NETCDF(filename=output_path)

        print('params: ' + str(params))
        polymer(level1, level2, **params)

        os.remove(meteo_file)
        os.remove(ozone_file)
    except Exception,e:
            print >> sys.stderr, "Exception"
            print >> sys.stderr, e
            print >> sys.stderr, traceback.format_exc()
            sys.exit(1)