package com.example.traviswhipple.seniorproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

/**/
/*
CLASS NAME
        BitmapResource - Creates and modifies bitmaps.

DESCRIPTION
        This class will modify and create bitmaps from image paths, or existing bitmaps. All
        bitmaps created will be returned in original orientation as BitmapFactory sometimes
        re-orients images, orientation can be vital for facial detection or label detection.
        This class currently supports functionality to create bitmaps, resize bitmaps and crop
        bitmaps.

AUTHOR
        Travis Whipple

DATE
        8/23/2018
*/
/**/


public class BitmapResource {

    // Constructor does nothing.
    public BitmapResource(){
    }

    /**/
    /*
    Bitmap GetBitmapFromPath(String a_imagePath)

    NAME
        GetBitmapFromPath(String a_imagePath) - gets un-cropped bitmap from image path.

    SYNOPSIS
        Bitmap GetBitmapFromPath(String a_imagePath)

            a_imagePath     --> Absolute path of image source to get bitmap.

    DESCRIPTION
        This function will create a bitmap from the given path. Bitmaps orientation will be
        corrected if it differs from original images orientation.

    RETURNS
        Returns a bitmap in correct orientation of image at specified path.

    AUTHOR
        Travis Whipple

    DATE
        8/23/2018

    */
    /**/
    public Bitmap GetBitmapFromPath(String a_imagePath){

        // Get bitmap from image path.
        Bitmap bitmap = BitmapFactory.decodeFile(a_imagePath);

        try{
            // ExifInterface has information about rotation of image.
            ExifInterface exifInterface = new ExifInterface(a_imagePath);

            // Get ExifInterface for image. This contains information about images orientation.
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            // Matrix is used for rotating image by X degrees.
            Matrix matrix = new Matrix();

            matrix.postRotate(0);

            // Rotate image back to its original orientation.
            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    // Rotate image back to its original 90 degrees orientation.
                    matrix.postRotate(90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    // Rotate image back to its original 180 degrees orientation.
                    matrix.postRotate(180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    // Rotate image back to its original 270 degrees orientation.
                    matrix.postRotate(270);
                    break;
            }

            // Rotate bitmap to its original orientation.
            bitmap = Bitmap.createBitmap(
                    bitmap,
                    0,
                    0,
                    bitmap.getWidth(),
                    bitmap.getHeight(),
                    matrix,
                    true);

        }catch(Exception e){
            Log.e("BitmapResource.GetBitmapFromPath", "Image: "
                    + a_imagePath + "IOException: " + e.getMessage());

        }

        return bitmap;
    }
    /* Bitmap GetBitmapFromPath(String a_imagePath) */


    /**/
    /*
    Bitmap GetCroppedFace(Bitmap a_bitmap, FaceBoundaries a_FaceBoundaries)

    NAME
        GetCroppedFace(Bitmap a_bitmap, FaceBoundaries a_FaceBoundaries) - Crops bitmap around
            given face boundaries.

    SYNOPSIS
        Bitmap GetCroppedFace(Bitmap a_bitmap, FaceBoundaries a_FaceBoundaries)

            a_bitmap            --> Bitmap to be cropped.
            a_FaceBoundaries    --> Boundaries of face.

    DESCRIPTION
        This function will create a bitmap that has been cropped from original bitmap on the
        boundaries passed in.

    RETURNS
        Returns a bitmap cropped to only contain a face at the given boundaries.

    AUTHOR
        Travis Whipple

    DATE
        8/23/2018

    */
    /**/
    public Bitmap GetCroppedFace(Bitmap a_bitmap, FaceBoundaries a_FaceBoundaries){

        // Crop bitmap to given boundaries.
        return Bitmap.createBitmap(a_bitmap,
                a_FaceBoundaries.getStartX(),
                a_FaceBoundaries.getStartY(),
                a_FaceBoundaries.getEndX(),
                a_FaceBoundaries.getEndY());
    }
    /* GetCroppedFace(Bitmap a_bitmap, FaceBoundaries a_FaceBoundaries) */


    /**/
    /*
    Bitmap GetScaledBitmap(String a_imagePath, int a_MaxDimension)

    NAME
        GetScaledBitmap(String a_imagePath, int a_MaxDimension)
            - Gets a scaled bitmap from original bitmap.

    SYNOPSIS
       Bitmap GetScaledBitmap(String a_imagePath, int a_MaxDimension)

            a_imagePath         --> Path of image to be scaled.
            a_MaxDimension      --> Maximum dimension for either height or width,
                                    whichever is larger.

    DESCRIPTION
        This function will create a bitmap that has been scaled proportionately to its original
        aspect ratio to not exceed the maximum dimension passed as a parameter. It will ensure
        that the returned bitmap is in correct orientation.

    RETURNS
        Returns a bitmap scaled to fit the maximum dimension.

    AUTHOR
        Travis Whipple

    DATE
        8/23/2018
    */
    /**/
    public Bitmap GetScaledBitmap(String a_imagePath, int a_MaxDimension){

        // Get bitmap of image.
        Bitmap bitmap = GetBitmapFromPath(a_imagePath);

        // Adjust size so that height nor width are greater than the maximum size.
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();

        // Max size of either dimension.
        int scaledWidth = a_MaxDimension;
        int scaledHeight = a_MaxDimension;

        // Scale bitmap down while retaining original aspect ratio.
        // If height and width are the same, scaling down will not affect ratio.
        if (originalHeight > originalWidth) {

            // Height is greater than width, so scale width while keeping aspect ratio the same.
            float aspectRatio = (float) originalWidth / (float) originalHeight;
            scaledWidth = (int) (scaledHeight * aspectRatio);

        } else{

            // Width is greater than height, so scale height while keeping aspect ratio the same.
            float aspectRatio = (float) originalHeight / (float) originalWidth;
            scaledHeight = (int) (scaledWidth * aspectRatio);
        }

        // Scale bitmap down based on above results.
        return Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, false);
    }
    /* GetScaledBitmap(String a_imagePath, int a_MaxDimension) */
}
/* class BitmapResource */


