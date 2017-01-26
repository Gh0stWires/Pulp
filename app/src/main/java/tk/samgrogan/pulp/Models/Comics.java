package tk.samgrogan.pulp.Models;

import android.graphics.Bitmap;

import com.github.junrar.rarfile.FileHeader;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gh0st on 3/6/16.
 */
public class Comics implements Serializable{

    transient List<FileHeader> pageHeaders;
    List<File> filenames = new ArrayList<File>();
    public List<Bitmap> bitmaps = new ArrayList<>();

    public Comics(){

    }

    public void setBitmaps(Bitmap bitmap) {
        this.bitmaps.add(bitmap);
    }

    public List<Bitmap> getBitmaps() {
        return this.bitmaps;
    }

    public void setBitmapList(List<Bitmap> bitmapList){
        this.bitmaps = bitmapList;
    }

    public void setFilenames(File filename) {
        this.filenames.add(filename);
    }

    public File getFilenames(int position) {
        return filenames.get(position);
    }

    public void setPageHeaders(List<FileHeader> pageHeaders) {
        this.pageHeaders = pageHeaders;
    }

    public List<FileHeader> getPageHeaders() {
        return pageHeaders;
    }
}

