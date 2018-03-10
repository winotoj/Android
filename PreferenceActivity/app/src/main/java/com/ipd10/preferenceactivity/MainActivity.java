package com.ipd10.preferenceactivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int SETTINGS_RESULT = 1;
    Button settingsButton;
    TextView textViewSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settingsButton=(Button)findViewById(R.id.buttonSettings);
        textViewSetting = (TextView) findViewById(R.id.textViewSettings);
        // start the UserSettingActivity when user clicks on Button
        settingsButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(getApplicationContext(), UserSettingActivity.class);
                startActivityForResult(i, SETTINGS_RESULT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==SETTINGS_RESULT)
        {
            displayUserSettings();
        }

    }


    private void displayUserSettings()
    {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String  settings = "";

        settings=settings+"Password: " + sharedPrefs.getString("prefUserPassword", "NOPASSWORD");

        settings=settings+"\nRemind For Update:"+ sharedPrefs.getBoolean("prefLockScreen", false);

        settings=settings+"\nUpdate Frequency: "
                + sharedPrefs.getString("prefUpdateFrequency", "NOUPDATE");

        textViewSetting.setText(settings);
    }
}
