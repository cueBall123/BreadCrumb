
package breadcrumb.cue.myapplication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.gimbal.android.Beacon;
import com.gimbal.android.BeaconEventListener;
import com.gimbal.android.BeaconManager;
import com.gimbal.android.BeaconSighting;
import com.gimbal.android.Communication;
import com.gimbal.android.CommunicationListener;
import com.gimbal.android.CommunicationManager;
import com.gimbal.android.Gimbal;
import com.gimbal.android.Place;
import com.gimbal.android.PlaceEventListener;
import com.gimbal.android.PlaceManager;
import com.gimbal.android.Push;
import com.gimbal.android.Push.PushType;
import com.gimbal.android.Visit;
import breadcrumb.cue.myapplication.GimbalEvent.TYPE;

public class AppService extends Service {
    final static int RSSI_DIFF_THRESHOLD = 10;
    final static String MY_ACTION = "MY_ACTION";
    static ConcurrentHashMap<String,BeaconDetail> HBeaconMap = new ConcurrentHashMap<>();
    private static final int MAX_NUM_EVENTS = 100;
    private LinkedList<GimbalEvent> events;
    private PlaceEventListener placeEventListener;
    private CommunicationListener communicationListener;
    private BeaconEventListener beaconEventListener;
    private BeaconManager bm;
    @Override
    public void onCreate() {

        events = new LinkedList<GimbalEvent>(GimbalDAO.getEvents(getApplicationContext()));

        Gimbal.setApiKey(this.getApplication(), "681db292-8351-4f0e-97b1-ec99ef6b0ff9");
        bm = new BeaconManager();
        calculatePosition calc = new calculatePosition();
        final Thread cal = new Thread(calc);
        cal.start();

        bm.startListening();
        beaconEventListener = new BeaconEventListener() {
            @Override
            public  void onBeaconSighting(BeaconSighting beaconSighting) {
                Log.e("beaconfind", beaconSighting.getRSSI().toString() + "-" + beaconSighting.getBeacon().getName());
                if (HBeaconMap.containsKey(beaconSighting.getBeacon().getName()))
                    HBeaconMap.get(beaconSighting.getBeacon().getName()).addRSSI(beaconSighting.getRSSI());
            }
        };
        bm.addListener(beaconEventListener);
        // Setup PlaceEventListener
        placeEventListener = new PlaceEventListener() {

            @Override
            public  void onVisitStart(Visit visit) {
                //addEvent(new GimbalEvent(TYPE.PLACE_ENTER, visit.getPlace().getName(), new Date(visit.getArrivalTimeInMillis())));
                Place p = visit.getPlace();
                String dTargetLat = new IndoorLocationCoord(p).getLat();
                String dTargetLong = new IndoorLocationCoord(p).getLong();
                String category = new IndoorLocationCoord(p).getCat();
                Intent intent = new Intent();
                intent.setAction(MY_ACTION);
                    if (category.equals("5"))
                    HBeaconMap.put(p.getName(), new BeaconDetail(p.getName(), dTargetLat, dTargetLong,category));

                intent.putExtra("Broad",BroadcastType.Breadcrumb.toString());
                intent.putExtra("LatPassed", dTargetLat);
                intent.putExtra("LongPassed", dTargetLong);
                intent.putExtra("CatPassed", category);
                intent.putExtra("Name", p.getName());

                sendBroadcast(intent);

            }

            @Override
            public synchronized void onVisitEnd(Visit visit) {
               // addEvent(new GimbalEvent(TYPE.PLACE_EXIT, visit.getPlace().getName(), new Date(visit.getDepartureTimeInMillis())));
                //HBeaconMap.remove(visit.getPlace().getName());

                Log.e("removed","ff");
            }
        };
        PlaceManager.getInstance().addListener(placeEventListener);

    }
    private  class calculatePosition implements Runnable{
        public class compareVisit implements Comparator<BeaconDetail>{

            @Override
            public int compare(BeaconDetail beaconDetail, BeaconDetail beaconDetail2) {
                return beaconDetail.VisitNumber.compareTo(beaconDetail2.VisitNumber);
            }
        }
        Stack<LocationCoord> stackofVisits = new Stack<>();
        Stack<Integer> visitRSSI = new Stack<>();
        @Override
        public  void run() {
            while(true){
                try {
                    Thread.sleep(1000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.e("fromThread","ff");
                Intent broad = new Intent();
                broad.setAction(MY_ACTION);
                broad.putExtra("Broad", BroadcastType.BroadCalc.toString());
                List<BeaconDetail> templist = new ArrayList<BeaconDetail>();
               for(String key : HBeaconMap.keySet()){
                   templist.add(HBeaconMap.get(key));
                   //stackofVisits.push(HBeaconMap.get(key).coord);
                   //visitRSSI.push(HBeaconMap.get(key).CurrentRSSI);
                   int RSSI_Diff  = HBeaconMap.get(key).CurrentRSSI - HBeaconMap.get(key).LastRSSI;
                   if(RSSI_Diff > RSSI_DIFF_THRESHOLD){
                       //broad.putExtra("direction", "Towards "+key);
                       //sendBroadcast(broad);
                   }
                   else if(RSSI_Diff < -RSSI_DIFF_THRESHOLD){
                       //broad.putExtra("direction", "Away from "+key);
                       //sendBroadcast(broad);
                   }
               }
               Collections.sort(templist,new compareVisit());
                Collections.reverse(templist);
                if(templist.size()>=2){
                    LocationCoord first = templist.get(0).coord;
                    LocationCoord second  = templist.get(1).coord;
                    int rssi2 = templist.get(0).CurrentRSSI;
                    int rssi1 = templist.get(1).CurrentRSSI;

                    if(rssi1 !=0  && rssi2 != 0) {
                        double ratio = (rssi1) / ((double)rssi1 + rssi2);
                        //double ratio = Math.pow(10,rssi1)/Math.pow(10,rssi1)+Math.pow(10,rssi2);
                        DistanceVector d = CalculateGPSDistance.Distance(second, first);

                        LocationCoord newCoord = CalculateGPSDistance.newCoordinates(second, new DistanceVector(Double.toString(d.Distance * ratio), Double.toString(d.Bearing)));
                        broad.putExtra("LatPassed", Double.toString(newCoord.Lat));
                        broad.putExtra("LongPassed",Double.toString( newCoord.Longt));
                        //broad.putExtra("Distance",Double.toString( d.Distance));
                        broad.putExtra("Bearing",Double.toString( d.Bearing));
                        sendBroadcast(broad);
                    }

                }
                templist.clear();




            }
        }
    }

    private void addEvent(GimbalEvent event) {
        while (events.size() >= MAX_NUM_EVENTS) {
            events.removeLast();
        }
        events.add(0, event);
        GimbalDAO.setEvents(getApplicationContext(), events);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        PlaceManager.getInstance().removeListener(placeEventListener);
        CommunicationManager.getInstance().removeListener(communicationListener);
        bm.removeListener(beaconEventListener);
        bm.stopListening();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
