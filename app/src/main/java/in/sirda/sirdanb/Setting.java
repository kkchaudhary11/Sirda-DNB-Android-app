package in.sirda.sirdanb;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

/**
 * Created by SpecTech on 9/17/2015.
 */
public class Setting extends AppCompatActivity {

    public static final String PREFS_NAME = "AOP_PREFS";
    public Switch mySwitch, mySwitch2;


    SharedPreferences settings;
    private RadioGroup study;
    private RadioButton b,d;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);


        settings = getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Boolean set = settings.getBoolean("push", true);
        Boolean vibration = settings.getBoolean("vib", true);
        Boolean st = settings.getBoolean("study", false);

        mySwitch = (Switch) findViewById(R.id.switch1);
        mySwitch2 = (Switch) findViewById(R.id.switch2);
        study = (RadioGroup)findViewById(R.id.radiogroup);
        b=(RadioButton)findViewById(R.id.btech);
        d=(RadioButton)findViewById(R.id.diploma);
        mySwitch.setChecked(set);

        mySwitch2.setChecked(vibration);

        if(st!=true)
        {
            b.setChecked(true);
            d.setChecked(false);
        }
        else {
            d.setChecked(true);
            b.setChecked(false);

        }


        //attach a listener to check for changes in state
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    settings = getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("push", true);
                    editor.commit();
                    MyGcmListenerService.Noti = true;

                } else {
                    settings = getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("push", false);
                    editor.commit();
                    MyGcmListenerService.Noti = false;
                }

            }
        });

        mySwitch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    settings = getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("vib", true);
                    editor.commit();
                    MyGcmListenerService.Vib = true;

                } else {
                    settings = getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("vib", false);
                    editor.commit();
                    MyGcmListenerService.Vib = false;
                }

            }
        });

        study.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                int radioButtonID = study.getCheckedRadioButtonId();
                View radioButton = study.findViewById(radioButtonID);
                int idx = study.indexOfChild(radioButton);

                if (idx==0) {
                    settings = getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("study", false);
                    editor.commit();


                } else {
                    settings = getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("study", true);
                    editor.commit();

                }
            }
        });


    }

    public void help(View v) {
        Intent help = new Intent(Setting.this, Help.class);
        startActivity(help);
    }

    public void reset(View v) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Reset to Default");
        alertDialogBuilder.setMessage("Are you sure, You want default Settings");

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Intent i = new Intent(Setting.this, Splash.class);
                startActivity(i);
                Toast.makeText(Setting.this, "Reset Successful", Toast.LENGTH_LONG).show();
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


}
