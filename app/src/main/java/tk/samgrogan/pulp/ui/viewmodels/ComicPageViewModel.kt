package tk.samgrogan.pulp.ui.viewmodels

import android.arch.lifecycle.ViewModel
import junrar.rarfile.FileHeader
import tk.samgrogan.pulp.Data.ReadCBR
import tk.samgrogan.pulp.Data.ReadCBZ
import java.io.File

class ComicPageViewModel(val filename: String): ViewModel() {

    var filesCbr: MutableList<FileHeader>? = mutableListOf()
    var filesCbz: MutableList<String>? = mutableListOf()

    fun getComicPages() {
        var file = File(filename)
        if (file.name.endsWith(".cbr")) {
            val cbr = ReadCBR(file.toString())
            filesCbr = cbr.getHeaders()
        } else {
            val cbz = ReadCBZ(file.name)
            cbz.CbzComic()
            filesCbz = cbz.pages
        }
    }
}