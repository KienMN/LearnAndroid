package com.example.kienmaingoc.cameratest;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentityprovider.AmazonCognitoIdentityProvider;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClient;
import com.amazonaws.services.rekognition.model.Attribute;
import com.amazonaws.services.rekognition.model.CompareFacesRequest;
import com.amazonaws.services.rekognition.model.CompareFacesResult;
import com.amazonaws.services.rekognition.model.DetectFacesRequest;
import com.amazonaws.services.rekognition.model.DetectFacesResult;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.ListCollectionsRequest;
import com.amazonaws.services.rekognition.model.ListCollectionsResult;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
//import com.amazonaws.services.rekognition.model.Image;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;
import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity {

    public static final String COLLECTION_ID = "FaceCollectionID";
    public static final String S3_BUCKET = "kienimages";
    public static final String COGNITO_IDENTITY_POOL = "eu-west-1:b7f1b6ed-270b-42f5-93fd-15bcf380cccc";
    public static final String ACCESS_KEY_ID = "AKIAITNPIQ3TMODHVGGA";
    public static final String SECRET_ACCESS_KEY = "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";
    private static final Float THRESHOLD = 70F;

    ImageView imageView;
    Uri photoURI;
    String imageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Log.i("i", "not permit");
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 100);

        }

        if (checkSelfPermission(android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            Log.i("internet", "not permit");
//            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 500);
        }


        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.i("ko chay", "true");
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 500);
        }
        imageView = (ImageView) findViewById(R.id.imageView);
        dispatchTakePictureIntent();

    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there is a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // Create a file where photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Continue only if the File was successfully created

            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(
                        this,
                        "com.example.kienmaingoc.cameratest",
                        photoFile
                );
                imageURL = photoFile.getAbsolutePath();
                Log.i("uri", photoURI.toString());

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                takePictureIntent.putExtra("url", photoFile.getAbsolutePath());
//                setResult(RESULT_OK, takePictureIntent);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.i("request code", String.valueOf(requestCode));
//        Log.i("result code", String.valueOf(resultCode));
//
//        if (data == null) {
//            Log.i("info", "null data");
//        } else {
//            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//                Log.i("data", data.toString());
//                System.out.println(data.getExtras().get("url"));
//                Bundle extra = data.getExtras();
//                Bitmap imageBitmap = (Bitmap) extra.get("data");
//                Log.i("url", extra.get("url").toString());
//                imageView.setImageBitmap(imageBitmap);
//            }
//        }

        Bitmap bitmap = BitmapFactory.decodeFile(imageURL);
        imageView.setImageBitmap(bitmap);

        Log.i("mCurrentImagePath", mCurrentImagePath);
        addImageToGallery(mCurrentImagePath, this);

        AmazonProcess process = new AmazonProcess();
        try {
            String string = process.execute(mCurrentImagePath).get();
            Log.i("list result", string);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
//        dispatchTakePictureIntent();
    }

    String mCurrentImagePath;
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file
        mCurrentImagePath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentImagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public static void addImageToGallery(final String filePath, final Context context) {

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    public class AmazonProcess extends AsyncTask<String, Void, String> {

        public Integer uploadImage(String imagePath){
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    getApplicationContext(),
                    COGNITO_IDENTITY_POOL,
                    Regions.EU_WEST_1
            );
            AmazonS3Client s3Client = new AmazonS3Client(credentialsProvider);
            File file = new File(imagePath);
            PutObjectRequest request = new PutObjectRequest(S3_BUCKET, file.getName(), file);
            PutObjectResult result = s3Client.putObject(request);
            if (result != null) {
                Log.i("put object result", result.toString());
                return 1;
            }
            else {
                return 0;
            }
        }

        public String compareFaces() {
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    getApplicationContext(),
                    COGNITO_IDENTITY_POOL,
                    Regions.EU_WEST_1
            );
            AmazonRekognitionClient rekognition = new AmazonRekognitionClient(credentialsProvider);


//            S3Object s3SourceObject = new S3Object()
//                    .withBucket(S3_BUCKET)
//                    .withName("source.jpg");
//            S3Object s3TargetObject = new S3Object()
//                    .withBucket(S3_BUCKET)
//                    .withName("target.jpg");
//            com.amazonaws.services.rekognition.model.Image source = new com.amazonaws.services.rekognition.model.Image()
//                    .withS3Object(s3SourceObject);
//            com.amazonaws.services.rekognition.model.Image target = new com.amazonaws.services.rekognition.model.Image()
//                    .withS3Object(s3TargetObject);
//
//            CompareFacesRequest request = new CompareFacesRequest()
//                    .withSourceImage(source)
//                    .withTargetImage(target)
//                    .withSimilarityThreshold(THRESHOLD);
//
//            CompareFacesResult result = rekognition.compareFaces(request);
//            return result.toString();

//            AmazonS3Client s3Client = new AmazonS3Client(credentialsProvider);
//            ListObjectsV2Request request = new ListObjectsV2Request().withBucketName(S3_BUCKET);
//            ListObjectsV2Result result = s3Client.listObjectsV2(request);
//            List<S3ObjectSummary> objectSummaries = result.getObjectSummaries();
//            String res = "";
//            for (S3ObjectSummary objectSummary : objectSummaries) {
//                res += objectSummary.getKey() + "; ";
//            }
//            return res;


//            DetectFacesRequest request = new DetectFacesRequest()
//                    .withImage(new com.amazonaws.services.rekognition.model.Image()
//                        .withS3Object(new S3Object()
//                            .withBucket(S3_BUCKET)
//                            .withName("source.jpg")))
//                    .withAttributes(String.valueOf(Attribute.ALL));

//            S3Object s3Object = new S3Object().withBucket(S3_BUCKET).withName("source.jpg");
//            com.amazonaws.services.rekognition.model.Image image = new com.amazonaws.services.rekognition.model.Image()
//                    .withS3Object(s3Object);
//            DetectFacesRequest request = new DetectFacesRequest()
//                    .withImage(image)
//                    .withAttributes(String.valueOf(Attribute.ALL));
//            DetectFacesResult result = rekognition.detectFaces(request);
//            ListCollectionsRequest request = new ListCollectionsRequest();
//            ListCollectionsResult result = rekognition.listCollections(request);
            try {
                FileInputStream file = new FileInputStream(mCurrentImagePath);
                ByteBuffer byteBuffer = ByteBuffer.wrap(IOUtils.toByteArray(file));
                com.amazonaws.services.rekognition.model.Image image = new com.amazonaws.services.rekognition.model.Image().withBytes(byteBuffer);
                DetectLabelsRequest request = new DetectLabelsRequest()
                        .withImage(image)
                        .withMaxLabels(2);
                DetectLabelsResult result = rekognition.detectLabels(request);
                return result.toString();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";

        }

        @Override
        protected String doInBackground(String... strings) {
//            Integer res = uploadImage(strings[0]);
//            if (res.intValue() == 1) {
//                return 1;
//            } else {
//                return 0;
//            }

            String res = compareFaces();
            return res;

        }
    }

}
