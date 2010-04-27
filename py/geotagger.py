from optparse import OptionParser
from elementtree.ElementTree import Element, SubElement, dump, XML, parse, dump
from bisect import bisect

verbose = False

def l(message):
	if verbose == True:
		print message

class GeoTagger():
	def __init__(self):
		self.startRanges = []
		self.ranges = {}
	def addRange(self, range):
		l('adding ' + repr(range))
		self.startRanges.append(range.start)
		self.ranges[range.start] = range
	def __repr__():
		return 'ranges:' + ranges + ' startranges:' + startRanges

class NotValidRange(Exception):
	pass


def parseArguments():
	parser = OptionParser()
	parser.add_option("-g", "--geotaggerfile", dest="geofile", help="location of geotagger xml", default="geotagger.xml")
	parser.add_option("-d", "--dir", dest="picdir", help="picture directory", default=".")
	parser.add_option("-v", "--verbose", dest="verbose", action="store_true", help="verbose mode on")
	(options, args) = parser.parse_args()
	global verbose
	verbose = options.verbose
	return options

class Position:
	def __init__(self, l, lo, al):
		self.latitude = l
		self.longitude = lo
		self.altitude = al

	def __repr__(self):
		return 'lat:' + self.latitude + ' long:' + self.longitude + ' alt:' +self.altitude

class RangePos:
	def __init__(self, start, end, position):
		self.start = start
		self.end = end
		self.position = position
	
	def isSuitable(picture):
		if picture >= self.start and picture <= self.end:
			return true
		return false

	def __init__(self, xmlrange):
		self.start = xmlrange.attrib['from']
		self.end = xmlrange.attrib['to']
		try:
			lat = xmlrange.attrib['latitude']
			lon = xmlrange.attrib['longitude']
			alt = xmlrange.attrib['altitude']
		except:
			l('some attributes not found')
		self.position = Position(lat, lon, alt)

	def __repr__(self):
		return 'start:' + self.start + ' end:' + self.end + ' pos:' + repr(self.position)

class PictureFile:
	def __init__(self, name):
		self.name = name

	def getRange():
		l('trying to get valid range for ' + self.numberid)
		candidatePos = bisect(startRanges, self.numberid)
		candidateStart = startRanges[candidatePos]
		l('candidate start for ' + self.numberid + ' ' + candidateStart)
		range = ranges[candidateStart]
		if range.isSuitable(self.numberid) == false:
			l('not valid range found for' + self.numberid)
			raise NotValidRange
		return range
			
	def writeExif():	
		#TODO
		pass


def processFile(filexml, g):
	l('Processing ' + filexml)
	tree = parse(filexml)
	dump(tree)
	allranges = tree.findall('range')
	for xmlrange in allranges:
		l('processing xml row')
		try:
			r = RangePos(xmlrange)
			g.addRange(r)
		except:
			l('unable to handle:')
			if verbose:
				dump(xmlrange)

	
if __name__ == "__main__":
	options = parseArguments()
	l('verbose mode on')
	g = GeoTagger()
	processFile(options.geofile, g)

