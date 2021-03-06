package com.ipd10.friendlist;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView lvFriends;
    int id = 0;
    ArrayList<String> friendsList = new ArrayList<>();
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvFriends = (ListView) findViewById(R.id.lvFriends);

        String[]friendsArray ={"Jerry", "Terry", "Merry", "Larry", "Joe"};

        for(String s: friendsArray){
            friendsList.add(s);
        }

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, friendsList);
        lvFriends.setAdapter(adapter);

        lvFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this,String.format("Item #%d clicked: %s", i, friendsList.get(i)), Toast.LENGTH_SHORT).show();
                id = i;
                showAddEditDialog();
            }
        });

        lvFriends.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                Toast.makeText(MainActivity.this,String.format("Item #%d long clicked:", i), Toast.LENGTH_SHORT).show();
                //friendsList.remove(i);
                showDeleteDialog(i);
                adapter.notifyDataSetChanged();
                return false;
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.menu_add){
            //Toast.makeText(this, "Add Selected", Toast.LENGTH_SHORT).show();
            showAddEditDialog();
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }

    }
    private void showAddEditDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.addedit_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);
        if(id ==0){
        dialogBuilder.setTitle("Add Friend");
        dialogBuilder.setMessage("Enter friend name below");
        dialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String name = edt.getText().toString();
                friendsList.add(name);
                adapter.notifyDataSetChanged();
                //do something with edt.getText().toString();
            }
        });

        }else{
            dialogBuilder.setTitle("Edit Friend");
            dialogBuilder.setMessage("Edit friend name below");
            edt.setText(friendsList.get(id));
            dialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String name = edt.getText().toString();
                            friendsList.set(id, name);
                            adapter.notifyDataSetChanged();
                            //do something with edt.getText().toString();
                        }
                    });
        }


        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }
    private void showDeleteDialog(final int i){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.delete_dialog, null);
        dialogBuilder.setView(dialogView);

        final TextView edt = (TextView) dialogView.findViewById(R.id.tvdelete);
        edt.setText(friendsList.get(i));

        dialogBuilder.setTitle("Delete Friend");
        dialogBuilder.setMessage("Are you sure you want to delete?");
        dialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                friendsList.remove(i);
                adapter.notifyDataSetChanged();
                //do something with edt.getText().toString();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }
}
