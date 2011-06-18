package com.nsu.smsbackup;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * @author andy
 */
public class Preferences extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }
}
