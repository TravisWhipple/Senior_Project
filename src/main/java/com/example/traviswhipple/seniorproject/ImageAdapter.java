package com.example.traviswhipple.seniorproject;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import java.util.ArrayList;


/**/
/*
CLASS NAME

    ImageAdapter - Used to inflate a View with images.

DESCRIPTION

    This class is used for inflating a View, mainly a GridView, with images. This class
    overcomes the issue of running out of memory due to loading a large gallery. Images
    are loaded into View dynamically using Glide. Glide helps to eliminate memory errors
    as images are only loaded on a as need basis, were a regular View loads all images
    into memory regardless if they are outside the frame.

AUTHOR
    Travis Whipple

DATE
    3/12/2018
*/
/**/
public class ImageAdapter extends BaseAdapter {

    // m_pathList stores paths of all images to be displayed in view.
    ArrayList<String> m_pathList = new ArrayList<String>();
    private Context m_Context;
    private int m_displayWidth;


    /**/
    /*
    ImageAdapter(Context a_context)

    NAME
        ImageAdapter(Context a_context) - General Constructor.

    SYNOPSIS
        ImageAdapter(Context a_context)
            a_context       --> Context of parent activity, used for creating new Views.
            a_displayWdith  --> Width of the display.

    AUTHOR
        Travis Whipple

    DATE
        3/12/2018
    */
    /**/
    public ImageAdapter(Context a_context, int a_displayWdith) {
        m_Context = a_context;
        m_displayWidth = a_displayWdith;
    }

    /**/
    /*
    NAME
        Add(String a_path) - Adds an images path to the view.

    SYNOPSIS
        void Add(String a_path)
            a_path     --> Path of image to be loaded into view.

    DESCRIPTION
        This function will add an images absolute path to the adapter to later be inflated
        into an ImageView.

    RETURNS
        Void.

    AUTHOR
        Travis Whipple

    DATE
        03/12/2018
    */
    /**/
    void Add(String a_path){
        m_pathList.add(a_path);
    }

    /**/
    /*
    NAME
        RemoveAllViews() - Removes all elements from view.

    DESCRIPTION
        This function will remove all elements within its parent view. When all views are removed
        from, lets say a GridView, then when the GridView is in is updated, GridView no longer
         draws anything to the screen.

    RETURNS
        Void.

    AUTHOR
        Travis Whipple

    DATE
        03/12/2018
    */
    /**/
    public void RemoveAllViews(){
        m_pathList.clear();
    }

    /**/
    /*
    Override parent functions.

    DESCRIPTION
        Override Parent functions to work with this custom adapter. Have to overload Parents
        getCount(), getItem(), getItemId() and getView() to work correctly with our data set.

    AUTHOR
        Travis Whipple

    DATE
        03/12/2018
    */
    /**/
    @Override
    public int getCount() {
        return m_pathList.size();
    }

    @Override
    public String getItem(int index) {
        return m_pathList.get(index);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**/
    /*
    getView(int position, View convertView, ViewGroup parent)

    NAME
        getView(int a_position, View a_convertView, ViewGroup a_parent)
            - Gets a View at a given position.

    SYNOPSIS
        View getView(int a_position, View a_convertView, ViewGroup a_parent)

            a_position      --> Position of View to return.
            a_convertView   --> Old View to to be reused, increases performance reusing
                                old view rather than creating new ones.
            a_parent        --> Not needed for our uses, but still needed in function definition
                                in order to override Parents getView function.

    DESCRIPTION
        This function will return a View for the requested position. This function will only
        be called by Android on a screen update. To allow for faster loading of images into
        memory along with less overall memory pressure, Glide will by used to Load images
        dynamically.

    RETURNS
        View   - An ImageView containing the image at the given position.

    AUTHOR
        Travis Whipple

    DATE
        9/01/2018
    */
    /**/
    @Override
    public View getView(int a_position, View a_convertView, ViewGroup a_parent) {

        int viewSize = m_displayWidth / 6;
        int padding = 1;

        ImageView imageView = null;

        if (a_convertView == null) {

            // If it's not recycled, create a new View.
            imageView = new ImageView(m_Context);
            imageView.setLayoutParams(new GridView.LayoutParams(viewSize, viewSize));
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setPadding(padding, padding, padding, padding);

            imageView.setCropToPadding(false);
            imageView.setAdjustViewBounds(true);

            imageView.setId(a_position);

        } else {
            // Recycle old view.
            imageView = (ImageView) a_convertView;
        }

        /*
        Using glide to Load images to avoid out of memory errors.
        https://github.com/bumptech/glide/blob/master/LICENSE

        More on Glide:
        https://inthecheesefactory.com/blog/get-to-know-glide-recommended-by-google/en

        This will clear glides cache every 5 minutes to prevent from loading an old image from
        its cache.
        */
        int fiveMinuteInMillisecods = 5 * 60 * 1000;
        StringSignature strSig = new StringSignature(String.valueOf(
                System.currentTimeMillis()/fiveMinuteInMillisecods));
        Glide.with(m_Context)
                .load(m_pathList.get(a_position))
                .signature(strSig)
                .into(imageView);

        return imageView;
    }
    /* getView(int a_position, View a_convertView, ViewGroup a_parent) */
}
/* class ImageAdapter extends BaseAdapter */