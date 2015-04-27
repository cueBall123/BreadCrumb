package breadcrumb.cue.myapplication;

/**
 * Created by cue on 4/23/2015.
 */
public class BeaconDetail {
        private static int SerialNumber = 0;
        Integer VisitNumber;
        String BName;
        LocationCoord coord;
        String Cat;
        int LastRSSI;
        int CurrentRSSI;
    BeaconDetail(String BName,String Lat,String Long,String Cat){
            SerialNumber++;
            this.BName = BName;
            this.coord = new LocationCoord(Lat,Long);
            this.LastRSSI = 0;
            this.CurrentRSSI = 0;
            this.Cat = Cat;
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
