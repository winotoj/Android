package com.ipd10.q2traveldb;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
    private static final int SETTINGS_RESULT = 1;
    ListView lvTravels;
    Database database;
    ArrayList<Travel> travelsArrayList;
    ArrayAdapter<Travel> adapter;
    String sortBy;
    public static final String EXTRA_MESSAGE = "ID";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sortBy = "name";
        database = new Database(this);
        try {
            refreshList();
        } catch (DataStorageException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error retrieving data", Toast.LENGTH_LONG).show();
        }
        //list view onclick
        lvTravels.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this,String.format("Item #%d clicked: %s", i, travelsArrayList.get(i)), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(adapterView.getContext(), AddEditActivity.class);
                intent.putExtra(EXTRA_MESSAGE, travelsArrayList.get(i).id);
                //intent.putExtra(EXTRA_MESSAGE, String.valueOf(travelsArrayList.get(i)._id));
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
        lvTravels = (ListView) findViewById(R.id.lvTravels);
        travelsArrayList = database.getAllTravels(sortBy);
        if (travelsArrayList != null) {
            adapter = new ArrayAdapter<Travel>(this, android.R.layout.simple_list_item_1, travelsArrayList);
            lvTravels.setAdapter(adapter);
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

        if (item.getItemId() == R.id.add_menu_main) {
            Toast.makeText(this, "Add Selected", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, AddEditActivity.class);
            startActivity(intent);
            return true;
        }
        else if(item.getItemId() == R.id.setting_main_menu){
            Intent i = new Intent(getApplicationContext(), UserSettingActivity.class);
            startActivityForResult(i, SETTINGS_RESULT);
            return true;
        }
        else
            {
            return super.onOptionsItemSelected(item);
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==SETTINGS_RESULT)
        {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

            sortBy = sharedPrefs.getString("prefSortSetting", "name");

        }

    }
}