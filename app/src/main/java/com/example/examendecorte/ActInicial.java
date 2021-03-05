package com.example.examendecorte;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
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
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.LocationInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ActInicial extends AppCompatActivity {
    private static final int REQUEST_GALLERY = 1;
    Uri imageUri;
    private Bitmap imgBitmap;
    private ImageView imgv,logouteq;
    private TextView txtresult;
    // Country Data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_inicial);
        imgv = findViewById(R.id.imgFotoPais);
        txtresult = findViewById(R.id.txtdisplay);
        logouteq.findViewById(R.id.logouteq);
        imgBitmap = null;
        Glide.with( this.getApplicationContext()).load("https://www.uteq.edu.ec/images/page/4/logoUTEQdegradado.png").into(logouteq);
    }

    public void doProcessGallery(View view){
        dispatchTakePictureIntent("gallery");
    }
    private void dispatchTakePictureIntent(String mode) {
        if (mode.equals("gallery")) {
            Intent intent = new Intent(Intent.ACTION_PICK).setType("image/*");
            startActivityForResult(intent, REQUEST_GALLERY);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
            imageUri = data.getData();
            startActivity(new Intent(this, RecognizeGallery.class).putExtra("imageUri", imageUri.toString()));
        }
    }

}