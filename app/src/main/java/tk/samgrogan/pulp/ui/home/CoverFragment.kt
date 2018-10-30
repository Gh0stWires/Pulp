package tk.samgrogan.pulp.ui.home

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.cover_fragment.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import swipeable.com.layoutmanager.OnItemSwiped
import swipeable.com.layoutmanager.SwipeableLayoutManager
import swipeable.com.layoutmanager.SwipeableTouchHelperCallback
import swipeable.com.layoutmanager.touchelper.ItemTouchHelper
import tk.samgrogan.pulp.Models.Comics
import tk.samgrogan.pulp.R
import tk.samgrogan.pulp.adapters.SwipeRecyclerViewAdapter
import tk.samgrogan.pulp.data.CoverPage
import tk.samgrogan.pulp.data.ReadCBR
import tk.samgrogan.pulp.data.ReadCBZ
import tk.samgrogan.pulp.recycle
import java.io.File
import java.util.*



/**
 * Created by ghost on 2/12/2017.
 */

class CoverFragment : Fragment() {

    private var bitmaps: MutableList<Bitmap> = mutableListOf()
    private var mContext: Context? = null
    private val filePaths = ArrayList<String>()
    private var coverPages: MutableList<CoverPage> = mutableListOf()
    private val comics = Comics()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.cover_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bitmaps = comics.bitmaps
        mContext = activity

        progress.visibility = View.VISIBLE

        val swipeableTouchHelperCallback = SwipeableTouchHelperCallback(object : OnItemSwiped {


            //Called after swiping view, place to remove top item from your recyclerview adapter
            override fun onItemSwiped() {

            }

            override fun onItemSwipedRight() {
                (coverRecyclerView.adapter as SwipeRecyclerViewAdapter).coverPhotos.recycle()
                (coverRecyclerView.adapter as SwipeRecyclerViewAdapter).notifyDataSetChanged()
                coverPages.recycle()

            }
        })

        val itemTouchHelper = ItemTouchHelper(swipeableTouchHelperCallback as ItemTouchHelper.Callback?)
        itemTouchHelper.attachToRecyclerView(coverRecyclerView)
        coverRecyclerView.layoutManager = SwipeableLayoutManager()
        coverRecyclerView.adapter = SwipeRecyclerViewAdapter(object: MotionLayout.TransitionListener {
            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
                //coverRecyclerView.alpha = 0.5f
                //coverRecyclerView.background = resources.getDrawable(R.color.pulp)
            }

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                (coverRecyclerView.adapter as SwipeRecyclerViewAdapter).coverPhotos.recycle()
                (coverRecyclerView.adapter as SwipeRecyclerViewAdapter).notifyDataSetChanged()
                coverPages.recycle()
                NavHostFragment.findNavController(this@CoverFragment).navigate(CoverFragmentDirections.ComicReadAction(coverPages[0].filename))

            }
            // More code here
        })
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
        //TODO turn this into a viewmodel
        doAsync {
            folder = File(Environment.getExternalStorageDirectory().toString())
            Log.d("path", folder.toString())
            checkFiles(folder, files)
            Log.d("files", files.toString())
            for (i in files.indices) {
                val file = files[i]
                comics.setFilenames(file)

                if (file.name.endsWith(".cbr")) {
                    cbr = ReadCBR(file.toString())
                    if (cbr.cbr != null) {
                        val cache = cbr.getBitmapFile(context, 0)
                        val coverPage = CoverPage(file.absolutePath, cbr.getBitmap(cache))
                        coverPages.add(coverPage)
                    }
                } else {
                    cbz = ReadCBZ(file.toString())
                    val zip = cbz.cbz
                    if (zip != null) {
                        cbz.CbzComic()
                        val coverPage = CoverPage(file.absolutePath, cbz.getPage(0))
                        coverPages.add(coverPage)
                    }
                }
            }
            uiThread {
                //TODO figure out home button invalidation bug
                (coverRecyclerView.adapter as SwipeRecyclerViewAdapter).swap(coverPages)
                progress.visibility = View.GONE


            }
        }
    }
}
