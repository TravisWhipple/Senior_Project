package com.example.traviswhipple.seniorproject;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

// Glide library.
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
/**
 * Created by traviswhipple on 9/20/18.
 */

/**/
/*
CLASS NAME

    ImageLibraryFragment - Creates and displays photo galleries.

DESCRIPTION

    This class creates a fragment that can display different photo albums that will sort
    and display users photos differently. It can organize photos based on the date they were
    taken, sort by tags associated to each photo, as well as display all people. When an album
    is selected the current display will be initiated to display all photos in that album. This
    class does not modify or create any new tags, images or people. It purely displays different
    albums to the screen. Once this fragment is created it must then be initialized by calling
    the SetArguments method. This is because if we create a constructor that takes multiple
    arguments, then when Android recycles this view, it will call the default constructor,
    leading to problems with member data not being initialized.

AUTHOR
    Travis Whipple

DATE
    7/13/2018
*/
/**/
public class ImageLibraryFragment extends Fragment {

    // Reference back to MainActivity where fragment was created.
    private WeakReference<MainActivity> m_MainActivity;

    // Display properties
    private int m_NumColumns;
    private int m_SCREEN_WIDTH;

    // Member data for creating albums.
    private int m_nextAlbumID;
    private int m_GALLERY_ALBUM = -1;
    private int m_DATES_ALBUM = -1;
    private int m_FACES_ALBUM = -1;
    private int m_TAGS_ALBUM = -1;
    private int m_PEOPLE_ALBUM = -1;

    // Data for retrieving images.
    private ImageManager m_ImageManager;
    private Context m_Context;

    // Main layout to be inflated with images.
    private GridLayout m_mainGridLayout;


    /**/
    /*
    SetArguments()

    NAME
        SetArguments - Initializes Fragment with all necessary data for displaying different albums.

    SYNOPSIS
        void SetArguments(MainActivity a_activity, Context a_context, ImageManager a_imageManager,
                    int a_screenWidth, int a_numberOfColumns)

                    a_activity          --> Parent activity.
                    a_context           --> Context of parent activity.
                    a_imageManager      --> ImageManager that has all image data.
                    a_screenWidth       --> Width of the devices display.
                    a_numberOfColumns   --> Number of images per column to display.

    DESCRIPTION
        This function will set all member variables from parameters. It is important that this
        function is called after creating this fragment as these parameters are need in order
        to display anything to the screen.

    RETURNS
        Void.

    AUTHOR
        Travis Whipple

    DATE
        7/15/2018
    */
    /**/
    public void SetArguments(MainActivity a_activity, Context a_context,
                             ImageManager a_imageManager, int a_screenWidth,
                             int a_numberOfColumns){

        m_MainActivity = new WeakReference<>(a_activity);
        m_Context = a_context;
        m_ImageManager = a_imageManager;
        m_SCREEN_WIDTH = a_screenWidth;
        m_NumColumns = a_numberOfColumns;
    }
    /* SetArguments(MainActivity a_activity, Context a_context, ImageManager a_imageManager,
                             int a_screenWidth, int a_numberOfColumns) */

    /**/
    /*
    onCreateView()

    Name
        onCreateView - Called when fragment is created.

    DESCRIPTION
        This function is called by Android when the fragment is created. It creates a layout
        from the image_library_fragment_layout XML file that defines how elements within the
        view are displayed.
    RETURN
        View - Current View created from XML layout resource file.
     */
    @Override
    public View onCreateView(LayoutInflater a_inflater, ViewGroup a_parent,
                             Bundle a_savedInstanceState) {

        // Inflate layout to use image library fragment layout XML file.
        return a_inflater.inflate(R.layout.image_library_fragment_layout,
                a_parent, false);
    }
    /* onCreateView(LayoutInflater a_inflater, ViewGroup a_parent, Bundle a_savedInstanceState) */

    /**/
    /*
    onViewCreated()

    NAME
       onViewCreated - Called after this Fragment's view has been inflated.

    SYNOPSIS
        void onViewCreated(View a_view, Bundle a_savedInstanceState)

            a_view                  --> View of the current Fragment.
            a_savedInstanceState    --> Current state of Fragment - null unless recycling View.

    DESCRIPTION
        This function overrides the default Fragment method onViewCreated so that we can
        initialize the main grid layout from its resource XML file. Then albums can be
        displayed to the screen.

    RETURNS
        Void.

    AUTHOR
        Travis Whipple

    DATE
        7/15/2018
    */
    /**/
    @Override
    public void onViewCreated(View a_view, Bundle a_savedInstanceState) {

        // Initialize main grid layout from XML layout file.
        m_mainGridLayout = a_view.findViewById(R.id.gridLayout);

        DisplayAlbumCovers();
    }
    /* onViewCreated(View a_view, Bundle a_savedInstanceState) */

    /**/
    /*
    DisplayAlbumCovers()

    NAME
       DisplayAlbumCovers - Will display the album cover for all albums.

    DESCRIPTION
        This function will display all album covers for the user to select. All albums will
        be displayed in a grid formation with only 2 album covers being displayed in each
        column to make for a clean look. Each album cover is prefixed with a name corresponding
        to what the album contains. Albums will only be created if images exist to match
        the albums criteria - Ex: Faces Album will not be displayed if no faces were found in
        any image.

    RETURNS
        Void.

    AUTHOR
        Travis Whipple

    DATE
        7/15/2018
    */
    /**/
    private void DisplayAlbumCovers(){

        // Used for giving each folder an ID.
        m_nextAlbumID = 0;

        // Only display 2 columns.
        int viewSize = m_SCREEN_WIDTH / 2;
        m_mainGridLayout.setColumnCount(2);

        // Create Gallery album.
        m_GALLERY_ALBUM = m_nextAlbumID;
        LinearLayout cover = GetAlbumCover("Image Gallery", m_ImageManager.GetImagePathFromPosition(0), viewSize);
        m_mainGridLayout.addView(cover);

        // Create Dates album.
        m_DATES_ALBUM = m_nextAlbumID;
        cover = GetAlbumCover("Photos By Date", m_ImageManager.GetImagePathFromPosition(0), viewSize);
        m_mainGridLayout.addView(cover);

        // Create Faces album.
        if(m_ImageManager.GetAllFaces().size() != 0){
            m_FACES_ALBUM = m_nextAlbumID;
            cover = GetAlbumCover("Faces", m_ImageManager.GetAllFaces().get(0).GetImagePath(), viewSize);
            m_mainGridLayout.addView(cover);
        }

        // Create similar tags album
        if(m_ImageManager.GetTags().size() != 0){
            m_TAGS_ALBUM = m_nextAlbumID;
            cover = GetAlbumCover("Photos by Tags", m_ImageManager.GetTopTags(1).get(0).GetImageList().get(0).GetPath(), viewSize);
            m_mainGridLayout.addView(cover);
        }

        // Create people album.
        if(m_ImageManager.GetPeople().size() != 0){
            m_PEOPLE_ALBUM = m_nextAlbumID;
            cover = GetAlbumCover("People", m_ImageManager.GetPeople().get(0).GetAllImagePathsContainingPerson().get(0), viewSize);
            m_mainGridLayout.addView(cover);
        }
    }
    /* DisplayAlbumCovers() */

    /**/
    /*
    AlbumSelected()

    NAME
        AlbumSelected - Displays images within the selected album.

    SYNOPSIS
        void AlbumSelected(int a_albumSelected)

            a_albumSelected     --> Index of the album selected, unique to only one album.

    DESCRIPTION
        This Function will decide which album was selected and then from there call the
        appropriate function to display the selected album. It will ensure that all views
        are removed from the layout so that only the selected album is displayed and that all
        other views are removed.

    RETURNS
        Void.

    AUTHOR
        Travis Whipple

    DATE
        7/15/2018
    */
    /**/
    private void AlbumSelected(int a_albumSelected){

        if(m_mainGridLayout != null && m_mainGridLayout.getChildCount() > 0){
            m_mainGridLayout.removeAllViews();
        }

        // Maximum size that a view can be given the screen width and number of columns to display.
        int maxViewSize = m_SCREEN_WIDTH / m_NumColumns;
        m_mainGridLayout.setColumnCount(m_NumColumns);


        if(a_albumSelected == m_GALLERY_ALBUM){
            // Display image gallery by removing this fragment.
            m_MainActivity.get().InitializePhotoGallery();

        }else if(a_albumSelected == m_DATES_ALBUM){
            // Display by date.
            ListByDate(maxViewSize);

        }else if(a_albumSelected == m_FACES_ALBUM){
            // Display faces.
            ListByFaces(maxViewSize);

        }else if(a_albumSelected == m_TAGS_ALBUM){
            // Display by tags.
            ListByTags(maxViewSize);

        }else if(a_albumSelected == m_PEOPLE_ALBUM){
            // Display by people.
            ListByPeople();
        }
    }
    /* AlbumSelected(int a_albumSelected) */

    /**/
    /*
    GetAlbumCover()

    NAME
        GetAlbumCover - Creates View that displays the tile along with an image.

    SYNOPSIS
        LinearLayout GetAlbumCover(String a_albumName, String a_coverPhotoPath, int a_maxViewSize)

            a_albumName         --> Name of album.
            a_coverPhotoPath    --> Path of image to be displayed as album's cover.
            a_maxViewSize       --> Maximum width and height for the Layout to be returned.


    DESCRIPTION
        This function will create an album cover to display the corresponding albums name along
        with the first photo in the album. It creates a LinearLayout by joining a TextView, which
        contains the albums name, with an ImageView, which displays the albums cover photo. The
        View is displayed so that the album name is above the album cover photo.This function will
        ensure that the returned layout is no larger than the maximum view size parameter.
        All images are loaded with Glide for optimization.

    RETURNS
        LinearLayout - View containing the album name and album cover photo..

    AUTHOR
        Travis Whipple

    DATE
        7/15/2018
    */
    /**/
    public LinearLayout GetAlbumCover(String a_albumName, String a_coverPhotoPath,
                                      int a_maxViewSize){

        // Inflate a TextView from text view template.
        TextView textView = (TextView) LayoutInflater.from(m_Context).inflate(R.layout.text_view_template, null);
        textView.setText(a_albumName);

        // ImageView displays cover photo
        ImageView coverPhotoView = new ImageView(m_Context);

        // Adjust size of layout to match maximum view size parameter.
        coverPhotoView.setLayoutParams(new GridView.LayoutParams(a_maxViewSize, a_maxViewSize));
        coverPhotoView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        // Photo will be displayed in the center of the coverPhotoView as well as not be cropped.
        int padding = 1;
        coverPhotoView.setPadding(padding, padding, padding, padding);
        coverPhotoView.setCropToPadding(false);
        coverPhotoView.setAdjustViewBounds(true);

        // Use glide for dynamic memory allocation.
        // Changing signature as Glide's cache holds old photos that may have been overwritten.
        Glide.with(this)
                .load(a_coverPhotoPath)
                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .into(coverPhotoView);

        // Layout is used to piece together the cover photo with its album covers text.
        LinearLayout layout = new LinearLayout(m_Context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        layout.setLayoutParams(layoutParams);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Add album name and album cover photo to layout.
        layout.addView(textView);
        layout.addView(coverPhotoView);

        // Give layout a unique id, and increment it for the next album to use.
        layout.setId(m_nextAlbumID++);

        // When an album is slected.
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlbumSelected(v.getId());
            }
        });

        return layout;
    }
    /* LinearLayout GetAlbumCover(String a_albumName, String a_coverPhotoPath,
                                      int a_maxViewSize) */

    /**/
    /*
    ListByDate()

    NAME
        ListByDate - Will display all photos by date.

    SYNOPSIS
        ListByDate(int a_maxViewSize)

            a_maxViewSize       --> Maximum size of each image to be displayed.


    DESCRIPTION
        This function will display users photos in date order. It will display a date followed
        by all images taken on that day. No date will be displayed twice and dates will only
        be displayed if there exists photos on that day. Images will be displayed in a
        grid formation below their respected date.

    RETURNS
        Void.

    AUTHOR
        Travis Whipple

    DATE
        7/15/2018
    */
    /**/
    public void ListByDate(int a_maxViewSize){

        // Date information about the last photo that was looked at.
        int lastDay = 0;
        int lastMonth = 0;
        int lastYear = 0;

        // Loop through every photo in data set.
        for(String currentPath : m_ImageManager.GetAllPaths()){

            ImageObject currentImage = m_ImageManager.GetImageObject(currentPath);

            // Get current images date data;
            int currentDay = currentImage.GetDay();
            int currentMonth = currentImage.GetMonth();
            int currentYear = currentImage.GetYear();


            /* Add a TextView to display date of current images, only Add new TextView
            when current image has a different date compared to the last image.
             */
            // Check if current image's date does NOT match last image date.
            if(!((currentDay == lastDay)
                            && (currentMonth == lastMonth)
                            && (currentYear == lastYear))){

                // Dates are not the same, Add text view to display the new date.
                lastDay = currentDay;
                lastMonth = currentMonth;
                lastYear = currentYear;

                TextView dateTextView = CreateTextView(
                        currentImage.GetDateString(),
                        MaxColSpanLayoutParams());

                m_mainGridLayout.addView(dateTextView);
            }

            // Add image to the display.
            int currentImagePosition = m_ImageManager.GetPosition(currentImage);
            ImageView imageView = CreateImageView(a_maxViewSize, currentImagePosition);

            /*
            Load current image into ImageView using Glide for optimal performance.
            This will clear glides cache every 5 minutes to prevent from loading an old image from
            its cache.
            */
            int fiveMinuteInMillisecods = 5 * 60 * 1000;
            Glide.with(this)
                    .load(currentPath)
                    .signature(new StringSignature(String.valueOf(
                            System.currentTimeMillis() / (fiveMinuteInMillisecods))))
                    .into(imageView);

            // Add image to layout.
            m_mainGridLayout.addView(imageView);
        }
    }
    /* ListByDate(int a_maxViewSize) */

    /**/
    /*
    ListByFaces()

    NAME
        ListByFaces - Displays all images with faces.

    SYNOPSIS
        ListByFaces(int a_maxViewSize)

            a_maxViewSize       --> Maximum size of each face to be displayed.


    DESCRIPTION
        This function will display all images with faces that were found in all images. This album
        allows a user to see every image that contains a face that was found by the face detector.
        Images are displayed in a grid with no view exceeding the maximum allowed view size.

    RETURNS
        Void.

    AUTHOR
        Travis Whipple

    DATE
        7/15/2018
    */
    /**/
    public void ListByFaces(int a_maxViewSize){

        // Get every face in data set.
        for(FaceObject faceObject : m_ImageManager.GetAllFaces()){

            // Get path of image that contains this face.
            String imagePath = faceObject.GetImagePath();

            /* Get the position of the original image in data set, this is used to set the
            ImageView's ID so that it correlates with the original image.
             */
            int imagePosition = m_ImageManager.GetPosition(faceObject.GetImagePath());
            ImageView imageView = CreateImageView(a_maxViewSize, imagePosition);

            /*
            Load current image into ImageView using Glide for optimal performance.
            This will clear glides cache every 5 minutes to prevent from loading an old image from
            its cache.
            */
            int fiveMinuteInMillisecods = 5 * 60 * 1000;
            StringSignature strSig = new StringSignature(String.valueOf(System.currentTimeMillis()/fiveMinuteInMillisecods));
            Glide.with(this)
                    .load(imagePath)
                    .signature(strSig)
                    .into(imageView);

            // Add view to layout.
            m_mainGridLayout.addView(imageView);
        }
    }
    /* ListByFaces(int a_maxViewSize) */

    /**/
    /*
    ListByTags()

    NAME
        ListByTags - Displays all images by most popular tags.

    SYNOPSIS
        void ListByTags(int a_maxViewSize)

            a_maxViewSize       --> Maximum size of each face to be displayed.

    DESCRIPTION
        This function will list all of users images organized by tags they are associated with.
        It will display a tag's name followed by a grid of all photos with that tag. It only will
        display photos under a tag if their confidence to that tag is greater than 70%, this
        helps to prevent photos with garbage results from populating the display. Images are
        only displayed once to prevent from the same images being displayed for more than one tag,
        as each photo can have upwards of 15 tags. A maximum amount of images are displayed for
        each tag, this is to prevent one tag from displaying every photo.

    RETURNS
        Void.

    AUTHOR
        Travis Whipple

    DATE
        7/15/2018
    */
    /**/
    public void ListByTags(int a_maxViewSize){

        ArrayList<ImageObject> imagesInView = new ArrayList<>();

        int totalNumberOfImages = m_ImageManager.GetImages().size();

        final int MAX_IMAGES_PER_TAG = 20;
        int currentImagesAddedForTag = 0;

        // Loop through all tags in data set.
        for(Tag tag : m_ImageManager.GetTags()){

            // Used to prevent us from adding same textView more than once.
            // New textView has not been added.
            boolean hasAddedTextView = false;
            currentImagesAddedForTag = 0;

            // Capitalize tag name.
            String tagName = tag.GetTagName();
            String capitalizedTagName = tagName.substring(0, 1).toUpperCase() + tagName.substring(1);

            // Create TextView to display the current tags name.
            TextView tagTextView = CreateTextView(
                    capitalizedTagName,
                    MaxColSpanLayoutParams());

            for(ImageObject image : tag.GetImageList()){

                // Only adds photos and tags with a confidence larger than 70%.
                if((!imagesInView.contains(image))
                        && tag.GetImageConfidence(image) > .7
                        && currentImagesAddedForTag < MAX_IMAGES_PER_TAG){

                    // Record that this image has been added to view.
                    imagesInView.add(image);

                    // Create an ImageView to display the current image.
                    int currentPosition = m_ImageManager.GetPosition(image.GetPath());
                    ImageView imageView = CreateImageView(a_maxViewSize, currentPosition);

                    /*
                    Load current image into ImageView using Glide for optimal performance.
                    This will clear glides cache every 5 minutes to prevent from loading an old image from
                    its cache.
                    */
                    int fiveMinuteInMillisecods = 5 * 60 * 1000;
                    StringSignature strSig = new StringSignature(String.valueOf(System.currentTimeMillis()/fiveMinuteInMillisecods));
                    Glide.with(this)
                            .load(image.GetPath())
                            .signature(strSig)
                            .into(imageView);



                    // Only add tag's name if it has not been added already.
                    if(!hasAddedTextView){
                        m_mainGridLayout.addView(tagTextView);
                        // TextView has now been added.
                        hasAddedTextView = true;
                    }

                    // Add image to layout.
                    m_mainGridLayout.addView(imageView);
                    currentImagesAddedForTag++;
                }

                // Check if there are more images to Add.
                if(imagesInView.size() >= totalNumberOfImages){
                    return;
                }
            }
        }
    }
    /* void ListByTags(int a_maxViewSize) */

    // Listing by people requires a PersonFragment to be created.
    public void ListByPeople(){
        // Call parent activities function to create the people fragment.
        m_MainActivity.get().CreatePeopleFragment();
    }

    /*
    NAME
        MaxColSpanLayoutParams

    DESCRIPTION
        This function will create layout parameters to be used to display a view across all
        columns of a GridLayout. This is useful for displaying text that seems to not follow
        the GridLayout's normal grid pattern as it stretches across all columns.

    RETURN
        GridLayout.LayoutParams - layout parameters that span across all columns of a grid.

    DATE
        7/20/2018
     */
    private GridLayout.LayoutParams MaxColSpanLayoutParams(){
        // Create layout parameters that span across all columns.
        GridLayout.Spec rowSpan = GridLayout.spec(GridLayout.UNDEFINED, 1);
        GridLayout.Spec colspan = GridLayout.spec(GridLayout.UNDEFINED, m_NumColumns);
        return new GridLayout.LayoutParams(rowSpan, colspan);
    }
    /* GridLayout.LayoutParams MaxColSpanLayoutParams() */

    /**/
    /*
    CreateTextView()

    NAME
        CreateTextView - Creates a text view with given layout parameters.

    SYNOPSIS
        TextView CreateTextView(String a_text, GridLayout.LayoutParams a_layoutParams)

            a_text          --> Text to be displayed in TextView.
            a_layoutParams  --> Layout parameters to be given to TextView.

    DESCRIPTION
        This function creates a text view with given text and given layout parameters. It is a
        basic function that was created to make code more legible.

    RETURNS
        TextView    - The View with proper text and layout parameters.

    AUTHOR
        Travis Whipple

    DATE
        8/19/2018
    */
    /**/
    private TextView CreateTextView(String a_text, GridLayout.LayoutParams a_layoutParams){

        TextView textView = (TextView) LayoutInflater
                .from(m_Context).inflate(R.layout.text_view_template, null);

        textView.setText(a_text);
        textView.setLayoutParams(a_layoutParams);
        return textView;
    }
    /* TextView CreateTextView(String a_text, GridLayout.LayoutParams a_layoutParams) */

    /**/
    /*
    CreateImageView()

    NAME
        CreateImageView - Creates an ImageView of a given size and from a given images path.

    SYNOPSIS
        ImageView CreateImageView(int a_viewSize, int a_imagePosition)

            a_viewSize      --> Size of ImageView.
            a_imagePosition --> Images position in data set.

    DESCRIPTION
        This function will create an ImageView from a given images path. It will assign the
        ImageView an ID which correlates to the position of the parent ImageObject in the
        main ImageManager data set. When an image is selected it will call this fragments
        parent activity's ImageSelected function, where it can then display and alter the
        view as it needs.

    RETURNS
        ImageView - View displaying given image's path.

    AUTHOR
        Travis Whipple

    DATE
        7/15/2018
    */
    /**/
    private ImageView CreateImageView(int a_viewSize, int a_imagePosition){

        ImageView imageView = new ImageView(m_Context);
        imageView.setLayoutParams(new GridView.LayoutParams(a_viewSize, a_viewSize));
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        int padding = 1;
        imageView.setPadding(padding, padding, padding, padding);

        imageView.setCropToPadding(false);
        imageView.setAdjustViewBounds(true);

        imageView.setId(a_imagePosition);

        // Get activity that created this fragment.
        MainActivity activity = m_MainActivity.get();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Call activities ImageSelected function.
                This is similar to defining a "onClick" attribute in XML file, but
                we call it this way as this view is created dynamically.
                */
                activity.ImageSelected(v.getId());
            }
        });

        return imageView;
    }
    /* ImageView CreateImageView(int a_viewSize, int a_imagePosition) */
}
/* class ImageLibraryFragment extends Fragment */
