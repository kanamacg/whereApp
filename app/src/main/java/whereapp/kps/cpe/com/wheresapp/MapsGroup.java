package whereapp.kps.cpe.com.wheresapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.LocationMeTypeFilter;
import org.jivesoftware.smack.packet.LocationMe;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MapsGroup extends FragmentActivity {

    private static LatLng start,end;
    private GMapV2Direction md;
    private Document doc;
    private Polyline line = null;
    private static Marker marke,markuse,markefinal;
    private static GoogleMap mMap;
    private List<Polyline> lineArr = new ArrayList<>();
    private List<Polyline> linePass = new ArrayList<>();
    private static List<Marker> markers = new ArrayList<>();
    private static List<Marker> markeruse = new ArrayList<>();
    private static List<Integer> path = new ArrayList<>();
    private boolean chklast = false,chkline = false,chkin =false,mm = true;
    private static int totaltime2, totaldis2;
    private TextView timeuse;
    private TextView disuse;
    private String address;
    private int j = 0;
    private static LocationClient mLocationClient;
    private static double uselat,uselng;
    long tStart = 0,tEnd = 0;
    double tSum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapsgroup);
        DBChecker myDB = new DBChecker(this);
        final String arrData[] = myDB.SelectData("1");

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        if (isServicesAvailable()) {
            mLocationClient = new LocationClient(this, mCallback, mListener);
        } else {
            finish();
        }

        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapG)).getMap();

        mMap.setMyLocationEnabled(true);
        md = new GMapV2Direction();
        if (mm) {
            start = new LatLng(Locate.getLat(), Locate.getLng());
            markuse = mMap.addMarker(new MarkerOptions().title("start")
                    .position(start)
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
            markers.add(markuse);
            marke = mMap.addMarker(new MarkerOptions().title("A").position(new LatLng(14.0207964, 99.9722265)));
            markers.add(marke);
            marke = mMap.addMarker(new MarkerOptions().title("B").position(new LatLng(14.0235171, 99.9737627)));
            markers.add(marke);
            marke = mMap.addMarker(new MarkerOptions().title("C").position(new LatLng(14.0334407, 99.9749516)));
            markers.add(marke);

            zoom();
            mm = false;
        }

        //####### update my marker ##########

        final Handler mHandler = new Handler();
        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        markuse.setPosition(new LatLng(uselat,uselng));
                    }
                });
            }
        }, 0, 1000);

        timeuse = (TextView) findViewById(R.id.textViewTimeUse);
        disuse = (TextView) findViewById(R.id.textViewDistanceUse);

        // -------------- calculate shorted path -----------------

        Button route = (Button) findViewById(R.id.buttonRoutingG);
        route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tStart = System.currentTimeMillis();
                markuse.setPosition(new LatLng(uselat,uselng));
                if (markefinal != null){
                    markefinal.remove();
                }
                int number_of_nodes = 0;
                final Document[] doc = new Document[1];
                for (Marker marker : markers) {
                    number_of_nodes++;
                }
                Log.i("Total marker ",String.valueOf(number_of_nodes));
                if (number_of_nodes >= 2) {
                    final int adjacency_matrix[][] = new int[number_of_nodes + 1][number_of_nodes + 1];

                    final int finalNumber_of_nodes = number_of_nodes;

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            // -------------------------------- create Matrix --------------------------------
                            for (int i = 1; i <= finalNumber_of_nodes; i++) {
                                start = markers.get(i - 1).getPosition();
                                System.out.println(markers.get(i - 1).getTitle());
                                try {
                                    for (int j = i + 1; j <= finalNumber_of_nodes; j++) {
                                        if (i == j) {
                                            adjacency_matrix[i][j] = 0;
                                        } else {
                                            end = markers.get(j - 1).getPosition();
                                            Log.i("Marker ", markers.get(i - 1).getTitle() + " - " + markers.get(j - 1).getTitle());
                                            doc[0] = md.getDocument(start, end, GMapV2Direction.MODE_DRIVING);
                                            int vale = md.getDurationValue(doc[0]);
                                            adjacency_matrix[i][j] = vale;
                                            adjacency_matrix[j][i] = vale;
                                        }
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(),".:: Please ,try again ::.",Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }).run();
                    tEnd = System.currentTimeMillis();
                    tSum = (tEnd - tStart) / 1000.0;

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //-------------------- routing shorted path by Greedy Algorithm ----------------

                            shortedpath route = new shortedpath();
                            path = route.tsp(adjacency_matrix);
                            if (path != null) {
                                String str = "";
                                for (int q : path) {
                                    str += " " + markers.get(q - 1).getTitle();
                                }
                                new AlertDialog.Builder(MapsGroup.this)
                                        .setTitle("Route all friends")
                                        .setMessage("Route " + str + " and press destination")
                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                tStart = System.currentTimeMillis();
                                                zoom();
                                                GMapV2Direction md = new GMapV2Direction();
                                                if (lineArr != null) {
                                                    for (Polyline polyline : lineArr)
                                                        polyline.remove();
                                                }
                                                if (linePass != null) {
                                                    for (Polyline polyline : linePass)
                                                        polyline.remove();
                                                }
                                                int totaltime = 0, totaldis = 0, vale, dis;
                                                try {
                                                    int a = 0;
                                                    for (int b : path) {
                                                        b = b - 1;
                                                        start = markers.get(a).getPosition();
                                                        end = markers.get(b).getPosition();
                                                        Document docR = md.getDocument(start, end, GMapV2Direction.MODE_DRIVING);
                                                        ArrayList<LatLng> directionPoint = md.getDirection(docR);
                                                        vale = md.getDurationValue(docR);
                                                        dis = md.getDistanceValue(docR);
                                                        totaltime += vale;
                                                        totaldis += dis;
                                                        PolylineOptions rectLine = new PolylineOptions().width(4).color(Color.RED);
                                                        for (int i = 0; i < directionPoint.size(); i++) {
                                                            rectLine.add(directionPoint.get(i));
                                                        }
                                                        line = mMap.addPolyline(rectLine);
                                                        lineArr.add(line);
                                                        a = b;
                                                    }
                                                }catch (ArrayIndexOutOfBoundsException e){
                                                    e.printStackTrace();
                                                    Toast.makeText(getApplicationContext(),".:: Please ,try again ::.",Toast.LENGTH_LONG).show();
                                                }
                                                totaltime2 = totaltime;
                                                totaldis2 = totaldis;
                                                settime(totaltime);
                                                setdis(totaldis);
                                                chklast = true;
                                                chkline = true;
                                                tEnd = System.currentTimeMillis();
                                                tSum += (tEnd - tStart) / 100.0;
                                                Log.i("total time ",String.valueOf(tSum));
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            } else {
                                new AlertDialog.Builder(MapsGroup.this)
                                        .setTitle("Sorry, we have some problem")
                                        .setMessage("please press start again")
                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                zoom();
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            }

                        }
                    }).run();
                }else {
                    Toast.makeText(getApplicationContext(),".::Please wait friends accept::.",Toast.LENGTH_LONG).show();
                }

            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (chklast) {
                    start = markers.get(path.get(path.size()-1)-1).getPosition();
                    doc = md.getDocument(start, latLng, GMapV2Direction.MODE_DRIVING);
                    address = md.getEndAddress(doc);
                    ArrayList<LatLng> directionPoint = md.getDirection(doc);
                    int vale = md.getDurationValue(doc);
                    int dis = md.getDistanceValue(doc);
                    totaltime2 += vale;
                    totaldis2 += dis;
                    markefinal = mMap.addMarker(new MarkerOptions().position(latLng).title("Terminal")
                            .snippet(address).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                    PolylineOptions rectLine = new PolylineOptions().width(4).color(Color.RED);
                    for (int i = 0; i < directionPoint.size(); i++) {
                        rectLine.add(directionPoint.get(i));
                    }
                    line = mMap.addPolyline(rectLine);
                    lineArr.add(line);

                    zoom();
                    settime(totaltime2);
                    setdis(totaldis2);
                    chklast = false;
                }
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (!chkline) {
                    if (lineArr != null) {
                        for (Polyline polyline : lineArr)
                            polyline.remove();
                    }
                    LatLng markerPosition = marker.getPosition();
                    doc = md.getDocument(new LatLng(uselat,uselng), markerPosition, GMapV2Direction.MODE_DRIVING);
                    int vale = md.getDurationValue(doc);
                    int dis = md.getDistanceValue(doc);
                    settime(vale);
                    setdis(dis);
                    ArrayList<LatLng> directionPoint = md.getDirection(doc);
                    PolylineOptions rectLine = new PolylineOptions().width(4).color(Color.BLUE);
                    for (int i = 0; i < directionPoint.size(); i++) {
                        rectLine.add(directionPoint.get(i));
                    }
                    line = mMap.addPolyline(rectLine);
                    lineArr.add(line);
                }
                return false;
            }
        });

        //-----------------------------------Filter------------------------------------------

        XMPPConnection connection = StaticConnection.getConnection();
        connection.addPacketListener(new PacketListener() {
            @Override
            public void processPacket(Packet packet) {
                LocationMe message = (LocationMe) packet;
                if (message.getBody() != null) {
                    final String nick = StringUtils.parseResource(message.getFrom());
                    if (!message.getBody().equals("Request")){
                        Log.i("MapsGroup",message.getBody());
                        if(!nick.equals(arrData[1]) && !nick.equals(ListFriends.getUserCore())) {
                            final String lo[] = message.getBody().split(",");
                            Log.i("MapsGroup","Pass");
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    final Double a = Double.parseDouble(lo[0]);
                                    final Double b = Double.parseDouble(lo[1]);
                                    doc = md.getDocument(start, new LatLng(b, a), GMapV2Direction.MODE_DRIVING);
                                    markeruse = markers;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            j = 0;
                                            for (Marker mk : markers){
                                                if (mk.getTitle().equals(nick)) {
                                                    if (!chkline) {             // if has line just no mark point
                                                        chkin = true;
                                                        mk.remove();
                                                        marke = mMap.addMarker(new MarkerOptions().position(new LatLng(b, a))
                                                                    .title(nick).snippet(md.getEndAddress(doc))
                                                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(b, a), 16));
                                                        markeruse.add(marke);
                                                        markeruse.remove(j);
                                                        break;
                                                    }
                                                }
                                                j++;
                                            }
                                            if (!chkin){
                                                marke = mMap.addMarker(new MarkerOptions().position(new LatLng(b, a))
                                                        .title(nick).snippet(md.getEndAddress(doc))
                                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(b, a), 16));
                                                markeruse.add(marke);
                                            }else {
                                                chkin = false;
                                            }
                                            for (Marker l : markers)
                                            System.out.println(l.getTitle());
                                            markers = markeruse;
                                        }
                                    });
                                }
                            }).run();
                        }
                    }
                }
            }
        }, new LocationMeTypeFilter(LocationMe.Type.groupchat));
    }

    private void zoom(){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        final LatLngBounds bounds = builder.build();

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
                mMap.setOnCameraChangeListener(null);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_maps_group, menu);
        return true;
    }

    private void settime (int total){
        if (total < 60) {
            timeuse.setText(String.valueOf(total) + " sec");
        }
        else {
            int h = total / 3600;
            int dm = total % 3600;
            int m = dm / 60;
            int s = dm % 60;
            if (h > 0) {
                timeuse.setText(String.valueOf(h) + " Hour " + String.valueOf(m) + " Min " + String.valueOf(s) + " Sec");
            }
            else {
                timeuse.setText(String.valueOf(m) + " Min " + String.valueOf(s) + " Sec");
            }
        }
    }
    private void setdis(int total){
        if (total < 1000) {
            disuse.setText(String.valueOf(total) + " m.");
        }else {
            int km = total / 1000;
            int m = total % 1000;
            disuse.setText(String.valueOf(km) + " km " + String.valueOf(m) + " m");
        }
    }

    protected void onStart() {
        super.onStart();
        mLocationClient.connect();
    }

    protected void onStop() {
        super.onStop();
        mLocationClient.disconnect();
    }

    private boolean isServicesAvailable() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        return (resultCode == ConnectionResult.SUCCESS);
    }

    private GooglePlayServicesClient.ConnectionCallbacks mCallback = new GooglePlayServicesClient.ConnectionCallbacks() {
        public void onConnected(Bundle bundle) {
            Toast.makeText(getApplicationContext(), "Services connected", Toast.LENGTH_SHORT).show();

            LocationRequest mRequest = new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(5000).setFastestInterval(1000);

            mLocationClient.requestLocationUpdates(mRequest, locationListener);
        }

        public void onDisconnected() {
            Toast.makeText(getApplicationContext(), "Services disconnected", Toast.LENGTH_SHORT).show();
        }
    };

    private GooglePlayServicesClient.OnConnectionFailedListener mListener = new GooglePlayServicesClient.OnConnectionFailedListener() {
        public void onConnectionFailed(ConnectionResult result) {
            Toast.makeText(getApplicationContext(), "Services connection failed", Toast.LENGTH_SHORT).show();
        }
    };

    private LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            uselat = location.getLatitude();
            uselng = location.getLongitude();
        }
    };
}
