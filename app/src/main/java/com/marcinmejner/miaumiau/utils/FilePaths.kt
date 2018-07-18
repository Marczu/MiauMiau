package com.marcinmejner.miaumiau.utils

import android.os.Environment

object FilePaths {

    //"storage/emulated/0"
    var ROOT_DIR = Environment.getExternalStorageDirectory().path

    var PICRURES = "$ROOT_DIR/Pictures"
    var DOWNLOADS = "$ROOT_DIR/Download"
    var CAMERA = "$ROOT_DIR/DCIM/camera"

    var FIREBASE_IMAGE_STORAGE = "photos/users"

}