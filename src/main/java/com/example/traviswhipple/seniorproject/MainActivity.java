

package com.example.traviswhipple.seniorproject;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.Manifest;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.Toast;

// Imports the Google Cloud client library
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;


/**/
/*

CLASS
    MainActivity    - Activity where program stems from.

DESCRIPTION
    This class is responsible for ensuring that the application has the proper permissions
    in order to load any images, save to a file and request the camera. Here different Fragments
    are created depending on what button the user selects. This class is event driven and does
    nothing when the user is inactive. Since this is the only Activity all views will be drawn
    under this class's context. It will ensure that only one instance of a Fragment will be
    draw. When another Fragment is requested, any active Fragments will be removed. When the
    Activity goes out of view (user is in another app) the class will automatically save all
    data.

AUTHOR
    Travis Whipple

DATE
    2/24/2018
*/
/**/

public class MainActivity extends AppCompatActivity{

    // Custom codes for getting permissions.
    private final int MY_PERMISSIONS_READ_EXTERNAL_DATA = 1;
    private final int MY_PERMISSIONS_WRITE_EXTERNAL_DATA = 2;
    private final int MY_PERMISSIONS_CAMERA = 3;

    // Custom request code for accessing camera.
    private final int CAMERA_REQUEST = 1;
    // File to save photos taken from camera to.
    private File tempFile;


    // Number of columns to be displayed in all views.
    private final int NUMBER_OF_COLUMNS = 6;

    // Used to load images, and save all data found about images.
    private Serializer m_serializer;

    // Used to store all information about images, tags and people.
    private ImageManager m_imageManager;

    // Search bar.
    private SearchView m_searchView;

    // Fragments for when user wants to change the view.
    private SimilarImagesFragment m_similarImagesFragment;
    private ImageLibraryFragment m_imageLibraryFragment;
    private PersonFragment m_personFragment;

    // Variables for display the photo gallery.
    private ImageAdapter m_imageAdapter;
    private GridView m_gridView;

    // Flags about the current state of the program.
    private boolean m_analyzeInBackground;
    private boolean m_isDarkMode;

    // View to darken background while user is in another Fragment.
    private View m_fadeBackground;


    /**/
    /*
    onCreate

    NAME
        onCreate()  - Initializes the Activity.

    SYNOPSIS
        void onCreate(Bundle a_savedInstanceState)
            a_savedInstanceState        -- > previous activity state. Initialized by Android.

    DESCRIPTION
        This class is called when this Activity is created. During the life of the program
        it is only called once since this is a single Activity application. Within this function
        all member variables will be set appropriately. After all member variables are set
        the image gallery will attempt to be inflated with all images.

    RETURNS
        Void.

    AUTHOR
        Travis Whipple

    DATE
        2/24/2018
    */
    /**/

    @Override
    public void onCreate(Bundle a_savedInstanceState) {
        super.onCreate(a_savedInstanceState);
        setContentView(R.layout.activity_main);

        m_analyzeInBackground = true;
        m_isDarkMode = false;

        m_gridView = findViewById(R.id.gridView);
        m_fadeBackground = findViewById(R.id.fadeBackground);
        m_searchView = findViewById(R.id.searchBar);

        m_imageAdapter = new ImageAdapter(this, GetDisplayWidth());

        /*
        Set search view's on query listener so that the proper function is called when
        user searches for photos
        */
        m_searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Searched(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Load photos into view.
        LoadPhotosInitializer();

        // Display Camera button.
        ShowCameraButton();
    }
    /* void onCreate(Bundle a_savedInstanceState) */

    /**/
    /*
    onStop

    NAME
        onStop()    - Called when the Activity goes out of view.

    DESCRIPTION
        This function is called when this Activity goes out of view. This happens when a user
        switches to another app, or when the user closes this app. It will save all image and
        tag relations, people and faces to a .txt file.

    AUTHOR
        Travis Whipple

    */
    /**/
    @Override
    protected void onStop(){
        // Call super class first to ensure proper onStop functionality.
        super.onStop();
        if(m_serializer != null){
            m_serializer.SaveToFile();
        }
    }
    /*void onStop()*/

    /**/
    /*
    onRestart

    NAME
        onRestart()     - Called when this Activity comes back into view.

    DESCRIPTION
        This function is called when this Activity comes back into view. It works in reverse to
        onStop(). In that this function will load all image and tag relations, people and faces
        into memory, then re-initialize the photo gallery.

    AUTHOR
        Travis Whipple

    */
    /**/
    @Override
    protected void onRestart(){
        // Call super class first to ensure proper onRestart functionality.
        super.onRestart();
        LoadPhotosInitializer();
    }
    /*void onRestart()*/

    /**/
    /*
    getTheme

    NAME
        getTheme()      - Called when a View object is created with respect to this Activities
                        context.

    DESCRIPTION
        This function is called when a new View object is created under this Activities context.
        When a new view is created it will sets its own theme to match its parent (this Activity).
        We override this function to allow the user to chang the theme of the application and
        have the new theme applied to all Fragments created.

    RETURNS
        Resources.Theme     - Current theme of Activity. Either the style LightTheme or DarkTheme,
                            one of these values will always be returned.

    AUTHOR
        Travis Whipple

    */
    /**/
    @Override
    public Resources.Theme getTheme() {
        Resources.Theme theme = super.getTheme();
        if(m_isDarkMode){
            theme.applyStyle(R.style.DarkTheme, true);
        }else{
            theme.applyStyle(R.style.LightTheme, true);
        }

        return theme;
    }
    /* Resources.Theme getTheme() */

    /**/
    /*
    onRequestPermissionsResult

    NAME
        onRequestPermissionsResult()    - Called after a request for a permission is called.

    SYNOPSIS
        void onRequestPermissionsResult(int a_requestCode,
                                           String a_permissions[], int[] a_grantResults)

            a_requestCode       --> Code given when requesting a permission.
            a_permissions       --> Not used, but needed to override function.
            a_grantResults      --> Response code if a was granted.

    DESCRIPTION
        This function will be called after a request for a certain permission is returned. Here
        we will grant the application all necessary permissions. This function get the current
        request code which correlates to the request code given when the permission request is
        sent. These codes are defined as final member variables so we can determine what request
        was made. After a request this function will check for the next permission in order from
        read external data, write external data then finally the camera permission.

    AUTHOR
        Travis Whipple

    */
    /**/
    @Override
    public void onRequestPermissionsResult(int a_requestCode,
                                           String a_permissions[], int[] a_grantResults) {
        switch (a_requestCode) {
            case MY_PERMISSIONS_READ_EXTERNAL_DATA:

                // If request is cancelled, the result arrays are empty.
                if (a_grantResults.length > 0 && a_grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted.
                    LoadPhotosInitializer();
                    AnalyzePhotos();

                    HasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, MY_PERMISSIONS_WRITE_EXTERNAL_DATA);

                } else {
                    // Permission denied. Inform user what will happen now.
                    Toast.makeText(this, "Cannot Load any images! Must have permission", Toast.LENGTH_LONG).show();
                }
                break;

            case MY_PERMISSIONS_WRITE_EXTERNAL_DATA:
                if (a_grantResults.length > 0 && a_grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted.

                    HasPermission(Manifest.permission.CAMERA, MY_PERMISSIONS_CAMERA);
                } else {
                    // Permission denied. Inform user what will happen now.
                    Toast.makeText(this, "Cannot Load any images! Must have permission", Toast.LENGTH_LONG).show();
                }
                break;

            case MY_PERMISSIONS_CAMERA:

                if(a_grantResults.length > 0 && a_grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // Camera permission granted.
                    ShowCameraButton();
                }else{
                    // Permission denied. Inform user what will happen now.
                    Toast.makeText(this, "Cannot take any new photos! Must have permission", Toast.LENGTH_LONG).show();
                }
        }
    }
    /* void onRequestPermissionsResult(int a_requestCode,
                                           String a_permissions[], int[] a_grantResults) */

    /**/
    /*
    LoadPhotosInitializer

    NAME
        LoadPhotosInitializer() - Loads photos into memory.

    DESCRIPTION
        This function will first check that the application has permission to read from external
        storage. If the application has the permission then it will initialize the ImageManager and
        Serializer and load all photos into memory. The function will then load all images
        into the photo gallery, then start analyzing photos in the background.

    AUTHOR
        Travis Whipple

    */
    /**/
    private void LoadPhotosInitializer(){
        if(HasPermission(Manifest.permission.READ_EXTERNAL_STORAGE, MY_PERMISSIONS_READ_EXTERNAL_DATA)){

            // Permission is granted to read from external storage.
            m_imageManager = new ImageManager(this.getApplicationContext());
            m_serializer = new Serializer(this.getApplicationContext(), m_imageManager);
            m_serializer.Load();

            InitializePhotoGallery();
            AnalyzePhotos();
        }
    }
    /* void LoadPhotosInitializer() */

    /**/
    /*
    HasPermission

    NAME
        HasPermission()     - Whether or not the application has a given permission.

    SYNOPSIS
        boolean HasPermission(String a_PERMISSION, int a_PERMISSION_CODE)
            a_PERMISSION        --> Manifest permission to check.
            a_PERMISSION_CODE   --> Member MY_PERMISSION... variable correlated with given
                                    Manifest permission.

    DESCRIPTION
        This function will check if a given permission has been granted by the user already. If
        a permission has not, the function will return a value to reflect this and request
        the given permission from the user.

    RETURNS
        boolean         - True if given permission was granted.
                        - False if not, one of these will always return.

    AUTHOR
        Travis Whipple

    */
    /**/
    private boolean HasPermission(String a_PERMISSION, int a_PERMISSION_CODE){

        // Check if permission was granted.
        if(checkSelfPermission(a_PERMISSION) != PackageManager.PERMISSION_GRANTED){

            // Check if this permission should have a message associated with it.
            if(shouldShowRequestPermissionRationale(a_PERMISSION)){
                Toast.makeText(this, "Permission needed to access camera", Toast.LENGTH_LONG).show();
            }

            // Request the permission that was not granted.
            requestPermissions(new String[]{a_PERMISSION}, a_PERMISSION_CODE);
            return false;
        }else{
            // Permission was granted.
            return true;
        }
    }
    /* boolean HasPermission(String a_PERMISSION, int a_PERMISSION_CODE) */

    /**/
    /*
    InitializePhotoGallery

    NAME
        InitializePhotoGallery()    - Initializes the photo gallery to display all images.

    DESCRIPTION
        This function will load every image on the users device into the view. Images are
        displayed in order of when they were taken. When this function is called it will ensure
        that all Fragments are removed and that the photo gallery is not grayed out. The photo
        gallery gets grayed out when a Fragment is created. This is prevent the photo gallery
        from being displayed at the same time as other Fragments.

    AUTHOR
        Travis Whipple

    */
    /**/
    public void InitializePhotoGallery(){

        // Make sure gallery is not grayed out.
        GrayOutGallery(false);

        // Remove any fragments in view so that only the gallery is shown.
        RemoveAllFragments();

        // Reset image adapter.
        ResetImageAdapter();

        for(String path : m_imageManager.GetAllPaths()){
            m_imageAdapter.Add(path);
        }

        m_gridView.setAdapter(m_imageAdapter);

        m_gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageSelected(position);
            }
        });
    }
    /* void InitializePhotoGallery() */

    /**/
    /*
    ShowCameraButton

    NAME
        ShowCameraButton()  - Displays the camera button.

    DESCRIPTION
        This function will check that the camera permission was granted and only then will it
        draw the camera button to the screen. This button will stay hidden until the camera
        permission is accepted.

    AUTHOR
        Travis Whipple

    */
    /**/
    private void ShowCameraButton(){
        // Check user has given permission to use the camera.
        if(HasPermission(Manifest.permission.CAMERA, MY_PERMISSIONS_CAMERA)){
            ImageButton cameraButton = findViewById(R.id.cameraButton);
            cameraButton.setVisibility(View.VISIBLE);
        }
    }
    /* void ShowCameraButton() */

    /**/
    /*
    CameraButtonPressed

    NAME
        CameraButtonPressed()   - Called when the user clicks the Camera button.

    SYNOPSIS
        void CameraButtonPressed(View a_view)
            a_view      --> View that called this function.

    DESCRIPTION
        This function is called when a user selects the Camera button to take a picture. It will
        check that the proper view called this function. It will start an Intent to capture
        an image. A file to save the photo is passed to the intent so that we can save the image
        the user takes. The files name for the image will be unique to avoid collisions with
        existing images.

    AUTHOR
        Travis Whipple

    */
    /**/
    public void CameraButtonPressed(View a_view){

        // Check that the camera button was the one who called this function.
        if(a_view.getId() == R.id.cameraButton){
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

            File directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            Long currentTime = new Date().getTime();

            // Create a unique file name from the current time in millisecconds.
            String fileName = getResources().getString(R.string.imagePrefix);
            fileName += Long.toString(currentTime);
            fileName += ".jpg";

            tempFile = new File(directory, fileName);

            // Get Uri for this file.
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", tempFile);

            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }
    /* void CameraButtonPressed(View a_view) */

    /**/
    /*
    onActivityResult

    NAME
        onActivityResult()  - Called after camera intent is finished.

    SYNOPSIS
        void onActivityResult(int a_requestCode, int a_resultCode, Intent a_data)
            a_requestCode       --> Request code given to the intent.
            a_resultCode        --> Value of how user finished camera intent.
            a_data              --> Contains bitmap thumbnail of image taken.

    DESCRIPTION
        This function is called when an intent finishes. Currently it only handles camera intent
        results. It will check that a camera intent called it and that the result was OK. This
        means that the user took a photo, and accepted it. Result will not be OK if user closes
        the intent or does not take a photo. The data returned is ignored as the image taken
        will be saved to the applications private Images folder. After the image is saved, photo
        gallery will be re-loaded to include the new image.

    AUTHOR
        Travis Whipple

    */
    /**/
    protected void onActivityResult(int a_requestCode, int a_resultCode, Intent a_data) {
        if (a_requestCode == CAMERA_REQUEST && a_resultCode == Activity.RESULT_OK) {

            // Get uri of where photo was saved.
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", tempFile);
            String imagePath = photoUri.getPath();

            // Save the image to the applications private Images folder.
            Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File file = new File(imagePath);
            Uri contentUri = Uri.fromFile(file);
            scanIntent.setData(contentUri);
            sendBroadcast(scanIntent);
        }

        // Reload photos as a new photo has been added.
        LoadPhotosInitializer();
    }
    /* void onActivityResult(int a_requestCode, int a_resultCode, Intent a_data) */

    /**/
    /*
    Searched

    NAME
        Searched()  - Called when a user commits a search string.

    SYNOPSIS
        void Searched(String a_searchString)
            a_searchString      --> Criteria to match tags and people.

    DESCRIPTION
        This function is called when a user searches for an image. This function will ensure
        that no other fragments are in view so that only the search results are displayed. The
        gallery will then reflect all images that match the users search criteria.

    AUTHOR
        Travis Whipple

    */
    /**/
    public void Searched(String a_searchString){

        // Reset all views.
        RemoveAllFragments();
        ResetImageAdapter();

        // Add all images to the photo gallery that matched the users search.
        for(String imagePath : m_imageManager.GetAllSearchResults(a_searchString)){
            m_imageAdapter.Add(imagePath);
        }

        m_gridView.setAdapter(m_imageAdapter);
        m_gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Get from its position in the image adapter.
                String imagePath = m_imageAdapter.getItem(position);
                position = m_imageManager.GetPosition(imagePath);
                ImageSelected(position);
            }
        });

    }
    /* void Searched(String a_searchString) */

    /**/
    /*
    ResetImageAdapter

    NAME
        ResetImageAdapter()     - Resets the image adapter to contain no images.

    DESCRIPTION
        This function will remove all images from the image adapter as well as remove its self
        from any views that use it.

    RETURNS

    AUTHOR
        Travis Whipple

    */
    /**/
    private void ResetImageAdapter(){
        m_gridView.setVisibility(View.VISIBLE);

        // Reset grid views adapter.
        m_gridView.setAdapter(null);
        m_imageAdapter.RemoveAllViews();
    }
    /* void ResetImageAdapter() */

    /**/
    /*
    RemoveAllFragments

    NAME
        RemoveAllFragments()    - Removes all Fragments currently in view.

    DESCRIPTION
        This function will remove all Fragments from the current view.

    AUTHOR
        Travis Whipple

    */
    /**/
    private void RemoveAllFragments(){
        RemovePeopleFragment();
        RemoveImageLibraryFragment();
        RemoveSimilarImagesFragment();
    }
    /* void RemoveAllFragments() */

    /**/
    /*
    RemovePeopleFragment

    NAME
        RemovePeopleFragment()  - Removes a PeopleFragment object.

    DESCRIPTION
        This function will remove a PeopleFragment object from the current view. This function
        will check that the view already exists in the view to avoid modifying a null object.

    AUTHOR
        Travis Whipple

    */
    /**/
    public void RemovePeopleFragment(){

        if(m_personFragment != null){
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.remove(m_personFragment);
            ft.commit();

            m_personFragment = null;
        }
    }
    /* void RemovePeopleFragment() */

    /**/
    /*
    CreateImageLibraryFragment

    NAME
        CreateImageLibraryFragment()    - Creates an ImageLibraryFragment object.

    SYNOPSIS
        void CreateImageLibraryFragment(View a_view)
            a_view      --> View that called this function.

    DESCRIPTION
        This function will create an ImageLibraryFragment object and add it to the current view.
        It will ensure that no other Fragments are currently in the view, then gray out the
        photo gallery so only the ImageLibraryFragment is visible.

    AUTHOR
        Travis Whipple

    */
    /**/
    public void CreateImageLibraryFragment(View a_view){

        // Only the createPhotoLibraryFragment button can initialize a new ImageLibraryFragment.
        if(a_view.getId() == R.id.createPhotoLibraryFragment){

            // Make sure no other Fragment is in view.
            RemoveAllFragments();

            // Gray out the gallery so only ImageLibraryFragment is visible.
            GrayOutGallery(true);

            // Create the Fragment.
            m_imageLibraryFragment = new ImageLibraryFragment();

            // Set the arguments for the fragment.
            m_imageLibraryFragment.SetArguments(
                    this,
                    this,
                    m_imageManager,
                    GetDisplayWidth(),
                    NUMBER_OF_COLUMNS);

            // Replace the contents of the container with the new fragment
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.imageLibraryFragment, m_imageLibraryFragment);

            // Apply fragment to view.
            ft.commit();
        }
    }
    /* void CreateImageLibraryFragment(View a_view) */

    /**/
    /*
    GrayOutGallery

    NAME
        GrayOutGallery()    - Grays out the photo gallery.

    SYNOPSIS
        void GrayOutGallery(boolean a_isHiding)
            a_isHiding      --> Whether or not to gray out the gallery.

    DESCRIPTION
        This function will gray out the photo gallery by displaying a view on top of the photo
        gallery. The view can be visible or not depending on the given value.

    AUTHOR
        Travis Whipple

    */
    /**/
    private void GrayOutGallery(boolean a_isHiding){

        // Check if we want to hide or show the gallery.
        if(a_isHiding){
            // Hide gallery by displaying a view over the gallery.
            m_fadeBackground.setVisibility(View.VISIBLE);
        }else{
            // Show the gallery by hiding this view.
            m_fadeBackground.setVisibility(View.GONE);
        }
    }
    /* void GrayOutGallery(boolean a_isHiding) */

    /**/
    /*
    RemoveImageLibraryFragment

    NAME
        RemoveImageLibraryFragment()    - Removes the ImageLibraryFragment from view.

    DESCRIPTION
        This function will attempt to remove the ImageLibraryFragment from the view if it exists.
        Then it will display the photo gallery ot the screen.

    AUTHOR
        Travis Whipple

    */
    /**/
    public void RemoveImageLibraryFragment(){

        //setSearchBarVisibility(true);

        if(m_imageLibraryFragment != null){

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.remove(m_imageLibraryFragment);
            ft.commit();
            m_imageLibraryFragment = null;

            InitializePhotoGallery();
        }
    }
    /* void RemoveImageLibraryFragment() */

    /**/
    /*
    CreatePeopleFragment

    NAME
        CreatePeopleFragment()  - Creates a PeopleFragmentObject and puts it in view.

    DESCRIPTION
        This function will create and display a PeopleFragment object to the screen.

    AUTHOR
        Travis Whipple

    */
    /**/
    public void CreatePeopleFragment(){

        m_personFragment = new PersonFragment();

        m_personFragment.SetArguments(
                this,
                m_imageManager,
                this,
                GetDisplayWidth(),
                NUMBER_OF_COLUMNS);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.peopleFragment, m_personFragment);
        ft.commit();
    }
    /* void CreatePeopleFragment() */

    /**/
    /*
    GetDisplayWidth

    NAME
        GetDisplayWidth()   - Gets the width of the current display.

    DESCRIPTION
        This function will return how many pixels wide the current display is. This is used to
        allow images to be loaded in a manner that best fits the display of the device.

    RETURNS
        int     - Width of screen in number of pixels.

    AUTHOR
        Travis Whipple

    */
    /**/
    public int GetDisplayWidth(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }
    /* int GetDisplayWidth() */

    /**/
    /*
    ImageSelected

    NAME
        ImageSelected() - Called when an image is selected by the user.

    SYNOPSIS
        void ImageSelected(int a_position)
            a_position      --> Position of image within data set.

    DESCRIPTION
        This function is called when an image is selected. It will create a SimilarImageFragment
        containing the image selected and add the Fragment to the view. This is so a user can
        select an image and quickly see all photos that are similar to it.

    AUTHOR
        Travis Whipple

    */
    /**/
    public void ImageSelected(int a_position){

        // Hide background while user is viewing image.
        m_fadeBackground.setVisibility(View.VISIBLE);

        m_similarImagesFragment = new SimilarImagesFragment();
        m_similarImagesFragment.SetArguments(
                this,
                m_imageManager,
                this,
                a_position);

        // Replace the contents of the container with the new fragment
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.similarImagesFragment, m_similarImagesFragment);

        // Apply fragment to view.
        ft.commit();

    }
    /* void ImageSelected(int a_position) */

    /**/
    /*
    HelpButtonPressed

    NAME
        HelpButtonPressed() - Called when user clicks the help button.

    SYNOPSIS
        void HelpButtonPressed(View a_view)
            a_view      --> View that called this function.

    DESCRIPTION
        This function will display to the user a dialog with tips and tricks for using the app.
        Settings for changing the theme to either LightTheme or DarkTheme as well as the option
        to analyze all photos in the background are here. These settings will reflect the current
        setting so the user can turn them on or back off.

    AUTHOR
        Travis Whipple

    */
    /**/
    public void HelpButtonPressed(View a_view){

        if(a_view.getId() == R.id.helpButton){

            // Use custom dialog to display the help screen.
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.settings_layout);
            dialog.setTitle("Help Menu");

            Switch autoAnalyzeSwitch = dialog.findViewById(R.id.autoAnalyze);
            Switch darkModeSwitch    = dialog.findViewById(R.id.darkModeSwitch);

            // Set values of switches to echo thier current value.
            autoAnalyzeSwitch.setChecked(m_analyzeInBackground);
            darkModeSwitch.setChecked(m_isDarkMode);

            Button applyDialogButton    = dialog.findViewById(R.id.applyDialogButton);
            Button cancelButton         = dialog.findViewById(R.id.cancelDialogButton);

            // if button is clicked, close the custom dialog
            applyDialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Get values of switches.
                    m_isDarkMode = darkModeSwitch.isChecked();
                    m_analyzeInBackground = autoAnalyzeSwitch.isChecked();

                    // Update the current theme.
                    UpdateTheme();

                    // Analyze all photos in background if user turned this feature on.
                    if(m_analyzeInBackground){
                        AnalyzePhotos();
                    }

                    dialog.dismiss();
                }
            });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        }
    }
    /* void HelpButtonPressed(View a_view) */

    /**/
    /*
    UpdateTheme

    NAME
        UpdateTheme()   - Updates the theme to match users preference.

    DESCRIPTION
        This function will set the theme of the app depending on if the user wants to use the
        LightTheme or the DarkTheme. One of these themes will always be set.

    AUTHOR
        Travis Whipple

    */
    /**/
    private void UpdateTheme(){

        LinearLayout mainLayout = findViewById(R.id.mainLayout);

        if (m_isDarkMode) {
            mainLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.ambient));
        }else{
            mainLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        }
    }
    /* void UpdateTheme() */

    /**/
    /*
    RemoveFragment

    NAME
        RemoveFragment()    - Removes a given fragment.

    SYNOPSIS
        void RemoveFragment(View a_view)
            a_view      --> View that called this function.

    DESCRIPTION
        This function will remove a fragment that calls it. Currently it only removes a
        SimilarImagesFragment. It will check that the remove similar images fragment button
        was the View who called this, as that is the only View that can remove the
        SimilarImagesFragment object from the view.

    AUTHOR
        Travis Whipple

    */
    /**/
    public void RemoveFragment(View a_view){

        if(a_view.getId() == findViewById(R.id.similarImagesRemoveFragment).getId()){
            RemoveSimilarImagesFragment();
        }
    }
    /* void RemoveFragment(View a_view) */

    /**/
    /*
    RemoveSimilarImagesFragment

    NAME
        RemoveSimilarImagesFragment() Removes the SimilarImagesFragment from view.

    SYNOPSIS
        This function will remove the SimilarImagesFragment only if it already exists. It also
        ensures that the photo gallery is not hidden be

    DESCRIPTION

    RETURNS

    AUTHOR
        Travis Whipple

    */
    /**/
    private void RemoveSimilarImagesFragment(){

        // Check that fragment exists.
        if(m_similarImagesFragment != null){
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.remove(m_similarImagesFragment);
            ft.commit();

            m_similarImagesFragment = null;

            // Bring background back into focus.
            GrayOutGallery(false);
        }
    }
    /* void RemoveSimilarImagesFragment() */

    /**/
    /*
    SimilarImagesActionButton

    NAME
        SimilarImagesActionButton() - Called when a SimilarImageFragment action button is pressed.

    SYNOPSIS
        void SimilarImagesActionButton(View a_view)
            a_view      --> Button selected.

    DESCRIPTION
        This function is called when an action button in the SimilarImagesFragment is selected.
        This function will call SimilarImagesFragment's function for dealing with action button
        presses. This is because buttons defined in a layout file inflated under this Context only
        can call functions within this Class.

    AUTHOR
        Travis Whipple

    */
    /**/
    public void SimilarImagesActionButton(View a_view){
        m_similarImagesFragment.ActionButtonPressed(a_view);
    }
    /* void SimilarImagesActionButton(View a_view) */

    /**/
    /*
    AnalyzePhotos

    NAME
        AnalyzePhotos()     - Will attempt to analyze all photos in data set.

    DESCRIPTION
        This function will create a thread to analyze for faces and tags in the background. This
        allows the user full functionality while images are being processed. It will only analyze
        images if the user has turned on the "Analyze photos in background" feature from the help
        menue.

    AUTHOR
        Travis Whipple

    */
    /**/
    public void AnalyzePhotos(){

        // Only analyze photos if analyze photos in background is turned on.
        if(m_analyzeInBackground){

            // Analyze photo for tags.
            MainActivity.AnalyzePhotoThread thread =
                    new MainActivity.AnalyzePhotoThread(m_imageManager.GetImages());

            thread.start();

        }
    }
    /* void AnalyzePhotos() */

    /**/
    /*
    AnalyzePhotoButtonSelected

    NAME
        AnalyzePhotoButtonSelected()  - Analyzes a given image for tags.

    SYNOPSIS
        void AnalyzePhotoButtonSelected(View a_view)
            a_view     --> View who called this function.

    DESCRIPTION
        This function creates an annotation request for Google Cloud Vision. It then sends the
        request on an AsyncTask. The AsyncTask allows for internet access to PUSH the request
        to Google's servers for analyzation.

    AUTHOR
        Travis Whipple

    */
    /**/
    public void AnalyzePhotoButtonSelected(View a_view){

        if(a_view.getId() == R.id.analyzeButton){
            String imagePath = m_similarImagesFragment.GetSelectedImagePath();

            // Analyze image only if Path is not null.
            if(!imagePath.equals("")){

                // Add image to an ArrayList.
                ArrayList<ImageObject> imageObjects = new ArrayList<>();
                imageObjects.add(m_imageManager.GetImageObject(imagePath));

                // Create Thread with the ArrayList.
                MainActivity.AnalyzePhotoThread thread =
                        new MainActivity.AnalyzePhotoThread(imageObjects);

                // Execute the thread.
                thread.start();
            }
        }
    }

    /**/
    /*

    CLASS
        AnalyzePhotoThread  - Analyzes photos for tags in a background thread.

    DESCRIPTION
        This class is used to analyze photos while the user uses the app. It will only analyze
        photos that have not already been analyzed.

    RETURNS

    AUTHOR
        Travis Whipple

    */
    /**/
    class AnalyzePhotoThread extends Thread {

        // List of images to be analyzed.
        private ArrayList<ImageObject> m_imagesToAnalyze;

        // Constructor stores images to be analyzed
        AnalyzePhotoThread(ArrayList<ImageObject> a_imagesToBeAnalyzed) {
            m_imagesToAnalyze = a_imagesToBeAnalyzed;
        }

        // Override run function to define custom background computation.
        @Override
        public void run() {

            // Put thread in background.
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

            for(ImageObject image : m_imagesToAnalyze){

                // Only analyze photos that have not already been analyzed.
                if(!image.GetHasBeenAnalyzed()){

                    // Log current image being analyzed.
                    Log.i("Analyzing image", image.GetPath());
                    AnalyzeImage(image.GetPath());
                }

                if(!image.GetHasDetectedFaces()){
                    Log.i("Detecting faces for Image", image.GetPath());
                    m_imageManager.AnalyzeForFaces(image.GetPath());
                }
            }
        }
    }
    /* class AnalyzePhotoThread extends Thread */

    /**/
    /*
    AnalyzeImage

    NAME
        AnalyzeImage()  - Analyzes a given image for tags.

    SYNOPSIS
        void AnalyzeImage(String a_imagePath, boolean a_hasTriedToAnalyze)
            a_imagePath     --> Path of image file to be analyzed.
            a_hasTriedToAnalyze     --> Whether or not an image has tried to be analyzed before.

    DESCRIPTION
        This function creates an annotation request for Google Cloud Vision. It then sends the
        request on an AsyncTask. The AsyncTask allows for internet access to PUSH the request
        to Google's servers for analyzation.

    AUTHOR
        Travis Whipple

    */
    /**/
    public void AnalyzeImage(String a_imagePath, boolean a_hasTriedToAnalyze) {


        try {
            // Get annotated request for Cloud Vision.
            AnnotationRequest annotationRequest = new AnnotationRequest(this);
            Vision.Images.Annotate annotatedRequest = annotationRequest.CreateRequest(a_imagePath);

            // Create AsyncTask to access Google Cloud Vision and get results.
            AsyncTask<Object, Void, String> labelDetectionTask = new LabelDetectionTask(
                    this,
                    annotatedRequest,
                    a_imagePath,
                    a_hasTriedToAnalyze);

            // Start the AsyncTask.
            labelDetectionTask.execute();

        } catch (IOException e) {
            Log.e("MainActivity.AnalyzeImage", e.getMessage());
        }
    }
    /* void AnalyzeImage(String a_imagePath) */

    // Calls AnalyzeImages with the image not having been analyzed before.
    public void AnalyzeImage(String a_imagePath){
        AnalyzeImage(a_imagePath, false);
    }

    /**/
    /*

    CLASS
        LabelDetectionTask  - PUSH annotation request to Google's servers.

    DESCRIPTION
        This Class Sends an annotated request to Google's servers and parses the
        response. The response is used to add the relation between the tags found and the
        image being analyzed.

    AUTHOR
        Travis Whipple

    DATE
        4/25/2018

    */
    /**/
    private static class LabelDetectionTask extends AsyncTask<Object, Void, String> {

        // Reference back to main activity
        private final WeakReference<MainActivity> m_MainActivity;

        // Annotation request, and image associated with request.
        private Vision.Images.Annotate m_annotatedRequest;
        private String m_imagePath;

        private boolean m_hasTriedAnalyzing;

        /**/
        /*
        LabelDetectionTask

        NAME
            LabelDetectionTask()    - Constructor.

        SYNOPSIS
            LabelDetectionTask(MainActivity a_activity, Vision.Images.Annotate a_annotatedRequest,
                            String a_imagePath)
                a_activity          --> The MainActivity that created this task.
                a_annotatedRequest  --> Annotated request to be used.
                a_imagePath         --> Path of image file.
                a_hasTriedAnalyzing --> If an image has tried to be analyzed before but failed.

        DESCRIPTION
            Constructor initializes all member variables. The boolean flag is so that we can try
            to analyze a photo a second time. Sometimes if the queue is too long, request will
            time out, so try again, but only once more.

        AUTHOR
            Travis Whipple

        */
        /**/
        LabelDetectionTask(MainActivity a_activity, Vision.Images.Annotate a_annotatedRequest,
                           String a_imagePath, boolean a_hasTriedAnalyzing) {

            m_MainActivity = new WeakReference<>(a_activity);
            m_annotatedRequest = a_annotatedRequest;
            m_imagePath = a_imagePath;
            m_hasTriedAnalyzing = a_hasTriedAnalyzing;
        }
        /* LabelDetectionTask(MainActivity a_activity, Vision.Images.Annotate a_annotatedRequest,
                           String a_imagePath) */

        /**/
        /*
        doInBackground

        NAME
            doInBackground()    - Task to be done in background.

        SYNOPSIS
            String doInBackground(Object... a_params)
                a_params        --> List of parameters, unused in this function.

        DESCRIPTION
            This function overrides AsyncTask's doInBackground function. We do not need the
            parameter list passed, but it is necessary to include it in order to override this
            function. The annotated request is executed here and the response has to be converted
            to a string as that is what the base function returns.

        RETURNS
            String      - All labels detected in image as well as their confidence value.

        AUTHOR
            Travis Whipple

        */
        /**/
        @Override
        protected String doInBackground(Object... a_params) {
            try {
                // Execute the annotated request.
                BatchAnnotateImagesResponse response = m_annotatedRequest.execute();

                // Have to return string since we are overriding original doInBackground function.
                return convertResponseToString(response);

                // Catch errors and log them.
            } catch (Exception e) {
                Log.e("MainActivity.LabelDetectionTask.doInBackground",
                        "Exception :" + e.getMessage());
            }

            return "ERROR";
        }
        /* String doInBackground(Object... params) */

        /**/
        /*
        onPostExecute

        NAME
            onPostExecute() - Called when this Task finishes.

        SYNOPSIS
            void onPostExecute(String a_result)
                a_result        --> Result of task, labels and confidence values found.

        DESCRIPTION
            This function is called after a an annotated request has been executed. The response
            will be parsed and the labels detected will be added to the data set as a new
            relation for the image being analyzed. If the request fails for "timeout" reasons,
            another request will be made to analyze the same image again. This is because if
            the queue for annotated requests is large, they will time out before they can be
            analyzed.

        AUTHOR
            Travis Whipple

        */
        /**/
        protected void onPostExecute(String a_result) {

            // Reference to main activity.
            MainActivity activity = m_MainActivity.get();

            // Make sure activity exists and is not finishing.
            if (activity != null && !activity.isFinishing()) {
                Log.i("onPostExecute", a_result);

                if(a_result.equals("ERROR")){
                    Log.e("MainActivity.LabelDetectionTask.onPostExecute", "Result is ERROR");

                    // Analyze photo again if this is its first time failing.
                    if(!m_hasTriedAnalyzing){

                        // Analyze this photo again.
                        m_MainActivity.get().AnalyzeImage(m_imagePath, true);
                    }else{

                        // Photo has tried to be analyzed twice, display error to user.
                        Toast.makeText(m_MainActivity.get().getApplicationContext(),
                                "Request failed for image " + m_imagePath,
                                Toast.LENGTH_LONG).show();

                        // Log error.
                        Log.e("MainActivity.LabelDetectionTask.onPostExecute",
                                "Image failed twice for analyzation " + m_imagePath);
                    }
                    return;
                }

                // Get each pair.
                String[] scoreAndDescription = a_result.split("\n");

                for(String foundPair : scoreAndDescription){

                    String[] data = foundPair.split(":");

                    double confidence = Double.parseDouble(data[0]);
                    String tagName = data[1];

                    activity.m_imageManager.AnalyzedImage(m_imagePath);
                    activity.m_imageManager.AddTagRelation(m_imagePath, tagName, confidence);
                }

                // Update similar images fragment to include new found tags.
                if(activity.m_similarImagesFragment != null){
                    activity.m_similarImagesFragment.UpdateLayout();
                }
            }
        }
        /* void onPostExecute(String a_result) */

        /**/
        /*
        convertResponseToString

        NAME
            convertResponseToString()   - Converts response to a String object containing
                                        label and confidence value.

        SYNOPSIS
            String convertResponseToString(BatchAnnotateImagesResponse a_response)
                a_response      --> Response from the annotated request.

        DESCRIPTION
            This class will parse the BatchAnnotateImagesResponse into a String format. The string
            returned will contain all labels and their confidence value pairs separated by a ':'
            on a new line. This makes parsing the String later easier.

        RETURNS
            String      - String with list of relations:    label:confidence
                        - Empty string if could not parse response.

        AUTHOR
            Travis Whipple

        */
        /**/
        private static String convertResponseToString(BatchAnnotateImagesResponse a_response) {

            // Use string builder instead of String as concatenating to a String is slower.
            StringBuilder stringResponse = new StringBuilder();

            // Create a list of EntityAnnotation, this class helps us parse the JSON response.
            // We get first instance in response since we are only looking for one feature: labels.
            List<EntityAnnotation> labels = a_response.getResponses().get(0).getLabelAnnotations();

            if (labels != null) {
                for (EntityAnnotation label : labels) {
                /* Parse JSON response into format "confidence:label"
                with confidence only having 3 points of precision.
                 */
                    stringResponse.append(String.format(Locale.US, "%.3f", label.getScore()));
                    stringResponse.append(":");
                    stringResponse.append(label.getDescription());
                    stringResponse.append("\n");
                }
            }

            return stringResponse.toString();
        }
        /* String convertResponseToString(BatchAnnotateImagesResponse a_response) */

    }
    /* class LabelDetectionTask extends AsyncTask<Object, Void, String> */

}
/* class MainActivity extends AppCompatActivity */