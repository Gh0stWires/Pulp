package tk.samgrogan.pulp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import tk.samgrogan.pulp.ui.reader.PageFragment

class ComicPageAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager) {
    private lateinit var pages: MutableList<Int>

    override fun getItem(position: Int): Fragment {
        return PageFragment.newInstance(pages[position])
    }

    override fun getCount(): Int {
        return pages.size
    }

    fun setPages(pages: MutableList<Int>) {
        this.pages = pages
    }
}