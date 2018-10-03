package com.example.traviswhipple.seniorproject;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by traviswhipple on 7/20/18.
 */

/**/
/*

CLASS
    Tag - Holds all member data for a tag for an image.

DESCRIPTION
    This class defines a Tag and all of its getters, setters. A Tag describes what is in an image
    with an associated m_confidence value. A Tag can have a reference to many ImageObjects as the
    Tag "Sky" could point to half of users images if they take many photos outside. The order
    of a Tags images are the order they were added in. This class can retrieve ImageObjects
    associated with this Tag, get ImageObjects who's m_confidence is above a certain threshold.
    This class also implements Comparable<Tag> so that Tag objects them selves may be organized.

DATE
    6/19/2018
*/
/**/

public class Tag implements Comparable<Tag> {

    private String m_tagName;
    private ArrayList<ImageObject> m_imageList;
    private ArrayList<Double> m_confidence;
    private Integer m_size;

    /**/
    /*
    Tag

    NAME
        Tag - Constructor.

    SYNOPSIS
        Tag(String a_tagName)
            a_tagName       --> Name of this Tag.

    DESCRIPTION
        Constructor calls initializer then saves given tag name.

    AUTHOR
        Travis Whipple

    */
    /**/
    public Tag(String a_tagName){
        Initializer();
        m_tagName = a_tagName;
    }
    /* Tag(String a_tagName) */

    /**/
    /*
    Initializer

    NAME
        Initializer()   - Initializes member variables.

    AUTHOR
        Travis Whipple

    */
    /**/
    private void Initializer(){
        m_tagName = "";
        m_imageList = new ArrayList<>();
        m_confidence = new ArrayList<>();
        m_size = m_imageList.size();
    }
    /* void Initializer() */

    /**/
    /*

    NAME
        GetTagName()    - Simple getter returns this Tag's name.

    AUTHOR
        Travis Whipple

    */
    /**/
    public String GetTagName(){
        return m_tagName;
    }
    /**/

    /**/
    /*
    AddImage

    NAME
        AddImage()  - Adds an image to this tag, along with the given confidence.

    SYNOPSIS
        void AddImage(ImageObject a_image, double a_confidence)
            a_image         --> Image to be added.
            a_confidence    --> How accurately this Tag defines the iamge.

    AUTHOR
        Travis Whipple

    */
    /**/
    public void AddImage(ImageObject a_image, double a_confidence){

        // Check that image is not in list.
        if(!m_imageList.contains(a_image)){

            // Image and confidence.
            m_confidence.add(a_confidence);
            m_imageList.add(a_image);
            m_size = m_imageList.size();
        }
    }
    /* void AddImage(ImageObject a_image, double a_confidence) */

    /**/
    /*
    RemoveImage

    NAME
        RemoveImage()   - Removes an image from this Tag.

    SYNOPSIS
        void RemoveImage(ImageObject a_image)
            a_image     --> Image to remove.

    DESCRIPTION
        This function will remove an ImageObject from this Tag as well as that ImageObject's
        associated confidence value.

    RETURNS

    AUTHOR
        Travis Whipple

    */
    /**/
    public void RemoveImage(ImageObject a_image){

        // Check if image exists in list.
        if(m_imageList.contains(a_image)){

            // Remove image and its corresponding confidence.
            int inxex = m_imageList.indexOf(a_image);
            m_imageList.remove(a_image);
            m_confidence.remove(m_confidence.get(inxex));
            m_size = m_imageList.size();
        }
    }
    /* void RemoveImage(ImageObject a_image) */

    /**/
    /*
    GetImageListAboveConfidence

    NAME
        GetImageListAboveConfidence()   - Gets all images whose confidence is above
                                        confidence limit.

    SYNOPSIS
        ArrayList<ImageObject> GetImageListAboveConfidence(double a_confidenceLowerLimit)
            a_confidenceLowerLimit       --> Lowest confidence value accepted.

    DESCRIPTION
        This function will return all ImageObjects whose confidence is above the given
        limit.

    RETURNS
        ArrayList<ImageObject>      - All ImageObject with confidence higher than limit, or
                                    - Empty list if no ImageObject wer found.

    AUTHOR
        Travis Whipple

    */
    /**/
    public ArrayList<ImageObject> GetImageListAboveConfidence(double a_confidenceLowerLimit){

        ArrayList<ImageObject> returnList = new ArrayList<>();

        // Loop through all elements in data set.
        for(int i = 0; i < m_imageList.size(); i++){
            if(m_confidence.get(i) >= a_confidenceLowerLimit){
                returnList.add(m_imageList.get(i));
            }
        }

        return returnList;
    }
    /* ArrayList<ImageObject> GetImageListAboveConfidence(double a_confidenceLimit) */

    /**/
    /*

    NAME
        GetImageList()  - Returns all ImageObjects in set.

    RETURNS
        ArrayList<ImageObject>      - All ImageObjects, or
                                    - Empty list if no ImageObject exist.

    AUTHOR
        Travis Whipple

    */
    /**/
    public ArrayList<ImageObject> GetImageList(){
        return m_imageList;
    }
    /* ArrayList<ImageObject> GetImageList() */

    /**/
    /*
    GetImageConfidence

    NAME
        GetImageConfidence()    - Gets confidence for given image.

    SYNOPSIS
        double GetImageConfidence(ImageObject a_image)
            a_image     --> Image to get confidence for.

    DESCRIPTION
        This function will return the confidence for a given image. It will check that the
        image is within the data set and return the images correlated confidence.

    RETURNS
        double      - Confidence value for the image.
                    - 0 if image could not be found.

    AUTHOR
        Travis Whipple

    */
    /**/
    public double GetImageConfidence(ImageObject a_image){
        int index = m_imageList.indexOf(a_image);
        if(index >= 0){
            return m_confidence.get(index);
        }else{
            return 0;
        }
    }
    /* double GetImageConfidence(ImageObject a_image) */

    /**/
    /*
    GetSerializedString

    NAME
        GetSerializedString()   - Returns this object in serialized format.

    SYNOPSIS
        String GetSerializedString(ImageObject a_image)
            a_image     --> Serialize string with respect to the given image.

    DESCRIPTION
        This function will ensure that the given image exists within the data set. Then it will
        return a String containing this Tag's name, along with the images correlated confidence
        value. The value will be inside of parentheses.

    RETURNS
        String      - String containing serialized string, or
                    - Empty string.

    AUTHOR
        Travis Whipple

    */
    /**/
    public String GetSerializedString(ImageObject a_image){

        String data = "";
        data += m_tagName;

        data += "(";
        data += Double.toString(GetImageConfidence(a_image));
        data += ")";

        return data;
    }
    /* String GetSerializedString(ImageObject a_image) */

    /**/
    /*

    DESCRIPTION
        Override Parent's hasCode so that Tags can be sorted. Use this Tag's name to create
        a hash code.

    */
    /**/
    @Override
    public int hashCode(){
        return m_tagName.hashCode();
    }

    /**/
    /*
    DESCRIPTION
        Override equals function so that we can compare two Tag object to each other. Or compare
        This Tag object to a String.

    RETURNS
        True if equal.
        False if not.

    AUTHOR
        Travis Whipple

    */
    /**/
    @Override
    public boolean equals(Object a_obj) {

        // Check if obj is a Tag object.
        if(a_obj instanceof Tag){
            // Compare Tags by their name.
            Tag tagOther = (Tag)a_obj;
            return this.m_tagName.equals(tagOther.m_tagName);
        }

        // Check if obj is a String object.
        if(a_obj instanceof String){
            // Compare this Tag's name with the given String.
            return this.m_tagName.equals(a_obj);
        }

        return false;
    }

    /**/
    /*

    DESCRIPTION
        Override compareTo so that Tags can be sorted within a collection. Tags will be sorted
        based on how many ImageObjects they have references to.

    */
    /**/
    @Override
    public int compareTo(Tag a_other) {

        if(a_other == null){
            return 1;
        }

        return Tag.Comparators.SIZE.compare(this, a_other);
    }

    /**/
    /*

    NAME
        static class Comparators    - Allows the ability to sort a collection of Tag objects.

    SYNOPSIS

    DESCRIPTION
        This class is used to sort Tag objects. They will by sorted by how many ImageObjects
        they contain. The first Tag in the collection will have the most images associated with
        it while the last Tag will have the least amount of images associated with it.

    AUTHOR
        Travis Whipple

    */
    /**/
    public static class Comparators {

        public static Comparator<Tag> SIZE = new Comparator<Tag>(){
            @Override
            public int compare(Tag tag1, Tag tag2){
                return tag1.m_size.compareTo(tag2.m_size);
            }
        };
    }
}
