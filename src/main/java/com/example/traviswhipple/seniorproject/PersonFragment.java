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
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**/
/*

CLASS NAME
    PersonFragment - Displays a UI for adding and modifying People.

DESCRIPTION
    This class will display all People within the data set. It will display their name, with
    every face they contain below in a grid formation. Here a UI will be created where the user
    can add a new Person, add faces to a given Person, remove faces and remove a Person all
    together.

DATE
    7/28/2018
*/
/**/
public class PersonFragment extends Fragment {

    private ImageButton m_addPersonBtn;
    private ImageButton m_removePersonBtn;
    private LinearLayout m_peopleLayout;

    private GridLayout m_gridLayout;

    private Context m_context;
    private ImageManager m_imageManager;

    private boolean m_isRemovingPeople;
    private int m_numColumns;
    private int m_maxViewSize;

    private ImageButton m_submitBtn;
    private boolean m_isSelectingFaces;
    private Person m_Person;

    private WeakReference<MainActivity> mMainActivity;

    /**/
    /*
    onCreateView

    NAME
        onCreateView()  - Called when this View is created.

    SYNOPSIS
        View onCreateView(LayoutInflater a_inflater, ViewGroup a_parent,
                        Bundle a_savedInstanceState)
            a_inflater      --> Inflater used to inflate a XML resource file.
            a_parent        --> Parent who created this Fragment.
            a_savedInstanceState    --> Not used, needed in order to override parent function.

    DESCRIPTION
        This function is called when a view is created. Here we inflate a layout resource XML
        file that contains all Views and Layouts for this Fragment.

    AUTHOR
        Travis Whipple

    */
    /**/
    @Override
    public View onCreateView(LayoutInflater a_inflater, ViewGroup a_parent,
                             Bundle a_savedInstanceState) {

        // Inflate layout to use tag fragment layout XML file.
        return a_inflater.inflate(R.layout.person_fragment_layout, a_parent, false);
    }
    /* View onCreateView(LayoutInflater a_inflater, ViewGroup a_parent,
    Bundle a_savedInstanceState) */

    /**/
    /*
    onViewCreated

    NAME
        onViewCreated()     - Called after onCreateView, here we initialize member variables.

    SYNOPSIS
        void onViewCreated(View a_view, Bundle a_savedInstanceState)
            a_view      --> View created from inflating resource file.
            a_savedInstanceState        --> Again not used, but necessary for overriding parent
                                        function.

    DESCRIPTION
        This function will initialize all member variables as well as set onClick listeners
        for the various buttons. All Views within this layout will be initialized to their
        respected View from the Layout Resource XML file.

    AUTHOR
        Travis Whipple

    */
    /**/
    @Override
    public void onViewCreated(View a_view, Bundle a_savedInstanceState){

        // Get all View elements from resource file.
        m_addPersonBtn = a_view.findViewById(R.id.addPersonButton);
        m_removePersonBtn = a_view.findViewById(R.id.removePersonButton);
        m_peopleLayout = a_view.findViewById(R.id.peopleLinearLayout);
        m_gridLayout = a_view.findViewById(R.id.gridLayout);
        m_submitBtn = a_view.findViewById(R.id.submit);

        // Set flags to false by default.
        m_isRemovingPeople = false;
        m_isSelectingFaces = false;

        // Set onClick listeners for the various buttons.

        m_submitBtn.setVisibility(View.INVISIBLE);
        m_submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Called when a user is finished making adjustments to a Person.
                DoneSelectingImages();
            }
        });

        m_addPersonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Called when the user wants to add a new Person.
                AddPerson(v);
            }
        });

        m_removePersonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Called when user wants to remove a person.
                It will inverse the current state of whether a user is removing a
                person or not. */
                RemovingPerson(!m_isRemovingPeople);
                UpdateLayout();
            }
        });

        // Update layout to display everything.
        UpdateLayout();
    }
    /* void onViewCreated(View a_view, Bundle a_savedInstanceState) */

    /**/
    /*
    SetArguments

    NAME
        SetArguments()  - Sets member variables.

    SYNOPSIS
        void SetArguments(MainActivity a_activity,
                             ImageManager a_imageManager,
                             Context a_context,
                             int a_screenWidth,
                             int a_numberOfColumns)
            a_activity      --> Activity who created this Fragment.
            a_imageManager  --> ImageManager that contains all data sets.
            a_context       --> Context of Activity Fragment was created in.
            a_screenWidth   --> Width of display in number of pixels.
            a_numberOfColumns   --> Number of columns to be displayed in grid layout.

    DESCRIPTION
        This function sets all necessary member variables from the Activity that created this
        Fragment. This function must be called before the Fragment is added to the View.

    AUTHOR
        Travis Whipple

    */
    /**/
    public void SetArguments(MainActivity a_activity,
                             ImageManager a_imageManager,
                             Context a_context,
                             int a_screenWidth,
                             int a_numberOfColumns){
        // Save parameters.
        mMainActivity = new WeakReference<>(a_activity);
        m_imageManager = a_imageManager;
        m_context = a_context;
        m_numColumns = a_numberOfColumns;

        // Max view size for an element with n given rows.
        m_maxViewSize = a_screenWidth / m_numColumns;
    }
    /* void SetArguments(MainActivity a_activity,
                             ImageManager a_imageManager,
                             Context a_context,
                             int a_screenWidth,
                             int a_numberOfColumns) */

    /**/
    /*
    AddPerson

    NAME
        AddPerson()     - Called when the addPerson button is pressed.

    SYNOPSIS
        void AddPerson(View a_view)
            a_view      --> View who called this function.

    DESCRIPTION
        This function will display to the user a dialog where they can create a new Person. The
        dialog will prompt the user to enter the new Persons name. Once a name has been set,
        the user can now select every image that contains that Persons face. Selecting the check
        on the right will finalize the Person object and commit it to the data set.

    AUTHOR
        Travis Whipple

    */
    /**/
    public void AddPerson(View a_view){

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(m_context);

        // Input is where user will input the depth they want.
        final EditText input = new EditText(m_context);
        input.setHint("Persons Name");
        input.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        input.setLayoutParams(lp);
        alertDialog.setView(input);

        // Alert dialog to get user input.
        alertDialog
                .setTitle("Enter Name")
                .setMessage("Persons Name")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })

                .setPositiveButton("Add Person", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        // Get the name for the new Person.
                        String personName = input.getText().toString();

                        // User is now selecting faces for this Person.
                        m_isSelectingFaces = true;

                        // Create a new Person from the given name.
                        m_Person = m_imageManager.GetNewPerson(personName);

                        /* Show the m_submitBtn button so user can finalize Person after selecting
                        faces that belong to this Person. */
                        m_submitBtn.setVisibility(View.VISIBLE);

                        // Display to the user what to do next.
                        Toast.makeText(m_context,
                                "Select all faces with " + personName + "!",
                                Toast.LENGTH_LONG).show();

                        // Update the layout.
                        UpdateLayout();
                    }
                });

        // Display the dialog.
        alertDialog.create();
        alertDialog.show();
    }
    /* void AddPerson(View a_view) */

    /**/
    /*
    RemovePerson

    NAME
        RemovePerson()  - Removes a Person object from the data set.

    SYNOPSIS
        void RemovePerson(int a_ID)
            a_ID     --> ID of Person button selected.

    DESCRIPTION
        This function will attempt to remove a given Person from the data set. a_ID is the
        ID of the button displaying the persons name, which correlates to a Person object
        with that ID. A person can only be removed if the user has selected the remove button
        prior to selecting a Person.

    RETURNS

    AUTHOR
        Travis Whipple

    */
    /**/
    private void RemovePerson(int a_ID){

        // Check if a Person exists with the given ID.
        for(Person person : m_imageManager.GetPeople()){
            if(person.GetId() == a_ID){
                m_imageManager.RemovePerson(person);

                // No longer removing a person.
                RemovingPerson(false);
                UpdateLayout();
                break;
            }
        }

    }
    /* void RemovePerson(int a_index) */

    /**/
    /*
    RemovingPerson

    NAME
        RemovingPerson()    - Sets whether or not the user is currently removing a person.

    SYNOPSIS
        void RemovingPerson(boolean a_isRemoving)
            a_isRemoving        --> Whether or not user is going to remove a person.

    DESCRIPTION
        This function will chang the color of the remove person button to reflect if the user
        is going to remove a Person from the data set. When the button is selected, it will change
        from blue to red. The user can revert back out of removing person mode by re clicking the
        remove person button, this will revert the button back to blue, indicating that the
        user is no longer in remove person mode.

    AUTHOR
        Travis Whipple

    */
    /**/
    private void RemovingPerson(boolean a_isRemoving){

        // Save given flag.
        m_isRemovingPeople = a_isRemoving;
        Drawable drawable = m_removePersonBtn.getBackground();

        // Wrap drawable so we can chang its tint.
        drawable = DrawableCompat.wrap(drawable);

        // Set color of button depending on if the user is removing a person or not.
        if(m_isRemovingPeople){

            // Display message to inform user what action to take next.
            Toast.makeText(m_context,
                    "Select all faces to remove.",
                    Toast.LENGTH_LONG).show();

            DrawableCompat.setTint(drawable,
                    ContextCompat.getColor(m_context, R.color.accentNegative));
        }else{

            DrawableCompat.setTint(drawable,
                    ContextCompat.getColor(m_context, R.color.accent));
        }
    }
    /* void RemovingPerson(boolean a_isRemoving) */

    /**/
    /*
    UpdateLayout

    NAME
        UpdateLayout()  - Updates the current layout to display any changes.

    DESCRIPTION
        This function will add all member elements to the view.

    AUTHOR
        Travis Whipple

    */
    /**/
    private void UpdateLayout(){

        // Clear gridLayout.
        if(m_gridLayout != null && m_gridLayout.getChildCount() > 0){
            m_gridLayout.removeAllViews();
        }

        // Clear peopleLayout.
        if(m_peopleLayout != null && m_peopleLayout.getChildCount() > 0){
            m_peopleLayout.removeAllViews();
        }

        // Set how many columns grid layout should have.
        m_gridLayout.setColumnCount(m_numColumns);

        /*
        This will add the current person to the person layout even if the person has not been
        committed to the data set yet.
          */
        if(m_Person != null && !m_imageManager.GetPeople().contains(m_Person)){
            AddPersonToLayout(m_Person);
        }

        // Add people to person layout.
        for(Person person : m_imageManager.GetPeople()) {
            AddPersonToLayout(person);
        }

        // All faces to be displayed.
        ArrayList<FaceObject> facesAdded = new ArrayList<>();
        String lastPersonName = "";

        // Display each person's name, followed by their faces.
        for(Person currentPerson : m_imageManager.GetPeople()){

            // Display persons name.
            lastPersonName = currentPerson.GetName();
            TextView textView = CreateTextView(lastPersonName);
            m_gridLayout.addView(textView);

            // Display all faces for the current person.
            for(FaceObject face : currentPerson.GetFaces()){
                // Add each face to layout.
                facesAdded.add(face);
                ImageView imageView = CreateImageButton(face);
                m_gridLayout.addView(imageView);

            }

        }

        // Add all remaining faces under the label "Unknown Faces"
        if(facesAdded.size() < m_imageManager.GetAllFaces().size()) {

            // Display "Unknown Faces".
            lastPersonName = "Unknown Faces";
            TextView textView = CreateTextView(lastPersonName);
            m_gridLayout.addView(textView);

            // Display all faces that do not belong to a person.
            for(FaceObject face : m_imageManager.GetAllFaces()){

                // Only add faces that have not been added already.
                if(!facesAdded.contains(face)){
                    facesAdded.add(face);
                    ImageView imageView = CreateImageButton(face);
                    m_gridLayout.addView(imageView);
                }
            }
        }
    }
    /**/

    /**/
    /*
    AddPersonToLayout

    NAME
        AddPersonToLayout()     - Adds a button containing persons name to the layout.

    SYNOPSIS
        void AddPersonToLayout(Person a_person)
            a_person        --> Person to be added to layout.

    DESCRIPTION
        This function will add a Button that displays a Person's name. These buttons will be
        displayed at the top of the view. When a button is selected the user can add faces
        and remove faces for the given person.

    AUTHOR
        Travis Whipple

    */
    /**/
    private void AddPersonToLayout(Person a_person){

        // Create a new button for this person.
        Button button = new Button(m_context);
        button.setId(a_person.GetId());

        // Set button to display persons name.
        button.setText(a_person.GetName());
        button.setTextColor(ContextCompat.getColor(m_context, R.color.textColor));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check if user is in removing people mode.
                if(m_isRemovingPeople){
                    // If they are, then remove this person they selected.
                    RemovePerson(v.getId());
                }else{
                    // Else, set the current person to the person selected.
                    SetPerson(v.getId());
                }
            }
        });

        // Re-color the button to reflect which person user has set.
        Drawable drawable = button.getBackground();

        // Wrap drawable so it works on devices before V.21
        drawable = DrawableCompat.wrap(drawable);

        // Set color of button.
        if(a_person == m_Person){
            //button.setBackgroundColor(ContextCompat.getColor(m_context, R.color.colorSubmit));
            DrawableCompat.setTint(drawable,
                    ContextCompat.getColor(m_context, R.color.colorSubmit));
        }else{
            //button.setBackgroundColor(ContextCompat.getColor(m_context, R.color.colorPrimary));
            DrawableCompat.setTint(drawable,
                    ContextCompat.getColor(m_context, R.color.primary));
        }

        // Add button to layout.
        m_peopleLayout.addView(button);
    }
    /* void AddPersonToLayout(Person a_person) */

    /**/
    /*
    SetPerson

    NAME
        SetPerson() - Sets the current person selected.

    SYNOPSIS
        void SetPerson(int a_selectedPersonID)
            a_selectedPersonID     --> ID of person selected.

    DESCRIPTION
        This function will enable the user to edit faces associated with a given person. They can
        click an image to add it to that person, or select an existing face of the person to
        remove that face from the person.

    AUTHOR
        Travis Whipple

    */
    /**/
    private void SetPerson(int a_selectedPersonID){

        // Check if index matches a persons ID.
        for(Person currentPerson : m_imageManager.GetPeople()){
            if(currentPerson.GetId() == a_selectedPersonID){
                m_Person = currentPerson;
                m_isSelectingFaces = true;
                m_submitBtn.setVisibility(View.VISIBLE);
                UpdateLayout();
                break;
            }
        }
    }
    /* void SetPerson(int a_selectedPersonID) */
    /*

    NAME
    CreateTextView

    SYNOPSIS
        CreateTextView()    - Creates a text view with the given text.

    DESCRIPTION
        The text view created here will span across all columns. This is so that it will split
        the grid view on each new text view added.

    RETURNS
        TextView    - New View created with given text.

    AUTHOR
        Travis Whipple

    */
    /**/
    private TextView CreateTextView(String a_text){

        // Create TextView from template.
        TextView textView = (TextView) LayoutInflater.from(
                m_context).inflate(R.layout.text_view_template, null);

        // Set the given text.
        textView.setText(a_text);

        // Set TextView to span across all columns.
        GridLayout.Spec rowSpan = GridLayout.spec(GridLayout.UNDEFINED, 1);
        GridLayout.Spec colspan = GridLayout.spec(GridLayout.UNDEFINED, m_numColumns);
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(rowSpan, colspan);

        textView.setLayoutParams(layoutParams);

        return textView;
    }
    /* TextView CreateTextView(String a_text) */

    /**/
    /*
    CreateImageButton

    NAME
        CreateImageButton() - Creates an ImageButton for a given face.

    SYNOPSIS
        ImageView CreateImageButton(FaceObject a_face)
            a_face      --> Face to create image button for.

    DESCRIPTION
        This function will create a new TextView from the given face. It will set its onClick
        listener appropriately. Images will be loaded using Glide for faster loading.

    RETURNS

    AUTHOR
        Travis Whipple

    */
    /**/
    private ImageView CreateImageButton(FaceObject a_face){

        ImageView imageView = new ImageView(m_context);
        imageView.setLayoutParams(new GridView.LayoutParams(m_maxViewSize, m_maxViewSize));
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        int padding = 1;
        imageView.setPadding(padding, padding, padding, padding);

        imageView.setCropToPadding(false);
        imageView.setAdjustViewBounds(true);

        // Set id to match face's id.
        imageView.setId(a_face.GetId());

        MainActivity activity = mMainActivity.get();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Call activities ImageSelected function.
                This is similar to defining a "onClick" attribute in XML file, but
                we call it this way as this view is created dynamically.
                */
                if(m_isSelectingFaces){
                    ImageSelected(v.getId());
                }else{
                    String imagePath = m_imageManager.GetFace(v.getId()).GetImagePath();
                    activity.ImageSelected(m_imageManager.GetPosition(imagePath));
                }
            }
        });

        /*
        This will clear glides cache every 5 minutes to prevent from loading an old face from
        its cache into the view.
        */
        int fiveMinuteInMillisecods = 5 * 60 * 1000;
        Glide.with(this)
                .load(a_face.GetFacePath())
                .signature(new StringSignature(String.valueOf(
                        System.currentTimeMillis() / (fiveMinuteInMillisecods))))
                .into(imageView);


        if(m_isSelectingFaces){
            // Set background color if image selected.
            if(m_Person.GetFaces().contains(a_face)){
                imageView.setBackgroundColor(
                        ContextCompat.getColor(m_context, R.color.colorSubmit));
            }
        }


        return imageView;
    }
    /* ImageView CreateImageButton(FaceObject a_face) */

    /**/
    /*
    ImageSelected

    NAME
        ImageSelected   - Called when an image is selected.

    SYNOPSIS
        void ImageSelected(int a_imageId)
            a_imageId       --> ID of image selected.

    DESCRIPTION
        This function will add or remove a face if the user is in selecting faces mode.

    RETURNS

    AUTHOR
        Travis Whipple

    */
    /**/
    private void ImageSelected(int a_imageId){

        if(m_isSelectingFaces){
            // Find face with given ID.
            for(FaceObject selectedFace : m_imageManager.GetAllFaces()){
                if(selectedFace.GetId() == a_imageId){

                    // Check that face does not already belong to a person.
                    for(Person p : m_imageManager.GetPeople()){
                        for(FaceObject f : p.GetFaces()){

                            if(f.GetId() == selectedFace.GetId()){

                                // If this face does not belong to the current person, then
                                // the face can not be added or removed.
                                if(p != m_Person){
                                    return;
                                }
                            }
                        }
                    }

                    // Add or remove face from person.
                    if(m_Person.GetFaces().contains(selectedFace)){
                        m_Person.RemoveFace(selectedFace);
                    }else{
                        m_Person.AddFace(selectedFace);
                    }

                    // Display changes.
                    UpdateLayout();
                    break;
                }
            }
        }

        if(m_isRemovingPeople){
            RemovePerson(a_imageId);
        }
    }
    /* void ImageSelected(int a_imageId) */

    /**/
    /*
    DoneSelectingImages

    NAME
        DoneSelectingImages()   - Called when user selects the check button.

    DESCRIPTION
        This function will finalize the create new Person process. It will commit the Person
        created to the data set.

    AUTHOR
        Travis Whipple

    */
    /**/
    private void DoneSelectingImages(){

        m_submitBtn.setVisibility(View.INVISIBLE);
        m_isSelectingFaces = false;

        // Only add person to data set if it contains faces.
        if(m_Person.GetFaces().size() > 0){
            m_imageManager.AddPerson(m_Person);
        }
        m_Person = null;
        UpdateLayout();
    }
    /* void DoneSelectingImages() */

}
/* class PersonFragment extends Fragment */
