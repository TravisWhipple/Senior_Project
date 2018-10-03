package com.example.traviswhipple.seniorproject;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;


/**/
/*

CLASS NAME
    ImageObject - Manages all relations between images, tags, people and faces.

DESCRIPTION
    This class contains everything about each image on the device. An ImageObject contains
    all faces and people within the image as well as all tags that define the context of the
    image. ImageObject implements Comparable<> so that we can compare ImageObject's based on
    the date they were taken rather than comparing the Object as a whole.

DATE
    3/12/2018
*/
/**/

public class ImageObject implements Comparable<ImageObject>  {

    // Flags about if an image has been analyzed for certain criteria.
    private boolean m_hasBeenAnalyzed;
    private boolean m_hasDetectedFaces;

    // Date data
    private Long m_rawDateData;
    private int m_Month;
    private int m_Day;
    private int m_Year;

    // Path of image.
    private String m_Path;

    // Sets containing relations to different objects.
    private ArrayList<Tag> m_tags;
    private ArrayList<FaceObject> m_faces;
    private ArrayList<Person> m_people;

    /**/
    /*

    NAME
        ImageObject()   - Constructors

    SYNOPSIS
        ImageObject(String a_path)
            a_path          --> Absolute path of image file.

        ImageObject(String a_path, Long a_dateTaken)
            a_path          --> Absolute path of image file.
            a_dateTaken     --> Date taken in milliseconds.

    DESCRIPTION
        Constructors initialize all member data and require an image's absolute file path.
        Two constructors are used for the lack of Java's ability to include default parameter
        values.

    AUTHOR
        Travis Whipple

    DATE
        3/12/2018
    */
    /**/
    public ImageObject(String a_path){
        Initialize();
        m_Path = a_path;
    }

    public ImageObject(String a_path, Long a_dateTaken){
        Initialize();
        m_Path = a_path;
        SetDateTaken(a_dateTaken);
    }

    /**/
    /*
    void Initialize()

    NAME
        Initialize()    - Initializes all member variables to default values.

    DESCRIPTION
        This function initializes all sets and member variables. All variables are set to be empty.

    AUTHOR
        Travis Whipple

    DATE
        3/12/2018
    */
    /**/
    private void Initialize(){
        // Initialize analyzed data.
        m_hasBeenAnalyzed = false;
        m_hasDetectedFaces = false;

        // Initialize data sets.
        m_tags = new ArrayList<>();
        m_faces = new ArrayList<>();
        m_people = new ArrayList<>();

        // Initialize date to 0.
        m_Path = "";
        m_rawDateData = 0l;

        // Initialize date to 0.
        m_Month = 0;
        m_Day = 0;
        m_Year = 0;
    }
    /* void Initialize() */

    /**/
    /*
    void SetDateTaken(Long a_date)

    NAME
        SetDateTaken()  - Sets date member variables from a Long date.

    SYNOPSIS
        void SetDateTaken(Long a_date)
            a_date      --> Time in milliseconds when the image was taken.

    DESCRIPTION
        This function will parse the Long date given and set the ImageObjects day, month and
        year to reflect this date. Errors will be logged if date can not be parsed.

    AUTHOR
        Travis Whipple

    DATE
        3/12/2018
    */
    /**/
    private void SetDateTaken(Long a_date){

        // Set member raw date.
        m_rawDateData = a_date;


        // Create a formatted date string that we can parse.
        Date dateTaken  = new Date(m_rawDateData);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyyyy", Locale.US);

        StringBuilder dateStringBuilder = new StringBuilder(dateFormat.format(dateTaken));
        String unformattedDate = dateStringBuilder.toString();

        // Since we know date is in mmddyyyy format, we can pull out each digit from date.
        if(unformattedDate.length() == 8){

            String month = "";
            String day = "";
            String year = "";

            // Get two digit month.
            month += unformattedDate.charAt(0);
            month += unformattedDate.charAt(1);

            // Get two digit day.
            day += unformattedDate.charAt(2);
            day += unformattedDate.charAt(3);

            // Get four digit year.
            year += unformattedDate.charAt(4);
            year += unformattedDate.charAt(5);
            year += unformattedDate.charAt(6);
            year += unformattedDate.charAt(7);

            // Convert string into int.
            m_Month = Integer.parseInt(month);
            m_Day = Integer.parseInt(day);
            m_Year = Integer.parseInt(year);
        }else{
            // Log error message as date was not formatted correctly.
            Log.e("ImageObject.SetDateTaken", "Error parcing Long a_date parameter");
        }
    }
    /* void SetDateTaken(Long a_date) */

    /**/
    /*
    Setters and Getters for if image has been analyzed for a certain criteria.

    NAME
        Setters for if image has been analyzed / detected faces.
        Getters for if image has been analyzed / detected faces.

    SYNOPSIS
        void SetHasBeen...(Boolean a_has...)
            a_has...      --> If image was analyzed for tags / detected faces.

    DESCRIPTION
        These basic setters save a given boolean value to the corresponding
        member flag. These basic getters simply return their value.

    RETURNS
        boolean     - True if image has been analyzed for criteria.
                    - False if image has not been analyzed for criteria.

    AUTHOR
        Travis Whipple

    DATE
        3/12/2018
    */
    /**/
    public void SetHasBeenAnalyzed(Boolean a_hasAnalyzed){
        m_hasBeenAnalyzed = a_hasAnalyzed;
    }

    public void SetHasDetectedFaces(Boolean a_hasDetected){
        m_hasDetectedFaces = a_hasDetected;
    }

    public boolean GetHasBeenAnalyzed(){
        return m_hasBeenAnalyzed;
    }

    public boolean GetHasDetectedFaces(){
        return m_hasDetectedFaces;
    }
    /**/

    /**/
    /*
    GetTags

    NAME
        GetTags()   - Gets all tags associated with this image.

    DESCRIPTION
        This function returns a list of all Tag objects that define what is in the image.

    RETURNS
        ArrayList<Tag>      - List of all Tag objects that correspond to this image.
                            - List may contain no elements if no tags have been created for
                            this image.
    AUTHOR
        Travis Whipple

    DATE
        3/12/2018
    */
    /**/
    public ArrayList<Tag> GetTags(){
        return m_tags;
    }
    /* ArrayList<Tag> GetTags() */

    /**/
    /*
    GetAllPeople

    NAME
        GetAllPeople

    DESCRIPTION
        This function returns a list of all people who are in this photo.

    RETURNS
        ArrayList<Person>       - List of Person objects; all people in this image.
                                - List may be null if no people are in this image.

    AUTHOR
        Travis Whipple

    DATE
        3/12/2018
    */
    /**/
    public ArrayList<Person> GetAllPeople(){
        return m_people;
    }
    /* ArrayList<Person> GetAllPeople() */

    /**/
    /*
    Getters for date attributes.

    NAME
        Getters for day / month / year - Returns corresponding date attribute.

    DESCRIPTION
        These simple getters are used to get different attributes about when a photo was taken.

    RETURNS
        int         - Day / month / year image was taken.
                    - 0 If date has not been set, or there was an error loading date.

    AUTHOR
        Travis Whipple

    DATE
        3/12/2018
    */
    /**/
    public int GetMonth(){
        return m_Month;
    }

    public int GetDay(){
        return m_Day;
    }

    public int GetYear(){
        return m_Year;
    }
    /* Getters for date attributes */

    /**/
    /*
    GetPath

    NAME
        GetPath()   - Gets the absolute path of the image file.

    DESCRIPTION
        This simple getter returns the absolute path of this ImageObject's image file path. This
        path is where this ImageObject's actual image is stored.

    RETURNS
        String      - Path of image file.
                    - Empty String if path was not defined.

    AUTHOR
        Travis Whipple

    DATE
        3/12/2018
    */
    /**/
    public String GetPath(){
        return m_Path;
    }
    /* String GetPath() */

    /**/
    /*
    GetDateString

    NAME
        GetDateString()     - Returns date taken in a string format: MM/DD/YYYY

    DESCRIPTION
        This function converts the images date into a string with the format: MM/DD/YYYY
        M meaning Month, D meaning Day, Y meaning Year. EX: 12/25/2018 - christmas day, 2018.

    RETURNS
        String      - Date in String format.
                    - Empty String if date could not be parsed.

    AUTHOR
        Travis Whipple

    DATE
        3/12/2018
    */
    /**/
    public String GetDateString(){

        // Construct dateString by appending day, month, year into following format: MM/DD/YYYY
        String dateString = "";
        dateString += Integer.toString(m_Month);
        dateString += "/";
        dateString += Integer.toString(m_Day);
        dateString += "/";
        dateString += Integer.toString(m_Year);

        return dateString;
    }
    /* String GetDateString() */

    /**/
    /*
    GetSerializedString

    NAME
        GetSerializedString()   - Returns a serialized string of this ImageObject.

    SYNOPSIS
        String GetSerializedString(String a_delimiter)
            a_delimiter     --> Delimiter to be used to separate data elements.

    DESCRIPTION
        This function will write all of its member data to a string separated by a given delimiter.
        Below is an example of the formatting used for an ImageObject that has been analyzed
        and has not detected faces with delimiter ':'
        /path/to/image/file.jpg:true:false:tag1:tag2:tag3:tag4  etc.

    RETURNS
        String      - String containing all member data separated by given delimiter.

    AUTHOR
        Travis Whipple

    DATE
        3/12/2018
    */
    /**/
    public String GetSerializedString(String a_delimiter){

        // Create string in format, with delimiter ":"
        // if image has been analyzed but not detected for faces yet Ex:
        // /path:true:false:tag1:tag2:tag3 ect.

        StringBuilder data = new StringBuilder();

        data.append(this.GetPath());

        data.append(a_delimiter);
        data.append(Boolean.toString(m_hasBeenAnalyzed));

        data.append(a_delimiter);
        data.append(Boolean.toString(m_hasDetectedFaces));

        // Add m_tags in format: ,(TAG)
        // This is so that each comment is separated by delimiter.
        for(Tag tag : m_tags){

            data.append(a_delimiter);
            data.append(tag.GetSerializedString(this));
        }

        return data.toString();
    }
    /* String GetSerializedString(String a_delimiter) */

    /**/
    /*
    AddTag

    NAME
        AddTag()        - Adds a Tag associated with this image.

    SYNOPSIS
        void AddTag(Tag a_tag)
            a_tag       --> Tag object to be added.

    DESCRIPTION
        This function will only add a reference to a Tag object if this image does not already
        contain a reference to the Tag.

    AUTHOR
        Travis Whipple

    DATE
        3/12/2018
    */
    /**/
    public void AddTag(Tag a_tag){

        if(!m_tags.contains(a_tag)){
            m_tags.add(a_tag);
        }
    }
    /* void AddTag(Tag a_tag) */

    /**/
    /*
    RemoveTag

    NAME
        RemoveTag()     - Removes a given Tag from this image.

    SYNOPSIS
        void RemoveTag(Tag a_tag)
            a_tag       --> Tag object to be removed.

    DESCRIPTION
        This function will remove an associated Tag from this objects list of associated tags. It
        will also remove its self from the Tags list of images that the Tag object has
        references to.

    AUTHOR
        Travis Whipple

    DATE
        3/12/2018
    */
    /**/
    public void RemoveTag(Tag a_tag){

        if(m_tags.contains(a_tag)){
            a_tag.RemoveImage(this);
            m_tags.remove(a_tag);
        }

        // Reset if image has been analyzed so user can add tag back that was removed.
        SetHasBeenAnalyzed(false);
    }
    /* void RemoveTag(Tag a_tag) */

    /**/
    /*
    AddFace

    NAME
        AddFace()   - Adds a face object to this image.

    SYNOPSIS
        void AddFace(FaceObject a_face)
            a_face      --> FaceObject found in this image.

    DESCRIPTION
        This function will add a FaceObject that was found within this image. This function will
        ensure that no duplicates will be added to the data set.

    AUTHOR
        Travis Whipple

    DATE
        3/12/2018
    */
    /**/
    public void AddFace(FaceObject a_face){
        if(!m_faces.contains(a_face)){
            m_faces.add(a_face);
        }
    }
    /* void AddFace(FaceObject a_face) */

    /**/
    /*
    AddPerson

    NAME
        AddPerson()     - Add a person who is in this image.

    SYNOPSIS
        void AddPerson(Person a_person)
            a_person        --> Person object to be added to this images data set.

    DESCRIPTION
        This function will add a Person object to the image's data set. It will ensure that no
        duplicates will be added.

    AUTHOR
        Travis Whipple

    DATE
        3/12/2018
    */
    /**/
    public void AddPerson(Person a_person){

        // Only Add person if it is not already in set.
        if(!m_people.contains(a_person)){
            m_people.add(a_person);
        }
    }
    /* void AddPerson(Person a_person) */

    /**/
    /*
    RemovePerson

    NAME
        RemovePerson()      - Removes a given person from the data set.

    SYNOPSIS
        void RemovePerson(Person a_personToRemove)
            a_personToRemove        --> Person to be removed from this image.

    DESCRIPTION
        This function will remove a person from the data set only if it exists. If person is
        not found in set, then this function will do nothing.

    AUTHOR
        Travis Whipple

    DATE
        3/12/2018
    */
    /**/
    public void RemovePerson(Person a_personToRemove){

        // Check that set contains the given person.
        if(m_people.contains(a_personToRemove)){
            m_people.remove(a_personToRemove);
        }
    }
    /* void RemovePerson(Person a_personToRemove) */

    /**/
    /*
    hashCode

    NAME
        hashCode()      - Overridden parent function.

    DESCRIPTION
        This function overrides the parent hasCode() function. We override this function to
        ensure that two ImageObjects can be compared for equality. We use the image's path
        for the hashCode because the image's path is unique to this ImageObject.

    RETURNS

    AUTHOR
        Travis Whipple

    DATE
        7/15/2018
    */
    /**/
    @Override
    public int hashCode() {
        return m_Path.hashCode();
    }

    /**/
    /*
    equals

    NAME
        equals()    - Returns if two ImageObject's are equal.

    SYNOPSIS
        boolean equals(Object obj)
            obj     --> Object to be compared to.

    DESCRIPTION
        This function will determine what instance the given object is of. Currently the function
        only allows for String and other ImageObjects to be compared to this ImageObject.
        ImageObjects are compared by their path as they are unique to every ImageObject.

    RETURNS

    AUTHOR
        Travis Whipple

    DATE
        7/15/2018
    */
    /**/
    @Override
    public boolean equals(Object obj) {

        // Check what type of Object obj is.

        if(obj instanceof ImageObject){
            // Compare both ImageObjects image path.
            ImageObject io = (ImageObject)obj;
            return this.m_Path.equals(io.m_Path);
        }

        if(obj instanceof String){
            // Compare this path to the String path given.
            return this.m_Path.equals(obj);
        }

        // Return false, obj is of a type that cannot be compared to an ImageObject.
        return false;
    }
    /* boolean equals(Object obj) */

    @Override
    public String toString(){

        String returnString = "";

        returnString += Integer.toString(m_tags.size());
        returnString += " Tags, ";
        returnString += GetDateString();
        returnString += ", ";
        returnString += Long.toString(m_rawDateData);
        return returnString;

    }

    @Override
    public int compareTo(ImageObject other) {
        return Comparators.DATE.compare(this, other);
    }

    /**/
    /*

    CLASS
        Comparators     - Allows for custom comparision of two ImageObjects.

    DESCRIPTION
        This class is used to allow two ImageObjects to be compared to each other. This class
        is mainly used to sort lists of ImageObjects. Two ImageObjects are compared by millisecond
        time they were taken, so having two ImageObjects return equal is very unlikely.

    RETURNS
        int     1   - If this ImageObject was taken after the other ImageObject.
                0   - If this ImageObject was taken at the same time as the other ImageObject
               -1   - if this ImageObject was taken before the other ImageObject.

    AUTHOR
        Travis Whipple

    DATE
        7/15/2018
    */
    /**/
    public static class Comparators {

        public static Comparator<ImageObject> DATE = new Comparator<ImageObject>(){
            @Override
            public int compare(ImageObject image1, ImageObject image2){
                return image1.m_rawDateData.compareTo(image2.m_rawDateData);
            }
        };
    }
    /* static class Comparators */
}
/* class ImageObject implements Comparable<ImageObject> */