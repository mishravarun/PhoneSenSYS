/*
 * ******************************************************************************
 *   Copyright (c) 2013-2014 Gabriele Mariotti.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *  *****************************************************************************
 */

package com.snu.msl.phonesensys.Cards;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.snu.msl.phonesensys.MainActivity;
import com.snu.msl.phonesensys.MyActivity;
import com.snu.msl.phonesensys.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.base.BaseCard;
import it.gmariotti.cardslib.library.prototypes.CardWithList;
import it.gmariotti.cardslib.library.prototypes.LinearListView;

/**
 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
 */
public class SensorCard extends CardWithList  {
    public List<ListObject> mObjects = new ArrayList<ListObject>();
    public SensorCard(Context context) {
        super(context);
    }
    String title = "Sensors Available";
    @Override
    protected CardHeader initCardHeader() {

        //Add Header
        CardHeader header = new CardHeader(getContext(), R.layout.carddemo_googlenowweather_inner_header);

        //Add a popup menu. This method set OverFlow button to visible

        header.setTitle(title); //should use R.string.
        return header;
    }

    @Override
    protected void initCard() {

        //Set the whole card as swipeable
        setSwipeable(false);

    }
    public int getIndex(String id)
    {
        for(int i=0;i<mObjects.size();i++)
        {
            WeatherObject w1=(WeatherObject)mObjects.get(i);
            if(w1.city.equals(id))
                return i;

        }
        return -1;
    }
    public void refresh(float[] values)
    {
        WeatherObject w1=(WeatherObject)mObjects.get(0);
        w1.temperature=""+values[0];
        ((WeatherObject) mObjects.get(0)).temperature=w1.temperature;
        w1=(WeatherObject)mObjects.get(1);
        w1.temperature=""+values[1];
        ((WeatherObject) mObjects.get(1)).temperature=w1.temperature;
        w1=(WeatherObject)mObjects.get(2);
        w1.temperature=""+values[2];
        ((WeatherObject) mObjects.get(2)).temperature=w1.temperature;
        w1=(WeatherObject)mObjects.get(getIndex("Sound"));
        w1.temperature=""+values[7];
        ((WeatherObject) mObjects.get(getIndex("Sound"))).temperature=w1.temperature;

        if(MyActivity.sensorAvailability[0]){
            w1=(WeatherObject)mObjects.get(getIndex("Temperature"));
            w1.temperature=""+values[3];
            ((WeatherObject) mObjects.get(getIndex("Temperature"))).temperature=w1.temperature;
        }
        if(MyActivity.sensorAvailability[1]){
            w1=(WeatherObject)mObjects.get(getIndex("Humidity"));
            w1.temperature=""+values[4];
            ((WeatherObject) mObjects.get(getIndex("Humidity"))).temperature=w1.temperature;
        }
        if(MyActivity.sensorAvailability[2]){
            w1=(WeatherObject)mObjects.get(getIndex("Pressure"));
            w1.temperature=""+values[5];
            ((WeatherObject) mObjects.get(getIndex("Pressure"))).temperature=w1.temperature;
        }
        if(MyActivity.sensorAvailability[3]){
            w1=(WeatherObject)mObjects.get(getIndex("Light"));
            w1.temperature=""+values[6];
            ((WeatherObject) mObjects.get(getIndex("Light"))).temperature=w1.temperature;
        }


    }
    @Override
    protected List<ListObject> initChildren() {
        WeatherObject w1= new WeatherObject(this);
        w1.city ="Latitude";
        w1.temperature = "--";
        w1.unit = " °";
        w1.setObjectId(w1.city); //It can be important to set ad id
        w1.setSwipeable(false);

        mObjects.add(w1);

        WeatherObject w2= new WeatherObject(this);
        w2.city ="Longitude";
        w2.temperature = "--";
        w2.unit = " °";
        w2.setObjectId(w2.city);
        w2.setSwipeable(false);

        mObjects.add(w2);

        WeatherObject w3= new WeatherObject(this);
        w3.city ="Altitude";
        w3.temperature = "--";
        w3.unit = " mts";
        w3.setObjectId(w3.city);
        w3.setSwipeable(false);
        mObjects.add(w3);


        if(MyActivity.sensorAvailability[0]){
             w3= new WeatherObject(this);
            w3.city ="Temperature";
            w3.temperature = "--";
            w3.unit = " °C";
            w3.setObjectId(w3.city);
            w3.setSwipeable(false);
            mObjects.add(w3);
        }

        if(MyActivity.sensorAvailability[1]){
            w3= new WeatherObject(this);
            w3.city ="Humidity";
            w3.temperature = "--";
            w3.unit = " %";
            w3.setObjectId(w3.city);
            w3.setSwipeable(false);
            mObjects.add(w3);
        }
        if(MyActivity.sensorAvailability[2]){
            w3= new WeatherObject(this);
            w3.city ="Pressure";
            w3.temperature = "--";
            w3.unit = " hPa";
            w3.setObjectId(w3.city);
            w3.setSwipeable(false);
            mObjects.add(w3);
        }
        if(MyActivity.sensorAvailability[3]){
            w3= new WeatherObject(this);
            w3.city ="Light";
            w3.temperature = "--";
            w3.unit = " lx";
            w3.setObjectId(w3.city);
            w3.setSwipeable(false);
            mObjects.add(w3);
        }
            w3= new WeatherObject(this);
            w3.city ="Sound";
            w3.temperature = "--";
            w3.unit = " dB";
            w3.setObjectId(w3.city);
            w3.setSwipeable(false);
            mObjects.add(w3);

        return mObjects;
    }

    @Override
    public View setupChildView(int childPosition, ListObject object, View convertView, ViewGroup parent) {

        //Setup the ui elements inside the item
        TextView city = (TextView) convertView.findViewById(R.id.carddemo_weather_city);
        TextView temperature = (TextView) convertView.findViewById(R.id.carddemo_weather_temperature);
        TextView unit = (TextView) convertView.findViewById(R.id.card_unit);
        //Retrieve the values from the object
        WeatherObject weatherObject= (WeatherObject)object;
        city.setText(weatherObject.city);
        temperature.setText(weatherObject.temperature );
        unit.setText(weatherObject.unit);
        return  convertView;
    }

    @Override
    public int getChildLayoutId() {
        return R.layout.carddemo_googlenowweather_inner_main;
    }




    // -------------------------------------------------------------
    // Weather Object
    // -------------------------------------------------------------

    public class WeatherObject extends DefaultListObject{

        public String city;
        public int weatherIcon;
        public String temperature="";
        public String unit="";

        public WeatherObject(Card parentCard){
            super(parentCard);
            init();
        }

        private void init(){
            //OnClick Listener
            setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(LinearListView parent, View view, int position, ListObject object) {
                    WeatherObject w = (WeatherObject)object;
                    createDialog(city,w.temperature,w.unit);
                }
            });


        }
        public  void createDialog(final String header, String s, String units)
        {
            final Dialog dialog = new Dialog(getContext());

            //tell the Dialog to use the dialog.xml as it's layout description
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog);
            final TextView col = (TextView) dialog.findViewById(R.id.color);
            col.setText("  "+header + "(" + units + ")");

            final TextView txt = (TextView) dialog.findViewById(R.id.txt);
            txt.setText(s);

            Timer timer = new Timer();
             TimerTask timerTask;
            timerTask = new TimerTask() {
                @Override
                public void run() {
                  final   WeatherObject w=(WeatherObject)mObjects.get(getIndex(header));
                    MyActivity.runOnUI(new Runnable() {
                        public void run() {
                            txt.setText(w.temperature);
                        }
                    });
                }
            };
            timer.schedule(timerTask, 0, 1000);


            dialog.show();
        }

    }


}
