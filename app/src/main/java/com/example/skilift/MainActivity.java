package com.example.skilift;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.libraries.places.api.Places;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, SharedPreferences.OnSharedPreferenceChangeListener {
    private MapView rsMapView;
    private GoogleMap gmap;
    private LocationManager locationManager;
    private LocationListener listener;
    private Bundle mainActBundle;
    private PlacesClient gmapPlacesClient;
    private AutocompleteSupportFragment autocompleteFragment;
    private Marker marker;
    private Button confirmDest;
    private Button pickFromList;
    private Place currentSelection;
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

        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        Intent intent = getIntent();
        String userType = intent.getStringExtra("UserType");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

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

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.googleApiKey));
        }

        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        initAutocomplete();

        gmapPlacesClient = Places.createClient(this);
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
        gmap = googleMap;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        boolean nightModeSetting = sp.getBoolean(getString(R.string.key_dark_theme_toggle), false);

        if(!nightModeSetting) {
            gmap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_light));
        }
        else {
            gmap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_dark));
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

        FloatingActionButton centerLoc = findViewById(R.id.centerLocButton);
        centerLoc.setBackgroundColor(Color.BLACK);
        requestLocationPerms();
        rsMapView.onResume();
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
        EditText etp = autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_input);
        etp.setTextColor(getColor(R.color.secondaryTextColor));

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
     * Initializes the Google Map instance in the main activity.
     */
    private void mapInitialize() {
        gmap.getUiSettings().setZoomGesturesEnabled(true);
        gmap.getUiSettings().setRotateGesturesEnabled(false);
        marker = gmap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).visible(false));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            FloatingActionButton centerLoc = findViewById(R.id.centerLocButton);

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
            gmap.setMyLocationEnabled(true);
            gmap.getUiSettings().setMyLocationButtonEnabled(false);

            centerLoc.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    centreMapOnLocation(getLastKnownLoc());
                }
            });
            centerLoc.show();

            centreMapOnLocation(getLastKnownLoc());
        }
    }

    /**
     * Center's the map to the passed in location.
     *
     * @param location - google maps location object being the location to center to.
     */
    public void centreMapOnLocation(Location location) {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14), 100, null);
    }

    /**
     * Center's the map to the passed in location.
     *
     * @param place - google maps place object being the location to center to.
     */
    public void centreMapOnLocation(Place place) {
        LatLng userLocation = place.getLatLng();
        gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14), 100, null);
    }


    /**
     * Center's the map to the passed in address.
     *
     * @param addr - google maps address object being the location to center to.
     */
    public void centreMapOnLocation(Address addr) {
        LatLng userLocation = new LatLng(addr.getLatitude(), addr.getLongitude());
        gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14), 100, null);
    }

    /**
     * Helper method that manages requesting location permissions.
     */
    private void requestLocationPerms() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
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

    /**
     * Gets the best, last known location based on accuracy from the location providers.
     *
     * @return Last known location, a Google Maps Location object.
     */
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

    private void toggleDarkTheme(boolean dark_theme) {
        if(dark_theme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.key_dark_theme_toggle))) {
            toggleDarkTheme(sharedPreferences.getBoolean(key, AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES));
        }
    }
}
