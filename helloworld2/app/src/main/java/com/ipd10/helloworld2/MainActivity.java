package com.ipd10.helloworld2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    //binding
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //inflating the xml file (instantiate the object, set the properties from the xml)
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.editText);
    }
    /** Called when the user taps the Send button */
    public void sendMessage(View view) {
        //sender is this and recipient is displaymessageactivity
        //to create activiy (always with ending activity)
        //do not create class but activity
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}
