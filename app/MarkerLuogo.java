package com.icloud.andreadimartino.bellini;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MarkerLuogo extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_luogo);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        String latitudine = getIntent().getExtras().getString("latitudine");
        String longitudine = getIntent().getExtras().getString("longitudine");
        String nomeLuogo = getIntent().getExtras().getString("nome");
        double lat = Double.parseDouble(latitudine);
        double lon = Double.parseDouble(longitudine);
        LatLng luog = new LatLng(lon,lat);
        mMap.addMarker(new MarkerOptions().position(luog).title(nomeLuogo));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(luog, 17.0f));



    }

}
