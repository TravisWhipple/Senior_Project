package com.example.traviswhipple.seniorproject;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * Created by traviswhipple on 7/31/18.
 */

public class SimilarImages extends AppCompatActivity {

    ImageView mImageView;
    ImageAdapter mSimilarImageAdapter;
    ImageManager mImageManager;
    LinearLayout mTagsLayout;
    Context mContext;


    public SimilarImages(Context appContext, ImageManager imageManager, ImageAdapter imageAdapter, ImageView imageView, LinearLayout tagsLayout){
        mImageManager = imageManager;
        mSimilarImageAdapter = imageAdapter;
        mImageView = imageView;
        mTagsLayout = tagsLayout;
        mContext = appContext;
    }


    public void setSimilarImages(String imagePath, int position){

        // Load selected image into selectedImageView.
        Glide.with(mContext)
                .load(imagePath)
                .into(mImageView);


        mImageView.setId(position);

        //similarImagesGridView.removeAllViewsInLayout();
        mSimilarImageAdapter.removeAllViews();

        // Get path from image id.
        //String imageSelected = myImageAdapter.getItem(selectedImageView.getId());

        ImageObject io = mImageManager.getImageObject(imagePath);

        for(ImageObject imageObject : mImageManager.getSimilarImages(io)){
            mSimilarImageAdapter.add(imageObject.getPath());
        }

        tagView(mImageManager.getImageObject(imagePath));
    }

    public void tagView(ImageObject imageObject){
        mTagsLayout.removeAllViews();

        //TODO testing tags
        TextView tempText = new TextView(mContext);
        tempText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        //tempText.setTextColor(getResources().getColor(R.color.colorAccent, null));
        tempText.setTextSize(25);

        String tag = "";
        Log.e("NumTags:", Integer.toString(imageObject.getNumTags()));
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        Long num = imageObject.getDate();
        calendar.setTimeInMillis(num);
        Date dateObject = calendar.getTime();
        String dateTaken = dateObject.toString();

        String dateAdded = dateFormat.format(calendar.getTime());

        String date = "DATE: " + dateAdded;
        tag += date + '\n';

        for(Tag tags : imageObject.getTags()){
            tag += tags.getTagName() + '\n';
        }
        tempText.setText(tag);

        // Add view to front so most recent move is at the top of move description.
        mTagsLayout.addView(tempText, 0);
    }

    public void tagViews(ImageObject imageObject){

        mTagsLayout.removeAllViews();

        //TODO testing tags
        TextView tempText = new TextView(mContext);
        tempText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        //tempText.setTextColor(getResources().getColor(R.color.colorAccent, null));
        tempText.setTextSize(25);

        String tag = "";
        //tag += input.getText().toString();

        //Log.e("NumTags:", Integer.toString(images.elementAt(images.indexOf(imageObject)).getNumTags()));

        //ImageObject imageObject = imageManager.getImageObject(imagePath);

        Log.e("NumTags:", Integer.toString(imageObject.getNumTags()));


        Cursor mImageCursor = mContext.getContentResolver()
                .query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        null,
                        null,
                        null,
                        MediaStore.Images.Media.DEFAULT_SORT_ORDER);





//        Date dateTaken = new Date();
//        Date dateAdded = new Date();
//        Date dateModified = new Date();



//        Calendar cal = Calendar.getInstance();
//        mImageCursor.moveToFirst();
//        String number = mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));
//        Long num = Long.parseLong(number);
//        cal.setTimeInMillis(num);
//        Date dateTest = cal.getTime();
//        String currDate = dateTest.toString();
//        char[] chararr = currDate.toCharArray();
//        currDate = "";
//
//        int count = 0;
//        for(char i : chararr){
//            if(count < 11 || count > 17){
//                currDate += i;
//            }
//            count++;
//        }
//
//        Log.e("D A T E:", currDate);



        String date = "DATE: ";
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        mImageCursor.moveToFirst();
        Date tempDateObject;
        while(!mImageCursor.isAfterLast()){

//            final int TAKEN = 0;
//            final int ADDED = 1;
//            final int MODIFIED = 2;
//
//            // Date times of various date data.
//            String[] dateDataTypes = {
//                    MediaStore.Images.Media.DATE_TAKEN,
//                    MediaStore.Images.Media.DATE_ADDED,
//                    MediaStore.Images.Media.DATE_MODIFIED
//            };
//
//            for(String dateType : dateDataTypes){
//
//            }
//
//            String dateTaken =




//            String dateTaken;
//            String dateAdded;
//            String dateModified;
//
//            // Date Taken, Added and Modified are stored here.
//            Integer dateTakenMiliSec;
//            Integer dateAddedMiliSec;
//            Integer dateModifMiliSec;
//
//            dateTakenMiliSec = mImageCursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
//            dateAddedMiliSec = mImageCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);
//            dateModifMiliSec = mImageCursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED);
//
//
//            calendar.setTimeInMillis(dateTakenMiliSec);
//            tempDateObject = calendar.getTime();
//            dateTaken = tempDateObject.toString();
//
//            calendar.setTimeInMillis(dateAddedMiliSec);
//            tempDateObject = calendar.getTime();
//            dateAdded = tempDateObject.toString();
//
//            calendar.setTimeInMillis(dateModifMiliSec);
//            tempDateObject = calendar.getTime();
//            dateModified = tempDateObject.toString();
//
//            Log.e("DateTaken", dateTaken);
//            Log.e("DateAdded", dateAdded);
//            Log.e("DateModified", dateModified);



//TODO start

//
//            String number = mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));
//            Long num = Long.parseLong(number);
//            calendar.setTimeInMillis(num);
//            Date dateObject = calendar.getTime();
//            String dateTaken = dateObject.toString();
//
//            number = mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
//            num = Long.parseLong(number);
//            calendar.setTimeInMillis(num);
//            dateObject = calendar.getTime();
//            String dateAdded = dateObject.toString();
//
//            number = mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));
//            num = Long.parseLong(number);
//            calendar.setTimeInMillis(num);
//            dateObject = calendar.getTime();
//            String dateModified = dateObject.toString();
//
//            //travis, here, need to check if date is putting two spaces between day and year. Otherwise i think this is close to done.
//
//            int count = 0;
//            char[] chararr = dateTaken.toCharArray();
//            dateTaken = "";
//            for(char i : chararr){
//                if(count < 11 || count > 22){
//                    dateTaken += i;
//                }
//                count++;
//            }
//
//            Log.e("Date Taken:   ", dateTaken);
//            Log.e("Date Added:   ", dateAdded);
//            Log.e("Date Modified:", dateModified);


// TODO end




//
//            Date dateTaken = new Date();
//            Date dateAdded = new Date();
//            Date dateModified = new Date();
//
//            Log.e("DateTaken", dateTaken.toString());
//
//
//            //dateTaken = new Date(mImageCursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));
//            dateAdded = new Date(mImageCursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));
//            dateModified = new Date(mImageCursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));
//
//            String result = dateFormat.format(dateTaken);
//            Log.e("NEWWW_________", result);
//
//
//            Log.e("DateTaken", mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN)));
//            Log.e("DateAdded", mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED)));
//            Log.e("DateModif", mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)));
//            Log.e("*", "");
//            Log.e("Photo________:", mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Images.Media.DATA)));
//            Log.e("Date_Taken___:", dateFormat.format(dateTaken));
//            Log.e("Date_Added___:", dateFormat.format(dateAdded));
//            Log.e("Date_Modified:", dateFormat.format(dateModified));
//            Log.e("Photo_Search_:", imagePath);
//            Log.e("*", ".");
//            Log.e("*", ".");
//            Log.e("*", ".");

            String searchPath = mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Images.Media.DATA));

            if(searchPath.contains(imageObject.getPath())){


                String number = mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));
                Long num = Long.parseLong(number);
                calendar.setTimeInMillis(num);
                Date dateObject = calendar.getTime();
                String dateTaken = dateObject.toString();

                number = mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
                num = Long.parseLong(number);
                calendar.setTimeInMillis(num);
                dateObject = calendar.getTime();
                String dateAdded = dateObject.toString();

                number = mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));
                num = Long.parseLong(number);
                calendar.setTimeInMillis(num);
                dateObject = calendar.getTime();
                String dateModified = dateObject.toString();

                //travis, here, need to check if date is putting two spaces between day and year. Otherwise i think this is close to done.

                int count = 0;
                char[] chararr = dateTaken.toCharArray();
                dateTaken = "";
                for(char i : chararr){
                    if(count < 11 || count > 22){
                        dateTaken += i;
                    }
                    count++;
                }

                Log.e("Date Taken:   ", dateTaken);
                Log.e("Date Added:   ", dateAdded);
                Log.e("Date Modified:", dateModified);

//                String dateTaken    = dateFormat.format(new Date(mImageCursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN)));
//                String dateAdded    = dateFormat.format(new Date(mImageCursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN)));
//                String dateModified = dateFormat.format(new Date(mImageCursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN)));

                //date += dateTaken + '\n';
                date += dateTaken;
                break;
            }
            mImageCursor.moveToNext();
        }
        mImageCursor.close();

        tag += date;

        for(Tag tags : imageObject.getTags()){
            tag += tags.getTagName() + '\n';
        }
        tempText.setText(tag);



        // Add view to front so most recent move is at the top of move description.
        mTagsLayout.addView(tempText, 0);
    }

}
