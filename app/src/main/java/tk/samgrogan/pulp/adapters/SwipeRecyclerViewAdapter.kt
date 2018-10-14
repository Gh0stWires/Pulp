package tk.samgrogan.pulp.adapters

import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import tk.samgrogan.pulp.R
import tk.samgrogan.pulp.inflate

class SwipeRecyclerViewAdapter: RecyclerView.Adapter<SwipeRecyclerViewAdapter.SwipeViewHolder>() {
    private var coverPhotos: MutableList<Bitmap> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SwipeViewHolder {
        return SwipeViewHolder(parent.inflate(R.layout.first_page_display))
    }

    override fun getItemCount(): Int {
        return coverPhotos.size
    }

    override fun onBindViewHolder(holder: SwipeViewHolder, position: Int) {
        val coverImage = coverPhotos[position]
        holder.cover.setImageBitmap(coverImage)
    }

    fun swap(coverPhotos: MutableList<Bitmap>) {
        this.coverPhotos.clear()
        this.coverPhotos.addAll(coverPhotos)
    }

    fun recycleBooks() {
        val firstToBack = coverPhotos[0]
        coverPhotos.removeAt(0)
        coverPhotos.add(firstToBack)
        notifyDataSetChanged()
    }

    inner class SwipeViewHolder internal constructor(view: View): RecyclerView.ViewHolder(view){
        internal var cover: ImageView = itemView.findViewById(R.id.coverImage)
    }
}