# -*- coding: utf-8 -*-

# Form implementation generated from reading ui file 'untitled.ui'
#
# Created: Sat May 22 12:24:30 2010
#      by: PyQt4 UI code generator 4.7.3
#
# WARNING! All changes made in this file will be lost!

from PyQt4 import QtCore, QtGui

class Ui_Dialog(object):
    def setupUi(self, Dialog):
        Dialog.setObjectName("Dialog")
        Dialog.resize(537, 317)
        self.verbose = QtGui.QCheckBox(Dialog)
        self.verbose.setGeometry(QtCore.QRect(370, 50, 87, 20))
        self.verbose.setObjectName("verbose")
        self.preserveOriginal = QtGui.QCheckBox(Dialog)
        self.preserveOriginal.setGeometry(QtCore.QRect(370, 90, 131, 20))
        self.preserveOriginal.setChecked(True)
        self.preserveOriginal.setObjectName("preserveOriginal")
        self.pictureType = QtGui.QComboBox(Dialog)
        self.pictureType.setGeometry(QtCore.QRect(370, 140, 111, 26))
        self.pictureType.setObjectName("pictureType")
        self.goButton = QtGui.QPushButton(Dialog)
        self.goButton.setGeometry(QtCore.QRect(370, 210, 113, 32))
        self.goButton.setObjectName("goButton")
        self.fileName = QtGui.QLabel(Dialog)
        self.fileName.setGeometry(QtCore.QRect(10, 20, 81, 41))
        self.fileName.setObjectName("fileName")
        self.filename = QtGui.QLineEdit(Dialog)
        self.filename.setGeometry(QtCore.QRect(10, 50, 241, 22))
        self.filename.setReadOnly(True)
        self.filename.setObjectName("filename")
        self.label = QtGui.QLabel(Dialog)
        self.label.setGeometry(QtCore.QRect(10, 110, 91, 41))
        self.label.setObjectName("label")
        self.picturefolder = QtGui.QLineEdit(Dialog)
        self.picturefolder.setGeometry(QtCore.QRect(10, 140, 241, 22))
        self.picturefolder.setReadOnly(True)
        self.picturefolder.setObjectName("picturefolder")
        self.label_2 = QtGui.QLabel(Dialog)
        self.label_2.setGeometry(QtCore.QRect(370, 160, 91, 41))
        self.label_2.setObjectName("label_2")
        self.chooseFileButton = QtGui.QPushButton(Dialog)
        self.chooseFileButton.setGeometry(QtCore.QRect(10, 70, 113, 32))
        self.chooseFileButton.setObjectName("chooseFileButton")
        self.chooseDirButton = QtGui.QPushButton(Dialog)
        self.chooseDirButton.setGeometry(QtCore.QRect(10, 160, 113, 32))
        self.chooseDirButton.setObjectName("chooseDirButton")
        self.status = QtGui.QLabel(Dialog)
        self.status.setGeometry(QtCore.QRect(50, 230, 261, 41))
        self.status.setFrameShape(QtGui.QFrame.Box)
        self.status.setText("")
        self.status.setObjectName("status")

        self.retranslateUi(Dialog)
        QtCore.QMetaObject.connectSlotsByName(Dialog)

    def retranslateUi(self, Dialog):
        Dialog.setWindowTitle(QtGui.QApplication.translate("Dialog", "Geotagger", None, QtGui.QApplication.UnicodeUTF8))
        self.verbose.setText(QtGui.QApplication.translate("Dialog", "Verbose", None, QtGui.QApplication.UnicodeUTF8))
        self.preserveOriginal.setText(QtGui.QApplication.translate("Dialog", "Preserve original", None, QtGui.QApplication.UnicodeUTF8))
        self.goButton.setText(QtGui.QApplication.translate("Dialog", "Go", None, QtGui.QApplication.UnicodeUTF8))
        self.fileName.setText(QtGui.QApplication.translate("Dialog", "Xml file", None, QtGui.QApplication.UnicodeUTF8))
        self.label.setText(QtGui.QApplication.translate("Dialog", "Picture folder", None, QtGui.QApplication.UnicodeUTF8))
        self.label_2.setText(QtGui.QApplication.translate("Dialog", "Picture type", None, QtGui.QApplication.UnicodeUTF8))
        self.chooseFileButton.setText(QtGui.QApplication.translate("Dialog", "Choose", None, QtGui.QApplication.UnicodeUTF8))
        self.chooseDirButton.setText(QtGui.QApplication.translate("Dialog", "Choose", None, QtGui.QApplication.UnicodeUTF8))

