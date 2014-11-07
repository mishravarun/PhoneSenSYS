package com.snu.msl.phonesensys;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.ContentObserver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.snu.msl.phonesensys.Cards.SensorCard;
import com.snu.msl.phonesensys.SensorService.TheService;
import com.snu.msl.phonesensys.SyncAdapter.Provider.Provider;

import java.io.File;
import java.util.Date;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.view.CardView;


public class MyActivity extends Activity implements SensorEventListener {
    public static SensorCard card;
    public static CardView cardView;
    public static boolean isRunning=false;
    public static TextView t;
    public static final String SCHEME = "content://";
    // Content provider authority
    // Path for the content provider table
    public static final String TABLE_PATH = "temperature";

    public static final String AUTHORITY = "com.snu.msl.phonesensys.provider";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "com.snu.msl.phonesensys.SyncAdapter.accounts.GenericAccountService";
    // The account name
    public static  String ACCOUNT = "";
    public static float[] updateValues;
    // Instance fields
    Account mAccount;
    Uri mUri;
    // A content resolver for accessing the provider
    ContentResolver mResolver;
    public static boolean[] sensorAvailability = {false,false,false,false,false};
    public static Handler UIHandler;

    static
    {
        UIHandler = new Handler(Looper.getMainLooper());
    }
    public static void runOnUI(Runnable runnable) {
        UIHandler.post(runnable);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        ACCOUNT=pref.getString("username",null);
        ActionBar ab = getActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#81a3d0"));
        ab.setBackgroundDrawable(colorDrawable);
        ab.setSplitBackgroundDrawable(colorDrawable);
        cardView = (CardView) findViewById(R.id.carddemo_weathercard);
        t=(TextView)findViewById(R.id.textView);
        SensorManager mSensorManager;
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) !=null){
          sensorAvailability[0]=true;
        }

        if(mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY) !=null){
          sensorAvailability[1]=true;
        }
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) !=null){
          sensorAvailability[2]=true;
        }
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) !=null){
           sensorAvailability[3] = true;
        }
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) !=null){
            sensorAvailability[4]=true;
        }
        mAccount = CreateSyncAccount(this);

        mUri = new Uri.Builder()
                .scheme(SCHEME)
                .authority(AUTHORITY)
                .path(TABLE_PATH)
                .build();

        TableObserver observer = new TableObserver(null);
        mResolver = getContentResolver();
        mResolver.registerContentObserver(mUri, true, observer);
        //mResolver.requestSync(mAccount,AUTHORITY,Bundle.EMPTY);
        // mResolver.registerContentObserver(mUri, true, observer);
        mResolver.setSyncAutomatically(mAccount, AUTHORITY, true);

        initCards();
    }
    @Override
    public void onBackPressed() {
        if(!isRunning){
            super.onBackPressed();
        }
    }
    public static void refreshStatus()
    {
        t.setText("" + TheService.GPS_STATUS);
    }
    public static void refreshDisplay(float[] values)
    {

        card.refresh(values);

        cardView.refreshCard(card);
    }
    private void initCards() {

        //Create a Card
        card= new SensorCard(this);
        card.init();
       // card.mObjects.get(0).
        //Set card in the cardView

        cardView.setCard(card);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

                case R.id.action_start:
                    // location found
                    start();
                    return true;
                case R.id.action_stop:
                    stop();
                    return true;
                case R.id.action_refresh:
                    // help action
                    refreshScreen();
                    return true;
            case R.id.action_settings:
                // help action
            {

                Intent i= new Intent(this,Settings.class);
                startActivity(i);
            }
                return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    public void refreshScreen()
    {
        if(isRunning)
            Toast.makeText(getApplicationContext(),"Cannot Refresh while Sampling",Toast.LENGTH_SHORT).show();
        else
        {
            float[] ref = {0,0,0,0,0,0,0,0,0,0};
            refreshDisplay(ref);
        }
    }
    public void start()
    {
        if(isRunning)
            Toast.makeText(getApplicationContext(),"Already Started",Toast.LENGTH_SHORT).show();
        else {
            startService(new Intent(getApplicationContext(), TheService.class));
            isRunning = true;
        }
    }
    public void stop()
    {
        if(!isRunning)
            Toast.makeText(getApplicationContext(),"Not Running",Toast.LENGTH_SHORT).show();
        else {

            isRunning = false;
            stopService(new Intent(this, TheService.class));
            File root = Environment.getExternalStorageDirectory();
            deleteDirectory(new File(root, "outputtmpaudio"));
        }
    }
    static public boolean deleteDirectory(File path) {
        if( path.exists() ) {
            File[] files = path.listFiles();
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                }
                else {
                    files[i].delete();
                }
            }
        }
        return( path.delete() );
    }
    public class TableObserver extends ContentObserver {


        public TableObserver(Handler handler) {
            super(handler);
        }

        /*
                         * Define a method that's called when data in the
                         * observed content provider changes.
                         * This method signature is provided for compatibility with
                         * older platforms.
                         */
        @Override
        public void onChange(boolean selfChange) {
            /*
             * Invoke the method signature available as of
             * Android platform version 4.1, with a null URI.
             */
            onChange(selfChange, null);
        }
        /*
         * Define a method that's called when data in the
         * observed content provider changes.
         */
        @Override
        public void onChange(boolean selfChange, Uri changeUri) {
            ContentResolver.requestSync(mAccount, AUTHORITY, null);
        }
    }
    public static Account CreateSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(
                ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call
             * context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }
        return newAccount;
    }

}
