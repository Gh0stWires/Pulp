package tk.samgrogan.pulp.UI;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
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

import com.amitshekhar.DebugDB;
import com.daprlabs.cardstack.SwipeDeck;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;

import tk.samgrogan.pulp.Data.ComicColumns;
import tk.samgrogan.pulp.Data.ComicProvider;
import tk.samgrogan.pulp.Data.ReadCBR;
import tk.samgrogan.pulp.Data.ReadCBZ;
import tk.samgrogan.pulp.Models.Comics;
import tk.samgrogan.pulp.R;
import tk.samgrogan.pulp.SwipeDeckAdapter;

/**
 * Created by ghost on 2/12/2017.
 */

public class CoverFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private List<Bitmap> bitmaps = new ArrayList<Bitmap>();
    private SwipeDeckAdapter adapter;
    private SwipeDeck pages;
    private Cursor mCursor;
    private Context mContext;
    private List<String> filePaths = new ArrayList<>();

    private static final int CURSOR_LOADER_ID = 0;
    private Comics comics = new Comics();
    private View view;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_main, container, false);
        bitmaps = comics.getBitmaps();
        mContext = view.getContext();


        MobileAds.initialize(view.getContext(), getString(R.string.app_pub));

        AdView mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        adapter = new SwipeDeckAdapter(bitmaps, view.getContext());

        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        new ThumbNailTask().execute();


        pages = (SwipeDeck) view.findViewById(R.id.test_list);
        pages.setEventCallback(new SwipeDeck.SwipeEventCallback() {
            @Override
            public void cardSwipedLeft(int position) {

            }

            @Override
            public void cardSwipedRight(int position) {
                //mCursor.moveToPosition(position);
                //String fileName = mCursor.getString(mCursor.getColumnIndex(ComicColumns.TITLE));
                String fileName = filePaths.get(position);
                Intent intent = new Intent(view.getContext(), ReaderActivity.class).putExtra("filename", fileName);
                startActivity(intent);

            }

            @Override
            public void cardsDepleted() {
                pages.setAdapter(adapter);
            }

            @Override
            public void cardActionDown() {

            }

            @Override
            public void cardActionUp() {

            }
        });
        return view;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(view.getContext(), ComicProvider.Comics.CONTENT_URI,new String[]{ComicColumns.TITLE, ComicColumns.PAGE},null,null,null);
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
        ContentValues mNewValues = new ContentValues();

        private void checkFiles(File dir, List<File> files) {
            String extensionOne = ".cbr";
            String extensionTwo = ".cbz";
            File[] fileList = dir.listFiles();
            if (fileList != null) {
                for (int i = 0; i < fileList.length; i++) {
                    if (fileList[i].isDirectory()) {
                        //if this is a directory, loop over the files in the directory
                        checkFiles(fileList[i], files);
                    } else {
                        if (fileList[i].getName().endsWith(extensionOne) || fileList[i].getName().endsWith(extensionTwo) ) {
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
            //mNewValues = new ContentValues();
            folder = new File(String.valueOf(Environment.getExternalStorageDirectory()));
            Log.d("path", folder.toString());
            checkFiles(folder,files);
            Log.d("files",files.toString());
            for (int i = 0; i < files.size(); i++){
                //
                File file = files.get(i);
                comics.setFilenames(file);
                mNewValues.put(ComicColumns.TITLE, file.toString());
                mContext.getContentResolver().insert(ComicProvider.Comics.CONTENT_URI, mNewValues);

                if (file.getName().endsWith(".cbr")){
                    cbr.read(file.toString());
                    cbr.getCbr();
                    File cache = cbr.getBitmapFile(mContext,1);
                    comics.setBitmaps(cbr.getBitmap(cache));
                    filePaths.add(file.toString());
                }else {
                    cbz.read(file.toString());
                    ZipFile zip = cbz.getCbz();
                    if (zip != null) {
                        cbz.CbzComic();
                        comics.setBitmaps(cbz.getPage(1));
                        filePaths.add(file.toString());
                    }
                }

                cbr.close();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            pages.setAdapter(adapter);
            Log.d("DB SITE", DebugDB.getAddressLog());


            /**/



        }
    }

}
