package com.snu.msl.phonesensys;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;

public class Settings extends Activity {

	EditTextPreference username;
	String user;
	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
	  // TODO Auto-generated method stub
	  super.onCreate(savedInstanceState);
         SharedPreferences prefs = this.getSharedPreferences( "MyPref", 0);
         String s= prefs.getString("username", "");
         user=s;
	  
	  FragmentManager mFragmentManager = getFragmentManager();
      FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
      SettingsFragment mPrefsFragment = new SettingsFragment();
      mPrefsFragment.user=user;
      mFragmentTransaction.replace(android.R.id.content, mPrefsFragment);
      mFragmentTransaction.commit();
	 }
	 
}
