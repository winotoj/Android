package com.ipd10.sandwich2;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {
    private SeekBar seekBar;
    EditText etName;
    TextView tvTemp;
    CheckBox cbCheese;
    CheckBox cbSalt;
    RadioButton rbWhite;
    RadioButton rbWheat;
    String orderStr;
    String rbStr;
    public static final String EXTRA_MESSAGE = "com.ipd10.sandwich2.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etName = (EditText) findViewById(R.id.etName);
        seekBar = (SeekBar)findViewById(R.id.sbTemp);
        tvTemp = (TextView)findViewById(R.id.tvTemp);
        seekBar.setProgress(20);
        seekBar.incrementProgressBy(5);
        rbWhite = (RadioButton) findViewById(R.id.rbWhite);
        rbWheat = (RadioButton) findViewById(R.id.rbWheat);
        cbCheese = (CheckBox) findViewById(R.id.cbCheese);
        cbSalt = (CheckBox) findViewById(R.id.cbSalt);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress += 20;
                tvTemp.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.rbWhite:
                if (checked)
                    rbStr = " White Bread ";
                    break;
            case R.id.rbWheat:
                if (checked)
                    rbStr = " Whole Wheat ";
                    break;
        }
    }

    public void onButtonOrderClicked(View view){

        orderStr = etName.getText().toString() + " " + tvTemp.getText().toString()+ rbStr;
        if(cbSalt.isChecked())
            orderStr += " Salt ";
        if (cbCheese.isChecked())
            orderStr += " Cheese";
        Context context = getApplicationContext();
        Toast.makeText(context, orderStr, LENGTH_SHORT).show();
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        intent.putExtra(EXTRA_MESSAGE, orderStr);
        startActivity(intent);



    }
}
