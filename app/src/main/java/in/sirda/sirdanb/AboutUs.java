package in.sirda.sirdanb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by SpecTech on 9/23/2015.
 */
public class AboutUs extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);
    }

    public void dev(View v) {
        Intent dev = new Intent(AboutUs.this, Developers.class);
        startActivity(dev);
    }
}
