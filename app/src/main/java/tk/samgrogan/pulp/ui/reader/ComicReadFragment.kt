package tk.samgrogan.pulp.ui.reader


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import tk.samgrogan.pulp.R
import tk.samgrogan.pulp.ui.viewmodels.ComicPageViewModel


class ComicReadFragment : Fragment() {
    private lateinit var comicPageViewModel: ComicPageViewModel
    private var fileName: String? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comic_reader, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        comicPageViewModel = ViewModelProviders.of(this).get(ComicPageViewModel::class.java)
        fileName = arguments?.getString("filename")
        comicPageViewModel.getComicPages(fileName)
    }

}
