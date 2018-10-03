package com.example.traviswhipple.seniorproject;

import android.content.Context;
import android.graphics.Bitmap;

// Libraries used for creating JSON request.
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

// Libraries used for creating Vision request.
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

// Java utilities used for converting data types.
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;


/**/
/*

CLASS NAME
    AnnotationRequest - Creates an annotated request

DESCRIPTION
    This Class will create an annotated request to access Google's Cloud Vision API.
    It will initialize the JSON request with proper authentication by using this apps
    signature. An API key is needed for all requests used and it is set for label detection
    with a maximum hit rate of 20. The image to be analyzed is compressed into an image
    with a size of 800x800, this is to save on bandwidth as well as speed up analyzing
    time. An image of this size still produces accurate results.

RESOURCES USED
    Vision API client libraries resources.
    https://cloud.google.com/vision/docs/libraries

    Information on all V1 Google Cloud Vision Objects and Libraries.
    https://developers.google.com/resources/api-libraries/documentation/vision/v1/java/latest/overview-tree.html
    Travis Whipple

DATE
    4/18/2018
*/
/**/

public class AnnotationRequest {

    private Context m_Context;
    // Resource used for creating vision requests.
    // https://cloud.google.com/vision/docs/libraries
    private final String API_KEY = "AIzaSyB53Zn1hsdqcrzRopzrVWXow82QSCr5m1c";
    private final int MAX_RESULTS = 20;

    // Max image size is 800x800. This still provides us with accurate results.
    private final int MAX_SIZE = 800;


    /**/
    /*
    AnnotationRequest()

    NAME
        AnnotationRequest - Constructor.
    SYNOPSIS
        public AnnotationRequest
            a_context        --> context of activity creating the request.

    AUTHOR
        Travis Whipple

    DATE
        4/18/2018
    */
    /**/
    public AnnotationRequest(Context a_context){
        m_Context = a_context;
    }

    /**/
    /*
    CreateRequest(String a_imageToBeAnalyzedPath)

    NAME
        CreateRequest   - creates a new request for Google Cloud Vision API.

    SYNOPSIS
        Vision.Images.Annotate CreateRequest(String a_imageToBeAnalyzedPath) throws IOException

            a_imageToBeAnalyzedPath     --> Absolute path to image to be sent in request.

    DESCRIPTION
        This function will attempt to create an annotated request for Google Cloud Vision
        API. For a request the API needs:

            Credentials for the application:
                - Applications signature.
                - API key - This key is unique to Travis Whipple and allows up to 1000 requests
                    per month.

            JSON request:
                - One or many AnnotateImageRequest object, created from image path parameter.
                - One or many Features object, these are the features to be used by API, this
                    function only adds the "LABEL_DETECTION" feature to get content in image.

        First a VisionRequestInitializer is created with API key along with application specific
        signature created automatically when compiled by Android. By adding a signature along
        with API key we can use a restricted API key. A scaled version of the image is added
        to the request. Finally the label detection feature is added to the request.

    RETURNS
        Returns an annotated request for Google Cloud Vision by returning a Vision.Images.Annotate
        object. Will throw an error if the request could not be created, usually due to API key
        being invalid.

    AUTHOR
        Travis Whipple

    DATE
        4/18/2018
    */
    /**/
    public Vision.Images.Annotate CreateRequest(String a_imageToBeAnalyzedPath) throws IOException {


        // Create httpTransport for an API call, need to use newCompatibleTransport.
        // More: https://developers.google.com/api-client-library/java/google-http-java-client/android
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();

        // Vision API uses JSON put and get requests. JsonFactory wraps it for us.
        // Gson is a low level JSON library implementation.
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        // Create an authenticated JSON request using android generated signature.
        VisionRequestInitializer requestInitializer =
                new VisionRequestInitializer(API_KEY) {

                    @Override
                    protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                            throws IOException {
                        // Call super to initializeVisionRequest as normal.
                        super.initializeVisionRequest(visionRequest);

                        visionRequest.getRequestHeaders().set("X-Android-Package", m_Context.getPackageName());

                        // Gets signature for this app.
                        String signature = PackageManagerUtils.getSignature(m_Context.getPackageManager(),
                                m_Context.getPackageName());

                        // Set signature of app.
                        visionRequest.getRequestHeaders().set("X-Android-Cert", signature);
                    }
                };

        // Create authenticated JSON request to be used when calling API.
        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(requestInitializer);
        Vision visionAnnotationRequest = builder.build();

        // Creating an annotated image for request.
        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();
        ByteArrayOutputStream byteArrOutputStream = new ByteArrayOutputStream();

        // Create scaled bitmap of image to be analyzed.
        BitmapResource bitmapResource = new BitmapResource();
        Bitmap bitmap = bitmapResource.GetScaledBitmap(a_imageToBeAnalyzedPath, MAX_SIZE);

        // Convert image into byteArray to convert image into a base 64 encoded Vision.Image object.
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrOutputStream);
        byte[] imageByteArray = byteArrOutputStream.toByteArray();

        // Vision request requires Vision.Image object encoded in base 64.
        Image visionImage = new Image();
        visionImage.encodeContent(imageByteArray);

        // Set image for request.
        annotateImageRequest.setImage(visionImage);

        // Add label detection feature to analyze image.
        ArrayList<Feature> features = new ArrayList<>();

        // Creating features for label detection, with a max of MAX_RESULTS results.
        Feature labelDetectionFeature = new Feature();
        labelDetectionFeature.setType("LABEL_DETECTION");
        labelDetectionFeature.setMaxResults(MAX_RESULTS);

        features.add(labelDetectionFeature);
        annotateImageRequest.setFeatures(features);

        // Finally create batchAnnotateImageRequest, this contains all annotated image requests.
        BatchAnnotateImagesRequest imageRequest = new BatchAnnotateImagesRequest();

        // Since BatchAnnotateImageRequests take an ArrayList of AnnotateImageRequest's,
        // Create ArrayList with only one element.
        ArrayList<AnnotateImageRequest> annotateImageRequestArr = new ArrayList<>();
        annotateImageRequestArr.add(annotateImageRequest);

        imageRequest.setRequests(annotateImageRequestArr);

        // Finally build final annotated request to be used for Google Vision call.
        return visionAnnotationRequest.images().annotate(imageRequest);
    }
    /* public Vision.Images.Annotate CreateRequest(String a_imageToBeAnalyzedPath) */
}
/* Class AnnotationRequest */
