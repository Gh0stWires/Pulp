package tk.samgrogan.pulp.UI;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.github.junrar.rarfile.FileHeader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tk.samgrogan.pulp.Data.ReadCBR;
import tk.samgrogan.pulp.R;

public class ReaderActivity extends AppCompatActivity {

    List<FileHeader> fileHeaderList = new ArrayList<>();
    File mFilename;
    ViewPager mPager;
    MyPagerAdapter myPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_reader);
        mFilename = (File) getIntent().getExtras().get("filename");
        mPager = (ViewPager)findViewById(R.id.pager);
        new GetBits().execute();
        myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), mFilename);
        mPager.setOffscreenPageLimit(3);

    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        List<FileHeader> data;
        File filename;
        public MyPagerAdapter(FragmentManager fm, File filename) {
            super(fm);

            this.filename = filename;
        }

        public void setData(List<FileHeader> data){
            this.data = data;
        }

        @Override
        public Fragment getItem(int position) {
            return ReaderFragement.newInstance(position,filename.getPath());
        }

        @Override
        public int getCount() {
            return data.size();
        }
    }

    class GetBits extends AsyncTask<Integer, Object, Bitmap> {
        ReadCBR cbr;
        File file = new File(mFilename.getPath());
        @Override
        protected Bitmap doInBackground(Integer... params) {
            cbr = new ReadCBR();
            cbr.read(file.toString());
            cbr.getCbr();
            fileHeaderList = cbr.getPages();
            cbr.close();
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            myPagerAdapter.setData(fileHeaderList);
            mPager.setAdapter(myPagerAdapter);


            //myPagerAdapter.getItem(mPager.getCurrentItem());



        }
    }
}
