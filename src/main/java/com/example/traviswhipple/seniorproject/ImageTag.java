package com.example.traviswhipple.seniorproject;

import java.util.Vector;

/**
 * Created by traviswhipple on 3/12/18.
 */

public class ImageTag {

    private String image;

    private Vector<LoadedImage> images;

    public ImageTag(String a_image){
        image = a_image;
        images = new Vector<LoadedImage>();
    }




}
