/**
 * Copyright (C) 2014 Gimbal, Inc. All rights reserved.
 *
 * This software is the confidential and proprietary information of Gimbal, Inc.
 *
 * The following sample code illustrates various aspects of the Gimbal SDK.
 *
 * The sample code herein is provided for your convenience, and has not been
 * tested or designed to work on any particular system configuration. It is
 * provided AS IS and your use of this sample code, whether as provided or
 * with any modification, is at your own risk. Neither Gimbal, Inc.
 * nor any affiliate takes any liability nor responsibility with respect
 * to the sample code, and disclaims all warranties, express and
 * implied, including without limitation warranties on merchantability,
 * fitness for a specified purpose, and against infringement.
 */
package breadcrumb.cue.myapplication;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.gimbal.android.BeaconEventListener;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.model.*;

import java.util.ArrayList;
import java.util.List;

public class AppActivity extends FragmentActivity implements OnMapLoadedCallback{

    //public static Runnable  runnable;

    String latPassed, longPassed;
    int catagory = 0;
    Double dLatPassed, dLongPassed;
    MyReceiver myReceiver;

    Location location;
    Marker markLocation;


    List<LatLng> listPoint;
    int currentPt;
    TextView info;

    private GoogleMap mMap;
    private GimbalEventListAdapter adapter;
    private BeaconEventListener beaconEventListener;
    double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.in_map);
        listPoint = new ArrayList<LatLng>();
        setUpMapIfNeeded();
        startService(new Intent(this, AppService.class));

        adapter = new GimbalEventListAdapter(this);

    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

    }

    private class MyReceiver extends BroadcastReceiver{
        LatLng MARK_LOCATION, LOC2;
        Polyline line;

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub

            try{

                latPassed = arg1.getStringExtra("LatPassed");
                longPassed = arg1.getStringExtra("LongPassed");
                catagory =Integer.parseInt( arg1.getStringExtra("CatPassed"));
                Toast.makeText(AppActivity.this,
                        "BreadCrumb dropped!",
                        Toast.LENGTH_LONG).show();


                MARK_LOCATION =new LatLng( Double.parseDouble(latPassed),Double.parseDouble(longPassed));

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MARK_LOCATION, 25));

                if(catagory==1){
                    markLocation = mMap.addMarker(new MarkerOptions().position(MARK_LOCATION)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
                }

                else if(catagory==2){
                    markLocation = mMap.addMarker(new MarkerOptions().position(MARK_LOCATION)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.m)));
                }

                else
                    markLocation = mMap.addMarker(new MarkerOptions().position(MARK_LOCATION)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));

               /* if (LOC2 != null && MARK_LOCATION !=null)
                {
                    line = mMap.addPolyline(new PolylineOptions()
                            .add(MARK_LOCATION,LOC2)
                            .width(5)
                            .color(Color.RED));
                }*/
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            LOC2 = null;
            LOC2 = MARK_LOCATION;
        }

    }
    @Override
    protected void onStart() {
        super.onStart();

        //Register BroadcastReceiver
        //to receive event from our service
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppService.MY_ACTION);
        registerReceiver(myReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        unregisterReceiver(myReceiver);
        super.onStop();

    }


    // --------------------
    // SETTINGS MENU
    // --------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (mMap != null) {
                //setUpMap();
                onMapLoaded();
            }
        }
    }


    public void onLocationChanged(Location location) {

        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
        LatLng latLng = new LatLng(latitude, longitude);

        mMap.addMarker(new MarkerOptions().position(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

    }




    @Override
    public void onMapLoaded() {
        // TODO Auto-generated method stub

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(41.8348494,-87.6134026), 18));

        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }

        final LatLng MARK_LOCATION = new LatLng(latitude, longitude);

        markLocation = mMap.addMarker(new MarkerOptions().position(MARK_LOCATION)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));


        mMap.setMyLocationEnabled(true);

    }




}