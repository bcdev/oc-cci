# Copyright (C) 2013 Brockmann Consult GmbH (info@brockmann-consult.de)
# 
# This program is free software; you can redistribute it and/or modify it
# under the terms of the GNU General Public License as published by the Free
# Software Foundation; either version 3 of the License, or (at your option)
# any later version.
# This program is distributed in the hope that it will be useful, but WITHOUT
# ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
# FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
# more details.
# 
# You should have received a copy of the GNU General Public License along
# with this program; if not, see http://www.gnu.org/licenses/gpl.html
from netCDF4 import Dataset
import os
from main.time_information_extractor import TimeInformationExtractor


class TimeInformationAdder(object):

    def addTimeInformation(self, filepath):
        filename = os.path.basename(filepath)
        try:
            start_time, stop_time = TimeInformationExtractor().extractTimeInformation(filename)
        except ValueError:
            print("Unable to parse time information from filename '" + filename + "'. Skipping file.")

        dataset = Dataset(filepath, mode='a')

        pattern = '%Y-%m-%d %H:%M:%S'
        dataset.start_time = start_time.strftime(pattern)
        dataset.stop_time = stop_time.strftime(pattern)

        dataset.close()