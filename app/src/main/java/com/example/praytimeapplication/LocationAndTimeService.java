
package com.example.praytimeapplication;

        import android.Manifest;
        import android.app.Service;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.content.pm.PackageManager;
        import android.location.Location;
        import android.location.LocationListener;
        import android.location.LocationManager;
        import android.os.Bundle;
        import android.os.IBinder;
        import android.os.Looper;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Date;
        import java.util.TimeZone ;
        import java.time.*;



        import androidx.annotation.NonNull;
        import androidx.core.app.ActivityCompat;

public class LocationAndTimeService extends Service {
    private Thread locationReaderThread;
    private LocationManager lm;
    private MyGPSLocationListener myLocationListener;
    private boolean isRunning = false;
    final String GPS_FILTER = "swe483.action.GPS_LOCATION";

    //here implements location listener class within location service
    class MyGPSLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(@NonNull Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            Intent myLocationIntent = new Intent(GPS_FILTER);
            myLocationIntent.putExtra("latitude", latitude);
            myLocationIntent.putExtra("longitude", longitude);
            sendBroadcast(myLocationIntent);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {

        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {

        }
    }

    public LocationAndTimeService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int res = super.onStartCommand(intent, flags, startId);
        locationReaderThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();

                lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                myLocationListener = new MyGPSLocationListener();
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 50, myLocationListener);
                }

                Looper.loop();
            }
        });
        if(!isRunning){
            locationReaderThread.start();
            isRunning = true;
        }
        return res;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onDestroy() {
    }


}
