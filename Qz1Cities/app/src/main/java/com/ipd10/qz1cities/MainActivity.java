package com.ipd10.qz1cities;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {
    ListView lvCities;
    ArrayList<City> citiesList = new ArrayList<>();
    ArrayAdapter<City> adapter;
    public static final String EXTRA_MESSAGE = "com.ipd10.qz1cities.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvCities = (ListView) findViewById(R.id.lvCities);
        String [] cityArray = {"montreal", "toronto", "ottawa"};
        double [] pop ={1.5, 6, 1};
       for(int i=0; i<cityArray.length; i++){
           City city = new City();
           city.name = cityArray[i];
           city.popMil = pop[i];
           citiesList.add(city);
       }
       adapter = new ArrayAdapter<City>(this, android.R.layout.simple_list_item_1,citiesList);
        lvCities.setAdapter(adapter);
        lvCities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this,String.format("Item #%d clicked: %s", i, citiesList.get(i)), Toast.LENGTH_SHORT).show();

                showAddEditDialog(i);
            }
        });

        lvCities.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                Toast.makeText(MainActivity.this,String.format("Item #%d long clicked:", i), Toast.LENGTH_SHORT).show();

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
        double biggest = 0;
        String cityName = "";
        double totalPop = 0;

        if(item.getItemId() == R.id.menu_add){
            //Toast.makeText(this, "Add Selected", Toast.LENGTH_SHORT).show();
            showAddEditDialog(-1);
            return true;
        }else if(item.getItemId() == R.id.menu_summary){
            City city = new City();

            for (City c: citiesList){
                if(c.popMil > biggest){
                    biggest = c.popMil;
                    cityName = c.name;
                }
                totalPop += c.popMil;

                //biggest = c.popMil>biggest? c.popMil: biggest;
            }
            Context context = getApplicationContext();

           Intent intent = new Intent(this, SummaryActivity.class);
           String summary = "The largest city is " + cityName + '\n' + " All " + citiesList.size() + "cities together have population of " + totalPop + " Millions";

           // Toast.makeText(context, summary, LENGTH_SHORT).show();
            intent.putExtra(EXTRA_MESSAGE, summary);
            startActivity(intent);
            return true;
        }
        else{
            return super.onOptionsItemSelected(item);
        }

    }
    private void showAddEditDialog(final int id) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.addedit_dialog, null);
        dialogBuilder.setView(dialogView);
        final EditText etCity = (EditText) dialogView.findViewById(R.id.etCity);
        final EditText etPop = (EditText) dialogView.findViewById(R.id.etPop);

        if(id < 0){
            dialogBuilder.setTitle("Add City");
            dialogBuilder.setMessage("Enter City name population below below");
            dialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    City city = new City();
                    String name = etCity.getText().toString();
                    try{
                        double population = Double.parseDouble(etPop.getText().toString());
                        city.name = name;
                        city.popMil = population;
                        citiesList.add(city);
                        adapter.notifyDataSetChanged();
                    } catch(NumberFormatException e){
                        Context context = getApplicationContext();
                        Toast.makeText(context, "Error Parsing" + e, Toast.LENGTH_SHORT).show();
                    }


                    //do something with edt.getText().toString();
                }
            });

        }else{
            dialogBuilder.setTitle("Edit city");
            dialogBuilder.setMessage("Edit City name  and/ or populationbelow");

            String cityStr = citiesList.get(id).toString();
            String[] outputFirst = cityStr.split(" - ");
            String popStr = outputFirst[1];
            String[] outputSecond = popStr.split(" ");
            String name = outputFirst[0];
            String pop = outputSecond[0];
            etCity.setText(name);
            etPop.setText(pop);
            dialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    City city = new City();
                    city.name = etCity.getText().toString();
                    try{
                        city.popMil = Double.parseDouble(etPop.getText().toString());
                        citiesList.set(id, city);
                        adapter.notifyDataSetChanged();
                    } catch(NumberFormatException e){
                        Context context = getApplicationContext();
                        Toast.makeText(context, "Error Parsing" + e, Toast.LENGTH_SHORT).show();
                    }

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
        edt.setText(citiesList.get(i).toString());

        dialogBuilder.setTitle("Delete Cities");
        dialogBuilder.setMessage("Are you sure you want to delete?");
        dialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                citiesList.remove(i);
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
    // if index is -1 then we're adding, 0 or more - we're editing an item friendList.get(index)


class City {
    String name;
    double popMil; // population in millions
    @Override
    public String toString() {
        return String.format("%s - %.3f millions", name, popMil);
    }
}