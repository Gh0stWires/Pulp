package tk.samgrogan.pulp.UI;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tk.samgrogan.pulp.Data.ComicColumns;
import tk.samgrogan.pulp.Data.ComicProvider;
import tk.samgrogan.pulp.Data.ReadCBR;
import tk.samgrogan.pulp.Data.ReadCBZ;
import tk.samgrogan.pulp.R;

public class ReaderActivity extends AppCompatActivity {

    List fileHeaderList = new ArrayList<>();
    String mFilename;
    ViewPager mPager;
    MyPagerAdapter myPagerAdapter;
    Cursor mCursor;

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
