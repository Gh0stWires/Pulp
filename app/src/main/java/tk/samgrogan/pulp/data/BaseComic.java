package tk.samgrogan.pulp.data;

import android.graphics.Bitmap;

/**
 * Created by ghost on 2/28/2017.
 */

public class BaseComic {
    private String title;
    public Bitmap cover;
    private boolean selected;

    public BaseComic(String title, Bitmap cover){
        this.title = title;
        this.cover = cover;
        this.selected = false;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getCover() {
        return cover;
    }

    public void setCover(Bitmap cover) {
        this.cover = cover;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


}
