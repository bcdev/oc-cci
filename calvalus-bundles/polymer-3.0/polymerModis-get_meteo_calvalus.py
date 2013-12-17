#!/usr/bin/env python

'''
Download closest NCEP + TOMS data
'''

from datetime import datetime, timedelta
from sys import exit, argv, stderr
from os.path import dirname, basename, join, isfile, isdir, exists
import os
import tempfile
from pyhdf.SD import SD, SDC


DIR_AUX='hdfs://master00:9000/calvalus/auxiliary/seadas/anc'
DIR_TMP='.'

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

def existPath(path):
    pathExist = not os.system('hadoop fs -ls %s' % (path))
    print >> stderr, 'path exist', path, pathExist
    return pathExist
    
def copyToLocal(path):
    if not exists(basename(path)):
        print >> stderr, 'Copy-to-Local ', path
        if os.system('hadoop fs -get %s .' % (path)):
            print >> stderr, 'Copy-to-Local error, stopping.'
            exit(1)
    return basename(path)


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

    path_oz_0_a = '%s/%s/%03d/N%s%03d00_O3_TOMSOMI_24h.hdf' % (DIR_AUX, do0year, do0doy, do0year, do0doy)
    path_oz_1_a = '%s/%s/%03d/N%s%03d00_O3_TOMSOMI_24h.hdf' % (DIR_AUX, do1year, do1doy, do1year, do1doy)
    path_oz_0_b = '%s/%d/%03d/S%s%03d00%03d23_TOVS.OZONE' % (DIR_AUX, do0year, do0doy, do0year, do0doy, do0doy)
    path_oz_1_b = '%s/%d/%03d/S%s%03d00%03d23_TOVS.OZONE' % (DIR_AUX, do1year, do1doy, do1year, do1doy, do1doy)
    path_oz_0_c = '%s/%d/%03d/S%s%03d00%03d23_TOAST.OZONE' % (DIR_AUX, do0year, do0doy, do0year, do0doy, do0doy)
    path_oz_1_c = '%s/%d/%03d/S%s%03d00%03d23_TOAST.OZONE' % (DIR_AUX, do1year, do1doy, do1year, do1doy, do1doy)

    # 2) meteo
    dm0 = datetime(date.year, date.month, date.day, 6*int(date.hour/6.))
    dm1 = dm0 + timedelta(hours=6)

    dm0year = dm0.year
    dm1year = dm1.year
    dm0doy = dm0.timetuple().tm_yday
    dm1doy = dm1.timetuple().tm_yday

    path_met_s_0 = '%s/%s/%03d/S%s%03d%02d_NCEP.MET' % (DIR_AUX, dm0year, dm0doy, dm0year, dm0doy, dm0.hour)
    path_met_s_1 = '%s/%s/%03d/S%s%03d%02d_NCEP.MET' % (DIR_AUX, dm1year, dm1doy, dm1year, dm1doy, dm1.hour)
    path_met_n_0 = '%s/%s/%03d/N%s%03d%02d_MET_NCEPN_6h.hdf' % (DIR_AUX, dm0year, dm0doy, dm0year, dm0doy, dm0.hour)
    path_met_n_1 = '%s/%s/%03d/N%s%03d%02d_MET_NCEPN_6h.hdf' % (DIR_AUX, dm1year, dm1doy, dm1year, dm1doy, dm1.hour)

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

def main(argv):
    fname = basename(argv[1])
    # A2006131132000.L1B_LAC
    date = datetime.strptime(fname[1:14], '%Y%j%H%M%S')
    try:
        (file_met_full, file_oz_full) = interp_meteo_toms(date)
        if file_met_full is not None and file_oz_full is not None:
            print 'FILE_METEO ', file_met_full
            print 'FILE_OZONE ', file_oz_full
    except Exception:
        pass


if __name__ == '__main__':
    print >> stderr, argv
    main(argv)

