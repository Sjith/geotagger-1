from optparse import OptionParser
from elementtree.ElementTree import Element, SubElement, dump, XML, parse, dump
from bisect import bisect
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
		self.startRanges = []
		self.ranges = {}
		self.options = options
		self.sorted = False
	
	def sort(self):
		if self.sorted:
			return
		self.startRanges.sort()

	def addRange(self, range):
		trace('adding ' + repr(range))
		self.startRanges.append(range.start)
		self.ranges[range.start] = range

	def getRange(self, pictureNumber):
		self.sort()
		trace('trying to get valid range for ' + str(pictureNumber))

		candidatePos = bisect(self.startRanges, pictureNumber)
		if candidatePos == 0:
			trace('not pos found for' + str(pictureNumber))
			raise NotValidRange
		if candidatePos == len(self.startRanges):	# out of last element. Gotta check last one
			candidatePos = candidatePos - 1
			
		candidateStart = self.startRanges[candidatePos]		#candidate start because need to check final element of range
		trace('candidate start for ' + str(pictureNumber) + ' ' + str(candidateStart))
		range = self.ranges[candidateStart]

		if range.isSuitable(pictureNumber) == False:	#checks if candidate range is valid
			trace('not valid range found for' + pictureNumber)
			raise NotValidRange
		return range
	
	def __repr__():
		return 'ranges:' + ranges + ' startranges:' + startRanges

	def isValidPictureFile(self, filename):
		ext = filename.rsplit('.')[-1]
		trace(filename + ' extension is ' + ext)
		if ext.upper() == self.options.filetype.upper():
			trace('is valid file')
			return True
		return False
		

class NotValidRange(Exception):
	pass


def parseArguments():
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
		self.latitude = l
		self.longitude = lo
		self.altitude = al
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
		self.start = start
		self.end = end
		self.position = position
	
	def isSuitable(self, picture):
		if (picture >= self.start) and (picture <= self.end):
			return True
		return False

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

	def __repr__(self):
		return 'start:' + str(self.start) + ' end:' + str(self.end) + ' pos:' + repr(self.position)



numbersRe = re.compile('[0-9]+')	#matches AT LEAST one number

class BasePictureFile:
	def __init__(self, name):
		self.name = name
	def getNumber(self):
		res = numbersRe.search(self.name)
		if res == None:
			trace('get number for ' + self.name + ' ' + 0)
			return 0
		trace('get number for ' + self.name + ' ' + res.group(0))
		return long(res.group(0))


class PyPictureFile(BasePictureFile):
	def writeExif(self, pos):	
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
	def writeExif(self, pos):	
		if sys.platform != 'win32':
			command = 'exiftool -m -overwrite_original -n -GPSLongitude=%f -GPSLatitude=%f \
			-GPSLongitudeRef=%s -GPSLatitudeRef=%s -GPSAltitude=%f "%s"'\
			%(pos.fLong,pos.fLat,pos.longOrientation,pos.latOrientation,pos.fAltitude,self.name)
		else:
			command = 'exiftool.exe -m -overwrite_original -n -GPSLongitude=%f -GPSLatitude=%f \
			-GPSLongitudeRef=%s -GPSLatitudeRef=%s -GPSAltitude=%s "%s"'\
			%(lon,lat,longRef,latRef,alt,self.name)
		trace('Executing ' + command)
		os.popen(command)


try: 
	import pexiv2	
	PictureFile = PyPictureFile
	trace('pexiv2 available')
except:	
	#because after spending three nights I didnt manage to compile pyexiv2 under macosx
	PictureFile = ExToolPictureFile
	trace('pexiv2 not available, using exiftool')




def processXmlFile(filexml, g):
	trace('Processing ' + filexml)
	tree = parse(filexml)
	dump(tree)
	allranges = tree.findall('range')
	for xmlrange in allranges:
		trace('processing xml row')
		#try:
		r = RangePos(xmlrange)
		g.addRange(r)
		#except:
		#	trace('unable to handle:')
		#	if verbose:
		#		dump(xmlrange)


def writeInfoToPictures(g, dir):
	trace('trying to write files in ' + dir)
	for file in listdir(dir):
		try:
			if g.isValidPictureFile(file):
				trace(file + ' is a valid picture')
				p = PictureFile(file)
				n = p.getNumber()	#number from the name of the picture
				if n == 0:
					continue
				r = g.getRange(n)	#range the number belongs to
				trace('Got range ' + repr(r))
				p.writeExif(r.position)

		except NotValidRange:
			trace('Range invalid for ' + file)
		#except:
		#	trace('Could not handle ' + file)
			
	

def run():
	global verbose
	options = parseArguments()
	if options.useExifTool:
		trace('forcing to use exiftool')
		PictureFile = ExToolPictureFile
		
	verbose = options.verbose
	trace('verbose mode on')
	g = GeoTagger(options)
	try:
		processXmlFile(options.geofile, g)
	except:
		print 'Unable to process ' + options.geofile
		return
	
	writeInfoToPictures(g, options.picdir)

run()
