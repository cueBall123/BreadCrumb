package breadcrumb.cue.myapplication;

/**
 * Created by cue on 4/24/2015.
 */
public class DistanceVector {
    public double Distance;
    public double Bearing;
    DistanceVector(){}
    DistanceVector(String dist,String Bearing){
        this.Distance = Double.parseDouble(dist);
        this.Bearing = Double.parseDouble(Bearing);
    }
}
