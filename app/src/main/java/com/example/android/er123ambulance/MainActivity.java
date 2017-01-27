package com.example.android.er123ambulance;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.android.er123ambulance.callbacks.GetDriverData;
import com.example.android.er123ambulance.data.Driver;
import com.example.android.er123ambulance.data.DriverLocation;
import com.example.android.er123ambulance.firebase.FirebaseHandler;
import com.example.android.er123ambulance.utilities.Locations;
import com.example.android.er123ambulance.utilities.OfficeApp;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GetDriverData{

    private GoogleMap mMap;
    private ProfileDrawerItem mProfile = null;
    private AccountHeader header;
    private Drawer result;
    private ProgressDialog mProgressDialog;
    private LocationManager mLocationManager;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final int REQUEST_LOCATION = 0x2;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Getting Things Ready");
        mProgressDialog.setCancelable(false);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mProfile = new ProfileDrawerItem();

        FirebaseHandler.getDriverInfo(FirebaseAuth.getInstance().getCurrentUser().getEmail(),this);


        header = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .withSelectionListEnabledForSingleProfile(false)
                .build();

        PrimaryDrawerItem aboutUs = new PrimaryDrawerItem().withIdentifier(1).withName("About Us").withIcon(R.drawable.aboutusicon);
        PrimaryDrawerItem logOff = new PrimaryDrawerItem().withIdentifier(2).withName("Log Off").withIcon(R.drawable.logoff);

        result = new DrawerBuilder().withActivity(this).withToolbar(toolbar).addDrawerItems(
                aboutUs,
                logOff
        ).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                if(position == 2)
                {
                    FirebaseAuth.getInstance().signOut();
                    MainActivity.this.finish();
                    startActivity(new Intent(MainActivity.this,SignIn.class));
                }
                return false;
            }
        }).withAccountHeader(header).build();


        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);

        try {
            getLocation();
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void getLocation() throws Settings.SettingNotFoundException {

        int off = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
        if (off == 0) {
            Locations.displayLocationSettingsRequest(this,MainActivity.this);
        }
        else {
            requestLocation();
        }

    }

    private void requestLocation()
    {

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION);
            return;
        }

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, mLocationListener);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLocation();
                }
                else
                    return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i("TAG", "User agreed to make required location settings changes.");
                        requestLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i("TAG", "User chose not to make required location settings changes.");
                        Toast.makeText(this, "Location must be turned On", Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                        try {
                            getLocation();
                        } catch (Settings.SettingNotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(29.9556449,30.9134569),16.0f));
    }

    @Override
    public void getDriverData(Driver driver) {

        header.addProfiles(new ProfileDrawerItem().withName(driver.getDriverName())
        .withEmail(driver.getDriverEmail())
        .withIcon(R.drawable.ahmedelsherif));
    }

    LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            Log.e("OnLocationListener","True");

            DriverLocation driverLocation = new DriverLocation();
            driverLocation.setLatPosition(Double.toString(location.getLatitude()));
            driverLocation.setLongPosition(Double.toString(location.getLongitude()));
            FirebaseHandler.sendDriverLocationToBackOffice(driverLocation, FirebaseAuth.getInstance().getCurrentUser().getEmail()
                    , FirebaseDatabase.getInstance(OfficeApp.officeApp(MainActivity.this)));
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
}
