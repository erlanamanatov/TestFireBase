package com.example.erlan.testfirebase;

import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private String userName;
    Location mLastLocation;
    GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    FirebaseDatabase database;
    DatabaseReference myRef;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static final int RC_SIGN_IN = 1;
    private FirebaseAuth mFirebaseAuth;
    List<Character> errorList;
    HashMap<String, Marker> markersOnScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        errorList = new ArrayList<>();
        errorList.add('.');
        errorList.add('#');
        errorList.add('$');
        errorList.add('[');
        errorList.add(']');

        markersOnScreen = new HashMap<>();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = firebaseAuth.getCurrentUser();
                //User is signed out
                if (mFirebaseUser == null) {
                    userName = "Anonymous";
                } else {
                    //onSignInInit(mFirebaseUser);
                    String email = mFirebaseUser.getEmail();
                    char[] ech = email.toCharArray();
                    StringBuilder username = new StringBuilder();
                    for (char i : ech) {
                        if (!errorList.contains(i)) username.append(i);
                    }
                    userName = username.toString();
                    //userName = "ok";
                    //Toast.makeText(MapsActivity.this, userName, Toast.LENGTH_LONG).show();
                }
                //Toast.makeText(MapsActivity.this, "here", Toast.LENGTH_SHORT).show();
                // if (mLastLocation != null) sendCoordinates(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            }
        };
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);


        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mapFragment.getMapAsync(this);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("locations");


        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//Toast.makeText(MapsActivity.this, "update", Toast.LENGTH_SHORT).show();
                // Get data from database and deserialize
//                Messages m = dataSnapshot.getValue(Messages.class);
//                String text = m.getName()+": "+m.getText();
//                chat.append(text+"\n");

//                Locs loc = dataSnapshot.getValue(Locs.class);
//                LatLng coordinates = new LatLng(loc.getLatitude(), loc.getLongitude());
//                Marker newMarker = mMap.addMarker(new MarkerOptions().position(coordinates).title(dataSnapshot.getKey()));
//                markersOnScreen.put(dataSnapshot.getKey(), newMarker);
                drawNewPosition(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                Locs loc = dataSnapshot.getValue(Locs.class);
//                LatLng coordinates = new LatLng(loc.getLatitude(), loc.getLongitude());
//                mMap.addMarker(new MarkerOptions().position(coordinates).title(dataSnapshot.getKey()));
                drawNewPosition(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        //Connects listener to specific reference in the database
        myRef.addChildEventListener(listener);


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng bishkek = new LatLng(42.8709181, 74.6144781);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(bishkek));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bishkek, 12));

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);


    }


    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onBackPressed()

    {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
           // Toast.makeText(MapsActivity.this, String.valueOf(mLastLocation.getLatitude()) + " " + String.valueOf(mLastLocation.getLongitude()), Toast.LENGTH_LONG).show();
            sendCoordinates(mLastLocation.getLatitude(), mLastLocation.getLongitude());
//            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
//            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));

        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void sendCoordinates(double latitude, double longitude) {
        Locs currentLocation = new Locs(latitude, longitude);
        myRef.child(userName).setValue(currentLocation);
    }

    @Override
    public void onLocationChanged(Location location) {
        //Toast.makeText(MapsActivity.this, "Location changed", Toast.LENGTH_SHORT).show();
        mLastLocation = location;


        double dLatitude = mLastLocation.getLatitude();
        double dLongitude = mLastLocation.getLongitude();
//        marker = map.addMarker(new MarkerOptions().position(new LatLng(dLatitude, dLongitude))
//                .title("My Location").icon(BitmapDescriptorFactory
//                        .defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        //mMap.addMarker(new MarkerOptions().position(new LatLng(dLatitude, dLongitude)).title(userName));
        sendCoordinates(dLatitude, dLongitude);
    }

    private void drawNewPosition(DataSnapshot dataSnapshot){
        String title = dataSnapshot.getKey();
        if (markersOnScreen.containsKey(title)){
            markersOnScreen.get(title).remove();
            markersOnScreen.remove(title);

        }
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.my_marker3);
        Locs loc = dataSnapshot.getValue(Locs.class);
        LatLng coordinates = new LatLng(loc.getLatitude(), loc.getLongitude());
        Marker newMarker = mMap.addMarker(new MarkerOptions().position(coordinates).title(dataSnapshot.getKey()).icon(icon));
        markersOnScreen.put(title, newMarker);


    }
}
