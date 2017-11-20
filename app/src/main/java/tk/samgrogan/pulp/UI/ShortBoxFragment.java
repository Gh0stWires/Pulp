package tk.samgrogan.pulp.UI;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daprlabs.cardstack.SwipeDeck;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tk.samgrogan.pulp.Data.ReadCBR;
import tk.samgrogan.pulp.Data.ReadCBZ;
import tk.samgrogan.pulp.Models.Comics;
import tk.samgrogan.pulp.R;
import tk.samgrogan.pulp.SwipeDeckAdapter;

/**
 * Created by ghost on 3/25/2017.
 */

public class ShortBoxFragment extends Fragment {
    private List<Bitmap> bitmaps = new ArrayList<Bitmap>();
    private SwipeDeckAdapter adapter;
    private SwipeDeck pages;
    private View view;
    private List<String> firePaths = new ArrayList<>();
    private Comics comics = new Comics();
    Cursor mCursor;
    private static final int CURSOR_LOADER_ID = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_main, container, false);
        bitmaps = comics.getBitmaps();

        firePaths.addAll(getArguments().getStringArrayList("collection-paths"));

        MobileAds.initialize(view.getContext(), getString(R.string.app_pub));

        AdView mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        adapter = new SwipeDeckAdapter(bitmaps, view.getContext());

        new ThumbNailTask().execute();
        //getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

        pages = (SwipeDeck) view.findViewById(R.id.test_list);
        pages.setEventCallback(new SwipeDeck.SwipeEventCallback() {
            @Override
            public void cardSwipedLeft(int position) {

            }

            @Override
            public void cardSwipedRight(int position) {
                String fileName = firePaths.get(position);
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



    public class ThumbNailTask extends AsyncTask<Object,Object,Bitmap> {
        File folder;
        ReadCBR cbr;
        ReadCBZ cbz;


        @Override
        protected Bitmap doInBackground(Object... params) {
            cbr = new ReadCBR();
            cbz = new ReadCBZ();

            folder = new File(String.valueOf(Environment.getExternalStorageDirectory()));
            for (int i = 0; i < firePaths.size(); i++){
                //
                String file = firePaths.get(i);
                File check = new File(file);
                if (!check.exists()){
                    firePaths.remove(i);
                }else {

                    if (file.endsWith(".cbr")) {
                        cbr.read(file);
                        cbr.getCbr();
                        File cache = cbr.getBitmapFile(getContext(), 1);
                        comics.setBitmaps(cbr.getBitmap(cache));
                    } else {
                        cbz.read(file);
                        cbz.getCbz();
                        cbz.CbzComic();
                        comics.setBitmaps(cbz.getPage(1));
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
            //Log.d("DB SITE", DebugDB.getAddressLog());


            /**/



        }
    }
}
