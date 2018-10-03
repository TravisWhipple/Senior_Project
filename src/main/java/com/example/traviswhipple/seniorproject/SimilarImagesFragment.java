package com.example.traviswhipple.seniorproject;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

//import com.google.api.services.vision.v1.model.AnnotateImageRequest;
//import com.google.api.services.vision.v1.model.AnnotateImageResponse;
//import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
//import com.google.api.services.vision.v1.model.EntityAnnotation;
//import com.google.api.services.vision.v1.model.Feature;
//import com.google.api.services.vision.v1.model.Image;

//import com.google.cloud.vision.v1.ImageAnnotatorClient;
//import com.google.protobuf.ByteString;
//import com.google.protobuf.Descriptors;

//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
import java.lang.ref.WeakReference;
//import java.lang.reflect.Type;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.function.BiConsumer;
//
//import static java.lang.System.out;

/**
 * Created by traviswhipple on 9/16/18.
 */

/**/
/*

CLASS
    SimilarImagesFragment  - Displays similar images to the current image in view.

DESCRIPTION
    This class displays images that are similar to the current image in view. This class can
    display photos that were taken on the same day, images with similar tags and images containing
    people in the current image. This Fragment can display all tags that were given to a photo.
    An analyze button will be displayed so the user can analyze the current image if it has not
    already been analyzed.

DATE
    4/29/2018
*/
/**/

public class SimilarImagesFragment extends Fragment {

    // Reference back to the Activity that created this Fragment.
    private WeakReference<MainActivity> m_MainActivity;

    // Position of current image in view.
    private int m_SelectedImagePosition;

    // Parent Activities data.
    private Context m_Context;
    private ImageManager m_ImageManager;

    // Views for displaying selected image and similar images.
    private ImageView m_SelectedImageView;
    private GridLayout m_mainGridLayout;

    // Layout for buttons.
    private LinearLayout m_actionButtonsLayout;
    private Boolean m_fragmentCreated;

    // Displays all tags within an image.
    private TagFragment m_TagFragment;

    /**/
    /*
    SetArguments

    NAME
        SetArguments()      - Sets arguments for Fragment.

    SYNOPSIS
        void SetArguments(MainActivity a_activity, ImageManager a_imageManager,
                            Context a_context, int a_position)
            a_activity          --> Parent Activity who created this Fragment.
            a_imageManager      --> Data set.
            a_context           --> Context of parent Activity
            a_position          --> Position of image selected.

    DESCRIPTION
        Simply sets member variables.

    AUTHOR
        Travis Whipple

    */
    /**/
    public void SetArguments(MainActivity a_activity, ImageManager a_imageManager,
                             Context a_context, int a_position){

        // Save passed data to member variables.
        m_MainActivity = new WeakReference<>(a_activity);
        m_ImageManager = a_imageManager;
        m_Context = a_context;
        m_SelectedImagePosition = a_position;
        m_fragmentCreated = false;
    }
    /* void SetArguments(MainActivity a_activity, ImageManager a_imageManager,
                            Context a_context, int a_position) */

    /**/
    /*
    onCreateView

    NAME
        onCreateView()  - Called when view is created.

    DESCRIPTION
        Inflates the view from the resource file for this Fragment.

    RETURNS

    AUTHOR
        Travis Whipple

    */
    /**/
    @Override
    public View onCreateView(LayoutInflater a_inflater, ViewGroup a_parent,
                             Bundle a_savedInstanceState) {

        // Inflate layout to use similar images layout XML file.
        return a_inflater.inflate(R.layout.similar_images_layout, a_parent, false);
    }
    /* View onCreateView(LayoutInflater a_inflater, ViewGroup a_parent,
                Bundle a_savedInstanceState) */

    /**/
    /*
    onViewCreated

    NAME
        onViewCreated()     - Called when view is inflated from resource file.

    SYNOPSIS
        void onViewCreated(View a_view, Bundle a_savedInstanceState)
            a_view      --> View created from inflating resource file.
            a_savedInstanceState    --> Ignored, but needed to overwrite function.

    DESCRIPTION
        This function sets all member View and will then call the proper functions to display
        the selected image and all similar iamges.

    AUTHOR
        Travis Whipple

    */
    /**/    @Override
    public void onViewCreated(View a_view, Bundle a_savedInstanceState) {

        // Parent view used to access all child views in layout.

        // Initialize views.
        m_actionButtonsLayout = a_view.findViewById(R.id.actionButtonsLayout);
        m_mainGridLayout = a_view.findViewById(R.id.mainGridLayout);
        m_SelectedImageView = a_view.findViewById(R.id.selectedImage);


        // Set view to display selected image.
        InitializeSelectedImageView();

        // Set view to display selected image.
        InitializeSimilarImages();
    }
    /* void onViewCreated(View a_view, Bundle a_savedInstanceState) */

    /**/
    /*
    InitializeSelectedImageView

    NAME
        InitializeSelectedImageView()   - Initializes the selected image into the View.

    DESCRIPTION
        This function will display the selected image large. When this image is clicked action
        buttons will be hidden or shown. These action buttons can be used to analyze the image
        or show the current tags associated with it.

    AUTHOR
        Travis Whipple

    */
    /**/
    private void InitializeSelectedImageView(){

        /* Set onClick listener for selectedImageView.
        This will hide and show different buttons along top of image. Mainly to exit out of
        large image view.
         */
        m_SelectedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Switch action buttons in and out of view when image is selected.
                if(m_actionButtonsLayout.getVisibility() == View.VISIBLE){
                    m_actionButtonsLayout.setVisibility(View.INVISIBLE);
                }else{
                    m_actionButtonsLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    /* void InitializeSelectedImageView() */

    /**/
    /*
    InitializeSimilarImages

    NAME
        InitializeSimilarImages()  - Loads all similar images into view.

    DESCRIPTION
        This function will remove all views from the display, except for the selected image.
        This function will draw all similar images to the view.

    AUTHOR
        Travis Whipple

    */
    /**/
    private void InitializeSimilarImages(){

        // Remove all views from main grid layout.
        if(m_mainGridLayout != null && m_mainGridLayout.getChildCount() > 0){
            m_mainGridLayout.removeAllViews();
        }

        // Get images path.
        String selectedImagePath = m_ImageManager.GetImagePathFromPosition(m_SelectedImagePosition);

        /*
        Load image into selectedImageView using Glide for optimal performance.
        This will clear glides cache every 5 minutes to prevent from loading an old image from
        its cache.
        */
        int fiveSecInMilli = 5 * 60 * 1000;
        Glide.with(m_Context)
                .load(selectedImagePath)
                .signature(new StringSignature(String.valueOf(
                        System.currentTimeMillis() / (fiveSecInMilli))))
                .into(m_SelectedImageView);

        m_SelectedImageView.setId(m_SelectedImagePosition);


        // Add similar photos to layout.
        int numSimilarImages = m_ImageManager.GetSimilarImages(selectedImagePath).size();
        if(numSimilarImages > 0){
            // Display how many similar images were found.
            String text = "Found " + Integer.toString(numSimilarImages) + " Similar Photo";

            // Add 's' to end of 'photo' if found more than one photo.
            text += numSimilarImages > 1 ? "s" : "";
            m_mainGridLayout.addView(CreateTextView(text));

            // Display all similar images.
            for(ImageObject image : m_ImageManager.GetSimilarImages(selectedImagePath)){
                m_mainGridLayout.addView(CreateImageView(image));
            }
        }

        // Add photos from this day to layout.
        numSimilarImages = m_ImageManager.GetSameDayPhotos(selectedImagePath).size();
        if(numSimilarImages > 0){
            // Add photos from this day to layout.
            String text = "Photos from " + m_ImageManager.GetImageObject(
                    selectedImagePath).GetDateString();
            m_mainGridLayout.addView(CreateTextView(text));

            for(ImageObject image : m_ImageManager.GetSameDayPhotos(selectedImagePath)){
                m_mainGridLayout.addView(CreateImageView(image));
            }
        }

        // Add photos with these people to layout.
        for(Person person : m_ImageManager.GetPeopleInImage(selectedImagePath)){

            // Display persons name.
            String text = "More photos of " + person.GetName();
            m_mainGridLayout.addView(CreateTextView(text));

            // Display all images with that person.
            for(String imagePath : person.GetAllImagePathsContainingPerson()){
                ImageObject tempObj = m_ImageManager.GetImageObject(imagePath);
                m_mainGridLayout.addView(CreateImageView(tempObj));
            }
        }
    }
    /* void InitializeSimilarImages() */

    /**/
    /*
    UpdateLayout

    NAME
        UpdateLayout()  - Updates the layout.

    DESCRIPTION
        Updates tags and initializes similar images.

    AUTHOR
        Travis Whipple

    */
    /**/
    public void UpdateLayout(){
        UpdateTags();
        InitializeSimilarImages();
    }
    /* void UpdateLayout() */


    /**/
    /*
    GetSelectedImagePath

    NAME
        GetSelectedImagePath()  - Gets the path of the current selected image.

    DESCRIPTION
        Returns the path of the current selected image.

    RETURN
        String      - Images path.

    AUTHOR
        Travis Whipple

    */
    /**/
    public String GetSelectedImagePath(){
        return m_ImageManager.GetImagePathFromPosition(m_SelectedImagePosition);
    }
    /* String GetSelectedImagePath() */

    /**/
    /*
    ActionButtonPressed

    NAME
        ActionButtonPressed()   - Called when an action button is selected.

    SYNOPSIS
        void ActionButtonPressed(View a_view)
            a_view      --> View who called this function.

    DESCRIPTION
        We will check if the button selected was the view tags button. If it was then we can
        create the TagFragment to display all tags for the current image. When the viewTagButton
        is selected it will either hide or show a TagFragment depending on if the Fragment
        was created already or not.

    AUTHOR
        Travis Whipple

    */
    /**/
    public void ActionButtonPressed(View a_view){

        if(a_view.getId() == R.id.viewTagsButton){
            /*
            When viewTagsButton is pressed all tags will either be shown or hidden depending
            on whether they are currently shown or not. This is so the user can show and hide
            them with the same button.
            */
            // Check if fragment was created.
            if(m_fragmentCreated){
                // If so, remove fragment from view.
                RemoveTagFragment();
            }else{
                // If not, Add fragment to view.
                AddTagFragment();
            }
        }
    }
    /* void ActionButtonPressed(View a_view) */

    /**/
    /*
    AddTagFragment

    NAME
        AddTagFragment()    - Creates and displays a TagFragment.

    DESCRIPTION
        This function will create and display a TagFragment to display all tags found for the
        current selected image.

    AUTHOR
        Travis Whipple

    */
    /**/
    public void AddTagFragment(){

        m_fragmentCreated = true;

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Create new fragment and set its arguments.
        m_TagFragment = new TagFragment();
        m_TagFragment.SetArguments(m_MainActivity.get(), m_ImageManager,
                m_Context, m_SelectedImagePosition);

        // Commit the fragment to display it.
        transaction.replace(R.id.tagFragment, m_TagFragment);
        transaction.commit();
    }
    /* void AddTagFragment() */

    /**/
    /*
    RemoveTagFragment

    NAME
        RemoveTagFragment()     - Removes the TagFragment.

    DESCRIPTION
        This function will attempt to remove the TagFragment if it exists.

    AUTHOR
        Travis Whipple

    */
    /**/
    public void RemoveTagFragment(){

        // Set flat to false cause Fragment is eiter removed, or about to be removed.
        m_fragmentCreated = false;

        // If tag fragment is null, then there's nothing to remove.
        if(m_TagFragment != null){
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.remove(m_TagFragment);
            ft.commit();
            m_TagFragment = null;
        }
    }
    /* void RemoveTagFragment() */

    /**/
    /*
    UpdateTags

    NAME
        UpdateTags()    - Will update the TagFragment's tags.

    DESCRIPTION
        This function will update the Tags currently in view to display changes. If the
        TagFragment was not created then this function will do nothing.

    AUTHOR
        Travis Whipple

    */
    /**/
    public void UpdateTags(){

        // Only update tagFragment if it has been created.
        if(m_fragmentCreated){
            m_TagFragment.UpdateTags();
        }
    }
    /* void UpdateTags() */

    /**/
    /*
    CreateTextView

    NAME
        CreateTextView()    - Will create a text view that spans across every column.

    SYNOPSIS
        TextView CreateTextView(String a_text)
            a_text      --> Text to be display within TextView.

    DESCRIPTION
        This function will create a TextView that spans across every column in the grid layout.
        This function will set the text color to white so that the text stands out against the
        dark background.

    RETURNS
        TextView    - TextView that spans all columns, and contains given text.

    AUTHOR
        Travis Whipple

    */
    /**/
    private TextView CreateTextView(String a_text){

        // TextView will span one row and every column.
        GridLayout.Spec rowSpan = GridLayout.spec(GridLayout.UNDEFINED, 1);
        GridLayout.Spec colspan = GridLayout.spec(
                GridLayout.UNDEFINED, m_mainGridLayout.getColumnCount());

        GridLayout.LayoutParams layoutParams =  new GridLayout.LayoutParams(rowSpan, colspan);

        // Inflate the TextView from the template file.
        TextView textView = (TextView) LayoutInflater
                .from(m_Context).inflate(R.layout.text_view_template, null);

        // Set TextViews parameters to display the text.
        textView.setText(a_text);
        textView.setLayoutParams(layoutParams);
        textView.setTextColor(ContextCompat.getColor(m_Context, R.color.white));
        return textView;
    }
    /* TextView CreateTextView(String a_text) */

    /**/
    /*
    CreateImageView

    NAME
        CreateImageView()   - Creates an ImageView to display images.

    SYNOPSIS
        ImageView CreateImageView(ImageObject a_image)
            a_image     --> Image to be displayed in ImageView.

    DESCRIPTION
        This function will load

    RETURNS

    AUTHOR
        Travis Whipple

    */
    /**/
    private ImageView CreateImageView(ImageObject a_image){

        // Get max view size.
        int displayWidth    = m_MainActivity.get().GetDisplayWidth();
        int numColmns       = m_mainGridLayout.getColumnCount();
        int maxViewSize = displayWidth / numColmns;

        // Create ImageView
        ImageView imageView = new ImageView(m_Context);
        imageView.setLayoutParams(new GridView.LayoutParams(maxViewSize, maxViewSize));
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        int padding = 1;
        imageView.setPadding(padding, padding, padding, padding);

        // These are so an image will be displayed in full within its View.
        imageView.setCropToPadding(false);
        imageView.setAdjustViewBounds(true);

        // Set the ID of the ImageView so when its selected,
        // we know which image it correlates to.
        imageView.setId(m_ImageManager.GetPosition(a_image));

        /*
        Load current image into ImageView using Glide for optimal performance.
        This will clear glides cache every 5 minutes to prevent from loading an old image from
        its cache.
        */
        int fiveMinuteInMillisecods = 5 * 60 * 1000;
        StringSignature strSig = new StringSignature(
                String.valueOf(System.currentTimeMillis()/fiveMinuteInMillisecods));
        Glide.with(m_Context)
                .load(a_image.GetPath())
                .signature(strSig)
                .into(imageView);

        MainActivity activity = m_MainActivity.get();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.ImageSelected(v.getId());
            }
        });

        return imageView;
    }
    /* ImageView CreateImageView(ImageObject a_image) */
}
/* class SimilarImagesFragment */