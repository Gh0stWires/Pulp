package tk.samgrogan.pulp;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by Gh0st on 1/16/2016.
 */
public class ImageArrayAdapter extends RecyclerView.Adapter<ImageArrayAdapter.ViewHolder> {

    List<Bitmap> files;
    LayoutInflater mInflater;


    public ImageArrayAdapter( List<Bitmap> imageUrls){
        this.files = imageUrls;

    }


    @Override
    public ImageArrayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflater = LayoutInflater.from(parent.getContext()).inflate(R.layout.first_page_display,null);
        ViewHolder viewHolder = new ViewHolder(inflater);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bitmap.setImageBitmap(files.get(position));
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView bitmap;

        public ViewHolder(View itemView) {
            super(itemView);
            bitmap = (ImageView) itemView.findViewById(R.id.cover_image);
        }
    }



    @Override
    public int getItemCount() {
        return files.size();
    }



}
