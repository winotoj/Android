package com.ipd10.tempcomp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText etCelcius;
    private TextView tvFahrenheit;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etCelcius = (EditText) findViewById(R.id.etCelcius);
        tvFahrenheit = (TextView) findViewById(R.id.tvFahrenheit);
        context  = getApplicationContext();
    }
// search about toast, tobe added for try parsing
    public void convertTemp(View view){

        String celStr = etCelcius.getText().toString();
        try{
            Double cel = Double.parseDouble(celStr);
            String fah = String.format("%.2f", cel * 9 /5 + 32);
            tvFahrenheit.setText(fah);
        }catch (NumberFormatException ex){
            Toast.makeText(this, "Must be  floating point", Toast.LENGTH_SHORT).show();
        }
        etCelcius.setText("");

    }

}
