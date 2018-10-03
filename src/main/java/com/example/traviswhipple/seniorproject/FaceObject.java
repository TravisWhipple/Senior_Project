package com.example.traviswhipple.seniorproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by traviswhipple on 9/19/18.
 */


/**/
/*
CLASS NAME

    FaceObject - Contains all information about a face found in an image.

DESCRIPTION

    This Class stores information about a face that was found in an image. Each face has a unique
    ID that associates it with an image containing only the cropped face. FaceObject's store the
    original image it was found in. Faces can be compared using their ID, this is particularly
    useful for checking a collection already contains this face.

AUTHOR
    Travis Whipple

DATE
    9/01/2018
*/
/**/
public class FaceObject {

    private final String m_DIRECTORY;
    private Context m_context;
    private String m_FacePath;
    private String m_ImagePath;
    private FaceBoundaries m_FaceBoundaries;
    private Integer m_ID;


    /**/
    /*
    FaceObject(Context a_context, String a_imagePath, FaceBoundaries a_faceBoundaries)

    NAME
        FaceObject(Context a_context, String a_imagePath, FaceBoundaries a_faceBoundaries)
            - Constructor for a new FaceObject.

    SYNOPSIS
        FaceObject(Context a_context, String a_imagePath, FaceBoundaries a_faceBoundaries)

            a_context           --> Context of parent Activity, used to define what directory
                                    to save the cropped face image into.
            a_imagePath         --> Path of image face was found in.
            a_faceBoundaries    --> Boundaries of this face.

    DESCRIPTION
        Constructor is used for creating a new FaceObject. This object has not yet been assigned
        an ID, meaning it has just been found within an image. To finalize this face and ID must
        be set.

    RETURNS
        None.

    AUTHOR
        Travis Whipple

    DATE
        9/01/2018
    */
    /**/
    public FaceObject(Context a_context, String a_imagePath, FaceBoundaries a_faceBoundaries){

        m_context = a_context;
        m_FacePath = null;
        m_ImagePath = a_imagePath;
        m_FaceBoundaries = a_faceBoundaries;
        m_ID = null;

        m_DIRECTORY = m_context.getResources().getString(R.string.project_directory)
                + "/"
                + m_context.getResources().getString(R.string.faces_directory);

        /* Create directory if it does not exist.
        Result is ignored as it will only create a directory if it does not exist already.
         */
        File dir = new File(m_DIRECTORY);
        dir.mkdirs();
    }
    /* FaceObject(Context a_context, String a_imagePath, FaceBoundaries a_faceBoundaries) */

    /**/
    /*
    FaceObject(Context a_context, String a_facePath, String a_imagePath,
                      FaceBoundaries a_faceBoundaries, int a_Id)

    NAME
        FaceObject(Context a_context, String a_facePath, String a_imagePath,
                      FaceBoundaries a_faceBoundaries, int a_Id)

    SYNOPSIS
        FaceObject(Context a_context, String a_facePath, String a_imagePath,
                      FaceBoundaries a_faceBoundaries, int a_Id)

            a_context           --> Context of parent Activity, used to define what directory
                                    to save the cropped face image into.
            a_facePath          --> Cropped face image path.
            a_imagePath         --> Path of image face was found in.
            a_faceBoundaries    --> Boundaries of this face.
            a_Id                --> ID unique to this face.

    DESCRIPTION
        Constructor is used when creating a FaceObject from an already found face. This is called
        when loading a face from a serialization file.

    RETURNS
        None.

    AUTHOR
        Travis Whipple

    DATE
        9/01/2018
    */
    /**/
    public FaceObject(Context a_context, String a_facePath, String a_imagePath,
                      FaceBoundaries a_faceBoundaries, int a_Id){

        m_context = a_context;
        m_FacePath = a_facePath;
        m_ImagePath = a_imagePath;
        m_FaceBoundaries = a_faceBoundaries;
        m_ID = a_Id;

        // Directory to save cropped faces.
        m_DIRECTORY = m_context.getResources().getString(R.string.project_directory)
                + "/"
                + m_context.getResources().getString(R.string.faces_directory);
    }
    /* FaceObject(Context a_context, String a_facePath, String a_imagePath,
                      FaceBoundaries a_faceBoundaries, int a_Id) */

    /**/
    /*
    GetFaceBitmap()

    NAME
        Bitmap GetFaceBitmap()  - Gets bitmap of face.

    DESCRIPTION
        This function will return a bitmap cropped from the original image that only contains
        the face.

    RETURNS
        Bitmap - bitmap of face.

    AUTHOR
        Travis Whipple

    DATE
        9/01/2018
    */
    /**/
    public Bitmap GetFaceBitmap(){

        // Get crop face from the original image's bitmap.
        BitmapResource bitmapResource = new BitmapResource();

        // Using BitmapResource to ensure proper orientation.
        Bitmap originalImageBitmap = bitmapResource.GetBitmapFromPath(m_ImagePath);
        return bitmapResource.GetCroppedFace(originalImageBitmap, m_FaceBoundaries);
    }
    /* GetFaceBitmap */

    /**/
    /*
    Getters

    NAME
        Getters

    DESCRIPTION
        Generic getters to get member data.

    RETURNS
        String - path of images face or original image.
        or
        int - this FaceObject's unique id.

    AUTHOR
        Travis Whipple

    DATE
        9/01/2018
    */
    /**/
    public String GetImagePath(){
        return m_ImagePath;
    }

    public String GetFacePath(){
        return m_FacePath;
    }

    public int GetId(){
        return m_ID;
    }
    /* Getters */

    /**/
    /*
    SetId(int a_Id)

    NAME
        void SetId(int a_Id)  - Sets images id and saves bitmap of face.

    SYNOPSIS
        SetId(int a_Id)
            a_Id    --> ID unique to only this FaceObject.

    DESCRIPTION
        This function will set this FaceObject's ID. Once the ID is set we can now save the
        cropped images face to a sub directory specifically for storing face's cropped images.
        A FaceObject's ID correlates exactly to the name of the cropped image.

    RETURNS
        Void.

    AUTHOR
        Travis Whipple

    DATE
        9/01/2018
    */
    /**/
    public void SetId(int a_Id){
        m_ID = a_Id;
        SaveFaceToDirectory();
    }
    /* SetID(int a_ID) */

    /**/
    /*
    SaveFaceToDirectory()

    NAME
        void SaveFaceToDirectory() - Saves a cropped image only containing the face.

    DESCRIPTION
        This function will create a cropped image containing only the face that was found. It will
        ensure that the orientation of the image is correct before cropping out the face. This
        prevents faces from displayed sideways. The cropped images will be stored in a directory
        only used to store faces.

    RETURNS
        Void.

    AUTHOR
        Travis Whipple

    DATE
        9/01/2018
    */
    /**/
    private void SaveFaceToDirectory(){


        // Create directories to store faces in.
//        String projectDirName = m_context.getResources().getString(R.string.project_directory);
//        File projectDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + projectDirName);
//        boolean ret = projectDir.mkdirs();

        // Get directory to store faces in.
        File facesDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + m_DIRECTORY);
        boolean ret = facesDirectory.mkdirs();

        // File name is simply just the ID.
        String fileName = Integer.toString(m_ID);

        // Create file for image.
        File file = new File(facesDirectory.getAbsolutePath() + "/" + fileName + ".jpeg");

        try{

            Bitmap faceBitmap = GetFaceBitmap();
            FileOutputStream outputStream = new FileOutputStream(file);

            // Save bitmap of face in Faces directory.
            faceBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            // Save path of newly created image.
            m_FacePath = file.getAbsolutePath();

        }catch(IOException e){
            Log.e("FaceObject.SaveFaceToDirectory", "Error creating file: " + e.getMessage());
        }
    }
    /* SaveFaceToDirectory() */

    /**/
    /*
    GetSerializedString(String a_delimiter)

    NAME
        GetSerializedString(String a_delimiter)  - Gets all member data in a serialized string,
                                                   separated by a given delimiter.

    SYNOPSIS
        String GetSerializedString(String a_delimiter)
            a_delimiter    --> Delimiter used to differentiate between data elements.

    DESCRIPTION
        This function will return all member variables in a string separated by a given delimiter.

    RETURNS
        String - serialized string.

    AUTHOR
        Travis Whipple

    DATE
        9/01/2018
    */
    /**/
    public String GetSerializedString(String a_delimiter){

        String data = "";
        data += m_ImagePath;

        data += a_delimiter;
        data += m_FacePath;

        data += a_delimiter;
        data += Integer.toString(m_ID);

        data += a_delimiter;
        data += m_FaceBoundaries.GetSerializedString(a_delimiter);

        return data;
    }
    /* GetSerializedString(String a_delimiter) */

    /**/
    /*
    @Override
    public boolean equals(Object a_obj)

    NAME
        @Override
        public boolean equals(Object a_obj) - Compares based on ID.

    SYNOPSIS
        @Override
        public boolean equals(Object a_obj)
            a_obj   --> other object to be compared to.

    DESCRIPTION
        This function overrides the base equals function as when we compare two FaceObjects, we
        only need to compare their ID's to see if they are the same or different.

    RETURNS
        boolean - True if objects are the same.
                - False if objects are not the same.

    AUTHOR
        Travis Whipple

    DATE
        9/01/2018
    */
    /**/
    @Override
    public boolean equals(Object a_obj) {

        if(a_obj instanceof FaceObject){
            FaceObject faceOther = (FaceObject)a_obj;
            return this.m_ID.equals(faceOther.m_ID);
        }
        return false;
    }
    /* equals(Object a_obj) */

}
/* class FaceObject */
