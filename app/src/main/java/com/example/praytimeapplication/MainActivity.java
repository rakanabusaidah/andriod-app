package com.example.praytimeapplication;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.praytimeapplication.PrayersNotifications.CHANNEL_1_ID;

public class MainActivity extends AppCompatActivity {
    static TextView fajerTextView;
    static TextView shurogTextView;
    static TextView duhurTextView;
    static TextView aserTextView;
    static TextView maghrebTextView;
    static TextView eishaTextView;
    static ImageView settingsImageView;
    static TextView cityTextView;
    static PrayTime pt = new PrayTime();
    Intent serviceStartIntent;
    Receiver receiver;
    private NotificationManagerCompat notificationManager;

      double latitude;
      double longitude;

    final String GPS_FILTER = "swe483.action.GPS_LOCATION";
    private boolean ACCESS_FINE_LOCATION_PERMISSION = false;
    private boolean ACCESS_COARSE_LOCATION_PERMISSION = false;


    class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //if gps recivied change lat and long else same else lat and long reset to 0 and problems
            if(action.equals(GPS_FILTER)){
                latitude = intent.getDoubleExtra("latitude",0.0);
                longitude = intent.getDoubleExtra("longitude",0.0);
            }

            try {
                cityName(latitude,longitude);
            } catch (IOException e) {
                e.printStackTrace();
            }
            updatePrayTime(latitude,longitude);

            //for compare current time with pray time
            ArrayList<String> prayTimes =  pt.getPrayerTimes24h(latitude, longitude);
            SimpleDateFormat formatter = new SimpleDateFormat("kk:mm");
            Date date = new Date();
            double currentTimeH = Double.parseDouble(formatter.format(date).substring(0,2));
            double currentTimeM = Double.parseDouble(formatter.format(date).substring(3,5));
            double prayTimeH;
            double prayTimeM;
            String [] PrayrtName = {"Fajar", "Sunrise", "Dhuhr", "Asr", "Maghrib","Maghrib", "Isha"};

            for(int i=0; i<prayTimes.size();i++){
                prayTimeH = Double.parseDouble(prayTimes.get(i).substring(0,2));
                prayTimeM = Double.parseDouble(prayTimes.get(i).substring(3,5));
                System.out.println(formatter.format(date)+"      CURRENT TIME:"+currentTimeH+" "+currentTimeM+"      PRAY TIME:"+prayTimeH+"  "+prayTimeM);

                if( (currentTimeH == prayTimeH) && (currentTimeM == prayTimeM) ){
                    sendOnChannel1(PrayrtName[i]);
                    System.out.println("Pray Time");

                }
            }
        }
    }

    //Permission Stuff
    private ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if(result){
                        Log.i("GPSReader","Permission Granted");
                    }else{
                        Log.i("GPSReader","Permission Denied");
                    }
                }
            });



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         fajerTextView = (TextView) findViewById((R.id.fajerTextView));
         shurogTextView = (TextView) findViewById((R.id.shurogTextView));
         duhurTextView = (TextView) findViewById((R.id.duhurTextView));
         aserTextView = (TextView) findViewById((R.id.aserTextView));
         maghrebTextView = (TextView) findViewById((R.id.maghrebTextView));
         eishaTextView = (TextView) findViewById((R.id.eishaTextView));
         settingsImageView = (ImageView) findViewById((R.id.settingsImageView));
        cityTextView = (TextView) findViewById(R.id.cityTextView);

        notificationManager = NotificationManagerCompat.from(this);


         //broadCastReciver
        receiver = new Receiver();
        IntentFilter Filter = new IntentFilter();
        Filter.addAction(Intent.ACTION_TIME_TICK);
        Filter.addAction(Intent.ACTION_DATE_CHANGED);
        Filter.addAction(Intent.ACTION_TIME_CHANGED);
        Filter.addAction(GPS_FILTER);
        registerReceiver(receiver,Filter);



        //Permission Stuff
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        else
            this.ACCESS_FINE_LOCATION_PERMISSION=true;

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
        else
            this.ACCESS_COARSE_LOCATION_PERMISSION=true;


        //Start Service
        if(ACCESS_FINE_LOCATION_PERMISSION && ACCESS_COARSE_LOCATION_PERMISSION) {
            serviceStartIntent = new Intent(getApplicationContext(), LocationAndTimeService.class);
            startService(serviceStartIntent);
        }

    }

    public void sendOnChannel1(String i){
        Intent activityIntent = new Intent(this,MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, activityIntent, 0);

        Intent broadcastIntent = new Intent(this, NotificationReceiver.class);
        broadcastIntent.putExtra("toastMessage","Prayer Time");
        PendingIntent actionIntent = PendingIntent.getBroadcast(this, 0
                , broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)

                .setContentTitle(i + " Prayer Time")
                .setSmallIcon(R.drawable.ic_one)
                .setContentIntent(contentIntent)
                .addAction(R.mipmap.ic_launcher, "Toast", actionIntent)
                .setColor(Color.RED)
                .build();

        notificationManager.notify(1, notification);


    }
    public void cityName(double latitude, double longitude) throws IOException {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
        String cityName = addresses.get(0).getLocality();
        cityTextView.setText("City: "+cityName);

    }
    public static void updatePrayTime(double latitude, double longitude){
        ArrayList<String> prayTimes =  pt.getPrayerTimes(latitude, longitude);
        fajerTextView.setText("Fajr - "+prayTimes.get(0));
        shurogTextView.setText("Sunrise - "+prayTimes.get(1));
        duhurTextView.setText("Dhuhr - "+prayTimes.get(2));
        aserTextView.setText("Asr - "+prayTimes.get(3));
        maghrebTextView.setText("Maghrib - "+prayTimes.get(5));
        eishaTextView.setText("Isha - "+prayTimes.get(6));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}