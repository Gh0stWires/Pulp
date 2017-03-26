package tk.samgrogan.pulp.UI;

import android.Manifest;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.daprlabs.cardstack.SwipeDeck;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    List<Bitmap> bitmaps = new ArrayList<Bitmap>();
    SwipeDeckAdapter adapter;
    SwipeDeck pages;
    Cursor mCursor;

    private static final int CURSOR_LOADER_ID = 0;
    Comics comics = new Comics();
    View view;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_main, container, false);
        bitmaps = comics.getBitmaps();

        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);
        MobileAds.initialize(view.getContext(), getString(R.string.app_pub));

        AdView mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        adapter = new SwipeDeckAdapter(bitmaps, view.getContext());

        new ThumbNailTask().execute();
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

        pages = (SwipeDeck) view.findViewById(R.id.test_list);
        pages.setEventCallback(new SwipeDeck.SwipeEventCallback() {
            @Override
            public void cardSwipedLeft(int position) {

            }

            @Override
            public void cardSwipedRight(int position) {
                mCursor.moveToPosition(position);
                String fileName = mCursor.getString(mCursor.getColumnIndex(ComicColumns.TITLE));
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
    public void onStop() {
        super.onStop();
        //mCursor.close();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new ThumbNailTask().execute();

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
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
            ContentValues mNewValues = new ContentValues();
            folder = new File(String.valueOf(Environment.getExternalStorageDirectory()));
            Log.d("path", folder.toString());
            checkFiles(folder,files);
            Log.d("files",files.toString());
            for (int i = 0; i < files.size(); i++){
                //
                File file = files.get(i);
                comics.setFilenames(file);
                mNewValues.put(ComicColumns.TITLE, file.toString());
                getActivity().getContentResolver().insert(ComicProvider.Comics.CONTENT_URI, mNewValues);

                if (file.getName().endsWith(".cbr")){
                    cbr.read(file.toString());
                    cbr.getCbr();
                    File cache = cbr.getBitmapFile(getContext(),1);
                    comics.setBitmaps(cbr.getBitmap(cache));
                }else {
                    cbz.read(file.toString());
                    cbz.getCbz();
                    cbz.CbzComic();
                    comics.setBitmaps(cbz.getPage(1));
                }

                cbr.close();
            }
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
