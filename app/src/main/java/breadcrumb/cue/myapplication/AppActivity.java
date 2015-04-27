
package breadcrumb.cue.myapplication;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    private final Map<String, MarkerOptions> mMarkers = new ConcurrentHashMap<String, MarkerOptions>();

    private void add(String name, MarkerOptions marker) {
        //final MarkerOptions marker = new MarkerOptions().position(ll).title(name);
        mMarkers.put(name, marker);
        final MarkerOptions newMarker = marker;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMap.addMarker(newMarker);
            }
        });
    }

    private void remove(String name) {
        mMarkers.remove(name);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMap.clear();

                for (MarkerOptions item : mMarkers.values()) {
                    mMap.addMarker(item);
                }
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.in_map);
        listPoint = new ArrayList<LatLng>();
        setUpMapIfNeeded();
        startService(new Intent(this, AppService.class));
      /*  Log.e("distance", Double.toString(CalculateGPSDistance.Distance(new LocationCoord("41.838849", "-87.627583"), new LocationCoord("41.838848", "-87.627485")).Distance));
        Log.e("newcord", Double.toString(CalculateGPSDistance.newCoordinates(new LocationCoord("41.838849", "-87.627583"),new DistanceVector("4","268.923523137032")).Lat));
        */
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
            String Broad_cat ;
            try{
                Broad_cat = arg1.getStringExtra("Broad");
                if(Broad_cat.equals(BroadcastType.Breadcrumb.toString())) {
                    latPassed = arg1.getStringExtra("LatPassed");
                    longPassed = arg1.getStringExtra("LongPassed");
                    catagory = Integer.parseInt(arg1.getStringExtra("CatPassed"));
                    String Name = arg1.getStringExtra("Name");
                    TextView v = (TextView) findViewById(R.id.textView1);
                    v.setText(latPassed);

                    MARK_LOCATION = new LatLng(Double.parseDouble(latPassed), Double.parseDouble(longPassed));

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MARK_LOCATION, 25));

                    if (catagory == 1) {
                        add(Name,new MarkerOptions().position(MARK_LOCATION)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
                    } else if (catagory == 2) {
                      add(Name, new MarkerOptions().position(MARK_LOCATION)
                              .icon(BitmapDescriptorFactory.fromResource(R.drawable.m)));

                    }
                    else if (catagory == 111) {
                        add(Name, new MarkerOptions().position(MARK_LOCATION)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.supermarion)));
                    }
                    else
                        add(Name,new MarkerOptions().position(MARK_LOCATION)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.beacon)));

               /* if (LOC2 != null && MARK_LOCATION !=null)
                {
                    line = mMap.addPolyline(new PolylineOptions()
                            .add(MARK_LOCATION,LOC2)
                            .width(5)
                            .color(Color.RED));
                }*/
                }
                else if(Broad_cat.equals(BroadcastType.BroadCalc.toString())){
                    //TextView v = (TextView) findViewById(R.id.textdist);
                    //v.setText( arg1.getStringExtra("Distance"));
                    String lat1 = arg1.getStringExtra("LatPassed");
                    String long1 = arg1.getStringExtra("LongPassed");
                    Float rotation = Float.parseFloat (arg1.getStringExtra("Bearing"));
                    LatLng foot = new LatLng(Double.parseDouble(lat1),Double.parseDouble(long1));
                    //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(), 25));
                    remove("step");
                    add("step",new MarkerOptions().position(foot)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.footprint_opt)).rotation((rotation+90)%360));

                }
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