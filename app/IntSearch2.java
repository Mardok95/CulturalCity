package com.icloud.andreadimartino.bellini;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
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
import java.util.ArrayList;
import java.util.Locale;

public class IntSearch2 extends AppCompatActivity implements OnMapReadyCallback {
    private FusedLocationProviderClient mFusedLocationClient;
    ArrayList<Luogo> luoghi = new ArrayList<Luogo>();
    private Double wayLatitude = 0.0, wayLongitude = 0.0;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private StringBuilder stringBuilder;
    private boolean isContinue = false;
    private ArrayList<Marker> marks = new ArrayList<Marker>();
    private boolean firstCall = true;
    private boolean isGPS = false;
    private GoogleMap mMap;
    private Double posLat;
    private Double posLng;
    private Marker myPos;
    private String tmpUri;
    private boolean done = false;
    private String nomePerDesc;
    private String nomeParlato = "luogoTest";
    private TextToSpeech t1;
    private String description1, description2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_int_search2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent intet = getIntent();
        posLng = Double.parseDouble(intet.getStringExtra("latitudine"));
        posLat = Double.parseDouble(intet.getStringExtra("longitudine"));

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        String lo = posLat.toString();
        String la = posLng.toString();



        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000); // 10 seconds
        locationRequest.setFastestInterval(5 * 1000); // 5 seconds

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR){
                    t1.setLanguage(Locale.ITALY);
                }
            }
        });

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
                            Toast.makeText(IntSearch2.this,String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude), Toast.LENGTH_SHORT).show();

                        } else {
                            stringBuilder.append(wayLatitude);
                            stringBuilder.append("-");
                            stringBuilder.append(wayLongitude);
                            stringBuilder.append("\n\n");
                            //Toast.makeText(IntSearch2.this,stringBuilder.toString(), Toast.LENGTH_SHORT).show();
                            LatLng mPosition = new LatLng(wayLatitude, wayLongitude);
                            myPos.setPosition(mPosition);
                            new listaLuoghi().execute(wayLatitude.toString(),wayLongitude.toString());
                            new GetUriDatas().execute(tmpUri,nomeParlato);


                        }
                        if (!isContinue && mFusedLocationClient != null) {
                            mFusedLocationClient.removeLocationUpdates(locationCallback);
                        }
                    }
                }
            }
        };
    }

    public void funziona(){

        while (true){

        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Inizializzo mappa ed evidenzio il marker della mia posizione
        mMap = googleMap;
        LatLng mPosition = new LatLng(posLng, posLat);
        myPos = mMap.addMarker(new MarkerOptions().position(mPosition).title("Your Position").icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mPosition,18f));




        //In base alla mia posizione invia una richiesta URI all'API km4city
        //new listaLuoghi().execute(posLat.toString(),posLng.toString());


                //Qui potrebbe servire una pausa, progress bar
       /* for (int i = 0; i < luoghi.size() ; i++) {
            createMarker(Double.parseDouble(luoghi.get(i).getLat()), Double.parseDouble(luoghi.get(i).getLon()),luoghi.get(i).getNomeLuogo());
        }
*/


        if (!isGPS) {

            Toast.makeText(this, "Please turn on GPS", Toast.LENGTH_SHORT).show();
            return;
        }
        isContinue = true;
        stringBuilder = new StringBuilder();
        getLocation();

    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(IntSearch2.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(IntSearch2.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(IntSearch2.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    AppConstants.LOCATION_REQUEST);

        } else {
            if (isContinue) {
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            } else {
                mFusedLocationClient.getLastLocation().addOnSuccessListener(IntSearch2.this, location -> {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                        Toast.makeText(IntSearch2.this,String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude), Toast.LENGTH_SHORT).show();

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
                        mFusedLocationClient.getLastLocation().addOnSuccessListener(IntSearch2.this, location -> {
                            if (location != null) {
                                wayLatitude = location.getLatitude();
                                wayLongitude = location.getLongitude();
                                Toast.makeText(IntSearch2.this,String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude), Toast.LENGTH_SHORT).show();

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

    public class listaLuoghi extends AsyncTask<String, Void, String> {

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
                    Intent intent = new Intent(IntSearch2.this, MainActivity.class);
                    startActivity(intent);
                } else if (response_code == 1000) {
                    Toast.makeText(getApplicationContext(), "Errore di rete", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(IntSearch2.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    try {

                        JSONObject joo = new JSONObject(res);
                        JSONObject jService = joo.getJSONObject("Services");
                        JSONArray jFeature = jService.getJSONArray("features");

                        luoghi.clear();
                        for (int i = 0; i < marks.size() ; i++) {
                            marks.get(i).remove();
                        }

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
                            if(i==0){
                                tmpUri = uriLuogo;
                                nomePerDesc = nomeLuogo;
                            }

                            luoghi.add(luog);

                        }


                        for (int i = 0; i < luoghi.size(); i++) {
                            LatLng mPos = new LatLng(Double.parseDouble(luoghi.get(i).getLon()),Double.parseDouble(luoghi.get(i).getLat()));
                            marks.add(mMap.addMarker(new MarkerOptions().position(mPos).title(luoghi.get(i).getNomeLuogo())));

                        }










                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        }

    }



    public class GetUriDatas extends AsyncTask<String, Void, String> {
        private int response_code;

        protected String doInBackground(String... params) {
            nomePerDesc = params[1];
            String jsonResponse = null;
            ClientResource cr;
            String URI ="http://servicemap.disit.org/WebAppGrafo/api/v1/?serviceUri=" + params[0];
            cr = new ClientResource(URI);
            try {
                jsonResponse = cr.get().getText();
                response_code = cr.getStatus().getCode();
            } catch (ResourceException | IOException e) {
                jsonResponse = " Error caused by resourceException: " + cr.getStatus().getCode() + " - " + cr.getStatus().getDescription() + " - " ;
                response_code = cr.getStatus().getCode();
            }
            return jsonResponse;
        }

        private float[] fillData(JSONArray jsonArray){

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
                    Intent intent = new Intent(IntSearch2.this, MainActivity.class);
                    startActivity(intent);
                } else if (response_code == 1000) {
                    Toast.makeText(getApplicationContext(), "Errore di rete", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(IntSearch2.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    try {

                        JSONObject joo = new JSONObject(res);
                        JSONObject jService = joo.getJSONObject("Service");
                        JSONArray jFeature = jService.getJSONArray("features");
                        for (int i = 0; i < jFeature.length(); i++) {
                            JSONObject object = jFeature.getJSONObject(i);
                            JSONObject prop = object.getJSONObject("properties");
                            description1 = prop.getString("description");
                            description2 = prop.getString("description2");


                        }


                        if (nomeParlato.equalsIgnoreCase(nomePerDesc)){

                        }

                        else{

                            if (description1.equalsIgnoreCase("") && description2.equalsIgnoreCase("")) {
                                String noText = ("Non esiste una descrizione valida per " + nomePerDesc);
                                String toSpeak = noText;
                                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                                nomeParlato = nomePerDesc;


                            } else if ( (description1 != "") && description2.equalsIgnoreCase("")){
                                t1.speak(description1, TextToSpeech.QUEUE_FLUSH, null);
                                nomeParlato = nomePerDesc;


                            } else  {
                                String noText = ("Non esiste una descrizione valida per " + nomePerDesc);
                                String toSpeak = noText;
                                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                                nomeParlato = nomePerDesc;


                            }


                        }




                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }





}
