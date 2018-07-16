package com.marcinmejner.miaumiau.utils

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.marcinmejner.miaumiau.R
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.assist.ImageScaleType
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener

class UniversalImageLoader(val context: Context) {

    private val defaultImage = R.drawable.img_holder


    fun getConfig(): ImageLoaderConfiguration {

        val defaultOtions = DisplayImageOptions.Builder()
                .showImageOnLoading(defaultImage)
                .showImageForEmptyUri(defaultImage)
                .showImageOnFail(defaultImage)
                .considerExifParams(true)
                .cacheOnDisk(true).cacheInMemory(true)
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(FadeInBitmapDisplayer(300))
                .build()

        return ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(defaultOtions)
                .memoryCache(WeakMemoryCache())
                .diskCacheSize(100 * 1024 * 1024).build()
    }

    fun setImage(imageUrl: String?, image: ImageView?, append: String) {
        val imageLoader = ImageLoader.getInstance()
        imageLoader.displayImage(append + imageUrl, image, object : ImageLoadingListener {
            override fun onLoadingStarted(imageUri: String?, view: View) {

            }

            override fun onLoadingFailed(imageUri: String?, view: View, failReason: FailReason) {

            }

            override fun onLoadingComplete(imageUri: String?, view: View, loadedImage: Bitmap?) {

            }

            override fun onLoadingCancelled(imageUri: String?, view: View) {

            }
        })

    }
}

