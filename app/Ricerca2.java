package com.icloud.andreadimartino.bellini;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import java.io.IOException;

public class Ricerca2 extends AppCompatActivity {
    private static final String TAG = "Ricerca2";
    private TextView mDisplayRadius;
    private Spinner spinner;
    private ArrayAdapter<CharSequence> adapter;
    private ArrayAdapter<CharSequence> adapter2;
    private EditText searchAdderss;
    private Button button1;
    private Button bottone2;
    private String indirizzo;

    private String latSearch;
    private String lonSearch;
    private String radiusPar;
    private String categoryPar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adv_search);
        Log.d(TAG, "onCreate: Started");

        searchAdderss = findViewById(R.id.ins_indirizzo);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);



        mDisplayRadius = findViewById(R.id.seleziona_raggio);
        setTitle("Advanced Search");

        bottone2 = findViewById(R.id.conferma_indirizzo);
        bottone2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmInput(v);

            }
        });


        Spinner spinner = (Spinner) findViewById(R.id.spinner_raggio);
        adapter = ArrayAdapter.createFromResource(this, R.array.radius_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(Ricerca2.this, parent.getItemAtPosition(position)+ "is selected", Toast.LENGTH_SHORT).show();
                radiusPar = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner spinner2 = (Spinner) findViewById(R.id.spinner_categoria);
        adapter2 = ArrayAdapter.createFromResource(this, R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                    categoryPar = parent.getItemAtPosition(position).toString();


                //Toast.makeText(Ricerca2.this, categoryPar + "is selected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        button1 = findViewById(R.id.conferma_ricerca);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openRicerca = new Intent(Ricerca2.this, Ricerca1.class);
                openRicerca.putExtra("latPar", latSearch);
                openRicerca.putExtra("lonPar",lonSearch);
                openRicerca.putExtra("radiusPar", radiusPar);
                openRicerca.putExtra("categoryPar", categoryPar);
                startActivity(openRicerca);
            }
        });

    }


    private boolean validateIndirizzoRicerca() {
        String indirizzoRiceca =  searchAdderss.getEditableText().toString();
        //searchAdderss.getEditText().getText().toString();
        if(indirizzoRiceca.isEmpty()){
            searchAdderss.setError("indirizzoRierca can't b empty");
            return false;
        }else {
            searchAdderss.setError(null);


            return true;
        }
    }

    public void confirmInput(View v){
        if (!validateIndirizzoRicerca()){
            return;
        }
        String input = searchAdderss.getEditableText().toString();
        Toast.makeText(this, input, Toast.LENGTH_SHORT).show();
        new GetUriDatas().execute();
        indirizzo = input;
    }

    public class GetUriDatas extends AsyncTask<String, Void, String> {
        private int response_code;

        protected String doInBackground(String... params) {
            String jsonResponse = null;
            ClientResource cr;
            String URI = "https://servicemap.disit.org/WebAppGrafo/api/v1/location/?search=" + indirizzo + "&maxResults=1";
            cr = new ClientResource(URI);
            try {
                jsonResponse = cr.get().getText();
                response_code = cr.getStatus().getCode();
            } catch (ResourceException | IOException e) {
                jsonResponse = " Error caused by resourceException: " + cr.getStatus().getCode() + " - " + cr.getStatus().getDescription() + " - ";
                response_code = cr.getStatus().getCode();
            }
            //System.out.println(jsonResponse);
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
                } else if (response_code == 1000) {
                    Toast.makeText(getApplicationContext(), "Errore di rete", Toast.LENGTH_LONG).show();
                } else {
                    try {

                        JSONObject joo = new JSONObject(res);
                        JSONArray jFeature = joo.getJSONArray("features");
                        for (int i = 0; i < jFeature.length(); i++) {
                            JSONObject object = jFeature.getJSONObject(i);
                            JSONObject geo = object.getJSONObject("geometry");
                            JSONArray coo = geo.getJSONArray("coordinates");
                            Object latitude= coo.get(0);
                            Object longitude = coo.get(1);
                            Double lat = (Double) latitude;
                            Double lon = (Double) longitude;
                            latSearch = lat.toString();
                            lonSearch = lon.toString();
                            Toast.makeText(Ricerca2.this, "latitudine e longitudine trovate: " + latSearch + lonSearch, Toast.LENGTH_SHORT).show();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

}
