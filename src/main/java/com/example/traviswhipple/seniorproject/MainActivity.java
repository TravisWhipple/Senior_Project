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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
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

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

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
                clicked(myImageAdapter.getItem(similarImageAdapter.getItem(position)));
            }
        });

    }

    @Override
    protected void onStop(){
        // Call super class first to ensure proper onStop functionality.
        super.onStop();
        saveToFile();
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
        similarImagesGridView.removeAllViewsInLayout();
        String imagePath = myImageAdapter.getItem(position);
        similarImagesView.setSimilarImages(imagePath, position);
    }

    public void analyzePhoto(View view){

        int position = selectedImageView.getId();
        String imagePath = myImageAdapter.getItem(position);
        ImageObject imageObject = imageManager.getImageObject(imagePath);

        //TODO TESTING CLOUD VIEW
        if(imageObject.getTags().size() < 3){
            Log.e("analyzePhoto", "analyzing...");
            uploadImage(Uri.fromFile(new File(imagePath)), imageObject);
        }else{
            Log.e("analyzePhoto", "Already Analyzed");
        }
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
                        imageManager.addTagRelation(imageObject, tagObject);

                        //similarImageAdapter.add(io.getImageID());

                        similarImagesView.setTagViewImage(imageObject);

                        saveToFile();
                    }
                });

        alertDialog.create();
        alertDialog.show();
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





    //TODO * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    private static final String CLOUD_VISION_API_KEY = "AIzaSyB53Zn1hsdqcrzRopzrVWXow82QSCr5m1c";

    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private static final int MAX_LABEL_RESULTS = 10;
    private static final int MAX_DIMENSION = 1200;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;



    public void uploadImage(Uri uri, ImageObject imageObject) {
        Log.e("UploadImage", "Called");
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                MAX_DIMENSION);


                callCloudVision(bitmap, imageObject);
                //mMainImage.setImageBitmap(bitmap);

            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                Toast.makeText(this, "Image Picker Error", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            Toast.makeText(this, "Image Picker Error", Toast.LENGTH_LONG).show();
        }
    }

    private Vision.Images.Annotate prepareAnnotationRequest(Bitmap bitmap) throws IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        VisionRequestInitializer requestInitializer =
                new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                    /**
                     * We override this so we can inject important identifying fields into the HTTP
                     * headers. This enables use of a restricted cloud platform API key.
                     */
                    @Override
                    protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                            throws IOException {
                        super.initializeVisionRequest(visionRequest);

                        String packageName = getPackageName();
                        visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                        String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                        visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                    }
                };

        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(requestInitializer);

        Vision vision = builder.build();

        BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                new BatchAnnotateImagesRequest();
        batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

            // Add the image
            Image base64EncodedImage = new Image();
            // Convert the bitmap to a JPEG
            // Just in case it's a format that Android understands but Cloud Vision
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Base64 encode the JPEG
            base64EncodedImage.encodeContent(imageBytes);
            annotateImageRequest.setImage(base64EncodedImage);

            // add the features we want
            annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                Feature labelDetection = new Feature();
                labelDetection.setType("LABEL_DETECTION");
                labelDetection.setMaxResults(MAX_LABEL_RESULTS);
                add(labelDetection);
            }});

            // Add the list of one thing to the request
            add(annotateImageRequest);
        }});

        Vision.Images.Annotate annotateRequest =
                vision.images().annotate(batchAnnotateImagesRequest);
        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotateRequest.setDisableGZipContent(true);
        Log.d(TAG, "created Cloud Vision request object, sending request");

        return annotateRequest;
    }

    private static class LableDetectionTask extends AsyncTask<Object, Void, String> {
        private final WeakReference<MainActivity> mActivityWeakReference;
        private Vision.Images.Annotate mRequest;
        private ImageObject mImageObject;

        LableDetectionTask(MainActivity activity, Vision.Images.Annotate annotate, ImageObject imageObject) {
            mActivityWeakReference = new WeakReference<>(activity);
            mRequest = annotate;
            mImageObject = imageObject;
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                Log.d(TAG, "created Cloud Vision request object, sending request");
                BatchAnnotateImagesResponse response = mRequest.execute();
                return convertResponseToString(response);

            } catch (GoogleJsonResponseException e) {
                Log.d(TAG, "failed to make API request because " + e.getContent());
            } catch (IOException e) {
                Log.d(TAG, "failed to make API request because of other IOException " +
                        e.getMessage());
            }
            return "Cloud Vision API request failed. Check logs for details.";
        }

        protected void onPostExecute(String result) {
            MainActivity activity = mActivityWeakReference.get();
            if (activity != null && !activity.isFinishing()) {
                //TextView imageDetail = activity.findViewById(R.id.image_details);
                //imageDetail.setText(result);

                Log.e("onPostExecute", result);

                String[] tags = result.split("\n");

                for(String tagData : tags){

                    String[] data = tagData.split(":");

                    double precision = Double.parseDouble(data[0]);
                    String tagName = data[1];

                    //Tag tagObject = activity.imageManager.getTagObject(tagName);
                    Tag tagObject = new Tag(tagName);
                    activity.imageManager.addTagRelation(mImageObject, tagObject, precision);

                    //activity.similarImagesView.setTagViewImage(mImageObject);
                }

                int position = activity.selectedImageView.getId();
                activity.clicked(position);
            }
        }
    }

    private void callCloudVision(final Bitmap bitmap, ImageObject imageObject) {
        // Switch text to loading
        //mImageDetails.setText(R.string.loading_message);

        // Do the real work in an async task, because we need to use the network anyway
        try {
            AsyncTask<Object, Void, String> labelDetectionTask = new LableDetectionTask(
                    this,
                    prepareAnnotationRequest(bitmap),
                    imageObject);
            labelDetectionTask.execute();
        } catch (IOException e) {
            Log.d(TAG, "failed to make API request because of other IOException " +
                    e.getMessage());
        }
    }

    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private static String convertResponseToString(BatchAnnotateImagesResponse response) {
        StringBuilder message = new StringBuilder("");

        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        if (labels != null) {
            for (EntityAnnotation label : labels) {
                message.append(String.format(Locale.US, "%.3f:%s", label.getScore(), label.getDescription()));
                message.append("\n");
            }
        } else {
            message.append("nothing");
        }

        return message.toString();
    }

}