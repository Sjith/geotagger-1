from optparse import OptionParser
from elementtree.ElementTree import Element, SubElement, dump, XML, parse, dump
from bisect import bisect_left, bisect
import re
from os import listdir
import os
import sys


verbose = False

def trace(message):
    if verbose == True:
        print message

class GeoTagger():
    def __init__(self, options):
        self._ranges = {}
        self.options = options
        self._initial_values = None

    def add_range(self, range):
        ''' Adds the range to the dictionary '''

        trace('adding %s'%(range))
        self._ranges[range.start] = range
        self._initial_values = None

    def add_ranges(self, ranges):
        for r in ranges:
            self._ranges[r.start] = r 
            self._initial_values = None

    def get_range(self, picture_number):
        ''' Returns the range the picture number belongs to '''

        if self._initial_values == None:
            trace('fava')
            self._initial_values = self._ranges.keys()
            self._initial_values.sort()

        trace('trying to get valid range for %s'%(picture_number))
        trace('start ranges %s'%(self._initial_values))

        candidate_pos = bisect(self._initial_values, long(picture_number)) - 1
        trace('bisect res %s pos %s value'%(picture_number, candidate_pos)) 

        if candidate_pos == -1:
            trace('not pos found for %s'%(picture_number))
            raise InvalidRange

        candidate_start = self._initial_values[candidate_pos]		#candidate start because need to check final element of range
        trace('candidate start for ' + str(picture_number) + ' ' + str(candidate_start))
        range = self._ranges[candidate_start]

        if range.is_suitable(picture_number):	#checks if candidate range is valid
            trace('not valid range found for' + str(picture_number))
            raise InvalidRange
        return range

    def __repr__():
        return 'ranges:' + ranges + ' startranges:' + startRanges

    def is_valid_picture_file(self, filename):
        ext = filename.rsplit('.')[-1]
        trace(filename + ' extension is ' + ext)
        if ext.upper() == self.options.filetype.upper():
            trace('is valid file')
            return True
        return False


class InvalidRange(Exception):
    '''Range invalid exception'''

class InvalidNumber(Exception):
    '''File name does not contain a valid number'''


def parse_arguments():
    parser = OptionParser()
    parser.add_option("-g", "--geotaggerfile", dest="geofile", help="location of geotagger xml", default="geotagger.xml")
    parser.add_option("-d", "--dir", dest="picdir", help="picture directory", default=".")
    parser.add_option("-v", "--verbose", dest="verbose", action="store_true", help="verbose mode on")
    parser.add_option("-f", "--fileext", dest="filetype", help="picture file extension", default="jpg")
    parser.add_option("-t", "--exiftool", dest="useExifTool", help="force to use exif tool")
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
            self.latOrientation = 'N'

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
    def __init__(self, start, end, position):
        self.start, self.end, self.position  = start, end, position

    def __init__(self, xmlrange):
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
        if (picture >= self.start) and (picture <= self.end):
            return True
        return False

    def __repr__(self):
        return 'start:' + str(self.start) + ' end:' + str(self.end) + ' pos:' + repr(self.position)


numbersRe = re.compile('[0-9]+')	#matches AT LEAST one number


class BasePictureFile:
    def __init__(self, name):
        self.name = name

    def get_number(self):
        res = numbersRe.search(self.name)
        if res == None:
            raise InvalidNumber
        trace('get number for ' + self.name + ' ' + res.group(0))
        return long(res.group(0))


class PyPictureFile(BasePictureFile):
    def write_exif(self, pos):	
        metadata = pyexiv2.ImageMetadata(self.name)
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
    def write_exif(self, pos):	
        if sys.platform != 'win32':
            command = 'exiftool -m -overwrite_original -n -GPSLongitude=%f -GPSLatitude=%f \
                       -GPSLongitudeRef=%s -GPSLatitudeRef=%s -GPSAltitude=%f "%s"'\
                       %(pos.fLong,pos.fLat,pos.longOrientation,pos.latOrientation,pos.fAltitude,self.name)
        else:
            command = 'exiftool.exe -m -overwrite_original -n -GPSLongitude=%f -GPSLatitude=%f \
                       -GPSLongitudeRef=%s -GPSLatitudeRef=%s -GPSAltitude=%f "%s"'\
                       %(pos.fLong,pos.fLat,pos.longOrientation,pos.latOrientation,pos.fAltitude,self.name)
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




def write_info_to_pictures(g, dir):
    '''Writes exif in the pictures stored in dir path'''

    trace('trying to write files in ' + dir)
    picture_files= filter(g.is_valid_picture_file, listdir(dir))

    for file in picture_files:
        try:
            p = PictureFile(file)
            n = p.get_number()	#number from the name of the picture
            r = g.get_range(n)	#range the number belongs to
            trace('Got range ' + repr(r))
            p.write_exif(r.position)
        except InvalidRange:
            trace('Range invalid for %s'%(file))
        except InvalidNumber:
            trace('File %s does not contain a number'%(file))



def run():
    global verbose
    options = parse_arguments()
    if options.useExifTool:
        trace('forcing to use exiftool')
        PictureFile = ExToolPictureFile

    verbose = options.verbose
    trace('verbose mode on')
    g = GeoTagger(options)
    try:
        #process_xml_file(options.geofile, g)
        g.add_ranges(ranges_generator(options.geofile))
    except:
        print 'Unable to process %s'%options.geofile
        return

    write_info_to_pictures(g, options.picdir)

run()
