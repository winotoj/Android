package com.ipd10.friendsapi;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_ID = "id";

    private ArrayList<Friend> friendsList = new ArrayList<>();
    private ArrayAdapter<Friend> adapter;

    private ListView lvFriends;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvFriends = findViewById(R.id.lvFriends);

        adapter = new ArrayAdapter<Friend>(this,
                android.R.layout.simple_list_item_1, friendsList);
        lvFriends.setAdapter(adapter);

        lvFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                Friend friend = friendsList.get(index);
                Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
                intent.putExtra(EXTRA_ID, friend.id);
                startActivity(intent);
            }
        });

        lvFriends.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int index, long l) {
                Friend friend = friendsList.get(index);
                showDeleteItemDialog(friend);
                return true;
            }
        });

    }

    private void showDeleteItemDialog(final Friend friend) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Confirm delete")
                .setMessage("Are you sure you want to delete\n" + friend.toString())
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        new AsyncFriendDelete().execute(friend.id);
                    }
                }).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        new AsyncFriendsListLoad().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.miAdd:
                Intent intent = new Intent(this, AddEditActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    class AsyncFriendDelete extends AsyncTask<Long, Void, Boolean> {

        private static final String TAG = "AsyncFriendDelete";

        @Override
        protected void onPostExecute(Boolean result) {
            Log.v(TAG, "onPostExecute");
            if (result) {
                Toast.makeText(MainActivity.this, "record deleted",
                        Toast.LENGTH_SHORT).show();
                // reload the list
                new AsyncFriendsListLoad().execute();
            } else {
                Toast.makeText(MainActivity.this, "API error deleting record",
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected Boolean doInBackground(Long... longArray) {
            long id = longArray[0];
            HttpURLConnection connection = null;
            try {
                Log.v(TAG, "DELETE /friends/" + id);
                URL url = new URL(Globals.BASE_API_URL + "/friends/" + id);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("DELETE");
                int httpCode = connection.getResponseCode();
                if (httpCode != 200) {
                    throw new IOException("Invalid HTTP code response: " + httpCode);
                }
                return true;
            } catch (IOException ex) {
                ex.printStackTrace();
                return false;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
    }


    class AsyncFriendsListLoad extends AsyncTask<Void, Void, ArrayList<Friend>> {

        private final static String TAG = "AsyncFriendsListLoad";

        @Override
        protected void onPostExecute(ArrayList<Friend> result) {
            Log.v(TAG, "onPostExecute");
            if (result == null) {
                Toast.makeText(MainActivity.this, "API error fetching data",
                        Toast.LENGTH_LONG).show();
                return;
            }

            friendsList.clear();
            for (Friend f : result) {
                friendsList.add(f);
            }
            adapter.notifyDataSetChanged();
        }

        @Override
        protected ArrayList<Friend> doInBackground(Void... voids) {
            InputStreamReader input = null;
            JsonReader reader = null;
            try {
                Log.v(TAG, "GET /friends");
                ArrayList<Friend> result = new ArrayList<>();
                URL url = new URL(Globals.BASE_API_URL + "/friends");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                input = new InputStreamReader(connection.getInputStream());
                int httpCode = connection.getResponseCode();
                if (httpCode != 200) {
                    throw new IOException("Invalid HTTP code response: " + httpCode);
                }
                reader = new JsonReader(input);
                reader.beginArray();
                while (reader.hasNext()) {
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
                    result.add(friend);
                    Log.v(TAG,"Friend added: " + friend.toString());
                    reader.endObject();
                }
                reader.endArray();
                return result;
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

}