package com.example.alvihn.myapplicationcartotpisic;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class EditMarkerActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PICK_FROM_GALLERY = 2;
    private static final int ASK_FOR_PERMISSION_CAMERA = 3;
    private static final int ASK_FOR_PERMISSION_WRITE_EXTERNAL_STORAGE = 3;
    private ImageButton takePictureFromCameraButton;
    private ImageButton takePictureFromGalleryButton;
    private ImageView imageView = null;
    private EditText currentTitle;
    private LatLng currentPosition;
    private Bitmap currentBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
    private Bitmap bitmapSelectionne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editmarker);

        Intent intent = getIntent();
        currentPosition = (LatLng) intent.getParcelableExtra("position");

        takePictureFromCameraButton = findViewById(R.id.imageButtonFromCamera);
        takePictureFromGalleryButton = findViewById(R.id.imageButtonFromGallery);
        imageView = findViewById(R.id.imageView);
        currentTitle = findViewById(R.id.zoneTextTitre);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            takePictureFromCameraButton.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, ASK_FOR_PERMISSION_CAMERA);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            takePictureFromGalleryButton.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ASK_FOR_PERMISSION_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap imageBitmap = null;
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    Bundle extras = data.getExtras();
                    imageBitmap = (Bitmap) extras.get("data");
                    break;
                case PICK_FROM_GALLERY:
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    if (cursor == null || cursor.getCount() < 1) {
                        // no cursor or no record.
                        imageBitmap = null;
                        break;
                    }
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    if (columnIndex < 0) {
                        // no column index
                        imageBitmap = null;
                        break;
                    }
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    imageBitmap = BitmapFactory.decodeFile(picturePath.toString());
                    break;
                default:
                    break;
            }
        }
        if (imageBitmap != null && imageView != null) {
            updateBitmap(imageBitmap);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == ASK_FOR_PERMISSION_CAMERA) {
                takePictureFromCameraButton.setEnabled(true);
            }
            if (requestCode == ASK_FOR_PERMISSION_WRITE_EXTERNAL_STORAGE) {
                takePictureFromGalleryButton.setEnabled(true);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(bitmapSelectionne != null) {
            outState.putParcelable("BitmapImage", bitmapSelectionne);
        }
        if(currentPosition != null) {
            outState.putParcelable("currentPosition", currentPosition);
        }
        // Saving the bundle
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("BitmapImage")) {
                Bitmap imageBitmap = (Bitmap) savedInstanceState.getParcelable("BitmapImage");
                updateBitmap(imageBitmap);
            }
            if(savedInstanceState.containsKey("currentPosition")) {
                currentPosition = (LatLng) savedInstanceState.getParcelable("currentPosition");
            }
        }
    }

    public void updateBitmap(Bitmap imageBitmap) {
        imageView.setImageBitmap(imageBitmap);
        currentBitmap = Bitmap.createScaledBitmap(imageBitmap, 50, 50, false); // preview image
        bitmapSelectionne = imageBitmap;
        //Toast.makeText(getBaseContext(), "updateBitmap appele", Toast.LENGTH_SHORT).show();
    }

    public void launchCameraActivity(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void launchGalleryActivity(View view) {
        Intent takePictureFromGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (takePictureFromGallery.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureFromGallery, PICK_FROM_GALLERY);
        }
    }

    public void validateMarkerInfos(View v) {
        String title = currentTitle.getText().toString().trim();
        MarkerInfos markerInfos = new MarkerInfos(title, currentPosition, currentBitmap);
        Toast.makeText(getBaseContext(), markerInfos.toString(), Toast.LENGTH_SHORT).show();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("markerInfos", markerInfos);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
