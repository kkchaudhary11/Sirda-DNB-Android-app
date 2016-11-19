package in.sirda.sirdanb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by SpecTech on 9/14/2015.
 */
public class aAccount extends AppCompatActivity {

    Boolean  pref;
    TextView study;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_account);
        study=(TextView)findViewById(R.id.study);
        LoadPreferences();

    }

    public void adminLogin(View v) {
        Intent create = new Intent(aAccount.this, aCreate.class);
        startActivity(create);
    }

    public void feedback(View v) {
        Intent feedback = new Intent(aAccount.this, GetFeedback.class);
        startActivity(feedback);
    }

    public void viewNotice(View v) {
        if(!pref){
            Intent main = new Intent(aAccount.this, NoticeAdminBtech.class);
            startActivity(main);
        }
        else{
            Intent diplma = new Intent(aAccount.this,NoticeAdminDiploma.class);
            startActivity(diplma);
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_admin_account, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case R.id.logout:
                Intent logout = new Intent(aAccount.this, home.class);
                Toast.makeText(aAccount.this, "Logged Out", Toast.LENGTH_SHORT).show();
                startActivity(logout);

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
