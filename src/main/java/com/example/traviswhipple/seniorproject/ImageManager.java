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

    public void addTagRelation(ImageObject imageObject, Tag tagObject){
        addTagRelation(imageObject, tagObject, 1);
    }

    public void addTagRelation(ImageObject imageObject, Tag tagObject, double confidence){

        if(!images.contains(imageObject)){
            Log.e("IM.addTagRelation", "does not contain image: " + imageObject.toString());
            images.add(imageObject);
        }

        if(tags.contains(tagObject)){
            Log.e("IM.addTagRelation", "ALREADY CONTAINS TAG: " + tagObject.toString());
            tagObject = getTagObject(tagObject.getTagName());

            tagObject.addImage(imageObject, confidence);
            imageObject.addTag(tagObject);

        }else{
            //Add the new tag.
            Log.e("IM.addTagRelation", "Adding tag: " + tagObject.toString());

            tagObject.addImage(imageObject, confidence);
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

        double confidenceLimit = .97d;
        final double LOWER_LIMIT = .60d;
        final double FACTOR = .40d;

        ArrayList<ImageObject> similarImages = new ArrayList<>();


        // Trying to set confidence limit by using the lowest confidence on the top 40% results.
        ArrayList<Tag> tags = image.getTags();

        // Get top 40% of tags.
        int index = (int) Math.ceil(tags.size() * FACTOR);

        // Index of base 0 array.
        index -= 1;

        double outerConfidenceLimit = .0;
        for(int i = index; i >= 0; i--){
            double lowestOfTop40 = tags.get(i).getImageConfidence(image);

            if(lowestOfTop40 > LOWER_LIMIT){
                outerConfidenceLimit = lowestOfTop40;
                break;
            }
        }



        for(Tag tag : image.getTags()) {

            if (tag.getImageConfidence(image) >= outerConfidenceLimit) {

                //similarImages.addAll(tag.getImageList());
                for (ImageObject io : tag.getImageList()) {

                    if (tag.getImageConfidence(io) >= confidenceLimit) {

                        if(!similarImages.contains(io)){
                            similarImages.add(io);
                        }
                    }
                }
            }
        }

        while(similarImages.contains(image)){
            similarImages.remove(image);
        }

        return similarImages;
    }
}
