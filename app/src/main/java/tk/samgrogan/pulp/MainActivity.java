package tk.samgrogan.pulp;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.GridView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<Bitmap> bitmaps = new ArrayList<Bitmap>();
    ImageArrayAdapter adapter;
    GridView pages;
    //ProgressBar bar;
    //File folder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Comics");
        //List<File> files = new ArrayList<>();
        //Collections.addAll(files, folder.listFiles());
        //Log.d("files", files.toString());
        bitmaps = new ArrayList<Bitmap>();

        adapter = new ImageArrayAdapter(this, bitmaps);
        new ThumbNailTask().execute();

        pages = (GridView) findViewById(R.id.test_list);
        //bar = (ProgressBar) findViewById(R.id.progressBar);
        //pages.setAdapter(adapter);

    }



    public class ThumbNailTask extends AsyncTask<Object,Object,Bitmap> {
        File folder;
        ReadCBR cbr;
        @Override
        protected Bitmap doInBackground(Object... params) {

            cbr = new ReadCBR();
            folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Comics");
            Log.d("path", folder.toString());
            List<File> files = new ArrayList<>();
            Collections.addAll(files, folder.listFiles());
            Log.d("files",files.toString());
            for (int i = 0; i < files.size(); i++){
                File file = files.get(i);
                cbr.read(file.toString());
                cbr.getCbr();
                bitmaps.add(cbr.getPage(1, 450));
                cbr.close();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            pages.setAdapter(adapter);
            Log.d("path", cbr.getPages().toString());



        }
    }
}
