package com.example.ptvimproved24;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ptvimproved24.databinding.ActivityRoutedetailsBinding;
import com.example.ptvimproved24.datastructures.PatternRequestHandler;
import com.example.ptvimproved24.datastructures.RouteDetailsAdapter;
import com.example.ptvimproved24.datastructures.RouteDirectionsRequestsHandler;
import com.example.ptvimproved24.datastructures.Stop;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.microsoft.maps.Geopoint;
import com.microsoft.maps.MapElementLayer;
import com.microsoft.maps.MapElementTappedEventArgs;
import com.microsoft.maps.MapIcon;
import com.microsoft.maps.MapRenderMode;
import com.microsoft.maps.MapView;
import com.microsoft.maps.OnMapElementTappedListener;

import java.util.ArrayList;
import java.util.List;
public class RouteDetails extends AppCompatActivity {
//public class RouteDetails extends AppCompatActivity implements OnMapReadyCallback {
    // Used for saved routes, showing both direction
    private static final String TAG = "RouteDetails";
    private float latitude=-37.818078f;
    private float longitude=144.96681f;
    private static final Geopoint FlinderSt = new Geopoint(-37.818078, 144.96681);
    private LocationManager locationManager;
    int REQUEST_LOCATION =99;

    private MapView mMapView;
    private ListView mListView;
    private RouteDirectionsRequestsHandler routeDirectionHandler;
    private RouteDetailsAdapter routeDetailsAdapter;
    private MapElementLayer mPinLayer;
    private MapIcon pushpin;
    private int lastSelectedStopId=-1;

    PatternRequestHandler patternRequestHandler = new PatternRequestHandler(this);

//    private GoogleMap mMap;
    private ActivityRoutedetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routedetails);
        String run_ref = getIntent().getStringExtra("run_ref");
        if(run_ref==null){
            run_ref="8724";
        }
        int route_id = getIntent().getIntExtra("route_id",2); // Get Route details to display
        int route_type = getIntent().getIntExtra("route_type",1); // Get Route details to display
        getUserLocation();
        getGeoLocation();

        mListView = (ListView) findViewById(R.id.route_detailslist);
        routeDetailsAdapter = new RouteDetailsAdapter(this, R.layout.routedetails_view, new ArrayList<>());
        mListView.setAdapter(routeDetailsAdapter);

//        try {
//            patternRequestHandler.getPatternRequest(route_type,run_ref,routeDetailsAdapter);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(RouteDetails.this, stops.class);
            Stop clickedStop = routeDetailsAdapter.getItem(i);
            intent.putExtra("index", clickedStop.getStop_id());
            intent.putExtra("type", clickedStop.getRouteType());
            intent.putExtra("name", clickedStop.getStop_name());
            intent.putExtra("suburb", clickedStop.getStop_suburb());
            startActivity(intent);
        }
    });


        mMapView = new MapView(this, MapRenderMode.RASTER);
        mMapView.setCredentialsKey(BuildConfig.CREDENTIALS_KEY);
        ((FrameLayout)findViewById(R.id.map_view)).addView(mMapView);
        mMapView.onCreate(savedInstanceState);
        mPinLayer = new MapElementLayer();
        mMapView.getLayers().add(mPinLayer);
        try {
            patternRequestHandler.getPatternRequest(route_type,run_ref,routeDetailsAdapter,mPinLayer,mMapView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.setTransitFeaturesVisible(true);
        mPinLayer.addOnMapElementTappedListener(new OnMapElementTappedListener() {
            @Override
            public boolean onMapElementTapped(MapElementTappedEventArgs e) {
                Log.d("","PinElement:"+e.mapElements);
                if(e.mapElements.size()>0){
                    pushpin = (MapIcon) e.mapElements.get(0);
                    String[] stopdetails = pushpin.getFlyout().getDescription().split("\\:");
                    int stopid = Integer.parseInt(stopdetails[stopdetails.length-1]);
                    Log.d("","stopid:"+stopid);
                    if(stopid == lastSelectedStopId){
                        Intent i = new Intent(RouteDetails.this,stops.class);
                        i.putExtra("index",stopid);
                        i.putExtra("type",route_type);
                        i.putExtra("name",pushpin.getFlyout().getTitle());
                        i.putExtra("suburb",stopdetails[1].split("\\n")[0]);
                        startActivity(i);
                    }
                    Log.d("","Last click stopid:"+lastSelectedStopId);
                    lastSelectedStopId = stopid;
                    Toast.makeText(getApplicationContext(),"Click again to see next departures of "+pushpin.getFlyout().getTitle(),Toast.LENGTH_SHORT);
                }
                return false;
            }
        });


    }

    private void getUserLocation() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(RouteDetails.this, "Location Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(RouteDetails.this, "Location Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject Location permission,some service may not available\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
    }

    private void getGeoLocation() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 100, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                }
            });
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Log.d("","locationfragStopselect:"+location);
            if(location != null) {
                latitude = (float) location.getLatitude();
                longitude = (float) location.getLongitude();
            }
        } catch (SecurityException e) {
            Toast.makeText(this, "GPS Permission has been disabled", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
