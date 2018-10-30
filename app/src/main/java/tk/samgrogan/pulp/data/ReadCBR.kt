package tk.samgrogan.pulp.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import junrar.Archive
import junrar.exception.RarException
import junrar.rarfile.FileHeader
import tk.samgrogan.pulp.isImage
import java.io.*
import java.util.*

/**
 * Created by gh0st on 2/25/16.
 */
class ReadCBR(filename: String) {
    
    private var mPages = mutableListOf<Any>()
    internal var isError = false
    internal var cbr: Archive? = try {
        Archive(File(filename))
    } catch (e: NullPointerException) {
        error(true)
        null
    }

    fun checkExists(): Boolean {
        return try {
            cbr = Archive(File(getmFileName()))
            true
        } catch (ex: RarException) {
            false
        }catch (ex: IOException) {
            false
        }
    }

    fun getHeaders(): MutableList<FileHeader>? {
        val fileHeaders = cbr?.fileHeaders
        Collections.sort(fileHeaders, Comp())
        return fileHeaders
    }


    fun error(boolean: Boolean) {
        isError = boolean
    }
    /*val pages: List<FileHeader>
        get() = mPages = headers*/

    fun getBitmapFile(context: Context?, pageNum: Int): File? {
        val files = getHeaders()
        val uri = getmFileName()
        var file: FileOutputStream? = null
        var c: File? = null
        try {

            val fileName = Uri.parse(uri).lastPathSegment
            c = File(context?.cacheDir.toString() + fileName)
            file = FileOutputStream(c)
            if (files != null) {
                if (files.get(pageNum).isImage(cbr)) {
                    cbr?.extractFile(files.get(pageNum), file)
                    file.close()
                } else {
                    cbr?.extractFile(files[1], file)
                    file.close()
                }
            }


        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: RarException) {
            e.printStackTrace()
        }

        return c
    }

    fun getBitmap(cacheBitmap: File?): Bitmap? {
        var bitmap: Bitmap? = null

        try {
            var input: FileInputStream? = null
            val opt = BitmapFactory.Options()
            //List<FileHeader> files = getHeaders();
            //cbr.extractFile();
            //List<FileHeader> files = getHeaders();

            try {
                opt.inJustDecodeBounds = true
                input = FileInputStream(cacheBitmap)
                BitmapFactory.decodeStream(input, null, opt)
            } finally {
                input?.close()

            }
            input = null

            //int scale = (maxLength <= 0) ? 1 : Math.max(opt.outWidth, opt.outHeight) / maxLength;

            opt.inSampleSize = calculateInSampleSize(opt)
            opt.inJustDecodeBounds = false


            try {
                input = FileInputStream(cacheBitmap)
                bitmap = BitmapFactory.decodeStream(input, null, opt)
            } finally {
                if (false) {
                    input!!.close()
                }

            }


        } catch (e: IOException) {
            Log.e("Error loading bitmap", e.toString())
        }

        return bitmap

    }

    private fun getmFileName(): String {
        return cbr.toString()
    }

    class Comp : Comparator<FileHeader> {

        override fun compare(lhs: FileHeader, rhs: FileHeader): Int {
            return lhs.fileNameString.compareTo(rhs.fileNameString)
        }
    }

    companion object {


        fun calculateInSampleSize(
                options: BitmapFactory.Options): Int {
            // Raw height and width of image
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1

            if (height > 600 || width > 600) {

                val halfHeight = height / 2
                val halfWidth = width / 2

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while (halfHeight / inSampleSize > 600 && halfWidth / inSampleSize > 600) {
                    inSampleSize *= 2
                }
            }
            return inSampleSize
        }
    }


}
