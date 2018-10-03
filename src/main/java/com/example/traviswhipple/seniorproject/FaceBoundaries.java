package com.example.traviswhipple.seniorproject;

import android.graphics.Bitmap;
import com.google.android.gms.vision.face.Face;


/**/
/*
CLASS NAME
    FaceBoundaries - Contains information about where a face is within an image.

DESCRIPTION
    This Class will store information about where a face is within an image. Face Boundaries
    holds information about the location of an imaginary rectangle around a face in an image.
    It has functionality to get a Bitmap of only the face from an image. Boundaries of a face are
    checked to ensure they do not exceed the boundaries of the given image.

AUTHOR
    Travis Whipple

DATE
    9/01/2018
*/
/**/
public class FaceBoundaries{

    // Boundaries of face.
    private int m_startX;
    private int m_startY;
    private int m_endX;
    private int m_endY;

    /**/
    /*
    FaceBoundaries()

    NAME
        FaceBoundaries() - Constructor

    DESCRIPTION
        Constructor will initialize all boundaries for face to zero.

    AUTHOR
        Travis Whipple

    DATE
        9/01/2018
    */
    /**/
    public FaceBoundaries(){
        m_startX = 0;
        m_startY = 0;
        m_endX = 0;
        m_endY = 0;
    }
    /* FaceBoundaries() */


    /**/
    /*
    FaceBoundaries(Bitmap a_originalImageBitmap, Face a_face)

    NAME
        FaceBoundaries(Bitmap a_originalImageBitmap, Face a_face)
            - Constructor initializes boundaries from given a_face.

    SYNOPSIS
        FaceBoundaries(Bitmap a_originalImageBitmap, Face a_face)

            a_originalImageBitmap   --> Bitmap of original image that contains the face.
            a_face                  --> Face detected in image.

    DESCRIPTION
        Sets boundaries of a given face with respect to the given bitmap. It will ensure
        that boundaries do not exceed boundaries of the original image bitmap. If a face's
        boundaries exceed the limits of the image, then they will be adjusted to match the limits
        of the image.

    RETURNS
        None.

    AUTHOR
        Travis Whipple

    DATE

        9/01/2018
    */
    /**/
    public FaceBoundaries(Bitmap a_originalImageBitmap, Face a_face){

        int imageHeight = a_originalImageBitmap.getHeight();
        int imageWidth  = a_originalImageBitmap.getWidth();

        m_startX = (int)a_face.getPosition().x;
        m_startY = (int)a_face.getPosition().y;
        m_endX = (int)a_face.getWidth();
        m_endY = (int)a_face.getHeight();

        /* face may have boundaries outside of image, we have to make sure that all values
        are within the boundaries of the images bitmap.
         */
        if(m_startX < 0){
            m_startX = 0;
        }

        if(m_startY < 0){
            m_startY = 0;
        }

        // Check that the width and height of face do not exceed images boundaries.
        if(m_startX + m_endX > imageWidth){
            m_endX = imageWidth - m_startX;
        }

        if(m_startY + m_endY > imageHeight){
            m_endY = imageHeight - m_startY;
        }
    }
    /* FaceBoundaries(Bitmap a_originalImageBitmap, Face a_face) */

    /**/
    /*
    SetFromSerializedData(int a_StartX, int a_StartY, int a_EndX, int a_EndY)

    NAME
        void SetFromSerializedData(int a_StartX, int a_StartY, int a_EndX, int a_EndY)
            - Sets face boundaries form serialized data.

    SYNOPSIS
        SetFromSerializedData(int a_StartX, int a_StartY, int a_EndX, int a_EndY)

            a_StartX    --> X axis of beginning of face boundary.
            a_StartY    --> Y axis of beginning of face boundary.
            a_EndX      --> X axis of end of face boundary.
            a_EndY      --> Y axis of end of face boundary.

    DESCRIPTION
        Sets boundaries of a given face with respect to the given bitmap. It will ensure
        that boundaries do not exceed boundaries of the original image bitmap. If a face's
        boundaries exceed the limits of the image, then they will be adjusted to match the limits
        of the image.

    RETURNS
        void.

    AUTHOR
        Travis Whipple

    DATE
        9/01/2018
    */
    /**/
    public void SetFromSerializedData(int a_StartX, int a_StartY, int a_EndX, int a_EndY){
        m_startX = a_StartX;
        m_startY = a_StartY;
        m_endX = a_EndX;
        m_endY = a_EndY;
    }
    /* SetFromSerializedData(int a_StartX, int a_StartY, int a_EndX, int a_EndY) */

    /**/
    /*
    Getters

    NAME
        Getters - gets face boundary information.

    SYNOPSIS
        Getters - Generic getters return values of face boundaries.

    DESCRIPTION
        Getters are used to get information out of this Class.

    RETURNS
        int - Location of boundary.

    AUTHOR
        Travis Whipple

    DATE
        9/01/2018
    */
    /**/
    public int getStartX(){
        return m_startX;
    }

    public int getStartY(){
        return m_startY;
    }

    public int getEndX(){
        return m_endX;
    }

    public int getEndY(){
        return m_endY;
    }
    /* Getters */

    /**/
    /*
    GetSerializedString()

    NAME
        String GetSerializedString() - Gets all data in a format used for serialization.

    SYNOPSIS
        GetSerializedString(String a_delimiter)
            a_delimiter     --> Delimiter to be used to separate elements.

    DESCRIPTION
        Creates a string of all information separate by the given delimiter.

    RETURNS
        String - Contains all FaceBoundaries data separated by delimiter.

    AUTHOR
        Travis Whipple

    DATE
        9/01/2018
    */
    /**/
    public String GetSerializedString(String a_delimiter){

        String serializeString = "";

        serializeString += Integer.toString(m_startX) + a_delimiter;
        serializeString += Integer.toString(m_startY) + a_delimiter;
        serializeString += Integer.toString(m_endX) + a_delimiter;
        serializeString += Integer.toString(m_endY);

        return serializeString;
    }
    /* GetSerializedString(String a_delimiter) */
}
/* class FaceBoundaries */