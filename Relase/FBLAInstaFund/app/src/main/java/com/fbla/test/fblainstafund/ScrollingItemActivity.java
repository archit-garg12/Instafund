package com.fbla.test.fblainstafund;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ScrollingItemActivity extends AppCompatActivity {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_scrolling_item);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//    }


    private static final String TAG = "RecyclerViewExample";

    private List<FeedItem> feedsList;
    private RecyclerView mRecyclerView;
    private MyRecyclerViewAdapter adapter;
    private ProgressBar progressBar;
    private final int GETITEMLIST = 1;
    private final int DELETE = 2;
    private final int BUY = 3;
    private final int ADD_ITEM = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        String url = FBLAUtility.URL+"/v1/items";
        new DownloadTask(GETITEMLIST).execute(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_scrolling_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                // User chose the "Refresh", fetch the List of Items from server...

                String url = FBLAUtility.URL+"/v1/items";
                new DownloadTask(GETITEMLIST).execute(url);

                return true;

            case R.id.action_add:
                // User chose the "Add New Item" action, mark the current item
                // as a favorite...
                Snackbar.make(this.findViewById(R.id.action_add), "Action Add", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();

                Intent intent = new Intent(ScrollingItemActivity.this, AddItemActivity.class);
                startActivityForResult(intent, ADD_ITEM);

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADD_ITEM){
            if(resultCode == RESULT_OK)
                getItemList();
        }
    }


    void getItemList(){
        String url = FBLAUtility.URL+"/v1/items";
        new DownloadTask(GETITEMLIST).execute(url);
    }

    public class DownloadTask extends AsyncTask<String, Void, Integer> {
        int mState;
        DownloadTask(int state){
            mState = state;
        }
        @Override
        protected void onPreExecute() {

            switch(mState) {
                case GETITEMLIST: mRecyclerView.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    break;
            }
        }

        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;
            HttpURLConnection urlConnection = null ;
            try {
                URL url;
                int statusCode = 0 ;

                switch (mState) {
                    case GETITEMLIST:
                        url = new URL(params[0]);
                        urlConnection = (HttpURLConnection) url.openConnection();
                        statusCode = urlConnection.getResponseCode();

                        Log.d(TAG, "Response code: " + statusCode);
                        break;

                    case BUY:
                    case DELETE:
                        url = new URL(params[0]);

                        urlConnection = (HttpURLConnection) url.openConnection();

                        urlConnection.setReadTimeout(5000);
                        // Timeout for connection.connect() arbitrarily set to 3000ms.
                        urlConnection.setConnectTimeout(5000);
                        // For this use case, set HTTP method to GET.
                        urlConnection.setRequestMethod("DELETE");
                        // Already true by default but setting just in case; needs to be true since this request
                        // is carrying an input (response) body.
                        urlConnection.setDoInput(true);

                        statusCode = urlConnection.getResponseCode();


                        break;
                }

                // 200 represents HTTP OK
                if (statusCode == 200) {
                    StringBuilder response;
                    String line;
                    switch (mState) {
                        case GETITEMLIST:

                            BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                            response = new StringBuilder();

                            while ((line = r.readLine()) != null) {
                                response.append(line);
                            }
                            Log.d(TAG, "response: " + response.toString());
                            parseResult(response.toString());

                            result = 1; // Successful
                            break;

                        case DELETE:

                            result = 1; // Successful
                            Snackbar.make(ScrollingItemActivity.this.findViewById(R.id.delete), "Item Deleted successfully", Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();


                            break;

                        case BUY:

                            result = 1; // Successful
                            Snackbar.make(ScrollingItemActivity.this.findViewById(R.id.buy), "Item can be picked up at SRV USD Inc.", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            break;
                    }

                } else {
                    result = 0; //"Failed to fetch data!";
                }
            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }
            return result; //"Failed to fetch data!";
        }

        @Override
        protected void onPostExecute(Integer result) {
            progressBar.setVisibility(View.GONE);

            if (result == 1) {
                adapter = new MyRecyclerViewAdapter(ScrollingItemActivity.this, feedsList);
                mRecyclerView.setVisibility(View.VISIBLE);
                mRecyclerView.setAdapter(adapter);

                switch (mState) {
                    case BUY:
                    case DELETE:
                        String url = FBLAUtility.URL+"/v1/items";
                        new DownloadTask(GETITEMLIST).execute(url);
                        break;

                }
                adapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(FeedItem item, int state) {
                        if(MyRecyclerViewAdapter.COMMENT == state ) {
                            Toast.makeText(ScrollingItemActivity.this, "COMMENT comming soon", Toast.LENGTH_LONG).show();
                        } else if(MyRecyclerViewAdapter.BUY == state ) {
//                            Toast.makeText(ScrollingItemActivity.this, "BUY ", Toast.LENGTH_LONG).show();
                            String url = FBLAUtility.URL+"/v1/items/" + item.getItemId();
                            new DownloadTask(BUY).execute(url);

                        } else if(MyRecyclerViewAdapter.DELETE == state) {

                            String url = FBLAUtility.URL+"/v1/items/" + item.getItemId();
                            new DownloadTask(DELETE).execute(url);
                        }

                    }
                });

            } else {
                Toast.makeText(ScrollingItemActivity.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();

            }
        }
    }

    /**
     * [
     * { "UserId": "undefined",
     * "ItemName": "454245",
     * "ItemPrice": "245245$",
     * "itemState": "New",
     * "ItemImage": "iVBORw0KGgoAAAANSUh",
     * "ItemDescription": "224524",
     * "ItemId": "d5acc800-e663-11e6-b487-254bd87b0bdf"
     * },
     * { "UserId": "1",
     * "ItemName": "aaaaaa",
     * "ItemPrice": "350$",
     * "itemState": "aaaaa",
     * "ItemImage": "asdasdaasdaeqeqeadadadasdasdadasdasdsadasdasdasd",
     * "ItemDescription": "asdaads asdads asdads",
     * "ItemId": "89afa0a0-edb9-11e6-b487-254bd87b0bdf"
     * }
     * ]
     **/

    /**
     *
     * @param result
     */

    private void parseResult(String result) {

//       String aresult = "[{ \"UserId\": \"1\", \"ItemName\": \"aaaaaa\", \"ItemPrice\": \"350$\", \"itemState\": \"aaaaa\", \"ItemImage\": \"asdasdaasdaeqeqeadadadasdasdadasdasdsadasdasdasd\", \"ItemDescription\": \"asdaads asdads asdads\", \"ItemId\": \"89afa0a0-edb9-11e6-b487-254bd87b0bdf\" } ]";
        Log.d(TAG, "result: " + result);
        try {

            JSONArray responseItemList = new JSONArray(result);
            feedsList = new ArrayList<>();

            for (int i = 0; i < responseItemList.length(); i++) {
                JSONObject post = responseItemList.optJSONObject(i);
                FeedItem item = new FeedItem();
                item.setItemUserID(post.getString("UserId"));
                item.setThumbnail(post.getString("ItemImage"));
                item.setItemName(post.getString("ItemName"));
                item.setItemPrice(post.getString("ItemPrice"));
                item.setItemState(post.getString("itemState"));
                item.setItemDescription(post.getString("ItemDescription"));
                item.setItemId(post.getString("ItemId"));

                feedsList.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
