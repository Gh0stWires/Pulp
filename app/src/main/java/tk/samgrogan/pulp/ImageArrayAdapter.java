package tk.samgrogan.pulp;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.meetic.dragueur.Direction;
import com.meetic.dragueur.DraggableView;

import java.util.List;

/**
 * Created by Gh0st on 1/16/2016.
 */
public class ImageArrayAdapter extends ArrayAdapter<Bitmap>{

    List<Bitmap> files;
    LayoutInflater mInflater;


    public ImageArrayAdapter(Context context, List<Bitmap> imageUrls){
        super(context, R.layout.first_page_display, imageUrls);
        this.files = imageUrls;




    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = null;


        ViewHolder viewHolder;

        if (customView == null) {
            customView = inflater.inflate(R.layout.first_page_display, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) customView.findViewById(R.id.cover_image);
            viewHolder.draggableView = (DraggableView) customView.findViewById(R.id.drag);
            viewHolder.draggableView.setDragListener(new DraggableView.DraggableViewListener() {
                @Override
                public void onDrag(DraggableView draggableView, float percentX, float percentY) {
                    draggableView.setExitDiration(500);
                    draggableView.setTouchInterceptSensibility(30f);
                }

                @Override
                public void onDraggedStarted(DraggableView draggableView, Direction direction) {

                }

                @Override
                public void onDraggedEnded(DraggableView draggableView, Direction direction) {
                    draggableView.setDraggable(true);
                    draggableView.animateToOrigin(300);

                }

                @Override
                public void onDragCancelled(DraggableView draggableView) {

                }
            });
            customView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) customView.getTag();
        }

        Bitmap singleItem = files.get(position);


        //new ThumbNailTask(viewHolder.imageView).execute(singleItem);
        viewHolder.imageView.setImageBitmap(singleItem);


        return customView;
    }

    /*public class ThumbNailTask extends AsyncTask<Object,Object,Bitmap> {
        WeakReference<ImageView> img;

        public ThumbNailTask(ImageView imageView){
            img = new WeakReference<ImageView>(imageView);
        }


        @Override
        protected Bitmap doInBackground(Object... params) {
            ReadCBR cbr = new ReadCBR();
            cbr.read(params[0].toString());
            cbr.getCbr();
            Bitmap bitmap;
            bitmap = cbr.getPage(1,450);
            cbr.close();

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (img != null && bitmap != null){
                final ImageView imageView = img.get();
                if (imageView != null){
                    imageView.setImageBitmap(bitmap);
                }
            }

        }
    }*/

    public static class ViewHolder {

        public ImageView imageView;
        public DraggableView draggableView;
    }


}
