package com.example.traviswhipple.seniorproject;

import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.Vector;

/**
 * Created by traviswhipple on 7/20/18.
 */

public class Tag {

    private String mTagName;
    private ArrayList<ImageObject> imageList;
    private ArrayList<Double> confidence;

    public Tag(){
        initializer();
        mTagName = "NO_NAME";
    }

    public Tag(String tagName){
        initializer();
        mTagName = tagName;
    }

    public Tag(String tagName, ImageObject image, Double a_confidence){
        initializer();
        mTagName = tagName;
        imageList.add(image);
        confidence.add(a_confidence);
    }

    private void initializer(){
        mTagName = "";
        imageList = new ArrayList<>();
        confidence = new ArrayList<>();
    }


    @Override
    public String toString(){
        return mTagName;
    }

    public void setTag(ImageObject a_imageObject, String a_tag, double a_confidence){
        mTagName = a_tag;
        imageList.add(a_imageObject);
        confidence.add(a_confidence);
    }

    public String getTagName(){
        return mTagName;
    }

    public boolean addImage(ImageObject image, double a_confidence){
        if(!imageList.contains(image)){
            boolean returnVall = confidence.add(a_confidence);
            boolean returnVal = imageList.add(image);
            return (returnVal && returnVall);
        }

        return false;
    }

    public ArrayList<ImageObject> getImageListAboveConfidence(double a_confidence){

        ArrayList<ImageObject> returnList = new ArrayList<>();

        for(int i = 0; i < imageList.size(); i++){
            if(confidence.get(i) > a_confidence){
                returnList.add(imageList.get(i));
            }
        }

        return returnList;
    }

    public ArrayList<ImageObject> getImageList(){
        return getImageListAboveConfidence(.1);
    }

    public boolean contains(ImageObject image){
        return imageList.contains(image);
    }

    public double getImageConfidence(ImageObject image){
        int index = imageList.indexOf(image);
        return confidence.get(index);
    }

    @Override
    public boolean equals(Object obj) {

        if(obj instanceof Tag){

            Tag tagOther = (Tag)obj;

            return this.mTagName.equals(tagOther.mTagName);
            //return mPath.equals(((ImageObject) obj).mPath);
        }

        if(obj instanceof String){
            //return obj.equals(this.mPath);
            return this.mTagName.equals(obj);
        }

        return false;
    }
}
