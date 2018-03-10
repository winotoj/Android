package com.ipd10.friendsapi;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;

public class AddEditActivity extends AppCompatActivity {

    private EditText etName, etAge;
    private Button btAddSave;

    private Friend currFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        btAddSave = findViewById(R.id.btAddSave);

        btAddSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Friend friend = currFriend != null ? currFriend : new Friend();
                // FIXME: require name 1-50 characters long
                friend.name = etName.getText().toString();
                // FIXME: handle parsing exception, require age to be 0-150 range
                friend.age = Integer.parseInt(etAge.getText().toString());
                if(currFriend != null){
                    new AsyncFriendUpdate().execute(friend);
                }else{
                    new AsyncFriendAdd().execute(friend);
                }

            }
        });

        long id = getIntent().getLongExtra(MainActivity.EXTRA_ID, -1);
        if (id != -1) {
            btAddSave.setText("Save");
            new AsyncFriendLoadById().execute(id);
        } else {
            btAddSave.setText("Add");
        }
    }

    class AsyncFriendLoadById extends AsyncTask<Long, Void, Friend> {

        private final static String TAG = "AsyncFriendsListLoad";

        @Override
        protected void onPostExecute(Friend result) {
            Log.v(TAG, "onPostExecute");
            if (result == null) {
                Toast.makeText(AddEditActivity.this, "API error fetching data",
                        Toast.LENGTH_LONG).show();
                finish();
            } else { // data fetched
                currFriend = result;
                etName.setText(result.name);
                etAge.setText(result.age + "");
            }

        }

        @Override
        protected Friend doInBackground(Long... longArray) {
            InputStreamReader input = null;
            JsonReader reader = null;
            long id = longArray[0];
            try {
                Log.v(TAG, "GET /friends/" + id);
                URL url = new URL(Globals.BASE_API_URL + "/friends/" + id);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                input = new InputStreamReader(connection.getInputStream());
                int httpCode = connection.getResponseCode();
                if (httpCode != 200) {
                    throw new IOException("Invalid HTTP code response: " + httpCode);
                }
                reader = new JsonReader(input);
                String key;
                Friend friend = new Friend();
                reader.beginObject();
                // id
                key = reader.nextName();
                if (!key.equals("id")) throw new ParseException("id expected when parsing", 0);
                friend.id = reader.nextInt();
                // name
                key = reader.nextName();
                if (!key.equals("name")) throw new ParseException("name expected when parsing", 0);
                friend.name = reader.nextString();
                // age
                key = reader.nextName();
                if (!key.equals("age")) throw new ParseException("age expected when parsing", 0);
                friend.age = reader.nextInt();
                //
                Log.v(TAG, "Friend fetched: " + friend.toString());
                reader.endObject();
                return friend;
            } catch (IOException | NumberFormatException | ParseException ex) {
                ex.printStackTrace();
                return null;
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }


    class AsyncFriendAdd extends AsyncTask<Friend, Void, Boolean> {

        private final static String TAG = "AsyncFriendAdd";

        @Override
        protected void onPostExecute(Boolean result) {
            Log.v(TAG, "onPostExecute");
            if (!result) {
                Toast.makeText(AddEditActivity.this, "API error adding record",
                        Toast.LENGTH_LONG).show();
                return;
            }
            Toast.makeText(AddEditActivity.this, "record added",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        protected Boolean doInBackground(Friend... friendArray) {
            Friend friend = friendArray[0];
            HttpURLConnection connection = null;

            try {
                Log.v(TAG, "POST /friends");
                URL url = new URL(Globals.BASE_API_URL + "/friends");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                // do not receive data, but send data
                connection.setDoInput(false);
                connection.setDoOutput(true);
                //
                PrintWriter printWriter = new PrintWriter(connection.getOutputStream());
                JSONObject jsonData = new JSONObject();
                jsonData.put("id", friend.id);
                jsonData.put("name", friend.name);
                jsonData.put("age", friend.age);
                printWriter.print(jsonData.toString());
                printWriter.flush();
                printWriter.close();

                int httpCode = connection.getResponseCode();
                if (httpCode != 201) {
                    throw new IOException("Invalid HTTP code response: " + httpCode);
                }
                return true;
            } catch (IOException | JSONException ex) {
                ex.printStackTrace();
                return false;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

        }
    }

    class AsyncFriendUpdate extends AsyncTask<Friend, Void, Boolean> {

        private final static String TAG = "AsyncFriendUpdate";

        @Override
        protected void onPostExecute(Boolean result) {
            Log.v(TAG, "onPostExecute");
            if (result) {
                Toast.makeText(AddEditActivity.this, "record updated",
                        Toast.LENGTH_SHORT).show();
                finish();
            }else{
                Toast.makeText(AddEditActivity.this, "API error updating record",
                        Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected Boolean doInBackground(Friend... friendArray) {
            Friend friend = friendArray[0];
            HttpURLConnection connection = null;

            try {
                Log.v(TAG, "PUT /friends" + friend.id);
                URL url = new URL(Globals.BASE_API_URL + "/friends/" + friend.id);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("PUT");
                // do not receive data, but send data
                connection.setDoInput(false);
                connection.setDoOutput(true);
                //
                PrintWriter printWriter = new PrintWriter(connection.getOutputStream());
                JSONObject jsonData = new JSONObject();
                jsonData.put("id", friend.id);
                jsonData.put("name", friend.name);
                jsonData.put("age", friend.age);
                printWriter.print(jsonData.toString());
                printWriter.flush();
                printWriter.close();

                int httpCode = connection.getResponseCode();
                if (httpCode != 200) {
                    throw new IOException("Invalid HTTP code response: " + httpCode);
                }
                return true;
            } catch (IOException | JSONException ex) {
                ex.printStackTrace();
                return false;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

        }
    }

}