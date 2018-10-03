package com.example.traviswhipple.seniorproject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

/**/
/*

CLASS NAME
    ImageManager - Manages all relations between images, tags, people and faces.

DESCRIPTION
    This class is the brain of the program, it is where all relations of photos are created.
    Every image can correlate to different tags that were found in the image, faces in the image
    and people in the image. This class will ensure that images, tags, people and faces are only
    added once. Relations between tags and images can be created or removed. Images and Tags are
    a many to many relationship so each image can have many tags and each tag can have many images
    associated with them. Objects are stored in dynamic containers to allow them to contain any
    number of elements. This class is also responsible for detecting faces in a specific image,
    or all images existing in the set. All objects being retrieved from a given set are checked
    that they exist to avoid returning an item that has either been removed, or items that do
    not exist.

DATE
    4/22/2018
*/
/**/

public class ImageManager {

    // Context of Activity where class was initialized.
    private Context m_Context;

    // ID to be used for the next Face or Person objects created.
    private int m_nextFaceId;
    private int m_nextPersonId;

    // Private members that contain data set.
    private ArrayList<ImageObject> m_images;
    private ArrayList<Tag> m_tags;
    private ArrayList<FaceObject> m_faces;
    private ArrayList<Person> m_people;


    /**/
    /*
    NAME
        ImageManager()

    SYNOPSIS
        ImageManager(Context a_context)
            a_context   --> Context of Activity where ImageManger was created.

    DESCRIPTION
        General constructor saves the applications context then initializes all member variables
        by calling helper function Initialize().

    AUTHOR
        Travis Whipple

    DATE
        4/22/2018
    */
    /**/
    public ImageManager(Context a_context){
        m_Context = a_context;
        Initialize();
    }
    /* ImageManager(Context a_context) */

    /**/
    /*
    NAME
        Initialize()    - Initializes member variables.

    DESCRIPTION
        This class initializes all member sets to new empty sets. It also initializes the next
        face id to be 0, as there exists no faces currently.

    AUTHOR
        Travis Whipple

    DATE
        4/22/2018
    */
    /**/
    private void Initialize(){
        m_tags = new ArrayList<>();
        m_images = new ArrayList<>();
        m_faces = new ArrayList<>();
        m_people = new ArrayList<>();

        m_nextFaceId = 0;
        m_nextPersonId = 0;
    }
    /* void Initialize() */

    /**/
    /*
    AddImage

    NAME
        AddImage()      - Adds an image to the set, without duplicates.

    SYNOPSIS
        void AddImage(ImageObject a_image)
            a_image     --> ImageObject to be added to set.

    DESCRIPTION
        This function will add an ImageObject to the data set. It will only add the ImageObject
        if it does not already exist in data set. ImageObjects are added in order of the date
        they were taken.

    AUTHOR
        Travis Whipple

    DATE
        4/22/2018
    */
    /**/
    public void AddImage(ImageObject a_image){

        // Only Add image if it is not already in set of m_images.
        if(!m_images.contains(a_image)){
            m_images.add(a_image);

            // Re-sort images, as the image added may be out of place.
            SortImages();
        }
    }
    /* void AddImage(ImageObject a_image) */

    /**/
    /*
    GetImages

    NAME
        GetImages() - Returns all ImageObjects in data set.

    SYNOPSIS
        ArrayList<ImageObject> GetImages()
            No parameters taken.

    DESCRIPTION
        This function returns all ImageObjects currently in the data set.

    RETURNS
        ArrayList<ImageObject>      - List of all ImageObjects currently in data set, or
                                    - Empty list if no ImageObjects are in data set.

    AUTHOR
        Travis Whipple

    DATE
        4/22/2018
    */
    /**/
    public ArrayList<ImageObject> GetImages(){
        return m_images;
    }
    /* ArrayList<ImageObject> GetImages() */

    /**/
    /*
    GetImageObject

    NAME
        GetImageObject()    - Gets an image object with the given path.

    SYNOPSIS
        ImageObject GetImageObject(String a_imagePath)
            a_imagePath     --> Absolute path of image to get ImageObject.

    DESCRIPTION
        This function will get an ImageObject from the given path. If no ImageObject exists in
        the data set with the given path, a new object will be created, otherwise the existing
        object will be returned.

    RETURNS
        ImageObject     - Either a new ImageObject created from the given path, or an existing
                        ImageObject with the given path from the data set.

    AUTHOR
        Travis Whipple

    DATE
        4/22/2018
    */
    /**/
    public ImageObject GetImageObject(String a_imagePath){

        ImageObject io = new ImageObject(a_imagePath);
        int index = m_images.indexOf(io);

        if(index == -1){
            Log.e("ImageManager.GetImageObject", "Created new image + " + a_imagePath);
            return io;
        }else{
            return m_images.get(index);
        }

    }
    /* ImageObject GetImageObject(String a_imagePath) */

    /**/
    /*
    GetImageObjectIfExists

    NAME
        GetImageObjectIfExists()    - Gets an ImageObject from data set if it exists.

    SYNOPSIS
        ImageObject GetImageObjectIfExists(String a_imagePath)
            a_imagePath     --> Absolute path of image to get from data set.

    DESCRIPTION
        This function will try to find an ImageObject in the data set that contains the given
        image path. If no ImageObject exists with the given path, then no new objects will be
        returned, instead a null object reference will be returned. This function should only
        be used if you only want an already existing ImageObject from the data set.

    RETURNS
        ImageObject     - ImageObject found with the given path.
                        - Null if no object was found.

    AUTHOR
        Travis Whipple

    DATE
        4/22/2018
    */
    /**/
    public ImageObject GetImageObjectIfExists(String a_imagePath){

        ImageObject io = new ImageObject(a_imagePath);

        int index = m_images.indexOf(io);

        if(index == -1){
            return null;
        }
        return m_images.get(index);
    }
    /* ImageObject GetImageObjectIfExists(String a_imagePath) */

    /**/
    /*
    GetAllPaths

    NAME
        GetAllPaths()   - Returns a list of every image's path in the set.

    DESCRIPTION
        This function will construct an ArrayList and for each image in set, it will add that
        images absolute path to the ArrayList to be returned. If no images are in the set then
        an ArrayList with no elements will be returned.

    RETURNS
        ArrayList<String>       - Each image's absolute path as a String.

    AUTHOR
        Travis Whipple

    DATE
        4/22/2018
    */
    /**/
    public ArrayList<String> GetAllPaths(){

        ArrayList<String> allPaths = new ArrayList<>();

        for(ImageObject image : m_images){
            allPaths.add(image.GetPath());
        }

        return allPaths;
    }
    /* ArrayList<String> GetAllPaths() */

    /**/
    /*
    GetImagePathFromPosition

    NAME
        GetImagePathFromPosition()      - Gets an ImageObject's image path from a position relative
                                        to first ImageObject in data set.

    SYNOPSIS
        String GetImagePathFromPosition(int a_position)
            a_position      --> Position of image in data set.

    DESCRIPTION
        This function is used to get an image's path from a position that is respect to the first
        image in data set. This is useful when a View containing an image is selected. A View's
        ID can only be an integer, and since ImageObjects are referenced off of their associated
        image's absolute path which is a string, an integer of its position relative to the first
        image is used instead.

    RETURNS
        String      - Absolute path of respected ImageObject's image file.
                    - Empty String if position is out of data set bounds.

    AUTHOR
        Travis Whipple

    DATE
        4/22/2018
    */
    /**/
    public String GetImagePathFromPosition(int a_position){
        if(a_position < 0 || a_position >= m_images.size()){
            return "";
        }
        return m_images.get(a_position).GetPath();
    }
    /* String GetImagePathFromPosition(int a_position) */

    /**/
    /*
    GetPosition

    NAME
        GetPosition()   - Gets the position of an ImageObject from an image file's absolute path.

    SYNOPSIS
        int GetPosition(String a_path)
            a_path      --> Absolute path of ImageObject's image file.

    DESCRIPTION
        This function will call GetPosition(ImageObject a_image) from a given image's path.

    RETURNS
        int     - Return from GetPosition(ImageObject a_image)

    AUTHOR
        Travis Whipple

    DATE
        4/22/2018
    */
    /**/
    public int GetPosition(String a_path){
        ImageObject io = GetImageObjectIfExists(a_path);
        return GetPosition(io);
    }
    /* int GetPosition(String a_path) */

    /**/
    /*
    GetPosition

    NAME
        GetPosition()   - Gets position of an ImageObject with respect to the first ImageObject.

    SYNOPSIS
        int GetPosition(ImageObject a_image)
            a_image     --> ImageObject to get position from.

    DESCRIPTION
        This function will find the position of an ImageObject in data set with respect to the
        first (most recently taken) image. This is useful for giving a View an ID that correlates
        to which ImageObject it represents. As View's IDs can only be integer values.

    RETURNS
        int     - Index of ImageObject in data set.
                - -1 if ImageObject does not exist in data set.

    AUTHOR
        Travis Whipple

    DATE
        4/22/2018
    */
    /**/
    public int GetPosition(ImageObject a_image){
        // If object is null, then -1 will be returned.
        return m_images.indexOf(a_image);
    }
    /* int GetPosition(ImageObject a_image) */

    /**/
    /*
    AddTagRelation

    NAME
        AddTagRelation()    - Adds a relationship between a tag and an image.

    SYNOPSIS
        void AddTagRelation(String a_imagePath, String a_tagName, double a_confidence)
            a_imagePath     --> Absolute path of image to create relation to.
            a_tagName       --> Name of the tag to create a relation to.
            a_confidence    --> Confidence rating, how accurate a tag defines an image.

    DESCRIPTION
        This function is used to define relations between an image and tag with the given
        confidence. This function is called either if an ImageObject and Tag object have
        not been created yet, or if the calling class does not have access to the objects
        them self. This function will find, or create if they do not exist, the objects
        corresponding to the given parameters and add the relationship between them.

    AUTHOR
        Travis Whipple

    DATE
        4/22/2018
    */
    /**/
    public void AddTagRelation(String a_imagePath, String a_tagName, double a_confidence){
        ImageObject tempImageObj = GetImageObject(a_imagePath);
        Tag tagObject = GetTagObject(a_tagName);
        AddTagRelation(tempImageObj, tagObject, a_confidence);
    }
    /* void AddTagRelation(String a_imagePath, String a_tagName, double a_confidence) */

    /**/
    /*
    AddTagRelation

    NAME
        AddTagRelation()    - Adds a relation between an image and a tag when
                            no confidence is defined.

    SYNOPSIS
        void AddTagRelation(String a_imagePath, String a_tagName)
            a_imagePath     --> Absolute path of image to create relation to.
            a_tagName       --> Name of the tag to create a relation to.

    DESCRIPTION
        This function is the same as the AddTagRelation(String a_imagePath, String a_tagName,
        double a_confidence) function except this function lacks a confidence parameter. This
        is because if a relation between an image and tag is being created without a known
        confidence, then it was most likely entered in by the user. This function is mainly
        for Java's lack of default parameter values as these two functions could be one by
        defining the confidence value in the function prototype.

    AUTHOR
        Travis Whipple

    DATE
        4/22/2018
    */
    /**/
    public void AddTagRelation(String a_imagePath, String a_tagName){
        ImageObject imageObjToAdd = GetImageObject(a_imagePath);
        Tag tagObjToAdd = new Tag(a_tagName);
        AddTagRelation(imageObjToAdd, tagObjToAdd, 1);
    }
    /* void AddTagRelation(String a_imagePath, String a_tagName) */

    /**/
    /*
    AddTagRelation

    NAME
        AddTagRelation()    - Adds a relationship between an image and its associated tag with
                            a given confidence value.

    SYNOPSIS
        void AddTagRelation(ImageObject a_imageObject, Tag a_tagObject, double a_confidence)
            a_imageObject       --> image to create relation to tag.
            a_tagObject         --> tag to create relation to image.
            a_confidence        --> Confidence value to how well tag defines contents of image.

    DESCRIPTION
        This function is where all Relations are actually created. Each Tag and ImageObject are
        checked that they exist in data set, and are added if not. A relation between a tag and
        image only exists once. The given confidence value is added to the Tags relation to
        the given image.

    AUTHOR
        Travis Whipple

    DATE
        4/22/2018
    */
    /**/
    public void AddTagRelation(ImageObject a_imageObject, Tag a_tagObject, double a_confidence){

        // Try to Add image and tag to data set if they are not already in it.
        AddImage(a_imageObject);

        // Check if tag with same name already exists.
        if(m_tags.contains(a_tagObject)){
            // Get existing tag so we can add a new image to its set of images.
            a_tagObject = GetTagObject(a_tagObject.GetTagName());
        }else{
            // Add tag to set.
            AddTag(a_tagObject);
        }

        // Add the image to tags list of images.
        a_tagObject.AddImage(a_imageObject, a_confidence);

        // Add the tag to images list of tags.
        a_imageObject.AddTag(a_tagObject);
    }
    /* void AddTagRelation(ImageObject a_imageObject, Tag a_tagObject, double a_confidence) */

    /**/
    /*
    AddTag

    NAME
        AddTag()    - Adds a tag to the set.

    SYNOPSIS
        void AddTag(Tag a_tagObject)
            a_tagObject     --> Tag object to be added to data set.

    DESCRIPTION
        This function is a private function used to add Tag objects to the data set. A Tag object
        will only be added if they do not exist in data set already. Tags are added in order
        based on how many images they point to.

    AUTHOR
        Travis Whipple

    DATE
        4/22/2018
    */
    /**/
    private void AddTag(Tag a_tagObject){
        // Only Add tag if it is not already in set of m_tags.
        if(!m_tags.contains(a_tagObject)){
            m_tags.add(a_tagObject);

            // Sort tags as tagObject may have been added out of order.
            SortTags();
        }
    }
    /* void AddTag(Tag a_tagObject) */

    /**/
    /*
    RemoveTagRelation

    NAME
        RemoveTagRelation()     - Removes a relation between a tag and an image.

    SYNOPSIS
        void RemoveTagRelation(String a_imagePath, String a_tagName)
            a_imagePath     --> Absolute path of image.
            a_tagName       --> Name of tag.

    DESCRIPTION
        This function will remove a relationship between a given image and tag. It will check
        that both the given tag and image exist in the data set and will then remove the
        relationship between the two. The image will remain in the set regardless if it has any
        tags still pointing to it. On the other hand, the given tag will be removed from the data
        set if it no longer points to any images, as it is now useless.

    AUTHOR
        Travis Whipple

    DATE
        4/22/2018
    */
    /**/
    public void RemoveTagRelation(String a_imagePath, String a_tagName){
        ImageObject imageObject = GetImageObjectIfExists(a_imagePath);

        // Check that given image is in set.
        if(imageObject != null){
            Tag tag = GetTagObjectIfExists(a_tagName);

            // Check that given tag is in set.
            if(tag != null){
                imageObject.RemoveTag(tag);
                tag.RemoveImage(imageObject);

                // If tag no longer points to any images, remove it from set.
                if(tag.GetImageList().size() == 0){
                    m_tags.remove(tag);
                }
            }
        }
    }
    /* void RemoveTagRelation(String a_imagePath, String a_tagName) */


    /**/
    /*
    GetTags

    NAME
        GetTags()   - Gets all tags within data set.

    DESCRIPTION
        This function will return all Tag objects currently in the data set.

    RETURNS
        ArrayList<Tag>  - All Tag objects in data set.

    AUTHOR
        Travis Whipple

    DATE
        4/22/2018
    */
    /**/
    public ArrayList<Tag> GetTags(){
        return m_tags;
    }
    /* ArrayList<Tag> GetTags() */


    /**/
    /*
    GetTagObject

    NAME
        GetTagObject()  - Gets a Tag object from given tag name.

    SYNOPSIS
        Tag GetTagObject(String a_tagName)
            a_tagName       --> Name of tag to find.

    DESCRIPTION
        This function will try to find a Tag object with the given name. If no object is found
        then a new Tag object created from the given name will be returned.

    RETURNS
        Tag     - Either a Tag object that exists in the data set, or
                - New Tag object created from the given name.

    AUTHOR
        Travis Whipple

    DATE
        4/22/2018
    */
    /**/
    public Tag GetTagObject(String a_tagName){

        Tag tagObject = new Tag(a_tagName);
        int index = m_tags.indexOf(tagObject);

        if(index == -1){
            return tagObject;
        }

        return m_tags.get(index);
    }
    /* Tag GetTagObject(String a_tagName) */

    /**/
    /*
    GetTagObjectIfExists

    NAME
        GetTagObjectIfExists()      - Will get a Tag from the data set only if Tag already exists.

    SYNOPSIS
        Tag GetTagObjectIfExists(String a_tagName)
            a_tagName       --> Name of tag to be found.

    DESCRIPTION
        This function will try and find a Tag object with the given tag name. If no Tag object
        is found with the given tag name then a Null object will be returned. This is useful if
        you only want to get objects that exist in the data set.

    RETURNS
        Tag     - Tag object found, or
                - Null if no object was found.

    AUTHOR
        Travis Whipple

    DATE
        4/22/2018
    */
    /**/
    private Tag GetTagObjectIfExists(String a_tagName){
        Tag tagObject = new Tag(a_tagName);

        if(m_tags.contains(tagObject)){
            return m_tags.get(m_tags.indexOf(tagObject));
        }else{
            return null;
        }
    }
    /* Tag GetTagObjectIfExists(String a_tagName) */

    /**/
    /*
    GetAllTags

    NAME
        GetAllTags()    - Returns all Tag objects in data set.

    SYNOPSIS
        ArrayList<Tag> GetAllTags(String a_path)
            a_path      --> Absolute path of image file.

    DESCRIPTION
        This function will find get all Tag objects associated with a given ImageObject who's
        image file's path matches the given path. It will check that an ImageObject exists
        that contains the given image file path, if no ImageObject exists then a list with
        no elements will be returned.

    RETURNS
        ArrayList<Tag>      - All Tag objects associated with given image path's ImageObject, or
                            - Empty list with no elements if ImageObject could not be found.

    AUTHOR
        Travis Whipple

    DATE
        4/22/2018
    */
    /**/
    public ArrayList<Tag> GetAllTags(String a_path){
        ImageObject imageObject = GetImageObjectIfExists(a_path);

        if(imageObject != null){
            return imageObject.GetTags();
        }
        return new ArrayList<>();
    }
    /* ArrayList<Tag> GetAllTags(String a_path) */


    /**/
    /*
    GetTopTags

    NAME
        GetTopTags()    - Returns the most popular tags.

    SYNOPSIS
        ArrayList<Tag> GetTopTags(int a_maxTags)
            a_maxTags      --> Maximum number of Tag objects to be returned.

    DESCRIPTION
        This function will return a given amount of tags who point to the most images. This is
        useful for getting the most popular Tag in the data set, or for sorting images based
        on the most popular tags. Since tags are sorted in popular order, that is first Tag in
        data set has the most associated images.

    RETURNS
        ArrayList<Tag>      - List containing a given number of Tag objects.

    AUTHOR
        Travis Whipple

    DATE
        4/22/2018
    */
    /**/
    public ArrayList<Tag> GetTopTags(int a_maxTags){

        ArrayList<Tag> foundTags = new ArrayList<>();

        // m_tags are sorted by popularity, first having most associated images.
        for(Tag tag : m_tags){

            if(foundTags.size() < a_maxTags){
                foundTags.add(tag);
            }else{
                // Have found
                break;
            }
        }

        return foundTags;
    }
    /* ArrayList<Tag> GetTopTags(int a_maxTags) */

    /**/
    /*
    AddFace

    NAME
        AddFace()   - Adds a FaceObject to the data set.

    SYNOPSIS
        void AddFace(FaceObject a_face)
            a_face      --> FaceObject to be added to data set.

    DESCRIPTION
        This function will add a FaceObject to the data set only if the data set does not already
        contain the given FaceObject. This will also give the FaceObject a unique ID which is
        created by indexing an integer by one more past the largest known existing ID of any
        other FaceObject.

    AUTHOR
        Travis Whipple

    DATE
        4/22/2018
    */
    /**/
    public void AddFace(FaceObject a_face){

        // Check if data set contains given face.
        if(!m_faces.contains(a_face)){

            String imagePath = a_face.GetImagePath();

            // Make sure image object exists. If not image has been removed, so do nothing.
            if(GetImageObjectIfExists(imagePath) != null){
                m_faces.add(a_face);
                GetImageObject(imagePath).AddFace(a_face);

                if(m_nextFaceId <= a_face.GetId()){
                    m_nextFaceId = a_face.GetId() + 1;
                }
            }
        }
    }
    /* void AddFace(FaceObject a_face) */

    /**/
    /*
    GetFace

    NAME
        GetFace()   - Gets a FaceObject from a given face's ID.

    SYNOPSIS
        FaceObject GetFace(int a_faceId)
            a_faceId        --> ID of face to be found.

    DESCRIPTION
        This function will search for a FaceObject in the data set who's unique ID matches the
        given ID.

    RETURNS
        FaceObject      - FaceObject found in data set with matching ID.
                        - Null object if no face was found with given ID.

    AUTHOR
        Travis Whipple

    DATE
        4/22/2018
    */
    /**/
    public FaceObject GetFace(int a_faceId){

        for(FaceObject face : m_faces){
            if(face.GetId() == a_faceId){
                return face;
            }
        }

        return null;
    }
    /* FaceObject GetFace(int a_faceId) */

    /**/
    /*
    GetAllFaces

    NAME
        GetAllFaces()   - Returns all FaceObjects currently existing in data set.

    DESCRIPTION
        This function will return a list of all FaceObject's existing in data set.

    RETURNS
        ArrayList<FaceObject>   - List containing all FaceObjects in data set.
                                - May contain no elements if no FaceObjects exist in set.

    AUTHOR
        Travis Whipple

    DATE
        4/22/2018
    */
    /**/
    public ArrayList<FaceObject> GetAllFaces(){
        return m_faces;
    }
    /* ArrayList<FaceObject> GetAllFaces() */

    /**/
    /*
    AddPerson

    NAME
        AddPerson()     - Adds a person to the data set.

    SYNOPSIS
        void AddPerson(Person a_person)
            a_person        --> Person object to be added to data set.

    DESCRIPTION
        This function will add a Person object to the data set only if the Person does not exist
        in data set already. When adding a person to the data set, all ImageObject's containing
        the person will be modified to now point to that person as well.

    AUTHOR
        Travis Whipple

    DATE
        4/22/2018
    */
    /**/
    public void AddPerson(Person a_person){

        if(!m_people.contains(a_person)){

            // Add person to all ImageObjects containing their face.
            for(String imagePath : a_person.GetAllImagePathsContainingPerson()){
                ImageObject imageObject = GetImageObjectIfExists(imagePath);

                if(imageObject != null){
                    imageObject.AddPerson(a_person);
                }
            }

            m_people.add(a_person);

            // ID of next person created will be one more than largest existing Person ID.
            if(m_nextPersonId <= a_person.GetId()){
                m_nextPersonId = a_person.GetId() +1;
            }
        }
    }
    /* void AddPerson(Person a_person) */


    /**/
    /*
    RemovePerson

    NAME
        RemovePerson()  - Removes a person from the data set.

    SYNOPSIS
        void RemovePerson(Person a_person)

            a_person        --> Person to be removed from data set.

    DESCRIPTION
        This function will remove the given person from the data set. All ImageObject's containing
        the given person will have their reference to the person removed. Relations If the person
        does not already exist in the data set, then nothing will be removed or altered as they
        are not in the data set.

    AUTHOR
        Travis Whipple

    DATE
        4/22/2018
    */
    /**/
    public void RemovePerson(Person a_person){


        if(m_people.contains(a_person)){
            m_people.remove(a_person);

            // Remove person from all ImageObjects the person is in.
            for(String imagePath : a_person.GetAllImagePathsContainingPerson()){
                ImageObject currentImage = GetImageObjectIfExists(imagePath);
                if(currentImage != null){
                    currentImage.RemovePerson(a_person);
                }
            }
        }
    }
    /* void RemovePerson(Person a_person) */

    /**/
    /*
    GetNewPerson

    NAME
        GetNewPerson()  - Will create a new person with a unique id.

    SYNOPSIS
        Person GetNewPerson(String a_personName)

            a_personName        --> Name of the person to be created.

    DESCRIPTION
        This function will create a new Person object from the given person's name. A unique ID
        will be given to the person. The unique ID is indexed one past the largest unique Person
        ID.

    RETURNS
        Person      - Person object created with the given name.

    AUTHOR
        Travis Whipple

    DATE
        4/22/2018
    */
    /**/
    public Person GetNewPerson(String a_personName){
        return new Person(m_nextFaceId++, a_personName);
    }
    /* Person GetNewPerson(String a_personName) */

    /**/
    /*
    GetPeople

    NAME
        GetPeople()     - Will return all People objects in data set.

    DESCRIPTION
        This function will return an ArrayList containing all People objects in data set.

    RETURNS
        ArrayList<Person>   - List of all Person objects in data set,
                            may contain no elements.

    AUTHOR
        Travis Whipple

    DATE
        4/22/2018
    */
    /**/
    public ArrayList<Person> GetPeople(){
        return m_people;
    }
    /* ArrayList<Person> GetPeople() */

    /**/
    /*
    GetPeopleInImage

    NAME
        GetPeopleInImage()  - Gets all Person objects in a given image.

    SYNOPSIS
        ArrayList<Person> GetPeopleInImage(String a_imagePath)
            a_imagePath     --> Absolute path of image.

    DESCRIPTION
        This function will return a list of people found in a given image. If there are no
        people in the image or if image does not exist in set, an empty list will be returned.

    RETURNS
        ArrayList<Person>   - All Person objects within the given image,
                            May contain no elements.

    AUTHOR
        Travis Whipple

    DATE
        4/22/2018
    */
    /**/
    public ArrayList<Person> GetPeopleInImage(String a_imagePath){

        ImageObject image = GetImageObjectIfExists(a_imagePath);
        ArrayList<Person> foundPeople = new ArrayList<>();

        if(image != null){

            if(m_images.contains(image)){
                foundPeople.addAll(image.GetAllPeople());
            }
        }

        return foundPeople;
    }
    /* ArrayList<Person> GetPeopleInImage(String a_imagePath) */

    /**/
    /*
    SortImages

    NAME
        SortImages()    - Sorts images data set with first image being the most recent.

    DESCRIPTION
        This function will sort images based on the day they were taken with the first image being
        the most recent and the last being the oldest.

    AUTHOR
        Travis Whipple

    DATE
        4/22/2018
    */
    /**/
    private void SortImages(){
        // Images will be sorted with first image being newest.
        Collections.sort(m_images);
        Collections.reverse(m_images);
    }
    /* void SortImages() */

    /**/
    /*
    SortTags

    NAME
        SortTags()  - Sorts tags data set with first tag having the most associated images.

    DESCRIPTION
        This function will sort all Tag objects in data set based on how many images each Tag
        is associated with. The first Tag object will have the most associated images, while the
        last Tag object will have the least associated images.

    AUTHOR
        Travis Whipple

    DATE
        4/22/2018
    */
    /**/
    private void SortTags(){
        // Tags will be sorted so first tag has the most associated m_images.
        Collections.sort(m_tags);
        Collections.reverse(m_tags);
    }
    /* void SortTags() */

    /**/
    /*
    GetAllSearchResults

    NAME
        GetAllSearchResults()   - Gets all image file paths matching a search string.

    SYNOPSIS
        ArrayList<String> GetAllSearchResults(String a_searchString)
            a_searchString      --> Search criteria.

    DESCRIPTION
        This function returns a list of all ImageObject's image file paths that relate to the
        search string. The search string can be a persons name or an existing tag. Tags can be
        matched partly while people's name have to be matched from the beginning. This is to
        prevent someone from search for "Race" and images of "Grace" coming up, But will allow a
        search of "Cat" to pull up images of "Catherine" as people commonly use abbreviations in
        names. For that same search "Race" images of "Racecar", "Race track" and "Foot race" will
        show up giving a larger hit result, helping the user find an image. All searches are case
        insensitive.

    RETURNS
        ArrayList<String>       - List of all image's file path that match the search criteria.
                                - May contain no elements if no elements match search criteria.

    AUTHOR
        Travis Whipple

    DATE
        4/22/2018
    */
    /**/
    public ArrayList<String> GetAllSearchResults(String a_searchString){

        ArrayList<String> allPathsResult = new ArrayList<>();

        // Add all photos with tags that contain search string.
        for(Tag tag : m_tags){
            if(tag.GetTagName().toLowerCase().contains(a_searchString.toLowerCase())){
                for(ImageObject image : tag.GetImageList()){
                    allPathsResult.add(image.GetPath());
                }
                break;
            }
        }

        // Add all people whose name matches the search string.
        for(Person person : m_people){
            if(person.GetName().toLowerCase().equals(a_searchString.toLowerCase())){
                allPathsResult.addAll(person.GetAllImagePathsContainingPerson());
                break;
            }
        }

        return allPathsResult;
    }
    /* ArrayList<String> GetAllSearchResults(String a_searchString) */

    /**/
    /*
    GetSimilarImages

    NAME
        GetSimilarImages()  - Gets all images that are similar to a given image's path.

    SYNOPSIS
        ArrayList<ImageObject> GetSimilarImages(String a_imagePath)
            a_imagePath     --> Path of image's file.

    DESCRIPTION
        This function will get all images that are similar to a given image. First the associated
        ImageObject for the given path is found. From there the following algorithm is applied:

        Precursor:
            1. Tags for images are sorted in order from most accurate, to least accurate.
            2. Confidence values range from .0 - 1, this represents a percent of how accurate
                a tag defines a photo.

        Algorithm:
        1. Create a set (S) containing 40% of the highest confidence Tags for this image.
        2. Get the lowest confidence (LC) from set S that is above a Lower Limit of .6
        3. For all tags in this image with a confidence higher than the LC value.
            4. For all images within that tag, only save images whose confidence to the current
                tag is greater than .9
        5. Ensure that the original image is not in the returned set.


    RETURNS
        ArrayList<ImageObject>  - List of similar images found with respect to the given image.
                                - List may contain no elements if no similar images were found.

    AUTHOR
        Travis Whipple

    DATE
        4/22/2018
    */
    /**/
    public ArrayList<ImageObject> GetSimilarImages(String a_imagePath){

        ImageObject imageBeingAnalyzed = GetImageObjectIfExists(a_imagePath);
        ArrayList<ImageObject> similarImages = new ArrayList<>();

        // Return empty list if image does not exist.
        if(imageBeingAnalyzed == null){
            return similarImages;
        }

        // Only get similar images whos tag has a confidence greater than 90%
        double similarImagesConfidenceLimit = .9d;

        // Only look at Tags of imageBeingAnalyzed whose confidence is greater than 60%
        final double LOWER_LIMIT = .60d;

        // Get top 40% of Tags for imageBeingAnalyzed.
        final double FACTOR = .40d;


        // Trying to set confidence limit by using the lowest confidence on the top 40% results.
        ArrayList<Tag> tags = imageBeingAnalyzed.GetTags();

        // Get top 40% of tags.
        int index = (int) Math.ceil(tags.size() * FACTOR);

        // Index of base 0 array.
        index -= 1;

        double outerConfidenceLimit = LOWER_LIMIT;
        for(int i = index; i >= 0; i--){
            double lowestOfTop40 = tags.get(i).GetImageConfidence(imageBeingAnalyzed);

            // Save the lowest confidence value of the top 40% of tags for this image.
            if(lowestOfTop40 > LOWER_LIMIT && lowestOfTop40 < outerConfidenceLimit){
                outerConfidenceLimit = lowestOfTop40;
                break;
            }
        }

        // Loop through all Tags of this image.
        for(Tag tag : imageBeingAnalyzed.GetTags()) {

            // OuterConfidenceLimit is at least above LOWER_LIMIT.
            // it is the lowest confidence value of the top 40% of tags.
            if (tag.GetImageConfidence(imageBeingAnalyzed) >= outerConfidenceLimit) {

                // Get all images associated with the current Tag.
                for (ImageObject io : tag.GetImageList()) {

                    // Image must have a high confidence.
                    if (tag.GetImageConfidence(io) >= similarImagesConfidenceLimit) {

                        // Only add image if it is not in similar images set.
                        if(!similarImages.contains(io)){
                            similarImages.add(io);
                        }
                    }
                }
            }
        }

        // Remove imageBeingAnalyzed from list so list does not contain image being analyzed.
        while(similarImages.contains(imageBeingAnalyzed)){
            similarImages.remove(imageBeingAnalyzed);
        }

        return similarImages;
    }
    /* ArrayList<ImageObject> GetSimilarImages(String a_imagePath) */

    /**/
    /*
    GetSameDayPhotos

    NAME
        GetSameDayPhotos()      - Gets photos taken on the same day as given photo.

    SYNOPSIS
        ArrayList<ImageObject> GetSameDayPhotos(String a_imagePath)
            a_imagePath     --> Absolute path of image file.

    DESCRIPTION
        This function will return all images that were taken on the same day as the given image.
        This function checks that the image exists to avoid any errors. The function first ensures
        that the set of images is sorted by date. Then it finds all images above and below the
        given image in the set that have the same date. The final collection returned is sorted.

    RETURNS
        ArrayList<ImageObject>      - All ImageObjects whose image was taken on the same day.
                                    - May contain no elements if image was not found, or if
                                    no other photos were taken on the same day.

    AUTHOR
        Travis Whipple

    DATE
        4/22/2018
    */
    /**/
    public ArrayList<ImageObject> GetSameDayPhotos(String a_imagePath){

        ImageObject imageObject = GetImageObjectIfExists(a_imagePath);
        ArrayList<ImageObject> photosFromThisDay = new ArrayList<>();

        // Return empty list if image does not exits.
        if(imageObject == null){
            return photosFromThisDay;
        }

        // Make sure photos are sorted.
        SortImages();


        int indexOfGivenImage = m_images.indexOf(imageObject);

        // Check for photos from same day above and below image in all m_images.

        // Check images below the given photo in m_images.
        int index = indexOfGivenImage - 1;
        while(index >= 0){

            // Current image we are evaluating.
            ImageObject currentImage = m_images.get(index);

            // Make sure that day, month and year are all the same.
            if((currentImage.GetDay() == imageObject.GetDay())
                    && (currentImage.GetMonth() == imageObject.GetMonth())
                    && (currentImage.GetYear() == imageObject.GetYear())) {

                photosFromThisDay.add(photosFromThisDay.size(), currentImage);
            }else{
                // Finish when dates are no longer the same.
                break;
            }

            index--;
        }

        // Check images above the given photo in m_images.
        index = indexOfGivenImage + 1;
        while(index < m_images.size()){

            // Current image we are evaluating.
            ImageObject currentImage = m_images.get(index);

            // Make sure that day, month and year are all the same.
            if((currentImage.GetDay() == imageObject.GetDay())
                    && (currentImage.GetMonth() == imageObject.GetMonth())
                    && (currentImage.GetYear() == imageObject.GetYear())) {

                photosFromThisDay.add(currentImage);
            }else{
                // Finish when dates are no longer the same.
                break;
            }

            index++;
        }

        Collections.sort(photosFromThisDay);
        return photosFromThisDay;
    }
    /* ArrayList<ImageObject> GetSameDayPhotos(String a_imagePath) */

    /**/
    /*
    AnalyzedImage

    NAME
        AnalyzedImage()     - Sets an image path's respected ImageObject to having been analyzed.

    SYNOPSIS
        void AnalyzedImage(String a_imagePath)
            a_imagePath     --> Absolute path of ImageObject's image file.

    DESCRIPTION
        This function will set an ImageObject's flag to indicate that the image has already
        been analyzed. This is to prevent an image from being analyzed twice.

    AUTHOR
        Travis Whipple

    DATE
        4/22/2018
    */
    /**/
    public void AnalyzedImage(String a_imagePath){
        ImageObject imageObject = GetImageObjectIfExists(a_imagePath);
        if(imageObject != null){
            imageObject.SetHasBeenAnalyzed(true);
        }
    }
    /* void AnalyzedImage(String a_imagePath) */

    /**/
    /*
    AnalyzeForFaces

    NAME
        AnalyzeForFaces()   - Analyzes a photo for faces.

    SYNOPSIS
        void AnalyzeForFaces(String a_imagePath)
            a_imagePath     --> Absolute path of image file to be analyzed for faces.

    DESCRIPTION
        This function will analyze an image for faces. It will create a thread to analyze for
        faces in the background. If the image trying to be analyzed for faces has already been
        analyzed then a message will appear to the user to inform them the image has already
        been analyzed. All faces found will be added to the faces set within this Class.

    AUTHOR
        Travis Whipple

    DATE
        4/22/2018
    */
    /**/
    public void AnalyzeForFaces(String a_imagePath){

        ImageObject imageObjectToBeAnalyzed = GetImageObjectIfExists(a_imagePath);

        if(imageObjectToBeAnalyzed != null){
            if(imageObjectToBeAnalyzed.GetHasDetectedFaces()){
                Toast.makeText(m_Context, "Faces have already been found!", Toast.LENGTH_LONG).show();
                return;
            }

            // Create ArrayList for thread call.
            ArrayList<ImageObject> imagesArr = new ArrayList<>();
            imagesArr.add(imageObjectToBeAnalyzed);

            // Create and start thread.
            FaceDetectionThread thread = new FaceDetectionThread(imagesArr);
            thread.start();
        }else{
            Log.e("ImageManager.AnalyzeForFaces(a_imagePath)", "Image is not in set!");
        }
    }
    /* void AnalyzeForFaces(String a_imagePath) */

    /**/
    /*
    NAME
        AnalyzeForFaces()   - Analyzes for faces in all photos.

    DESCRIPTION
        This function will analyze all images currently in the set on its own thread. All faces
        found will be added to the faces set within the thread.

    AUTHOR
        Travis Whipple

    DATE
        4/22/2018
    */
    /**/
    public void AnalyzeForFaces(){

        FaceDetectionThread thread = new FaceDetectionThread(m_images);
        thread.start();
    }
    /* void AnalyzeForFaces() */


    /**/
    /*

    CLASS NAME
        FaceDetectionThread - Analyzes for faces in given images.

    DESCRIPTION
        This sub class will analyze all images passed to it by using the FacialDetector. This
        class will only analyze images that have not previously been analyzed. When a face is
        found this class will set the ID of the FaceObject with a unique ID so that it is
        distinguishable from the other FaceObjects. All images analyzed will have their proper
        flags set to reflect that the image has been analyzed for faces.

    DATE
        7/12/2018
    */
    /**/
    class FaceDetectionThread extends Thread {

        private ArrayList<ImageObject> m_Images;

        FaceDetectionThread(ArrayList<ImageObject> images) {
            m_Images = images;
        }

        @Override
        public void run() {
            // Put thread in background.
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            FacialDetection detector = new FacialDetection(m_Context);

            // Loop through all images in set.
            for(ImageObject image : m_Images){

                // Only detect m_faces for photos that have not been detected already.
                if(!image.GetHasDetectedFaces()){

                    // Detect faces for current image and collect found faces.
                    ArrayList<FaceObject> faceObjects = detector.DetectFaces(image.GetPath());

                    // Set that this image has been detected for faces.
                    image.SetHasDetectedFaces(true);

                    // Set ID for all faces found with unique id.
                    for(FaceObject face : faceObjects){
                        face.SetId(m_nextFaceId++);
                        AddFace(face);
                    }
                }
            }
        }
    }
}
