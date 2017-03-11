package tk.samgrogan.pulp.UI;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.List;

import tk.samgrogan.pulp.R;

/**
 * Created by ghost on 2/17/2017.
 */

public class ImageArrayAdapter extends ArrayAdapter<Bitmap> {
    public ImageArrayAdapter(Context context, List<Bitmap> imageUrls){
        super(context,R.layout.small_cover,imageUrls);


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.small_cover, parent, false);

        if (customView == null) customView = inflater.inflate(R.layout.small_cover,null);

        Bitmap singleItem = getItem(position);
        ImageView image = (ImageView) customView.findViewById(R.id.cover_images);

        image.setImageBitmap(singleItem);


        return customView;
    }
}

