package tk.samgrogan.pulp.ui;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.daprlabs.cardstack.SwipeDeck;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tk.samgrogan.pulp.Data.ReadCBR;
import tk.samgrogan.pulp.Data.ReadCBZ;
import tk.samgrogan.pulp.Models.ComicDataObject;
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
    private ProgressBar progressBar;
    private Context mContxt;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String comicName;
    private String path;
    private Snackbar missing;
    private String boxName;
    private static final int CURSOR_LOADER_ID = 0;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (missing != null) {
            missing.dismiss();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.short_box_fragment, container, false);
        mContxt = getActivity();
        //bitmaps = comics.getBitmaps();
        boxName = getArguments().getString("box-name");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        progressBar = view.findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);

        databaseReference = firebaseDatabase.getReference().child("users").child(firebaseAuth.getCurrentUser().getUid()).child("collections").child(boxName);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ComicDataObject comicDataObject = dataSnapshot.getValue(ComicDataObject.class);
                Log.d("Where is it", comicDataObject.getCollectionTitle());
                firePaths = new ArrayList<>();
                firePaths.addAll(comicDataObject.getCollectionList());
                comics.clearBitmaps();
                //bitmaps = comics.getBitmaps();
                new ThumbNailTask(mContxt).execute();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //firePaths.addAll(getArguments().getStringArrayList("collection-paths"));


        //firebaseDatabase.getReference().child("users").child(firebaseAuth.getCurrentUser().getUid()).child("collections").child(boxName).child("collectionList").

        /*MobileAds.initialize(view.getContext(), getString(R.string.app_pub));

        AdView mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/


        //new ThumbNailTask().execute();
        //getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

        pages = (SwipeDeck) view.findViewById(R.id.box_list);
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
        Context mContext;
        boolean refreshToken = false;

        public ThumbNailTask(Context context){
            this.mContext = context;
        }


        @Override
        protected Bitmap doInBackground(Object... params) {
            //cbr = new ReadCBR();
            //cbz = new ReadCBZ();

            folder = new File(String.valueOf(Environment.getExternalStorageDirectory()));
            for (int i = 0; i < firePaths.size(); i++){
                //
                String file = firePaths.get(i);
                File check = new File(file);
                if (!check.exists()){
                    refreshToken = true;
                    comicName = check.getName();
                    path = firePaths.get(i);
                    firePaths.remove(i);
                }else {

                    if (file.endsWith(".cbr")) {
                        /*cbr.read(file);
                        cbr.getCbr();
                        File cache = cbr.getBitmapFile(mContext, 0);
                        comics.setBitmaps(cbr.getBitmap(cache));*/
                    } else {
                        /*cbz.read(file);
                        ZipFile zip = cbz.getCbz();
                        if (zip != null) {
                            cbz.CbzComic();
                            comics.setBitmaps(cbz.getPage(0));
                        }*/
                    }
                }

                //cbr.close();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            bitmaps = comics.getBitmaps();
            adapter = new SwipeDeckAdapter(bitmaps, view.getContext());
            pages.setAdapter(adapter);
            if (refreshToken){

                missing = Snackbar.make(getActivity().findViewById(R.id.private_box), "Comic is missing", BaseTransientBottomBar.LENGTH_INDEFINITE);
                missing.setAction("Find Comic", new ComicSearchAction());
                missing.show();
            }
            //Log.d("DB SITE", DebugDB.getAddressLog());


            /**/



        }
    }

    public class ComicSearchAction implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            File folder = new File(String.valueOf(Environment.getExternalStorageDirectory()));
            checkFiles(folder, comicName);
            missing.dismiss();

        }
    }

    private void checkFiles(File dir, String comic) {
        File[] fileList = dir.listFiles();
        if (fileList != null) {
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    //if this is a directory, loop over the files in the directory
                    checkFiles(fileList[i], comic);
                } else {
                    if (fileList[i].getName().equals(comic)) {
                        //this is the file you want, do whatever with it here
                        firePaths.add(fileList[i].getAbsolutePath());
                        //firebaseDatabase.getReference().child("users").child(firebaseAuth.getCurrentUser().getUid()).child("collections").child(boxName).child("collectionList").removeValue();
                        firebaseDatabase.getReference().child("users").child(firebaseAuth.getCurrentUser().getUid()).child("collections").child(boxName).child("collectionList").setValue(firePaths);
                        firePaths.clear();
                        //Fragment refresh = new ShortBoxFragment();
                        //FragmentTransaction ft = getFragmentManager().beginTransaction();
                        //ft.replace(R.id.flContent, refresh).commit();
                    }

                }
            }
        }
    }
}
