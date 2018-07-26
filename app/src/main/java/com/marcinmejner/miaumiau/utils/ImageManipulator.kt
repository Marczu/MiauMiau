package com.marcinmejner.miaumiau.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.*

class ImageManipulator {

    companion object {
        private val TAG = "ImageManipulator"

        fun getBitmap(imgUrl: String): Bitmap? {

            val imageFile = File(imgUrl)
            var fis: FileInputStream? = null
            var bitmap: Bitmap? = null
            try {
                fis = FileInputStream(imageFile)
                bitmap = BitmapFactory.decodeStream(fis)

            } catch (e: FileNotFoundException) {
                Log.e(TAG, "getBitmap: FileNotFoundException " + e.message)
            } finally {
                try {
                    fis!!.close()
                } catch (e: IOException) {
                    Log.e(TAG, "getBitmap: FileNotFoundException " + e.message)
                }
            }
            return bitmap
        }

        /**
         * Quality from 0 to 100
         *
         * @param bm
         * @param quality
         * @return
         */
        fun getBytesFromBitmap(bm: Bitmap, quality: Int): ByteArray {
            val stream = ByteArrayOutputStream()
            bm.compress(Bitmap.CompressFormat.JPEG, quality, stream)
            return stream.toByteArray()
        }
    }
}