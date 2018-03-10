package com.ipd10.q2traveldb;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class AddEditActivity extends AppCompatActivity {
    Database database;
    EditText etName;
    EditText etDestination;
    DatePicker dpDepartureDate;
    MenuItem menu_add;
    MenuItem menu_save;
    MenuItem menu_delete;
    int id;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        Intent intent = getIntent();
        id = intent.getIntExtra(MainActivity.EXTRA_MESSAGE, -1);
        //FIX-ME: ADD ERROR HANDLER IN CONSTRUCTOR
        database = new Database(this);
        etName = findViewById(R.id.etName);
        etDestination = findViewById(R.id.etDestination);
        dpDepartureDate = findViewById(R.id.dpDepartureDate);
        if (id != -1) {
            try {
                Travel travel = database.getTravel(id);
                etName.setText(travel.name);
                etDestination.setText(travel.destination);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(travel.departureDate);
                dpDepartureDate.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            } catch (DataStorageException ex) {
                ex.printStackTrace();
                Toast.makeText(this, "Database error fetching all records", Toast.LENGTH_LONG).show();
            }
        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_edit_delete_menu, menu);
        menu_add = (MenuItem) menu.findItem(R.id.menu_add);
        menu_save = (MenuItem) menu.findItem(R.id.menu_save);
        menu_delete = (MenuItem) menu.findItem(R.id.menu_delete);
        if(id == -1) {
            menu_save.setVisible(false);
            menu_delete.setVisible(false);
        }
        else{
            menu_add.setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_delete) {
            showDeleteDialog(id);
            Toast.makeText(this, "Delete Selected", Toast.LENGTH_SHORT).show();
            return true;
        }else if(item.getItemId() == R.id.menu_save || item.getItemId() == R.id.menu_add){

            Toast.makeText(this, "Save Selected", Toast.LENGTH_SHORT).show();
            try {
                addEdit(id);
            } catch (DataStorageException e) {
                e.printStackTrace();
                Toast.makeText(this, "Database error fetching all records", Toast.LENGTH_LONG).show();
            }
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void addEdit(int id) throws DataStorageException {
        String nameStr = etName.getText().toString();
        String destination = etDestination.getText().toString();
        if (nameStr.length()< 1 || nameStr.length()>50){
            showErrorDialog();
            return;
        } else if (destination.length()<1 || destination.length()>50){
            showErrorDialog();
            return;
        }
        String name = nameStr.substring(0, 1).toUpperCase() + nameStr.substring(1);
        int day = dpDepartureDate.getDayOfMonth();
        int month = dpDepartureDate.getMonth();
        int year = dpDepartureDate.getYear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        Date departureDate = calendar.getTime();

        Travel travel = new Travel(id, name, destination, departureDate);
        if (id == -1) {
            database.addTravel(travel);
            reset();
        }else{
            database.updateTravel(travel);
            finish();
        }
        //System.out.println(dueDate);
    }

    public void reset(){
        etName.setText("");
        etDestination.setText("");
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        dpDepartureDate.updateDate(year, month, day);

// set current date into datepicker
        dpDepartureDate.init(year, month, day, null);
    }

    private void showDeleteDialog(final int id){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.delete_dialog, null);

        dialogBuilder.setView(dialogView);
        final TextView textView = dialogView.findViewById(R.id.tvDelete);
        textView.setText(etName.getText().toString());
        dialogBuilder.setTitle("Delete a Travel");
        dialogBuilder.setMessage("Are you sure want to delete?");
        dialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                try {
                    database.deleteTravel(id);
                } catch (DataStorageException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Database error", Toast.LENGTH_LONG).show();
                }
                finish();
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
    private void showErrorDialog(){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.delete_dialog, null);

        dialogBuilder.setView(dialogView);
        final TextView textView = dialogView.findViewById(R.id.tvDelete);
        textView.setText(etName.getText().toString());
        dialogBuilder.setTitle("Error");
        dialogBuilder.setMessage("Name and Destination must be between 1 and 50 chars");
        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                finish();

            }
        });
        //dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        //    public void onClick(DialogInterface dialog, int whichButton) {
                //pass
        //    }
      //  });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

}