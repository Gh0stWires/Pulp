package tk.samgrogan.pulp.UI;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.junrar.rarfile.FileHeader;

import java.io.File;
import java.util.List;

import tk.samgrogan.pulp.Data.ReadCBR;
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
        File file = new File(title);
        @Override
        protected Bitmap doInBackground(Integer... params) {
            cbr = new ReadCBR();
            cbr.read(file.toString());
            cbr.getCbr();
            int pageNum = params[0];
            List<FileHeader> fileHeaderList = cbr.getPages();
            //for (int i = 0; i < fileHeaderList.size(); i++) {
            //cbr.getBitmapFile(getApplicationContext(), i);
            bitmaps = cbr.getPage(page, 250);

            //bitmaps.add(cbr.getPage(5, 450));

            //}
            cbr.close();
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
