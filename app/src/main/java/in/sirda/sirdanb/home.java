package in.sirda.sirdanb;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class home extends ActionBarActivity {
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    Boolean  pref;
    TextView study;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        study=(TextView)findViewById(R.id.study);
        LoadPreferences();
        cd = new ConnectionDetector(getApplicationContext());

    }

    private void LoadPreferences(){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("AOP_PREFS", Context.MODE_PRIVATE);
        pref = sharedPreferences.getBoolean("study", false) ;
        if(pref==false){
            study.setText("B.Tech");
        }
        else {
            study.setText("Diploma");
        }
    }

    public void info(View v){
        Toast.makeText(home.this,"Change your Preferences on Settings",Toast.LENGTH_LONG).show();
    }

    public void admin_login(View v) {
        Intent adminlogin = new Intent(home.this, AdminLogin.class);
        startActivity(adminlogin);
    }

    public void mainView(View v) {
        // get Internet status

        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (isInternetPresent) {
            // Internet Connection is Present
            // make HTTP requests
            if(!pref){
                Intent main = new Intent(home.this, NoticeBtech.class);
                startActivity(main);
            }
            else{
                Intent diplma = new Intent(home.this,NoticeDiploma.class);
                startActivity(diplma);
            }


        } else {
            // Internet connection is not present
            // Ask user to connect to Internet
            showAlertDialog(home.this, "No Internet Connection!", "Please check your Internet connection and Try Again.", false);
        }


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case R.id.reg:
                Intent r = new Intent(this, Register.class);
                startActivity(r);
                return true;
            case R.id.feedback:
                Intent i = new Intent(this, Feedback.class);
                startActivity(i);
                return true;
            case R.id.exit:
                finish();
                return true;
            case R.id.action_settings:
                Intent s = new Intent(this, Setting.class);
                startActivity(s);
                return true;
            case R.id.about_us:
                Intent about = new Intent(this, AboutUs.class);
                startActivity(about);
                return true;
            case R.id.contact_us:
                Intent contact = new Intent(this, ContactUs.class);
                startActivity(contact);
                return true;
            case R.id.help:
                Intent help = new Intent(this, Help.class);
                startActivity(help);
                return true;
            case R.id.web_version:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.sirdanb.in"));
                startActivity(browserIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
