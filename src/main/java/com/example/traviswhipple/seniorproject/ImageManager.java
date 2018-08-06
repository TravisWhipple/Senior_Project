package com.example.traviswhipple.seniorproject;

/**
 * Created by traviswhipple on 7/23/18.
 */

import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class ImageManager {

    private String imageID;
    private int intId;
    private String mPath;
    private String mName;


    //private Vector<String> images;
    //private Vector<Vector<String>> tags;
    private ArrayList<ImageObject> images;
    private ArrayList<Tag> tags;

    private Map<String, ImageObject> imageMap;
    private Map<String, Tag> tagMap;


    public ImageManager(){
        initialize();
    }

    public ImageManager(String id){
        initialize();
        imageID = id;
    }

    public ImageManager(String path, String name, int id){
        initialize();
        setPath(path);
        setName(name);
        setId(id);
    }

    private void initialize(){
        tags = new ArrayList<>();
        images = new ArrayList<>();
    }

    public void setPath(String path){
        mPath = path;
    }

    public void setId(int id){
        intId = id;
    }

    public void setName(String imageName){
        mName = imageName;
    }

    public String getName(){
        return mName;
    }

    public int getIntId(){
        return intId;
    }

    public ArrayList<String> getAllPaths(){

        ArrayList<String> allPaths = new ArrayList<>();

        for(ImageObject image : images){
            allPaths.add(image.getPath());
        }

        return allPaths;
    }

    public void addTag(ImageObject imageObject, Tag tagObject){

        if(!images.contains(imageObject)){
            Log.e("addTag", "does not contain image +" + imageObject.toString());
            images.add(imageObject);
        }

        if(tags.contains(tagObject)){
            Log.e("addTag", "ALREADY CONTAINS TAG +" + tagObject.toString());
            tagObject = getTagObject(tagObject.getTagName());

            tagObject.addImage(imageObject, 1);
            imageObject.addTag(tagObject);

        }else{
            //Add the new tag.
            Log.e("addTag", "Adding tag" + tagObject.toString());

            tagObject.addImage(imageObject, 1);
            imageObject.addTag(tagObject);

            tags.add(tagObject);
        }
    }


    public void addImage(ImageObject image){
        if(!images.contains(image)){
            images.add(image);
        }else{
            ImageObject existingImage = images.get(images.indexOf(image));
            existingImage.setEqual(image);
        }
    }

    public void sort(){
        Collections.sort(images);
        Log.e("sort", "sort");
        Log.e("sort", "sort");
        Log.e("sort", "sort");
        Log.e("sort", "sort");

        for(ImageObject image : images){
            Log.e("Date", Long.toString(image.getDate()));
        }
    }

    public ImageObject getImageObject(String imagePath){
        ImageObject io = new ImageObject(imagePath);
        int index = images.indexOf(io);

        if(index == -1){
            return new ImageObject(imagePath);
        }
        return images.get(index);
    }

    public String getImagePathFromPosition(int position){
        return images.get(position).getPath();
    }

    public Tag getTagObject(String tag){


        Tag tagObject = new Tag(tag);
        int index = tags.indexOf(tagObject);

        if(index == -1){
            return new Tag(tag);
        }

        return tags.get(index);
    }

    public int getNumImages(){
        return images.size();
    }

    public ArrayList<ImageObject> getSimilarImages(String image){
        return getSimilarImages(getImageObject(image));
    }

    public ArrayList<ImageObject> getImages(){
        return images;
    }


    public ArrayList<ImageObject> getSimilarImages(ImageObject image){
        ArrayList<ImageObject> similarImages = new ArrayList<>();

        for(Tag tag : image.getTags()){
            similarImages.addAll(tag.getImageList());
        }

        similarImages.remove(image);

        return similarImages;
    }
}
