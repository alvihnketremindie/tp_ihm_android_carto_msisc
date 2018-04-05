package com.example.alvihn.myapplicationcartotpisic;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<MarkerInfos> pointList = new ArrayList<MarkerInfos>();
    private boolean marqueurAreplacer = false;
    private int vueDeLaCarte = GoogleMap.MAP_TYPE_NORMAL;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_ADD_MARKER = 2;
    MarkerInfos markerInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_maps, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Sauvegarde de l'arraylist pointList dans le Bundle
        outState.putParcelableArrayList("points", pointList);
        outState.putInt("typeCarte", vueDeLaCarte);
        // Saving the bundle
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("typeCarte")) {
                vueDeLaCarte = savedInstanceState.getInt("typeCarte");
            }
            if (savedInstanceState.containsKey("points")) {
                //Il faut replacer les marqueurs sur la carte
                marqueurAreplacer = true;
                pointList = savedInstanceState.getParcelableArrayList("points");
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        /*
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        */
        // Add a marker in IMT Atlantique and move the camera
        String titleIMTAtlantique = "Marker in IMT Atlantique";
        LatLng markerIMTAtlantique = new LatLng(48.359285, -4.569933);
        markerInfos = new MarkerInfos(titleIMTAtlantique, markerIMTAtlantique);

        mMap.addMarker(new MarkerOptions().position(markerIMTAtlantique).title(titleIMTAtlantique));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(markerIMTAtlantique));

        //Autoriser la visualisation de la position actuelle
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        /*
        ********************
        Ajout des listeners
        *******************
        */
        MyMapListener myMapListener = new MyMapListener();
        //Ajout du Listener sur un Click
        mMap.setOnMapClickListener(myMapListener);
        mMap.setOnMapLongClickListener(myMapListener);
        mMap.setOnMarkerClickListener(myMapListener);

        if (marqueurAreplacer) {
            restaurationMarqueur();
            setVueDeLaCarte(vueDeLaCarte);
        }
    }

    /**
     * Change la vue de la carte
     * Trois mode disponible (Normal, Hibryde et Satellite)
     *
     * @param i
     */
    public void setVueDeLaCarte(int i) {
        if (mMap != null) {
            mMap.setMapType(i);
            vueDeLaCarte = i;
        }
    }

    public void setMapTypeNormal(MenuItem item) {
        setVueDeLaCarte(GoogleMap.MAP_TYPE_NORMAL);
    }

    public void setMapTypeHybrid(MenuItem item) {
        setVueDeLaCarte(GoogleMap.MAP_TYPE_HYBRID);
    }

    public void setMapTypeSatellite(MenuItem item) {
        setVueDeLaCarte(GoogleMap.MAP_TYPE_SATELLITE);
    }

    /**
     * Class d'implementation des listeners sur la MAP
     */
    public class MyMapListener implements
            GoogleMap.OnMapClickListener,
            GoogleMap.OnMapLongClickListener,
            GoogleMap.OnMarkerClickListener {

        @Override
        public void onMapClick(LatLng latLng) {
            String textToShow = "Latitude : " + latLng.latitude + "\r\n" + "Longitude : " + latLng.longitude;
            Toast.makeText(getApplicationContext(), textToShow, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onMapLongClick(LatLng latLng) {
            Intent intent = new Intent(getApplicationContext(), EditMarkerActivity.class);
            intent.putExtra("position", latLng);
            startActivityForResult(intent, REQUEST_ADD_MARKER);
        }

        @Override
        public boolean onMarkerClick(Marker marker) {
            LatLng latLng = marker.getPosition();
            String title = marker.getTitle();
            Intent intent = new Intent(getApplicationContext(), AfficheMarkerActivity.class);
            intent.putExtra("title", title);
            intent.putExtra("latitude", latLng.latitude+"");
            intent.putExtra("longitude", latLng.longitude+"");
            startActivity(intent);
            return false;
        }
    }

    /**
     * Ajoute un marqueur à l'emplacement indiqué
     *
     * @param markerInfos
     */
    private void ajoutMarqueur(MarkerInfos markerInfos) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(markerInfos.getPosition());
        markerOptions.title(markerInfos.getTitle());
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(markerInfos.getImage()));
        if(mMap != null) {
            mMap.addMarker(markerOptions);
        }
    }

    /**
     * Restaure les marqueurs lors d'un changement d'état de l'activité
     */
    public void restaurationMarqueur() {
        if (mMap != null && pointList != null) {
            // Restauration des marqueurs au changement de configuration
            for (MarkerInfos point : pointList) {
                ajoutMarqueur(point);
            }
        }
        marqueurAreplacer = false;
    }

    private void displayMarkerInfos() {
        Intent intent = new Intent(getApplicationContext(), AfficheMarkerActivity.class);
        //intent.putExtra("position", latLng);
        startActivity(intent);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            switch (requestCode) {
                case REQUEST_ADD_MARKER:
                    MarkerInfos markerInfos = (MarkerInfos) data.getParcelableExtra("markerInfos");
                    if(markerInfos != null) {
                        ajoutMarqueur(markerInfos);
                        Toast.makeText(getBaseContext(), R.string.marker_added, Toast.LENGTH_SHORT).show();
                        pointList.add(markerInfos);
                    }
                    break;
            }
        }
    }
}
