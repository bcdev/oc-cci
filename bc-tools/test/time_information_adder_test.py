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
import datetime
import os

from unittest import TestCase
from main.time_information_adder import TimeInformationAdder
from netCDF4 import Dataset
from time import strptime, mktime


class TimeInformationAdderTest(TestCase):

    def test_add_time_information(self):

        path = os.path.join(os.getcwd(), "test", "MER_RR__2PRACR20071224_131046_000026212064_00296_30410_0000.nc")
        try:
            dataset = Dataset(path, 'r')
            self.assertFalse(hasattr(dataset, 'start_time'))
            self.assertFalse(hasattr(dataset, 'stop_time'))

            dataset.close()

            timeInformationAdder = TimeInformationAdder()
            timeInformationAdder.addTimeInformation(path)

            dataset = Dataset(path, 'r')

            self.assertTrue(hasattr(dataset, 'start_time'))
            self.assertTrue(hasattr(dataset, 'stop_time'))

            start_time = datetime.datetime.fromtimestamp(mktime(strptime(dataset.start_time, '%Y-%m-%d %H:%M:%S')))
            stop_time = datetime.datetime.fromtimestamp(mktime(strptime(dataset.stop_time, '%Y-%m-%d %H:%M:%S')))

            expectedStartDate = datetime.datetime(2007, 12, 24, 13, 10, 46)
            expectedEndDate = datetime.datetime(2007, 12, 24, 13, 54, 27)

            self.assertEquals(expectedStartDate, start_time)
            self.assertEquals(expectedEndDate, stop_time)

            dataset.close()

        finally:
           dataset = Dataset(path, mode='a')
           del dataset.start_time
           del dataset.stop_time
           dataset.close()