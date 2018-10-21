package tk.samgrogan.pulp.Data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import tk.samgrogan.pulp.isImage
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.zip.ZipFile


/**
 * Created by ghost on 1/15/2017.
 */

class ReadCBZ(fileName: String) {

    internal var cbz: ZipFile = ZipFile(File(fileName))
    private var mPages: MutableList<String> = mutableListOf()
    val pages: MutableList<String>
        get() = mPages

    fun close() {
        try {
            cbz.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    //TODO
    fun CbzComic() {

        try {
            val entries = cbz.entries()
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()

                if (entry.isImage(cbz)) {
                    mPages.add(entry.name)
                }

            }
        } catch (e: Exception) {
            Log.e("Error opening file", "error")
        }

    }

    fun getPage(pageNum: Int): Bitmap? {
        var bitmap: Bitmap? = null
        mPages.sort()

        try {
            val entry = cbz.getEntry(mPages[pageNum])
            var input: InputStream? = null
            val opt = BitmapFactory.Options()
            try {
                opt.inJustDecodeBounds = true
                input = cbz.getInputStream(entry)
                BitmapFactory.decodeStream(input, null, opt)
            } finally {
                input?.close()

            }
            input = null

            //opt.inSampleSize = calculateInSampleSize(opt, 600, 600);
            opt.inJustDecodeBounds = false

            try {
                input = cbz.getInputStream(entry)
                bitmap = BitmapFactory.decodeStream(input, null, opt)
            } finally {
                if (false) {
                    input!!.close()
                }
            }


        } catch (e: IOException) {
        }

        return bitmap
    }
}
