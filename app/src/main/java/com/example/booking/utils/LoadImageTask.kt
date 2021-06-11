package com.example.booking.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.ImageView
import java.io.InputStream
import java.net.URL


class LoadImageTask(private var bmImage: ImageView) : AsyncTask<String?, Void?, Bitmap?>() {
    override fun doInBackground(vararg params: String?): Bitmap? {
            val url = params[0]
            var mIcon11: Bitmap? = null
            try {
                val stream: InputStream = URL(url).openStream()
                mIcon11 = BitmapFactory.decodeStream(stream)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return mIcon11
    }

    override fun onPostExecute(result: Bitmap?) {
        bmImage.setImageBitmap(result)
    }
}