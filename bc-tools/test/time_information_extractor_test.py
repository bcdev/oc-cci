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
from datetime import datetime

from unittest import TestCase
from main.time_information_extractor import TimeInformationExtractor


class TimeInformationExtractorTest(TestCase):

    def test_time_extraction(self):
        extractor = TimeInformationExtractor()
        startTime, endTime = extractor.extractTimeInformation('MER_RR__2PRACR20071224_131046_000026212064_00296_30410_0000.nc')

        expectedStartDate = datetime(2007, 12, 24, 13, 10, 46)
        expectedEndDate = datetime(2007, 12, 24, 13, 54, 27)

        self.assertEquals(expectedStartDate, startTime)
        self.assertEquals(expectedEndDate, endTime)


    def testTimeExtraction_fail(self):
        extractor = TimeInformationExtractor()
        self.assertRaises(ValueError, lambda : extractor.extractTimeInformation('completely_wrong_but_at_least_long_filename'))