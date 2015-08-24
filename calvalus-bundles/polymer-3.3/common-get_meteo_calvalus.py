#!/usr/bin/env python

'''
Download closest ERA-INTERIM or NCEP + TOMS data
'''

from datetime import datetime, timedelta
from sys import exit, argv, stderr
from os.path import dirname, basename, join, isfile, isdir, exists
import os
import tempfile
from pyhdf.SD import SD, SDC
import pygrib
from numpy import roll, around
import re
import traceback
import socket

#BC
DIR_AUX_ERA='hdfs://calvalus/calvalus/auxiliary/era_interim'
DIR_AUX_NCEP='hdfs://calvalus/calvalus/auxiliary/seadas/anc'
#PML
if socket.gethostname().startswith("calvalus"):
    DIR_AUX_ERA='/data/datasets/ERA_Interim/level3/era_interim-downloaded_20140430/swath/0d'
    DIR_AUX_NCEP='/data/datasets/Sensor/modis/ancillary/metoz'

DIR_TMP='.'

def read_grib(filename, dataset, dt):
    '''
    read dataset in grib file filename
    with date and hour specified by datetime object dt
    '''
    localFilename = copyToLocal(filename)

    hour = dt.hour
    date = int(dt.strftime('%Y%m%d'))

    try:
        g = pygrib.open(localFilename)
    except IOError:
        raise Exception('Could not open grib file %s' % (localFilename))

    data = None
    grblist = []
    for grb in g:
        grblist.append(grb)
        if (grb.name == dataset) and (grb.hour == hour) and (grb.dataDate == date):
            data = grb.values

    g.close()

    if data == None:
        print 'List of datasets in grib file:'
        for grb in grblist:
            print grb
            # for j in sorted(grb.keys()): print '->', j
            # print '===', '"%s"' % (grb.name), grb.dataDate, type(grb.dataDate), grb.hour

        raise Exception('Could not read "%s" at %s in file %s' % (dataset, dt, localFilename))

    return data

def write_hdf(filename, dataset, data):
    '''
    write a dataset in hdf file
    '''

    hdf = SD(filename, SDC.WRITE|SDC.CREATE)

    typ = {
            'int16'   : SDC.INT16,
            'float32' : SDC.FLOAT32,
            'float64' : SDC.FLOAT64,
            }[data.dtype.name]

    sds = hdf.create(dataset, typ, data.shape)
    sds[:] = data[:]
    sds.endaccess()

    hdf.end()



def interp_era_interim(date):
    '''
    Temporal interpolation of era-interim ancillary files.

    INPUT
        date:      date and time of the interpolation (datetime object)
    '''


    #
    # initialization
    #

    # date/time of the bracketing datasets
    d0 = datetime(date.year, date.month, date.day,
            6*int(date.hour/6.))
    d1 = d0 + timedelta(hours=6)

    grib0 = join(DIR_AUX_ERA, str(d0.year), d0.strftime('era_interim_%Y%m%d.grib'))
    grib1 = join(DIR_AUX_ERA, str(d1.year), d1.strftime('era_interim_%Y%m%d.grib'))

    # create TMP files
    #
    file_met = tempfile.mktemp(prefix='MET_NCEP_', suffix='.hdf', dir=DIR_TMP)
    file_oz = tempfile.mktemp(prefix='OZ_TOMS_', suffix='.hdf', dir=DIR_TMP)
    #
    # read and interpolate gribfile
    #

    # interpolation factor
    fact = (date-d0).total_seconds()/(d1-d0).total_seconds()

    for (name1, name2, output, convert, typ) in [
            ('Mean sea level pressure', 'press',
                file_met, lambda x: x/100., 'float32'), # convert from Pa to HPa
            ('10 metre U wind component', 'z_wind',
                file_met, lambda x: x, 'float32'),
            ('10 metre V wind component', 'm_wind',
                file_met, lambda x: x, 'float32'),
            ('Total column ozone', 'ozone',
                file_oz, lambda x: x/2.144e-5, 'int16'), # convert from kg/m2 to DU
            ]:
        data0 = read_grib(grib0, name1, d0)
        data1 = read_grib(grib1, name1, d1)
        (h, w) = data0.shape
        interpolated = roll(convert((1-fact)*data0 + fact*data1)[::-1,:], w/2, axis=1).astype(typ)

        if 'int' in typ:
            interpolated = around(interpolated)
        interpolated = interpolated.astype(typ)

        write_hdf(output, name2, interpolated)

    return (file_met, file_oz)


def write_interpolated(filename, f0, f1, fact, datasets):
    '''
    interpolate two hdf files f0 and f1 using factor fact, and
    write the result to filename
    '''

    hdf = SD(filename, SDC.WRITE|SDC.CREATE)
    for dataset in datasets:

        try:
            info = SD(f0).select(dataset).info()
        except:
            print >> stderr, 'Error loading %s in %s' % (dataset, f0)
            raise

        typ  = info[3]
        shp  = info[2]
        met0 = SD(f0).select(dataset).get()
        met1 = SD(f1).select(dataset).get()

        interp = (1-fact)*met0 + fact*met1

        interp = interp.astype({
                SDC.INT16: 'int16',
                SDC.FLOAT32: 'float32',
                SDC.FLOAT64: 'float64',
            }[typ])

        # write
        sds = hdf.create(dataset, typ, shp)
        sds[:] = interp[:]
        sds.endaccess()

    hdf.end()


def interp_meteo_toms(date):
    '''
    Temporal interpolation of meteo (NCEP) and ozone (TOMS)
    ancillary files.  The interpolated data is written on
    temporary hdf files with automatically generated filename.
    INPUT
        date:      date and time of the interpolation (datetime object)
    RETURN VALUES:
        (file_meteo, file_ozone), where:
        file meteo is the temporary interpolated meteo file
        file ozone is the temporary interpolated ozone file
    '''

    assert isinstance(date, datetime)

    # determine bracketing files
    #
    # 1) ozone
    do0 = datetime(date.year, date.month, date.day)
    do1 = do0 + timedelta(days=1)

    do0year = do0.year
    do1year = do1.year
    do0doy = do0.timetuple().tm_yday
    do1doy = do1.timetuple().tm_yday

    path_oz_0_a = '%s/%s/%03d/N%s%03d00_O3_TOMSOMI_24h.hdf' % (DIR_AUX_NCEP, do0year, do0doy, do0year, do0doy)
    path_oz_1_a = '%s/%s/%03d/N%s%03d00_O3_TOMSOMI_24h.hdf' % (DIR_AUX_NCEP, do1year, do1doy, do1year, do1doy)
    path_oz_0_b = '%s/%d/%03d/S%s%03d00%03d23_TOVS.OZONE' % (DIR_AUX_NCEP, do0year, do0doy, do0year, do0doy, do0doy)
    path_oz_1_b = '%s/%d/%03d/S%s%03d00%03d23_TOVS.OZONE' % (DIR_AUX_NCEP, do1year, do1doy, do1year, do1doy, do1doy)
    path_oz_0_c = '%s/%d/%03d/S%s%03d00%03d23_TOAST.OZONE' % (DIR_AUX_NCEP, do0year, do0doy, do0year, do0doy, do0doy)
    path_oz_1_c = '%s/%d/%03d/S%s%03d00%03d23_TOAST.OZONE' % (DIR_AUX_NCEP, do1year, do1doy, do1year, do1doy, do1doy)

    # 2) meteo
    dm0 = datetime(date.year, date.month, date.day, 6*int(date.hour/6.))
    dm1 = dm0 + timedelta(hours=6)

    dm0year = dm0.year
    dm1year = dm1.year
    dm0doy = dm0.timetuple().tm_yday
    dm1doy = dm1.timetuple().tm_yday

    path_met_s_0 = '%s/%s/%03d/S%s%03d%02d_NCEP.MET' % (DIR_AUX_NCEP, dm0year, dm0doy, dm0year, dm0doy, dm0.hour)
    path_met_s_1 = '%s/%s/%03d/S%s%03d%02d_NCEP.MET' % (DIR_AUX_NCEP, dm1year, dm1doy, dm1year, dm1doy, dm1.hour)
    path_met_n_0 = '%s/%s/%03d/N%s%03d%02d_MET_NCEPR2_6h.hdf' % (DIR_AUX_NCEP, dm0year, dm0doy, dm0year, dm0doy, dm0.hour)
    path_met_n_1 = '%s/%s/%03d/N%s%03d%02d_MET_NCEPR2_6h.hdf' % (DIR_AUX_NCEP, dm1year, dm1doy, dm1year, dm1doy, dm1.hour)

    # download them
    path_met_0 = None
    if existPath(path_met_s_0):
        path_met_0 = copyToLocal(path_met_s_0)
    else:
        if existPath(path_met_n_0):
            path_met_0 = copyToLocal(path_met_n_0)
        
    path_met_1 = None
    if existPath(path_met_s_1):
        path_met_1 = copyToLocal(path_met_s_1)
    else:
        if existPath(path_met_n_1):
            path_met_1 = copyToLocal(path_met_n_1)

    path_oz_0 = None
    if existPath(path_oz_0_a):
        path_oz_0 = copyToLocal(path_oz_0_a)
    else:
        if existPath(path_oz_0_b):
            path_oz_0 = copyToLocal(path_oz_0_b)
        else:
            if existPath(path_oz_0_c):
              path_oz_0 = copyToLocal(path_oz_0_c)

    path_oz_1 = None
    if existPath(path_oz_1_a):
        path_oz_1 = copyToLocal(path_oz_1_a)
    else:
        if existPath(path_oz_1_b):
            path_oz_1 = copyToLocal(path_oz_1_b)
        else:
            if existPath(path_oz_1_c):
              path_oz_1 = copyToLocal(path_oz_1_c)

    # data interpolation factor
    fact_met = (date-dm0).total_seconds()/(dm1-dm0).total_seconds()
    fact_oz  = (date-do0).total_seconds()/(do1-do0).total_seconds()

    # interpolate and write dataset
    if path_met_0 is not None and path_met_1 is not None:
        file_met = tempfile.mktemp(prefix='MET_NCEP_', suffix='.hdf', dir=DIR_TMP)
        write_interpolated(file_met, path_met_0, path_met_1, fact_met, ['z_wind', 'm_wind', 'press'])
    else:
        file_met = None

    if path_oz_0 is not None and path_oz_1 is not None:
        file_oz = tempfile.mktemp(prefix='OZ_TOMS_', suffix='.hdf', dir=DIR_TMP)
        write_interpolated(file_oz, path_oz_0, path_oz_1, fact_oz, ['ozone'])
    else:
        file_oz = None

    return (file_met, file_oz)

def existPath(path):
    print >> stderr, 'testing path: %s' % (path)
    if path.startswith('hdfs'):
        pathExist = not os.system('hadoop fs -ls %s 1>/dev/null 2>/dev/null' % (path))
    else:
        pathExist = exists(path)
    print >> stderr, 'Path exist[%s]: %s' % (pathExist, path)
    return pathExist

def copyToLocal(path):
    if path.startswith('hdfs'):
        if not exists(basename(path)):
            print >> stderr, 'Copy-to-Local ', path
            if os.system('hadoop fs -get %s . 1>/dev/null 2>/dev/null' % (path)):
                print >> stderr, 'Copy-to-Local error, stopping.'
                exit(1)
        else:
            print >> stderr, 'Local-file-exist ', path
        return basename(path)
    else:
        print >> stderr, 'directly using local file', path
        return path


def getDate(filename):
    # MERIS
    # MER_FRS_1PRACR20071224_131046_
    # MER_RR__2PRACR20071224_131046_
    if re.compile("^MER_..._\d.....\d{8}_\d{6}_").match(filename):
        date = datetime.strptime(filename[14:29], '%Y%m%d_%H%M%S')

    # MODIS Aqua
    # A2006131132000.L1B_LAC
    if re.compile("^A\d{13}").match(filename):
        date = datetime.strptime(filename[1:14], '%Y%j%H%M%S')

    # SeaWiFS
    # S2006131132000.L1B_LAC
    if re.compile("^S\d{13}").match(filename):
        date = datetime.strptime(filename[1:14], '%Y%j%H%M%S')

    print >> stderr, date
    return date

def main(argv):
    fname = basename(argv[1])
    date = getDate(fname)

    meteo_type="ERA_INTERIM"
    if len(argv) > 2:
        meteo_type=argv[2]
    try:
        if meteo_type == "ERA_INTERIM":
            print >> stderr, "Using meteo type: " + meteo_type
            (file_met_full, file_oz_full) = interp_era_interim(date)
        elif meteo_type == "NCEP":
            print >> stderr, "Using meteo type: " + meteo_type
            (file_met_full, file_oz_full) = interp_meteo_toms(date)
        else:
            print >> stderr, "Wrong meteo type: " + meteo_type
            exit(1)

        if file_met_full is not None and file_oz_full is not None:
            print 'FILE_METEO ', file_met_full
            print 'FILE_OZONE ', file_oz_full
        else:
            print >> stderr, "Failed to get meteo files"
            print >> stderr, 'FILE_METEO ', file_met_full
            print >> stderr, 'FILE_OZONE ', file_oz_full
            exit(1)
    except Exception,e:
        print >> stderr, "Exception"
        print >> stderr, e
        print >> stderr, traceback.format_exc()
        exit(1)


if __name__ == '__main__':
    print >> stderr, argv
    main(argv)

