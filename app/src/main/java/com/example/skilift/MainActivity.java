package com.example.skilift;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.location.LocationListener;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Bundle;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.security.Security;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapView rsMapView;
    private GoogleMap gmap;
    private LocationManager locationManager;
    private LocationListener listener;
    private Bundle mainActBundle;

    public static final int LOCATION_REQUEST = 0;

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getBoolean(R.bool.portrait_only)) {
            //noinspection AndroidLintSourceLockedOrientationActivity
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        setContentView(R.layout.activity_main);

        mainActBundle = new Bundle();

        rsMapView = findViewById(R.id.mapView);

        rsMapView.onCreate(savedInstanceState);
        rsMapView.getMapAsync(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "LANDSCAPE", Toast.LENGTH_SHORT).show();
            onSaveInstanceState(mainActBundle);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "PORTRAIT", Toast.LENGTH_SHORT).show();
            onSaveInstanceState(mainActBundle);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        rsMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mapInitialize();
                } else {
                    FloatingActionButton centerLoc = findViewById(R.id.centerLocButton);
                    centerLoc.hide();
                }
            }
            default: {
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        rsMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        rsMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        rsMapView.onStop();
    }

    @Override
    protected void onPause() {
        rsMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        rsMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        rsMapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;

        // Zoom into users location
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        FloatingActionButton centerLoc = findViewById(R.id.centerLocButton);
        centerLoc.setBackgroundColor(Color.BLACK);

        requestLocationPerms();
    }

    @SuppressLint("MissingPermission")
    // ONLY CALL THIS IN DEFINED CHECKS OR ELSE RIP BROKEN APP CITY.
    private void mapInitialize() {
        FloatingActionButton centerLoc = findViewById(R.id.centerLocButton);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
        gmap.setMyLocationEnabled(true);
        gmap.getUiSettings().setMyLocationButtonEnabled(false);
        gmap.getUiSettings().setZoomGesturesEnabled(true);
        gmap.getUiSettings().setRotateGesturesEnabled(false);

        centerLoc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                centreMapOnLocation(getLastKnownLoc());
            }
        });
        centerLoc.show();

        centreMapOnLocation(getLastKnownLoc());
    }

    public void centreMapOnLocation(Location location) {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14), 100, null);
    }

    private void requestLocationPerms() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("Location Permissions Request");
                alertBuilder.setMessage("Location permissions are needed in Ski Lift so rideshare providers can find you much easier. " +
                        "If denied, you can set a location marker when requesting as pick up.");
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                LOCATION_REQUEST);
                    }
                });

                AlertDialog alert = alertBuilder.create();
                alert.show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_REQUEST);
            }
        } else {
            mapInitialize();
        }
    }

    private Location getLastKnownLoc() {
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {

            //noinspection AndroidLintMissingPermission
            Location l = locationManager.getLastKnownLocation(provider);

            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }
}
