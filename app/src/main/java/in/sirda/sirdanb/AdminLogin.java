package in.sirda.sirdanb;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class AdminLogin extends ActionBarActivity {
    Button b;
    EditText et, pass;
    TextView tv;
    HttpPost httppost;
    StringBuffer buffer;
    HttpResponse response;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;
    ProgressDialog dialog = null;
    String add = Constants.SrvAdd;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_login);


        b = (Button) findViewById(R.id.Button01);
        et = (EditText) findViewById(R.id.username);
        pass = (EditText) findViewById(R.id.password);
        tv = (TextView) findViewById(R.id.tv);


        cd = new ConnectionDetector(getApplicationContext());


        b.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                String eet = et.getText().toString();
                String pass1 = pass.getText().toString();
                if (TextUtils.isEmpty(eet)) {
                    et.setError("Please Enter Username");
                    return;
                }
                if (TextUtils.isEmpty(pass1)) {
                    pass.setError("Please Enter Password");
                    return;
                }


                isInternetPresent = cd.isConnectingToInternet();

                // check for Internet status
                if (isInternetPresent) {
                    dialog = ProgressDialog.show(AdminLogin.this, "",
                            "Validating user...", true);
                    new Thread(new Runnable() {
                        public void run() {
                            login();
                        }
                    }).start();

                } else {
                    // Internet connection is not present
                    // Ask user to connect to Internet
                    showAlertDialog(AdminLogin.this, "No Internet Connection!",
                            "Please check your Internet connection and Try Again.", false);
                }


            }
        });

    }


    void login() {
        try {
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost(add + "login.php"); // make sure the url is correct.
            //add your data
            nameValuePairs = new ArrayList<NameValuePair>(2);
            // Always use the same variable name for posting i.e the android side variable name and php side variable name should be similar,
            nameValuePairs.add(new BasicNameValuePair("username", et.getText().toString().trim()));  // $Edittext_value = $_POST['Edittext_value'];
            nameValuePairs.add(new BasicNameValuePair("password", pass.getText().toString().trim()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            //Execute HTTP Post Request
            response = httpclient.execute(httppost);
            // edited by James from coderzheaven.. from here....
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);

            System.out.println("Response : " + response);
            runOnUiThread(new Runnable() {
                public void run() {
                    tv.setText("Response from PHP : " + response);
                    dialog.dismiss();
                }
            });

            if (response.substring(0, 10).equalsIgnoreCase("User Found")) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(AdminLogin.this, "Login Success", Toast.LENGTH_SHORT).show();


                    }
                });

                startActivity(new Intent(AdminLogin.this, aAccount.class));
            } else {
                showAlert();
            }

        } catch (Exception e) {
            dialog.dismiss();
            System.out.println("Exception : " + e.getMessage());
        }
    }

    public void showAlert() {
        AdminLogin.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminLogin.this);
                builder.setTitle("Login Error!");
                builder.setMessage("Incorrect Username/Password Please recheck and try again")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }


    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


}





