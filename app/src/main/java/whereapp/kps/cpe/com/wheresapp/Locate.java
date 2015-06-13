package whereapp.kps.cpe.com.wheresapp;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by apple on 3/7/15.
 */
public class Locate {

    public static double getLng() {
        return lng;
    }

    public static void setLng(double lng) {
        Locate.lng = lng;
    }

    public static double getLat() {
        return lat;
    }

    public static void setLat(double lat) {
        Locate.lat = lat;
    }

    private LocationManager locationManager;
    public static double lat,lng ;
    Context mContext;
    public Locate(Context mContext){
        this.mContext = mContext;
    }
    public void getLocation(){
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                setLat(location.getLatitude());
                setLng(location.getLongitude());
                Log.i("Maps", "Lat long --->  " + lng + lng);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

}
