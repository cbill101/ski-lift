package com.example.skilift.views;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;

import com.example.skilift.R;
import com.example.skilift.misc.Utils;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.libraries.places.api.Places;

import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, SharedPreferences.OnSharedPreferenceChangeListener {
    private MapView rsMapView;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener listener;
    private Bundle mainActBundle;
    private AutocompleteSupportFragment autocompleteFragment;
    private SharedPreferences sp;
    private Marker marker;
    private Button confirmDest;
    private Button pickFromList;
    private Place currentSelection;
    private boolean mLocationPermissionGranted;
    private boolean isProvider;

    public static final int LOCATION_REQUEST = 0;
    private static final int AUTOCOMPLETE_REQUEST = 1;
    private static final String TAG = "MainActivity";

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final String PICK_BTN_TEXT = "pick_btn_text";
    private static final String CONFIRM_DEST_TEXT = "pick_dest_text";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Locking in portrait for our purposes for now... is it really worth doing landscape unless it's a tablet?
        if (getResources().getBoolean(R.bool.portrait_only)) {
            //noinspection AndroidLintSourceLockedOrientationActivity
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        mLocationPermissionGranted = checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        Intent intent = getIntent();
        String userType = intent.getStringExtra("UserType");

        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sp.registerOnSharedPreferenceChangeListener(this);

        confirmDest = findViewById(R.id.confirmDestinationButton);
        pickFromList = findViewById(R.id.pickFromListButton);

        if(userType != null) {
            switch (userType) {
                case "Provider":
                    isProvider = true;
                    pickFromList.setText(getText(R.string.pick_a_request));
                    break;
                case "Requester":
                    isProvider = false;
                    pickFromList.setText(getText(R.string.pick_a_ride));
                    break;
            }
        }


        confirmDest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmSelectionAndProceed();
            }
        });

        pickFromList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFromList();
            }
        });

        mainActBundle = new Bundle();

        rsMapView = findViewById(R.id.mapView);

        rsMapView.onCreate(savedInstanceState);
        rsMapView.getMapAsync(this);

        if(savedInstanceState != null) {

        }
        else {
            getLocationPermission();
        }

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.googleApiKey));
        }

        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        initAutocomplete();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_context_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.account:
                startActivity(new Intent(getApplicationContext(), AccountPage.class));
                return true;
            case R.id.settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                return true;
            case R.id.chats:
                startActivity(new Intent(getApplicationContext(), ChatHistoryActivity.class));
                return true;
            case R.id.help:
                return true;
            default:
                return super.onOptionsItemSelected(item);
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

        outState.putString(PICK_BTN_TEXT, pickFromList.getText().toString());
        outState.putString(CONFIRM_DEST_TEXT, confirmDest.getText().toString());

        rsMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
                } else {
                    FloatingActionButton centerLoc = findViewById(R.id.centerLocButton);
                    centerLoc.hide();
                }
            }
            default: {
            }
        }
        updateLocationUI();
    }

    @Override
    protected void onResume() {
        rsMapView.onResume();
        super.onResume();
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
        mMap = googleMap;

        String themeChoice = sp.getString("key_theme_choice", "0");

        int nightModeFlags =
                getApplicationContext().getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;

        switch(themeChoice) {
            case "1":
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_light));
                break;
            case "2":
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_dark));
                break;
        }

        if(themeChoice.equals("0"))
        {
            switch (nightModeFlags) {
                case Configuration.UI_MODE_NIGHT_YES:
                    mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_dark));
                    break;
                case Configuration.UI_MODE_NIGHT_NO:
                case Configuration.UI_MODE_NIGHT_UNDEFINED:
                default:
                    PowerManager powerManager = (PowerManager)
                            getSystemService(Context.POWER_SERVICE);

                    if (powerManager.isPowerSaveMode()) {
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_dark));
                        break;
                    }

                    mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_light));
                    break;
            }
        }

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

        updateLocationUI();

        rsMapView.onResume();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            mMap.getUiSettings().setZoomGesturesEnabled(true);
            mMap.getUiSettings().setRotateGesturesEnabled(false);

            if(marker == null) {
                marker = mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).visible(false));
            }

            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            FloatingActionButton centerLoc = findViewById(R.id.centerLocButton);
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                centerLoc.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        centreMapOnLocation(getLastKnownLoc());
                    }
                });
                centerLoc.show();

                centreMapOnLocation(getLastKnownLoc());
            } else {
                mMap.setMyLocationEnabled(false);
                centerLoc.hide();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }

    }

    private void confirmSelectionAndProceed() {
        Intent intent = new Intent(this, Info.class);
        intent.putExtra("DestLatitude", marker.getPosition().latitude);
        intent.putExtra("DestLongitude", marker.getPosition().longitude);

        if(!isProvider)
            intent.putExtra("PickupLocation", getLastKnownLoc());

        intent.putExtra("PlaceName", currentSelection.getName());
        intent.putExtra("Provider", isProvider);
        startActivity(intent);
    }

    private void pickFromList() {
        Intent intent = new Intent(this, RideListActivity.class);
        intent.putExtra("Provider", isProvider);
        startActivity(intent);
    }

    private void initAutocomplete() {
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
        autocompleteFragment.setTypeFilter(TypeFilter.ESTABLISHMENT);
        autocompleteFragment.setHint("Where to?");

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                currentSelection = place;
                centreMapOnLocation(place);
                marker.setPosition(place.getLatLng());
                marker.setVisible(true);
                confirmDest.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    /**
     * Center's the map to the passed in location.
     *
     * @param location - google maps location object being the location to center to.
     */
    public void centreMapOnLocation(Location location) {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14), 100, null);
    }

    /**
     * Center's the map to the passed in location.
     *
     * @param place - google maps place object being the location to center to.
     */
    public void centreMapOnLocation(Place place) {
        LatLng userLocation = place.getLatLng();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14), 100, null);
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (!mLocationPermissionGranted) {
            if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("Location Permissions Request");
                alertBuilder.setMessage("Location permissions (specifically GPS) are used in Ski Lift to determine pickup location and help rideshare providers find you easier. " +
                        "If denied, you can set a location marker when requesting as pick up. " +
                        "If you're sure about denying for this app, tap/check don't ask again. You can always go to Settings -> Apps and manually turn on permissions.");
                alertBuilder.setPositiveButton("Got It", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                LOCATION_REQUEST);
                    }
                });

                AlertDialog alert = alertBuilder.create();
                alert.show();
            }
            else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST);
            }
        }
    }

    /**
     * Gets the best, last known location based on accuracy from the location providers.
     *
     * @return Last known location, a Google Maps Location object.
     */
    private Location getLastKnownLoc() {
        List<String> providers = locationManager.getAllProviders();
        Location bestLocation = null;

        try {
            if (mLocationPermissionGranted) {
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
            }
            else {
                bestLocation = new Location("DEFAULT_PROVIDER");
            }
        }
        catch (SecurityException e) {
            bestLocation = new Location("DEFAULT_PROVIDER");
        }

        return bestLocation;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("key_theme_choice")) {
            Utils.toggleTheme(sharedPreferences.getString(key, "0"));
        }
    }
}
