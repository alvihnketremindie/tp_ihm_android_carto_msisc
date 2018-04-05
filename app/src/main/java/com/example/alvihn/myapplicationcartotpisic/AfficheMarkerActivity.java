package com.example.alvihn.myapplicationcartotpisic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Activite d'affichage des informations présent sur un marqueur
 */
public class AfficheMarkerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affichemarker);

        TextView zoneTextViewTitre = findViewById(R.id.zoneTextViewTitre);
        TextView zoneTextViewLatitude = findViewById(R.id.zoneTextViewLatitude);
        TextView zoneTextViewLongitude = findViewById(R.id.zoneTextViewLongitude);

        Intent intent = getIntent();
        String title = "Titre : "+intent.getStringExtra("title");
        String latitude = "Latitude : "+intent.getStringExtra("latitude");
        String longitude = "Longitude : "+intent.getStringExtra("longitude");

        zoneTextViewTitre.setText(title);
        zoneTextViewLatitude.setText(latitude);
        zoneTextViewLongitude.setText(longitude);
    }

    public void finAffichage(View v) {
        finish();
    }
}
