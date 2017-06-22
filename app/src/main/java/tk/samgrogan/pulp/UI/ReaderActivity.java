package tk.samgrogan.pulp.UI;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tk.samgrogan.pulp.Data.ComicColumns;
import tk.samgrogan.pulp.Data.ComicProvider;
import tk.samgrogan.pulp.Data.ReadCBR;
import tk.samgrogan.pulp.Data.ReadCBZ;
import tk.samgrogan.pulp.R;

public class ReaderActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, MessageApi.MessageListener, GoogleApiClient.OnConnectionFailedListener {

    List fileHeaderList = new ArrayList<>();
    String mFilename;
    ViewPager mPager;
    MyPagerAdapter myPagerAdapter;
    Cursor mCursor;
    GoogleApiClient mWear;

    private static final String NEXT_MESSAGE = "/next";

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

        mWear = new GoogleApiClient.Builder(this).addApi(Wearable.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();

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

    public int getItem(int item){
        return mPager.getCurrentItem() + item;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("Wear","Connected");
        Wearable.MessageApi.addListener(mWear,this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("Wear","Suspended");
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("Wear","Got Message");
        if (messageEvent.getPath().equals(NEXT_MESSAGE)){
            mPager.setCurrentItem(getItem(1));
        }
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
        Wearable.MessageApi.removeListener(mWear, this);
    }

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
                cbr = new ReadCBR();
                cbr.read(file.toString());
                cbr.getCbr();
                fileHeaderList = cbr.getPages();
                cbr.close();
            }else {
                cbz = new ReadCBZ();
                cbz.read(file.toString());
                cbz.getCbz();
                cbz.CbzComic();
                fileHeaderList = cbz.getPages();
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
