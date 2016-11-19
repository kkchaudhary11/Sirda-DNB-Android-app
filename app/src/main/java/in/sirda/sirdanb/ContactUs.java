package in.sirda.sirdanb;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by SpecTech on 9/23/2015.
 */
public class ContactUs extends AppCompatActivity {

    private TextView fb;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_us);
        fb = (TextView) findViewById(R.id.face);
        fb.setClickable(true);


    }

    public void dev(View v) {
        Intent dev = new Intent(ContactUs.this, Developers.class);
        startActivity(dev);
    }

    public void phno1(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:01907-262612"));
        startActivity(intent);
    }

    public void email(View view) {

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:sirda.edu@gmail.com"));

        intent.putExtra(Intent.EXTRA_SUBJECT, "Order for ");
        intent.putExtra(Intent.EXTRA_TEXT, "kuch bhinhi");
        startActivity(intent);

    }


}
