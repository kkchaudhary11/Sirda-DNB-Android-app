package in.sirda.sirdanb;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NoticeDiploma extends AppCompatActivity {

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_DATA = "data";
    private static final String TAG_PID = "pid";
    private static final String TAG_SRNO = "srno";
    private static final String TAG_NAME = "name";
    private static final String TAG_DEPT = "dept";
    private static final String TAG_SUB = "sub";
    private static final String TAG_CAT = "cat";
    private static final String TAG_DATE = "date";
    String add = Constants.SrvAdd;
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
    ArrayList<HashMap<String, String>> dataList;
    // products JSONArray
    JSONArray products = null;

    ListView lv;
    private ProgressBar bar;
    // no notice or something went wrong
    TextView no;
    Button retry;
    // url to get all notice list
    String url_all_products;
    //view for load more
    View v;

    private Boolean flag = true;
    private Boolean flag2 = true;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // Hashmap for ListView
        dataList = new ArrayList<HashMap<String, String>>();
        bar = (ProgressBar) this.findViewById(R.id.progressBar);
        url_all_products = add + "diploma.php?srno=NULL";

        // Loading products in Background Thread
        new LoadAllProducts().execute();

        // Get listview
        lv = (ListView) findViewById(android.R.id.list);

        // on seleting single product
        // launching Edit Product Screen

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                // getting values from selected ListItem
                String pid = ((TextView) view.findViewById(R.id.pid)).getText()
                        .toString();


                if (!pid.isEmpty()) {
                    // Starting new intent
                    Intent in = new Intent(getApplicationContext(),
                            NoticeImage.class);
                    // sending pid to next activity
                    in.putExtra(TAG_PID, pid);

                    // starting new activity and expecting some response back
                    startActivityForResult(in, 100);
                } else {

                    TextView url= ((TextView) view.findViewById(R.id.name));
                    String urlstr=url.getText().toString();

                    if (!urlstr.contains("http://") && !urlstr.contains("https://")) {
                        Toast.makeText(NoticeDiploma.this, "Image Not Available", Toast.LENGTH_SHORT).show();
                    } else {
                        Linkify.addLinks(url, Linkify.WEB_URLS);
                    }

                }
            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                Log.v("long clicked", "pos: " + pos);
                return true;
            }
        });

        v = getLayoutInflater().inflate(R.layout.test, null);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LoadAllProducts().execute();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    // Response from Edit Product Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if result code 100
        if (resultCode == 100) {
            // if result code 100 is received
            // means user edited/deleted product
            // reload this screen again
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "NoticeBtech Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://in.sirda.sirdanb/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "NoticeBtech Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://in.sirda.sirdanb/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    //Background Async Task to Load all product by making HTTP Request
    class LoadAllProducts extends AsyncTask<String, String, String> {
        //Before starting background thread Show Progress Dialog
        @Override
        protected void onPreExecute() {
            bar.setVisibility(View.VISIBLE);
        }

        // getting All products from url

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            try {
                JSONObject json = jParser.makeHttpRequest(url_all_products, "GET", params);

                // Check your log cat for JSON reponse
                Log.d("All data: ", json.toString());
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    products = json.getJSONArray(TAG_DATA);

                    // looping through All Products
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_PID);
                        String srno = "#" + c.getString(TAG_SRNO);
                        String temp = c.getString(TAG_SRNO);
                        url_all_products = add + "student.php?srno=" + temp;

                        String name = c.getString(TAG_NAME);
                        String dept = c.getString(TAG_DEPT);
                        String sub = c.getString(TAG_SUB);
                        String cat = c.getString(TAG_CAT);

                        String date = c.getString(TAG_DATE);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_PID, id);
                        map.put(TAG_SRNO, srno);
                        map.put(TAG_NAME, name);
                        map.put(TAG_DEPT, dept);
                        map.put(TAG_SUB, sub);
                        map.put(TAG_CAT, cat);

                        map.put(TAG_DATE, date);

                        // adding HashList to ArrayList
                        dataList.add(map);
                    }

                } else {
                    flag2 = false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                flag = false;
                Log.e("error:", "connection timeout" + e.toString());
            }
            return null;
        }


        //After completing background task Dismiss the progress dialog

        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            bar.setVisibility(View.GONE);
            // updating UI from Background Thread
            if (!flag2) {
                v.setVisibility(View.GONE);
                no = (TextView) findViewById(R.id.no);
                String msg = "No Notice Found !";
                no.setText(msg);
            }
            if (!flag) {
                v.setVisibility(View.GONE);
                no = (TextView) findViewById(R.id.no);
                String msg = "Something Went Wrong !";
                no.setText(msg);

                retry = (Button) findViewById(R.id.retry);
                retry.setVisibility(View.VISIBLE);

                retry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                });
            }

            runOnUiThread(new Runnable() {
                public void run() {
                    // Updating parsed JSON data into ListView


                    ListAdapter adapter = new SimpleAdapter(
                            NoticeDiploma.this, dataList,
                            R.layout.list_item_main, new String[]{TAG_PID, TAG_SRNO,
                            TAG_NAME, TAG_DEPT, TAG_SUB, TAG_DATE},
                            new int[]{R.id.pid, R.id.srno, R.id.name, R.id.dept, R.id.sub, R.id.date,});
                    // updating listview
                    lv.addFooterView(v);
                    lv.setAdapter(adapter);
                }
            });
        }
    }
    //***********************
}
