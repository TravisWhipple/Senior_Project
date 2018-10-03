package com.example.traviswhipple.seniorproject;

import android.content.Context;
import android.database.Cursor;
import android.media.ExifInterface;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**/
/*

CLASS
    Serializer  - Save and load functionality

DESCRIPTION
    This class is in charge of saving and loading image data to a text file. This class can
    load images from the Downloads directory under the sub directory "PhotoLibrary", from the
    applications private "Pictures" directory, all images on the external SdCard and all photos
    taken on this device under the DCIM folder. This class will attempt to load a file from
    the directory "SeniorProject" located at the devices root external drive. If no existing
    file exists, then it will load a file from the downloads directory.


DATE
    6/22/2018
*/
/**/
public class Serializer {

    // Context of parent Application.
    private Context m_Context;
    private ImageManager m_ImageManager;

    // Name of file to save image data.
    private String m_FILE_NAME;

    // File to save data.
    private File m_SAVE_FILE;

    // Directory to all cropped face images.
    private String m_FACE_DIRECTORY_PATH;

    // Type of data to parse.
    private final String m_IMAGES = "IMAGES";
    private final String m_FACES = "FACES";
    private final String m_PEOPLE = "PEOPLE";


    /**/
    /*
    Serializer

    NAME
        Serializer()    - Constructor.

    SYNOPSIS
        Serializer(Context a_context, ImageManager a_imageManager)
            a_context       --> Context of parent Activity.
            a_imageManager  --> Class containing all data sets.

    DESCRIPTION
        This is a general constructor that saves the parameters to member variables.

    AUTHOR
        Travis Whipple

    */
    /**/
    public Serializer(Context a_context, ImageManager a_imageManager) {
        m_Context = a_context;
        m_ImageManager = a_imageManager;

        Initializer();
    }
    /* Serializer(Context a_context, ImageManager a_imageManager) */

    /**/
    /*
    Initializer

    NAME
        Initializer()   - Initializes all Files and Directories.

    DESCRIPTION
        This function will ensure that all directories exist in order to save/load image data.
        This fucntion will recieve the name as well as directory for the save file.

    RETURNS

    AUTHOR
        Travis Whipple

    */
    /**/
    private void Initializer(){

        // Do not save images from the directory that stores cropped faces.
        String projectDirectory = m_Context.getResources().getString(R.string.project_directory);
        String facesDirectory = m_Context.getResources().getString(R.string.faces_directory);
        m_FACE_DIRECTORY_PATH = projectDirectory + "/" + facesDirectory;

        // Get name to save file under.
        m_FILE_NAME = m_Context.getResources().getString(R.string.serializer_file);

        /* Create a public directory on external XD card. This is so that we can view and edit
        the file easier as the data is public.
        */
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/" + projectDirectory;
        File dir = new File(path);

        /* Create directory if it does not exist.
        Result is ignored as it will only create a directory if it does not exist already.
         */
        dir.mkdirs();

        // Create the save file.
        m_SAVE_FILE = new File(path + "/" + m_FILE_NAME);
    }
    /* void Initializer() */

    /**/
    /*
    Load

    NAME
        Load()  - Loads images into data set.

    DESCRIPTION
        This function will load all images into the data set. The data set is the ImageManager
        class. Files will be loaded from: images saved under the projects private Directory
        "Pictures". images saved in public libraries, including the "DCIM" (where photos are
        stored when taken from the devices camera application) and "Pictures" folders. This
        function will load each row from a file and depending on what type of data that row
        contains, the proper parse function will be called.

    RETURNS
        boolean     - True if load successful
                    - False if load failed, one of these results will always be returned.

    AUTHOR
        Travis Whipple

    */
    /**/
    public boolean Load(){

        // Load images from different directories.
        LoadImagesFromPublicDirectories();
        LoadImagesFromPrivateDirectory();

        // Get raw data from save file.
        String rawData = GetRawData();

        // If no data was returned, finish loading.
        if(rawData.equals("")){
            Log.e("Serializer.Load()", "No Data In File!");
            return false;
        }

        // Split data on each row.
        String data[] = rawData.split("\n");

        // Used to determine what type of data we are currently parsing.
        String currentlyParsing = "";

        Log.i("Serializer.Load()", "Loading file: " + m_SAVE_FILE.getAbsolutePath());
        for(String row : data){

            // Check if this row contains they type of data key.
            if(row.contains(m_IMAGES)){
                /* Images data will follow this line,
                continue to next line to read image data into data set. */
                Log.i("LOADING", "m_IMAGES");
                currentlyParsing = m_IMAGES;
                continue;

            }
            if(row.contains(m_FACES)){
                /* Face data will follow this line,
                continue to next line to read face data into data set. */
                Log.i("LOADING", "m_FACES");
                currentlyParsing = m_FACES;
                continue;

            }
            if(row.contains(m_PEOPLE)){
                /* People data will follow this line,
                continue to next line to read people data into data set. */
                Log.i("LOADING", "m_PEOPLE");
                currentlyParsing = m_PEOPLE;
                continue;
            }


            // Check what type of data we are currently parsing and call proper parse function.
            if(currentlyParsing.equals(m_IMAGES)){
                ParseImageData(row);
            }

            if(currentlyParsing.equals(m_FACES)){
                ParseFaceData(row);
            }

            if(currentlyParsing.equals(m_PEOPLE)){
                ParsePersonData(row);
            }
        }

        // Log tat files were successfully loaded.
        Log.i("Serializer.Load()", "Finished loading file "
                + m_SAVE_FILE.getAbsolutePath());
        return true;
    }
    /* boolean Load() */

    /**/
    /*
    LoadImagesFromPublicDirectories

    NAME
        LoadImagesFromPublicDirectories()  - Loads images from public directories.

    DESCRIPTION
        This function will load images from the public directories. The public directories
        include the following directories: Pictures, Downloads and DCIM.

    AUTHOR
        Travis Whipple

    */
    /**/
    private void LoadImagesFromPublicDirectories(){

        Cursor imageCursor = m_Context.getContentResolver()
                .query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        null,
                        null,
                        null,
                        MediaStore.Images.Media.DEFAULT_SORT_ORDER);
        imageCursor.moveToFirst();

        while(!imageCursor.isAfterLast()){

            String path = imageCursor.getString(imageCursor.getColumnIndex(
                    MediaStore.Images.Media.DATA));
            String dateStr = imageCursor.getString(imageCursor.getColumnIndex(
                    MediaStore.Images.Media.DATE_TAKEN));

            Long dateTaken = null;

            try{
                dateTaken = Long.parseLong(dateStr);
            }catch (Exception e){
                Log.e("Failed to parse date", e.getMessage());
            }

            AddImageToDataSet(path, dateTaken);
            imageCursor.moveToNext();
        }

        // Free cursor.
        imageCursor.close();
    }
    /* void LoadImagesFromPublicDirectories() */

    /**/
    /*
    LoadImagesFromPrivateDirectory

    NAME
        LoadImagesFromPrivateDirectory()   - Loads images from applications private Directory.

    DESCRIPTION
        This function will load images from the "Pictures" Directory located under the
        applications private data Directory. This Directory contains images taken within the
        applications Camera feature.

    AUTHOR
        Travis Whipple

    */
    /**/
    private void LoadImagesFromPrivateDirectory(){

        // Get applications private directory.
        File directory = m_Context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // All files within directory.
        File[] files = directory.listFiles();

        // Loop through each file
        for(File file : files){

            // Skip over directories.
            if(file.isDirectory()){
                continue;
            }

            // Get the current files path.
            String currentFilePath = file.getAbsolutePath();

            /* Get date data from image. If file is not an image, then ExifInterface will return
            a null object when trying to get images date. This is useful so we do not load
            any file except for image files. If an image does not have any date data, then
            we will ignore this image as the date is needed for the data set.
             */
            try{
                ExifInterface exifInterface = new ExifInterface(currentFilePath);
                String rawDate = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);

                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "yyyy:MM:dd HH:mm:ss", Locale.US);

                if(rawDate != null){
                    Log.e("Loaded", currentFilePath);
                    Date date = dateFormat.parse(rawDate);
                    Long longDate = date.getTime();

                    // Add the found image to the data set.
                    AddImageToDataSet(currentFilePath, longDate);

                }else{
                    // Log that date attribute was null as a warning.
                    Log.w("Serializer.LoadImagesFromPrivateDirectory",
                            "Null date, File may not be an image " + currentFilePath);
                }

            }catch (Exception e){
                // Log caught error as a warning.
                Log.w("Serializer.LoadImagesFromPrivateDirectory",
                        "Exception caught for image: " + currentFilePath
                                + " With error message: " + e.getMessage());
            }
        }
    }
    /* void LoadImagesFromPrivateDirectory() */

    /**/
    /*
    AddImageToDataSet

    NAME
        AddImageToDataSet()     - Adds an image to the data set.

    SYNOPSIS
        void AddImageToDataSet(String a_path, Long a_dateTaken)
            a_path          --> Path of images file.
            a_dateTaken     --> Date taken in milliseconds.

    DESCRIPTION
        This function will add an image to the data set. Currently the data set is the
        ImageManager class. This function will ensure that images from the "Faces" directory
        are not added, as we do not want them loaded into the photo gallery. If the date is
        null, the image will not be added either. If a file meets the requirements then the
        image will be added to the data set.

    RETURNS

    AUTHOR
        Travis Whipple

    */
    /**/
    private void AddImageToDataSet(String a_path, Long a_dateTaken){

        // Only Add image if it is not from the faces directory.
        if(!a_path.contains(m_FACE_DIRECTORY_PATH)){

            // If date is null, then a wrong file is trying to be loaded.
            if(a_dateTaken != null){

                // Add image to data set.
                ImageObject imageToAdd = new ImageObject(a_path, a_dateTaken);
                m_ImageManager.AddImage(imageToAdd);
            }
        }

    }
    /* void AddImageToDataSet(String a_path, Long a_dateTaken) */

    /**/
    /*
    GetRawData

    NAME
        GetRawData()    - Gets all data in file as a String object.

    DESCRIPTION
        This function will read all bytes of the save file and return the bytes as a String
        object. This function will search for the save file under the created SeniorProject
        Directory. If the file does not exist under the SeniorProject directory the this function
        will check the Downloads directory for the save file. If no file is found then a blank
        String will be returned.

    RETURNS
        String      - Containing every character in file. Or,
                    - Empty string if file was not found. This function will never return null.

    AUTHOR
        Travis Whipple

    */
    /**/
    private String GetRawData(){

        // Will return empty string if file could not be loaded.
        String rawData = "";
        FileInputStream inputStream = null;

        // If file does not exist, check Downloads directory.
        if(!m_SAVE_FILE.exists()){

            // Find save file under Downloads folder.
            File downloadsDirectory = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
            File[] filesInDir = downloadsDirectory.listFiles();

            // Loop through files in the Downloads directory.
            for(File file : filesInDir){

                if(file.getName().equals(m_FILE_NAME)){
                    m_SAVE_FILE = file;
                    break;
                }
            }
        }

        // No file was still not found, return empty string.
        if(!m_SAVE_FILE.exists()){
            Log.w("Serializer.GetRawData", "No save file found");
            return rawData;
        }

        // Log where the file was found.
        Log.i("Serializer.GetRawData", "Save file found: "
                + m_SAVE_FILE.getAbsolutePath());

        // Read in bytes.
        try {
            // Get input stream from file.
            inputStream = new FileInputStream(m_SAVE_FILE);

            // Set up buffer.
            int size = inputStream.available();
            byte[] buffer = new byte[size];

            // Read into buffer
            inputStream.read(buffer);
            inputStream.close();

            // Save contents of buffer.
            rawData = new String(buffer);
            Log.e("LOAD", "Success");
        }

        catch(Exception e) {
            e.printStackTrace();
            Log.e("Serializer.GetRawData", "IOException: " + e.getMessage());
        }

        // Return raw data.
        return rawData;
    }
    /* String GetRawData() */

    /**/
    /*
    ParseImageData

    NAME
        ParseImageData()    - Parses image data from string.

    SYNOPSIS
        void ParseImageData(String a_imageData)
            a_imageData     --> All data about an image.

    DESCRIPTION
        This class parse all elements within the given image data string. It will then locate
        the image within the data set. If no image was found in the data set for the given data,
        then the image was deleted, so we do not add it. For the given image, this function
        will parse out data containing if an image was analyzed for faces or for tags. It will
        add all tags associated for that image. If an image does not have at least the bare
        minimum of 3 elements; image path, has been analyzed, has detected faces. Then the
        file was corrupted.

    AUTHOR
        Travis Whipple

    */
    /**/
    private void ParseImageData(String a_imageData){

        if(a_imageData == null || a_imageData.length() == 0){
            // Given string is empty, log data and return.
            Log.e("Serializer.ParseImageData", "Image data is empty!");
            return;
        }

        // Get all ImageObject data elements.
        String[] imageElements = SplitIntoElements(a_imageData);

        //ImageObject imageObject = new ImageObject(tagEntry[0]);
        String imagePath = imageElements[0];
        ImageObject imageObject = m_ImageManager.GetImageObjectIfExists(imagePath);

        // If object is null, then image was removed from device. So do nothing with it.
        // Must contain at least 3 elements: file name, hasBeenAnalyzed, hasDetectedFaces
        if(imageObject != null && imageElements.length >= 3){

            imageObject.SetHasBeenAnalyzed(Boolean.parseBoolean(imageElements[1]));
            imageObject.SetHasDetectedFaces(Boolean.parseBoolean(imageElements[2]));

            // Start at 3 to skip over path name, hasBeenAnalyzed and hasDetectedFaces.
            for(int i = 3; i < imageElements.length; i++){

                // Split tag entry data into tag's name and associated confidence.
                String[] tagSplitData = imageElements[i].split("[()]", 0);

                String tagName = tagSplitData[0];
                double confidence = Double.parseDouble(tagSplitData[1]);

                // Create a new Tag object and Add relationship to associated image.
                Tag tag = new Tag(tagName);
                m_ImageManager.AddTagRelation(imageObject, tag, confidence);
            }
        }
    }
    /* void ParseImageData(String a_imageData) */

    /**/
    /*
    ParseFaceData

    NAME
        ParseFaceData()     - Parses data for a face.

    SYNOPSIS
        void ParseFaceData(String a_faceData)
            a_faceData      --> All data about a face.

    DESCRIPTION
        This function will attempt to split a face object into its 7 elements: imagePath,
        facePath, id and 4 facial boundaries values. This function will ensure that the given
        data is valid

    RETURNS

    AUTHOR
        Travis Whipple

    */
    /**/
    private void ParseFaceData(String a_faceData){

        // Check that data is not null or empty.
        if(a_faceData == null || a_faceData.length() == 0){
            Log.e("Serializer.ParseFaceData",
                    "Face data is null or has no elements.");
            return;
        }

        // Get all Face data elements.
        String[] faceDataElements = SplitIntoElements(a_faceData);

        // Check that there are the correct number of elements.
        if(faceDataElements.length != 7){
            // Log error.
            Log.e("Serializer.ParseFaceData",
                    "Face data should contain 7 elements. Data: " + a_faceData);
            return;
        }

        // Get paths for face and original image.
        String imagePath = faceDataElements[0];
        String facePath  = faceDataElements[1];

        // Get face's ID.
        int id      = Integer.parseInt(faceDataElements[2]);

        // Get face's boundaries location.
        int startX  = Integer.parseInt(faceDataElements[3]);
        int startY  = Integer.parseInt(faceDataElements[4]);
        int endX    = Integer.parseInt(faceDataElements[5]);
        int endY    = Integer.parseInt(faceDataElements[6]);

        // Create face boundaries from parsed values.
        FaceBoundaries fb = new FaceBoundaries();
        fb.SetFromSerializedData(startX, startY, endX, endY);

        // Add face to data set.
        FaceObject face = new FaceObject(m_Context, facePath, imagePath, fb, id);
        m_ImageManager.AddFace(face);
    }
    /* void ParseFaceData(String a_faceData) */

    /**/
    /*
    ParsePersonData

    NAME
        ParsePersonData()   - Parses data for a Person.

    SYNOPSIS
        void ParsePersonData(String a_personData)
            a_personData        --> All data about a Person.

    DESCRIPTION
        This function will parse a Person object from a persons data string. This function will
        check that the given string is not null nor empty, and that it contains at least the
        minimum amount of elements.

    RETURNS

    AUTHOR
        Travis Whipple

    */
    /**/
    private void ParsePersonData(String a_personData){

        int minimumNumberOfElements = 2;

        // Check that data is not null nor empty.
        if(a_personData == null || a_personData.length() == 0){

            // Log error message.
            Log.e("Serializer.ParsePersonData", "Data is either null, or empty");
            return;
        }

        // Get all People data elements.
        String[] personDataElements = SplitIntoElements(a_personData);

        // Check that there are correct number of elements. Should be at least 2
        if(personDataElements.length < minimumNumberOfElements){

            // Log error message.
            Log.e("Serializer.ParsePersonData",
                    "Not enough data elements, required at least 2. Data: " + a_personData);
            return;
        }

        // Get Persons from Name and ID.
        String personName   = personDataElements[0];
        int personId        = Integer.parseInt(personDataElements[1]);
        Person person = new Person(personId, personName);

        // Load all associated faces for the given person.
        if(personDataElements.length > minimumNumberOfElements){

            for(int i = minimumNumberOfElements; i < personDataElements.length; i++){

                // Get Face from parsed ID.
                int faceId = Integer.parseInt(personDataElements[i]);
                FaceObject face = m_ImageManager.GetFace(faceId);

                // Add face to person.
                person.AddFace(face);
            }
        }

        // Add the Person to the data set.
        m_ImageManager.AddPerson(person);
    }
    /* void ParsePersonData(String a_personData) */

    /**/
    /*
    SaveToFile

    NAME
        SaveToFile()    - Save program data to file.

    DESCRIPTION
        This function will save all data to a save file. This function is responsible for writing
        3 main aspects to the save file; Images and their tags, Faces and their boundaries, People
        and the faces they contain. Each of these 3 aspects will be prefaced in the save file to
        make parsing and organization easier.

    AUTHOR
        Travis Whipple

    */
    /**/
    public void SaveToFile(){

        // Make sure File is in correct directory.
        Initializer();

        if(!IsExternalStorageWritable()){
            Log.e("Serializer.SaveToFile", "Cannot write to SD, no permission");
            return;
        }

        // Get file in path.
        FileOutputStream outputStream = null;
        StringBuilder data = new StringBuilder();

        // Get all image data to add to file.
        data.append(m_IMAGES);
        data.append("\n");
        for(ImageObject image : m_ImageManager.GetImages()){

            data.append(image.GetSerializedString(","));
            data.append("\n");
        }

        // Get all face data to add to file.
        data.append(m_FACES);
        data.append("\n");
        for(FaceObject face : m_ImageManager.GetAllFaces()){

            data.append(face.GetSerializedString(","));
            data.append("\n");
        }

        // Get all people data to add to file.
        data.append(m_PEOPLE);
        data.append("\n");
        for(Person person : m_ImageManager.GetPeople()){

            data.append(person.GetSerializedString(","));
            data.append("\n");
        }

        // Attempt to write to file.
        try {
            outputStream = new FileOutputStream(m_SAVE_FILE);
            outputStream.write(data.toString().getBytes());
            outputStream.close();
            Log.i("Serializer.SaveToFile", "Saved under: "
                    + m_SAVE_FILE.getAbsolutePath());
        }
        catch(Exception e) {
            Log.e("Serialize.SaveToFile", "Cannot write to file: " + e.toString());
            Log.e("Serialize.SaveToFile", "Error message: " + e.getMessage());
        }
    }
    /* void SaveToFile() */

    /**/
    /*
    SplitIntoElements

    NAME
        SplitIntoElements() - Splits a row of data into its individual elements.

    SYNOPSIS
        String[] SplitIntoElements(String a_data)
            a_data      --> Data to be split.

    DESCRIPTION
        This function will split a String into an array of Strings on the delimiter ','

    RETURNS
        String[]        - Array containing each element with no ',' or,
                        - An empty array. This function will never return null.

    AUTHOR
        Travis Whipple

    */
    /**/
    private String[] SplitIntoElements(String a_data){

        // Remove any spaces after "," in file.
        a_data = a_data.replaceAll(",\\s", ",");

        // Split the row by each tag entry, separated by ","
        return a_data.split("[,]");
    }
    /* String[] SplitIntoElements(String a_data) */

    /**/
    /*
    IsExternalStorageWritable

    NAME
        IsExternalStorageWritable()     - Checks if external storage is writable.

    DESCRIPTION
        Simply checks if the write permission is granted.

    RETURNS
        boolean     - True if has write permission.
                    - False if not, one of these values will always be returned.

    AUTHOR
        Travis Whipple

    */
    /**/
    // Check if external storage is writable.
    private boolean IsExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}
/* class Serializer */
