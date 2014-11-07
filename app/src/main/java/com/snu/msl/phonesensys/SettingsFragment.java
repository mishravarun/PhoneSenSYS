package com.snu.msl.phonesensys;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {
	EditTextPreference username;
	String user;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);
        username=(EditTextPreference)findPreference("username");
        username.setTitle("Username: " + user);
    }
}
