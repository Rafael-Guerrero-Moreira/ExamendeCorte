package com.example.examendecorte;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.examendecorte.WebServices.Asynchtask;
import com.example.examendecorte.WebServices.WebService;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.common.collect.Maps;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private TextView txtpais, txtinfo;
    private ImageView imgpais;
    private RequestQueue request;
    private StringRequest stringRequest;
    private JSONObject countryObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        txtpais = findViewById(R.id.txtNombrePais);
        txtinfo = findViewById(R.id.txtInfoPais);
        imgpais = findViewById(R.id.imgFotoPais);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle b = this.getIntent().getExtras();
        String countryCode = b.getString("countryCode");
        if (countryCode != "") {
            getCountryData(countryCode);
            Glide.with( this.getApplicationContext()).load("http://www.geognos.com/api/en/countries/flag/"+countryCode+".png").into(imgpais);
        } else {
            Toast.makeText(this, "No se ha encontrado la información del país", Toast.LENGTH_LONG).show();
        }
    }

    private void getCountryData(String countryCode) {
        request = Volley.newRequestQueue(MapsActivity.this);
        String URL = "http://www.geognos.com/api/en/countries/info/"+countryCode+".json";
        stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                countryObject = null;
                try {
                    countryObject = new JSONObject(response);
                    if (countryObject.getJSONObject("Results") == null) {
                        Toast.makeText(MapsActivity.this, "No hay información", Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        countryObject = countryObject.getJSONObject("Results");
                        try {
                            txtpais.setText(countryObject.getString("Name"));
                            txtinfo.setText("Capital: "+countryObject.getJSONObject("Capital").getString("Name")+"\n"+
                                    "Code ISO 2: "+countryObject.getJSONObject("CountryCodes").getString("iso2")+"\n"+
                                    "Tel Prefix: "+countryObject.getString("TelPref"));
                            mMap.getUiSettings().setZoomControlsEnabled(false);
                            mMap.getUiSettings().setAllGesturesEnabled(true);
                            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            Double centerLat = countryObject.getJSONArray("GeoPt").getDouble(0);
                            Double centerLng = countryObject.getJSONArray("GeoPt").getDouble(1);
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(centerLat, centerLng), 4);
                            mMap.moveCamera(cameraUpdate);
                            Double pointNorth = countryObject.getJSONObject("GeoRectangle").getDouble("North");
                            Double pointSouth = countryObject.getJSONObject("GeoRectangle").getDouble("South");
                            Double pointEast = countryObject.getJSONObject("GeoRectangle").getDouble("East");
                            Double pointWest = countryObject.getJSONObject("GeoRectangle").getDouble("West");
                            PolylineOptions lines = new PolylineOptions()
                                    .add(new LatLng(pointNorth, pointWest))
                                    .add(new LatLng(pointNorth, pointEast))
                                    .add(new LatLng(pointSouth, pointEast))
                                    .add(new LatLng(pointSouth, pointWest))
                                    .add(new LatLng(pointNorth, pointWest));
                            lines.width(8);
                            lines.color(Color.BLUE);
                            mMap.addPolyline(lines);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MapsActivity.this, "Sucedió un error en la consulta de la información del país", Toast.LENGTH_LONG).show();
            }
        });
        request.add(stringRequest);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

}