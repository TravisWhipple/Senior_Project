package com.example.traviswhipple.seniorproject;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import static android.content.ContentValues.TAG;

/**
 * Created by traviswhipple on 7/11/18.
 */

public class ImageLoader {

    private Cursor mImageCursor;
    //private Map<String, ImageObject> images;
    private Vector<String> imageId;
    private Vector<String> imagePath;
    private final String FILE_PATH;

    private ImageManager mImageManager;

    public ImageLoader(Context context, ImageManager imageManager, String saveFilePath){

        imageId = new Vector<>();
        imagePath = new Vector<>();
        mImageManager = imageManager;

        FILE_PATH = saveFilePath;

        mImageCursor = context.getContentResolver()
                .query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        null,
                        null,
                        null,
                        MediaStore.Images.Media.DEFAULT_SORT_ORDER);

        loadImagesFromDevice();
        loadImagesIntoVector();
        imageManager.sort();
        Log.e("Sort", "Sorted");
    }

    public void laodImages(){

    }

    private boolean loadImagesFromDevice(){
        String rawData = getRawData();

        if(rawData == ""){
            return false;
        }

        // Split data on each row.
        String data[] = rawData.split("\n");

        for(String row : data){
            // Remove any spaces after "," in file.
            row = row.replaceAll(",\\s", ",");

            // Split the row by each tag entry, separated by ","
            String[] tagEntry = row.split("[,]");

            ImageObject imageObject = new ImageObject(tagEntry[0]);
            mImageManager.addImage(imageObject);

            // Start at 1 to skip over path name.
            for(int i = 1; i < tagEntry.length; i++){

                // Split tag entry data into tag's name and associated confidence.
                String[] tagSplitData = tagEntry[i].split("[()]", 0);

                String tagName = tagSplitData[0];
                double confidence = Double.parseDouble(tagSplitData[1]);

                // Create a new Tag object and add relationship to associated image.
                Tag tag = new Tag(tagName);
                mImageManager.addTagRelation(imageObject, tag, confidence);
            }
        }

        return true;
    }

    private String getRawData(){

        // Will return empty string if file could not be loaded.
        String rawData = "";

        // Get file to load through given path.
        File saveFile = new File(FILE_PATH);
        FileInputStream inputStream = null;

        // Check if file exists or not.
        if(!saveFile.exists()){
            return "";
        }

        try {
            inputStream = new FileInputStream(saveFile);

            // Set up buffer.
            int size = inputStream.available();
            byte[] buffer = new byte[size];

            // Read into buffer
            inputStream.read(buffer);
            inputStream.close();

            // Save contents of buffer.
            rawData = new String(buffer);
            Log.e("LOAD", "Success");
        }
        catch(Exception e) {
            e.printStackTrace();
            Log.e("LOAD", "Fail");
            Log.e("ERR", e.toString());
        }

        // Return raw data.
        return rawData;
    }


    private void loadImagesIntoVector(){


        mImageCursor.moveToFirst();

        while(!mImageCursor.isAfterLast()){

            int id = mImageCursor.getInt(mImageCursor.getColumnIndex(MediaStore.Images.Media._ID));
            String fileName = mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
            String path = mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Images.Media.DATA));

            String dateStr = mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));
            Log.e("Date", dateStr);
            Long dateTaken = Long.parseLong(dateStr);

            imageId.add(Integer.toString(id));
            imagePath.add(path);

            ImageObject tempImageObj = new ImageObject(path, fileName, id, dateTaken);
            mImageManager.addImage(tempImageObj);
            //images.put(Integer.toString(id), tempImageObj);
            mImageCursor.moveToNext();
        }
    }

    public void listAllToConsole(){

        mImageCursor.moveToFirst();

        while(!mImageCursor.isAfterLast()) {
            Log.d(TAG, " - _ID : " + mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Images.Media._ID)));
            Log.d(TAG, " - File Name : " + mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
            Log.d(TAG, " - File Path : " + mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Images.Media.DATA)));
            mImageCursor.moveToNext();
        }
    }

    public String getImagePath(int imageID){
        return getImagePath(Integer.toString(imageID));
    }

    public String getImagePath(String imageID){
        //ImageObject io = images.get(imageID);

        int idIndex = imageId.indexOf(imageID);
        String path = imagePath.elementAt(idIndex);

        return path;

        //return io.getPath();
    }

    public String getImageDate(String imagePath){
        mImageCursor.moveToFirst();
        while(!mImageCursor.isAfterLast()){
            if(mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Images.Media.DATA)) == imagePath){
                return mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));
            }
        }
        return "NONE";
    }

}
