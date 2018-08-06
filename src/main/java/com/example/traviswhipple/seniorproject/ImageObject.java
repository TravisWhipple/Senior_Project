package com.example.traviswhipple.seniorproject;

import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Vector;

/**
 * Created by traviswhipple on 3/12/18.
 */

public class ImageObject implements Comparable<ImageObject>  {

    private Long mDate;
    private int imageID;
    private String mPath;
    private String mName;

    private ArrayList<Tag> tags;

    public ImageObject(){
        initialize();
    }

    public ImageObject(String path){
        initialize();
        mPath = path;
    }

    public ImageObject(String path, String name, int ID){
        initialize();
        setID(ID);
        setPath(path);
        setName(name);
    }

    public ImageObject(String path, String name, int ID, Long dateTaken){
        initialize();
        setID(ID);
        setPath(path);
        setName(name);
        setDateTaken(dateTaken);
    }

    public void setEqual(ImageObject other){
        setID(other.getImageID());
        setName(other.getName());
        setDateTaken(other.getDate());
        setPath(other.getPath());
    }


    private void initialize(){
        tags = new ArrayList<>();
        imageID = -1;
        mPath = "";
        mName = "";
        mDate = Long.parseLong("0");
    }

    public boolean setID(int ID){
        // Only set ID if ID has not been set already.
        if(imageID == -1){
            imageID = ID;
            return true;
        }else{
            return false;
        }
    }

    public boolean setPath(String path){

        // Will only set path if not set before.
        if(mPath == ""){
            mPath = path;
            return true;
        }else{
            return false;
        }
    }

    public boolean setName(String imageName){
        if(mName == ""){
            mName = imageName;
            return true;
        }else{
            return false;
        }
    }

    public boolean setDateTaken(Long date){
        mDate = date;
        return true;
    }

    public Long getDate(){
        return mDate;
    }

    public String getPath(){
        return mPath;
    }

    public String getName(){
        return mName;
    }

    public boolean addTag(Tag tag){
        if(!tags.contains(tag)){
            return tags.add(tag);
        }
        return false;
    }

    public int getImageID(){
        return imageID;
    }

    public ArrayList<Tag> getTags(){
        return tags;
    }

    public int getNumTags(){
        return tags.size();
    }

    @Override
    public int hashCode() {
        return mName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if(obj instanceof ImageObject){

            ImageObject io = (ImageObject)obj;

            return this.mPath.equals(io.mPath);
            //return mPath.equals(((ImageObject) obj).mPath);
        }

        if(obj instanceof String){
            //return obj.equals(this.mPath);
            return this.mPath.equals(obj);
        }

        return false;
    }

    @Override
    public String toString(){
        return mPath;
    }

    @Override
    public int compareTo(ImageObject other) {
        return Comparators.DATE.compare(this, other);
    }

    public static class Comparators {

        public static Comparator<ImageObject> DATE = new Comparator<ImageObject>(){
            @Override
            public int compare(ImageObject image1, ImageObject image2){
                return image1.mDate.compareTo(image2.mDate);
            }
        };
    }
}
