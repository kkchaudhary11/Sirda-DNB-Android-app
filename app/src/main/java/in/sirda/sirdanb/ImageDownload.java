package in.sirda.sirdanb;



import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Arrays;


public class ImageDownload extends AppCompatActivity {

    String add = Constants.SrvAdd;

    private BroadcastReceiver mDLCompleteReceiver;

    @SuppressLint({"SetTextI18n", "CutPasteId"})
    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String name = getIntent().getStringExtra("pid");
        final String url = add + "uploads/" + name;


        setContentView(R.layout.notice_download);
        getSupportActionBar().setTitle("Download NoticeImage");

        final TextView tvStatus = (TextView) findViewById(R.id.tvDMWorking);

        final Button opn=(Button)findViewById(R.id.open);
        final Button retry=(Button)findViewById(R.id.retry);

        final ImageView wait=(ImageView)findViewById(R.id.wait);
        final ImageView done=(ImageView)findViewById(R.id.done);
        final ImageView error=(ImageView)findViewById(R.id.error);
        final ImageView wrong=(ImageView)findViewById(R.id.wrong);

        Animation anim = AnimationUtils.loadAnimation(ImageDownload.this, android.R.anim.fade_out);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setDuration(700L);
        tvStatus.startAnimation(anim);

        DownloadManager.Request request = null;

        try {
            request = new DownloadManager.Request(Uri.parse(url));
        } catch (IllegalArgumentException e) {
            tvStatus.setText("Error: " + e.getMessage());
            tvStatus.clearAnimation();
            finish();
        }
                /* allow mobile and WiFi downloads */
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setDescription("Downloading file");

                /* we let the user see the download in a notification */
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                /* Try to determine the file extension from the url. Only allow image types. You
                 * can skip this check if you only plan to handle the downloaded file manually and
                 * don't care about file managers not recognizing the file as a known type */
        String[] allowedTypes = {"png", "jpg", "jpeg", "gif", "webp"};
        String suffix = url.substring(url.lastIndexOf(".") + 1).toLowerCase();
        if (!Arrays.asList(allowedTypes).contains(suffix)) {
            tvStatus.clearAnimation();
            tvStatus.setText("Invalid file extension. Allowed types: \n");
            for (String s : allowedTypes) {
                tvStatus.append("\n" + "." + s);
            }
            finish();
        }

                /* set the destination path for this download */
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS +
                File.separator + "Sirda", name);
                /* allow the MediaScanner to scan the downloaded file */
        request.allowScanningByMediaScanner();
        final DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                /* this is our unique download id */
        final long DL_ID = dm.enqueue(request);

                /* get notified when the download is complete */
        mDLCompleteReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                wait.setVisibility(View.GONE);
                        /* our download */
                if (DL_ID == intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)) {

                    tvStatus.clearAnimation();
                            /* get the path of the downloaded file */
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(DL_ID);
                    Cursor cursor = dm.query(query);
                    if (!cursor.moveToFirst()) {
                        tvStatus.setText("Download error: cursor is empty");
                        error.setVisibility(View.VISIBLE);
                        retry.setVisibility(View.VISIBLE);

                        retry.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
                            }
                        });
                        return;
                    }

                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                            != DownloadManager.STATUS_SUCCESSFUL) {
                        tvStatus.setText("Download failed: no success status");
                        wrong.setVisibility(View.VISIBLE);
                        retry.setVisibility(View.VISIBLE);
                        retry.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
                            }
                        });
                        return;
                    }

                    final String path = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    tvStatus.setText("File download complete.\n\nLocation:\n" + path);
                    opn.setVisibility(View.VISIBLE);
                    done.setVisibility(View.VISIBLE);

                    opn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.parse(path), "image/*");
                            startActivity(intent);
                        }
                    });
                }
            }
        };
                /* register receiver to listen for ACTION_DOWNLOAD_COMPLETE action */
        registerReceiver(mDLCompleteReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));



    }




}
