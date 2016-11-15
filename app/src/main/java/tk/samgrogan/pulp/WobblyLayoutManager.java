package tk.samgrogan.pulp;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

/**
 * Created by ghost on 11/5/2016.
 */

public class WobblyLayoutManager extends LinearLayoutManager {

    private boolean isScrollEnabled = true;

    public WobblyLayoutManager(Context context) {
        super(context);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return isScrollEnabled && super.canScrollVertically();
    }
}

