package tk.samgrogan.pulp.Data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by ghost on 1/15/2017.
 */

public class ReadCBZ {

    private String mFileName;
    ZipFile cbz = null;
    private ArrayList mPages;


    public ReadCBZ(){}

    public void close(){
        try {
            cbz.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void read(String fileName){
        this.mFileName = fileName;
    }

    public ZipFile getCbz(){
        try {
            cbz = new ZipFile(new File(mFileName));
        } catch (IOException e) {
            e.printStackTrace();
            cbz = null;
        }

        return cbz;
    }

    public  boolean isImage(ZipEntry entry) {
        if (entry == null){
            return false;
        }
        InputStream in = null;
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        try {
            in = cbz.getInputStream(entry);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BitmapFactory.decodeStream(in,null,opt);
        return opt.outWidth != -1 && opt.outHeight != -1;
    }


    //TODO
    public void CbzComic() {

        // populate mPages with the names of all the ZipEntries
        //cbz = new ZipFile(mFileName);
        try {
            mPages = new ArrayList<String>();
            Enumeration<? extends ZipEntry> entries = cbz.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                if (isImage(entry)) {
                    mPages.add(entry.getName());
                }

            }
        } catch (Exception e) {
            Log.e( "Error opening file", "error");
        }

    }
    public List getPages(){
        return mPages;
    }

    public Bitmap getPage(int pageNum) {
        Bitmap bitmap = null;
        Collections.sort(mPages);

        try {
            ZipEntry entry = cbz.getEntry((String) mPages.get(pageNum));
            InputStream in = null;
            BitmapFactory.Options opt = new BitmapFactory.Options();
            try {
                opt.inJustDecodeBounds = true;
                in = cbz.getInputStream(entry);
                bitmap = BitmapFactory.decodeStream(in,null,opt);
            } finally {
                if (in != null) {
                    in.close();
                }

            }
            in = null;

            //opt.inSampleSize = calculateInSampleSize(opt, 600, 600);
            opt.inJustDecodeBounds = false;

            try {
                in = cbz.getInputStream(entry);
                bitmap = BitmapFactory.decodeStream(in, null, opt);
            }finally {
                if (in != null){
                    in.close();
                }
            }


        } catch (IOException e) {
        }
        return bitmap;
    }




    /*public File getBitmapFile(Context context, int pageNum){
        Enumeration<? extends ZipEntry> files = cbz.entries();
        String uri = cbz.toString();
        FileOutputStream file = null;
        File c = null;
        try {
            String fileName = Uri.parse(uri).getLastPathSegment();
            c = new File(context.getCacheDir() + fileName);
            file = new FileOutputStream(c);
            cbz.e(files.get(pageNum),file);
            file.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RarException e) {
            e.printStackTrace();
        }
        return c;
    }

    public Bitmap getBitmap(Context context, int page){
        Bitmap bitmap = null;
        try{
            FileInputStream in = null;
            BitmapFactory.Options opt = new BitmapFactory.Options();
            //List<FileHeader> files = getHeaders();
            //cbr.extractFile();
            //List<FileHeader> files = getHeaders();

            try {
                opt.inJustDecodeBounds = true;
                in = new FileInputStream(getBitmapFile(context,page));
                BitmapFactory.decodeStream(in, null, opt);
            }finally {
                if (in != null){
                    in.close();

                }

            }
            in = null;

            //int scale = (maxLength <= 0) ? 1 : Math.max(opt.outWidth, opt.outHeight) / maxLength;

            opt.inSampleSize = calculateInSampleSize(opt, 600, 600);
            opt.inJustDecodeBounds = false;


            try {
                in = new FileInputStream(getBitmapFile(context,page));
                bitmap = BitmapFactory.decodeStream(in, null, opt);
            } finally {
                if (in != null){
                    in.close();
                }

            }


        } catch (IOException e){
            Log.e("Error loading bitmap", e.toString());
        }
        return bitmap;

    }*/
    }
