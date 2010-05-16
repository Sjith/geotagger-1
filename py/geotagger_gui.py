from PyQt4 import QtCore, QtGui
import sys
import geotagger_pyui as BuildGui
import geotagger

GEOFILE_NAME = 'xml_file'
DIR_NAME = 'folder'
VERBOSE_NAME = 'verbose'
MUST_USE_EXIF_NAME = 'use exif tool'
FILE_TYPE_NAME = 'file type'

class Options:
    def __init__(self, **params):
        self.geofile = params.get(GEOFILE_NAME, 'geotagger.xml')
        self.picdir = params.get(DIR_NAME, 'geotagger.xml')
        self.verbose = params.get(VERBOSE_NAME, 'geotagger.xml')
        self.useExifTool = params.get(MUST_USE_EXIF_NAME, 'geotagger.xml')
        self.filetype = params.get(FILE_TYPE_NAME, 'geotagger.xml')


class GeotaggerDialog(QtGui.QMainWindow):
    def __init__(self):
        QtGui.QMainWindow.__init__(self)
        self.ui=BuildGui.Ui_Dialog()
        self.ui.setupUi(self)
        self.connect(self.ui.goButton, QtCore.SIGNAL('clicked()'), self.go)
        self.connect(self.ui.chooseFileButton, QtCore.SIGNAL('clicked()'), self.choose_file)
        self.connect(self.ui.chooseDirButton, QtCore.SIGNAL('clicked()'), self.choose_picture_folder)
        self.ui.pictureType.addItems(geotagger.pictures_ext)

    def go(self):
        o = Options()
        o.geofile = str(self.ui.filename.text())
        o.picdir = str(self.ui.picturefolder.text())
        o.filetype = str(self.ui.pictureType.currentText())
        o.useExifTool = self.ui.useExifTool.isChecked()
        o.verbose = self.ui.verbose.isChecked()
        if o.geofile == '' or o.picdir == '':
            QtGui.QMessageBox.question(self, 'Message', "Must set file and picture folder")
        else:
            geotagger.run(o)
            QtGui.QMessageBox.question(self, 'Message', "Pictures geotagged")

    def choose_picture_folder(self):
        dir = QtGui.QFileDialog.getExistingDirectory(self, 'Picture folder', '.')    #TODO scegliere la home in base al sistema operativo 
        self.ui.picturefolder.setText(dir)

    def choose_file(self):
        filename = QtGui.QFileDialog.getOpenFileName(self, 'Get xml file', '.')    #TODO scegliere la home in base al sistema operativo 
        self.ui.filename.setText(filename)

    def reject(self):
        pass

    def accept(self):
        pass


app = QtGui.QApplication(sys.argv)
dialog = GeotaggerDialog()
dialog.show()
app.exec_()


