package com.ipd10.tododb;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView lvTodos;
    Database database;
    ArrayList<Todo> todosArrayList;
    ArrayAdapter<Todo> adapter;
    public static final String EXTRA_MESSAGE = "ID";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = new Database(this);
        try {
            refreshList();
        } catch (DataStorageException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error retrieving data", Toast.LENGTH_LONG).show();
        }
        //list view onclick
        lvTodos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               Toast.makeText(MainActivity.this,String.format("Item #%d clicked: %s", i, todosArrayList.get(i)), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(adapterView.getContext(), AddEditActivity.class);
                intent.putExtra(EXTRA_MESSAGE, todosArrayList.get(i)._id);
                //intent.putExtra(EXTRA_MESSAGE, String.valueOf(todosArrayList.get(i)._id));
                startActivity(intent);
            }
        });
    }
   //reload main activity
    @Override
    protected void onStart()
    {
        super.onStart();
        try {
            refreshList();
        } catch (DataStorageException e) {
            e.printStackTrace();
        }

    }

    //refresh list
    public void refreshList() throws DataStorageException {
        lvTodos = (ListView) findViewById(R.id.lvTodos);
        todosArrayList = database.getAllTodos();
        if (todosArrayList != null) {
            adapter = new ArrayAdapter<Todo>(this, android.R.layout.simple_list_item_1, todosArrayList);
            lvTodos.setAdapter(adapter);
        }
        adapter.notifyDataSetChanged();
    }
    //menu
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    //Menu clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_add) {
            Toast.makeText(this, "Add Selected", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, AddEditActivity.class);
            startActivity(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }


    }
}
