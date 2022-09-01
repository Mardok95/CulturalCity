package com.icloud.andreadimartino.bellini;

import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import java.io.IOException;
import java.util.ArrayList;

public class Ricerca1 extends AppCompatActivity {

    ArrayList<Luogo> luoghi = new ArrayList<Luogo>();
    //private ListView listView;
    //private TextView nome_luoghi;
    //private ListAdapter adapter;
    private Double latitudine, longitudine;
    private String lat;
    private String lon;
    private String raggio;
    private String categoria;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ricerca1);
        Intent intent = getIntent();
        lat = intent.getStringExtra("latPar");
        lon = intent.getStringExtra("lonPar");
        //Toast.makeText(Ricerca1.this, "Lat: " + lat + " Lon: " + lon, Toast.LENGTH_SHORT).show();

        raggio = intent.getStringExtra("radiusPar");
        categoria = intent.getStringExtra("categoryPar");
        setTitle("Near you");


        new InsList().execute(lon, lat, raggio, categoria);


    }

    public class InsList extends AsyncTask<String, Void, String> {
        private int response_code;

        protected String doInBackground(String... params) {
            String jsonResponse = null;
            ClientResource cr;
            String URI = //"http://servicemap.disit.org/WebAppGrafo/api/v1/?selection=43.7756;11.2490&categories=Museum;Botanical_and_zoological_gardens;Churches;Cultural_centre;Cultural_sites;Historical_buildings;Library;Monument_location;Photographic_activities;Squares;Theatre;&maxResults=0&maxDists=2&lang=it&format=json";
                    "http://servicemap.disit.org/WebAppGrafo/api/v1/?selection=" + params[0] + ";" + params[1] +
                    "&categories=" + params[3] +
                   "&maxResults=0&maxDists=" + params[2] + "&lang=it&format=json";
            //Toast.makeText(Ricerca1.this, URI, Toast.LENGTH_SHORT).show();
            cr = new ClientResource(URI);
            try {
                jsonResponse = cr.get().getText();
                response_code = cr.getStatus().getCode();
            } catch (ResourceException | IOException e) {
                jsonResponse = " Error caused by resourceException: " + cr.getStatus().getCode() + " - " + cr.getStatus().getDescription() + " - " ;
                response_code = cr.getStatus().getCode();
            }
            //System.out.println(jsonResponse);
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
                    Intent intent = new Intent(Ricerca1.this, MainActivity.class);
                    startActivity(intent);
                } else if (response_code == 1000) {
                    Toast.makeText(getApplicationContext(), "Errore di rete", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Ricerca1.this, MainActivity.class);
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
                            Object latitude= coo.get(0);
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

            //adapter = new ListAdapter(Ricerca1.this, luoghi);
            //listView.setAdapter(adapter);
            //listView.setVisibility(View.VISIBLE);

            ArrayList<Luogo> locations = new ArrayList<Luogo>(luoghi.size());
            for (int i = 0; i<luoghi.size(); i++){
               locations.add(i,luoghi.get(i));
            }
            LuogoListAdapter adapter = new LuogoListAdapter(Ricerca1.this, R.layout.adapter_view_layout, locations);

            final ListView mylist = (ListView) findViewById(R.id.listView1);
            //final ArrayAdapter<String> adp = new  ArrayAdapter(Ricerca1.this, R.layout.adapter_view_layout,nomi);
            mylist.setAdapter(adapter);



            mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(Ricerca1.this, SchedaLuogoActivity.class);
                    String selected = ((TextView) view.findViewById(R.id.textView2)).getText().toString();
                    for (int i = 0; i<luoghi.size(); i++){
                        if(luoghi.get(i).getNomeLuogo()==selected){
                            intent.putExtra("nomeLuogo", selected);
                            intent.putExtra("lat", luoghi.get(i).getLat());
                            intent.putExtra("lon", luoghi.get(i).getLon());
                            intent.putExtra("uri", luoghi.get(i).getUriLuogo());
                            intent.putExtra("multimedia", luoghi.get(i).getMultimediaLuogo());
                            intent.putExtra("tipo", luoghi.get(i).getTipoLuogo());
                        }
                    }
                    Toast.makeText(Ricerca1.this,"item clicked", Toast.LENGTH_SHORT).show();


                    Ricerca1.this.startActivity(intent);
                }
            });
        }
    }
}


