package com.icloud.andreadimartino.bellini;

import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import java.io.IOException;

public class Luogo {
    private String nomeLuogo;
    private Double lat;
    private Double lon;
    private String tipoLuogo;
    private String distanzaLuogo;
    private String uriLuogo;
    private String multimediaLuogo;

    public Luogo(String nomeLuogo, Double lat, Double lon, String tipoLuogo, String distanzaLuogo, String uriLuogo, String multimediaLuogo){
        this.nomeLuogo = nomeLuogo;
        this.lat = lat;
        this.lon = lon;
        this.tipoLuogo = tipoLuogo;
        this.distanzaLuogo = distanzaLuogo;
        this.uriLuogo = uriLuogo;
        this.multimediaLuogo = multimediaLuogo;

    }

    public String getLat() {
        return lat.toString();
    }

    public String getLon() {
        return lon.toString();
    }

    public String getNomeLuogo() {
        return this.nomeLuogo;
    }

    public String getTipoLuogo() {
        return tipoLuogo;
    }

    public String getDistanzaLuogo() {
        return distanzaLuogo;
    }

    public String getUriLuogo() {
        return uriLuogo;
    }

    public String getMultimediaLuogo() {
        return multimediaLuogo;
    }

    public void setDistanzaLuogo(String distanzaLuogo) {
        this.distanzaLuogo = distanzaLuogo;
    }





}
