package tk.samgrogan.pulp.Data;

import android.graphics.Bitmap;

/**
 * Created by ghost on 2/28/2017.
 */

public class BaseComic {
    public String title;
    public Bitmap cover;

    public BaseComic(String title, Bitmap cover){
        this.title = title;
        this.cover = cover;
    }
}
