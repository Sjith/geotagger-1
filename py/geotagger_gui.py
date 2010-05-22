from PyQt4 import QtCore, QtGui
from PyQt4.QtCore import QThread
import sys
import geotagger_pyui as BuildGui
import geotagger
import os.path
import cPickle

GEOFILE_NAME = 'xml_file'
DIR_NAME = 'folder'
VERBOSE_NAME = 'verbose'
MUST_USE_EXIF_NAME = 'use exif tool'
FILE_TYPE_NAME = 'file type'
OPTIONS_FILE='geotagger.ini'
CHECKED = 2
UNCHECKED = 0

class Options:
    def __init__(self, **params):
        self.geofile = ''
        self.picdir = ''
        self.verbose = params.get(VERBOSE_NAME, True)
        self.useExifTool = params.get(MUST_USE_EXIF_NAME, True)
        self.filetype = params.get(FILE_TYPE_NAME, 'jpg')
        self.index = 0
        self.preserve = False


class TaggerThread(QThread):
    def __init__(self, parent = None):
        QThread.__init__(self, parent)
        self.exiting = False

    def __del__(self):
        self.exiting = True
        self.wait()

    def geotag(self, o):
        ''' here it does the geotagging job '''
        self._options = o
        self.start()

    def run(self):
            geotagger.run(self._options, self.report)

    def report(self, message):
        self.emit(QtCore.SIGNAL("processing(QString)"), message)



class GeotaggerDialog(QtGui.QMainWindow):
    def __init__(self):
        QtGui.QMainWindow.__init__(self)
        self.ui=BuildGui.Ui_Dialog()
        self.ui.setupUi(self)
        self.thread = TaggerThread()

        self.connect(self.ui.goButton, QtCore.SIGNAL('clicked()'), self.go)
        self.connect(self.ui.chooseFileButton, QtCore.SIGNAL('clicked()'), self.choose_file)
        self.connect(self.ui.chooseDirButton, QtCore.SIGNAL('clicked()'), self.choose_picture_folder)
        self.connect(self.thread, QtCore.SIGNAL("finished()"), self.geotagging_report)
        self.connect(self.thread, QtCore.SIGNAL("processing(QString)"), self.ui.status.setText)


        geotagger.load_extensions()
        self.ui.pictureType.addItems(geotagger.pictures_ext)
        try:
            self._path = os.path.expanduser('~')
        except:
            self._path = '.'

        self.load_options()

    def report(self, message):
        print message + 'fava'

    def load_options(self):
        try:
            opt_file = open(OPTIONS_FILE, 'r')
            self._options = cPickle.load(opt_file)
            opt_file.close()
        except:
            self._options = Options()
            return
        self.ui.filename.setText(self._options.geofile)
        self.ui.picturefolder.setText(self._options.picdir)
        self.ui.pictureType.setCurrentIndex(self._options.index)
        if self._options.preserve:
            self.ui.preserveOriginal.setCheckState(CHECKED)
        else:
            self.ui.preserveOriginal.setCheckState(UNCHECKED)

        if self._options.verbose:
            self.ui.verbose.setCheckState(CHECKED)
        else:
            self.ui.verbose.setCheckState(UNCHECKED)

    def fill_options(self):
        self._options.geofile = str(self.ui.filename.text())
        self._options.picdir = str(self.ui.picturefolder.text())
        self._options.filetype = str(self.ui.pictureType.currentText())
        self._options.index = self.ui.pictureType.currentIndex()
        self._options.preserve = self.ui.preserveOriginal.isChecked()
        self._options.verbose = self.ui.verbose.isChecked()

    def go(self):
        self.fill_options()
        if self._options.geofile == '' or self._options.picdir == '':
            QtGui.QMessageBox.question(self, 'Message', "Must set file and picture folder")
        else:
            self.thread.geotag(self._options)

    def geotagging_report(self):
        ''' shows the results of the operation'''
        QtGui.QMessageBox.question(self, 'Message', "Pictures geotagged")

    def save_options(self):
        self.fill_options()
        opt_file = open(OPTIONS_FILE, 'w')
        cPickle.dump(self._options, opt_file)
        opt_file.close()

    def choose_picture_folder(self):
        if self._options.picdir == '':
            folder = self._path
        else:
            folder = self._options.picdir
        dir = QtGui.QFileDialog.getExistingDirectory(self, 'Picture folder', folder)    
        self.ui.picturefolder.setText(dir)

    def choose_file(self):
        if self._options.geofile == '':
            folder = self._path
        else:
            folder = self._options.geofile

        filename = QtGui.QFileDialog.getOpenFileName(self, 'Get xml file', folder)   
        ext = str(filename).rsplit('.')[-1]
        if ext.upper() not in ['XML', 'GPX']:
            QtGui.QMessageBox.question(self, 'Message', "Invalid file type")
        else:
            self.ui.filename.setText(filename)

    def reject(self):
        pass

    def accept(self):
        pass


app = QtGui.QApplication(sys.argv)
dialog = GeotaggerDialog()
dialog.show()
app.exec_()
dialog.save_options()


