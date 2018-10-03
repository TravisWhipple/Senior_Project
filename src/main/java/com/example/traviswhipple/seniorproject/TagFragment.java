package com.example.traviswhipple.seniorproject;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.lang.ref.WeakReference;

/**/
/*

CLASS
    TagFragment - Displays all Tags for a given photo.

DESCRIPTION
    This class will display all Tags associated with a given image. Tags can be added or removed.
    Tags are displayed as Buttons so when the

DATE
    6/19/2018
*/
/**/
public class TagFragment extends Fragment {

    // Reference back to the MainActivity.
    private WeakReference<MainActivity> m_MainActivity;

    // Layout views.
    private ImageButton m_addTag;
    private ImageButton m_removeTag;
    private LinearLayout m_tagsLayout;

    // Data set.
    private ImageManager m_ImageManager;
    private Context m_Context;

    // Current image.
    private String m_ImagePath;
    private int m_SelectedImagePosition;

    // If user is currently removing tags.
    private boolean m_isRemovingTags;

    /**/
    /*

    NAME
        onCreateView()  - Called by Android when Fragment is created.

    DESCRIPTION
        Inflates the current view from a the layout file.

    AUTHOR
        Travis Whipple

    */
    /**/
    @Override
    public View onCreateView(LayoutInflater a_inflater, ViewGroup a_parent,
                             Bundle a_savedInstanceState) {

        // Inflate layout to use tag fragment layout XML file.
        return a_inflater.inflate(R.layout.tag_fragment_layout, a_parent, false);
    }
    /* View onCreateView(LayoutInflater a_inflater, ViewGroup a_parent,
                            Bundle a_savedInstanceState) */

    /**/
    /*
    onViewCreated

    NAME
        onViewCreated() - Called by Android after the View is inflated from resource file.

    DESCRIPTION
        This function will initialize all View member variables. Then it will call UpdateTags
        to display all tags to the screen.

    AUTHOR
        Travis Whipple

    */
    /**/
    @Override
    public void onViewCreated(View a_view, Bundle a_savedInstanceState) {

        m_isRemovingTags = false;

        // Initialize views from tag fragment layout.
        m_addTag = a_view.findViewById(R.id.addTagButton);
        m_removeTag = a_view.findViewById(R.id.removeTagButton);
        m_tagsLayout = a_view.findViewById(R.id.tagsLinearLayout);

        // Add onClickListeners to both buttons.
        m_addTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pressed(v);
                AddTag(v);
            }
        });

        /* When user selects the remove tag button it will turn red to reflect that they
        are in removing tag mode. Then selecting it again will revert the tag back to its
        original blue color.
         */
        m_removeTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get background so we can alter its color.
                Drawable drawable = m_removeTag.getBackground();

                // Wrap drawable so it works on devices before V.21
                drawable = DrawableCompat.wrap(drawable);

                // Set color of button to not removing tags color.
                ContextCompat.getColor(m_Context, R.color.accent);

                // Set color of button back.
                if(m_isRemovingTags){
                    m_isRemovingTags = false;
                    DrawableCompat.setTint(drawable, ContextCompat.getColor(
                            m_Context, R.color.accent));

                }else{
                    m_isRemovingTags = true;
                    DrawableCompat.setTint(drawable, ContextCompat.getColor(
                            m_Context, R.color.accentNegative));
                }

                UpdateTags();
            }
        });

        // Draw the view to the screen.
        UpdateTags();
    }
    /**/

    /**/
    /*
    SetArguments

    NAME
        SetArguments()  - Sets member variables from Parent who created this Fragment.

    SYNOPSIS
        void SetArguments(MainActivity a_activity, ImageManager a_imageManager,
                        Context a_context, int a_selectedPhotoPosition)
            a_activity      --> Parent Activity.
            a_imageManager  --> Data set.
            a_context       --> Context of Activity.
            a_selectedPhotoPosition     --> Current image selected's position.

    DESCRIPTION
        This function will initialize all member variables to reflect their parameter value.

    AUTHOR
        Travis Whipple

    */
    /**/
    public void SetArguments(MainActivity a_activity, ImageManager a_imageManager,
                             Context a_context, int a_selectedPhotoPosition){

        // Save data to be used to retrieve similar photo data.
        m_MainActivity = new WeakReference<>(a_activity);
        m_ImageManager = a_imageManager;
        m_Context = a_context;
        m_SelectedImagePosition = a_selectedPhotoPosition;

        // Convert position of image to the images path.
        m_ImagePath = m_ImageManager.GetImagePathFromPosition(m_SelectedImagePosition);
    }
    /* void SetArguments(MainActivity a_activity, ImageManager a_imageManager, Context a_context,
     int a_selectedPhotoPosition) */

    /**/
    /*
    UpdateTags

    NAME
        UpdateTags()    - Updates all tags for selected image.

    DESCRIPTION
        This function will draw all Tags of the selected image to the tagsLayout. The Tags
        will be blue if the user is not in removing tags mode, and red if they are. When
        a tag is selected while not in removing tags mode then that tag will be searched for
        using MainActivity's Searched function.

    RETURNS

    AUTHOR
        Travis Whipple

    */
    /**/
    public void UpdateTags(){

        // Remove all views from the layout.
        if(m_tagsLayout.getChildCount() > 0){
            m_tagsLayout.removeAllViews();
        }

        //
        int nextID = 0;
        for(Tag tag : m_ImageManager.GetAllTags(m_ImagePath)){
            Button button = new Button(m_Context);
            button.setText(tag.GetTagName());

            // Set padding to left and right of image.
            int padding = 5;
            button.setPadding(padding, 0, padding, 0);

            // ID is used when removing a specific tag.
            button.setId(nextID++);

            // Display buttons differently if user is removing tags.
            if(m_isRemovingTags){
                button.setBackgroundResource(R.color.accentNegative);
            }else{
                button.setBackgroundResource(R.color.accent);
            }

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(m_isRemovingTags){
                        RemoveTag(v);
                        UpdateTags();
                    }else{
                        Button tempBtn = (Button)v;
                        String tagName = tempBtn.getText().toString();
                        m_MainActivity.get().Searched(tagName);
                    }
                }
            });

            // Add view to layout.
            m_tagsLayout.addView(button);
        }
    }
    /* void UpdateTags() */

    /**/
    /*
    RemoveTag

    NAME
        RemoveTag() - Removes a tag from the view.

    SYNOPSIS
        void RemoveTag(View a_view)
            a_view      -- > View who called this function.

    DESCRIPTION
        Removes the tag relation between the selected image and the selected tag.

    AUTHOR
        Travis Whipple

    */
    /**/
    public void RemoveTag(View a_view){
        // Get tag to remove.
        int tagPosition = a_view.getId();
        String tagName = m_ImageManager.GetAllTags(m_ImagePath).get(tagPosition).GetTagName();
        m_ImageManager.RemoveTagRelation(m_ImagePath, tagName);

    }
    /**/

    /**/
    /*
    AddTag

    NAME
        AddTag()    - Adds a Tag to the current image.

    SYNOPSIS
        void AddTag(View a_view)
            a_view      --> Add tag button.

    DESCRIPTION
        This function will add a Tag to the selected image. The user will by prompted by a
        dialog where they can input the name of the new tag to add. When the user sets the
        Tag the relation will be added to the data set.

    AUTHOR
        Travis Whipple

    */
    /**/
    public void AddTag(View a_view){

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(m_Context);

        // Input is where user will input the tag they want.
        final EditText input = new EditText(m_Context);
        input.setHint("Things in photo");
        input.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        input.setLayoutParams(lp);
        alertDialog.setView(input);

        // Alert dialog to get user input.
        alertDialog
                .setTitle("Enter Tag")
                .setMessage("Any word")
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })

                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String userInput = input.getText().toString();

                        m_ImageManager.AddTagRelation(m_ImagePath, userInput);
                        UpdateTags();
                    }
                });

        alertDialog.create();
        alertDialog.show();
    }
    /* void AddTag(View a_view) */
}
/* class TagFragment extends Fragment */
