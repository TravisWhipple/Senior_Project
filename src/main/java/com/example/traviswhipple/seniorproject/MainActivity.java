/*
package com.example.traviswhipple.seniorproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
*/



package com.example.traviswhipple.seniorproject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.provider.MediaStore;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import static android.content.ContentValues.TAG;
import static android.os.Build.VERSION_CODES.LOLLIPOP;

public class MainActivity extends Activity{

    private final int MY_PERMISSIONS_EXTERNAL_DATA = 1;


    private ImageView selectedImageView;

    //private Vector<ImageObject> images;

    private ImageLoader imageLoader;

    private ImageAdapter myImageAdapter;
    private ImageAdapter similarImageAdapter;
    private String ExternalStorageDirectoryPath;
    //private final String IMAGES_PATH = "/DCIM/Camera/";
    private final String IMAGES_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/snr_prj/saveFile_images2.txt";

    private GridView gridView;
    private GridView similarImagesGridView;

    private LinearLayout tagLayout;

    //private ImageObject imageLibrary;
    private ImageManager imageManager;
    private SimilarImages similarImagesView;


    private boolean externalStorageAvailable() {
        return
                Environment.MEDIA_MOUNTED
                        .equals(Environment.getExternalStorageState());
    }

    public String DEBUG_TAG = "onPress";



    //TODO NEW * * *
    public void checkPermissions(){
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.e("PERMISSION:", "GRANTED");
        } else {
            Log.e("PERMISSION:", "ATAINING");
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "We need photos for this", Toast.LENGTH_SHORT).show();
            }

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_EXTERNAL_DATA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_EXTERNAL_DATA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    showPhotos();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


    public void showPhotos(){


        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            Log.e("PERMISSION:", "TRUE");
        }else{
            Log.e("PERMISSION:", "FALSE");

        }

        Cursor mCursor = getContentResolver()
                .query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        null,
                        null,
                        null,
                        MediaStore.Images.Media.DEFAULT_SORT_ORDER);


        mCursor.moveToFirst();
        while(!mCursor.isAfterLast()) {
            Log.d(TAG, " - _ID : " + mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media._ID)));
            Log.d(TAG, " - File Name : " + mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
            Log.d(TAG, " - File Path : " + mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA)));
            mCursor.moveToNext();
        }
        mCursor.close();

        Context context = getApplicationContext();

        mCursor = context.getContentResolver()
                .query(
                        MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                        null,
                        MediaStore.Images.Thumbnails.IMAGE_ID + "=?" ,
                        //new String[]{id},
                        null,
                        null);


        mCursor.moveToFirst();
        while (!mCursor.isAfterLast()){
            Log.d(TAG, "  - Thumbnail File Path : " + mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA)));
            Log.d(TAG, "  - Thumbnail Type : " + mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Thumbnails.KIND)));
            mCursor.moveToNext();
        }
        mCursor.close();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_copy_orig);

        //images = new Vector<>();

        if(Build.VERSION.SDK_INT <= LOLLIPOP){
            // Permissions ok
        }else{
            checkPermissions();
        }

        imageManager = new ImageManager();
        imageLoader = new ImageLoader(this, imageManager, IMAGES_PATH);

        selectedImageView = findViewById(R.id.selectedImage);

        gridView = (GridView) findViewById(R.id.gridView);

        tagLayout = findViewById(R.id.tagLayout);


        myImageAdapter = new ImageAdapter(this);
        gridView.setAdapter(myImageAdapter);

        ArrayList<String> allPaths = imageManager.getAllPaths();


        if(allPaths != null){
            for (String path : allPaths) {
                myImageAdapter.add(path);
                //ImageObject imgObj = new ImageObject(myImageAdapter.getItem(count));
                //count++;
            }
        }

        similarImagesGridView = findViewById(R.id.similarImagesGridView);
        similarImageAdapter = new ImageAdapter(this);
        similarImagesGridView.setAdapter(similarImageAdapter);

        similarImagesView = new SimilarImages(getApplicationContext(), imageManager, similarImageAdapter, selectedImageView, tagLayout);

        Glide.with(this)
                .load(allPaths.get(0))
                .into(selectedImageView);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                clicked(position);
            }
        });


        similarImagesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                Log.e("similarImagesClick:", similarImageAdapter.getItem(position));

                myImageAdapter.getItem(position);

                clicked(myImageAdapter.getItem(similarImageAdapter.getItem(position)));


                Glide.with(getApplicationContext())
                        .load(similarImageAdapter.getItem(position))
                        .into(selectedImageView);

                selectedImageView.setId(position);


                similarImagesGridView.removeAllViewsInLayout();

                similarImageAdapter.removeAllViews();



                String imageSelected = myImageAdapter.getItem(selectedImageView.getId());
                ImageObject imag = null;

//                for(ImageObject io : images){
//                    if(io.getImageID() == imageSelected){
//                        imag = io;
//                        break;
//                    }
//                }


//                for (ImageObject image : images){
//                    if(imag == null) {
//                        break;
//                    }
//                    for(String tag : image.getTags()){
//                        for(String thisTag : imag.getTags()){
//                            if(thisTag.contains(tag)){
//                                similarImageAdapter.add(image.getImageID());
//                            }
//                        }
//                    }
//                }

                Log.e("Len", Integer.toString(similarImageAdapter.getCount()));



            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        String DEBUG_TAG = "Touch_Action";

        int action = event.getActionMasked();
        switch(action) {
            case (MotionEvent.ACTION_DOWN) :
                Log.d(DEBUG_TAG,"Action was DOWN");
                return true;
            case (MotionEvent.ACTION_MOVE) :
                Log.d(DEBUG_TAG,"Action was MOVE");
                return true;
            case (MotionEvent.ACTION_UP) :
                Log.d(DEBUG_TAG,"Action was UP");
                return true;
            case (MotionEvent.ACTION_CANCEL) :
                Log.d(DEBUG_TAG,"Action was CANCEL");
                return true;
            case (MotionEvent.ACTION_OUTSIDE) :
                Log.d(DEBUG_TAG,"Movement occurred outside bounds " +
                        "of current screen element");
                return true;
            default :
                return super.onTouchEvent(event);
        }
    }

    private void setViewLayout(int contentView){
        setViewLayout(contentView, true);
    }

    private void setViewLayout(int contentView, boolean hasViews){
        setContentView(contentView);

        if(hasViews){
            similarImagesGridView = findViewById(R.id.similarImagesGridView);
            selectedImageView = findViewById(R.id.selectedImage);
            tagLayout = findViewById(R.id.tagLayout);
        }
    }

    public void clicked(int position){

        //String imagePath = myImageAdapter.getItem(position);
        String imagePath = imageManager.getImagePathFromPosition(position);
        //tagViews(imageManager.getImageObject(imagePath));

        //setViewLayout(R.layout.similar_images_activity);

        similarImagesView.setSimilarImages(imagePath, position);

    }

    public void tagViews(ImageObject imageObject){

        tagLayout.removeAllViews();

        //TODO testing tags
        TextView tempText = new TextView(getApplicationContext());
        tempText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tempText.setTextColor(getResources().getColor(R.color.colorAccent, null));
        tempText.setTextSize(25);

        String tag = "";
        //tag += input.getText().toString();

        //Log.e("NumTags:", Integer.toString(images.elementAt(images.indexOf(imageObject)).getNumTags()));

        //ImageObject imageObject = imageManager.getImageObject(imagePath);

        Log.e("NumTags:", Integer.toString(imageObject.getNumTags()));


        Cursor mImageCursor = getContentResolver()
                .query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        null,
                        null,
                        null,
                        MediaStore.Images.Media.DEFAULT_SORT_ORDER);

        String date = "DATE: ";
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        mImageCursor.moveToFirst();
        Date tempDateObject;
        while(!mImageCursor.isAfterLast()){

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
        tagLayout.addView(tempText, 0);
        //TODO end of testing tags
    }

    public void addTag(View v){

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Input is where user will input the depth they want.
        final EditText input = new EditText(this);
        input.setHint("Things in photo");
        input.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        input.setLayoutParams(lp);
        alertDialog.setView(input);

        // Alert dialog to get user input.
        alertDialog
                .setTitle("Enter Tag")
                .setMessage("Any word")
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })

                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String userInput = input.getText().toString();
                        String imagePath = myImageAdapter.getItem(selectedImageView.getId());

                        ImageObject imageObject = imageManager.getImageObject(imagePath);
                        Tag tagObject = imageManager.getTagObject(userInput);
                        imageManager.addTag(imageObject, tagObject);

                        //similarImageAdapter.add(io.getImageID());

                        tagViews(imageObject);
                        similarImagesView.tagView(imageObject);

                        saveToFile();
                    }
                });

        alertDialog.create();
        alertDialog.show();
    }

    //TODO Save and Load functionality.

    public void loadFromFile(){

    }

    public void saveToFile(){

        String fileName = "saveFile_" + "images2" + ".txt";
        Log.e("FileName", fileName);

        /* Create a path to save games to the file "aKonane"
        using this file name because it will stay at the top of the list of files
        making it easier to access for debugging.
        */
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/snr_prj";
        File dir = new File(path);
        dir.mkdirs();

        Log.e("PATH", path);


        if(isExternalStorageWritable() == false){
            Log.e("ERR", "Cannot write to SD");
            return;
        }

        // Get file in path.
        File myFile = new File(path + "/" + fileName);
        FileOutputStream outputStream = null;

        String data = "";

        // Get all image data to add to file.
        for(ImageObject image : imageManager.getImages()){

            if(image.getNumTags() == 0){
                continue;
            }

            data += image.getPath();

            for(Tag tag : image.getTags()){
                data += ",";
                data += tag.getTagName() + "(";
                data += tag.getImageConfidence(image) + ")";
            }

//            for(String tag : image.getTags()){
//                data += tag + ",";
//            }

            data += "\n";
        }

        try {
            outputStream = new FileOutputStream(myFile);
            outputStream.write(data.getBytes());
            outputStream.close();
            Log.e("SAVE", "Saved under: " + fileName);
            return;
        }
        catch(Exception e) {
            e.printStackTrace();
            Log.e("SAVE", "ERROR, Below");
            Log.e("ERR", e.toString());
            return;
        }

    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}