package tk.samgrogan.pulp

import android.graphics.BitmapFactory
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import junrar.Archive
import junrar.exception.RarException
import junrar.rarfile.FileHeader
import java.io.IOException
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View{
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun ZipEntry.isImage(cbz: ZipFile): Boolean {
    var input: InputStream? = null
    val opt = BitmapFactory.Options()
    opt.inJustDecodeBounds = true
    try {
        input = cbz.getInputStream(this)
    } catch (e: IOException) {
        e.printStackTrace()
    }

    BitmapFactory.decodeStream(input, null, opt)
    return opt.outWidth != -1 && opt.outHeight != -1
}

fun FileHeader.isImage(cbr: Archive?): Boolean {
    var input: InputStream? = null
    val opt = BitmapFactory.Options()
    opt.inJustDecodeBounds = true
    try {
        input = cbr?.getInputStream(this)
    } catch (e: IOException) {
        e.printStackTrace()
    } catch (e: RarException) {
        e.printStackTrace()
    }

    BitmapFactory.decodeStream(input, null, opt)
    return opt.outWidth != -1 && opt.outHeight != -1
}