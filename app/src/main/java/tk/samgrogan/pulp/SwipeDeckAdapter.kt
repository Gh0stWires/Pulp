package tk.samgrogan.pulp

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView

/**
 * Created by ghost on 11/5/2016.
 */

class SwipeDeckAdapter(private val data: List<Bitmap>, private val context: Context) : BaseAdapter() {

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): Any {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {

        var v: View? = convertView
        if (v == null) {
            val inflater = LayoutInflater.from(context)
            // normally use a viewholder
            v = inflater.inflate(R.layout.first_page_display, parent, false)
        }
        val imageView = v!!.findViewById<View>(R.id.cover_image) as ImageView
        imageView.setImageBitmap(data[position])

        v.setOnClickListener {
            //String item = (String)getItem(position);
            //Log.i("MainActivity", item);
        }

        return v
    }
}