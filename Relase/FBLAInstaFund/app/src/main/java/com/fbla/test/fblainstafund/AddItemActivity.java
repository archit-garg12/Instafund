package com.fbla.test.fblainstafund;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class AddItemActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_GALLERY = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int IMAGE_WIDTH = 180;
    private static final int IMAGE_HEIGHT = 60;

    EditText editTestName, editTextState, editTextPrice, editTextDescription;

    ImageView pictureView, imageButtoncamera, imageButtonGallery;
    private static final String TAG = "AddItemActivity.java";
    private Uri filePath;
    Bitmap selectedImage;
    private ProgressBar progressBar;
    ScrollView scrollView;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTestName = (EditText) findViewById(R.id.editTestName);
        editTextState = (EditText) findViewById(R.id.editTextState);
        editTextPrice = (EditText) findViewById(R.id.editTextPrice);
        editTextDescription = (EditText) findViewById(R.id.editTextDescription);
        imageButtoncamera = (ImageView) findViewById(R.id.imageButtoncamera);
        imageButtonGallery = (ImageView) findViewById(R.id.imageButtonGallery);
        pictureView = (ImageView) findViewById(R.id.pictureView);
        progressBar = (ProgressBar) findViewById(R.id.login_progress);
        scrollView = (ScrollView)findViewById(R.id.scrollView);
    }

    /**
     * @param view
     */
    public void openGallery(View view) {
        Log.d(TAG, "openGallery called.");
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        if (photoPickerIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY);
        } else {
            Toast.makeText(AddItemActivity.this, "Gallery app not found.", Toast.LENGTH_LONG).show();
        }
    }

    public void openCamera(View view) {
        Log.d(TAG, "openCamera called.");
        filePath = dispatchTakePictureIntent();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {

            case REQUEST_CODE_GALLERY:
                if (resultCode == RESULT_OK) {
                    Uri uri = imageReturnedIntent.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(
                            uri, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();
                    Log.d(TAG, "filePath " + filePath);
                     selectedImage = decodeSampledBitmapFromResource(filePath, IMAGE_WIDTH, IMAGE_HEIGHT);
                    if (selectedImage != null) {
                        pictureView.setImageBitmap(selectedImage);
                    } else {
                        Log.e(TAG, "selectedImage path is null");
                    }
                }
                break;
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {

                    selectedImage = decodeSampledBitmapFromResource(filePath.getPath(), IMAGE_WIDTH, IMAGE_HEIGHT);
                    if (selectedImage != null) {
                        pictureView.setImageBitmap(selectedImage);
                    } else {
                        Log.e(TAG, "selectedImage path is null");
                    }


                }
        }
    }

    public static Bitmap decodeSampledBitmapFromResource(String filePath,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private Uri dispatchTakePictureIntent() {
        Log.d(TAG, "dispatchTakePictureIntent called.");
        File photoFile = null;
        Uri photoURI = null;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
                Log.d(TAG, "photoFile " + photoFile);
            } catch (IOException ex) {
                Log.e(TAG, ex.getMessage());
            }

            if (photoFile != null) {

                photoURI = Uri.fromFile(photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
        return photoURI;
    }


    Handler myEventHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 2:
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),"Item Posted Successfully.",Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                   finish();
                    break;
                case 1:
                    progressBar.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.GONE);
                    break;
                case 3:
                    scrollView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),"Item posted cancelled.",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    public void submmit(View view) {
        SharedPreferences sp = this.getSharedPreferences("com.whiznets.test.fblainstafund",Activity.MODE_PRIVATE);
        String id = sp.getString("user_id","");
        String name = editTestName.getText().toString();
        String state = editTextState.getText().toString();
        String price = editTextPrice.getText().toString();
        String description = editTextDescription.getText().toString();
        String decodedImage = encodeToBase64(selectedImage,Bitmap.CompressFormat.JPEG, 100);
        if(isValidate(id, name,state,price,description,decodedImage)) {
            PostDataToLoderManager pd = new PostDataToLoderManager(myEventHandler);
            pd.execute(id, name, state, price, description, decodedImage);
        }



    }
    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        if(image != null) {
            ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
            image.compress(compressFormat, quality, byteArrayOS);
            return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
        }
        return  null;
    }


    private boolean isValidate(String id, String name,String state, String price, String description,String image) {

        if(id == null || id.trim().equals("")){
            Toast.makeText(this,"Your id invalid",Toast.LENGTH_LONG).show();
            editTestName.setError("This field is required.");
            return false;
        }
        if(name == null || name.trim().equals("")){
            editTestName.setError("This field is required.");
                return false;
        }
        if(state == null || state.trim().equals("")){
            editTextState.setError("This field is required.");
            return false;
        }
        if(price == null || price.trim().equals("")){
            editTextPrice.setError("This field is required.");
            return false;
        }
        if(description == null || description.trim().equals("")){
            editTextDescription.setError("This field is required.");
            return false;
        }
        if(image == null){
            Toast.makeText(this,"Capture Image First.",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }



}

