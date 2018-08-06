package com.example.traviswhipple.seniorproject;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by traviswhipple on 3/12/18.
 */


public class ImageAdapter extends BaseAdapter {

    private Context mContext;

    // PathList contains all paths in order how they appear in apps grid view.
    // This is so that their position (relative to first photo) is associated with correct path.
    ArrayList<String> pathList = new ArrayList<String>();

    public ImageAdapter(Context c) {
        mContext = c;
    }

    void add(String path){
        pathList.add(path);
    }

    public void removeAllViews(){
        pathList.clear();
    }

    @Override
    public int getCount() {
        return pathList.size();
    }

    @Override
    public String getItem(int arg0) {
        // TODO Auto-generated method stub

        return pathList.get(arg0);
        //return null;
    }

    public int getItem(String id){
        return pathList.indexOf(id);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int width = 200;
        int height = 80;
        height = width;
        int padding = 1;

        ImageView imageView;

        if (convertView == null) {

            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(width, height));
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setPadding(padding, padding, padding, padding);



            imageView.setCropToPadding(false);
            imageView.setAdjustViewBounds(true);

            imageView.setId(position);

        } else {
            imageView = (ImageView) convertView;
        }


        /*
         Use glide to load images to avoid out of memory errors.
         https://github.com/bumptech/glide/blob/master/LICENSE

         More on Glide:
         https://inthecheesefactory.com/blog/get-to-know-glide-recommended-by-google/en
          */
        Glide.with(mContext)
                .load(pathList.get(position))
                .into(imageView);

        return imageView;
    }
}
