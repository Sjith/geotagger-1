from optparse import OptionParser
from elementtree.ElementTree import Element, SubElement, dump, XML, parse, dump
from bisect import bisect_left, bisect
import re
from os import listdir
import os
import os.path
import sys


pictures_ext = []

verbose = False

def trace(message):
    if verbose:
        print message


class InvalidRange(Exception):
    '''Range invalid exception'''


class InvalidNumber(Exception):
    '''File name does not contain a valid number'''


class GeoTagger():
    ''' Global class that contains ranges '''

    def __init__(self, options):
        self._ranges = {}
        self.options = options
        self._start_values = None

    def add_range(self, range):
        ''' Adds the range to the dictionary '''

        trace('adding %s'%(range))
        self._ranges[range.start] = range
        self._start_values = None

    def add_ranges(self, ranges):
        ''' Adds a list of ranges to the dictionary '''
        for r in ranges:
            self.add_range(r)

    def get_range(self, picture_number):
        ''' Returns the range the picture number belongs to '''

        if self._start_values == None:
            self._start_values = self._ranges.keys()
            self._start_values.sort()

        trace('trying to get valid range for %s'%(picture_number))
        trace('start ranges %s'%(self._start_values))

        candidate_pos = bisect(self._start_values, long(picture_number)) - 1
        trace('bisect res %s pos %s value'%(picture_number, candidate_pos)) 

        if candidate_pos == -1:
            trace('not pos found for %s'%(picture_number))
            raise InvalidRange

        candidate_start = self._start_values[candidate_pos]		#candidate start because need to check final element of range
        trace('candidate start for ' + str(picture_number) + ' ' + str(candidate_start))
        range = self._ranges[candidate_start]

        if not range.is_suitable(picture_number):	#checks if candidate range is valid
            trace('not valid range found for' + str(picture_number))
            raise InvalidRange
        return range

    def __repr__():
        return 'ranges:' + ranges + ' startranges:' + startRanges

    def is_valid_picture_file(self, filename):
        ''' returns true if the file has the right extension'''

        ext = filename.rsplit('.')[-1]
        trace(filename + ' extension is ' + ext)
        if ext.upper() == self.options.filetype.upper():
            trace('is valid file')
            return True
        return False



def parse_arguments():
    parser = OptionParser()
    parser.add_option("-g", "--geotaggerfile", dest="geofile", help="location of geotagger xml", default="geotagger.xml")
    parser.add_option("-d", "--dir", dest="picdir", help="picture directory", default=".")
    parser.add_option("-v", "--verbose", dest="verbose", action="store_true", help="verbose mode on")
    parser.add_option("-f", "--fileext", dest="filetype", help="picture file extension", default="jpg")
    parser.add_option("-t", "--exiftool", dest="useExifTool", help="force to use exif tool")
    parser.add_option("-p", "--preserve", dest="preserve", action="store_true", help="preserve original images")
    (options, args) = parser.parse_args()
    return options

class Position:
    def __init__(self, l, lo, al):
        self.latitude, self.longitude, self.altitude = l, lo, al

        self.fLat = float(self.latitude)
        if self.fLat > 0:
            self.latOrientation = 'N'
        else:
            self.fLat = -self.fLat;
            self.latOrientation = 'S'

        self.fLong = float(self.longitude)
        if self.fLong > 0:
            self.longOrientation = 'E'
        else:
            self.fLong = -self.fLong;
            self.longOrientation = 'W'

        self.fAltitude = float(al)

        def __repr__(self):
            return 'lat:' + self.latitude + ' long:' + self.longitude + ' alt:' +self.altitude


class RangePos:
    ''' Range of picture and related position'''

    def __init__(self, start, end, position):
        self.start, self.end, self.position  = start, end, position

    def __init__(self, xmlrange):
        ''' constructor based from xml row'''

        self.start = long(xmlrange.attrib['from'])
        self.end = long(xmlrange.attrib['to'])
        try:
            lat = xmlrange.attrib['latitude']
            lon = xmlrange.attrib['longitude']
            alt = xmlrange.attrib['altitude']
        except:
            trace('some attributes not found')
        self.position = Position(lat, lon, alt)

    def is_suitable(self, picture):
        ''' tells if a picture number is suitable for the selected range '''
        if (picture >= self.start) and (picture <= self.end):
            return True
        return False

    def __repr__(self):
        return 'start:' + str(self.start) + ' end:' + str(self.end) + ' pos:' + repr(self.position)


numbersRe = re.compile('[0-9]+')	#matches AT LEAST one number


class BasePictureFile:
    ''' represents the picture file '''
    def __init__(self, name, dir):
        self._name = name
        self._dir = dir
        self._file_with_path = self._dir + os.path.sep + self._name

    def get_number(self):
        res = numbersRe.search(self._name)
        if res == None:
            raise InvalidNumber
        trace('get number for ' + self._name + ' ' + res.group(0))
        return long(res.group(0))


class PyPictureFile(BasePictureFile):
    ''' override the write method using pyexiv2 library'''
    def write_exif(self, pos, preserve):	
        metadata = pyexiv2.ImageMetadata(self._file_with_path)
        metadata.read()
        tag = metadata['Exif.GPSInfo.GPSLatitudeRef']
        tag.value = pos.latOrientation
        tag = metadata['Exif.GPSInfo.GPSLatitude']
        tag.value = str(pos.fLat)
        tag = metadata['Exif.GPSInfo.GPSLongitudeRef']
        tag.value = pos.longOrientation
        tag = metadata['Exif.GPSInfo.GPSLongitude']
        tag.value = str(pos.fLong)
        metadata.write()


class ExToolPictureFile(BasePictureFile):
    ''' override the write method relaying on external exiftool command '''
    def write_exif(self, pos, preserve):	
        if preserve:
            override_options = ''
        else:
            override_options = '-overwrite_original'

        if sys.platform != 'win32':
            output_file = ''
            command = 'exiftool -m -n %s -GPSLongitude=%f -GPSLatitude=%f \
                       -GPSLongitudeRef=%s -GPSLatitudeRef=%s -GPSAltitude=%f "%s"'\
                       %(override_options, pos.fLong,pos.fLat,pos.longOrientation,pos.latOrientation,pos.fAltitude,self._file_with_path)
        else:
            command = 'exiftool.exe -m  -n %s -GPSLongitude=%f -GPSLatitude=%f \
                       -GPSLongitudeRef=%s -GPSLatitudeRef=%s -GPSAltitude=%f "%s"'\
                       %(override_options, pos.fLong,pos.fLat,pos.longOrientation,pos.latOrientation,pos.fAltitude, self._file_with_path)
        trace('Executing ' + command)
        os.popen(command)


#TODO switch if import fails
PictureFile = ExToolPictureFile

def process_xml_file(filexml, g):
    ''' Processes xml file and stores info in ranges contained in g'''

    trace('Processing ' + filexml)
    tree = parse(filexml)
    allranges = tree.findall('range')
    for xmlrange in allranges:
        trace('processing xml row')
    #try:
        r = RangePos(xmlrange)
        g.add_range(r)
    #except:
    #	trace('unable to handle:')
    #	if verbose:
    #		dump(xmlrange)



def ranges_generator(filexml):
    ''' Processes xml file and stores info in ranges contained in g'''

    trace('Processing ' + filexml)
    tree = parse(filexml)
    allranges = tree.findall('range')
    for xmlrange in allranges:
        trace('processing xml row')
    #try:
        yield RangePos(xmlrange)
    #except:
    #	trace('unable to handle:')
    #	if verbose:
    #		dump(xmlrange)




def write_info_to_pictures(g, dir, status_report):
    '''Writes exif in the pictures stored in dir path'''

    trace('trying to write files in ' + dir)
    picture_files= filter(g.is_valid_picture_file, listdir(dir))
    pictures_num = len(picture_files)

    if not pictures_num:
        status_report('No pictures found in folder')
        return 

    for file_num, file in enumerate(picture_files):
        try:
            status_report('Processing %s, %d of %d'%(file, file_num + 1, pictures_num))
            p = PictureFile(file, dir)
            n = p.get_number()	#number from the name of the picture
            r = g.get_range(n)	#range the number belongs to
            trace('Got range '%(r))
            p.write_exif(r.position, g.options.preserve)
        except InvalidRange:
            trace('Range invalid for %s'%(file))
        except InvalidNumber:
            trace('File %s does not contain a number'%(file))


def load_extensions():
    ext_file = open('extensions.ini')
    [pictures_ext.append(ext[:-1]) for ext in ext_file]     #taking off the \n
    if 'JPG' not in pictures_ext:
        pictures_ext.append('JPG')
    ext_file.close()


def run(options, status_report):
    global verbose # TODO Remove me
    if options.useExifTool:
        trace('forcing to use exiftool')
        PictureFile = ExToolPictureFile

    verbose = options.verbose
    trace('verbose mode on')
    load_extensions()
    g = GeoTagger(options)
    try:
        g.add_ranges(ranges_generator(options.geofile))
    except:
        print 'Unable to process %s'%options.geofile
        return

    write_info_to_pictures(g, options.picdir, status_report)

def command_line_status_rep(status):
    trace(status)

if __name__ == '__main__':
    options = parse_arguments()
    run(options, command_line_status_rep)


