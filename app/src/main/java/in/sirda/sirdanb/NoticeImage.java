package in.sirda.sirdanb;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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

import java.io.File;

public class NoticeImage extends AppCompatActivity {

    private static final String TAG_PID = "pid";
    private final int RES_ERROR = R.drawable.error_orange;
    private final int RES_PLACEHOLDER = R.drawable.placeholder_grey;
    String add = Constants.SrvAdd;
    File myImageFile;
    String id;
    private ImageView imgDisplay;
    private String name = "temp";

    @SuppressLint({"SetTextI18n", "CutPasteId"})
    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getIntent().getStringExtra("pid");

        final String url = add + "uploads/" + id;
        setContentView(R.layout.notice);


        imgDisplay = (ImageView) findViewById(R.id.imgResult);
        imgDisplay.setImageResource(RES_PLACEHOLDER);
        final TextView tvPercent = (TextView) findViewById(R.id.tvPercent);
        final ProgressBar pbLoading = (ProgressBar) findViewById(R.id.pbImageLoading);
        final BasicImageDownloader downloader = new BasicImageDownloader(new BasicImageDownloader.OnImageLoaderListener() {
            @Override
            public void onError(BasicImageDownloader.ImageError error) {
                Toast.makeText(NoticeImage.this, "Unable to download image", Toast.LENGTH_LONG).show();
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

                        imgDisplay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.parse("file://" + myImageFile.getAbsolutePath()), "image/*");
                                startActivity(intent);
                            }
                        });


                    }

                    @Override
                    public void onBitmapSaveError(BasicImageDownloader.ImageError error) {
                        Toast.makeText(NoticeImage.this, "Error code " + error.getErrorCode() + ": " +
                                error.getMessage(), Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }


                }, mFormat, true);
                tvPercent.setVisibility(View.GONE);
                pbLoading.setVisibility(View.GONE);
                imgDisplay.setImageBitmap(result);
                imgDisplay.startAnimation(AnimationUtils.loadAnimation(NoticeImage.this, android.R.anim.fade_in));
            }
        });
        downloader.download(url, true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notice, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case R.id.download:
                Intent i = new Intent(this, ImageDownload.class);
                i.putExtra(TAG_PID, id);
                startActivity(i);
                return true;

            case R.id.share:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + myImageFile.getAbsolutePath()));
                shareIntent.setType("image/jpeg");
                startActivity(Intent.createChooser(shareIntent, id));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}




