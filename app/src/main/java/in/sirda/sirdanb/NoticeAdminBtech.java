package in.sirda.sirdanb;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NoticeAdminBtech extends AppCompatActivity {

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // Hashmap for ListView
        dataList = new ArrayList<HashMap<String, String>>();
        bar = (ProgressBar) this.findViewById(R.id.progressBar);
        url_all_products = add + "btech.php?srno=NULL";


        // Loading products in Background Thread
        new LoadAllProducts().execute();

        // Get listview
        lv = (ListView) findViewById(android.R.id.list);

        // on seleting single product
        // launching Edit Product Screen
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // getting values from selected ListItem
                String pid = ((TextView) view.findViewById(R.id.pid)).getText().toString();
                final String srno = ((TextView) view.findViewById(R.id.srno)).getText().toString();
                    // Starting new intent
                    Intent in = new Intent(getApplicationContext(), NoticeAdminImageBtech.class);
                    Bundle extras = new Bundle();
                    extras.putString(TAG_PID,pid);
                    extras.putString(TAG_SRNO, srno);
                    in.putExtras(extras);

                    // starting new activity and expecting some response back
                    startActivityForResult(in, 100);



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
                        String srno = c.getString(TAG_SRNO);
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
                    flag2=false;
                }
            }
            catch (JSONException e) {
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
            if (!flag2){
                v.setVisibility(View.GONE);
                no = (TextView)findViewById(R.id.no);
                String msg="No Notice Found !";
                no.setText(msg);
            }
            if (!flag) {
                v.setVisibility(View.GONE);
                no = (TextView)findViewById(R.id.no);
                String msg="Something Went Wrong !";
                no.setText(msg);

                retry = (Button)findViewById(R.id.retry);
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
                            NoticeAdminBtech.this, dataList,
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
