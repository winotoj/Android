package com.ipd10.todoapi;

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
    private ArrayList<Todo> todosList = new ArrayList<>();
    private ArrayAdapter<Todo> adapter;

    private ListView lvTodos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvTodos = findViewById(R.id.lvTodos);
        adapter = new ArrayAdapter<Todo>(this,
                android.R.layout.simple_list_item_1, todosList);
        lvTodos.setAdapter(adapter);

        lvTodos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                Todo todo = todosList.get(index);
                Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
                intent.putExtra(EXTRA_ID, todo.id);
                startActivity(intent);
            }
        });

        lvTodos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int index, long l) {
                Todo todo = todosList.get(index);
                showDeleteItemDialog(todo);
                return true;
            }
        });
    }
    private void showDeleteItemDialog(final Todo todo) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Confirm delete")
                .setMessage("Are you sure you want to delete\n" + todo.toString())
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        new AsyncTodoDelete().execute(todo.id);
                    }
                }).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        new AsyncTodosListLoad().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add_main_menu:
                Intent intent = new Intent(this, AddEditActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    class AsyncTodoDelete extends AsyncTask<Long, Void, Boolean> {

        private static final String TAG = "AsyncTodoDelete";

        @Override
        protected void onPostExecute(Boolean result) {
            Log.v(TAG, "onPostExecute");
            if (result) {
                Toast.makeText(MainActivity.this, "record deleted",
                        Toast.LENGTH_SHORT).show();
                // reload the list
                new AsyncTodosListLoad().execute();
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
                Log.v(TAG, "DELETE /todos/" + id);
                URL url = new URL(Globals.BASE_API_URL + "/todos/" + id);
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


    class AsyncTodosListLoad extends AsyncTask<Void, Void, ArrayList<Todo>> {

        private final static String TAG = "AsyncTodosListLoad";

        @Override
        protected void onPostExecute(ArrayList<Todo> result) {
            Log.v(TAG, "onPostExecute");
            if (result == null) {
                Toast.makeText(MainActivity.this, "API error fetching data",
                        Toast.LENGTH_LONG).show();
                return;
            }

            todosList.clear();
            for (Todo f : result) {
                todosList.add(f);
            }
            adapter.notifyDataSetChanged();
        }

        @Override
        protected ArrayList<Todo> doInBackground(Void... voids) {
            InputStreamReader input = null;
            JsonReader reader = null;
            try {
                Log.v(TAG, "GET /todos");
                ArrayList<Todo> result = new ArrayList<>();
                URL url = new URL(Globals.BASE_API_URL + "/todos");
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
                    Todo todo = new Todo();
                    reader.beginObject();
                    // id
                    key = reader.nextName();
                    if (!key.equals("id")) throw new ParseException("id expected when parsing", 0);
                    todo.id = reader.nextInt();
                    // Task
                    key = reader.nextName();
                    if (!key.equals("task")) throw new ParseException("Task expected when parsing", 0);
                    todo.task = reader.nextString();
                    // isDone
                    key = reader.nextName();
                    if (!key.equals("isDone")) throw new ParseException("Status expected when parsing", 0);
                    todo.isDone = reader.nextBoolean();
                    //dueDate
                    key = reader.nextName();
                    if (!key.equals("dueDate")) throw new ParseException("Status expected when parsing", 0);
                    todo.dueDate = Todo.dateFormat.parse(reader.nextString());
                    result.add(todo);
                    Log.v(TAG,"Todo added: " + todo.toString());
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