package tk.samgrogan.pulp.Models;

/**
 * Created by ghost on 3/24/2017.
 */

public class DrawerItem {
    private String mTitle;
    private int mIcon;

    public DrawerItem(){

    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public int getmIcon() {
        return mIcon;
    }

    public void setmIcon() {
        this.mIcon = tk.samgrogan.pulp.R.drawable.ic_menu_slideshow;
    }
}
