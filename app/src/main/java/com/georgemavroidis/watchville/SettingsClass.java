package com.georgemavroidis.watchville;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by george on 14-11-18.
 */
public class SettingsClass extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setlayout);
    }
}
