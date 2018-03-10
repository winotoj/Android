package com.ipd10.tododb;

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
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class AddEditActivity extends AppCompatActivity {
    Database database;
    EditText etTask;
    Switch swDone;
    DatePicker dpDueDate;
    MenuItem menu_add;
    MenuItem menu_save;
    MenuItem menu_delete;
    long id;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        Intent intent = getIntent();
        id = intent.getLongExtra(MainActivity.EXTRA_MESSAGE, -1);
        //FIX-ME: ADD ERROR HANDLER IN CONSTRUCTOR
        database = new Database(this);
        etTask = findViewById(R.id.etTask);
        swDone = findViewById(R.id.swDone);
        dpDueDate = findViewById(R.id.dpDueDate);
        if (id != -1) {
            try {
                Todo todo = database.getTodo(id);
                etTask.setText(todo.task);
                swDone.setChecked(todo.isDone);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(todo.dueDate);
                dpDueDate.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            } catch (DataStorageException ex) {
                ex.printStackTrace();
                Toast.makeText(this, "Database error fetching all records", Toast.LENGTH_LONG).show();
            }
        }

/*
        swDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
               done = isChecked? 1:0;
           }
       });
*/
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

    public void addEdit(long id) throws DataStorageException {
        String task = etTask.getText().toString();
        boolean isDone = swDone.isChecked();
        int day = dpDueDate.getDayOfMonth();
        int month = dpDueDate.getMonth();
        int year = dpDueDate.getYear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        Date dueDate = calendar.getTime();

        Todo todo = new Todo(id, task, isDone, dueDate);
        if (id == -1) {
            database.addTodo(todo);
            reset();
        }else{
            database.updateTodo(todo);
            finish();
        }
        //System.out.println(dueDate);
    }

    public void reset(){
        etTask.setText("");
        swDone.setChecked(false);
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        dpDueDate.updateDate(year, month, day);

// set current date into datepicker
        dpDueDate.init(year, month, day, null);
    }

    private void showDeleteDialog(final long id){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.delete_dialog, null);

        dialogBuilder.setView(dialogView);
        final TextView textView = dialogView.findViewById(R.id.tvDelete);
        textView.setText(etTask.getText().toString());
        dialogBuilder.setTitle(R.string.title_delete);
        dialogBuilder.setMessage(R.string.msg_delete);
        dialogBuilder.setPositiveButton(R.string.btn_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                try {
                    database.deleteTodo(id);
                } catch (DataStorageException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Database error fetching all records", Toast.LENGTH_LONG).show();
                }
                finish();
                //do something with edt.getText().toString();
            }
        });
        dialogBuilder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }


}
