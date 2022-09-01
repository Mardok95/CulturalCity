package com.icloud.andreadimartino.bellini;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class SchedaLuogoActivity extends AppCompatActivity {
    private static final String TAG = "SchedaLuogoActivity";
    private String questaUri = "";
    private String imgurl = "";
    private ImageView imgFromUrl;
    private Bitmap bitmap;
    private TextToSpeech t1;
    private TextView ed1;
    private ImageButton b1;
    private ImageButton b2;
    private String nomePerDesc;



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheda_luogo);
        ed1=(TextView)findViewById(R.id.description);
        b1=(ImageButton)findViewById(R.id.imageButtonAudio);
        b2=(ImageButton)findViewById(R.id.imageButtonMarker);
        Log.d(TAG, "onCreate: SchedaLuogoAtivity Started!");
        Intent intent = getIntent();
        final String nome = intent.getStringExtra("nomeLuogo");
        final String nomeLuogo = nome;
        nomePerDesc = nomeLuogo;
        final String lat = intent.getStringExtra("lat");
        final String lon = intent.getStringExtra("lon");
        String uri = intent.getStringExtra("uri");
        imgFromUrl = (ImageView)findViewById(R.id.locationImage);
        questaUri = uri;
        setTitle("Location Details");
        String multimedia = intent.getStringExtra("multimedia");
        //System.out.println("ECCO LA STRINGA" + multimedia);
        String tipo = intent.getStringExtra("tipo");
        try{
            new GetUriDatas().execute().get();
        } catch(InterruptedException e) {
            System.out.println("got interrupted!");
        }catch (ExecutionException e) {
            // TODO: return something else or throw a runtime exception
        }

        TextView txv1 = (TextView) findViewById(R.id.name);


        txv1.setText(nome);

        if ((multimedia.equalsIgnoreCase("")) && (tipo.equals("Museo"))){
            imgFromUrl.setImageResource(R.drawable.museumicon);
        }
            else if((multimedia.equalsIgnoreCase("")) && (tipo.equals("Giardini_botanicci_e_zoologici"))){
                imgFromUrl.setImageResource(R.drawable.gardenicon);
            }
            else if((multimedia.equalsIgnoreCase("")) && (tipo.equals("Chiese"))){
                imgFromUrl.setImageResource(R.drawable.churchicon);
            }
            else if((multimedia.equalsIgnoreCase("")) && (tipo.equals("Palazzi"))){
                imgFromUrl.setImageResource(R.drawable.historicalicon);
            }
            else if((multimedia.equalsIgnoreCase("")) && (tipo.equals("Biblioteca"))){
                imgFromUrl.setImageResource(R.drawable.library);
            }
            else if((multimedia.equalsIgnoreCase("")) && (tipo.equals("Luogo_monumento"))){
                imgFromUrl.setImageResource(R.drawable.monumenticon);
            }
            else if((multimedia.equalsIgnoreCase("")) && (tipo.equals("Fotografia_e_studi_fotografici"))){
                imgFromUrl.setImageResource(R.drawable.photo);
            }
            else if((multimedia.equalsIgnoreCase("")) && (tipo.equals("Piazze"))){
                imgFromUrl.setImageResource(R.drawable.squareicon);
            }
            else if((multimedia.equalsIgnoreCase("")) && (tipo.equals("Teatro"))){
                imgFromUrl.setImageResource(R.drawable.theatreicon);
            }
            else if((multimedia != "")){
            new GetImageFromURL(imgFromUrl).execute(multimedia);
            }
            else imgFromUrl.setImageResource(R.drawable.culturalcentreicon);


    t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int i) {
            if (i != TextToSpeech.ERROR){
                t1.setLanguage(Locale.ITALY);
            }
        }
    });

    b1.setOnClickListener(new View.OnClickListener(){
        public void onClick(View v){
            String toSpeak = ed1.getText().toString();
            Toast.makeText(getApplicationContext(), "Riproduco descrizione", Toast.LENGTH_SHORT).show();
            t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            
            }
        });

    b2.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent toMarker = new Intent(SchedaLuogoActivity.this,MarkerLuogo.class);
            toMarker.putExtra("latitudine", lat);
            toMarker.putExtra("longitudine",lon);
            toMarker.putExtra("nome",nomeLuogo);
            startActivity(toMarker);
            finish();
        }
    });



    }

    public void onPause(){
        if(t1 != null){
            t1.stop();
            t1.shutdown();
        }
        super.onPause();
    }


    public class GetImageFromURL extends AsyncTask<String,Void,Bitmap>{
        ImageView imgView;

        public GetImageFromURL(ImageView imgV){
            this.imgView = imgV;
        }

        @Override
        protected Bitmap doInBackground(String... url) {
            String urldisplay = url[0];
            imgFromUrl = null;
            try{
                InputStream srt = new java.net.URL(urldisplay).openStream();
                bitmap = BitmapFactory.decodeStream(srt);
            }catch (Exception e){
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap bitmap){
            super.onPostExecute(bitmap);
            imgView.setImageBitmap(bitmap);

        }
    }



    public class GetUriDatas extends AsyncTask<String, Void, String> {
        private int response_code;

        protected String doInBackground(String... params) {
            String jsonResponse = null;
            ClientResource cr;
            String URI ="http://servicemap.disit.org/WebAppGrafo/api/v1/?serviceUri=" + questaUri;
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
                    Intent intent = new Intent(SchedaLuogoActivity.this, MainActivity.class);
                    startActivity(intent);
                } else if (response_code == 1000) {
                    Toast.makeText(getApplicationContext(), "Errore di rete", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(SchedaLuogoActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    try {

                        JSONObject joo = new JSONObject(res);
                        JSONObject jService = joo.getJSONObject("Service");
                        JSONArray jFeature = jService.getJSONArray("features");
                        for (int i = 0; i < jFeature.length(); i++) {
                            JSONObject object = jFeature.getJSONObject(i);
                            JSONObject prop = object.getJSONObject("properties");
                            String label = prop.getString("typeLabel");
                            String cell = prop.getString("phone");
                            String provincia = prop.getString("province");
                            String città = prop.getString("city");
                            String immagine = prop.getString("multimedia");
                            imgurl = immagine;
                            String cap = prop.getString("cap");
                            String via = prop.getString("address");
                            String civico = prop.getString("civic");
                            String sito = prop.getString("website");
                            String score = prop.getString("avgStars");
                            String description1 = prop.getString("description");
                            String description2 = prop.getString("description2");


                            TextView txv12 = (TextView) findViewById(R.id.description);

                            if (description1.equalsIgnoreCase("") && description2.equalsIgnoreCase("")){
                                String noText = ("Non esiste una descrizione valida per " + nomePerDesc);
                                txv12.setText(noText);
                            }
                            else if ((description1 != "") && description2.equalsIgnoreCase("")){
                                txv12.setText(description1);
                                txv12.setMovementMethod(new ScrollingMovementMethod());


                            }
                            else {
                                String noText = ("Non esiste una descrizione valida per " + nomePerDesc);
                                txv12.setText(noText);

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
