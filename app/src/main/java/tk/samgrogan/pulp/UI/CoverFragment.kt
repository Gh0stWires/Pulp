package tk.samgrogan.pulp.UI

import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daprlabs.cardstack.SwipeDeck
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import tk.samgrogan.pulp.Data.ReadCBR
import tk.samgrogan.pulp.Data.ReadCBZ
import tk.samgrogan.pulp.Models.Comics
import tk.samgrogan.pulp.R
import tk.samgrogan.pulp.SwipeDeckAdapter
import java.io.File
import java.util.*

/**
 * Created by ghost on 2/12/2017.
 */

class CoverFragment : Fragment() {

    private var bitmaps: List<Bitmap> = ArrayList()
    private var adapter: SwipeDeckAdapter? = null
    private var mContext: Context? = null
    private val filePaths = ArrayList<String>()
    private val comics = Comics()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_main, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bitmaps = comics.bitmaps
        mContext = activity

        progress.visibility = View.VISIBLE
        adapter = SwipeDeckAdapter(bitmaps, activity)
        getComics()

        test_list.setEventCallback(object : SwipeDeck.SwipeEventCallback {
            override fun cardSwipedLeft(position: Int) {

            }

            override fun cardSwipedRight(position: Int) {
                val fileName = filePaths[position]
                val intent = Intent(activity, ReaderActivity::class.java).putExtra("filename", fileName)
                startActivity(intent)

            }

            override fun cardsDepleted() {
                test_list.setAdapter(adapter!!)
            }

            override fun cardActionDown() {

            }

            override fun cardActionUp() {

            }
        })
    }

    private fun checkFiles(dir: File, files: MutableList<File>) {
        val extensionOne = ".cbr"
        val extensionTwo = ".cbz"
        val fileList = dir.listFiles()
        if (fileList != null) {
            for (i in fileList.indices) {
                if (fileList[i].isDirectory) {
                    //if this is a directory, loop over the files in the directory
                    checkFiles(fileList[i], files)
                } else {
                    if (fileList[i].name.endsWith(extensionOne) || fileList[i].name.endsWith(extensionTwo)) {
                        //this is the file you want, do whatever with it here
                        files.add(fileList[i])
                    }

                }
            }
        }
    }

    fun getComics() {
        lateinit var folder: File
        lateinit var cbr: ReadCBR
        lateinit var cbz: ReadCBZ
        var files: MutableList<File> = ArrayList()

        doAsync {
            cbr = ReadCBR()
            cbz = ReadCBZ()
            //mNewValues = new ContentValues();
            folder = File(Environment.getExternalStorageDirectory().toString())
            Log.d("path", folder.toString())
            checkFiles(folder, files)
            Log.d("files", files.toString())
            for (i in files.indices) {
                //
                val file = files[i]
                comics.setFilenames(file)

                if (file.name.endsWith(".cbr")) {
                    cbr.read(file.toString())
                    cbr.cbr
                    val cache = cbr.getBitmapFile(mContext, 0)
                    comics.setBitmaps(cbr.getBitmap(cache))
                    filePaths.add(file.toString())

                } else {
                    cbz.read(file.toString())
                    val zip = cbz.cbz
                    if (zip != null) {
                        cbz.CbzComic()
                        comics.setBitmaps(cbz.getPage(0))
                        filePaths.add(file.toString())
                    }
                }

                //cbr.close();

            }
            uiThread {
                test_list.setAdapter(adapter)
                progress.visibility = View.GONE

            }
        }
    }
}
