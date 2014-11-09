package com.snu.msl.phonesensys.SensorService;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import com.snu.msl.phonesensys.MyActivity;
import com.snu.msl.phonesensys.R;
import com.snu.msl.phonesensys.SyncAdapter.Provider.Provider;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TheService extends Service implements SensorEventListener,LocationListener {
    public static final String TAG = TheService.class.getName();
    public static final int SCREEN_OFF_RECEIVER_DELAY = 500;
    protected LocationManager locationManager;
   public static LocationListener locationListener;
   SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyHHmmss");
   public MediaRecorder mRecorder = null;
    protected Context context;
    String lat;
    Runnable r;
    String provider;
    protected String latitude,longitude;
    protected boolean gps_enabled,network_enabled;
    private SensorManager mSensorManager = null;
    private WakeLock mWakeLock = null;
    public float[] sensorValues;
    int checka=0,checkl=0,checkh=0,checkp=0,checkt=0,checkloc=0,checks=0;
    public static float accx=0,accy=0,accz=0,light=0,humidity=0,pressure=0,temp=0,phonebat=0,avgtemp=0,avghumidity=0,avgpressure=0,avgtemp1=0,avghumidity1=0,avgpressure1=0,rawtemp=0,tempaccuracy=0;
    public static double sound=0;
    int accflag=0,lightflag=0,humidityflag=0,pressureflag=0,tempflag=0,soundflag=0;
    int count1=0;
    int checkgps=0;
    public static File dir;
    int time=0,avgtime=0;
    public static String GPS_STATUS="Waiting For Location";
    public static String format="";
    public static int io=0,sampling=0;
    public static String firstTime="";
    public static int fTime=0;
    /*
     * Register this as a sensor event listener.
     */
    private void registerListener() {
    	int count = 4;
    	if(mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) !=null){
    		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);


    		}
    	if(mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) !=null){
    		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL);

    		}
    		if(mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY) !=null){
    		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY), SensorManager.SENSOR_DELAY_NORMAL);

            }
    		if(mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) !=null){
    		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE), SensorManager.SENSOR_DELAY_NORMAL);

            }
    		if(mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) !=null){
    		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE), SensorManager.SENSOR_DELAY_NORMAL);

            }
        sensorValues = new float[11];
        for(int i=0;i<11;i++)
            sensorValues[i]=0;
        File root = Environment.getExternalStorageDirectory();
        File tmpdir = new File(root,"outputtmpaudio");
        File output = new File(tmpdir,"output");
        tmpdir.mkdir();

    		if (mRecorder == null) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile(output+"new.mp4"); 
           
            try {
				mRecorder.prepare();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            mRecorder.start();
    }

    }

    /*
     * Un-register this as a sensor event listener.
     */
    private void unregisterListener() {
    	if (mRecorder != null) {
            mRecorder.stop();   
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
    }
        mSensorManager.unregisterListener(this);

    }

    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive(" + intent + ")");

            if (!intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                return;
            }
             
            Runnable runnable = new Runnable() {
                public void run() {
                    Log.i(TAG, "Runnable executing.");
                    unregisterListener();
                    registerListener();
                }
            };

            new Handler().postDelayed(runnable, SCREEN_OFF_RECEIVER_DELAY);
        }
    };

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.i(TAG, "onAccuracyChanged().");
    }

    public void onSensorChanged(SensorEvent event) {
        Log.i(TAG, "onSensorChanged().");
        
        count1++;
         format = s.format(new Date());
         if(event.sensor.getType()== Sensor.TYPE_ACCELEROMETER)
    	{ sensorValues[8]=event.values[0];
            sensorValues[9]=event.values[1];
            sensorValues[10]=event.values[2];
    		
    	}
        if(event.sensor.getType()== Sensor.TYPE_LIGHT)
		 {	

             sensorValues[6]=event.values[0];
			 
		 }
	if(event.sensor.getType()== Sensor.TYPE_RELATIVE_HUMIDITY)
	 {
         sensorValues[4]=event.values[0];
	 }
	if(event.sensor.getType()== Sensor.TYPE_PRESSURE)
	 {sensorValues[5]=event.values[0];
		 
	 }if(event.sensor.getType()== Sensor.TYPE_AMBIENT_TEMPERATURE)
	 {	 temp=event.values[0];
        sensorValues[3] = event.values[0];
	 }
	  if(count1%23==0)
	  {
		  time++;
	  }
	  if(time >3)
	  {
		  GPS_STATUS="GPS Lost";
          MyActivity.refreshStatus();
		  checkgps=0;
	  }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
PowerManager manager =
            (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
     
       
        registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
    }

    
    public double getAmplitude() {
        if (mRecorder != null)
                return  (20* Math.log10(mRecorder.getMaxAmplitude() / 2700.0));
        else
                return 0;

}

    @Override
    public void onDestroy() {
    	 if (mRecorder != null) {
             mRecorder.stop();   
             mRecorder.reset();
             mRecorder.release();
             mRecorder = null;
     }
        unregisterReceiver(mReceiver);
        unregisterListener();
        locationManager.removeUpdates(this);
      sampling=0;
        GPS_STATUS="IDLE";
        MyActivity.refreshStatus();

        mWakeLock.release();
        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
      
        
        Intent intent1 = new Intent(this, MyActivity.class);
        	PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent1, 0);

        	// build notification
        	// the addAction re-use the same intent to keep the example short
        	Notification n  = new Notification.Builder(this)
        	        .setContentTitle("Sensor Collector")
        	        .setContentText("Sampling")
        	        .setSmallIcon(R.drawable.ic_launcher)
        	        .setContentIntent(pIntent)
        	        .setAutoCancel(true)
        	       
        	       .build();
        	    
        	
        
       
        
        startForeground(Process.myPid(), n);
        registerListener();
        mWakeLock.acquire();

        return START_STICKY;
    }

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if(fTime==0) {
            fTime = 1;
            firstTime=s.format(new Date());
        }
            time=0;
		sampling=1;
		sensorValues[7]=(float)getAmplitude();
		

		if(checkgps==0)
		{
			GPS_STATUS="Location Set";
            MyActivity.refreshStatus();

            checkgps=1;
		}
        sensorValues[0] = (float)location.getLatitude();
        sensorValues[1] = (float)location.getLongitude();
        sensorValues[2] = (float)location.getAltitude();
        updateTable();
	MyActivity.refreshDisplay(sensorValues);
}

	@Override
	public void onProviderDisabled(String provider) {
	Log.d("Latitude", "disable");
	GPS_STATUS="Location Not Available";
        MyActivity.refreshStatus();
        MyActivity.isRunning=false;
        if(sampling==0)
	{
	File root = Environment.getExternalStorageDirectory();
      File dir = new File(root,"sensordata2");
      File v = new File(root, "tmpsensordata");
      dir.renameTo(v);
deleteDirectory(new File(root,"tmpsensordata"));

deleteDirectory(new File(root,"outputtmpaudio"));
	}
		  stopSelf();
		  
	}
	 public void updateTable()
     {

         ContentValues values = new ContentValues();
         values.put(Provider.timestamp, TheService.format);
         values.put(Provider.firstTime,firstTime);
         values.put(Provider.gpsLatitude, sensorValues[0]);
         values.put(Provider.gpsLongitude,sensorValues[1]);
         values.put(Provider.gpsAltitude, sensorValues[2]);
         values.put(Provider.phoneTemperature, sensorValues[3]);
         values.put(Provider.phoneHumidity, sensorValues[4]);
         values.put(Provider.phonePressure,sensorValues[5]);
         values.put(Provider.phoneLight, sensorValues[6]);
         values.put(Provider.phoneSound, sensorValues[7]);
         values.put(Provider.phoneAccX, sensorValues[8]);
         values.put(Provider.phoneAccY, sensorValues[9]);
         values.put(Provider.phoneAccZ, sensorValues[10]);


         Uri uri = getContentResolver().insert(com.snu.msl.phonesensys.SyncAdapter.Provider.Provider.CONTENT_URI, values);
     }
	@Override
	public void onProviderEnabled(String provider) {
	Log.d("Latitude", "enable");
	}
	 
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	Log.d("Latitude", "" + status);
	
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

    
	}