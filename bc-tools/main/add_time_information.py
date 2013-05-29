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
import os
import sys

from main.time_information_adder import TimeInformationAdder

def main():
    tia = TimeInformationAdder()
    if not len(sys.argv) == 2:
        print('Usage:\n    python add_time_information <directory_path>')
        exit(-1)

    print('starting process...')
    directory = sys.argv[1]
    for file in os.listdir(directory):
        current_file = os.path.join(directory, file)
        print("adding time information to '" + current_file + "'.")
        tia.addTimeInformation(current_file)

    print('...done.')
    exit(0)


if __name__ == "__main__":
    main()