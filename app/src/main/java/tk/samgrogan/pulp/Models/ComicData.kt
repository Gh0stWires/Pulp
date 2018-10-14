package tk.samgrogan.pulp.Models

import android.graphics.Bitmap
import junrar.rarfile.FileHeader
import java.io.File

data class ComicData(
        val cover: Bitmap,
        val pageHeader: FileHeader,
        val fileName: File
)