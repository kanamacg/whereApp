package whereapp.kps.cpe.com.wheresapp;

import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.w3c.dom.Document;

import java.util.ArrayList;

/**
 * Created by apple on 2/28/15.
 */
public class Maps extends FragmentActivity {

    GoogleMap mMap;
    LatLng start,end;
    GMapV2Direction md ;
    private double lat,lng;

    private  static XMPPConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        lng = Locate.getLng();
        lat = Locate.getLat();
        Log.i("Maps", "Lat long --->  " + lng + lng);

        start = new LatLng(lat, lng);
        end = new LatLng(14.375546,99.8850587);

        mMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(start, 16));
        mMap.addMarker(new MarkerOptions().position(start).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        Button buttonGoto = (Button)findViewById(R.id.buttonRouting);
        buttonGoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                md = new GMapV2Direction();
                Document doc = md.getDocument(start, end, GMapV2Direction.MODE_DRIVING);
                int duration = md.getDurationValue(doc);
                String distance = md.getDistanceText(doc);
                String start_address = md.getStartAddress(doc);
                String copy_right = md.getCopyRights(doc);
                int valueDuration = md.getDurationValue(doc);
                Toast.makeText(Maps.this, String.valueOf(valueDuration), Toast.LENGTH_SHORT).show();

                ArrayList<LatLng> directionPoint = md.getDirection(doc);
                PolylineOptions rectLine = new PolylineOptions().width(3).color(Color.RED);

                for (int i = 0; i < directionPoint.size(); i++) {
                    rectLine.add(directionPoint.get(i));
                }

                mMap.addPolyline(rectLine);

                mMap.addMarker(new MarkerOptions().position(end).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (LatLng marker : directionPoint) {
                    builder.include(marker);
                }
                LatLngBounds bounds = builder.build();
                int padding = 70; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.animateCamera(cu);

            }
        });


        Button me = (Button) findViewById(R.id.buttonMe);
        me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(start, 16));
            }
        });

        Button buddy = (Button) findViewById(R.id.buttonBuddy);
        buddy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.addMarker(new MarkerOptions().position(end).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(end, 16));
            }
        });
    }

    private void reQuestLocation(){
        final XMPPConnection connection = StaticConnection.getConnection();
        Presence presence = new Presence(Presence.Type.available);
        connection.sendPacket(presence);
        setConnection(connection);
        String user = ListFriends.getUserCore();
        Message msg = new Message(user, Message.Type.location);
        if (connection != null) {
            connection.sendPacket(msg);
        }
    }
    private void setConnection(XMPPConnection connection) {

        this.connection = connection;
    }
}
