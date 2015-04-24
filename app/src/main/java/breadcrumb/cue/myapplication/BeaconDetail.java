package breadcrumb.cue.myapplication;

/**
 * Created by cue on 4/23/2015.
 */
public class BeaconDetail {
        String BName;
        String Lat;
        String Long;
        int LastRSSI;
        int CurrentRSSI;
    BeaconDetail(String BName,String Lat,String Long){
            this.BName = BName;
            this.Lat = Lat;
            this.Long = Long;
            this.LastRSSI = 0;
            this.CurrentRSSI = 0;

        }
        public void addRSSI(int RSSI){
            if (this.CurrentRSSI == 0){
                this.CurrentRSSI = RSSI;
            }
            else{
                this.LastRSSI = this.CurrentRSSI;
                this.CurrentRSSI = RSSI;
            }
        }

}
