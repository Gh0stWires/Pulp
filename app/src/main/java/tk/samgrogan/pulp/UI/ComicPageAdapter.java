package tk.samgrogan.pulp.UI;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import tk.samgrogan.pulp.R;

/**
 * Created by ghost on 11/14/2016.
 */

public class ComicPageAdapter extends PagerAdapter {
    private List<Bitmap> data;
    private Context context;

    public ComicPageAdapter(Context context, List<Bitmap> data){
        this.data = data;
        this.context = context;
    }


    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view ==  object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {


        LayoutInflater inflater = LayoutInflater.from(context);
            // normally use a viewholder
        View layout = (View) inflater.inflate(R.layout.first_page_display, container, false);

        ImageView imageView = (ImageView) layout.findViewById(R.id.cover_image);
        imageView.setImageBitmap(data.get(position));
        container.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((ImageView) view);
    }
}
