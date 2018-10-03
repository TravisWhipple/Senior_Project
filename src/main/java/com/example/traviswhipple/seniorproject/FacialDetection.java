package com.example.traviswhipple.seniorproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;

import java.util.ArrayList;


/**/
/*
CLASS NAME

    FacialDetection - Detects faces within an image.

DESCRIPTION

    This class implements Google Play Service for facial detection. This class can detect many
    faces within an image. For each face found a FaceObject will be return where it can later
    be assigned a unique ID.

REFERENCES

    Google Play Service documentation.
    https://developers.google.com/android/guides/overview

AUTHOR
    Travis Whipple

DATE
    8/18/2018
*/
/**/
public class FacialDetection {

    private Context m_Context;
    private com.google.android.gms.vision.face.FaceDetector m_detector;

    /**/
    /*
    FacialDetection(Context a_context)

    NAME
        FacialDetection(Context a_context) - Constructor initializes google's face detector.

    SYNOPSIS
        FacialDetection(Context a_context)

            a_context           --> Context of parent Activity, used to initialize Google's
                                    face detector.

    DESCRIPTION
        Constructor is used for creating a new FacialDetection object. It initializes Google's
        Play Store Face Detector to analyze in accurate mode. This will allow the detector
        to make more precise calculations, while it is marginally more costly, the improved
        accuracy allows for almost no garbage results.

    RETURNS
        None.

    AUTHOR
        Travis Whipple

    DATE
        9/01/2018
    */
    /**/
    public FacialDetection(Context a_context){

        m_Context = a_context;

        // Set m_detector to allow it to detect as much information as possible.
        m_detector = new com.google.android.gms.vision.face.FaceDetector.Builder(m_Context)
                .setTrackingEnabled(false)
                .setLandmarkType(com.google.android.gms.vision.face
                        .FaceDetector.ALL_LANDMARKS)
                .setClassificationType(com.google.android.gms.vision.face
                        .FaceDetector.ACCURATE_MODE)
                .build();
    }
    /* FacialDetection(Context a_context) */

    /**/
    /*
    DetectFaces(ImageObject a_imagePath)

    NAME
        ArrayList<FaceObject> DetectFaces(ImageObject a_imagePath)
            - Returns a list of all faces found within image.

    SYNOPSIS
        ArrayList<FaceObject> DetectFaces(String a_imagePath)

            a_imagePath     --> Path of image to detect faces in.

    DESCRIPTION
        This function will try to detect faces for a given image. It can detect multiple faces
        within a single image. Location of each faces along with the faces boundaries are
        returned.

    RETURNS
        ArrayList<FaceObject>   - A list of all faces found in the image as FaceObjects.

    AUTHOR
        Travis Whipple

    DATE
        9/01/2018
    */
    /**/
    public ArrayList<FaceObject> DetectFaces(String a_imagePath){

        // Get bitmap of image from parameter.
        BitmapResource bitmapResource = new BitmapResource();
        Bitmap bitmap = bitmapResource.GetBitmapFromPath(a_imagePath);

        // List of found faces.
        ArrayList<FaceObject> detectedFaceObjects = new ArrayList<>();

        // The face detector analyzes a frame, so convert bitmap into a frame.
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();

        // Detect faces!
        SparseArray<Face> faces = m_detector.detect(frame);

        // Create a FaceObject for each face in image.
        for(int i = 0; i < faces.size(); i++){

            Face face = faces.valueAt(i);
            FaceBoundaries faceBoundaries = new FaceBoundaries(bitmap, face);

            FaceObject faceObject = new FaceObject(m_Context, a_imagePath, faceBoundaries);
            detectedFaceObjects.add(faceObject);
        }

        // Log how many faces were found in image.
        Log.i("FacialDetection", "Found " + Integer.toString(faces.size())
                + " faces in image: " + a_imagePath);
        return detectedFaceObjects;
    }
    /* DetectFaces(String a_imagePath) */
}
/* FacialDetection */
