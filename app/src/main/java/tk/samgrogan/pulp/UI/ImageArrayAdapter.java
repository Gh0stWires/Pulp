package tk.samgrogan.pulp.UI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.List;

import tk.samgrogan.pulp.Data.BaseComic;
import tk.samgrogan.pulp.R;

/**
 * Created by ghost on 2/17/2017.
 */

class ImageArrayAdapter extends ArrayAdapter<BaseComic> {
    public ImageArrayAdapter(Context context, List<BaseComic> imageUrls){
        super(context,R.layout.small_cover,imageUrls);


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.small_cover, parent, false);

        if (customView == null) customView = inflater.inflate(R.layout.small_cover,null);
        BaseComic singleItem = getItem(position);
        ImageView image = (ImageView) customView.findViewById(R.id.cover_images);

        image.setImageBitmap(singleItem.cover);

        if (singleItem.isSelected() == true){
            image.setBackgroundColor(customView.getResources().getColor(R.color.colorPrimary));
        }else {
            image.setBackgroundColor(customView.getResources().getColor(R.color.pulp));
        }


        return customView;
    }
}

