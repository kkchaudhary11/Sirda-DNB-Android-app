package in.sirda.sirdanb;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class aCreate extends AppCompatActivity  {

    public static final int MEDIA_TYPE_IMAGE = 1;
    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "Sirda";
    String add = Constants.SrvAdd;
    private TextView messageText,selectimage;
    private EditText title, desc;
    private ImageView imageview;
    private int serverResponseCode = 0;
    private ProgressDialog dialog = null;
    private String upLoadServerUri = null;
    private String imagepath = null;
    private Spinner spinner1, spinner2;
    private Uri fileUri; // file url to store image
    private static final int SELECT_PICTURE = 1;
    private ImageButton imgAdd,camera,clear;
    private RadioGroup study;
    private RadioButton studyselect;



    //**********************************************************************************************
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_create);

        messageText = (TextView) findViewById(R.id.messageText);
        imageview = (ImageView) findViewById(R.id.imageView_pic);
        title = (EditText) findViewById(R.id.sub);
        desc = (EditText) findViewById(R.id.desc);

        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner2 = (Spinner) findViewById(R.id.spinner2);

        imgAdd = (ImageButton)findViewById(R.id.add_image);
        camera = (ImageButton)findViewById(R.id.camera);
        clear = (ImageButton)findViewById(R.id.clear);

        selectimage= (TextView)findViewById(R.id.select_image);

        study=(RadioGroup)findViewById(R.id.radiogroup);

        upLoadServerUri = add + "createnotice.php";


        imgAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);
            }
        });

        camera.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });

        clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                imageview.setImageBitmap(null);
                imagepath=null;
                messageText.setText("Image was clear");
                clear.setVisibility(View.GONE);
                selectimage.setVisibility(View.VISIBLE);
                imgAdd.setVisibility(View.VISIBLE);
                camera.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SELECT_PICTURE &&(resultCode == RESULT_OK) ){
            Uri selectedImageUri = data.getData();
            String imgpath = getPath(selectedImageUri);


            if(imgpath!=null) {

                messageText.setText("Uploading file path: " + imgpath);
            }
            else {
                try {
                    Uri selectedImage = data.getData();
                    String wholeID = DocumentsContract.getDocumentId(selectedImage);

                    // Split at colon, use second item in the array
                    String id = wholeID.split(":")[1];

                    String[] column = {MediaStore.Images.Media.DATA};

                    // where id is equal to
                    String sel = MediaStore.Images.Media._ID + "=?";

                    Cursor cursor = getContentResolver().
                            query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    column, sel, new String[]{id}, null);

                    imgpath = "";

                    int columnIndex = cursor.getColumnIndex(column[0]);

                    if (cursor.moveToFirst()) {
                        imgpath = cursor.getString(columnIndex);
                    }
                    cursor.close();

                    messageText.setText("Uploading file path: " + imgpath);
                }
                catch (Exception e){
                    Toast.makeText(aCreate.this,"Unable to select image, Try by selecting Gallery option",Toast.LENGTH_LONG).show();
                }
            }

            imageview.setImageURI(selectedImageUri);

            compressImage(imgpath);

            selectimage.setVisibility(View.GONE);
            imgAdd.setVisibility(View.GONE);
            camera.setVisibility(View.GONE);
            clear.setVisibility(View.VISIBLE);

            imageview.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    Uri uri = Uri.parse("file://" + imagepath);
                    intent.setDataAndType(uri, "image/*");
                    startActivity(intent);
                }
            });


        }
        // Receiving activity result method will be called after closing the camera
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // display it in image view
                previewCapturedImage();
                messageText.setText("Uploading file path: " + fileUri.toString());
                compressImage(fileUri.toString());

                selectimage.setVisibility(View.GONE);
                imgAdd.setVisibility(View.GONE);
                camera.setVisibility(View.GONE);
                clear.setVisibility(View.VISIBLE);


                imageview.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        Uri uri = Uri.parse("file://" + imagepath);
                        intent.setDataAndType(uri, "image/*");
                        startActivity(intent);
                    }
                });


            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
    //**********************************************************************************************




     //file upload code
    //**********************************************************************************************
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public int uploadFile(String sourceFileUri) {

//sourceFileUri.replace(sourceFileUri, "ashifaq");
        //
        int day, month, year;
        int second, minute, hour;
        GregorianCalendar date = new GregorianCalendar();

        day = date.get(Calendar.DAY_OF_MONTH);
        month = date.get(Calendar.MONTH);
        year = date.get(Calendar.YEAR);

        second = date.get(Calendar.SECOND);
        minute = date.get(Calendar.MINUTE);
        hour = date.get(Calendar.HOUR);

        String name = (hour + "" + minute + "" + second + "" + day + "" + (month + 1) + "" + year);
        String tag = name + ".jpg";
        String fileName = sourceFileUri.replace(sourceFileUri, tag);

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {

            dialog.dismiss();

            Log.e("uploadFile", "Source File not exist :" + imagepath);

            runOnUiThread(new Runnable() {
                public void run() {
                    messageText.setText("Source File not exist :" + imagepath);
                }
            });

            return 0;

        } else {

            try {

                String dept = String.valueOf(spinner1.getSelectedItem());
                String cat = String.valueOf(spinner2.getSelectedItem());

                String titleValue = title.getText().toString();
                String descValue = desc.getText().toString();
                int selectedId=study.getCheckedRadioButtonId();
                studyselect=(RadioButton)findViewById(selectedId);
                String select = studyselect.getText().toString();
                int variablesAdded = 0;
                if (descValue != null && !descValue.equals("")) {
                    if (variablesAdded == 0)
                        upLoadServerUri = upLoadServerUri + "?desc=" + URLEncoder.encode(descValue, "UTF-8");
                    else
                        upLoadServerUri = upLoadServerUri + "&desc=" + URLEncoder.encode(descValue, "UTF-8");
                    variablesAdded++;
                }

                if (titleValue != null && !titleValue.equals("")) {
                    if (variablesAdded == 0)
                        upLoadServerUri = upLoadServerUri + "?title=" + URLEncoder.encode(titleValue, "UTF-8");
                    else
                        upLoadServerUri = upLoadServerUri + "&title=" + URLEncoder.encode(titleValue, "UTF-8");
                    variablesAdded++;
                }

                if (dept != null && !dept.equals("")) {
                    if (variablesAdded == 0)
                        upLoadServerUri = upLoadServerUri + "?dept=" + URLEncoder.encode(dept, "UTF-8");
                    else
                        upLoadServerUri = upLoadServerUri + "&dept=" + URLEncoder.encode(dept, "UTF-8");
                    variablesAdded++;
                }
                if (cat != null && !cat.equals("")) {
                    if (variablesAdded == 0)
                        upLoadServerUri = upLoadServerUri + "?cat=" + URLEncoder.encode(cat, "UTF-8");
                    else
                        upLoadServerUri = upLoadServerUri + "&cat=" + URLEncoder.encode(cat, "UTF-8");
                    variablesAdded++;
                }
                if (select != null && !select.equals("")) {
                    if (variablesAdded == 0)
                        upLoadServerUri = upLoadServerUri + "?sel=" + URLEncoder.encode(select, "UTF-8");
                    else
                        upLoadServerUri = upLoadServerUri + "&sel=" + URLEncoder.encode(select, "UTF-8");
                    variablesAdded++;
                }




                URL url = new URL(upLoadServerUri);
// open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);


// Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);


// create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

// read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

// send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

// Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if (serverResponseCode == 200) {

                    runOnUiThread(new Runnable() {
                        public void run() {
                            String msg = "File Upload Completed.";
                            messageText.setText(msg);
                            title.setText("");
                            desc.setText("");
                            imageview.setImageBitmap(null);
                            clear.setVisibility(View.GONE);
                            selectimage.setVisibility(View.VISIBLE);
                            imgAdd.setVisibility(View.VISIBLE);
                            camera.setVisibility(View.VISIBLE);
                            Toast.makeText(aCreate.this, "Upload Complete.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

//close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        messageText.setText("MalformedURLException Exception : check script url.");
                        Toast.makeText(aCreate.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        messageText.setText("Got Exception : see logcat ");
                        Toast.makeText(aCreate.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file Exception", "Exception : " + e.getMessage(), e);
            }
            dialog.dismiss();
            return serverResponseCode;

        }
    }
    //**********************************************************************************************



     //Compression Code
    //**********************************************************************************************
    public String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 1116.0f;
        float maxWidth = 912.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);


//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "Sirda");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        imagepath = uriSting;

        return uriSting;

    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }

    }
    //*************************************************************************************


    //Optioon menu code
    //**********************************************************************************************
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {


            case R.id.upload:
                if (TextUtils.isEmpty(title.getText().toString())) {
                    title.setError("Please Enter Subject");
                    return false;
                }

                if (imagepath!=null) {
                    dialog = ProgressDialog.show(aCreate.this, "", "Uploading file...", true);
                    messageText.setText("uploading started.....");
                    new Thread(new Runnable() {
                        public void run() {
                            uploadFile(imagepath);
                        }
                    }).start();
                } else {
                    String dept = String.valueOf(spinner1.getSelectedItem());
                    String cat = String.valueOf(spinner2.getSelectedItem());
                    String titlesend = title.getText().toString();
                    String descsend = desc.getText().toString();
                    int selectedId=study.getCheckedRadioButtonId();
                    studyselect=(RadioButton)findViewById(selectedId);
                    String select = studyselect.getText().toString();
                    insertToDatabase(titlesend, descsend,dept,cat,select);
                }
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //**********************************************************************************************




     //Capture photo code
    //**********************************************************************************************
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

    }
    //Here we store the file url as it will be null after returning from camera
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");

    }
    //------------ Helper Methods ----------------------
    //Display image from a path to ImageView
    private void previewCapturedImage() {
        try {
            // hide video preview

            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 5;

            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                    options);

            imageview.setImageBitmap(bitmap);
        } catch (NullPointerException e) {
            Log.d("image preview:", "image preview not work");
            e.printStackTrace();
        }
    }
    //returning image
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }
    // Creating file uri to store image/video
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }
    //**********************************************************************************************


    private void insertToDatabase(String t, String de, String d,String c,String s) {

        final String tit = t;
        final String des = de;
        final String dep= d;
        final String ca= c;
        final String se= s;

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                dialog = ProgressDialog.show(aCreate.this, "", "Uploading file...", true);
                messageText.setText("uploading started.....");
            }
            @Override
            protected String doInBackground(String... params) {
                String titlename = params[0];
                String descrmane = params[1];
                String deptname = params[2];
                String catname = params[3];
                String select = params[4];



                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("title", tit));
                nameValuePairs.add(new BasicNameValuePair("desc", des));
                nameValuePairs.add(new BasicNameValuePair("dept", dep));
                nameValuePairs.add(new BasicNameValuePair("cat", ca));
                nameValuePairs.add(new BasicNameValuePair("sel", se));

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(upLoadServerUri);
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpClient.execute(httpPost);

                    HttpEntity entity = response.getEntity();


                } catch (ClientProtocolException e) {
                    System.out.println("Exception : " + e.getMessage());

                } catch (IOException e) {
                    System.out.println("Exception : " + e.getMessage());
                }

                return "success";

            }

            @Override
            protected void onPostExecute(String result) {
                dialog.dismiss();
                Toast.makeText(aCreate.this, "Upload Complete.", Toast.LENGTH_SHORT).show();
                String msg = "File Upload Completed.";
                messageText.setText(msg);
                title.setText("");
                desc.setText("");
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(tit, des,dep,ca,se);
    }

}

