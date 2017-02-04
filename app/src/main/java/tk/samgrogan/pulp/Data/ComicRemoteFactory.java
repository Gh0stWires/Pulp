package tk.samgrogan.pulp.Data;

import android.content.Context;
import android.database.Cursor;
import android.os.Binder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import tk.samgrogan.pulp.R;

/**
 * Created by ghost on 2/2/2017.
 */
public class ComicRemoteFactory implements RemoteViewsService.RemoteViewsFactory {
    Context mContext;
    Cursor mCursor = null;

    public ComicRemoteFactory(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        if (mCursor != null){
            mCursor.close();
        }

        final long identityToken = Binder.clearCallingIdentity();
        mCursor = mContext.getContentResolver().query(ComicProvider.Comics.CONTENT_URI,new String[]{ComicColumns.TITLE, ComicColumns.PAGE},null,null,null);
        Binder.restoreCallingIdentity(identityToken);

    }

    @Override
    public void onDestroy() {
        if (mCursor != null){
            mCursor.close();
        }
    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.list_item_textview);
        //
        if (mCursor.moveToPosition(position)){
            String title = mCursor.getString(mCursor.getColumnIndex(ComicColumns.TITLE));
            views.setTextViewText(R.id.list_item, title.substring(title.lastIndexOf("/") + 1, title.length() - 4).trim());
            views.setTextViewText(R.id.page_num, mCursor.getString(mCursor.getColumnIndex(ComicColumns.PAGE)));
        }
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
