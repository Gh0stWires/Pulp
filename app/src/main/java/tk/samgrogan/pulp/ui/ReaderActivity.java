package tk.samgrogan.pulp.ui;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import tk.samgrogan.pulp.R;
import tk.samgrogan.pulp.data.ComicColumns;
import tk.samgrogan.pulp.data.ComicProvider;
import tk.samgrogan.pulp.data.ReadCBR;
import tk.samgrogan.pulp.data.ReadCBZ;

public class ReaderActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private List fileHeaderList = new ArrayList<>();
    private String mFilename;
    private ViewPager mPager;

    private MyPagerAdapter myPagerAdapter;
    private Cursor mCursor;
    private GoogleApiClient mWear;

    private static final String NEXT_MESSAGE = "/next";
    private static final String BACK_MESSAGE = "/back";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.fragment_reader);
        mFilename = (String) getIntent().getExtras().get("filename");
        mPager = (ViewPager)findViewById(R.id.pager);
        new GetBits().execute();
        myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), mFilename);
        mPager.setOffscreenPageLimit(3);

        //mWear = new GoogleApiClient.Builder(this).addApi(Wearable.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();

        mCursor = getContentResolver().query(ComicProvider.Comics.CONTENT_URI,
                new String[]{ComicColumns.PAGE}, ComicColumns.TITLE + "= ?",
                new String[]{mFilename}, null);


        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ContentValues mNewValues = new ContentValues();
                mNewValues.put(ComicColumns.PAGE, position);
                getContentResolver().update(ComicProvider.Comics.CONTENT_URI,mNewValues,ComicColumns.TITLE + "= ?",
                        new String[]{mFilename.toString()});

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    private int getNextItem(){
        return mPager.getCurrentItem() + 1;
    }

    private int getPreviousItem(){
        return mPager.getCurrentItem() - 1;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("Wear","Connected");
        //Wearable.DataApi.addListener(mWear,this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("Wear","Suspended");
    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("Wear","Failed");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWear.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWear.disconnect();
        //Wearable.DataApi.removeListener(mWear, this);
    }

    /*@Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        for (DataEvent event : dataEventBuffer){
            if (event.getType() == DataEvent.TYPE_CHANGED){
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().equals(NEXT_MESSAGE)){
                    Log.d("Wear","Got Message");
                    mPager.setCurrentItem(getNextItem());
                }else if (item.getUri().getPath().equals(BACK_MESSAGE)){
                    mPager.setCurrentItem(getPreviousItem());
                }
            }
        }
    }*/

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        List data;
        String filename;
        public MyPagerAdapter(FragmentManager fm, String filename) {
            super(fm);

            this.filename = filename;
        }

        public void setData(List data){
            this.data = data;
        }

        @Override
        public Fragment getItem(int position) {
            return ReaderFragement.newInstance(position,filename);
        }

        @Override
        public int getCount() {
            return data.size();
        }
    }

    class GetBits extends AsyncTask<Integer, Object, Bitmap> {
        ReadCBR cbr;
        ReadCBZ cbz;
        File file = new File(mFilename);
        @Override
        protected Bitmap doInBackground(Integer... params) {
            if(file.getName().endsWith(".cbr")) {
                /*cbr = new ReadCBR();
                cbr.read(file.toString());
                cbr.getCbr();
                fileHeaderList = cbr.getPages();
                cbr.close();*/
            }else {
               /* cbz = new ReadCBZ();
                cbz.read(file.toString());
                cbz.getCbz();
                cbz.CbzComic();
                fileHeaderList = cbz.getPages();*/
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            myPagerAdapter.setData(fileHeaderList);
            mPager.setAdapter(myPagerAdapter);
            if (mCursor != null){
                mCursor.moveToFirst();
                mPager.setCurrentItem(mCursor.getInt(mCursor.getColumnIndex(ComicColumns.PAGE)));

            }


            //myPagerAdapter.getItem(mPager.getCurrentItem());



        }
    }


}
