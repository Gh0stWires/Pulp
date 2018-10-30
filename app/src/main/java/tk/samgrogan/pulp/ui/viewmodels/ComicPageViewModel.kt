package tk.samgrogan.pulp.ui.viewmodels

import androidx.lifecycle.ViewModel
import junrar.rarfile.FileHeader
import tk.samgrogan.pulp.data.ReadCBR
import tk.samgrogan.pulp.data.ReadCBZ
import java.io.File

class ComicPageViewModel: ViewModel() {

    var filesCbr: MutableList<FileHeader>? = mutableListOf()
    var filesCbz: MutableList<String>? = mutableListOf()

    fun getComicPages(filename: String?) {
        var file = File(filename)
        if (file.name.endsWith(".cbr")) {
            val cbr = ReadCBR(file.toString())
            filesCbr = cbr.getHeaders()
        } else {
            val cbz = ReadCBZ(file.toString())
            cbz.CbzComic()
            filesCbz = cbz.pages
        }
    }

    fun isCbr
}