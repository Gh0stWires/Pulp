package tk.samgrogan.pulp.UI;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;

import tk.samgrogan.pulp.Data.ReadCBR;
import tk.samgrogan.pulp.Data.ReadCBZ;
import tk.samgrogan.pulp.R;


public class ReaderFragement extends Fragment {
    private String title;
    private int page;
    private Bitmap bitmaps;
    private ImageView imageView;

    // newInstance constructor for creating fragment with arguments
    public static ReaderFragement newInstance(int page, String title) {
        ReaderFragement fragmentFirst = new ReaderFragement();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt");
        title = getArguments().getString("someTitle");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pages, container, false);
        imageView = (ImageView)view.findViewById(R.id.cover_image);
        new GetBits().execute(page);

        return view;
    }

    class GetBits extends AsyncTask<Integer, Object, Bitmap> {
        ReadCBR cbr;
        ReadCBZ cbz;
        File file = new File(title);
        @Override
        protected Bitmap doInBackground(Integer... params) {
            if (file.getName().endsWith(".cbr")) {
                cbr = new ReadCBR();
                cbr.read(file.toString());
                cbr.getCbr();
                //for (int i = 0; i < fileHeaderList.size(); i++) {
                //cbr.getBitmapFile(getApplicationContext(), i);
                bitmaps = cbr.getBitmap(getContext(), page);
                cbr.close();
            }else {
                cbz = new ReadCBZ();
                cbz.read(file.toString());
                cbz.getCbz();
                cbz.CbzComic();
                bitmaps = cbz.getPage(page);
            }

            //bitmaps.add(cbr.getPage(5, 450));

            //}

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            //comicPageAdapter.notifyDataSetChanged();
            imageView.setImageBitmap(bitmaps);




        }
    }
}
