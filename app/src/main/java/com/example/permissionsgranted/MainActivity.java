package com.example.permissionsgranted;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    Location loc;
    private LocationCallback locationCallback;
    private TextView lat, lon, alt, spe, bea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initGui();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }

                for (Location location : locationResult.getLocations()) {
                    if (location == null) continue;

                    // Update UI with location data
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    double altitude = location.hasAltitude() ? location.getAltitude() : 0.0;
                    float speed = location.hasSpeed() ? location.getSpeed() : 0.0f;
                    float bearing = location.hasBearing() ? location.getBearing() : 0.0f;

                    lat.setText("Latitude: " + latitude);
                    lon.setText("Longitude: " + longitude);
                    alt.setText("Altitude: " + altitude);
                    spe.setText("Speed: " + speed);
                    bea.setText("Bearing: " + bearing);

                    Log.d("Location", "Latitude: " + latitude + ", Longitude: " + longitude +
                            ", Altitude: " + altitude + ", Speed: " + speed + ", Bearing: " + bearing);
                }
            }
        };
    }

    private void initGui(){
        lat = findViewById(R.id.txt_latitude);
        lon = findViewById(R.id.txt_longitude);
        alt = findViewById(R.id.txt_altitude);
        spe = findViewById(R.id.txt_speed);
        bea = findViewById(R.id.txt_bearing);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            getPermission();
            return;
        }

        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,1000).build();
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    void getPermission(){
        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,false);
                            if (fineLocationGranted != null && fineLocationGranted) {
                                // Precise location access granted.
                                getPermission();
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted
                                 getPermission();
                            } else {
                                // No location access granted.
                                finish();
                                Toast.makeText(this, "closing app", Toast.LENGTH_LONG).show();
                            }
                        }
                );
// ...
// Before you perform the actual permission request, check whether your app
// already has the permissions, and whether your app needs to show a permission
// rationale dialog. For more details, see Request permissions.
        locationPermissionRequest.launch(new String[] {
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            getPermission();
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            Log.d("Location", "Latitude: " + latitude + ", Longitude: " + longitude);
                            Toast.makeText(MainActivity.this, "Latitude: " + latitude + ", Longitude: " + longitude, Toast.LENGTH_LONG).show();
                        }
                        loc = location;

                    }
                });
    }





}