package com.example.myapplication.User;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import com.example.myapplication.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
public class MyLocation extends FragmentActivity implements OnMapReadyCallback
        , GoogleApiClient.ConnectionCallbacks , GoogleApiClient.OnConnectionFailedListener {
    Button Next, next2;
    public static final String SHARED_PREFS = "sharedPrefs";
    ArrayList<String> itemsList =  new ArrayList<String>();
    String radioValue, NumberOfAmountWeight, Note;
    ArrayList<String> locationList= new ArrayList<String>();
    List<Address> addresses = new ArrayList<Address>();
    GoogleMap mMap;
    Geocoder geocoder;
    SupportMapFragment supportMapFragment;
    Boolean isPermissionGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    LatLng latLng =  new LatLng(0,0);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location);
        checkMyPermission();
        Toast.makeText(MyLocation.this, "Location and Internet must be turn on!", Toast.LENGTH_LONG).show();
        itemsList = (ArrayList<String>) getIntent().getSerializableExtra("itemsList");
        Bundle bundle = getIntent().getExtras();
        radioValue = bundle.getString("radioValue");
        NumberOfAmountWeight = bundle.getString("Number Of Amount-Weight");
        Note = bundle.getString("Note");
        next2 = (Button) findViewById(R.id.next2);
        next2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next2.setText("Getting now!!!");
                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MyLocation.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_PERMISSION_REQUEST_CODE);
                } else {
                    getCurrentLocation();
                }
            }
        });
        Next = (Button) findViewById(R.id.next);
        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(locationList.isEmpty()){
                    Toast.makeText(MyLocation.this, "you should select your location", Toast.LENGTH_LONG).show();
                }else{
                    Intent intent = new Intent(MyLocation.this, OrderInformation.class);
                    intent.putExtra("itemsList"  , itemsList);
                    intent.putExtra("locationList"  , locationList);
                    intent.putExtra("radioValue", radioValue);
                    intent.putExtra("Number Of Amount-Weight", NumberOfAmountWeight);
                    intent.putExtra("Note", Note);
                    startActivity(intent);
                }
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permission is denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void getCurrentLocation() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager
                .PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(MyLocation.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {

                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(getApplicationContext())
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestlocIndex = locationResult.getLocations().size() - 1;
                            double lati = locationResult.getLocations().get(latestlocIndex).getLatitude();
                            double longi = locationResult.getLocations().get(latestlocIndex).getLongitude();
                            Location location = new Location("providerNA");
                            location.setLongitude(longi);
                            location.setLatitude(lati);
                            geocoder = new Geocoder(MyLocation.this, Locale.getDefault());
                            try {
                                  //addresses = geocoder.getFromLocation(  19.1178963, 42.0866781, 1);
                                addresses = geocoder.getFromLocation(  18.2416843, 42.4419994, 1);
                                if(addresses.get(0).getFeatureName().equals("Abha") || addresses.get(0).getAdminArea().equals
                                        ("Abha Province") || addresses.get(0).getSubAdminArea().equals("Abha") ){
                                    String state = addresses.get(0).getAdminArea();
                                    String address = addresses.get(0).getAddressLine(0);
                                    locationList.add(address);
                                    locationList.add(state);
                                    locationList.add(String.valueOf(lati));
                                    locationList.add(String.valueOf(longi));
                                }
                                else{
                                    next2.setText("Try Again later");
                                    Toast.makeText(MyLocation.this, "The App works just for Abha Province work right now! "
                                            , Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,13);
                            mMap.moveCamera(cameraUpdate);
                            mMap.addMarker(new MarkerOptions().position(latLng).title("my location"));
                            next2.setText("Done (:");
                        } else {
                        }
                    }
                }, Looper.getMainLooper());
    }
    private void initMap() {
        if (isPermissionGranted) {
            supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            supportMapFragment.getMapAsync(this);
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        LatLng TutorialsPoint = new LatLng( 18.2416843, 42.4419994);
        mMap.setMinZoomPreference(9);
        this.mMap.addMarker(new
                MarkerOptions().position(TutorialsPoint).title("initial location"));
        this.mMap.moveCamera(CameraUpdateFactory.newLatLng(TutorialsPoint));
    }
    public void checkMyPermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                isPermissionGranted = true;
                initMap();
            }
            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), "");
                intent.setData(uri);
                startActivity(intent);
            }
            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }
    @Override
    public void onConnectionSuspended(int i) {
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
}

