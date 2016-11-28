package tk.samgrogan.pulp.UI;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.daprlabs.cardstack.SwipeDeck;
import com.github.junrar.rarfile.FileHeader;
import com.meetic.dragueur.DraggableView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tk.samgrogan.pulp.Data.ComicColumns;
import tk.samgrogan.pulp.Data.ComicProvider;
import tk.samgrogan.pulp.Data.ReadCBR;
import tk.samgrogan.pulp.Models.Comics;
import tk.samgrogan.pulp.R;
import tk.samgrogan.pulp.SwipeDeckAdapter;
import tk.samgrogan.pulp.WobblyLayoutManager;

public class MainActivity extends AppCompatActivity {

    List<Bitmap> bitmaps = new ArrayList<Bitmap>();
    SwipeDeckAdapter adapter;
    SwipeDeck pages;
    List<FileHeader> sendMaps;
    WobblyLayoutManager manager;
    DraggableView draggableView;
    Comics comics = new Comics();
    Cursor mCursor;
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

        mCursor = getContentResolver().query(ComicProvider.Comics.CONTENT_URI, new String[]{ComicColumns.TITLE},null,null,null);

        adapter = new SwipeDeckAdapter(bitmaps,this);
        new ThumbNailTask().execute();

        pages = (SwipeDeck) findViewById(R.id.test_list);
        pages.setEventCallback(new SwipeDeck.SwipeEventCallback() {
            @Override
            public void cardSwipedLeft(int position) {

            }

            @Override
            public void cardSwipedRight(int position) {
                Intent intent = new Intent(getApplicationContext(),ReaderActivity.class).putExtra("filename",  comics.getFilenames(position));
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

    }


    public class ThumbNailTask extends AsyncTask<Object,Object,Bitmap> {
        File folder;
        ReadCBR cbr;
        @Override
        protected Bitmap doInBackground(Object... params) {
            cbr = new ReadCBR();
            ContentValues mNewValues = new ContentValues();
            folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Comics");
            Log.d("path", folder.toString());
            List<File> files = new ArrayList<>();
            Collections.addAll(files, folder.listFiles());
            Log.d("files",files.toString());
            for (int i = 0; i < files.size(); i++){
                //
                File file = files.get(i);
                comics.setFilenames(file);
                if (mCursor != null && mCursor.getCount() > 0) {
                    mCursor.moveToPosition(i);
                    if (!mCursor.getString(mCursor.getColumnIndex(ComicColumns.TITLE)).equals(file.toString())) {
                        mNewValues.put(ComicColumns.TITLE, file.toString());
                        getContentResolver().insert(ComicProvider.Comics.CONTENT_URI, mNewValues);
                    }
                }

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


            /**/



        }
    }
}
