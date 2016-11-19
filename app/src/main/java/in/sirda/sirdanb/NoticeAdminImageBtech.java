package in.sirda.sirdanb;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.imagedownloader.BasicImageDownloader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NoticeAdminImageBtech extends AppCompatActivity {

    private static final String TAG_PID = "pid";
    private static final String TAG_SRNO = "srno";
    private ImageView imgDisplay;
    private final int RES_ERROR = R.drawable.error_orange;
    private final int RES_PLACEHOLDER = R.drawable.placeholder_grey;
    String add = Constants.SrvAdd;
    private String name="temp";
    File myImageFile;
    String id,srno;

    @SuppressLint({"SetTextI18n", "CutPasteId"})
    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        id = extras.getString(TAG_PID);
        srno = extras.getString(TAG_SRNO);


        final String url = add + "uploads/" + id;
        setContentView(R.layout.notice_admin);

        imgDisplay = (ImageView) findViewById(R.id.imgResult);
        imgDisplay.setImageResource(RES_PLACEHOLDER);
        final TextView tvPercent = (TextView) findViewById(R.id.tvPercent);
        final ProgressBar pbLoading = (ProgressBar) findViewById(R.id.pbImageLoading);
        final BasicImageDownloader downloader = new BasicImageDownloader(new BasicImageDownloader.OnImageLoaderListener() {
            @Override
            public void onError(BasicImageDownloader.ImageError error) {
                Toast.makeText(NoticeAdminImageBtech.this,"Unable to download image", Toast.LENGTH_LONG).show();
                error.printStackTrace();
                imgDisplay.setImageResource(RES_ERROR);
                tvPercent.setVisibility(View.GONE);
                pbLoading.setVisibility(View.GONE);
            }

            @Override
            public void onProgressChange(int percent) {
                pbLoading.setProgress(percent);
                tvPercent.setText(percent + "%");
            }

            @Override
            public void onComplete(Bitmap result) {



 /* save the image - I'm gonna use JPEG */
                Bitmap.CompressFormat mFormat = Bitmap.CompressFormat.JPEG;
                        /* don't forget to include the extension into the file name */
                myImageFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separator + "Sirda" + File.separator + name + "." + mFormat.name().toLowerCase());
                BasicImageDownloader.writeToDisk(myImageFile, result, new BasicImageDownloader.OnBitmapSaveListener() {
                    @Override
                    public void onBitmapSaved() {

                        imgDisplay.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.parse("file://"+myImageFile.getAbsolutePath()), "image/*");
                                startActivity(intent);
                            }
                        });


                    }

                    @Override
                    public void onBitmapSaveError(BasicImageDownloader.ImageError error) {
                        Toast.makeText(NoticeAdminImageBtech.this, "Error code " + error.getErrorCode() + ": " +
                                error.getMessage(), Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }


                }, mFormat, true);
                tvPercent.setVisibility(View.GONE);
                pbLoading.setVisibility(View.GONE);
                imgDisplay.setImageBitmap(result);
                imgDisplay.startAnimation(AnimationUtils.loadAnimation(NoticeAdminImageBtech.this, android.R.anim.fade_in));
            }
        });
        downloader.download(url, true);
    }

    private void sendtoserver(String del) {
        final String delete=del;
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String paramDel = params[0];

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("del", delete));


                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(add + "deletebtech.php");
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
                super.onPostExecute(result);

                Toast.makeText(getApplicationContext(), "Notice deleted", Toast.LENGTH_LONG).show();

                Intent list = new Intent(NoticeAdminImageBtech.this, NoticeAdminBtech.class);
                startActivity(list);
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(del);
    }


            @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notice_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case R.id.delete:
                sendtoserver(srno);
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }
}




