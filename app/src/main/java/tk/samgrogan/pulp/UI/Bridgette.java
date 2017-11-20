package tk.samgrogan.pulp.UI;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tk.samgrogan.pulp.Data.BaseComic;
import tk.samgrogan.pulp.Data.ComicColumns;
import tk.samgrogan.pulp.Data.ComicProvider;
import tk.samgrogan.pulp.Data.ReadCBR;
import tk.samgrogan.pulp.Data.ReadCBZ;
import tk.samgrogan.pulp.Models.ComicDataObject;
import tk.samgrogan.pulp.Models.Comics;
import tk.samgrogan.pulp.R;

import static android.support.design.widget.Snackbar.make;

public class Bridgette extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private View view;
    private GridView pages;
    private ImageArrayAdapter adapter;
    private List<Bitmap> bitmaps = new ArrayList<Bitmap>();
    private List<BaseComic> baseComicList = new ArrayList<BaseComic>();
    private List<String> pathList = new ArrayList<>();
    private String boxName;
    private Cursor mCursor;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    private static final int CURSOR_LOADER_ID = 0;
    private Comics comics = new Comics();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bridgette, container, false);
        setHasOptionsMenu(true);
        bitmaps = comics.getBitmaps();
        adapter = new ImageArrayAdapter(view.getContext(),baseComicList);

        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

        pages = (GridView) view.findViewById(R.id.selector_grid);
        pages.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("users").child(firebaseAuth.getCurrentUser().getUid()).child("collections");

        pages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!baseComicList.get(position).isSelected()) {
                    baseComicList.get(position).setSelected(true);
                    Snackbar snackbar = make(pages,R.string.selected,Snackbar.LENGTH_LONG);
                    snackbar.show();
                    pathList.add(baseComicList.get(position).getTitle());
                    adapter.notifyDataSetChanged();

                }else {
                    baseComicList.get(position).setSelected(false);
                    Snackbar snackbar = make(pages,R.string.unselected,Snackbar.LENGTH_LONG);
                    snackbar.show();
                    pathList.remove(position);
                    adapter.notifyDataSetChanged();
                }

            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.short_box_creation_tools, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_to_box:
                showDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());

        alert.setTitle(R.string.dialog_title);
        alert.setMessage(R.string.dialog_message);

        final EditText input = new EditText(view.getContext());
        alert.setView(input);

        alert.setPositiveButton(R.string.pos_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boxName = input.getText().toString();
                pushData();
            }
        });

        alert.setNegativeButton(R.string.neg_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.show();
    }

    private void pushData(){
        ComicDataObject dataObject = new ComicDataObject(boxName,pathList);
        databaseReference.push().setValue(dataObject);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(view.getContext(), ComicProvider.Comics.CONTENT_URI,new String[]{ComicColumns.TITLE},null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;
        new ThumbNailTask().execute();

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
            //folder = new File(String.valueOf(Environment.getExternalStorageDirectory()));
            //Log.d("path", folder.toString());
            //checkFiles(folder,files);
            //Log.d("files",files.toString());
            for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()){
                if (mCursor.getString(mCursor.getColumnIndex(ComicColumns.TITLE)).endsWith(".cbr")) {
                    cbr.read(mCursor.getString(mCursor.getColumnIndex(ComicColumns.TITLE)));
                    cbr.getCbr();
                    //bitmaps.add(cbr.getBitmap(getApplicationContext(), 1));
                    File cache = cbr.getBitmapFile(getContext(),1);
                    baseComicList.add(new BaseComic(mCursor.getString(mCursor.getColumnIndex(ComicColumns.TITLE)),cbr.getBitmap(cache)));
                    comics.setBitmaps(cbr.getBitmap(cache));
                    //baseComicList.add(new BaseComic(mCursor.getString(mCursor.getColumnIndex(ComicColumns.TITLE)),cbr.getBitmap(cache)));
                    cbr.close();
                }else {
                    cbz.read(mCursor.getString(mCursor.getColumnIndex(ComicColumns.TITLE)));
                    cbz.getCbz();
                    cbz.CbzComic();
                    baseComicList.add(new BaseComic(mCursor.getString(mCursor.getColumnIndex(ComicColumns.TITLE)),cbz.getPage(1)));
                    comics.setBitmaps(cbz.getPage(1));
                }
            }
            //mCursor.close();
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
