package in.sirda.sirdanb;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Feedback extends ActionBarActivity {

    String add = Constants.SrvAdd;
    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextReview;
    Button post;
    private ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);

        editTextName = (EditText) findViewById(R.id.feed_name);
        editTextEmail = (EditText) findViewById(R.id.feed_email);
        editTextReview = (EditText) findViewById(R.id.feed_review);
        bar = (ProgressBar) this.findViewById(R.id.progressBar);
        post= (Button)findViewById(R.id.Post);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString();
                String email = editTextEmail.getText().toString();
                String rev = editTextReview.getText().toString();

                if (TextUtils.isEmpty(name)) {
                    editTextName.setError("Please Enter Your Name");
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    editTextEmail.setError("Please Enter Email-Id");
                    return;
                }
                if (TextUtils.isEmpty(rev)) {
                    editTextReview.setError("Please Enter your Suggestion");
                    return;
                }

                insertToDatabase(name, email, rev);
            }
        });


    }


    private void insertToDatabase(String n, String e, String r) {

       final String name = n;
       final String email = e;
        final String rev = r;
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
        post.setVisibility(View.GONE);
                bar.setVisibility(View.VISIBLE);



            }
            @Override
            protected String doInBackground(String... params) {
                String paramUsername = params[0];
                String paramEmail = params[1];
                String paramReview = params[2];




                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("name", name));
                nameValuePairs.add(new BasicNameValuePair("email", email));
                nameValuePairs.add(new BasicNameValuePair("review", rev));


                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(add + "feedback.php");
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
                bar.setVisibility(View.GONE);
           post.setVisibility(View.VISIBLE);
                editTextName.setText("");
                editTextEmail.setText("");
                editTextReview.setText("");
                super.onPostExecute(result);

                Toast.makeText(getApplicationContext(), "ThankYou! Review successfully posted", Toast.LENGTH_LONG).show();
                TextView textViewResult = (TextView) findViewById(R.id.textViewResult);
                textViewResult.setText("Inserted");

            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(name, email, rev);


    }

}