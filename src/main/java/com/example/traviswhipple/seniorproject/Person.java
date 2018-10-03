package com.example.traviswhipple.seniorproject;

import java.util.ArrayList;

/**/
/*

CLASS NAME
    Person - A person holds a list of faces, as well as a unique ID and name.

DESCRIPTION
    This class is fairly simple as it implements a Person object. A Person has a name and a unique
    ID. After a Person is created FaceObjects can be added to the person to identify them.
    FaceObjects given to a Person at this time have to be entered by the user as their is no
    automatic Facial Recognition built into the project at this time. This class has functionality
    to get and set their name and unique ID, as well as retrieve all FaceObjects this Person
    is associated with. Functionality also includes a method to get every image that this person
    was found in.

DATE
    7/19/2018
*/
/**/

public class Person {

    // List of faces that identify this Person.
    private ArrayList<FaceObject> m_PersonsFace;

    // Unique name and ID.
    private String m_Name;
    private int m_Id;

    /**/
    /*
    Person

    NAME
        Person()    - Constructor.

    SYNOPSIS
        Person(int a_id, String a_name)
            a_id        --> ID created for this person.
            a_name      --> Name for this person.

    DESCRIPTION
        This function creates a new Person object. It is important that a unique ID be given as
        the ID is used to to distinguish different People objects from one another. The creation
        of this ID is up to the ImageManager as it keeps a record of all People created.

    AUTHOR
        Travis Whipple

    */
    /**/
    public Person(int a_id, String a_name){
        m_Id = a_id;
        m_Name = a_name;
        m_PersonsFace = new ArrayList<>();
    }
    /* Person(int a_id, String a_name) */

    /**/
    /*
    AddFace

    NAME
        AddFace()   - Add a face to this Person.

    SYNOPSIS
        void AddFace(FaceObject a_face)
            a_face      --> Face containing this Person.

    DESCRIPTION
        This function will add a face to this Person's data set. Each face added to this Person
        are only added if the data set does not already contain the given face.

    AUTHOR
        Travis Whipple

    */
    /**/
    public void AddFace(FaceObject a_face){

        // Add face if it does not exist in list.
        if(!m_PersonsFace.contains(a_face)){
            m_PersonsFace.add(a_face);
        }
    }
    /* void AddFace(FaceObject a_face) */

    /**/
    /*
    RemoveFace

    NAME
        RemoveFace()    - Removes a face from this Person.

    SYNOPSIS
        void RemoveFace(FaceObject a_face)
            a_face      --> Face to be removed.

    DESCRIPTION
        This function will remove a given face from this Person. This function will check that
        the face being removed exists in the data set. Faces can be removed and added back
        at no cost.

    AUTHOR
        Travis Whipple

    */
    /**/
    public void RemoveFace(FaceObject a_face){

        // Only remove given face if it is in data set.
        if(m_PersonsFace.contains(a_face)){
            m_PersonsFace.remove(a_face);
        }
    }
    /* void RemoveFace(FaceObject a_face) */

    /**/
    /*
    GetFaces

    NAME
        GetFaces()  - Get every face this Person has been assigned to.

    DESCRIPTION
        This function will return all FaceObject's associated with this Person. A person may have
        many or few faces, but a person should always have at least one face.

    RETURNS
        ArrayList<FaceObject>       - List of FaceObjects that define this Person.
                                    - May contain no elements, but will never return null.

    AUTHOR
        Travis Whipple

    */
    /**/
    public ArrayList<FaceObject> GetFaces(){
        return m_PersonsFace;
    }
    /* ArrayList<FaceObject> GetFaces() */

    /**/
    /*
    GetName

    NAME
        GetName()   - Gets the name for this Person.

    DESCRIPTION
        This simple getter function returns the name for this Person. Once a Person is created
        their name cannot be altered.

    RETURNS
        String      - Persons name, will always be set.

    AUTHOR
        Travis Whipple

    */
    /**/
    public String GetName(){
        return m_Name;
    }
    /* String GetName() */

    /**/
    /*
    GetId

    NAME
        GetId() - Returns this Persons unique ID.

    DESCRIPTION
        This simple getter function returns this Persons unique ID. The unique ID is used to create
        new unique ID's for future People objects, and for serialization to distinguish it from
        other Person objects.

    RETURNS
        int     - Integer value of Person's ID. This will always be set.

    AUTHOR
        Travis Whipple

    */
    /**/
    public int GetId(){
        return m_Id;
    }
    /* int GetId() */

    /**/
    /*
    GetAllImagePathsContainingPerson

    NAME
        GetAllImagePathsContainingPerson()  - Returns list of all image file paths containing this
                                            Person object.

    DESCRIPTION
        This function will return a list containing every image the Person is in. It does this
        by getting each face in its data set, and returning the original image the face was
        found in.

    RETURNS

    AUTHOR
        Travis Whipple

    */
    /**/
    public ArrayList<String> GetAllImagePathsContainingPerson(){
        ArrayList<String> allImagePaths = new ArrayList<>();

        for(FaceObject face : m_PersonsFace){
            // Add image's path to allImagePaths.
            allImagePaths.add(face.GetImagePath());
        }

        return allImagePaths;
    }
    /* ArrayList<String> GetAllImagePathsContainingPerson() */

    /**/
    /*
    GetSerializedString

    NAME
        GetSerializedString()   - Returns this object in a serialized format.

    SYNOPSIS
        String GetSerializedString(String a_delimiter)
            a_delimiter     --> Delimiter to be used to distinguish between data elements.

    DESCRIPTION
        This function will save all information about a Person to a String separated by a given
        delimiter. The format for this serialized string is demonstrated in the example below:
        Name = travis, ID = 23, contains 3 FaceObjects.    -> Travis:23:1:2:3
        This makes it easy to parse the data as we know exactly which faces belong to this person.

    RETURNS
        String      - All data elements separated by given delimiter.

    AUTHOR
        Travis Whipple

    */
    /**/
    public String GetSerializedString(String a_delimiter){

        StringBuilder data = new StringBuilder();

        data.append(m_Name);

        data.append(a_delimiter);
        data.append(Integer.toString(m_Id));

        for(FaceObject face : m_PersonsFace){
            data.append(a_delimiter);
            data.append(Integer.toString(face.GetId()));
        }

        return data.toString();
    }
    /* String GetSerializedString(String a_delimiter) */

    /**/
    /*
    equals

    NAME
        equals()    - Overriding default equals function.

    SYNOPSIS
        boolean equals(Object a_obj)
            a_obj       --> Object to check for equality.

    DESCRIPTION
        This function overrides the default equals function for a Java Object. We override this
        function to check equality depending on what we are comparing this Person to. Person
        objects are compared by their name. This allows us to search a collection for a Person
        either from another Person object or from a String.

    RETURNS

    AUTHOR
        Travis Whipple

    */
    /**/
    @Override
    public boolean equals(Object a_obj) {

        if(a_obj instanceof Person){
            // Compare by each Persons name.
            Person otherPerson = (Person)a_obj;
            return this.m_Name.equals(otherPerson.m_Name);
        }

        if(a_obj instanceof String){
            // Compare this Persons name to the given String.
            String otherPersonName = (String)a_obj;
            return this.m_Name.equals(otherPersonName);
        }

        return false;
    }
    /* boolean equals(Object a_obj) */
}
/* class Person */
