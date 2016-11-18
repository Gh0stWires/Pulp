package tk.samgrogan.pulp.Data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import tk.samgrogan.pulp.RARFile;

/**
 * Created by gh0st on 2/25/16.
 */
public class ReadCBR {

    String mFileName;
    List<FileHeader> mPages;
    RARFile mCBR;
    Archive cbr;

    public ReadCBR(){

    }

    public void read(String filename){
        mFileName = filename;
        /*try {
            mCBR = new RARFile(filename);
            mPages = new ArrayList();
            Enumeration<? extends RAREntry> entries = mCBR.entries();

            while (entries.hasMoreElements()){
                RAREntry entry = entries.nextElement();
                mPages.add(entry.getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public void close(){
        if (cbr != null){
            try {
                cbr.close();
                mFileName = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Archive getCbr() {
        try {
            cbr = new Archive(new File(mFileName));
        } catch (RarException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cbr;
    }

    public List<FileHeader> getHeaders(){
        List<FileHeader> fileHeaders = cbr.getFileHeaders();
        Collections.sort(fileHeaders, new Comp());
        return fileHeaders;
    }


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


    public File getBitmapFile(Context context, int pageNum){
        List<FileHeader> files = getHeaders();
        File file = new File(String.valueOf(context.getCacheDir()));
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            cbr.extractFile(files.get(pageNum), outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (RarException e) {
            e.printStackTrace();
        }
        return file;
    }

    public Bitmap getBitmap(Context context, int page){
        Bitmap bitmap = null;
        try{
            FileInputStream in = null;
            BitmapFactory.Options opt = new BitmapFactory.Options();
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

    }

    public Bitmap getPage(int pageNum, int maxLength){
        Bitmap bitmap = null;
        try{
            InputStream in = null;
            BitmapFactory.Options opt = new BitmapFactory.Options();
            List<FileHeader> files = getHeaders();

            try {
                opt.inJustDecodeBounds = true;
                in = cbr.getInputStream(files.get(pageNum));
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
                in = cbr.getInputStream(files.get(pageNum));
                bitmap = BitmapFactory.decodeStream(in, null, opt);
            } catch (RarException e) {
                e.printStackTrace();
            } finally {
                if (in != null){
                    in.close();
                }

            }


            } catch (IOException e){
                Log.e("Error loading bitmap", e.toString());
        } catch (RarException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public List<FileHeader> getPages() {
        return mPages = getHeaders();
    }

    public String getmFileName(){
        return mCBR.toString();
    }

    public static class Comp implements Comparator<FileHeader>{

        @Override
        public int compare(FileHeader lhs, FileHeader rhs) {
            return lhs.getFileNameString().compareTo(rhs.getFileNameString());
        }
    }


    public Bitmap getPageFile(FileHeader fileHeader, int maxLength){
        Bitmap bitmap = null;
        try{
            InputStream in = null;
            BitmapFactory.Options opt = new BitmapFactory.Options();



            try {
                opt.inJustDecodeBounds = true;
                in = cbr.getInputStream(fileHeader);
                BitmapFactory.decodeStream(in, null, opt);
            } catch (RarException e) {
                e.printStackTrace();
            } finally {
                if (in != null){
                    in.close();

                }

            }
            in = null;

            //int scale = (maxLength <= 0) ? 1 : Math.max(opt.outWidth, opt.outHeight) / maxLength;

            opt.inSampleSize = calculateInSampleSize(opt, opt.outWidth, opt.outHeight) / maxLength;
            opt.inJustDecodeBounds = false;


            try {
                in = cbr.getInputStream(fileHeader);
                bitmap = BitmapFactory.decodeStream(in, null, opt);
            } catch (RarException e) {
                e.printStackTrace();
            } finally {
                if (in != null){
                    in.close();
                }

            }


        } catch (IOException e){
            Log.e("Error loading bitmap", e.toString());
        }
        return bitmap;
    }
}
