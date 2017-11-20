package tk.samgrogan.pulp.Data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import junrar.Archive;
import junrar.exception.RarException;
import junrar.rarfile.FileHeader;

/**
 * Created by gh0st on 2/25/16.
 */
public class ReadCBR {

    private String mFileName;
    private List<FileHeader> mPages;
    private Archive cbr;
    public ReadCBR(){

    }

    public void read(String filename){
        mFileName = filename;

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

    private List<FileHeader> getHeaders(){
        List<FileHeader> fileHeaders = cbr.getFileHeaders();
        Collections.sort(fileHeaders, new Comp());
        return fileHeaders;
    }


    public static int calculateInSampleSize(
            BitmapFactory.Options options) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > 600 || width > 600) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > 600
                    && (halfWidth / inSampleSize) > 600) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


    public File getBitmapFile(Context context, int pageNum){
        List<FileHeader> files = getHeaders();
        String uri = getmFileName();
        FileOutputStream file = null;
        File c = null;
        try {
            String fileName = Uri.parse(uri).getLastPathSegment();
            c = new File(context.getCacheDir() + fileName);
            file = new FileOutputStream(c);
            cbr.extractFile(files.get(pageNum),file);
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

    public Bitmap getBitmap( File cacheBitmap){
        Bitmap bitmap = null;

        try{
            FileInputStream in = null;
            BitmapFactory.Options opt = new BitmapFactory.Options();
            //List<FileHeader> files = getHeaders();
            //cbr.extractFile();
            //List<FileHeader> files = getHeaders();

            try {
                opt.inJustDecodeBounds = true;
                in = new FileInputStream(cacheBitmap);
                BitmapFactory.decodeStream(in, null, opt);
            }finally {
                if (in != null){
                    in.close();

                }

            }
            in = null;

            //int scale = (maxLength <= 0) ? 1 : Math.max(opt.outWidth, opt.outHeight) / maxLength;

            opt.inSampleSize = calculateInSampleSize(opt);
            opt.inJustDecodeBounds = false;


            try {
                in = new FileInputStream(cacheBitmap);
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



    public List<FileHeader> getPages() {
        return mPages = getHeaders();
    }

    private String getmFileName(){
        return cbr.toString();
    }

    public static class Comp implements Comparator<FileHeader>{

        @Override
        public int compare(FileHeader lhs, FileHeader rhs) {
            return lhs.getFileNameString().compareTo(rhs.getFileNameString());
        }
    }


}
