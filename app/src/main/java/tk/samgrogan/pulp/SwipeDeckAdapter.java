package tk.samgrogan.pulp;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by ghost on 11/5/2016.
 */

public class SwipeDeckAdapter extends BaseAdapter {

    private List<Bitmap> data;
    private Context context;

    public SwipeDeckAdapter(List<Bitmap> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if(v == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            // normally use a viewholder
            v = inflater.inflate(R.layout.first_page_display, parent, false);
        }
        ImageView imageView = (ImageView)v.findViewById(R.id.cover_image);
        imageView.setImageBitmap(data.get(position));

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String item = (String)getItem(position);
                //Log.i("MainActivity", item);
            }
        });

        return v;
    }
}