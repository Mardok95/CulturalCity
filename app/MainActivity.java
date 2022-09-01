package com.icloud.andreadimartino.bellini;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FusedLocationProviderClient mFusedLocationClient;

    private Double wayLatitude = 0.0 , wayLongitude = 0.0 ;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private StringBuilder stringBuilder;
    private boolean isContinue = false;
    private boolean isGPS = false;
    private CardView aboutUs;
    private CardView intSearch;
    private CardView advSearch;
    private CardView nearYou;
    private CardView km4city;
    private int clickId;
    Button button;
    String latitudine, longitudine;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.provaui);
        aboutUs = (CardView) findViewById(R.id.aboutUs);
        intSearch = (CardView) findViewById(R.id.intSearch);
        advSearch = (CardView) findViewById(R.id.advSearch);
        nearYou = (CardView) findViewById(R.id.nearYou);
        km4city = (CardView) findViewById(R.id.km4city);

        aboutUs.setOnClickListener(this);
        intSearch.setOnClickListener(this);
        advSearch.setOnClickListener(this);
        nearYou.setOnClickListener(this);
        km4city.setOnClickListener(this);

        setTitle("Welcome");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000); // 10 seconds
        locationRequest.setFastestInterval(5 * 1000); // 5 seconds

        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                        if (!isContinue) {
                            //Toast.makeText(MainActivity.this,String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude), Toast.LENGTH_SHORT).show();

                        } else {
                            stringBuilder.append(wayLatitude);
                            stringBuilder.append("-");
                            stringBuilder.append(wayLongitude);
                            stringBuilder.append("\n\n");
                            //Toast.makeText(MainActivity.this,stringBuilder.toString(), Toast.LENGTH_SHORT).show();

                        }
                        if (!isContinue && mFusedLocationClient != null) {
                            mFusedLocationClient.removeLocationUpdates(locationCallback);
                        }
                    }
                }
            }
        };


    }


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    AppConstants.LOCATION_REQUEST);

        } else {
            if (isContinue) {
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            } else {
                mFusedLocationClient.getLastLocation().addOnSuccessListener(MainActivity.this, location -> {
                    if (location != null ) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                        if (clickId == R.id.nearYou){
                            clickId = 0;
                            Intent i = new Intent(MainActivity.this, Ricerca1.class);
                            latitudine = wayLatitude.toString();
                            longitudine = wayLongitude.toString();
                            i.putExtra("latPar", longitudine);
                            i.putExtra("lonPar", latitudine);
                            i.putExtra("radiusPar", "0.5");
                            i.putExtra("categoryPar", "Museum;Botanical_and_zoological_gardens;Churches;Cultural_centre;Cultural_sites;Historical_buildings;Library;Monument_location;Photographic_activities;Squares;Theatre;");
                            startActivity(i);
                        } else {
                            Intent i = new Intent(MainActivity.this,IntSearch2.class);
                            latitudine = wayLatitude.toString();
                            longitudine = wayLongitude.toString();
                            i.putExtra("latitudine", latitudine);
                            i.putExtra("longitudine", longitudine);
                            startActivity(i);

                        }

                        //Toast.makeText(MainActivity.this,String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude), Toast.LENGTH_SHORT).show();

                    } else {
                        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    }
                });
            }
        }

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (isContinue) {
                        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    } else {
                        mFusedLocationClient.getLastLocation().addOnSuccessListener(MainActivity.this, location -> {
                            if (location != null) {
                                wayLatitude = location.getLatitude();
                                wayLongitude = location.getLongitude();
                               // Toast.makeText(MainActivity.this,String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude), Toast.LENGTH_SHORT).show();

                            } else {
                                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                            }
                        });
                    }
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.GPS_REQUEST) {
                isGPS = true; // flag maintain before get location
            }
        }
    }

    @Override
    public void onClick(View view) {
        Intent i;

        switch (view.getId()) {

            case R.id.intSearch:
                if (!isGPS) {
                    Toast.makeText(this, "Please turn on GPS", Toast.LENGTH_SHORT).show();
                    return;
                }
                isContinue = false;
                getLocation();
                break;


            case R.id.advSearch:
                i = new Intent(MainActivity.this, Ricerca2.class);

                startActivity(i);
                break;

            case R.id.nearYou:

                clickId = R.id.nearYou;

                if (!isGPS) {
                    Toast.makeText(this, "Please turn on GPS", Toast.LENGTH_SHORT).show();
                    return;
                }
                isContinue = false;
                getLocation();


                break;

            case R.id.km4city:
                i = new Intent(MainActivity.this, com.icloud.andreadimartino.bellini.km4city.class);
                startActivity(i);
                break;

            case R.id.aboutUs:
                i = new Intent(MainActivity.this, com.icloud.andreadimartino.bellini.aboutUs.class);
                startActivity(i);
                break;

            default:
                break;

        }

    }
}






