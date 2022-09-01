package com.icloud.andreadimartino.bellini;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import java.io.IOException;
import java.security.Provider;
import java.util.ArrayList;
import java.util.List;

public class IntellySearch extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<Luogo> luoghi = new ArrayList<Luogo>();
    ArrayList<Marker> mArray = new ArrayList<Marker>();
    private List<LatLng> fountain = null;
    private LocationManager locationManager;
    private Double posLat;
    private Double posLng;
    private LatLng position;
    private Marker mPosition;
    private String provider;
    private Marker nMarker;
    private int numero = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intelly_search);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
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
        Intent intet = getIntent();
        posLat = Double.parseDouble(intet.getStringExtra("latitudine"));
        posLng = Double.parseDouble(intet.getStringExtra("longitudine"));


        startGPS();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        // Add a marker in Sydney and move the camera
        LatLng duomo = new LatLng(43.773251,11.255474);

        //mMap.moveCamera(CameraUpdateFactory.newLatLng(duomo));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(duomo,18f));
    }




    public void startGPS() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 5);
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 5, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                posLat = location.getLatitude();
                posLng = location.getLongitude();


                position = new LatLng(posLat, posLng);

                if (mPosition != null) {
                    mPosition.remove();
                }

                mPosition = mMap.addMarker(new MarkerOptions().position(position).title("Your position"));

                new listaLuogi().execute(posLat.toString(),posLng.toString());
                for (int i = 0; i < luoghi.size() ; i++) {
                    createMarker(Double.parseDouble(luoghi.get(i).getLat()), Double.parseDouble(luoghi.get(i).getLon()),luoghi.get(i).getNomeLuogo());

                }
                if(numero<2){
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position,18));
                    numero = 30;
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLng(position));

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
        });

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 5: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    getDialog2("Keine Erlaubnis für GPS").show();
                }
            }
        }
    }

    public class listaLuogi extends AsyncTask<String, Void, String> {

        private int response_code;

        @Override
        protected String doInBackground(String... params) {
            String jsonResponse = null;
            ClientResource cr;
            // Params 0 e params 1 sono latitudine e longitudine
            String URI = "http://servicemap.disit.org/WebAppGrafo/api/v1/?selection=" + params[0] + ";" + params[1] +
                    "&categories=Museum;Botanical_and_zoological_gardens;Churches;Cultural_centre;Cultural_sites;Historical_buildings;Library;Monument_location;Photographic_activities;Squares;Theatre;&maxResults=0&maxDists=0.6&lang=it&format=json";
            cr = new ClientResource(URI);
            try {
                jsonResponse = cr.get().getText();
                response_code = cr.getStatus().getCode();
            } catch (ResourceException | IOException e) {
                jsonResponse = " Error caused by resourceException: " + cr.getStatus().getCode() + " - " + cr.getStatus().getDescription() + " - ";
                response_code = cr.getStatus().getCode();
            }
            return jsonResponse;
        }


        private float[] fillData(JSONArray jsonArray) {

            float[] fData = new float[jsonArray.length()];

            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    fData[i] = Float.parseFloat(jsonArray.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return fData;
        }

        protected void onPostExecute(String res) {
            if (res != null) {
                if (response_code == 404) {
                    Toast.makeText(getApplicationContext(), "Nessun risultato", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(IntellySearch.this, MainActivity.class);
                    startActivity(intent);
                } else if (response_code == 1000) {
                    Toast.makeText(getApplicationContext(), "Errore di rete", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(IntellySearch.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    try {

                        JSONObject joo = new JSONObject(res);
                        JSONObject jService = joo.getJSONObject("Services");
                        JSONArray jFeature = jService.getJSONArray("features");
                        for (int i = 0; i < jFeature.length(); i++) {
                            JSONObject object = jFeature.getJSONObject(i);

                            JSONObject geo = object.getJSONObject("geometry");
                            JSONArray coo = geo.getJSONArray("coordinates");
                            Object latitude = coo.get(0);
                            Object longitude = coo.get(1);
                            Double lat = (Double) latitude;
                            Double lon = (Double) longitude;

                            JSONObject prop = object.getJSONObject("properties");
                            String nomeLuogo = prop.getString("name");
                            String tipoLuogo = prop.getString("tipo");
                            String distanzaLuogo = prop.getString("distance");
                            String uriLuogo = prop.getString("serviceUri");
                            String multimediaLuogo = prop.getString("multimedia");


                            Luogo luog = new Luogo(nomeLuogo, lat, lon, tipoLuogo, distanzaLuogo, uriLuogo, multimediaLuogo);
                            luoghi.add(luog);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        }

    }






    public Dialog getDialog2(String string) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(string);
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override

                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        return builder.create();
    }


    public Dialog getDialog(String string) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(string);
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }

    protected Marker createMarker(double latitudine, double longitudine, String nome){
        return mMap.addMarker(new MarkerOptions()
        .position(new LatLng(latitudine,longitudine))
        .title(nome));
    }

}