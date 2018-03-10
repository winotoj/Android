package com.ipd10.preferenceactivity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by 1795661 on 1/16/2018.
 */

public class UserSettingActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // add the xml resource                     addPreferencesFromResource(R.xml.user_settings);
        addPreferencesFromResource(R.xml.user_setting);
    }

}
