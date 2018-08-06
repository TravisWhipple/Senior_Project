package com.example.traviswhipple.seniorproject;

/**
 * Created by traviswhipple on 7/23/18.
 */

import java.util.Vector;

public class ImageObjectCopy {

    private String imageID;
    private int intId;
    private String mPath;
    private String mName;


    private Vector<String> images;
    private Vector<Vector<String>> tags;

    private Vector<Tag> newTags;

    public ImageObjectCopy(){
        initialize();
    }

    public ImageObjectCopy(String id){
        initialize();
        imageID = id;
    }

    public ImageObjectCopy(String path, String name, int id){
        initialize();
        setPath(path);
        setName(name);
        setId(id);
    }

    private void initialize(){
        tags = new Vector<Vector<String>>();
        images = new Vector<String>();

        newTags = new Vector<>();
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

    public String getPath(){
        return mPath;
    }

    public void addTag(String image, String tag){

        int index = images.indexOf(image);

        if(index == -1){
            addImage(image);
            addTag(image, tag);
            return;
        }

        tags.elementAt(index).add(tag);

    }

    public void addImage(String image){
        images.add(image);
        Vector<String> tempVector = new Vector<>();
        tags.add(tempVector);
    }

    public String getImageID(){
        return imageID;
    }

    public Vector<String> getTags(String image){
        int index = images.indexOf(image);

        if(index == -1){
            addImage(image);
            return new Vector<String>();
        }else{
            return tags.elementAt(images.indexOf(image));
        }
    }

    public int getNumImages(){
        return images.size();
    }

    public int getNumTags(String image){
        int index = images.indexOf(image);
        if(index == -1){
            return 0;
        }else{
            return tags.elementAt(index).size();
        }
    }

    public Vector<String> getSimilarImages(String image){
        Vector<String> similarImages = new Vector<>();

        int index = images.indexOf(image);

        if(index == -1){
            return similarImages;
        }

        Vector<String> tagsToBeFound = new Vector<>();

        for(String currentImage : images){
            for(String currentTag : tags.elementAt(images.indexOf(currentImage))){
                if(tags.elementAt(images.indexOf(image)).contains(currentTag)){
                    similarImages.add(currentImage);
                    break;
                }
            }
        }

        similarImages.remove(image);

        return similarImages;
    }
}
