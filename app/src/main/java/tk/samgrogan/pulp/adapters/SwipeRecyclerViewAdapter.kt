package tk.samgrogan.pulp.adapters

import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import tk.samgrogan.pulp.R
import tk.samgrogan.pulp.data.CoverPage
import tk.samgrogan.pulp.inflate

class SwipeRecyclerViewAdapter(val listener: MotionLayout.TransitionListener): RecyclerView.Adapter<SwipeRecyclerViewAdapter.SwipeViewHolder>() {
    var coverPhotos: MutableList<CoverPage> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SwipeViewHolder {
        return SwipeViewHolder(parent.inflate(R.layout.first_page_display))
    }

    override fun getItemCount(): Int {
        return coverPhotos.size
    }

    override fun onBindViewHolder(holder: SwipeViewHolder, position: Int) {
        val coverPage = coverPhotos[position]
        holder.cover.setImageBitmap(coverPage.coverImage)
        holder.anim.setTransitionListener( listener )

    }

    fun swap(coverPhotos: MutableList<CoverPage>) {
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
        internal var anim: MotionLayout = itemView.findViewById(R.id.motion_container)
    }
}

