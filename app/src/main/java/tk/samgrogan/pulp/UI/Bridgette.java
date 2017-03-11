package tk.samgrogan.pulp.UI;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tk.samgrogan.pulp.Data.BaseComic;
import tk.samgrogan.pulp.Data.ComicColumns;
import tk.samgrogan.pulp.Data.ComicProvider;
import tk.samgrogan.pulp.Data.ReadCBR;
import tk.samgrogan.pulp.Data.ReadCBZ;
import tk.samgrogan.pulp.Models.Comics;
import tk.samgrogan.pulp.R;

public class Bridgette extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    View view;
    GridView pages;
    ImageArrayAdapter adapter;
    List<Bitmap> bitmaps = new ArrayList<Bitmap>();
    List<BaseComic> baseComicList;
    Cursor mCursor;

    private static final int CURSOR_LOADER_ID = 0;
    Comics comics = new Comics();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bridgette, container, false);
        bitmaps = comics.getBitmaps();
        adapter = new ImageArrayAdapter(view.getContext(),bitmaps);
        new ThumbNailTask().execute();
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

        pages = (GridView) view.findViewById(R.id.selector_grid);

        pages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(view.getContext(), ComicProvider.Comics.CONTENT_URI,new String[]{ComicColumns.TITLE},null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }



    public class ThumbNailTask extends AsyncTask<Object,Object,Bitmap> {
        File folder;
        ReadCBR cbr;
        ReadCBZ cbz;
        List<File> files = new ArrayList<>();

        private void checkFiles(File dir, List<File> files) {
            String extension = ".cbr";
            File[] fileList = dir.listFiles();
            if (fileList != null) {
                for (int i = 0; i < fileList.length; i++) {
                    if (fileList[i].isDirectory()) {
                        //if this is a directory, loop over the files in the directory
                        checkFiles(fileList[i], files);
                    } else {
                        if (fileList[i].getName().endsWith(extension)) {
                            //this is the file you want, do whatever with it here
                            files.add(fileList[i]);
                        }

                    }
                }
            }
        }

        @Override
        protected Bitmap doInBackground(Object... params) {
            cbr = new ReadCBR();
            cbz = new ReadCBZ();
            ContentValues mNewValues = new ContentValues();
            folder = new File(String.valueOf(Environment.getExternalStorageDirectory()));
            Log.d("path", folder.toString());
            checkFiles(folder,files);
            Log.d("files",files.toString());
            for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()){
                if (mCursor.getString(mCursor.getColumnIndex(ComicColumns.TITLE)).endsWith(".cbr")) {
                    cbr.read(mCursor.getString(mCursor.getColumnIndex(ComicColumns.TITLE)));
                    cbr.getCbr();
                    //bitmaps.add(cbr.getBitmap(getApplicationContext(), 1));
                    File cache = cbr.getBitmapFile(getContext(),1);
                    comics.setBitmaps(cbr.getBitmap(cache));
                    //baseComicList.add(new BaseComic(mCursor.getString(mCursor.getColumnIndex(ComicColumns.TITLE)),cbr.getBitmap(cache)));
                    cbr.close();
                }else {
                    cbz.read(mCursor.getString(mCursor.getColumnIndex(ComicColumns.TITLE)));
                    cbz.getCbz();
                    cbz.CbzComic();
                    comics.setBitmaps(cbz.getPage(1));
                }
            }
            //mCursor.close();
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            pages.setAdapter(adapter);

            //Log.d("DB SITE", DebugDB.getAddressLog());


            /**/



        }
    }
}
