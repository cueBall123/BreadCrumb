package breadcrumb.cue.myapplication;

/**
 * Created by cue on 4/23/2015.
 */
public class BeaconDetail {
        static int SerialNumber = 0;
        int VisitNumber;
        String BName;
        LocationCoord coord;
        String Lat;
        String Long;
        int LastRSSI;
        int CurrentRSSI;
    BeaconDetail(String BName,String Lat,String Long){
            SerialNumber++;
            this.BName = BName;
            this.coord.Lat = Double.parseDouble(Lat);
            this.coord.Longt =  Double.parseDouble(Long);
            this.LastRSSI = 0;
            this.CurrentRSSI = 0;
            VisitNumber = SerialNumber;
        }
        public void addRSSI(int RSSI){
            if (this.CurrentRSSI == 0){
                this.CurrentRSSI = RSSI;

            }
            else if(Math.abs(CurrentRSSI - RSSI)>10){
                this.LastRSSI = this.CurrentRSSI;
                this.CurrentRSSI = RSSI;
            }
        }

}
