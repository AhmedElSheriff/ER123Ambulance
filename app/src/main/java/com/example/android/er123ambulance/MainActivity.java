package com.example.android.er123ambulance;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.widget.Button;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.example.android.er123ambulance.callbacks.CheckExistance;
import com.example.android.er123ambulance.callbacks.GetDriverData;
import com.example.android.er123ambulance.callbacks.GetPatientData;
import com.example.android.er123ambulance.data.Driver;
import com.example.android.er123ambulance.data.PendingRequests;
import com.example.android.er123ambulance.firebase.FirebaseHandler;
import com.example.android.er123ambulance.utilities.Locations;
import com.example.android.er123ambulance.utilities.OfficeApp;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GetDriverData {

    private GoogleMap mMap;
    private ProfileDrawerItem mProfile;
    private AccountHeader header;
    private Drawer result;
    private ProgressDialog mProgressDialog;
    private LocationManager mLocationManager;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final int REQUEST_LOCATION = 0x2;
    private Driver mDriver;
    private Marker yourLoc = null;
    private PendingRequests mRequest;
    private Button arrivalButton;
    Location location1 = new Location("");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(this,"Logged In",Toast.LENGTH_SHORT).show();

        arrivalButton = (Button) findViewById(R.id.arrive_btn);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Getting Things Ready");
        mProgressDialog.setCancelable(false);

        mDriver = new Driver();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mProfile = new ProfileDrawerItem();

        FirebaseHandler.getDriverInfo(FirebaseAuth.getInstance().getCurrentUser().getEmail(), this);


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
                if (position == 2) {
                    FirebaseAuth.getInstance().signOut();
                    MainActivity.this.finish();
                    startActivity(new Intent(MainActivity.this, SignIn.class));
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
            Locations.displayLocationSettingsRequest(this, MainActivity.this);
        } else {
            requestLocation();
        }

    }

    private void requestLocation() {

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION);
            return;
        }

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, mLocationListener);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, mLocationListener);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLocation();
                } else
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

        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(29.9556449,30.9134569),16.0f));
    }

    @Override
    public void getDriverData(Driver driver) {

        Log.e("TAGKEY","Inside Get Driver Data");
        mProfile.withName(driver.getDriverName()).withEmail(driver.getDriverEmail()).withIcon(R.drawable.ahmedelsherif);
        header.addProfiles(mProfile);
        mDriver = driver;
        FirebaseHandler.checkIfPatientExist(mDriver.getDriverEmail(), new CheckExistance() {
            @Override
            public void onSearchComplete(boolean isFound) {
                if(isFound)
                {
                    FirebaseHandler.setDriverAvailability(mDriver,"false");
                    Log.e("Car Availability","Set to false");
                }
                else
                {
                    FirebaseHandler.setDriverAvailability(mDriver,"true");
                    Log.e("Car Availability","Set to true");
                }
            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.removeUpdates(mLocationListener);
         mLocationListener = null;
    }

    LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(final Location location) {
            Log.e("OnLocationListener","True");

            final BitmapDescriptor patient = BitmapDescriptorFactory.fromResource(R.drawable.patient);
            BitmapDescriptor vehicle = BitmapDescriptorFactory.fromResource(R.drawable.goodambulance);
            final LatLng currentLatLng = new LatLng(location.getLatitude(),location.getLongitude());
            if(yourLoc != null)
            {
                yourLoc.remove();
                yourLoc = null;
            }


            if(yourLoc == null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16.0f));
                yourLoc = mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title(mDriver.getPlateChars() +" " + mDriver.getPlateNums())
                        .icon(vehicle));
            }
            mDriver.setLatPosition(Double.toString(location.getLatitude()));
            mDriver.setLongPosition(Double.toString(location.getLongitude()));

            FirebaseHandler.sendDriverLocationToBackOffice(mDriver,Double.toString(location.getLatitude()),
                    Double.toString(location.getLongitude()),FirebaseDatabase.getInstance(OfficeApp.officeApp(MainActivity.this)));
//            FirebaseHandler.getPatientLocation(mDriver.getDriverEmail(), new GetPatientData() {
//                @Override
//                public void getDriverData(PendingRequests requests) {
//                     mRequest = requests;
//                }
//            });

           // LatLng target = new LatLng(Double.parseDouble(mRequest.getLatPosition()),Double.parseDouble(mRequest.getLongPosition()));

            FirebaseHandler.getPatientLocation(mDriver.getDriverEmail(), new GetPatientData() {
                @Override
                public void getDriverData(PendingRequests request) {
                    location1.setLatitude(Double.parseDouble(request.getLatPosition()));
                    location1.setLongitude(Double.parseDouble(request.getLongPosition()));
                    mMap.addMarker(new MarkerOptions().position(new LatLng(location1.getLatitude(), location1.getLongitude())).title("Patient: ")
                            .icon(patient));
//                                        location1.setLatitude(29.9887649);
//                                        location1.setLongitude(30.9422747);
                }
            });


            LatLng target = new LatLng(location1.getLatitude(),location1.getLongitude());
            GoogleDirection.withServerKey(getString(R.string.google_maps_key))
                    .from(currentLatLng)
                    .to(target)
                    .execute(new DirectionCallback() {
                        @Override
                        public void onDirectionSuccess(Direction direction, String rawBody) {
                            if(direction.isOK()) {
                                Leg leg = direction.getRouteList().get(0).getLegList().get(0);
                                ArrayList<LatLng> directionPositionsList = leg.getDirectionPoint();
                                PolylineOptions polylineOptions = DirectionConverter.createPolyline(MainActivity.this, directionPositionsList
                                ,5, Color.RED);
                                final Polyline polyline = mMap.addPolyline(polylineOptions);
                                Log.e("Direction :", "Direction Success");




                                Log.e("Distance :", Float.toString(location.distanceTo(location1)));
                                if(location.distanceTo(location1) <= 300)
                                {
                                    arrivalButton.setVisibility(View.VISIBLE);
                                    arrivalButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            polyline.remove();
                                        }
                                    });
                                }
                            }
                            else
                            {
                                
                            }
                              //  Log.e("Direction :", direction.getErrorMessage());
                        }

                        @Override
                        public void onDirectionFailure(Throwable t) {
                            Log.e("Direction :", "Failed :" + t.getLocalizedMessage());
                        }
                    });
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
