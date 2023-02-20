package com.here.example.geojson_demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;

/*HERE SDK IMPORTS*/
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.mapview.MapScheme;
import com.here.sdk.mapview.MapView;
/*HERE SDK IMPORTS*/

public class MainActivity extends AppCompatActivity {
    private MapView mapView;
    private RadioGroup optionsGroup;
    private RadioButton radioItemButton;
    private Context context;
    private Button getGeoJsonButton;
    private MapObjects mapObjects;
    AlertDialog.Builder builder;

    private static final int STORAGE_PERMISSION_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get a MapView instance from the layout.
        mapView = findViewById(R.id.map_view);
        optionsGroup = findViewById(R.id.optionsGroup);
        getGeoJsonButton = findViewById(R.id.getGeoJson);

        mapView.onCreate(savedInstanceState);
        context = this;

        mapView.setOnReadyListener(() -> {
            // This will be called each time after this activity is resumed.
            // It will not be called before the first map scene was loaded.
            // Any code that requires map data may not work as expected beforehand.
            Log.d("Info", "HERE Rendering Engine attached.");
        });
        loadMapScene();
        showMessage();
    }

    private void loadMapScene() {
        // Load a scene from the HERE SDK to render the map with a map scheme.
        mapView.getMapScene().loadScene(MapScheme.NORMAL_DAY, mapError -> {
            if (mapError == null) {
                mapObjects = new MapObjects(mapView);

                double distanceInMeters = 40000;
                mapView.getCamera().lookAt(
                        new GeoCoordinates( 50.16352417, 8.53379056), distanceInMeters);

                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
            } else {
                Log.d("Info", "Loading map failed: mapError: " + mapError.name());
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    // Function to check and request permission.
    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { permission }, requestCode);
        }
        else {
            Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
            addButtonListener();
        }
    }

    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
                addButtonListener();
            } else {
                Toast.makeText(MainActivity.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void addButtonListener() {
        getGeoJsonButton.setOnClickListener(v -> {
            int selectedId= optionsGroup.getCheckedRadioButtonId();
            radioItemButton = findViewById(selectedId);

            GeoJson readGeoJson = new GeoJson();
            ArrayList <GeoCoordinates> points = readGeoJson.read_file(context,"GeoJson.json"); //Replace the name of this file with the name of your json file

            if(points != null) {
                if (radioItemButton.getText().equals("Polyline")) {
                    mapObjects.createPolyline(points);
                    Toast.makeText(MainActivity.this, "Adding the Polyline", Toast.LENGTH_SHORT).show();
                } else {
                    mapObjects.createPolygon(points);
                    Toast.makeText(MainActivity.this, "Adding the Polygon", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void showMessage() {
        builder = new AlertDialog.Builder(this);

        builder.setMessage("" +
                "Click the \"Get GeoJson\" button to initialize the directory, then move the GeoJson.json file to the Documents folder inside the application directory \n" +
                "\nThe directory path should be something like this: \n" +
                "/emulated/0/Android/data/com.here.example.geojson_demo/Documents/")
                .setCancelable(false)
                .setPositiveButton("Ok", (dialog, id) -> dialog.cancel());

        AlertDialog alert = builder.create();

        alert.setTitle("Instructions: ");

        alert.show();
    }
}