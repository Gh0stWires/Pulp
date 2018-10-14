package tk.samgrogan.pulp.UI

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.cover_fragment.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import swipeable.com.layoutmanager.OnItemSwiped
import swipeable.com.layoutmanager.SwipeableLayoutManager
import swipeable.com.layoutmanager.SwipeableTouchHelperCallback
import swipeable.com.layoutmanager.touchelper.ItemTouchHelper
import tk.samgrogan.pulp.Data.ReadCBR
import tk.samgrogan.pulp.Data.ReadCBZ
import tk.samgrogan.pulp.Models.Comics
import tk.samgrogan.pulp.R
import tk.samgrogan.pulp.adapters.SwipeRecyclerViewAdapter
import java.io.File
import java.util.*



/**
 * Created by ghost on 2/12/2017.
 */

class CoverFragment : Fragment() {

    private var bitmaps: MutableList<Bitmap> = mutableListOf()
    private var mContext: Context? = null
    private val filePaths = ArrayList<String>()
    private val comics = Comics()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.cover_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bitmaps = comics.bitmaps
        mContext = activity

        progress.visibility = View.VISIBLE

        val swipeableTouchHelperCallback = SwipeableTouchHelperCallback(object : OnItemSwiped {
            override fun onItemSwipedDown() {
            }

            override fun onItemSwipedUp() {
            }

            //Called after swiping view, place to remove top item from your recyclerview adapter
            override fun onItemSwiped() {
                (coverRecyclerView.adapter as SwipeRecyclerViewAdapter).recycleBooks()
            }

            override fun onItemSwipedLeft() {

            }

            override fun onItemSwipedRight() {

            }
        })
        val itemTouchHelper = ItemTouchHelper(swipeableTouchHelperCallback as ItemTouchHelper.Callback?)
        itemTouchHelper.attachToRecyclerView(coverRecyclerView)
        coverRecyclerView.layoutManager = SwipeableLayoutManager()
        coverRecyclerView.adapter = SwipeRecyclerViewAdapter()
        getComics()
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

    private fun getComics() {
        lateinit var folder: File
        lateinit var cbr: ReadCBR
        lateinit var cbz: ReadCBZ
        val files: MutableList<File> = mutableListOf()

        doAsync {
            cbr = ReadCBR()
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
                    cbz = ReadCBZ(file.toString())
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
                //TODO figure out home button invalidation bug
                (coverRecyclerView.adapter as SwipeRecyclerViewAdapter).swap(bitmaps)
                progress.visibility = View.GONE

            }
        }
    }
}
